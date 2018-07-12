package com.example.newsbroswer.beans.channel;

import java.util.List;

/**
 * Created by 王灿 on 2018/7/9.
 */

public class ChannelResBody {
    public int totalNum;
    public int ret_code;
    public List<Channel> channelList;

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public int getRet_code() {
        return ret_code;
    }

    public void setRet_code(int ret_code) {
        this.ret_code = ret_code;
    }

    public List<Channel> getChannelList() {
        return channelList;
    }

    public void setChannelList(List<Channel> channelList) {
        this.channelList = channelList;
    }
}
