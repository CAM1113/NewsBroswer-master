package com.example.newsbroswer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class StartActivity extends AppCompatActivity {
    private TextView textView;
    private ImageView imageView1;
    private ImageView imageView2;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        preferences=getSharedPreferences("StartActivity",MODE_PRIVATE);
        editor=preferences.edit();

        imageView1=(ImageView) findViewById(R.id.image_View1);
        textView=(TextView) findViewById(R.id.text_View1);
        imageView2=(ImageView)findViewById(R.id.image_View2);
        final Animation animatorBg= AnimationUtils.loadAnimation(this,R.anim.anim);
        final Animation animatorText=AnimationUtils.loadAnimation(this,R.anim.text_anim);
        imageView1.startAnimation(animatorBg);
        textView.startAnimation(animatorText);
        imageView2.startAnimation(animatorBg);
        animatorText.setFillAfter(true);
        animatorBg.setFillAfter(true);

        animatorBg.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                boolean is=preferences.getBoolean("IS_FIRST_START_APP",true);
                Intent intent;
                if(is)
                {
                    intent = new Intent(StartActivity.this,LeadActivity.class);
                    editor.putBoolean("IS_FIRST_START_APP",false);
                    //editor.apply();
                }
                else
                {
                    intent=new Intent(StartActivity.this,MainActivity.class);
                }
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
}
