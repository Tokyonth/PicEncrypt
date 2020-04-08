package com.tokyonth.tools.exif;

import android.media.ExifInterface;

import java.io.IOException;

public class ExifInfo {

    public static Boolean CheckingPic(String path) {
        ExifInterface exifInterface = null;
        String str = null;
        try {
            exifInterface = new ExifInterface(path);
            str = exifInterface.getAttribute(ExifInterface.TAG_ARTIST);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (str != null) {
            if (str.equals("handle")) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public static void SettingsPic(String path,String tag) {
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(path);
            exifInterface.setAttribute(ExifInterface.TAG_ARTIST,tag);
            exifInterface.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
