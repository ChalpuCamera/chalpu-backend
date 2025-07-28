package com.example.chalpu.common.exception;

public class RefreshTokenException extends BaseException {
    public RefreshTokenException(ErrorMessage errorMessage) {
        super(errorMessage);
    }
}
