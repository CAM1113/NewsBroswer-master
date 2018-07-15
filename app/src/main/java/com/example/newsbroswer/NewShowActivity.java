package com.example.newsbroswer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newsbroswer.utils.StaticFinalValues;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class NewShowActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_show);
        Intent intent = getIntent();
        initToolBar();
        //String link = intent.getStringExtra(StaticFinalValues.NEWS_INTENE_LINK);
        String html=intent.getStringExtra(StaticFinalValues.NEWS_INTENT_HTML);
        Log.e("CAM",html);
        WebView view= (WebView) findViewById(R.id.webViewShowNews);
        view.loadDataWithBaseURL(null,getNewContent(html), "text/html", "utf-8",null);

        initfoot();
        setFootOnUnEvalution();




    }


    Button fabuBtn;
    ImageView souchangImageView;
    EditText evalutionEditText;
    ImageView chaKanImageView;
    private void initfoot()
    {
        fabuBtn= (Button) findViewById(R.id.fabu_button);
        souchangImageView= (ImageView) findViewById(R.id.shouchang_imageView);
        evalutionEditText= (EditText) findViewById(R.id.evalution_textView);
        chaKanImageView= (ImageView) findViewById(R.id.show_evalution_imageView);



        fabuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String evalu=evalutionEditText.getText()+"";
                //判断是否为空，执行发送逻辑
                if(evalu.trim().equals(""))
                {
                    Toast.makeText(NewShowActivity.this, "评论为空，发送失败", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //发送评论
                    sendEvalution(evalu);
                }
                //重置界面
                evalutionEditText.setText("");
                evalutionEditText.clearFocus();//取消焦点
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(NewShowActivity.this
                                        .getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);//关闭输入法
            }
        });



        evalutionEditText.setOnFocusChangeListener(new android.view.View.
                OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                    setFootOnEvalution();
                } else {
                    // 此处为失去焦点时的处理内容
                    setFootOnUnEvalution();
                }
            }
        });
    }

    private void setFootOnEvalution()
    {
        fabuBtn.setVisibility(View.VISIBLE);
        chaKanImageView.setVisibility(View.GONE);
        souchangImageView.setVisibility(View.GONE);
    }

    private void setFootOnUnEvalution()
    {
        fabuBtn.setVisibility(View.GONE);
        chaKanImageView.setVisibility(View.VISIBLE);
        souchangImageView.setVisibility(View.VISIBLE);

    }


    Toolbar toolBar;
    ImageView backImageView;
    TextView channelName;
    private void initToolBar()
    {
        toolBar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        backImageView= (ImageView) findViewById(R.id.backImageView);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewShowActivity.this.finish();
            }
        });
        channelName= (TextView) findViewById(R.id.denglutixing_Title);
        channelName.setText(getIntent().getStringExtra(StaticFinalValues.NEWS_INTENT_CHANNEL_NAME));

    }












    private void sendEvalution(String s)
    {


    }

    private String getNewContent(String htmltext){

        Document doc= Jsoup.parse(htmltext);
        Elements elements=doc.getElementsByTag("img");
        for (Element element : elements) {
            element.attr("width","100%").attr("height","auto");
        }
        return doc.toString();
    }
}
