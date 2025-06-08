package com.olaaref.weather.repository;

import com.olaaref.weather.commonlib.model.Location;
import com.olaaref.weather.commonlib.model.RealtimeWeather;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RealtimeWeatherRepositoryTest {

    @Autowired
    private RealtimeWeatherRepository realtimeWeatherRepository;
    
    @Autowired
    private LocationRepository locationRepository;
    
    @Test
    @DisplayName("Test Find By Location City Name And Country Code")
    void testFindByLocationCityNameAndLocationCountryCode() {
        // Act
        RealtimeWeather weather = realtimeWeatherRepository.findByLocationCityNameAndLocationCountryCode("Los Angeles", "US");
        
        // Assert
        assertNotNull(weather);
        assertEquals("US-CA-LA", weather.getLocationCode());
        assertEquals(25.5, weather.getTemperature());
        assertEquals("Sunny", weather.getStatus());
        assertNotNull(weather.getLocation());
        assertEquals("Los Angeles", weather.getLocation().getCityName());
        assertEquals("US", weather.getLocation().getCountryCode());
    }
    
    @Test
    @DisplayName("Test Find By Location City Name And Country Code - Not Found")
    void testFindByLocationCityNameAndLocationCountryCode_NotFound() {
        // Act
        RealtimeWeather weather = realtimeWeatherRepository.findByLocationCityNameAndLocationCountryCode("Paris", "FR");
        
        // Assert
        assertNull(weather);
    }
    
    @Test
    @DisplayName("Test Find By Location Code And Location Trashed False")
    void testFindByLocationCodeAndLocationTrashedFalse() {
        // Act
        RealtimeWeather weather = realtimeWeatherRepository.findByLocationCodeAndLocationTrashedFalse("US-NY-NY");
        
        // Assert
        assertNotNull(weather);
        assertEquals("US-NY-NY", weather.getLocationCode());
        assertEquals(18.2, weather.getTemperature());
        assertEquals("Rainy", weather.getStatus());
        assertNotNull(weather.getLocation());
        assertEquals("New York City", weather.getLocation().getCityName());
        assertEquals("US", weather.getLocation().getCountryCode());
        assertFalse(weather.getLocation().isTrashed());
    }
    
    @Test
    @DisplayName("Test Find By Location Code And Location Trashed False - Not Found")
    void testFindByLocationCodeAndLocationTrashedFalse_NotFound() {
        // Create a trashed location
        Location location = locationRepository.findById("US-CA-LA").orElseThrow();
        location.setTrashed(true);
        locationRepository.save(location);
        
        // Act
        RealtimeWeather weather = realtimeWeatherRepository.findByLocationCodeAndLocationTrashedFalse("US-CA-LA");
        
        // Assert
        assertNull(weather);
        
        // Cleanup - restore location to untrashed state
        location.setTrashed(false);
        locationRepository.save(location);
    }
    
    @Test
    @DisplayName("Test Save RealtimeWeather")
    void testSaveRealtimeWeather() {
        // Arrange
        Location location = locationRepository.findById("GB-ENG-LDN").orElseThrow();
        RealtimeWeather weather = new RealtimeWeather();
        weather.setLocationCode(location.getCode());
        weather.setTemperature(12.5);
        weather.setHumidity(85.0);
        weather.setPrecipitation(3.0);
        weather.setWindSpeed(20.0);
        weather.setStatus("Stormy");
        weather.setLastUpdated(LocalDateTime.now());
        weather.setLocation(location);
        
        // Act
        RealtimeWeather savedWeather = realtimeWeatherRepository.save(weather);
        
        // Assert
        assertNotNull(savedWeather);
        assertEquals(location.getCode(), savedWeather.getLocationCode());
        assertEquals(weather.getTemperature(), savedWeather.getTemperature());
        assertEquals(weather.getStatus(), savedWeather.getStatus());
        assertNotNull(savedWeather.getLocation());
        assertEquals(location.getCityName(), savedWeather.getLocation().getCityName());
    }
}