package com.xsjqzt.module_main.faceSdk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.util.SimpleArrayMap;
import android.text.TextUtils;
import android.util.Log;

import com.jbb.library_common.utils.ToastUtil;
import com.jbb.library_common.utils.log.LogUtil;
import com.xsjqzt.module_main.Config.DemoConfig;
import com.xsjqzt.module_main.activity.base.ExApplication;
import com.xsjqzt.module_main.dataSource.DataSource;
import com.xsjqzt.module_main.dataSource.UserDataUtil;
import com.xsjqzt.module_main.greendao.DbManager;
import com.xsjqzt.module_main.greendao.FaceImageDao;
import com.xsjqzt.module_main.greendao.entity.FaceImage;
import com.xsjqzt.module_main.model.user.UserInfoInstance;
import com.xsjqzt.module_main.modle.FaceResult;
import com.xsjqzt.module_main.modle.FaceSuccessEventBean;
import com.xsjqzt.module_main.modle.User;
import com.xsjqzt.module_main.util.DataConversionUtil;
import com.xsjqzt.module_main.util.ThreadPoolManager;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import mobile.ReadFace.YMFace;
import mobile.ReadFace.YMFaceTrack;

public class FaceSet {

    private YMFaceTrack faceTrack = null;
    private Context context;
    private SimpleArrayMap<Integer, YMFace> trackingMap;
    private SimpleArrayMap<Integer, float[]> trackingFeatureMap;
    private final int[] mCode = {0, -3, -6, -5, -4, -1, -2, -11, -12, 1001, 1002, 2101, 2102, 1128, 1106, 1107};
    private final String[] mMsg = {"初始化成功", "包名不匹配", "app_id不匹配", "未读取到激活信息", "激活失败或者网络不正常", "已过期，当前日期比截止日期要大", "已过期，当前日期比打包日期要小", "异常", "句柄初始化失败", "比对文件格式不正确", "校验失败", "激活已满", "密钥不匹配", "网络超时", "DNS解析出错", "服务器异常"};
    private int frame = 0;//累积到10帧，重置为0.

    /**
     * sdk 识别方向
     */
    private int orientation = 0;
    /**
     * 质量检测相关数据
     */
    private SimpleArrayMap<Integer, QualityInfo> qualityMap = new SimpleArrayMap<>();
    /**
     * 识别线程是否正在执行
     */
    private boolean isRecognitionThreadRunning = false;

    public FaceSet(Context c) {
        context = c;
        trackingMap = new SimpleArrayMap<>();
        trackingFeatureMap = new SimpleArrayMap<>();
    }

