package com.example.newsbroswer.beans;

import com.example.newsbroswer.beans.database_beans.DBUserInfo;

/**
 * Created by 王灿 on 2018/7/15.
 */

public class UserLoginJson {
    private int code=0;
    private DBUserInfo data;
    private String message="";


    public UserLoginJson() {}

    public UserLoginJson(int code, DBUserInfo data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DBUserInfo getData() {
        return data;
    }

    public void setData(DBUserInfo data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
