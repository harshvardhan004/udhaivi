package com.udhaivi.udhaivihealthcare.frags;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
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
import com.udhaivi.udhaivihealthcare.app.WebView;
import com.udhaivi.udhaivihealthcare.menu.Payment;
import com.udhaivi.udhaivihealthcare.menu.PhotoUpload;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment implements HistoryFragModel.ItemClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String adminid;
    ArrayList<String> image = new ArrayList<>();
    ArrayList<String> pdf_title = new ArrayList<>();
    ArrayList<String> descrip = new ArrayList<>();
    ArrayList<String> type = new ArrayList<>();
    private HistoryFragModel adapter;

    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        LocalDate today = LocalDate.now(ZoneId.of("UTC+05:30"));

        DateTimeFormatter userFormatter
                = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);
        System.out.println(today.format(userFormatter));

        SharedPreferences prefs = getActivity().getSharedPreferences("User_Details", MODE_PRIVATE);
        adminid = prefs.getString("user_id", "");//"No name defined" is the default value.

        RecyclerView recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HistoryFragModel(getContext(), image, pdf_title, descrip, type);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);


//        TextView date = view.findViewById(R.id.reportdate);
//        date.setText(today.format(userFormatter));
//
//        view.findViewById(R.id.doctorcard).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getActivity(), WebView.class));
//            }
//        });
        Call_server();
    }


    public void Call_server() {
        try {
            RequestQueue MyRequestQueue = Volley.newRequestQueue(getActivity());
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
                    Toast.makeText(getActivity(), "Data not loaded, please try after sometime!", Toast.LENGTH_LONG).show();
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
            ex.printStackTrace();   Toast.makeText(getActivity(), "Data not loaded, please try after sometime!", Toast.LENGTH_LONG).show();
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