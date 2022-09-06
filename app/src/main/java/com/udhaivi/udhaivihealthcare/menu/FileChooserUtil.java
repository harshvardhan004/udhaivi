package com.udhaivi.udhaivihealthcare.menu;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class FileChooserUtil {

    private static final String TAG = "FileChooserUtil";

    public static final String[] MIME_TYPE_IMAGE = new String[]{"image/*"};        // old type 1
    public static final String[] MIME_TYPE_ALL = new String[]{"*/*"};              // old type 2
    public static final String[] MIME_TYPE_GIF = new String[]{"image/gif"};        // old type 4
    public static final String[] MIME_TYPE_VIDEO_AUDIO = new String[]{"video/*","audio/*"};    // old type 5
    public static final String[] MIME_TYPE_AUDIO = new String[]{"audio/*"};        // old type 6

    /**
     * Displays a file chooser to allow the user to select an image from the gallery
     * or take a new image with the camera
     *
     * @param context
     * @param title	The help text to display in the chooser
     * @param imageOutputFile	An output file to use if a new file is created via the camera
     *
     * @return	The file chooser intent that can be used with startActivity
     */
    public static Intent getImageChooserIntent(Context context, CharSequence title, Uri imageOutputFile) {

        return getFileChooserIntent(context, title, imageOutputFile, "image/*");
    }

    /**
     * Displays a file chooser to allow the user to select a file of the chosen type.
     * Can accommodate files that already exist, or new files taken via the camera
     *
     * @param context
     * @param title	The help text to display in the chooser
     * @param imageOutputFile	An output file to use if a new file is created via the camera
     * @param type	The type of files you wish to allow the user to select e.g. image/* or video/* or both
     *
     * @return	The file chooser intent that can be used with startActivity
     */
    private static Intent getFileChooserIntent(Context context, CharSequence title, Uri imageOutputFile, String type) {

        //TODO: Investigate selecting a "Recent" image on Kitkat as it throws file not found exceptions on the return Uri
        //e.g. 01-21 11:04:55.910: E/FirstUseFragment(5779): Unable to open profile image: selectedImageUri=content://com.android.providers.media.documents/document/image%3A1462
        //     01-21 11:04:55.910: E/FirstUseFragment(5779): java.io.FileNotFoundException: No such file or directory

        List<Intent> cameraIntents = new ArrayList<>();

        final PackageManager packageManager = context.getPackageManager();

        if (imageOutputFile != null) {

            //Camera.
            final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
            for(ResolveInfo res : listCam) {
                final String packageName = res.activityInfo.packageName;
                final Intent intent = new Intent(captureIntent);
                intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                intent.setPackage(packageName);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageOutputFile);
                cameraIntents.add(intent);
            }
        }

        //Filesystem.
        final Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        //Only allow data to be picked that can be represented as a stream, allow them to be opened
        //with openFileDescriptor
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
        //API Level 11 and above, which will only allow data to be picked that is locally available
        //on the device
        galleryIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        galleryIntent.setType(type);
        //API Level 18 and above, returned as ClipData
        //galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        //Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, title);

        if (cameraIntents.size() > 0) {

            //Add the camera options.
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));
        }

        return chooserIntent;
    }

    /**
     * Gets the Uri of a file selected the file chooser intent
     *
     * See <code>getFileChooserIntent</code>
     *
     * @param data	The data returned from the file chooser
     * @param cameraOutputFileUri	The pre-specified camera output file Uri
     *
     * @return	The Uri of the selected file
     */
    public static Uri getSelectedFileUri(Intent data, Uri cameraOutputFileUri) {

        boolean isCamera;

        isCamera = data == null || MediaStore.ACTION_IMAGE_CAPTURE.equals(data.getAction());
//getAction could return null so equals written this way around to avoid potential null pointer

        if (isCamera) {

            return cameraOutputFileUri;

        } else {

            return data == null ? null : data.getData();
        }
    }

    private static boolean UriExists(Uri check) {
        Log.d(TAG, "Checking if " + check.toString() + " exists");
        final String path = URI.create(check.toString()).getPath();
        File file;
        try {
            file = new File(path).getCanonicalFile();
            if (file.exists()) {
                return true;
            }
        } catch (IOException e) {
            return false;
        }

        return false;
    }


    public final static long MAX_UPLOAD_SIZE =  3670016;				//3.5 MB

    /**
     * Gets the Uri of a file selected from the file chooser intent
     *
     * See <code>getFileChooserIntent</code>
     *
     * @param data	The data returned from the file chooser
     * @param mTmpImageFile	The pre-specified camera output file
     * @param mTmpVideoFile	The pre-specified video output file
     *
     * @return	The Uri of the selected file
     */
    public static Uri getSelectedFileUri(Intent data, Uri mTmpImageFile, Uri mTmpVideoFile) {

        Uri selectedFileUri = null;

        if (data != null) {

            selectedFileUri = data.getData();
        }

        Log.d(TAG, "getSelectedFileUri: " + selectedFileUri);

        if (selectedFileUri == null) {
            Log.d(TAG, "getSelectedFileUri: image " + ((mTmpImageFile == null) ? "unknown":mTmpImageFile.toString()));
            Log.d(TAG, "getSelectedFileUri: video " + ((mTmpVideoFile == null) ? "unknown":mTmpVideoFile.toString()));
            // check if image exists

            if (mTmpImageFile != null) {
                File file = new File(URI.create(mTmpImageFile.toString()).getPath());
                if (file.exists()) {
                    //Do something
                }
            }

            if (mTmpImageFile != null && UriExists(mTmpImageFile)) {

                selectedFileUri = mTmpImageFile;

            } else if (mTmpVideoFile != null && UriExists(mTmpVideoFile)) {

                selectedFileUri = mTmpVideoFile;
            }

        }

        return selectedFileUri;
    }
}
