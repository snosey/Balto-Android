package com.example.snosey.balto.Support.image;

import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.v4.app.FragmentActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Snosey on 2/9/2018.
 */

public class GetFileName {
    String fileName;

    public GetFileName(Uri uri, FragmentActivity activity) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.US);
        String currentDateandTime = sdf.format(new Date());

        if (!(result.endsWith("jpg") || result.endsWith("png") || result.endsWith("jpeg"))) {
            result += ".png";
        }

        fileName = currentDateandTime + result;
    }

    public String FileName() {
        return fileName;
    }

}
