package com.xsjqzt.module_main.util;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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
}
