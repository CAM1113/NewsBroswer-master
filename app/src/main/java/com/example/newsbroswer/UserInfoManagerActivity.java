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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
    private static final int SUCCESS=0;
    Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            DBUserInfo userInfo=result.getData();
            Log.e("CAM",result.getData().getName());
            Log.e("CAM",result.getData().getNickname());
            Log.e("CAM",result.getData().getPassword());
            Log.e("CAM",result.getData().getProfilePicture());
            Log.e("CAM",result.getData().getSex());
            Log.e("CAM",result.getData().getLogin()+"");

        }
    };




    //登陆的用户名和密码
    String userName="";
    String password="";
    private void showDengLuDialog()
    {
        View view= LayoutInflater.from(UserInfoManagerActivity.this).inflate(R.layout.login_layout,null);
        Button logBtn=view.findViewById(R.id.login_button);
        EditText userNameTextView=view.findViewById(R.id.num_edit);
        EditText passwordEditText=view.findViewById(R.id.pwd_edit);

        userName=((userNameTextView.getText())+"").trim();
        password=(passwordEditText.getText()+"").trim();
        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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


    UserLoginJson result=null;
    private void handlerLogin(String n,String p)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {

                String s= Utils.sendHttpRequest("http://10.55.160.144:10280/user/login","POST","username=ewenlai&password=mimimi123");
                Gson gson=new Gson();
                result= gson.fromJson(s,UserLoginJson.class);
                handler.sendEmptyMessage(0x123);
            }
        }).start();

    }



}
