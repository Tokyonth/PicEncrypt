package com.tokyonth.tools;

import android.content.DialogInterface;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.tokyonth.tools.fragment.GlobalConfiguration;
import com.tokyonth.tools.fragment.FileTransmission;
import com.tokyonth.tools.fragment.PictureProcessing;
import com.tokyonth.tools.utils.PlatformUtil;
import com.tokyonth.tools.utils.SPUtils;
import com.tokyonth.tools.view.NetImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private BottomNavigationView btn_navigation;
    private FloatingActionButton fab;
    private NetImageView imageView;

    private GlobalConfiguration cameraTaking;
    private FileTransmission fileTransmission;
    private PictureProcessing pictureProcessing;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_main);
        initView();
        BootApp();
        setSupportActionBar(toolbar);
        initPictureProcessing();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    private void BootApp() {
        new SPUtils(this,"app_config");
        if (!(boolean)SPUtils.getData("Custom_Tag",false)) {
            SPUtils.putData("SdPath", "/sdcard/Tools");
        }
        if(PlatformUtil.isInstallApp(this,PlatformUtil.PACKAGE_TSHOT)) {
            SPUtils.putData("APP_CHECK",true);
        } else {
            SPUtils.putData("APP_CHECK",false);
        }
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        btn_navigation = (BottomNavigationView) findViewById(R.id.navigation);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        imageView = (NetImageView) findViewById(R.id.main_pic);
        btn_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        fab.setOnClickListener(this);
    }

    private void StatementDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage(R.string.warning_msg);
            builder.setPositiveButton("确定", null);
            builder.setNegativeButton("不再显示", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SPUtils.putData("Statement_TAG", true);
                }
            });
            builder.create();
            builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate( R.menu.menu_main , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()) {
            case R.id.action_warning:
                    StatementDialog();
                break;
        }
        return true;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_picture_processing:
                    initPictureProcessing();
                    return true;
                case R.id.navigation_file_transmission:
                    initFileTransmission();
                    return true;
                case R.id.navigation_global_configuration:
                    initGlobalConfiguration();
                    return true;
            }
            return false;
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                Toast.makeText(this,"正在加载图片...请稍等",Toast.LENGTH_LONG).show();
                imageView.setImageURL("https://source.unsplash.com/1080x600/?");
                break;
        }
    }

    private void initPictureProcessing(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (pictureProcessing == null){
            pictureProcessing = new PictureProcessing();
            transaction.add(R.id.main_frame_layout, pictureProcessing);
        }
        HideFragment(transaction);
        transaction.show(pictureProcessing);
        transaction.commit();
    }

    private void initFileTransmission(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (fileTransmission == null){
            fileTransmission = new FileTransmission();
            transaction.add(R.id.main_frame_layout,fileTransmission);
        }
        HideFragment(transaction);
        transaction.show(fileTransmission);
        transaction.commit();
    }

    private void initGlobalConfiguration(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (cameraTaking == null){
            cameraTaking = new GlobalConfiguration();
            transaction.add(R.id.main_frame_layout,cameraTaking);
        }
        HideFragment(transaction);
        transaction.show(cameraTaking);
        transaction.commit();
    }

    private void HideFragment(FragmentTransaction transaction){
        if(pictureProcessing != null){
            transaction.hide(pictureProcessing);
        }
        if(fileTransmission != null){
            transaction.hide(fileTransmission);
        }
        if(cameraTaking != null){
            transaction.hide(cameraTaking);
        }
    }

}
