package com.jwzt.caibian.fragment;

import java.io.File;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.jwzt.caibian.activity.NewScriptActivity;
import com.jwzt.caibian.activity.PhotoPickerActivity;
import com.jwzt.caibian.activity.RecordActivity;
import com.jwzt.caibian.activity.VideoRecordActivity;
import com.jwzt.caibian.adapter.VideoFootageAdapter;
import com.jwzt.caibian.adapter.VideoFootageAdapter.VideoEditListener;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.application.Configs;
import com.jwzt.caibian.bean.AttachsBeen;
import com.jwzt.caibian.bean.CloseMediaButton;
import com.jwzt.caibian.bean.ComposeInfo;
import com.jwzt.caibian.bean.DismissMessage;
import com.jwzt.caibian.bean.LoginBean;
import com.jwzt.caibian.bean.MergeVideoNameMessage;
import com.jwzt.caibian.bean.MessageEvent;
import com.jwzt.caibian.bean.NewMessage;
import com.jwzt.caibian.bean.PickMessage;
import com.jwzt.caibian.bean.PlayCompleteMessage;
import com.jwzt.caibian.bean.ResetMessage;
import com.jwzt.caibian.bean.ResolutionBean;
import com.jwzt.caibian.bean.ShowPlusMessage;
import com.jwzt.caibian.bean.StringMessage;
import com.jwzt.caibian.bean.VideoPath;
import com.jwzt.caibian.interfaces.EmptyListener;
import com.jwzt.caibian.interfaces.PlayerVideoInterface;
import com.jwzt.caibian.interfaces.UploadIndexListener;
import com.jwzt.caibian.util.Config;
import com.jwzt.caibian.util.DialogHelp;
import com.jwzt.caibian.util.FileOperateUtil;
import com.jwzt.caibian.util.FileUtil;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.MyWindowManager;
import com.jwzt.caibian.util.RDSdkUtils;
import com.jwzt.caibian.util.VideoStitchingRequest;
import com.jwzt.caibian.widget.DragSortListView;
import com.jwzt.caibian.xiangce.Bimp;
import com.jwzt.cb.product.R;
import com.rd.lib.utils.CoreUtils;
import com.rd.vecore.exception.InvalidArgumentException;
import com.rd.veuisdk.SdkEntry;

import static com.rd.veuisdk.SdkEntry.editMedia;
import static com.rd.veuisdk.manager.CameraConfiguration.SQUARE_SCREEN_CAN_CHANGE;

/**
 * 素材管理中视频素材对应的fragment
 * 
 * @author howie
 * 
 */
public class FootageVideoFragment extends BaseFootageFragment implements OnClickListener,VideoEditListener,EmptyListener,UploadIndexListener,PlayerVideoInterface{
	private AlertDialog alertDialog;
	/**正在编辑的索引，用于编辑合并完毕之后去更新UI*/
	private int editingIndex=-1;
	public static final String ACTION="videoMerge";
	private ProgressDialog progressDialog;
	private String listValue,listPath;
	/**是否已经跳转到了拾取多个视频的界面，通过改变这个变量防止打开多个VideoGridActivity*/
	private boolean isPicking;
	/**从本地拾取视频*/
	private static final int PICK_VIDEO=1;
	/**录制视频*/
	private static final int RECORD_VIDEO=2;
	private VideoFootageAdapter videoFootageAdapter;
	private DragSortListView lv;
	private TextView tv_delete;
	private ArrayList<String> strs;
	/** 视频附件的集合 */
	private ArrayList<AttachsBeen> attachList = new ArrayList<AttachsBeen>();
	/**暂无素材*/
	private View tv_empty;
	private View tv_merge;
	private boolean isRegistered;
	/**当前fragment是否可见*/
	private boolean isVisible=true;
	private String uploadPath;//在列表中点击上传按钮后保存的要上传的资源地址
	private String xinghao;
	
