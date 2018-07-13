package com.example.newsbroswer.beans.database_beans;

/**
 * Created by 王灿 on 2018/7/13.
 */

public class DBUserInfo {
    private String  name="";
    private String nickname="";
    private String password="";
    private String sex="";
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
}
