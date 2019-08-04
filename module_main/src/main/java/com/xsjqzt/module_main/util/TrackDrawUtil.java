package com.xsjqzt.module_main.util;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.util.SimpleArrayMap;
import android.text.TextUtils;
import android.view.SurfaceView;

import com.xsjqzt.module_main.Config.DemoConfig;
import com.xsjqzt.module_main.activity.base.ExApplication;
import com.xsjqzt.module_main.modle.User;

import java.util.List;
import java.util.Map;

import mobile.ReadFace.YMFace;

/**
 * Created by qyg on 2018/10/15.
 */
public class TrackDrawUtil {

    private static Paint paint;
    private static SimpleArrayMap<Integer, FacePoint> trackingMap;
    private static int fontSize = DisplayUtil.dip2px(ExApplication.getContext(), 15);
    private static int rectSize = DisplayUtil.dip2px(ExApplication.getContext(), 4);


    public static void draw() {


    }

    /**
     * 绘制人脸框、关键点、性别&年龄属性
     *
     * @param outputView
     * @param scale_bit
     * @param cameraId
     * @param fps
     * @param showPoint
     */
    public static void drawFaceTracking(List<YMFace> ymFaces, DemoConfig mConfig, SurfaceView outputView, float scale_bit, int cameraId,
                                        String fps, boolean showPoint, Map<Integer, User> userMap) throws Exception {
        Canvas canvas = null;
        try {
            canvas = outputView.getHolder().lockCanvas();
            if (canvas == null) return;
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            if (ymFaces == null || ymFaces.size() < 1) return;
            if (paint == null) {
                paint = new Paint();
                paint.setAntiAlias(true);
            }
            scale_bit = scale_bit * mConfig.screenZoon;
            int viewW = (int) (outputView.getLayoutParams().width * mConfig.screenZoon);
            int viewH = (int) (outputView.getLayoutParams().height * mConfig.screenZoon);
            // android.util.Log.d("wlDebug","viewW = " + viewW + " viewH = " + viewH);
            double zoom = mConfig.screenIrZoon;
            for (YMFace faces : ymFaces) {
                if (faces == null) return;
                //人脸位置坐标
                float[] rect = faces.getRect();
                //xy坐标点
                FacePoint xy = getXY(mConfig, rect, cameraId, scale_bit, viewW, viewH, mConfig.specialCameraLeftRightReverse, mConfig.specialCameraTopDownReverse);
                //人脸框宽度
                xy.mWidth = rect[2] * scale_bit;
                //防止人脸框抖动
                xy = adjustView(xy, faces.getTrackId());

                //绘制人脸关键点
                if (showPoint) {
                    drawPoints(faces.getLandmarks(), canvas, scale_bit, viewW);
                }
                //判断是否识别
                FaceInfo faceInfo = drawRecog(faces, null, userMap);
                //判断活体
                faceInfo = faceLiveness(faces, faceInfo);

                paint.reset();
                paint.setTextSize(fontSize);
                Paint.FontMetrics fontMetrics = paint.getFontMetrics();
                //每一行文字的高度
                float fHeight = fontMetrics.bottom - fontMetrics.top;
                //绘画人脸框
                drawRect(canvas, xy.mX, xy.mY, xy.mWidth, faceInfo, fHeight, viewW, viewH);

                if (mConfig.isDrawIr) {
                    viewW = (int) (viewW * zoom);
                    viewH = (int) (viewH * zoom);
                    rect = faces.getRect(); //人脸位置坐标
                    xy.mWidth = (float) (rect[2] * scale_bit * zoom); //人脸框宽度
                    xy = getXY(mConfig, rect, cameraId, (float) (scale_bit * zoom), viewW, viewH, mConfig.specialCameraLeftRightReverse, mConfig.specialCameraTopDownReverse);
                    drawRect(canvas, xy.mX, xy.mY, xy.mWidth, Color.WHITE);
                }
            }
            //绘制fps
            if (!TextUtils.isEmpty(fps)) {
                drawFps(fps, canvas, viewH);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            outputView.getHolder().unlockCanvasAndPost(canvas);
        }
    }

    public static void drawFace(List<YMFace> ymFaces, DemoConfig mConfig, SurfaceView outputView, float scale_bit, int cameraId,
                                String fps, boolean showPoint) {
        Canvas canvas = null;
        try {
            canvas = outputView.getHolder().lockCanvas();
            if (canvas == null) return;
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            if (ymFaces == null || ymFaces.size() < 1) return;

            if (paint == null) {
                paint = new Paint();
                paint.setAntiAlias(true);
            }
            scale_bit = scale_bit * mConfig.screenZoon;
            int viewW = (int) (outputView.getLayoutParams().width * mConfig.screenZoon);
            int viewH = (int) (outputView.getLayoutParams().height * mConfig.screenZoon);
            for (YMFace faces : ymFaces) {
                if (faces == null) return;
                //人脸位置坐标
                float[] rect = faces.getRect();
                //xy坐标点
                FacePoint xy = getXY(mConfig, rect, cameraId, scale_bit, viewW, viewH, mConfig.specialCameraLeftRightReverse, mConfig.specialCameraTopDownReverse);
                //人脸框宽度
                xy.mWidth = rect[2] * scale_bit;
                //防止人脸框抖动
                xy = adjustView(xy, faces.getTrackId());
                recogByCard(faces, canvas, xy.mX, xy.mY);
                //绘制人脸关键点
                if (showPoint) {
                    drawPoints(faces.getLandmarks(), canvas, scale_bit, viewW);
                }
                //绘画人脸框
                drawRect(canvas, xy.mX, xy.mY, xy.mWidth, Color.WHITE);
            }
            //绘制fps
            if (!TextUtils.isEmpty(fps)) {
                drawFps(fps, canvas, viewH);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            outputView.getHolder().unlockCanvasAndPost(canvas);
        }
    }

    //防止抖动
    public static FacePoint adjustView(FacePoint xy, int trackId) {
        if (trackingMap == null) {
            trackingMap = new SimpleArrayMap<>();
        }
        if (trackingMap.size() > 50) trackingMap.clear();
        FacePoint old = trackingMap.get(trackId);
        if (old == null) {
            trackingMap.put(trackId, xy);
            return xy;
        }
        int num = DisplayUtil.dip2px(ExApplication.getContext(), 8);
        if (old.mX < xy.mX + num && old.mX > xy.mX - num) {
            if (old.mY < xy.mY + num && old.mY > xy.mY - num) {
                xy.mX = old.mX;
                xy.mY = old.mY;
                xy.mWidth = old.mWidth;
            }
        }
        trackingMap.put(trackId, xy);
        return xy;
    }


    private static FacePoint getXY(DemoConfig mConfig, float[] rect, int cameraId, float scale_bit, int viewW, int viewH, boolean isLR, boolean isTD) {
        float x1 = (viewW - rect[0] * scale_bit - rect[2] * scale_bit);
        if (isLR) {//特殊设备，需要额外左右翻转
            if (x1 == viewW - rect[0] * scale_bit - rect[2] * scale_bit) {
                x1 = rect[0] * scale_bit;//后置摄像头翻转
            } else {
                x1 = viewW - rect[0] * scale_bit - rect[2] * scale_bit;
            }
        }
        float y1 = rect[1] * scale_bit;
        if (isTD) {//特殊设备，需要额外上下翻转
            y1 = viewH - rect[1] * scale_bit - rect[3] * scale_bit;
        }
        return new FacePoint(x1, y1);
    }


    /**
     * 绘制人脸关键点
     *
     * @param points
     * @param canvas
     * @param scale_bit
     * @param viewW
     */
    private static void drawPoints(float[] points, Canvas canvas, double scale_bit, float viewW) {
        paint.setColor(Color.rgb(57, 138, 243));
        int size = DisplayUtil.dip2px(ExApplication.getContext(), 2.5f);
        paint.setStrokeWidth(size);
        for (int j = 0; j < points.length / 2; j++) {
            float x = (float) (viewW - points[j * 2] * scale_bit);
            float y = (float) (points[j * 2 + 1] * scale_bit);
            canvas.drawPoint(x, y, paint);
        }
    }

    /**
     * 绘制人脸框
     *
     * @param canvas
     * @param color  绘制颜色
     */
    private static void drawRect(Canvas canvas, float x1, float y1, float rect_width, int color) {
        RectF rectf = new RectF(x1, y1, x1 + rect_width, y1 + rect_width);
        int size = DisplayUtil.dip2px(ExApplication.getContext(), 2);
        paint.setStrokeWidth(size);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        canvas.drawRect(rectf, paint);
    }

    private static void drawRect(Canvas canvas, float x1, float y1, float rect_width, FaceInfo faceInfo, float fontH, float viewW, float viewH) {
        if (faceInfo == null) return;
        //人脸框四角
        paint.setStrokeWidth(rectSize);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        float rw = rect_width / 10;

        Path path = new Path();
        path.moveTo(x1, y1 + rw);
        path.lineTo(x1, y1);
        path.lineTo(x1 + rw, y1);
        path.moveTo(x1 + rect_width - rw, y1);
        path.lineTo(x1 + rect_width, y1);
        path.lineTo(x1 + rect_width, y1 + rw);
        path.moveTo(x1, y1 + rect_width - rw);
        path.lineTo(x1, y1 + rect_width);
        path.lineTo(x1 + rw, y1 + rect_width);
        path.moveTo(x1 + rect_width - rw, y1 + rect_width);
        path.lineTo(x1 + rect_width, y1 + rect_width);
        path.lineTo(x1 + rect_width, y1 + rect_width - rw);
        canvas.drawPath(path, paint);

        //人脸框虚线
        paint.setStrokeWidth(rectSize / 4);
        paint.setPathEffect(new DashPathEffect(new float[]{15, 15, 15, 15}, 1));
        RectF rectf = new RectF(x1, y1, x1 + rect_width, y1 + rect_width);
        canvas.drawRect(rectf, paint);
        int num = 6;

        // 是否绘制人脸虚线框 wlDebug;
        drawFaceInfo(canvas, faceInfo, x1, y1, rect_width, fontH * 10, fontH * num, fontH, viewW, viewH);
    }

    private static void drawFaceInfo(Canvas canvas, FaceInfo faceInfo, float x1, float y1, float rect_width, float w, float h, float fontH, float viewW, float viewH) {
        paint.reset();
        //人物信息卡
        paint.setStrokeWidth(rectSize / 4);
        paint.setPathEffect(new DashPathEffect(new float[]{0, 0, 0, 0}, 1));
        paint.setStyle(Paint.Style.FILL);
        if (faceInfo.isRecog == 1 || faceInfo.isLiveness == 1) {
            paint.setColor(Color.parseColor("#1E90FF"));
        } else {
            paint.setColor(Color.parseColor("#1E90FF"));
        }
        paint.setAlpha(90);
        Path path = new Path();
        float left;
        float right;
        float top;
        float bottom;
        float distance = fontH * 3;
        //top
        left = x1;
        right = left + w;
        top = y1 - h - distance;
        bottom = y1 - distance;
        if (top > distance - fontH) {
            canvas.drawRoundRect(new RectF(left, top, right, bottom), 20, 20, paint);
            canvas.drawCircle(left + (right - left) / 4, bottom + 5 * distance / 6, distance / 6, paint);
            canvas.drawCircle(left + (right - left) / 3, bottom + 1 * distance / 2, distance / 4, paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.WHITE);
            canvas.drawRoundRect(new RectF(left, top, right, bottom), 20, 20, paint);
            canvas.drawCircle(left + (right - left) / 4, bottom + 5 * distance / 6, distance / 6, paint);
            canvas.drawCircle(left + (right - left) / 3, bottom + 1 * distance / 2, distance / 4, paint);
            if (faceInfo.isRecog != -1 || faceInfo.isLiveness != -1) {
                //绘画笑脸
                drawSmile(canvas, top, left, right, bottom, fontH, true);
            } else {
                drawSmile(canvas, top, left, right, bottom, fontH, false);
            }
        } else {
            //left
            left = x1 - distance - w;
            right = left + w;
            top = y1;
            bottom = top + h;
            if (left > distance - fontH) {
                canvas.drawRoundRect(new RectF(left, top, right, bottom), 20, 20, paint);
                canvas.drawCircle(right + distance * 5 / 6, (y1 + rect_width - bottom) * 2 / 3 + bottom, distance / 6, paint);
                canvas.drawCircle(right, (y1 + rect_width - bottom) / 4 + bottom, distance / 4, paint);
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.WHITE);
                canvas.drawRoundRect(new RectF(left, top, right, bottom), 20, 20, paint);
                canvas.drawCircle(right + distance * 5 / 6, (y1 + rect_width - bottom) * 2 / 3 + bottom, distance / 6, paint);
                canvas.drawCircle(right, (y1 + rect_width - bottom) / 4 + bottom, distance / 4, paint);
            } else {
                //right
                left = x1 + rect_width + distance;
                right = left + w;
                top = y1;
                bottom = y1 + h;
                if (viewW - right > distance - fontH) {
                    canvas.drawRoundRect(new RectF(left, top, right, bottom), 20, 20, paint);
                    canvas.drawCircle(left - distance / 6, (y1 + rect_width - bottom) * 2 / 3 + bottom, distance / 6, paint);
                    canvas.drawCircle(left - distance / 2, (y1 + rect_width - bottom) / 4 + bottom, distance / 4, paint);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setColor(Color.WHITE);
                    canvas.drawRoundRect(new RectF(left, top, right, bottom), 20, 20, paint);
                    canvas.drawCircle(left - distance / 6, (y1 + rect_width - bottom) * 2 / 3 + bottom, distance / 6, paint);
                    canvas.drawCircle(left - distance / 2, (y1 + rect_width - bottom) / 4 + bottom, distance / 4, paint);
                } else {
                    //bottom
                    left = x1;
                    right = left + w;
                    top = y1 + rect_width + distance;
                    bottom = top + h;
                    canvas.drawRoundRect(new RectF(left, top, right, bottom), 20, 20, paint);
                    canvas.drawCircle(left + (right - left) / 4, top - distance * 5 / 6, distance / 6, paint);
                    canvas.drawCircle(left + (right - left) / 3, top - distance / 2, distance / 4, paint);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setColor(Color.WHITE);
                    canvas.drawRoundRect(new RectF(left, top, right, bottom), 20, 20, paint);
                    canvas.drawCircle(left + (right - left) / 4, top - distance * 5 / 6, distance / 6, paint);
                    canvas.drawCircle(left + (right - left) / 3, top - distance / 2, distance / 4, paint);
                }
            }
        }

        //人物信息卡边框
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        canvas.drawPath(path, paint);
        //绘画人脸信息
        paint.reset();
        //人物信息内容
        paint.setTextSize(fontSize);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(0);
        paint.setStyle(Paint.Style.FILL);
        String personId = paint.measureText("personId:" + faceInfo.mPersonId) > w ? ("personId:" +
                faceInfo.mPersonId).substring(0, 7) + "..." : "personId:" + faceInfo.mPersonId;
        String name = paint.measureText("姓名:" + faceInfo.mName) > w ? ("姓名:" + faceInfo.mName).substring(0, 7) + "..." : "姓名:" + faceInfo.mName;
        String ageAndmGender = paint.measureText("年龄:" + faceInfo.mAge + "   性别:" + faceInfo.mGender) > w ? ("年龄:"
                + faceInfo.mAge + "   性别:" + faceInfo.mGender).substring(0, 7)
                + "..." : "年龄:" + faceInfo.mAge + "   性别:" + faceInfo.mGender;
        String isRecog = "" + (paint.measureText(faceInfo.recogShow) > w ? (faceInfo.recogShow).substring(0, 7) + "..." : faceInfo.recogShow);
        drawInfoText(canvas, personId, Color.WHITE, left + 20, top + 1 * fontH, paint);
        drawInfoText(canvas, name, Color.WHITE, left + 20, top + 2 * fontH, paint);
        drawInfoText(canvas, ageAndmGender, Color.WHITE, left + 20, top + 3 * fontH, paint);
        if (faceInfo.isRecog == 1) {
            drawInfoText(canvas, isRecog, Color.YELLOW, left + 20, top + 4 * fontH, paint);
        } else if (faceInfo.isRecog == 0) {
            drawInfoText(canvas, isRecog, Color.RED, left + 20, top + 4 * fontH, paint);
        } else {
            drawInfoText(canvas, isRecog, Color.WHITE, left + 20, top + 4 * fontH, paint);
        }
        String isLiveness = "" + (paint.measureText(faceInfo.livenessShow) > w ? (faceInfo.livenessShow).substring(0, 7) + "..." : faceInfo.livenessShow);
        if (faceInfo.isLiveness == 1) {
            drawInfoText(canvas, isLiveness, Color.YELLOW, left + 20, top + 5 * fontH, paint);
        } else if (faceInfo.isLiveness == 0) {
            drawInfoText(canvas, isLiveness, Color.RED, left + 20, top + 5 * fontH, paint);
        } else {
            drawInfoText(canvas, isLiveness, Color.WHITE, left + 20, top + 5 * fontH, paint);
        }
        paint.reset();
    }

    private static void drawSmile(Canvas canvas, float top, float left, float right, float bottom, float fontH, boolean isSmile) {
        paint.setStrokeWidth(rectSize / 4);
        paint.setPathEffect(new DashPathEffect(new float[]{0, 0, 0, 0}, 1));
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);//取消锯齿
        //笑脸半径
        float sR = 2 * fontH;
        if (isSmile) {
            paint.setColor(Color.YELLOW);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(right - sR, top, sR, paint);
            paint.setColor(Color.BLACK);
            canvas.drawCircle(right - 3 * sR / 2, top - sR / 2, sR / 4, paint);
            canvas.drawCircle(right - sR / 2, top - sR / 2, sR / 4, paint);
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(right - sR, top, sR, paint);
            paint.setColor(Color.BLACK);
            canvas.drawArc(new RectF(right - 5 * sR * 2 / 6, top - 2 * sR / 3, right - sR * 2 / 6, top + sR * 2 / 3), 0, 180, false, paint);
        } else {
            paint.setColor(Color.GRAY);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(right - sR, top, sR, paint);
            paint.setColor(Color.WHITE);
            canvas.drawCircle(right - 3 * sR / 2, top - sR / 2, sR / 6, paint);
            canvas.drawCircle(right - sR / 2, top - sR / 2, sR / 6, paint);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(right - sR, top, sR, paint);
        }
        paint.reset();
    }

