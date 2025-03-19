package com.olaaref.weather.repository;


import com.olaaref.weather.commonlib.model.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LocationRepositoryTest {

    @Autowired
    private LocationRepository locationRepository;

    @Test
    void testSaveLocation() {
        // Arrange
        Location location = new Location(
            "US-TX-HOU",
            "US",
            "United States",
            "Texas",
            "Houston",
            29.7604,
            -95.3698,
            "77001",
            "America/Chicago",
            true,
            false
        );

        // Act
        Location savedLocation = locationRepository.save(location);

        // Assert
        assertNotNull(savedLocation);
        assertEquals(location.getCode(), savedLocation.getCode());
        assertEquals(location.getCountryCode(), savedLocation.getCountryCode());
        assertEquals(location.getCityName(), savedLocation.getCityName());
    }

    @Test
    @DisplayName("Test List Untrashed Locations")
    void testListUntrashedLocations() {
        // Act
        List<Location> locations = locationRepository.findUntrashed();

        // Assert
        assertNotNull(locations);
        assertEquals(3, locations.size());
        assertTrue(locations.stream().noneMatch(Location::isTrashed));
    }

    @Test
    @DisplayName("Test Find Location By Code")
    void testFindByCode() {
        // Act
        Optional<Location> foundLocation = locationRepository.findById("US-CA-LA");

        // Assert
        assertTrue(foundLocation.isPresent());
        assertEquals("Los Angeles", foundLocation.get().getCityName());
        assertEquals("California", foundLocation.get().getRegionName());
    }

    @Test
    @DisplayName("Test Find Location By Non-Existent Code")
    void testFindByNonExistentCode() {
        // Act
        Optional<Location> foundLocation = locationRepository.findById("NON-EXISTENT");

        // Assert
        assertTrue(foundLocation.isEmpty());
    }
}