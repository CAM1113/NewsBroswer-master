package com.example.newsbroswer.beans.news;

import java.util.List;

/**
 * Created by 王灿 on 2018/7/9.
 */

public class NewsPageBean {
    public int allPages;
    public List<News> contentlist;
    public int currentPage;
    public int allNum;
    public int maxResult;

    public int getAllPages() {
        return allPages;
    }

    public void setAllPages(int allPages) {
        this.allPages = allPages;
    }

    public List<News> getContentlist() {
        return contentlist;
    }

    public void setContentlist(List<News> contentlist) {
        this.contentlist = contentlist;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getAllNum() {
        return allNum;
    }

    public void setAllNum(int allNum) {
        this.allNum = allNum;
    }

    public int getMaxResult() {
        return maxResult;
    }

    public void setMaxResult(int maxResult) {
        this.maxResult = maxResult;
    }
}
