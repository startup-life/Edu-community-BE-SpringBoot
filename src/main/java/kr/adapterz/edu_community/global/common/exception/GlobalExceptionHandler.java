package kr.adapterz.edu_community.global.common.exception;

import kr.adapterz.edu_community.global.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Custom Business Exceptions
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BusinessException exception) {

        log.warn("{}: {}", exception.getClass().getSimpleName(), exception.getMessage());

        return ResponseEntity
                .status(exception.getStatus())
                .body(ApiResponse.of(exception.getStatus(), exception.getMessage(), null));
    }

    // 400 Bad Request - Validation Errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException exception) {

        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("Validation failed: {}", errors);

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.of(HttpStatus.BAD_REQUEST, "validation_error", errors));
    }

    // 400 Bad Request - Missing Path Variable, Query Parameter
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleMissingParam(
            MissingServletRequestParameterException exception) {

        String parameterName = exception.getParameterName();

        log.warn(
                "Missing request parameter: name={}, type={}",
                parameterName,
                exception.getParameterType()
        );

        Map<String, String> data = Map.of(
                parameterName, "required"
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.of(
                        HttpStatus.BAD_REQUEST,
                        "missing_request_parameter",
                        data
                ));
    }

    // 400 - Missing Request Body
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingRequestBody(
            org.springframework.http.converter.HttpMessageNotReadableException exception) {

        log.warn("Missing request body: {}", exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.of(
                        HttpStatus.BAD_REQUEST,
                        "missing_request_body",
                        null
                ));
    }

    // 405 Method Not Allowed
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex) {

        log.warn("Method not supported: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.of(
                        HttpStatus.METHOD_NOT_ALLOWED,
                        "method_not_allowed",
                        null
                ));
    }

    // 500 Internal Server Error - Unexpected Exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {

        log.error("Unexpected error", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "internal_server_error", null));
    }
}