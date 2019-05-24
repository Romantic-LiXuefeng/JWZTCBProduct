package com.jwzt.caibian.fragment;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jwzt.caibian.rd.CameraWatermarkBuilder;
import com.jwzt.caibian.util.RDSdkUtils;
import com.jwzt.caibian.view.SyncHorizontalScrollView;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.adapter.FootageFragmentPagerAdapter;
import com.jwzt.caibian.bean.EditMessage;
import com.jwzt.caibian.bean.MessageEvent;
import com.jwzt.caibian.bean.NewMessage;
import com.jwzt.caibian.bean.PickMessage;
import com.jwzt.caibian.bean.PlayCompleteMessage;
import com.jwzt.caibian.bean.PlusMessage;
import com.jwzt.caibian.bean.ResetMessage;
import com.jwzt.caibian.bean.CloseMediaButton;
import com.jwzt.caibian.bean.ShowPlusMessage;
import com.jwzt.caibian.interfaces.FragmentToParentInterface;
import com.jwzt.caibian.util.GETImageUntils;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.MyWindowManager;
import com.jwzt.caibian.widget.FloatWindowSmallView;
import com.jwzt.caibian.xiangce.Bimp;
import com.rd.veuisdk.SdkEntry;

import static com.rd.veuisdk.manager.CameraConfiguration.SQUARE_SCREEN_CAN_CHANGE;


/**
 * 素材管理
 * @author howie
 *
 */
public class FootageManageFragment extends Fragment implements OnClickListener,FragmentToParentInterface{
	/**用于记录当前正在展示的fragment的id*/
	private int mCheckedId=-1;
	
	/**从本地拾取视频*/
	private static final int PICK_VIDEO=1;
	/**加号按钮 是否可见*/
	private boolean isPlusVisible=true;
	private boolean isRegistered;
	/**记录viewpager当前显示的是哪一个*/
	private int currentSelected;
	private boolean isOpen;
	private static int yDelta=-150;
	private ImageView iv_plus,iv_file,iv_camera,iv_image,iv_audio; 
	private int currentIndicatorLeft = 0;
	private int indicatorWidth;
	private ViewPager mViewPager;
	public static String[] tabTitle = { "视频", "音频", "图片" }; // 标题
	private RadioGroup rg_nav_content;
	private ImageView iv_back,iv_close;
	private List<BaseFootageFragment> mFragmentList;
	private FootageFragmentPagerAdapter manageAdapter;
	private boolean isEdit;//表示是否是编辑状态

	private TextView tv_titles,tv_select;
	private RDSdkUtils rdSdkUtils;
	private RelativeLayout rl_nav;
	private SyncHorizontalScrollView mHsv;
	private ImageView iv_nav_indicator;
	private LayoutInflater mInflater;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		System.out.println("isRegisteredisRegistered"+isRegistered);
		if(!isRegistered){
			EventBus.getDefault().register(FootageManageFragment.this);
			isRegistered=true;
		}
		rdSdkUtils = new RDSdkUtils(getActivity());
		rdSdkUtils.initEditorUIAndExportConfig();
		View view=View.inflate(getActivity(), R.layout.layout_footage, null);
		mFragmentList=new ArrayList<BaseFootageFragment>();
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		indicatorWidth = (dm.widthPixels / 3) - 60;
		findViews(view);
		initView();
	//	initNavigate();

