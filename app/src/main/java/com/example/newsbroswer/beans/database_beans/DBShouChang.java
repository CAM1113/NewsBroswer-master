package com.example.newsbroswer.beans.database_beans;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.newsbroswer.beans.news.ImagesListItem;
import com.example.newsbroswer.beans.news.News;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 王灿 on 2018/7/16.
 */

public class DBShouChang {

    public String name;
    public String pubDate;
    public String channelName;
    public String title;
    public String desc;
    public String imageurls1;
    public String imageurls2;
    public String imageurls3;
    public String source;
    public String channelId;
    public String link;
    public String html;

    public DBShouChang() {}


    public DBShouChang(String name, String pubDate, String channelName, String title, String desc, String imageurls1, String imageurls2, String imageurls3, String source, String channelId, String link, String html) {
        this.name = name;
        this.pubDate = pubDate;
        this.channelName = channelName;
        this.title = title;
        this.desc = desc;
        this.imageurls1 = imageurls1;
        this.imageurls2 = imageurls2;
        this.imageurls3 = imageurls3;
        this.source = source;
        this.channelId = channelId;
        this.link = link;
        this.html = html;
    }

    public String getImageurls3() {
        return imageurls3;
    }

    public void setImageurls3(String imageurls3) {
        this.imageurls3 = imageurls3;
    }

    public String getImageurls1() {
        return imageurls1;
    }

    public void setImageurls1(String imageurls1) {
        this.imageurls1 = imageurls1;
    }

    public String getImageurls2() {
        return imageurls2;
    }

    public void setImageurls2(String imageurls2) {
        this.imageurls2 = imageurls2;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }


    public static List<News> getNewsByName(SQLiteDatabase db,String name)
    {
        List<News> list=new ArrayList<>();
        String sql="select * from DBShouChang where name = '"+name+"'";
        Cursor cursor=db.rawQuery(sql,null);
        if(cursor.moveToFirst())
        {
            do
            {
                String  pubDate=cursor.getString(cursor.getColumnIndex("pubDate"));
                String channelName=cursor.getString(cursor.getColumnIndex("channelName"));
                String title=cursor.getString(cursor.getColumnIndex("title"));
                String desc=cursor.getString(cursor.getColumnIndex("desc"));
                String source=cursor.getString(cursor.getColumnIndex("source"));
                String channelId=cursor.getString(cursor.getColumnIndex("channelId"));
                String link=cursor.getString(cursor.getColumnIndex("link"));
                String html=cursor.getString(cursor.getColumnIndex("html"));
                String imageurls1=cursor.getString(cursor.getColumnIndex("imageurls1"));
                String imageurls2=cursor.getString(cursor.getColumnIndex("imageurls2"));
                String imageurls3=cursor.getString(cursor.getColumnIndex("imageurls3"));
                List<ImagesListItem> imagesListItems=new ArrayList<>();
                if(imageurls1!=null&&!imageurls1.equals(""))
                {
                    imagesListItems.add(new ImagesListItem(imageurls1));
                }
                if(imageurls2!=null&&!imageurls2.equals(""))
                {
                    imagesListItems.add(new ImagesListItem(imageurls2));
                }
                if(imageurls3!=null&&!imageurls3.equals(""))
                {
                    imagesListItems.add(new ImagesListItem(imageurls3));
                }

                News news=new News(pubDate, channelName, title, desc,
                        imagesListItems, source,  channelId, link,  html);
                list.add(news);
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public static void storeDBShouChang(SQLiteDatabase db,DBShouChang sc)
    {
        String sql="insert into DBShouChang(name,pubDate,channelName,title,desc,imageurls1,imageurls2,imageurls3," +
                "source,channelId,link,html) values (?,?,?,?,?,?,?,?,?,?,?,?)";
        db.execSQL(sql,new String[]{sc.name,sc.pubDate,sc.channelName,sc.title,sc.desc,sc.imageurls1,sc.imageurls2,sc.imageurls3,
                sc.source,sc.channelId,sc.link,sc.html});
    }


    public static boolean isShouChang(SQLiteDatabase db,String name,String link)
    {
        String sql="select * from DBShouChang where name = '"+name+"' and link = '"+link+"'";
        Cursor cursor=db.rawQuery(sql,null);
        if(cursor.moveToFirst())
        {
            cursor.close();
            return true;
        }
        else
        {
            cursor.close();
            return false;
        }
    }

    public static void cancalShouChang(SQLiteDatabase db,String name,String link)
    {
        String sql="delete from DBShouChang where name = '"+name+"' and link = '"+link+"'";
        db.execSQL(sql);
    }


}
