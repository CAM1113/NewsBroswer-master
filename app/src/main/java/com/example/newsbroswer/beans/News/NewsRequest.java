package com.example.newsbroswer.beans.news;

/**
 * Created by 王灿 on 2018/7/9.
 */

public class NewsRequest {
    public String showapi_res_error;
    public int showapi_res_code;
    public NewsResBody showapi_res_body;

    public String getShowapi_res_error() {
        return showapi_res_error;
    }

    public void setShowapi_res_error(String showapi_res_error) {
        this.showapi_res_error = showapi_res_error;
    }

    public int getShowapi_res_code() {
        return showapi_res_code;
    }

    public void setShowapi_res_code(int showapi_res_code) {
        this.showapi_res_code = showapi_res_code;
    }

    public NewsResBody getShowapi_res_body() {
        return showapi_res_body;
    }

    public void setShowapi_res_body(NewsResBody showapi_res_body) {
        this.showapi_res_body = showapi_res_body;
    }
}
