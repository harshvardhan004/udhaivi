package com.udhaivi.udhaivihealthcare.adapter;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Administrator on 2018/6/27.
 */

public class BaseViewHolder extends RecyclerView.ViewHolder {
    private View mView;
    public BaseViewHolder(View itemView) {
        super(itemView);
        this.mView=itemView;
    }

    public View getView() {
        return mView;
    }
}
