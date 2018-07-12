package com.example.newsbroswer.beans.news;

/**
 * Created by 王灿 on 2018/7/9.
 */

public class NewsResBody {
    public int ret_code;
    public NewsPageBean pagebean;


    public int getRet_code() {
        return ret_code;
    }

    public void setRet_code(int ret_code) {
        this.ret_code = ret_code;
    }

    public NewsPageBean getPagebean() {
        return pagebean;
    }

    public void setPagebean(NewsPageBean pagebean) {
        this.pagebean = pagebean;
    }
}
