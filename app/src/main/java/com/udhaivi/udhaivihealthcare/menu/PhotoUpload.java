package com.udhaivi.udhaivihealthcare.menu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.mikelau.croperino.CropImage;
import com.mikelau.croperino.CroperinoConfig;
import com.mikelau.croperino.CroperinoFileUtil;
import com.mikelau.croperino.InternalStorageContentProvider;
import com.udhaivi.udhaivihealthcare.R;
import com.udhaivi.udhaivihealthcare.app.CameraUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PhotoUpload extends AppCompatActivity {

    Uri uri;
    String FileName, File_path=null;
    String extension=null;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 1000;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 2000;
    private static String imageStoragePath;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private String selectedFilePath= "", selectedPDFFilePath="";
    public String ImageURL= "";
    public String upload_extension="", upload_encodedFile="", upload_file_name;
    private static final String Locale_Preference = "Locale Preference";
    private static final String Locale_KeyValue = "Saved Locale";
    String uploaded_image_name="", uploaded_pdf_name="";
    int check_filetype=0;
    ProgressBar mProgressBar;
    ProgressDialog myDialog;
    int extension1=0;
    TextView path, pdfpath;
    ImageView logo;
    TextInputEditText tct,description,attempt;
    String adminid, phone;
    ArrayList<String> pack_id = new ArrayList<>();
    ArrayList<String> pack_type = new ArrayList<>();
    ArrayList<String> payment_amount = new ArrayList<>();
    Spinner data_spinner, type_spinner;
    ArrayAdapter adapter1, type_adapter;
    String selected_pack, selected_pack_id, pack_price, consultant_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_upload);

        path = findViewById(R.id.path);
        logo = findViewById(R.id.logo);

        tct = findViewById(R.id.tct);
        description = findViewById(R.id.description);
        data_spinner = findViewById(R.id.data_spinner);
        type_spinner = findViewById(R.id.type_spinner);

        String type_consultant[] = {"Select", "Report", "Consultancy"};

        type_adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, type_consultant);
        type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_spinner.setAdapter(type_adapter);

        type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int p, long id) {
                consultant_type = "";
                consultant_type = type_consultant[p];
                Log.d("jgiiug", type_consultant[p]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        adapter1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, pack_type);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        data_spinner.setAdapter(adapter1);

        data_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int p, long id) {
                Log.d("Adsds", pack_type.get(p));
                Log.d("dlfmdfkjsn", payment_amount.get(p));

                selected_pack = pack_type.get(p);
                pack_price = payment_amount.get(p);
                selected_pack_id = pack_id.get(p);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        Button b1 = findViewById(R.id.filechoose);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraper();
            }
        });

        AppCompatButton upload = findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Upload();
            }
        });

        SharedPreferences prefs = this.getSharedPreferences("User_Details", MODE_PRIVATE);
        adminid = prefs.getString("user_id", "");//"No name defined" is the default value.
        phone = prefs.getString("phone", "");//"No name defined" is the default value.

        new CroperinoConfig("IMG_" + System.currentTimeMillis() + "", "/udhaivi/Pictures", "/sdcard/udhaivi/Pictures");
        CroperinoFileUtil.setupDirectory(this);

        Call_Server();
    }

    public void cameraper(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                take();
            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 401);
            }
        }
        else
        {
            take();
            // if version is below m then write code here,
        }
    }

    void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        View focusedView = this.getCurrentFocus();
        if (focusedView != null) {
            inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 401) {
            if (grantResults.length == 0 || grantResults == null) {

            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            }
        } else if (requestCode == 402) {
            if (grantResults.length == 0 || grantResults == null) {

            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            }
        }
    }


    public void take(){
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image");

        String[] animals = {"Camera", "Choose from Library"};
        builder.setItems(animals, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // horse
                        try {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            Uri mImageCaptureUri;
                            String state = Environment.getExternalStorageState();
                            if (Environment.MEDIA_MOUNTED.equals(state)) {
                                if (Uri.fromFile(CroperinoFileUtil.newCameraFile()) != null) {
                                    if (Build.VERSION.SDK_INT >= 23) {
                                        mImageCaptureUri = FileProvider.getUriForFile(PhotoUpload.this,
                                                PhotoUpload.this.getApplicationContext().getPackageName() + ".provider",
                                                CroperinoFileUtil.newCameraFile());
                                    } else {
                                        mImageCaptureUri = Uri.fromFile(CroperinoFileUtil.newCameraFile());
                                    }
                                } else {
                                    mImageCaptureUri = FileProvider.getUriForFile(PhotoUpload.this,
                                            PhotoUpload.this.getApplicationContext().getPackageName() + ".provider",
                                            CroperinoFileUtil.newCameraFile());
                                }
                            } else {
                                mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;
                            }
                            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                            intent.putExtra("return-data", true);
                            startActivityForResult(intent, CroperinoConfig.REQUEST_TAKE_PHOTO);
                        } catch (Exception e) {

                            if (e instanceof ActivityNotFoundException) {
                                Toast.makeText(PhotoUpload.this, "Activity not found", Toast.LENGTH_SHORT).show();
                            } else if (e instanceof IOException) {
                                Toast.makeText(PhotoUpload.this, "Image file captured not found", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(PhotoUpload.this, "Camera access failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    case 1: // cow
                        Intent i = new Intent(Intent.ACTION_PICK);
                        i.setType("image/*");
                        startActivityForResult(i, CroperinoConfig.REQUEST_PICK_FILE);
                        break;
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    String Dtct;
    String Ddesc;

    public void Upload() {

        Dtct= tct.getText().toString();
        Ddesc= description.getText().toString();


        if(selectedFilePath.equals("")){
            Toast.makeText(PhotoUpload.this, "Please Select Image", Toast.LENGTH_SHORT).show();
            return;
        }

        if(Dtct.equals("")){
            Toast.makeText(PhotoUpload.this, "Please Enter Tutorial Title", Toast.LENGTH_SHORT).show();
            tct.requestFocus();
            return;
        }

        if(Ddesc.equals("")){
            Toast.makeText(PhotoUpload.this, "Enter Description",Toast.LENGTH_SHORT).show();
            description.requestFocus();
            return;
        }

        hideKeyboard();
        alertupload();

    }

    public void alertupload(){
        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Are You Sure, You Want To Add This?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        dialog.dismiss();
                        upload_image();
                        // Toast.makeText(getContext(), "Uploaded!", Toast.LENGTH_SHORT).show();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void upload_image() {

        if (!selectedFilePath.equals("")) {
            New_uploadVideo(selectedFilePath);
            Log.d("ADdd", selectedFilePath);
        } else {
//        Toast.makeText(getContext(), "Not Uploaded!", Toast.LENGTH_LONG).show();
        }
    }

    private void New_uploadVideo(final String imagePath) {
        class UploadVideo extends AsyncTask<Void, Void, String> {

            ProgressDialog uploading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                uploading = ProgressDialog.show(PhotoUpload.this, "Uploading File", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                uploading.dismiss();
                Log.d("Uploaded at ",s);

                Log.d("Response", s);
                try {
                    JSONObject jObj = new JSONObject(s);
                    uploaded_image_name = jObj.getString("fullpath");
                    final String status = jObj.getString("status");


                    if(status.equals("success")) {

                        myDialog = new ProgressDialog(PhotoUpload.this);
                        myDialog.setMessage("Loading...");
                        myDialog.setCancelable(false);

                        register_server("", "");

                        Log.d("file status88-----", status);

                    }else {
                        Log.d("file status88-----", status);
                        Toast.makeText(PhotoUpload.this, "Uploading fail! please try again", Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    // JSON error

                    e.printStackTrace();

                    Log.d("file status288-----", "Json error: " + e.getMessage());
                    Toast.makeText(PhotoUpload.this, "Loading error! please try again", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            protected String doInBackground(Void... params) {
                Upload_File u = new Upload_File();
                String msg = u.uploadVideo(imagePath);
                return msg;
            }
        }
        UploadVideo uv = new UploadVideo();
        uv.execute();
        //  uv.execute();
    }

    public void register_server(final String method, final String query) {
        try {
            RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
            String url = "http://udhaivihealthcare.com/php/file.php";//Helpers.getappUrl(this); // <----enter your post url here
            StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("response", response);

                    if(response.equals("200")){
//                        finish();
                        if(consultant_type.equals("Select") || consultant_type.equals("Report") ) {
                            Log.d("Dasdd", "AFwfa");
                            finish();
                        }
                        else{
                            Log.d("skjefhef", "sdbjfbs");

                            Intent i = new Intent(PhotoUpload.this, Payment.class);
                            i.putExtra("pack_id", selected_pack_id);
                            i.putExtra("pack_name", selected_pack);
                            i.putExtra("pack_paymemt", pack_price);
                            startActivity(i);
                        }

                    }

                }
            }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(PhotoUpload.this, "Data not loaded, please try after sometime!", Toast.LENGTH_LONG).show();
                }
            }) {
                protected Map<String, String> getParams() {
                    Map<String, String> MyData = new HashMap<String, String>();

                    MyData.put("username",adminid);
                    MyData.put("image", uploaded_image_name);
                    MyData.put("pdf_title", Dtct);
                    MyData.put("descrip", Ddesc);
                    MyData.put("type", "image");

                    Log.d("MyData ", String.valueOf(MyData));

                    return MyData;
                }

            };

            MyRequestQueue.add(MyStringRequest);
        } catch (Exception ex) {
            ex.printStackTrace();   Toast.makeText(PhotoUpload.this, "Data not loaded, please try after sometime!", Toast.LENGTH_LONG).show();
            if(myDialog.isShowing()){
                myDialog.dismiss();}
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CroperinoConfig.REQUEST_TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    callCropper(CroperinoFileUtil.getTempFile(), PhotoUpload.this, true, 1, 1, com.mikelau.croperino.R.color.gray, com.mikelau.croperino.R.color.gray_variant);
                }
                break;
            case CroperinoConfig.REQUEST_PICK_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    CroperinoFileUtil.newGalleryFile(data, PhotoUpload.this);
                    callCropper(CroperinoFileUtil.getTempFile(), PhotoUpload.this, true, 1, 1, com.mikelau.croperino.R.color.gray, com.mikelau.croperino.R.color.gray_variant);
                }
                break;
            case CroperinoConfig.REQUEST_CROP_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    Uri i = Uri.fromFile(CroperinoFileUtil.getTempFile());


                    File_path = i.getPath();

                    selectedFilePath = i.getPath();

                    Log.d("PAAAAAAAAAAAAAATH ",File_path);

                    logo.setImageURI(i);
                }
                break;
            default:
                break;
        }

    }

    public void callCropper(File file, Activity ctx, boolean isScalable, int aspectX, int aspectY, int color, int bgColor){

        Intent intent = new Intent(this, CropImage.class);
        intent.putExtra(CropImage.IMAGE_PATH, file.getPath());
        intent.putExtra(CropImage.SCALE, isScalable);
        intent.putExtra(CropImage.ASPECT_X, aspectX);
        intent.putExtra(CropImage.ASPECT_Y, aspectY);
        intent.putExtra("color", color);
        intent.putExtra("bgColor", bgColor);
        startActivityForResult(intent, CroperinoConfig.REQUEST_CROP_PHOTO);

    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File file = CameraUtils.getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (file != null) {
            imageStoragePath = file.getAbsolutePath();
        }

        Uri fileUri = CameraUtils.getOutputMediaFileUri(this.getApplicationContext(), file);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    public void message_popup(String title, String  desc){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {


                android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(PhotoUpload.this)
//set icon
                        .setIcon(R.mipmap.ic_logo)
//set title
                        .setTitle(title)
//set message
                        .setMessage(desc)
//set positive button
                        .setPositiveButton("Back", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //set what would happen when positive button is clicked
                            }
                        })
//set negative button
                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //set what should happen when negative button is clicked
                                //Toast.makeText(getApplicationContext(),"Nothing Happened",Toast.LENGTH_LONG).show();

//                                PizzasecondFragment.refreshList();
//                                getFragmentManager().popBackStack();
                                finish();
                            }
                        }).show();
            }
        });
    }


    public void Call_Server() {
        try {
            RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
            //  String url = Config.URL_API;//Helpers.getappUrl(this); // <----enter your post url here
            String url = "http://udhaivihealthcare.com/php/get_pkg.php";
            Log.d("URl- ", url);
            StringRequest MyStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //   Log.d("xyz", user_id_txt);
                        try {

                            JSONObject tbldata = new JSONObject(response);
                            Log.d("tbldata", String.valueOf(tbldata.getJSONArray("tbldata")));
                            JSONArray parentArray = new JSONArray();
                            parentArray = tbldata.getJSONArray("tbldata");

                            for (int i = 0; i < parentArray.length(); i++) {

                                pack_id.add((String) parentArray.getJSONObject(i).get("pack_id"));
//                                Log.d("pack_id", String.valueOf(parentArray.getJSONObject(i).get("pack_id")));

                                pack_type.add((String) parentArray.getJSONObject(i).get("pack_type"));
//                                Log.d("pack_type", String.valueOf(parentArray.getJSONObject(i).get("pack_type")));

                                payment_amount.add((String) parentArray.getJSONObject(i).get("payment_amount"));
//                                Log.d("payment_amount", String.valueOf(parentArray.getJSONObject(i).get("payment_amount")));
                            }

                            adapter1.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
            }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(PhotoUpload.this, "Data not loaded, please try after sometime....", Toast.LENGTH_LONG).show();

                }
            }) {
                protected Map<String, String> getParams() {
                    Map<String, String> MyData = new HashMap<String, String>();
                    return MyData;
                }
            };

            MyRequestQueue.add(MyStringRequest);
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(PhotoUpload.this, "Data not loaded, please try after sometime!", Toast.LENGTH_LONG).show();
        }
    }


}