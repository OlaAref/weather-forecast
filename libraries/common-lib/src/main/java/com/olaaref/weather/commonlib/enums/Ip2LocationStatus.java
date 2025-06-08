package com.olaaref.weather.commonlib.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Ip2LocationStatus {

    OK("OK", "Location retrieved successfully from database."),
    EMPTY_IP_ADDRESS("EMPTY_IP_ADDRESS", "IP address cannot be blank."),
    INVALID_IP_ADDRESS("INVALID_IP_ADDRESS", "Invalid IP address."),
    MISSING_FILE("MISSING_FILE", "Invalid database path."),
    IPV6_NOT_SUPPORTED("IPV6_NOT_SUPPORTED", "This BIN does not contain IPv6 data."),
    UNKNOWN_ERROR("UNKNOWN_ERROR", "Unknown error.");

    private final String status;
    private final String message;

}
