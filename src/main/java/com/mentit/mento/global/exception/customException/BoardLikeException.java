package com.mentit.mento.global.exception.customException;

import com.mentit.mento.global.exception.ExceptionCode;
import com.mentit.mento.global.exception.MentoAppException;

public class BoardLikeException extends MentoAppException {
    public BoardLikeException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }
}
