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

package com.readsense.cameraview.camera;

import android.annotation.SuppressLint;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.support.v4.util.SparseArrayCompat;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.List;


@SuppressWarnings("deprecation")
class Camera1 extends CameraViewImpl {

    private static final int INVALID_CAMERA_ID = -1;

    private static final SparseArrayCompat<String> FLASH_MODES = new SparseArrayCompat<>();

    private int mCameraId;
    Camera mCamera;
    private Camera.Parameters mCameraParameters;
    private final Camera.CameraInfo mCameraInfo = new Camera.CameraInfo();
    private boolean mShowingPreview; //是否显示预览
    private boolean mAutoFocus;  //是否自动对焦
    private int mFacing;  //方向
    private int mDisplayOrientation;  //方向
    private Size mPreviewSize; //分辨率
    private int LANDSCAPE_90 = 90;
    private int LANDSCAPE_270 = 270;

    private byte[] callbackBuffer;

    Camera1(Callback callback, PreviewImpl preview) {
        super(callback, preview);
        preview.setCallback(new PreviewImpl.Callback() {
            @Override
            public void onSurfaceChanged() {
                if (mCamera != null) {
                    setUpPreview();
                    adjustCameraParameters();
                }
            }
        });
    }

    /**
     * 打开相机与显示预览
     *
     * @return
     */
    @Override
    boolean start() {
        chooseCamera();
        stop();
        openCamera();
        if (mPreview.isReady()) {
            setUpPreview();
        }
        mShowingPreview = true;
        startPreview();
        return true;
    }

    /**
     * 开始显示预览
     */
    public void startPreview() {
        if (mCamera != null) {

//            if(callbackBuffer != null) {
//                mCamera.addCallbackBuffer(callbackBuffer);
//            }
//            mCamera.setPreviewCallbackWithBuffer(mCallback);
            mCamera.setPreviewCallback(mCallback);
            mCamera.startPreview();
        }
    }

