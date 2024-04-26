package org.users.core;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleEntityNotFound(EntityNotFoundException ex) {
        // entity not found is a common exception, so log as warning
        log.warn(ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleInvalidFormat(HttpMessageNotReadableException ex) {
        // invalid format is a common exception, so log as warning
        log.warn(ex.getMessage());
        if (ex.getCause() instanceof InvalidFormatException formatEx) {
            if (formatEx.getCause() instanceof DateTimeParseException) {
                return ResponseEntity.badRequest().body("Invalid format for %s: %s".formatted(
                        getTemporalTypeName(formatEx.getTargetType()),
                        formatEx.getValue()
                ));
            }
        }
        return ResponseEntity.badRequest().build();
    }

    // better not to expose the actual type in the error message
    private String getTemporalTypeName(Class<?> type) {
        if (LocalDate.class.isAssignableFrom(type)) {
            return "date";
        } else if (LocalDateTime.class.isAssignableFrom(type)) {
            return "date-time";
        } else {
            return "temporal";
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex) {
        // log all other exceptions as errors
        log.error("An error occurred", ex);
        // returning manually to not expose in swagger
        // forbidden status code is used to not expose internal errors
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
