package com.jbb.library_common.widght;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jbb.library_common.R;
import com.jbb.library_common.other.ViewClickCallBack;

/**
 *类名:ErrorAndEmptyTipView
 *描述:网络错误和空页面提示
 */
public class ErrorAndEmptyTipView extends RelativeLayout{

    private ViewStub tipStub;
    private View tipView;
    private ImageView ivTipimg;
    private TextView tvTipMsg;
    private TextView btnTip;
    private ViewClickCallBack viewCallBack;

    public ErrorAndEmptyTipView(Context context) {
        this(context, null);
        initView();
    }

    public ErrorAndEmptyTipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        tipStub = new ViewStub(getContext());
        tipStub.setLayoutResource(R.layout.emptyanderror_tip_view);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        addView(tipStub, lp);
    }

    /**
     *描述:初始化错误提示view
     *作者:nn
     *时间:2015/11/10 18:04
     *版本:3.1.3
     */
    private void initViewStub(){
        if(tipView==null){
            tipView = tipStub.inflate();
            ivTipimg = (ImageView)tipView.findViewById(R.id.tip_img_iv);
            tvTipMsg = (TextView)tipView.findViewById(R.id.tip_msg_tv);

            btnTip = (TextView)tipView.findViewById(R.id.tip_btn);
            btnTip.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (viewCallBack != null) {
                        viewCallBack.onViewClicked(btnTip);
                    }
                }
            });
        }
    }


    /**
     *描述:设置按钮的回调监听
     *作者:nn
     *时间:2015/11/11 12:19
     *版本:3.1.3
     */
    public void setViewCallBack(ViewClickCallBack callBack){
        this.viewCallBack = callBack;
    }








    /**
     *描述:只设置图片和文字提示
     *作者:nn
     *时间:2015/11/11 12:15
     *版本:3.1.3
     */
    public void setTipShowImageNoBtn(int resId,String tipText){
        initViewStub();
        ivTipimg.setVisibility(View.VISIBLE);
        ivTipimg.setImageResource(resId);
        tvTipMsg.setVisibility(View.VISIBLE);
        tvTipMsg.setText(tipText);

        btnTip.setVisibility(View.GONE);
    }



    /**
     *描述:只设置图片和文字提示
     *作者:nn
     *时间:2015/11/11 12:15
     *版本:3.1.3
     */
    public void setTipShowImageAndText(int resId,String tipText,String btnMsg){
        initViewStub();
        ivTipimg.setVisibility(View.VISIBLE);
        ivTipimg.setImageResource(resId);
        tvTipMsg.setVisibility(View.VISIBLE);
        tvTipMsg.setText(tipText);

        btnTip.setVisibility(View.VISIBLE);
        btnTip.setText(btnMsg);
    }





   /**
    *描述：ErrorAndEmptyTipView
    * @param isAnim 图片是否是动画
    *@author nn
    *@time 2017/1/11 20:43
    */
    public void setAllShow(int resId,int tipMsgId,int btnMsgId,boolean isAnim){
        initViewStub();
        ivTipimg.setVisibility(View.VISIBLE);
        tvTipMsg.setVisibility(View.VISIBLE);
        btnTip.setVisibility(View.VISIBLE);
        if(isAnim){
            final AnimationDrawable animationDrawable = (AnimationDrawable) getContext().getResources().getDrawable(resId);
            ivTipimg.setBackgroundDrawable(animationDrawable);
            ivTipimg.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    animationDrawable.start();
                    return true;
                }
            });
            ivTipimg.setImageBitmap(null);
        }else{
            ivTipimg.setImageResource(resId);
        }
        tvTipMsg.setText(getContext().getResources().getString(tipMsgId));
        btnTip.setText(getContext().getResources().getString(btnMsgId));
    }








    public void setBtnBackGround(int backgroundId){
        btnTip.setBackgroundResource(backgroundId);
    }

    public void setBtnTextColor(int color){
        btnTip.setTextColor(color);
    }
}
