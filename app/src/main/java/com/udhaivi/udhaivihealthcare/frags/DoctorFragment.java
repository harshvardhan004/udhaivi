package com.udhaivi.udhaivihealthcare.frags;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.udhaivi.udhaivihealthcare.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DoctorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DoctorFragment extends Fragment implements DoctorFragModel.ItemClickListener {

    DoctorFragModel adapter;
    ArrayList<String> address1 = new ArrayList<>();

    ArrayList<String> name = new ArrayList<String>();
    ArrayList<String> address = new ArrayList<String>();
    ArrayList<String> img = new ArrayList<String>();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String title;
    private int page;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    LinearLayout sched;
    CardView card1;
    final Calendar myCalendar= Calendar.getInstance();
    private DatePickerDialog datePickerDialog;
    int countercheck = 0;
    CircularProgressButton calendar;
    private int mYear, mDay, mHour, mMinute;
    int day, mMonth;
    int sec = 00;
    String hr, min;

    public DoctorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
     * @return A new instance of fragment FirstFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DoctorFragment newInstance(int page, String title) {
        DoctorFragment fragment = new DoctorFragment();
        Bundle args = new Bundle();
        args.putInt("1", page);
        args.putString("A", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            page = getArguments().getInt("1", 0);
            title = getArguments().getString("A");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        RecyclerView recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DoctorFragModel(getContext(), name, address, img);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        Call_Server();
    }



    public void Call_Server() {

        name.clear();
        address.clear();
        img.clear();

        try {
            RequestQueue MyRequestQueue = Volley.newRequestQueue(getActivity());
            //  String url = Config.URL_API;//Helpers.getappUrl(this); // <----enter your post url here
            String url = "http://udhaivihealthcare.com/php/doctor_list.php";


            Log.d("URl- ", url);

            StringRequest MyStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //   Log.d("xyz", user_id_txt);
                    Log.d("status", response);

                    if (response.equals("0")) {
                        Toast.makeText(getContext(), "Not Found!", Toast.LENGTH_LONG).show();
                    } else {

                        try {

                            JSONObject tbldata = new JSONObject(response);
                            Log.d("tbldata", String.valueOf(tbldata.getJSONArray("tbldata")));
                            JSONArray parentArray = new JSONArray();
                            parentArray = tbldata.getJSONArray("tbldata");

                            for (int i = 0; i < parentArray.length(); i++) {

                                name.add((String) parentArray.getJSONObject(i).get("member_name"));
                                Log.d("awd3dd", String.valueOf(parentArray.getJSONObject(i).get("member_name")));

                                address.add((String) parentArray.getJSONObject(i).get("address"));
                                Log.d("address", String.valueOf(parentArray.getJSONObject(i).get("address")));

                                img.add((String) parentArray.getJSONObject(i).get("contact _category"));
                                Log.d("imgsrc_center", String.valueOf(parentArray.getJSONObject(i).get("contact _category")));
                            }

                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
            }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getContext(), "Data not loaded, please try after sometime....", Toast.LENGTH_LONG).show();

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
            ex.printStackTrace();
            Toast.makeText(getContext(), "Data not loaded, please try after sometime!", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onItemClick(View view, int position) {

    }
}