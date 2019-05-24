package com.jwzt.caibian.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Intent;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jwzt.caibian.application.GlobalContants;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.adapter.MembersAdapter;
import com.jwzt.caibian.application.BaseActivity;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.application.Configs;
import com.jwzt.caibian.bean.ChatMessageBean;
import com.jwzt.caibian.bean.GroupListBean;
import com.jwzt.caibian.bean.LoginBean;
import com.jwzt.caibian.bean.ResourcesBean;
import com.jwzt.caibian.bean.TaskDetailsBean;
import com.jwzt.caibian.bean.TaskListResourceListBean;
import com.jwzt.caibian.bean.TaskListResourceListResourceFileBean;
import com.jwzt.caibian.bean.TaskListUserListBean;
import com.jwzt.caibian.db.ChatsDao;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.UIUtils;
import com.jwzt.caibian.util.UserToast;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;




public class TaskDetailActivity extends BaseActivity implements OnClickListener {
	/**标题*/
	private TextView tv_titles,tv_personNum,tv_channel_detail,tv_address,tv_address_detail,tv_date,tv_date_detail,tv_submit_detail,tv_submit,tv_belong_detail,tv_belong,tv_create_detail,tv_create;
	/**全部*/
	private View tv_all;
	/**右上角的小窗口是否处于显示状态*/
	private boolean isPopShowing;
	private View iv_arrow;
	private View rl_arrow;
	/**点击三个点按钮的时候弹出的小窗口*/
	private View rl_pop;
	/**动画时长*/
	private static final int DURATION=300;
	private LinearLayout ll;
	private View rl_move;
	/**聊天按钮*/
	private View iv_chat;
	/**返回按钮*/
	private View iv_back;
	/**开始任务按钮*/
	private View tv_start;
	/**右上角三个点的按钮*/
	private View iv_dot;
	private TextView tv_renwuxiangqing_detail;
	/**是否是展开的*/
	private boolean isShowing=true;
	/**发送、保存和随拍即传*/
	private TextView tv_send,tv_upload,tv_count;
	private ScrollView sv;
	/**展开收起动画垂直的动画距离*/
	private int ANIMATE_DISTANCE;
	private GridView gv;
	
	private String id;
	private String pid,TaskStatus;
	private TaskDetailsBean taskDetailsBean;
	private List<TaskListResourceListBean> resourcelistbean;
	private List<ResourcesBean> resourceList;
	private ImageLoader imageLoader;
    private DisplayImageOptions options;
    
    private CbApplication application;
    private LoginBean mLoginBean;
    
    private List<GroupListBean> groupList;
    private GroupListBean groupListBean;
    private int personNum;
    private ChatsDao mChatsDao;
    private boolean isTaskGroup;//判断当前任务是否在群组交流的列表中
    
	
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				initView();
				break;
				case 10:
					Intent intent = new Intent();
					intent.putExtra("result", "");
					/*
					 * 调用setResult方法表示我将Intent对象返回给之前的那个Activity，这样就可以在onActivityResult方法中得到Intent对象，
					 */
					setResult(500, intent);
					finish();
					break;
			}
		};
	};
	private TextView tv_tuihui;
	private TextView tv_renling;
	private TextView tv_one_channel;
	private View rl_sucai;
	private View view_bars;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_detail); 
		
		options = new DisplayImageOptions.Builder()  
        .showImageOnLoading(R.drawable.replace) // 设置图片下载期间显示的图片  
        .showImageForEmptyUri(R.drawable.replace) // 设置图片Uri为空或是错误的时候显示的图片  
        .showImageOnFail(R.drawable.replace) // 设置图片加载或解码过程中发生错误显示的图片  
        .cacheInMemory(false) // 设置下载的图片是否缓存在内存中  
        .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中 
        .bitmapConfig(Config.RGB_565)
//        .displayer(new FadeInBitmapDisplayer(100))
        .build(); // 构建完成  
	    imageLoader = ImageLoader.getInstance();

	    application=(CbApplication) getApplication();
	    mLoginBean=application.getmLoginBean();
	    groupList=new ArrayList<GroupListBean>();
	    groupListBean=new GroupListBean();
	    resourceList=new ArrayList<ResourcesBean>();
	    mChatsDao=new ChatsDao(getHelper());
	   
	    
	    
