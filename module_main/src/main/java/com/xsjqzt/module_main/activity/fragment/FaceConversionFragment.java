package com.xsjqzt.module_main.activity.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;

import java.util.ArrayList;

import com.xsjqzt.module_main.R;
import com.xsjqzt.module_main.faceSdk.FaceSet;
import com.xsjqzt.module_main.util.BitmapUtil;
import com.xsjqzt.module_main.util.DataConversionUtil;
import com.xsjqzt.module_main.util.GlideImageLoader;

public class FaceConversionFragment extends Fragment {

    private Button btSelect;
    private TextView tvFaceFeature;
    private TextView tv2Char;
    private TextView tv2Byte;
    private TextView tvChar2F;
    private TextView tvByte2F;
    private TextView tv2String;
    private TextView tvString2F;

    private ImagePicker imagePicker;
    private int IMAGE_PICKER = 10000;
    private FaceSet faceSet;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversion, container, false);
        init(view);
        return view;
    }

    public void init(View view) {
        //sdk
        faceSet = new FaceSet(getActivity());
        faceSet.startTrack(0);

        tvFaceFeature = view.findViewById(R.id.tv_fc_float);
        tv2Char = view.findViewById(R.id.tv_fc_2char);
        tv2Byte = view.findViewById(R.id.tv_fc_2byte);
        tvChar2F = view.findViewById(R.id.tv_fc_char2f);
        tvByte2F = view.findViewById(R.id.tv_fc_byte2f);

        tv2String = view.findViewById(R.id.tv_fc_2string);
        tvString2F = view.findViewById(R.id.tv_fc_string2f);

        tv2String.setMovementMethod(ScrollingMovementMethod.getInstance());
        tvString2F.setMovementMethod(ScrollingMovementMethod.getInstance());
        tv2Char.setMovementMethod(ScrollingMovementMethod.getInstance());
        tvChar2F.setMovementMethod(ScrollingMovementMethod.getInstance());
        tv2Byte.setMovementMethod(ScrollingMovementMethod.getInstance());
        tvByte2F.setMovementMethod(ScrollingMovementMethod.getInstance());
        tvFaceFeature.setMovementMethod(ScrollingMovementMethod.getInstance());


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
        btSelect = view.findViewById(R.id.bt_fc_conversion);
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
                    float[] faceFeature = faceSet.
                            getFaceFeature(BitmapUtil.decodeScaleImage(images.get(0).path, 1000, 1000));
                    if (faceFeature != null) {
                        //char[]互转
                        tvFaceFeature.setText("" + faceFeature == null ? "null" : java.util.Arrays.toString(faceFeature));
                        char[] chars = faceSet.floatToChar(faceFeature);
                        tv2Char.setText("" + chars == null ? "null" : java.util.Arrays.toString(chars));
                        float[] char2f = faceSet.charToFloat(chars);
                        tvChar2F.setText("" + char2f == null ? "null" : java.util.Arrays.toString(char2f));

                        //byte[]互转
                        byte[] bytes = DataConversionUtil.floatArrayToByteArray(faceFeature);
                        tv2Byte.setText("" + bytes == null ? "null" : java.util.Arrays.toString(bytes));
                        float[] bytes2f = DataConversionUtil.byteArrayToFloatArray(bytes);
                        tvByte2F.setText("" + bytes2f == null ? "null" : java.util.Arrays.toString(bytes2f));

                        //String互转
//                        String str = faceSet.floatToString(faceFeature);
//                        tv2String.setText(str);
//                        float[] stringToFloat = faceSet.stringToFloat(str);
//                        tvString2F.setText("" + stringToFloat == null ? "null" : java.util.Arrays.toString(stringToFloat));
                        String strBase64 = new String(Base64.encode(bytes, Base64.DEFAULT));
                        byte[] bytes2 = Base64.decode(strBase64.getBytes(), Base64.DEFAULT);
                        float[] mFaceFeature = DataConversionUtil.byteArrayToFloatArray(bytes2);
                        tv2String.setText(strBase64);
                        tvString2F.setText("" + mFaceFeature == null ? "null" : java.util.Arrays.toString(mFaceFeature));

                    } else {
                        tvFaceFeature.setText("null");
                        tv2Char.setText("null");
                        tvChar2F.setText("null");
                        tv2Byte.setText("null");
                        tvByte2F.setText("null");
                    }
                }
            }
        }
    }

}
