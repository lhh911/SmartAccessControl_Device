package com.xsjqzt.module_main.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;

import com.xsjqzt.module_main.R;
import com.xsjqzt.module_main.activity.base.BaseActivity;
import com.xsjqzt.module_main.faceSdk.FaceSet;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private TextView faceVersion;//sdk版本
    private String appVersion = "";//app版本

    private TextView faceDemo;//演示
    private TextView faceExamples;//详细调用例子
    private FaceSet faceSet;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_face);
        //获取相关权限
        RxPermissions permissions = new RxPermissions(this);
        permissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                        } else {
                            showLongToast(MainActivity.this, "请同意软件的权限，才能继续使用");
                        }
                    }
                });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        faceDemo = findViewById(R.id.face_demo);
        faceExamples = findViewById(R.id.face_examples);
        faceVersion = findViewById(R.id.face_version);


        faceSet = new FaceSet(getApplication());
        faceSet.startTrack(0);
        appVersion = faceSet.getSdkVersion();
        faceSet.stopTrack();
        faceVersion.setText("app-v : " + appVersion);
    }

    @Override
    protected void initEvent() {
        faceDemo.setOnClickListener(this);
        faceExamples.setOnClickListener(this);

        Intent faceDemoIntent = new Intent(MainActivity.this, FaceDemoActivity.class);
        startActivity(faceDemoIntent);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.face_demo) {
            Intent faceDemoIntent = new Intent(MainActivity.this, FaceDemoActivity.class);
            startActivity(faceDemoIntent);

        } else if (i == R.id.face_examples) {
            Intent faceExamplesIntent = new Intent(MainActivity.this, FaceExamplesActivity.class);
            startActivity(faceExamplesIntent);

        } else {
        }
    }

}
