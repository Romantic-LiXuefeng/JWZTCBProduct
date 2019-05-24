package com.jwzt.caibian.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.application.Configs;
import com.jwzt.caibian.util.UserToast;

/**
 * 监听网络类型改变的广播
 * 
 * @author hly
 * 
 */
public class MyNetReceiver extends BroadcastReceiver {
	private ConnectivityManager mConnectivityManager;
	private NetworkInfo netInfo;
//	private CbApplication application;
//	private Context mContext;

	@SuppressWarnings("unused")
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			netInfo = mConnectivityManager.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isAvailable()) {
				//网络连接
				String name = netInfo.getTypeName();
				if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
					//WiFi网络
					CbApplication.setNetType(1);
					UserToast.toSetToast(context, context.getResources().getString(R.string.wifiON));
				} else if (netInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
					//有线网络
					CbApplication.setNetType(2);
				} else if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
					//2g 3g 4g网络
					UserToast.toSetToast(context, context.getResources().getString(R.string.wifiOFF_FlowON));
					CbApplication.setNetType(0);
				}
				if(Configs.mContinueInterface!=null){
					Configs.mContinueInterface.setContinueUp(true);
				}
				
			} else {
				 //网络断开
				UserToast.toSetToast(context, context.getResources().getString(R.string.netoff));
				CbApplication.setNetType(-1);
			}
		}
	}
}
