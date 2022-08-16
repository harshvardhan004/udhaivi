package com.udhaivi.udhaivihealthcare.app;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.FileUtils;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;

import com.udhaivi.udhaivihealthcare.R;

import java.net.URL;

public class WebView extends AppCompatActivity {

    android.webkit.WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        web =  findViewById(R.id.web);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setSupportZoom(true);
        web.getSettings().setPluginState(WebSettings.PluginState.ON);
        web.setWebViewClient(new WebViewClient());

        web.loadUrl("https://docs.google.com/gview?embedded=true&url="+ "http://udhaivihealthcare.com/php/ecg_pdf/ms_report.pdf");
    }
}