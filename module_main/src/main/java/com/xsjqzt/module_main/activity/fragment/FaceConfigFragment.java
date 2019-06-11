package com.xsjqzt.module_main.activity.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.readsense.cameraview.camera.Size;

import java.util.List;

import com.xsjqzt.module_main.Config.DemoConfig;
import com.xsjqzt.module_main.R;
import com.xsjqzt.module_main.activity.base.ExApplication;
import com.xsjqzt.module_main.util.DisplayUtil;
import com.xsjqzt.module_main.util.SharedPrefUtils;

public class FaceConfigFragment extends Fragment implements View.OnClickListener {
    private SwitchButton btLR;
    private SwitchButton btRD;
    private SwitchButton btAdkustView;
    private SwitchButton btRotate90;
    private SwitchButton btIsMulti;


    private SeekBar btScreenZoom;
    private SeekBar btCameraAngle;
    private SeekBar btSDKAngle;
    private SeekBar btCameraResolution;

    private TextView tvScreenZoom;
    private TextView tvCameraAngle;
    private TextView tvSDKAngle;
    private TextView tvCameraResolution;
    private DemoConfig mConfig;

    private Button btConfirm;
    private Button btCancle;
    private clickConfigListener mCallBack;
    private List<Camera.Size> supportedPreviewSize;

