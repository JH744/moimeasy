package com.kosa.moimeasy.user.exception;

public class SignupException extends RuntimeException {

    public SignupException(String message)
    {
        super(message);
    }
    public SignupException(String message, Throwable cause) {
        super(message, cause);
    }
}
