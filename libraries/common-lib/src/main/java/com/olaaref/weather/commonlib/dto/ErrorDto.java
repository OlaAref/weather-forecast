package com.olaaref.weather.commonlib.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorDto(int status,
                       String errorCode,
                       String message,
                       List<String> details,
                       LocalDateTime timestamp) {
}