    private ImageView ivVideoAngle;
    private ImageView ivIrVideoAngle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mConfig = SharedPrefUtils.getObject(ExApplication.getContext(), "DEMO_CONFIG", DemoConfig.class);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.config, container, false);
        view.getLayoutParams().width = (DisplayUtil.getDisplayMetrics(getActivity()).widthPixels);
        view.getLayoutParams().height = (int) (DisplayUtil.getDisplayMetrics(getActivity()).heightPixels * 0.8f);
        initView(view);
        initData();
        initEvent();
        return view;
    }

    public void setOnclickListener(clickConfigListener listener) {
        mCallBack = listener;
    }

    public void initView(View view) {
        btLR = view.findViewById(R.id.bt_config_lr);
        btRD = view.findViewById(R.id.bt_config_td);
        btRotate90 = view.findViewById(R.id.bt_config_rotate90);


        btAdkustView = view.findViewById(R.id.bt_adjust_view);
        btIsMulti = view.findViewById(R.id.bt_config_isMulti);


        btScreenZoom = view.findViewById(R.id.sb_config_screen_zoom);
        tvScreenZoom = view.findViewById(R.id.tv_config_screen_zoom);
        btScreenZoom.setMax(10);
        btScreenZoom.setProgress((int) (mConfig.screenZoon * 10));
        tvScreenZoom.setText(mConfig.screenZoon + "");

        btCameraAngle = view.findViewById(R.id.sb_config_camera_angle);
        tvCameraAngle = view.findViewById(R.id.tv_config_camera_angle);
        btCameraAngle.setMax(3);
        btCameraAngle.setProgress(mConfig.cameraAngle / 90);
        tvCameraAngle.setText("" + mConfig.cameraAngle);

        btSDKAngle = view.findViewById(R.id.sb_config_sdk_angle);
        tvSDKAngle = view.findViewById(R.id.tv_config_sdk_angle);
        btSDKAngle.setMax(3);
        btSDKAngle.setProgress(mConfig.sdkAngle / 90);
        tvSDKAngle.setText("" + mConfig.sdkAngle);

        btCameraResolution = view.findViewById(R.id.sb_config_screen_resolution_camera);
        tvCameraResolution = view.findViewById(R.id.tv_config_screen_resolution_camera);
        supportedPreviewSize = mCallBack.getSupportedPreviewSize();
        btCameraResolution.setMax(supportedPreviewSize.size() - 1);
        btCameraResolution.setProgress(getSupportedPreviewSizePosition(supportedPreviewSize, mConfig.previewSizeWidth, mConfig.previewSizeHeight));
        tvCameraResolution.setText("" + mConfig.getPreviewSize());


        btConfirm = view.findViewById(R.id.bt_config_confirm);
        btCancle = view.findViewById(R.id.bt_config_cancle);

        ivVideoAngle = view.findViewById(R.id.iv_config_video_angle);
        ivIrVideoAngle = view.findViewById(R.id.iv_config_video_angle_ir);

        Bitmap[] bitmaps = mCallBack.getCameraBytes();
        if (bitmaps == null || bitmaps.length < 1) {
            ivVideoAngle.setImageResource(R.drawable.ic_face_reco_off);
            ivIrVideoAngle.setImageResource(R.drawable.ic_face_reco_off);
        } else {
            if (bitmaps[0] == null) {
                ivVideoAngle.setImageResource(R.drawable.ic_face_reco_off);
            } else {
                ivVideoAngle.setImageBitmap(bitmaps[0]);
            }
            if (bitmaps[1] == null) {
                ivIrVideoAngle.setImageResource(R.drawable.ic_face_reco_off);
            } else {
                ivIrVideoAngle.setImageBitmap(bitmaps[1]);
            }
        }
    }

    public void initData() {
        btLR.setChecked(mConfig.specialCameraLeftRightReverse);
        btRD.setChecked(mConfig.specialCameraTopDownReverse);
        btRotate90.setChecked(mConfig.screenrRotate90);
        btAdkustView.setChecked(mConfig.isAdjustView);
        btIsMulti.setChecked(mConfig.isMulti);
    }


    public void initEvent() {
        btLR.setOnClickListener(this);
        btRD.setOnClickListener(this);
        btRotate90.setOnClickListener(this);
        btAdkustView.setOnClickListener(this);
        btConfirm.setOnClickListener(this);
        btCancle.setOnClickListener(this);
        btIsMulti.setOnClickListener(this);
        btScreenZoom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mConfig.screenZoon = progress / (float) 10;
                tvScreenZoom.setText(mConfig.screenZoon + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        btCameraAngle.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                try {
                    mConfig.cameraAngle = progress * 90;
                    tvCameraAngle.setText(mConfig.cameraAngle + "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        btSDKAngle.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mConfig.sdkAngle = progress * 90;
                tvSDKAngle.setText("" + mConfig.sdkAngle);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        btCameraResolution.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mConfig.setPreviewSize(supportedPreviewSize.get(progress).width, supportedPreviewSize.get(progress).height);
                tvCameraResolution.setText("" + mConfig.getPreviewSize());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.bt_adjust_view) {
            mConfig.isAdjustView = !mConfig.isAdjustView;

        } else if (i == R.id.bt_config_lr) {
            mConfig.specialCameraLeftRightReverse = !mConfig.specialCameraLeftRightReverse;

        } else if (i == R.id.bt_config_td) {
            mConfig.specialCameraTopDownReverse = !mConfig.specialCameraTopDownReverse;

        } else if (i == R.id.bt_config_rotate90) {
            mConfig.screenrRotate90 = !mConfig.screenrRotate90;

        } else if (i == R.id.bt_config_isMulti) {
            mConfig.isMulti = !mConfig.isMulti;

        } else if (i == R.id.bt_config_confirm) {
            SharedPrefUtils.putObject(ExApplication.getContext(), "DEMO_CONFIG", mConfig);
            mCallBack.adjustCameraParameters(new Size(mConfig.previewSizeWidth, mConfig.previewSizeHeight), mConfig.screenZoon, mConfig.sdkAngle, mConfig.cameraAngle, mConfig.isAdjustView, mConfig.screenrRotate90);
            getActivity().onBackPressed();

        } else if (i == R.id.bt_config_cancle) {
            getActivity().onBackPressed();

        }
    }

    public interface clickConfigListener {
        boolean adjustCameraParameters(Size resolution, float zoom, int sdkAngle, int cameraAngle, boolean isAdjustView, boolean adjustVertical);

        List<Camera.Size> getSupportedPreviewSize();

        Bitmap[] getCameraBytes();

    }

    public int getSupportedPreviewSizePosition(List<Camera.Size> list, int w, int h) {
        int position = 0;
        for (int i = 0; i < list.size(); i++) {
            if ((list.get(i).width == w && list.get(i).height == h) || (list.get(i).width == h && list.get(i).height == w)) {
                position = i;
                break;
            }
        }
        return position;
    }
}
