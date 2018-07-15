package com.example.newsbroswer.beans.database_beans;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.annotations.SerializedName;

import static com.example.newsbroswer.utils.StaticFinalValues.DBUSERINFO_FOR_LOGIN;

/**
 * Created by 王灿 on 2018/7/13.
 */

public class DBUserInfo {
    @SerializedName("name")
    private String  name="";

    @SerializedName("nickname")
    private String nickname="";

    @SerializedName("password")
    private String password="";

    @SerializedName("sex")
    private String sex="";

    @SerializedName("profilePicture")
    private String profilePicture="";


    private int isLogin=0;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public int getLogin() {
        return isLogin;
    }

    public void setLogin(int login) {
        isLogin = login;
    }

    public DBUserInfo() {}

    public DBUserInfo(String name, String nickname, String password, String sex, String profilePicture, int isLogin) {
        this.name = name;
        this.nickname = nickname;
        this.password = password;
        this.sex = sex;
        this.profilePicture = profilePicture;
        this.isLogin = isLogin;
    }


    private static final String LoginUserInDB_SQL="select * from DBUserInfo where isLogin = "+DBUSERINFO_FOR_LOGIN;
    public static DBUserInfo getLoginUserInDB(SQLiteDatabase db)
    {
        DBUserInfo userInfo=null;
        Cursor cursor=db.rawQuery(LoginUserInDB_SQL,null);
        if(cursor.moveToFirst())
        {
            String  name=cursor.getString(cursor.getColumnIndex("name"));
            String nickname=cursor.getString(cursor.getColumnIndex("nickname"));
            String password=cursor.getString(cursor.getColumnIndex("password"));
            String sex=cursor.getString(cursor.getColumnIndex("sex"));
            String profilePicture=cursor.getString(cursor.getColumnIndex("profilePicture"));
            int isLogin=cursor.getInt(cursor.getColumnIndex("isLogin"));
            userInfo=new DBUserInfo(name,nickname,password,sex,profilePicture,isLogin);
        }
        return userInfo;
    }
}
