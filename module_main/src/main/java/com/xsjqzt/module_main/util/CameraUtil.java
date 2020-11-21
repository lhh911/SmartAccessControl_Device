package com.xsjqzt.module_main.util;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;

import com.jbb.library_common.comfig.KeyContacts;
import com.jbb.library_common.utils.SharePreferensUtil;
import com.jbb.library_common.utils.log.LogUtil;
import com.xsjqzt.module_main.activity.base.ExApplication;
import com.xsjqzt.module_main.faceSdk.FaceSet;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class CameraUtil {
    public CameraUtil() {
    }

    public static void saveFromPreview(byte[] yuv_data, String save_path, int iw, int ih) {
        saveFromPreview(yuv_data, ImageFormat.NV21, save_path, iw, ih);
    }

    public static void saveFromPreview(byte[] yuv_data, int imageFormat, String save_path, int iw, int ih) {
        FileOutputStream outStream = null;

        try {
            YuvImage yuvimage = new YuvImage(yuv_data, imageFormat, iw, ih, (int[]) null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            yuvimage.compressToJpeg(new Rect(0, 0, iw, ih), 100, baos);
            outStream = new FileOutputStream(save_path);
            outStream.write(baos.toByteArray());
            outStream.close();
        } catch (IOException var16) {
            var16.printStackTrace();
        } finally {
            if (outStream != null) {
                try {
                    outStream.close();
                    outStream = null;
                } catch (IOException var15) {
                    var15.printStackTrace();
                }
            }

        }

    }


    public static void clearAllFace( FaceSet faceSet) {
        if(faceSet == null)
            return;
        faceSet.removeAllUser();
    }

    public static int getFaceSize( FaceSet faceSet) {
        if(faceSet == null)
            return 0;
        return faceSet.getUserSize();
    }



    public static Camera.Size getCloselyPreSize(boolean isPortrait, int surfaceWidth, int surfaceHeight, List<Camera.Size> preSizeList) {
        LogUtil.d("预览分辨率中屏幕分辨率： " + surfaceWidth + " * " + surfaceHeight);

        int reqTmpWidth;
        int reqTmpHeight;
        // 当屏幕为垂直的时候需要把宽高值进行调换，保证宽大于高
        if (isPortrait) {
            reqTmpWidth = surfaceHeight;
            reqTmpHeight = surfaceWidth;
        } else {
            reqTmpWidth = surfaceWidth;
            reqTmpHeight = surfaceHeight;
        }
        //先查找preview中是否存在与surfaceview相同宽高的尺寸
        for (Camera.Size size : preSizeList) {
            if ((size.width == reqTmpWidth) && (size.height == reqTmpHeight)) {
                return size;
            }
        }
        // 得到与传入的宽高比最接近的size
        float reqRatio = ((float) reqTmpWidth) / reqTmpHeight;
        float curRatio, deltaRatio;
        float deltaRatioMin = Float.MAX_VALUE;
        Camera.Size retSize = null;
        for (Camera.Size size : preSizeList) {
            LogUtil.d("支持的预览分辨率： " + size.width + " * " + size.height);

            curRatio = ((float) size.width) / size.height;
            deltaRatio = Math.abs(reqRatio - curRatio);
            if (deltaRatio < deltaRatioMin) {
                deltaRatioMin = deltaRatio;
                retSize = size;
            }
        }
        return retSize;
    }


}
