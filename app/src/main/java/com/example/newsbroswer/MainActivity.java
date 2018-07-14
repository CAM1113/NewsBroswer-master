package com.example.newsbroswer;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newsbroswer.adapters.ChannelAdapter;
import com.example.newsbroswer.adapters.NewsAdapter;
import com.example.newsbroswer.beans.channel.Channel;
import com.example.newsbroswer.beans.database_beans.DBChannel;
import com.example.newsbroswer.beans.news.News;
import com.example.newsbroswer.beans.news.news_config.NewsConfig;
import com.example.newsbroswer.interfaces.OnChannelClickListener;
import com.example.newsbroswer.interfaces.OnNewsClickListener;
import com.example.newsbroswer.interfaces.RequestChannelsOverListener;
import com.example.newsbroswer.interfaces.RequestNewsOverListener;
import com.example.newsbroswer.utils.DataBaseUtil;
import com.example.newsbroswer.utils.StaticFinalValues;
import com.example.newsbroswer.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.example.newsbroswer.utils.StaticFinalValues.NEWS_INTENT_CHANNEL_NAME;
import static com.example.newsbroswer.utils.StaticFinalValues.NEWS_INTENT_TITLE;

public class MainActivity extends AppCompatActivity {
    List<News> newsList =new ArrayList<>();
    List<Channel> channelListForRecycler = new ArrayList<>();
    List<Channel> channelListForUnChoosed=new ArrayList<>();

    RecyclerView channelRecyclerView;
    RecyclerView newsRecvyclerView;

    SwipeRefreshLayout refreshLayout;

    NewsAdapter newsAdapter;
    ChannelAdapter channelAdapter;

    //新闻列表的Manager
    LinearLayoutManager newsLinearLayoutManager;

    //新闻列表的最后一个可见项
    int lastVisibleItem=0;
    //加载进度条
    FrameLayout frameLayoutForJiaZai;

    //当前新闻页
    int pageNow=1;
    //当前频道
    String channelNow="";
    //当前标题
    String titleNow="";

