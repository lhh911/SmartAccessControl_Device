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
import com.xsjqzt.module_main.R;

public class NumInputView extends ViewGroup implements View.OnClickListener {

    private int mType ;// 1 密码开门， 2 房号呼叫

    private int itemWidth = CommUtil.dp2px(100);
    private int itemHeight = itemWidth/2;
    private int itemSpace = CommUtil.dp2px(20);
    private int paddingLeft = CommUtil.dp2px(20);
    private int colCount = 4;//4列


    //itemView
    private ImageView logoTipIv;
    private TextView inputTv,helpTv,confirmTv,backTv,clearTv;


    public NumInputView(Context context) {
        this(context,null);
    }

    public NumInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        //添加各个按钮到view中来、

        for (int i = 1; i< 10 ; i++){
            addView(createNumTv(i));
        }

        addView(logoTipIv = createLogoIv());
        addView(inputTv = createInputTv());
        addView(helpTv = createHelpTv());
        addView(confirmTv = createConfirmTv());
        addView(backTv = createBackTv());
        addView(clearTv = createClearTv());
    }

    private ImageView createLogoIv() {
        ImageView imageView = new ImageView(getContext());
        LayoutParams params = new LayoutParams(itemWidth, itemWidth);
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
        textView.setPadding(10,0,0,0);
        textView.setBackgroundResource(R.drawable.white_corners20_bg);

        LayoutParams params = new LayoutParams(itemWidth * 3, CommUtil.dp2px(30));
        textView.setLayoutParams(params);
        return textView;
    }



    private String getHintText() {
        if(mType == 1){
            return "输入住户密码，按\"确定\"键开门";
        }else {
            return "输入住户栋数+房号，按\"确定\"键呼叫";
        }
    }


    private TextView createHelpTv() {

        TextView textView = new TextView(getContext());
        textView.setTextSize(14);
        textView.setTextColor(Color.parseColor("#3e6cbb"));
        textView.setText("帮助");
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundResource(R.drawable.white_corners20_bg);
        textView.setTag("other");
        LayoutParams params = new LayoutParams(itemWidth , CommUtil.dp2px(30));
        textView.setLayoutParams(params);

        textView.setOnClickListener(this);
        return textView;
    }

    private TextView createConfirmTv() {

        TextView textView = new TextView(getContext());
        textView.setTextSize(14);
        textView.setTextColor(Color.parseColor("#333333"));
        textView.setText("确\n定");
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundResource(R.drawable.confirm_selector);
        textView.setTag("other");
        LayoutParams params = new LayoutParams(itemWidth , itemHeight *3 + itemSpace *2);
        textView.setLayoutParams(params);

        textView.setOnClickListener(this);
        return textView;
    }


    private TextView createBackTv() {

        TextView textView = new TextView(getContext());
        textView.setTextSize(14);
        textView.setTextColor(Color.parseColor("#333333"));
        textView.setText("返回");
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundResource(R.drawable.input_num_selector);
        textView.setTag("other");
        LayoutParams params = new LayoutParams(itemWidth , itemHeight);
        textView.setLayoutParams(params);

        textView.setOnClickListener(this);
        return textView;
    }



    private TextView createClearTv() {

        TextView textView = new TextView(getContext());
        textView.setTextSize(14);
        textView.setTextColor(Color.parseColor("#333333"));
        textView.setText("清空");
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundResource(R.drawable.clear_selector);
        textView.setTag("other");
        LayoutParams params = new LayoutParams(itemWidth , itemHeight);
        textView.setLayoutParams(params);

        textView.setOnClickListener(this);
        return textView;
    }






    private TextView createNumTv(int num) {

        TextView textView = new TextView(getContext());
        textView.setTextSize(24);
        textView.setTextColor(Color.BLACK);
        textView.setText(num+"");
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundResource(R.drawable.input_num_selector);
        textView.setTag("num");
        LayoutParams params = new LayoutParams(itemWidth , itemHeight);
        textView.setLayoutParams(params);

        textView.setOnClickListener(this);

        return textView;
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        itemWidth = (r - l - paddingLeft * 2) / colCount;
        itemWidth = Math.round(itemWidth * 0.9f);
        itemHeight = itemWidth / 2 ;
        itemSpace = ((r - l - paddingLeft * 2) - (itemWidth * colCount)) / (colCount + 1);

        //确定各个按钮在布局中的位置，


    }



    //1-9 个数字返回位置 x y 坐标
    protected Point getCoorFromIndex(int index) {
        int col = index % colCount -1;
        int row = index / colCount -1;
        return new Point(paddingLeft + itemSpace + (itemWidth + itemSpace) * col, (paddingLeft + itemWidth) + (itemSpace
                + (itemHeight + itemSpace) * row ));
    }

    @Override
    public void onClick(View v) {

    }
}
