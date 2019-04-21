package com.jbb.library_common.widght;


import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.jbb.library_common.R;


public class LoadDialog {


    private Dialog dialog;
    private TextView textView;
    public LoadDialog(Context context , String text){
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.common_dialogview, null);// 得到加载view
        textView = (TextView)v.findViewById(R.id.dialogview_text);
        if(!TextUtils.isEmpty(text))
            textView.setText(text);

        dialog = new Dialog(context, R.style.common_loading_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(v);

    }


    public boolean isShowing(){
        if(dialog != null && dialog.isShowing())
            return true;
        else
            return false;
    }

    /***/
    public void show(){
        if(dialog != null && !dialog.isShowing())
            dialog.show();
    }
    
    public  void dismiss(){
    	if(dialog != null&&dialog.isShowing()){
    		dialog.dismiss();
    	}
    }


    public void setText(String text) {
        textView.setText(text);
    }
}
