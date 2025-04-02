package com.olaaref.weather.commonlib.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.olaaref.weather.commonlib.model.Location;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record LocationDto(
        @NotBlank(message = "Code is mandatory")
        @Length(min = 3, max = 12, message = "Code length must be between 3 and 12")
        String code,
        @NotNull(message = "Country code is mandatory")
        @Length(min = 2, max = 2, message = "Country code length must be 2")
        String countryCode,
        @NotNull(message = "Country name is mandatory")
        @Length(min = 1, max = 128, message = "Country name length must be between 1 and 128")
        String countryName,
        @Length(min = 1, max = 128, message = "Region name length must be between 1 and 128")
        String regionName,
        @NotNull(message = "City name is mandatory")
        @Length(min = 1, max = 128, message = "City name length must be between 1 and 128")
        String cityName,
        double latitude,
        double longitude,
        @Length(min = 1, max = 30, message = "Zip code length must be between 1 and 30")
        String zipCode,
        @Length(min = 1, max = 30, message = "Time zone length must be between 1 and 30")
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
