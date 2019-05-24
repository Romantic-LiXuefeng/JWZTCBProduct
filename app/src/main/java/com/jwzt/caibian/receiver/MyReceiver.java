package com.jwzt.caibian.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.jwzt.caibian.activity.ChuanLianDaishenGaodanActivity;
import com.jwzt.caibian.activity.ChuanLianGaodanYiShenActivity;
import com.jwzt.caibian.activity.MainActivity;
import com.jwzt.caibian.activity.NotifyActivity;
import com.jwzt.caibian.activity.PreviewWanCHengActivity;
import com.jwzt.caibian.activity.ShowMessageDeatilsActivity;
import com.jwzt.caibian.activity.ShowTuiSongMessageDeatilsActivity;
import com.jwzt.caibian.activity.TaskDetailActivity;
import com.jwzt.caibian.bean.MessageAllBean;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "JIGUANG-Example";
    private String userid;
    private String messageid;
    private MessageAllBean messageAllBean1;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            SharedPreferences sp = context.getSharedPreferences("message", Activity.MODE_PRIVATE);

            Bundle bundle = intent.getExtras();
            String ss = printBundle(bundle);
            System.out.println("ss===="+ss);
            String string = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Gson gson = new Gson();
            messageAllBean1 = gson.fromJson(string, MessageAllBean.class);
            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                //send the Registration Id to your server...

            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                //processCustomMessage(context, bundle);
                String data = bundle.getString(JPushInterface.EXTRA_MESSAGE);
                System.out.println("data==="+data);
                if (data != null) {
                    try {//{"id":"511","userId":"201","msg":"采访任务消息:五月五日 新闻组织 夏令营"}
                        JSONObject jsonObject = new JSONObject(data);
                        userid = jsonObject.getString("userId");
                        messageid = jsonObject.getString("id");
					   /*news_id = jsonObject.getString("news_id"); // id
						news_title = jsonObject.getString("news_title"); // news_title
						node_id = jsonObject.getString("node_id"); // node_id
						site_id = jsonObject.getString("site_id"); // site_id
						type = jsonObject.getString("type"); // site_id
						Editor editor = sp.edit();
						editor.putString("news_id", news_id);
						editor.putString("node_id", node_id);
						editor.putString("site_id", site_id);*/
                        //editor.commit();
                        Editor editor = sp.edit();
                        editor.putString("userid", userid);
                        editor.putString("messageid", messageid);
                        editor.commit();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);

            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                //context.get
//                String dis = sp.getString("messageid", "");
//                Intent is = new Intent(context, ShowTuiSongMessageDeatilsActivity.class);
//                is.putExtra("userid", dis);
//                is.putExtra("ids", dis);
//                is.putExtra("messageid", dis);
//                is.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                context.startActivity(is);
                if(null!=messageAllBean1) {
                    String parentType = messageAllBean1.getParentType();
                    System.out.println("parentType=====" + parentType);
                    Intent intent2 = new Intent();

                    switch (Integer.parseInt(parentType)) {
                        case 1:
                            intent2.setClass(context, TaskDetailActivity.class);
//                        intent.putExtra("id",messageAllBean1.getParentId());
                            intent2.putExtra("id", messageAllBean1.getParentId());
                            intent2.putExtra("pid", messageAllBean1.getId());
                            intent2.putExtra("state", messageAllBean1.getState());
                            System.out.println("state=======" + messageAllBean1.getState());
                            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            context.startActivity(intent2);
                            break;
                        case 2:
                            intent2.setClass(context, PreviewWanCHengActivity.class);
                            System.out.println("pid=====" + messageAllBean1.getId());
                            intent2.putExtra("id", messageAllBean1.getParentId());
                            intent2.putExtra("state", messageAllBean1.getState());
                            intent2.putExtra("pid", messageAllBean1.getId());

                            intent2.putExtra("operateType", messageAllBean1.getParentType());
                            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            context.startActivity(intent2);
                            break;
                        case 3:
                            intent2.setClass(context, PreviewWanCHengActivity.class);
                            System.out.println("pid=====" + messageAllBean1.getId());
                            intent2.putExtra("pid", messageAllBean1.getId());
                            intent2.putExtra("id", messageAllBean1.getParentId());
                            intent2.putExtra("state", messageAllBean1.getState());
                            intent2.putExtra("operateType", messageAllBean1.getParentType());
                            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            context.startActivity(intent2);
                            break;
                        case 4:
                            intent2.setClass(context, PreviewWanCHengActivity.class);
                            intent2.putExtra("pid", messageAllBean1.getId());
                            System.out.println("pid=====" + messageAllBean1.getId());
                            intent2.putExtra("id", messageAllBean1.getParentId());
                            intent2.putExtra("state", messageAllBean1.getState());
                            intent2.putExtra("operateType", messageAllBean1.getParentType());

                            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            context.startActivity(intent2);
                            break;
                        case 5://通知
                            intent2.setClass(context, NotifyActivity.class);

                            intent2.putExtra("state", messageAllBean1.getState());
                            intent2.putExtra("messageAllBean", messageAllBean1);
                            intent2.putExtra("type", "5");
                            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            context.startActivity(intent2);
                            break;
                        case 6://通知
                            intent2.setClass(context, NotifyActivity.class);
                            System.out.println("state====" + messageAllBean1.getState());
                            intent2.putExtra("state", messageAllBean1.getState());
                            intent2.putExtra("messageAllBean", messageAllBean1);
                            intent2.putExtra("type", "6");
                            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            context.startActivity(intent2);
                            break;
                        case 7://通知
                            intent2.setClass(context, NotifyActivity.class);
                            intent2.putExtra("type", "7");
                            intent2.putExtra("state", messageAllBean1.getState());
                            intent2.putExtra("messageAllBean", messageAllBean1);
                            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            context.startActivity(intent2);
                            break;
                        case 8://通知
                            intent2.setClass(context, NotifyActivity.class);
                            intent2.putExtra("type", "8");
                            intent2.putExtra("state", messageAllBean1.getState());
                            intent2.putExtra("messageAllBean", messageAllBean1);
                            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            context.startActivity(intent2);
                            break;
                        case 9://串联单
                            intent2.setClass(context, ChuanLianDaishenGaodanActivity.class);

                            intent2.putExtra("type", "2");
                            intent2.putExtra("id", messageAllBean1.getParentId());
                            intent2.putExtra("messageAllBean", messageAllBean1);
                            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            context.startActivity(intent2);
                            break;

                        case 10://串联单
                            intent2.setClass(context, ChuanLianGaodanYiShenActivity.class);

                            intent2.putExtra("type", "2");
                            intent2.putExtra("id", messageAllBean1.getParentId());
                            intent2.putExtra("messageAllBean", messageAllBean1);
                            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            context.startActivity(intent2);
                            break;
                        case 11://串联单
                            intent2.setClass(context, ChuanLianGaodanYiShenActivity.class);

                            intent2.putExtra("type", "2");
                            intent2.putExtra("id", messageAllBean1.getParentId());
                            intent2.putExtra("messageAllBean", messageAllBean1);
                            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            context.startActivity(intent2);
                            break;
                        case 12://通知
                            intent2.setClass(context, NotifyActivity.class);
                            intent2.putExtra("type", "12");
                            intent2.putExtra("state", messageAllBean1.getState());
                            intent2.putExtra("messageAllBean", messageAllBean1);
                            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            context.startActivity(intent2);
                            break;
                    }
                }

            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

            } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            } else {
            }
        } catch (Exception e) {

        }

    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }


}
