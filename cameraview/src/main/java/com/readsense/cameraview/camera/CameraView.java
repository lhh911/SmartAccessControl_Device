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

import android.content.Context;
import android.content.res.TypedArray;
import android.hardware.Camera;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.readsense.cameraview.R;

import java.util.List;


public class CameraView extends FrameLayout {

    public static final int FACING_BACK = 0;  //后摄像头
    public static final int FACING_FRONT = 1; //前摄像头
    private CameraViewImpl mImpl;
    private CallbackBridge mCallbacks;
    private boolean mAdjustViewBounds;
    private final DisplayOrientationDetector mDisplayOrientationDetector;
    private float mScale = 1;
    private boolean mAdjustVertical = false;


    public CameraView(Context context) {
        this(context, null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) {
            mCallbacks = null;
            mDisplayOrientationDetector = null;
            return;
        }
        final PreviewImpl preview = createPreviewImpl(context);
        mCallbacks = new CallbackBridge();
//        if (Build.VERSION.SDK_INT < 21) {
//            mImpl = new Camera1(mCallbacks, preview);
//        } else if (Build.VERSION.SDK_INT > 21) {
//            mImpl = new Camera2(mCallbacks, preview, context);
//        }
        mImpl = new Camera1(mCallbacks, preview);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CameraView, defStyleAttr,
                R.style.Widget_CameraView);
        mAdjustViewBounds = a.getBoolean(R.styleable.CameraView_android_adjustViewBounds, false);
        a.recycle();
        mDisplayOrientationDetector = new DisplayOrientationDetector(context) {        // Display orientation detector
            @Override
            public void onDisplayOrientationChanged(int displayOrientation) {
                mImpl.setDisplayOrientation(displayOrientation);
            }
        };
    }

    @NonNull
    private PreviewImpl createPreviewImpl(Context context) {
        PreviewImpl preview;
        if (Build.VERSION.SDK_INT < 14) {
            preview = new SurfaceViewPreview(context, this);
        } else {
            preview = new TextureViewPreview(context, this);
        }
        return preview;
    }

    /**
     * 设置屏幕预览方向
     *
     * @param displayOrientation
     */
    public void setDisplayOrientation(int displayOrientation) {
        if (mImpl != null) {
            mImpl.setDisplayOrientation(displayOrientation);
        }
    }

    /**
     * 获取当前屏幕预览方向
     *
     * @return
     */
    public int getDisplayOrientation() {
        if (mImpl != null) {
            return mImpl.getDisplayOrientation();
        }
        return 0;
    }

    /**
     * 获取摄像头分辨率
     *
     * @return
     */
    public Size getCameraResolution() {
        if (mImpl != null)
            return mImpl.getCameraResolution();
        return null;
    }


    public float getScale() {
        return mScale;
    }

    /**
     * 设置摄像头分辨率
     *
     * @param s
     */
    public void setCameraResolution(Size s) {
        if (mImpl.setCameraResolution(s)) {
            requestLayout();
        }
    }

    public void setCameraResolution(int w, int h) {
        if (mImpl.setCameraResolution(new Size(w, h))) {
            requestLayout();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isInEditMode()) {
            mDisplayOrientationDetector.enable(ViewCompat.getDisplay(this));
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (!isInEditMode()) {
            mDisplayOrientationDetector.disable();
        }
        super.onDetachedFromWindow();
    }


    /**
     * 设置预览缩放
     *
     * @param scale
     */
    public void setScale(float scale) {
        if (scale > 1)
            mScale = 1;
        else
            mScale = scale;
        requestLayout();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY));
        if (isInEditMode()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        Size ratio = getAspectRatio();
        assert ratio != null;
        if (DisplayUtil.getDisplayMetrics(getContext()).widthPixels > DisplayUtil.getDisplayMetrics(getContext()).heightPixels) {
            ratio = Size.inverse(ratio);
        }
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int w = Math.min(DisplayUtil.getDisplayMetrics(getContext()).widthPixels, widthSize);
        int h = (int) (w * ratio.toFloat());
        if (!mAdjustViewBounds) {
            w = Math.min(DisplayUtil.getDisplayMetrics(getContext()).widthPixels, ratio.getWidth());
            h = (int) (w / ratio.toFloat());
        }
        if (DisplayUtil.getDisplayMetrics(getContext()).widthPixels < DisplayUtil.getDisplayMetrics(getContext()).heightPixels) {
            //竖屏
            if (mAdjustVertical) {
                mImpl.getView().measure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize((int) (Math.max(w, h) * mScale)), MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(MeasureSpec.getSize((int) (Math.min(w, h) * mScale)), MeasureSpec.EXACTLY));
            } else {
                mImpl.getView().measure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize((int) (Math.min(w, h) * mScale)), MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(MeasureSpec.getSize((int) (Math.max(w, h) * mScale)), MeasureSpec.EXACTLY));
            }
        } else {
            //横屏
            mImpl.getView().measure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize((int) (Math.max(w, h) * mScale)), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(MeasureSpec.getSize((int) (Math.min(w, h) * mScale)), MeasureSpec.EXACTLY));
        }
    }


    public void adjustVertical(boolean adjustVertical) {
        if (mAdjustVertical != adjustVertical) {
            mAdjustVertical = adjustVertical;
            requestLayout();
        }
    }

    public boolean adjustVertical() {
        return mAdjustVertical;
    }

    /**
     * 开启摄像头
     */
    public void start() {
        if (!mImpl.start()) {
            Parcelable state = onSaveInstanceState();//store the state ,and restore this state after fall back o Camera1
            mImpl = new Camera1(mCallbacks, createPreviewImpl(getContext()));// Camera2 uses legacy hardware layer; fall back to Camera1
            onRestoreInstanceState(state);
            mImpl.start();
        }
    }


    /**
     * 停止摄像头
     */
    public void stop() {
        mCallbacks = null;
        mImpl.stop();
    }

    /**
     * 是否开启
     *
     * @return
     */
    public boolean isCameraOpened() {
        return mImpl.isCameraOpened();
    }

    public void addCallback(@NonNull Callback callback) {
        if (mCallbacks == null) mCallbacks = new CallbackBridge();
        mCallbacks.setmCallback(callback);
    }

    public void setAdjustViewBounds(boolean adjustViewBounds) {
        if (mAdjustViewBounds != adjustViewBounds) {
            mAdjustViewBounds = adjustViewBounds;
            requestLayout();
        }
    }

    public boolean getAdjustViewBounds() {
        return mAdjustViewBounds;
    }

    /**
     * 设置相机方向（前后相机）
     *
     * @param facing
     */
    public void setFacing(int facing) {
        if (mImpl != null)
            mImpl.setFacing(facing);
    }


    /**
     * 开启相机
     *
     * @param facing            cameraId
     * @param mScale            预览缩放比例
     * @param caneraResolution  设置相机分辨率
     * @param mAdjustViewBounds 是否适配屏幕
     * @return
     */
    public boolean start(int facing, float mScale, Size caneraResolution, boolean mAdjustViewBounds) {
        try {
            setFacing(facing);
            setScale(mScale);
            setCameraResolution(caneraResolution);
            setAdjustViewBounds(mAdjustViewBounds);
            start();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 同上
     *
     * @return
     */
    public int getFacing() {
        return mImpl.getFacing();
    }


    @Nullable
    public Size getAspectRatio() {
        Size size = getCameraResolution();
        if (size != null) {
            return size;
        }
        return new Size(4, 3);
    }

    public List<Camera.Size> getSupportedPreviewSize() {
        if (mImpl == null) return null;
        else return mImpl.getSupportedPreviewSize();
    }



    private class CallbackBridge implements CameraViewImpl.Callback {
        private CameraView.Callback mCallback = null;

        @Override
        public void onCameraOpened() {
            if (mCallback != null) {
                mCallback.onCameraOpened(CameraView.this);
            }

        }

        @Override
        public void onCameraClosed() {
            if (mCallback != null) {
                mCallback.onCameraClosed(CameraView.this);
            }
        }

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            if (mCallback != null) {
                mCallback.onPreviewFrame(data, camera);
            }
        }

        public void setmCallback(@NonNull CameraView.Callback callback) {
            mCallback = callback;
        }
    }

    public abstract static class Callback {

        public void onCameraOpened(CameraView cameraView) {
        }

        public void onCameraClosed(CameraView cameraView) {
        }

        public void onPreviewFrame(byte[] data, Camera camera) {
        }

    }

    public boolean adjustCameraParameters(Size resolution, int displayOrientation, boolean adjustViewBounds, boolean mAdjustVertical, float zoom) {
        if (mImpl == null) return false;
        if (mAdjustViewBounds != adjustViewBounds) {
            setAdjustViewBounds(adjustViewBounds);
        }
        adjustVertical(mAdjustVertical);
        if (mScale != zoom) {
            setScale(zoom);
        }
        if (mImpl.adjustCameraParameters(mImpl.getFacing(), resolution)) {
            setDisplayOrientation(displayOrientation);
            requestLayout();
            return true;
        }
        return false;
    }



    public void setCallBackBuffer(int previewBufferSize) {
        mImpl.setCallBackBuffer(previewBufferSize);
    }



}
