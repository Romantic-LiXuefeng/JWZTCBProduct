package com.jwzt.caibian.activity;


import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jwzt.caibian.fragment.ScriptFragment;
import com.jwzt.caibian.view.SyncHorizontalScrollView;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.application.Configs;
import com.jwzt.caibian.fragment.ChuanLianDanDaiSHenFragment;
import com.jwzt.caibian.fragment.ChuanLianDanYiShenFragment;
import com.jwzt.caibian.fragment.DaiSHenFragment;
import com.jwzt.caibian.fragment.TuiHuiFragment;
import com.jwzt.caibian.fragment.YiShenFragment;


/**
 * 素材管理
 * @author howie
 */
public class ChuanLianShenheActivity extends FragmentActivity{


	private int indicatorWidth;
	private ViewPager mViewPager;
	public static String[] tabTitle = { "待审", "已审"}; // 标题
	public RadioGroup rg_nav_content;
	private int currentIndicatorLeft = 0;
	private LinearLayout iv_back;
	private LiveViewPageAdapter manageAdapter;
	private TextView tv_titles,tv_select;
	private SyncHorizontalScrollView mHsv;
	private RelativeLayout rl_nav;
	private ImageView iv_nav_indicator;
	private LayoutInflater mInflater;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.layout_shenhe);
		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		indicatorWidth = (dm.widthPixels / 3) - 60;
		findViews();
		initView();
		//initNavigate();
	setListener();
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
	
	class MyCheckChangeListener implements OnCheckedChangeListener{

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
				mViewPager.setCurrentItem(checkedId); // ViewPager
		
	
	}
	}

	//初始化控件
	private void initView() {
		DisplayMetrics dm = new DisplayMetrics();

		getWindowManager().getDefaultDisplay().getMetrics(dm);


		indicatorWidth = dm.widthPixels / tabTitle.length;
		android.view.ViewGroup.LayoutParams cursor_Params = iv_nav_indicator.getLayoutParams();
		cursor_Params.width = indicatorWidth;// 初始化滑动下标的宽
		iv_nav_indicator.setLayoutParams(cursor_Params);

		mHsv.setSomeParam(rl_nav, null, null, this);
		mInflater = LayoutInflater.from(this);
		initNavigationHSV();

		ArrayList<Fragment> fragmentLive = new ArrayList<Fragment>();
		fragmentLive.add(new ChuanLianDanDaiSHenFragment());
		fragmentLive.add(new ChuanLianDanYiShenFragment());
		//fragmentLive.add(new TuiHuiFragment());

		manageAdapter = new LiveViewPageAdapter(this.getSupportFragmentManager(),fragmentLive);
		mViewPager.setAdapter(manageAdapter);


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
	private void findViews() {
		tv_titles = (TextView)this.findViewById(R.id.tv_titles);
		tv_titles.setText("串联单");
		mHsv = (SyncHorizontalScrollView) findViewById(R.id.mHsv);
		rg_nav_content=(RadioGroup) this.findViewById(R.id.rg_nav_content);
		rl_nav = (RelativeLayout) findViewById(R.id.rl_nav);
		iv_back=(LinearLayout) this.findViewById(R.id.ll_back);
		iv_nav_indicator = (ImageView) findViewById(R.id.iv_nav_indicator);
		//iv_back.setVisibility(View.GONE);
		iv_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		tv_select=(TextView) this.findViewById(R.id.tv_select);
		mViewPager=(ViewPager) this.findViewById(R.id.mViewPager_1);
		
		//mViewPager.setOffscreenPageLimit(2);//设置2个页面的缓存
	}
	
	/**
	 * 设置为非编辑状态
	 * @param checkedId
	 */
	class MyPagerChangeListener implements OnPageChangeListener{

		@Override
		public void onPageScrollStateChanged(int position) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}

		@Override
		public void onPageSelected(int position) {
			if (rg_nav_content != null&& rg_nav_content.getChildCount() > position) {
				((RadioButton) rg_nav_content.getChildAt(position)).performClick();
			}
		}
		
	}
	private void initNavigate(){
			for (int i = 0; i < tabTitle.length; i++) {
				RadioButton rb = (RadioButton) View.inflate(this, R.layout.nav_radiogroup_item, null);
//				RadioButton rb = (RadioButton) View.inflate(R.layout.nav_radiogroup_item, null);
				if(i==1){
					rb.setBackgroundResource(R.drawable.center_selector);
				}else if(i==2){
					rb.setBackgroundResource(R.drawable.right_selector);
				}
				rb.setId(i);
				rb.setText(tabTitle[i]);
				rb.setLayoutParams(new LayoutParams(indicatorWidth ,LayoutParams.MATCH_PARENT));
				rg_nav_content.addView(rb);
			}
	}
	
	
	public class LiveViewPageAdapter extends FragmentPagerAdapter {

		private ArrayList<Fragment> fragmenglist;

		public LiveViewPageAdapter(FragmentManager fm, ArrayList<Fragment> fragmenglist) {
			super(fm);
			this.fragmenglist = fragmenglist;
		}

		@Override
		public Fragment getItem(int arg0) {
			return fragmenglist.get(arg0);
		}

		@Override
		public int getCount() {
			return fragmenglist.size();
		}
	}
	
}
