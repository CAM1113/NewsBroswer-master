package com.example.newsbroswer.beans.json_beans;

import com.example.newsbroswer.beans.database_beans.DBUserInfo;

import java.util.List;

/**
 * Created by 王灿 on 2018/7/16.
 */

public class EvalutionResult {
    int code;
    List<Evalution> data;
    String message;

    public EvalutionResult() {}

    public EvalutionResult(int code, List<Evalution> data, String message) {
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

    public List<Evalution> getData() {
        return data;
    }

    public void setData(List<Evalution> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class Evalution
    {
        int id;//评论id
        DBUserInfo user;
        String time;//评论时间
        String newsURL;//新闻地址
        String content;//评论内容

        public Evalution(int id, DBUserInfo user, String time, String newsURL, String content, long likeCount) {
            this.id = id;
            this.user = user;
            this.time = time;
            this.newsURL = newsURL;
            this.content = content;
            this.likeCount = likeCount;
        }

        long likeCount;

        public DBUserInfo getUser() {
            return user;
        }

        public void setUser(DBUserInfo user) {
            this.user = user;
        }

        public long getLikeCount() {
            return likeCount;
        }

        public void setLikeCount(long likeCount) {
            this.likeCount = likeCount;
        }

        public Evalution() {

        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }


        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getNewsURL() {
            return newsURL;
        }

        public void setNewsURL(String newsURL) {
            this.newsURL = newsURL;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

    }

}
