package kr.adapterz.edu_community.global.common.exception;

import kr.adapterz.edu_community.global.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Business Exceptions
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

    // Integration Exception
    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MissingServletRequestPartException.class,
            HttpMessageNotReadableException.class,
            HttpRequestMethodNotSupportedException.class,
            MultipartException.class
            // add more as needed (4xx exceptions)
    })
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(Exception exception) {
        log.warn("Bad request: {}", exception.getMessage());

        String errorMessage = switch (exception) {
            case MissingServletRequestParameterException e -> "missing_parameter";
            case MissingServletRequestPartException e -> "missing_part";
            case HttpMessageNotReadableException e -> "invalid_request_body";
            case HttpRequestMethodNotSupportedException e -> "method_not_allowed";
            case MultipartException e -> "invalid_multipart";
            default -> "bad_request";
        };

        HttpStatus status = exception instanceof HttpRequestMethodNotSupportedException
                ? HttpStatus.METHOD_NOT_ALLOWED
                : HttpStatus.BAD_REQUEST;

        return ResponseEntity
                .status(status)
                .body(ApiResponse.of(status, errorMessage, null));
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