package com.olaaref.weather.exception;

import com.olaaref.weather.commonlib.dto.ErrorDto;
import com.olaaref.weather.commonlib.enums.ErrorCode;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@Log4j2
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(LocationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorDto handleWeatherException(LocationNotFoundException ex) {
        ex.printStackTrace();
        return new ErrorDto(HttpStatus.NOT_FOUND.value(),
                ErrorCode.LOCATION_NOT_FOUND.getCode(),
                ErrorCode.LOCATION_NOT_FOUND.getMessage(),
                null,
                LocalDateTime.now());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        ErrorDto errorDto = new ErrorDto(
                status.value(),
                ErrorCode.INVALID_REQUEST.getCode(),
                ErrorCode.INVALID_REQUEST.getMessage(),
                ex.getBindingResult().getFieldErrors().stream().map(this::mapFieldError).toList(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorDto, headers, status);
    }

    private String mapFieldError(FieldError fieldError) {
        return new StringBuilder()
                .append("Field: ")
                .append(fieldError.getField())
                .append(", Message: ")
                .append(fieldError.getDefaultMessage())
                .append(", Rejected value: ")
                .append(fieldError.getRejectedValue())
                .toString();
    }
}
