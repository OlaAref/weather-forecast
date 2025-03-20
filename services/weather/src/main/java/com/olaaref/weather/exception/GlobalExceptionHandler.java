package com.olaaref.weather.exception;

import com.olaaref.weather.commonlib.dto.ErrorDto;
import com.olaaref.weather.commonlib.enums.ErrorCode;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

@Log4j2
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LocationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorDto handleWeatherException(LocationNotFoundException ex) {
        ex.printStackTrace();
        return new ErrorDto(HttpStatus.NOT_FOUND.value(),
                ErrorCode.LOCATION_NOT_FOUND.getCode(),
                ErrorCode.LOCATION_NOT_FOUND.getMessage(),
                LocalDateTime.now());
    }

}
