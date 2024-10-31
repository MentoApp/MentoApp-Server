package com.mentit.mento.global.exception.customException;

import com.mentit.mento.global.exception.ExceptionCode;
import com.mentit.mento.global.exception.MentoAppException;

public class BoardException extends MentoAppException {
    public BoardException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }
}
