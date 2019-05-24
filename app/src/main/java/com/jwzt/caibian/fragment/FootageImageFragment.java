package com.jwzt.caibian.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.activity.CameraAty;
import com.jwzt.caibian.activity.NewScriptActivity;
import com.jwzt.caibian.activity.PhotoPickerActivity;
import com.jwzt.caibian.adapter.ImageFootageAdapter;
import com.jwzt.caibian.adapter.ImageFootageAdapter.ImageEditListener;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.bean.AttachsBeen;
import com.jwzt.caibian.bean.EditPicMessage;
import com.jwzt.caibian.bean.LoginBean;
import com.jwzt.caibian.bean.MessageEvent;
import com.jwzt.caibian.bean.NewMessage;
import com.jwzt.caibian.bean.PickMessage;
import com.jwzt.caibian.bean.ResetMessage;
import com.jwzt.caibian.bean.ShowPlusMessage;
import com.jwzt.caibian.bean.StringMessage;
import com.jwzt.caibian.bean.StringMessageImage;
import com.jwzt.caibian.interfaces.DeleteListener;
import com.jwzt.caibian.interfaces.EmptyListener;
import com.jwzt.caibian.interfaces.UploadIndexListener;
import com.jwzt.caibian.util.BitmapUtils;
import com.jwzt.caibian.util.FileOperateUtil;
import com.jwzt.caibian.util.GETImageUntils;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.xiangce.Bimp;


/**
 * 素材管理中图片素材对应的fragment
 * 
 * @author howie
 * 
 */
@SuppressLint("HandlerLeak") public class FootageImageFragment extends BaseFootageFragment implements OnClickListener,EmptyListener,UploadIndexListener,DeleteListener,ImageEditListener{
	private int imageEditIndex=-1;
	private AlertDialog alertDialog;
	/**暂无素材*/
	private RelativeLayout rl_imgzw;
	private View tv_delete;
	private ArrayList<AttachsBeen> attachList;
	private String  listPath;
	private static final int PHOTOALBUM = 1;
	private static final int PHOTOHRAPH = 2;
	private static final int MULTIPHOTO = 3;
	private boolean isRegistered;
	private ImageFootageAdapter imageFootageAdapter;
	private ListView lv;
	private View view;
	
	private String uploadPath;//在列表中点击上传按钮后保存的要上传的资源地址
	private String userId;
	
	private CbApplication app;
	private LoginBean loginBean;
	
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				Intent intentUpload=new Intent(getActivity(), NewScriptActivity.class);
				intentUpload.putExtra("tag", "resources");
				intentUpload.putExtra("resourcespath", uploadPath);
				getActivity().startActivity(intentUpload);
				break;
			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		if (!isRegistered) {
			EventBus.getDefault().register(FootageImageFragment.this);
			isRegistered = true;
		}
		view = View.inflate(getActivity(), R.layout.fragment_image_footage_layout,null);
		app=(CbApplication) getActivity().getApplication();
		loginBean=app.getmLoginBean();
		userId=loginBean.getUserID();
		
		findView();
		
