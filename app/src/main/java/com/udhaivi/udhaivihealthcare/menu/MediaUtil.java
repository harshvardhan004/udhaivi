package com.udhaivi.udhaivihealthcare.menu;


import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.core.content.FileProvider;


import java.io.File;
import java.io.IOException;
import java.util.Random;

public class MediaUtil {

    /**
     * Size to aim for when storing images used to calculate a sample size
     * when loading bitmaps
     */
    public static final int TARGET_DIMENSION_PROFILE_IMAGE = 400;
    public static final int TARGET_DIMENSION_SHARED_IMAGE = 800;

    public static final int TARGET_DIMENSION_AVATAR = 50;
    public static final int TARGET_DIMENSION_ATTACHMENT = 200;

    public static final String EXTENSION_IMAGE = ".jpg";
    public static final String EXTENSION_VIDEO = ".mp4";

    public static final String DIRECTORY_VIDEOS = "videos";
    public static final String DIRECTORY_PICTURES = "pictures";
    public static final String DIRECTORY_SOUND = "sound" ;



    public static Bitmap getBitmap(String fileUriString) {
        Bitmap bitmap = null;
        final Uri fileUri = Uri.parse(fileUriString);
        File file = new File(fileUri.getPath());
        if (file != null && file.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = MediaUtil.calculateInSampleSize(options, MediaUtil.TARGET_DIMENSION_AVATAR);
            bitmap = BitmapFactory.decodeFile(file.getPath(), options);
        }
        return bitmap;

    }

    /**
     * Method will allow the user to view an image or video using the
     * inbuilt gallery / media viewer, offering a choice if there is more
     * than one app that can open the media
     *
     * NOTE: Only works for files stored by the OS in the media store,
     *
     * @param context
     * @param mediaUri	The Uri of the media to open
     */
    public static void view(Context context, Uri mediaUri) {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);

        ContentResolver cr = context.getContentResolver();

        if (cr.getType(mediaUri).contains("video")) {

            intent.setDataAndType(mediaUri, "video/*");

        } else {

            intent.setDataAndType(mediaUri, "image/*");
        }

        context.startActivity(intent);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int targetDimension) {

        // Raw max dimension
        final int maxDimension;

        //Reverse the width and height if the bitmap is a portrait image
        if (options.outHeight > options.outWidth) {

            maxDimension = options.outHeight;

        } else {

            maxDimension = options.outWidth;

        }

        int inSampleSize = 1;

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while (maxDimension / (inSampleSize << 1) > targetDimension) {
            inSampleSize <<= 1;
        }

        return inSampleSize;

    }

    /**
     * Calculate the inSampleSize for bitmap loading options which meets
     * both dimensions requirements
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * Load a Bitmap from a Resource scaling the image using the
     * bitmap options based on the required width and height requirements
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = MediaUtil.calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * Load a Bitmap from a a full path scaling the image using the
     * bitmap options based on the required width and height requirements
     */
    public static Bitmap decodeSampledBitmapFromPath(String pathName,
                                                     int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);

        // Calculate inSampleSize
        options.inSampleSize = MediaUtil.calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }


    public static String getMimeTypeFromUrl(String url) {

        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        String mimeType = null;

        if (extension != null) {

            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }

        return mimeType;

    }

    /**
     * Attempts to determine the orientation of an image using EXIF information
     * if available. Some devices store the image automatically rotated, others
     * store it as taken and use the EXIF orientation to display it correctly
     *
     * @param path	The path of the file to check
     * @return	The rotation of the image, or 0 if not rotated
     * @throws IOException
     */
    public static int getImageRotationFromFile(String path) throws IOException {

        int rotation = 0;

        ExifInterface exif = new ExifInterface(path);
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

        // Determine Rotation
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotation = 90;
        else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotation = 180;
        else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotation = 270;

        return rotation;

    }

    /**
     * Attempts to determine the orientation of an image picked from the gallery.
     * Some devices store the image automatically rotated, others
     * store it as taken and the orientation is stored with the image so that it
     * can be displayed properly
     *
     * @param context
     * @param selectedImageUri	The Uri of an image from the gallery to check
     * @return	The rotation of the image, or 0 if not rotated
     */
    public static int getImageRotationFromMediaStore(Context context, Uri selectedImageUri) {

        int rotation = 0;

        Cursor cursor = context.getContentResolver().query(selectedImageUri,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION },
                null,
                null,
                null);

        if (cursor != null) {

            if (cursor.getCount() == 1 && cursor.getColumnCount() > 0) {
                cursor.moveToFirst();
                rotation = cursor.getInt(0);
            }
            cursor.close();
        }

        return rotation;

    }

    public static void addFileToMediaStore(Context context, String filePath) {


        MediaScannerConnection.scanFile(context,
                new String[] { filePath },
                null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri) {
                       Log.d("TAG", "File added to MediaStore: path=" + path + ", uri=" + uri);
                    }
                });


		/*
		Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		intent.setData(file);
		context.sendBroadcast(intent);
		*/

    }

    public static String createRandomAvatarFilename(Context context) {
        return String.format("%s%s.jpg",
                new Random().nextLong()
        );
    }

}
