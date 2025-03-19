package com.olaaref.weather.controller;

import com.olaaref.weather.commonlib.dto.LocationDto;
import com.olaaref.weather.commonlib.model.Location;
import com.olaaref.weather.exception.LocationNotFoundException;
import com.olaaref.weather.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/location")
public class LocationController {

    private final LocationService locationService;

    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping
    public ResponseEntity<LocationDto> saveLocation(@RequestBody @Valid LocationDto locationDto) {
        Location savedLocation = locationService.saveLocation(locationDto);
        URI locationUri = URI.create("/v1/location/" + savedLocation.getCode());

        return ResponseEntity.created(locationUri).body(savedLocation.toLocationDto());
    }

    @GetMapping
    public ResponseEntity<List<LocationDto>> getAllLocations() {
        List<Location> allLocations = locationService.getAllLocations();
        if(allLocations.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(
                allLocations.stream()
                        .map(Location::toLocationDto)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/{code}")
    public ResponseEntity<LocationDto> getLocationByCode(@PathVariable String code) {
        Optional<Location> location = locationService.getLocationByCode(code);
        return location
                .map(value -> ResponseEntity.ok(value.toLocationDto()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping
    public ResponseEntity<LocationDto> updateLocation(@RequestBody @Valid LocationDto locationDto) {
        try {
            Location updateLocation = locationService.updateLocation(locationDto);
            return ResponseEntity.ok(updateLocation.toLocationDto());
        } catch (LocationNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<LocationDto> deleteLocation(@PathVariable String code) {
        try {
            locationService.trashLocation(code);
            return ResponseEntity.noContent().build();
        } catch (LocationNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
