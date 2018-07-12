package com.example.newsbroswer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import com.example.newsbroswer.utils.StaticFinalValues;

public class NewShowActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_show);
        Intent intent = getIntent();
        //String link = intent.getStringExtra(StaticFinalValues.NEWS_INTENE_LINK);
        String html=intent.getStringExtra(StaticFinalValues.NEWS_INTENT_HTML);
        Log.e("CAM",html);
        WebView view= (WebView) findViewById(R.id.webViewShowNews);
        view.loadData(html, "text/html", "utf-8");
    }
}
