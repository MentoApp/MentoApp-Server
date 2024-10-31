package com.mentit.mento.global.exception.customException;

import com.mentit.mento.global.exception.ExceptionCode;
import com.mentit.mento.global.exception.MentoAppException;

public class CommentException extends MentoAppException {
    public CommentException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }
}
