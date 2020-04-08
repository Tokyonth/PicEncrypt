package com.tokyonth.tools.share;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.tokyonth.tools.utils.PlatformUtil;

import java.io.File;
import java.util.ArrayList;

public class Share {

    public static int QQ = 1;
    public static int WeChat = 2;
    public static int BLUETOOTH = 3;

    public static void ShareFile(Context context,int mode) {
        ComponentName comp = null;
        Intent intent = new Intent();
        if (mode == QQ) {
            if (PlatformUtil.isInstallApp(context,PlatformUtil.PACKAGE_MOBILE_QQ)) {
                comp = new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.SplashActivity");
                Toast.makeText(context, "因为QQ限制,请手动发送高清图片!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "未安装QQ!", Toast.LENGTH_LONG).show();
            }
        } else if (mode == WeChat) {
            if (PlatformUtil.isInstallApp(context,PlatformUtil.PACKAGE_WECHAT)) {
                comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
                Toast.makeText(context, "因为微信限制,请手动发送高清图片!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "未安装微信!", Toast.LENGTH_LONG).show();
            }
        }
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(comp);
        context.startActivity(intent);
    }

    public static void ShareFile(Context context,ArrayList<String> list, int mode) {
        ComponentName comp = null;
        ArrayList<Uri> imageUris = new ArrayList<>();
        Intent intent = new Intent();
        for (String str : list) {
            File file = new File(str);
            Uri uri = Uri.fromFile(file);
            imageUris.add(uri);
        }
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("*/*");
        if (mode == WeChat || mode == QQ) {
            if (mode == WeChat && PlatformUtil.isInstallApp(context, PlatformUtil.PACKAGE_WECHAT)) {
                comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
            } else if (mode == QQ && PlatformUtil.isInstallApp(context, PlatformUtil.PACKAGE_MOBILE_QQ)) {
                comp = new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity");
                //comp = new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.SplashActivity");
            } else {
                Toast.makeText(context, "未安装该软件！", Toast.LENGTH_SHORT).show();
            }
            intent.setComponent(comp);
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
            context.startActivity(Intent.createChooser(intent, "Share"));
            Log.d("FileOperation", "调用成功!");
        } else if (mode == BLUETOOTH) {
            Toast.makeText(context,"连接蓝牙后发送",Toast.LENGTH_SHORT).show();
            intent.setPackage("com.android.bluetooth");
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Intent chooser = Intent.createChooser(intent, "Share");
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(chooser);
        }
    }
}
