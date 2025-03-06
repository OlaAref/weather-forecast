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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
       assertEquals(locationDto.cityName(), location.cityName());
       assertNotNull(location);
       verify(locationRepository, times(1)).save(locationEntity);
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