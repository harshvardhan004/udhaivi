package com.udhaivi.udhaivihealthcare.adapter;

import android.annotation.SuppressLint;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.udhaivi.udhaivihealthcare.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/4/9.
 */

public class MainAdapter extends RecyclerView.Adapter {
    String[]arrays;
    onItemClickListener onItemClickListener;
    public MainAdapter(String[]arrays,onItemClickListener onItemClickListener) {
        this.onItemClickListener=onItemClickListener;
        this.arrays=arrays;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CmdViewHolder cmdViewHolder=null;
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cmd,parent,false);
        cmdViewHolder=new CmdViewHolder(view);
        return cmdViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        CmdViewHolder cmdViewHolder=(CmdViewHolder)holder;
        cmdViewHolder.bt_cmd.setEnabled(enable);
        cmdViewHolder.t1.setText(arrays[position]);
        cmdViewHolder.bt_cmd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position);
            }
        });
    }

    private boolean enable;
    public void setEnable(boolean enable){
        this.enable=enable;
        notifyDataSetChanged();
    }




    @Override
    public int getItemCount() {
        return arrays==null?0:arrays.length;
    }

    class CmdViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.button_cmd)
        CardView bt_cmd;
        TextView t1;
        public CmdViewHolder(View itemView) {


            super(itemView);

            bt_cmd  = itemView.findViewById(R.id.button_cmd);
            t1  = itemView.findViewById(R.id.t1);

            ButterKnife.bind(this,itemView);
        }
    }
    public interface onItemClickListener{
        public void onItemClick(int position);
    }
}
