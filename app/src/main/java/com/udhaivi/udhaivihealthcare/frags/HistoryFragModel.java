package com.udhaivi.udhaivihealthcare.frags;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.udhaivi.udhaivihealthcare.R;
import com.udhaivi.udhaivihealthcare.app.WebView;

import java.util.ArrayList;

public class HistoryFragModel extends RecyclerView.Adapter<HistoryFragModel.ViewHolder> {

    ArrayList<String> image = new ArrayList<String>();
    ArrayList<String> pdf_title = new ArrayList<String>();
    ArrayList<String> descrip = new ArrayList<String>();
    ArrayList<String> type = new ArrayList<String>();

    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    private Context context;

    // data is passed into the constructor
    public HistoryFragModel(Context context, ArrayList<String> image, ArrayList<String> pdf_title, ArrayList<String> descrip, ArrayList<String> type) {
        this.mInflater = LayoutInflater.from(context);
        this.image = image;
        this.pdf_title = pdf_title;
        this.descrip = descrip;
        this.type = type;

        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public com.udhaivi.udhaivihealthcare.frags.HistoryFragModel.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_history, parent, false);
        return new com.udhaivi.udhaivihealthcare.frags.HistoryFragModel.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(com.udhaivi.udhaivihealthcare.frags.HistoryFragModel.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        String t1 = image.get(position);
        String t2 = descrip.get(position);
        String t3 = pdf_title.get(position);
        holder.text1.setText(t3);

        if(type.get(position).equals("image")){
            Glide.with(context).load(image.get(position))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.img);
        }
        holder.text2.setText(t2);

        holder.hospitalcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(type.get(position).equals("pdf")){
                    Intent i = new Intent(context, WebView.class);
                    i.putExtra("link", image.get(position));
                    context.startActivity(i);

                }
                else{
                    onButtonShowPopupWindowClick(view, image.get(position));
                }
            }
        });

    }

    public void onButtonShowPopupWindowClick(View view, String s) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = LayoutInflater.from(context).inflate(R.layout.popup_window, null);


        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.setAnimationStyle(R.style.popup_window_animation);

        int location[] = new int[2];
        view.getLocationOnScreen(location);
        popupWindow.setBackgroundDrawable(new ColorDrawable());


        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, location[0], location[1]);


        ImageView img = popupView.findViewById(R.id.imageView);
        Glide.with(context).load(s)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(img);

        // dismiss the popup window when touched
//        popupView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                popupWindow.dismiss();
//                return true;
//            }
//        });
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
        ImageView img;
        CardView hospitalcard;

        ViewHolder(View itemView) {
            super(itemView);
            text1 = itemView.findViewById(R.id.title);
            img = itemView.findViewById(R.id.image);
//            itemView.setOnClickListener(this);
//
            text2 = itemView.findViewById(R.id.desc);
//            itemView.setOnClickListener(this);
//
            hospitalcard = itemView.findViewById(R.id.hospitalcard);
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
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}