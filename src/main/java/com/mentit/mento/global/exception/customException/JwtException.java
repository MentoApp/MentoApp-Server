package com.mentit.mento.global.exception.customException;

import com.mentit.mento.global.exception.ExceptionCode;
import com.mentit.mento.global.exception.MentoAppException;

public class JwtException extends MentoAppException {
    public JwtException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }

    public JwtException() {
        super(ExceptionCode.INVALID_PARAMETER);
    }
}
