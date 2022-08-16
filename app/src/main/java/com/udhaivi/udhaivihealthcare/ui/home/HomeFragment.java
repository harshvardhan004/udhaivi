package com.udhaivi.udhaivihealthcare.ui.home;

import static android.content.Context.MODE_PRIVATE;
import static android.graphics.BlendMode.COLOR;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.compose.ui.graphics.Color;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.udhaivi.udhaivihealthcare.Dashboard;
import com.udhaivi.udhaivihealthcare.ECGList;
import com.udhaivi.udhaivihealthcare.R;
import com.udhaivi.udhaivihealthcare.activity.MainActivity;
import com.udhaivi.udhaivihealthcare.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private List<Map<String,String>> listnew = new ArrayList<>();
    ArrayList<String> values = new ArrayList<>();
    TextView hrtrate, bp, spo2, hrv, temp, stress, username;
    CardView ecgcard;
    String address;
    int sync = 1;
    LinearProgressIndicator line;


    @RequiresApi(api = Build.VERSION_CODES.Q)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        hrtrate = root.findViewById(R.id.hrtrate);
        bp = root.findViewById(R.id.bp);
        spo2 = root.findViewById(R.id.spo2);
        hrv = root.findViewById(R.id.hrv);
        temp = root.findViewById(R.id.temp);
        stress = root.findViewById(R.id.stress);
        username = root.findViewById(R.id.username);
        line = root.findViewById(R.id.horizontalprogress);

        line.animate();

        SharedPreferences editor = getActivity().getSharedPreferences("User_Details", MODE_PRIVATE);
        String name = editor.getString("name", "");
        String phone = editor.getString("phone", "");

        username.setText(name);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter("BDATA"));

        root.findViewById(R.id.ecgcard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                address = String.valueOf((((Dashboard) getActivity()).getaddress()));
                Intent go = new Intent(getActivity(), ECGList.class);
                go.putExtra("address", address);
                startActivity(go);
            }
        });

        root.findViewById(R.id.sos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ViewDialog alert = new ViewDialog();
//                alert.showDialog(getActivity());
                callsos();
            }
        });

        root.findViewById(R.id.reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sync = 0;

                line.setVisibility(View.VISIBLE);
                ((Dashboard)getActivity()).getalldata();
                ((Dashboard)getActivity()).showConnectDialog(getString(R.string.loading));

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        getdata();
                    }
                }, 2000);
            }
        });

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
//                getdata();
                sync = 0;
            }
        }, 3000);

        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();

        return root;

    }

    public void callsos(){

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel: +91 7688837688"));
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);

    }

    public void getdata(ArrayList<String> val){

//        this.values.clear();

//        Log.d("afs4f485f4d", String.valueOf(((Dashboard) getActivity()).getlistdata()));

//        this.values = ((Dashboard) getActivity()).getlistdata();

        if(val.size() == 0)return;
        hrtrate.setText(val.get(7));
        bp.setText(val.get(2)+"/"+ val.get(3));
        spo2.setText(val.get(0)+"%");
        hrv.setText(val.get(5));
        temp.setText(val.get(6)+"C");
        stress.setText(val.get(4));


//        ((Dashboard)getActivity()).dissMissDialog();

//        String query = "insert into vitals(date, hrtrate, hbp, lbp, spo, hrv, temp, stress ) values('"+values.get(1)+"', '"+values.get(5)+"', '"+values.get(2)+"', '"+values.get(3)+"', '"+values.get(0)+"', '"+values.get(6)+"', '"+values.get(7)+"', '"+values.get(4)+"')";
        if(sync == 0) {
            sync = 1;
            Call_server(val);
        }
        line.setVisibility(View.GONE);
    }


    public void Call_server(final ArrayList<String> x) {
        try {
            RequestQueue MyRequestQueue = Volley.newRequestQueue(getActivity());
            //  String url = Config.URL_API;//Helpers.getappUrl(this); // <----enter your post url here
//            String url = Config.URL_API_Attendance+"?"+data;
            Log.d("url", String.valueOf(x));

            StringRequest MyStringRequest = new StringRequest(Request.Method.GET, "http://udhaivihealthcare.com/php/insert.php?post="+x, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("response", response);


                }
            }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), "Data not loaded, please try after sometime!", Toast.LENGTH_LONG).show();

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
            Toast.makeText(getActivity(), "Data not loaded, please try after sometime!", Toast.LENGTH_LONG).show();
        }
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            //  values.clear();

            Log.d("receiver", "Got_message: " + intent.getStringArrayListExtra("peerId"));

            values = intent.getStringArrayListExtra("peerId");

            getdata(values);

//            if(values.size() == 0)return;
//            hrtrate.setText(values.get(5));
//            bp.setText(values.get(2)+"/"+values.get(3));
//            spo2.setText(values.get(0)+"%");
//            hrv.setText(values.get(6));
//            temp.setText(values.get(7)+"C");
//            stress.setText(values.get(4));
//
////            if(((Dashboard)getActivity()).progressDialog.isShowing())
//            ((Dashboard)getActivity()).dissMissDialog();
//
////        String query = "insert into vitals(date, hrtrate, hbp, lbp, spo, hrv, temp, stress ) values('"+values.get(1)+"', '"+values.get(5)+"', '"+values.get(2)+"', '"+values.get(3)+"', '"+values.get(0)+"', '"+values.get(6)+"', '"+values.get(7)+"', '"+values.get(4)+"')";
//            if(sync == 0) {
//                sync = 1;
//                Call_server(values);
//            }

            values.clear();
        }
    };

}