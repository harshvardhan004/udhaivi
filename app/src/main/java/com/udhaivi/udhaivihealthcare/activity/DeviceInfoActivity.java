package com.udhaivi.udhaivihealthcare.activity;

import static com.android.volley.Request.Method.POST;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.SwitchCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpResponse;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpEntity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.ClientProtocolException;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.CloseableHttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpPost;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.entity.StringEntity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.DefaultHttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.EntityUtils;
import com.google.gson.Gson;
import com.jstyle.blesdk2025.Util.BleSDK;
import com.jstyle.blesdk2025.constant.BleConst;
import com.jstyle.blesdk2025.model.MyDeviceInfo;
import com.udhaivi.udhaivihealthcare.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
/**
 *设置/获取手环基本参数 (Set / obtain basic parameters of Bracelet)
 */
public class DeviceInfoActivity extends BaseActivity {
    @BindView(R.id.radio_12h)
    RadioButton radio12h;
    @BindView(R.id.radio_24h)
    RadioButton radio24h;
    @BindView(R.id.radioGroup1)
    RadioGroup radioGroupTimeMode;
    @BindView(R.id.radio_km)
    RadioButton radioKm;
    @BindView(R.id.radio_mile)
    RadioButton radioMile;
    @BindView(R.id.radioGroup3)
    RadioGroup radioGroup_distanceUnit;
    @BindView(R.id.SwitchCompat_hand)
    SwitchCompat SwitchCompatHand;
    @BindView(R.id.left_or_light)
    SwitchCompat left_or_light;
    @BindView(R.id.Night_mode)
    SwitchCompat Night_mode;
    @BindView(R.id.Social_distance_switch)
    SwitchCompat Social_distance_switch;
    @BindView(R.id.Chinese_English_switch)
    SwitchCompat Chinese_English_switch;
    @BindView(R.id.button_deviceinfo_set)
    Button buttonDeviceinfoSet;
    @BindView(R.id.button_deviceinfo_get)
    Button buttonDeviceinfoGet;
    @BindView(R.id.b1)
    Button b1;
    @BindView(R.id.Dial_switch)
    EditText Dial_switch;
    @BindView(R.id.radio_temp_c)
    RadioButton radioTempC;
    @BindView(R.id.radio_temp_f)
    RadioButton radioTempF;
    @BindView(R.id.radioGroup_tempUnit)
    RadioGroup radioGroupTempUnit;
    @BindView(R.id.AppCompatEditText_baseHr)
    AppCompatEditText AppCompatEditText_baseHr;
    @BindView(R.id.Dialinterface)
    AppCompatEditText Dialinterface;
    @BindView(R.id.BASE_heart)
    AppCompatEditText BASE_heart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
        ButterKnife.bind(this);

