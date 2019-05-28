package com.xsjqzt.module_main.activity.register;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.readsense.cameraview.dialog.BindViewHolder;
import com.readsense.cameraview.dialog.OnBindViewListener;
import com.readsense.cameraview.dialog.OnViewClickListener;
import com.readsense.cameraview.dialog.TDialog;

import java.util.List;

import mobile.ReadFace.YMFace;
import com.xsjqzt.module_main.Config.DemoConfig;
import com.xsjqzt.module_main.R;
import com.xsjqzt.module_main.activity.FaceRegistActivity;
import com.xsjqzt.module_main.activity.base.CameraActivity;
import com.xsjqzt.module_main.modle.FaceResult;
import com.xsjqzt.module_main.util.BitmapUtil;
import com.xsjqzt.module_main.util.TrackDrawUtil;

public class RegistFromCamAcitvity extends CameraActivity implements View.OnClickListener {
    private Button btnAddFace;
    private Button btnSwitchCamera;
    private TextView tvTips;
    private boolean isAdd = false;
    private boolean isCorrect = false;//标志人脸是否校验通过，默认为false

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_regist_from_cam);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        super.initView();
        btnAddFace = findViewById(R.id.btn_add_face);
        btnAddFace.setEnabled(false);
        btnAddFace.setOnClickListener(this);
        tvTips = findViewById(R.id.tv_tips);
        tvTips.setText(R.string.tips1);
        btnSwitchCamera = findViewById(R.id.bt_switch_camera);
        btnSwitchCamera.setOnClickListener(this);
    }

    int iWidth;
    int iHeight;

    @Override
    protected List<YMFace> onCameraPreviewFrame(byte[] bytes, byte[] irBytes, int iw, int ih, boolean isMulti) {
        List<YMFace> ymFaces = faceSet.faceTracking(bytes, iw, ih, false);
        if (ymFaces == null) return ymFaces;
        final YMFace face = ymFaces.get(0);
        iWidth = iw;
        iHeight = ih;
        final byte[] data = bytes;
        if (face != null) {
            runOnUiThread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void run() {
                    if (null != face) {
                        checkAndRegistFace(face, data, iWidth, iHeight);
                    } else {
                        btnAddFace.setEnabled(false);
                        btnAddFace.setBackground(getResources().getDrawable(R.mipmap.add_face_unable));
                    }
                }
            });
        }
        return ymFaces;
    }

    @Override
    protected void drawView(List<YMFace> ymFaces, DemoConfig mConfig, SurfaceView draw_view, float scale_bit, int cameraId, String fps) {
        TrackDrawUtil.drawFace(ymFaces, mConfig, draw_view, scale_bit, cameraId, fps, false);
    }


    private void checkAndRegistFace(final YMFace face, final byte[] bytes, final int iw, final int ih) {
        if (!isCorrect)
            isCorrect = faceSet.checkFaceDirection(face);
        if (isCorrect) {
            btnAddFace.setEnabled(true);
            btnAddFace.setBackground(getResources().getDrawable(R.mipmap.add_face_able));
            if (isAdd) {
                isCorrect = false;
                final int isRegist = faceSet.isRegist(0);
                if (isRegist > 0) {
                    new TDialog.Builder(getSupportFragmentManager())
                            .setLayoutRes(R.layout.dialog_regist)
                            .setHeight(600)
                            .setGravity(Gravity.CENTER)
                            .setScreenWidthAspect(this, 0.9f)
                            .addOnClickListener(R.id.regist_cancle, R.id.regist_confirm)
                            .setCancelableOutside(true)
                            .setOnViewClickListener(new OnViewClickListener() {
                                @Override
                                public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
                                    if (view.getId() == R.id.regist_confirm) {
                                        if (faceSet.deleteUserByPersonId(isRegist)) {
                                            regist(bytes, 0, iw, ih, face.getRect());
                                        }
                                    }
                                    tDialog.dismiss();
                                }
                            })
                            .create()
                            .show();
                } else {
                    regist(bytes, 0, iw, ih, face.getRect());
                }
            }
        } else {
            btnAddFace.setEnabled(false);
            btnAddFace.setBackground(getResources().getDrawable(R.mipmap.add_face_unable));
        }
        isAdd = false;

    }

    /**
     * @param bytes 视频流
     * @param index
     * @param iw
     * @param ih
     */
    public boolean regist(final byte[] bytes, final int index, final int iw, final int ih, final float[] rect) {
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
                            FaceResult faceResult = faceSet.registByCam(name, index, rect, BitmapUtil.getBitmapFromYuvByte(bytes, iw, ih));
                            if (faceResult.code == 0) {
                                Toast.makeText(getApplicationContext(), "录入成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegistFromCamAcitvity.this, FaceRegistActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "录入失败", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            regist(bytes, 0, iw, ih, rect);
                        }
                    }
                })
                .create()
                .show();
        isCorrect = true;
        return true;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_add_face) {
            if (!isAdd) {
                isAdd = true;
            }

        } else if (i == R.id.bt_switch_camera) {
            swichCamera();

        }
    }

}