    /**
     * 关闭相机与预览
     */
    @Override
    void stop() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
        }
        mShowingPreview = false;
        releaseCamera();
    }

    @SuppressLint("NewApi")
    void setUpPreview() {
        try {
            if (mPreview.getOutputClass() == SurfaceHolder.class) {
                final boolean needsToStopPreview = mShowingPreview && Build.VERSION.SDK_INT < 14;
                if (needsToStopPreview) {
                    mCamera.stopPreview();
                }
                mCamera.setPreviewDisplay(mPreview.getSurfaceHolder());
                if (needsToStopPreview) {
                    startPreview();
                }
            } else {
                mCamera.setPreviewTexture((SurfaceTexture) mPreview.getSurfaceTexture());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    boolean isCameraOpened() {
        return mCamera != null;
    }

    @Override
    void setFacing(int facing) {
        if (mFacing == facing) {
            return;
        }
        mFacing = facing;
        if (isCameraOpened()) {
            stop();
            start();
        }
    }

    @Override
    int getFacing() {
        return mFacing;
    }


    @Override
    Size getCameraResolution() {
        if (mPreviewSize == null)
            mPreviewSize = new Size(1920, 1080);
        return mPreviewSize;
    }

    @Override
    boolean setCameraResolution(Size resolution) {
        if (resolution != null) {
            mPreviewSize = resolution;
            adjustCameraParameters();
            return true;
        }
        return false;
    }


    @Override
    List<Camera.Size> getSupportedPreviewSize() {
        if (mCamera != null) {
            List<Camera.Size> sizes = mCamera.getParameters().getSupportedPreviewSizes();
            return sizes;
        }
        return null;
    }


    @Override
    void setDisplayOrientation(int displayOrientation) {
        if (mDisplayOrientation == displayOrientation) {
            return;
        }
        mDisplayOrientation = displayOrientation;
        if (isCameraOpened()) {
            mCameraParameters.setRotation(calcCameraRotation(displayOrientation));
            mCamera.setParameters(mCameraParameters);
            final boolean needsToStopPreview = mShowingPreview && Build.VERSION.SDK_INT < 14;
            if (needsToStopPreview) {
                mCamera.stopPreview();
            }
            mCamera.setDisplayOrientation(calcDisplayOrientation(displayOrientation));
            if (needsToStopPreview) {
                startPreview();
            }
        }
    }

    @Override
    int getDisplayOrientation() {
        if (mCameraInfo != null)
            return mCameraInfo.orientation;
        return 0;
    }

    @Override
    boolean adjustCameraParameters(int facing, Size resolution) {
        if (facing != -1) {
            mFacing = facing;
        }
        if (resolution != null) {
            mPreviewSize = resolution;
        }
        if (isCameraOpened()) {
            adjustCameraParameters();
            return true;
        }
        return false;
    }


    /**
     * This rewrites {@link #mCameraId} and {@link #mCameraInfo}.
     */
    private void chooseCamera() {
        for (int i = 0, count = Camera.getNumberOfCameras(); i < count; i++) {
            Camera.getCameraInfo(i, mCameraInfo);
            if (i == mFacing) {
                mCameraId = i;
                return;
            }
        }
        mCameraId = INVALID_CAMERA_ID;
    }

    private void openCamera() {
        try {

            if (mCamera != null) {
                releaseCamera();
            }
            mCamera = Camera.open(mCameraId);
            mCameraParameters = mCamera.getParameters();
            adjustCameraParameters();
            mCamera.setDisplayOrientation(calcDisplayOrientation(mDisplayOrientation));
            mCallback.onCameraOpened();
        } catch (Exception e) {
            Log.e("pan", "error:" + e);
        }
    }

    void adjustCameraParameters() {
        if (mPreviewSize == null) {
            Camera.Size size = mCameraParameters.getSupportedPictureSizes().get(0);//设置摄像头支持该比例下最高分辨率
            mPreviewSize = new Size(size.width, size.height);
        }

        if (mShowingPreview) {
            mCamera.stopPreview();
        }
        if (mCameraParameters == null || mCamera == null) return;
        mCameraParameters.setPreviewSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        mCameraParameters.setRotation(
                calcCameraRotation(mDisplayOrientation));
        setAutoFocusInternal(mAutoFocus);
        try {
            mCamera.setParameters(mCameraParameters);
        } catch (Exception e) {
            mPreviewSize = new Size(1920, 1080);
            mCameraParameters.setPreviewSize(1920, 1080);
            mCameraParameters.setPictureSize(1920, 1080);
            mCamera.setParameters(mCameraParameters);
        }
        if (mShowingPreview) {
            startPreview();
        }
    }


    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
            mCallback.onCameraClosed();
        }
    }

    private int calcDisplayOrientation(int screenOrientationDegrees) {
        if (mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            return (360 - (mCameraInfo.orientation + screenOrientationDegrees) % 360) % 360;
        } else {  // back-facing
            return (mCameraInfo.orientation - screenOrientationDegrees + 360) % 360;
        }
    }

    private int calcCameraRotation(int screenOrientationDegrees) {
        if (mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            return (mCameraInfo.orientation + screenOrientationDegrees) % 360;
        } else {  // back-facing
            final int landscapeFlip = isLandscape(screenOrientationDegrees) ? 180 : 0;
            return (mCameraInfo.orientation + screenOrientationDegrees + landscapeFlip) % 360;
        }
    }

    private boolean isLandscape(int orientationDegrees) {
        return (orientationDegrees == LANDSCAPE_90 ||
                orientationDegrees == LANDSCAPE_270);
    }

    /**
     * @return {@code true} if {@link #mCameraParameters} was modified.
     */
    private boolean setAutoFocusInternal(boolean autoFocus) {
        mAutoFocus = autoFocus;
        if (isCameraOpened()) {
            final List<String> modes = mCameraParameters.getSupportedFocusModes();
            if (autoFocus && modes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                mCameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            } else if (modes.contains(Camera.Parameters.FOCUS_MODE_FIXED)) {
                mCameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
            } else if (modes.contains(Camera.Parameters.FOCUS_MODE_INFINITY)) {
                mCameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
            } else {
                mCameraParameters.setFocusMode(modes.get(0));
            }
            return true;
        } else {
            return false;
        }
    }


    /**
     * 在调用Camera.startPreview()接口前，我们需要setPreviewCallbackWithBuffer，而setPreviewCallbackWithBuffer之前我们需要重新addCallbackBuffer，
     * 因为setPreviewCallbackWithBuffer 使用时需要指定一个字节数组作为缓冲区，用于预览图像数据 即addCallbackBuffer，然后你在onPerviewFrame中的data才会有值；
     *
     * addCallbackBuffer 和 我们需要setPreviewCallbackWithBuffer 配合使用，可以把数组传入回到中，onPerviewFrame预览回调中的 data数组不用频繁 GC  而导致内存暴增，o
     *  onPerviewFrame执行完后data数组会回收，会导致频繁GC
     * @param callbackBuffer
     */
    public  void addCallbackBuffer(byte[] callbackBuffer) {
        if(mCamera != null)
            mCamera.addCallbackBuffer(callbackBuffer);
    }

    @Override
    void setPreviewCallbackWithBuffer(Camera.PreviewCallback cb) {
        if(mCamera != null)
            mCamera.setPreviewCallbackWithBuffer(cb);
    }

    @Override
    void setCallBackBuffer(byte[] callbackBuffer) {
        this.callbackBuffer = callbackBuffer;
    }
}
