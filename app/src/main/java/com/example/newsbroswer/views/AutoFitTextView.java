package com.example.newsbroswer.views;

import android.widget.TextView;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;


public class AutoFitTextView extends TextView {


    public AutoFitTextView(Context context) {
        super(context);
    }

    public AutoFitTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoFitTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        switch (text.length())
        {
            case 1:setTextSize(25);break;
            case 2:setTextSize(21);break;
            case 3:setTextSize(19);break;
            case 4:setTextSize(17);break;
            case 5:setTextSize(15);break;
            case 6:setTextSize(13);break;
                default:;setTextSize(10);break;
        }
        super.setText(text, type);
    }
}