//		ANIMATE_DISTANCE=dip2px(200);
		id=getIntent().getStringExtra("id");
		pid=getIntent().getStringExtra("pid");
		TaskStatus=getIntent().getStringExtra("TaskStatus");
		findViews();
		
		sv.post(new Runnable() {
			
			@Override
			public void run() {
				sv.fullScroll(ScrollView.FOCUS_UP);
			}
		});
		
		initData();
	}
	
	/**
	 * 请求网络数据
	 */
	private void initData(){
		if (CbApplication.getNetType() != -1) {
			showLoadingDialog(TaskDetailActivity.this,"","");
			String taskdetailUrl=String.format(Configs.taskDetailsUrl, id);
			System.out.println("taskdetailUrl:"+taskdetailUrl);
			RequestData(taskdetailUrl, Configs.taskDetailsCode, -1);
		}else {
			UserToast.toSetToast(UIUtils.getContext(), getString(R.string.please_check_net));
		}
	}
	
	/**
	 * 初始化控件
	 */
	private void initView(){
		// TODO Auto-generated method stub
		 isTaskGroup=isTaskGroup();
		if(taskDetailsBean!=null){
			if(null==taskDetailsBean.getTitle()||"".equals(taskDetailsBean.getTitle())){
				//tv_channel_detail.setVisibility(View.GONE);
				findViewById(R.id.ll_one_channel).setVisibility(View.GONE);
			}else{
				tv_channel_detail.setText(taskDetailsBean.getTitle());
			}
			if(null==taskDetailsBean.getClueAddress()||"".equals(taskDetailsBean.getClueAddress())){
			//	tv_address.setVisibility(View.GONE);
				findViewById(R.id.ll_address).setVisibility(View.GONE);
			}else {
				tv_address_detail.setText(taskDetailsBean.getClueAddress());
			}
			if(null==taskDetailsBean.getCreateTime()||"".equals(taskDetailsBean.getCreateTime())){
			//	tv_date.setVisibility(View.GONE);
				findViewById(R.id.ll_date).setVisibility(View.GONE);
			}else {
				tv_date_detail.setText(taskDetailsBean.getCreateTime());
			}
			if(null==taskDetailsBean.getEndTime()||"".equals(taskDetailsBean.getEndTime())){
				//tv_submit.setVisibility(View.GONE);
				findViewById(R.id.ll_submit).setVisibility(View.GONE);
			}else{
				tv_submit_detail.setText(taskDetailsBean.getEndTime());
			}
			if(null==taskDetailsBean.getInstruction()||"".equals(taskDetailsBean.getInstruction())){
				findViewById(R.id.ll_belong).setVisibility(View.GONE);

			}else{
				tv_belong_detail.setText(taskDetailsBean.getInstruction());
			}
			if(null==taskDetailsBean.getCreator()||"".equals(taskDetailsBean.getCreator())){
				findViewById(R.id.ll_create).setVisibility(View.GONE);

			}else {

				tv_create_detail.setText(taskDetailsBean.getCreator());
			}
			resourcelistbean=taskDetailsBean.getResourceList();
			for(int i=0;i<resourcelistbean.size();i++){
				TaskListResourceListResourceFileBean resourcebean=resourcelistbean.get(i).getResourceFile();
				ResourcesBean bean=new ResourcesBean();
				bean.setCreateTime(resourcebean.getUploadTime());
				bean.setFileId(resourcebean.getParentFileId());
				bean.setFileRealPath(resourcebean.getFilePath());
				bean.setFileSize(resourcebean.getFileSize());
				bean.setId(resourcebean.getId());
				bean.setInfoId(resourcebean.getParentFileId());
				String preViewImg=resourcebean.getPreViewImg();
				if(IsNonEmptyUtils.isString(preViewImg)){
					bean.setPreviewUrl(resourcebean.getPreViewImg());
				}else{
					bean.setPreviewUrl(resourcebean.getFileRealPath());
				}
				bean.setResourceName(resourcebean.getFileName());
				bean.setResourceType(resourcebean.getResourceType());
				resourceList.add(bean);
			}
			if(IsNonEmptyUtils.isList(resourcelistbean)){
				addViews(resourcelistbean);
			}else{
				rl_sucai.setVisibility(View.GONE);
				view_bars.setVisibility(View.GONE);
			}
			
			List<TaskListUserListBean> userList=taskDetailsBean.getUserList();
			if(IsNonEmptyUtils.isList(userList)){
				personNum=userList.size();
				tv_personNum.setText("参与人员： "+userList.size()+"人");
				gv.setAdapter(new MembersAdapter(TaskDetailActivity.this,userList));
			}else{
				findViewById(R.id.rl_num).setVisibility(View.INVISIBLE);
			}
			
		}
		if(IsNonEmptyUtils.isString(groupListBean.getGroupId())){
			List<ChatMessageBean> mListBean=mChatsDao.getNoreadMessage(new Integer(groupListBean.getGroupId()), new Integer(mLoginBean.getUserID()),1);
			if(IsNonEmptyUtils.isList(mListBean)){
				tv_count.setVisibility(View.VISIBLE);
				tv_count.setText(mListBean.size()+"");
			}else{
				tv_count.setVisibility(View.GONE);
			}
		}
		
		tv_renwuxiangqing_detail.setText(taskDetailsBean.getInstruction());
		dismisLoadingDialog();
	}

	/**
	 * 实例化控件
	 */
	private void findViews() {
//		findViewById(R.id.tv_image_num).setVisibility(View.GONE);
//		findViewById(R.id.view_bar).setVisibility(View.GONE);
//		findViewById(R.id.blue_bar).setVisibility(View.GONE);
//		findViewById(R.id.view_bars).setVisibility(View.GONE);
//		findViewById(R.id.tv_video_num).setVisibility(View.GONE);
		view_bars = findViewById(R.id.view_bars);
		tv_one_channel = findViewById(R.id.tv_one_channel);
		tv_titles=(TextView) findViewById(R.id.tv_titles);
		tv_titles.setText("任务详情");
		tv_all=findViewById(R.id.tv_all);
		tv_all.setOnClickListener(this);
	//	tv_all.setVisibility(View.GONE);
		ll=(LinearLayout) findViewById(R.id.ll);
		gv=(GridView) findViewById(R.id.gv);
		rl_pop=findViewById(R.id.rl_pop);
		rl_pop.setVisibility(View.INVISIBLE);
		iv_dot=findViewById(R.id.iv_dot);
		iv_dot.setVisibility(View.GONE);
		iv_chat=findViewById(R.id.iv_chat);
		iv_chat.setOnClickListener(this);
		iv_chat.setVisibility(View.GONE);
		
		iv_arrow=findViewById(R.id.iv_arrow);
		rl_arrow=findViewById(R.id.rl_arrow);
		rl_arrow.setOnClickListener(this);
		rl_arrow.setVisibility(View.GONE);
		//move
		rl_move=findViewById(R.id.rl_move);
		
		//rl_move.setVisibility(View.GONE);
		iv_back=findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		tv_start=findViewById(R.id.tv_start);
		tv_start.setOnClickListener(this);
		iv_dot.setOnClickListener(this);
		tv_send=(TextView) findViewById(R.id.tv_send);
		//tv_save=(TextView) findViewById(R.id.tv_save);
		tv_upload=(TextView) findViewById(R.id.tv_upload);
		tv_send.setOnClickListener(this);
		tv_upload.setOnClickListener(this);
		//tv_save.setOnClickListener(this);
		sv=(ScrollView) findViewById(R.id.sv);
		tv_address=(TextView) findViewById(R.id.tv_address);
		tv_date=(TextView) findViewById(R.id.tv_date);
		tv_count=(TextView) findViewById(R.id.tv_count);
		
	    tv_renwuxiangqing_detail=(TextView) findViewById(R.id.tv_renwuxiangqing_detail);
		
		tv_address.post(new Runnable() {
			
			@Override
			public void run() {
				int top = tv_address.getTop();
//				Toast.makeText(TaskDetailActivity.this, "top:"+top, 0).show();
				int top2 = tv_date.getTop();
//				Toast.makeText(TaskDetailActivity.this, ",top2="+top2, 0).show();
				ANIMATE_DISTANCE=4*(top2-top);
			}
		});
		
		tv_personNum=(TextView) this.findViewById(R.id.tv_personNum);
		tv_channel_detail=(TextView) this.findViewById(R.id.tv_channel_detail);
		tv_address_detail=(TextView) this.findViewById(R.id.tv_address_detail);

		tv_date_detail=(TextView) this.findViewById(R.id.tv_date_detail);
		tv_submit_detail=(TextView) this.findViewById(R.id.tv_submit_detail);
		tv_submit=(TextView) this.findViewById(R.id.tv_submit);
		tv_belong_detail=(TextView) this.findViewById(R.id.tv_belong_detail);
		rl_sucai = findViewById(R.id.Rl_sucai);
		tv_belong=(TextView) this.findViewById(R.id.tv_belong);
		tv_create_detail=(TextView) this.findViewById(R.id.tv_create_detail);
		tv_create=(TextView) this.findViewById(R.id.tv_create);
		tv_tuihui = findViewById(R.id.tv_tuihui1);
		tv_renling = findViewById(R.id.tv_renling1);
		LinearLayout viewById = findViewById(R.id.ll_renling1);
		Intent intent = getIntent();
		String state = intent.getStringExtra("state");

		tv_tuihui.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				System.out.println("点击了  任务我退回");
				sendMessage("任务退回");
			}
		});
		tv_renling.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendMessage("任务认领");
			}
		});

		if(null!=state){
if("1".equals(state)){
	viewById.setVisibility(View.VISIBLE);
	tv_start.setVisibility(View.INVISIBLE);
}else if("2".equals(state)){
	viewById.setVisibility(View.INVISIBLE);
	tv_start.setVisibility(View.VISIBLE);
}else{
	viewById.setVisibility(View.INVISIBLE);
	tv_start.setVisibility(View.VISIBLE);
}
		}else{
			viewById.setVisibility(View.INVISIBLE);
			tv_start.setVisibility(View.VISIBLE);
		}