        radioGroup_distanceUnit.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                sendValue(BleSDK.SetDistanceUnit(checkedId==R.id.radio_km));//距离单位切换 Distance unit switching
            }
        });
        radioGroupTimeMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                sendValue(BleSDK.SetTimeModeUnit(checkedId==R.id.radio_12h));//12/24时间切换 12 / 24 time switching
            }
        });
        SwitchCompatHand.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sendValue(BleSDK.setWristOnEnable(isChecked));//抬手亮屏开关 Raise the hand to light the screen switch
            }
        });

        radioGroupTempUnit.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                sendValue(BleSDK.setTemperatureUnit(checkedId!=R.id.radio_temp_c));//温度华氏度/摄氏度切换  Temperature Fahrenheit / Celsius switching
            }
        });
        Night_mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sendValue(BleSDK.setLightMode(isChecked));//夜间模式开关 Night mode switch
            }
        });
        Social_distance_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sendValue(BleSDK.Social_distance_switch(isChecked));//社交距离提醒开关 Social distance reminder switch
            }
        });
        Chinese_English_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sendValue(BleSDK.setLauage(isChecked));//中英文切换 Chinese English switching
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.d("asdsd", String.valueOf(sendValue(BleSDK.GetDeviceInfo())));
                sendValue(BleSDK.GetDeviceInfo());
            }
        });
    }

    @OnClick({R.id.setDialinterface, R.id.button_deviceinfo_set, R.id.button_deviceinfo_get})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.setDialinterface://设备表盘切换 Device dial switching
                if(TextUtils.isEmpty(Dialinterface.getText())){
                    showToast("Dialinterface is null");
                    return;
                }
                sendValue(BleSDK.SetDialinterface(Integer.valueOf(Dialinterface.getText().toString())));
                break;
            case R.id.button_deviceinfo_set://设置手环基本参数
                setDeviceInfo();
                break;
            case R.id.button_deviceinfo_get://获取手环基本参数
                sendValue(BleSDK.GetDeviceInfo());
                break;
        }
    }


    private void setDeviceInfo() {
        String brightness = AppCompatEditText_baseHr.getText().toString();
        MyDeviceInfo deviceBaseParameter = new MyDeviceInfo();
        deviceBaseParameter.setDistanceUnit(radioGroup_distanceUnit.getCheckedRadioButtonId() == R.id.radio_mile);//距离单位切换 Distance unit switching
        deviceBaseParameter.setIs12Hour(radioGroupTimeMode.getCheckedRadioButtonId() == R.id.radio_12h);//12/24时间切换 12 / 24 time switching
        deviceBaseParameter.setTemperature_unit(radioGroupTempUnit.getCheckedRadioButtonId() == R.id.radio_temp_c);//温度华氏度/摄氏度切换  Temperature Fahrenheit / Celsius switching
        deviceBaseParameter.setBright_screen(SwitchCompatHand.isChecked());//抬手亮屏开关 Raise the hand to light the screen switch
        deviceBaseParameter.setFahrenheit_or_centigrade(left_or_light.isChecked());//温度华氏度/摄氏度切换  Temperature Fahrenheit / Celsius switching
        deviceBaseParameter.setNight_mode(Night_mode.isChecked());//夜间模式开关 Night mode switch
        if(!TextUtils.isEmpty(Dial_switch.getText())){//设备表盘切换 Device dial switching
            deviceBaseParameter.setDialinterface(Integer.parseInt(Dial_switch.getText().toString()));
        }
        deviceBaseParameter.setSocial_distance_switch(Social_distance_switch.isChecked());//社交距离提醒开关 Social distance reminder switch
        deviceBaseParameter.setChinese_English_switch(Chinese_English_switch.isChecked());//中英文切换 Chinese English switching
        String base = BASE_heart.getText().toString();
        if(!TextUtils.isEmpty(base)&&Integer.valueOf(base)>40){//基础心率值，不低于40  Basic heart rate value, no less than 40
            deviceBaseParameter.setBaseheart(Integer.valueOf(base));
        }else{
            showToast("The input value must be greater than 40");
            return;
        }
        if (!TextUtils.isEmpty(brightness)) {//设备亮度0-5 Equipment brightness 0-5
            deviceBaseParameter.setScreenBrightness(Integer.valueOf(brightness));
        }

        sendValue(BleSDK.SetDeviceInfo(deviceBaseParameter));
    }

    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        Log.e("info",maps.toString());
        String dataType = getDataType(maps);
        Map<String, String> data = getData(maps);
        switch (dataType) {
            case BleConst.GetDeviceInfo:// 获取手环基本参数  Get basic parameters of Bracelet
            case BleConst.SetDeviceInfo:// 设置手环基本参数 Set basic parameters of Bracelet
                showDialogInfo(maps.toString());
                Log.d("Asdssds", maps.toString());


                Gson gson = new Gson();
                String json = gson.toJson(maps);
                Call_server(json);
                ;
//
//                JSONObject requestJsonObject=new JSONObject();
//                try {
//                    requestJsonObject.put("test", new JSONObject(maps));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

//                loginUser(requestJsonObject, null);

//                Log.d("sf54df54e5esf", makeServiceCallSubmit(POST, maps));

                break;
        }
    }


    public void loginUser(JSONObject jsondata, final OnLoginResponse onLoginResponse){
//        JSONObject requestJsonObject=new JSONObject();
        //            requestJsonObject.put("jsondata", jsondata);
//            requestJsonObject.put("jsondata",jsondata);
            Log.d("Sfefa0", String.valueOf(jsondata));

        JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET, "http://192.168.1.14/php/",jsondata , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("response", response.toString());
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error",error.toString());
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };


        request.setRetryPolicy(new DefaultRetryPolicy(10000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(DeviceInfoActivity.this).add(request);
    }

    public interface OnLoginResponse{
        void onResponse(boolean success);
    }


    public void Call_server(final String x) {
        try {
            RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
            //  String url = Config.URL_API;//Helpers.getappUrl(this); // <----enter your post url here
//            String url = Config.URL_API_Attendance+"?"+data;
            Log.d("url", x);

            StringRequest MyStringRequest = new StringRequest(Request.Method.GET, "http://192.168.1.5/API/public/?post="+x, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("response", response);


                }
            }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(DeviceInfoActivity.this, "Data not loaded, please try after sometime!", Toast.LENGTH_LONG).show();

                }
            }) {
                protected Map<String, String> getParams() {
                    Map<String, String> MyData = new HashMap<String, String>();
//                    MyData.put("json", x);
                    return MyData;
                }
            };

            MyRequestQueue.add(MyStringRequest);
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(DeviceInfoActivity.this, "Data not loaded, please try after sometime!", Toast.LENGTH_LONG).show();
        }
    }


    public String makeServiceCallSubmit(int method, JSONObject object) {
        String response = null;

        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            CloseableHttpResponse httpResponse = null;

            // Checking http request method type
            if (method == POST) {

                HttpPost httpPost = new HttpPost("http://192.168.1.14/API/public/");
                httpPost.setHeader("Content-type", "application/json");


                StringEntity se = new StringEntity(object.toString());
                //  se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                httpPost.setEntity(se);
                httpResponse = httpClient.execute(httpPost);

            }
            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

}
