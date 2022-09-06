package com.udhaivi.udhaivihealthcare.menu;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import java.util.ArrayList;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.udhaivi.udhaivihealthcare.R;

import butterknife.ButterKnife;

import java.util.ArrayList;

    public class Payment extends AppCompatActivity  {
        //reference - https://medium.com/fnplus/integrating-upi-payments-inside-your-android-app-514d800d5baa
        int UPI_PAYMENT = 0;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_payment);
            ButterKnife.bind(this);
            // For application development contact to - parthdarji812@gmail.com
            //  Contact no : +919033278089
            // Alternet contact no : +918160151681
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Getting the values from the EditTexts
                    String amount = "1";
                    String note = "Test";
                    String name = "Narayan Test";
                    String upiId = "nanditamahant@ybl";
                    payUsingUpi(amount, upiId, name, note);
                }
            },0);

//            amountEt.setText(getIntent().getStringExtra("pack_paymemt").toString().trim());
//            noteEt.setText(getIntent().getStringExtra("pack_name").trim());
//            nameEt.setText("Udhaivi Healthcare");
//            upiIdEt.setText("7338667777@hdfcbank");
//
//            if(amountEt.getText().toString().equals("") || noteEt.getText().toString().equals("")) return;
//
//            payUsingUpi(amountEt.getText().toString(), upiIdEt.getText().toString(), nameEt.getText().toString(), noteEt.getText().toString());
        }

        public void payUsingUpi( String amount, String upiId, String name, String note) {
//
//            Uri uri = Uri.parse("upi://pay").buildUpon()
//                    .appendQueryParameter("pa", upiId)
//                    .appendQueryParameter("pn", name)
//                    .appendQueryParameter("tn", note)
//                    .appendQueryParameter("am", amount)
//                    .appendQueryParameter("cu", "INR")
//                    .build();

//            Uri uri = Uri.parse("upi://pay").buildUpon()
//                    .appendQueryParameter("pa", upiId)  // google pay business id
//                    .appendQueryParameter("pn", name)
//                    .appendQueryParameter("mc", "")            /// 1st param - use it (it was commented on my earlier tutorial)
//                    //.appendQueryParameter("tid", "02125412")
//                    .appendQueryParameter("tr", "44332255")   /// 2nd param - use it (it was commented on my earlier tutorial)
//                    .appendQueryParameter("tn", note)
//                    .appendQueryParameter("am", amount)
//                    .appendQueryParameter("cu", "INR")
//                    //.appendQueryParameter("refUrl", "blueapp")
//                    .build();

            Uri uri = Uri.parse("upi://pay").buildUpon()
                    .appendQueryParameter("pa", upiId)                  // YOUR UPI ID
                    .appendQueryParameter("pn", upiId)                  // USE YOUR UPI ID NOT YOUR NAME
                    .appendQueryParameter("mc", "1234")
                    .appendQueryParameter("tid", "12343333356")
                    .appendQueryParameter("tr", upiId)
                    .appendQueryParameter("am", amount)
                    .appendQueryParameter("mam", amount)
                    .appendQueryParameter("cu", "INR")
                    .build();

            Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
            upiPayIntent.setData(uri);

            Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");

            if (null != chooser.resolveActivity(getPackageManager())) {
                startActivityForResult(chooser, UPI_PAYMENT);
            } else {
                Toast.makeText(this, "No UPI app found, please install one to continue", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if(requestCode == UPI_PAYMENT && resultCode== RESULT_OK || resultCode == 11){
                if (data != null) {
                    String trxt = data.getStringExtra("response");
                    Log.e("UPI", "onActivityResult: "+trxt);
                    ArrayList<String> dataList = new ArrayList<String>();
                    dataList.add(trxt);
                    upiPaymentDataOperation(dataList);
                } else {
                    Log.e("UPI", "onActivityResult: " + "Return data is null");
                    ArrayList<String> dataList = new ArrayList<String>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
            }else{
                Log.e("UPI", "onActivityResult: " + "Return data is null"); //when user simply back without payment
                ArrayList<String> dataList = new ArrayList<String>();
                dataList.add("nothing");
                upiPaymentDataOperation(dataList);
            }
        }


        private void upiPaymentDataOperation(ArrayList<String> data) {
            if (isConnectionAvailable(this)) {
                String str = data.get(0);
                Log.e("UPIPAY", "upiPaymentDataOperation: " + str);
                String paymentCancel = "";
                if (str == null) str = "discard";
                String status = "";
                String approvalRefNo = "";
                String txnRef = "";
                String responseCode = "";
                String txnId = "";

                String[] response = str.split("&");
                for (String res : response) {
                    String[] equalStr = res.split("=");
                    if (equalStr.length >= 2) {
                        if (equalStr[0].equalsIgnoreCase("Status")) {
                            status = equalStr[1].toLowerCase();
                        }
                        if (equalStr[0].equalsIgnoreCase("ApprovalRefNo")) {
                            approvalRefNo = equalStr[1];
                        }
                        if (equalStr[0].equalsIgnoreCase("txnRef")) {
                            txnRef = equalStr[1];
                        } if (equalStr[0].equalsIgnoreCase("txnId")) {
                            txnId = equalStr[1];
                        }
                    }else{
                        paymentCancel = "Payment cancelled by user.";
                    }
                }

                if (status.equalsIgnoreCase("success")) {
                    //Code to handle successful transaction here.
                    Toast.makeText(this, "Transaction successful.", Toast.LENGTH_SHORT).show();
                    Log.e("UPI", "txnId: "+txnId);
                    Log.e("UPI", "responseStr: "+approvalRefNo);
                    Log.e("UPI", "txnRef: "+txnRef);
                } else if (paymentCancel.equalsIgnoreCase("Payment cancelled by user.")) {
                    Toast.makeText(this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
            }

            finish();

        }



        @Override
        protected void onResume() {
            super.onResume();
        }

        @Override
        protected void onStart() {
            super.onStart();
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
        }


        @Override
        public void onBackPressed() {
            super.onBackPressed();
        }

        public static boolean isConnectionAvailable(Context context) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnected()
                        && netInfo.isConnectedOrConnecting()
                        && netInfo.isAvailable()) {
                    return true;
                }
            }
            return false;
        }
    }