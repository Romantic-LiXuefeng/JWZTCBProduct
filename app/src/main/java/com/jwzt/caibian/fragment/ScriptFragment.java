package com.jwzt.caibian.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.jwzt.caibian.activity.NewScriptActivity;
import com.jwzt.caibian.db.DatabaseHelper;
import com.jwzt.caibian.view.SyncHorizontalScrollView;
import com.jwzt.cb.product.R;

/**
 * 稿件
 *
 * @author howie
 */
@SuppressLint("ValidFragment")
public class ScriptFragment extends Fragment implements OnClickListener {
    private View iv_plus;
    private DatabaseHelper mDatabaseHelper;

    public static String[] tabTitle = {"已上传", "未上传"};
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
    private View view;

    @SuppressLint("ValidFragment")
    public ScriptFragment(DatabaseHelper databaseHelper) {
        this.mDatabaseHelper = databaseHelper;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = getChildFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = View.inflate(getActivity(), R.layout.fragment_gaojian, null);
        findView();
        initView();
        setListener();
        return view;
    }

    private void findView() {
        rl_nav = (RelativeLayout) view.findViewById(R.id.rl_nav);
        mHsv = (SyncHorizontalScrollView) view.findViewById(R.id.mHsv);
        rg_nav_content = (RadioGroup) view.findViewById(R.id.rg_nav_content);
        iv_nav_indicator = (ImageView) view.findViewById(R.id.iv_nav_indicator);
        mViewPager = (ViewPager) view.findViewById(R.id.mViewPager_1);
        iv_plus = view.findViewById(R.id.iv_plus);
        iv_plus.setOnClickListener(this);
        View back_im = view.findViewById(R.id.back_im);
        back_im.setVisibility(View.INVISIBLE);
        back_im.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    //初始化控件
    private void initView() {
        DisplayMetrics dm = new DisplayMetrics();
        if (getActivity() != null) {
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        }

        indicatorWidth = dm.widthPixels / tabTitle.length;
        android.view.ViewGroup.LayoutParams cursor_Params = iv_nav_indicator.getLayoutParams();
        cursor_Params.width = indicatorWidth;// 初始化滑动下标的宽
        iv_nav_indicator.setLayoutParams(cursor_Params);

        mHsv.setSomeParam(rl_nav, null, null, getActivity());
        mInflater = LayoutInflater.from(getActivity());
        initNavigationHSV();
        mAdapter = new TabFragmentPagerAdapter(manager);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(2);
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
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
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
            switch (arg0) {
                case 0:
                    fragment = new UploadedFragment();//已上传
                    break;
                case 1:
                    fragment = new UploadingFragment(mDatabaseHelper);//未上传
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return tabTitle.length;
        }
    }


    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub
        switch (view.getId()) {
            case R.id.iv_plus://右上角的加号
                Intent intent = new Intent(getActivity(), NewScriptActivity.class);
                intent.putExtra("isFromIndex", true);
                intent.putExtra("tag", "speediness");
                startActivityForResult(intent, 1);
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }
}