	//	mViewPager.setAdapter(manageAdapter);
//		mViewPager.setOnPageChangeListener(new MyPagerChangeListener());
//		rg_nav_content.setOnCheckedChangeListener(new MyCheckChangeListener());
//		rg_nav_content.check(0);
		setListener();
		return view;
		
	}
	private void findViews(View view) {
		tv_titles = (TextView)view.findViewById(R.id.tv_titles);
		tv_titles.setText("视频素材");
		iv_plus = (ImageView) view.findViewById(R.id.iv_plus);
		iv_plus.setOnClickListener(this);
		iv_file=(ImageView) view.findViewById(R.id.iv_file);
		iv_camera=(ImageView) view.findViewById(R.id.iv_camera);
		iv_image=(ImageView) view.findViewById(R.id.iv_image);
		iv_audio=(ImageView) view.findViewById(R.id.iv_audio);
		rg_nav_content=(RadioGroup) view.findViewById(R.id.rg_nav_content);
		iv_back=(ImageView) view.findViewById(R.id.iv_back);
		iv_back.setVisibility(View.INVISIBLE);
		tv_select=(TextView) view.findViewById(R.id.tv_select);
		iv_close=(ImageView) view.findViewById(R.id.iv_close);
		rl_nav = (RelativeLayout) view.findViewById(R.id.rl_nav1);
		mHsv = (SyncHorizontalScrollView) view.findViewById(R.id.mHsv1);
		iv_nav_indicator = (ImageView) view.findViewById(R.id.iv_nav_indicator1);
		iv_close.setVisibility(View.INVISIBLE);
		iv_file.setOnClickListener(this);
		iv_camera.setOnClickListener(this);
		iv_audio.setOnClickListener(this);
		iv_image.setOnClickListener(this);
		tv_select.setOnClickListener(this);
		iv_close.setOnClickListener(this);
		mViewPager=(ViewPager) view.findViewById(R.id.vp);
		mViewPager.setOffscreenPageLimit(2);//设置2个页面的缓存
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
		manageAdapter = new FootageFragmentPagerAdapter(getFragmentManager(),FootageManageFragment.this);
		mViewPager.setAdapter(manageAdapter);
		mViewPager.setOffscreenPageLimit(2);
		rg_nav_content.getChildAt(0).performClick();
	}

	@Override
	public void onResume() {
		if(!isRegistered){
			EventBus.getDefault().register(FootageManageFragment.this);
			isRegistered=true;
		}
		super.onResume();

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
	class MyCheckChangeListener implements OnCheckedChangeListener{

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
				mViewPager.setCurrentItem(checkedId); // ViewPager
				mCheckedId=checkedId;
				
//				EventBus.getDefault().post(new ResetMessage());	//发送空消息重置视频、音频、图片素材适配器为非编辑状态								// 跟随一起 切换
				if(isEdit){
//					EventBus.getDefault().post(new MessageEvent(mCheckedId));
				}else{
					reset(checkedId);
				}
		}
	
	}
	/**
	 * 设置为非编辑状态
	 * @param checkedId
	 */
	public void reset(int checkedId) {
		EventBus.getDefault().post(new ResetMessage());	
		if(IsNonEmptyUtils.isList(mFragmentList)){
			Fragment fragment = mFragmentList.get(checkedId);
			if(checkedId==0){//视频列表
				tv_titles.setText("视频素材");
				FootageVideoFragment fvf=(FootageVideoFragment)fragment;
				if(!fvf.isEditing()){//如果处于非编辑状态
					isPlusVisible=true;
//						iv_plus.setVisibility(View.VISIBLE);  
					show();
				}else{
					isPlusVisible=false;
//						iv_plus.setVisibility(View.INVISIBLE);  
					hide();
				}
			}else if(checkedId==1){//音频列表
				tv_titles.setText("音频素材");
				FootageAudioFragment faf=(FootageAudioFragment)fragment;
				if(!faf.isEditing()){
					isPlusVisible=true;
//						iv_plus.setVisibility(View.VISIBLE);
					show();
				}else{
					isPlusVisible=false;
//						iv_plus.setVisibility(View.INVISIBLE);
					hide();
				}
			}else if(checkedId==2){//音频列表
				tv_titles.setText("图片素材");
				FootageImageFragment fif=(FootageImageFragment)fragment;
//				FootageAudioFragment fif=(FootageAudioFragment)fragment;
				if(!fif.isEditing()){
					isPlusVisible=true;
					show();
				}else{
					isPlusVisible=false;
					hide();
				}
			}
			
			FloatWindowSmallView smallWindow = MyWindowManager.getSmallWindow();isPlusVisible=true;
			if(smallWindow!=null){//切换的时候就移除小窗口
				smallWindow.onExit();
			}
		}

	}
	class MyPagerChangeListener implements OnPageChangeListener{

		@Override
		public void onPageScrollStateChanged(int position) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}

		@Override
		public void onPageSelected(int position) {
			currentSelected=position;
			if (rg_nav_content != null&& rg_nav_content.getChildCount() > position) {
				((RadioButton) rg_nav_content.getChildAt(position)).performClick();
			}
		}
		
	}
