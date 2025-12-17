package kr.adapterz.edu_community.global.common.exception;

public class DuplicateException extends BusinessException {
    public DuplicateException(String message) {
        super(message, 409);
    }
}
