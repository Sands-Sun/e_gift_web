package com.bayer.gifts.process.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

//@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<R> handleAllExceptions(Exception ex) {
        if(ex instanceof BaseException){
            BaseException be = (BaseException) ex;
            HttpStatus httpStatus = HttpStatus.valueOf(be.getErrorCode());
             R  r = R.error(be.errorCode, be.getErrorMessage());
            return new ResponseEntity<>(r,httpStatus);
        }
        return new ResponseEntity<>(R.error(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
