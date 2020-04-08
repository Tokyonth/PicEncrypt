package com.tokyonth.tools.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.huantansheng.easyphotos.EasyPhotos;
import com.tokyonth.tools.R;
import com.tokyonth.tools.logistic.HandlePicActivity;

public class PictureProcessing extends Fragment implements View.OnClickListener {

    private Button btn_choose;
    private Button btn_camera;

    private static int Camara_CODE = 101;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_picture_processing,container,false);
        btn_choose = (Button) root.findViewById(R.id.btn_choose);
        btn_camera = (Button) root.findViewById(R.id.btn_camera);
        btn_choose.setOnClickListener(this);
        btn_camera.setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_choose:
                Intent intent = new Intent();
                intent.setClass(getContext(),HandlePicActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_camera:
                EasyPhotos.createCamera(this)
                        .setFileProviderAuthority("com.tokyonth.tools.fileprovider")
                        .start(Camara_CODE);
                break;
        }
    }

}


