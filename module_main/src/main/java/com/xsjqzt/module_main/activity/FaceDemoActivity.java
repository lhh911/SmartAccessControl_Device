/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xsjqzt.module_main.activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import com.readsense.cameraview.camera.Size;
import com.readsense.cameraview.dialog.BindViewHolder;
import com.readsense.cameraview.dialog.TBaseAdapter;
import com.readsense.cameraview.dialog.TDialog;
import com.readsense.cameraview.dialog.TListDialog;
import com.xsjqzt.module_main.Config.DemoConfig;
import com.xsjqzt.module_main.R;
import com.xsjqzt.module_main.activity.base.CameraActivity;
import com.xsjqzt.module_main.activity.base.ExApplication;
import com.xsjqzt.module_main.activity.fragment.FaceConfigFragment;
import com.xsjqzt.module_main.activity.register.IDCardRecognitionAcitvity;
import com.xsjqzt.module_main.dataSource.UserDataUtil;
import com.xsjqzt.module_main.modle.User;
import com.xsjqzt.module_main.util.BitmapUtil;
import com.xsjqzt.module_main.util.SharedPrefUtils;
import com.xsjqzt.module_main.util.TrackDrawUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import mobile.ReadFace.YMFace;


public class FaceDemoActivity extends CameraActivity implements View.OnClickListener, FaceConfigFragment.clickConfigListener, TBaseAdapter.OnAdapterItemClickListener {

    private final int[] FACE_RECO = {R.drawable.ic_face_reco_off, R.drawable.ic_face_reco_on,};
    private final int[] FACE_TRACK = {R.drawable.ic_face_track_off, R.drawable.ic_face_track_on,};
    private final int[] FACK_LIVENESS = {R.drawable.ic_face_liveness_off, R.drawable.ic_face_liveness_on,};
    private final int[] FACK_RGB_LIVENESS = {R.drawable.ic_face_rgb_liveness_off, R.drawable.ic_face_rgb_liveness_on,};
    private final int[] FACK_BINOCULARE_LIVENESS = {R.drawable.ic_face_binocular_liveness_off, R.drawable.ic_face_binocular_liveness_on,};
    private String[] registStr = new String[]{"人脸库", "人证识别"};
    private Map<Integer, User> userMap;

    private boolean openFaceTrack = false;//追踪
    private boolean openFaceReco = false;//识别
    private boolean openFaceLiveness = true; //红外活体
    private boolean openFaceRgbLiveness = true;  //可见光活体
    private boolean openFaceBinoculareLiveness = true; //双目活体（可见光+红外）

    private ImageView btFaceTrack;//人脸追踪
    private ImageView btFaceReco;//人脸识别
    private ImageView btFaceRegist;//人脸注册
    private ImageView btRgbLiveness;//可见光活体
    private ImageView btFaceLiveness;//红外活体
    private ImageView btFaceBinoculareLiveness;//双目活体
    private ImageView btFaceConfig;//配置
    private ImageView btSwitchCamera;//切换相机

    private FaceConfigFragment fragment; //配置fragment
    private byte[] mBytes;
    private byte[] mBytesIr;
    private int mWidth;
    private int mHeight;

    private final String Tag = "Rs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_face_demo);
        super.onCreate(savedInstanceState);

