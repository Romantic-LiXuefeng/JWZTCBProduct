package com.jwzt.caibian.activity;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.application.Configs;
import com.jwzt.caibian.bean.GaoJianNum;
import com.jwzt.caibian.bean.LoginBean;
import com.jwzt.caibian.fragment.DaiSHenFragment;
import com.jwzt.caibian.fragment.TuiHuiFragment;
import com.jwzt.caibian.fragment.YiShenFragment;
import com.jwzt.caibian.view.SyncHorizontalScrollView;
import com.jwzt.cb.product.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.io.StringReader;


/**
 * 素材管理
 *
 * @author howie
 */
public class ShenheActivity extends FragmentActivity {
    public static String[] tabTitle = {"待审", "已审", "退回"}; // 标题
    private RelativeLayout rl_nav;
    private SyncHorizontalScrollView mHsv;
    private RadioGroup rg_nav_content;
    private ViewPager mViewPager;
    private ImageView iv_nav_indicator;
    private int indicatorWidth;
    private LayoutInflater mInflater;
    private TabFragmentPagerAdapter mAdapter;
    private FragmentManager manager;
    private int currentIndicatorLeft = 0;

    private LinearLayout iv_back;
    private TextView tv_titles;
    private CbApplication application;
    private LoginBean mLoginBean;
private Handler mHandler=new Handler(){
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what){
            case 0:
                String obj = (String) msg.obj;
                if(obj!=null){
                    Gson gson = new Gson();
                    GaoJianNum gaoJianNum = gson.fromJson(obj, GaoJianNum.class);
                    if(gaoJianNum!=null){
                        RadioButton childAt = (RadioButton) rg_nav_content.getChildAt(0);
                        childAt.setText("待审"+"("+gaoJianNum.getData()+")");
                    }
                }
                break;
            case 1:
                String obj1 = (String) msg.obj;
                if(obj1!=null){
                    Gson gson = new Gson();
                    GaoJianNum gaoJianNum = gson.fromJson(obj1, GaoJianNum.class);
                    if(gaoJianNum!=null){
                        RadioButton childAt = (RadioButton) rg_nav_content.getChildAt(1);
                        childAt.setText("已审"+"("+gaoJianNum.getData()+")");
                    }
                }
                break;
            case 2:
                String obj2 = (String) msg.obj;
                if(obj2!=null){
                    Gson gson = new Gson();
                    GaoJianNum gaoJianNum = gson.fromJson(obj2, GaoJianNum.class);
                    if(gaoJianNum!=null){
                        RadioButton childAt = (RadioButton) rg_nav_content.getChildAt(2);
                        childAt.setText("退回"+"("+gaoJianNum.getData()+")");
                    }
                }
                break;
        }
    }
};
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.layout_shenhe);
        manager = getSupportFragmentManager();
        application = (CbApplication) getApplication();
        mLoginBean = application.getmLoginBean();
        findView();
        initView();
        setListener();
        String liveListUrl = String.format(Configs.getChuanlianDanNum, mLoginBean.getUserID(), "1");
        String liveListUrl2 = String.format(Configs.getChuanlianDanNum, mLoginBean.getUserID(), "2");
        String liveListUrl3 = String.format(Configs.getChuanlianDanNum, mLoginBean.getUserID(), "3");


        getShenhe(liveListUrl, 1);
        getShenhe(liveListUrl2, 2);
        getShenhe(liveListUrl3, 3);
    }

    private void getShenhe(String url, final int i) {

        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart(); //开始的时候  做一些加载进度条的
            }

            @Override
            public void onCancelled() {
                super.onCancelled(); //取消加载的
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
           //     Toast.makeText(ShenheActivity.this, "success" + responseInfo.result.toString(), Toast.LENGTH_SHORT).show();
                Message obtain = Message.obtain();
                if(i==1){

    obtain.what=0;
    obtain.obj=responseInfo.result.toString();
}else if(i==2){
                    obtain.what=1;
                    obtain.obj=responseInfo.result.toString();
}else if(i==3){
                    obtain.what=2;
                    obtain.obj=responseInfo.result.toString();
}
mHandler.sendMessage(obtain);
            }

            @Override
            public void onFailure(HttpException e, String s) {
              //  Toast.makeText(MainActivity.this, "fail", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void findView() {
        iv_back = (LinearLayout) findViewById(R.id.ll_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShenheActivity.this.finish();
            }
        });
        tv_titles = (TextView) findViewById(R.id.tv_titles);
        tv_titles.setText("稿件审核");
        System.out.println("稿件审核==");
        rl_nav = (RelativeLayout) findViewById(R.id.rl_nav);
        mHsv = (SyncHorizontalScrollView) findViewById(R.id.mHsv);
        rg_nav_content = (RadioGroup) findViewById(R.id.rg_nav_content);
        iv_nav_indicator = (ImageView) findViewById(R.id.iv_nav_indicator);
        mViewPager = (ViewPager) findViewById(R.id.mViewPager_1);


    }
    //

    //初始化控件
    private void initView() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        indicatorWidth = dm.widthPixels / tabTitle.length;
        android.view.ViewGroup.LayoutParams cursor_Params = iv_nav_indicator.getLayoutParams();
        cursor_Params.width = indicatorWidth;// 初始化滑动下标的宽
        iv_nav_indicator.setLayoutParams(cursor_Params);

        mHsv.setSomeParam(rl_nav, null, null, ShenheActivity.this);
        mInflater = LayoutInflater.from(ShenheActivity.this);
        initNavigationHSV();
        mAdapter = new TabFragmentPagerAdapter(manager);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(3);
        rg_nav_content.getChildAt(0).performClick();
    }

    /**
     * TODO step1 初始化导航条(使用RadioGroup+RadioButton 根据屏幕宽度和可见数量 来设置RadioButton的宽度)
     */
    private void initNavigationHSV() {
        rg_nav_content.removeAllViews();
        for (int i = 0; i < tabTitle.length; i++) {
            RadioButton rb = (RadioButton) mInflater.inflate(R.layout.nav_radiogroupsecond_item, null);
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int a = dm.widthPixels;
            int b = dm.heightPixels;
            if (a == 1080 && b == 1812) {
                rb.setTextSize(12);
            } else {
                rb.setTextSize(14);
            }
            rb.setId(i);
            rb.setText(tabTitle[i]);
            rb.setBackgroundResource(R.drawable.select_title);
            rb.setLayoutParams(new ViewGroup.LayoutParams(indicatorWidth, ViewGroup.LayoutParams.MATCH_PARENT));
            rg_nav_content.addView(rb);
        }
    }

    /**
     * TODO step2 设置viewPage和RadioButton联动 一个点击另一个执行对应动作
     */
    private void setListener() {
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                if (rg_nav_content != null && rg_nav_content.getChildCount() > position) {
                    ((RadioButton) rg_nav_content.getChildAt(position)).performClick();
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        rg_nav_content.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rg_nav_content.getChildAt(checkedId) != null) {
                    TranslateAnimation animation = new TranslateAnimation(currentIndicatorLeft, ((RadioButton) rg_nav_content.getChildAt(checkedId)).getLeft(), 0f, 0f);
                    animation.setInterpolator(new LinearInterpolator());
                    animation.setDuration(100);
                    animation.setFillAfter(true);
                    // 执行位移动画
                    iv_nav_indicator.startAnimation(animation);
                    mViewPager.setCurrentItem(checkedId); // ViewPager
                    // 跟随一起 切换
                    // 记录当前 下标的距最左侧的 距离
                    currentIndicatorLeft = ((RadioButton) rg_nav_content.getChildAt(checkedId)).getLeft();
                    mHsv.smoothScrollTo((checkedId > 1 ? ((RadioButton) rg_nav_content.getChildAt(checkedId)).getLeft() : 0) - ((RadioButton) rg_nav_content.getChildAt(1)).getLeft(), 0);
                }
            }
        });
    }

    //fragment适配
    public class TabFragmentPagerAdapter extends FragmentPagerAdapter {
        public TabFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {
            Fragment fragment = null;
            System.out.println("arg0=====" + arg0);
            switch (arg0) {
                case 0:
                    fragment = new DaiSHenFragment();//待审
                    break;
                case 1:
                    fragment = new YiShenFragment();//已审
                    break;
                case 2:
                    fragment = new TuiHuiFragment();//已审
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return tabTitle.length;
        }
    }


}
