package kr.adapterz.edu_community.global.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final int status;

    public BusinessException(String message, int status) {
        super(message);
        this.status = status;
    }

    public BusinessException(String message) {
        this(message, 400);
    }
}