    /**
     * 开始算法
     *
     * @return
     */
    public FaceResult startTrack(int orientation) {
        FaceResult result = new FaceResult();
        if (faceTrack != null) {
            result.code = -111;
            result.msg = "faceTrack 已经初始化";
            return result;
        }
        faceTrack = new YMFaceTrack();
        faceTrack.setDistanceType(YMFaceTrack.DISTANCE_TYPE_FARTHESTER);        //设置人脸检测距离，默认近距离，需要在initTrack之前调用
        //license激活版本初始化
//        int result = faceTrack.initTrack(this, YMFaceTrack.FACE_0, YMFaceTrack.RESIZE_WIDTH_640,SenseConfig.appid, SenseConfig.appsecret);
        //普通有效期版本初始化
        File file = new File(DemoConfig.UserDatabasePath);
        if (!file.exists()) {
            file.mkdirs();
        }
//        String licContent = "################################################################\n" +
//                "# License Product: FaceSDK\n" +
//                "# Expiration: 20180808 ~ 20291010\n" +
//                "# AppId: 4902325e73a37e1f9ea8d57975d3030c\n" +
//                "################################################################\n" +
//                "p/HZKoLI2P5DTZhVjOKzRxcEZkd2aRsbEh72B3f2FxNSaqNVvcyzUZ3FE2tnBsyq\n" +
//                "xz21usKUZRq0yIOkp+AXdORLRv5D1QD56BvQYPqH9C7J6RfYFNQtTouF+ZFBRUxc\n" +
//                "xRv+2jq6oVgTtmwyjnWlbV58Y9ewACsYBVRcI1wCUlIx7WZ8cEkt4Bt8nJdnAfZ0\n" +
//                "CUk9ZJB3ewb3f5QMfYF+VDHqbanWTypP0ylXV1VwogSnmUb+Q9UA+egb0GD6h/Qu\n" +
//                "yelG/kPVAPnoG9Bg+of0Lsnp8v9bWD+qZKdFFT34aMKQKB0qELSrsZphUApiaz4F\n" +
//                "rqBG/kPVAPnoG9Bg+of0LsnpRv5D1QD56BvQYPqH9C7J6UNyuCEUIazfYxVhNZ/Y\n" +
//                "MkSCQWGiyweKP0Yq2ZRRgLW5Rv5D1QD56BvQYPqH9C7J6Ub+Q9UA+egb0GD6h/Qu\n" +
//                "yek+vsUVYHnJHv4rD8GbxpJSxRcWcFnpAmU0BoKOlEpqvmoIEPHWByglxchr8PPM\n" +
//                "HltG/kPVAPnoG9Bg+of0LsnpRv5D1QD56BvQYPqH9C7J6Q==\n" +
//                "################################################################";
//        final String deviceContent = faceTrack.getDeviceContent(context, licContent);
//        String authContent = "KSO+hOFs1q5SkEnx8bvp67MMcTnYNjfrh9wov2i8xDe65pPS1Kka8GNZchSTHIGZi9XRru8PDBKuPYaWOkr5f1N2LvYkG4yuoLIl9XnnuEhWcoDOxN5A87FuhcjSXPINC+lwx3b+EbZ36iFdE2KK06OALHphS9jxOfZ2bMBFRhyymS72JBuMrqCyJfV557hIVnI0Lb7tcBI/YF9EdB00u+3bq8bWqUmVlbO0Fq3+nncEVqLe58XlN21jQ0POACgHmgLzCeNTHdsi+wIIozL7VPorXRIB6weEtCHOJTg2MMbsgHg1biXIIcyL6IQoOL12aWMvyt5bNjJpX41squGOgxocjp1/iy+Rn82EQj6k/0oKQe4jdxgNkE95Iv2744yhHXIu9iQbjK6gsiX1eee4SFZyLvYkG4yuoLIl9XnnuEhWci72JBuMrqCyJfV557hIVnIu9iQbjK6gsiX1eee4SFZyLvYkG4yuoLIl9XnnuEhWci72JBuMrqCyJfV557hIVnIu9iQbjK6gsiX1eee4SFZyLvYkG4yuoLIl9XnnuEhWci72JBuMrqCyJfV557hIVnIu9iQbjK6gsiX1eee4SFZyLvYkG4yuoLIl9XnnuEhWci72JBuMrqCyJfV557hIVnIu9iQbjK6gsiX1eee4SFZyLvYkG4yuoLIl9XnnuEhWci72JBuMrqCyJfV557hIVnIu9iQbjK6gsiX1eee4SFZyLvYkG4yuoLIl9XnnuEhWci72JBuMrqCyJfV557hIVnIu9iQbjK6gsiX1eee4SFZyLvYkG4yuoLIl9XnnuEhWci72JBuMrqCyJfV557hIVnIu9iQbjK6gsiX1eee4SFZyLvYkG4yuoLIl9XnnuEhWcleLLuqFZPetZyQ149+uq8ou9iQbjK6gsiX1eee4SFZyLvYkG4yuoLIl9XnnuEhWci72JBuMrqCyJfV557hIVnLwZmgBl/yBhEaHsXjOFMLXLvYkG4yuoLIl9XnnuEhWci72JBuMrqCyJfV557hIVnIu9iQbjK6gsiX1eee4SFZye1NJLgkRWdCTs+qymtbXW+SJi2PNYTwW9+eSjYZOGT7T5pDKnY1X8DoWGTMWxs4ELvYkG4yuoLIl9XnnuEhWci72JBuMrqCyJfV557hIVnIu9iQbjK6gsiX1eee4SFZyLvYkG4yuoLIl9XnnuEhWci72JBuMrqCyJfV557hIVnIu9iQbjK6gsiX1eee4SFZyLvYkG4yuoLIl9XnnuEhWci72JBuMrqCyJfV557hIVnIu9iQbjK6gsiX1eee4SFZyLvYkG4yuoLIl9XnnuEhWci72JBuMrqCyJfV557hIVnIu9iQbjK6gsiX1eee4SFZyLvYkG4yuoLIl9XnnuEhWci72JBuMrqCyJfV557hIVnIu9iQbjK6gsiX1eee4SFZyLvYkG4yuoLIl9XnnuEhWci72JBuMrqCyJfV557hIVnIu9iQbjK6gsiX1eee4SFZyLvYkG4yuoLIl9XnnuEhWci72JBuMrqCyJfV557hIVnIu9iQbjK6gsiX1eee4SFZyLvYkG4yuoLIl9XnnuEhWci72JBuMrqCyJfV557hIVnIu9iQbjK6gsiX1eee4SFZyLvYkG4yuoLIl9XnnuEhWci72JBuMrqCyJfV557hIVnIu9iQbjK6gsiX1eee4SFZyLvYkG4yuoLIl9XnnuEhWci72JBuMrqCyJfV557hIVnIu9iQbjK6gsiX1eee4SFZy";
//        DLog.d("faceTrack.initTrackAuth - "+deviceContent);
//        final int i1 = faceTrack.initTrackAuth(context, YMFaceTrack.FACE_0, YMFaceTrack.RESIZE_WIDTH_640, licContent, authContent, DemoConfig.UserDatabasePath);
//        DLog.d("faceTrack.initTrackAuth - "+i1);


        // result.code = faceTrack.initTrack(context, YMFaceTrack.FACE_0, YMFaceTrack.RESIZE_WIDTH_640, DemoConfig.UserDatabasePath);
        result.code = faceTrack.initTrack(context, YMFaceTrack.FACE_0, YMFaceTrack.RESIZE_WIDTH_640,
                "398de72afa668c3565f7e8ab5d2af752", "4fd4aa712c7ff4dc654dfdb655d35201949c4c97");
//        faceTrack.setPersonName(1, "窦红斌");
//        final String personName = faceTrack.getPersonName(1);
//        DLog.d("personName: "+personName);

        this.orientation = orientation;
        faceTrack.setOrientation(orientation);
        for (int i = 0; i < mCode.length; i++) {
            if (result.code == mCode[i]) {
                result.msg = mMsg[i];
                return result;
            }
        }

        result.msg = "";
        return result;
    }

    /**
     * 停止算法
     */
    public void stopTrack() {
        if (faceTrack == null) {
            return;
        }
        faceTrack.onRelease();
        faceTrack = null;
    }

    public String getSdkVersion() {
        if (faceTrack == null) {
            return "";
        }
        return faceTrack.getVersion();
    }

    public float[] getFaceFeature(Bitmap bitmap) {
        if (bitmap == null) return null;
        final List<YMFace> ymFaces = faceTrack.detectMultiBitmap(bitmap);
        if (ymFaces == null || ymFaces.size() < 0) return null;
        float[] faceFeature = faceTrack.getFaceFeature(0);
        return faceFeature;
    }

    public float[] getFaceFeatureCard(Bitmap bitmap) {
        if (bitmap == null) return null;
        final List<YMFace> ymFaces = faceTrack.detectMultiBitmap(bitmap);
        if (ymFaces == null || ymFaces.size() < 0) return null;
        float[] faceFeature = faceTrack.getFaceFeatureCard(0);
        return faceFeature;
    }

    public int compareFaceFeature(float[] faceFeature, float[] faceFeature2) {
        if (faceTrack == null) return -1;
        return faceTrack.compareFaceFeature(faceFeature, faceFeature2);
    }


