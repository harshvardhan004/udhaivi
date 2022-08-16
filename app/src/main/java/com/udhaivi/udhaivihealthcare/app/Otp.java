package com.udhaivi.udhaivihealthcare.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.udhaivi.udhaivihealthcare.R;

public class Otp extends AppCompatActivity {

    TextView timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        timer = findViewById(R.id.timer);

        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText( millisUntilFinished / 1000 + " Sec");
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                timer.setText("done!");
            }

        }.start();

        findViewById(R.id.getotp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Otp.this, Signupdetails.class);
                startActivity(i);
            }
        });
    }
}