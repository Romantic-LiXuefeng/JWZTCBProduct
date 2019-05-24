package com.jwzt.caibian.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.adapter.BackTransferAdapter;
import com.jwzt.caibian.application.BaseActivity;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.application.Configs;
import com.jwzt.caibian.bean.LoginBean;
import com.jwzt.caibian.bean.ManuscriptBean;
import com.jwzt.caibian.bean.TypeBean;
import com.jwzt.caibian.interfaces.NetWorkContinueInterface;
import com.jwzt.caibian.util.FileUtil;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.UserToast;
import com.jwzt.caibian.xiangce.Bimp;
import com.jwzt.caibian.xiangce.ItemImage;
import com.jwzt.upload.main.TaskInfo;
import com.jwzt.upload.main.UploadInfoInterface;
import com.jwzt.upload.utils.MainService;
import com.jwzt.upload.utils.UploadUtils;
/**
 * 回传管理
 * @author howie
 *
 */
public class BackTransferActivity extends BaseActivity implements OnClickListener,UploadInfoInterface,NetWorkContinueInterface  {
	private TextView tv_titles;
	/**右上角的加号*/
	private ImageView iv_plus;
	private ListView lv;
	private View iv_back;
	/**“暂无回传任务”对应的textview*/
	private TextView tv_empty;
	/**暂无回传任务对应的图片*/
	private ImageView iv_empty;
	private UploadUtils mUploadUtils;
	private String title,content;
	private TypeBean mTypeBean;
	private String mInfoType="1";
	private CbApplication application;
	private LoginBean mLoginBean;
	private BackTransferAdapter mAdapter;
	private List<TaskInfo> mList;
	private ArrayList<ItemImage> selectImage;
	/**
	 * 标识来自哪个页面 
	 * NewScriptSend表示来自快速发稿页面的发送按钮
	 * NoUpSend表示来自未上传详情页面的上传
	 * backmanager表示来自回传管理按钮
	 * taskBack//表示任务中的记者回传上传
	 */
	private String tag;
	
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				initView();
				break;
			case 3:
				mAdapter.notifyDataSetChanged();
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_back_transfer);
		
		tag=getIntent().getStringExtra("tag");
		
		application=(CbApplication) getApplication();
		mLoginBean=application.getmLoginBean();
		mList=new ArrayList<TaskInfo>();
		mUploadUtils=new UploadUtils(BackTransferActivity.this, BackTransferActivity.this);
		Configs.mContinueInterface=BackTransferActivity.this;
		
		findViews();
		if(tag.equals("NewScriptSend")){
			title=getIntent().getStringExtra("title");
			content=getIntent().getStringExtra("content");
			mTypeBean=(TypeBean) getIntent().getSerializableExtra("type");
			mInfoType=getIntent().getStringExtra("infotype");
//			selectImage=(ArrayList<ItemImage>) getIntent().getSerializableExtra("imglist");
			selectImage=Bimp.tempSelectBitmap;
			sendManuscript(false,mInfoType);
		}else if(tag.equals("NoUpSend")){
			title=getIntent().getStringExtra("title");
			content=getIntent().getStringExtra("content");
			mTypeBean=(TypeBean) getIntent().getSerializableExtra("type");
			selectImage=(ArrayList<ItemImage>) getIntent().getSerializableExtra("imglist");
			mInfoType=getIntent().getStringExtra("infotype");
			sendManuscript(true,mInfoType);
		}else if(tag.equals("backmanager")){
			List<TaskInfo> list=mUploadUtils.getAllTaskList();
			System.out.println(""+list.size());
			if(IsNonEmptyUtils.isList(list)){
				for(int i=0;i<list.size();i++){
					System.out.println("===================>>"+list.get(i).getFileName());
					if(list.get(i).getTask_statu()!=3){
						mList.add(list.get(i));
					}
				}
				mHandler.sendEmptyMessage(1);
			}
		}else if(tag.equals("taskBack")){//任务上传
			mInfoType=getIntent().getStringExtra("infotype");
			sendTask(mInfoType);
		}
	}
	
	/**
	 * 文稿上传发送
	 * @param isflag true表示来自未上传详情页面 false表示来自快速发稿页面
	 */
	private void sendManuscript(boolean isflag,String infotype){
		List<ManuscriptBean> list=new ArrayList<ManuscriptBean>();
		ArrayList<String> listpath=new ArrayList<String>();//所选本地资源路径集合
		if(IsNonEmptyUtils.isList(selectImage)){
			int size;
			if(isflag){
				size=selectImage.size();
			}else{
				size=selectImage.size()-1;//小于size-1是应为有个空的item用来显示加号
			}
			for(int i=0;i<size;i++){
				ManuscriptBean bean=new ManuscriptBean();
				String filepath=selectImage.get(i).getFilepath();
				if(filepath.startsWith("file:///")){
					String filepaths=filepath.replaceFirst("file:///", "");
					filepath=filepaths;
					listpath.add(filepath);
				}else{
					listpath.add(filepath);
				}
				String fileName=FileUtil.getFileName(filepath);
				bean.setName(fileName);
				bean.setPath(filepath);
				String type="";
				if(filepath.endsWith("jpg")||filepath.endsWith("png")){
					type=3+"";
				}else if(filepath.endsWith("mp3")){
					type=2+"";
				}else if(filepath.endsWith("mp4")){
					type=1+"";
				}else{
					type=3+"";
				}
				bean.setType(type);
				list.add(bean);
			}
			
			String typeId=mTypeBean.getId();
			String location="";
			if(IsNonEmptyUtils.isString(application.getLocation())){
				location=application.getLocation();
			}else{
				location="金隅嘉华";
			}
			//"测试","185","42",116.316278,40.043518,"上帝","测试",list
			String filepath=FileUtil.assemblyXML(title, mLoginBean.getUserID(), typeId, application.getLongitude(),application.getLatitude(), location, content, list);
	//		System.out.println("filepath=========="+filepath);
			listpath.add(filepath);//测试过程中写死的资源路径
		}
//		File file = com.jwzt.upload.main.FileUtil.packageFile(Environment.getExternalStorageDirectory()+"/upload/", listpath);
//		String fileName=com.jwzt.upload.main.FileUtil.getFileName(file.getAbsolutePath());
//		String UUID=com.jwzt.upload.main.FileUtil.getUUID();
//		RSSBean bean=new RSSBean();
//		bean.setUUID(UUID);
//		bean.setName(fileName);
//		bean.setZipPath(file.getAbsolutePath());
//		bean.setList(listpath);
//		if(IsNonEmptyUtils.isList(selectImage)){
//			bean.setSessionId(selectImage.get(0).getFilepath());
//		}
		if(selectImage.get(0)!=null&&selectImage.get(0).getFilepath()!=null) {
			mUploadUtils.UploadFile(Environment.getExternalStorageDirectory() + "/upload/", listpath, selectImage.get(0).getFilepath(), infotype);
		}else{
			mUploadUtils.UploadFile(Environment.getExternalStorageDirectory() + "/upload/", listpath, "", infotype);
		}
		}
	
	/**
	 * 任务上传
	 */
	private void sendTask(String infotype){
		String xmlpath=getIntent().getStringExtra("xmlpath");
		ArrayList<ItemImage> selectImage=(ArrayList<ItemImage>) getIntent().getSerializableExtra("imglist");
		ArrayList<String> listpath=new ArrayList<String>();//所选本地资源路径集合
		if(IsNonEmptyUtils.isList(selectImage)){
			for(int i=0;i<selectImage.size();i++){//小于size-1是应为有个空的item用来显示加号
				String filepath=selectImage.get(i).getFilepath();
				if(filepath.startsWith("file:///")){
					String filePaths=filepath.replaceFirst("file:///", "");
					listpath.add(filePaths);
				}else{
					listpath.add(filepath);
				}
			}
		}
		listpath.add(xmlpath);
//		File file = com.jwzt.upload.main.FileUtil.packageFile(Environment.getExternalStorageDirectory()+"/upload/", listpath);
//		String fileName=com.jwzt.upload.main.FileUtil.getFileName(file.getAbsolutePath());
//		String UUID=com.jwzt.upload.main.FileUtil.getUUID();
//		RSSBean bean=new RSSBean();
//		bean.setUUID(UUID);
//		bean.setName(fileName);
//		bean.setZipPath(file.getAbsolutePath());
//		bean.setList(listpath);
//		if(IsNonEmptyUtils.isList(selectImage)){
//			bean.setSessionId(selectImage.get(0).getFilepath());
//		}
		if(IsNonEmptyUtils.isList(selectImage)){
			mUploadUtils.UploadFile(Environment.getExternalStorageDirectory()+"/upload/",listpath,selectImage.get(0).getFilepath(),infotype);
		}else{
			mUploadUtils.UploadFile(Environment.getExternalStorageDirectory()+"/upload/",listpath,"",infotype);
		}
	}
	
	/**
	 * 适配列表
	 */
	private void initView(){
		mAdapter=new BackTransferAdapter(BackTransferActivity.this,mList);
		lv.setAdapter(mAdapter);
	}
	
	private void findViews() {
		tv_titles=(TextView) findViewById(R.id.tv_titles);
		tv_titles.setText("内容回传");
		iv_plus=(ImageView) findViewById(R.id.iv_plus);
		iv_empty=(ImageView) findViewById(R.id.iv_empty);
		tv_empty=(TextView) findViewById(R.id.tv_empty);
		lv=(ListView) findViewById(R.id.lv);
		iv_back=findViewById(R.id.iv_back);
		iv_plus.setOnClickListener(this);
		iv_back.setOnClickListener(this);
		iv_empty.setOnClickListener(this);
		iv_empty.setVisibility(View.GONE);
		tv_empty.setVisibility(View.GONE);
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				TaskInfo taskInfos = mList.get(arg2);
				switch (taskInfos.getTask_statu()) {
				case 1://表示点击之前是正在上传点击后变成暂停
					taskInfos.setTask_statu(2);
					mList.set(arg2, taskInfos);
					mAdapter.setList(mList);
					mAdapter.notifyDataSetChanged();
					for (int i = 0; i < MainService.allTask.size(); i++) {
						TaskInfo taskInList = MainService.allTask.get(i);
						if (taskInList.getTask_name().equals(taskInfos.getTask_name())) {
							taskInList.setTaskStatus(1);
							// MainService.allTask.remove(taskInList);
						}
					}
					break;
				case 2://表示点击之前是暂停状态点击后变成继续上传
					if (taskInfos.getTaskstop() == 0) {
						taskInfos.setTask_statu(1);
						mList.set(arg2, taskInfos);
						mAdapter.setList(mList);
						mAdapter.notifyDataSetChanged();
						if (MainService.allTask.indexOf(taskInfos) <= -1) // 判断任务列表中是否存在该任务
						{
							taskInfos.setTaskStatus(0);
							MainService.startThread();
							// 向任务列表中 添加任务
							MainService.allTask.add(taskInfos);
						}
					}
					break;
				case 3://表示上传完成点击后不做操作
					// String Xmlpath = taskInfos.getDataXmlPath();
					// File file = new File(Xmlpath);
					// String FilePath = file.getPath();
					// // 获取xml中的存储信息
					// RSSBean bean = CommonActivity.getCaoGaoMsg(file);
					// // 用网页显示 xml文件
					// Bundle bundle = new Bundle();
					// // Intent intent = new
					// // Intent(getApplicationContext(),WebViewActivity.class);
					// Intent intent = new Intent(getApplicationContext(),
					// NewsBrowseActivity.class);
					// bundle.putSerializable("bean", bean);
					// intent.putExtras(bundle);
					// intent.putExtra("XmlPath", FilePath);
					// startActivity(intent);
					break;
				case 4://表示点击之前是上传失败点击后重新上传
					taskInfos.setTask_statu(1);
					mList.set(arg2, taskInfos);
					mAdapter.setList(mList);
					mAdapter.notifyDataSetChanged();
					if (MainService.allTask.indexOf(taskInfos) <= -1) // 判断任务列表中是否存在该任务
					{
						taskInfos.setTaskStatus(0);
						MainService.startThread();
						// 向任务列表中 添加任务
						MainService.allTask.add(taskInfos);
					}
					break;
				}
			}
		});

