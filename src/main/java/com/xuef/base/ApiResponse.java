package com.xuef.base;

import java.io.Serializable;

/**
 * Created by moveb on 2018/5/19.
 * 统一响应对象
 */
public class ApiResponse<T> implements Serializable {
    private int code;// 状态码
    private String message; // 消息
    private T obj; // 返回数据

    /**
     * 方便前端判断请求的响应状态
     * @return
     */
    public boolean isSuccess(){
        return this.code == Status.SUCCESS.getCode();
    }

    public ApiResponse(){}
    public ApiResponse(int code, String message, T data){
        this.code = code;
        this.message = message;
        this.obj = data;
    }

    public static <T> ApiResponse ofSuccess(T data){
        return new ApiResponse(Status.SUCCESS.getCode(), Status.SUCCESS.getMessage(), data);
    }
    public static <T> ApiResponse ofMessage(int code, String message){
        return new ApiResponse(code, message, null);
    }
    /**
     * 封装状态信息
     */
    private enum Status{
        SUCCESS(200, "OK"),
        BAD_REQUEST(400, "BAD REQUEST"),
        INTERNAL_ERR(500, "INTERNAL ERROR"),
        NOT_VALID_PARAM(10001, "NOT VALID PARAM"),
        NOT_LOGIN(10000, "NOT LOGIN"),
        NOT_SUPPORTED_OPERATION(10003, "NOT SUPPORTED OPERATION");

        private int code;
        private String message;
        Status(int code, String message){
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }
}
