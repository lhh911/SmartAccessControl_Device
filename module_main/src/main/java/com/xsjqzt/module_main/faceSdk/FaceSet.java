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
import com.xsjqzt.module_main.modle.FaceResult;
import com.xsjqzt.module_main.modle.FaceSuccessEventBean;
import com.xsjqzt.module_main.modle.User;
import com.xsjqzt.module_main.util.DataConversionUtil;
import com.xsjqzt.module_main.util.ThreadPoolManager;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
        faceTrack.setDistanceType(YMFaceTrack.DISTANCE_TYPE_FAR);        //设置人脸检测距离，默认近距离，需要在initTrack之前调用
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
            User user = getUser(0);
            user.setName(name);
            result.personId = insertPerson(user, 0, bitmap, result.rect);
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
            UserDataUtil.clearDb();
            DataSource dataSource = new DataSource(ExApplication.getContext());
            dataSource.deleteAllUser();
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
        User user = getUser(0);
        user.setName(name);
        result.personId = insertPerson(user, index, bitmap, rect);
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
            user.setPersonId(personId + "");
            if (UserDataUtil.addDataSource(head, user, rect, 0)) {
                return personId;
            } else {
                faceTrack.deletePerson(personId);
                return -1;
            }
        }
        return -1;
    }


    public boolean deleteUserByPersonId(int personId) {
        if (faceTrack == null) return false;
        if (faceTrack.deletePerson(personId) == 0) {
            UserDataUtil.deleteByPersonId(String.valueOf(personId));
            return true;
        }
        return false;
    }


    /**
     * 获取属性
     *
     * @param index
     * @return
     */
    public User getUser(int index) {
        User user = new User();
        float[] faceFeature = faceTrack.getFaceFeature(index);
        int gender = faceTrack.getGender(index);
        int gender_confidence = faceTrack.getGenderConfidence(index);
        if (gender_confidence >= 90)
            user.setGender(gender == 0 ? "F" : "M");
        else {
            user.setGender("");
        }
        user.setScore(faceTrack.getHappyScore(index) + "");
        user.setAge(faceTrack.getAge(index) + "");
        user.setFaceFeature(floatToString(faceFeature));
        return user;
    }

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
            if (!trackingMap.containsKey(trackId) || trackingMap.get(trackId).getPersonId() <= 0) {
                //人脸识别：identifyPerson>0 为识别成功，identifyPerson为识别对应人脸的personid identifyPerson<0  即该人脸未注册
                identifyPerson = faceTrack.identifyPerson(i);
                int confidence = faceTrack.getRecognitionConfidence();
                ymFace.setIdentifiedPerson(identifyPerson, confidence);
                if (identifyPerson >= 0) {
//                    EventBus.getDefault().post(new FaceSuccessEventBean(0 , "",true));
//                    context.sendBroadcast(new Intent("aqy.intent.action.OPEN_DOOR"));
                    android.util.Log.d("wlDebug", "ymFace.getLiveness() = " + ymFace.getLiveness());
                    // 当liveeness == 1时活体识别通过;
                    if(ymFace.getLiveness() == 1) {
//                        String code = DataConversionUtil.floatToString(ymFace.getRect());
//                        FaceImage faceImage = DbManager.getInstance().getDaoSession().getFaceImageDao().queryBuilder().where(FaceImageDao.Properties.Code.eq(code)).unique();
//                        if(faceImage != null)//数据库有这个人注册的数据
                        String personName = faceTrack.getPersonName(i);
                        LogUtil.w("personName = "+personName);
//                        int user_id = TextUtils.isEmpty(personName) ? 0 : Integer.parseInt(personName);
                        EventBus.getDefault().post(new FaceSuccessEventBean(0 , "",true));

                    }
                }else{//未注册
//                    String code = DataConversionUtil.floatToString(ymFace.getRect());
                    EventBus.getDefault().post(new FaceSuccessEventBean(0, "",false));
                }
            }
        } else {
            ymFace.setIdentifiedPerson(-1, 0);
        }
        trackingMap.put(trackId, ymFace);
    }


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