	private Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case 0:
				SharedPreferences sp=getActivity().getSharedPreferences("footage", Context.MODE_PRIVATE);
				String mergePath = sp.getString("mergePath", "");
				if(TextUtils.isEmpty(mergePath)){//
					
					sendEmptyMessageDelayed(0, 1000);//每隔一秒检查一次sharedPreference
				}else{
					if(progressDialog!=null&&progressDialog.isShowing()){
						progressDialog.dismiss();
					}

					listPath=mergePath;
					
					listValue= listPath.substring(listPath.lastIndexOf("/") + 1);
					refreshListView();  
					Editor editor = sp.edit();
					editor.putString("mergePath",  "");//清空sharedPreference
					editor.commit();
					removeMessages(0);
				}
				break;
			case 1://上传   
				Intent intentUpload=new Intent(getActivity(), NewScriptActivity.class);
				intentUpload.putExtra("tag", "resources");
				intentUpload.putExtra("resourcespath", uploadPath);
				getActivity().startActivity(intentUpload);
//				String userID = loginBean.getUserID();   
//				if(!TextUtils.isEmpty(userID)&&uploadList!=null&&!uploadList.isEmpty()){
//					toUpImg(userID, uploadList);
//					if(progressDialog==null){
//						progressDialog=DialogHelp.getWaitDialog(getActivity(), "");
//					}
//					progressDialog.setMessage("开始准备上传");
//					progressDialog.show();
//				}
				break;
			case 2:
				refreshListView();
				break;
			case 3:
				//TODO
				progressDialog.dismiss();
				progressDialog.cancel();
				progressDialog=null;
				listPath=finalPath;
				listValue= finalPath.substring(finalPath.lastIndexOf("/") + 1);
				refreshListView();
				Toast.makeText(getActivity(), "合并成功！", Toast.LENGTH_SHORT).show();
				break;
			case 4:
				progressDialog.dismiss();
				progressDialog.cancel();
				progressDialog=null;
				Toast.makeText(getActivity(), "合并失败！", Toast.LENGTH_SHORT).show();
				break;
			}
			
		};
	};
	private RDSdkUtils rdSdkUtils;

	@RequiresApi(api = Build.VERSION_CODES.M)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		app = (CbApplication) getActivity().getApplication();
		loginBean = app.getmLoginBean();
		mUserId=loginBean.getUserID();
		xinghao = android.os.Build.MODEL.trim();
		if(!isRegistered){
			EventBus.getDefault().register(FootageVideoFragment.this);
			isRegistered=true;
		}
		attachList = readCache();
		strs=new ArrayList<String>();
		View view = View.inflate(getActivity(), R.layout.fragment_audio_footage_layout,null);
		tv_empty=view.findViewById(R.id.rl_empty);
		tv_merge=view.findViewById(R.id.tv_merge);
		tv_merge.setOnClickListener(this);
		tv_delete=(TextView) view.findViewById(R.id.tv_delete);
		tv_delete.setOnClickListener(this);
		lv = (DragSortListView) view.findViewById(R.id.lv);
		
		 lv.setDropListener(onDrop);
	     lv.setRemoveListener(onRemove);
	     
	     if(attachList!=null&&!attachList.isEmpty()){
	    	 initAdapter();
	    	 tv_empty.setVisibility(View.GONE);
	     }else{
	    	 tv_empty.setVisibility(View.VISIBLE);
	     }
		rdSdkUtils = new RDSdkUtils(getActivity());
		rdSdkUtils.restoreConfigInstanceState();
		boolean hasM = CoreUtils.hasM();
		if (hasM && !SdkEntry.isInitialized()) {
			int re = getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
			if (re != PackageManager.PERMISSION_GRANTED) {

			}
		} else {
			System.out.println();
			rdSdkUtils.exportDemoResource();
		}


		// 初始化秀拍客配置
		rdSdkUtils.initEditorUIAndExportConfig();
		//registerAllResultHandlers();
		return view;
	}

	
	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			AttachsBeen item = videoFootageAdapter.getItem(from);
			/*if(item.getStatus()==AttachsBeen.STATUS_PLAYING){//如果移动之前处于播放状态，就修改正在播放的index
				videoFootageAdapter.setCurrentPlaying(to);
			}*/
			if(from==videoFootageAdapter.getCurrentPlaying()){//如果移动之前处于播放状态，就修改正在播放的index为to
				videoFootageAdapter.setCurrentPlaying(to);
			}
			videoFootageAdapter.remove(item);
			videoFootageAdapter.insert(item, to);
		}
	};

	private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
		@Override
		public void remove(int which) {
			videoFootageAdapter.remove(videoFootageAdapter.getItem(which));
			
		}
	};
	private ProgressDialog waitDialog;
	private CbApplication app;
	private LoginBean loginBean;
	private List<String> uploadList;
	private ArrayList<AttachsBeen> mergeList;
	private ArrayList<String> mergePaths=new ArrayList<String>();
	private ResolutionBean resolution;
	/**合并生成的视频的名字*/
	private String mergeVideoName,mUserId;
