package com.example.newsbroswer.interfaces;

import com.example.newsbroswer.beans.news.News;
import com.example.newsbroswer.beans.news.NewsRequest;

import java.util.List;

/**
 * Created by 王灿 on 2018/7/10.
 */

public interface RequestNewsOverListener {
    void onSuccess(List<News> newsList);
    void onFail(String errorInfo,int errorCode);
}
