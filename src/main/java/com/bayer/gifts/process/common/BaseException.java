package com.bayer.gifts.process.common;

public class BaseException extends RuntimeException{

    protected int errorCode;
    protected String errorMessage;

    protected String errorDetail;


    public BaseException() {
        super();
    }
    public BaseException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

    public BaseException(String errorMessage, String errorDetail) {
        super(String.format("errorMessage: %s, errorDetail: %s", errorMessage, errorDetail));
        this.errorMessage = errorMessage;
        this.errorDetail = errorDetail;
    }


    public BaseException(int errorCode, String errorMessage, String errorDetail){
        super(String.format("errorCode: %s, errorMessage: %s, errorDetail: %s",
                errorCode, errorMessage, errorDetail));
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorDetail = errorDetail;
    }


    public BaseException(int errorCode, String errorMessage){
        super(String.format("errorCode: %s, errorMessage: %s", errorCode, errorMessage));
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public BaseException(int errorCode, String errorMessage, Throwable cause){
        super(String.format("errorCode: %s, errorMessage: %s", errorCode, errorMessage), cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorDetail() {
        return errorDetail;
    }

    public void setErrorDetail(String errorDetail) {
        this.errorDetail = errorDetail;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