//	private Gson gson;
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.tv_yes://确认删除
			alertDialog.dismiss();
			if(attachList!=null&&!attachList.isEmpty()){
				for (int i = 0; i < attachList.size(); i++) {
					AttachsBeen attachsBeen = attachList.get(i);
					if(attachsBeen.getStatus()==AttachsBeen.STATUS_SELECTED){
						String achsPath = attachsBeen.getAchsPath();//缩略图
						File file1=new File(achsPath);
						if(file1.exists()){
							file1.delete();
						}
						attachList.remove(i);
						i--;
					}
				}
				
				saveVideoFootages();
			}
			if(attachList!=null&&attachList.isEmpty()){//如果清空了数据
				if(videoFootageAdapter!=null){
					videoFootageAdapter.setEditing(false);
					tv_empty.setVisibility(View.GONE);
					EventBus.getDefault().post(new ShowPlusMessage(true));
				}
			}
			videoFootageAdapter.notifyDataSetChanged();
			
			break;
		case R.id.tv_no://否
			alertDialog.dismiss();
			break;
		case R.id.tv_merge://合并
			mergeList = new ArrayList<AttachsBeen>();
			for (int i = 0; i < attachList.size(); i++) {
				AttachsBeen attachsBeen = attachList.get(i);
				if(attachsBeen.getStatus()==AttachsBeen.STATUS_SELECTED){
					mergeList.add(attachsBeen);
					
				}
			}
			if(mergeList.isEmpty()){//如果一个视频都没有选中
				Toast.makeText(getActivity(), "您还没有选择任何视频", 0).show();
				return;
			}
			if(mergeList.size()==1){
				Toast.makeText(getActivity(), "一个视频不能进行合并操作", 0).show();
				return;
			}
			resolution = null;
			mergePaths.clear();//清空之前的集合
//			Toast.makeText(getActivity(), "分辨率宽"+resolution.getWidth()+"高"+resolution.getHeight(), 0).show();
			for (int i = 0; i < mergeList.size(); i++) {
				AttachsBeen attachsBeen = mergeList.get(i);
				mergePaths.add(mergeList.get(i).getAchsPath());
				
				if(resolution==null){
					resolution = getResolution(mergeList.get(i).getAchsPath());
				}
				ResolutionBean bean=getResolution(mergeList.get(i).getAchsPath());
				if(!resolution.getWidth().equals(bean.getWidth())||!resolution.getHeight().equals(bean.getHeight())){
					Toast.makeText(getActivity(), "视频的分辨率不一致，不能进行合并", 0).show();
					return;
				}
			}
			String format=null;
			for (int i = 0; i < mergeList.size(); i++) {
				if(format==null){
					format=getFormat(mergeList.get(i).getAchsPath());
				}
				String mFormat=getFormat(mergeList.get(i).getAchsPath());
				if(!mFormat.equals(format)){
					Toast.makeText(getActivity(), "文件格式不一致，不能进行合并", 0).show();
					return;
				}
			}
			//TODO
			MergeVideos(mergePaths);
			break;
		case R.id.tv_delete://删除按钮
			if(attachList!=null&&!attachList.isEmpty()){
				for (int i = 0; i < attachList.size(); i++) {
					AttachsBeen attachsBeen = attachList.get(i);
					if(attachsBeen.getStatus()==AttachsBeen.STATUS_SELECTED){//只有选中了视频素材的时候才弹出对话框
						showTip();
						break;
					}
				}
			}
			break;
