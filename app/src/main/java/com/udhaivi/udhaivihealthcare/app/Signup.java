package com.udhaivi.udhaivihealthcare.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.udhaivi.udhaivihealthcare.R;
import com.udhaivi.udhaivihealthcare.adapter.TotalDataAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {

    TextInputEditText ph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ph = findViewById(R.id.ph);

        findViewById(R.id.getotp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call_server();
            }
        });

    }

    public void Call_server() {
        try {
            RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
            //  String url = Config.URL_API;//Helpers.getappUrl(this); // <----enter your post url here
//            String url = Config.URL_API_Attendance+"?"+data;
            Log.d("url", String.valueOf(ph.getText()));

            StringRequest MyStringRequest = new StringRequest(Request.Method.POST, "https://udhaivihealthcare.com/php/msg_api/insert_data.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("response", response);

                    if(response.equals("User Already Exist")) {
                        Toast.makeText(Signup.this, "User Already Exists! Please Login...", Toast.LENGTH_SHORT).show();
                        return;
                    }
                        else{
                            Intent i = new Intent(Signup.this, Otp.class);
                            i.putExtra("phone", String.valueOf(ph.getText()));
                            startActivity(i);
                            finish();
                        }
                }
            }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Signup.this, "Data not loaded, please try after sometime!", Toast.LENGTH_LONG).show();
                }
            }) {
                protected Map<String, String> getParams() {
                    Map<String, String> MyData = new HashMap<String, String>();
                    MyData.put("phone", String.valueOf(ph.getText()));
                    return MyData;
                }
            };

            MyRequestQueue.add(MyStringRequest);
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(Signup.this, "Data not loaded, please try after sometime!", Toast.LENGTH_LONG).show();
        }
    }

}