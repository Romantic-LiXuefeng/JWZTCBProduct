package com.jwzt.caibian.activity;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.adapter.ChatAdapter;
import com.jwzt.caibian.application.BaseActivity;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.application.Configs;
import com.jwzt.caibian.bean.ChatMessageBean;
import com.jwzt.caibian.bean.GroupListBean;
import com.jwzt.caibian.bean.LoginBean;
import com.jwzt.caibian.db.ChatsDao;
import com.jwzt.caibian.interfaces.ChatListRefreshInterface;
import com.jwzt.caibian.interfaces.VolumeListener;
import com.jwzt.caibian.util.BitmapUtils;
import com.jwzt.caibian.util.ChatUtils;
import com.jwzt.caibian.util.DeviceUtils;
import com.jwzt.caibian.util.FileUtil;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.UIUtils;
import com.jwzt.caibian.util.UserToast;
import com.jwzt.caibian.util.Utils;
import com.jwzt.caibian.xiangce.Bimp;
import com.jwzt.caibian.xiangce.ItemImage;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.zc.RecordDemo.MyAudioRecorder;
/**
 * 聊天界面
 * @author howie
 *
 */
public class ChatActivity extends BaseActivity implements OnClickListener,VolumeListener,ChatListRefreshInterface {
	private ListView lv;
	private View iv_back,iv_group;
	/**添加图片的面板是否可见*/
	private boolean isAddVisible;
	private View iv_plus;
	private View ll;
	/**添加图片，拍照，添加文件，定位*/
	private View sl_image,sl_camera,sl_file,sl_location;
	private ChatAdapter chatAdapter;
	private ImageView iv_sound;
	/**标题*/
	private TextView tv_titles;
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
	private ImageView iv_recording;
	/***定义计时器*/ 
	private boolean bool;
	/***录音时长*/
	private int luYinTime;
	/***限制录音时长*/
	private int totalTime=60;
	/***录音完成后保存的路径*/
	private String mp3Path;
	
	private EditText et_content;
	private Button bt_send,bt_press_speak;//发送和按下说话
	private ImageView iv_play;
	private String personNum;
	private String iemi;
	private List<ChatMessageBean> mListBean;
	private GroupListBean groupListBean;
	private CbApplication application;
	private LoginBean mLoginBean;
	/***1：左文字、2：左图片、4：左音频、3：左视频、5：右文字、6：右图片、7：右视频、8：右音频*/
	private int msgType;
	/***标识是否是录音状态*/
	private boolean isLuying=false;
	
	private ChatsDao mChatsDao;
	private List<String> listString;
	
	private ProgressBar pb_upload;
	
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
				pb_upload.setVisibility(View.GONE);
				if(IsNonEmptyUtils.isList(listString)){
					listString.clear();
				}
				if(IsNonEmptyUtils.isList(Bimp.tempSelectBitmap)){
					Bimp.tempSelectBitmap.clear();
				}
				
