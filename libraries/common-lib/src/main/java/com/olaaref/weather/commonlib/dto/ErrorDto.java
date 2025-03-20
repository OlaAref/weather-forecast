package com.olaaref.weather.commonlib.dto;

import java.time.LocalDateTime;

public record ErrorDto(int status,
                       String errorCode,
                       String message,
                       LocalDateTime timestamp) {
}
