package com.example.newsbroswer.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.example.newsbroswer.NewShowActivity;
import com.example.newsbroswer.R;

/**
 * Created by 王灿 on 2018/7/16.
 */

public class NewsFragment extends Fragment {
    NewShowActivity activity;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.webview_fragment,container,false);
        WebView webView=view.findViewById(R.id.webViewShowNews);
        activity= (NewShowActivity) getActivity();
        webView.loadDataWithBaseURL(null,activity.getHtmls(), "text/html", "utf-8",null);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().finish();
    }
}
