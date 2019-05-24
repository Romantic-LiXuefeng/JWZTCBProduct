package com.jwzt.caibian.activity;

import java.util.ArrayList;
import java.util.List;

import org.greenrobot.eventbus.EventBus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jwzt.caibian.fragment.FootageManageFragment;
import com.jwzt.caibian.view.SyncHorizontalScrollView;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.adapter.FootageFragmentPagerAdapter;
import com.jwzt.caibian.bean.MessageEvent;
import com.jwzt.caibian.fragment.BaseFootageFragment;
import com.jwzt.caibian.interfaces.FragmentToParentInterface;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.UserToast;
import com.jwzt.caibian.xiangce.Bimp;
import com.jwzt.caibian.xiangce.ItemImage;
/**
 * 选择素材
 * @author howie
 *
 */
public class SelectFootageActivity extends FragmentActivity implements OnClickListener,FragmentToParentInterface {
	/**标题*/
	private TextView tv_titles;
	private RadioGroup rg_nav_content;
	private static String[] tabTitle = { "视频", "音频", "图片" }; // 标题
	private int indicatorWidth;
	private ViewPager vp;
	/**选用*/
	private TextView tv_select;
	/**返回按钮*/
	private View iv_back;
	private boolean isEdit;//表示是否是编辑状态
	private ImageView iv_plus,iv_file,iv_camera,iv_image,iv_audio;
	private FootageFragmentPagerAdapter ffpa;
//	private FootageFromBackFragmentPagerAdapter ffpa;
	private List<BaseFootageFragment> listFragment;
    private int currentIndicatorLeft = 0;
	private ArrayList<ItemImage> saveSelect;
	private ImageView iv_nav_indicator;
	private SyncHorizontalScrollView mHsv;
	private RelativeLayout rl_nav;
	private LayoutInflater mInflater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_footage);
		listFragment=new ArrayList<BaseFootageFragment>();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		saveSelect=(ArrayList<ItemImage>) getIntent().getSerializableExtra("saveselect");
		indicatorWidth = (dm.widthPixels / 3) - 60;
		findviews();
		initView();
        setListener();
		System.out.println("=====================>>"+Bimp.tempSelectBitmap.size());
	}
	//初始化控件
	private void initView() {
		DisplayMetrics dm = new DisplayMetrics();
		//if (getActivity() != null) {
			getWindowManager().getDefaultDisplay().getMetrics(dm);
	//	}

		indicatorWidth = dm.widthPixels / tabTitle.length;
		android.view.ViewGroup.LayoutParams cursor_Params = iv_nav_indicator.getLayoutParams();
		cursor_Params.width = indicatorWidth;// 初始化滑动下标的宽
		iv_nav_indicator.setLayoutParams(cursor_Params);

		mHsv.setSomeParam(rl_nav, null, null, this);
		mInflater = LayoutInflater.from(this);
		initNavigationHSV();
	 ffpa = new FootageFragmentPagerAdapter(getSupportFragmentManager(),SelectFootageActivity.this);
		vp.setAdapter(ffpa);
		vp.setOffscreenPageLimit(2);
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
	private void findviews() {
		tv_titles=(TextView) findViewById(R.id.tv_titles);
		tv_titles.setText("任务素材");
		
		iv_back=findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		tv_select=(TextView) findViewById(R.id.tv_select);
		tv_select.setOnClickListener(this);
		vp=(ViewPager) findViewById(R.id.vp);

	//	vp.setOffscreenPageLimit(2);//设置2个页面的缓存
		rg_nav_content=(RadioGroup) findViewById(R.id.rg_nav_content);
		mHsv = (SyncHorizontalScrollView) findViewById(R.id.mHsv1);
		rl_nav = (RelativeLayout)findViewById(R.id.rl_nav1);
		iv_nav_indicator = (ImageView) findViewById(R.id.iv_nav_indicator1);
		iv_file=(ImageView) this.findViewById(R.id.iv_file);
		iv_camera=(ImageView) this.findViewById(R.id.iv_camera);
		iv_audio=(ImageView) this.findViewById(R.id.iv_audio);
		iv_image=(ImageView) this.findViewById(R.id.iv_image);
		iv_plus = (ImageView) this.findViewById(R.id.iv_plus);
		iv_file.setVisibility(View.GONE);
		iv_camera.setVisibility(View.GONE);
		iv_audio.setVisibility(View.GONE);
		iv_image.setVisibility(View.GONE);
		iv_plus.setVisibility(View.GONE);
		initNavigate();

//		ffpa = new FootageFromBackFragmentPagerAdapter(getSupportFragmentManager());


	}
    /**
     * TODO step2 设置viewPage和RadioButton联动 一个点击另一个执行对应动作
     */
    private void setListener() {
        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

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
                    vp.setCurrentItem(checkedId); // ViewPager
                    // 跟随一起 切换
                    // 记录当前 下标的距最左侧的 距离
                    currentIndicatorLeft = ((RadioButton) rg_nav_content.getChildAt(checkedId)).getLeft();
                    mHsv.smoothScrollTo((checkedId > 1 ? ((RadioButton) rg_nav_content.getChildAt(checkedId)).getLeft() : 0) - ((RadioButton) rg_nav_content.getChildAt(1)).getLeft(), 0);
                }
            }
        });
    }
	private void initNavigate(){
		for (int i = 0; i < tabTitle.length; i++) {
			RadioButton rb = (RadioButton) View.inflate(SelectFootageActivity.this, R.layout.nav_radiogroup_item, null);
//			RadioButton rb = (RadioButton) View.inflate(R.layout.nav_radiogroup_item, null);
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
	
	/**
	 * 控制标题栏的编辑状态
	 * @param isChange
	 * 
	 * true   可编辑
	 * 
	 * false  不可编辑
	 */
	private void changEnable(boolean isChange){
		int childCount = rg_nav_content.getChildCount();
		for(int i=0;i<childCount;i++){
			rg_nav_content.getChildAt(i).setClickable(isChange);
		}
	}
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()){
		//选用
		case R.id.tv_select:
			ffpa.setSelect(vp.getCurrentItem());
			System.out.println("=====================>>"+Bimp.tempSelectBitmap.size());
			
			if(tv_select.getText().toString().equals("完成")){
				UserToast.toSetToast(SelectFootageActivity.this, "选择完成");
				Intent intentSave=new Intent();
				intentSave.putExtra("saveselect", saveSelect);
				setResult(8, intentSave);//2任意
				SelectFootageActivity.this.finish();
			}
			
			changEnable(isEdit);
			if(isEdit){
				isEdit=false;
				tv_select.setText("选用");
			}else{
				isEdit=true;
				tv_select.setText("完成");
			}
			
			EventBus.getDefault().post(new MessageEvent(0));
			EventBus.getDefault().post(new MessageEvent(1));
			EventBus.getDefault().post(new MessageEvent(2));
			break;
		case R.id.iv_back://返回按钮
			finish();
			overridePendingTransition(R.anim.push_left_out,
					R.anim.push_right_out);
			break;
		}
		
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent(SelectFootageActivity.this, NewScriptActivity.class);
			setResult(RESULT_OK, intent);
			finish();
			overridePendingTransition(R.anim.push_left_out,
					R.anim.push_right_out);
			return false;
		}
		return false;
	}
	@Override
	public void setListFragment(List<BaseFootageFragment> list) {
		// TODO Auto-generated method stub
		listFragment=list;
	}
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

}
