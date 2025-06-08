package com.olaaref.weather.commonlib.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.olaaref.weather.commonlib.dto.LocationDto;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    /**
     * @PrimaryKeyJoinColumn: This annotation is used to specify that the primary key of the associated entity (RealtimeWeather) is the same as the primary key of the owning entity (Location).
     * It is used to establish a one-to-one relationship between the two entities based on the shared primary key.
     */
    @ToString.Exclude
    @JsonIgnore
    @OneToOne(mappedBy = "location", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private RealtimeWeather realtimeWeather;

    public Location(String code, String countryCode, String countryName, String regionName, String cityName, double latitude, double longitude, String zipCode, String timeZone, boolean enabled, boolean trashed) {
        this.code = code;
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.regionName = regionName;
        this.cityName = cityName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.zipCode = zipCode;
        this.timeZone = timeZone;
        this.enabled = enabled;
        this.trashed = trashed;
    }

    public LocationDto toLocationDto() {
        return new LocationDto(code, countryCode, countryName, regionName, cityName, latitude, longitude, zipCode, timeZone, enabled, trashed);
    }

}