		return view;
	}
	
	
	
	private void findView(){
		tv_delete=view.findViewById(R.id.tv_delete);
		tv_delete.setOnClickListener(this);
		lv = (ListView) view.findViewById(R.id.lv);
		rl_imgzw=(RelativeLayout) view.findViewById(R.id.rl_imgzw);
	}
	
	@SuppressLint("ShowToast") 
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void receiver(MessageEvent bean) {
		if (bean.getWhich() == 2) {
			if(imageFootageAdapter!=null){
				boolean editing = imageFootageAdapter.isEditing();
				if (editing) {
					tv_delete.setVisibility(View.GONE);  
					EventBus.getDefault().post(new ShowPlusMessage(true));
					for (AttachsBeen attachBean : attachList) {
						attachBean.setStatus(AttachsBeen.STATUS_NORMAL);
					}
				} else {
					EventBus.getDefault().post(new ShowPlusMessage(false));
					tv_delete.setVisibility(View.VISIBLE);
					for (AttachsBeen attachBean : attachList) {
						attachBean.setStatus(AttachsBeen.STATUS_UNSELECTED);
					}
				}
				imageFootageAdapter.setEditing(!editing);
				imageFootageAdapter.notifyDataSetChanged();
			}
		}

	}
	
	/**
	 * 开始从本地拾取图片
	 */
	@Subscribe
	public void startPick(PickMessage message) {
		if(message.getType()==PickMessage.TYPE_IMAGE){
			/*Uri localUri = Uri.fromFile(new File(""));
			Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,localUri);
			getActivity().sendBroadcast(localIntent);
			Intent intent_album = new Intent(Intent.ACTION_GET_CONTENT);
			intent_album.setType("image/*");
			startActivityForResult(intent_album, PHOTOALBUM);*/
			Intent intent_select = new Intent(getActivity(), PhotoPickerActivity.class);
			intent_select.putExtra(PhotoPickerActivity.EXTRA_SHOW_CAMERA, false);
			intent_select.putExtra(PhotoPickerActivity.EXTRA_SELECT_MODE, 1);
			intent_select.putExtra(PhotoPickerActivity.ALL_TYPE, PhotoPickerActivity.ALL_PHOTO);
			intent_select.putExtra(PhotoPickerActivity.EXTRA_MAX_MUN, 100);
	        startActivityForResult(intent_select, MULTIPHOTO);
//			Intent intent=new Intent(getActivity(),PhotoPickerActivity.class);
//			startActivityForResult(intent, MULTIPHOTO);
			
		}
	}
	
	@Subscribe
	public void makeNew(NewMessage message){
		if(message.getType()==NewMessage.TYPE_IMAGE){
			//开始录制视频
			Intent intent=new Intent(getActivity(),CameraAty.class);
			startActivityForResult(intent, PHOTOHRAPH);
		}
	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(FootageImageFragment.this);
		isRegistered = false;
		super.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//System.out.println("requestCode====="+requestCode);
		// 选择相片或视频
		String picName = "";
		if (requestCode == PHOTOALBUM) {
			if (data != null) {
				String path = GETImageUntils.getPath(getActivity(),data.getData());
				if (path != null) {
					picName = path.substring(path.lastIndexOf("/") + 1);
					listPath = path;
				}
			}
			String path1 = FileOperateUtil.getFolderPath(getActivity(), FileOperateUtil.TYPE_THUMBNAIL, "test")+"/"+picName;
			String path2 = FileOperateUtil.getFolderPath(getActivity(), FileOperateUtil.TYPE_IMAGE, "test")+"/"+picName;
			BitmapUtils.transferPicFile(listPath, path1, true);
			BitmapUtils.transferPicFile(listPath, path2, false);
		}else if(requestCode==MULTIPHOTO){//同时选择多张图片
			if(IsNonEmptyUtils.isList(Bimp.tempSelectBitmap)){
				ArrayList<String> paths=new ArrayList<String>();
				for(int i=0;i<Bimp.tempSelectBitmap.size();i++){
					paths.add(Bimp.tempSelectBitmap.get(i).getFilepath());
				}
				doRefresh(paths);
			}
//			if(data!=null){
//				ArrayList<String> paths = data.getStringArrayListExtra("paths");
//				doRefresh(paths);
//			}
		}else if(requestCode==PHOTOHRAPH){
			if(data!=null){
			/*	Uri uri = data.getData();
				String path = uri.getPath();
				if(attachList==null){
					attachList=new ArrayList<AttachsBeen>();
					
				}
				AttachsBeen bean=new AttachsBeen();
				bean.setAchsPath(path);
				attachList.add(0,bean);
				refresh();
				tv_empty.setVisibility(View.INVISIBLE);//隐藏暂无素材
			
*/			
				ArrayList<String> paths = data.getStringArrayListExtra("picPathList");
				doRefresh(paths);
				}
		}
		saveVideoFootages();
	}

	@Subscribe (threadMode=ThreadMode.POSTING)
	public void saveMessage(StringMessageImage stringMessage){
		String savepath=stringMessage.getMessage();
		ArrayList<String> paths=new ArrayList<String>();
		paths.add(savepath);
		doRefresh(paths);
		saveVideoFootages();
	}
	
	private void doRefresh(ArrayList<String> paths) {
		if(paths!=null&&!paths.isEmpty()){
			/*for (String path : paths) {
				String imageName=path.substring(path.lastIndexOf("/") + 1);
				String path1 = FileOperateUtil.getFolderPath(getActivity(), FileOperateUtil.TYPE_THUMBNAIL, "test")+"/"+imageName;
				String path2 = FileOperateUtil.getFolderPath(getActivity(), FileOperateUtil.TYPE_IMAGE, "test")+"/"+imageName;
				BitmapUtils.transferPicFile(path, path1, true);
				BitmapUtils.transferPicFile(path, path2, false);
			}*/
			rl_imgzw.setVisibility(View.GONE);//隐藏暂无素材
			if(attachList==null){
				attachList=new ArrayList<AttachsBeen>();
				
			}
			for (String path : paths) {
				AttachsBeen bean=new AttachsBeen();
				bean.setAchsPath(path);
				bean.setAchsPicurl(path);
				attachList.add(0,bean);
			}
			refresh();
		
		}
	}

	private void refresh() {
		if(imageFootageAdapter==null){
			imageFootageAdapter=new ImageFootageAdapter(getActivity(), attachList);
			imageFootageAdapter.setUploadIndexListener(FootageImageFragment.this);
			imageFootageAdapter.setDeleteListener(FootageImageFragment.this);
			lv.setAdapter(imageFootageAdapter);
		}else{
			imageFootageAdapter.update(attachList);
//					lv.setSelection(attachList.size()-1);
		}
	}

	
	@Override
	public void onResume() {
		super.onResume();
		loadAlbum();
		app=(CbApplication) getActivity().getApplication();
		loginBean=app.getmLoginBean();
		userId=loginBean.getUserID();
		attachList=readCache();
		if(attachList!=null&&!attachList.isEmpty()){
			rl_imgzw.setVisibility(View.GONE);//隐藏暂无素材
			//if(imageFootageAdapter!=null){
				imageFootageAdapter=new ImageFootageAdapter(getActivity(), attachList);
				imageFootageAdapter.setUploadIndexListener(FootageImageFragment.this);
				imageFootageAdapter.setDeleteListener(FootageImageFragment.this);
				imageFootageAdapter.setImageEditListener(FootageImageFragment.this);
				lv.setAdapter(imageFootageAdapter);
			//}
		}else{
			rl_imgzw.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * 加载本地拍摄的图片并在列表上适配
	 */
	public void loadAlbum(){
		//获取根目录下缩略图文件夹
		String thumbFolder=FileOperateUtil.getFolderPath(getActivity(), FileOperateUtil.TYPE_THUMBNAIL, "test");
		
		String dstFolder=FileOperateUtil.getFolderPath(getActivity(), FileOperateUtil.TYPE_IMAGE, "test");
		System.out.println("dstFolder===="+dstFolder);
		System.out.println("thumbFolder===="+thumbFolder);
		List<File> files=FileOperateUtil.listFiles(thumbFolder, ".jpg");
		List<File> dstFiles=FileOperateUtil.listFiles(dstFolder, ".jpg");
		
		
		if(dstFiles!=null&&dstFiles.size()>0){
			rl_imgzw.setVisibility(View.GONE);
			lv.setVisibility(View.VISIBLE);
			rl_imgzw.setVisibility(View.GONE);//隐藏暂无素材
			attachList=new ArrayList<AttachsBeen>();
			for (int i=0;i<dstFiles.size();i++) {
				AttachsBeen been = new AttachsBeen();
				been.setAchsPath(files.get(i).getAbsolutePath());//缩略图地址
				been.setAchsPicurl(dstFiles.get(i).getAbsolutePath());//源图片地址
				attachList.add(been);
			}
			
			if(imageFootageAdapter==null){
				imageFootageAdapter=new ImageFootageAdapter(getActivity(), attachList);
				imageFootageAdapter.setUploadIndexListener(FootageImageFragment.this);
				lv.setAdapter(imageFootageAdapter);
			}else{
				imageFootageAdapter.update(attachList);
//				lv.setSelection(attachList.size()-1);
			}
		}else{
			rl_imgzw.setVisibility(View.VISIBLE);
			lv.setVisibility(View.GONE);
		}
		
		saveVideoFootages();
	}
	
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.tv_yes:
			alertDialog.dismiss();
			if(attachList!=null&&!attachList.isEmpty()){
				for (int i = 0; i < attachList.size(); i++) {
					AttachsBeen attachsBeen = attachList.get(i);
					if(attachsBeen.getStatus()==AttachsBeen.STATUS_SELECTED){
						//需要删除文件夹中的源文件
						String achsPicurl = attachList.get(i).getAchsPicurl();//原图
						String achsPath = attachList.get(i).getAchsPath();//缩略图
						File file1=new File(achsPath);
						File file2=new File(achsPicurl);
						if(file1.exists()&&file2.exists()){
							file1.delete();
							file2.delete();
						}
						attachList.remove(i);
						i--;  
					}  
				}
				
				saveVideoFootages();
			}
			if(attachList!=null&&attachList.isEmpty()){//如果清空了数据
				if(imageFootageAdapter!=null){
					imageFootageAdapter.setEditing(false);//
					rl_imgzw.setVisibility(View.VISIBLE);//显示暂无素材
					tv_delete.setVisibility(View.GONE);
					EventBus.getDefault().post(new ShowPlusMessage(true));//使加号按钮可见
				}
			}
			
			imageFootageAdapter.notifyDataSetChanged();
			break;
		case R.id.tv_no:
			alertDialog.dismiss();
			break;
		case R.id.tv_delete:
			
			if(attachList!=null&&!attachList.isEmpty()){
				for (int i = 0; i < attachList.size(); i++) {
					AttachsBeen attachsBeen = attachList.get(i);
					if(attachsBeen.getStatus()==AttachsBeen.STATUS_SELECTED){//只有选中了视频素材的时候才弹出对话框
						showTip();
						break;
					}
				}
			}
			
			
//			for (int i = 0; i < attachList.size(); i++) {
//				AttachsBeen attachsBeen = attachList.get(i);
//				if(attachsBeen.getStatus()==AttachsBeen.STATUS_SELECTED){
//					attachList.remove(i);
//					i--;
//				}
//			}
//			imageFootageAdapter.notifyDataSetChanged();
			break;
		}
	}
	public static String getUUID() {
		String s = UUID.randomUUID().toString();
		return s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18)+ s.substring(19, 23) + s.substring(24);
	}
	
	public String getFolderPath() {
		String path = Environment.getExternalStorageDirectory()+ "/Media/Photo/";
		File filePath = new File(path);
		if (!filePath.exists()) {
			filePath.mkdirs();
		}
		return filePath.getPath();
	}

	@Override
	public void empty() {
		rl_imgzw.setVisibility(View.VISIBLE);//显示暂无素材
	}
	/**
	 * 显示是否保存编辑的提示
	 */
	private void showTip(){
		if (alertDialog == null) {
			alertDialog = new AlertDialog.Builder(getActivity())
					.create();
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
			/*alertDialog.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface arg0) {
					
				}
			});*/
			

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
	public void uploadIndex(int index) {
		if(loginBean!=null){
			uploadPath=attachList.get(index).getAchsPath();
//			uploadList = new ArrayList<String>();
//			uploadList.add(attachList.get(index).getAchsPath());
			mHandler.sendEmptyMessage(1);//发送消息进行上传
		}
//		if(index!=-1){
//			CbApplication app = (CbApplication) getActivity().getApplication();
//			LoginBean loginBean = app.getmLoginBean();
//			if(loginBean!=null){
//				String userID = loginBean.getUserID();
//				ArrayList<String> list=new ArrayList<String>();
//				list.add(attachList.get(index).getAchsPath());
//				toUpImg(userID, list);
//				if(progressDialog==null){
//					progressDialog=DialogHelp.getWaitDialog(getActivity(), "开始准备上传");
//				}
//				progressDialog.show();
////				Toast.makeText(getActivity(), "开始准备上传", 0).show();
//				
//			}
//		}
	}
	
	/**
	 * 列表是否处于编辑状态
	 * @return
	 */
	public boolean isEditing(){
		if(imageFootageAdapter!=null){
			return imageFootageAdapter.isEditing();
		}
		return false;
		
	}
	// 设置为非编辑状态
	public void setNotEditing() {
		imageFootageAdapter.setEditing(false);
		if(attachList!=null&&!attachList.isEmpty()){
			for (AttachsBeen bean : attachList) {
				bean.setStatus(AttachsBeen.STATUS_NORMAL);
			}
		}
		imageFootageAdapter.notifyDataSetChanged();

	}
	@Subscribe(threadMode =ThreadMode.MAIN)
	public void reset(ResetMessage message){
			if(imageFootageAdapter!=null){
				imageFootageAdapter.setEditing(false);
			}
			
			if(attachList!=null&&!attachList.isEmpty()){
				for (AttachsBeen attachsBeen : attachList) {
					attachsBeen.setStatus(AttachsBeen.STATUS_NORMAL);//这里暂时都设置为正常状态，等加上播放视频的逻辑之后需要再修改一下
				}
				imageFootageAdapter.notifyDataSetChanged();
				tv_delete.setVisibility(View.GONE);
				
			}

	}
	/**
	 * 保存图片素材信息
	 */
	private void saveVideoFootages(){
		if(IsNonEmptyUtils.isString(userId)){
			SharedPreferences sp=getActivity().getSharedPreferences(userId, Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			String json=JSON.toJSONString(attachList);
			editor.putString("imagefootages_"+loginBean.getUsername(), json);//根据userId进行区分
			editor.commit();
		}
	}
	/**
	 * 读取之前的保存在sharedPreference里面的素材
	 * @return
	 */
	private ArrayList<AttachsBeen> readCache(){
		if(IsNonEmptyUtils.isString(userId)){
			SharedPreferences sp=getActivity().getSharedPreferences(userId, Context.MODE_PRIVATE);
			String cache = sp.getString("imagefootages_"+loginBean.getUsername(), "");//根据userId进行区分
			if(!TextUtils.isEmpty(cache)){
				ArrayList<AttachsBeen> list=(ArrayList<AttachsBeen>) JSON.parseArray(cache, AttachsBeen.class);
				if(IsNonEmptyUtils.isList(list)){
					for (int i = 0; i < list.size(); i++) {
						AttachsBeen attachsBeen = list.get(i);
						String achsPath=attachsBeen.getAchsPath();
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
					return list;
				}
			}
		}
		return null;
		
	}

	@Override
	public void delete() {
		saveVideoFootages();
	}

	@Override
	public void imageEditIndex(int index) {
		imageEditIndex=index;
	}
	/**
	 * 接收经过编辑的图片的路径
	 * @param message
	 */
	@Subscribe (threadMode = ThreadMode.MAIN)
	public void receiveEditedImage(EditPicMessage message){
		if(message!=null&&imageEditIndex!=-1&&attachList!=null&&!attachList.isEmpty()){
			String path = message.getPath();
			AttachsBeen bean=attachList.get(imageEditIndex);
			bean.setAchsPath(path);
			bean.setAchsPicurl(path);
			imageFootageAdapter.notifyDataSetChanged();
			saveVideoFootages();
		}
	}
	@Override
	public int getSelectedCount() {
		return super.getSelectedCount();
	}
}
