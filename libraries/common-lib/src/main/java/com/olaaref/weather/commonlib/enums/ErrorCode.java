package com.olaaref.weather.commonlib.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    LOCATION_NOT_FOUND("LOC_SEARCH_001", "Location not found");

    private final String code;
    private final String message;
}