package com.olaaref.weather.service;

import com.olaaref.weather.commonlib.dto.request.RealtimeWeatherRequestDto;
import com.olaaref.weather.commonlib.model.Location;
import com.olaaref.weather.commonlib.model.RealtimeWeather;
import com.olaaref.weather.exception.LocationNotFoundException;
import com.olaaref.weather.repository.RealtimeWeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RealtimeWeatherService {

    private final RealtimeWeatherRepository realtimeWeatherRepository;

    @Autowired
    public RealtimeWeatherService(RealtimeWeatherRepository realtimeWeatherRepository) {
        this.realtimeWeatherRepository = realtimeWeatherRepository;
    }

    public RealtimeWeather getWeatherByLocation(Location location) throws LocationNotFoundException {
        String cityName = location.getCityName();
        String countryCode = location.getCountryCode();

        RealtimeWeather weather = realtimeWeatherRepository.findByLocationCityNameAndLocationCountryCode(cityName, countryCode);
        if (weather == null) {
            throw new LocationNotFoundException("Weather data not found for location: " + cityName + ", " + countryCode);
        }
        return weather;
    }

    public RealtimeWeather getWeatherByLocationCode(String locationCode) throws LocationNotFoundException {
        RealtimeWeather weather = realtimeWeatherRepository.findByLocationCodeAndLocationTrashedFalse(locationCode);
        if (weather == null) {
            throw new LocationNotFoundException("Weather data not found for location code: " + locationCode);
        }
        return weather;
    }
    
    public RealtimeWeather updateWeatherByLocationCode(String locationCode, RealtimeWeatherRequestDto weatherDto) throws LocationNotFoundException {
        RealtimeWeather weather = realtimeWeatherRepository.findByLocationCodeAndLocationTrashedFalse(locationCode);
        if (weather == null) {
            throw new LocationNotFoundException("Weather data not found for location code: " + locationCode);
        }
        
        // Update the weather properties
        weather.setTemperature(weatherDto.getTemperature());
        weather.setHumidity(weatherDto.getHumidity());
        weather.setPrecipitation(weatherDto.getPrecipitation());
        weather.setWindSpeed(weatherDto.getWindSpeed());
        weather.setStatus(weatherDto.getStatus());
        weather.setLastUpdated(LocalDateTime.now());
        
        // Save and return the updated weather
        return realtimeWeatherRepository.save(weather);
    }
}
