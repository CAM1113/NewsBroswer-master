package com.example.newsbroswer.beans.channel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 王灿 on 2018/7/9.
 */

public class Channel {
    @SerializedName("channelId")
    public String channelId="";//频道Id
    @SerializedName("name")
    public String channelNames="";//频道名称

    public Channel() {}

    public Channel(String channelId, String channelNames) {
        this.channelId = channelId;
        this.channelNames = channelNames;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getName() {
        return channelNames;
    }

    public void setName(String name) {
        this.channelNames = name;
    }
}
