package com.jwzt.caibian.activity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.adapter.StartTaskAdapter;
import com.jwzt.caibian.application.BaseActivity;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.bean.DoTaskBean;
import com.jwzt.caibian.bean.GroupListBean;
import com.jwzt.caibian.bean.LoginBean;
import com.jwzt.caibian.bean.TaskDetailsBean;
import com.jwzt.caibian.bean.TaskListUserListBean;
import com.jwzt.caibian.db.DoTaskDao;
import com.jwzt.caibian.interfaces.VolumeListener;
import com.jwzt.caibian.util.DialogHelp;
import com.jwzt.caibian.util.FileUtil;
import com.jwzt.caibian.util.GETImageUntils;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.TimeUtil;
import com.jwzt.caibian.util.UserToast;
import com.jwzt.caibian.util.Utils;
import com.jwzt.caibian.widget.SquareLayout;
import com.jwzt.caibian.xiangce.Bimp;
import com.jwzt.caibian.xiangce.ItemImage;
import com.jwzt.upload.configs.Configs;
import com.zc.RecordDemo.MyAudioRecorder;
/**
 * 任务开始
 * @author howie
 *
 */
@SuppressLint({ "UseValueOf", "ClickableViewAccessibility", "ShowToast" }) @SuppressWarnings("unused")
public class TaskStartActivity extends BaseActivity implements OnClickListener,VolumeListener {
	private ScrollView sv;
	private ImageView iv_arrow;
	/**动画时长*/
	private static final int DURATION=300;
	/**是否是展开的*/
	private boolean isShowing=true;
	/**点击三个点按钮的时候弹出的小窗口*/
	private RelativeLayout rl_pop;
	/**发送、保存和随拍即传*/
	private TextView tv_send,tv_upload;
//	private TextView mTextView;   //文本域
	int maxLine=1;  //TextView设置默认最大展示行数为1
	/**添加图片，位置信息，文件等所在的布局*/
	private LinearLayout ll;
	private ImageView iv_dot;
	/**返回按钮*/
	private ImageView iv_back;
	private ImageView iv_sound;  //翻转icon
	private ListView lv;
	/**添加图片的面板是否可见*/
	private boolean isAddVisible;
	private ImageView iv_plus;
	/**聊天按钮*/
	private ImageView iv_chat;
	/**右上角的小窗口是否处于显示状态*/
	private boolean isPopShowing;
	/**添加图片，拍照，添加文件，定位*/
	private SquareLayout sl_image,sl_camera,sl_file,sl_location;
	private Button bt_send;//发送按钮
	private LinearLayout ll_move;
	/**采访地址和采访日期*/
	private View tv_address,tv_date;
	/**收起展开的时候垂直方向移动的距离*/
	private int ANIMATE_DISTANCE;
	private StartTaskAdapter startTaskAdapter;
	
	/***拍照*/
	public static final int PHOTOHRAPH = 3;
	/***本地选择视频*/
	public static final int VIDEOSELECT=4;
	/***拍摄视频*/
	public static final int VIDEOPAISHE=5;
	private static final int TAKE_PICTURE = 0x000001;
	/***素材库选择*/
	private static final int SELECTRESOURCE=8;
	/***图片保存地址*/
	private String IMG_SAVE_NAME;
	
	//录音
	private boolean luyinflag;
	private MyAudioRecorder recorder;
	/**开始录音的时间**/
	private long start_record;
	/**结束录音的时间**/
	private long end_record;
	/** "录音中"提示框 **/
	private RelativeLayout rl_recording;
	/***录音音量显示图片*/
	private ImageView iv_recording,iv_play;
	/***定义计时器*/ 
	private boolean bool;
	/***录音时长*/
	private int luYinTime;
	/***限制录音时长*/
	private int totalTime=60;
	/***录音完成后保存的路径*/
	private String mp3Path;
	/***开始任务*/
	private TaskDetailsBean taskDetailsBean;
	/***头部控件*/
	private TextView tv_personNum,tv_channel_detail,tv_address_detail,tv_date_detail,tv_submit_detail,tv_belong_detail,tv_create_detail,tv_count;
	private EditText et_input;
	private Button bt_press_speak;
	private int personNum;
	private GroupListBean groupListBean;//群组信息
	private CbApplication application;
    private List<GroupListBean> groupList;
	private LoginBean mLoginBean;
	/***表示是否选中即拍即传 true表示是即拍即传则发送一条上传一条 false表示非即拍即传则点发送按钮是上传所有未上传的*/
	private boolean isJPJC=false;
	/***1：文字、2：图片、3：音频、4：视频、5：文件、6：位置*/
	private int msgType;
	
	private TextView tv_one_channel;
	//获取的数据库操作对象
	private DoTaskDao dotask;
	private List<DoTaskBean> mList;
	/***标识是否是录音状态*/
	private boolean isLuying=false;
	
