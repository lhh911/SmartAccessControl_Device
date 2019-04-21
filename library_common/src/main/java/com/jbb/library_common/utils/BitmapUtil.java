package com.jbb.library_common.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtil {

    public static void saveToFile(Context mContext, Bitmap bitmap,File file){
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    public static Bitmap getViewBitmap(View view)  {
        Bitmap bm = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        view.draw(canvas);
        return bm;
    }

}
