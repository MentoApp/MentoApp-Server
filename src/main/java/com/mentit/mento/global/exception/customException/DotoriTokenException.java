package com.mentit.mento.global.exception.customException;

import com.mentit.mento.global.exception.ExceptionCode;
import com.mentit.mento.global.exception.MentoAppException;

public class DotoriTokenException extends MentoAppException {
    public DotoriTokenException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }
}
