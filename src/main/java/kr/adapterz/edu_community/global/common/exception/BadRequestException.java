package kr.adapterz.edu_community.global.common.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends BusinessException {
    public BadRequestException(String code) {
        super(code, HttpStatus.BAD_REQUEST);
    }
}
