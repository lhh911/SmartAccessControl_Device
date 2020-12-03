package com.xsjqzt.module_main.Config;

import android.os.Environment;

import com.readsense.cameraview.camera.CameraView;

import java.io.File;
import java.io.Serializable;

public class DemoConfig implements Serializable {
    //默认开启cameraId
    public int cameraFacing = CameraView.FACING_BACK;
    //是否多人识别
    public boolean isMulti = false;
    public boolean isAdjustView = true;
    //后置摄像头绘制左右翻转
    public boolean screenrRotate90 = false;
    //特殊设备摄像头绘制左右翻转
    public boolean specialCameraLeftRightReverse = true;
    //特殊设备摄像头绘制上下翻转
    public boolean specialCameraTopDownReverse = false;
    //默认预览分辨率
    public int previewSizeWidth = 640;
    public int previewSizeHeight = 480;
    //默认预览缩放比例
    public float screenZoon = 1;
    //ir默认预览缩放比例
    public float screenIrZoon = (float) 0.3;
    //sdk识别方向
    public int sdkAngle = -1;
    //屏幕方向
    public int cameraAngle = -1;
    //是否绘画Ir预览中的人脸框
    public boolean isDrawIr = false;
    //批量注册图片路径
    public static final String ImagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/face_recognition_pic/";
    //注册头像的存储路径
    public static final String UserDatabasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/face_user_db/";//本地数据库路径

    public void setPreviewSize(int w, int h) {
        previewSizeWidth = w;
        previewSizeHeight = h;
    }

    public String getPreviewSize() {
        return previewSizeWidth + ":" + previewSizeHeight;
    }

    static {
        File file = new File(DemoConfig.UserDatabasePath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @Override
    public String toString() {
        return "DemoConfig{" +
                "cameraFacing=" + cameraFacing +
                ", isMulti=" + isMulti +
                ", isAdjustView=" + isAdjustView +
                ", screenrRotate90=" + screenrRotate90 +
                ", specialCameraLeftRightReverse=" + specialCameraLeftRightReverse +
                ", specialCameraTopDownReverse=" + specialCameraTopDownReverse +
                ", previewSizeWidth=" + previewSizeWidth +
                ", previewSizeHeight=" + previewSizeHeight +
                ", screenZoon=" + screenZoon +
                ", screenIrZoon=" + screenIrZoon +
                ", sdkAngle=" + sdkAngle +
                ", cameraAngle=" + cameraAngle +
                ", isDrawIr=" + isDrawIr +
                '}';
    }
}
