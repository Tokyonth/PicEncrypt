package com.tokyonth.tools.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.huantansheng.easyphotos.EasyPhotos;
import com.tokyonth.tools.R;
import com.tokyonth.tools.engine.GlideEngine;
import com.tokyonth.tools.share.Share;
import com.tokyonth.tools.utils.CopyAssetsFile;
import com.tokyonth.tools.utils.PlatformUtil;
import com.tokyonth.tools.utils.SPUtils;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class FileTransmission extends Fragment implements View.OnClickListener {

    private Button btn_install;
    private Button btn_boot;
    private ImageButton ib_share_qq;
    private ImageButton ib_share_wechat;
    private ImageButton ib_share_bluetooth;
    private TextView tv_app_check;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_file_transmission,container,false);
        btn_boot = (Button) root.findViewById(R.id.btn_boot);
        btn_install = (Button) root.findViewById(R.id.btn_install);
        ib_share_qq = (ImageButton) root.findViewById(R.id.share_qq);
        ib_share_wechat = (ImageButton) root.findViewById(R.id.share_wechat);
        tv_app_check = (TextView) root.findViewById(R.id.tv_app_check);
        ib_share_bluetooth = (ImageButton) root.findViewById(R.id.share_bluetooth);

        btn_boot.setOnClickListener(this);
        btn_install.setOnClickListener(this);
        ib_share_qq.setOnClickListener(this);
        ib_share_wechat.setOnClickListener(this);
        ib_share_bluetooth.setOnClickListener(this);

        if ((Boolean) SPUtils.getData("APP_CHECK",true)) {
            tv_app_check.setText("已安装！无需再次安装");
            btn_install.setEnabled(false);
        } else {
            btn_boot.setEnabled(false);
            btn_boot.setText("未安装");
        }
        return root;
    }

    public static void openApk(Uri uri, Context context) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= 24) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            //相机或相册回调
            //返回图片地址集合：如果你只需要获取图片的地址，可以用这个
            ArrayList<String> resultPaths = data.getStringArrayListExtra(EasyPhotos.RESULT_PATHS);
            if (requestCode == Share.QQ) {
                Share.ShareFile(getContext(),resultPaths,Share.QQ);
            } /*else if (requestCode == Share.WeChat) {
                Share.ShareFile(getContext(),resultPaths,Share.WeChat);
            } else if (requestCode == Share.BLUETOOTH) {
                Share.ShareFile(getContext(),resultPaths,Share.BLUETOOTH);
            }*/
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_install:
                openApk(CopyAssetsFile.CopyFile(getContext(), "app-fossReliant-debug.apk", "/sdcard/Tools/"),getContext());
                break;
            case R.id.btn_boot:
                if (PlatformUtil.isInstallApp(getContext(),PlatformUtil.PACKAGE_TSHOT)) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    ComponentName componentName = new ComponentName("com.genonbeta.TrebleShot.debug", "com.genonbeta.TrebleShot.activity.HomeActivity");
                    intent.setComponent(componentName);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(),"未安装！",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.share_qq:
                /*EasyPhotos.createAlbum(this, true, GlideEngine.getInstance())
                        .setFileProviderAuthority("com.tokyonth.tools.fileprovider")
                        .setCount(8)
                        .start(Share.QQ);*/
                Share.ShareFile(getContext(),Share.QQ);
                break;
            case R.id.share_wechat:
                /*EasyPhotos.createAlbum(this, true, GlideEngine.getInstance())
                        .setFileProviderAuthority("com.tokyonth.tools.fileprovider")
                        .setCount(8)
                        .start(Share.WeChat);*/
                Share.ShareFile(getContext(),Share.WeChat);
                break;
            case R.id.share_bluetooth:
                EasyPhotos.createAlbum(this, true, GlideEngine.getInstance())
                        .setFileProviderAuthority("com.tokyonth.tools.fileprovider")
                        .setCount(8)
                        .start(Share.BLUETOOTH);
                break;
        }
    }

}
