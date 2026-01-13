package kr.adapterz.edu_community.global.exception;

import org.springframework.http.HttpStatus;

public class RateLimitException extends BusinessException {
    public RateLimitException(String code) {
        super(code, HttpStatus.TOO_MANY_REQUESTS);
    }
}
