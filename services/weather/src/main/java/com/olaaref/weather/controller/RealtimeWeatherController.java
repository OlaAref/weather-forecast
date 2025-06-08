package com.olaaref.weather.controller;

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
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/v1/weather")
public class RealtimeWeatherController {
    private final RealtimeWeatherService realtimeWeatherService;
    private final GeolocationService geolocationService;
    private final ModelMapper modelMapper;

    @Autowired
    public RealtimeWeatherController(RealtimeWeatherService realtimeWeatherService, GeolocationService geolocationService, ModelMapper modelMapper) {
        this.realtimeWeatherService = realtimeWeatherService;
        this.geolocationService = geolocationService;
        this.modelMapper = modelMapper;
    }

    /**
     * Get weather information based on the client's IP address
     * 
     * @param request The HTTP request containing the client's IP address
     * @return Weather information for the client's location
     */
    @GetMapping
    public ResponseEntity<RealtimeWeatherDto> getWeatherByIpAddress(HttpServletRequest request) {
        try {
            String ipAddress = Utils.getIpAddress(request);
            Location location = geolocationService.getLocation(ipAddress);
            RealtimeWeather realtimeWeather = realtimeWeatherService.getWeatherByLocation(location);
            RealtimeWeatherDto realtimeWeatherDto = modelMapper.map(realtimeWeather, RealtimeWeatherDto.class);
            return ResponseEntity.ok(realtimeWeatherDto);
        } catch (LocationNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (GeolocationException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get weather information for a specific location by its code
     * 
     * @param locationCode The location code in format COUNTRY-REGION-CITY (e.g., US-NY-NY)
     * @return Weather information for the specified location
     */
    @GetMapping("/{locationCode}")
    public ResponseEntity<RealtimeWeatherDto> getWeatherByLocationCode(@PathVariable String locationCode) {
        try {
            RealtimeWeather realtimeWeather = realtimeWeatherService.getWeatherByLocationCode(locationCode);
            RealtimeWeatherDto realtimeWeatherDto = modelMapper.map(realtimeWeather, RealtimeWeatherDto.class);
            return ResponseEntity.ok(realtimeWeatherDto);
        } catch (LocationNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update weather information for a specific location by its code
     * 
     * @param locationCode The location code in format COUNTRY-REGION-CITY (e.g., US-NY-NY)
     * @param weatherDto The weather data to update
     * @return Updated weather information for the specified location
     */
    @PutMapping("/{locationCode}")
    public ResponseEntity<RealtimeWeatherDto> updateWeatherByLocationCode(
            @PathVariable String locationCode,
            @RequestBody @Valid RealtimeWeatherRequestDto weatherDto) {
        try {
            weatherDto.setLocationCode(locationCode);
            RealtimeWeather updatedWeather = realtimeWeatherService.updateWeatherByLocationCode(locationCode, weatherDto);
            RealtimeWeatherDto updatedWeatherDto = modelMapper.map(updatedWeather, RealtimeWeatherDto.class);
            return ResponseEntity.ok(updatedWeatherDto);
        } catch (LocationNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
