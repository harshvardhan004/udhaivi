package com.udhaivi.udhaivihealthcare;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class ViewDialog {

    public void showDialog(Activity activity){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialogue);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        int width  = Resources.getSystem().getDisplayMetrics().widthPixels;
        int height  = Resources.getSystem().getDisplayMetrics().widthPixels;

        int width1 = (int)(width*0.70);
        int height1 = (int)(height*0.70);
        dialog.getWindow().setLayout(width1, height1);

        TextView text = (TextView) dialog.findViewById(R.id.cancel);
//        text.setText(msg);

//        Button dialogButton = (Button) dialog.findViewById(R.id.close);

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.callsos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel: +91 7688837688"));
                activity.startActivity(intent);
            }
        });


        dialog.show();

    }
}

