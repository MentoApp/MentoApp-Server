package com.mentit.mento.global.exception.customException;

import com.mentit.mento.global.exception.ExceptionCode;
import com.mentit.mento.global.exception.MentoAppException;

public class S3Exception extends MentoAppException {
    public S3Exception(ExceptionCode errorCode) {
        super(errorCode);
    }
}