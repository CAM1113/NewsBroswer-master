package com.example.newsbroswer.interfaces;

import com.example.newsbroswer.beans.channel.Channel;
import com.example.newsbroswer.beans.channel.ChannelRequest;

import java.util.List;

/**
 * Created by 王灿 on 2018/7/10.
 */

public interface RequestChannelsOverListener {
    void onSuccess(List<Channel> channelList);
    void onFail(String errorInfo,int errorCode);
}
