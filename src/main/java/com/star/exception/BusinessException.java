package com.star.exception;

/**
 * custom business exception
 *
 * @author star [137879802@qq.com]
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable t) {
        super(message, t);
    }

}
