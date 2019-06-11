package com.jbb.library_common.widght;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.jbb.library_common.R;
import com.jbb.library_common.utils.CommUtil;


/**
 * Created by lhh
 * DATE 2017/5/31.
 */

public class DoubleButtonDialog extends Dialog {

    private TextView title;
    private Button cancelBtn,confirmBtn;

    public DoubleButtonDialog(Context context) {
        super(context);
        init(context);
    }

    public DoubleButtonDialog(Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    protected DoubleButtonDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    public void init(Context context) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.commit_dialog, null);
        title = (TextView)view.findViewById(R.id.update_title_dialog);



        confirmBtn = (Button)view.findViewById(R.id.btn_confirm_dialog);
        cancelBtn = (Button)view.findViewById(R.id.btn_cancel_dialog);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                dismiss();
            }
        });
        setContentView(view);
        setCancelable(false);
        Window dialogWindow = getWindow();
        dialogWindow.setBackgroundDrawableResource(R.color.common_transparent);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();


        lp.width = CommUtil.getScreenWidth(context) * 5/6; //
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);

    }

    public void showDialog(){
        if (!this.isShowing()) {
            this.show();

        } else {
            this.dismiss();
        }
    }



    public void setDatas(String title){
        this.title.setText(title);
    }


    public Button getConfirmBtn() {
        return confirmBtn;
    }

    public Button getCancelBtn() {
        return cancelBtn;
    }

}
