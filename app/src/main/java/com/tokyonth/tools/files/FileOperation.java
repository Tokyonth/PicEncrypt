package com.tokyonth.tools.files;

import android.util.Log;

import java.io.File;
import java.io.RandomAccessFile;

public class FileOperation {

    public void writeTxtToFile(String str_content, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);
        String strFilePath = filePath+fileName;
        // 每次写入时，都换行写
        String strContent = str_content + "\r\n";
        try {
            java.io.File file = new java.io.File(strFilePath);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write FileOperation:" + e);
        }
    }

    public File makeFilePath(String filePath, String fileName) {
        java.io.File file = null;
        makeRootDirectory(filePath);
        try {
            file = new java.io.File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public static void makeRootDirectory(String filePath) {
        java.io.File file = null;
        try {
            file = new java.io.File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
    }

}
