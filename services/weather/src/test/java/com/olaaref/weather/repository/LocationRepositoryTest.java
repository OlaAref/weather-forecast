package com.olaaref.weather.repository;

import com.olaaref.weather.commonlib.dto.LocationDto;
import com.olaaref.weather.commonlib.model.Location;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LocationRepositoryTest {

    @Autowired
    private LocationRepository locationRepository;

    @Test
    void testSaveLocation() {
        // Arrange
        Location location = getValidLocation();

        // Act
        Location savedLocation = locationRepository.save(location);

        // Assert
        assertNotNull(savedLocation);
        assertEquals(location.code(), savedLocation.code());
    }

    private Location getValidLocation() {
        return new Location("US-CA-LA","US","United States","California","Los Angeles",34.052235,-118.243683,"90001","America/Los_Angeles",true,false);
    }

}