package com.xsjqzt.module_main.activity.register;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.readsense.cameraview.dialog.BindViewHolder;
import com.readsense.cameraview.dialog.OnBindViewListener;
import com.readsense.cameraview.dialog.OnViewClickListener;
import com.readsense.cameraview.dialog.TDialog;

import java.io.File;

import com.xsjqzt.module_main.Config.DemoConfig;
import com.xsjqzt.module_main.R;
import com.xsjqzt.module_main.activity.base.BaseActivity;
import com.xsjqzt.module_main.faceSdk.FaceSet;
import com.xsjqzt.module_main.modle.FaceResult;
import com.xsjqzt.module_main.util.BitmapUtil;

public class RegistFromBatchPicActivity extends BaseActivity implements View.OnClickListener {
    private int mCountNow = 0;
    private int mCount = 0;
    private int successCount = 0;
    private int failCount = 0;
    private int repeatCount = 0;
    private FaceSet faceSet;

    private EditText editText;
    private Button btEdit;
    private Button btConfirm;
    private Button btProgressConfirm;

    private TextView txPrompt;
    private ProgressBar progressBar;
    private TextView mProgressRatio;
    private TextView mProgressRegiNow;

    private MyHandler myHandler;
    private String picturePath;
    private MyRunnable runnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.regist_from_batch_pic_activity);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        myHandler = new MyHandler();
        faceSet = new FaceSet(getApplication());
        faceSet.startTrack(0);
        editText = findViewById(R.id.et_pic_path);
        btEdit = findViewById(R.id.bt_batch_edit);
        btEdit.setOnClickListener(this);
        btConfirm = findViewById(R.id.bt_batch_confirm);
        btConfirm.setOnClickListener(this);
        //设置默认图片路径
        editText.setText(DemoConfig.ImagePath);
        editText.setEnabled(false);
    }

    @Override
    protected void initEvent() {
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public void getFileCount(String filepath) {
        File file = new File(filepath);
        File[] listfile = file.listFiles();
        if (listfile != null)
            for (int i = 0; i < listfile.length; i++) {
                if (!listfile[i].isDirectory()) {
                    mCount++;
                } else {
                    getFileCount(listfile[i].toString());
                }
            }
    }

    public void begin(String path, MyRunnable runnable) {
        getFileCount(path);
        progressBar.setMax(mCount);
        File file = new File(path);
        if (file.exists()) {
            if (file.canRead()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    File f = files[i];
                    Log.e("pan", "Process.myUid() = " + android.os.Process.myTid());
                    if (runnable.flag) break;
                    if (f.isDirectory())
                        begin(f.getAbsolutePath(), runnable);
                    else {
                        register(f);
                    }
                }
            }
        }

    }

    public void register(File f) {
        picturePath = f.getAbsolutePath();
        try {
            Bitmap selectedImage = BitmapUtil.decodeScaleImage(picturePath, 1000, 1000);
            if (selectedImage != null) {
                FaceResult faceResult = faceSet.registByBitmap(selectedImage, f.getName());
                if (faceResult.code == 0) successCount++;
                else if (faceResult.code == 102) repeatCount++;
                else failCount++;
            }
        } catch (Exception e) {
            failCount++;
            //  e.printStackTrace();
        } finally {
            //更新当前进度
            Message msg = new Message();
            msg.what = 3;
            myHandler.sendMessage(msg);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*
            case R.id.bt_batch_edit:
                if (editText.isEnabled()) {
                    editText.setEnabled(false);
                    btEdit.setText("更改");
                } else {
                    editText.setEnabled(true);
                    btEdit.setText("确定");
                }
                break;
            case R.id.bt_batch_confirm:
                if (editText.isEnabled()) {
                    Toast.makeText(getApplicationContext(), "请确定您修改的路径", Toast.LENGTH_SHORT).show();
                } else {
                    new TDialog.Builder(getSupportFragmentManager())
                            .setLayoutRes(R.layout.dialog_progress)
                            .setGravity(Gravity.CENTER)
                            .setScreenWidthAspect(this, 0.8f)
                            .addOnClickListener(R.id.dialog_progress_confirm)
                            .setCancelableOutside(false)
                            .setOnBindViewListener(new OnBindViewListener() {
                                @Override
                                public void bindView(BindViewHolder viewHolder) {
                                    txPrompt = viewHolder.getView(R.id.dialog_progress_prompt);
                                    progressBar = viewHolder.getView(R.id.dialog_progress_progressBar);
                                    mProgressRatio = viewHolder.getView(R.id.dialog_progress_ratio);
                                    mProgressRegiNow = viewHolder.getView(R.id.dialog_progress_prompt_now);
                                    btProgressConfirm = viewHolder.getView(R.id.dialog_progress_confirm);
                                    runnable = new MyRunnable();
                                    progressBar.setProgress(0);
                                    progressBar.incrementProgressBy(0);
                                    new Thread(runnable).start();
                                }
                            })
                            .setOnViewClickListener(new OnViewClickListener() {
                                @Override
                                public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
                                    try {
                                        if (runnable != null) {
                                            runnable.flag = true;
                                        }
                                        runnable = null;
                                    } catch (Exception e) {
                                    }
                                    tDialog.dismiss();
                                }
                            })
                            .create()
                            .show();
                }
                break;
                */
        }
    }

    public class MyRunnable implements Runnable {
        private volatile boolean flag = false;

        @Override
        public void run() {
            mCount = 0;
            successCount = 0;
            mCountNow = 0;
            failCount = 0;
            repeatCount = 0;
            begin(editText.getText().toString(), this);
            if (mCount > 0) {
                //注册完
                Message msg = new Message();
                msg.what = 0;
                myHandler.sendMessage(msg);
            } else {
                //文件夹内无图片
                Message msg = new Message();
                msg.what = 1;
                myHandler.sendMessage(msg);
            }
        }
    }

    //更新UI
    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //注册完成
                    progressBar.setMax(mCount);
                    txPrompt.setText(txPrompt.getText() + "," + String.format("已经完成..."));
                    progressBar.incrementProgressBy(mCount);
                    progressBar.setProgress(mCount);
                    progressBar.incrementProgressBy(mCountNow / mCount);
                    btProgressConfirm.setText("知道了");
                    btProgressConfirm.setTextColor(Color.BLACK);
                    break;
                case 1:
                    txPrompt.setText("当前路径下并无图片...");
                    progressBar.setMax(1);
                    progressBar.incrementProgressBy(1);
                    mProgressRegiNow.setText(editText.getText());
                    mProgressRatio.setText(0 + "/" + 0);
                    btProgressConfirm.setText("知道了");
                    btProgressConfirm.setTextColor(Color.BLACK);
                    break;
                case 3:
                    //更新当前进度
                    mProgressRegiNow.setText(picturePath);
                    mCountNow++;
                    progressBar.setProgress(mCountNow);
                    progressBar.incrementProgressBy(mCountNow / mCount);
                    mProgressRatio.setText(mCountNow + "/" + mCount);
                    txPrompt.setText(successCount + "/" + mCountNow + "(成功率)  " + failCount + "/" + mCountNow + "(失败率)" + "  " + repeatCount + "/" + mCountNow + "(重复率)");
                    btProgressConfirm.setText("关闭");
                    btProgressConfirm.setTextColor(Color.RED);
                    break;
            }
        }
    }
}
