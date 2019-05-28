package com.xsjqzt.module_main.activity.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;

import java.util.ArrayList;

import com.xsjqzt.module_main.R;
import com.xsjqzt.module_main.faceSdk.FaceSet;
import com.xsjqzt.module_main.util.BitmapUtil;
import com.xsjqzt.module_main.util.GlideImageLoader;

public class FaceIDCardRecoFragment extends Fragment {

    private FaceSet faceSet;
    private ImageView ivHead;
    private ImageView ivHead2;
    private TextView tvResult;
    private Button btSelect;
    private Button btSelect2;
    private ImagePicker imagePicker;

    private float[] faceFeature;
    private float[] faceFeature2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reco_card, container, false);
        init(view);
        return view;
    }

    public void init(View view) {
        faceSet = new FaceSet(getActivity());
        faceSet.startTrack(0);
        ivHead = view.findViewById(R.id.bt_fr_card);
        ivHead2 = view.findViewById(R.id.bt_fr_card2);

        btSelect = view.findViewById(R.id.bt_fr_select);
        btSelect2 = view.findViewById(R.id.bt_fr_select2);
        tvResult = view.findViewById(R.id.tv_fr_result);
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

        btSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ImageGridActivity.class);
                startActivityForResult(intent, 1000);
            }
        });

        btSelect2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ImageGridActivity.class);
                startActivityForResult(intent, 2000);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == 1000) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null && images.size() > 0) {
                    faceFeature = faceSet.
                            getFaceFeatureCard(BitmapUtil.decodeScaleImage(images.get(0).path, 1000, 1000));
                    if (faceFeature != null) {
                        ivHead.setImageBitmap(BitmapUtil.decodeScaleImage(images.get(0).path, 1000, 1000));
                    } else {
                        ivHead.setImageResource(R.drawable.face_card);
                    }
                } else {
                    faceFeature = null;
                }
            }
            if (data != null && requestCode == 2000) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null && images.size() > 0) {
                    faceFeature2 = faceSet.
                            getFaceFeatureCard(BitmapUtil.decodeScaleImage(images.get(0).path, 1000, 1000));
                    if (faceFeature2 != null) {
                        ivHead2.setImageBitmap(BitmapUtil.decodeScaleImage(images.get(0).path, 1000, 1000));
                    } else {
                        ivHead2.setImageResource(R.drawable.face_card2);
                    }
                } else {
                    faceFeature2 = null;
                }
            }
            if (faceFeature2 != null && faceFeature != null) {
                int result = faceSet.compareFaceFeature(faceFeature, faceFeature2);
                tvResult.setText("" + result);
                faceFeature2 = null;
                faceFeature = null;
            }
        }
    }
}
