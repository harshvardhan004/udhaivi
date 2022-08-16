package com.udhaivi.udhaivihealthcare.menu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.udhaivi.udhaivihealthcare.R;

public class ProfileActivity extends AppCompatActivity {

    ImageView back;
    TextView uname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        back = findViewById(R.id.back);
        uname = findViewById(R.id.name);

        SharedPreferences editor = getSharedPreferences("User_Details", MODE_PRIVATE);
        String name = editor.getString("name", "");
//        String phone = editor.getString("phone", "");

        uname.setText(name);
        
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}