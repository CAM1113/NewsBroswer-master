package com.example.newsbroswer.utils;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.newsbroswer.beans.database_beans.DBChannel;

import java.util.List;

/**
 * Created by 王灿 on 2018/7/13.
 */

public class DataBaseUtil extends SQLiteOpenHelper {

    public static final String CREATE_TABLE_CHANNEL="create table DBChannel ( " +
            "channelId text primary key," +
            "channelNames text," +
            "isShow integer)";

    public static final String CREATE_TABLE_USERINFO="create table DBUserInfo ( " +
            "name text primary key," +
            "nickname text," +
            "password text," +
            "sex text," +
            "profilePicture text," +
            "isLogin integer)";





    private Context context;


    public DataBaseUtil(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context=context;
    }

    public DataBaseUtil(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.e("CAM","in database onCreate");
        sqLiteDatabase.execSQL(CREATE_TABLE_CHANNEL);
        sqLiteDatabase.execSQL(CREATE_TABLE_USERINFO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("drop table if exists DBChannel");
        sqLiteDatabase.execSQL("drop table if exists DBUserInfo");
        onCreate(sqLiteDatabase);
    }
}
