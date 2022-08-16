package com.udhaivi.udhaivihealthcare.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.udhaivi.udhaivihealthcare.R;
import com.udhaivi.udhaivihealthcare.activity.DeviceScanActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class LoginActivity extends AppCompatActivity {

    CircularProgressButton textButton;
    TextInputEditText phone, pass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        textButton = findViewById(R.id.signup);
        phone = findViewById(R.id.phone);
        pass = findViewById(R.id.password1);

        ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null) {
            Internet_connection();
        }

        textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textButton.startAnimation();

                try { verifyUser();
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        });
    }

    public void verifyUser(){

        String user_id_txt ="";
        String password_txt = "";

        user_id_txt = phone.getText().toString().trim();
        Log.d("xyz", user_id_txt);
        password_txt = pass.getText().toString().trim();

        if (user_id_txt.isEmpty()) {
            phone.setError("Enter Phone Number");
            phone.requestFocus();
            return;
        }

        if (password_txt.isEmpty()) {
            pass.setError("Enter Password");
            pass.requestFocus();
            return;
        }

        //  String query = "select * from `tbl_admissionform` where emp_id = '"+user_id_txt+"' and user_password ='"+password_txt+"' ";
        //   Log.d("query_student", query);

        // Server call
        Call_server("emp_id="+user_id_txt+"&password="+password_txt);

    }


    public void Call_server(final String data) {
        try {
            RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
            //  String url = Config.URL_API;//Helpers.getappUrl(this); // <----enter your post url here
            String url = "http://udhaivihealthcare.com/php/login_api.php"+"?"+data;


            Log.d("URl- ",url);

            StringRequest MyStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //   Log.d("xyz", user_id_txt);
                    Log.d("status", response);

                    if(response.equals("0")) {
                        Toast.makeText(LoginActivity.this, "Not Found!", Toast.LENGTH_LONG).show();
                    }else{

                        try {

                            JSONObject tbldata = new JSONObject(response);
                            Log.d("tbldata", String.valueOf(tbldata.getJSONArray("tbldata")));
                            JSONArray parentArray = new JSONArray();
                            parentArray = tbldata.getJSONArray("tbldata");

                            for (int i = 0; i < parentArray.length(); i++) {

                                JSONObject row = parentArray.getJSONObject(i);

                                String emp_id = row.getString("name");

                                    String firstname = row.getString("username");
                                    String contact1 = row.getString("id");

                                    //Log.d("id",id);
//                                    String classes = innerElem.getString("classes");
//                                    //Log.d("classes",classes);
//                                    String section = innerElem.getString("section");
//                                    Log.d("section",section);
//                                    String firstname = innerElem.getString("firstname");
//                                    //Log.d("firstname",firstname);
//                                    String middlename = innerElem.getString("middlename");
//                                    //Log.d("middlename",middlename);
//                                    String lastname = innerElem.getString("lastname");
//                                    //Log.d("lastname",lastname);
//                                    String gender = innerElem.getString("gender");
//                                    //Log.d("gender",gender);
//
//                                    String dob = innerElem.getString("dob");
//                                    //Log.d("dob",dob);
//                                    String father = innerElem.getString("father");
//                                    //Log.d("father",father);
//                                    String mother = innerElem.getString("mother");
//                                    //Log.d("mother",mother);
//                                    String caste_category = innerElem.getString("caste_category");
//                                    //Log.d("caste_category",caste_category);
//                                    String email = innerElem.getString("email");
//                                    //Log.d("firstname",firstname);
//                                    String contact1 = innerElem.getString("contact1");
//                                    //Log.d("middlename",middlename);
//                                    String address1 = innerElem.getString("address1");
//                                    //Log.d("lastname",lastname);
//                                    String address2 = innerElem.getString("address2");
//                                    //Log.d("lastname",lastname);
//
//                                    String state = innerElem.getString("state");
//                                    //Log.d("dob",dob);
//                                    String city = innerElem.getString("city");
//                                    //Log.d("father",father);
//                                    String country = innerElem.getString("country");
//                                    //Log.d("mother",mother);
//                                    String pincode = innerElem.getString("pincode");
//                                    //Log.d("caste_category",caste_category);
////                                            String admission_date = innerElem.getString("addmission_date");
////                                            Log.d("admission_date",admission_date);
//                                    String enroll = innerElem.getString("enroll");
//                                    //Log.d("middlename",middlename);
//                                    String profilepic = innerElem.getString("profilepic");
//                                    Log.d("profilepic", profilepic);
//                                    String contact2 = innerElem.getString("contact2");
//                                    //Log.d("lastname",lastname);
//                                    String father_occupation = innerElem.getString("father_occupation");
//                                    String mother_occupation = innerElem.getString("mother_occupation");



                                    SharedPreferences.Editor editor = getSharedPreferences("User_Details", MODE_PRIVATE).edit();
                                    editor.putString("firstname", firstname);
                                    editor.putString("phone", contact1);
//                                    editor.putString("lastname", innerElem.getString("lastname"));
//                                    editor.putString("address2", innerElem.getString("address2"));
//                                    editor.putString("address1", innerElem.getString("address1"));
//                                    editor.putString("id", innerElem.getString("id"));
//                                    editor.putString("emp_id", innerElem.getString("emp_id"));
//                                    editor.putString("section", innerElem.getString("section"));
//                                    editor.putString("contact1", innerElem.getString("contact1"));
//                                    editor.putString("contact2", innerElem.getString("contact2"));
//                                    editor.putString("email", innerElem.getString("email"));
//                                    editor.putString("gender", innerElem.getString("gender"));
//                                    editor.putString("blood_group", innerElem.getString("blood_group"));
//                                    editor.putString("classes", innerElem.getString("classes"));
//                                    editor.putString("father", innerElem.getString("father"));
//                                    editor.putString("school_id", innerElem.getString("school_id"));
//                                    editor.putString("dob", innerElem.getString("dob"));
//                                    editor.putString("mother", innerElem.getString("mother"));
//                                    editor.putString("emp_id", innerElem.getString("emp_id"));
//                                    editor.putString("caste_category", innerElem.getString("caste_category"));
//                                    editor.putString("city", innerElem.getString("city"));
//                                    editor.putString("state", innerElem.getString("state"));
//                                    editor.putString("country", innerElem.getString("country"));
//                                    editor.putString("pincode", innerElem.getString("pincode"));
//                                    editor.putString("enroll", innerElem.getString("enroll"));
//                                    editor.putString("profilepic", innerElem.getString("profilepic"));
//                                    editor.putString("country", innerElem.getString("country"));
//                                    editor.putString("admission_date", innerElem.getString("admission_date"));
//                                    editor.putString("father_occupation", innerElem.getString("father_occupation"));
//                                    editor.putString("mother_occupation", innerElem.getString("mother_occupation"));

                                    editor.apply();

                                    textButton.stopAnimation();

                                    Intent ia = new Intent(LoginActivity.this, DeviceScanActivity.class);
                                    startActivity(ia);
                                    finish();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            textButton.stopAnimation();

                        }

                    }

                }
            }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                @Override
                public void onErrorResponse(VolleyError error) {
                    textButton.stopAnimation();

                    Toast.makeText(LoginActivity.this, "Data not loaded, please try after sometime....", Toast.LENGTH_LONG).show();

                }
            }) {
                protected Map<String, String> getParams() {
                    Map<String, String> MyData = new HashMap<String, String>();


                    //MyData.put("emp_id", user_id_txt);
                    //Log.d("emp_id", user_id_txt);

                    //  MyData.put("password", password_txt);
                    // MyData.put("token", token);

                    return MyData;
                }
            };

            MyRequestQueue.add(MyStringRequest);
        } catch (Exception ex) {

            textButton.stopAnimation();

            ex.printStackTrace();
            Toast.makeText(LoginActivity.this, "Data not loaded, please try after sometime!", Toast.LENGTH_LONG).show();
        }
    }

    protected void Internet_connection() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No internet Connection");
        builder.setMessage("Please turn on internet connection to continue")
                .setCancelable(false)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {

                    }
                })
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_SETTINGS));
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

}