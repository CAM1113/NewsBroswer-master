package com.example.newsbroswer.beans.database_beans;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.newsbroswer.beans.channel.Channel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 王灿 on 2018/7/13.
 */

public class DBChannel {
    private String channelId="";//频道Id
    private String channelNames="";//频道名称
    private int isShow=0;//0表示不在展示，1表示在展示

    public DBChannel() {}

    public DBChannel(String channelId, String channelNames, int isShow) {
        this.channelId = channelId;
        this.channelNames = channelNames;
        this.isShow = isShow;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelNames() {
        return channelNames;
    }

    public void setChannelNames(String channelNames) {
        this.channelNames = channelNames;
    }

    public int isShow() {
        return isShow;
    }

    public void setShow(int show) {
        isShow = show;
    }

    public synchronized static void storeAllChannel(SQLiteDatabase db, List<DBChannel> list)
    {
        db.execSQL("delete from DBChannel");
        for(DBChannel channel: list)
        {
            channel.save(db);
        }
    }

    public void save(SQLiteDatabase db)
    {
        db.execSQL("insert into DBChannel(channelId,channelNames,isShow) values (?,?,?)",
                new String[]{channelId,channelNames,isShow+""});
    }

    public synchronized static List<DBChannel> getAllChannel(SQLiteDatabase db)
    {
        List<DBChannel> list=new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from DBChannel",null);
        if(cursor.moveToFirst())
        {
            do
            {
                String id=cursor.getString(cursor.getColumnIndex("channelId"));
                String na=cursor.getString(cursor.getColumnIndex("channelNames"));
                int isShow=cursor.getInt(cursor.getColumnIndex("isShow"));
                list.add(new DBChannel(id,na,isShow));
            }while(cursor.moveToNext());
        }
        cursor.close();
        return list;
    }









}