	@SuppressLint("HandlerLeak") 
	private Handler mHandler=new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				iv_recording.setImageResource(R.drawable.record01);
				break;
			case 2:
				iv_recording.setImageResource(R.drawable.record02);
				break;
			case 3:
				iv_recording.setImageResource(R.drawable.record03);
				break;
			case 4:
			    iv_recording.setImageResource(R.drawable.record04);
				break;
			case 5:
				iv_recording.setImageResource(R.drawable.record05);
				break;
			case 6:
				iv_recording.setImageResource(R.drawable.record06);
				break;
			case 7:
				iv_recording.setImageResource(R.drawable.record07);
				break;
			case 8:
				iv_recording.setImageResource(R.drawable.record08);
				break;
			case 9:
				mList=dotask.getTasks(new Integer(taskDetailsBean.getId()));
				if(IsNonEmptyUtils.isList(mList)){
					initChatList();
				}
				if(IsNonEmptyUtils.isList(Bimp.tempSelectBitmap)){
					Bimp.tempSelectBitmap.clear();
				}
				
//				System.out.println("==================>>"+Bimp.tempSelectBitmap.size());
				break;
			case 10://发送本地图片
				sendLocationImg();
				break;
			case 11://发送本地语音
				sendLocationAudio();
				break;
			case 12://发送本地视频
				sendLocationVideo();
				break;
			case 13:
				initView();
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_start_layout);
		application=(CbApplication) getApplication();
		mLoginBean=application.getmLoginBean();
		//配置任务上传接口
		/***任务上传第一次请求接口*/
		Configs.firstUploadUrl=com.jwzt.caibian.application.Configs.ICON_URL+"/bvCaster_direct/phone/phoneResourceUpload.jspx?requestType=getFileSize&uuid=%s&fileName=%s";
		/***文稿上传第二次请求接口*/
		Configs.SecondUploadUrl=com.jwzt.caibian.application.Configs.ICON_URL+"/bvCaster_direct/phone/phoneResourceUpload.jspx?requestType=uploadFile&uuid=%s&fileName=%s&beginPosition=%s";
		/***文稿上传第三次请求接口*/                                                                             
		Configs.ThirdUploadUrl=com.jwzt.caibian.application.Configs.ICON_URL+"/bvCaster_direct/phone/phoneResourceUpload.jspx?requestType=finishNotify&uuid=%s&fileName=%s&fileTotalSize=%s&fileMD5=%s&infoType=%s";
		
		findViews();
		recorder = new MyAudioRecorder("voice_luyin");
		taskDetailsBean=(TaskDetailsBean) getIntent().getSerializableExtra("detailsbean");
		groupListBean=(GroupListBean) getIntent().getSerializableExtra("groupListBean");
		dotask=new DoTaskDao(getHelper());
		
		initViewTop();
		DialogHelp.showLoadingDialog(TaskStartActivity.this, "", "");
