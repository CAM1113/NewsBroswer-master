package com.example.newsbroswer;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
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

    SwipeRefreshLayout refreshLayout;

    NewsAdapter newsAdapter;
    ChannelAdapter channelAdapter;
    String channelNow="";

    //新闻列表的Manager
    LinearLayoutManager newsLinearLayoutManager;

    //新闻列表的最后一个可见项
    int lastVisibleItem=0;
    FrameLayout frameLayoutForXiaLaJiaZai;
    int pageNow=1;

    //用来记录是否在刷新新闻，如果正在刷新新闻，则不在响应下拉刷新，上拉加载和点击涮新
    boolean isGettingNews=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设置新的actionbar
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //初始化actionbar中的控件
        ImageView refreshImageView= (ImageView) findViewById(R.id.refreshNews);
        refreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isGettingNews)
                    initNews(new NewsConfig("",channelNow,"","",""));
                Log.e("CAM",channelNow+"?");
            }
        });

        frameLayoutForXiaLaJiaZai= (FrameLayout) findViewById(R.id.xialajindutiao);


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


        //设置下拉刷新的控件SwipeRefreshLayout
        refreshLayout= (SwipeRefreshLayout) findViewById(R.id.swip_refresh);
        refreshLayout.setColorSchemeResources(R.color.red);


        //设置新闻的RecyclerView
        newsRecvyclerView= (RecyclerView) findViewById(R.id.news_recycler);
        newsLinearLayoutManager=new LinearLayoutManager(this);
        newsRecvyclerView.setLayoutManager(newsLinearLayoutManager);
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

        //设置新闻列表的下拉加载
        newsRecvyclerView .addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState){
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                        lastVisibleItem + 1 == newsAdapter.getItemCount() &&
                        !isGettingNews) {
                    //上拉加载的事件处理
                    frameLayoutForXiaLaJiaZai.setVisibility(View.VISIBLE);
                    moreNews(new NewsConfig("",channelNow,"",""+pageNow,""));
                    }
                }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = newsLinearLayoutManager.findLastVisibleItemPosition();
            }
        });

        //设置新闻列表的下拉刷新
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!isGettingNews)
                    initNews(new NewsConfig("",channelNow,"","",""));
            }
        });





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
                        //取消获取新闻信息的限制，和加载动画
                        isGettingNews=false;
                        refreshLayout.setRefreshing(false);
                        frameLayoutForXiaLaJiaZai.setVisibility(View.GONE);
                        if(newsList ==null||newsList.size()==0)
                        {
                            Log.e("CAM","新闻列表为空");
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


    private void initNews(NewsConfig config)
    {
        pageNow=1;
        isGettingNews=true;
        refreshLayout.setRefreshing(true);
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
        pageNow++;
        isGettingNews=true;
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
