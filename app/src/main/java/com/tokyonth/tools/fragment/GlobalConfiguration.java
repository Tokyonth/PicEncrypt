package com.tokyonth.tools.fragment;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tokyonth.tools.R;
import com.tokyonth.tools.files.FileOperation;
import com.tokyonth.tools.utils.SPUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class GlobalConfiguration extends Fragment implements View.OnClickListener {

    private EditText et_path;
    private Button btn_save;
    private TextView tv_key;
    private TextView tv_conf_msg;
    private Button btn_add;
    private Button btn_check;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_global_configuration,container,false);
        et_path = (EditText) root.findViewById(R.id.et_path);
        btn_save = (Button) root.findViewById(R.id.btn_save);
        tv_key = (TextView) root.findViewById(R.id.tv_key);
        btn_add = (Button) root.findViewById(R.id.btn_add);
        btn_check = (Button) root.findViewById(R.id.btn_check);
        tv_conf_msg = (TextView) root.findViewById(R.id.tv_conf_msg);

        btn_check.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        SetLineNumber();
        tv_conf_msg.setText((String)SPUtils.getData("SdPath",""));
        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                String path = et_path.getText().toString().trim();
                SPUtils.putData("SdPath",path);
                SPUtils.putData("Custom_Tag",true);
                tv_conf_msg.setText((String)SPUtils.getData("SdPath",""));
                Toast.makeText(getContext(),"设置成功",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_add:
                AddKey();
                break;
            case R.id.btn_check:
                CheckKey();
                break;
        }
    }

    private void CheckKey() {
        String filepath = getContext().getFilesDir().getPath() + "/key";
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            File file = new File(filepath);
            RandomAccessFile fileR = new RandomAccessFile(file,"r");
            String str = null;
            while ((str = fileR.readLine())!= null) {
                arrayList.add(str);
            }
            fileR.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对ArrayList中存储的字符串进行处理
        int length = arrayList.size();
        final String[] array = new String[length];
        for (int i = 0; i < length; i++) {
            String s = arrayList.get(i);
            array[i] = s;
        }

        AlertDialog.Builder listDialog = new AlertDialog.Builder(getContext());
        listDialog.setTitle("密匙");
        listDialog.setItems(array, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setText(array[which]);
                Toast.makeText(getContext(), "已复制到粘贴板", Toast.LENGTH_SHORT).show();
            }
        });
        listDialog.setNegativeButton("关闭",null);
        listDialog.show();
    }

    private void SetLineNumber() {
        try {
            File file = new File(getContext().getFilesDir().getPath() + "/key");
            LineNumberReader lnr = new LineNumberReader(new FileReader(file));
            lnr.skip(Long.MAX_VALUE);
            int lineNo = lnr.getLineNumber() + 1;
            lnr.close();
            String str = Integer.toString(lineNo);
            tv_key.setText("已保存" + str + "条");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void AddKey() {
        final String filePath = getContext().getFilesDir().getPath() + "/";
        final String fileName = "key";
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View root = View.inflate(getContext(), R.layout.dialog_choosepage, null);
        final EditText edit = (EditText) root.findViewById(R.id.choose_page_edit);
        builder.setView(root);
        builder.setTitle("输入密匙");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("添加", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String keyStr = edit.getText().toString().trim();
                FileOperation fileOperation = new FileOperation();
                fileOperation.writeTxtToFile(keyStr, filePath, fileName);
                Toast toast = Toast.makeText(getContext(),"添加成功!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                SetLineNumber();
                dialog.dismiss();
            }
        }).create().show();

    }

}
