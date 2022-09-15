package com.udhaivi.udhaivihealthcare.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.udhaivi.udhaivihealthcare.Dashboard;
import com.udhaivi.udhaivihealthcare.R;
import com.udhaivi.udhaivihealthcare.activity.DeviceScanActivity;
import com.udhaivi.udhaivihealthcare.menu.PhotoUpload;

public class Splashscreen extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        getSupportActionBar().hide();

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                //
                SharedPreferences editor = getSharedPreferences("User_Details", MODE_PRIVATE);
                String name = editor.getString("firstname", "");
                String phone = editor.getString("firstname", "");
                if( name == "" && phone == ""){

                    Intent i = new Intent(Splashscreen.this, LoginSplash.class);
                    startActivity(i);
                    finish();
                }
                else{

                    Intent i = new Intent(Splashscreen.this, ModeCheck.class);
                    startActivity(i);
                    finish();

//                        SharedPreferences Device_Details = getSharedPreferences("Device_Details", MODE_PRIVATE);
//                        String address = Device_Details.getString("address", "");
//                        String devicename = Device_Details.getString("name", "");
//                        if( address == "" && devicename == ""){
//                            Intent i = new Intent(Splashscreen.this, DeviceScanActivity.class);
//                            startActivity(i);
//                            finish();
//                        }else {
//                            final Intent intent = new Intent(getApplicationContext(), Dashboard.class);
//                            intent.putExtra("address", address);
//                            intent.putExtra("name", devicename);
//                            startActivity(intent);
//                            finish();
//                        }
                }
            }
        }, 3000);



    }
}