        btFaceReco.setImageResource(FACE_RECO[openFaceReco ? 0 : 1]);
        openFaceReco = openFaceReco == false ? true : false;


//        btFaceBinoculareLiveness.setImageResource(FACK_BINOCULARE_LIVENESS[openFaceBinoculareLiveness ? 0 : 1]);
//        openFaceBinoculareLiveness = openFaceBinoculareLiveness == false ? true : false;
//        if (openFaceBinoculareLiveness)
//            openIRCamera();
//        else
//            closeIRCamera();
    }

    /**
     * 相机帧回调
     *
     * @param bytes   rgb视频流
     * @param irBytes ir视频流
     * @param iw      camera分辨率
     * @param ih      camera分辨率
     * @param isMulti
     * @return
     */
    @Override
    protected List<YMFace> onCameraPreviewFrame(final byte[] bytes, final byte[] irBytes, final int iw, final int ih, final boolean isMulti) {
        if (bytes == null) return null;
        mBytes = bytes;
        mBytesIr = irBytes;
        mWidth = iw;
        mHeight = ih;

        return faceSet.logic(bytes, irBytes, iw, ih, isMulti, openFaceTrack, openFaceReco, getLivenessType());
    }


    /**
     * 人脸绘画
     *
     * @param ymFaces
     * @param mConfig
     * @param draw_view 绘画view
     * @param scale_bit
     * @param cameraId  cameraId
     * @param fps
     */
    @Override
    protected void drawView(List<YMFace> ymFaces, DemoConfig mConfig, SurfaceView draw_view, float scale_bit, int cameraId, String fps) {
        try {
            //打开配置界面时不做绘画处理
            if (fragment != null) {
                TrackDrawUtil.drawFaceTracking(null, null, draw_view, 0, 0, "", false, null);
                return;
            }
            //当开启追踪时，不做识别与活体
            if (openFaceTrack) {
                TrackDrawUtil.drawFace(ymFaces, mConfig, draw_view, scale_bit, cameraId, fps, false);
                return;
            }
            TrackDrawUtil.drawFaceTracking(ymFaces, mConfig, draw_view, scale_bit, cameraId, fps, false, userMap);
        } catch (Exception e) {
            Log.d(Tag, "" + e);
        }
    }

    /**
     * 获取当前活体开启类型
     * 双目：0 可见光：1 红外：2 都不开启:-1
     *
     * @return
     */
    public int getLivenessType() {
//        return (openFaceBinoculareLiveness && !openFaceLiveness && !openFaceRgbLiveness) ? 0 : (!openFaceBinoculareLiveness && !openFaceLiveness
//                && openFaceRgbLiveness) ? 1 : (openFaceLiveness && !openFaceRgbLiveness && !openFaceBinoculareLiveness) ? 2 : -1;
        return 0;
    }

    @Override
    protected void onResume() {
        //获取数据库人脸集合
        super.onResume();
        userMap = UserDataUtil.updateDataSource(true);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void initEvent() {
        super.initEvent();
    }

    /**
     * 初始化
     */
    @Override
    protected void initView() {
        btFaceLiveness = findViewById(R.id.face_liveness);
        btFaceLiveness.setOnClickListener(this);
        btRgbLiveness = findViewById(R.id.face_rgb_liveness);
        btRgbLiveness.setOnClickListener(this);
        btFaceBinoculareLiveness = findViewById(R.id.face_binocular_liveness);
        btFaceBinoculareLiveness.setOnClickListener(this);
        btFaceTrack = findViewById(R.id.face_track);
        btFaceTrack.setOnClickListener(this);
        btFaceReco = findViewById(R.id.face_reco);
        btFaceReco.setOnClickListener(this);
        btFaceRegist = findViewById(R.id.face_regist);
        btFaceRegist.setOnClickListener(this);
        btFaceConfig = findViewById(R.id.face_config);
        btFaceConfig.setOnClickListener(this);
        btSwitchCamera = findViewById(R.id.bt_switch_camera);
        btSwitchCamera.setOnClickListener(this);
        super.initView();


    }


    @Override
    public void onClick(View item) {
        int i = item.getId();
        if (i == R.id.face_reco) {/**人脸识别开关*/
            btFaceReco.setImageResource(FACE_RECO[openFaceReco ? 0 : 1]);
            openFaceReco = openFaceReco == false ? true : false;
            showShortToast(this, "人脸识别：" + openFaceReco);
            return;
        } else if (i == R.id.face_binocular_liveness) {/**双目识别开关*/
            btFaceBinoculareLiveness.setImageResource(FACK_BINOCULARE_LIVENESS[openFaceBinoculareLiveness ? 0 : 1]);
            openFaceBinoculareLiveness = openFaceBinoculareLiveness == false ? true : false;
            if (openFaceBinoculareLiveness)
                openIRCamera();
            else
                closeIRCamera();
            showShortToast(this, "双目活体识别：" + openFaceLiveness);
            return;
        } else if (i == R.id.face_track) {/**人脸追踪开关*/
            btFaceTrack.setImageResource(FACE_TRACK[openFaceTrack ? 0 : 1]);
            openFaceTrack = openFaceTrack == false ? true : false;
            showShortToast(this, "人脸追踪：" + openFaceTrack);
            return;
        } else if (i == R.id.face_liveness) {/**红外活体*/
            btFaceLiveness.setImageResource(FACK_LIVENESS[openFaceLiveness ? 0 : 1]);
            openFaceLiveness = openFaceLiveness == false ? true : false;
            showShortToast(this, "红外活体识别：" + openFaceLiveness);
            return;
        } else if (i == R.id.face_rgb_liveness) {/**可见光活体*/
            btRgbLiveness.setImageResource(FACK_RGB_LIVENESS[openFaceRgbLiveness ? 0 : 1]);
            openFaceRgbLiveness = openFaceRgbLiveness == false ? true : false;
            showShortToast(this, "可见光活体识别：" + openFaceRgbLiveness);
            return;
        } else if (i == R.id.bt_switch_camera) {/**切换摄像头*/
            swichCamera();
            return;
        } else if (i == R.id.face_regist) {/**用户注册*/
            new TListDialog.Builder(getSupportFragmentManager())
                    .setScreenWidthAspect(this, 1f)
                    .setGravity(Gravity.BOTTOM)
                    .setAdapter(new TBaseAdapter<String>(R.layout.item_simple_text, Arrays.asList(registStr)) {
                        @Override
                        protected void onBind(BindViewHolder holder, int position, String s) {
                            holder.setText(R.id.tv, s);
                        }
                    })
                    .setOnAdapterItemClickListener(this)
                    .create()
                    .show();
            return;
        } else if (i == R.id.face_config) {/**参数设置*/
            if (fragment == null) {
                fragment = new FaceConfigFragment();
                fragment.setOnclickListener(this);
                FragmentTransaction ft2 = getFragmentManager().beginTransaction();
                ft2.replace(R.id.demo_fragment, fragment);
                ft2.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft2.addToBackStack(null);
                ft2.commit();
            }
            return;
        }
    }

    /**
     * 更新配置
     *
     * @param resolution     相机分辨率
     * @param zoom           缩放比例
     * @param sdkAngle       sdk识别角度
     * @param cameraAngle    相机预览方向
     * @param isAdjustView   是否适配预览
     * @param adjustVertical 是否进行旋转90度适配
     * @return
     */
    @Override
    public boolean adjustCameraParameters(Size resolution, float zoom, int sdkAngle, int cameraAngle, boolean isAdjustView, boolean adjustVertical) {
        if (null == faceSet || null == mCameraView) {
            return false;
        }
        //设置sdk识别方向
        faceSet.setOrientation(sdkAngle);
        //更新config
        mConfig = SharedPrefUtils.getObject(ExApplication.getContext(), "DEMO_CONFIG", DemoConfig.class);
        //重置camera参数
        return mCameraView.adjustCameraParameters(resolution, cameraAngle, isAdjustView, adjustVertical, zoom);
    }

    /**
     * 获取摄像头可支持分辨率列表
     *
     * @return
     */
    @Override
    public List<Camera.Size> getSupportedPreviewSize() {
        return mCameraView.getSupportedPreviewSize();
    }


    @Override
    public Bitmap[] getCameraBytes() {
        return new Bitmap[]{null == mBytes ? null : BitmapUtil.getBitmapFromYuvByte(mBytes, mWidth, mHeight),
                null == mBytesIr ? null : BitmapUtil.getBitmapFromYuvByte(mBytesIr, mWidth, mHeight)};
    }

    @Override
    public void onBackPressed() {
        fragment = null;
        super.onBackPressed();
    }

    /**
     * 注册：功能跳转
     *
     * @param holder
     * @param position
     * @param o
     * @param tDialog
     */
    @Override
    public void onItemClick(BindViewHolder holder, int position, Object o, TDialog tDialog) {
        Intent intent;
        switch (position) {
            case 0:
                intent = new Intent(FaceDemoActivity.this, FaceRegistActivity.class);
                startActivity(intent);
                break;
            case 1:
                intent = new Intent(FaceDemoActivity.this, IDCardRecognitionAcitvity.class);
                startActivity(intent);
                break;
        }
        tDialog.dismiss();
    }
}