    //用来记录是否在刷新新闻，如果正在刷新新闻，则不在响应下拉刷新，上拉加载和点击涮新
    boolean isGettingNews=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化数据库帮助类
        dbutil=new DataBaseUtil(this,"NewsBroswer",null,StaticFinalValues.DB_VERSION);
        db=dbutil.getWritableDatabase();
        //设置新的actionbar
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initImageViews();
        initTextViewForSearch();
        //设置频道的RecyclerView
        channelListForRecycler.add(new Channel("","推荐"));//设置推荐频道
        channelNow="";//推荐频道的频道名为“”
        pageNow=1;
        titleNow="";
        channelRecyclerView = (RecyclerView) findViewById(R.id.channelRecyclerView);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        channelRecyclerView.setLayoutManager(manager);
        channelAdapter=new ChannelAdapter(channelListForRecycler, new OnChannelClickListener() {
            @Override
            public void onClick(Channel c) {
                //处理频道的点击事件
                //如果点击推荐，则不需设置频道
               if(c.channelId.equals(""))
                {
                    initNews(new NewsConfig());
                    channelNow="";
                }
                else
                {
                    channelNow=c.getName();
                    pageNow=1;
                    initNews(new NewsConfig("",c.getName(),titleNow,pageNow+"",""));
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
                String htmls="<h2>"+news.title+"</h2>"
                        +"&nbsp;&nbsp;"+news.source+"&nbsp;&nbsp;&nbsp;"+news.getPubDate()+"</br>"
                        +news.html;
                intent.putExtra(StaticFinalValues.NEWS_INTENT_HTML,htmls);
                intent.putExtra(NEWS_INTENT_TITLE,news.title);

                //将频道名称放入intent，如果频道名称是"",放入名称为推荐
                if(channelNow.equals(""))
                {
                    intent.putExtra(NEWS_INTENT_CHANNEL_NAME,"推荐");
                }
                else
                {
                    intent.putExtra(NEWS_INTENT_CHANNEL_NAME,channelNow);
                }

                startActivity(intent);
            }
        });
        newsRecvyclerView.setAdapter(newsAdapter);
        //新闻列表的下拉加载的progressBar
        frameLayoutForJiaZai = (FrameLayout) findViewById(R.id.jiazaijindutiao);
        //设置新闻列表的上拉加载逻辑
        newsRecvyclerView .addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState){
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                        lastVisibleItem + 1 == newsAdapter.getItemCount() &&
                        !isGettingNews) {
                    //上拉加载的事件处理
                    frameLayoutForJiaZai.setVisibility(View.VISIBLE);
                    moreNews(new NewsConfig("",channelNow,titleNow,""+pageNow,""));
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
                {
                    initNews(new NewsConfig("",channelNow,titleNow,"",""));
                }
            }
        });

        //获取频道列表
        initChannels();
        //获取新闻列表
        initNews(new NewsConfig("",channelNow,titleNow,pageNow+"",""));

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
                        frameLayoutForJiaZai.setVisibility(View.GONE);

                        //更新新闻列表
                        if(newsList ==null||newsList.size()==0)
                        {
                            Toast.makeText(MainActivity.this, "没有该类型的新闻", Toast.LENGTH_SHORT).show();
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
                //获取到了新闻，更新列表
                MainActivity.this.newsList.clear();
                MainActivity.this.newsList.addAll(newsList);
                handler.sendEmptyMessage(UPDATE_NEWS);
            }
            @Override
            public void onFail(String errorInfo, int errorCode) {Log.e("CAM",errorInfo+"    "+errorCode);}
        }, config);
    }

    private void moreNews(NewsConfig config) {
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

            //频道获取成功后，要将列表的初始形态也放到数据库缓存中
            List<DBChannel> list=new ArrayList<>();
            //前一半放到列表中展示，后一半保留
            int icount;
            for(icount=num-1;icount>=num/2;icount--)
            {
                Channel c=cList.get(icount);
                channelListForRecycler.add(c);
                list.add(new DBChannel(c.channelId,c.channelNames,StaticFinalValues.DBCHANNEL_FOR_SHOW));
            }
            for(;icount>=0;icount--)
            {
                Channel c=cList.get(icount);
                channelListForUnChoosed.add(c);
                list.add(new DBChannel(c.channelId,c.channelNames,StaticFinalValues.DBCHANNEL_FOR_UNSHOW));
            }
            DBChannel.storeAllChannel(db,list);
            //发送消息，更新频道列表
            handler.sendEmptyMessage(UPDATE_CHANNELS);
        }
        @Override
        public void onFail(String errorInfo, int errorCode) {
            Log.e("CAM",errorInfo+"    "+errorCode);
        }
    };

    DataBaseUtil dbutil;
    SQLiteDatabase db;
    //初始化频道
    private void initChannels()
    {
        List<DBChannel> list=DBChannel.getAllChannel(db);
        if(list.size()==0)
        {
            //数据库中没有数据，从网络中获取
            Utils.getChannels(channelsOverListener);
        }
        else
        {
            //数据库中有数据，从数据库中获取
            for(DBChannel channel:list)
            {
                if(channel.isShow()==1)
                {
                    channelListForRecycler.add(new Channel(channel.getChannelId(),channel.getChannelNames()));
                }else
                {
                    channelListForUnChoosed.add(new Channel(channel.getChannelId(),channel.getChannelNames()));
                }
                Log.e("CAM",channel.getChannelNames());
            }
            updateChannelUI();
        }



        //从网络上获取频道信息
    }


    private void initTextViewForSearch()
    {
        final EditText edittextSearch= (EditText) findViewById(R.id.editView);
        edittextSearch.setText("");
        titleNow="";
        //用户点击搜索，开始查找新的新闻
        edittextSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    // 先隐藏键盘
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(MainActivity.this.getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    //进行搜索操作的方法，在该方法中可以加入mEditSearchUser的非空判断
                    titleNow=edittextSearch.getText()+"";
                    pageNow=1;
                    channelNow="";
                    channelAdapter.setFirstTrue();
                    channelAdapter.notifyDataSetChanged();
                    initNews(new NewsConfig("",channelNow,titleNow,pageNow+"",""));
                }
                return false;
            }
        });

        //根据用户的输入，改变titleNow的值
        edittextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                titleNow=edittextSearch.getText()+"";
            }
        });
    }

    //记录打开频道修改对话框后，是否修改了频道列表
    private boolean isChangeChannelForShow=false;
    private void initImageViews()
    {
        //点击刷新的控件
        ImageView refreshImageView= (ImageView) findViewById(R.id.refreshNews);
        refreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //刷新图片的点击事件，如果正在获取新闻，则不允许刷新
                if(!isGettingNews)
                {
                    refreshLayout.setRefreshing(true);
                    initNews(new NewsConfig("",channelNow,"","",""));
                }

            }
        });

        ImageView channelSetImageView= (ImageView) findViewById(R.id.imageView);
        channelSetImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog=new AlertDialog.Builder(MainActivity.this,R.style.MyDialogStyle)
                        .setCancelable(true)
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                               //锁住修改锁，下次打开必须解锁才能修改
                                changChannelForShowLock=true;
                                //判断频道列表是否被修改过
                                if(isChangeChannelForShow)
                                {
                                    isChangeChannelForShow=false;
                                    channelAdapter.setFirstTrue();
                                    channelAdapter.notifyDataSetChanged();
                                    channelNow="";
                                    //将改变存到数据库的缓存中
                                    List<DBChannel> list=new ArrayList<DBChannel>();
                                    for(Channel channel:channelListForRecycler)
                                    {
                                        if(channel.channelId.equals(""))
                                        {
                                            //不存第一项推荐
                                            continue;
                                        }
                                        list.add(new DBChannel(channel.channelId,channel.channelNames,1));
                                    }
                                    for(Channel channel:channelListForUnChoosed)
                                    {
                                        list.add(new DBChannel(channel.channelId,channel.channelNames,0));
                                    }
                                     DBChannel.storeAllChannel(db,list);
                                    //初始化新闻列表
                                    initNews(new NewsConfig("",channelNow,titleNow,"",""));
                                }
                            }
                        })
                        .create();
                dialog.show();
                WindowManager m = getWindowManager();
                Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
                android.view.WindowManager.LayoutParams params = dialog.getWindow().getAttributes();  //获取对话框当前的参数值、
                params.width = (int) (d.getWidth());    //宽度设置全屏宽度
                dialog.getWindow().setAttributes(params);
                dialog.getWindow().setGravity(Gravity.BOTTOM);//设置对话框打开位置
                dialog.getWindow().setContentView(getChannelDialogView());//设置对话框界面
            }
        });
    }

    //频道选择对话框
    AlertDialog dialog;
    //是否要修改频道列表
    boolean changChannelForShowLock =true;
    private static final int LINECOUNT=3;
    private static final int TEXTSIZE=17;
    private View getChannelDialogView() {
        ScrollView scrollView= (ScrollView) LayoutInflater.from(this).inflate(R.layout.my_channel_choose,null);
        GridView channelshowLayout=scrollView.findViewById(R.id.channel_show_layout);
        GridView channelchooseLayout=scrollView.findViewById(R.id.channel_choose_layout);
        Button button=scrollView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        final TextView channelLock=scrollView.findViewById(R.id.changeChannelliebiao);
        if(changChannelForShowLock)
        {
            channelLock.setTextColor(getResources().getColor(R.color.red));
        }
        else
        {
            channelLock.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
        channelLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changChannelForShowLock=!changChannelForShowLock;
                if(changChannelForShowLock)
                {
                    channelLock.setTextColor(getResources().getColor(R.color.red));
                }
                else
                {
                    channelLock.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
            }
        });

        final ChannelGrideViewAdapter adapter=new ChannelGrideViewAdapter(channelListForRecycler,true);
        channelshowLayout.setAdapter(adapter);

        final ChannelGrideViewAdapter adapter1=new ChannelGrideViewAdapter(channelListForUnChoosed,false);
        channelchooseLayout.setAdapter(adapter1);

        channelshowLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(changChannelForShowLock)
                {
                    return;
                }
                //第一个推荐不能移除
                if(i==0)
                {
                    return;
                }
                channelListForUnChoosed.add(channelListForRecycler.get(i));
                channelListForRecycler.remove(i);
                adapter.notifyDataSetChanged();
                adapter1.notifyDataSetChanged();
                updateChannelUI();
                //频道列表被修改
                isChangeChannelForShow=true;
            }
        });

        channelchooseLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(changChannelForShowLock)
                {
                    return;
                }
                channelListForRecycler.add(channelListForUnChoosed.get(i));
                channelListForUnChoosed.remove(i);
                adapter.notifyDataSetChanged();
                adapter1.notifyDataSetChanged();
                updateChannelUI();
                //频道列表被修改
                isChangeChannelForShow=true;
            }
        });
        return scrollView;
    }
    public class ChannelGrideViewAdapter extends BaseAdapter{
        List<Channel> list;
        boolean isForShow;
        ChannelGrideViewAdapter(List<Channel> l,boolean is)
        {
            list = l;
            isForShow = is;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            TextView textView=new TextView(MainActivity.this);
            textView.setText(list.get(i).getName());
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(TEXTSIZE);
            //textView.setBackground(getResources().getDrawable(R.drawable.bg_searchview));
            return textView;
        }
    }
}