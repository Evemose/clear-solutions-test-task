package org.users.core.validation;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestControllerAdvice(basePackages = "org.users.core")
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

    // this handler is configured to work with English_USA.utf8 locale
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        if (ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException constraintEx) {
            // data integrity violation is a common exception, so log as warning
            log.warn(ex.getMessage());
            if (constraintEx.getMessage().contains("Unique index or primary key")) {
                var fieldName = extractFieldNameFromIndexViolation(constraintEx.getMessage());
                return fieldName.map(s -> ResponseEntity.badRequest().body(
                        s + " must be unique, but a duplicate was found"
                                + extractValueFromIndexViolation(constraintEx.getSQLException().getMessage()).map(v -> ": " + v).orElse("")
                )).orElseGet(() -> ResponseEntity.badRequest().build());
            } else if (constraintEx.getMessage().contains("violates foreign key constraint")) {
                return ResponseEntity.badRequest().body("Data integrity violation: invalid reference");
            } else if (constraintEx.getMessage().contains("value violates unique constraint")) {
                var fieldName = extractFieldNameFromUniqueViolation(constraintEx.getMessage());
                return fieldName.map(s -> ResponseEntity.badRequest().body(
                        s + " must be unique, but a duplicate was found"
                                + extractValueFromUniqueViolation(constraintEx.getMessage()).map(v -> ": " + v).orElse("")
                )).orElseGet(() -> ResponseEntity.badRequest().build());
            }
        }
        // usually this should be unreachable,
        // as DataIntegrityViolationException is a wrapper for ConstraintViolationException
        // because this is unexpected, log as error
        log.error(ex.getMessage());
        return ResponseEntity.badRequest().build();
    }

    private Optional<String > extractValueFromUniqueViolation(String message) {
        var outerGroupMatcher = Pattern.compile("Detail: Key \\(\\S+\\)=\\(\\S*\\)").matcher(message);
        if (outerGroupMatcher.find()) {
            return Optional.of(outerGroupMatcher.group().split("\\(")[2].split("\\)")[0].strip());
        }
        return Optional.empty();
    }

    private Optional<String> extractFieldNameFromUniqueViolation(String message) {
        var outerGroupMatcher = Pattern.compile("Detail: Key \\(\\S+\\)").matcher(message);
        return extractNameFromMatcher(outerGroupMatcher, "\\)");
    }


    private Optional<String> extractNameFromMatcher(Matcher outerGroupMatcher, String rightDelimiter) {
        if (outerGroupMatcher.find()) {
            var snakeCase = outerGroupMatcher.group().split("\\(")[1].split(rightDelimiter)[0].strip().toLowerCase();
            var pascalCase = Optional.of(Arrays.stream(snakeCase.split("_"))
                    .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1))
                    .collect(Collectors.joining()));
            return pascalCase.map(s -> s.substring(0, 1).toLowerCase() + s.substring(1)); // convert to camelCase
        }
        return Optional.empty();
    }

    /**
     * Extracts the field name from the message of DataIntegrityViolationException
     *
     * @param message the message of the exception
     * @return the field name if found
     */
    private Optional<String> extractFieldNameFromIndexViolation(String message) {
        var outerGroupMatcher = Pattern.compile("ON PUBLIC\\.\\S+\\([\\w\\s]+\\)").matcher(message);
        return extractNameFromMatcher(outerGroupMatcher, "\\s");
    }

    /**
     * Extracts the value from the message of DataIntegrityViolationException
     *
     * @param message the message of the exception
     * @return the value if found
     */
    private Optional<String> extractValueFromIndexViolation(String message) {
        var outerGroupMatcher = Pattern.compile("\\sVALUES\\s*\\(\\s*/\\*\\s*\\d+\\s*\\*/.+\\)").matcher(message);
        if (outerGroupMatcher.find()) {
            return Optional.of(outerGroupMatcher.group().split("\\*/")[1].split("\\)")[0].strip());
        }
        return Optional.empty();
    }
}
