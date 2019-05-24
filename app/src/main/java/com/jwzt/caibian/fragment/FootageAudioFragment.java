package com.jwzt.caibian.fragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.activity.AudioActivity;
import com.jwzt.caibian.activity.AudioEditActivity;
import com.jwzt.caibian.activity.NewScriptActivity;
import com.jwzt.caibian.adapter.AudioFootageAdapter2;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.bean.AttachsBeen;
import com.jwzt.caibian.bean.LoginBean;
import com.jwzt.caibian.bean.MessageEvent;
import com.jwzt.caibian.bean.NewMessage;
import com.jwzt.caibian.bean.PickMessage;
import com.jwzt.caibian.bean.ResetMessage;
import com.jwzt.caibian.bean.ShowPlusMessage;
import com.jwzt.caibian.interfaces.AudioEditInterface;
import com.jwzt.caibian.interfaces.UploadIndexListener;
import com.jwzt.caibian.util.AudioUtils;
import com.jwzt.caibian.util.DensityUtil;
import com.jwzt.caibian.util.FileOperateUtil;
import com.jwzt.caibian.util.GETImageUntils;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.U;
import com.jwzt.caibian.widget.DragSortListView;

/**
 * 素材管理中音频素材对应的fragment
 * 
 * @author howie
 * 
 */
public class FootageAudioFragment extends BaseFootageFragment implements OnClickListener, UploadIndexListener,AudioEditInterface {
	private ProgressDialog progressDialog;
	private static final int PICK_AUDIO = 1;
	private static final int SAVE_AUDIO = 2;
	private static final int EDIT_AUDIO = 3;
	private String listPath, listValue;
	private boolean isRegistered;
	private ArrayList<AttachsBeen> attachList;
	private AudioFootageAdapter2 audioFootageAdapter;
	private List<String> mMergePaths = new ArrayList<String>();
	/** 暂无素材 */
	private View lv_empty;
	/** 合并按钮和删除按钮 */
	private View tv_merge, tv_delete;
	private DragSortListView lv;
	
	/**需要上传的条目的索引*/
	private int mUploadIndex=-1;
	private String uploadPath;//在列表中点击上传按钮后保存的要上传的资源地址
	private AlertDialog alertDialog;
	
	
	private String userId;
	private CbApplication app;
	private LoginBean loginBean;
	private String mergerPath;
	/***跳转到编辑页面时要编辑的音频文件的路径 当编辑完成后返回时将其去除*/
	private String editPath;

