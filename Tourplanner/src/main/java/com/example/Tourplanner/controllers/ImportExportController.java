package com.example.Tourplanner.controllers;

import com.example.Tourplanner.services.ImportExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tour")
@RequiredArgsConstructor
public class ImportExportController {

    private final ImportExportService importExportService;

    @GetMapping("/export")
    public ResponseEntity<String> exportTours(@AuthenticationPrincipal String username) {
        String json = importExportService.exportTours(username);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=tours.json")
                .contentType(MediaType.APPLICATION_JSON)
                .body(json);
    }

    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> importTours(
            @RequestBody String json,
            @AuthenticationPrincipal String username) {
        int count = importExportService.importTours(json, username);
        return ResponseEntity.ok(Map.of("imported", count));
    }
}
