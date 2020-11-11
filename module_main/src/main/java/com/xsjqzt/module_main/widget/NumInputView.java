package com.xsjqzt.module_main.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jbb.library_common.utils.CommUtil;
import com.jbb.library_common.utils.log.LogUtil;
import com.xsjqzt.module_main.R;
import com.xsjqzt.module_main.greendao.DbManager;
import com.xsjqzt.module_main.greendao.OpenCodeDao;
import com.xsjqzt.module_main.greendao.entity.OpenCode;

import java.util.ArrayList;
import java.util.List;

public class NumInputView extends ViewGroup implements View.OnClickListener {

    private String TAG = "NumInputView";
    private int mType =1;// 1 密码开门， 2 房号呼叫

    private int logoIvSize = CommUtil.dp2px(100);
    private int inputHeight = CommUtil.dp2px(45);
    private int itemWidth = CommUtil.dp2px(100);
    private int itemHeight = itemWidth / 2;
    private int itemSpace = CommUtil.dp2px(20);
    private int paddingLeft = CommUtil.dp2px(15);
    private int colCount = 4;//4列

    private List<TextView> numViews = new ArrayList<>();

    NumInputListeren clickListeren;


    //itemView
    private ImageView logoTipIv;
    private TextView inputTv, helpTv, confirmTv, backTv, clearTv, zoroTv;


    public NumInputView(Context context) {
        this(context, null);
    }

    public NumInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public void setmType(int mType) {
        this.mType = mType;
        inputTv.setHint(getHintText());
        inputTv.setHintTextColor(Color.parseColor("#666666"));
        logoTipIv.setImageResource(getLogo());
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }

        int height = paddingLeft * 2 + logoIvSize + (itemHeight + itemSpace) * 4;


        setMeasuredDimension(getDefaultSize(MeasureSpec.getSize(widthMeasureSpec), widthMeasureSpec),
                height
        );
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

//        itemWidth = (r - l - paddingLeft * 2) / colCount;
//        itemWidth = Math.round(itemWidth * 0.8f);
//        itemHeight = itemWidth / 2;
//        logoIvSize = itemWidth * 2 / 3;
//        itemSpace = ((r - l - paddingLeft * 2) - (itemWidth * colCount)) / (colCount + 1);

        LogUtil.d(TAG, "r - l  = " + (r - l));

        //确定各个按钮在布局中的位置，
        for (int i = 0; i < numViews.size(); i++) {
            Point xy = getCoorFromIndex(i);
            numViews.get(i).layout(xy.x, xy.y, xy.x + itemWidth, xy.y + itemHeight);
        }

        logoTipIv.layout(paddingLeft, paddingLeft, paddingLeft + logoIvSize, paddingLeft + logoIvSize);
        inputTv.layout(paddingLeft + logoIvSize + itemSpace,
                paddingLeft + logoIvSize- 10 - inputHeight,
                paddingLeft + logoIvSize + itemWidth * 3,
                paddingLeft + logoIvSize -10);
        helpTv.layout(r - l - paddingLeft - logoIvSize,
                paddingLeft ,
                r - l - paddingLeft,
                paddingLeft + inputHeight);
        confirmTv.layout(paddingLeft + (itemSpace + itemWidth) * 3 + itemSpace,
                paddingLeft + logoIvSize + itemSpace,
                paddingLeft + (itemSpace + itemWidth) * 3 + itemSpace + itemWidth,
                paddingLeft + logoIvSize + itemSpace + (itemSpace + itemHeight) * 2 + itemHeight);
        backTv.layout(paddingLeft + (itemSpace + itemWidth) * 2 + itemSpace,
                paddingLeft + logoIvSize + itemSpace + (itemSpace + itemHeight) * 3,
                paddingLeft + (itemSpace + itemWidth) * 2 + itemSpace + itemWidth,
                paddingLeft + logoIvSize + itemSpace + (itemSpace + itemHeight) * 3 + itemHeight);

        clearTv.layout(paddingLeft + (itemSpace + itemWidth) * 3 + itemSpace,
                paddingLeft + logoIvSize + (itemSpace + itemHeight) * 3 + itemSpace,
                paddingLeft + (itemSpace + itemWidth) * 3 + itemSpace + itemWidth,
                paddingLeft + logoIvSize + (itemSpace + itemHeight) * 3 + itemSpace + itemHeight);

