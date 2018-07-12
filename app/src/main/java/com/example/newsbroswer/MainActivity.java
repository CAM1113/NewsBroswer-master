package com.example.newsbroswer;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.newsbroswer.adapters.ChannelAdapter;
import com.example.newsbroswer.adapters.NewsAdapter;
import com.example.newsbroswer.beans.channel.Channel;
import com.example.newsbroswer.beans.news.News;
import com.example.newsbroswer.beans.news.news_config.NewsConfig;
import com.example.newsbroswer.interfaces.OnChannelClickListener;
import com.example.newsbroswer.interfaces.OnNewsClickListener;
import com.example.newsbroswer.interfaces.RequestChannelsOverListener;
import com.example.newsbroswer.interfaces.RequestNewsOverListener;
import com.example.newsbroswer.utils.StaticFinalValues;
import com.example.newsbroswer.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<News> newsList =new ArrayList<>();
    List<Channel> channelListForRecycler = new ArrayList<>();
    List<Channel> channelListForUnChoosed=new ArrayList<>();
    //请求新闻频道列表结束时回调，新的线程
    RequestChannelsOverListener channelsOverListener=new RequestChannelsOverListener() {
        @Override
        public void onSuccess(List<Channel> cList) {
            if(cList==null||cList.size()==0)
                return;
            int num=cList.size();
            for(int i=num-1;i>=num/2;i--)
            {
                channelListForRecycler.add(cList.get(i));
                cList.remove(i);
            }
            channelListForUnChoosed.addAll(cList);
            Log.e("CAM",channelListForRecycler.size()+"");
            Log.e("CAM",channelListForUnChoosed.size()+"");
            updateChannelUI();
        }

        @Override
        public void onFail(String errorInfo, int errorCode) {
            Log.e("CAM",errorInfo);
        }
    };
    RecyclerView channelRecyclerView;
    RecyclerView newsRecvyclerView;


    String channelNow="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设置新的actionbar
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //初始化actionbar中的空间
        ImageView refreshImageView= (ImageView) findViewById(R.id.refreshNews);
        refreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initNews(new NewsConfig("",channelNow,"","",""));
                Log.e("CAM",channelNow+"?");
            }
        });


        //设置频道的RecyclerView
        channelListForRecycler.add(new Channel("","推荐"));//设置推荐频道
        channelNow="";//推荐频道的频道名为“”
        channelRecyclerView = (RecyclerView) findViewById(R.id.channelRecyclerView);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        channelRecyclerView.setLayoutManager(manager);
        channelAdapter=new ChannelAdapter(channelListForRecycler, new OnChannelClickListener() {
            @Override
            public void onClick(Channel c) {
                //处理频道的点击事件
                Toast.makeText(MainActivity.this, c.getName()+c.channelId, Toast.LENGTH_SHORT).show();
                if(c.channelId.equals(""))
                {
                    initNews(new NewsConfig());
                    channelNow="";
                }
                else
                {
                    channelNow=c.getName();
                    initNews(new NewsConfig("",c.getName(),"","",""));
                }
            }
        });
        channelRecyclerView.setAdapter(channelAdapter);

        //设置新闻的RecyclerView
        newsRecvyclerView= (RecyclerView) findViewById(R.id.news_recycler);
        LinearLayoutManager manager2=new LinearLayoutManager(this);
        newsRecvyclerView.setLayoutManager(manager2);
        newsAdapter=new NewsAdapter(newsList, new OnNewsClickListener() {
            @Override
            public void onClick(News news) {
                //点击新闻列表成功后的响应事件
                Intent intent=new Intent(MainActivity.this,NewShowActivity.class);
                //新闻界面可能用link连接打开，也可能直接显示html文档
                intent.putExtra(StaticFinalValues.NEWS_INTENE_LINK,news.link);
                intent.putExtra(StaticFinalValues.NEWS_INTENT_HTML,news.html);
                startActivity(intent);
                Log.e("CAM",news.getLink());
            }
        });
        newsRecvyclerView.setAdapter(newsAdapter);
        initNews(new NewsConfig());
        //网络请求获取频道列表
        Utils.getChannels(channelsOverListener);
    }

    //异步消息通信，当网络请求结束后，更新页面
    private static final int FINISH_NEWS =0x1;
    private static final int FINISH_CHANNEL =0x2;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                //新闻请求结束
                case FINISH_NEWS:
                    //防止多个线程刷新新闻列表，使用线程锁同步
                    synchronized(MainActivity.this)
                    {
                        if(newsList ==null)
                        {
                            Log.e("CAM","新闻列表为空");
                            return;
                        }
                        newsAdapter.notifyDataSetChanged();
                        break;
                    }
                //频道请求结束,更新频道UI
                case FINISH_CHANNEL:
                    if(channelListForRecycler ==null)
                    {
                        Toast.makeText(MainActivity.this,"频道列表为空",Toast.LENGTH_LONG).show();
                        return;
                    }
                    channelAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    private void updateNewsUI()
    {
        handler.sendEmptyMessage(FINISH_NEWS);
    }

    private void updateChannelUI()
    {
        handler.sendEmptyMessage(FINISH_CHANNEL);
    }

    NewsAdapter newsAdapter;
    ChannelAdapter channelAdapter;
    private void initNews(NewsConfig config)
    {
        newsRecvyclerView.scrollToPosition(0);
        //网络请求获取新闻列表
        Utils.getNews(new RequestNewsOverListener() {
            @Override
            public void onSuccess(List<News> newsList) {
                MainActivity.this.newsList.clear();
                MainActivity.this.newsList.addAll(newsList);
                updateNewsUI();
            }
            @Override
            public void onFail(String errorInfo, int errorCode) {}
        }, config);
    }

    private void moreNews(NewsConfig config)
    {
        //网络请求获取新闻列表
        Utils.getNews(new RequestNewsOverListener() {
            @Override
            public void onSuccess(List<News> newsList) {
                MainActivity.this.newsList.addAll(newsList);
                updateNewsUI();
            }
            @Override
            public void onFail(String errorInfo, int errorCode) {}
        }, config);
    }

    private void initChannels()
    {

    }

}