    private static void drawInfoText(Canvas canvas, String content, int color, float x, float y, Paint paint) {
        paint.setColor(color);
        canvas.drawText(content, x, y, paint);
    }


    /**
     * 绘制人脸已注册的信息
     *
     * @param userMap
     */

    public static FaceInfo drawRecog(YMFace ymFace, FaceInfo faceInfo, Map<Integer, User> userMap) {
        if (faceInfo == null) faceInfo = new FaceInfo();
        if (ymFace == null) return faceInfo;
        int personId = ymFace.getPersonId();

        if (personId == -1) {
            faceInfo.isRecog = -1;
            faceInfo.recogShow = "人脸识别：未开启";
        } else {
            float[] headposes = ymFace.getHeadpose();
            //检测角度
            if ((Math.abs(headposes[0]) > 30 || Math.abs(headposes[1]) > 30 || Math.abs(headposes[2]) > 30)) {
                //角度不佳
                faceInfo.isRecog = 0;
                faceInfo.recogShow = "人脸：角度不佳";
                if (ymFace.getAge() > 0) {
                    faceInfo.mAge = "" + ymFace.getAge();
                }
                if (ymFace.getGender() > 0) {
                    faceInfo.mGender = ymFace.getGender() == 1 ? "男" : ymFace.getGender() == 0 ? "女" : "";
                }
                return faceInfo;
            }
            if (ymFace.getFaceQuality() < 6) {
                //质量不行
                faceInfo.isRecog = 0;
                faceInfo.recogShow = "人脸：质量不佳：" + ymFace.getFaceQuality();
                if (ymFace.getAge() > 0) {
                    faceInfo.mAge = "" + ymFace.getAge();
                }
                if (ymFace.getGender() > 0) {
                    faceInfo.mGender = ymFace.getGender() == 1 ? "男" : ymFace.getGender() == 0 ? "女" : "";
                }
                return faceInfo;
            }
            if (personId > 0 && userMap != null && userMap.containsKey(personId)) {
                User user = userMap.get(personId);
                faceInfo.mName = user.getName();
                faceInfo.mGender = user.getGender();
                faceInfo.mAge = user.getAge();
                faceInfo.mPersonId = "" + personId;
                faceInfo.isRecog = 1;
                faceInfo.recogShow = "人脸识别：已注册";
            } else if (personId == -111) {
                faceInfo.isRecog = 0;
                faceInfo.recogShow = "人脸识别：未注册";
                if (ymFace.getAge() > 0) {
                    faceInfo.mAge = "" + ymFace.getAge();
                }
                if (ymFace.getGender() > 0) {
                    faceInfo.mGender = ymFace.getGender() == 1 ? "男" : ymFace.getGender() == 0 ? "女" : "";
                }
            }
        }
        return faceInfo;
    }

