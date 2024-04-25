package org.users.core;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleEntityNotFound(EntityNotFoundException ex) {
        // entity not found is a common exception, so log as warning
        log.warn(ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ApiResponse(
            description = "Returns a list of constraint violations",
            responseCode = "400"
    )
    public ResponseEntity<String[]> handleConstraintDeclaration(ConstraintViolationException ex) {
        // constraint violation exception is a common exception, so log as warning
        log.warn(ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getConstraintViolations().stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .toArray(String[]::new));
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