//		tv_date.post(new Runnable() {    
//			 
//			@Override
//			public void run() {
//				
//			}
//		});
		
//		int top2 = tv_date.getTop();
//		Toast.makeText(TaskDetailActivity.this, "top:"+top+",top2="+top2, 0).show();
		if(null!=TaskStatus){
			if("1".equals(TaskStatus)){

			}else{
				tv_start.setVisibility(View.GONE);
				findViewById(R.id.ll_view).setVisibility(View.GONE);
			}
		}


	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.tv_all://全部
			if(IsNonEmptyUtils.isList(resourceList)){
				Intent intent_all=new Intent(TaskDetailActivity.this,AllFootageActivity.class);
				intent_all.putExtra("alls", (Serializable)resourceList);
				startActivity(intent_all);
				overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			}
			
			break;
		case R.id.iv_plus://右上角的加号按钮
			
			break;
		case R.id.rl_arrow:
			
			if(isShowing){
				hide();
			}else{
				show();
			}
			
			break;
		case R.id.iv_chat://聊天
//			if(isTaskGroup){
				Intent intent=new Intent(TaskDetailActivity.this,ChatActivity.class);
				intent.putExtra("personNum", personNum+"");
				intent.putExtra("groupListBean", groupListBean);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
//			}else{
//				UserToast.toSetToast(TaskDetailActivity.this, "该任务不在群组交流中");
//			}
			break;
		case R.id.iv_back:
			finish();
			overridePendingTransition(R.anim.push_left_out,R.anim.push_right_out);
			break;
		case R.id.tv_start://任务开始 
			//Intent intent_start=new Intent(TaskDetailActivity.this,TaskStartActivity.class);
			Intent intent_start=new Intent(TaskDetailActivity.this,RenWuNewScriptActivity.class);
			intent_start.putExtra("detailsbean", taskDetailsBean);
			intent_start.putExtra("groupListBean", groupListBean);
			intent_start.putExtra("groupListBean", groupListBean);
			intent_start.putExtra("tag", "speediness");
			intent_start.putExtra("isFromIndex", true);
			intent_start.putExtra("title", taskDetailsBean.getTitle());
			startActivity(intent_start);
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			break;
		case R.id.iv_dot://右上角的三个点的按钮
			if(!isPopShowing){
				scaleUp();
			}else{
				scaleDown();
			}
			break;
		case R.id.tv_send://发送
			scaleDown();
			break;
		/*case R.id.tv_save://保存
			scaleDown();
			break;*/
		case R.id.tv_upload://随拍即传
			scaleDown();
			break;
		}
	}
	
	/**
	 * 判断改任务是否存在群组交流列表中
	 * @return
	 */
	private boolean isTaskGroup(){
		groupList=application.getGroupList();
		if(IsNonEmptyUtils.isList(groupList)){
			for(int i=0;i<groupList.size();i++){
				if(taskDetailsBean.getId().equals(groupList.get(i).getTaskId())){
					groupListBean=groupList.get(i);
					return true;
				}
			}
		}else{
			return false;
		}
		return false;
	}
	
	/**
	 * 收起动画
	 */
	private void hide(){
		RotateAnimation ra=new RotateAnimation(0,180 , RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		ra.setDuration(DURATION);
		ra.setFillAfter(true);
		iv_arrow.startAnimation(ra);
		ObjectAnimator animator = ObjectAnimator.ofFloat(rl_move, "translationY", 0, -ANIMATE_DISTANCE);  
		animator.setDuration(500);
		animator.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator va) {
				float af = va.getAnimatedFraction();
//				sv.setPadding(0, 0, 0, (int) (800*af*(-1)));
				sv.setPadding(0, 0, 0, -(int)((ANIMATE_DISTANCE+dip2px(50))*af+0.5f));
			}
		});
		
		animator.start();
		isShowing=false;
		
	}
	/**
	 * 展开动画
	 */
	private void show(){
		RotateAnimation ra=new RotateAnimation(180,360 , RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		ra.setDuration(DURATION);
		ra.setFillAfter(true);
		iv_arrow.startAnimation(ra);
		ObjectAnimator animator = ObjectAnimator.ofFloat(rl_move, "translationY", -ANIMATE_DISTANCE, 0);  
		animator.setDuration(500);
		animator.start();
		isShowing=true;
		animator.addListener(new AnimatorListener() {   
			
			@Override
			public void onAnimationStart(Animator arg0) {
			}
			
			@Override
			public void onAnimationRepeat(Animator arg0) {
			}
			
			@Override
			public void onAnimationEnd(Animator arg0) {
				sv.setPadding(0, 0, 0, 0);
			}
			
			@Override
			public void onAnimationCancel(Animator arg0) {
			}
		});
	}
	/**
	 * 点击右上角的三个点的按钮的小窗弹出动画
	 */
	private void scaleUp(){
		rl_pop.setVisibility(View.VISIBLE);
		ScaleAnimation sa=new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, 1, ScaleAnimation.RELATIVE_TO_SELF, 0);
		sa.setInterpolator(new OvershootInterpolator());
		sa.setDuration(300);
		rl_pop.startAnimation(sa);
		isPopShowing=true;
	}
	/**
	 * 点击右上角的三个点的按钮的小窗收起动画
	 */
	private void scaleDown(){
		ScaleAnimation sa=new ScaleAnimation(1, 0, 1, 0, ScaleAnimation.RELATIVE_TO_SELF, 1, ScaleAnimation.RELATIVE_TO_SELF, 0);
		sa.setDuration(300);
		sa.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation arg0) {
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {
			}
			
			@Override
			public void onAnimationEnd(Animation arg0) {
				rl_pop.setVisibility(View.INVISIBLE);
			}
		});
		rl_pop.startAnimation(sa);
		isPopShowing=false;
		
	}
	 /** 
     *dip转换px,传入的dp值是double类型的
     */  
    private int dip2px(double dpValue) {  
        final float scale = getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }   
    /**
     * 往水平滚动的控件里面添加视图
     */
    private void addViews(List<TaskListResourceListBean> resourcelistbean){
    	ll.removeAllViews();//去除之前添加的默认View
		for (int i = 0; i < resourcelistbean.size(); i++) {
			View view=View.inflate(TaskDetailActivity.this, R.layout.media_item_layout, null);
			RelativeLayout rl_resource=(RelativeLayout) view.findViewById(R.id.rl_resource);
			ImageView iv=(ImageView) view.findViewById(R.id.iv);
			ImageView iv_play=(ImageView) view.findViewById(R.id.iv_play);
			LinearLayout ll_video_audio = view.findViewById(R.id.ll_video_audio);
			TextView tv_duration = view.findViewById(R.id.tv_duration);
			rl_resource.setId(i);
			String preViewImg=resourcelistbean.get(i).getResourceFile().getPreViewImg();
			String fileRealPath=resourcelistbean.get(i).getResourceFile().getFileRealPath();
			if(IsNonEmptyUtils.isString(preViewImg)){
				imageLoader.displayImage(preViewImg, iv, options);
			}else if(IsNonEmptyUtils.isString(fileRealPath)){
				imageLoader.displayImage(fileRealPath, iv, options);
			}
			if(fileRealPath.endsWith("mp4")){
				tv_duration.setText(resourcelistbean.get(i).getResourceFile().getDuration()+"");
				ll_video_audio.setVisibility(View.VISIBLE);
			}else if(fileRealPath.endsWith("mp3")){
				ll_video_audio.setVisibility(View.VISIBLE);

				iv.setImageResource(R.drawable.audio_renwu);
			}else if(fileRealPath.endsWith("png")||fileRealPath.endsWith("jpg")){
				ll_video_audio.setVisibility(View.GONE);
			}else{
				ll_video_audio.setVisibility(View.GONE);
			}
			rl_resource.setOnClickListener(resourceClickListener);
			ll.addView(view);
		}
	}
    
	/**
	 * 动态添加的布局每一项的item点击事件
	 */
	private OnClickListener resourceClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int position = v.getId();
			String preViewImg=resourcelistbean.get(position).getResourceFile().getPreViewImg();
			String fileRealPath=resourcelistbean.get(position).getResourceFile().getFileRealPath();
			Intent intent=new Intent(TaskDetailActivity.this, ShowImageActivity.class);
			if(IsNonEmptyUtils.isString(preViewImg)){
				intent.putExtra(GlobalContants.NEWSHOWIMAGE, preViewImg);
			}else if(IsNonEmptyUtils.isString(fileRealPath)){
				intent.putExtra(GlobalContants.NEWSHOWIMAGE, fileRealPath);
			}
			startActivity(intent);
			//Toast.makeText(TaskDetailActivity.this, position+"", 0).show();
		}
	};


	@Override
	protected void initRequestOnSuccess(String result, int code, int biaoshi) {
		// TODO Auto-generated method stub
		initDataParse(result, code);
	}

	@Override
	protected void initRequestOnStart(String result, int code, int biaoshi) {
	}
	@Override
	protected void initRequestOnFailure(String failure, int code, int biaoshi) {
	}
	@Override
	protected void initRequestOnCache(String result, int code, int biaoshi) {
//		initDataParse(result, code);
	}
	
	private void initDataParse(String result, int code){
		if(code==Configs.taskDetailsCode){
			JSONObject jsonObject=JSON.parseObject(result);
			String status=jsonObject.getString("status");
			if(status.equals("100")){//表示获取成功
				String data=jsonObject.getString("data");
				taskDetailsBean=JSON.parseObject(data,TaskDetailsBean.class);
				if(taskDetailsBean!=null){
					mHandler.sendEmptyMessage(1);
				}else{
					mHandler.sendEmptyMessage(0);
				}
			}
		}
	}

	private void sendMessage(final String content){

		String infoListUrl=String.format(Configs.huifuUrl,mLoginBean.getUserID(),pid,content);
		RequestParams params = new RequestParams();
		HttpUtils http = new HttpUtils();
		http.configCurrentHttpCacheExpiry(1000); //设置超时时间   10s
		http.send(HttpRequest.HttpMethod.GET, infoListUrl ,params,new RequestCallBack<String>(){
			@Override
			public void onLoading(long total, long current, boolean isUploading) {

			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				if("任务认领".equals(content)){
					Toast.makeText(TaskDetailActivity.this,"任务认领成功！",Toast.LENGTH_SHORT).show();
				}else if("任务退回".equals(content)){
					Toast.makeText(TaskDetailActivity.this,"任务退回成功！",Toast.LENGTH_SHORT).show();
				}

				Message obtain = Message.obtain();
				obtain.what=10;
				mHandler.sendMessageDelayed(obtain,2000);
				//  finish();
			}

			@Override
			public void onStart() {

			}

			@Override
			public void onFailure(HttpException error, String msg) {

			}
		});

	}


}
