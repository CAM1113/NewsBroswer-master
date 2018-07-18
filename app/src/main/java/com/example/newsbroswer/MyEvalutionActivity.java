package com.example.newsbroswer;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.newsbroswer.adapters.MyEvalutionAdapter;
import com.example.newsbroswer.adapters.NewsAdapter;
import com.example.newsbroswer.beans.database_beans.DBMyEvalution;
import com.example.newsbroswer.beans.database_beans.DBShouChang;
import com.example.newsbroswer.beans.database_beans.DBUserInfo;
import com.example.newsbroswer.beans.json_beans.RequestResult;
import com.example.newsbroswer.beans.news.ImagesListItem;
import com.example.newsbroswer.beans.news.News;
import com.example.newsbroswer.interfaces.LongPressListener;
import com.example.newsbroswer.interfaces.OnNewsClickListener;
import com.example.newsbroswer.utils.DataBaseUtil;
import com.example.newsbroswer.utils.StaticFinalValues;
import com.example.newsbroswer.utils.Utils;
import com.google.gson.Gson;

import java.util.List;

import static com.example.newsbroswer.utils.StaticFinalValues.NEWS_INTENT_CHANNEL_NAME;
import static com.example.newsbroswer.utils.StaticFinalValues.NEWS_INTENT_TITLE;
import static com.google.gson.internal.UnsafeAllocator.create;

public class MyEvalutionActivity extends AppCompatActivity {


    SQLiteDatabase db;
    DBUserInfo userInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_evalution);
        db=new DataBaseUtil(this,"NewsBroswer",null, StaticFinalValues.DB_VERSION).getWritableDatabase();
        userInfo=DBUserInfo.getLoginUserInDB(db);
        initTitle();
    }

    private void initRecyclerView()
    {
        final List<DBMyEvalution> list = DBMyEvalution.getMyEvalution(db,userInfo.getName());
        LinearLayoutManager newsLinearLayoutManager=new LinearLayoutManager(this);
        RecyclerView newsRecvyclerView= (RecyclerView) findViewById(R.id.shouchang_recyclerView);
        newsRecvyclerView.setLayoutManager(newsLinearLayoutManager);
        MyEvalutionAdapter newsAdapter=new MyEvalutionAdapter(list, new OnNewsClickListener() {
            @Override
            public void onClick(News news) {
                startNewShowActivity(news);
            }
        },MyEvalutionActivity.this,new LongPressListener(){
            @Override
            public void doLongPress(final DBMyEvalution myEvalution) {
                AlertDialog alertDialog=new AlertDialog.Builder(MyEvalutionActivity.this)
                        .setCancelable(true)
                        .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                doDeleteMyEvalution(myEvalution.link,myEvalution.getId());
                                }
                        })
                        .setMessage("确定删除评论么？")
                        .create();
                alertDialog.show();
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
    }


    @Override
    protected void onResume() {
        super.onResume();
        initRecyclerView();
    }

    private void startNewShowActivity(News news)
    {
        //点击新闻列表成功后的响应事件
        Intent intent=new Intent(MyEvalutionActivity.this,NewShowActivity.class);
        //把点击的新闻传到新闻展示活动中去
        intent.putExtra(NEWS_INTENT_TITLE,news.title);
        intent.putExtra(StaticFinalValues.NEWS_INTENT_PUBLICDATE,news.getPubDate());
        intent.putExtra(StaticFinalValues.NEWS_INTENE_LINK,news.link);
        intent.putExtra(StaticFinalValues.NEWS_INTENT_HTML,news.html);
        //将频道名称放入intent，如果频道名称是"",放入名称为推荐
        intent.putExtra(NEWS_INTENT_CHANNEL_NAME,news.channelName);
        intent.putExtra(StaticFinalValues.NEWS_INTENT_CHANNEL_ID,"");
        intent.putExtra(StaticFinalValues.NEWS_INTENT_DESC,news.desc);
        int i=-1;
        String [] keys=new String[]{StaticFinalValues.NEWS_INTENT_IMAGEURL1,StaticFinalValues.NEWS_INTENT_IMAGEURL2,StaticFinalValues.NEWS_INTENT_IMAGEURL3};
        for(ImagesListItem ima:news.getImageurls())
        {
            i++;
            intent.putExtra(keys[i],ima.getUrl());
        }
        i++;
        for(;i<3;i++)
        {
            if(i==3)
                break;
            intent.putExtra(keys[i],"");
        }
        intent.putExtra(StaticFinalValues.NEWS_INTENT_SOURCE,news.source);
        startActivity(intent);
    }

    private static final int NET_ERROE=0x11;
    private static final int DELETE_FAIL=0x222;
    private static final int DELETE_SUCCESS=0x333;

    Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case NET_ERROE:
                    Toast.makeText(MyEvalutionActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    break;
                case DELETE_FAIL:
                    Toast.makeText(MyEvalutionActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                    break;
                case DELETE_SUCCESS:
                    initRecyclerView();
                    break;
            }
        }
    };

    private void doDeleteMyEvalution(final String link, final int id)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result=Utils.sendHttpRequest(StaticFinalValues.NEWS_URL+"/comment/"+id,"DELETE",null);
                Gson gson=new Gson();
                if(result==null)
                {
                    //网络错误
                    handler.sendEmptyMessage(NET_ERROE);
                    return;
                }
                Log.e("CAM",result);
                RequestResult requestResult=gson.fromJson(result,RequestResult.class);
                if(requestResult.getCode()!=0)
                {
                    //删除失败
                    handler.sendEmptyMessage(DELETE_FAIL);
                    return;
                }
                DBMyEvalution.deleteDBMyEvalution(db,userInfo.getName(),link);
                handler.sendEmptyMessage(DELETE_SUCCESS);
            }
        }).start();








    }
}
