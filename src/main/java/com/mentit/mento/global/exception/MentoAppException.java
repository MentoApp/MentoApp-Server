package com.mentit.mento.global.exception;

import org.springframework.http.HttpStatus;

public class MentoAppException extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public MentoAppException() {
        super(ExceptionCode.INVALID_PARAMETER.getMessage());
        this.exceptionCode = ExceptionCode.INVALID_PARAMETER;
    }

    public MentoAppException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }

    public ExceptionCode getExceptionCode() {
        return this.exceptionCode;
    }

    public HttpStatus getHttpStatus() {
        return this.exceptionCode.getHttpStatus();
    }

    public String getErrorMessage() {
        return this.exceptionCode.getMessage();
    }

}
