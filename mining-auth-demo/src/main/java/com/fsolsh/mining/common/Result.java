package com.fsolsh.mining.common;

import java.io.Serializable;

public class Result implements Serializable {

    public static final int OK_CODE = 0, ERROR_CODE = -1;
    public static final String OK_MSG = "success", ERROR_MSG = "failure";
    private Integer code;
    private String msg;
    private Object data;

    private Result() {
    }

    public Result(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Result(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static Result OK() {
        return new Result(OK_CODE, OK_MSG);
    }

    public static Result OK(Object data) {
        return new Result(OK_CODE, OK_MSG, data);
    }

    public static Result error() {
        return new Result(ERROR_CODE, ERROR_MSG);
    }

    public static Result error(Object data) {
        return new Result(ERROR_CODE, ERROR_MSG, data);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