    public YMFace getFaceAttribute(Bitmap bitmap) {
//        if (bitmap == null) return null;
//        List<YMFace> ymFaces = faceTrack.detectMultiBitmap(bitmap);
//        if (ymFaces == null || ymFaces.size() < 0) return null;
//        int result = faceTrack.getFaceAttribute(0, ymFaces);
//        if (result == 0) {
//            YMFace ymFace = ymFaces.get(0);
//            //获取性别
//            ymFace.setGender(faceTrack.getGender(0));
//            ymFace.setGenderConfidence(faceTrack.getGenderConfidence(0));
//            ymFace.setAge(faceTrack.getAge(0));
//            return ymFace;
//        }
        return null;
    }

    /**
     * 图片注册
     *
     * @param bitmap
     * @return
     */
    public FaceResult registByBitmap(Bitmap bitmap, String name) {
        faceTrack.setOrientation(0);
        FaceResult result = new FaceResult();
        result.code = 0;
        result.personId = -1;
        final List<YMFace> ymFaces = faceTrack.detectMultiBitmap(bitmap);
        if (ymFaces == null || ymFaces.size() < 1) {
            result.code = 100;
            result.msg = "";
            return result;
        }
        int FaceQuality = faceTrack.getFaceQuality(0);
        if (FaceQuality < 6) {
            result.code = 101;
            result.msg = "图片质量小于6";
        }
        result.personId = faceTrack.identifyPerson(0);
        result.rect = ymFaces.get(0).getRect();
        if (result.personId > 0) {
            int faceConfidence = faceTrack.getRecognitionConfidence();
            if (faceConfidence > 75) {
                result.code = 102;
                result.msg = "用户(" + result.personId + ")已注册 ,相似对为" + faceConfidence;
            }
        } else {
//            User user = getUser(0);
//            user.setName(name);
            result.personId = insertPerson(null, 0, bitmap, result.rect);
            if (result.personId > 0) {
                result.code = 0;
                result.msg = "成功注册人脸";
            } else {
                result.code = 200;
                result.msg = "注册人脸失败";
            }
        }
        return result;
    }

    //异步注册
    public FaceResult getFaceFeatureFromBitmap(Bitmap bitmap) {
        FaceResult result = new FaceResult();
        result.code = 0;
        result.personId = -1;

        float[] rect = new float[4];
        float[] faceFeature = faceTrack.getFaceFeatureFromBitmapNss(bitmap, rect);
        if (faceFeature == null) {//
            result.code = 200;
            result.msg = "注册人脸失败";
            return result;
        }

        result.personId = faceTrack.identifyPerson(faceFeature);
        if (result.personId < 0) {
            //未注册
            result.personId = faceTrack.addPerson(faceFeature);
            if (result.personId > 0) {
                //注册成功
                result.code = 0;
                result.msg = "成功注册人脸";
                result.rect = faceFeature;
            } else {
                result.code = 200;
                result.msg = "注册人脸失败";
            }
        } else {
            //已注册
            int faceConfidence = faceTrack.getRecognitionConfidence();
            result.code = 102;
            result.msg = "用户(" + result.personId + ")已注册 ,相似对为" + faceConfidence;
            result.rect = faceFeature;
        }
        return result;
    }

    //通过唯一识别码注册阅面
    public FaceResult registByfaceFeature(float[] faceFeature) {
        FaceResult result = new FaceResult();
        result.code = 0;
        result.personId = -1;

        if (faceFeature == null) {//
            result.code = 200;
            result.msg = "注册人脸失败";
            return result;
        }

        result.personId = faceTrack.identifyPerson(faceFeature);
        if (result.personId < 0) {
            //未注册
            result.personId = faceTrack.addPerson(faceFeature);
            if (result.personId > 0) {
                //注册成功
                result.code = 0;
                result.msg = "成功注册人脸";
            } else {
                result.code = 200;
                result.msg = "注册人脸失败";
            }
        } else {
            //已注册
            int faceConfidence = faceTrack.getRecognitionConfidence();
            result.code = 102;
            result.msg = "用户(" + result.personId + ")已注册 ,相似对为" + faceConfidence;
        }
        return result;
    }

    public int getPersonCount() {
        if (faceTrack == null) return -1;
        if (0 == UserDataUtil.getUserCount()) {
            if (faceTrack.getAlbumSize() > 0) faceTrack.resetAlbum();
        }
        return UserDataUtil.getUserCount();
    }


    /**
     * 清除人脸库
     */
    public boolean removeAllUser() {
        if (faceTrack == null) return false;
        if (faceTrack.resetAlbum() == 0) {
//            UserDataUtil.clearDb();
//            DataSource dataSource = new DataSource(ExApplication.getContext());
//            dataSource.deleteAllUser();
            return true;
        }
        return false;
    }

    public boolean removeUserByPersonId(int personId) {
        if (faceTrack == null) return false;
        if (faceTrack.deletePerson(personId) == 0) {
            UserDataUtil.deleteByPersonId(personId + "");
            return true;
        }
        return false;
    }


    public int getUserSize() {
        if (faceTrack == null) return 0;
        return faceTrack.getAlbumSize();
    }

    public float[] getFaceFeatureByBitmap(Bitmap bitmap) {
        if (faceTrack == null) return null;
        return faceTrack.getFaceFeatureFromBitmapNss(bitmap, new float[4]);
    }

    /**
     * @param index
     * @return
     */
    public FaceResult registByCam(String name, int index, float[] rect, Bitmap bitmap) {
        FaceResult result = new FaceResult();
        result.code = 0;
        result.personId = -1;
        result.personId = faceTrack.identifyPerson(index);
        if (result.personId > 0) {
            faceTrack.deletePerson(result.personId);
        }
//        User user = getUser(0);
//        user.setName(name);
        result.personId = insertPerson(null, index, bitmap, rect);
        if (result.personId > index) {
            result.code = 0;
            result.msg = "成功注册人脸";
        } else {
            result.code = 200;
            result.msg = "注册人脸失败";
        }
        return result;
    }

    public int insertPerson(User user, int index, Bitmap head, float[] rect) {
        if (faceTrack == null) return -1;
        int personId = faceTrack.addPerson(index);

        if (personId > 0) {
            return personId;
//            user.setPersonId(personId + "");
//            if (UserDataUtil.addDataSource(head, user, rect, 0)) {
//                return personId;
//            } else {
//                faceTrack.deletePerson(personId);
//                return -1;
//            }
        }
        return -1;
    }


