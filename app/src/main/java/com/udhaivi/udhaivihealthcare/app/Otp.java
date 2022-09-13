package com.udhaivi.udhaivihealthcare.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chaos.view.PinView;
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;
import com.udhaivi.udhaivihealthcare.R;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Otp extends AppCompatActivity {

    TextView timer;
    String otp_generated, contactNo, id1;
    PinView pin;
    SmsVerifyCatcher smsVerifyCatcher;
    String phone;
    private ProgressDialog progressDialog;
    String fetchedotp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        timer = findViewById(R.id.timer);
        pin = findViewById(R.id.pinView);

        phone = getIntent().getStringExtra("phone");
        Log.d("SFffs", getIntent().getStringExtra("phone"));

        timer.setClickable(false);

        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText( millisUntilFinished / 1000 + " Sec");
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                timer.setText("Resent OTP");
                timer.setClickable(true);

            }

        }.start();
//
//        timer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(phone != ""){
////                    ((Signup) getApplicationContext()).Call_server();
//                    pin.setText(null);
//                    fetchedotp = "";
//                    Call_server();
//                }
//            }
//        });

        findViewById(R.id.getotp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pin.getText().toString() == "") return;
                verify_otp(pin.getText().toString());
            }
        });

        smsVerifyCatcher = new SmsVerifyCatcher(this, new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {
                String code = parseCode(message);//Parse verification code
//                Log.d("Agilanbu OTP", code);
//                Toast.makeText(Otp.this, "Agilanbu OTP: " + code, Toast.LENGTH_LONG).show();
                pin.setText(code);//set code in edit text
                fetchedotp = code;
//                if(pin.getText() != null){
//                    startActivity(new Intent(Otp.this, Signupdetails.class));
//                }
                showConnectDialog("Verifying OTP...");
                verify_otp(fetchedotp);
            }
        });
    }

    private String parseCode(String message) {
        Pattern p = Pattern.compile("\\b\\d{4}\\b");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        return code;
    }

    @Override
    protected void onStart() {
        super.onStart();
        smsVerifyCatcher.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        smsVerifyCatcher.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void Call_server() {
        try {
            RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
            //  String url = Config.URL_API;//Helpers.getappUrl(this); // <----enter your post url here
//            String url = Config.URL_API_Attendance+"?"+data;
            Log.d("url", phone);

            StringRequest MyStringRequest = new StringRequest(Request.Method.POST, "http://udhaivihealthcare.com/php/msg_api/POST/sendsms_post.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("response", response);

//                    smsVerifyCatcher = new SmsVerifyCatcher(Otp.this, new OnSmsCatchListener<String>() {
//                        @Override
//                        public void onSmsCatch(String message) {
//                            String code = parseCode(message);//Parse verification code
////                            Log.d("Agilanbu OTP", code);
////                            Toast.makeText(Otp.this, "Agilanbu OTP: " + code, Toast.LENGTH_LONG).show();
//                            pin.setText(code);//set code in edit text
//                            if(pin.getText() != null){
//                                startActivity(new Intent(Otp.this, Signupdetails.class));
//                                finish();
//                            }
//                        }
//                    });

                }
            }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Otp.this, "Data not loaded, please try after sometime!", Toast.LENGTH_LONG).show();
                }
            }) {
                protected Map<String, String> getParams() {
                    Map<String, String> MyData = new HashMap<String, String>();
                    MyData.put("phone", phone);
                    return MyData;
                }
            };

            MyRequestQueue.add(MyStringRequest);
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(Otp.this, "Data not loaded, please try after sometime!", Toast.LENGTH_LONG).show();
        }
    }

    public void verify_otp(String s) {
        try {
//            Intent i2 = new Intent(Otp.this, Signupdetails.class);
//            i2.putExtra("phone", phone);
//            i2.putExtra("otp", s);
//            startActivity(i2);
//            finish();
            RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
            //  String url = Config.URL_API;//Helpers.getappUrl(this); // <----enter your post url here
//            String url = Config.URL_API_Attendance+"?"+data;
            Log.d("urlwwww", s);

            StringRequest MyStringRequest = new StringRequest(Request.Method.POST, "https://udhaivihealthcare.com/php/msg_api/verify_otp.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("response", response);

                    if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();

                    Intent i2 = new Intent(Otp.this, Signupdetails.class);
                    i2.putExtra("phone", phone);
                    i2.putExtra("otp", s);
                    startActivity(i2);
                    finish();
                }
            }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Otp.this, "Data not loaded, please try after sometime!", Toast.LENGTH_LONG).show();
                }
            }) {
                protected Map<String, String> getParams() {
                    Map<String, String> MyData = new HashMap<String, String>();
                    MyData.put("phone", phone);
                    MyData.put("otp", s);
                    return MyData;
                }
            };

            MyRequestQueue.add(MyStringRequest);
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(Otp.this, "Data not loaded, please try after sometime!", Toast.LENGTH_LONG).show();
        }
    }

    public void showConnectDialog(String string) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(string);
        if (!progressDialog.isShowing()) progressDialog.show();

    }

    public void dissMissDialog() {
        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
    }
}