				if(IsNonEmptyUtils.isString(groupListBean.getGroupId())&&IsNonEmptyUtils.isString(mLoginBean.getUserID())){
					mListBean=mChatsDao.getTasks(new Integer(groupListBean.getGroupId()), new Integer(mLoginBean.getUserID()));
					if(IsNonEmptyUtils.isList(mListBean)){
						initView();
					}
				}
				break;
			case 10://上传音频
				File files = FileUtil.packageFile(Environment.getExternalStorageDirectory()+"/upload", listString);
				toUpMp3(files.getAbsolutePath());
				break;
			case 11://上传图片
				if (IsNonEmptyUtils.isList(Bimp.tempSelectBitmap)) {
					for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
						String imgpath=Bimp.tempSelectBitmap.get(i).getFilepath();
						if(imgpath.startsWith("file:///")){
							String path=imgpath.replaceFirst("file:///", "");
							listString.add(path);
						}else{
							listString.add(imgpath);
						}
					}
					File fileimg = FileUtil.packageFile(Environment.getExternalStorageDirectory()+"/upload", listString);
					toUpImg(fileimg.getAbsolutePath());
					// 隐藏popwindow；
				}
				break;
			case 12://上传视频
				File fileMp4 = FileUtil.packageFile(Environment.getExternalStorageDirectory()+"/upload", listString);
				toUpMp4(fileMp4.getAbsolutePath());
				break;
			case 13:
				if(pb_upload!=null){
					pb_upload.setVisibility(View.GONE);
				}
				dismisLoadingDialog();
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		personNum=getIntent().getStringExtra("personNum");
		groupListBean=(GroupListBean) getIntent().getSerializableExtra("groupListBean");
		iemi=DeviceUtils.getDeviceUUID(ChatActivity.this).toString();
		recorder = new MyAudioRecorder("voice_luyin");
		application=(CbApplication) getApplication();
		mLoginBean=application.getmLoginBean();
		mListBean=new ArrayList<ChatMessageBean>();
		listString=new ArrayList<String>();
		mChatsDao=new ChatsDao(getHelper());
		ChatUtils.setRefresh(ChatActivity.this);
		findViews();
		if(IsNonEmptyUtils.isString(groupListBean.getGroupId())){
			application.setGroupId(groupListBean.getGroupId());
			mListBean=mChatsDao.getTasks(new Integer(groupListBean.getGroupId()), new Integer(mLoginBean.getUserID()));
			if(IsNonEmptyUtils.isList(mListBean)){
				initView();
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		application.setGroupId("");
	}
	private void findViews() {
		tv_titles=(TextView) findViewById(R.id.tv_titles);
//		tv_titles.setText("任务交流("+personNum+")");
		tv_titles.setText("任务交流");
		iv_group=findViewById(R.id.iv_group);
		iv_back=findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		lv=(ListView) findViewById(R.id.lv);
		lv.setCacheColorHint(0);
		iv_plus=findViewById(R.id.iv_plus);
		iv_plus.setOnClickListener(this);
		ll=findViewById(R.id.ll);
		sl_image=findViewById(R.id.sl_image);
		sl_camera=findViewById(R.id.sl_camera);
		sl_file=findViewById(R.id.sl_file);
		sl_location=findViewById(R.id.sl_location);
		sl_image.setOnClickListener(this);
		sl_camera.setOnClickListener(this);
		sl_file.setOnClickListener(this);
		sl_location.setOnClickListener(this);
		iv_group.setOnClickListener(this);
		iv_sound=(ImageView) findViewById(R.id.iv_sound);
		iv_sound.setOnClickListener(this);
		
		pb_upload = (ProgressBar) findViewById(R.id.pb_upload);
		
		bt_press_speak=(Button) findViewById(R.id.bt_press_speak);//按下说话
		bt_send=(Button) findViewById(R.id.bt_send);
		et_content=(EditText) findViewById(R.id.et_input);
		rl_recording=(RelativeLayout) findViewById(R.id.rl_recording);
		iv_recording=(ImageView) findViewById(R.id.iv_recording);
		iv_play=(ImageView) findViewById(R.id.iv_play);
		iv_play.setOnClickListener(this);
		
		bt_send.setOnClickListener(this);
		bt_press_speak.setOnClickListener(this);
		bt_press_speak.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:// 按下事件
					bt_press_speak.setBackgroundResource(R.drawable.new_chat_send_voice_d);
					bt_press_speak.setTextColor(getResources().getColor(R.color.white));
					//标记为没有滑离按钮
					luyinflag=true;
					if (null != recorder) {
						start_record=System.currentTimeMillis();
						recorder.prepare();
						recorder.startRecording();
						recorder.setmVolumeListener(ChatActivity.this);
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
		
		et_content.addTextChangedListener(new TextWatcher() {
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
				
				String content=et_content.getText().toString();
				if(IsNonEmptyUtils.isString(content)){
					msgType=1;
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
	}
	
	/**
	 * 初始化列表
	 */
	private void initView(){
		for(int i=0;i<mListBean.size();i++){//改循环将改群组中所有消息变成已读消息
			ChatMessageBean chatMessageBean=mListBean.get(i);
			chatMessageBean.setIsRead("0");
			mChatsDao.saveOrUpdateTask(chatMessageBean);
		}
		chatAdapter = new ChatAdapter(ChatActivity.this,mListBean,mLoginBean.getUserID());
		lv.setAdapter(chatAdapter);
		lv.setSelection(mListBean.size()-1);
		dismisLoadingDialog();
	}
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()){
		case R.id.iv_back:
			finish();
			overridePendingTransition(R.anim.push_left_out,R.anim.push_right_out);
		break;
		case R.id.iv_plus:
			if(!isAddVisible){
				ll.setVisibility(View.VISIBLE);
				isAddVisible=true;
			}else{
				ll.setVisibility(View.GONE);
				isAddVisible=false;
			}
			break;
		case R.id.bt_send:
			String content=et_content.getText().toString();
			if(content.isEmpty()){
				Toast.makeText(ChatActivity.this, "请输入内容", 0).show();
				return ;
			}
			
			if(mLoginBean!=null){
				try {/***1：左文字、2：左图片、3：左音频、4：左视频、5：右文字、6：右图片、7：右视频、8：右音频*/
					String userId=mLoginBean.getUserID();
					String groupId=groupListBean.getGroupId();
					String contents=URLEncoder.encode(content, "utf-8");
					sendMessageText(userId, groupId, contents,msgType+"", iemi);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			
			et_content.setText("");
			break;
	case R.id.sl_image://添加本地图片
		if(Bimp.tempSelectBitmap.size()>=9){
			UserToast.toSetToast(ChatActivity.this, "已到选择上线");
		}else{
			Intent intent_select = new Intent(ChatActivity.this, PhotoPickerActivity.class);
			intent_select.putExtra(PhotoPickerActivity.EXTRA_SHOW_CAMERA, false);
			intent_select.putExtra(PhotoPickerActivity.EXTRA_SELECT_MODE, 1);
			intent_select.putExtra(PhotoPickerActivity.ALL_TYPE, PhotoPickerActivity.ALL_PHOTO);
			intent_select.putExtra(PhotoPickerActivity.EXTRA_MAX_MUN, 100);
	        startActivityForResult(intent_select, TAKE_PICTURE);
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
		}
			break;
		case R.id.sl_camera://拍照
			if(Bimp.tempSelectBitmap.size()>=9){
				UserToast.toSetToast(ChatActivity.this, "已到选择上线");
			}else{
				Intent intent_paizhao = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				IMG_SAVE_NAME = Utils.getUUID() + ".png";
				intent_paizhao.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(new File(FileUtil.getFolderPath(),IMG_SAVE_NAME)));
				startActivityForResult(intent_paizhao, PHOTOHRAPH);
				overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			}
			break;
		case R.id.sl_file://本地文件
			Intent intent_doc=new Intent(ChatActivity.this,SelectFootageActivity.class);
			Bimp.tempSelectBitmap.add(null);
			startActivityForResult(intent_doc, SELECTRESOURCE);
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			break;
		case R.id.sl_location://定位
			msgType=1;
			if(mLoginBean!=null){
				try {
					/***1：左文字、2：左图片、3：左音频、4：左视频、5：右文字、6：右图片、7：右视频、8：右音频*/
					String userId=mLoginBean.getUserID();
					String groupId=groupListBean.getGroupId();
					String location=application.getLocation();
					String contents=URLEncoder.encode(location, "utf-8");
					sendMessageText(userId, groupId, contents,msgType+"", iemi);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			break;
		case R.id.iv_group://右上角的群组按钮
			Intent intent=new Intent(ChatActivity.this,TaskGroupActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
		break;
		case R.id.iv_sound:
			if(isLuying){
				isLuying=false;
				et_content.setVisibility(View.VISIBLE);
				bt_press_speak.setVisibility(View.GONE);
			}else{
				isLuying=true;
				et_content.setVisibility(View.GONE);
				bt_press_speak.setVisibility(View.VISIBLE);
			}
//			if(luyinflag){
//				luyinflag=false;
//				LuYinTaiQi();
//			}else{
//				luyinflag=true;
//				if (null != recorder) {
//					start_record=System.currentTimeMillis();
//					recorder.prepare();
//					recorder.startRecording();
//					recorder.setmVolumeListener(ChatActivity.this);
//				} else {
//					recorder = new MyAudioRecorder("voice_luyin");
//					recorder.prepare();
//					recorder.startRecording();
//				}
//				
//				// 显示“录音中”的提示框
//				rl_recording.setVisibility(View.VISIBLE);
//				
//				//开始计时
//				bool = true;
//				luYinTime=0;
//				mHandler.postDelayed(task, 1000);
//			}
			break;
		case R.id.iv_play:
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode==TAKE_PICTURE) {//本地选择图片
			if(IsNonEmptyUtils.isList(Bimp.tempSelectBitmap)){
				mHandler.sendEmptyMessage(11);
			}
		}else if(requestCode==PHOTOHRAPH){//表示拍照
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
    		   mHandler.sendEmptyMessage(11);
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
					listString.add(path);
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
						mHandler.sendEmptyMessage(11);
					}else if(path.endsWith(".mp3")||path.endsWith(".wav")||path.endsWith(".pcm")){
						listString.add(path);
						mHandler.sendEmptyMessage(10);
					}else if(path.endsWith(".mp4")){
						listString.add(path);
						mHandler.sendEmptyMessage(12);
					}
				}
			}
		}
	}
	
	/**
	 * 上传录音
	 */
	public void toUpMp3(String absolutePath) {
		showLoadingDialog(ChatActivity.this, "", "");
		msgType=3;
		String userId=mLoginBean.getUserID();
		String groupId=groupListBean.getGroupId();
//		String contents=URLEncoder.encode(content, "utf-8");
		String sendMessageUrl=String.format(Configs.sendMessageUrl, userId,groupId,"",msgType,iemi);
		RequestParams params = new RequestParams();
		params.addBodyParameter("file", new File(absolutePath));
		HttpUtils http = new HttpUtils();
		http.send(com.lidroid.xutils.http.client.HttpRequest.HttpMethod.POST,
				sendMessageUrl, params, new RequestCallBack<String>() {

			@Override
			public void onStart() {
						pb_upload.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
						if (isUploading) {
							int precent = (int) (current * 100) / (int) total;
							pb_upload.setProgress(precent);
							System.out.println("uploaduploadupload: "+ current + "/" + total + "/" + precent);
						} else {
						}
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						System.out.println("uploaduploaduploadsuccess: "+ responseInfo.result);
//						if (responseInfo.result.contains("成功")) {
//							mHandler.sendEmptyMessageDelayed(9, 500);
//						}
						mHandler.sendEmptyMessage(13);
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						pb_upload.setVisibility(View.GONE);
						System.out.println("uploaduploaduploadfial: "+ msg);
						mHandler.sendEmptyMessage(13);
//						if(IsNonEmptyUtils.isList(huodongmiddle)){
//							UserToast.toSetToast(CommunityActivity.this, "上传失败");
//							mHandler.sendEmptyMessage(36);
//						}else{
//							UserToast.toSetToast(CommunityActivity.this, "上传失败");
//							mHandler.sendEmptyMessage(24);
//						}
					}
				});
	}
	
	/**
	 * 上传图片
	 * 
	 * @param absolutePath
	 */
	private void toUpImg(String absolutePath) {
		showLoadingDialog(ChatActivity.this, "", "");
		msgType=2;
		String userId=mLoginBean.getUserID();
		String groupId=groupListBean.getGroupId();
//		String contents=URLEncoder.encode(content, "utf-8");
		String sendMessageUrl=String.format(Configs.sendMessageUrl, userId,groupId,"",msgType,iemi);
		RequestParams params = new RequestParams();
		params.addBodyParameter("file", new File(absolutePath));
		HttpUtils http = new HttpUtils();
		http.send(com.lidroid.xutils.http.client.HttpRequest.HttpMethod.POST,
				sendMessageUrl, params, new RequestCallBack<String>() {

					
					public void onStart() {
						pb_upload.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoading(long total, long current,boolean isUploading) {
						if (isUploading) {
							int precent = (int) (current * 100) / (int) total;
							pb_upload.setProgress(precent);
							//System.out.println(("uploaduploadupload: "+ current + "/" + total + "/" + precent));
						} else {
						}
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
//						if (responseInfo.result.contains("成功")) {
//							mHandler.sendEmptyMessage(9);
//						}
						mHandler.sendEmptyMessage(13);
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						pb_upload.setVisibility(View.GONE);
						System.out.println("upload:"+ error.getExceptionCode() + ":" + msg);
						mHandler.sendEmptyMessage(13);
//						if(IsNonEmptyUtils.isList(huodongmiddle)){
//							UserToast.toSetToast(CommunityActivity.this, "上传失败");
//							Bimp.tempSelectBitmap.clear();
//							mHandler.sendEmptyMessage(36);
//						}else{
//							UserToast.toSetToast(CommunityActivity.this, "上传失败");
//							Bimp.tempSelectBitmap.clear();
//							mHandler.sendEmptyMessage(24);
//						}
					}
				});

	}
	
	/**
	 * 上传视频
	 * 
	 * @param absolutePath
	 */
	private void toUpMp4(String absolutePath) {
		showLoadingDialog(ChatActivity.this, "", "");
		msgType=4;
		String userId=mLoginBean.getUserID();
		String groupId=groupListBean.getGroupId();
//		String contents=URLEncoder.encode(content, "utf-8");
		String sendMessageUrl=String.format(Configs.sendMessageUrl, userId,groupId,"",msgType,iemi);
		RequestParams params = new RequestParams();
		params.addBodyParameter("file", new File(absolutePath));
		HttpUtils http = new HttpUtils();
		http.send(com.lidroid.xutils.http.client.HttpRequest.HttpMethod.POST,
				sendMessageUrl, params, new RequestCallBack<String>() {

					@Override
					public void onStart() {
						pb_upload.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoading(long total, long current,boolean isUploading) {
						if (isUploading) {
							int precent = (int) (current * 100) / (int) total;
//							pb_upload.setProgress(precent);
							System.out.println(("uploaduploadupload: "+ current + "/" + total + "/" + precent));
							pb_upload.setProgress(precent);
							//System.out.println("uploaduploadupload: "+ current + "/" + total + "/" + precent);
						} else {
						}
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
//						if (responseInfo.result.contains("成功")) {
//							mHandler.sendEmptyMessageDelayed(9, 500);
//						}
						mHandler.sendEmptyMessage(13);
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						System.out.println("upload:"+ error.getExceptionCode() + ":" + msg);
						mHandler.sendEmptyMessage(13);
//						if(IsNonEmptyUtils.isList(huodongmiddle)){
//							UserToast.toSetToast(CommunityActivity.this, "上传失败");
//							Bimp.tempSelectBitmap.clear();
//							mHandler.sendEmptyMessage(36);
//						}else{
//							UserToast.toSetToast(CommunityActivity.this, "上传失败");
//							Bimp.tempSelectBitmap.clear();
//							mHandler.sendEmptyMessage(24);
//						}
					}
				});

	}
	
	/**
	 * 发送文字
	 * @param userId
	 * @param groupid
	 * @param content
	 * @param msgType
	 * @param imei
	 */
	private void sendMessageText(String userId,String groupid,String content,String msgType,String imei){
		if (CbApplication.getNetType()!=-1) {
			String sendMessageUrl=String.format(Configs.sendMessageUrl, mLoginBean.getUserID(),groupid,content,msgType,imei);
			System.out.println("sendMessageUrl:"+sendMessageUrl);
			RequestData(sendMessageUrl, Configs.sendMessageCode, -1);
		}else {
			UserToast.toSetToast(UIUtils.getContext(), getString(R.string.please_check_net));
		}
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
		image.setBitmap(BitmapUtils.readBitMap(ChatActivity.this, R.drawable.replace));
		image.setFilepath(mp3Path);
		image.setResult(true);
		Bimp.tempSelectBitmap.add(0,image);
		listString.add(mp3Path);
		mHandler.sendEmptyMessage(10);
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
	

	@Override
	public void onCurrentVoice(int currentVolume) {
		mHandler.sendEmptyMessage(currentVolume);
	}
	@Override
	protected void initRequestOnSuccess(String result, int code, int biaoshi) {
		// TODO Auto-generated method stub
//		initDataParse(result, code);
	}
	@Override
	protected void initRequestOnStart(String result, int code, int biaoshi) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void initRequestOnFailure(String failure, int code, int biaoshi) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void initRequestOnCache(String result, int code, int biaoshi) {
		// TODO Auto-generated method stub
//		initDataParse(result, code);
	}
	
	private void initDataParse(String result, int code){
		if(code==Configs.sendMessageCode){
			JSONObject jsonObject=JSON.parseObject(result);
			String status=jsonObject.getString("status");
			if(status.equals("100")){//表示获取成功
				String dataresult=jsonObject.getString("data");
				ChatMessageBean chatMessageBean=JSON.parseObject(dataresult,ChatMessageBean.class);
				if(chatMessageBean!=null){//入库操作
					if(mLoginBean!=null){
						mHandler.sendEmptyMessageDelayed(9, 500);
					}
				}
			}
		}
	}
	@Override
	public void setRefresh(ChatMessageBean chatMessageBean) {
		// TODO Auto-generated method stub
//		if(){
//			
//		}
		mHandler.sendEmptyMessageDelayed(9, 1000);
	}
	
}
