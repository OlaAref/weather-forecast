package com.olaaref.weather.commonlib.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.olaaref.weather.commonlib.model.Location;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record LocationDto(
        @NotBlank
        String code,
        @NotNull
        String countryCode,
        @NotNull
        String countryName,
        String regionName,
        @NotNull
        String cityName,
        double latitude,
        double longitude,
        String zipCode,
        String timeZone,
        boolean enabled,
        @JsonIgnore
        boolean trashed
) {

        public LocationDto() {
                this(null, "", "", null, "", 0.0, 0.0, null, null, true, false);
        }

        public Location toLocationEntity() {
                return new Location(code(), countryCode(), countryName(), regionName(), cityName(), latitude(), longitude(), zipCode(), timeZone(), enabled(), trashed());
        }
}
