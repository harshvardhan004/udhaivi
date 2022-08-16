//package com.udhaivi.udhaivihealthcare.app;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//import android.os.Bundle;
//import android.telephony.SmsMessage;
//
//import androidx.annotation.Nullable;
//
//public class SmsReceiver extends BroadcastReceiver {
//    public static final String SMS_BUNDLE = "pdus";
//    public static final String OTP_REGEX = "[0-9]{1,6}";
//    private static final String FORMAT = "format";
//
//    private OnOTPSMSReceivedListener otpSMSListener;
//
//    public void SmsReceiver(OnOTPSMSReceivedListener listener)
//    {
//        otpSMSListener = listener;
//    }
//
//    @Override
//    public void onReceive(Context context, Intent intent)
//    {
//        Bundle intentExtras = intent.getExtras();
//        if (intentExtras != null)
//        {
//            Object[] sms_bundle = (Object[]) intentExtras.get(SMS_BUNDLE);
//            String format = intent.getStringExtra(FORMAT);
//            if (sms_bundle != null)
//            {
//                otpSMSListener.onOTPSMSReceived(format, sms_bundle);
//            }
//            else {
//                // do nothing
//            }
//        }
//    }
//
//    @FunctionalInterface
//    public interface OnOTPSMSReceivedListener
//    {
//        void onOTPSMSReceived(@Nullable String format, Object... smsBundle);
//    }
//
//    @Override
//    public void onOTPSMSReceived(@Nullable String format, Object... smsBundle)
//    {
//        for (Object aSmsBundle : smsBundle)
//        {
//            SmsMessage smsMessage = getIncomingMessage(format, aSmsBundle);
//            String sender = smsMessage.getDisplayOriginatingAddress();
//            if (sender.toLowerCase().contains(ONEMG))
//            {
//                getIncomingMessage(smsMessage.getMessageBody());
//            } else
//            {
//                // do nothing
//            }
//        }
//    }
//
//    private SmsMessage getIncomingMessage(@Nullable String format, Object aObject)
//    {
//        SmsMessage currentSMS;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && format != null)
//        {
//            currentSMS = SmsMessage.createFromPdu((byte[]) aObject, format);
//        } else
//        {
//            currentSMS = SmsMessage.createFromPdu((byte[]) aObject);
//        }
//
//        return currentSMS;
//    }
//}