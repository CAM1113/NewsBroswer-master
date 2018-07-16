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
import com.example.newsbroswer.beans.json_beans.RequestResult;
import com.example.newsbroswer.beans.json_beans.UserLoginJson;
import com.example.newsbroswer.beans.database_beans.DBUserInfo;
import com.example.newsbroswer.utils.DataBaseUtil;
import com.example.newsbroswer.utils.StaticFinalValues;
import com.example.newsbroswer.utils.Utils;
import com.google.gson.Gson;

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
                openLoginDialog();
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
    private static final int LOGIN_SUCCESS=-5;
    private static final int LOGIN_FAIL=-4;
    private static final int NET_ERROR=-3;
    private static final int REGISTER_SUCCESS=-1;
    private static final int REGISTER_FAIL=-2;

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
                case REGISTER_FAIL:
                    registerFail();
                    break;
                case REGISTER_SUCCESS:
                    registerSuccess();
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
        loginDialog.dismiss();
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

    private void registerFail()
    {
        //登陆失败
        registerProgressBar.setVisibility(View.GONE);
        Toast.makeText(this, "该用户名已存在", Toast.LENGTH_SHORT).show();
    }
    private void registerSuccess()
    {
        //登录成功
        registerProgressBar.setVisibility(View.GONE);
        registerDialog.dismiss();
        Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();

    }





    //登陆的用户名和密码
    String userName="";
    String password="";
    ProgressBar login_ProgressBar;
    CheckBox checkBox;
    private void openLoginDialog()
    {
        View view= LayoutInflater.from(UserInfoManagerActivity.this).inflate(R.layout.login_layout,null);
        Button logBtn=view.findViewById(R.id.login_button);
        //初始化退出按钮
        ImageView dismissDialogImageView=view.findViewById(R.id.imageView4);
        dismissDialogImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginDialog.dismiss();
            }
        });

        //初始化注册按钮
        TextView registerTextView=view.findViewById(R.id.register_TexiView);
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRegisterDialog();
                loginDialog.hide();
            }
        });

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


    AlertDialog registerDialog;
    private void openRegisterDialog()
    {
        View view= LayoutInflater.from(UserInfoManagerActivity.this).inflate(R.layout.register,null);

        registerProgressBar=view.findViewById(R.id.register_ProgressBar);
        ImageView cancalImageView=view.findViewById(R.id.imageView4);
        cancalImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerDialog.dismiss();
            }
        });

        TextView nameTextView=view.findViewById(R.id.text1);
        TextView nickNameTextView=view.findViewById(R.id.text2);
        TextView passwordTextView=view.findViewById(R.id.text3);
        TextView confirmPasswordTextView=view.findViewById(R.id.text4);

        final String name=nameTextView.getText()+"";
        final String nickName=nickNameTextView.getText()+"";
        final String pwd=passwordTextView.getText()+"";
        final String cpwd=confirmPasswordTextView.getText()+"";

        Button registerButton=view.findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlerRegister(name,nickName,pwd, cpwd);
            }
        });


        registerDialog =new AlertDialog.Builder(UserInfoManagerActivity.this,R.style.MyDialogStyle)
                .setCancelable(true)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        loginDialog.show();
                    }
                })
                .create();
        registerDialog.show();
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
        android.view.WindowManager.LayoutParams params = registerDialog.getWindow().getAttributes();  //获取对话框当前的参数值、
        params.width = (int) (d.getWidth());    //宽度设置全屏宽度
        registerDialog.getWindow().setAttributes(params);
        registerDialog.getWindow().setGravity(Gravity.BOTTOM);//设置对话框打开位置
        registerDialog.getWindow().setContentView(view);//设置对话框界面

    }




    private void handlerLogin(final String n, final String p)
    {
        login_ProgressBar.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserLoginJson result=null;
                String s= Utils.sendHttpRequest(StaticFinalValues.NEWS_URL+"/user/login","POST","username="+n.trim()+"&password="+p.trim());
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


    ProgressBar registerProgressBar;
    private void handlerRegister(final String name, final String nickName, final String password, String comfirmPassword)
    {
        if(!isRegisterInfoOK(name,nickName,password, comfirmPassword))
        {
            Toast.makeText(this, "输入信息有误，请检查后重新输入", Toast.LENGTH_SHORT).show();
            return;
        }
        registerProgressBar.setVisibility(View.VISIBLE);
        //开启线程执行注册操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                String params="username="+name+"&password="+password+"&nickname="+nickName+"&sex="+"男";
                String s=Utils.sendHttpRequest(StaticFinalValues.NEWS_URL+"/user","POST",params);
                if(s==null)
                {
                   handler.sendEmptyMessage(NET_ERROR);
                    return;
                }
                Gson gson=new Gson();
                RequestResult result=gson.fromJson(s,RequestResult.class);
                if(result.getCode()==StaticFinalValues.REGISTER_RESULT_FAIL)
                {
                    handler.sendEmptyMessage(REGISTER_FAIL);
                    return;
                }
                else
                {
                    handler.sendEmptyMessage(REGISTER_SUCCESS);
                    return;
                }
            }
        }).start();




    }

    private boolean isRegisterInfoOK(String name,String nickName,String password,String comfirmPassword)
    {
        return true;
    }



}