//	private void initNavigate(){
//			for (int i = 0; i < tabTitle.length; i++) {
//				RadioButton rb = (RadioButton) View.inflate(getActivity(), R.layout.nav_radiogroup_item, null);
////				RadioButton rb = (RadioButton) View.inflate(R.layout.nav_radiogroup_item, null);
//				if(i==1){
//					rb.setBackgroundResource(R.drawable.center_selector);
//				}else if(i==2){
//
//					rb.setBackgroundResource(R.drawable.right_selector);
//				}
//				rb.setId(i);
//				rb.setText(tabTitle[i]);
//
//				rb.setLayoutParams(new LayoutParams(indicatorWidth ,LayoutParams.MATCH_PARENT));
//				rg_nav_content.addView(rb);
//			}
//	}
	/**
	 * REQUEST_CODE定义：<br>
	 * 录制
	 */
	private final int CAMERA_REQUEST_CODE = 100;
	@Override
	public void onClick(View v) {
		Intent intent=new Intent();
		switch(v.getId()){
		case R.id.iv_plus:
			if(isOpen){
				close();
			}else{
				expand();
			}
			break;
		case R.id.iv_file:
//			Toast.makeText(getActivity(), "你点击了文件", 0).show();
			close();
			pick(currentSelected);
			break;
		case R.id.iv_camera://视频
//			Toast.makeText(getActivity(), "你点击了视频", 0).show();
			close();
			mViewPager.setCurrentItem(0);
		//	EventBus.getDefault().post(new NewMessage(NewMessage.TYPE_VIDEO));
			SdkEntry.registerOSDBuilder(CameraWatermarkBuilder.class);
			CameraWatermarkBuilder.setText("北京经纬中天");// 可自定义水印显示文本
			rdSdkUtils.initCameraConfig(SQUARE_SCREEN_CAN_CHANGE);
			SdkEntry.record((Activity)getActivity(), CAMERA_REQUEST_CODE);
			break;
		case R.id.iv_image://图片
//			Toast.makeText(getActivity(), "你点击了图片", 0).show();
//			intent.setClass(getActivity(), CameraAty.class);
//			startActivity(intent);
			close();
			mViewPager.setCurrentItem(2);
			EventBus.getDefault().post(new NewMessage(NewMessage.TYPE_IMAGE));
			break;
		case R.id.iv_audio://音频
//			intent.setClass(getActivity(), AudioActivity.class);
//			startActivity(intent);
			close();
			mViewPager.setCurrentItem(1);
			EventBus.getDefault().post(new NewMessage(NewMessage.TYPE_AUDIO));
			break;
		case R.id.tv_select:
			//TODO
			changEnable(isEdit);
			if(isEdit){
				isEdit=false;
				show();
				tv_select.setText("选用");
				if(IsNonEmptyUtils.isList(Bimp.tempSelectBitmap)){
					Bimp.tempSelectBitmap.clear();
				}
			}else{
				isEdit=true;
				hide();
				tv_select.setText("完成");
				if(IsNonEmptyUtils.isList(Bimp.tempSelectBitmap)){
					Bimp.tempSelectBitmap.clear();
					Bimp.tempSelectBitmap.add(null);
				}else{
					Bimp.tempSelectBitmap.add(null);
				}
			}
			
			EventBus.getDefault().post(new MessageEvent(0));
			EventBus.getDefault().post(new MessageEvent(1));
			EventBus.getDefault().post(new MessageEvent(2));
			break;
		case R.id.iv_close://点击关闭按钮
			iv_close.setVisibility(View.GONE);
			tv_select.setVisibility(View.VISIBLE);
			EventBus.getDefault().post(new PlayCompleteMessage());//点击关闭按钮发送改消息通知其对应的子fragment更新列表
			break;
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
	
	
	
	/**
	 * 拾取文件
	 * @param current
	 */
	private void pick(int current) {
		switch(current){
		case 0://视频
            Intent intent1 = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);   //, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            String path = Environment.getExternalStorageDirectory() + "/DCIM/Camera";
            Uri uri = Uri.fromFile(new File(path));
            intent1.setData(uri);
            getContext().sendBroadcast(intent1);
			EventBus.getDefault().post(new PickMessage(PickMessage.TYPE_VIDEO));
			break;
		case 1://音频
            Intent intent2 = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);   //, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            String path2 = Environment.getExternalStorageDirectory() +"";
            Uri uri2 = Uri.fromFile(new File(path2));
            intent2.setData(uri2);
            getContext().sendBroadcast(intent2);
			EventBus.getDefault().post(new PickMessage(PickMessage.TYPE_AUDIO));
			break;
		case 2://图片
            Intent intent3 = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);   //, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            String path3 = Environment.getExternalStorageDirectory() + "/DCIM/Camera";
            Uri uri3 = Uri.fromFile(new File(path3));
            intent3.setData(uri3);
            getContext().sendBroadcast(intent3);

			EventBus.getDefault().post(new PickMessage(PickMessage.TYPE_IMAGE));
			break;
		}
	}
	/**
	 * 展开动画
	 */
	private void expand(){
		iv_audio.setVisibility(View.VISIBLE);
		iv_camera.setVisibility(View.VISIBLE);
		iv_file.setVisibility(View.VISIBLE);
		iv_image.setVisibility(View.VISIBLE);
		 AnimatorSet set = new AnimatorSet();
		 set.playTogether(
				 ObjectAnimator.ofFloat(iv_file, "translationX", -210).setDuration(300),
				 ObjectAnimator.ofFloat(iv_file, "translationY", yDelta).setDuration(300),

				  ObjectAnimator.ofFloat(iv_image, "translationX", 210).setDuration(300),
				 ObjectAnimator.ofFloat(iv_image, "translationY", yDelta).setDuration(300),
				 
				  ObjectAnimator.ofFloat(iv_audio, "translationX", 70).setDuration(300),
				 ObjectAnimator.ofFloat(iv_audio, "translationY", yDelta).setDuration(300),
				 
				 ObjectAnimator.ofFloat(iv_camera, "translationX", -70).setDuration(300),
				 ObjectAnimator.ofFloat(iv_camera, "translationY", yDelta).setDuration(300),
				 ObjectAnimator.ofFloat(iv_plus, "rotation", 45)
				 );
		 set.setDuration(300).setInterpolator(new OvershootInterpolator());
		 set.start();
		isOpen=true;
	}
	/**
	 * 收起动画
	 */
	private void close(){
		 AnimatorSet set = new AnimatorSet();
		 set.playTogether(
				 ObjectAnimator.ofFloat(iv_file, "translationX", 0).setDuration(300),
				 ObjectAnimator.ofFloat(iv_file, "translationY", 0).setDuration(300),

				  ObjectAnimator.ofFloat(iv_image, "translationX", 0).setDuration(300),
				 ObjectAnimator.ofFloat(iv_image, "translationY", 0).setDuration(300),
				 
				  ObjectAnimator.ofFloat(iv_audio, "translationX", 0).setDuration(300),
				 ObjectAnimator.ofFloat(iv_audio, "translationY", 0).setDuration(300),
				 
				 ObjectAnimator.ofFloat(iv_camera, "translationX", 0).setDuration(300),
				 ObjectAnimator.ofFloat(iv_camera, "translationY", 0).setDuration(300),
				 ObjectAnimator.ofFloat(iv_plus, "rotation", 0)
				 );
		 set.setDuration(300);
		 set.start();
		 isOpen=false;
	}
	private void show(){
		iv_audio.setVisibility(View.VISIBLE);
		iv_camera.setVisibility(View.VISIBLE);
		iv_file.setVisibility(View.VISIBLE);
		iv_image.setVisibility(View.VISIBLE);
		iv_plus.setVisibility(View.VISIBLE);
	}
	private void hide(){
		iv_audio.setVisibility(View.INVISIBLE);
		iv_camera.setVisibility(View.INVISIBLE);
		iv_file.setVisibility(View.INVISIBLE);
		iv_image.setVisibility(View.INVISIBLE);
		iv_plus.setVisibility(View.INVISIBLE);
	}
	@Subscribe (threadMode=ThreadMode.POSTING)
	public void getMessage(EditMessage message){
		if(IsNonEmptyUtils.isList(mFragmentList)){
			Fragment fragment = mFragmentList.get(mCheckedId);
			if(mCheckedId==0){
				FootageVideoFragment fvf=(FootageVideoFragment)fragment;
				if( fvf.isEditing()){//如果当前处于编辑状态
					show();
				}else{
					hide();
				}
			}else if(mCheckedId==1){
				FootageAudioFragment faf=(FootageAudioFragment)fragment;
				if( faf.isEditing()){//如果当前处于编辑状态
					show();
				}else{
					hide();
				}
			}else if(mCheckedId==2){
				FootageImageFragment fif=(FootageImageFragment)fragment;
				if( fif.isEditing()){//如果当前处于编辑状态
					show();
				}else{
					hide();
				}
			}
			
//			isPlusVisible=!isPlusVisible;
//			switchPlusVisibility(isPlusVisible);
			EventBus.getDefault().post(new MessageEvent(currentSelected));
		}
	}
	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(FootageManageFragment.this);
		isRegistered=false;
		super.onDestroy();
	}

	/**
	 * 切换加号按钮是否可见
	 */
	private void switchPlusVisibility(boolean isVisible){
		if(isVisible){//可见
			iv_audio.setVisibility(View.VISIBLE);
			iv_camera.setVisibility(View.VISIBLE);
			iv_file.setVisibility(View.VISIBLE);
			iv_image.setVisibility(View.VISIBLE);
			iv_plus.setVisibility(View.VISIBLE);
		}else{
			iv_audio.setVisibility(View.INVISIBLE);
			iv_camera.setVisibility(View.INVISIBLE);
			iv_file.setVisibility(View.INVISIBLE);
			iv_image.setVisibility(View.INVISIBLE);
			iv_plus.setVisibility(View.INVISIBLE);
		}
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		if(requestCode==PICK_VIDEO&&data!=null){//从本地拾取视频
//			Uri uri = data.getData();
//			if(uri!=null){
////				String[] filePathColumn = { MediaColumns.DATA };
//				String picturePath = GETImageUntils.getPath(getActivity(), uri);
//				System.out.println("我是选择图片地址："+picturePath);
//				String picName = "";
//				picName = picturePath.substring(picturePath.lastIndexOf("/") + 1);
//			}
//		}
//		if(requestCode==1||requestCode==2||requestCode==3) {
//			if (getFragmentManager().getFragments() != null && getFragmentManager().getFragments().size() > 0) {
//				List<Fragment> fragments = getFragmentManager().getFragments();
//				for (Fragment mFragment : fragments) {
//
//					mFragment.onActivityResult(requestCode, resultCode, data);
//				}
//			}
//		}
	}
	/**
	 * 如果加号点击菜单是展开的就收起来
	 */
	@Subscribe
	public void closeIfOpen(PlusMessage plusMessage){
		if(isPlusVisible&&isOpen){//如果加号菜单可见并且是展开的
			close();
		}
	}
	@Subscribe
	public void showPlus(ShowPlusMessage message){
		switchPlusVisibility(message.isVisible());
	}
	
	
	@Subscribe (threadMode=ThreadMode.POSTING)
	public void showCloseButton(CloseMediaButton showClosebutton){
	//	iv_close.setVisibility(View.VISIBLE);
		tv_select.setVisibility(View.GONE);
	}
	
	@Subscribe (threadMode=ThreadMode.POSTING)
	public void hideCloseButton(CloseMediaButton showClosebutton){
		iv_close.setVisibility(View.GONE);
		tv_select.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 当前正在显示的fragment是否正在处于编辑状态
	 * @return
	 */
	public boolean isCurrentEditing(){
		Fragment fragment = manageAdapter.getFragments().get(currentSelected);
		if(currentSelected==0){
			return ((FootageVideoFragment)fragment).isEditing();
		}else if(currentSelected==1){
			return ((FootageAudioFragment)fragment).isEditing();
		}else{
			return ((FootageImageFragment)fragment).isEditing();
		}
		
		
	}
	/**
	 * 设置正在显示的fragment的列表为非编辑状态
	 */
	public void setNotEditing() {
		Fragment fragment = manageAdapter.getFragments().get(currentSelected);
		if(currentSelected==0){
			FootageVideoFragment fvf=((FootageVideoFragment)fragment);
			fvf.setNotEditing();
		}else if(currentSelected==1){
			FootageAudioFragment faf=((FootageAudioFragment)fragment);
			faf.setNotEditing();
		}else{
			FootageImageFragment fif=((FootageImageFragment)fragment);
			fif.setNotEditing();
		}
	}
	public int getmCheckedId() {
		return mCheckedId;
	}
	public void setmCheckedId(int mCheckedId) {
		this.mCheckedId = mCheckedId;
	}
	@Override
	public void setListFragment(List<BaseFootageFragment> list) {
		mFragmentList=list;
	}
	@Override
	public void reset() {
	}

}
