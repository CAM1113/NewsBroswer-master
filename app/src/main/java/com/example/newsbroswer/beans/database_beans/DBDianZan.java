package com.example.newsbroswer.beans.database_beans;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import static com.example.newsbroswer.utils.StaticFinalValues.DBUSERINFO_FOR_LOGIN;

/**
 * Created by 王灿 on 2018/7/16.
 */

public class DBDianZan {
    String newsURL;
    String name;

    public DBDianZan() {}

    public DBDianZan(String newsURL, String name) {
        this.newsURL = newsURL;
        this.name = name;
    }

    public String getNewsURL() {
        return newsURL;
    }

    public void setNewsURL(String newsURL) {
        this.newsURL = newsURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public static boolean isDianZan(SQLiteDatabase db,DBDianZan dz)
    {
        String IsDianZan_SQL="select * from DBDianZan where name = '"
                +dz.getName()+"' and newsURL = '" + dz.getNewsURL()+"'";
        Cursor cursor=db.rawQuery(IsDianZan_SQL,null);
        if(cursor.moveToFirst())
        {
            Log.e("CAM","name="+cursor.getString(cursor.getColumnIndex("name")));
            Log.e("CAM","newsURL="+cursor.getString(cursor.getColumnIndex("newsURL")));
            cursor.close();
            return true;
        }
        else
        {
            cursor.close();
            return false;
        }
    }

    public static void dianZan(SQLiteDatabase db,DBDianZan dz)
    {
        String sql="insert into DBDianZan " +
                "(name,newsURL) values (?,?)";
        db.execSQL(sql,new String[]{dz.getName(),dz.getNewsURL()});
    }

    public static void cancalDianZan(SQLiteDatabase db,DBDianZan dz)
    {
        String sql="delete from DBDianZan " +
                "where name = '"+dz.getName()+"' and newsURL = '"+dz.getNewsURL()+"'";
        db.execSQL(sql,new String []{});
    }

}
