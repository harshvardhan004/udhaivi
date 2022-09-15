package com.udhaivi.udhaivihealthcare.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.udhaivi.udhaivihealthcare.Dashboard;
import com.udhaivi.udhaivihealthcare.R;
import com.udhaivi.udhaivihealthcare.activity.DeviceScanActivity;

public class ModeCheck extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_check);

        SharedPreferences editor = getSharedPreferences("User_Details", MODE_PRIVATE);
        String name = editor.getString("firstname", "");
        String phone = editor.getString("phone", "");

//        if( name == "" && phone == ""){
//                    Intent i = new Intent(ModeCheck.this, LoginSplash.class);
//                    startActivity(i);
//                    finish();
//            }

        findViewById(R.id.premiumuser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ModeCheck.this, DeviceScanActivity.class));
                finish();
            }
        });

        findViewById(R.id.normaluser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ModeCheck.this, Report.class));
                finish();
            }
        });
    }
}