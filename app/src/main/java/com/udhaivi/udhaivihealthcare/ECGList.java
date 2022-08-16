package com.udhaivi.udhaivihealthcare;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.udhaivi.udhaivihealthcare.activity.EcgActivity;
import com.udhaivi.udhaivihealthcare.activity.EcgDataActivity;
import com.udhaivi.udhaivihealthcare.app.LoginSplash;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ECGList extends AppCompatActivity {

    BottomSheetDialog bottomSheetDialog;
    TextView textFile;
    String address;
    private static final int PICKFILE_RESULT_CODE = 1;
    Uri path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecglist);

//        address = getIntent().getExtras().getString("address");
        SharedPreferences editor = getSharedPreferences("User_Details", MODE_PRIVATE);
        String name = editor.getString("firstname", "");
        String phone = editor.getString("firstname", "");

        if( name == "" && phone == ""){
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms

                    Intent i = new Intent(ECGList.this, LoginSplash.class);
                    startActivity(i);
                    finish();
                }
            }, 3000);
        }

        findViewById(R.id.ecg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go = new Intent(ECGList.this, EcgActivity.class);
                go.putExtra("address", address);
                startActivity(go);
            }
        });

//        findViewById(R.id.ecghistory).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent his = new Intent(ECGList.this, EcgDataActivity.class);
//                his.putExtra("address", address);
//                startActivity(his);
//            }
//        });

        findViewById(R.id.ecgup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomsheetdialog();
            }
        });

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void bottomsheetdialog() {

        bottomSheetDialog = new BottomSheetDialog(ECGList.this, R.style.TransparentDialog);
        View parentView = getLayoutInflater().inflate(R.layout.add_new_prescrip, null);
        bottomSheetDialog.setContentView(parentView);

        Button buttonPick = parentView.findViewById(R.id.buttonpick);
        textFile = parentView.findViewById(R.id.textfile);

        buttonPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PICKFILE_RESULT_CODE);

            }
        });

        parentView.findViewById(R.id.uploadecg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                (new Upload(ECGList.this, path)).execute();
            }
        });

        bottomSheetDialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                if (requestCode == 1) {
                    path = data.getData();
                }
        }
    }

    class Upload extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pd;
        private Context c;
        private Uri path;

        public Upload(Context c, Uri path) {
            this.c = c;
            this.path = path;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(c, "Uploading", "Please Wait");
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pd.dismiss();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String url_path = "http://udhaivihealthcare.com/php/pdf.php/";
            HttpURLConnection conn = null;

            int maxBufferSize = 1024;
            try {
                URL url = new URL(url_path);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setChunkedStreamingMode(1024);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "multipart/form-data");

                OutputStream outputStream = conn.getOutputStream();
                InputStream inputStream = c.getContentResolver().openInputStream(path);

                int bytesAvailable = inputStream.available();
                int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                byte[] buffer = new byte[bufferSize];

                int bytesRead;
                while ((bytesRead = inputStream.read(buffer, 0, bufferSize)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
                inputStream.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    Log.i("result", line);
                }
                reader.close();
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return null;
        }
    }

}