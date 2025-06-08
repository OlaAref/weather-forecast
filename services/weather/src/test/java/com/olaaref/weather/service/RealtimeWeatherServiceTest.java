package com.olaaref.weather.service;

import com.olaaref.weather.commonlib.model.RealtimeWeather;
import com.olaaref.weather.exception.LocationNotFoundException;
import com.olaaref.weather.repository.RealtimeWeatherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RealtimeWeatherServiceTest {

    @Mock
    private RealtimeWeatherRepository realtimeWeatherRepository;

    @InjectMocks
    private RealtimeWeatherService realtimeWeatherService;

    private RealtimeWeather mockWeather;

    @BeforeEach
    void setUp() {
        mockWeather = new RealtimeWeather();
        mockWeather.setLocationCode("US-NY-NY");
        mockWeather.setTemperature(18.2);
        mockWeather.setStatus("Rainy");
    }

    @Test
    @DisplayName("Test Get Weather By Location Code - Success")
    void testGetWeatherByLocationCode_Success() throws LocationNotFoundException {
        // Arrange
        String locationCode = "US-NY-NY";
        when(realtimeWeatherRepository.findByLocationCodeAndLocationTrashedFalse(locationCode))
                .thenReturn(mockWeather);

        // Act
        RealtimeWeather result = realtimeWeatherService.getWeatherByLocationCode(locationCode);

        // Assert
        assertNotNull(result);
        assertEquals(locationCode, result.getLocationCode());
        assertEquals(18.2, result.getTemperature());
        assertEquals("Rainy", result.getStatus());
        verify(realtimeWeatherRepository, times(1)).findByLocationCodeAndLocationTrashedFalse(locationCode);
    }

    @Test
    @DisplayName("Test Get Weather By Location Code - Not Found")
    void testGetWeatherByLocationCode_NotFound() {
        // Arrange
        String locationCode = "NON-EXISTENT";
        when(realtimeWeatherRepository.findByLocationCodeAndLocationTrashedFalse(locationCode))
                .thenReturn(null);

        // Act & Assert
        LocationNotFoundException exception = assertThrows(LocationNotFoundException.class, () ->
                realtimeWeatherService.getWeatherByLocationCode(locationCode));

        assertEquals("Weather data not found for location code: " + locationCode, exception.getMessage());
        verify(realtimeWeatherRepository, times(1)).findByLocationCodeAndLocationTrashedFalse(locationCode);
    }
}