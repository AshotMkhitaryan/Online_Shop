package com.test.service.exceptions;

public class AuthenticationFailException extends IllegalArgumentException{
    public AuthenticationFailException(String msg) {
        super(msg);
    }
}
