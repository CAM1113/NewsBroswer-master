package com.example.newsbroswer;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.newsbroswer.adapters.NewsAdapter;
import com.example.newsbroswer.beans.database_beans.DBHistory;
import com.example.newsbroswer.beans.database_beans.DBShouChang;
import com.example.newsbroswer.beans.database_beans.DBUserInfo;
import com.example.newsbroswer.beans.news.ImagesListItem;
import com.example.newsbroswer.beans.news.News;
import com.example.newsbroswer.interfaces.OnNewsClickListener;
import com.example.newsbroswer.utils.DataBaseUtil;
import com.example.newsbroswer.utils.StaticFinalValues;

import java.util.List;

import static com.example.newsbroswer.utils.StaticFinalValues.NEWS_INTENT_CHANNEL_NAME;
import static com.example.newsbroswer.utils.StaticFinalValues.NEWS_INTENT_TITLE;

public class HistoryActivity extends AppCompatActivity {
    SQLiteDatabase db;
    DBUserInfo userInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Log.e("CAM","History");
        db=new DataBaseUtil(this,"NewsBroswer",null, StaticFinalValues.DB_VERSION).getWritableDatabase();
        userInfo=DBUserInfo.getLoginUserInDB(db);
        initTitle();
    }

    private void initRecyclerView()
    {
        List<News> list = DBHistory.getNewsByName(db,userInfo.getName());
        LinearLayoutManager newsLinearLayoutManager=new LinearLayoutManager(this);
        RecyclerView newsRecvyclerView= (RecyclerView) findViewById(R.id.shouchang_recyclerView);
        newsRecvyclerView.setLayoutManager(newsLinearLayoutManager);
        NewsAdapter newsAdapter=new NewsAdapter(list, new OnNewsClickListener() {
            @Override
            public void onClick(News news) {
                startNewShowActivity(news);
            }
        });
        newsRecvyclerView.setAdapter(newsAdapter);
    }

    private void initTitle()
    {
        ImageView backImageView= (ImageView) findViewById(R.id.back_imageView);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ImageView deletsAllImageView= (ImageView) findViewById(R.id.delete);
        deletsAllImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog=new AlertDialog.Builder(HistoryActivity.this)
                        .setCancelable(true)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.e("CAM","删除");
                                DBHistory.deleteAllDBHistory(db);
                                initRecyclerView();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setMessage("是否清空历史记录？")
                        .create();
                dialog.show();
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        initRecyclerView();
    }

    private void startNewShowActivity(News news)
    {
        //点击新闻列表成功后的响应事件
        Intent intent=new Intent(HistoryActivity.this,NewShowActivity.class);
        //把点击的新闻传到新闻展示活动中去
        intent.putExtra(NEWS_INTENT_TITLE,news.title);
        intent.putExtra(StaticFinalValues.NEWS_INTENT_PUBLICDATE,news.getPubDate());
        intent.putExtra(StaticFinalValues.NEWS_INTENE_LINK,news.link);
        intent.putExtra(StaticFinalValues.NEWS_INTENT_HTML,news.html);
        //将频道名称放入intent，如果频道名称是"",放入名称为推荐
        intent.putExtra(NEWS_INTENT_CHANNEL_NAME,news.channelName);
        intent.putExtra(StaticFinalValues.NEWS_INTENT_CHANNEL_ID,"");
        intent.putExtra(StaticFinalValues.NEWS_INTENT_DESC,news.desc);
        int i=0;
        for(ImagesListItem ima:news.getImageurls())
        {
            i++;
            intent.putExtra("NEWS_INTENT_IMAGEURL"+i,ima.getUrl());
        }
        for(;i<3;i++)
        {
            intent.putExtra("NEWS_INTENT_IMAGEURL"+i,"");
        }
        intent.putExtra(StaticFinalValues.NEWS_INTENT_SOURCE,news.source);
        startActivity(intent);
    }
}
