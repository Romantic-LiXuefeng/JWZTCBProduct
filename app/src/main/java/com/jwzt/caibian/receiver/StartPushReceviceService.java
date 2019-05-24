package com.jwzt.caibian.receiver;

import cn.jpush.android.service.PushService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartPushReceviceService extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Intent pushintent=new Intent(context,PushService.class);//启动极光推送的服务
        context.startService(pushintent);
	}

}
