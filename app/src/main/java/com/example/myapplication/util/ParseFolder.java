package com.example.myapplication.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.io.File;

/**
 * Created by Administrator on 8/15/2017.
 */

public class ParseFolder {
    //for MedialPlayer;
    public static int seekBeforeRotation = 0;
    public static int currentSongIndex = 0;


    public Cursor getMediaFolderList(Activity context) {
        System.gc();
        String[] projection = new String[]{MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.PARENT,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DISPLAY_NAME
        };

        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO +
                " OR " + MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
//  ") GROUP BY (" + MediaStore.Files.FileColumns.PARENT;


        String sortOrder = MediaStore.Files.FileColumns.DISPLAY_NAME + " ASC";
        Cursor mediaCursor = null;
        mediaCursor = context.getContentResolver().query(MediaStore.Files.getContentUri("external"), projection, selection, null, sortOrder);

        return mediaCursor;
    }


    public Cursor getMediaDetailList(Activity mActivity, String folderPath) {
        System.gc();
        String selection = null;
        String[] selectionArgs = null;
        if (folderPath != null) {
            selection = MediaStore.Files.FileColumns.MEDIA_TYPE + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO +
                    " OR " + MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
            selection = selection + " and _data like?";
            selectionArgs = new String[]{folderPath + "%"};
        }

        String sortOrder = MediaStore.Files.FileColumns.DISPLAY_NAME + " ASC";
        String[] proj = new String[]{"_id", "_data", "_display_name", "_size", "duration"};

        Cursor musiccursor = null;

        musiccursor = mActivity.getContentResolver().query(MediaStore.Files.getContentUri("external"), proj, selection, selectionArgs, sortOrder);
        MatrixCursor newCursor = new MatrixCursor(proj);
        if (musiccursor.moveToFirst()) {
            do {
                if (musiccursor.getString(musiccursor.getColumnIndex("_data")).equals(folderPath + "/" + musiccursor.getString(musiccursor.getColumnIndex("_display_name")))) {
                    newCursor.addRow(new Object[]{musiccursor.getString(musiccursor.getColumnIndex("_id")),
                            musiccursor.getString(musiccursor.getColumnIndex("_data")),
                            musiccursor.getString(musiccursor.getColumnIndex("_display_name")),
                            musiccursor.getString(musiccursor.getColumnIndex("_size")),
                            musiccursor.getString(musiccursor.getColumnIndex("duration"))});
                }
            } while (musiccursor.moveToNext());
        }
        return newCursor;
    }

    public void deleteFolder(Activity mActivity, String folderPath) {
        System.gc();
        String selection = null;
        String[] selectionArgs = null;
        if (folderPath != null) {
            selection = MediaStore.Files.FileColumns.MEDIA_TYPE + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO +
                    " OR " + MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
            selection = selection + " and _data like?";
            selectionArgs = new String[]{folderPath + "%"};
        }

        String sortOrder = MediaStore.Files.FileColumns.DISPLAY_NAME + " ASC";
        String[] proj = new String[]{"_id", "_data", "_display_name", "_size", "duration"};

        Cursor musiccursor = null;
        musiccursor = mActivity.managedQuery(MediaStore.Files.getContentUri("external"), proj, selection, selectionArgs, sortOrder);
        MatrixCursor newCursor = new MatrixCursor(proj);
        ContentResolver contentResolver = mActivity.getContentResolver();
        if (musiccursor.moveToFirst()) {
            do {
                if (musiccursor.getString(musiccursor.getColumnIndex("_data")).equals(folderPath + "/" + musiccursor.getString(musiccursor.getColumnIndex("_display_name")))) {
                    Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, musiccursor.getLong(musiccursor.getColumnIndex("_id")));
                    contentResolver.delete(deleteUri, null, null);
                    mActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(folderPath))));


                }
            } while (musiccursor.moveToNext());
        }

        //Delete the folder
        deleteRecursive(new File(folderPath));
    }

    public void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }

        fileOrDirectory.delete();
    }

    public String removeExtension(String s) {
        String separator = System.getProperty("file.separator");
        String filename;
        // Remove the path upto the filename.
        int lastSeparatorIndex = s.lastIndexOf(separator);
        if (lastSeparatorIndex == -1) {
            filename = s;
        } else {
            filename = s.substring(lastSeparatorIndex + 1);
        }
        // Remove the extension.
        int extensionIndex = filename.lastIndexOf(".");
        if (extensionIndex == -1)
            return filename;
        return filename.substring(0, extensionIndex);
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static boolean deleteViaContentProvider(Context context, String fullname) {
        Uri uri = getFileUri(context, fullname);

        if (uri == null) {
            return false;
        }

        try {
            ContentResolver resolver = context.getContentResolver();

            // change type to image, otherwise nothing will be deleted
            ContentValues contentValues = new ContentValues();
            int media_type = 1;
            contentValues.put("media_type", media_type);
            resolver.update(uri, contentValues, null, null);

            return resolver.delete(uri, null, null) > 0;
        } catch (Throwable e) {
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static Uri getFileUri(Context context, String fullname) {
        // Note: check outside this class whether the OS version is >= 11
        Uri uri = null;
        Cursor cursor = null;
        ContentResolver contentResolver = null;

        try {
            contentResolver = context.getContentResolver();
            if (contentResolver == null)
                return null;

            uri = MediaStore.Files.getContentUri("external");
            String[] projection = new String[2];
            projection[0] = "_id";
            projection[1] = "_data";
            String selection = "_data = ? ";    // this avoids SQL injection
            String[] selectionParams = new String[1];
            selectionParams[0] = fullname;
            String sortOrder = "_id";
            cursor = contentResolver.query(uri, projection, selection, selectionParams, sortOrder);

            if (cursor != null) {
                try {
                    if (cursor.getCount() > 0) // file present!
                    {
                        cursor.moveToFirst();
                        int dataColumn = cursor.getColumnIndex("_data");
                        String s = cursor.getString(dataColumn);
                        if (!s.equals(fullname))
                            return null;
                        int idColumn = cursor.getColumnIndex("_id");
                        long id = cursor.getLong(idColumn);
                        uri = MediaStore.Files.getContentUri("external", id);
                    } else // file isn't in the media database!
                    {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("_data", fullname);
                        uri = MediaStore.Files.getContentUri("external");
                        uri = contentResolver.insert(uri, contentValues);
                    }
                } catch (Throwable e) {
                    uri = null;
                } finally {
                    cursor.close();
                }
            }
        } catch (Throwable e) {
            uri = null;
        }
        return uri;
    }
}