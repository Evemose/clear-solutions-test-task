package org.users.core.validation;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestControllerAdvice
@ComponentScan("org.users.core") // base package for component scan
@Slf4j
public class ValidationExceptionHandler {

    // this exception is thrown when entity validation fails
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ApiResponse(
            description = "Returns a list of constraint violations",
            responseCode = "400"
    )
    public ResponseEntity<String[]> handleConstraintViolation(ConstraintViolationException ex) {
        // constraint violation exception is a common exception, so log as warning
        log.warn(ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getConstraintViolations().stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .toArray(String[]::new));
    }

    // this exception is thrown when @Validated fails on the controller method parameter
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ApiResponse(
            description = "Returns a list of validation errors",
            responseCode = "400"
    )
    public ResponseEntity<String[]> handleValidation(MethodArgumentNotValidException ex) {
        // method argument not valid exception is a common exception, so log as warning
        log.warn(ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toArray(String[]::new));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String[]> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        if (ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException constraintEx) {
            // data integrity violation is a common exception, so log as warning
            log.warn(ex.getMessage());
            if (constraintEx.getMessage().contains("Unique index or primary key")) {
                var fieldName = extractFieldName(constraintEx.getMessage());
                return fieldName.map(s -> ResponseEntity.badRequest().body(
                        new String[]{s + " must be unique, but a duplicate was found"
                                + extractValue(constraintEx.getSQLException().getMessage()).map(v -> ": " + v).orElse("")}
                )).orElseGet(() -> ResponseEntity.badRequest().build());
            }
        }
        // usually this should be unreachable,
        // as DataIntegrityViolationException is a wrapper for ConstraintViolationException
        // because this is unexpected, log as error
        log.error(ex.getMessage());
        return ResponseEntity.badRequest().build();
    }

    /**
     * Extracts the field name from the message of DataIntegrityViolationException
     * @param message the message of the exception
     * @return the field name if found
     */
    private Optional<String> extractFieldName(String message) {
        var outerGroupMatcher = Pattern.compile("ON PUBLIC\\.\\w+\\([\\w\\s]+\\)").matcher(message);
        if (outerGroupMatcher.find()) {
            var snakeCase = outerGroupMatcher.group().split("\\(")[1].split("\\s")[0].strip().toLowerCase();
            return Optional.of(Arrays.stream(snakeCase.split("_"))
                    .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1))
                    .collect(Collectors.joining()));
        }
        return Optional.empty();
    }

    /**
     * Extracts the value from the message of DataIntegrityViolationException
     * @param message the message of the exception
     * @return the value if found
     */
    private Optional<String> extractValue(String message) {
        var outerGroupMatcher = Pattern.compile("\\sVALUES\\s*\\(\\s*/\\*\\s*\\d+\\s*\\*/.+\\)").matcher(message);
        if (outerGroupMatcher.find()) {
            return Optional.of(outerGroupMatcher.group().split("\\*/")[1].split("\\)")[0].strip());
        }
        return Optional.empty();
    }
}
