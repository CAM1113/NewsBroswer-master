package com.example.newsbroswer;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newsbroswer.beans.database_beans.DBUserInfo;
import com.example.newsbroswer.beans.json_beans.EvalutionResult;
import com.example.newsbroswer.beans.json_beans.RequestResult;
import com.example.newsbroswer.fragments.NewsFragment;
import com.example.newsbroswer.fragments.ShowEvalutionFragment;
import com.example.newsbroswer.utils.DataBaseUtil;
import com.example.newsbroswer.utils.StaticFinalValues;
import com.example.newsbroswer.utils.Utils;
import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;


public class NewShowActivity extends AppCompatActivity {

    DBUserInfo userInfo=null;
    SQLiteDatabase db=null;
    String newsURL;
    String htmls=null;
    NewsFragment webViewFragment;
    ShowEvalutionFragment evalutionFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_show);

        db=new DataBaseUtil(this,"NewsBroswer",null,StaticFinalValues.DB_VERSION).getWritableDatabase();
        userInfo=DBUserInfo.getLoginUserInDB(db);

        Intent intent = getIntent();
        newsURL = intent.getStringExtra(StaticFinalValues.NEWS_INTENE_LINK);
        String html=intent.getStringExtra(StaticFinalValues.NEWS_INTENT_HTML);
        htmls=getNewContent(html);

        initToolBar();
        initfoot();
        setFootOnUnEvalution();
        initFragmentLayout();




        Log.e("CAM","onCreate");
    }


    Button fabuBtn;
    ImageView souchangImageView;
    EditText evalutionEditText;
    ImageView chaKanImageView;
    private void initfoot()
    {
        fabuBtn= (Button) findViewById(R.id.fabu_button);
        souchangImageView= (ImageView) findViewById(R.id.shouchang_imageView);
        evalutionEditText= (EditText) findViewById(R.id.evalution_textView);
        chaKanImageView= (ImageView) findViewById(R.id.show_evalution_imageView);

        //点击查看的图片，获取评论
        chaKanImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(evalutionFragment==null)
                {
                    evalutionFragment=new ShowEvalutionFragment();
                    replaceFragment(evalutionFragment);
                }
            }
        });



        fabuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String evalu=evalutionEditText.getText()+"";
                clearEditTextFocus();
                //判断是否为空，执行发送逻辑
                if(evalu.trim().equals(""))
                {
                    Toast.makeText(NewShowActivity.this, "评论为空，发送失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(userInfo==null)
                {
                    //没有登陆，不能评论
                    Toast.makeText(NewShowActivity.this, "没有登陆，不能评论", Toast.LENGTH_SHORT).show();
                    return;
                }
                //发送评论
                sendEvalutionToHouTai(userInfo,evalu,newsURL);
                //重置界面
                evalutionEditText.setText("");
                clearEditTextFocus();
            }
        });

        evalutionEditText.setOnFocusChangeListener(new android.view.View.
                OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                    setFootOnEvalution();
                } else {
                    // 此处为失去焦点时的处理内容
                    setFootOnUnEvalution();
                }
            }
        });
    }


    public void clearEditTextFocus()
    {
        evalutionEditText.clearFocus();//取消焦点
        if(NewShowActivity.this.getCurrentFocus()!=null)
        {
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(NewShowActivity.this
                                    .getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);//关闭输入法;
        }
        Log.e("CAM","11111");
    }

    private void setFootOnEvalution()
    {
        fabuBtn.setVisibility(View.VISIBLE);
        chaKanImageView.setVisibility(View.GONE);
        souchangImageView.setVisibility(View.GONE);
    }

    private void setFootOnUnEvalution()
    {
        fabuBtn.setVisibility(View.GONE);
        chaKanImageView.setVisibility(View.VISIBLE);
        souchangImageView.setVisibility(View.VISIBLE);

    }


    Toolbar toolBar;
    ImageView backImageView;
    TextView channelName;
    private void initToolBar()
    {
        toolBar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        backImageView= (ImageView) findViewById(R.id.backImageView);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        channelName= (TextView) findViewById(R.id.denglutixing_Title);
        channelName.setText(getIntent().getStringExtra(StaticFinalValues.NEWS_INTENT_CHANNEL_NAME));

    }

    private void initFragmentLayout()
    {
        webViewFragment = new NewsFragment();
        replaceFragment(webViewFragment);
    }











    private static final int SEND_EVALUTION_SUCCESS=0;
    private static final int NET_ERROR=1;
    private static final int RECEIVE_EVALUTION_SUCCESS=3;
    private static final int EVALUTION_FAIL=4;//评论失败，每个用户对一条新闻只能发表一条评论
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case SEND_EVALUTION_SUCCESS:
                    //评论发送成功
                    Log.e("CAM","评论发送成功");
                    suaxinPinglun();
                    break;
                case NET_ERROR:
                    //网络错误
                    Log.e("CAM","网络错误");
                    break;
                case EVALUTION_FAIL:
                    Toast.makeText(NewShowActivity.this,"每个用户对一条新闻只能发表一条评论",Toast.LENGTH_LONG).show();
                    Log.e("CAM","每个用户对一条新闻只能发表一条评论");
            }

        }
    };

    private void sendEvalutionToHouTai(final DBUserInfo userInfo, final String s, final String newsURL)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String params="username="+userInfo.getName()+"&content="+s+"&newsURL="+newsURL;
                String result = Utils.sendHttpRequest(StaticFinalValues.NEWS_URL+"/comment","POST",params);
                if(result==null)
                {
                    handler.sendEmptyMessage(NET_ERROR);
                }
                else
                {
                    Gson gson=new Gson();
                    RequestResult rr=gson.fromJson(result,RequestResult.class);
                    if(rr.getCode()==1)
                    {
                        handler.sendEmptyMessage(EVALUTION_FAIL);
                        return;
                    }
                    handler.sendEmptyMessage(SEND_EVALUTION_SUCCESS);
                    Log.e("CAM",result);
                }
            }
        }).start();
    }

    private void replaceFragment(Fragment fragment)
    {
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        transaction.replace(R.id.fragment_layout,fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    public String getHtmls()
    {
        return htmls;
    }
    private String getNewContent(String htmltext){

        Document doc= Jsoup.parse(htmltext);
        Elements elements=doc.getElementsByTag("img");
        for (Element element : elements) {
            element.attr("width","100%").attr("height","auto");
        }
        return doc.toString();
    }


    public String getNewsURL()
    {
        return newsURL;
    }

    public void suaxinPinglun()
    {
        if(evalutionFragment!=null)
        {
            evalutionFragment.getEvalutionFronHouTai(newsURL);
        }

    }


    public void releaseEvalutionFragment()
    {
        evalutionFragment=null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        clearEditTextFocus();
        return super.onTouchEvent(event);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.e("CAM","onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("CAM","onStart");
    }
}