//		case R.id.viewMask:
//			//TODO
//		if(playVideopopu!=null){
//			if(playVideopopu.isShowing()){
//				playVideopopu.dismiss();
//				playVideopopu=null;
//				AnimationUtils.hide(viewMask);
//				viewMask.setVisibility(View.GONE);
//			}
//		}
//			break;
		}
	}
	@Subscribe(threadMode=ThreadMode.MAIN)
	public void receiver(MessageEvent bean){
//		tv.setText(bean.getName());
	//	System.out.println("bean.getWhich()===="+bean.getWhich()+"==editing=="+videoFootageAdapter.isEditing());
		if(bean.getWhich()==0){
			boolean editing = videoFootageAdapter.isEditing();
			if(editing){
				tv_merge.setVisibility(View.GONE);//隐藏合并按钮和删除按钮
				tv_delete.setVisibility(View.GONE);
				for (int i = 0; i < attachList.size(); i++) {
					if(i!=videoFootageAdapter.getCurrentPlaying()){//如果没有处于播放状态就置为正常状态
						attachList.get(i).setStatus(AttachsBeen.STATUS_NORMAL);
					}else{
						attachList.get(i).setStatus(AttachsBeen.STATUS_PLAYING);
					}
				}
				saveVideoFootages();//根据新的列表顺序保存一下
			}else{
			//	tv_merge.setVisibility(View.VISIBLE);
				tv_delete.setVisibility(View.VISIBLE);
				for (AttachsBeen attachBean : attachList) {
					attachBean.setStatus(AttachsBeen.STATUS_UNSELECTED);//未选择状态
				}
			}
			videoFootageAdapter.setEditing(!editing);
			videoFootageAdapter.notifyDataSetChanged();
		}
		
			
		
	}
	@Subscribe(threadMode =ThreadMode.MAIN)
	public void reset(ResetMessage message){
			if(videoFootageAdapter!=null){
				videoFootageAdapter.setEditing(false);
			}
			
			if(attachList!=null&&!attachList.isEmpty()){
				for (AttachsBeen attachsBeen : attachList) {
					attachsBeen.setStatus(AttachsBeen.STATUS_NORMAL);//这里暂时都设置为正常状态，等加上播放视频的逻辑之后需要再修改一下
				}
				videoFootageAdapter.notifyDataSetChanged();
				tv_delete.setVisibility(View.GONE);
				tv_merge.setVisibility(View.GONE);
			}
			
	
		
	}
	@Override
	public void onDestroy() {
		if(isRegistered){
			EventBus.getDefault().unregister(FootageVideoFragment.this);
		}
		isRegistered=false;
		super.onDestroy();
	}
	
	
	
	
	
	
	/**
	 * 开始从本地拾取视频
	 */
	@Subscribe
	public void startPick(PickMessage message){
		if(message.getType()==PickMessage.TYPE_VIDEO){
			/*Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("video/*");
			startActivityForResult(intent, PICK_VIDEO);*/
         //   System.out.println("isPicking==="+isPicking);
          //  if(!isPicking){
				Intent intent_select = new Intent(getActivity(), PhotoPickerActivity.class);
				intent_select.putExtra(PhotoPickerActivity.EXTRA_SHOW_CAMERA, false);
				intent_select.putExtra(PhotoPickerActivity.EXTRA_SELECT_MODE, 1);
				intent_select.putExtra(PhotoPickerActivity.ALL_TYPE, PhotoPickerActivity.ALL_VIDEO);
				intent_select.putExtra(PhotoPickerActivity.EXTRA_MAX_MUN, 100);
		        startActivityForResult(intent_select, PICK_VIDEO);
		        getActivity().overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
				
			//	isPicking=true;
			//}
			
		}
	}
	@Subscribe
	public void makeNew(NewMessage message){
		if(message.getType()==NewMessage.TYPE_VIDEO){
			//开始录制视频
//			Intent intent=new Intent(getActivity(),RecordActivity.class);
			Intent intent=new Intent(getActivity(),VideoRecordActivity.class);
			startActivityForResult(intent, RECORD_VIDEO);
		}
		
	}
	/**
	 * 用于接收用户裁剪视频之后最终生成的视频的路径
	 */
	@Subscribe
	public void receiveVideoEditPath(StringMessage  strMessage){
		String path = strMessage.getMessage();
		listPath=path;
		if(TextUtils.isEmpty(listPath)){
			return;
		}
		listValue= listPath.substring(listPath.lastIndexOf("/") + 1);
		if(progressDialog==null){
			progressDialog=DialogHelp.getWaitDialog(getActivity(), "");
		}
		progressDialog.setMessage("列表刷新中...");
		progressDialog.show();
		handler.sendEmptyMessageDelayed(2, 500);
//		refreshListView();  
//		saveVideoFootages();
		
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		app = (CbApplication) getActivity().getApplication();
		loginBean = app.getmLoginBean();
		mUserId=loginBean.getUserID();
		attachList = readCache();
	     if(attachList!=null&&!attachList.isEmpty()){
	    	 if(videoFootageAdapter!=null&&videoFootageAdapter.isEditing()){
	    		 //当前为编辑状态
	    		 for(int i=0;i<attachList.size();i++){
	    			 attachList.get(i).setStatus(AttachsBeen.STATUS_UNSELECTED);
	    		 }
	    		 
	    	 }
	    	 
	    	 initAdapter();
	    	 tv_empty.setVisibility(View.GONE);
	     }else{
	    	 tv_empty.setVisibility(View.VISIBLE);
	     }
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==PICK_VIDEO){//从本地拾取视频
			if(IsNonEmptyUtils.isList(Bimp.tempSelectBitmap)){
				if(attachList==null){
					attachList=new ArrayList<AttachsBeen>();
				}
				for(int i=0;i<Bimp.tempSelectBitmap.size();i++){
					AttachsBeen bean=new AttachsBeen();
					String filepath=Bimp.tempSelectBitmap.get(i).getFilepath();
					System.out.println("/从本地拾取视频==="+filepath);
					String path="";
					if(filepath.startsWith("file:///")){
						path=filepath.replaceAll("file:///", "");
					}else{
						path=filepath;
					}
					bean.setAchsPath(path);
					attachList.add(0,bean);
				}
			
				initAdapter();
				if(IsNonEmptyUtils.isList(Bimp.tempSelectBitmap)){
					Bimp.tempSelectBitmap.clear();
				}
				saveVideoFootages();//保存到sharedPreferences,
			}
			
//			if(data!=null){
//				ArrayList<String> paths = data.getStringArrayListExtra("paths");
//				if(paths!=null&&!paths.isEmpty()){
//					for (int i = 0; i < paths.size(); i++) {
//						AttachsBeen been = new AttachsBeen();
//						been.setAchsPath(paths.get(i));
//						if(attachList==null){
//							attachList=new ArrayList<AttachsBeen>();
//						}
//						attachList.add(0, been);
//					}
//					initAdapter();
//					if(!attachList.isEmpty()){
//						saveVideoFootages();//保存到sharedPreferences,
//						 tv_empty.setVisibility(View.INVISIBLE);
//					}
//				}
//			}
			isPicking=false;
		}else if(requestCode==RECORD_VIDEO){//从视频录制界面返回的
				if(data!=null){
					listPath = data.getStringExtra("path");
					if(TextUtils.isEmpty(listPath)){
						return;
					}
					System.out.println("从视频录制界面返回的=listPath=="+listPath);
					listValue= listPath.substring(listPath.lastIndexOf("/") + 1);
					System.out.println("从视频录制界面返回的=listValue=="+listValue);
					refreshListView();
					if(attachList!=null&&!attachList.isEmpty()){
						saveVideoFootages();//保存到sharedPreferences,
						 tv_empty.setVisibility(View.GONE);
					}
				}
		}
	}
	/**
	 * 刷新listview的显示
	 */
	private void refreshListView() {
		if (!"".equals(listPath) && listPath != null
				&& listPath != "") {
			if (listValue.endsWith(".3gp") || listValue.endsWith(".mp4")
					|| listValue.endsWith(".wmv")
					|| listValue.endsWith(".flv")
					|| listValue.endsWith(".rmvb")
					|| listValue.endsWith(".avi")
					|| listValue.endsWith(".ts")) {

				AttachsBeen been = new AttachsBeen();
				if(videoFootageAdapter!=null&&videoFootageAdapter.isEditing()){
					been.setStatus(AttachsBeen.STATUS_UNSELECTED);
				}
				been.setAchsPath(listPath);
				if(attachList==null){
					attachList=new ArrayList<AttachsBeen>();
				}
				attachList.add(been);
				
				for(int i=0;i<attachList.size();i++){
					attachList.get(i).setStatus(AttachsBeen.STATUS_UNSELECTED);
				}
				
				initAdapter();
				if(!attachList.isEmpty()){
					saveVideoFootages();//保存到sharedPreferences,
				}
				
				if(progressDialog!=null){
					progressDialog.dismiss();
				}
				
				// this.setSelection(list.size() - 1);
				// adapater.notifyDataSetChanged();

			}

		}
	}
	/**
	 * 为适配器填充数据
	 */
	private void initAdapter() {
		Collections.sort(attachList,new Comparator<AttachsBeen>() {
			/**
			 *
			 * @param lhs
			 * @param rhs
			 * @return an integer < 0 if lhs is less than rhs, 0 if they are
			 *         equal, and > 0 if lhs is greater than rhs,比较数据大小时,这里比的是时间
			 */
			@Override
			public int compare(AttachsBeen lhs, AttachsBeen rhs) {
				Date date1 = FileUtil.stringToDate(lhs.getAchsPath());
				Date date2 = FileUtil.stringToDate(rhs.getAchsPath());
				// 对日期字段进行升序，如果欲降序可采用after方法
				if (date1.before(date2)) {
					return 1;
				}
				return -1;
			}
		});

		if (videoFootageAdapter == null) {
			videoFootageAdapter = new VideoFootageAdapter(getActivity(), attachList,loginBean.getUserID());
			videoFootageAdapter.setListener(FootageVideoFragment.this);
			videoFootageAdapter.setUploadIndexListener(FootageVideoFragment.this,FootageVideoFragment.this);
			videoFootageAdapter.setEmptyListener(FootageVideoFragment.this);
			lv.setAdapter(videoFootageAdapter);
		} else {
			videoFootageAdapter.update(attachList);
			lv.setSelection(0);
		}
	}


	/**
	 * 保存视频素材信息
	 */
	public void saveVideoFootages(){
		if(IsNonEmptyUtils.isString(mUserId)){
			SharedPreferences sp=getActivity().getSharedPreferences(mUserId, Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			String json=JSON.toJSONString(attachList);

			editor.putString("videofootages", json);//根据userId进行区分
			editor.commit();
		}
	}
	//用于接收录制完成但是没有编辑的视频信息
	@Subscribe
	public void receiveRecord(ComposeInfo info){
		
	}
	/**
	 * 读取之前的保存在sharedPreference里面的素材
	 * @return
	 */
	private ArrayList<AttachsBeen> readCache(){
		if(IsNonEmptyUtils.isString(mUserId)){
			SharedPreferences sp=getActivity().getSharedPreferences(mUserId, Context.MODE_PRIVATE);
			String cache = sp.getString("videofootages", "");//根据userId进行区分
			System.out.println("cache==="+cache);
			ArrayList<AttachsBeen> list;
			List<File> files;
			if(!TextUtils.isEmpty(cache)){
				list=(ArrayList<AttachsBeen>) JSON.parseArray(cache, AttachsBeen.class);
//				files = FileOperateUtil.listFiles(Configs.BLSavePath, "");
				files = FileOperateUtil.listFiles(Config.VIDEO_STORAGE_DIR+"videos/", "");
				if(IsNonEmptyUtils.isList(list)){//表示有采集的数据
					for (int i = 0; i < list.size(); i++) {
						AttachsBeen attachsBeen = list.get(i);
						String achsPath=attachsBeen.getAchsPath();
						System.out.println("表示有采集的数据===achsPath="+achsPath);
						if(!TextUtils.isEmpty(achsPath)){
							String attchs="";
							if(achsPath.startsWith("file:///")){
								attchs=achsPath.replaceAll("file:///", "");
							}else{
								attchs=achsPath;
							}
							
							File file=new File(attchs);
							if(!file.exists()){//如果这个文件已经不存在了,就不要显示了
								list.remove(i);
								i--;
							}else{
								attachsBeen.setStatus(AttachsBeen.STATUS_NORMAL);//都置为正常状态
							}
						}
					}
					
					
					if (IsNonEmptyUtils.isList(files)) {//表示有采集的数据且直播录制的视频文件不为空那么将直播录制的视频添加到素材中
						for (File file : files) {
							AttachsBeen been = new AttachsBeen();
							been.setAchsPath(file.getAbsolutePath());
							System.out.println("有采集的数据==="+file.getAbsolutePath());
							been.setStatus(AttachsBeen.STATUS_NORMAL);
							if(isData(list, file.getAbsolutePath())){//true表示已在集合中 则不用在次添加
								
							}else{//false表示不再集合中，则添加到集合中
								if (been.getAchsPath().contains("pl-section-")) {

								} else {
                                    if(file.getAbsolutePath().endsWith(".mp4")){
                                        list.add(been);
                                    }

								}
							}
						}
					}
					return list;
				}else{//如果采集的数据为空
					if(IsNonEmptyUtils.isList(files)){//表示直播录制的视频不为空
						list=new ArrayList<AttachsBeen>();
						for (File file : files) {
							AttachsBeen been = new AttachsBeen();
						//	System.out.println("如果采集的数据为空file.getAbsolutePath()=="+file.getAbsolutePath());
							been.setAchsPath(file.getAbsolutePath());
							been.setStatus(AttachsBeen.STATUS_NORMAL);
							if (been.getAchsPath().contains("pl-section-")) {

							} else {
                                if(file.getAbsolutePath().endsWith(".mp4")){
                                    list.add(been);
                                }
							}
						}
						return list;
					}else{
						return null;
					}
				}
			}else{
//				files = FileOperateUtil.listFiles(Configs.BLSavePath, "");
				files = FileOperateUtil.listFiles(Config.VIDEO_STORAGE_DIR+"videos/", "");
				if(IsNonEmptyUtils.isList(files)){//表示直播录制的视频不为空
					list=new ArrayList<AttachsBeen>();
					for (File file : files) {
						AttachsBeen been = new AttachsBeen();
					//	System.out.println("file.getAbsolutePath()==="+file.getAbsolutePath());
						been.setAchsPath(file.getAbsolutePath());
						been.setStatus(AttachsBeen.STATUS_NORMAL);
						if (been.getAchsPath().contains("pl-section-")) {

						} else {
                            if(file.getAbsolutePath().endsWith(".mp4")){
                                list.add(been);
                            }
						}
					}
					return list;
				}else{
					return null;
				}
			}
		}
		return null;
	}
	
	/**
	 * 判断该条数据是否已在集合中
	 * @return
	 */
	private boolean isData(ArrayList<AttachsBeen> list,String absolutePath){
		if(IsNonEmptyUtils.isList(list)){
			for(int i=0;i<list.size();i++){
				if(absolutePath.equals(list.get(i).getAchsPath())){
					return true;
				}
			}
		}else{
			return false;
		}
		return false;
	}
	
	/**
	 * 小窗视频播放完毕
	 */
	@Subscribe
	public void playComplete(PlayCompleteMessage playCompleteMessage){
//		EventBus.getDefault().post(new HideClosebutton());//通知其父类隐藏关闭按钮
		EventBus.getDefault().post(new CloseMediaButton());
		resetListView();
	}
	/**
	 * 重置listview的列表的显示
	 */
	private void resetListView() {
		if(videoFootageAdapter!=null){
			int currentPlaying = videoFootageAdapter.getCurrentPlaying();
			if(currentPlaying!=-1){
				attachList.get(currentPlaying).setStatus(AttachsBeen.STATUS_NORMAL);
				videoFootageAdapter.setCurrentPlaying(-1);
				videoFootageAdapter.notifyDataSetChanged();
			}
		}
	}
	/**
	 * 获取视频的分辨率
	 * @param path
	 * @return
	 */
	private ResolutionBean getResolution(String path){
		MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
		metaRetriever.setDataSource(path);
		String height = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
		String width = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
		return new ResolutionBean(width, height);
	}
	/**
	 * 获取文件拓展名
	 * @param path
	 * @return
	 */
	private String getFormat(String path){
		int i = path.lastIndexOf('.');
		return path.substring(i+1);
	}
	/**
	 * 开始合并的操作
	 * @param attachList2 
	 * @param resolution 分辨率
	 */
	private void startMerge(ArrayList<AttachsBeen> attachList2, ResolutionBean resolution) {
		if(progressDialog==null){
			progressDialog=DialogHelp.getWaitDialog(getActivity(), "视频生成中...");
		}
		 progressDialog.show();
//		progressBar1.setVisibility(View.VISIBLE);
		 
//		 registerReceiver();
		 startComposeService(attachList2,resolution);
		 
	}
	/**
	 * 开启服务合并视频
	 * @param attachList2 
	 * @param resolution 分辨率
	 */
	private void startComposeService(ArrayList<AttachsBeen> attachList2, ResolutionBean resolution){
//		Intent intent=new Intent(getActivity(),MergeService.class);
//		MergeBean bean=new MergeBean();
//		bean.setList(attachList2);
//		intent.putExtra("mergeVideoName", mergeVideoName);
//		intent.putExtra("bean", bean);
//		intent.putExtra("resolution", resolution);
//		getActivity().startService(intent);
//		handler.sendEmptyMessage(0);//发送消息，开始不断的检查shareedPreference
		
	}
	/**
	 * 注册视频合并完成的广播接收者
	 */
	/*private void registerReceiver(){
		if(!isRegistered){
			IntentFilter filter=new  IntentFilter();
			filter.addAction(ACTION);
			
			getActivity().registerReceiver(new MergeCompleteReceiver(), filter);
			isRegistered=true;
		}
	}*/
	/**
	 * 合并完成的广播的接收者
	 * @author howie
	 *
	 */
	class MergeCompleteReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			progressDialog.dismiss();
			String path = intent.getStringExtra("path");
//			Toast.makeText(getActivity(), "最终文件的路径"+path, 0).show();
//			progressDialog.dismiss();
			listPath=path;
			if(TextUtils.isEmpty(listPath)){
				return;
			}
			listValue= listPath.substring(listPath.lastIndexOf("/") + 1);
			refreshListView();  
			saveVideoFootages();
		}

	
	};
	/**接收合成的视频的路径*/
	@Subscribe(threadMode=ThreadMode.MAIN)//这里必须设置为main,否则会和IntentService处于同一子线程
	public void receivePath(VideoPath message){
//		String path = intent.getStringExtra("path");
		progressDialog.dismiss();
		
		listPath= message.getPath();
		if(TextUtils.isEmpty(listPath)){
			return;
		}
		listValue= listPath.substring(listPath.lastIndexOf("/") + 1);
		refreshListView();  
		saveVideoFootages();
	}
	@Override
	public void edit(int position) {
		editingIndex=position;
		
		
	}

    @Override
    public void directEdit(int position, String path) {
		rdSdkUtils.initEditorUIAndExportConfig();
		rdSdkUtils.initCameraConfig(SQUARE_SCREEN_CAN_CHANGE);

		ArrayList<String> list = new ArrayList<String>();
		//list.add(rdSdkUtils.EDIT_PICTURE_PATH);
		list.add(path);
		try {
			editMedia(getActivity(), list, rdSdkUtils.EDIT_REQUEST_CODE);
		} catch (InvalidArgumentException e) {
			e.printStackTrace();
		}
    }

    /**
	 * 显示是否进行删除的提示
	 */
	private void showTip(){
		if (alertDialog == null) {
			alertDialog = new AlertDialog.Builder(getActivity()).create();
			 OnKeyListener keylistener = new OnKeyListener() {
		        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {  
		            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {  
		                return true;  
		            } else {  
		                return false;  
		            }  
		        }  
		    }; 
		    alertDialog.setOnKeyListener(keylistener);//保证按返回键的时候alertDialog也不会消失
			

		}
		alertDialog.show();
		View tipView = View.inflate(getActivity(), R.layout.edit_alert_layout, null);
		TextView tv_tip=(TextView) tipView.findViewById(R.id.tv_tip);
		tv_tip.setText("确认要删除选中的素材吗？");
		View tv_yes = (TextView) tipView.findViewById(R.id.tv_yes);
		//不再提醒
		tv_yes.setOnClickListener(this);
		tipView.findViewById(R.id.tv_no).setOnClickListener(this);
		
		alertDialog.setContentView(tipView);

	}
	@Override
	public void empty() {
		tv_empty.setVisibility(View.GONE);
	}
	@Subscribe
	public void receiveDismiss(DismissMessage dismissMessage){
		if(waitDialog!=null&&waitDialog.isShowing()){
			waitDialog.dismiss();
		}
	}
	
	
	@Override
	public void uploadIndex(int index) {
		if(loginBean!=null){
			uploadPath=attachList.get(index).getAchsPath();
//			uploadList = new ArrayList<String>();
//			uploadList.add(attachList.get(index).getAchsPath());
			handler.sendEmptyMessage(1);//发送消息进行上传
		}
		
		
	}
	/**
	 * 列表是否处于编辑状态
	 * @return
	 */
	public boolean isEditing(){
		if(videoFootageAdapter!=null){
			return videoFootageAdapter.isEditing();
		}
		return false;
		
		
	}
	/**
	 * 设置为非编辑状态
	 */
	public void setNotEditing() {
		videoFootageAdapter.setEditing(false);
		if(attachList!=null&&!attachList.isEmpty()){
			for (AttachsBeen bean : attachList) {
				bean.setStatus(AttachsBeen.STATUS_NORMAL);
			}
		}
		videoFootageAdapter.notifyDataSetChanged();
		
	}
	/**
	 * 接收合并的视频的名字
	 */
	@Subscribe
	public void receiveMergeVideoName(MergeVideoNameMessage message){
		mergeVideoName = message.getVideoName();
		startMerge(mergeList,resolution);
	}
	@Override
	public int getSelectedCount() {
		return super.getSelectedCount();
	}
	
	
	
	private String finalPath;//合并生成的路径
	private String mMessage=null;
	
	/**
	 * 视频合并线程
	 * @author TCX
	 *
	 */
	private void MergeVideos (ArrayList<String> mergePaths) {
		
		if(progressDialog==null){
			progressDialog = ProgressDialog.show(getActivity(),
					"合并中...", "请稍等...", true);
		}else{
			progressDialog.show();
		}
		String workingPath=getRecordFileFolder()+"";
		File storagePath = new File(workingPath);             
		storagePath.mkdirs();  
		File myMovie = new File(storagePath, String.format("new-%s.mp4", System.currentTimeMillis()+"")); 
		finalPath=myMovie.getAbsolutePath();
		VideoStitchingRequest videoStitchingRequest = new VideoStitchingRequest.Builder()
		.inputVideoFilePath(mergePaths)
		.outputPath(finalPath).build();
//		FfmpegManager manager = FfmpegManager.getInstance();
//		manager.stitchVideos(getActivity(), videoStitchingRequest,
//		new CompletionListener() {
//			@Override
//			public void onProcessCompleted(String message,List<String> merger) {
//				if(message!=null){
//					mMessage=null;
//					handler.sendEmptyMessage(3);
//				}else{
//					handler.sendEmptyMessage(4);
//				}
//			}
//		});
	}
	
	
	 private static final String fileFolder = Environment.getExternalStorageDirectory().getAbsolutePath()+"/jwzt_recorder";
	 private String getRecordFileFolder() {
	        File file = new File(fileFolder);
	        if (!file.exists()) {
	            file.mkdir();
	        }
	        return fileFolder;
	    }
	@Override
	public void setPlayVideo(String videoPath, int position) {
		// TODO Auto-generated method stub
//		if(playVideopopu==null){
//			playVideopopu=PhotoChangeUtil.getPlayVideoPopupWindow(getActivity(), rl, videoPath);
//		}
		if(xinghao.contains("MI")){
			EventBus.getDefault().post(new CloseMediaButton());//发送播放完毕的通知
			MyWindowManager.createSmallWindow(getActivity(),videoPath,position);
		}else{
			MyWindowManager.createSmallWindow(getActivity(),videoPath,position);
		}
	}



}
