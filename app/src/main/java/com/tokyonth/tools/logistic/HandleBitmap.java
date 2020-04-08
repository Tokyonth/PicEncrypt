package com.tokyonth.tools.logistic;

import android.graphics.Bitmap;

public class HandleBitmap {

    public static Bitmap Encrypt(double x,Bitmap bmp) {
        //获取算法对象
        Algorithms ma = new Algorithms();
        ArrayFunctions af = new ArrayFunctions();
        //获取图像像素矩阵的行数与列数
        //Bitmap bmp = ((BitmapDrawable)show_img.getDrawable()).getBitmap();
        int M = bmp.getHeight(),N = bmp.getWidth();
        //获取图像像素矩阵
        int[] pixel = new int[M * N];
        bmp.getPixels(pixel, 0, N, 0, 0, N, M);
        //像素矩阵转二维
        int [][]pixels = new int[M][N];
        af.change(pixel, pixels, M, N);
        //进行加密
        ma.encrypt(pixels, x, M, N);
        //加密后矩阵降一维
        af.recovery(pixels, pixel, M, N);
        //生成加密后的图像
        return Bitmap.createBitmap(pixel, 0, N, N, M, Bitmap.Config.ARGB_8888);
        //Handle_PIC_TAG = true;
    }

    public static Bitmap Decrypt(double x,Bitmap bmp) {
        //获取算法对象
        Algorithms ma = new Algorithms();
        ArrayFunctions af = new ArrayFunctions();
        //获取图像像素矩阵的行数与列数
       // Bitmap bmp = ((BitmapDrawable)show_img.getDrawable()).getBitmap();
        int M = bmp.getHeight(),N = bmp.getWidth();
        //获取图像像素矩阵
        int[] pixel = new int[M * N];
        bmp.getPixels(pixel, 0, N, 0, 0, N, M);
        //像素矩阵转二维
        int [][]pixels = new int[M][N];
        af.change(pixel, pixels, M, N);
        //进行加密
        ma.decrypt(pixels, x, M, N);
        //加密后矩阵降一维
        af.recovery(pixels, pixel, M, N);
        //生成加密后的图像
        return Bitmap.createBitmap(pixel, 0, N, N, M, Bitmap.Config.ARGB_8888);
       // Handle_PIC_TAG = false;
    }

}
