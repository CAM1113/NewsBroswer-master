package com.example.newsbroswer.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.newsbroswer.R;
import com.example.newsbroswer.beans.channel.Channel;
import com.example.newsbroswer.interfaces.OnChannelClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 王灿 on 2018/7/11.
 */

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ViewHolder> {
    private List<Channel> channelList;
    private OnChannelClickListener listener;
    private List<Boolean> isClicks=new ArrayList<>();

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView channelTextView;
        TextView line;
        public ViewHolder(View itemView) {
            super(itemView);
            channelTextView=(TextView)itemView.findViewById(R.id.ChanneltextView);
            line=itemView.findViewById(R.id.line_buttom);
        }
    }

    public ChannelAdapter(List<Channel> channelList, OnChannelClickListener listener)
    {
        this.channelList=channelList;
        this.listener=listener;
        isClicks.add(true);
        for(int i=1;i<channelList.size();i++)
        {
            isClicks.add(false);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.channel_recycler_item,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Channel channel=channelList.get(position);
        holder.channelTextView.setText(channel.getName());
        while(isClicks.size()<=position)
        {
            isClicks.add(false);
        }
        if(isClicks.get(position))
        {
            holder.channelTextView.setTextColor(holder
                    .channelTextView.getResources().getColor(R.color.red));
            holder.line.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.channelTextView.setTextColor(Color.BLACK);
            holder.line.setVisibility(View.INVISIBLE);
        }
        holder.channelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(channel);
                for(int i=0;i<isClicks.size();i++)
                {
                    isClicks.set(i,false);
                }
                isClicks.set(position,true);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return channelList.size();
    }


    public void setFirstTrue()
    {
        for(int i=0;i<isClicks.size();i++)
        {
            isClicks.set(i,false);
        }
        isClicks.set(0,true);
    }

    public void setTrue(int position)
    {
        for(int i=0;i<isClicks.size();i++)
        {
            isClicks.set(i,false);
        }
        isClicks.set(position,true);
    }

}
