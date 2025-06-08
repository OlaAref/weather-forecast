package com.olaaref.weather.repository;

import com.olaaref.weather.commonlib.model.RealtimeWeather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RealtimeWeatherRepository extends JpaRepository<RealtimeWeather, String> {
    RealtimeWeather findByLocationCityNameAndLocationCountryCode(String cityName, String countryCode);
    RealtimeWeather findByLocationCodeAndLocationTrashedFalse(String locationCode);
}
