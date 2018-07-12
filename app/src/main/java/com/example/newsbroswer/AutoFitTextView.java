package com.example.newsbroswer;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by 王灿 on 2018/7/12.
 */

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
        
        super.setText(text, type);
    }
}
