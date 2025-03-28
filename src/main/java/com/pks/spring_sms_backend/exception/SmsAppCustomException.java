package com.pks.spring_sms_backend.exception;

import lombok.Data;

@Data
public class SmsAppCustomException  extends RuntimeException{
    private String errCode;

    public SmsAppCustomException(String message, String errCode) {
        super(message);
        this.errCode = errCode;
    }
}
