package com.olaaref.weather.commonlib.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.olaaref.weather.commonlib.dto.LocationDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "LOCATIONS")
public class Location {
    @Id
    @Column(name = "CODE", length = 12, nullable = false, unique = true)
    private String code;

    @Column(name = "COUNTRY_CODE", length = 2, nullable = false)
    private String countryCode;

    @Column(name = "COUNTRY_NAME", length = 128, nullable = false)
    private String countryName;

    @Column(name = "REGION_NAME", length = 128)
    private String regionName;

    @Column(name = "CITY_NAME", length = 128, nullable = false)
    private String cityName;

    @Column(name = "LATITUDE")
    private double latitude;

    @Column(name = "LONGITUDE")
    private double longitude;

    @Column(name = "ZIP_CODE", length = 30)
    private String zipCode;

    @Column(name = "TIME_ZONE", length = 30)
    private String timeZone;

    @Column(name = "ENABLED")
    private boolean enabled;

    @JsonIgnore
    @Column(name = "TRASHED")
    private boolean trashed;

    public LocationDto toLocationDto() {
        return new LocationDto(code, countryCode, countryName, regionName, cityName, latitude, longitude, zipCode, timeZone, enabled, trashed);
    }

}
