package com.jwzt.caibian.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jwzt.caibian.bean.QuanxianBean;
import com.jwzt.caibian.receiver.ExampleUtil;
import com.jwzt.caibian.util.BDLocationUtils;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.adapter.FragmentTabAdapter;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.application.Configs;
import com.jwzt.caibian.bean.LoginBean;
import com.jwzt.caibian.db.BaseFramgmentActivity;
import com.jwzt.caibian.fragment.FootageManageFragment;
import com.jwzt.caibian.fragment.HomeFragment;
import com.jwzt.caibian.fragment.MineFragment;
import com.jwzt.caibian.fragment.ScriptFragment;
import com.jwzt.caibian.receiver.TagAliasOperatorHelper;
import com.jwzt.caibian.receiver.TagAliasOperatorHelper.TagAliasBean;
import com.jwzt.caibian.util.ChatUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * 主界面
 *
 * @author awq
 */
@SuppressWarnings("rawtypes")
public class MainActivity extends BaseFramgmentActivity {
    private List<Fragment> fragments;
    private FragmentTabAdapter tabAdapter;
    private RadioGroup rg_radios;
    /**
     * 我的
     */
    private RadioButton mine;
    public static int sequence = 1;
    private CbApplication application;
    private LoginBean mLoginBean;
    private long mExitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //做登录页面操作








        rg_radios = (RadioGroup) findViewById(R.id.rg_radios);
        mine = (RadioButton) findViewById(R.id.mine);
        fragments = new ArrayList<Fragment>();
        fragments.add(new HomeFragment(getHelper()));//首页界面
        fragments.add(new ScriptFragment(getHelper())); //稿件界面
        fragments.add(new FootageManageFragment());//素材界面
        fragments.add(new MineFragment(getHelper()));//我的界面
        tabAdapter = new FragmentTabAdapter(MainActivity.this, fragments, R.id.fl_content, rg_radios);

        application = (CbApplication) getApplication();
        mLoginBean = application.getmLoginBean();
        initquanxian();// 获取用户审核权限
        if (mLoginBean != null) {
//			chatUtils.initData(mLoginBean.getUserID());
            String username = "";
            try {
                username = URLEncoder.encode(mLoginBean.getUsername(), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //	chatUtils.getToken(mLoginBean.getUserID(),username);
        }
        setAlias();
        BDLocationUtils.onStart(MainActivity.this);
    }

    // 这是来自 JPush Example 的设置别名的 Activity 里的代码。一般 App 的设置的调用入口，在任何方便的地方调用都可以。
    private void setAlias() {
     if(null!=mLoginBean){
         String userID = mLoginBean.getUserID();
         // 调用 Handler 来异步设置别名
         mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, userID));
     }

    }

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs ;
            System.out.println("===========code=="+code);
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                 //   Log.i(TAG, logs);
                    // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    System.out.println("==============Set tag and alias success");
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                  //  Log.i(TAG, logs);
                    // 延迟 60 秒来调用 Handler 设置别名
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
                   // Log.e(TAG, logs);

                    System.out.println("===========Failed with errorCode = " + code);
            }
         //   ExampleUtil.showToast(logs, getApplicationContext());
        }
    };
    private static final int MSG_SET_ALIAS = 1009;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                   // Log.d(TAG, "Set alias in handler.");
                    System.out.println("===========Set alias in handler."+msg.obj);
                    // 调用 JPush 接口来设置别名。
                    JPushInterface.setAliasAndTags(getApplicationContext(),
                            (String) msg.obj,
                            null,
                            mAliasCallback);
                    break;
                default:
                  ///  Log.i(TAG, "Unhandled msg - " + msg.what);

                    System.out.println("==========Unhandled msg - " + msg.what);

            }
        }
    };




    /**
     * 初始化权限
     */
    public void initquanxian() {
        String userid = mLoginBean.getUserID();
        HttpUtils utils = new HttpUtils();
        RequestParams params = new RequestParams();
        String url = Configs.shenhequanxian + userid;
        System.out.println("初始化权限==="+url);
        utils.send(HttpRequest.HttpMethod.GET, url, params, new RequestCallBack<String>() {
            @Override
            public void onLoading(long total, long current, boolean isUploading) {


            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Gson gson = new Gson();

                QuanxianBean quanxianBean = gson.fromJson(responseInfo.result, QuanxianBean.class);
                if(null!=quanxianBean){
                    if(quanxianBean.getStatus()==100){
                        Configs.firstChecks = quanxianBean.getData().isProgrammeFirstCheck();
                        Configs.secondChecks =quanxianBean.getData().isProgrammeSecondCheck();
                        Configs.newsReadOnly = false;
                    }
                }
//                try {
//                    JSONObject jsonObject = new JSONObject(
//                            responseInfo.result);
//                    //{"data":{"programmeFirstCheck":true,"programmeSecondCheck":false},"message":"成功。","status":100}
//                    String message = jsonObject.getString("message");
//                    String status = jsonObject.getString("status");
//                    if (status.equals("100")) {
//                        JSONObject jsonobjtct = jsonObject.getJSONObject("data");
//                        boolean firstCheck = jsonobjtct.getBoolean("programmeFirstCheck");
//                        boolean secondCheck = jsonobjtct.getBoolean("programmeSecondCheck");
//                        boolean newsReadOnly = jsonobjtct.getBoolean("newsReadOnly");
//                        Configs.firstChecks = firstCheck;
//                        Configs.secondChecks = secondCheck;
//                        Configs.newsReadOnly = newsReadOnly;
//                        System.out.println("newsReadOnly==="+newsReadOnly);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFailure(HttpException error, String msg) {
                System.out.println("初始化权限=====msg=="+msg);
            }
        });


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出采编", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                //清除Alients
                TagAliasBean tagAliasBean = new TagAliasBean();
                tagAliasBean.action = 3;
                sequence++;
                tagAliasBean.alias = mLoginBean.getUserID();
                tagAliasBean.isAliasAction = true;
                TagAliasOperatorHelper.getInstance().handleAction(getApplicationContext(), sequence, tagAliasBean);
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 切换到“我的”
     */
    public void switchToMine() {
//		rg_radios.check(R.id.mine);/
        mine.performClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case 1050:
//                System.out.println("requestCode===" + requestCode + "===resultCode==" + resultCode);
//            super.onActivityResult(requestCode, resultCode, data);
//                break;
//
//        }
      //  System.out.println("requestCode===="+requestCode+"===resultCode=="+resultCode);
        if (getSupportFragmentManager().getFragments() != null && getSupportFragmentManager().getFragments().size() > 0) {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            for (Fragment mFragment : fragments) {
                mFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}
