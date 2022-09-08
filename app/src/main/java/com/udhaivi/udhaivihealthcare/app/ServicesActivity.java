package com.udhaivi.udhaivihealthcare.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.udhaivi.udhaivihealthcare.Dashboard;
import com.udhaivi.udhaivihealthcare.R;
import com.udhaivi.udhaivihealthcare.activity.DeviceScanActivity;
import com.udhaivi.udhaivihealthcare.menu.PhotoUpload;

public class ServicesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.services_activity);
    }

    public void services(View view) {
        SharedPreferences Device_Details = getSharedPreferences("Device_Details", MODE_PRIVATE);
        String address = Device_Details.getString("address", "");
        String devicename = Device_Details.getString("name", "");
        if( address == "" && devicename == ""){
            Intent i = new Intent(ServicesActivity.this, DeviceScanActivity.class);
            startActivity(i);
            finish();
        }else {
            final Intent intent = new Intent(ServicesActivity.this, Dashboard.class);
            intent.putExtra("address", address);
            intent.putExtra("name", devicename);
            startActivity(intent);

        }

    }

    public void supports(View view) {
        Intent i = new Intent(ServicesActivity.this, PhotoUpload.class);
        startActivity(i);

    }
}