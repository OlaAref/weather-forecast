package com.olaaref.weather.service;

import com.olaaref.weather.commonlib.dto.LocationDto;
import com.olaaref.weather.commonlib.model.Location;
import com.olaaref.weather.repository.LocationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationService locationService;

   @DisplayName("Save valid location")
   @Test
   void saveValidLocation() {
       //Given
       LocationDto locationDto = getValidLocationDto();
       Location locationEntity = locationDto.toLocationEntity();
       when(locationRepository.save(any(Location.class))).thenReturn(locationEntity);

       //When
       Location location = locationService.saveLocation(locationDto);

       //Then
       assertEquals(locationDto.cityName(), location.getCityName());
       assertNotNull(location);
   }

    @DisplayName("Get all untrashed locations")
    @Test
    void getAllUntrashedLocations() {
        //Given
        LocationDto locationDto = getValidLocationDto();
        Location locationEntity = locationDto.toLocationEntity();
        when(locationRepository.findUntrashed()).thenReturn(List.of(locationEntity));

        //When
        List<Location> locations = locationService.getAllLocations();

        //Then
        assertNotNull(locations);
        verify(locationRepository, times(1)).findUntrashed();
    }
    @DisplayName("Get location by code when exists")
    @Test
    void getLocationByCode_WhenLocationExists_ShouldReturnLocation() {
        // Given
        LocationDto locationDto = getValidLocationDto();
        Location locationEntity = locationDto.toLocationEntity();
        when(locationRepository.findByCodeAndTrashed("US-CA-LA", false))
                .thenReturn(java.util.Optional.of(locationEntity));

        // When
        java.util.Optional<Location> result = locationService.getLocationByCode("US-CA-LA");

        // Then
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(locationDto.code(), result.get().getCode());
        assertEquals(locationDto.cityName(), result.get().getCityName());
        verify(locationRepository, times(1)).findByCodeAndTrashed("US-CA-LA", false);
    }

    @DisplayName("Get location by code when not exists")
    @Test
    void getLocationByCode_WhenLocationDoesNotExist_ShouldReturnEmpty() {
        // Given
        when(locationRepository.findByCodeAndTrashed("INVALID", false))
                .thenReturn(java.util.Optional.empty());

        // When
        java.util.Optional<Location> result = locationService.getLocationByCode("INVALID");

        // Then
        assertNotNull(result);
        assertFalse(result.isPresent());
        verify(locationRepository, times(1)).findByCodeAndTrashed("INVALID", false);
    }

    @DisplayName("Get location by code when trashed")
    @Test
    void getLocationByCode_WhenLocationIsTrashed_ShouldReturnEmpty() {
        // Given
        LocationDto locationDto = getValidLocationDto();
        Location locationEntity = locationDto.toLocationEntity();
        locationEntity.setTrashed(true);
        when(locationRepository.findByCodeAndTrashed("US-CA-LA", false))
                .thenReturn(java.util.Optional.empty());

        // When
        java.util.Optional<Location> result = locationService.getLocationByCode("US-CA-LA");

        // Then
        assertNotNull(result);
        assertFalse(result.isPresent());
        verify(locationRepository, times(1)).findByCodeAndTrashed("US-CA-LA", false);
    }
    private LocationDto getValidLocationDto() {
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
                .build();
    }

}