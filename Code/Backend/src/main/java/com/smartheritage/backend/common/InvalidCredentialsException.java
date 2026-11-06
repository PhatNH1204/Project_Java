package com.smartheritage.backend.common;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Email/username or password is incorrect");
    }
}