    public boolean deleteUserByPersonId(int personId) {
        if (faceTrack == null) return false;
        if (faceTrack.deletePerson(personId) == 0) {
//            UserDataUtil.deleteByPersonId(String.valueOf(personId));
            return true;
        }
        return false;
    }


    /**
     * 获取属性
     *
     * @param
     * @return
     */
//    public User getUser(int index) {
//        User user = new User();
//        float[] faceFeature = faceTrack.getFaceFeature(index);
//        int gender = faceTrack.getGender(index);
//        int gender_confidence = faceTrack.getGenderConfidence(index);
//        if (gender_confidence >= 90)
//            user.setGender(gender == 0 ? "F" : "M");
//        else {
//            user.setGender("");
//        }
//        user.setScore(faceTrack.getHappyScore(index) + "");
//        user.setAge(faceTrack.getAge(index) + "");
//        user.setFaceFeature(floatToString(faceFeature));
//        return user;
//    }

    //float[]转换String
    public String floatToString(float[] var1) {
        String str = null;
        try {
            char[] chars = faceTrack.convertFeatureToHalf(var1);
            str = String.valueOf(chars);
        } catch (Exception e) {

        }
        return str;
    }

    //String转换float[]
    public float[] stringToFloat(String str) {
        float[] feature = null;
        try {
            feature = faceTrack.convertFeatureToFloat(str.toCharArray());
        } catch (Exception e) {

        }
        return feature;
    }

    //float[]转换String
    public char[] floatToChar(float[] var1) {
        char[] chars = null;
        try {
            chars = faceTrack.convertFeatureToHalf(var1);
        } catch (Exception e) {

        }
        return chars;
    }

    //String转换float[]
    public float[] charToFloat(char[] str) {
        float[] feature = null;
        try {
            feature = faceTrack.convertFeatureToFloat(str);
        } catch (Exception e) {

        }
        return feature;
    }

    /**
     * 校验人脸是否处于正脸方向
     *
     * @return
     */
    public Boolean checkFaceDirection(YMFace face) {
        float facialOri[] = face.getHeadpose();
        float x = facialOri[0];
        float y = facialOri[1];
        float z = facialOri[2];
        if (Math.abs(x) <= 15 && Math.abs(y) <= 15 && Math.abs(z) <= 15)
            return true;
        return false;
    }


    /**
     * 设置sdk识别方向
     *
     * @param orientation
     */
    public void setOrientation(int orientation) {
        faceTrack.setOrientation(orientation);
    }

    /**
     * 是否注册
     *
     * @param index
     * @return
     */
    public int isRegist(int index) {
        return faceTrack.identifyPerson(index);
    }

    public int isRegist(Bitmap bitmap) {
        final List<YMFace> ymFaces = faceTrack.detectMultiBitmap(bitmap);
        return faceTrack.identifyPerson(0);
    }

    /**
     * 人证识别
     *
     * @param bytes
     * @param iw
     * @param ih
     * @return
     */
    public YMFace faceRecognitionByCard(byte[] bytes, int iw, int ih, float[] faceFeature) {
        if (null == faceTrack) {
            return null;
        }
        if (trackingMap.size() > 50) {
            trackingMap.clear();
        }
        YMFace ymFace = null;
        final List<YMFace> ymFaces = faceTrack.trackMulti(bytes, iw, ih);
        if (ymFaces == null || ymFaces.size() < 1) return null;
        int maxIndex = 0;
        //获取最大人脸的下标
        for (int i = 1; i < ymFaces.size(); i++) {
            if (ymFaces.get(maxIndex).getRect()[2] <= ymFaces.get(i).getRect()[2]) {
                maxIndex = i;
            }
        }
        ymFace = ymFaces.get(maxIndex);
        if (faceFeature == null) return ymFace;
        int trackId = ymFace.getTrackId();
        if (ymFace != null) {
            if (!trackingMap.containsKey(trackId)) {
                float[] floats = faceTrack.getFaceFeature(maxIndex);
                int mresult = faceTrack.compareFaceFeature(faceFeature, floats);
                ymFace.setIdentifiedPerson(1, mresult);
            } else if (trackingMap.get(trackId).getPersonId() > 0) {
                ymFace.setIdentifiedPerson(1, trackingMap.get(trackId).getConfidence());
            }
            trackingMap.put(trackId, ymFace);
        }

        return ymFace;
    }

    /**
     * 活体识别/人脸识别/人脸检测
     *
     * @param bytes
     * @param irBytes
     * @param iw
     * @param ih
     * @param isMulti
     * @param isRecog
     * @param livenessType
     * @return
     */
    public List<YMFace> logic(byte[] bytes, byte[] irBytes, final int iw, final int ih,
                              boolean isMulti, boolean isTrack, boolean isRecog, int livenessType) {
        if (null == faceTrack || bytes == null)
            return null;
        //当追踪开启时，不做人脸识别与活体检测
        if (isTrack) {
            //获取人脸集合
            final List<YMFace> ymFaces = faceTrack.trackMulti(bytes, iw, ih);
            return ymFaces;
        }
        if (!isRecog && (livenessType < 0 || livenessType > 2))
            return null;
        //获取人脸集合
        final List<YMFace> ymFaces = faceTrack.trackMulti(bytes, iw, ih);
        if (ymFaces == null || ymFaces.size() < 1) return null;
        if (trackingMap.size() > 50) {
            trackingMap.clear();
        }
        //判断是否为单人识别还是多人识别
        return isMulti ? multiTrack(ymFaces, isRecog, livenessType, irBytes, iw, ih) : singleTrack(ymFaces, isRecog, livenessType, irBytes, iw, ih);
    }


    /** ================================ logic2 ================================== */


