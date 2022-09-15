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
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.material.textfield.TextInputLayout;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PdfActivity extends AppCompatActivity {

    Uri uri;
    String FileName, File_path = null;
    String extension = null;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 1000;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 2000;
    private static String imageStoragePath;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private String selectedFilePath = "", selectedPDFFilePath = "";
    public String ImageURL = "";
    public String upload_extension = "", upload_encodedFile = "", upload_file_name;
    private static final String Locale_Preference = "Locale Preference";
    private static final String Locale_KeyValue = "Saved Locale";
    String uploaded_image_name = "", uploaded_pdf_name = "";
    int check_filetype = 0;
    ProgressBar mProgressBar;
    ProgressDialog myDialog;
    int extension1 = 0;
    TextView path, pdfpath;
    ImageView logo;
    TextInputEditText tct, description, attempt;
    String adminid, phone;
    ArrayList<String> pack_id = new ArrayList<>();
    ArrayList<String> pack_type = new ArrayList<>();
    ArrayList<String> payment_amount = new ArrayList<>();
    Spinner data_spinner, type_spinner;
    ArrayAdapter adapter1, type_adapter;
    String selected_pack, selected_pack_id, pack_price, consultant_type;
    private final static int PDF_RESULT= 400;
    LinearLayout lean1;
    TextInputLayout medicaltype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

            pdfpath = findViewById(R.id.pdfpath);
            logo = findViewById(R.id.logo);

            tct = findViewById(R.id.nameid);
            description = findViewById(R.id.description);
            medicaltype = findViewById(R.id.medicaltype);

        String type_consultant[] = {"Select", "Report", "Consultancy"};

        ArrayAdapter<String> adapterxx =
                new ArrayAdapter<>(
                        this,
                        R.layout.dropdown_menu_popup_item,
                        type_consultant);

        AutoCompleteTextView editTextFilledExposedDropdown =
                findViewById(R.id.filled_exposed_dropdown);
        editTextFilledExposedDropdown.setAdapter(adapterxx);

        editTextFilledExposedDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                consultant_type = type_consultant[i];
                Log.d("jgiiug", type_consultant[i]);
                if(consultant_type.equals("Consultancy")){
                    medicaltype.setVisibility(View.VISIBLE);
                }
                else{
                    medicaltype.setVisibility(View.GONE);
                }            }
        });

        adapter1 = new ArrayAdapter<>(
                this,
                R.layout.dropdown_menu_popup_item,
                pack_type);

        AutoCompleteTextView data_type =
                findViewById(R.id.data_spinner);
        data_type.setAdapter(adapter1);

        data_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("Adsds", pack_type.get(i));
                Log.d("dlfmdfkjsn", payment_amount.get(i));

                selected_pack = pack_type.get(i);
                pack_price = payment_amount.get(i);
                selected_pack_id = pack_id.get(i);
            }
        });


            ImageView b1 = findViewById(R.id.filechoose);
            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pdfup();
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

            public void cameraper () {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    take();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 401);
                }
            } else {
                take();
                // if version is below m then write code here,
            }
        }

            void hideKeyboard () {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            View focusedView = this.getCurrentFocus();
            if (focusedView != null) {
                inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }

            @Override
            public void onRequestPermissionsResult ( int requestCode, String[] permissions,
            int[] grantResults){
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


            public void take () {
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
                                            mImageCaptureUri = FileProvider.getUriForFile(PdfActivity.this,
                                                    PdfActivity.this.getApplicationContext().getPackageName() + ".provider",
                                                    CroperinoFileUtil.newCameraFile());
                                        } else {
                                            mImageCaptureUri = Uri.fromFile(CroperinoFileUtil.newCameraFile());
                                        }
                                    } else {
                                        mImageCaptureUri = FileProvider.getUriForFile(PdfActivity.this,
                                                PdfActivity.this.getApplicationContext().getPackageName() + ".provider",
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
                                    Toast.makeText(PdfActivity.this, "Activity not found", Toast.LENGTH_SHORT).show();
                                } else if (e instanceof IOException) {
                                    Toast.makeText(PdfActivity.this, "Image file captured not found", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(PdfActivity.this, "Camera access failed", Toast.LENGTH_SHORT).show();
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

            public void Upload () {

            Dtct = tct.getText().toString();
            Ddesc = description.getText().toString();


            if (selectedPDFFilePath.equals("")) {
                Toast.makeText(PdfActivity.this, "Please Select PDF", Toast.LENGTH_SHORT).show();
                return;
            }

            if (Dtct.equals("")) {
                Toast.makeText(PdfActivity.this, "Please Enter Tutorial Title", Toast.LENGTH_SHORT).show();
                tct.requestFocus();
                return;
            }

            if (Ddesc.equals("")) {
                Toast.makeText(PdfActivity.this, "Enter Description", Toast.LENGTH_SHORT).show();
                description.requestFocus();
                return;
            }

            hideKeyboard();
            alertupload();

        }

            public void alertupload () {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm")
                    .setMessage("Are You Sure, You Want To Add This?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {

                            dialog.dismiss();
                            upload_pdf();
                            // Toast.makeText(getContext(), "Uploaded!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        }

    public void upload_pdf() {

        if (!selectedPDFFilePath.equals("")) {
            New_uploadpdf(selectedPDFFilePath);
        } else {
//        Toast.makeText(getContext(), "Not Uploaded!", Toast.LENGTH_LONG).show();
        }
    }

    private void New_uploadpdf(final String pdfPath) {
        class Uploadpdf extends AsyncTask<Void, Void, String> {

            ProgressDialog uploading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                uploading = ProgressDialog.show(PdfActivity.this, "Uploading File", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                uploading.dismiss();
                Log.d("Uploaded at ",s);

                Log.d("Response", s);
                try {
                    JSONObject jObj = new JSONObject(s);
                    uploaded_pdf_name = jObj.getString("fullpath");
                    final String status = jObj.getString("status");


                    if(status.equals("success")) {

                        myDialog = new ProgressDialog(PdfActivity.this);
                        myDialog.setMessage("Loading...");
                        myDialog.setCancelable(false);


                        register_server("", "");

                        Log.d("file status88-----", status);
                        //    Toast.makeText(getContext(), "Uploaded!", Toast.LENGTH_LONG).show();

                    }else {
                        Log.d("file status88-----", status);
                        Toast.makeText(PdfActivity.this, "Uploading fail! please try again", Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    // JSON error

                    e.printStackTrace();
                    //   message_all("Internet connection error! Please try again", false);
                    //   msg.setText("Internet connection error! Please try again");
                    //   msg.setVisibility(View.VISIBLE);
                    //   msg.setTextColor(Color.parseColor("#FF0000"));
//                    pb.setVisibility(View.GONE);
                    //mProgressDialog.dismiss();
                    Log.d("file status288-----", "Json error: " + e.getMessage());
                    Toast.makeText(PdfActivity.this, "Loading error! please try again", Toast.LENGTH_LONG).show();
                }

                //  textViewResponse.setText(Html.fromHtml("<b>Uploaded at <a href='" + s + "'>" + s + "</a></b>"));
                //  textViewResponse.setMovementMethod(LinkMovementMethod.getInstance());
            }

            @Override
            protected String doInBackground(Void... params) {
                Upload_File u = new Upload_File();
                String msg = u.uploadVideo(pdfPath);
                return msg;
            }
        }
        Uploadpdf uv = new Uploadpdf();
        uv.execute();
        //  uv.execute();
    }

    public void register_server ( final String method, final String query){
            try {
                RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
                String url = "http://udhaivihealthcare.com/php/file.php";//Helpers.getappUrl(this); // <----enter your post url here
                StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response", response);

                        if (response.equals("200")) {
                            if (consultant_type.equals("Select") || consultant_type.equals("Report")) {
                                Log.d("Dasdd", "AFwfa");
                                finish();
                            } else {
                                Log.d("skjefhef", "sdbjfbs");

                                Intent i = new Intent(PdfActivity.this, Payment.class);
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
                        Toast.makeText(PdfActivity.this, "Data not loaded, please try after sometime!", Toast.LENGTH_LONG).show();
                    }
                }) {
                    protected Map<String, String> getParams() {
                        Map<String, String> MyData = new HashMap<String, String>();

                        MyData.put("username", adminid);
                        MyData.put("image", uploaded_pdf_name);
                        MyData.put("pdf_title", Dtct);
                        MyData.put("descrip", Ddesc);
                        MyData.put("type", "pdf");
                        MyData.put("report_type", consultant_type);

                        Log.d("MyData ", String.valueOf(MyData));

                        return MyData;
                    }

                };

                MyRequestQueue.add(MyStringRequest);
            } catch (Exception ex) {
                ex.printStackTrace();
                Toast.makeText(PdfActivity.this, "Data not loaded, please try after sometime!", Toast.LENGTH_LONG).show();
                if (myDialog.isShowing()) {
                    myDialog.dismiss();
                }
            }
        }


            @Override
            public void onActivityResult ( int requestCode, int resultCode, Intent data){
            super.onActivityResult(requestCode, resultCode, data);

                if (requestCode == PDF_RESULT){
                    selectedPDFFilePath="";
                    uri = data.getData();
                    String filePath="";

                    // filePath = getImageFilePath(data);

                    filePath = getFilePathForN(uri,this);
                    Log.d("File path3: ", filePath);

                    if (filePath != null) {
                        uri = Uri.fromFile(new File(filePath));
                        Bitmap selectedImage = BitmapFactory.decodeFile(filePath);
                        //   logo.setImageBitmap(selectedImage);

                        File_path = filePath;
                        selectedPDFFilePath= filePath;
                        // Toast.makeText(this, "File is"+filePath, Toast.LENGTH_SHORT).show();
                    }



                    if (File_path == null) {
                        Toast.makeText(this, "File not selected", Toast.LENGTH_SHORT).show();
                        //  fileName.setText(File_path);
                        //  fileName.setTextColor(Color.RED);
                        selectedPDFFilePath="";
                        pdfpath.setText(upload_file_name);
                    } else {

                        upload_file_name= uri.getLastPathSegment();
                        FileName = uri.getLastPathSegment();

                        if (extension1==0) {
                            //Check uri format to avoid null
                            if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                                //If scheme is a content
                                final MimeTypeMap mime = MimeTypeMap.getSingleton();
                                extension = mime.getExtensionFromMimeType(this.getContentResolver().getType(uri));
                            } else {
                                //If scheme is a File
                                //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
                                extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

                            }

                            Log.d("", "File extension: " + extension);
                            upload_extension = extension;
                            //  fileName.setTextColor(Color.BLUE);
                            String scheme = uri.getScheme();

                            if (scheme.equals("file")) {
                                FileName = uri.getLastPathSegment();
                            } else if (scheme.equals("content")) {
                                Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
                                if (cursor != null && cursor.moveToFirst()) {
                                    FileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                }
                            }
                            upload_file_name = FileName;
                        }


                        Log.d("", "File FileName: " + upload_file_name);
/*
                    if (uri != null) {

                        try {
                            readBytes(uri, File_path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    */

                        //    fileName.setText(upload_file_name);
                    }

                    if (File_path == null) {
                        Toast.makeText(this, "File not selected", Toast.LENGTH_SHORT).show();
                        //  tvHeading.setText(FileName + " File not selected");
                        //  tvHeading.setTextColor(Color.RED);
                        //    tvHeading.setText("File not selected");
                        pdfpath.setText(upload_file_name);
                    }else {
                        //   File file=null;
  /*
                    try {
                        file_row = new File(getPath(context, uri));
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }finally {

                    }
*/
                        pdfpath.setText(upload_file_name);
                        if(ImageURL.equals("")) {}

                    }
                }

        }

            public void callCropper (File file, Activity ctx,boolean isScalable, int aspectX,
            int aspectY, int color, int bgColor){

            Intent intent = new Intent(this, CropImage.class);
            intent.putExtra(CropImage.IMAGE_PATH, file.getPath());
            intent.putExtra(CropImage.SCALE, isScalable);
            intent.putExtra(CropImage.ASPECT_X, aspectX);
            intent.putExtra(CropImage.ASPECT_Y, aspectY);
            intent.putExtra("color", color);
            intent.putExtra("bgColor", bgColor);
            startActivityForResult(intent, CroperinoConfig.REQUEST_CROP_PHOTO);

        }

            private void captureImage () {
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

            public void message_popup (String title, String desc){
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(PdfActivity.this)
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


            public void Call_Server () {
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
                        Toast.makeText(PdfActivity.this, "Data not loaded, please try after sometime....", Toast.LENGTH_LONG).show();

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
                Toast.makeText(PdfActivity.this, "Data not loaded, please try after sometime!", Toast.LENGTH_LONG).show();
            }
        }

    public void pdfup(){

        check_filetype=1;
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select PDF");

// add a list
        String[] animals = {"Choose PDF"};
        builder.setItems(animals, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {

                    case 0: // cow
                        startActivityForResult(getPickImageChooserIntent(), PDF_RESULT);
                        break;
                }
            }
        });

// create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public Intent getPickImageChooserIntent() {

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = this.getPackageManager();


        String[] mimeTypes =
                {"image/*", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                        "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                        "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                        "text/plain",
                        "application/pdf"};



        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        //  galleryIntent.setType("image/*");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            galleryIntent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                galleryIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            galleryIntent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }

        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    public static String getFilePathForN(Uri uri, Context context) {
        Uri returnUri = uri;
        Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);
        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
        File file = new File(context.getFilesDir(), name);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = inputStream.available();

            //int bufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            Log.e("File Size", "Size " + file.length());
            inputStream.close();
            outputStream.close();
            Log.e("File Path", "Path " + file.getPath());

        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        return file.getPath();
    }


}