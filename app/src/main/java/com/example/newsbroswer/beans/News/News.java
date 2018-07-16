package com.example.newsbroswer.beans.news;

import java.util.List;

/**
 * Created by 王灿 on 2018/7/9.
 */

public class News {
    public String pubDate;
    public String channelName;
    public String title;
    public String desc;
    public List<ImagesListItem> imageurls;
    public String source;
    public String channelId;
    public String link;
    public String html;

    public News() {
    }

    public News(String pubDate, String channelName, String title, String desc, List<ImagesListItem> imageurls, String source, String channelId, String link, String html) {
        this.pubDate = pubDate;
        this.channelName = channelName;
        this.title = title;
        this.desc = desc;
        this.imageurls = imageurls;
        this.source = source;
        this.channelId = channelId;
        this.link = link;
        this.html = html;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<ImagesListItem> getImageurls() {
        return imageurls;
    }

    public void setImageurls(List<ImagesListItem> imageurls) {
        this.imageurls = imageurls;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
