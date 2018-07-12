package com.example.newsbroswer.beans.news.news_config;

/**
 * Created by 王灿 on 2018/7/10.
 */

public class NewsConfig {
    public String channelId="";//频道id，可选，必须精确匹配
    public String channelName="";//新闻频道名称，可选，模糊匹配
    public String title="";//新闻标题名称，可选，模糊匹配
    public String page="1";//页数，可选，默认1，每页最多20条记录
    public String id="";//新闻id，用此信息可返回一条新闻记录，精确匹配，可选
    public NewsConfig() {}

    public NewsConfig(String channelId, String channelName, String title, String page, String id) {
        this.channelId = channelId;
        this.channelName = channelName;
        this.title = title;
        this.page = page;
        this.id = id;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
