package com.olaaref.weather.service;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import com.olaaref.weather.commonlib.enums.Ip2LocationStatus;
import com.olaaref.weather.commonlib.model.Location;
import com.olaaref.weather.exception.GeolocationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class GeolocationService {
    private static final String DB_PATH = "src/main/resources/ip2location/IP2LOCATION-LITE-DB11.BIN";
    private IP2Location ip2Location = new IP2Location();

    public  GeolocationService() {
        try {
            ip2Location.Open(DB_PATH);
        } catch (Exception e) {
            log.error("Error opening IP2Location database: {}", e.getMessage());
        }
    }

    public Location getLocation(String ip) throws GeolocationException {
        try {
            IPResult result = ip2Location.IPQuery(ip);
            if (!result.getStatus().equals(Ip2LocationStatus.OK.getStatus())) {
                log.error("Error getting IP2Location: {}", result.getStatus());
                throw new GeolocationException("Error getting IP2Location: " + result.getStatus());
            }

            return Location.builder()
                    .countryCode(result.getCountryShort())
                    .cityName(result.getCity())
                    .countryName(result.getCountryLong())
                    .regionName(result.getRegion())
                    .latitude(result.getLatitude())
                    .longitude(result.getLongitude())
                    .zipCode(result.getZipCode())
                    .timeZone(result.getTimeZone())
                    .build();

        } catch (IOException e) {
            log.error("Error getting IP2Location from database: {}", e.getMessage());
            throw new GeolocationException("Error getting IP2Location from database", e);
        }
    }

}