//		initView();
		mHandler.sendEmptyMessageDelayed(13, 1000);
		
	}
	
	/**
	 * 进入页面时获取数据库中的数据初始化列表
	 */
	private void initView(){
		mList=dotask.getTasks(new Integer(taskDetailsBean.getId()),new Integer(mLoginBean.getUserID()));
		if(IsNonEmptyUtils.isList(mList)){
			initChatList();
		}
		DialogHelp.dismisLoadingDialog();
	}
	
	/**
	 * 初始化头部控件
	 */
	private void initViewTop(){
		if(taskDetailsBean!=null){
			tv_channel_detail.setText(taskDetailsBean.getTitle());
			tv_address_detail.setText(taskDetailsBean.getClueAddress());
			tv_date_detail.setText(taskDetailsBean.getCreateTime());
			tv_submit_detail.setText(taskDetailsBean.getEndTime());
			tv_belong_detail.setText(taskDetailsBean.getInstruction());
			tv_create_detail.setText(taskDetailsBean.getCreator());
			
			List<TaskListUserListBean> userList=taskDetailsBean.getUserList();
			if(IsNonEmptyUtils.isList(userList)){
				personNum=userList.size();
				tv_personNum.setText("参与人员： "+userList.size()+"人");
			}
		}
	}
	
	/**
	 * 适配列表
	 */
	private void initChatList(){
		if(IsNonEmptyUtils.isList(mList)){
			tv_count.setVisibility(View.VISIBLE);
			tv_count.setText(mList.size()+"");
		}else{
			tv_count.setVisibility(View.GONE);
		}
		startTaskAdapter = new StartTaskAdapter(TaskStartActivity.this,mList);
		lv.setAdapter(startTaskAdapter);
	}
	
	/**
	 * 实例化控件
	 */
	private void findViews() {
		lv=(ListView) findViewById(R.id.lv);
		iv_back=(ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		iv_sound=(ImageView) findViewById(R.id.iv_sound);
		iv_sound.setOnClickListener(this);
	    ll=(LinearLayout) findViewById(R.id.ll);
//	    ll.setVisibility(View.GONE);
	    iv_plus=(ImageView) findViewById(R.id.iv_plus);
	    iv_plus.setOnClickListener(this);
	    iv_chat=(ImageView) findViewById(R.id.iv_chat);
	    iv_chat.setOnClickListener(this);
		tv_send=(TextView) findViewById(R.id.tv_send);
		//tv_save=(TextView) findViewById(R.id.tv_save);
		tv_upload=(TextView) findViewById(R.id.tv_upload);
		tv_send.setOnClickListener(this);
		tv_upload.setOnClickListener(this);
		//tv_save.setOnClickListener(this);  
		iv_dot=(ImageView) findViewById(R.id.iv_dot);
		iv_dot.setOnClickListener(this);
		rl_pop=(RelativeLayout) findViewById(R.id.rl_pop);
		rl_pop.setVisibility(View.INVISIBLE);
		sl_image=(SquareLayout) findViewById(R.id.sl_image);
		sl_camera=(SquareLayout) findViewById(R.id.sl_camera);
		sl_file=(SquareLayout) findViewById(R.id.sl_file);
		sl_location=(SquareLayout) findViewById(R.id.sl_location);
		sl_image.setOnClickListener(this);
		sl_camera.setOnClickListener(this);
		sl_file.setOnClickListener(this);
		sl_location.setOnClickListener(this);
//		rl_arrow=findViewById(R.id.rl_arrow);
//		rl_arrow.setOnClickListener(this);
		iv_arrow=(ImageView) findViewById(R.id.iv_arrow);
		iv_arrow.setOnClickListener(this);
		ll_move=(LinearLayout) findViewById(R.id.ll_move);
		tv_address=findViewById(R.id.tv_address);
		tv_date=findViewById(R.id.tv_date);
		sv=(ScrollView) findViewById(R.id.sv);
		
		tv_personNum=(TextView) findViewById(R.id.tv_personNum);
		tv_channel_detail=(TextView) findViewById(R.id.tv_channel_detail);
		tv_address_detail=(TextView) findViewById(R.id.tv_address_detail);
		tv_date_detail=(TextView) findViewById(R.id.tv_date_detail);
		tv_submit_detail=(TextView) findViewById(R.id.tv_submit_detail);
		tv_belong_detail=(TextView) findViewById(R.id.tv_belong_detail);
		tv_create_detail=(TextView) findViewById(R.id.tv_create_detail);
		
		rl_recording=(RelativeLayout) findViewById(R.id.rl_recording);
		iv_recording=(ImageView) findViewById(R.id.iv_recording);
		tv_one_channel = (TextView)findViewById(R.id.tv_one_channel);
		et_input=(EditText) findViewById(R.id.et_input);
		bt_send=(Button) findViewById(R.id.bt_send);
		iv_play=(ImageView) findViewById(R.id.iv_play);
		
		tv_count=(TextView) findViewById(R.id.tv_count);
		tv_count.setVisibility(View.GONE);
		
		bt_press_speak=(Button) findViewById(R.id.bt_press_speak);
		bt_press_speak.setOnClickListener(this);
		bt_press_speak.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:// 按下事件
					//标记为没有滑离按钮
					bt_press_speak.setBackgroundResource(R.drawable.new_chat_send_voice_d);
					bt_press_speak.setTextColor(getResources().getColor(R.color.white));
					luyinflag=true;
					if (null != recorder) {
						start_record=System.currentTimeMillis();
						recorder.prepare();
						recorder.startRecording();
						recorder.setmVolumeListener(TaskStartActivity.this);
					} else {
						recorder = new MyAudioRecorder("voice_luyin");
						recorder.prepare();
						recorder.startRecording();
					}
					
					// 显示“录音中”的提示框
					rl_recording.setVisibility(View.VISIBLE);
					bt_press_speak.setText("松开自动发送");
					//开始计时
					bool = true;
					luYinTime=0;
					mHandler.postDelayed(task, 1000);
					break;
				case MotionEvent.ACTION_UP:// 抬起
					bt_press_speak.setBackgroundResource(R.drawable.new_chat_send_voice);
					bt_press_speak.setTextColor(getResources().getColor(R.color.grey9));
					bt_press_speak.setText("按住说话");
					luyinflag=false;
					LuYinTaiQi();
					break;
//				case MotionEvent.ACTION_MOVE:
//					System.out.println("Y值"+event.getY()+"X值"+event.getX());
//					if(event.getY()<-100){//如果偏离超过100像素，视为已经滑离按钮
//						//表明用户的手指已经滑离按钮
//						isSlideOut=true;
//						//显示“松开取消发送”的提示
//						rl_cancel.setVisibility(View.VISIBLE);
//						rl_recording.setVisibility(View.INVISIBLE);
//						rl_short.setVisibility(View.INVISIBLE);
//					}else{
//						//表明用户的手指没有滑离按钮
//						isSlideOut=false;
//						rl_recording.setVisibility(View.VISIBLE);
//						rl_cancel.setVisibility(View.INVISIBLE);
//						rl_short.setVisibility(View.INVISIBLE);
//					}
//					break;
//				case MotionEvent.ACTION_CANCEL:
//					isSlideOut=false;
//					if(recorder!=null){
//						recorder.stopRecording();// 停止录音
//						recorder.release();  
//					}
//					break;
				}
				return true;
			}
		});
		
		bt_send.setOnClickListener(this);
		iv_play.setOnClickListener(this);
		
		et_input.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,int count) {
					if (TextUtils.isEmpty(s)) {
						bt_send.setVisibility(View.VISIBLE);
						iv_plus.setVisibility(View.GONE);
						bt_send.setClickable(true);
					} else {
						bt_send.setVisibility(View.VISIBLE);
						iv_plus.setVisibility(View.GONE);
						bt_send.setClickable(true);
					}
				
				String content=et_input.getText().toString();
				if(IsNonEmptyUtils.isString(content)){
					if(content.length()>=140){
						UserToast.toSetToast(TaskStartActivity.this, "");
					}
				}else{
					bt_send.setVisibility(View.GONE);
					iv_plus.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
		tv_address.post(new Runnable() {
			
			@Override
			public void run() {
				int top = tv_address.getTop();
				int top2 = tv_date.getTop();
				ANIMATE_DISTANCE=4*(top2-top);
			}
		});
		
	}
	@SuppressWarnings("static-access")
	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.iv_back://返回按钮
			finish();
			overridePendingTransition(R.anim.push_left_out,R.anim.push_right_out);
			break;
		case R.id.iv_plus:
			isLuying=false;
			et_input.setVisibility(View.VISIBLE);
			bt_press_speak.setVisibility(View.GONE);
			
			if(!isAddVisible){
				ll.setVisibility(View.VISIBLE);
				isAddVisible=true;
			}else{
				ll.setVisibility(View.GONE);
				isAddVisible=false;
			}
			break;
		case R.id.iv_chat://聊天按钮
			if(isTaskGroup()){
				Intent intent=new Intent(TaskStartActivity.this,ChatActivity.class);
				intent.putExtra("personNum", personNum+"");
				intent.putExtra("groupListBean", groupListBean);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			}else{
				UserToast.toSetToast(TaskStartActivity.this, "该任务不在群组交流中");
			}
			break;
		case R.id.iv_dot://右上角的三个点的按钮
			if(!isPopShowing){
				scaleUp();
			}else{
				scaleDown();
			}
			break;
		case R.id.tv_send://发送
			// TODO 发送
			List<DoTaskBean> nouploadList=dotask.getTasks(new Integer(taskDetailsBean.getId()),new Integer(mLoginBean.getUserID()),1);
			if(IsNonEmptyUtils.isList(nouploadList)){
				List<ItemImage> list=new ArrayList<ItemImage>();
				String xmlPath=FileUtil.taskassemblyXML(dotask,taskDetailsBean.getId(), TimeUtil.getDate3(), mLoginBean.getUserID(), Utils.getUUID(), TimeUtil.getDate3(), "", -1, application.getLongitude(), application.getLatitude(), null, "0",nouploadList);
				for(int i=0;i<nouploadList.size();i++){
					String nouploadString=nouploadList.get(i).getThumbImages();
					if(IsNonEmptyUtils.isString(nouploadString)){
						String[] nouploadarray=nouploadString.split(",");
						for(int j=0;j<nouploadarray.length;j++){
							ItemImage itemImage=new ItemImage();
							itemImage.setBitmap(null);
							itemImage.setFilepath(nouploadarray[j]);
							itemImage.setResult(false);
							list.add(itemImage);
						}
					}
				}
				
				Intent intent2=new Intent(TaskStartActivity.this, BackTransferActivity.class);
				intent2.putExtra("tag", "taskBack");
				intent2.putExtra("xmlpath", xmlPath);
				intent2.putExtra("infotype", "1");
				intent2.putExtra("imglist", (Serializable)list);
				startActivity(intent2);
			}
			scaleDown();
			break;
		case R.id.iv_sound:
			if(isLuying){
				isLuying=false;
				et_input.setVisibility(View.VISIBLE);
				bt_press_speak.setVisibility(View.GONE);
			}else{
				isLuying=true;
				et_input.setVisibility(View.GONE);
				bt_press_speak.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.tv_upload:
			//TODO 随拍即传
			scaleDown();
			if(isJPJC){
				isJPJC=false;
				Drawable drawable= getResources().getDrawable(R.drawable.letter);  
				/// 这一步必须要做,否则不会显示.  
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());  
				tv_upload.setCompoundDrawables(drawable,null,null,null); 
			}else{
				isJPJC=true;
				Drawable drawable= getResources().getDrawable(R.drawable.camera);  
				/// 这一步必须要做,否则不会显示.  
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());  
				tv_upload.setCompoundDrawables(drawable,null,null,null); 
			}
			break;
		case R.id.sl_image://添加本地图片
			if(Bimp.tempSelectBitmap.size()>=9){
				UserToast.toSetToast(TaskStartActivity.this, "已到选择上线");
			}else{
				Intent intent_select = new Intent(TaskStartActivity.this, PhotoPickerActivity.class);
				intent_select.putExtra(PhotoPickerActivity.EXTRA_SHOW_CAMERA, false);
				intent_select.putExtra(PhotoPickerActivity.EXTRA_SELECT_MODE, 1);
				intent_select.putExtra(PhotoPickerActivity.ALL_TYPE, PhotoPickerActivity.ALL_PHOTO);
				intent_select.putExtra(PhotoPickerActivity.EXTRA_MAX_MUN, 100);
		        startActivityForResult(intent_select, TAKE_PICTURE);
				overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			}
			Toast.makeText(TaskStartActivity.this, "点了图片", 0).show();
			break;
		case R.id.sl_camera://拍照
			if(Bimp.tempSelectBitmap.size()>=9){
				UserToast.toSetToast(TaskStartActivity.this, "已到选择上线");
			}else{
				Intent intent_paizhao = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				IMG_SAVE_NAME = Utils.getUUID() + ".png";
				intent_paizhao.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(new File(FileUtil.getFolderPath(),IMG_SAVE_NAME)));
				startActivityForResult(intent_paizhao, PHOTOHRAPH);
				overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			}
			Toast.makeText(TaskStartActivity.this, "点了拍照", 0).show();
			break;
		case R.id.sl_file://本地文件
			Intent intent_doc=new Intent(TaskStartActivity.this,SelectFootageActivity.class);
