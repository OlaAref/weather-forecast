package com.olaaref.weather.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.olaaref.weather.commonlib.dto.LocationDto;
import com.olaaref.weather.commonlib.model.Location;
import com.olaaref.weather.exception.LocationNotFoundException;
import com.olaaref.weather.service.LocationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LocationControllerTest {
    private static final String LOCATION_URI = "/v1/location";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LocationService locationService;

    @InjectMocks
    private LocationController locationController;

    @DisplayName("Save empty location")
    @Test
    void saveEmptyLocation() throws Exception {
        LocationDto location = new LocationDto();
        String bodyContent = objectMapper.writeValueAsString(location);
        mockMvc.perform(
                        post(LOCATION_URI)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(bodyContent)
                )
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @DisplayName("Save valid location")
    @Test
    void saveValidLocation() throws Exception {
        LocationDto locationDto = LocationDto.builder()
                .code("US-CA-LA")
                .countryCode("US")
                .countryName("United States")
                .regionName("California")
                .cityName("Los Angeles")
                .latitude(34.0522)
                .longitude(-118.2437)
                .zipCode("90001")
                .timeZone("PST")
                .enabled(true)
                .build();
        String bodyContent = objectMapper.writeValueAsString(locationDto);

        when(
                locationService.saveLocation(any(LocationDto.class))
        ).thenReturn(
                locationDto.toLocationEntity()
        );

        mockMvc
                .perform(
                        post(LOCATION_URI)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(bodyContent)
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(locationDto.code()))
                .andExpect(jsonPath("$.countryCode").value(locationDto.countryCode()))
                .andExpect(header().string("Location", "/v1/location/" + locationDto.code()))
                .andDo(print());
    }

    @DisplayName("Get All Untrashed Locations")
    @Test
    void getAllUntrashedLocations() throws Exception {

        when(
                locationService.getAllLocations()
        ).thenReturn(
                List.of(getValidLocation())
        );

        mockMvc
                .perform(
                        get(LOCATION_URI) .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].code").value("US-CA-LA"))
                .andExpect(jsonPath("$[0].countryCode").value("US"))
                .andDo(print());
    }

    @DisplayName("Get Empty Locations List")
    @Test
    void getEmptyLocationsList() throws Exception {

        when(
                locationService.getAllLocations()
        ).thenReturn(
                Collections.emptyList()
        );

        mockMvc
                .perform(
                        get(LOCATION_URI) .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @DisplayName("Get location by code when exists")
    @Test
    void getLocationByCode_WhenLocationExists_ShouldReturnLocation() throws Exception {
        // Given
        Location location = getValidLocation();
        when(locationService.getLocationByCode("US-CA-LA"))
                .thenReturn(java.util.Optional.of(location));

        // When/Then
        mockMvc.perform(
                        get(LOCATION_URI + "/US-CA-LA")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(location.getCode()))
                .andExpect(jsonPath("$.cityName").value(location.getCityName()))
                .andDo(print());
    }

    @DisplayName("Get location by code when not exists")
    @Test
    void getLocationByCode_WhenLocationDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Given
        when(locationService.getLocationByCode("INVALID"))
                .thenReturn(java.util.Optional.empty());

        // When/Then
        mockMvc.perform(
                        get(LOCATION_URI + "/INVALID")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @DisplayName("Get location by code when trashed")
    @Test
    void getLocationByCode_WhenLocationIsTrashed_ShouldReturnNotFound() throws Exception {
        // Given
        when(locationService.getLocationByCode("US-CA-LA"))
                .thenReturn(java.util.Optional.empty());

        // When/Then
        mockMvc.perform(
                        get(LOCATION_URI + "/US-CA-LA")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @DisplayName("Get location by code when method is post")
    @Test
    void getLocationByCode_WhenRequestIsPost_ShouldReturnMethodNotAllowed() throws Exception {
        // Given
        when(locationService.getLocationByCode("US-CA-LA"))
                .thenReturn(java.util.Optional.empty());

        // When/Then
        mockMvc.perform(
                        post(LOCATION_URI + "/US-CA-LA")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isMethodNotAllowed())
                .andDo(print());
    }

    @DisplayName("Update location with wrong code")
    @Test
    void updateLocation_WhenWrongCode_ShouldReturnNotFound() throws Exception {
        // Given
        LocationDto locationDto = LocationDto.builder()
                .code("ABCDF")
                .countryCode("US")
                .countryName("United States")
                .regionName("California")
                .cityName("Los Angeles")
                .latitude(34.0522)
                .longitude(-118.2437)
                .zipCode("90001")
                .timeZone("PST")
                .enabled(true)
                .build();
        when(locationService.updateLocation(locationDto))
                .thenThrow(new LocationNotFoundException("Location with code " + locationDto.code() + " not found"));
        String bodyContent = objectMapper.writeValueAsString(locationDto);

        // When/Then
        mockMvc.perform(
                        put(LOCATION_URI)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(bodyContent)
                )
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @DisplayName("Update location with valid data")
    @Test
    void updateLocation_WhenValidData_ShouldReturnOk() throws Exception {
        // Given
        LocationDto locationDto = LocationDto.builder()
                .code("US-CA-LA")
                .countryCode("US")
                .countryName("United States")
                .regionName("California")
                .cityName("Los Angeles")
                .latitude(34.0522)
                .longitude(-118.2437)
                .zipCode("90001")
                .timeZone("PST")
                .enabled(true)
                .build();
        when(locationService.updateLocation(locationDto))
                .thenReturn(locationDto.toLocationEntity());
        String bodyContent = objectMapper.writeValueAsString(locationDto);

        // When/Then
        mockMvc.perform(
                        put(LOCATION_URI)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(bodyContent)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(locationDto.code()))
                .andExpect(jsonPath("$.cityName").value(locationDto.cityName()))
                .andDo(print());
    }

    private Location getValidLocation() {
        return LocationDto.builder()
                .code("US-CA-LA")
                .countryCode("US")
                .countryName("United States")
                .regionName("California")
                .cityName("Los Angeles")
                .latitude(34.052235)
                .longitude(-118.243683)
                .zipCode("90001")
                .timeZone("America/Los_Angeles")
                .enabled(true)
                .trashed(false)
                .build()
                .toLocationEntity();
    }
}