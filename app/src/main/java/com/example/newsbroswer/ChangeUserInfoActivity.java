package com.example.newsbroswer;

import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newsbroswer.beans.database_beans.DBUserInfo;
import com.example.newsbroswer.beans.json_beans.UserLoginJson;
import com.example.newsbroswer.utils.DataBaseUtil;
import com.example.newsbroswer.utils.StaticFinalValues;
import com.example.newsbroswer.utils.Utils;
import com.google.gson.Gson;

public class ChangeUserInfoActivity extends AppCompatActivity {

    DBUserInfo userInfo;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_user_info_activity);
        db=new DataBaseUtil(this,"NewsBroswer",null,StaticFinalValues.DB_VERSION).getWritableDatabase();
        userInfo=DBUserInfo.getLoginUserInDB(db);
        initTitle();
        initUserInfoShow();

    }

    private void initTitle()
    {
        ImageView back_ImageView= (ImageView) findViewById(R.id.back_ImageView);
        back_ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    //异步消息通信，当网络请求结束后，更新页面
    private static final int UPDATE_SUCCESS =0x111;
    private static final int UPDATE_FAIL =0x222;
    private static final int NET_ERROR=0x333;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                //修改成功
                case UPDATE_SUCCESS:
                    DBUserInfo.storeLoginUserInDB(db,userInfo);
                    initUserInfoShow();
                    Toast.makeText(ChangeUserInfoActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    break;
                case UPDATE_FAIL:
                    Toast.makeText(ChangeUserInfoActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    break;
                case NET_ERROR:
                    Toast.makeText(ChangeUserInfoActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
            }
        }
    };

    private void sendUpdateRequest(final String name, final String nickName, final String sex, final String oldPsw, final String newPsw)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String params="oldPassword="+oldPsw+"&newPassword="+newPsw+"&nickname="+nickName+"&sex="+sex;
                String result= Utils.sendHttpRequest(StaticFinalValues.NEWS_URL+"/user/"+name,"PUT",params);
                if(result==null)
                {
                    Log.e("CAM","网络错误");
                    handler.sendEmptyMessage(NET_ERROR);
                }
                else
                {
                    Gson g=new Gson();
                    UserLoginJson userLoginJson=g.fromJson(result,UserLoginJson.class);
                    if(userLoginJson.getCode()==0)
                    {
                        userInfo=userLoginJson.getData();
                        userInfo.setLogin(StaticFinalValues.DBUSERINFO_FOR_LOGIN);
                        handler.sendEmptyMessage(UPDATE_SUCCESS);
                    }
                    else
                    {
                        handler.sendEmptyMessage(UPDATE_FAIL);
                    }
                    Log.e("CAM",result);
                }
            }
        }).start();
    }

    private void initUserInfoShow()
    {
        LinearLayout touxiang = (LinearLayout) findViewById(R.id.touxiang);
        LinearLayout nicheng = (LinearLayout) findViewById(R.id.nicheng);
        LinearLayout xingbie = (LinearLayout) findViewById(R.id.xingbie);
        final LinearLayout xiugaimima = (LinearLayout) findViewById(R.id.xiugaimima);


        TextView niChengTextView = (TextView) findViewById(R.id.niChengTextView);
        niChengTextView.setText(userInfo.getNickname());

        TextView sex_now= (TextView) findViewById(R.id.sex_now);
        if(userInfo.getSex().equals("F")||userInfo.getSex().equals("f"))
        {
            sex_now.setText("女");
        }
        else
        {
            sex_now.setText("男");
        }


        touxiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xiuGaiTouXiang();
            }
        });

        nicheng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xiuGaiNiCheng();
            }
        });
        xingbie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xiuGaiXingBie();
            }
        });
        xiugaimima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xiuGaiMiMa();
            }
        });
    }

    AlertDialog dialog;

    private void xiuGaiTouXiang()
    {
        Toast.makeText(this, "暂未提供修改头像功能，敬请期待", Toast.LENGTH_SHORT).show();
    }

    private void xiuGaiNiCheng()
    {
        View v=LayoutInflater.from(this).inflate(R.layout.edit_nick,null);
        Button ok_button=v.findViewById(R.id.ok_Button);
        Button cancal_button=v.findViewById(R.id.cancal_Button);
        final EditText nickName_EditText=v.findViewById(R.id.nickName_EditText);
        cancal_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((nickName_EditText.getText()+"").trim().equals(""))
                {
                    Toast.makeText(ChangeUserInfoActivity.this, "昵称不能为空", Toast.LENGTH_SHORT).show();
                }
                sendUpdateRequest(userInfo.getName(),nickName_EditText.getText()+"",userInfo.getSex(),userInfo.getPassword(),userInfo.getPassword());
            }
        });
        dialog=new AlertDialog.Builder(this)
                .setCancelable(true)
                .create();
        dialog.setView(v);
        dialog.show();

    }

    private void xiuGaiMiMa()
    {
        View v= LayoutInflater.from(this).inflate(R.layout.editpwd_layout,null);
        final EditText pwd_old=v.findViewById(R.id.pwd_old);
        final EditText pwd_new1=v.findViewById(R.id.pwd_new_1);
        final EditText pwd_new2=v.findViewById(R.id.pwd_new_2);
        Button canacl_Button=v.findViewById(R.id.cancal_button);
        Button ok_Button=v.findViewById(R.id.ok_button);

        canacl_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        ok_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((pwd_new1.getText()+"").equals("")||(pwd_new2.getText()+"").equals("")||(pwd_old.getText()+"").equals(""))
                {
                    Toast.makeText(ChangeUserInfoActivity.this, "信息不全，修改失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!pwd_new1.getText().toString().equals(pwd_new2.getText().toString()))
                {
                    Toast.makeText(ChangeUserInfoActivity.this, "两次密码输入不一致，修改失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendUpdateRequest(userInfo.getName(),userInfo.getNickname(),userInfo.getSex(),pwd_old.getText()+"",pwd_new1.getText()+"");
            }
        });




        dialog=new AlertDialog.Builder(this)
                .setCancelable(true)
                .create();






        dialog.setView(v);
        dialog.show();
    }

    private void xiuGaiXingBie()
    {
        View v= LayoutInflater.from(this).inflate(R.layout.sex,null);
        dialog=new AlertDialog.Builder(this)
                .setCancelable(true)
                .create();
        RadioButton ra1=v.findViewById(R.id.ff);
        RadioButton ra2=v.findViewById(R.id.mm);
        if(userInfo.getSex().equals("F")||userInfo.getSex().equals("f"))
        {
            ra1.setChecked(true);
            ra2.setChecked(false);
        }
        else
        {
            ra1.setChecked(false);
            ra2.setChecked(true);
        }
        TextView ca=v.findViewById(R.id.cancal);
        ca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        ra1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUpdateRequest(userInfo.getName(),userInfo.getNickname(),"F",userInfo.getPassword(),userInfo.getPassword());
            }
        });
        ra2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUpdateRequest(userInfo.getName(),userInfo.getNickname(),"M",userInfo.getPassword(),userInfo.getPassword());
            }
        });
        dialog.setView(v);
        dialog.show();
    }

}
