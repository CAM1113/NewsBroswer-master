package com.example.newsbroswer.adapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.newsbroswer.R;
import com.example.newsbroswer.beans.database_beans.DBDianZan;
import com.example.newsbroswer.beans.database_beans.DBMyEvalution;
import com.example.newsbroswer.beans.json_beans.EvalutionResult;
import com.example.newsbroswer.beans.news.ImagesListItem;
import com.example.newsbroswer.beans.news.News;
import com.example.newsbroswer.interfaces.ClickListener;
import com.example.newsbroswer.interfaces.DianZanClickListener;
import com.example.newsbroswer.interfaces.LongPressListener;
import com.example.newsbroswer.interfaces.OnNewsClickListener;
import com.example.newsbroswer.utils.DataBaseUtil;
import com.example.newsbroswer.utils.StaticFinalValues;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 王灿 on 2018/7/18.
 */

public class MyEvalutionAdapter extends RecyclerView.Adapter <MyEvalutionAdapter.ViewHolder>{
    List<DBMyEvalution> list;
    OnNewsClickListener listener;
    LongPressListener longPressListener;
    Context context;
    SQLiteDatabase db;


    @Override
    public MyEvalutionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.my_evaluate_item,parent,false);
        MyEvalutionAdapter.ViewHolder holder=new MyEvalutionAdapter.ViewHolder(view);
        return holder;
    }

    public MyEvalutionAdapter(List<DBMyEvalution> list,OnNewsClickListener l,Context context,LongPressListener longPressListener)
    {
        listener=l;
        this.longPressListener=longPressListener;
        this.list=list;
        this.context=context;
        db=new DataBaseUtil(context,"NewsBroswer",null, StaticFinalValues.DB_VERSION).getWritableDatabase();
    }

    @Override
    public void onBindViewHolder(final MyEvalutionAdapter.ViewHolder holder, final int position)
    {
        holder.title.setText(list.get(position).title);
        holder.evalution.setText(list.get(position).evalution);
        holder.evalutionTime.setText(list.get(position).getPubDate());
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBMyEvalution myEvalution=list.get(position);
                String pubDate = myEvalution.pubDate;
                String channelName =myEvalution.channelName;
                String title = myEvalution.title;
                String desc = myEvalution.desc;
                String source = myEvalution.source;
                String channelId = myEvalution.channelId;
                String link = myEvalution.link;
                String html = myEvalution.html;

                List<ImagesListItem> listItems=new ArrayList<ImagesListItem>();
                listItems.add(new ImagesListItem(myEvalution.imageurls1));
                listItems.add(new ImagesListItem(myEvalution.imageurls2));
                listItems.add(new ImagesListItem(myEvalution.imageurls3));
                News news=new News(pubDate, channelName,title,desc,
                        listItems,  source,  channelId, link,  html);
                listener.onClick(news);
            }
        });

        holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                longPressListener.doLongPress(list.get(position));
                return false;
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView title;
        TextView evalution;
        TextView evalutionTime;
        LinearLayout linearLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.textView5);
            evalution=itemView.findViewById(R.id.text_View6);
            evalutionTime=itemView.findViewById(R.id.text_View7);
            linearLayout=itemView.findViewById(R.id.my_evalution_item_layout);
        }
    }
}
