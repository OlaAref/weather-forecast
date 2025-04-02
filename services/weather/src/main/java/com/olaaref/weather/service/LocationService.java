package com.olaaref.weather.service;

import com.olaaref.weather.aop.logger.advice.around.LogAround;
import com.olaaref.weather.commonlib.dto.LocationDto;
import com.olaaref.weather.commonlib.model.Location;
import com.olaaref.weather.exception.LocationNotFoundException;
import com.olaaref.weather.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@LogAround
@Service
public class LocationService {

    private final LocationRepository locationRepository;

    @Autowired
    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public Location saveLocation(LocationDto locationDto) {
        Location location = locationDto.toLocationEntity();
        return locationRepository.save(location);
    }

    public List<Location> getAllLocations() {
        return locationRepository.findUntrashed();
    }

    public Optional<Location> getLocationByCode(String code) {
        return locationRepository.findByCodeAndTrashed(code, false);
    }

    public Location updateLocation(LocationDto locationDto) throws LocationNotFoundException {
        Optional<Location> location = getLocationByCode(locationDto.code());
        if(location.isEmpty()) {
            throw new LocationNotFoundException("Location with code " + locationDto.code() + " not found");
        }
        return locationRepository.save(locationDto.toLocationEntity());
    }

    public void trashLocation(String code) throws LocationNotFoundException {
        Optional<Location> location = getLocationByCode(code);
        if(location.isEmpty()) {
            throw new LocationNotFoundException("Location with code " + code + " not found");
        }
        locationRepository.trashLocation(code);
    }
}
