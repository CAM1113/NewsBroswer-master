package com.example.newsbroswer.adapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.DateFormat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.newsbroswer.R;
import com.example.newsbroswer.beans.database_beans.DBDianZan;
import com.example.newsbroswer.beans.json_beans.EvalutionResult;
import com.example.newsbroswer.interfaces.DianZanClickListener;
import com.example.newsbroswer.utils.DataBaseUtil;
import com.example.newsbroswer.utils.StaticFinalValues;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 王灿 on 2018/7/16.
 */

public class EvalutionAdapter extends RecyclerView.Adapter<EvalutionAdapter.ViewHolder> {
    List<EvalutionResult.Evalution> list;
    DianZanClickListener listener;
    Context context;
    SQLiteDatabase db;


    @Override
    public EvalutionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.evaluate_item_layout,parent,false);
        EvalutionAdapter.ViewHolder holder=new EvalutionAdapter.ViewHolder(view);
        return holder;
    }

    public EvalutionAdapter(List<EvalutionResult.Evalution> list,DianZanClickListener l,Context context)
    {
        listener=l;
        this.list=list;
        this.context=context;
        db=new DataBaseUtil(context,"NewsBroswer",null,StaticFinalValues.DB_VERSION).getWritableDatabase();
    }

    @Override
    public void onBindViewHolder(final EvalutionAdapter.ViewHolder holder, final int position)
    {
        if(holder.touxiang==null)
        {
            Log.e("CAM","holder.touxiang==null");

        }else {
            Glide.with(holder.dianzan.getContext())
                    .load(StaticFinalValues.NEWS_URL+list.get(position).getUser().getProfilePicture())
                    .into(holder.touxiang);
        }
        holder.yonghuming.setText(list.get(position).getUser().getNickname());
        holder.dianzancishu.setText(list.get(position).getLikeCount()+"");
        holder.pingLunLeiRong.setText(list.get(position).getContent());
        //拆出时间的年月日，可能会有bug
        String [] ss=list.get(position).getTime().split("T");
        holder.evalutionTime.setText(ss[0]);
        if(DBDianZan.isDianZan(db,new DBDianZan(list.get(position).getNewsURL(),
                list.get(position).getUser().getName())))
        {
            Glide.with(holder.dianzan.getContext()).load(R.drawable.dianzan).into(holder.dianzan);
        }
        else
        {
            Glide.with(holder.dianzan.getContext()).load(R.drawable.budianzan).into(holder.dianzan);
        }
        holder.dianzan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(DBDianZan.isDianZan(db,new DBDianZan(list.get(position).getNewsURL(),
                        list.get(position).getUser().getName())))
                {
                    listener.cancalDianZan(list.get(position));
                }
                else
                {
                    listener.dianZan(list.get(position));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView touxiang;
        TextView yonghuming;
        ImageView dianzan;
        TextView dianzancishu;
        TextView pingLunLeiRong;
        TextView evalutionTime;
        public ViewHolder(View itemView) {
            super(itemView);
            touxiang=itemView.findViewById(R.id.image_View1);
            yonghuming=itemView.findViewById(R.id.text_View1);
            dianzan=itemView.findViewById(R.id.image_View2);
            dianzancishu=itemView.findViewById(R.id.text_View2);
            pingLunLeiRong=itemView.findViewById(R.id.text_View3);
            evalutionTime=itemView.findViewById(R.id.text_View4);
        }
    }
}