	private Handler mHandler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:// 取消合并进度条
				dismissLoadingDialog();
//				receiver(new MessageEvent(1));//TODO
				if (isSuccess) {
					Toast.makeText(getActivity(), "合并成功！", 0).show();
					System.out.println("====================录音返回=====================mergerPath");
					saveAudioList(mergerPath);
					
//					loadAlbum1();// 重新加载一下工作目录下的音频文件
					
					for (AttachsBeen attachBean : attachList) {
						attachBean.setStatus(AttachsBeen.STATUS_UNSELECTED);
					}
				} else {
					Toast.makeText(getActivity(), "合并失败！", 0).show();
				}
				break;
			case 2:
				Toast.makeText(getActivity(), "请选择2个以上的文件进行操作！", 0).show();
				break;
			case 3:
				Intent intentUpload=new Intent(getActivity(), NewScriptActivity.class);
				intentUpload.putExtra("tag", "resources");
				intentUpload.putExtra("resourcespath", uploadPath);
				getActivity().startActivity(intentUpload);
				break;
			default:
				break;
			}

		};

	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (!isRegistered) {
			EventBus.getDefault().register(FootageAudioFragment.this);
			isRegistered = true;
		}
		View view = View.inflate(getActivity(), R.layout.fragment_audio_footage_layout,null);
		
		app=(CbApplication) getActivity().getApplication();
		loginBean=app.getmLoginBean();
		userId=loginBean.getUserID();
		
		lv_empty = view.findViewById(R.id.rl_empty);
		lv = (DragSortListView) view.findViewById(R.id.lv);
		lv.setDropListener(onDrop);
		lv.setRemoveListener(onRemove);
		tv_delete = view.findViewById(R.id.tv_delete);
		tv_merge = view.findViewById(R.id.tv_merge);
		tv_delete.setOnClickListener(this);
		tv_merge.setOnClickListener(this);
		/*
		 * audioFootageAdapter = new AudioFootageAdapter(getActivity());
		 * lv.setAdapter(audioFootageAdapter);
		 */
		
		attachList=readCache();
		if(IsNonEmptyUtils.isList(attachList)){
            System.out.println("attachList======"+attachList.size());
			if(audioFootageAdapter==null){
				audioFootageAdapter = new AudioFootageAdapter2(getActivity(),attachList,FootageAudioFragment.this);
				audioFootageAdapter.setUploadIndexListener(FootageAudioFragment.this);
				lv.setAdapter(audioFootageAdapter);
			}
		}else{
            System.out.println("attachList=====");
			lv_empty.setVisibility(View.VISIBLE);
		}
		
		return view;
	}

	@Subscribe
	public void makeNew(NewMessage message) {
		if (message.getType() == NewMessage.TYPE_AUDIO) {// 开始去录音
			Intent intent=new Intent(getActivity(), AudioActivity.class);
			startActivityForResult(intent, SAVE_AUDIO);
//			startActivity(intent);
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void receiver(MessageEvent bean) {
		// tv.setText(bean.getName());
		if (bean.getWhich() == 1) {
			boolean editing = audioFootageAdapter.isEditing();
			if (editing) {
				tv_merge.setVisibility(View.GONE);
				tv_delete.setVisibility(View.GONE);
				for (AttachsBeen attachBean : attachList) {
					attachBean.setStatus(AttachsBeen.STATUS_NORMAL);
				}
			} else {
				tv_merge.setVisibility(View.VISIBLE);
				tv_delete.setVisibility(View.VISIBLE);
				for (AttachsBeen attachBean : attachList) {
					attachBean.setStatus(AttachsBeen.STATUS_UNSELECTED);
				}
			}
			audioFootageAdapter.setEditing(!editing);
			audioFootageAdapter.notifyDataSetChanged();
		}

	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void reset(ResetMessage message) {
		
		
		if(attachList!=null&&!attachList.isEmpty()){
			for (AttachsBeen bean : attachList) {
				bean.setStatus(AttachsBeen.STATUS_NORMAL);
			}
		}
		
		tv_delete.setVisibility(View.GONE);
		tv_merge.setVisibility(View.GONE);
		if(audioFootageAdapter!=null){
			audioFootageAdapter.setEditing(false);
			audioFootageAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(FootageAudioFragment.this);
		isRegistered = false;
		super.onDestroy();
	}

	private boolean isSuccess = false;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_delete:// 删除
//			for (int i = 0; i < attachList.size(); i++) {
//				AttachsBeen attachsBeen = attachList.get(i);
//				if (attachsBeen.getStatus() == AttachsBeen.STATUS_SELECTED) {
//					attachList.remove(i);
//					i--;
//				}
//			}
//			audioFootageAdapter.notifyDataSetChanged();
			if(attachList!=null&&!attachList.isEmpty()){
				for (int i = 0; i < attachList.size(); i++) {
					AttachsBeen attachsBeen = attachList.get(i);
					if(attachsBeen.getStatus()==AttachsBeen.STATUS_SELECTED){//只有选中了视频素材的时候才弹出对话框
						showTip();
						break;
					}
					//TODO
//					attachsBeen.getAchsPath();//文件存储的路径
				}
			}
			break;
			case R.id.tv_yes:
				alertDialog.dismiss();
				if(attachList!=null&&!attachList.isEmpty()){
					for (int i = 0; i < attachList.size(); i++) {
						AttachsBeen attachsBeen = attachList.get(i);
						if(attachsBeen.getStatus()==AttachsBeen.STATUS_SELECTED){
							String achsPath = attachList.get(i).getAchsPath();
							File file = new File(achsPath);
							if (file.exists())
								file.delete();
							attachList.remove(i);
							i--;  
						}  
					}
				}
				
				if(attachList!=null&&attachList.isEmpty()){//如果清空了数据
					if(audioFootageAdapter!=null){
						audioFootageAdapter.setEditing(false);//
//						rl_imgzw.setVisibility(View.VISIBLE);//显示暂无素材
//						tv_delete.setVisibility(View.GONE);
						EventBus.getDefault().post(new ShowPlusMessage(true));//使加号按钮可见
						lv_empty.setVisibility(View.GONE);
						}
				}else{
					lv_empty.setVisibility(View.VISIBLE);
				}
				
				audioFootageAdapter.notifyDataSetChanged();
				break;
			case R.id.tv_no:
				alertDialog.dismiss();
				break;
		case R.id.tv_merge:// 合并
			mMergePaths.clear();
			ArrayList<AttachsBeen> mergeList = new ArrayList<AttachsBeen>();
			for (int i = 0; i < attachList.size(); i++) {
				AttachsBeen attachsBeen = attachList.get(i);
				if (attachsBeen.getStatus() == AttachsBeen.STATUS_SELECTED) {
					mergeList.add(attachsBeen);
					if (attachsBeen.getAchsPath().endsWith(".wav")) {
						mMergePaths.add(attachsBeen.getAchsPath());
					} else {
						Toast.makeText(getActivity(), "暂不支持其他音频格式合并！", 0)
								.show();
						return ;
					}
				}
			}
			
			if (mMergePaths.size() >= 2) {
				initLoadingDialog("合并中！");
			}
			
			// 在这里进行合并的操作
			new Thread(new Runnable() {
				@Override
				public void run() {
					if (mMergePaths.size() >= 2) {
						mergerPath=U.DATA_DIRECTORY + System.currentTimeMillis() + ".wav";
						isSuccess = AudioUtils.mergeAudioFiles(mergerPath,mMergePaths);
						mHandler.sendEmptyMessage(1);
					} else {
						mHandler.sendEmptyMessage(2);
					}
				}
			}).start();
			break;
		}
	}
	
	/**
	 * 显示是否保存编辑的提示
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

	/**
	 * 加载框
	 * 
	 * @param tip
	 */
	private void initLoadingDialog(String tip) {
		if (loadingDialog == null) {
			loadingDialog = new Dialog(getActivity(), R.style.prograssBarStyle);
			View view = View.inflate(getActivity(), R.layout.dialog_loading,null);
			TextView textTip = (TextView) view.findViewById(R.id.tv_loading_tip);
			textTip.setText(tip == null ? "正在加载" : tip + "");
			loadingDialog.setContentView(view);
			loadingDialog.setCancelable(false);
			if ((!loadingDialog.isShowing())) {
				loadingDialog.show();
				WindowManager.LayoutParams params = loadingDialog.getWindow().getAttributes();
				params.width = (int) DensityUtil.dip2px(120);
				params.height = (int) DensityUtil.dip2px(90);
				loadingDialog.getWindow().setAttributes(params);
			}
		}
	}

	/**
	 * 取消加载对话框
	 */
	private void dismissLoadingDialog() {
		if (loadingDialog != null) {
			loadingDialog.dismiss();
			loadingDialog = null;
		}
	}

	/**
	 * 开始从本地拾取视频
	 */
	@Subscribe
	public void startPick(PickMessage message) {
		if (message.getType() == PickMessage.TYPE_AUDIO) {
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("audio/*");
			startActivityForResult(intent, PICK_AUDIO);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PICK_AUDIO && data != null) {
			if (attachList == null) {
				attachList = new ArrayList<AttachsBeen>();
			}
			Uri uri = data.getData();
			if (uri != null) {
				String picturePath = GETImageUntils.getPath(getActivity(), uri);
				String picName = "";
				picName = picturePath.substring(picturePath.lastIndexOf("/") + 1);
				listValue = picName;
				listPath = picturePath;

				File file = new File(picturePath);
				try {
					FileInputStream in = new FileInputStream(file);
					FileOutputStream out = new FileOutputStream(U.DATA_DIRECTORY+ picturePath.substring(picturePath.lastIndexOf("/")));
					byte[] bt = new byte[1024 * 100];

					int length = 0;
					while ((length = in.read(bt)) != -1) {
						out.write(bt, 0, length);
					}
					in.close();
					out.close();

					saveAudioList(picturePath);
//					loadAlbum();// 重新加载
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else if(requestCode==SAVE_AUDIO){
			//TODO
			if(data!=null){
				String audioPath=data.getStringExtra("audioPath");
				System.out.println("====================录音返回=====================audioPath");
				saveAudioList(audioPath);
			}
		}else if(requestCode==EDIT_AUDIO){
			if(data!=null){
				String audioPath=data.getStringExtra("audioEditPath");
				System.out.println("====================编辑返回====================="+audioPath);
				saveAudioList(audioPath);
			}
		}
	}

	/**
	 * 保存SharedPreferences并适配
	 * @param audioPath
	 */
	private void saveAudioList(String audioPath){
		if(IsNonEmptyUtils.isList(attachList)){
			AttachsBeen bean=new AttachsBeen();
			bean.setAchsPath(audioPath);
			attachList.add(0,bean);
		}else{
			attachList=new ArrayList<AttachsBeen>();
			AttachsBeen bean=new AttachsBeen();
			bean.setAchsPath(audioPath);
			attachList.add(0,bean);
		}
		
		
		if(IsNonEmptyUtils.isList(attachList)){
			for (int i = 0; i < attachList.size(); i++) {
				AttachsBeen attachsBeen = attachList.get(i);
				if(!TextUtils.isEmpty(attachsBeen.getAchsPath())){
					File file=new File(attachsBeen.getAchsPath());
					if(!file.exists()){//如果这个文件已经不存在了,就不要显示了
						attachList.remove(i);
					}else{
						attachsBeen.setStatus(AttachsBeen.STATUS_NORMAL);//都置为正常状态
					}
				}
			}
			//if(audioFootageAdapter==null){
				audioFootageAdapter = new AudioFootageAdapter2(getActivity(),attachList,FootageAudioFragment.this);
				audioFootageAdapter.setUploadIndexListener(FootageAudioFragment.this);
				lv.setAdapter(audioFootageAdapter);
//			}else{
//				audioFootageAdapter.setList(attachList);
//			}
		}
		
		saveAudioFootages();
	}
	
	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			AttachsBeen item = audioFootageAdapter.getItem(from);
			audioFootageAdapter.remove(item);
			audioFootageAdapter.insert(item, to);
		}
	};

	private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
		@Override
		public void remove(int which) {
			audioFootageAdapter.remove(audioFootageAdapter.getItem(which));
		}
	};
	private Dialog loadingDialog;

	@SuppressLint("NewApi")
	@Override
	public void onResume() {
		super.onResume();
//		lv.scrollListBy(0);
		app=(CbApplication) getActivity().getApplication();
		loginBean=app.getmLoginBean();
		userId=loginBean.getUserID();
		
		loadAlbum();
	}

	/**
	 * 加载本地音频文件并在列表上适配
	 */
	public void loadAlbum() {
		// 获取根目录下缩略图文件夹
		List<File> files = FileOperateUtil.listFiles(U.DATA_DIRECTORY, "");
		if (files != null && files.size() > 0) {
			lv_empty.setVisibility(View.GONE);
			attachList = new ArrayList<AttachsBeen>();
			for (File file : files) {
				AttachsBeen been = new AttachsBeen();
				been.setAchsPath(file.getAbsolutePath());
				attachList.add(been);
			}

//			if (audioFootageAdapter == null) {
//				audioFootageAdapter = new AudioFootageAdapter2(getActivity(),attachList,FootageAudioFragment.this);
//				audioFootageAdapter.setUploadIndexListener(FootageAudioFragment.this);
//				lv.setAdapter(audioFootageAdapter);
//			} else {

			System.out.println("==========");
				audioFootageAdapter = new AudioFootageAdapter2(getActivity(),attachList,FootageAudioFragment.this);
				audioFootageAdapter.setUploadIndexListener(FootageAudioFragment.this);
				lv.setAdapter(audioFootageAdapter);
			audioFootageAdapter.notifyDataSetChanged();
			//	lv.setSelection(attachList.size() - 1);
			//}
		}
	}
	
	/**
	 * 读取之前的保存在sharedPreference里面的素材
	 * @return
	 */
	private ArrayList<AttachsBeen> readCache(){
		if(IsNonEmptyUtils.isString(userId)){
			SharedPreferences sp=getActivity().getSharedPreferences(userId, Context.MODE_PRIVATE);
			String cache = sp.getString("audiofootages_"+loginBean.getUsername(), "");//根据userId进行区分
			if(!TextUtils.isEmpty(cache)){
				ArrayList<AttachsBeen> list=(ArrayList<AttachsBeen>) JSON.parseArray(cache, AttachsBeen.class);
				if(IsNonEmptyUtils.isList(list)){
					for (int i = 0; i < list.size(); i++) {
						AttachsBeen attachsBeen = list.get(i);
						if(!TextUtils.isEmpty(attachsBeen.getAchsPath())){
							File file=new File(attachsBeen.getAchsPath());
							if(!file.exists()){//如果这个文件已经不存在了,就不要显示了
								list.remove(i);
								i--;
							}else{
								attachsBeen.setStatus(AttachsBeen.STATUS_NORMAL);//都置为正常状态
							}
						}
					}
					return list;
				}
			}
		}
		return null;
		
	}
	
	/**
	 * 保存音频数据
	 */
	private void saveAudioFootages(){
		if(IsNonEmptyUtils.isString(userId)){
			SharedPreferences sp=getActivity().getSharedPreferences(userId, Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			String json=JSON.toJSONString(attachList);
			editor.putString("audiofootages_"+loginBean.getUsername(), json);//根据userId进行区分
			editor.commit();
		}
	}
	
	
	/**
	 * 加载本地音频文件并在列表上适配
	 */
	public void loadAlbum1() {
		// 获取根目录下缩略图文件夹
		List<File> files = FileOperateUtil.listFiles(U.DATA_DIRECTORY, "");
		// List<File> files=FileOperateUtil.listFiles(U.DATA_DIRECTORY, ".wav");
		if (files != null && files.size() > 0) {
			lv_empty.setVisibility(View.GONE);
			attachList = new ArrayList<AttachsBeen>();
			for (File file : files) {
				AttachsBeen been = new AttachsBeen();
				been.setAchsPath(file.getAbsolutePath());
				been.setStatus(AttachsBeen.STATUS_UNSELECTED);
				attachList.add(been);
			}

		//	if (audioFootageAdapter == null) {
				audioFootageAdapter = new AudioFootageAdapter2(getActivity(),attachList,FootageAudioFragment.this);
				audioFootageAdapter.setUploadIndexListener(FootageAudioFragment.this);
				lv.setAdapter(audioFootageAdapter);
//			} else {
//				audioFootageAdapter.update(attachList);
//				lv.setSelection(attachList.size() - 1);
//			}
		}
	}

	@Override
	public void uploadIndex(int index) {
		CbApplication app = (CbApplication) getActivity().getApplication();
		LoginBean loginBean = app.getmLoginBean();
		if(loginBean!=null){
			mUploadIndex=index;
			uploadPath=attachList.get(index).getAchsPath();
//			uploadList = new ArrayList<String>();
//			uploadList.add(attachList.get(index).getAchsPath());
			mHandler.sendEmptyMessage(3);//发送消息进行上传
		}
		
//		if (index != -1) {
//			CbApplication app = (CbApplication) getActivity().getApplication();
//			LoginBean loginBean = app.getmLoginBean();
//			if (loginBean != null) {
//				String userID = loginBean.getUserID();
//				ArrayList<String> list = new ArrayList<String>();
//				list.add(attachList.get(index).getAchsPath());
//				toUpImg(userID, list);
//				if (progressDialog == null) {
//					progressDialog = DialogHelp.getWaitDialog(getActivity(),"开始准备上传");
//				}
//				progressDialog.show();
//				// Toast.makeText(getActivity(), "开始准备上传", 0).show();
//			}
//		}

	}


	/**
	 * 列表是否处于编辑状态
	 * 
	 * @return
	 */
	public boolean isEditing() {
		if (audioFootageAdapter != null) {
			return audioFootageAdapter.isEditing();
		}

		return false;

	}

	// 设置为非编辑状态
	public void setNotEditing() {
		// TODO Auto-generated method stub
		audioFootageAdapter.setEditing(false);
		if(attachList!=null&&!attachList.isEmpty()){
			for (AttachsBeen bean : attachList) {
				bean.setStatus(AttachsBeen.STATUS_NORMAL);
			}
		}
		audioFootageAdapter.notifyDataSetChanged();

	}
//	@Override
//	public int getSelectedCount() {
//		return super.getSelectedCount();
//	}

	@Override
	public void setAudioEdit(String achsPath) {
		// TODO Auto-generated method stub
		if(IsNonEmptyUtils.isString(achsPath)){
			if(achsPath.endsWith(".wav")){
				Intent it = new Intent(getActivity(), AudioEditActivity.class);
				String filePath = achsPath;
				editPath=filePath;
				it.setData(Uri.parse(filePath));
				startActivityForResult(it, EDIT_AUDIO);
			}else{
				Toast.makeText(getActivity(), "暂不支持编辑其他格式音频!", 0).show();
			}
		}
	}

}
