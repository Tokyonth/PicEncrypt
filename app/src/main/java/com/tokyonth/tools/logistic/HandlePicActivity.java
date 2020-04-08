package com.tokyonth.tools.logistic;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.tokyonth.tools.BaseActivity;
import com.tokyonth.tools.common.Md5Util;
import com.tokyonth.tools.engine.GlideEngine;
import com.tokyonth.tools.R;
import com.tokyonth.tools.share.Share;
import com.tokyonth.tools.utils.GetDate;
import com.tokyonth.tools.utils.SPUtils;
import com.tokyonth.tools.media.SingleMediaScanner;
import com.tokyonth.tools.view.CustomDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HandlePicActivity extends BaseActivity implements View.OnClickListener {

    private ImageView show_img;
    private TextView tv_pic_info;
    private RequestManager mGlide;
    private LinearLayout encrypt;
    private LinearLayout decrypt;
    private LinearLayout ll_save;
    private LinearLayout ll_send;
    private LinearLayout ll_container;
    private ProgressDialog progressDialog;

    private static final int SUCCESS = 10;
    private static final int SAVE_SUCCESS = 11;
    private static final int EasyPhotos_CODE = 101;
    private static final int ENCRYPT = 3;
    private static final int DECRYPT = 4;

    private boolean Handle_PIC_TAG = false;
    private boolean SHOW_TAG = false;
    private int RE_Handle_PIC_TAG = -1;

    private String pic_path;
    private String pic_name;
    private Bitmap re_bitmap;

    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_pic);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        initView();
        EasyPhotos.createAlbum(this, true,
         /*图片引擎*/       GlideEngine.getInstance())
                .setFileProviderAuthority("com.tokyonth.tools.fileprovider")
                .start(EasyPhotos_CODE);
    }

    private void initView() {
        mGlide = Glide.with(this);
        show_img = (ImageView) findViewById(R.id.show_photo);
        tv_pic_info = (TextView) findViewById(R.id.tv_pic_info);
        encrypt = (LinearLayout) findViewById(R.id.ll_encryption);
        decrypt = (LinearLayout) findViewById(R.id.ll_decrypt);
        ll_container = (LinearLayout) findViewById(R.id.ll_container);
        ll_save = (LinearLayout) findViewById(R.id.ll_save);
        ll_send = (LinearLayout) findViewById(R.id.ll_send);
        ll_send.setOnClickListener(this);
        ll_save.setOnClickListener(this);
        encrypt.setOnClickListener(this);
        decrypt.setOnClickListener(this);
        show_img.setOnClickListener(this);
    }

    private void SetDialog(final int mode){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View root = View.inflate(HandlePicActivity.this, R.layout.dialog_choosepage, null);
        final EditText edit = (EditText) root.findViewById(R.id.choose_page_edit);
        builder.setView(root);
        builder.setTitle("输入密匙");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                final Bitmap bmp = ((BitmapDrawable)show_img.getDrawable()).getBitmap();
                String keyStr = edit.getText().toString().trim();
                StringBuilder sb = new StringBuilder();
                sb.append("0.");
                sb.append(keyStr);
                if (keyStr.length() == 0) {
                    showToast("请输入密钥");
                } else {
                    final double x = Double.valueOf(sb.toString());
                    Log.d("PicActivity",String.valueOf(x));
                    progressDialog = ProgressDialog.show(HandlePicActivity.this,null, "处理中，请稍候……");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (mode == DECRYPT) {
                                //decrypt(x);
                                re_bitmap = HandleBitmap.Decrypt(x, bmp);
                                RE_Handle_PIC_TAG = 1;
                                Handle_PIC_TAG = false;
                            } else if (mode == ENCRYPT) {
                                //encrypt(x);
                                re_bitmap = HandleBitmap.Encrypt(x, bmp);
                                Handle_PIC_TAG = true;
                                RE_Handle_PIC_TAG = 0;
                            }
                            Message msg = new Message();
                            msg.what = SUCCESS;
                            Bundle bundle = new Bundle();
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                            handler.sendEmptyMessage(0);
                        }
                    }).start();
                }
                dialog.dismiss();
            }
        }).create().show();
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == SUCCESS) {
                show_img.setImageBitmap(re_bitmap);
                progressDialog.dismiss();
            } else if (msg.what == SAVE_SUCCESS) {
                progressDialog.dismiss();
                showToast("保存成功！");
            }
        }
    };

    private void ShowHide() {
        if (!SHOW_TAG) {
            tv_pic_info.setVisibility(View.INVISIBLE);
            ll_container.setVisibility(View.INVISIBLE);
            SHOW_TAG = true;
        } else if (SHOW_TAG) {
            tv_pic_info.setVisibility(View.VISIBLE);
            ll_container.setVisibility(View.VISIBLE);
            SHOW_TAG = false;
        }
    }

    private String SavePic() {
        String save_path;
        Bitmap bm1 = ((BitmapDrawable) show_img.getDrawable()).getBitmap();
        File tmpDir = new File((String) SPUtils.getData("SdPath", ""));
        save_path = tmpDir.getAbsolutePath() + "/" + GetDate.getDate() + "-" + pic_name;
        if (!tmpDir.exists()) {
            tmpDir.mkdir();
        }
        File img = new File(save_path);
        try {
            FileOutputStream fos = new FileOutputStream(img);
            bm1.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            new SingleMediaScanner(HandlePicActivity.this, img);
        } catch (FileNotFoundException e) {
            showToast("保存失败!");
            e.printStackTrace();
        } catch (IOException e) {
            showToast("保存失败!");
            e.printStackTrace();
        }
        return save_path;
    }

    private void SavePicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HandlePicActivity.this);
        builder.setTitle("图像存储");
        builder.setMessage("是否要保存图片?");
        builder.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog = ProgressDialog.show(HandlePicActivity.this,null, "请稍候……");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (Handle_PIC_TAG) {
                                    Response response = null;
                                    String md5 = Md5Util.getMd5(new File(SavePic()));
                                    RequestBody formBody = new FormBody.Builder()
                                            .add("md5", md5)
                                            .build();
                                    final Request request = new Request.Builder()
                                            .url("http://118.24.135.36:8080/PicMd5/Set")
                                            .post(formBody)
                                            .build();
                                    try {
                                        response = client.newCall(request).execute();
                                        if (response.isSuccessful()) {
                                            Log.i("打印POST响应的数据", response.body().string());
                                        } else {
                                            throw new IOException("Unexpected code " + response);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    SavePic();
                                }
                                Message msg = new Message();
                                msg.what = SAVE_SUCCESS;
                                handler.sendMessage(msg);
                                handler.sendEmptyMessage(0);
                            }
                        }).start();
                    }
                });
        builder.setNegativeButton("取消", null);
        builder.create();
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            if (requestCode == EasyPhotos_CODE) {
                ArrayList<Photo> resultPhotos = data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);
                Photo photo = resultPhotos.get(0);
                mGlide.load(photo.path).into(show_img);
                tv_pic_info.setText("[图片名称]： "
                        + photo.name
                        + "\n[宽]："
                        + photo.width
                        + "  [高]："
                        + photo.height
                        + "\n[文件大小,单位bytes]："
                        + photo.size
                        + "\n[图片地址]："
                        + photo.path);
                pic_path = photo.path;
                pic_name = photo.name;
            }
        } else if (RESULT_CANCELED == resultCode) {
            showToast("未选择图像!");
            ll_container.setEnabled(false);
            ll_send.setEnabled(false);
            ll_save.setEnabled(false);
            encrypt.setEnabled(false);
            decrypt.setEnabled(false);
        }
    }

    private void GetHttp(final Handler httphandler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                String md5 = Md5Util.getMd5(new File(pic_path));
                RequestBody formBody = new FormBody.Builder()
                        .add("md5", md5)
                        .build();
                final Request request = new Request.Builder()
                        .url("http://118.24.135.36:8080/PicMd5/Get")
                        .post(formBody)
                        .build();
                try {
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        httphandler.obtainMessage(1, response.body().string()).sendToTarget();
                    } else {
                        throw new IOException("Unexpected code " + response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.show_photo:
                ShowHide();
                break;
            case R.id.ll_encryption:
                progressDialog = ProgressDialog.show(HandlePicActivity.this,null, "请稍候……");
                @SuppressLint("HandlerLeak")
                final Handler enhandler = new Handler() {
                    public void handleMessage(Message msg) {
                        if(msg.what == 1) {
                            String ReturnMessage = (String) msg.obj;
                            JsonObject jsonObject = (JsonObject) new JsonParser().parse(ReturnMessage);
                            Log.d("返回数据",ReturnMessage);

                            boolean tag = jsonObject.get("tag").getAsBoolean();
                            Log.d("TAG数据",String.valueOf(tag));

                            progressDialog.dismiss();
                            if (tag) {
                                if (Handle_PIC_TAG || RE_Handle_PIC_TAG == -1) {
                                    showToast("无需加密!");
                                } else {
                                    SetDialog(ENCRYPT);
                                }
                            } else if (!tag) {
                                if (!Handle_PIC_TAG || RE_Handle_PIC_TAG == -1) {
                                    SetDialog(ENCRYPT);
                                } else {
                                    showToast("无需加密!");
                                }
                            }
                        }
                    }
                };
                GetHttp(enhandler);
                Log.d("RE_Handle_PIC_TAG内容",String.valueOf(RE_Handle_PIC_TAG));
                Log.d("Handle_PIC_TAG内容",String.valueOf(Handle_PIC_TAG));
               // Log.d("CheckFileHandle内容",String.valueOf(CheckFileHandle(pic_path)));
                break;
            case R.id.ll_decrypt:
                progressDialog = ProgressDialog.show(HandlePicActivity.this,null, "请稍候……");
                @SuppressLint("HandlerLeak")
                final Handler dehandler = new Handler() {
                    public void handleMessage(Message msg) {
                        if(msg.what == 1) {
                            String ReturnMessage = (String) msg.obj;
                            JsonObject jsonObject = (JsonObject) new JsonParser().parse(ReturnMessage);
                            Log.d("返回数据",ReturnMessage);

                            boolean tag = jsonObject.get("tag").getAsBoolean();
                            Log.d("TAG数据",String.valueOf(tag));

                            progressDialog.dismiss();
                            if (tag) {
                                if (Handle_PIC_TAG || RE_Handle_PIC_TAG == -1) {
                                    SetDialog(DECRYPT);
                                } else {
                                    showToast("无需解密!");
                                }
                            } else if (!tag) {
                                if (!Handle_PIC_TAG || RE_Handle_PIC_TAG == -1) {
                                    showToast("无需解密!");
                                } else {
                                    SetDialog(DECRYPT);
                                }
                            }
                        }
                    }
                };
               GetHttp(dehandler);
                Log.d("RE_Handle_PIC_TAG内容",String.valueOf(RE_Handle_PIC_TAG));
                Log.d("Handle_PIC_TAG内容",String.valueOf(Handle_PIC_TAG));
               // Log.d("CheckFileHandle内容",String.valueOf(CheckFileHandle(pic_path)));
                break;
            case R.id.ll_save:
                SavePicDialog();
                break;
            case R.id.ll_send:
                CustomDialog dialog = new CustomDialog(HandlePicActivity.this,new CustomDialog.DialogListener() {
                    @Override
                    public void onClick(View view) {
                        showToast("请稍等...");
                        final String path = SavePic();
                        if (Handle_PIC_TAG) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    if (Handle_PIC_TAG) {
                                        Response response = null;
                                        String md5 = Md5Util.getMd5(new File(path));
                                        RequestBody formBody = new FormBody.Builder()
                                                .add("md5", md5)
                                                .build();
                                        final Request request = new Request.Builder()
                                                .url("http://118.24.135.36:8080/PicMd5/Set")
                                                .post(formBody)
                                                .build();
                                        try {
                                            response = client.newCall(request).execute();
                                            if (response.isSuccessful()) {
                                                Log.i("打印POST响应的数据", response.body().string());
                                            } else {
                                                throw new IOException("Unexpected code " + response);
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        SavePic();
                                    }
                                 //   Message msg = new Message();
                                  //  msg.what = SAVE_SUCCESS;
                                  //  handler.sendMessage(msg);
                                  //  handler.sendEmptyMessage(0);
                                }
                            }).start();
                        }
                        showToast("已为你保存当前图像");
                        ArrayList<String> list = new ArrayList<>();
                        list.add(path);
                            switch (view.getId()) {
                                case R.id.share_qq:
                                    Share.ShareFile(HandlePicActivity.this, Share.QQ);
                                    break;
                                case R.id.share_wechat:
                                    Share.ShareFile(HandlePicActivity.this, Share.WeChat);
                                    break;
                                case R.id.share_bluetooth:
                                    Share.ShareFile(HandlePicActivity.this, list, Share.BLUETOOTH);
                                    break;
                            }
                    }
                });
                dialog.show();
                break;
        }
    }
}
