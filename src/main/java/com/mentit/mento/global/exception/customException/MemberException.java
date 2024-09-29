package com.mentit.mento.global.exception.customException;

import com.mentit.mento.global.exception.ExceptionCode;
import com.mentit.mento.global.exception.MentoAppException;

public class MemberException extends MentoAppException {
    public MemberException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }
}
