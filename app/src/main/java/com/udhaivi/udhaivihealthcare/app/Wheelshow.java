package com.udhaivi.udhaivihealthcare.app;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.udhaivi.udhaivihealthcare.R;
import com.udhaivi.udhaivihealthcare.activity.BaseActivity;
import com.udhaivi.udhaivihealthcare.views.WheelView;

public class Wheelshow extends BaseActivity {

    WheelView wheel = new WheelView(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheelshow);

       wheel =  findViewById(R.id.wheel);
    }
}