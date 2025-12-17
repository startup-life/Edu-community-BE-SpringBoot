package kr.adapterz.edu_community.global.common.exception;

import org.springframework.http.HttpStatus;

public class AuthorizedException extends BusinessException {
    public AuthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
