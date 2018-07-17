package com.example.newsbroswer.fragments;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.newsbroswer.NewShowActivity;
import com.example.newsbroswer.R;
import com.example.newsbroswer.adapters.EvalutionAdapter;
import com.example.newsbroswer.beans.database_beans.DBDianZan;
import com.example.newsbroswer.beans.json_beans.EvalutionResult;
import com.example.newsbroswer.beans.news.News;
import com.example.newsbroswer.interfaces.DianZanClickListener;
import com.example.newsbroswer.utils.DataBaseUtil;
import com.example.newsbroswer.utils.StaticFinalValues;
import com.example.newsbroswer.utils.Utils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 王灿 on 2018/7/16.
 */

public class ShowEvalutionFragment extends Fragment{

    EvalutionAdapter adapter;
    List<EvalutionResult.Evalution> list = new ArrayList<>();

    String newsURL;
    NewShowActivity activity;
    RecyclerView recyclerView;

    SQLiteDatabase db;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.evalution_fragment,container,false);
        recyclerView=view.findViewById(R.id.recyclerView_evalution);
        activity = (NewShowActivity) getActivity();
        newsURL=activity.getNewsURL();
        db=new DataBaseUtil(activity,"NewsBroswer",null,StaticFinalValues.DB_VERSION).getWritableDatabase();

        getEvalutionFronHouTai(newsURL);

        adapter=new EvalutionAdapter(list, new DianZanClickListener() {
            @Override
            public void cancalDianZan(EvalutionResult.Evalution evalution) {
                cancalDianZan_fragment(evalution);
            }

            @Override
            public void dianZan(EvalutionResult.Evalution evalution) {
                dianZan_fragment(evalution);
            }
        },activity);
        LinearLayoutManager manager=new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        return view;
    }

    private static final int CANCAL_SUCCESS=5;
    private static final int DIANZAN_SUCCESS=6;
    private static final int NET_ERROR=7;
    private static final int RECEIVE_EVALUTION_SUCCESS=8;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case CANCAL_SUCCESS:
                case DIANZAN_SUCCESS:
                    getEvalutionFronHouTai(newsURL);
                    activity.clearEditTextFocus();
                    break;
                case RECEIVE_EVALUTION_SUCCESS:
                    adapter.notifyDataSetChanged();
                    activity.clearEditTextFocus();
                    break;
                case NET_ERROR:
                    Toast.makeText(activity, "网络故障", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void cancalDianZan_fragment(final EvalutionResult.Evalution evalution)
    {
        new Thread(new Runnable() {
        @Override
        public void run() {
            String params="username="+evalution.getUser().getName()+"&commentId="+evalution.getId();
            String result=Utils.sendHttpRequest(StaticFinalValues.NEWS_URL+"/like/delete","POST",params);
            if(result==null)
            {
                Log.e("CAM","网络错误");
                handler.sendEmptyMessage(NET_ERROR);
            }
            else
            {
                DBDianZan.cancalDianZan(db,new DBDianZan(evalution.getNewsURL(),evalution.getUser().getName()));
                Log.e("CAM",result);
                handler.sendEmptyMessage(CANCAL_SUCCESS);
            }
        }
    }).start();
    }

    private void dianZan_fragment(final EvalutionResult.Evalution evalution)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String params="username="+evalution.getUser().getName()+"&commentId="+evalution.getId();
                String result=Utils.sendHttpRequest(StaticFinalValues.NEWS_URL+"/like","POST",params);
                if(result==null)
                {
                    Log.e("CAM","网络错误");
                    handler.sendEmptyMessage(NET_ERROR);
                }
                else
                {
                    Log.e("CAM",result);
                    DBDianZan.dianZan(db,new DBDianZan(evalution.getNewsURL(),evalution.getUser().getName()));
                    handler.sendEmptyMessage(DIANZAN_SUCCESS);
                }
            }
        }).start();
    }


    public void getEvalutionFronHouTai(final String newsURL)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = Utils.sendHttpRequest(StaticFinalValues.NEWS_URL+"/comment?newsURL="+newsURL,"GET",null);
                if(result==null)
                {
                    Log.e("CAM","评论获取失败");
                    handler.sendEmptyMessage(NET_ERROR);
                }
                else
                {
                    list.clear();
                    Gson gson=new Gson();
                    EvalutionResult evalutionResult=gson.fromJson(result,EvalutionResult.class);
                    for(EvalutionResult.Evalution e:evalutionResult.getData())
                    {
                       list.add(e);
                    }
                    handler.sendEmptyMessage(RECEIVE_EVALUTION_SUCCESS);
                    Log.e("CAM",result);
                }
            }
        }).start();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        activity.releaseEvalutionFragment();
    }






}
