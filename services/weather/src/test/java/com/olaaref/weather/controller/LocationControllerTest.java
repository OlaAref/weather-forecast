package com.olaaref.weather.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.olaaref.weather.commonlib.dto.LocationDto;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
}