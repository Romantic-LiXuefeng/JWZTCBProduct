package com.jwzt.caibian.activity;

import java.io.File;

import com.jwzt.caibian.application.BaseFragmentActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.FragmentActivity;


public class BaseImageEditActivity extends FragmentActivity {

    /**
     * 检测图片载入框架是否导入  若没有 则导入并初始化
     */

    /**
     * 初始化图片载入框架
     */

    public static Dialog getLoadingDialog(Context context, int titleId,
                                          boolean canCancel) {
        return getLoadingDialog(context,context.getString(titleId),canCancel);
    }


    public static Dialog getLoadingDialog(Context context, String title,
                                          boolean canCancel) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setCancelable(canCancel);
        dialog.setMessage(title);
        return dialog;
    }
}
