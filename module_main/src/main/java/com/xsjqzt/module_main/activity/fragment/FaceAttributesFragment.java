package com.xsjqzt.module_main.activity.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
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

import mobile.ReadFace.YMFace;
import com.xsjqzt.module_main.R;
import com.xsjqzt.module_main.faceSdk.FaceSet;
import com.xsjqzt.module_main.util.BitmapUtil;
import com.xsjqzt.module_main.util.DataConversionUtil;
import com.xsjqzt.module_main.util.GlideImageLoader;

public class FaceAttributesFragment extends Fragment {

    private FaceSet faceSet;
    private ImageView ivHead;
    private TextView tvAge;
    private TextView tvGender;
    private TextView tvGenderConfidence;
    private TextView tvBeautyScore;
    private TextView tvCloseEye;
    private TextView tvOpenMouth;
    private TextView tvHasGlass;
    private TextView tvSmile;
    private TextView tvFaceFeature;
    private Button btSelect;
    private ImagePicker imagePicker;
    private int IMAGE_PICKER = 10000;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attributes, container, false);
        init(view);
        return view;
    }

    public void init(View view) {
        faceSet = new FaceSet(getActivity());
        faceSet.startTrack(0);
        ivHead = view.findViewById(R.id.iv_fa_head);
        tvAge = view.findViewById(R.id.tv_fa_age);
        tvGender = view.findViewById(R.id.tv_fa_face_gender);
        tvGenderConfidence = view.findViewById(R.id.tv_fa_face_gender_confidence);
        tvBeautyScore = view.findViewById(R.id.tv_fa_face_beauty_score);
        tvCloseEye = view.findViewById(R.id.tv_fa_face_eye_close);
        tvOpenMouth = view.findViewById(R.id.tv_fa_face_mouth_open);
        tvSmile = view.findViewById(R.id.tv_fa_face_smile);
        tvFaceFeature = view.findViewById(R.id.tv_fa_face_feature);
        tvHasGlass = view.findViewById(R.id.tv_fa_face_has_glass);
        btSelect = view.findViewById(R.id.bt_fa_select);

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
                startActivityForResult(intent, IMAGE_PICKER);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == IMAGE_PICKER) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null && images.size() > 0) {
                    YMFace ymFace = faceSet.
                            getFaceAttribute(BitmapUtil.decodeScaleImage(images.get(0).path, 1000, 1000));
                    float[] faceFeature = faceSet.
                            getFaceFeature(BitmapUtil.decodeScaleImage(images.get(0).path, 1000, 1000));
                    if (ymFace != null) {
                        ivHead.setImageBitmap(BitmapUtil.decodeScaleImage(images.get(0).path, 1000, 1000));
                        tvAge.setText("" + ymFace.getAge());
                        tvGender.setText(ymFace.getGender() + "");
                        tvGenderConfidence.setText("" + ymFace.getGenderConfidence());
                        tvBeautyScore.setText("" + ymFace.getBeautyScore());
                        tvCloseEye.setText("" + ymFace.isEyeClose());
                        tvOpenMouth.setText("" + ymFace.isMouthOpen());
                        tvSmile.setText("" + ymFace.isSmile());
                        tvHasGlass.setText("" + ymFace.isHasGlass());
                    } else {
                        tvAge.setText("");
                        tvGender.setText("");
                        tvGenderConfidence.setText("");
                        tvBeautyScore.setText("");
                        tvCloseEye.setText("");
                        tvOpenMouth.setText("");
                        tvSmile.setText("");
                    }
                    if (faceFeature != null) {
                        byte[] bytes = DataConversionUtil.floatArrayToByteArray(faceFeature);
                        String strBase64 = new String(Base64.encode(bytes, Base64.DEFAULT));
                        tvFaceFeature.setText("" + strBase64);
                    } else {
                        tvFaceFeature.setText("null");
                    }

                }
            }
        }
    }
}
