package com.example.newsbroswer;
import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
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
import com.example.newsbroswer.views.*;

import java.util.ArrayList;
import java.util.List;
public class MainActivity extends AppCompatActivity {
    List<News> newsList =new ArrayList<>();
    List<Channel> channelListForRecycler = new ArrayList<>();
    List<Channel> channelListForUnChoosed=new ArrayList<>();

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

        initImageViews();




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
                //处理频道的点击事件,如果点击推荐，则不需设置频道
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
            }
        });
        newsRecvyclerView.setAdapter(newsAdapter);


        //新闻列表的下拉加载的progressBar
        frameLayoutForXiaLaJiaZai= (FrameLayout) findViewById(R.id.xialajindutiao);
        //设置新闻列表的下拉加载逻辑
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

        //获取频道列表
        initChannels();
        //获取新闻列表
        initNews(new NewsConfig());

    }

    //异步消息通信，当网络请求结束后，更新页面
    private static final int UPDATE_NEWS =0x1;
    private static final int UPDATE_CHANNELS =0x2;
    private static final int NO_MORE_NEWS=0x3;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                //新闻请求结束
                case UPDATE_NEWS:
                    //防止多个线程刷新新闻列表，使用线程锁同步
                    synchronized(MainActivity.this)
                    {
                        //取消获取新闻信息的限制，和加载动画
                        isGettingNews=false;
                        refreshLayout.setRefreshing(false);
                        frameLayoutForXiaLaJiaZai.setVisibility(View.GONE);

                        //更新新闻列表
                        if(newsList ==null||newsList.size()==0)
                        {
                            Toast.makeText(MainActivity.this, "没有获取到新闻", Toast.LENGTH_SHORT).show();
                            Log.e("CAM","新闻列表为空");
                        }
                        newsAdapter.notifyDataSetChanged();
                        break;
                    }
                //频道请求结束,更新频道UI
                case UPDATE_CHANNELS:
                    if(channelListForRecycler ==null)
                    {
                        Toast.makeText(MainActivity.this,"频道列表为空",Toast.LENGTH_LONG).show();
                        return;
                    }
                    channelAdapter.notifyDataSetChanged();
                    break;
                case NO_MORE_NEWS:
                    Toast.makeText(MainActivity.this, "没有更多新闻了", Toast.LENGTH_SHORT).show();
            }

        }
    };

    private void updateNewsUI()
    {
        handler.sendEmptyMessage(UPDATE_NEWS);
    }

    private void updateChannelUI()
    {
        handler.sendEmptyMessage(UPDATE_CHANNELS);
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
                if(newsList.size()>0)
                {
                    //获取到了新闻，更新列表
                    MainActivity.this.newsList.clear();
                    MainActivity.this.newsList.addAll(newsList);
                    handler.sendEmptyMessage(UPDATE_NEWS);
                }
                else
                {
                    //没有新闻了
                    handler.sendEmptyMessage(NO_MORE_NEWS);
                }
            }
            @Override
            public void onFail(String errorInfo, int errorCode) {Log.e("CAM",errorInfo+"    "+errorCode);}
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
                if(newsList.size()>0)
                {
                    MainActivity.this.newsList.addAll(newsList);
                    updateNewsUI();
                }
                else
                {
                    handler.sendEmptyMessage(NO_MORE_NEWS);
                }
            }
            @Override
            public void onFail(String errorInfo, int errorCode) {Log.e("CAM",errorInfo+"    "+errorCode);}
        }, config);
    }




    //请求新闻频道列表结束时回调，新的线程
    RequestChannelsOverListener channelsOverListener=new RequestChannelsOverListener() {
        @Override
        public void onSuccess(List<Channel> cList) {
            //获取成功
            if(cList==null||cList.size()==0)
                return;
            int num=cList.size();
            //前一半放到列表中展示，后一半保留
            for(int i=num-1;i>=num/2;i--)
            {
                channelListForRecycler.add(cList.get(i));
                cList.remove(i);
            }
            channelListForUnChoosed.addAll(cList);
            //发送消息，更新频道列表
            handler.sendEmptyMessage(UPDATE_CHANNELS);
        }
        @Override
        public void onFail(String errorInfo, int errorCode) {
            Log.e("CAM",errorInfo+"    "+errorCode);
        }
    };
    //初始化频道
    private void initChannels()
    {
        //从网络上获取频道信息
        Utils.getChannels(channelsOverListener);
    }


    private void initImageViews()
    {
        //点击刷新的控件
        ImageView refreshImageView= (ImageView) findViewById(R.id.refreshNews);
        refreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //刷新图片的点击事件，如果正在获取新闻，则不允许刷新
                if(!isGettingNews)
                    initNews(new NewsConfig("",channelNow,"","",""));
            }
        });

        ImageView channelSetImageView= (ImageView) findViewById(R.id.imageView);
        channelSetImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog=new AlertDialog.Builder(MainActivity.this,R.style.MyDialogStyle)
                        .setCancelable(true)
                        .create();
                dialog.show();
                WindowManager m = getWindowManager();
                Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
                android.view.WindowManager.LayoutParams params = dialog.getWindow().getAttributes();  //获取对话框当前的参数值、
                params.width = (int) (d.getWidth());    //宽度设置全屏宽度
                dialog.getWindow().setAttributes(params);
                dialog.getWindow().setGravity(Gravity.BOTTOM);
                dialog.getWindow().setContentView(getChannelDialogView());

            }
        });

    }






    private static final int LINECOUNT=3;
    private View getChannelDialogView()
    {
        ScrollView linearLayout= (ScrollView) LayoutInflater.from(this).inflate(R.layout.channel_choose,null);
        LinearLayout channelshowLayout=linearLayout.findViewById(R.id.channel_show_layout);
        LinearLayout channelchooseLayout=linearLayout.findViewById(R.id.channel_choose_layout);
        int i=0;
        int count=0;
        LinearLayout.LayoutParams params=
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,1);
        while(i<channelListForRecycler.size())
        {
            LinearLayout layout=new LinearLayout(MainActivity.this);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setLayoutParams(params);
            for(count=0;count<LINECOUNT&&i<channelListForRecycler.size();count++,i++)
            {
                TextView textView=new TextView(MainActivity.this);
                textView.setText(channelListForRecycler.get(i).getName());
                textView.setLayoutParams(params);
                textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
                textView.setTextSize(15);
                layout.addView(textView);
            }
            for(;count<LINECOUNT;count++)
            {

                TextView textView=new TextView(MainActivity.this);
                textView.setTextSize(15);
                textView.setLayoutParams(params);
                textView.setVisibility(View.INVISIBLE);
                textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
                layout.addView(textView);
            }
            channelshowLayout.addView(layout);
        }


        i=0;
        count=0;
        while(i<channelListForUnChoosed.size())
        {
            LinearLayout layout=new LinearLayout(MainActivity.this);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setLayoutParams(params);
            for(count=0;count<LINECOUNT&&i<channelListForUnChoosed.size();count++,i++)
            {
                TextView textView=new TextView(MainActivity.this);
                textView.setText(channelListForUnChoosed.get(i).getName());
                textView.setLayoutParams(params);
                textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
                textView.setTextSize(15);
                layout.addView(textView);
            }
            for(;count<LINECOUNT;count++)
            {
                TextView textView=new TextView(MainActivity.this);
                textView.setLayoutParams(params);
                textView.setTextSize(15);
                textView.setVisibility(View.INVISIBLE);
                textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
                layout.addView(textView);
            }
            channelchooseLayout.addView(layout);
        }

        return linearLayout;

    }


}
