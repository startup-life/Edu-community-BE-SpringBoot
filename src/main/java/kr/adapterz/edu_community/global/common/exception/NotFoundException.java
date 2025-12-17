package kr.adapterz.edu_community.global.common.exception;

public class NotFoundException extends BusinessException {
    public NotFoundException(String message) {
        super(message, 404);
    }
}
