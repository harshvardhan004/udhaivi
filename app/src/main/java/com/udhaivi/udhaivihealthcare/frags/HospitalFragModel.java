package com.udhaivi.udhaivihealthcare.frags;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.udhaivi.udhaivihealthcare.R;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import java.util.ArrayList;

public class HospitalFragModel extends RecyclerView.Adapter<com.udhaivi.udhaivihealthcare.frags.HospitalFragModel.ViewHolder> {

    ArrayList<String> name = new ArrayList<String>();
    ArrayList<String> address = new ArrayList<String>();
    ArrayList<String> imgsrc_center = new ArrayList<String>();

    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    Context context;
    // data is passed into the constructor
    HospitalFragModel(Context context, ArrayList<String> name, ArrayList<String> address, ArrayList<String> img) {
        this.mInflater = LayoutInflater.from(context);
        this.name = name;
        this.address = address;
        this.imgsrc_center = img;

        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public com.udhaivi.udhaivihealthcare.frags.HospitalFragModel.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_hospital, parent, false);
        return new com.udhaivi.udhaivihealthcare.frags.HospitalFragModel.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(com.udhaivi.udhaivihealthcare.frags.HospitalFragModel.ViewHolder holder, final int position) {
//        String t1 = name.get(position);
//        String t2 = attday.get(position);
//        String t3 = attdate.get(position);
        holder.text1.setText(name.get(position));
        holder.text2.setText(address.get(position));

        Glide.with(context)
                .load(imgsrc_center.get(position))
                .into(holder.hospitalcard);
//        holder.text3.setText(t3);


    }

    // binds the data to the TextView in each row


    // total number of rows
    @Override
    public int getItemCount() {
        return name.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView text1, text2, text3;
        ImageView hospitalcard;


        ViewHolder(View itemView) {
            super(itemView);
            text1 = itemView.findViewById(R.id.hospitalname);
//            itemView.setOnClickListener(this);
//
            text2 = itemView.findViewById(R.id.address);
            hospitalcard = itemView.findViewById(R.id.hospitalcard);
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
        return name.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(HospitalFragment itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}