//			startActivity(intent_doc);
			Bimp.tempSelectBitmap.add(null);
			startActivityForResult(intent_doc, SELECTRESOURCE);
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			Toast.makeText(TaskStartActivity.this, "点了文件", 0).show();
			break;
		case R.id.sl_location://定位
			String location="";
			if(application!=null){
				location=application.getLocation();
			}
			msgType=6;
			int isSendlocation;
			if(isJPJC){//表示是即拍即传
				isSendlocation=0;//表示是否发送
				String xmlPath=FileUtil.taskassemblyXML(dotask,taskDetailsBean.getId(), TimeUtil.getDate3(), mLoginBean.getUserID(), Utils.getUUID(), TimeUtil.getDate3(), location, msgType, application.getLongitude(), application.getLatitude(), null, "0",null);
				Intent intent2=new Intent(TaskStartActivity.this, BackTransferActivity.class);
				intent2.putExtra("tag", "taskBack");
				intent2.putExtra("xmlpath", xmlPath);
				intent2.putExtra("infotype", "1");
				intent2.putExtra("imglist", Bimp.tempSelectBitmap);
				startActivity(intent2);
				locationSend(new Integer(mLoginBean.getUserID()),new Integer(taskDetailsBean.getId()), isSendlocation, TimeUtil.getDate3(), location, msgType, "", "");
			}else{
				isSendlocation=1;//表示是否发送
				locationSend(new Integer(mLoginBean.getUserID()),new Integer(taskDetailsBean.getId()), isSendlocation, TimeUtil.getDate3(), location, msgType, "", "");
			}
			et_input.setText("");
			bt_send.setVisibility(view.GONE); 
			iv_plus.setVisibility(View.VISIBLE);
			//隐藏软键盘
			InputMethodManager immlocation = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
			immlocation.hideSoftInputFromWindow(et_input.getWindowToken(), 0);
			
			break;
		case R.id.iv_arrow://展开收起箭头按钮
			if(isShowing){
				hide();
			}else{
				show();
			}
			break;
		case R.id.bt_send:
			//TODO 文字发送按钮
			String content=et_input.getText().toString();
			int isSend;
			msgType=1;
			if(isJPJC){
				isSend=0;//表示是否发送
				/***1：文字、2：图片、3：音频、4：视频、5：文件、6：位置*/
				String xmlPath=FileUtil.taskassemblyXML(dotask,taskDetailsBean.getId(), TimeUtil.getDate3(), mLoginBean.getUserID(), Utils.getUUID(), TimeUtil.getDate3(), content, msgType, application.getLongitude(), application.getLatitude(), null, "0",null);
				Intent intent2=new Intent(TaskStartActivity.this, BackTransferActivity.class);
				intent2.putExtra("tag", "taskBack");
				intent2.putExtra("xmlpath", xmlPath);
				intent2.putExtra("infotype", "1");
				intent2.putExtra("imglist", Bimp.tempSelectBitmap);
				startActivity(intent2);
				locationSend(new Integer(mLoginBean.getUserID()),new Integer(taskDetailsBean.getId()),isSend,TimeUtil.getDate3(),content,msgType,"","");
			}else{
				isSend=1;//表示是否发送
				locationSend(new Integer(mLoginBean.getUserID()),new Integer(taskDetailsBean.getId()),isSend,TimeUtil.getDate3(),content,msgType,"","");
			}
			
			et_input.setText("");
			bt_send.setVisibility(view.GONE);
			iv_plus.setVisibility(View.VISIBLE);
			//隐藏软键盘
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
			imm.hideSoftInputFromWindow(et_input.getWindowToken(), 0);
			break;
		case R.id.iv_play:
			isLuying=false;
			et_input.setVisibility(View.VISIBLE);
			bt_press_speak.setVisibility(View.GONE);
			
			Intent intent_Paishe = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			// 设置视频大小
			intent_Paishe.putExtra(MediaStore.EXTRA_SIZE_LIMIT,768000);
			// 设置视频时间，毫秒
			intent_Paishe.putExtra(MediaStore.EXTRA_DURATION_LIMIT,450000);
			intent_Paishe.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
			startActivityForResult(intent_Paishe, VIDEOPAISHE);
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
	 * 本地发送只是在列表上显示不上传到后台
	 * @param userId 用户id
	 * @param id 任务id
	 * @param isSend 是否发送后台 0已经发送  1未发送
	 * @param sendtime 发送时间
	 * @param msgContent 发送内容
	 * @param megType 消息类型 1：文字、2：图片、3：音频、4：视频、5：文件、6：位置
	 * @param thumbimg 图片地址
	 * @param videopath 视音频地址
	 */
	private void locationSend(int userId,int id,int isSend,String sendtime,String msgContent,int megType,String thumbimg,String videopath){
		DoTaskBean dotasks=new DoTaskBean();
		dotasks.setUserid(userId);
		dotasks.setTaskid(id);//任务id
		dotasks.setReaderStatue(isSend);//标识任务是否已发送0已经发送  1未发送
		dotasks.setSendTime(sendtime);//发送时间
		dotasks.setSucaiContent(msgContent);//发送内容
		dotasks.setSucaiId(Utils.getUUID());//
		dotasks.setSucaiType(megType);////发送类型   0：文字，1：图片  2：音频  3：视频
		dotasks.setThumbImages(thumbimg);//图片地址 多张图片则以“，”分割
		dotasks.setVideopath(videopath);//视频地址 多张图片则以“，”分割
		dotasks.setLatitude(application.getLatitude());
		dotasks.setLongitude(application.getLongitude());
		dotasks.setTimeLength(luYinTime);
		dotask.saveOrUpdateTask(dotasks);
		mHandler.sendEmptyMessage(9);
	}
	
	/**
	 * 发送本地图片
	 */
	private void sendLocationImg(){
		int isSend;
		/***1：文字、2：图片、3：音频、4：视频、5：文件、6：位置*/
		msgType=2;
		if(isJPJC){
			isSend=0;//表示是否发送
			if(IsNonEmptyUtils.isList(Bimp.tempSelectBitmap)){
				StringBuffer sb=new StringBuffer();
				List<String> listPath=new ArrayList<String>();
				for(int i=0;i<Bimp.tempSelectBitmap.size();i++){
					String imgPath=Bimp.tempSelectBitmap.get(i).getFilepath();
					listPath.add(imgPath);
					
					if(i==(Bimp.tempSelectBitmap.size()-1)){//表示最后一个
						sb.append(imgPath);
					}else{
						sb.append(imgPath+",");
					}
				}
				String imgPath=sb.toString();
				locationSend(new Integer(mLoginBean.getUserID()),new Integer(taskDetailsBean.getId()), isSend, TimeUtil.getDate3(), "", msgType, imgPath, "");
				
				String xmlPath=FileUtil.taskassemblyXML(dotask,taskDetailsBean.getId(), TimeUtil.getDate3(), mLoginBean.getUserID(), Utils.getUUID(), TimeUtil.getDate3(), "", msgType, application.getLongitude(), application.getLatitude(), listPath, "0",null);
				Intent intent2=new Intent(TaskStartActivity.this, BackTransferActivity.class);
				intent2.putExtra("tag", "taskBack");
				intent2.putExtra("xmlpath", xmlPath);
				intent2.putExtra("infotype", "1");
				intent2.putExtra("imglist", Bimp.tempSelectBitmap);
				startActivity(intent2);
			}
		}else{
			isSend=1;//表示是否发送
			if(IsNonEmptyUtils.isList(Bimp.tempSelectBitmap)){//选择的图片不为空
				StringBuffer sb=new StringBuffer();
				for(int i=0;i<Bimp.tempSelectBitmap.size();i++){
					String imgPath=Bimp.tempSelectBitmap.get(i).getFilepath();
					if(i==(Bimp.tempSelectBitmap.size()-1)){//表示最后一个
						sb.append(imgPath);
					}else{
						sb.append(imgPath+",");
					}
				}
				String imgPath=sb.toString();
				locationSend(new Integer(mLoginBean.getUserID()),new Integer(taskDetailsBean.getId()), isSend, TimeUtil.getDate3(), "", msgType, imgPath, "");
			}
		}

		Bimp.tempSelectBitmap.clear();
		
	}
	
	/**
	 * 发送本地语音
	 */
	private void sendLocationAudio(){
		int isSend;
		/***1：文字、2：图片、3：音频、4：视频、5：文件、6：位置*/
		msgType=3;
		if(isJPJC){//如果是即拍即传则直接上传
			isSend=0;//表示是否发送
			if(IsNonEmptyUtils.isList(Bimp.tempSelectBitmap)){//选择的图片不为空
				String audioPath=Bimp.tempSelectBitmap.get(0).getFilepath();
				locationSend(new Integer(mLoginBean.getUserID()),new Integer(taskDetailsBean.getId()), isSend, TimeUtil.getDate3(), "", msgType, audioPath, audioPath);
				List<String> listPath=new ArrayList<String>();
				listPath.add(audioPath);
				String xmlPath=FileUtil.taskassemblyXML(dotask,taskDetailsBean.getId(), TimeUtil.getDate3(), mLoginBean.getUserID(), Utils.getUUID(), TimeUtil.getDate3(), "", msgType, application.getLongitude(), application.getLatitude(), listPath, "0",null);
				
				Intent intent2=new Intent(TaskStartActivity.this, BackTransferActivity.class);
				intent2.putExtra("tag", "taskBack");
				intent2.putExtra("xmlpath", xmlPath);
				intent2.putExtra("infotype", "1");
				intent2.putExtra("imglist", Bimp.tempSelectBitmap);
				startActivity(intent2);
			}
		}else{//如果非即拍即传则直接入库
			isSend=1;//表示是否发送
			if(IsNonEmptyUtils.isList(Bimp.tempSelectBitmap)){//选择的图片不为空
				String audioPath=Bimp.tempSelectBitmap.get(0).getFilepath();
				locationSend(new Integer(mLoginBean.getUserID()),new Integer(taskDetailsBean.getId()), isSend, TimeUtil.getDate3(), "", msgType, audioPath, audioPath);
			}
		}
		
		Bimp.tempSelectBitmap.clear();//操作完成清空数据
	}
	
	/**
	 * 发送本地视频
	 */
	private void sendLocationVideo(){
		int isSend;
		/***1：文字、2：图片、3：音频、4：视频、5：文件、6：位置*/
		msgType=4;
		if(isJPJC){//如果是即拍即传则直接上传
			isSend=0;//表示是否发送
			if(IsNonEmptyUtils.isList(Bimp.tempSelectBitmap)){//选择的图片不为空
				String audioPath=Bimp.tempSelectBitmap.get(0).getFilepath();
				locationSend(new Integer(mLoginBean.getUserID()),new Integer(taskDetailsBean.getId()), isSend, TimeUtil.getDate3(), "", msgType, audioPath, audioPath);
				List<String> listPath=new ArrayList<String>();
				listPath.add(audioPath);
				String xmlPath=FileUtil.taskassemblyXML(dotask,taskDetailsBean.getId(), TimeUtil.getDate3(), mLoginBean.getUserID(), Utils.getUUID(), TimeUtil.getDate3(), "", msgType, application.getLongitude(), application.getLatitude(), listPath, "0",null);
				
				Intent intent2=new Intent(TaskStartActivity.this, BackTransferActivity.class);
				intent2.putExtra("tag", "taskBack");
				intent2.putExtra("xmlpath", xmlPath);
				intent2.putExtra("infotype", "1");
				intent2.putExtra("imglist", Bimp.tempSelectBitmap);
				startActivity(intent2);
			}
		}else{//如果非即拍即传则直接入库
			isSend=1;//表示是否发送
			if(IsNonEmptyUtils.isList(Bimp.tempSelectBitmap)){//选择的图片不为空
				String audioPath=Bimp.tempSelectBitmap.get(0).getFilepath();
				locationSend(new Integer(mLoginBean.getUserID()),new Integer(taskDetailsBean.getId()), isSend, TimeUtil.getDate3(), "", msgType, audioPath, audioPath);
			}
		}
		
		Bimp.tempSelectBitmap.clear();//操作完成清空数据
	}
	
	/**
	 * 录音抬起方法
	 */
	private void LuYinTaiQi(){
		bool = false;
		end_record=System.currentTimeMillis();
		//如果录音时间大于3秒并且没有滑离按钮
		if(end_record-start_record>1500){
//			// 隐藏“录音中”的提示框
			rl_recording.setVisibility(View.GONE);
			// 释放
			mp3Path = recorder.stopRecording();// 停止录音
			recorder.release();
		}else if(end_record-start_record<1500){//手指没有滑离但是录音时间小于3秒
			rl_recording.setVisibility(View.GONE);
			recorder.stopRecording();// 停止录音
			recorder.release();
			//500毫秒之后隐藏“录音时间太短”的提示
			mHandler.sendEmptyMessageDelayed(30, 500);
		}
		
		ItemImage image=new ItemImage();
//		image.setBitmap(BitmapUtils.readBitMap(TaskStartActivity.this, R.drawable.replace));
		image.setFilepath(mp3Path);
		image.setResult(true);
		Bimp.tempSelectBitmap.add(0,image);
		mHandler.sendEmptyMessage(11);
		System.out.println("mp3Pathmp3Pathmp3Path"+mp3Path);
	}
	
	//录音计时
	private Runnable task = new Runnable() {
		public void run() {
			if (bool) {
				++luYinTime;
				Message message=new Message();
				message=mHandler.obtainMessage(35, luYinTime);
				mHandler.sendMessage(message);
				mHandler.postDelayed(this, 1000);
			}
		}
	};

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
	 * 收起动画
	 */
	private void hide(){
		RotateAnimation ra=new RotateAnimation(0,180 , RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		ra.setDuration(DURATION);
		ra.setFillAfter(true);
		iv_arrow.startAnimation(ra);
		ObjectAnimator animator = ObjectAnimator.ofFloat(ll_move, "translationY", 0, -ANIMATE_DISTANCE);  
		animator.setDuration(500);
		animator.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator va) {
				float af = va.getAnimatedFraction();
				sv.setPadding(0, 0, 0, 0);
//				sv.setPadding(0, 0, 0, -(int)(ANIMATE_DISTANCE*af+0.5f));
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
		ObjectAnimator animator = ObjectAnimator.ofFloat(ll_move, "translationY", -ANIMATE_DISTANCE, 0);  
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
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(IsNonEmptyUtils.isList(Bimp.tempSelectBitmap)){
			Bimp.tempSelectBitmap.clear();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//	
		if (requestCode==TAKE_PICTURE) {//本地选择图片
			if(IsNonEmptyUtils.isList(Bimp.tempSelectBitmap)){//选择图片后直接发送图片
				mHandler.sendEmptyMessage(10);
			}
		}else if(requestCode == PHOTOHRAPH){//拍照
			final File picture = new File(FileUtil.getFolderPath(), IMG_SAVE_NAME);
			Uri localUri = Uri.fromFile(picture);
			Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
			sendBroadcast(localIntent);
			int degree = FileUtil.readPictureDegree(picture.getAbsolutePath());  
			BitmapFactory.Options opts=new BitmapFactory.Options();//获取缩略图显示到屏幕上
			opts.inSampleSize=2;
			Bitmap cbitmap=BitmapFactory.decodeFile(picture.getAbsolutePath(),opts);
			if(cbitmap!=null){//把图片旋转为正的方向 
               Bitmap bm = FileUtil.rotaingImageView(degree, cbitmap);    //控制旋转角度
           	   ItemImage image=new ItemImage();
    		   image.setBitmap(bm);
    		   image.setFilepath(picture.getAbsolutePath());
    		   image.setResult(true);
    		   Bimp.tempSelectBitmap.add(0,image);
    		   mHandler.sendEmptyMessage(10);
           }
		}else if(requestCode == VIDEOSELECT){//本地选择视频
			if (data != null) {
				Uri uri = data.getData();
				if(uri!=null){
					String picturePath = GETImageUntils.getPath(TaskStartActivity.this, uri);
					ItemImage image=new ItemImage();
		    		image.setBitmap(ThumbnailUtils.createVideoThumbnail(picturePath, 0));
		    		image.setFilepath(picturePath);
		    		image.setResult(true);
		    		Bimp.tempSelectBitmap.add(0,image);
					mHandler.sendEmptyMessage(1);
				}
			}
		}else if(requestCode == VIDEOPAISHE){//本地拍摄视频
			if (data != null) {
				Uri originalUri = data.getData();
				if (originalUri != null) {
					String[] proj = { MediaStore.Images.Media.DATA };
					Cursor cursor = managedQuery(originalUri, proj, null, null,null);
					int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					cursor.moveToFirst();
					String path = cursor.getString(column_index);
					ItemImage image=new ItemImage();
//		    		image.setBitmap(ThumbnailUtils.createVideoThumbnail(path, 0));
		    		image.setFilepath(path);
		    		image.setResult(true);
		    		Bimp.tempSelectBitmap.add(0,image);
					mHandler.sendEmptyMessage(12);
				}
			}
		}else if(requestCode==SELECTRESOURCE){//从素材库选择
			if(IsNonEmptyUtils.isList(Bimp.tempSelectBitmap)){
				for(int i=0;i<Bimp.tempSelectBitmap.size();i++){
					if(Bimp.tempSelectBitmap.get(i)==null){
						Bimp.tempSelectBitmap.remove(i);
					}
				}
				
				if(IsNonEmptyUtils.isList(Bimp.tempSelectBitmap)){
					String path=Bimp.tempSelectBitmap.get(0).getFilepath();
					if(path.endsWith(".jpg")||path.endsWith(".png")){
						mHandler.sendEmptyMessage(10);
					}else if(path.endsWith(".mp3")||path.endsWith(".wav")||path.endsWith(".pcm")){
						mHandler.sendEmptyMessage(11);
					}else if(path.endsWith(".mp4")){
						mHandler.sendEmptyMessage(12);
					}
				}
			}
		}
	}
	@Override
	public void onCurrentVoice(int currentVolume) {
		mHandler.sendEmptyMessage(currentVolume);
	}
	@Override
	protected void initRequestOnSuccess(String result, int code, int biaoshi) {
	}
	@Override
	protected void initRequestOnStart(String result, int code, int biaoshi) {
	}
	@Override
	protected void initRequestOnFailure(String failure, int code, int biaoshi) {
	}
	@Override
	protected void initRequestOnCache(String result, int code, int biaoshi) {
	}
}
