package com.example.newsbroswer.utils;

import com.example.newsbroswer.beans.news.ImagesListItem;

import java.util.List;

/**
 * Created by 王灿 on 2018/7/12.
 */

public class StaticFinalValues {

    public String source;


    public static final String NEWS_INTENT_IMAGEURL1="NEWS_INTENT_IMAGEURL1" ;
    public static final String NEWS_INTENT_IMAGEURL2="NEWS_INTENT_IMAGEURL2" ;
    public static final String NEWS_INTENT_IMAGEURL3="NEWS_INTENT_IMAGEURL3" ;
    public static final String NEWS_INTENT_SOURCE="NEWS_INTENT_SOURCE" ;
    public static final String NEWS_INTENT_DESC="NEWS_INTENT_DESC" ;
    public static final String NEWS_INTENE_LINK="NEWS_INTENE_LINK";
    public static final String NEWS_INTENT_HTML="NEWS_INTENT_HTML";
    public static final String NEWS_INTENT_TITLE="NEWS_INTENT_TITLE";
    public static final String NEWS_INTENT_PUBLICDATE="NEWS_INTENT_PUBLICDATE";
    public static final String NEWS_INTENT_CHANNEL_NAME="NEWS_INTENT_CHANNEL_NAME";
    public static final String NEWS_INTENT_CHANNEL_ID="NEWS_INTENT_CHANNEL_ID";

    //数据库版号
    public static final int DB_VERSION=4;

    //频道展示的常量
    public static final int DBCHANNEL_FOR_SHOW=1;
    public static final int DBCHANNEL_FOR_UNSHOW=0;

    //用户是否登陆的常量
    public static final int DBUSERINFO_FOR_LOGIN=1;
    public static final int DBUSERINFO_FOR_UNLOGIN=0;

    //连接后台的url
    //public static final String NEWS_URL="http://10.55.160.193:10280";

    public static final String NEWS_URL="http://ewenlai.xin:20280";
    public static final String DEFAULT_TOUXIANG="/static/upload/monkey.jpg";

    public static final int LOGIN_RESULT_SUCCESS=0;
    public static final int LOGIN_RESULT_FAIL=1;


    public static final int REGISTER_RESULT_SUCCESS=0;
    public static final int REGISTER_RESULT_FAIL=1;




}
