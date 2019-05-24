package com.jwzt.caibian.util;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.widget.CustomProgressDialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 对话框辅助类
 */
public class DialogHelp {

	public DialogHelp(Context context) {
		// TODO Auto-generated constructor stub
	}
	
    /***
     * 获取一个dialog
     * @param context
     * @return
     */
    public static AlertDialog.Builder getDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        return builder;
    }

    /***
     * 获取一个耗时等待对话框
     * @param context
     * @param message
     * @return
     */
    public static ProgressDialog getWaitDialog(Context context, String message) {
        ProgressDialog waitDialog = new ProgressDialog(context);
        waitDialog.setCancelable(false);
        if (!TextUtils.isEmpty(message)) {
            waitDialog.setMessage(message);
        }
        return waitDialog;
    }

    /***
     * 获取一个信息对话框，注意需要自己手动调用show方法显示
     * @param context
     * @param message
     * @param onClickListener
     * @return
     */
    public static AlertDialog.Builder getMessageDialog(Context context, String message, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = getDialog(context);
        builder.setMessage(message);
        builder.setPositiveButton("确定", onClickListener);
        return builder;
    }

    public static AlertDialog.Builder getMessageDialog(Context context, String message) {
        return getMessageDialog(context, message, null);
    }

    public static AlertDialog.Builder getConfirmDialog(Context context, String message, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = getDialog(context);
        builder.setMessage(Html.fromHtml(message));
        builder.setPositiveButton("确定", onClickListener);
        builder.setNegativeButton("取消", null);
        return builder;
    }

    public static AlertDialog.Builder getConfirmDialog(Context context, String message, DialogInterface.OnClickListener onOkClickListener, DialogInterface.OnClickListener onCancleClickListener) {
        AlertDialog.Builder builder = getDialog(context);
        builder.setMessage(message);
        builder.setPositiveButton("确定", onOkClickListener);
        builder.setNegativeButton("取消", onCancleClickListener);
        return builder;
    }

    public static AlertDialog.Builder getConfirmDialog(Context context, String message, String posString, String negaString, DialogInterface.OnClickListener onOkClickListener, DialogInterface.OnClickListener onCancleClickListener) {
        AlertDialog.Builder builder = getDialog(context);
        builder.setCancelable(true);
        builder.setMessage(message);
        builder.setPositiveButton(posString, onOkClickListener);
        builder.setNegativeButton(negaString, onCancleClickListener);
        return builder;
    }

    public static AlertDialog.Builder getSelectDialog(Context context, String title, String[] arrays, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = getDialog(context);
        builder.setItems(arrays, onClickListener);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        builder.setPositiveButton("取消", null);
        return builder;
    }

    public static AlertDialog.Builder getSelectDialog(Context context, String[] arrays, DialogInterface.OnClickListener onClickListener) {
        return getSelectDialog(context, "", arrays, onClickListener);
    }

    public static AlertDialog.Builder getSingleChoiceDialog(Context context, String title, String[] arrays, int selectIndex, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = getDialog(context);
        builder.setSingleChoiceItems(arrays, selectIndex, onClickListener);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        builder.setNegativeButton("取消", null);
        return builder;
    }

    public static AlertDialog.Builder getSingleChoiceDialog(Context context, String[] arrays, int selectIndex, DialogInterface.OnClickListener onClickListener) {
        return getSingleChoiceDialog(context, "", arrays, selectIndex, onClickListener);
    }
    
    
    private static CustomProgressDialog progressDialog;
	/**
	 * 显示等待对话框
	 */
	public static void showLoadingDialog(Context mContext,String titleStr,String contentStr){
		if (progressDialog == null&&mContext!=null) {
			progressDialog = CustomProgressDialog.createDialog(mContext);
			progressDialog.setMessage(contentStr);
			progressDialog.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode,KeyEvent event) {
						if (keyCode == KeyEvent.KEYCODE_SEARCH) {
							return true;
						} else if (keyCode == KeyEvent.KEYCODE_BACK) {
							if (progressDialog != null) {
								progressDialog.dismiss();
								progressDialog = null;
							}
							return true; // 默认返回 false，这里false不能屏蔽返回键，改成true就可以了
						}else {
							return true;
						}
					}
				});
			if(progressDialog!=null&&!progressDialog.isShowing()){
			   progressDialog.show();
			}
		}else{
			   progressDialog.show();
		}
	}
	public static void dismisLoadingDialog(){
		if (progressDialog != null&&progressDialog.isShowing()) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}
	
	
