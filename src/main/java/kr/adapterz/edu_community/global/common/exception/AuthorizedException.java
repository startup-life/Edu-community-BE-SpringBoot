package kr.adapterz.edu_community.global.common.exception;

public class AuthorizedException extends BusinessException {
    public AuthorizedException(String message) {
        super(message, 401);
    }
}
