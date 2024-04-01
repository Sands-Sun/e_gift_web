package com.bayer.gifts.process.common;

import lombok.Data;

@Data
public class R <T>{

    private static final long serialVersionUID = 1L;

    private T data;
    private String message;
    private int code;

    public R() {
        this.code = 0;
        this.message = "success";
    }

    public R(String message) {
        this.code = 0;
        this.message = "success";
    }

    public R(T data) {
        this.code = 0;
        this.message = "success";
        this.data = data;
    }

    public R(String message, T data) {
        this.code = 0;
        this.message = message;
        this.data = data;
    }

    public R(int code, String message) {
        this.code = code;
        this.message = message;
    }


    public static <T> R<T> error() {
        return error(500, "未知异常，请联系管理员");
    }

    public static <T> R<T> error(String message) {
        return error(500, message);
    }

    public static <T> R<T> error(int code, String message) {
        return  new R<>(code, message);

    }

    public static <T> R<T> ok(String message, T data) {
        return new R<>(message,data);
    }

    public static <T> R<T> ok(T data) {
        return new R<>(data);
    }

    public static <T> R<T> ok(String message) {
        return new R<>(message);
    }

    public static <T> R<T> ok() {
        return new R<>();
    }

}
