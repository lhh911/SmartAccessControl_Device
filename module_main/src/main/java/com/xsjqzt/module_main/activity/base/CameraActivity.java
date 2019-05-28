package com.xsjqzt.module_main.activity.base;

import android.Manifest;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.readsense.cameraview.camera.CameraView;
import com.readsense.cameraview.camera.Size;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.xsjqzt.module_main.faceSdk.FaceSet;

import java.util.List;

import io.reactivex.functions.Consumer;
import mobile.ReadFace.YMFace;
import com.xsjqzt.module_main.Config.DemoConfig;
import com.xsjqzt.module_main.R;
import com.xsjqzt.module_main.faceSdk.FaceSet;
import com.xsjqzt.module_main.modle.FaceResult;
import com.xsjqzt.module_main.util.DisplayUtil;
import com.xsjqzt.module_main.util.SharedPrefUtils;

public abstract class CameraActivity extends BaseActivity {
    private SurfaceView sfv_draw_view; //人脸框绘画层
    public FaceSet faceSet = null; //sdk逻辑层
    public CameraView mIRCameraView; //红外摄像头
    public CameraView mCameraView; //RGB摄像头
    private byte[] irData; //ir视频流
    private int screenW = 0;//屏幕分辨率w
    private final Object lock = new Object();
    private Size ratio;//摄像头预览分辨率
    private float scale_bit = 1;
    private List<YMFace> ymFaces;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取屏幕分辨率
        screenW = DisplayUtil.getDisplayMetrics(getApplicationContext()).widthPixels;
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initView() {
        mCameraView = findViewById(R.id.camera);
        sfv_draw_view = findViewById(R.id.sfv_draw_view);
        sfv_draw_view.setZOrderOnTop(true);
        sfv_draw_view.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        //算法sdk
        faceSet = new FaceSet(this);
        ViewGroup.LayoutParams params = sfv_draw_view.getLayoutParams();
        params.height = DisplayUtil.getDisplayMetrics(this).heightPixels;
        params.width = DisplayUtil.getDisplayMetrics(this).widthPixels;
        sfv_draw_view.requestLayout();
    }

