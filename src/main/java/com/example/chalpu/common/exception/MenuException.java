package com.example.chalpu.common.exception;

/**
 * 메뉴 관련 예외
 */
public class MenuException extends BaseException {
    
    public MenuException(ErrorMessage errorMessage) {
        super(errorMessage);
    }
} 