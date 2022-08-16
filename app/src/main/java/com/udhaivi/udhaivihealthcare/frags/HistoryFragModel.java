package com.udhaivi.udhaivihealthcare.frags;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.udhaivi.udhaivihealthcare.R;

import java.util.ArrayList;

public class HistoryFragModel extends RecyclerView.Adapter<com.udhaivi.udhaivihealthcare.frags.HistoryFragModel.ViewHolder> {

    ArrayList<String> attendance = new ArrayList<String>();
    ArrayList<String> attdate = new ArrayList<String>();
    ArrayList<String> attday = new ArrayList<String>();

    private LayoutInflater mInflater;
    private com.udhaivi.udhaivihealthcare.frags.HistoryFragModel.ItemClickListener mClickListener;

    // data is passed into the constructor
    HistoryFragModel(Context context, ArrayList<String> attendance, ArrayList<String> attdate, ArrayList<String> attday) {
        this.mInflater = LayoutInflater.from(context);
        this.attendance = attendance;
        this.attdate = attdate;
        this.attday = attday;
    }

    // inflates the row layout from xml when needed
    @Override
    public com.udhaivi.udhaivihealthcare.frags.HistoryFragModel.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_history, parent, false);
        return new com.udhaivi.udhaivihealthcare.frags.HistoryFragModel.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(com.udhaivi.udhaivihealthcare.frags.HistoryFragModel.ViewHolder holder, final int position) {
        String t1 = attendance.get(position);
        String t2 = attday.get(position);
        String t3 = attdate.get(position);
//        holder.text1.setText(t1);
//        holder.text2.setText(t2);
//        holder.text3.setText(t3);


    }

    // binds the data to the TextView in each row


    // total number of rows
    @Override
    public int getItemCount() {
        return attendance.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView text1, text2, text3;

        ViewHolder(View itemView) {
            super(itemView);
//            text1 = itemView.findViewById(R.id.text1);
//            itemView.setOnClickListener(this);
//
//            text2 = itemView.findViewById(R.id.text2);
//            itemView.setOnClickListener(this);
//
//            text3 = itemView.findViewById(R.id.text3);
            itemView.setOnClickListener(this);

        }


        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return attendance.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}