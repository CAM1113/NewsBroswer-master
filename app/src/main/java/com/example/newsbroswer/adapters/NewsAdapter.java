package com.example.newsbroswer.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.newsbroswer.R;
import com.example.newsbroswer.beans.news.News;
import com.example.newsbroswer.interfaces.OnNewsClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 王灿 on 2018/7/11.
 */

public class NewsAdapter extends RecyclerView.Adapter <NewsAdapter.ViewHolder>{

    List<News> newsList;
    OnNewsClickListener listener;
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.news_recycler_item,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    public NewsAdapter(List<News> newsList, OnNewsClickListener listener) {
        this.newsList = newsList;
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final News news=newsList.get(position);
        holder.title.setText(news.title);
        holder.dateTime.setText(news.getPubDate());
        holder.source.setText(news.source);
        holder.channel.setText(news.channelName);
        List<ImageView> imageViews=new ArrayList<>();
        imageViews.add(holder.imageView1);
        imageViews.add(holder.imageView2);
        imageViews.add(holder.imageView3);

            switch(news.getImageurls().size())
            {
                case 2:
                    holder.imageView3.setVisibility(View.GONE);
                    imageViews.remove(2);
                    break;
                case 1:
                    holder.imageView3.setVisibility(View.GONE);
                    holder.imageView2.setVisibility(View.GONE);
                    imageViews.remove(2);
                    imageViews.remove(1);
                    break;
                case 0:
                    holder.imageView3.setVisibility(View.GONE);
                    holder.imageView2.setVisibility(View.GONE);
                    holder.imageView1.setVisibility(View.GONE);
                    imageViews.remove(2);
                    imageViews.remove(1);
                    imageViews.remove(0);
                    break;
            }
        for(int i=0;i<imageViews.size();i++)
        {
            Glide.with(imageViews.get(i)
                    .getContext())
                    .load(news.getImageurls().get(i).url)
                    .into(imageViews.get(i));
        }
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(news);
            }
        });

    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView title;
        ImageView imageView1;
        ImageView imageView2;
        ImageView imageView3;
        TextView channel;
        TextView dateTime;
        TextView source;
        LinearLayout linearLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            linearLayout=itemView.findViewById(R.id.layout_newsItem);
            title=itemView.findViewById(R.id.title_textView);
            imageView1=itemView.findViewById(R.id.imageView1);
            imageView2=itemView.findViewById(R.id.imageView2);
            imageView3=itemView.findViewById(R.id.imageView3);
            channel=itemView.findViewById(R.id.channelTextView);
            dateTime=itemView.findViewById(R.id.timeTexiView);
            source=itemView.findViewById(R.id.sourceTextView);
        }
    }




}
