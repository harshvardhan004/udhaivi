package com.udhaivi.udhaivihealthcare.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.udhaivi.udhaivihealthcare.R;
import com.udhaivi.udhaivihealthcare.activity.DeviceScanActivity;
import com.udhaivi.udhaivihealthcare.frags.HistoryFragModel;
import com.udhaivi.udhaivihealthcare.menu.PdfActivity;
import com.udhaivi.udhaivihealthcare.menu.PhotoUpload;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Report extends AppCompatActivity implements HistoryFragModel.ItemClickListener{

    ArrayList<String> image = new ArrayList<>();
    ArrayList<String> pdf_title = new ArrayList<>();
    ArrayList<String> descrip = new ArrayList<>();
    ArrayList<String> type = new ArrayList<>();
    private HistoryFragModel adapter;
    private String adminid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryFragModel(this, image, pdf_title, descrip, type);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        SharedPreferences prefs = getSharedPreferences("User_Details", MODE_PRIVATE);
        adminid = prefs.getString("user_id", "");//"No name defined" is the default value.

        findViewById(R.id.addhistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomsheetdialog();
            }
        });

        Call_server();

    }

    private CardView cd1;

    public void bottomsheetdialog() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.TransparentDialog);
        View parentView = getLayoutInflater().inflate(R.layout.add_new_files, null);
        bottomSheetDialog.setContentView(parentView);

        cd1 = parentView.findViewById(R.id.cd1);
        cd1.setBackgroundResource(R.drawable.card_view_bg);

        bottomSheetDialog.show();

        CardView pdf = parentView.findViewById(R.id.pdf);
        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Report.this, PdfActivity.class));
                bottomSheetDialog.dismiss();
            }
        });

        CardView stdtxt = parentView.findViewById(R.id.photo);
        stdtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetDialog.dismiss();
                startActivity(new Intent(Report.this, PhotoUpload.class));
            }
        });
    }

    public void Call_server() {
        try {
            RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
            String url = "http://udhaivihealthcare.com/php/get_prescription_list.php";//Helpers.getappUrl(this); // <----enter your post url here
            StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("responsesssssss", response);
                    try {

                        JSONObject tbldata = new JSONObject(response);
                        Log.d("tbldata", String.valueOf(tbldata.getJSONArray("tbldata")));
                        JSONArray parentArray = new JSONArray();
                        parentArray = tbldata.getJSONArray("tbldata");

                        for (int i = 0; i < parentArray.length(); i++) {

                            image.add((String) parentArray.getJSONObject(i).get("image"));
                            pdf_title.add((String) parentArray.getJSONObject(i).get("pdf_title"));
                            descrip.add((String) parentArray.getJSONObject(i).get("descrip"));
                            type.add(parentArray.getJSONObject(i).getString("type"));
                        }

                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Report.this, "Data not loaded, please try after sometime!", Toast.LENGTH_LONG).show();
                }
            }) {
                protected Map<String, String> getParams() {
                    Map<String, String> MyData = new HashMap<String, String>();

                    MyData.put("username",adminid);

                    Log.d("MyData ", String.valueOf(MyData));

                    return MyData;
                }

            };

            MyRequestQueue.add(MyStringRequest);
        } catch (Exception ex) {
            ex.printStackTrace();   Toast.makeText(this, "Data not loaded, please try after sometime!", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onResume(){
        super.onResume();

        image.clear();
        pdf_title.clear();
        descrip.clear();
        type.clear();

        Call_server();

    }
}