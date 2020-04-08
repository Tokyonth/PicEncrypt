package com.tokyonth.tools.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.tokyonth.tools.R;

public class CustomDialog extends Dialog implements android.view.View.OnClickListener {

    private Context context;
    private ImageButton ib_share_qq,ib_share_wechat,ib_share_bluetooth;
    private DialogListener listener;

    public interface DialogListener{
        public void onClick(View view);
    }

    public CustomDialog(Context context) {
        super(context);
        this.context = context;
    }

    public CustomDialog(Context context, DialogListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.custom_dialog);
        ib_share_qq = (ImageButton)findViewById(R.id.share_qq);
        ib_share_wechat = (ImageButton)findViewById(R.id.share_wechat);
        ib_share_bluetooth = (ImageButton)findViewById(R.id.share_bluetooth);
        ib_share_qq.setOnClickListener(this);
        ib_share_wechat.setOnClickListener(this);
        ib_share_bluetooth.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        listener.onClick(v);
    }
}

