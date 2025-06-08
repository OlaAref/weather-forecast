package com.olaaref.weather.service;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Ip2LocationTest {

    private final String ipPath = "src/main/resources/ip2location/IP2LOCATION-LITE-DB11.BIN";

    @Test
    void testInvalidIp() throws IOException {
        // Given
        IP2Location ip2Location = new IP2Location();
        ip2Location.Open(ipPath);
        String ip = "invalid_ip";

        // When
        IPResult result = ip2Location.IPQuery(ip);

        // Then
        assertEquals("INVALID_IP_ADDRESS", result.getStatus());
    }

    @Test
    void testNewYorkIp() throws IOException {
        // Given
        IP2Location ip2Location = new IP2Location();
        ip2Location.Open(ipPath);
        String ip = "108.30.178.78";

        // When
        IPResult result = ip2Location.IPQuery(ip);
        System.out.println(result);

        // Then
        assertEquals("OK", result.getStatus());
    }

    @Test
    void testNewDelhiIp() throws IOException {
        // Given
        IP2Location ip2Location = new IP2Location();
        ip2Location.Open(ipPath);
        String ip = "103.48.198.141";

        // When
        IPResult result = ip2Location.IPQuery(ip);
        System.out.println(result);

        // Then
        assertEquals("OK", result.getStatus());
    }
}
