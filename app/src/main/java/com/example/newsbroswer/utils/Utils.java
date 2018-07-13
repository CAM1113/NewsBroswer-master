package com.example.newsbroswer.utils;

import android.util.Log;

import com.example.newsbroswer.beans.channel.ChannelRequest;
import com.example.newsbroswer.beans.news.News;
import com.example.newsbroswer.beans.news.NewsRequest;
import com.example.newsbroswer.beans.news.news_config.NewsConfig;
import com.example.newsbroswer.interfaces.RequestChannelsOverListener;
import com.example.newsbroswer.interfaces.RequestNewsOverListener;
import com.google.gson.Gson;
import com.show.api.ShowApiRequest;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;

/**
 * Created by 王灿 on 2018/7/9.
 */

public class Utils {
    private static final String appid="69271";
    private static final String secret="57222480f9ef437b9063dc9fc22f8a0f";
    private static final int MAXRESULT=10;

    public static void  getNews(final RequestNewsOverListener listener, final NewsConfig config)
    {
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                String res=new ShowApiRequest( "http://route.showapi.com/109-35", appid, secret)
                        .addTextPara("channelId", config.channelId)
                        .addTextPara("channelName", config.channelName)
                        .addTextPara("title", config.title)
                        .addTextPara("page", config.page)
                        .addTextPara("needContent", "0")//不用正文
                        .addTextPara("needHtml", "1")//0不用正文的html,1用正文的html
                        .addTextPara("needAllList", "0")//不用全部信息
                        .addTextPara("maxResult", ""+MAXRESULT)//每页最多20条记录
                        .addTextPara("id", config.id)
                        .post();


                /*
                String res="{\n" +
                        "  \"showapi_res_error\": \"\",\n" +
                        "  \"showapi_res_code\": 0,\n" +
                        "  \"showapi_res_body\": {\n" +
                        "    \"ret_code\": 0,\n" +
                        "    \"pagebean\": {\n" +
                        "      \"allPages\": 1523,\n" +
                        "      \"contentlist\": [\n" +
                        "        {\n" +
                        "          \"id\": \"76c5ffc280ab3096e3adcd40037147be\",\n" +
                        "          \"pubDate\": \"2018-07-10 13:32:59\",\n" +
                        "          \"havePic\": true,\n" +
                        "          \"channelName\": \"体育最新\",\n" +
                        "          \"title\": \"埃及阿赫利官方：终止和华夏幸福有关阿扎罗的谈判\",\n" +
                        "          \"desc\": \"对阵分析赛事前瞻亚洲详盘欧赔足球赔率篮球比分直播nba比分直播网球比分直播。7月10日讯、埃及阿赫利俱乐部官方宣布，终止和河北华夏幸福有关队内前锋阿扎罗的谈判，这名摩洛哥前锋将继续留在球队效力。我们已经决定关闭出售阿扎罗的窗口，并告知教练组他将继续留在球队。阿赫利官网声明中写道。\",\n" +
                        "          \"imageurls\": [\n" +
                        "            {\n" +
                        "              \"height\": 0,\n" +
                        "              \"width\": 0,\n" +
                        "              \"url\": \"http://img.sportscn.com/q.jpg?https://c2.hoopchina.com.cn/uploads/star/event/images/180710/42dbc9953275d7c2787dcb62fd8804246e397804.png\"\n" +
                        "            }\n" +
                        "          ],\n" +
                        "          \"source\": \"华体网\",\n" +
                        "          \"channelId\": \"5572a109b3cdc86cf39001e6\",\n" +
                        "          \"nid\": \"8214007555185556730\",\n" +
                        "          \"link\": \"http://we.sportscn.com/viewnews-2557459.html\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"id\": \"e4ede5648b5f65d7fdb8a1069787d872\",\n" +
                        "          \"pubDate\": \"2018-07-10 12:46:10\",\n" +
                        "          \"havePic\": true,\n" +
                        "          \"channelName\": \"体育最新\",\n" +
                        "          \"title\": \"巴西野兽：出局不是运气不好 欧洲足球领先南美\",\n" +
                        "          \"desc\": \"今天下午，有足坛“野兽”之称的巴西前国脚胡里奥-塞萨尔-巴普蒂斯塔出现在红场，与小球迷进行互动。巴普蒂斯塔在活动结束之后也接受了媒体采访，谈到世界杯和足球发展趋势。至于巴西裔俄罗斯国脚马里奥-费尔南德斯在本届世界杯上的表现，巴普蒂斯塔不愿意过多评价。返回搜狐，查看更多。\",\n" +
                        "          \"imageurls\": [\n" +
                        "            {\n" +
                        "              \"height\": 0,\n" +
                        "              \"width\": 0,\n" +
                        "              \"url\": \"http://5b0988e595225.cdn.sohucs.com/images/20180708/ef1a3b4e1fb24da08ae80b81b9120c8c.jpeg\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "              \"height\": 0,\n" +
                        "              \"width\": 0,\n" +
                        "              \"url\": \"http://5b0988e595225.cdn.sohucs.com/images/20180708/7df319a87cb843de9c19d3616f434a6f.jpeg\"\n" +
                        "            }\n" +
                        "          ],\n" +
                        "          \"source\": \"搜狐体育\",\n" +
                        "          \"channelId\": \"5572a109b3cdc86cf39001e6\",\n" +
                        "          \"nid\": \"16517280667023523813\",\n" +
                        "          \"link\": \"http://sports.sohu.com/20180710/n542885907.shtml\"\n" +
                        "        }\n" +
                        "      ],\n" +
                        "      \"currentPage\": 1,\n" +
                        "      \"allNum\": 15222,\n" +
                        "      \"maxResult\": 10\n" +
                        "    }\n" +
                        "  }\n" +
                        "}";

                        */
                Gson gson=new Gson();
                NewsRequest newsRequest =gson.fromJson(res,NewsRequest.class);
                if(newsRequest.showapi_res_code==0)
                {
                    /*
                    for(News news:newsRequest.getShowapi_res_body().getPagebean().contentlist)
                    {
                        for(int i=news.getImageurls().size()-1;i>=0;i--)
                        {
                            //移除无法显示的图片连接
                            if(!isPicture(news.getImageurls().get(i).url))
                                news.getImageurls().remove(i);
                        }
                    }
                    */
                    listener.onSuccess(newsRequest.getShowapi_res_body().getPagebean().contentlist);
                }else
                {
                    listener.onFail(newsRequest.showapi_res_error,newsRequest.getShowapi_res_code());
                }
            }
        }).start();
    }

    public static void getChannels(final RequestChannelsOverListener listener)
    {
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                String res =new ShowApiRequest( "http://route.showapi.com/109-34", appid, secret)
                        .post();
                Gson gson=new Gson();
                ChannelRequest channelRequest =gson.fromJson(res,ChannelRequest.class);
                if(channelRequest.showapi_res_code==0)
                {
                    listener.onSuccess(channelRequest.getShowapi_res_body().channelList);

                }else
                {
                    listener.onFail(channelRequest.showapi_res_error,channelRequest.getShowapi_res_code());
                }
            }
        }).start();










    }


    private static boolean isPicture(String b)
    {
        String a;
        a=b.toUpperCase();//转换为大写
        if(a.indexOf(".BMP")!=-1||a.indexOf(".JPG")!=-1||a.indexOf(".JPEG")!=-1||a.indexOf(".GIF")!=-1||a.indexOf(".PSD")!=-1
                ||a.indexOf(".PNG")!=-1||a.indexOf(".TIFF")!=-1||a.indexOf(".TGA")!=-1||a.indexOf(".EPS")!=-1)
            return true;
        else
            return false;
    }


    public static String getMD5(String str) throws Exception {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            throw new Exception("MD5加密出现错误");
        }
    }
}