//		lv.setOnItemLongClickListener(new OnItemLongClickListener() {
//
//			@Override
//			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,final int arg2, long arg3) {
//				new AlertDialog.Builder(FileUpManageActivity.this)
//						.setTitle("操作选项")
//						.setItems(new CharSequence[] { "删除", "取消" },
//								new DialogInterface.OnClickListener() {
//
//									public void onClick(DialogInterface dialog,int which) {
//										switch (which) {
//										case 0:
//											TaskInfo taskInfo = list.get(arg2);
//											MainService.allTask.remove(taskInfo);
//
//											String dataXmlPath = taskInfo.getDataXmlPath();
//											File xmlFile = new File(dataXmlPath);
//											String copyFile = dataXmlPath.replace(".xml","-copy.xml");
//
//											if (xmlFile.exists()) {
//												FileUtil.delFile1(dataXmlPath); // 删除文件
//												FileUtil.delFile1(copyFile); // 删除copy文件(去掉文件路径的xml文件)
//												FileUtil.delFile1(dataXmlPath.replace("uploading","caogao")); // 删除草稿文件
//											} else {
//												System.out.println("%%%%%%%%%%%%  *任务对应的 稿件xml 不存在*  %%%%%%% %%%%%");
//											}
//
//											// 删除 data 管理
//											SharedPreferences preferences = getSharedPreferences("JWZT_Tasks",Activity.MODE_PRIVATE);
//											String uuidName = preferences.getString("data" + arg2,"");
//											String data = preferences.getString("dataNum", "");
//											// 获取 总得任务数量
//											int num = Integer.parseInt(data);
//											SharedPreferences.Editor editor = preferences.edit();
//											editor.remove(taskInfo.getDataNum());
//											editor.remove("dataNum");
//											// 更新 num 数量
//											editor.putString("dataNum", ""+ (num - 1));
//											editor.commit();
//
//											// 删除 单独任务信息
//											SharedPreferences pferences = getSharedPreferences(uuidName,Activity.MODE_PRIVATE);
//											SharedPreferences.Editor edit = pferences.edit();
//											edit.clear();
//											edit.commit();
//
//											list.remove(arg2);
//											adapter.notifyDataSetChanged();
//
//											File file = new File(taskInfo.getTask_path());
//											file.delete();
//											System.out.println("删除 rar 包:"+ file.exists());
//											Toast.makeText(getApplicationContext(),"已删除", Toast.LENGTH_SHORT).show();
//											break;
//										default:
//											break;
//										}
//									}
//								}).show();
//				return true;
//			}
//		});
	}
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()){
		case R.id.iv_back://返回按钮
			finish();
			overridePendingTransition(R.anim.push_left_out,R.anim.push_right_out);
			break;
		case R.id.iv_plus://右上角的加号
			Intent intent_all=new Intent(BackTransferActivity.this,SelectFootageActivity.class);
			startActivity(intent_all);
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			break;
		}
	}
	@Override
	protected void initRequestOnSuccess(String result, int code, int biaoshi) {
		// TODO Auto-generated method stub
		
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
		
	}

	@Override
	public void onComplete(TaskInfo overTask, List<TaskInfo> list) {
		// TODO Auto-generated method stub
		SharedPreferences preferences = getSharedPreferences(overTask.getTask_name(), Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean("task_isok", true);
		editor.remove("task_statu");
		editor.commit();

		overTask.setTask_isok(true);
		for (int i = 0; i < mList.size(); i++) {
			TaskInfo info = mList.get(i);
			if (info.getTask_name().equals(overTask.getTask_name())) {
				info.setTask_statu(3);
				mList.set(i, info);
				mList.remove(i);
				System.out.println("上传成功");
				Toast.makeText(getApplicationContext(),"上传成功",0).show();
				mAdapter.setList(mList);
				mAdapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public void onCreateTaskOver(TaskInfo tinfo, List<TaskInfo> list) {
		// TODO Auto-generated method stub
//		if(mAdapter==null){
//			mList=list;
//			mHandler.sendEmptyMessage(1);
//		}else{
//			mAdapter.setList(list);// 更新完list的数据要把它重新传入adapter，这样才能更新
//			mAdapter.notifyDataSetChanged();
//		}
		
//		String uuid = tinfo.getTask_name();
//		System.out.println("rar包文件创建完毕后返回的值：");
//		System.out.println("UUID = " + uuid);
//		System.out.println("rar-path = " + tinfo.getTask_path());
//		System.out.println("rar-length = " + tinfo.getTask_length());
//
//		// 通过 uuid 获取xml信息
//		SharedPreferences preferences1 = getSharedPreferences(uuid,Activity.MODE_PRIVATE);
//		SharedPreferences.Editor ed = preferences1.edit();
//		// 将包的相关信息添加进任务信息中
//		ed.putString("task_path", tinfo.getTask_path()); // 包得路径
//		ed.putString("task_length", tinfo.getTask_length()); // 文件总长度
//		ed.commit();
//
////		for (int i = 0; i < list.size(); i++) {
////			TaskInfo infos1 = list.get(i);
////			if (tinfo.getTask_name().equals(infos1.getTask_name())) {
////				infos1.setTask_statu(1);
////				list.set(i, infos1);
////				mAdapter.setList(list);// 更新完list的数据要把它重新传入adapter，这样才能更新
////				mAdapter.notifyDataSetChanged();
////			}
////		}
	}

	@Override
	public void onException(TaskInfo errorTask, List<TaskInfo> list) {
		// TODO Auto-generated method stub
		UserToast.toSetToast(BackTransferActivity.this, "网络连接异常，请稍后再试...");
	}

	@Override
	public void onNoFile() {
//		mHandler.post(new Runnable() {
//			
//			@Override
//			public void run() {
//				UIUtils.showToast("没有文件");
				UserToast.toSetToast(BackTransferActivity.this, "所要上传的文件不存在（路径更改 或者 已经删除）...");
//			}
//		});
	}

	@Override
	public void onUpdatePrograss(TaskInfo tinfo, List<TaskInfo> list) {
		// TODO Auto-generated method stub
		System.out.println("=========================>>"+tinfo.getTask_length()+"===="+tinfo.getLength() + "");
		if(mAdapter!=null){
			for (int i = 0; i < mList.size(); i++) {
				TaskInfo infos1 = mList.get(i);
				if (tinfo.getTask_name().equals(infos1.getTask_name())) {
					infos1.setTask_statu(1);
					mList.set(i, tinfo);
					mAdapter.setList(mList);
					mAdapter.notifyDataSetChanged();
				}
			}
		}
	}

	@Override
	public void allTaskList(List<TaskInfo> list) {
		if(IsNonEmptyUtils.isList(list)){
			for(int i=0;i<list.size();i++){
				System.out.println("===================>>"+list.get(i).getFileName());
				if(list.get(i).getTask_statu()!=3){
					mList.add(list.get(i));
				}
			}
			mHandler.sendEmptyMessage(1);
		}
	}

	@Override
	public void setContinueUp(boolean iscontinue) {
		// TODO Auto-generated method stub
		List<TaskInfo> list=mUploadUtils.getAllTaskList();
		System.out.println(""+list.size());
		if(IsNonEmptyUtils.isList(list)){
			for(int i=0;i<list.size();i++){
				System.out.println("===================>>"+list.get(i).getFileName());
				if(list.get(i).getTask_statu()!=3){
					if(IsNonEmptyUtils.isList(mList)){
						if(isTaskGroup(list.get(i).getFileName(), mList)){//表示mlist集合中有该任务
							
						}else{
							mList.add(list.get(i));
						}
					}
				}
			}
			mHandler.sendEmptyMessage(1);
		}
	}
	
	/**
	 * 判断改任务是否存在群组交流列表中
	 * @return
	 */
	private boolean isTaskGroup(String filename,List<TaskInfo> list){
		if(IsNonEmptyUtils.isList(list)){
			for(int i=0;i<list.size();i++){
				if(filename.equals(list.get(i).getFileName())){
					return true;
				}
			}
		}else{
			return false;
		}
		return false;
	}
}
