package com.xsjqzt.module_main.widget;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xsjqzt.module_main.R;


/**
 * Created by Sai on 15/11/22.
 * 精仿iOSPickerViewController控件
 */
public class NumInputDialog extends BaseInputView implements NumInputView.NumInputListeren , View.OnClickListener {
    NumInputView numInputView;
    LinearLayout helpLl;
    TextView helpTipTv;


    public NumInputDialog(Context context){
        super(context);

        initViews();
    }

    protected void initViews(){
        numInputView = (NumInputView) findViewById(R.id.numinputview);
        numInputView.setClickListeren(this);

        helpLl = (LinearLayout) findViewById(R.id.help_tip_Layout);
        helpTipTv = (TextView) findViewById(R.id.help_tip_tv);
        TextView backTv = (TextView) findViewById(R.id.back_tv);
        backTv.setOnClickListener(this);
    }


    @Override
    public void helpClick() {
        numInputView.setVisibility(View.GONE);
        helpLl.setVisibility(View.VISIBLE);

        if(inputClickListeren != null){
            inputClickListeren.inputClick();
        }
    }

    @Override
    public void backClick() {
        dismiss();
        if(inputClickListeren != null){
            inputClickListeren.inputDismiss();
        }
    }

    @Override
    public void confirmClick(String inputNum,int type) {
        if(inputClickListeren != null){
            inputClickListeren.inputDismiss();
            inputClickListeren.inputConfirm(inputNum,type);
        }

    }

    @Override
    public void numClick() {
        if(inputClickListeren != null){
            inputClickListeren.inputClick();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.back_tv){
            numInputView.setVisibility(View.VISIBLE);
            helpLl.setVisibility(View.GONE);

            if(inputClickListeren != null){
                inputClickListeren.inputClick();
            }
        }
    }


    public void setmType (int type){
        numInputView.setmType(type);
    }


    InputClickListeren inputClickListeren;

    public void setInputClickListeren(InputClickListeren inputClickListeren) {
        this.inputClickListeren = inputClickListeren;
    }

    public interface InputClickListeren{
        void inputClick();
        void inputDismiss();
        void inputConfirm(String input,int type);
    }
}
