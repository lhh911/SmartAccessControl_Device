package com.xsjqzt.module_main.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class DataConversionUtil {
    public static float[] byteArrayToFloatArray(byte[] src) {
        int len = src.length;
        ByteBuffer byteBuffer = ByteBuffer.wrap(src, 0, len);
        FloatBuffer floatBuffer = byteBuffer.order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer();
        float[] floatSamples = new float[len / 4];
        floatBuffer.get(floatSamples, 0, floatSamples.length);
        return floatSamples;
    }

    private static boolean isAction = false;

    public static byte[] floatArrayToByteArray(float[] src) {
        int len = src.length;
        ByteBuffer byteBuffer = ByteBuffer.allocate(src.length * 4).order(ByteOrder.LITTLE_ENDIAN);
        for (Float f : src) {
            byteBuffer.putFloat(f);
        }

        if (!isAction) {
            isAction = true;
            float[] floats = byteArrayToFloatArray(byteBuffer.array());
        }
        return byteBuffer.array();
    }
}
