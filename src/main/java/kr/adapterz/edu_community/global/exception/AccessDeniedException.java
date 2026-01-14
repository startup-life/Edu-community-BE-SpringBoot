package kr.adapterz.edu_community.global.exception;

import org.springframework.http.HttpStatus;

public class AccessDeniedException extends BusinessException {
    public AccessDeniedException(String code) {
        super(code, HttpStatus.FORBIDDEN);
    }
}
