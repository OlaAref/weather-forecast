package com.olaaref.weather.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.olaaref.weather.commonlib.dto.RealtimeWeatherDto;
import com.olaaref.weather.commonlib.dto.request.RealtimeWeatherRequestDto;
import com.olaaref.weather.commonlib.model.Location;
import com.olaaref.weather.commonlib.model.RealtimeWeather;
import com.olaaref.weather.exception.GeolocationException;
import com.olaaref.weather.exception.LocationNotFoundException;
import com.olaaref.weather.service.GeolocationService;
import com.olaaref.weather.service.RealtimeWeatherService;
import com.olaaref.weather.util.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RealtimeWeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RealtimeWeatherService realtimeWeatherService;

    @MockitoBean
    private GeolocationService geolocationService;

    @MockitoBean
    private ModelMapper modelMapper;

    @DisplayName("Get weather by IP address - Success")
    @Test
    void getWeatherByIpAddress_Success() throws Exception {
        // Given
        String ipAddress = "192.168.1.1";
        Location location = createTestLocation();
        RealtimeWeather realtimeWeather = createTestRealtimeWeather(location);
        RealtimeWeatherDto realtimeWeatherDto = createTestRealtimeWeatherDto();

        try (MockedStatic<Utils> utilities = mockStatic(Utils.class)) {
            utilities.when(() -> Utils.getIpAddress(any(HttpServletRequest.class)))
                    .thenReturn(ipAddress);

            when(geolocationService.getLocation(ipAddress)).thenReturn(location);
            when(realtimeWeatherService.getWeatherByLocation(location)).thenReturn(realtimeWeather);
            when(modelMapper.map(realtimeWeather, RealtimeWeatherDto.class)).thenReturn(realtimeWeatherDto);

            // When/Then
            // Using the base endpoint with GET mapping
            mockMvc.perform(get("/v1/weather")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.location").value(realtimeWeatherDto.getLocation()))
                    .andExpect(jsonPath("$.temperature").value(realtimeWeatherDto.getTemperature()))
                    .andExpect(jsonPath("$.humidity").value(realtimeWeatherDto.getHumidity()))
                    .andExpect(jsonPath("$.precipitation").value(realtimeWeatherDto.getPrecipitation()))
                    .andExpect(jsonPath("$.windSpeed").value(realtimeWeatherDto.getWindSpeed()))
                    .andExpect(jsonPath("$.status").value(realtimeWeatherDto.getStatus()))
                    .andDo(print());
        }
    }

    @DisplayName("Get weather by IP address - Location Not Found")
    @Test
    void getWeatherByIpAddress_LocationNotFound() throws Exception {
        // Given
        String ipAddress = "192.168.1.1";
        Location location = createTestLocation();

        try (MockedStatic<Utils> utilities = mockStatic(Utils.class)) {
            utilities.when(() -> Utils.getIpAddress(any(HttpServletRequest.class)))
                    .thenReturn(ipAddress);

            when(geolocationService.getLocation(ipAddress)).thenReturn(location);
            when(realtimeWeatherService.getWeatherByLocation(location))
                    .thenThrow(new LocationNotFoundException("Weather data not found for location"));

            // When/Then
            // Using the base endpoint with GET mapping
            mockMvc.perform(get("/v1/weather")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andDo(print());
        }
    }

    @DisplayName("Get weather by IP address - Geolocation Exception")
    @Test
    void getWeatherByIpAddress_GeolocationException() throws Exception {
        // Given
        String ipAddress = "192.168.1.1";

        try (MockedStatic<Utils> utilities = mockStatic(Utils.class)) {
            utilities.when(() -> Utils.getIpAddress(any(HttpServletRequest.class)))
                    .thenReturn(ipAddress);

            when(geolocationService.getLocation(ipAddress))
                    .thenThrow(new GeolocationException("Error getting location from IP"));

            // When/Then
            mockMvc.perform(get("/v1/weather")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }
    }

    @DisplayName("Verify GetMapping annotation works correctly")
    @Test
    void verifyGetMappingAnnotation() throws Exception {
        // Given
        String ipAddress = "192.168.1.1";
        Location location = createTestLocation();
        RealtimeWeather realtimeWeather = createTestRealtimeWeather(location);
        RealtimeWeatherDto realtimeWeatherDto = createTestRealtimeWeatherDto();

        try (MockedStatic<Utils> utilities = mockStatic(Utils.class)) {
            utilities.when(() -> Utils.getIpAddress(any(HttpServletRequest.class)))
                    .thenReturn(ipAddress);

            when(geolocationService.getLocation(ipAddress)).thenReturn(location);
            when(realtimeWeatherService.getWeatherByLocation(location)).thenReturn(realtimeWeather);
            when(modelMapper.map(realtimeWeather, RealtimeWeatherDto.class)).thenReturn(realtimeWeatherDto);

            // When/Then
            // Verify that HTTP GET method works with the base endpoint
            mockMvc.perform(get("/v1/weather")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            // Verify that other HTTP methods are not allowed
            mockMvc.perform(post("/v1/weather")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isMethodNotAllowed())
                    .andDo(print());

            mockMvc.perform(put("/v1/weather")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isMethodNotAllowed())
                    .andDo(print());
        }
    }

    @DisplayName("Get weather by location code - Success")
    @Test
    void getWeatherByLocationCode_Success() throws Exception {
        // Given
        String locationCode = "US-NY-NY";
        Location location = createTestLocation();
        location.setCode(locationCode);
        location.setCityName("New York City");
        location.setRegionName("New York");

        RealtimeWeather realtimeWeather = createTestRealtimeWeather(location);
        RealtimeWeatherDto realtimeWeatherDto = createTestRealtimeWeatherDto();
        realtimeWeatherDto.setLocation("New York City, US");

        when(realtimeWeatherService.getWeatherByLocationCode(locationCode)).thenReturn(realtimeWeather);
        when(modelMapper.map(realtimeWeather, RealtimeWeatherDto.class)).thenReturn(realtimeWeatherDto);

        // When/Then
        mockMvc.perform(get("/v1/weather/{locationCode}", locationCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.location").value(realtimeWeatherDto.getLocation()))
                .andExpect(jsonPath("$.temperature").value(realtimeWeatherDto.getTemperature()))
                .andExpect(jsonPath("$.humidity").value(realtimeWeatherDto.getHumidity()))
                .andExpect(jsonPath("$.precipitation").value(realtimeWeatherDto.getPrecipitation()))
                .andExpect(jsonPath("$.windSpeed").value(realtimeWeatherDto.getWindSpeed()))
                .andExpect(jsonPath("$.status").value(realtimeWeatherDto.getStatus()))
                .andDo(print());
    }

    @DisplayName("Get weather by location code - Not Found")
    @Test
    void getWeatherByLocationCode_NotFound() throws Exception {
        // Given
        String locationCode = "INVALID-CODE";

        when(realtimeWeatherService.getWeatherByLocationCode(locationCode))
                .thenThrow(new LocationNotFoundException("Weather data not found for location code: " + locationCode));

        // When/Then
        mockMvc.perform(get("/v1/weather/{locationCode}", locationCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @DisplayName("Update weather by location code - Success")
    @Test
    void updateWeatherByLocationCode_Success() throws Exception {
        // Given
        String locationCode = "US-NY-NY";
        Location location = createTestLocation();
        location.setCode(locationCode);
        location.setCityName("New York City");
        location.setRegionName("New York");


        RealtimeWeatherDto inputWeatherDto = createTestRealtimeWeatherDto();
        inputWeatherDto.setLocation("New York City, US");
        inputWeatherDto.setTemperature(28.5); // Updated temperature
        inputWeatherDto.setHumidity(70.0);    // Updated humidity
        inputWeatherDto.setStatus("Cloudy");  // Updated status

        RealtimeWeather updatedWeather = createTestRealtimeWeather(location);
        updatedWeather.setTemperature(28.5);
        updatedWeather.setHumidity(70.0);
        updatedWeather.setStatus("Cloudy");

        RealtimeWeatherDto outputWeatherDto = createTestRealtimeWeatherDto();
        outputWeatherDto.setLocation("New York City, US");
        outputWeatherDto.setTemperature(28.5);
        outputWeatherDto.setHumidity(70.0);
        outputWeatherDto.setStatus("Cloudy");

        when(realtimeWeatherService.updateWeatherByLocationCode(eq(locationCode), any(RealtimeWeatherRequestDto.class)))
                .thenReturn(updatedWeather);
        when(modelMapper.map(updatedWeather, RealtimeWeatherDto.class)).thenReturn(outputWeatherDto);

        // When/Then
        mockMvc.perform(put("/v1/weather/{locationCode}", locationCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputWeatherDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.location").value(outputWeatherDto.getLocation()))
                .andExpect(jsonPath("$.temperature").value(outputWeatherDto.getTemperature()))
                .andExpect(jsonPath("$.humidity").value(outputWeatherDto.getHumidity()))
                .andExpect(jsonPath("$.precipitation").value(outputWeatherDto.getPrecipitation()))
                .andExpect(jsonPath("$.windSpeed").value(outputWeatherDto.getWindSpeed()))
                .andExpect(jsonPath("$.status").value(outputWeatherDto.getStatus()))
                .andDo(print());
    }

    @DisplayName("Update weather by location code - Not Found")
    @Test
    void updateWeatherByLocationCode_NotFound() throws Exception {
        // Given
        String locationCode = "INVALID-CODE";
        RealtimeWeatherDto weatherDto = createTestRealtimeWeatherDto();

        when(realtimeWeatherService.updateWeatherByLocationCode(eq(locationCode), any(RealtimeWeatherRequestDto.class)))
                .thenThrow(new LocationNotFoundException("Weather data not found for location code: " + locationCode));

        // When/Then
        mockMvc.perform(put("/v1/weather/{locationCode}", locationCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(weatherDto)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    private Location createTestLocation() {
        Location location = new Location();
        location.setCode("US-CA-LA");
        location.setCountryCode("US");
        location.setCountryName("United States");
        location.setRegionName("California");
        location.setCityName("Los Angeles");
        location.setLatitude(34.0522);
        location.setLongitude(-118.2437);
        location.setZipCode("90001");
        location.setTimeZone("PST");
        location.setEnabled(true);
        return location;
    }

    private RealtimeWeather createTestRealtimeWeather(Location location) {
        RealtimeWeather realtimeWeather = new RealtimeWeather();
        realtimeWeather.setLocationCode(location.getCode());
        realtimeWeather.setTemperature(25.5);
        realtimeWeather.setHumidity(65.0);
        realtimeWeather.setPrecipitation(0.0);
        realtimeWeather.setWindSpeed(10.0);
        realtimeWeather.setStatus("Sunny");
        realtimeWeather.setLastUpdated(LocalDateTime.now());
        realtimeWeather.setLocation(location);
        return realtimeWeather;
    }

    private RealtimeWeatherDto createTestRealtimeWeatherDto() {
        RealtimeWeatherDto dto = new RealtimeWeatherDto();
        dto.setLocation("Los Angeles, US");
        dto.setTemperature(25.5);
        dto.setHumidity(65.0);
        dto.setPrecipitation(0.0);
        dto.setWindSpeed(10.0);
        dto.setStatus("Sunny");
        dto.setLastUpdated(LocalDateTime.now());
        return dto;
    }
}
