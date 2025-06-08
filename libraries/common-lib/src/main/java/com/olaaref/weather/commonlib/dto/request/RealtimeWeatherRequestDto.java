package com.olaaref.weather.commonlib.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class RealtimeWeatherRequestDto {

    private String locationCode;
    @Range(min = -100, max = 100, message = "Temperature must be between -100 and 100 degrees Celsius")
    private double temperature;
    @Range(min = 0, max = 100, message = "Humidity must be between 0 and 100 percent")
    private double humidity;
    @Range(min = 0, max = 100, message = "Precipitation must be between 0 and 100 percent")
    private double precipitation;
    @Range(min = 0, max = 200, message = "Wind speed must be between 0 and 200 km/h")
    private double windSpeed;
    @NotBlank(message = "Weather status cannot be blank or empty")
    @Length(min = 3, max = 50, message = "Weather status must not exceed 3-50 characters")
    private String status;

}