//    private Dialog mDialog;
//    private RelativeLayout imgBg;
//    private TextView tipsTxt;
//    private RelativeLayout imgBg2;
//    private TextView tipsTxt2;
//    public void showRecordingDialog(Context context) {
//        mDialog = new Dialog(context, R.style.Theme_audioDialog);
//        // 用layoutinflater来引用布局
//        LayoutInflater inflater = LayoutInflater.from(context);
//        View view = inflater.inflate(R.layout.dialog_manager, null);
//        mDialog.setContentView(view);
//        imgBg = (RelativeLayout) view.findViewById(R.id.dm_rl_bg);
//        tipsTxt = (TextView) view.findViewById(R.id.dm_tv_txt);
//        imgBg2 = (RelativeLayout) view.findViewById(R.id.dm_rl_bg2);
//        tipsTxt2 = (TextView) view.findViewById(R.id.dm_tv_txt2);
//        mDialog.show();
//    }
//
//    /**
//     * 设置正在录音时的dialog界面
//     */
//    public void recording(Context context) {
//        if (mDialog != null && mDialog.isShowing()) {
//            imgBg.setVisibility(View.VISIBLE);
//            tipsTxt.setVisibility(View.VISIBLE);
//            imgBg2.setVisibility(View.GONE);
//            tipsTxt2.setVisibility(View.GONE);
//            imgBg.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.yuyin_voice_1));
//            tipsTxt.setText(R.string.up_for_cancel);
//        }
//    }
//
//    /**
//     * 取消界面
//     */
//    public void wantToCancel(Context context) {
//        if (mDialog != null && mDialog.isShowing()) {
//            imgBg.setVisibility(View.GONE);
//            tipsTxt.setVisibility(View.GONE);
//            imgBg2.setVisibility(View.VISIBLE);
//            tipsTxt2.setVisibility(View.VISIBLE);
//            imgBg2.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.yuyin_cancel));
//            tipsTxt2.setText(R.string.want_to_cancle);
//            tipsTxt2.setBackgroundColor(context.getResources().getColor(R.color.colorRedBg));
//
//        }
//
//    }
//
//    // 时间过短
//    public void tooShort(Context context) {
//        if (mDialog != null && mDialog.isShowing()) {
//            imgBg2.setVisibility(View.VISIBLE);
//            tipsTxt2.setVisibility(View.VISIBLE);
//            imgBg.setVisibility(View.GONE);
//            tipsTxt.setVisibility(View.GONE);
//            imgBg2.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.yuyin_gantanhao));
//            tipsTxt2.setText(R.string.time_too_short);
//        }
//
//    }
//
//    // 隐藏dialog
//    public void dimissDialog() {
//
//        if (mDialog != null && mDialog.isShowing()) {
//            mDialog.dismiss();
//            mDialog = null;
//        }
//
//    }
//
//    public void updateVoiceLevel(Context context,int level) {
//        if (level > 0 && level < 6) {
//
//        } else {
//            level = 5;
//        }
//        if (mDialog != null && mDialog.isShowing()) {
//
//            //通过level来找到图片的id，也可以用switch来寻址，但是代码可能会比较长
//            int resId = context.getResources().getIdentifier("yuyin_voice_" + level,
//                    "drawable", context.getPackageName());
//            imgBg.setBackgroundResource(resId);
//        }
//
//    }
//
//    public TextView getTipsTxt() {
//        return tipsTxt;
//    }
//
//    public void setTipsTxt(TextView tipsTxt) {
//        this.tipsTxt = tipsTxt;
//    }
}