    /**
     * 相比 {  logic(byte[], byte[], int, int, boolean, boolean, boolean, int, boolean)}，
     * 本方法调整了逻辑，优化了识别准确率，识别速度，优化的点如下
     * <p>
     * 1. 使用了质量优选的功能，用户无需处理人脸质量/角度/瞳距的逻辑，质量较佳的帧被传入算法，
     * 提升识别准确率，关键方法 faceQualityPick()
     * 2. 执行识别和活体的子线程，线程互斥，同一时刻最多只有一个线程在执行识别和活体，
     * 整体识别速度提升，通过 boolean 变量 {@link #isRecognitionThreadRunning} 实现互斥
     */
    public List<YMFace> logic2(final byte[] bytes, final byte[] irBytes, final int iw, final int ih,
                               boolean isMulti, boolean isTrack, final boolean isRecog, final int livenessType, boolean isSaveImage) {

        if (null == faceTrack || bytes == null) return null;
        if (trackingMap.size() > 50) trackingMap.clear();
        // 由于跟踪算法的特性，关闭算法后必须清空
        // 跟踪算法的特性，关闭算法后，在人脸相同的位置换了其他人脸，再打开算法，依然会被跟踪为同一个tid
        if ((!isRecog) && (livenessType == -1) && (trackingMap.size() != 0)) trackingMap.clear();

        if (!isTrack && !isRecog && livenessType == -1) return null;

        // 跟踪检测人脸
        final List<YMFace> ymFaces = faceTrack.trackMulti(bytes, iw, ih);
        if (ymFaces == null || ymFaces.size() == 0) return null;
        // 是否多人脸
        if (!isMulti) {
            int maxIndex = getMaxFace(ymFaces);
            YMFace face = ymFaces.get(maxIndex);
            ymFaces.clear();
            ymFaces.add(face);
        }
        if (isTrack) return ymFaces;


        // 质量/活体/识别
        // 执行识别和活体的子线程，线程互斥
        if (!isRecognitionThreadRunning) {
            isRecognitionThreadRunning = true;
            // 创建新的ArrayList 给子线程调用，否则会出现问题，原因未明
            final List<float[]> headPosesList = getAllHeadPose(ymFaces);
//            ThreadPoolManager.getInstance().execute(() -> {
            ThreadPoolManager.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 创建新的ArrayList 给子线程调用，否则会出现问题，原因未明
                        for (int i = 0; i < headPosesList.size(); i++) {
                            ymFaces.get(i).setHeadPose(headPosesList.get(i));
                        }

                        qualityMap = faceQualityPick(bytes, irBytes, iw, ih, ymFaces);

                        for (int i = 0; i < ymFaces.size(); i++) {

                            YMFace ymFace = ymFaces.get(i);
                            int trackId = ymFace.getTrackId();

                            // 获取人脸质量检测结果
                            QualityInfo qualityInfo = qualityMap.get(trackId);
                            if (qualityInfo == null)
                                continue;


                            // 识别和活体
//                            String str = "tid===" + trackId + "   isRecognition  " + qualityInfo.isRecognition;
                            if (qualityInfo.isRecognition) {

                                // 可识别的帧数据
                                byte[] qualityInfoBytes = qualityInfo.bytes;
                                byte[] qualityInfoIrBytes = qualityInfo.irBytes;
                                // 可识别的人脸坐标
                                float[] qualityInfoRect = qualityInfo.rect;

                                // 识别
                                if (isRecog) {
                                    YMFace face = trackingMap.get(trackId);
                                    if (face == null || face.getPersonId() <= 0) {

                                        //识别到的人脸对应的id
                                        int personId = faceTrack.identifyPerson(
                                                qualityInfoBytes, iw, ih, qualityInfoRect);
                                        // 获取相似度
                                        int confidence = faceTrack.getRecognitionConfidence();
                                        ymFace.setIdentifiedPerson(personId, confidence);
//                                        str += " identifyPerson:" + personId + " " + confidence;

                                        // 识别通过则抓拍
//                                    if (isSaveImage && personId > 0)
//                                        saveRecognitionFrame(bytes, ymFace, iw, ih, true);
                                    } else {
                                        ymFace.setIdentifiedPerson(face.getPersonId(), face.getConfidence());
                                    }
                                } else {
                                    ymFace.setIdentifiedPerson(-1, 0);
                                }

                                // 活体
                                switch (livenessType) {
                                    case 0:
                                        // 双目活体
                                        if (qualityInfoIrBytes == null)
                                            break;
                                        int result = faceTrack.livenessDetectFrame(
                                                qualityInfoIrBytes, iw, ih, qualityInfoRect);
                                        ymFace.setLiveness(result);
                                        break;
                                    case 1:
                                        // 可见光 活体
                                        int[] resultDetect = faceTrack.livenessDetect(
                                                qualityInfoBytes, iw, ih, qualityInfoRect);
                                        ymFace.setLiveness(resultDetect[0]);
                                        break;
                                    case 2: //红外活体
                                        int DetectInfrared[] = faceTrack.livenessDetectInfrared(i);
                                        ymFace.setLiveness(DetectInfrared[0]);
                                        break;
                                    default:
                                        ymFace.setLiveness(-1);
                                        Log.e("logic2", "liveness type not support");
                                        break;
                                }


                                android.util.Log.d("wlDebug", "personId= " + ymFace.getPersonId() +  " |  getLiveness= " + ymFace.getLiveness());
//                                toast("personId= " + ymFace.getPersonId() +  " |  getLiveness= " + ymFace.getLiveness());

                                if(!UserInfoInstance.getInstance().isIrClose()) {
                                    if (ymFace.getPersonId() >= 0 && ymFace.getLiveness() == 1) {
                                        int user_id = 0;
                                        FaceImage unique = DbManager.getInstance().getDaoSession().getFaceImageDao().queryBuilder()
                                                .where(FaceImageDao.Properties.PersonId.eq(ymFace.getPersonId())).unique();
                                        if (unique != null) {
                                            user_id = unique.getUser_id();
                                        }
                                        LogUtil.w("user_id" + user_id);
                                        EventBus.getDefault().post(new FaceSuccessEventBean(user_id, "", true));
                                    }
                                }else{
                                    if (ymFace.getPersonId() >= 0 ) {
                                        int user_id = 0;
                                        FaceImage unique = DbManager.getInstance().getDaoSession().getFaceImageDao().queryBuilder()
                                                .where(FaceImageDao.Properties.PersonId.eq(ymFace.getPersonId())).unique();
                                        if (unique != null) {
                                            user_id = unique.getUser_id();
                                        }
                                        LogUtil.w("user_id" + user_id);
                                        EventBus.getDefault().post(new FaceSuccessEventBean(user_id, "", true));
                                    }
                                }

                                // 提取了数据后，要重置 qualityInfo
                                qualityInfo.reset();
                            } else {
                                YMFace face = trackingMap.get(trackId);
                                if (face != null) {
                                    ymFace.setIdentifiedPerson(face.getPersonId(), face.getConfidence());
                                    ymFace.setLiveness(face.getLiveness());
                                }
                            }

//                            Log.e("logic2", str);
                            //将识别与活体结果保存到map中
                            trackingMap.put(trackId, ymFace);
                        }
                    } catch (
                            Exception e) {
                        e.printStackTrace();
                    } finally {
                        isRecognitionThreadRunning = false;
                    }

                }
            });
        }

        // 复制识别结果
        for (int i = 0; i < ymFaces.size(); i++) {
            final YMFace ymFace = ymFaces.get(i);
            final int trackId = ymFace.getTrackId();
            if (trackingMap.containsKey(trackId)) {
                final YMFace face = trackingMap.get(trackId);
                ymFace.setHeadPose(face.getHeadpose());//人脸角度
                //ymFace.setFaceQuality(face.getFaceQuality());//人脸质量
                ymFace.setLiveness(face.getLiveness()); //活体结果
                ymFace.setHelmet(face.getHelmet());// 安全帽
                ymFace.setIdentifiedPerson(face.getPersonId(), face.getConfidence()); //识别结果
            }
        }

        return ymFaces;
    }


    /**
     * 获取最大人脸的下标
     */
    public int getMaxFace(List<YMFace> ymFaces) {
        if (ymFaces == null || ymFaces.size() < 1) return -1;
        int maxIndex = 0;
        for (int i = 1; i < ymFaces.size(); i++) {
            if (ymFaces.get(maxIndex).getRect()[2] <= ymFaces.get(i).getRect()[2]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }


    private List<float[]> getAllHeadPose(List<YMFace> ymFaces) {
        List<float[]> headPoses = new ArrayList<>();
        for (YMFace ymFace : ymFaces) {
            headPoses.add(ymFace.getHeadpose());
        }
        return headPoses;
    }


    /**
     * 人脸质量优选信息
     */
    class QualityInfo {
        int trackId;
        /**
         * 累计最高人脸质量的那一帧的帧数据
         */
        byte[] bytes = null;
        /**
         * 累计最高人脸质量的那一帧的ir帧数据
         */
        public byte[] irBytes = null;
        /**
         * 累计最高的人脸质量
         */
        int faceQuality = -1;
        /**
         * 低质量的次数
         */
        int lowQualityTime = 0;
        /**
         * 是否可以进行识别和活体
         */
        boolean isRecognition = false;
        /**
         * 累计最高人脸质量的那一帧的人脸坐标
         */
        float[] rect;
        /**
         * 人脸三维姿态
         */
        float[] headPose;

        /**
         * 重置信息，提取信息用于识别之后要重置信息
         */
        void reset() {
            bytes = null;
            faceQuality = -1;
            lowQualityTime = 0;
            isRecognition = false;
            rect = null;
        }
    }


    /**
     * 人脸角度/质量优选
     */
    private SimpleArrayMap<Integer, QualityInfo> faceQualityPick(byte[] bytes, byte[] irBytes, int iw, int ih, List<YMFace> faces) {
        if (faces == null || faces.size() == 0) return null;
        if (bytes == null) return null;
        if (qualityMap.size() > 50) qualityMap.clear();

        /** 人脸质量阈值 */
        final int QUALITY_CONFIDENCE = 6;
        /** 优选质量最大帧数 */
        final int MAX_FRAME_INTERVAL = 3;
        final int BORDER_LIMIT = 50;

        for (int i = 0; i < faces.size(); i++) {

            YMFace face = faces.get(i);
            int tid = face.getTrackId();

            // 角度检测
            float[] headPose = face.getHeadpose();
            if ((Math.abs(headPose[0]) > 30 || Math.abs(headPose[1]) > 30 || Math.abs(headPose[2]) > 30)) {
                Log.e("faceQualityPick", "角度不佳");
                continue;
            }

            // 边缘检测
            float[] rect = face.getRect();
            if (orientation == 90 || orientation == 270) {
                int temp = iw;
                iw = ih;
                ih = temp;
            }
            if (rect[0] < BORDER_LIMIT || rect[0] + rect[2] > iw - BORDER_LIMIT ||
                    rect[1] < BORDER_LIMIT || rect[1] + rect[3] > ih - BORDER_LIMIT) {
                Log.e("faceQualityPick", "人脸位于边缘");
                //Log.e("faceQualityPick", "人脸位于边缘(" + iw + ", " + ih + ")" +
                //        "(" + rect[0] + ", " + rect[1] + ", " + rect[2] + ", " + rect[3] + ")");
                continue;
            }

            // 添加新的 qualityInfo 进入 qualityMap，或者从 qualityMap 找出对应 tid 的 qualityInfo
            QualityInfo qualityInfo;
            if (qualityMap.containsKey(tid)) {
                qualityInfo = qualityMap.get(tid);
            } else {
                qualityInfo = new QualityInfo();
                qualityInfo.trackId = tid;
                qualityMap.put(tid, qualityInfo);
            }


            String str = "";
            // 获取人脸指令
            float[] landmarks = face.getLandmarks();
            int faceQuality = faceTrack.getFaceQuality(bytes, iw, ih, landmarks);
            str += "tid===" + tid + "   quality" + faceQuality;
            if (faceQuality > qualityInfo.faceQuality) {

                qualityInfo.faceQuality = faceQuality;

                qualityInfo.bytes = new byte[bytes.length];
                System.arraycopy(bytes, 0, qualityInfo.bytes, 0, bytes.length);
                if (irBytes != null) {
                    qualityInfo.irBytes = new byte[irBytes.length];
                    System.arraycopy(irBytes, 0, qualityInfo.irBytes, 0, irBytes.length);
                }
                float[] rect2 = face.getRect();
                qualityInfo.rect = new float[]{rect2[0], rect2[1], rect2[2], rect2[3]};
                qualityInfo.headPose = new float[]{headPose[0], headPose[1], headPose[2]};
                str += "  put in list";
            }

            // 质量不通过，则累计次数
            if (faceQuality < QUALITY_CONFIDENCE) {
                qualityInfo.lowQualityTime++;
                if (qualityInfo.lowQualityTime >= MAX_FRAME_INTERVAL)
                    if (!qualityInfo.isRecognition) qualityInfo.isRecognition = true;
            }
            // 质量通过，则设置可识别
            else {
                qualityInfo.isRecognition = true;
            }
            str += "  " + qualityInfo.isRecognition;
            //Log.e("faceQualityPick", str);
        }

        return qualityMap;
    }


    /** ================================ logic2 ================================== */


    /**
     * 单人脸追踪/识别/活体检测
     *
     * @param ymFaces
     * @param mRecog
     * @param type
     * @param mIrBytes
     * @param iw
     * @param ih
     * @return
     */
    private List<YMFace> singleTrack(final List<YMFace> ymFaces, final boolean mRecog,
                                     final int type, final byte[] mIrBytes, final int iw, final int ih) {
        int maxIndex = 0;
        for (int i = 1; i < ymFaces.size(); i++) {
            if (ymFaces.get(maxIndex).getRect()[2] <= ymFaces.get(i).getRect()[2]) {
                maxIndex = i;
            }
        }
        final float[] headposes = ymFaces.get(maxIndex).getHeadpose();
        //检测人脸角度与人脸质量
        final int finalMaxIndex = maxIndex;
        if (frame > 10) {
            frame = 0;
            ThreadPoolManager.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    YMFace ymFace = ymFaces.get(finalMaxIndex);
                    ymFace.setHeadPose(headposes);
                    logic(ymFace, finalMaxIndex, type, mRecog, mIrBytes, iw, ih);
                }
            });
        }
        frame++;
        YMFace ymFace = ymFaces.get(maxIndex);
        if (ymFace == null) return null;
        int trackId = ymFace.getTrackId();
        List<YMFace> ymFaceList = new ArrayList<>();
        if (trackingMap.containsKey(trackId)) {
            YMFace face = trackingMap.get(trackId);
            ymFace.setHeadPose(face.getHeadpose());//人脸角度
            ymFace.setFaceQuality(face.getFaceQuality());//人脸质量
            ymFace.setLiveness(face.getLiveness()); //活体结果
            ymFace.setIdentifiedPerson(face.getPersonId(), face.getConfidence()); //识别结果
            ymFaceList.add(ymFace);
        }
        return ymFaceList;
    }

    /**
     * 多人脸追踪/识别/活体检测
     *
     * @param ymFaces
     * @param mRecog
     * @param type
     * @param mIrBytes
     * @param iw
     * @param ih
     * @return
     */
    private List<YMFace> multiTrack(final List<YMFace> ymFaces, final boolean mRecog,
                                    final int type, final byte[] mIrBytes, final int iw, final int ih) {
        final List<float[]> headposes = getAllHeadpose(ymFaces);
        if (frame > 10) {
            frame = 0;
            ThreadPoolManager.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < ymFaces.size(); i++) {
                        YMFace ymFace = ymFaces.get(i);
                        ymFace.setHeadPose(headposes.get(i));
                        logic(ymFace, i, type, mRecog, mIrBytes, iw, ih);
                    }
                }
            });
        }
        frame++;
        for (int i = 0; i < ymFaces.size(); i++) {
            final YMFace ymFace = ymFaces.get(i);
            final int trackId = ymFace.getTrackId();
            if (trackingMap.containsKey(trackId)) {
                YMFace face = trackingMap.get(trackId);
                ymFace.setHeadPose(face.getHeadpose());//人脸角度
                ymFace.setFaceQuality(face.getFaceQuality());//人脸质量
                ymFace.setLiveness(face.getLiveness()); //活体结果
                ymFace.setIdentifiedPerson(face.getPersonId(), face.getConfidence()); //识别结果
            }
        }
        return ymFaces;
    }


    private List<float[]> getAllHeadpose(List<YMFace> ymFaces) {
        List<float[]> headposes = new ArrayList<>();
        for (YMFace ymFace : ymFaces) {
            headposes.add(ymFace.getHeadpose());
        }
        return headposes;
    }


    /**
     * 该方法用于实时监测同一个trackingId是否为同一个人
     *
     * @param facefeature
     * @param ymFace
     * @return
     */
    private boolean checkFaceFeatureByTrackId(float[] facefeature, YMFace ymFace) {
        if (facefeature == null || ymFace == null) return false;
        int trackId = ymFace.getTrackId();
        if (trackingFeatureMap.containsKey(trackId)) {
            float[] oldfacefeature = trackingFeatureMap.get(trackId);
            int result = faceTrack.compareFaceFeature(oldfacefeature, facefeature);
            if (result < 75) {
                trackingMap.put(trackId, ymFace);
                return true;
            }
        } else {
            trackingFeatureMap.put(trackId, facefeature);
        }
        return false;
    }

    private void logic(YMFace ymFace, int i, int livenessType, boolean isRecog, byte[] irBytes,
                       int iw, int ih) {
        if (ymFace == null) return;
        int trackId = ymFace.getTrackId();
        boolean next = true;
        float[] headposes = ymFace.getHeadpose();
        //检测人脸角度
        if ((Math.abs(headposes[0]) > 30 || Math.abs(headposes[1]) > 30 || Math.abs(headposes[2]) > 30)) {
            //角度不佳不再识别
            next = false;
        }
        // 检测获取的人脸质量
        int faceQuality = faceTrack.getFaceQuality(i);
        ymFace.setFaceQuality(faceQuality);
        if (faceQuality < 6) {
            //人脸质量不佳不再识别
            next = false;
        }
        if (!next) {
            trackingMap.put(ymFace.getTrackId(), ymFace);
            return;
        }
        // if (checkFaceFeatureByTrackId(faceTrack.getFaceFeature(i), ymFace)) return;
        //活体识别
        switch (livenessType) {
            case 0://双目活体
                if (irBytes == null) return;
                float[] rect = ymFace.getRect();
                //可见光和红外预览反向
                //rect[0] = ih - rect[0] - rect[2];
                int result = faceTrack.livenessDetectFrame(irBytes, iw, ih, rect);
                ymFace.setLiveness(result);
                ymFace.setPersonId(ymFace.getPersonId());
                break;
            case 1: //可见光活体
                int resultDetect[] = faceTrack.livenessDetect(i);
                ymFace.setLiveness(resultDetect[0]);
                ymFace.setPersonId(ymFace.getPersonId());
                break;
            case 2: //红外活体
                int DetectInfrared[] = faceTrack.livenessDetectInfrared(i);
                ymFace.setLiveness(DetectInfrared[0]);
                break;
            default:
                ymFace.setLiveness(-111);
                break;
        }
        //人脸识别
        if (isRecog) {
            int identifyPerson = -111;
            android.util.Log.d("wlDebug", "ymFace.getLiveness().... getConfidence" + ymFace.getConfidence());
            android.util.Log.d("wlDebug", "ymFace.getLiveness().... getLiveness" + ymFace.getLiveness());

            identifyPerson = faceTrack.identifyPerson(i);
            int confidence = faceTrack.getRecognitionConfidence();

            if (ymFace.getConfidence() >= 75 && ymFace.getLiveness() == 1) {
                ymFace.setIdentifiedPerson(identifyPerson, confidence);
                if (identifyPerson >= 0) {
                    android.util.Log.d("wlDebug", "ymFace.getLiveness() = " + ymFace.getLiveness());
                    // 当liveeness == 1时活体识别通过;
                    int user_id = 0;
                    FaceImage unique = DbManager.getInstance().getDaoSession().getFaceImageDao().queryBuilder()
                            .where(FaceImageDao.Properties.PersonId.eq(identifyPerson)).unique();
                    if (unique != null) {
                        user_id = unique.getUser_id();
                    }
                    LogUtil.w("user_id" + user_id);
                    EventBus.getDefault().post(new FaceSuccessEventBean(user_id, "", true));
//                    toast("人脸已注册");
                } else {
//                    toast("人脸未注册");
//                    EventBus.getDefault().post(new FaceSuccessEventBean(0, "", false));
                }
            } else {
//                toast("未通过,getConfidence= " + ymFace.getConfidence() +  " | getLiveness= " + ymFace.getLiveness());
            }


            if (!trackingMap.containsKey(trackId) || trackingMap.get(trackId).getPersonId() <= 0) {
                //人脸识别：identifyPerson>0 为识别成功，identifyPerson为识别对应人脸的personid identifyPerson<0  即该人脸未注册
//                identifyPerson = faceTrack.identifyPerson(i);
//                int confidence = faceTrack.getRecognitionConfidence();
//                float[] faceFeature = faceTrack.getFaceFeature(i);//特征值（貌似识别时不唯一）
                ymFace.setIdentifiedPerson(identifyPerson, confidence);
                if (identifyPerson >= 0) {
                    android.util.Log.d("wlDebug", "ymFace.getLiveness() = " + ymFace.getLiveness());
                    // 当liveeness == 1时活体识别通过;
                    if (ymFace.getLiveness() == 1) {
//
                    }
                } else {//未注册
//                    EventBus.getDefault().post(new FaceSuccessEventBean(0, "", false));
                }
            }
        } else {
            ymFace.setIdentifiedPerson(-1, 0);
        }
        trackingMap.put(trackId, ymFace);
    }


