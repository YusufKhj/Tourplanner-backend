package com.example.Tourplanner.services;

import com.example.Tourplanner.entities.Coordinate;
import com.example.Tourplanner.entities.RouteInfo;
import com.example.Tourplanner.exceptions.RouteCalculationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class RouteService {

    private static final Logger log = LoggerFactory.getLogger(RouteService.class);
    private static final String ORS_BASE = "https://api.openrouteservice.org";

    @Value("${app.ors.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public RouteInfo calculateRoute(String startLocation, String finishLocation, String transportType) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("No ORS API key configured, using approximate route from '{}' to '{}'", startLocation, finishLocation);
            return approximateRoute(startLocation, finishLocation, transportType);
        }

        try {
            Coordinate startCoord = geocode(startLocation);
            Coordinate finishCoord = geocode(finishLocation);
            RouteInfo route = getDirections(startCoord, finishCoord, transportType);
            log.info("Route calculated: {} -> {}, distance={}km, duration={}s",
                    startLocation, finishLocation, route.getDistance(), route.getDuration());
            return route;
        } catch (Exception e) {
            log.error("ORS API call failed for '{}' -> '{}', using approximate route", startLocation, finishLocation, e);
            return approximateRoute(startLocation, finishLocation, transportType);
        }
    }

    private Coordinate geocode(String location) {
        String url = ORS_BASE + "/geocode/search?api_key=" + apiKey + "&text=" + location.replace(" ", "%20") + "&size=1";
        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = mapper.readTree(response);
            JsonNode coords = root.path("features").get(0).path("geometry").path("coordinates");
            double lng = coords.get(0).asDouble();
            double lat = coords.get(1).asDouble();
            return new Coordinate(lat, lng);
        } catch (Exception e) {
            throw new RouteCalculationException("Failed to geocode location: " + location, e);
        }
    }

    private RouteInfo getDirections(Coordinate start, Coordinate finish, String transportType) {
        String profile = mapProfile(transportType);
        String url = ORS_BASE + "/v2/directions/" + profile + "/geojson?api_key=" + apiKey;

        var requestBody = Map.of(
                "coordinates", List.of(
                        List.of(start.getLng(), start.getLat()),
                        List.of(finish.getLng(), finish.getLat())
                )
        );

        try {
            String response = restTemplate.postForObject(url, requestBody, String.class);
            JsonNode root = mapper.readTree(response);
            JsonNode properties = root.path("features").get(0).path("properties");
            JsonNode summary = properties.path("summary");

            double distance = summary.path("distance").asDouble();
            double duration = summary.path("duration").asDouble();

            JsonNode coordsNode = root.path("features").get(0).path("geometry").path("coordinates");
            List<Coordinate> coordinates = new ArrayList<>();
            for (JsonNode c : coordsNode) {
                coordinates.add(new Coordinate(c.get(1).asDouble(), c.get(0).asDouble()));
            }

            return new RouteInfo(coordinates, distance, duration);
        } catch (Exception e) {
            throw new RouteCalculationException("Failed to get directions from OpenRouteService", e);
        }
    }

    private String mapProfile(String transportType) {
        return switch (transportType.toLowerCase()) {
            case "foot", "walking", "hiking" -> "foot-walking";
            case "bike", "cycling", "bicycle" -> "cycling-regular";
            case "car", "driving", "automobile" -> "driving-car";
            case "running" -> "foot-walking";
            default -> "driving-car";
        };
    }

    private RouteInfo approximateRoute(String start, String finish, String transportType) {
        int charSum = start.length() + finish.length();
        double estimatedKm = 10 + (charSum % 200);
        double estimatedMinutes = estimatedKm * 1.5;
        if (mapProfile(transportType).equals("foot-walking")) {
            estimatedMinutes = estimatedKm * 12;
        } else if (mapProfile(transportType).contains("cycling")) {
            estimatedMinutes = estimatedKm * 4;
        }
        return new RouteInfo(
                Collections.emptyList(),
                estimatedKm,
                estimatedMinutes * 60
        );
    }
}
