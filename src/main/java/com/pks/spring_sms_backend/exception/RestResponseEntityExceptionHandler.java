package com.pks.spring_sms_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(SmsAppCustomException.class)
    public ResponseEntity<ErrorResponse> handleSmsAppCustomException(
            SmsAppCustomException exception){
        ErrorResponse errorResponse = ErrorResponse
                .builder()
                .errorCode(exception.getErrCode())
                .errorMessage(exception.getMessage())
                .build();

        return new ResponseEntity<>(errorResponse,
                HttpStatus.NOT_FOUND);

    }

}
