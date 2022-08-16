package com.udhaivi.udhaivihealthcare.ui.dashboard;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener;
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker;
import com.google.android.material.snackbar.Snackbar;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.udhaivi.udhaivihealthcare.R;
import com.udhaivi.udhaivihealthcare.databinding.FragmentDashboardBinding;
import org.joda.time.DateTime;
import java.util.ArrayList;
import kotlin.Unit;

public class DashboardFragment extends Fragment implements DatePickerListener {

    private FragmentDashboardBinding binding;
    private CircularProgressBar circularProgressBar;
    ArrayList<String> values = new ArrayList<>();

    @SuppressLint("ResourceAsColor")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        HorizontalPicker picker = (HorizontalPicker) root.findViewById(R.id.datePicker);
        picker
                .setListener(this)
                .setDays(20)
                .setOffset(10)
                .setDateSelectedColor(R.color.colororange)
                .setDateSelectedTextColor(Color.WHITE)
                .setMonthAndYearTextColor(Color.BLACK)
                .setTodayButtonTextColor(R.color.colororange)
                .setTodayDateTextColor(Color.WHITE)
                .setTodayDateBackgroundColor(Color.TRANSPARENT)
                .setUnselectedDayTextColor(Color.BLACK)
                .setDayOfWeekTextColor(Color.BLACK)
                .setUnselectedDayTextColor(Color.WHITE)
                .showTodayButton(true)
                .init();

        picker.setBackgroundColor(Color.TRANSPARENT);
        picker.setDate(new DateTime().plusDays(4));

        Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Coming Soon...", Snackbar.LENGTH_LONG);
        snackbar.show();


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void onDateSelected(DateTime dateSelected) {

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("receiver", "Got_message1: " + intent.getStringArrayListExtra("peerIdD"));

            values = intent.getStringArrayListExtra("peerIdD");

            if(values.size() !=0) {
            }

            values.clear();
        }
    };

}