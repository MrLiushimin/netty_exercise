package com.liushimin.hchat.pojo.vo;

/**
 * 返回给客户端的数据统一封装
 */
public class Result {

    private boolean success; //是否操作成功
    private String message;  //返回消息
    private Object result;   //返回给客户端的对象

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "Result{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", result=" + result +
                '}';
    }

    public Result(boolean success, String message, Object result) {
        this.success = success;
        this.message = message;
        this.result = result;
    }

    public Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