    public static void recogByCard(YMFace ymFace, Canvas canvas, float x1, float y1) {
        if (ymFace == null) return;
        int personId = ymFace.getPersonId();
        //人证识别
        if (personId > 0) {
            StringBuffer sb2 = new StringBuffer();
            if (ymFace.getConfidence() >= 75) {
                sb2.append("人证检测通过: " + ymFace.getConfidence());
                paint.setColor(Color.GREEN);
            } else if (ymFace.getLiveness() < 75) {
                sb2.append("人证检测失败: " + ymFace.getConfidence());
            } else {
                sb2.append("");
            }
            paint.setStrokeWidth(0);
            paint.setStyle(Paint.Style.FILL);
            int fontSize = DisplayUtil.dip2px(ExApplication.getContext(), 20);
            paint.setTextSize(fontSize);
            Rect rect_text = new Rect();
            paint.getTextBounds(sb2.toString(), 0, sb2.toString().length(), rect_text);
            canvas.drawText(sb2.toString(), x1, y1 - 40, paint);
        }
    }

    /**
     * 活体绘制
     *
     * @param faces
     */
    public static FaceInfo faceLiveness(YMFace faces, FaceInfo faceInfo) {
        if (faceInfo == null) faceInfo = new FaceInfo();
        if (faces == null) return faceInfo;
        if (faces.getLiveness() < 0) {
            faceInfo.isLiveness = -1;
            faceInfo.livenessShow = "活体识别：未开启";
        } else {
            float[] headposes = faces.getHeadpose();
            //检测角度
            if ((Math.abs(headposes[0]) > 30 || Math.abs(headposes[1]) > 30 || Math.abs(headposes[2]) > 30)) {
                //角度不佳
                faceInfo.isLiveness = 0;
                faceInfo.livenessShow = "人脸：角度不佳";
                return faceInfo;
            }
            if (faces.getFaceQuality() < 6) {
                //质量不行
                faceInfo.livenessShow = "人脸：质量不佳：" + faces.getFaceQuality();
                faceInfo.isLiveness = 0;
                return faceInfo;
            }
            // 活体检测
            if (faces.getLiveness() == 1) {
                faceInfo.isLiveness = 1;
                faceInfo.livenessShow = "活体识别：通过";
                return faceInfo;
            } else if (faces.getLiveness() == 0) {
                faceInfo.isLiveness = 0;
                faceInfo.livenessShow = "活体识别：未通过";
            }
        }
        return faceInfo;
    }

    /**
     * 绘制fps
     *
     * @param fps
     * @param canvas
     * @param viewH
     */
    private static void drawFps(String fps, Canvas canvas, int viewH) {
        if (paint == null) {
            paint = new Paint();
            paint.setAntiAlias(true);
        }
        paint.setColor(Color.RED);
        paint.setStrokeWidth(0);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText(fps, 20, viewH * 3 / 17, paint);
    }

    /**
     * 判断是否有中文
     */
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否有中文
     */
    private static boolean isChinese(String strName) {
        char[] ch = strName.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }

    public static class FacePoint {
        private float mX;
        private float mY;
        private float mWidth;

        public FacePoint(float x, float y) {
            mX = x;
            mY = y;
        }
    }

    public static class FaceInfo {
        private String mName = "";
        private String mAge = "";
        private String mPersonId = "";
        private String mGender = "";
        private int isRecog = -1;
        private int isLiveness = -1;
        private String livenessShow = "";
        private String recogShow = "";
    }
}
