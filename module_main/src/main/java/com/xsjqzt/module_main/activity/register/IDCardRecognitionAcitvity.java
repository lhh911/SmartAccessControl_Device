package com.xsjqzt.module_main.activity.register;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;

import java.util.ArrayList;
import java.util.List;

import mobile.ReadFace.YMFace;
import com.xsjqzt.module_main.Config.DemoConfig;
import com.xsjqzt.module_main.R;
import com.xsjqzt.module_main.activity.base.CameraActivity;
import com.xsjqzt.module_main.util.BitmapUtil;
import com.xsjqzt.module_main.util.GlideImageLoader;
import com.xsjqzt.module_main.util.TrackDrawUtil;

public class IDCardRecognitionAcitvity extends CameraActivity implements View.OnClickListener {
    private Button btSelect;
    private ImageView vImage;
    private Button btSwitchCamera;
    private ImagePicker imagePicker;
    private int IMAGE_PICKER = 10000;
    private float[] faceFeature;

    Bitmap bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.regist_from_card_acitvity);
        super.onCreate(savedInstanceState);
        btSelect = findViewById(R.id.btn_select);
        vImage = findViewById(R.id.view_show_image);
        btSwitchCamera = findViewById(R.id.bt_switch_camera);
        btSwitchCamera.setOnClickListener(this);
        btSelect.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected List<YMFace> onCameraPreviewFrame(byte[] bytes, byte[] irBytes, int iw, int ih, boolean isMulti) {
        if (faceFeature == null && bitmap != null)
            faceFeature = faceSet.getFaceFeatureByBitmap(bitmap);
        YMFace ymFace = faceSet.faceRecognitionByCard(bytes, iw, ih, faceFeature);
        if (ymFace != null) {
            List<YMFace> ymFaces = new ArrayList<>();
            ymFaces.add(ymFace);
            return ymFaces;
        }
        return null;
    }

    @Override
    protected void drawView(List<YMFace> ymFaces, DemoConfig mConfig, SurfaceView draw_view, float scale_bit, int cameraId, String fps) {
        TrackDrawUtil.drawFace(ymFaces, mConfig, draw_view, scale_bit, cameraId, fps, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == IMAGE_PICKER) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                for (int i = 0; i < images.size(); i++) {
                    faceFeature = null;
                    bitmap = BitmapUtil.decodeScaleImage(images.get(i).path, 1000, 1000);
                    if (bitmap != null) {
                        vImage = findViewById(R.id.view_show_image);
                        vImage.setImageBitmap(bitmap);
                    } else {
                        Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else {
            Toast.makeText(this, "未选择照片", Toast.LENGTH_SHORT).show();
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    protected void initEvent() {
        super.initEvent();
    }

    @Override
    protected void initData() {
        imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setCrop(true);        //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        imagePicker.setSelectLimit(1);    //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素
        super.initData();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_select) {
            Intent intent = new Intent(this, ImageGridActivity.class);
            startActivityForResult(intent, IMAGE_PICKER);

        } else if (i == R.id.bt_switch_camera) {
            swichCamera();

        }
    }
}
