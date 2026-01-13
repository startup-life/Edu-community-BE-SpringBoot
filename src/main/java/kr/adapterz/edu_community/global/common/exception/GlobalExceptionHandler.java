package kr.adapterz.edu_community.global.common.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import kr.adapterz.edu_community.global.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.Path;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Business Exceptions
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BusinessException exception) {

        log.warn("{}: {}", exception.getClass().getSimpleName(), exception.getCode());

        return ResponseEntity
                .status(exception.getStatus())
                .body(ApiResponse.of(exception.getCode(), null));
    }

    /**
     * [400] Bad Request 통합 처리
     * 1. HttpMessageNotReadableException: JSON 문법 오류, Body 누락, 파싱 실패
     * 2. MethodArgumentTypeMismatchException: 타입 불일치 (Long 자리에 String 등)
     * 3. MissingServletRequestParameterException: 필수 파라미터 누락
     * 4. ServletRequestBindingException: 그 외 바인딩 예외
     */
    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            ServletRequestBindingException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(Exception exception) {

        // 디버깅을 위해 서버 로그에는 상세 원인을 남겨두는 것이 좋습니다.
        log.warn("Bad Request: {}", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.of("BAD_REQUEST", null));
    }

    // 405 Method Not Allowed
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotAllowedException(
            Exception exception) {

        log.warn("Method not allowed: {}", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.of("METHOD_NOT_ALLOWED", null));
    }

    /**
     * [422] @RequestBody (DTO) 검증 실패 시
     * 예외: MethodArgumentNotValidException
     */
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
                .status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(ApiResponse.of("INVALID_INPUT", errors));
    }

    /**
     * [422] @Validated (RequestParam, PathVariable) 검증 실패 시
     * 예외: ConstraintViolationException
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleConstraintViolation(
            ConstraintViolationException exception) {

        Map<String, String> errors = new HashMap<>();

        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            // 1. 파라미터 이름 추출
            // violation.getPropertyPath()는 "메서드명.파라미터명" (예: checkEmailAvailability.email) 형태로 나옵니다.
            String fieldName = extractFieldName((Path) violation.getPropertyPath());

            // 2. 에러 메시지 (예: INVALID_FORMAT)
            String errorMessage = violation.getMessage();

            errors.put(fieldName, errorMessage);
        }

        log.warn("Parameter Validation Error: {}", errors);

        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_CONTENT) // 422
                .body(ApiResponse.of("INVALID_INPUT", errors));
    }

    // 500 Internal Server Error - Unexpected Exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception exception) {

        log.error("Unexpected error", exception);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.of("INTERNAL_SERVER_ERROR", null));
    }

    // ========== Helper Methods ==========
    // "메서드명.파라미터명" 형식에서 파라미터명만 추출하는 헬퍼 메서드
    private String extractFieldName(jakarta.validation.Path path) {
        // 1. 검증 경로 객체(Path)를 문자열로 변환합니다. (예: "checkEmailAvailability.email")
        String pathString = path.toString();

        // 2. 문자열 뒤에서부터 탐색하여 마지막 점(.)의 위치(인덱스)를 찾습니다.
        int lastDotIndex = pathString.lastIndexOf('.');

        // 3. 점(.)이 발견되었다면 (즉, "메서드명.파라미터명" 구조라면)
        if (lastDotIndex != -1) {
            // 4. 점 바로 다음 인덱스(+1)부터 끝까지 잘라내어 반환합니다. (결과: "email")
            return pathString.substring(lastDotIndex + 1);
        }

        // 5. 점(.)이 없다면 원본 문자열을 그대로 반환합니다.
        return pathString;
    }
}