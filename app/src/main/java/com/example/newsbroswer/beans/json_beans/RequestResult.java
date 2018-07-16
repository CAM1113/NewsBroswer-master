package com.example.newsbroswer.beans.json_beans;

/**
 * Created by 王灿 on 2018/7/15.
 */

public class RequestResult {
    int code;
    String message;

    public RequestResult() {}

    public RequestResult(int code, String message) {
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
