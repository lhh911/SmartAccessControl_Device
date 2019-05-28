package com.xsjqzt.module_main.activity.register;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.readsense.cameraview.dialog.BindViewHolder;
import com.readsense.cameraview.dialog.OnBindViewListener;
import com.readsense.cameraview.dialog.OnViewClickListener;
import com.readsense.cameraview.dialog.TDialog;

import java.util.ArrayList;

import com.xsjqzt.module_main.R;
import com.xsjqzt.module_main.activity.FaceRegistActivity;
import com.xsjqzt.module_main.activity.base.BaseActivity;
import com.xsjqzt.module_main.faceSdk.FaceSet;
import com.xsjqzt.module_main.modle.FaceResult;
import com.xsjqzt.module_main.util.BitmapUtil;
import com.xsjqzt.module_main.util.GlideImageLoader;

public class RegistFromPicActivity extends BaseActivity {
    ImagePicker imagePicker;
    private int IMAGE_PICKER = 10000;
    private FaceSet faceSet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    protected void initView() {
        faceSet = new FaceSet(getApplication());
        faceSet.startTrack(0);
    }

    protected void initEvent() {
        Intent intent = new Intent(this, ImageGridActivity.class);
        startActivityForResult(intent, IMAGE_PICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == IMAGE_PICKER) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                for (int i = 0; i < images.size(); i++) {
                    doEnd(BitmapUtil.decodeScaleImage(images.get(i).path, 1000, 1000));
                }
            } else {
                showShortToast(RegistFromPicActivity.this, "未选择照片");
                finish();
            }
        } else {
            Toast.makeText(this, "未选择照片", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void doEnd(final Bitmap bitmap) {
        final Bitmap selectedImage = bitmap;
        new TDialog.Builder(getSupportFragmentManager())
                .setLayoutRes(R.layout.dialog_evaluate)
                .setScreenWidthAspect(this, 0.9f)
                .setGravity(Gravity.CENTER)
                .addOnClickListener(R.id.btn_evluate)
                .setOnBindViewListener(new OnBindViewListener() {
                    @Override
                    public void bindView(BindViewHolder viewHolder) {
                        TextView txShow = viewHolder.getView(R.id.tx_show);
                        txShow.setText(String.format("请输入昵称:"));
                        final EditText editText = viewHolder.getView(R.id.editText);
                        editText.post(new Runnable() {
                            @Override
                            public void run() {
                                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.showSoftInput(editText, 0);
                            }
                        });
                    }
                })
                .setOnViewClickListener(new OnViewClickListener() {
                    @Override
                    public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
                        EditText editText = viewHolder.getView(R.id.editText);
                        String name = editText.getText().toString();
                        if (!TextUtils.isEmpty(name.trim())) {
                            FaceResult faceResult = faceSet.registByBitmap(bitmap, name);
                            if (faceResult == null) return;
                            if (faceResult.code == 0) {
                                Intent intent = new Intent(RegistFromPicActivity.this, FaceRegistActivity.class);
                                startActivity(intent);
                                //添加成功，此返回值即为数据库对当前⼈人脸的中唯⼀一标识
                            } else {
                                showLongToast(RegistFromPicActivity.this, "添加失败:" + faceResult.msg);
                                finish();
                            }
                        } else {
                            doEnd(selectedImage);
                            return;
                        }

                    }
                })
                .create()
                .show();
    }
}