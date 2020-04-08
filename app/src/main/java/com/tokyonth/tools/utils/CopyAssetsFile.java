package com.tokyonth.tools.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.support.constraint.Constraints.TAG;

public class CopyAssetsFile {

    public static Uri CopyFile(Context context, String fileName, String path) {
        try {
            InputStream mInputStream = context.getAssets().open(fileName);
            File file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }
            File mFile = new File(path + File.separator + "tshot.apk");
            if(!mFile.exists())
                mFile.createNewFile();
            Log.d(TAG,"开始拷贝");
            FileOutputStream mFileOutputStream = new FileOutputStream(mFile);
            byte[] mbyte = new byte[1024];
            int i = 0;
            while((i = mInputStream.read(mbyte)) > 0){
                mFileOutputStream.write(mbyte, 0, i);
            }
            mInputStream.close();
            mFileOutputStream.close();
            Uri uri = null;
            try{
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    uri = FileProvider.getUriForFile(context, "com.tokyonth.tools.fileprovider", mFile);
                }else{
                    uri = Uri.fromFile(mFile);
                }
            }catch (ActivityNotFoundException anfe){
                Log.e(TAG,anfe.getMessage());
            }
            MediaScannerConnection.scanFile(context, new String[]{mFile.getAbsolutePath()}, null, null);
            Log.d(TAG,"拷贝完毕：" + uri);
            return uri;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, fileName + "not exists" + "or write err");
            return null;
        } catch (Exception e) {
            return null;
        }
    }

}
