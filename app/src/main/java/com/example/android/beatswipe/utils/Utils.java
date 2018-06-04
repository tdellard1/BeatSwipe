package com.example.android.beatswipe.utils;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import com.example.android.beatswipe.data.local.Beat;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.ContentValues.TAG;

public class Utils {

    private static String displayName;

    public static final DatabaseReference  testRef = FirebaseDatabase.getInstance().getReference().child("test");

    public static String getDisplayName(Activity activity, Uri uri) {
        Cursor cursor = activity.getContentResolver()
                .query(uri, null, null, null, null, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                Log.i(TAG, "Display Name: " + displayName);
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                String size = null;
                if (!cursor.isNull(sizeIndex)) {
                    size = cursor.getString(sizeIndex);
                } else {
                    size = "Unknown";
                }
                Log.i(TAG, "Size: " + size);
            }
        } finally {
            cursor.close();
        }
        return displayName;
    }

    public static Beat createBeat(String name, String url, String genre) {
        Beat beat = new Beat();
        beat.setName(name);
        beat.setUrl(url);
        beat.setGenre(genre);
        return beat;
    }
}
