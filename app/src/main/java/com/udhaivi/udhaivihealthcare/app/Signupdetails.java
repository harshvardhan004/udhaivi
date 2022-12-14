package com.udhaivi.udhaivihealthcare.app;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Signupdetails extends AppCompatActivity {

    String phone11, macid;
    TextInputEditText name, email, pass, cpass, address, pin;
    int  year, month, date;
    TextView dobdate;
    String dob="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupdetails);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        cpass = findViewById(R.id.cpass);
        dobdate= findViewById(R.id.dobdate);
        address= findViewById(R.id.address);
        pin= findViewById(R.id.pin);

        phone11 = getIntent().getStringExtra("phone");
//        Log.d("aefd", phone11);

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(this.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        macid = wInfo.getMacAddress();

        findViewById(R.id.getotp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String newpass = pass.getText().toString();
                String cpasss = cpass.getText().toString();

                radioSexGroup = (RadioGroup) findViewById(R.id.radioSex);

// get selected radio button from radioGroup
                int selectedId = radioSexGroup.getCheckedRadioButtonId();
// find the radiobutton by returned id
                radioSexButton = (RadioButton) findViewById(selectedId);



                Log.d("jhvhv", String.valueOf(newpass.equals(cpasss)));

                if(String.valueOf(dob) == "" && String.valueOf(pin.getText()) == "" && String.valueOf(address.getText()) == "" && String.valueOf(name.getText()) == "" && String.valueOf(name.getText()) == "" && String.valueOf(email.getText()) == "" && String.valueOf(pass.getText()) == "" && String.valueOf(cpass.getText()) == "" ) {
                    Toast.makeText(Signupdetails.this, "Please Enter all the fields", Toast.LENGTH_LONG).show();
                }
                else {
                    if(pass.getText().toString().equals(cpass.getText().toString())) {
                        Call_server();
                    }else{
                        Toast.makeText(Signupdetails.this, "Passwords do not match!", Toast.LENGTH_LONG).show();
                        pass.setFocusableInTouchMode(true);
                        pass.requestFocus();
                        InputMethodManager inputMethodManager = (InputMethodManager) Signupdetails.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    }
                }
            }
        });

        findViewById(R.id.dob).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog mdiDialog =new DatePickerDialog(Signupdetails.this,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                      //  Toast.makeText(getApplicationContext(),year+ " "+monthOfYear+" "+dayOfMonth,Toast.LENGTH_LONG).show();

                         dob=year+"-"+monthOfYear+"-"+dayOfMonth;
                        dobdate.setText(dayOfMonth+"-"+monthOfYear+"-"+year);

                    }
                }, year, month, date);
                mdiDialog.show();

            }

        });


//    addListenerOnButton();
}
    private RadioGroup radioSexGroup;
    private RadioButton radioSexButton;

    public void addListenerOnButton() {
        radioSexGroup = (RadioGroup) findViewById(R.id.radioSex);

// get selected radio button from radioGroup
                int selectedId = radioSexGroup.getCheckedRadioButtonId();
// find the radiobutton by returned id
                radioSexButton = (RadioButton) findViewById(selectedId);

                Toast.makeText(this, radioSexButton.getText(), Toast.LENGTH_SHORT).show();

    }

    public void Call_server() {
        try {


            RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
            //  String url = Config.URL_API;//Helpers.getappUrl(this); // <----enter your post url here
//            String url = Config.URL_API_Attendance+"?"+data;
            Log.d("url", String.valueOf(phone11)+"  "+macid);

            StringRequest MyStringRequest = new StringRequest(Request.Method.POST, "https://udhaivihealthcare.com/php/msg_api/insert_customer_data.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
             //       Log.d("response", response);
               //     Log.d("integert", String.valueOf(Integer.parseInt(response)));

                    if(Integer.parseInt(response) == 200){
                        Toast.makeText(Signupdetails.this,"Profile Created Successfully! Contact admin to activate your account...", Toast.LENGTH_LONG).show();
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                               startActivity(new Intent(Signupdetails.this, LoginSplash.class));
                               finish();
                            }
                        }, 2000);
                    }
                }
            }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Signupdetails.this, "Data not loaded, please try after sometime!", Toast.LENGTH_LONG).show();

                }
            }) {
                protected Map<String, String> getParams() {
                    Map<String, String> MyData = new HashMap<String, String>();
                    MyData.put("phone", phone11);
                    MyData.put("macid", macid);
                    MyData.put("dob", dob);
                    MyData.put("address", address.getText().toString());
                    MyData.put("pin_code", pin.getText().toString());
                    MyData.put("gender", String.valueOf(radioSexButton.getText()));
                    MyData.put("pass", String.valueOf(pass.getText().toString()));
                    MyData.put("name", String.valueOf(name.getText()));
                    MyData.put("email", String.valueOf(email.getText()));
                    return MyData;
                }
            };

            MyRequestQueue.add(MyStringRequest);
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(Signupdetails.this, "Data not loaded, please try after sometime!", Toast.LENGTH_LONG).show();
        }
    }

}