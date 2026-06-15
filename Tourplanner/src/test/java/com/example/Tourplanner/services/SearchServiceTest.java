package com.example.Tourplanner.services;

import com.example.Tourplanner.entities.Users;
import com.example.Tourplanner.exceptions.UserNotFoundException;
import com.example.Tourplanner.repository.TourLogRepository;
import com.example.Tourplanner.repository.TourRepository;
import com.example.Tourplanner.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private TourRepository tourRepository;
    @Mock
    private TourLogRepository tourLogRepository;
    @Mock
    private UsersRepository usersRepository;

    private SearchService searchService;
    private Users testUser;

    @BeforeEach
    void setUp() {
        searchService = new SearchService(tourRepository, tourLogRepository, usersRepository);
        testUser = new Users();
        testUser.setId(UUID.randomUUID());
        testUser.setUsername("testuser");
    }

    @Test
    void search_shouldReturnEmptyResultsForNoMatch() {
        when(usersRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(tourRepository.search("xyz")).thenReturn(java.util.Collections.emptyList());
        when(tourLogRepository.searchByComment("xyz")).thenReturn(java.util.Collections.emptyList());
        when(tourRepository.findByUser(testUser)).thenReturn(java.util.Collections.emptyList());

        var result = searchService.search("xyz", "testuser");

        assertNotNull(result);
        assertTrue(((java.util.List<?>) result.get("tours")).isEmpty());
        assertTrue(((java.util.List<?>) result.get("logs")).isEmpty());
    }

    @Test
    void search_shouldThrowWhenUserNotFound() {
        when(usersRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                searchService.search("test", "ghost"));
    }
}
