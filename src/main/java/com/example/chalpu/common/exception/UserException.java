package com.example.chalpu.common.exception;

/**
 * 사용자 관련 예외
 */
public class UserException extends BaseException {
    
    public UserException(ErrorMessage errorMessage) {
        super(errorMessage);
    }
}