//    private void toast(String msg){
//        Observable.just(msg)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<String>() {
//                    public void onSubscribe(Disposable d) {
//                    }
//                    public void onNext(String integer) {
//                        ToastUtil.showCustomToast("人脸识别： "+integer);
//                    }
//                    public void onError(Throwable e) {
//                    }
//                    public void onComplete() {
//                    }
//                });
//    }


    /**
     * 检测人脸角度与人脸质量
     *
     * @param ymFace
     * @param i
     * @return code  0:通过，1:角度不佳  2：质量不佳
     */
    private boolean checkAngleAndQuality(YMFace ymFace, int i) {
        float[] headposes = ymFace.getHeadpose();
        //检测角度
        if ((Math.abs(headposes[0]) > 30 || Math.abs(headposes[1]) > 30 || Math.abs(headposes[2]) > 30)) {
            //角度不佳不再识别
            return false;
        }
        //检测获取的人脸质量
        int faceQuality = faceTrack.getFaceQuality(i);
        if (faceQuality < 6) {
            return false;
        }
        return true;
    }

    /**
     * 获取对视频流中最大的人脸进行追踪
     *
     * @param bytes yuv视频流
     * @param iw    预览图像宽度
     * @param ih    预览图像高度
     * @return null||maxFace
     */
    public List<YMFace> faceTracking(byte[] bytes, int iw, int ih, boolean isMulti) {
        if (null == faceTrack) {
            return null;
        }
        try {
            List<YMFace> ymFaces = faceTrack.trackMulti(bytes, iw, ih);
            if (ymFaces == null || ymFaces.size() < 1) return null;
            int maxIndex = 0;
            for (int i = 1; i < ymFaces.size(); i++) {
                if (ymFaces.get(maxIndex).getRect()[2] <= ymFaces.get(i).getRect()[2]) {
                    maxIndex = i;
                }
            }
            if (isMulti) return ymFaces;
            List<YMFace> faces = new ArrayList<>();
            faces.add(ymFaces.get(maxIndex));
            return faces;
        } catch (Exception e) {
            Log.e("Rs", "error:" + e);
        }
        return null;
    }


}
