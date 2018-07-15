package com.example.newsbroswer;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.newsbroswer.beans.UserLoginJson;
import com.example.newsbroswer.beans.database_beans.DBUserInfo;
import com.example.newsbroswer.utils.DataBaseUtil;
import com.example.newsbroswer.utils.StaticFinalValues;
import com.example.newsbroswer.utils.Utils;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;
import com.example.newsbroswer.utils.StaticFinalValues;

public class UserInfoManagerActivity extends AppCompatActivity {

    DBUserInfo userInfo;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_manager);
        //初始化用户信息
        db=new DataBaseUtil(this,"NewsBroswer",null,StaticFinalValues.DB_VERSION).getWritableDatabase();
        userInfo= DBUserInfo.getLoginUserInDB(db);
        initToolBar();
    }





    Toolbar toolbar;
    TextView denglu_zuce_TextView;
    CircleImageView touxinagImageView;
    TextView readTimeTextView;
    TextView yonghumingTextView;
    TextView denglutixing_Title;
    private void initToolBar()
    {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        denglutixing_Title= (TextView) findViewById(R.id.denglutixing_Title);
        ImageView backImageView= (ImageView) findViewById(R.id.backImageView);
        denglu_zuce_TextView= (TextView) findViewById(R.id.log_zuce_textView);
        touxinagImageView= (CircleImageView) findViewById(R.id.yonghutouxiang);
        readTimeTextView=(TextView) findViewById(R.id.readTimeTextView);
        yonghumingTextView= (TextView) findViewById(R.id.yonghuming);

        if(userInfo==null)
        {
            showToolBarUnLogin();
        }
        else
        {
            showToolBarLogin();
        }
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //点击登陆注册，弹出对话框进行登陆，在登陆页面可选择注册
        denglu_zuce_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDengLuDialog();
            }
        });

    }

    private void showToolBarLogin()
    {
        denglutixing_Title.setVisibility(View.INVISIBLE);
        Glide.with(UserInfoManagerActivity.this)
                .load(StaticFinalValues.NEWS_URL+userInfo.getProfilePicture()).into(touxinagImageView);
        denglu_zuce_TextView.setVisibility(View.GONE);
        yonghumingTextView.setVisibility(View.VISIBLE);
        yonghumingTextView.setText(userInfo.getNickname());
    }
    private void showToolBarUnLogin()
    {
        denglutixing_Title.setVisibility(View.VISIBLE);
        Glide.with(UserInfoManagerActivity.this)
                .load(StaticFinalValues.NEWS_URL+StaticFinalValues.DEFAULT_TOUXIANG).into(touxinagImageView);
        denglu_zuce_TextView.setVisibility(View.VISIBLE);
        yonghumingTextView.setVisibility(View.GONE);
        denglu_zuce_TextView.setText("登陆/注册");
    }



    //登陆对话框
    private AlertDialog loginDialog;
    //异步消息处理，登陆注册网络请求耗时
    private static final int LOGIN_SUCCESS=0;
    private static final int LOGIN_FAIL=1;
    private static final int NET_ERROR=2;
    Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case LOGIN_SUCCESS:
                    logSuccess();
                    break;
                case LOGIN_FAIL:
                    logFail();
                    break;
                case NET_ERROR:
                    netError();
                    break;
            }
        }
    };
    private void logSuccess()
    {
        login_ProgressBar.setVisibility(View.GONE);
        DBUserInfo.storeLoginUserInDB(db,userInfo);
        initToolBar();
    }
    private void logFail()
    {
        Toast.makeText(UserInfoManagerActivity.this,"账号或密码错误登陆失败",Toast.LENGTH_LONG).show();
        userInfo=null;
        login_ProgressBar.setVisibility(View.GONE);
    }
    private void netError()
    {
        Toast.makeText(UserInfoManagerActivity.this,"网络错误",Toast.LENGTH_LONG).show();
        userInfo=null;
        login_ProgressBar.setVisibility(View.GONE);
    }

    //登陆的用户名和密码
    String userName="";
    String password="";
    ProgressBar login_ProgressBar;
    CheckBox checkBox;
    private void showDengLuDialog()
    {
        View view= LayoutInflater.from(UserInfoManagerActivity.this).inflate(R.layout.login_layout,null);
        Button logBtn=view.findViewById(R.id.login_button);
        final EditText userNameTextView=view.findViewById(R.id.num_edit);
        final EditText passwordEditText=view.findViewById(R.id.pwd_edit);
        login_ProgressBar=view.findViewById(R.id.login_ProgressBar);
        checkBox=view.findViewById(R.id.jizumima);
        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userName=((userNameTextView.getText())+"").trim();
                password=(passwordEditText.getText()+"").trim();
                handlerLogin(userName,password);
            }
        });
        loginDialog =new AlertDialog.Builder(UserInfoManagerActivity.this,R.style.MyDialogStyle)
                .setCancelable(true)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        //对话框消失时的回调
                    }
                })
                .create();
        loginDialog.show();
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
        android.view.WindowManager.LayoutParams params = loginDialog.getWindow().getAttributes();  //获取对话框当前的参数值、
        params.width = (int) (d.getWidth());    //宽度设置全屏宽度
        loginDialog.getWindow().setAttributes(params);
        loginDialog.getWindow().setGravity(Gravity.BOTTOM);//设置对话框打开位置
        loginDialog.getWindow().setContentView(view);//设置对话框界面
    }




    private void handlerLogin(final String n, final String p)
    {
        login_ProgressBar.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserLoginJson result=null;
                String s= Utils.sendHttpRequest(StaticFinalValues.NEWS_URL+"user/login","POST","username="+n.trim()+"&password="+p.trim());
                Gson gson=new Gson();
                result= gson.fromJson(s,UserLoginJson.class);
                if(result==null)
                {
                    //result==null,网络故障
                    handler.sendEmptyMessage(NET_ERROR);
                    return;
                }

                if(result.getCode()==StaticFinalValues.LOGIN_RESULT_SUCCESS)
                {
                    //登陆成功
                    userInfo=result.getData();
                    if(checkBox.isChecked())
                    {
                        userInfo.setPassword(p);
                    }
                    else
                    {
                        //没有记住密码，密码设置为空
                        userInfo.setPassword("");
                    }
                    userInfo.setLogin(StaticFinalValues.DBUSERINFO_FOR_LOGIN);
                    handler.sendEmptyMessage(LOGIN_SUCCESS);
                    return;
                }
                if(result.getCode()==StaticFinalValues.LOGIN_RESULT_FAIL)
                {
                    //登陆失败
                    handler.sendEmptyMessage(LOGIN_FAIL);
                    return;
                }
            }
        }).start();

    }



}
