package com.udhaivi.udhaivihealthcare.frags;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.udhaivi.udhaivihealthcare.R;

import java.util.ArrayList;
import java.util.Calendar;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class DoctorFragModel extends RecyclerView.Adapter<DoctorFragModel.ViewHolder> {

    ArrayList<String> name = new ArrayList<String>();
    ArrayList<String> address = new ArrayList<String>();
    ArrayList<String> imgsrc_center = new ArrayList<String>();
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    Context context;

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


    DoctorFragModel(Context context, ArrayList<String> name, ArrayList<String> address, ArrayList<String> img) {
        this.mInflater = LayoutInflater.from(context);
        this.name = name;
        this.address = address;
        this.imgsrc_center = img;

        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public DoctorFragModel.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_doctor, parent, false);
        return new DoctorFragModel.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(DoctorFragModel.ViewHolder holder, final int position) {
//        String t1 = name.get(position);
//        String t2 = attday.get(position);
//        String t3 = attdate.get(position);
        holder.text1.setText(name.get(position).trim());
        holder.text2.setText(address.get(position).trim());
        holder.designation.setText(imgsrc_center.get(position).trim());


    }

    // binds the data to the TextView in each row


    // total number of rows
    @Override
    public int getItemCount() {
        return name.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView text1, text2, designation;
        ImageView hospitalcard;


        ViewHolder(View itemView) {
            super(itemView);
            text1 = itemView.findViewById(R.id.doctorname);
            text2 = itemView.findViewById(R.id.address);
            designation = itemView.findViewById(R.id.designation);
//
            sched = itemView.findViewById(R.id.scheddoctor);
//            card1 = itemView.findViewById(R.id.doctorcard);
//            calendar = itemView.findViewById(R.id.calendar);
//
//            sched.setVisibility(View.GONE);
//
//            card1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if(countercheck == 0){
//                        TransitionManager.beginDelayedTransition((ViewGroup) view, new AutoTransition());
//                        sched.setVisibility(View.VISIBLE);
//                        calendar.setClickable(true);
//                        countercheck = 1;
//                    }
//                    else{
//                        countercheck = 0;
//                        TransitionManager.beginDelayedTransition((ViewGroup) view, new AutoTransition());
//                        new Handler(Looper.myLooper()).postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                //do what you want
//                                sched.setVisibility(View.GONE);
//                            }
//                        }, 300);
//                    }
//                }
//            });
//
//            calendar.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    Calendar c = Calendar.getInstance();
//                    mYear = c.get(Calendar.YEAR);
//                    mMonth = c.get(Calendar.MONTH);
//                    mDay = c.get(Calendar.DAY_OF_MONTH);
//                    DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
//                        @Override
//                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                            Log.d("bjboi", "" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
////                      String gff = ("" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
//
//                            day = dayOfMonth;
//                            mMonth = (monthOfYear + 1);
//                            mYear = year;
//
//                            Calendar mcurrentTime = Calendar.getInstance();
//                            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
//                            int minute = mcurrentTime.get(Calendar.MINUTE);
//                            TimePickerDialog mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
//
//                                @Override
//                                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
//
//                                    String dfsd = ("0$selectedHour:$selectedMinute:$selectedMinute");
//
//                                    if (selectedHour > 9) {
//                                        hr = String.valueOf(selectedHour);
//
//                                    } else {
//                                        hr = "0" + selectedHour;
//                                    }
//
//                                    if (selectedMinute > 9) {
//                                        min = String.valueOf(selectedMinute);
//
//                                    } else {
//                                        min = "0" + selectedMinute;
//                                    }
//
//                                    Log.d("sefff", hr+min);
//
////                                edit_sched(s, s1, s2);
//                                }
//
//                            }, hour, minute, true); //Yes 24 hour time
//
//                            mTimePicker.setTitle("Select Time");
//                            mTimePicker.show();
//                        }
//                    }, mYear, mMonth, mDay);
//                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
//                    datePickerDialog.show();
//                }
//            });




            itemView.setOnClickListener(this);

        }


        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return name.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(DoctorFragment itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}