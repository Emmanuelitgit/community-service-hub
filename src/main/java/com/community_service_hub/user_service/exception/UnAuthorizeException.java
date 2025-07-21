package com.community_service_hub.user_service.exception;

public class UnAuthorizeException extends RuntimeException{
    public UnAuthorizeException(String message) {
        super(message);
    }

    public UnAuthorizeException(String message, Throwable cause) {
        super(message, cause);
    }
}