    @Override
    protected void initEvent() {
        if (mCameraView != null) {
            mCameraView.addCallback(new CameraView.Callback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    final byte[] mdata = data;
                    synchronized (lock) {
                        //调用sdk获取人脸集合
                        ymFaces = onCameraPreviewFrame(mdata, irData,
                                mCameraView.getCameraResolution().getWidth(), mCameraView.getCameraResolution().getHeight(), mConfig.isMulti);
                        //获取预览分辨率
                        ratio = mCameraView.getCameraResolution();
                        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                            ratio = Size.inverse(ratio);
                        }
                        //获取缩放比例
                        mConfig.screenZoon = mCameraView.getScale();
                        initDrawViewSize();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //绘画人脸框
                                drawView(ymFaces, mConfig, sfv_draw_view, scale_bit, mCameraView.getFacing(), "");
                            }
                        });
                    }
                }
            });
        }
    }


    /**
     * 更新DrawView 大小
     */
    public void initDrawViewSize() {
        int viewW;
        int viewH;
        scale_bit = 1;
        if (mCameraView.getAdjustViewBounds()) {
            if (ratio.getWidth() <= screenW) {
                scale_bit = (screenW / (float) ratio.getWidth());
                viewW = (screenW);
            } else {
                viewW = (screenW);
                scale_bit = (screenW / (float) ratio.getWidth());
            }
            viewH = (int) (viewW / ratio.toFloat());
        } else {
            viewW = (int) (ratio.getWidth() * scale_bit);
            viewH = (int) (viewW / ratio.toFloat());
        }
        //设置sfv_draw_view 大小与cameraView预览一致
        sfv_draw_view.getLayoutParams().height = viewH;
        sfv_draw_view.getLayoutParams().width = viewW;
        sfv_draw_view.requestLayout();
    }


    @Override
    protected void onResume() {
        super.onResume();
        RxPermissions permissions = new RxPermissions(this);
        permissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            //预览适配
                            mCameraView.setAdjustViewBounds(mConfig.isAdjustView);
                            //设置cameraId
                            mCameraView.setFacing(mConfig.cameraFacing);
                            //设置分辨率
                            mCameraView.setCameraResolution(mConfig.previewSizeWidth, mConfig.previewSizeHeight);
                            //设置预览方向
                            mConfig.cameraAngle = mConfig.cameraAngle == -1 ? mCameraView.getDisplayOrientation() : mConfig.cameraAngle;
                            mCameraView.setDisplayOrientation(mConfig.cameraAngle);
                            //横竖屏调整
                            mCameraView.adjustVertical(mConfig.screenrRotate90);
                            //开启相机
                            mCameraView.start();
                            mConfig.sdkAngle = mConfig.sdkAngle == -1 ? getSdkOrientation(mConfig.cameraFacing) : mConfig.sdkAngle;
                            //初始化算法sdk
                            FaceResult result = faceSet.startTrack(mConfig.sdkAngle);
                            showShortToast(getApplicationContext(), "code:" + result.code + "  " + result.msg);
                            //保存配置
                            SharedPrefUtils.putObject(getApplicationContext(), "DEMO_CONFIG", mConfig);
                        } else {
                            showLongToast(getApplicationContext(), "请同意软件的权限，才能继续使用");
                        }
                    }
                });
    }

    /**
     * 开启IR摄像头
     */
    public void openIRCamera() {
        if (mIRCameraView == null) {
            mIRCameraView = findViewById(R.id.ir_camera);
            mIRCameraView.addCallback(new CameraView.Callback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    //将IR回调的视频流帧赋值给irData
                    irData = data;
                }
            });
        }
        int cameraFacing = mCameraView.getFacing();
        int facing = (cameraFacing == CameraView.FACING_FRONT ? CameraView.FACING_BACK : CameraView.FACING_FRONT);
        //设置摄像头
        mIRCameraView.setVisibility(View.VISIBLE);
        //设置与可见光一样的分辨率
        Size size = mCameraView.getCameraResolution();
        mIRCameraView.start(facing, mConfig.screenIrZoon, size, true);
    }

    /**
     * 关闭IR摄像头
     */
    public void closeIRCamera() {
        if (mIRCameraView == null) return;
        if (mIRCameraView.isCameraOpened())
            mIRCameraView.stop();
        mIRCameraView.setVisibility(View.GONE);
    }


    /**
     * 切换摄像头
     */
    public void swichCamera() {
        int facting = mCameraView.getFacing();
        //若两个摄像头同时开启时，需要先关闭摄像头，避免占用导致开启失败
        if (mIRCameraView != null && mCameraView != null) {
            mIRCameraView.stop();
            mCameraView.stop();
            mIRCameraView.setFacing(facting);
            mIRCameraView.start();
            mCameraView.setFacing(facting == CameraView.FACING_FRONT ?
                    CameraView.FACING_BACK : CameraView.FACING_FRONT);
            mCameraView.start();
        } else {
            mCameraView.setFacing(facting == CameraView.FACING_FRONT ?
                    CameraView.FACING_BACK : CameraView.FACING_FRONT);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        faceSet.stopTrack();
        if (mCameraView != null) {
            mCameraView.stop();
        }
        if (mIRCameraView != null) {
            if (mIRCameraView.isCameraOpened()) {
                mIRCameraView.stop();
            }
        }
        super.onPause();
    }

    /**
     * 获取sdk识别方向
     *
     * @param facing
     * @return
     */
    private int getSdkOrientation(int facing) {
        int sdkOrientation;
        /*
         * 横屏：
         * -前/后摄像头 sdkOrientation=0;
         * 竖屏：
         * -前摄像头 sdkOrientation=270;
         * -后摄像头 sdkOrientation=90;
         */
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            sdkOrientation = 0;  //横屏
        else {
            if (facing == 0) sdkOrientation = 90;  //竖屏后置
            else sdkOrientation = 270;//竖屏前置
        }
        return 0;
    }

    /**
     * 算法处理回调
     *
     * @param bytes   rgb视频流
     * @param irBytes ir视频流
     * @param iw      camera分辨率
     * @param ih      camera分辨率
     * @return
     */
    protected abstract List<YMFace> onCameraPreviewFrame(byte[] bytes, byte[] irBytes, int iw, int ih, boolean isMulti);

    /**
     * 人脸框绘画
     *
     * @param faces     人脸集合
     * @param draw_view 绘画view
     * @param scale_bit
     * @param cameraId  cameraId
     * @param fps
     */
    protected abstract void drawView(List<YMFace> faces, DemoConfig mConfig, SurfaceView draw_view, float scale_bit, int cameraId, String fps);
}