        zoroTv.layout(paddingLeft + itemSpace,
                paddingLeft + logoIvSize + itemSpace + (itemSpace + itemHeight) * 3,
                paddingLeft + (itemWidth + itemSpace) * 2,
                paddingLeft + logoIvSize + itemSpace + (itemSpace + itemHeight) * 3 + itemHeight);
    }


    private void initView() {
        initSize();

        //添加各个按钮到view中来、
        numViews.clear();
        for (int i = 1; i < 10; i++) {
            TextView numTv = createNumTv(i);
            numViews.add(numTv);
            addView(numTv);
        }

        addView(logoTipIv = createLogoIv());
        addView(inputTv = createInputTv());
        addView(helpTv = createHelpTv());
        addView(confirmTv = createConfirmTv());
        addView(backTv = createBackTv());
        addView(clearTv = createClearTv());
        addView(zoroTv = createNumTv(0));
    }

    private void initSize(){
        int screenWidth = CommUtil.getScreenWidth(getContext());
        itemWidth = (screenWidth - paddingLeft * 2) / colCount;
        itemWidth = Math.round(itemWidth * 0.8f);
        itemHeight = itemWidth / 2;
        logoIvSize = itemWidth * 2 / 3;
        itemSpace = ((screenWidth - paddingLeft * 2) - (itemWidth * colCount)) / (colCount + 1);
    }

    private ImageView createLogoIv() {
        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(R.mipmap.ic_launcher);
        LayoutParams params = new LayoutParams(logoIvSize, logoIvSize);
        imageView.setLayoutParams(params);
        return imageView;
    }

    private TextView createInputTv() {
        TextView textView = new TextView(getContext());
        textView.setTextSize(14);
        textView.setTextColor(Color.parseColor("#333333"));
        textView.setHint(getHintText());
        textView.setHintTextColor(Color.parseColor("#666666"));
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setPadding(30, 0, 0, 0);
        textView.setBackgroundResource(R.drawable.white_corners20_bg);

        LayoutParams params = new LayoutParams(itemWidth * 3, inputHeight);
        textView.setLayoutParams(params);
        return textView;
    }


    private String getHintText() {
        if (mType == 1) {
            return "输入住户密码，按\"确定\"键开门";
        } else {
            return "输入住户栋数+房号，按\"确定\"键呼叫";
        }
    }


    private int getLogo() {
        if (mType == 1) {
            return R.mipmap.ic_open_psw;
        } else {
            return R.mipmap.ic_open_call;
        }
    }

    private TextView createHelpTv() {

        TextView textView = new TextView(getContext());
        textView.setId(R.id.numinput_help);
        textView.setTextSize(16);
        textView.setTextColor(Color.parseColor("#3e6cbb"));
        textView.setText("帮助");
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundResource(R.drawable.white_corners20_bg);
        textView.setTag("other");
        LayoutParams params = new LayoutParams(logoIvSize, inputHeight);
        textView.setLayoutParams(params);

        textView.setOnClickListener(this);
        return textView;
    }

    private TextView createConfirmTv() {

        TextView textView = new TextView(getContext());
        textView.setId(R.id.numinput_confirm);
        textView.setTextSize(16);
        textView.setTextColor(Color.parseColor("#333333"));
        textView.setText("确\n定");
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundResource(R.drawable.confirm_selector);
        textView.setTag("other");
        LayoutParams params = new LayoutParams(itemWidth, itemHeight * 3 + itemSpace * 2);
        textView.setLayoutParams(params);

        textView.setOnClickListener(this);
        return textView;
    }


    private TextView createBackTv() {

        TextView textView = new TextView(getContext());
        textView.setId(R.id.numinput_back);
        textView.setTextSize(16);
        textView.setTextColor(Color.parseColor("#333333"));
        textView.setText("返回");
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundResource(R.drawable.input_num_selector);
        textView.setTag("other");
        LayoutParams params = new LayoutParams(itemWidth, itemHeight);
        textView.setLayoutParams(params);

        textView.setOnClickListener(this);
        return textView;
    }


    private TextView createClearTv() {

        TextView textView = new TextView(getContext());
        textView.setId(R.id.numinput_clear);
        textView.setTextSize(16);
        textView.setTextColor(Color.parseColor("#333333"));
        textView.setText("清空");
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundResource(R.drawable.clear_selector);
        textView.setTag("other");
        LayoutParams params = new LayoutParams(itemWidth, itemHeight);
        textView.setLayoutParams(params);

        textView.setOnClickListener(this);
        return textView;
    }


    private TextView createNumTv(int num) {

        TextView textView = new TextView(getContext());
        textView.setTextSize(24);
        textView.setTextColor(Color.BLACK);
        textView.setText(num + "");
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundResource(R.drawable.input_num_selector);
        textView.setTag("num");
        LayoutParams params = new LayoutParams(itemWidth, itemHeight);
        textView.setLayoutParams(params);

        textView.setOnClickListener(this);

        return textView;
    }


    //1-9 个数字返回位置 x y 坐标
    protected Point getCoorFromIndex(int index) {
        int col = index % (colCount - 1);
        int row = index / (colCount - 1);
        return new Point(paddingLeft + itemSpace + (itemWidth + itemSpace) * col, (paddingLeft + logoIvSize) + itemSpace
                + (itemHeight + itemSpace) * row);
    }

    @Override
    public void onClick(View v) {
        String tag = (String) v.getTag();
        if ("num".equals(tag)) {
            String oldNum = inputTv.getText().toString().trim();
            setInputData(oldNum, ((TextView) v).getText().toString());
            if (clickListeren != null)
                clickListeren.numClick();
        } else {
            if (v.getId() == R.id.numinput_help) {
                if (clickListeren != null) {
                    clickListeren.helpClick();
                    clickListeren.numClick();
                }
            } else if (v.getId() == R.id.numinput_confirm) {
                checkInput(inputTv.getText().toString().trim());

            } else if (v.getId() == R.id.numinput_back) {
                if (clickListeren != null) {
                    clickListeren.backClick();
                }
            } else if (v.getId() == R.id.numinput_clear) {
                inputTv.setText("");
                clickListeren.numClick();
            }

        }
    }

    private void setInputData(String oldNum, String inputNum) {
        inputTv.setText(oldNum + inputNum);

        String hint = inputTv.getHint().toString();
        if (getContext().getString(R.string.input_error).equals(hint) ||
                getContext().getString(R.string.input_psw_error).equals(hint)) {
            inputTv.setHint(getHintText());
            inputTv.setHintTextColor(Color.parseColor("#666666"));
        }
    }

    private void checkInput(String inputNum) {
        if (inputNum.length() == 5) {//密码开门
            //请求接口验证密码是否正确，是就开门
            OpenCode openCode = DbManager.getInstance().getDaoSession().getOpenCodeDao().queryBuilder()
                    .where(OpenCodeDao.Properties.Code.eq(inputNum)).unique();

            if (openCode != null) {
                int expiry_time = openCode.getExpiry_time();
                long now = System.currentTimeMillis();
                if (expiry_time < (now / 1000)) {//过期了
                    inputError(2);
                } else {//密码开门成功
                    if (clickListeren != null) {
                        clickListeren.confirmClick(inputNum, mType == 1 ? 3 : 5);
                    }
                    DbManager.getInstance().getDaoSession().getOpenCodeDao().delete(openCode);
                }
            } else {
                inputError(2);
            }

        } else if (inputNum.length() == 4 || inputNum.length() == 6) {
            if (clickListeren != null) {
                clickListeren.confirmClick(inputNum, mType == 1 ? 3 : 5);
            }
        } else {
//            ToastUtil.showCustomToast("请输入正确的房间号或者临时密码");
            inputError(1);
        }
    }

    private void inputError(int type) {
        inputTv.setText("");
        if (type == 1)
            inputTv.setHint(getContext().getString(R.string.input_error));
        else{
            inputTv.setHint(getContext().getString(R.string.input_psw_error));
        }
        inputTv.setHintTextColor(Color.RED);
    }


    public void setClickListeren(NumInputListeren clickListeren) {
        this.clickListeren = clickListeren;
    }

    public void reset() {
        inputTv.setText("");
    }

    public interface NumInputListeren {
        void helpClick();

        void backClick();

        void confirmClick(String inputNum, int type);

        void numClick();

    }

}
