package kr.adapterz.edu_community.global.common.exception;

import org.springframework.http.HttpStatus;

public class DuplicateException extends BusinessException {
    public DuplicateException(String code) {
        super(code, HttpStatus.CONFLICT);
    }
}
