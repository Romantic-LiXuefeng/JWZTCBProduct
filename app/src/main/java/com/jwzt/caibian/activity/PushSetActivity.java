package com.jwzt.caibian.activity;

import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import java.util.LinkedHashSet;
import java.util.Set;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.CustomPushNotificationBuilder;
import cn.jpush.android.api.InstrumentedActivity;
import cn.jpush.android.api.JPushInterface;

import  com.jwzt.cb.product.R;
import static com.jwzt.caibian.receiver.TagAliasOperatorHelper.sequence;;

public class PushSetActivity extends InstrumentedActivity implements OnClickListener {
    private static final String TAG = "JIGUANG-Example";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        initListener();
        onTagAliasAction();
    }

    private void initListener() {
        setStyleBasic();
    }

    @Override
    public void onClick(View view) {}

  


    /**
     * 设置通知提示方式 - 基础属性
     */
    private void setStyleBasic() {
        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(PushSetActivity.this);
        builder.statusBarDrawable = R.drawable.logo1;
        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL
                | Notification.FLAG_SHOW_LIGHTS;  //设置为自动消失和呼吸灯闪烁
        builder.notificationDefaults = Notification.DEFAULT_SOUND
                | Notification.DEFAULT_VIBRATE
                | Notification.DEFAULT_LIGHTS;  // 设置为铃声、震动、呼吸灯闪烁都要
        JPushInterface.setPushNotificationBuilder(1, builder);
    }


    /**
     * 设置通知栏样式 - 定义通知栏Layout
     */
    private void setStyleCustom() {}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setAddActionsStyle() {}


    /**===========================================================================**/
    /**=========================TAG/ALIAS 相关=====================================**/
    /**===========================================================================**/

    /**
     * 处理tag/alias相关操作的点击
     * */
    public void onTagAliasAction() {
    	++sequence;
        JPushInterface.setAlias(PushSetActivity.this,sequence,"201");

    }

  
}