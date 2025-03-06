package com.olaaref.weather.commonlib.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.olaaref.weather.commonlib.dto.LocationDto;
import jakarta.persistence.*;

@Entity
@Table(name = "LOCATIONS")
public record Location(
        @Id
        @Column(name = "CODE", length = 2, nullable = false, unique = true)
        String code,

        @Column(name = "COUNTRY_CODE", length = 2, nullable = false)
        String countryCode,

        @Column(name = "COUNTRY_NAME", length = 64, nullable = false)
        String countryName,

        @Column(name = "REGION_NAME", length = 128)
        String regionName,

        @Column(name = "CITY_NAME", length = 128, nullable = false)
        String cityName,

        @Column(name = "LATITUDE")
        double latitude,

        @Column(name = "LONGITUDE")
        double longitude,

        @Column(name = "ZIP_CODE", length = 30)
        String zipCode,

        @Column(name = "TIME_ZONE", length = 8)
        String timeZone,

        @Column(name = "ENABLED")
        boolean enabled,

        @JsonIgnore
        @Column(name = "TRASHED")
        boolean trashed
) {
    //add a default constructor to make JPA happy
    public Location() {
        this(null, null, null, null, null, 0.0, 0.0, null, null, true, false);
    }

    public LocationDto toLocationDto() {
        return new LocationDto(code(), countryCode(), countryName(), regionName(), cityName(), latitude(), longitude(), zipCode(), timeZone(), enabled(), trashed());
    }
}
