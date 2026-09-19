package com.antoniodias.tasklist.shared.web;

import com.antoniodias.tasklist.shared.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String NOT_NULL_VIOLATION = "23502";
    private static final Pattern COLUMN_NAME = Pattern.compile("column \"(\\w+)\"");

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError notFound(ResourceNotFoundException e) {
        log.warn("{}", e.getMessage());
        return ApiError.of(e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> dataIntegrity(DataIntegrityViolationException e) {
        Throwable cause = e.getMostSpecificCause();
        if (cause instanceof SQLException sql && NOT_NULL_VIOLATION.equals(sql.getSQLState())) {
            String column = columnName(sql.getMessage());
            log.warn("Missing required field: {}", column);
            return ResponseEntity.badRequest().body(ApiError.of("Missing required field: " + column));
        }
        log.warn("Data integrity violation: {}", e.getMostSpecificCause().getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiError.of("Data integrity violation"));
    }

    @ExceptionHandler(PropertyReferenceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError invalidSort(PropertyReferenceException e) {
        log.warn("Invalid sort property: {}", e.getPropertyName());
        return ApiError.of("Invalid sort property: " + e.getPropertyName());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError internal(Exception e) {
        log.error("Unhandled exception", e);
        return ApiError.of("Unexpected error");
    }

    private static String columnName(String message) {
        Matcher matcher = COLUMN_NAME.matcher(message);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "unknown";
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        HttpStatus status = HttpStatus.valueOf(statusCode.value());
        log.warn("{} {}", status.value(), ex.getMessage());
        return ResponseEntity.status(status).headers(headers).body(ApiError.of(status.getReasonPhrase()));
    }
}
