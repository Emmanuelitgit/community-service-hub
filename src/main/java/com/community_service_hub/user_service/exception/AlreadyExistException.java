package com.community_service_hub.user_service.exception;

public class AlreadyExistException extends RuntimeException{
    public AlreadyExistException(String message) {
        super(message);
    }

    public AlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
