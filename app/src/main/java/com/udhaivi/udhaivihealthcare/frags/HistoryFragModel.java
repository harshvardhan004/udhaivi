package com.udhaivi.udhaivihealthcare.frags;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.udhaivi.udhaivihealthcare.R;

import java.util.ArrayList;

public class HistoryFragModel extends RecyclerView.Adapter<HistoryFragModel.ViewHolder> {

    ArrayList<String> image = new ArrayList<String>();
    ArrayList<String> pdf_title = new ArrayList<String>();
    ArrayList<String> descrip = new ArrayList<String>();
    ArrayList<String> type = new ArrayList<String>();

    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    HistoryFragModel(Context context, ArrayList<String> image, ArrayList<String> pdf_title, ArrayList<String> descrip, ArrayList<String> type) {
        this.mInflater = LayoutInflater.from(context);
        this.image = image;
        this.pdf_title = pdf_title;
        this.descrip = descrip;
        this.type = type;
    }

    // inflates the row layout from xml when needed
    @Override
    public com.udhaivi.udhaivihealthcare.frags.HistoryFragModel.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_history, parent, false);
        return new com.udhaivi.udhaivihealthcare.frags.HistoryFragModel.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(com.udhaivi.udhaivihealthcare.frags.HistoryFragModel.ViewHolder holder, final int position) {
        String t1 = image.get(position);
        String t2 = descrip.get(position);
        String t3 = pdf_title.get(position);
        holder.text1.setText(t3);
//        holder.text2.setText(t2);
//        holder.text3.setText(t3);


    }

    // binds the data to the TextView in each row


    // total number of rows
    @Override
    public int getItemCount() {
        return image.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView text1, text2, text3;

        ViewHolder(View itemView) {
            super(itemView);
            text1 = itemView.findViewById(R.id.title);
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
        return image.get(id);
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