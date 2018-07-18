package com.example.newsbroswer;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ViewFlipper;

public class LeadActivity extends AppCompatActivity implements View.OnTouchListener{
    private ViewFlipper vf;
    float startX;//声明手指按下时X的坐标
    float endX;//声明手指松开后X的坐标
    int count=0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lead);
        vf= (ViewFlipper) findViewById(R.id.vf);
        vf.setOnTouchListener(this);
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //判断捕捉到的动作为按下，则设置按下点的X坐标starX
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            startX=event.getX();
            //判断捕捉到的动作为抬起，则设置松开点X坐标endX
        }else if(event.getAction()==MotionEvent.ACTION_UP){
            endX=event.getX();
            //由右到左滑动屏幕，X值会减小，图片由屏幕右侧进入屏幕
            if(startX>endX){
                count++;
                if(count==3)
                {
                    Intent intent=new Intent(LeadActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                //进出动画成对
                vf.setInAnimation(this,R.anim.in_rightleft);
                vf.setOutAnimation(this,R.anim.out_rightleft);
                vf.showNext();//显示下个view
                //由左到右滑动屏幕，X值会增大，图片由屏幕左侧进入屏幕
            }else if(startX<endX){
                count--;
                if(count<=0)
                {
                    count=0;
                    return true;
                }
                vf.setInAnimation(this,R.anim.in_leftright);
                vf.setOutAnimation(this,R.anim.out_leftright);
                vf.showPrevious();//显示上个view
            }
        }
        return true;
    }
}
