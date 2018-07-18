package com.example.newsbroswer.beans.json_beans;

import com.example.newsbroswer.beans.database_beans.DBUserInfo;

/**
 * Created by 王灿 on 2018/7/18.
 */

public class SendEvalutionResult {
    private int code=0;
    private int data;
    private String message="";

    public SendEvalutionResult() {
    }


    public SendEvalutionResult(int code, int id, String message) {
        this.code = code;
        this.data = id;
        this.message = message;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getId() {
        return data;
    }

    public void setId(int id) {
        this.data = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
