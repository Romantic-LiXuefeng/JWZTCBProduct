package com.jwzt.caibian.activity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.jwzt.caibian.application.GlobalContants;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.application.BaseActivity;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.bean.LocationNoUploadBean;
import com.jwzt.caibian.bean.LoginBean;
import com.jwzt.caibian.bean.NewUploadinglistBean;
import com.jwzt.caibian.bean.ResourcesBean;
import com.jwzt.caibian.bean.TypeBean;
import com.jwzt.caibian.bean.UploadingDetailsBean;
import com.jwzt.caibian.db.LocationUploadDao;
import com.jwzt.caibian.util.FileUtil;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.UserToast;
import com.jwzt.caibian.xiangce.Bimp;
import com.jwzt.caibian.xiangce.ItemImage;
import com.jwzt.upload.configs.Configs;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * 稿件预览（未上传）
 * @author howie
 *
 */
public class PreviewActivity extends BaseActivity implements OnClickListener {
	private TextView tv_titles,tv_title,tv_time,tv_author_name,tv_type,tv_location,tv_image_num,tv_video_num,tv_audio_num,tv_all,tv_content;
	private ImageView iv_back;
	private LinearLayout ll;
	/**编辑和上传按钮*/
	private TextView tv_edit,tv_upload;
	private LocationNoUploadBean locationNoUploadBean;
	private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private int imgNum,videoNum,audioNum;//统计图音视的个数
    private List<ResourcesBean> resourceList;
    private ArrayList<ItemImage> selectImage;//保存所选图片
    private UploadingDetailsBean mNewUploadinglistBean;
    private LocationUploadDao noUpLoadtask;
    
    private CbApplication application;
    private LoginBean mLoginBean;
	private View rv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preview);
		
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
	    mNewUploadinglistBean=new UploadingDetailsBean();
	    resourceList=new ArrayList<ResourcesBean>();
	    selectImage=new ArrayList<ItemImage>();
	    noUpLoadtask=new LocationUploadDao(getHelper());
	    
		locationNoUploadBean=(LocationNoUploadBean) getIntent().getSerializableExtra("preview");
		findViews();
		
//		addViews();
//		rv.setLayoutManager(new LinearLayoutManager(PreviewActivity.this, LinearLayoutManager.HORIZONTAL, false));
	}
	private void findViews() {
		tv_titles=(TextView) findViewById(R.id.tv_titles);
		tv_titles.setText("稿件");
		tv_all=(TextView) findViewById(R.id.tv_all);
		ll=(LinearLayout) findViewById(R.id.ll);
		iv_back=(ImageView) findViewById(R.id.iv_back);
		tv_edit=(TextView) findViewById(R.id.tv_edit);
		tv_upload=(TextView) findViewById(R.id.tv_upload);
		tv_title=(TextView) findViewById(R.id.tv_title);
		tv_time=(TextView) findViewById(R.id.tv_time);
		tv_author_name=(TextView) findViewById(R.id.tv_creater);
		tv_type=(TextView) findViewById(R.id.tv_type);
		tv_location=(TextView) findViewById(R.id.tv_location);
		tv_content=(TextView) findViewById(R.id.tv_content);
		tv_image_num=(TextView) findViewById(R.id.tv_image_num);
		tv_video_num=(TextView) findViewById(R.id.tv_video_num);
		tv_audio_num=(TextView) findViewById(R.id.tv_audio_num);
		rv = findViewById(R.id.rv);
		tv_all.setOnClickListener(this);
		tv_all.setVisibility(View.GONE);
		iv_back.setOnClickListener(this);
		tv_edit.setOnClickListener(this);
		tv_upload.setOnClickListener(this);
		TextView tv_form = findViewById(R.id.tv_form);
		tv_form.setText("未上传");
		TextView tv_status = findViewById(R.id.tv_status);
		tv_status.setText("未上传");
		if(locationNoUploadBean!=null){
			tv_title.setText(locationNoUploadBean.getTitle());
			tv_time.setText(locationNoUploadBean.getSaveTime1());
			tv_author_name.setText(locationNoUploadBean.getUserName());
			tv_type.setText(locationNoUploadBean.getTypeName());
			tv_location.setText(locationNoUploadBean.getLocation());
			tv_content.setText(locationNoUploadBean.getContent());
			String resourcepath=locationNoUploadBean.getImgpath();
			if(IsNonEmptyUtils.isString(resourcepath)){
				String[] pathArray=resourcepath.split(",");
				if(pathArray!=null&&pathArray.length>0){
					addViews(pathArray);
					
					for(int i=0;i<pathArray.length;i++){
						ResourcesBean bean=new ResourcesBean();
						ItemImage imgBean=new ItemImage();
						String resourcespath=pathArray[i];
						imgBean.setFilepath(resourcespath);
						selectImage.add(imgBean);
						
						if(resourcespath.startsWith("file:///")){
							String mp3Path=resourcespath.replaceFirst("file:///", "");
							File file=new File(mp3Path);
							bean.setCreateTime(FileUtil.getModifiedTime(mp3Path));
							bean.setFileSize(file.length()+"");
						}else{
							File file=new File(resourcespath);
							bean.setCreateTime(FileUtil.getModifiedTime(resourcespath));
							bean.setFileSize(file.length()+"");
						}
//						File file=new File(resourcespath);
//						bean.setCreateTime(FileUtil.getModifiedTime(resourcespath));
						bean.setFileId(i+"");
						bean.setFileRealPath(resourcespath);
//						bean.setFileSize(file.length()+"");
						bean.setId(i+"");
						bean.setInfoId(i+"");
						if(resourcespath.startsWith("file:///")||resourcespath.startsWith("http://")){
							bean.setPreviewUrl(resourcespath);
						}else{
							bean.setPreviewUrl("file:///"+resourcespath);
						}
						bean.setResourceName(resourcespath.substring(resourcespath.lastIndexOf("/") + 1));
						bean.setResourceType("3");
						resourceList.add(bean);
					}
				}else {
					findViewById(R.id.view_bar).setVisibility(View.GONE);
					rv.setVisibility(View.GONE);
					if(imgNum==0){
						tv_image_num.setVisibility(View.GONE);
					}if(videoNum==0){
						tv_video_num.setVisibility(View.GONE);
					}if(audioNum==0){
						tv_audio_num.setVisibility(View.GONE);
					}
				}
			}else{
				findViewById(R.id.view_bar).setVisibility(View.GONE);
				rv.setVisibility(View.GONE);
				if(imgNum==0){
					tv_image_num.setVisibility(View.GONE);
				}if(videoNum==0){
					tv_video_num.setVisibility(View.GONE);
				}if(audioNum==0){
					tv_audio_num.setVisibility(View.GONE);
				}
			}
		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.tv_all://全部
			Intent intent=new Intent(PreviewActivity.this,AllFootageActivity.class);
			intent.putExtra("alls", (Serializable)resourceList);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			break;
		case R.id.iv_back://返回按钮
			finish();
			overridePendingTransition(R.anim.push_left_out,R.anim.push_right_out);
			break;
		case R.id.tv_edit://编辑
			Intent intent_edit=new Intent(PreviewActivity.this,NewScriptActivity.class);
			mNewUploadinglistBean.setAddress(application.getLocation());
			mNewUploadinglistBean.setCategoryId(locationNoUploadBean.getTypeId());
			mNewUploadinglistBean.setCategoryName(locationNoUploadBean.getTypeName());
			mNewUploadinglistBean.setContent(locationNoUploadBean.getContent());
			mNewUploadinglistBean.setCreateTime(locationNoUploadBean.getSaveTime());
			mNewUploadinglistBean.setId("");
			mNewUploadinglistBean.setKeyWord("");
			mNewUploadinglistBean.setLatitude(application.getLatitude()+"");
			mNewUploadinglistBean.setLongitude(application.getLongitude()+"");
			mNewUploadinglistBean.setPreviewUrl("");
			mNewUploadinglistBean.setReason("");
			mNewUploadinglistBean.setResources(resourceList);
			mNewUploadinglistBean.setSource("");
			mNewUploadinglistBean.setTitle(locationNoUploadBean.getTitle());
			intent_edit.putExtra("tag", "detailsnoup");
			intent_edit.putExtra("details", mNewUploadinglistBean);
			intent_edit.putExtra("nouploadBean", locationNoUploadBean);
//			mNewUploadinglistBean=(NewUploadinglistBean) getIntent().getSerializableExtra("details");
			startActivity(intent_edit);
			this.finish();
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			break; 
		case R.id.tv_upload://上传
			if(mLoginBean!=null){
				/***文稿上传第一次请求接口*/
				Configs.firstUploadUrl=com.jwzt.caibian.application.Configs.ICON_URL+"/bvCaster_converge/phone/media/upload.jspx?requestType=getFileSize&uuid=%s&fileName=%s";
				/***文稿上传第二次请求接口*/
				Configs.SecondUploadUrl=com.jwzt.caibian.application.Configs.ICON_URL+"/bvCaster_converge/phone/media/upload.jspx?requestType=uploadFile&uuid=%s&fileName=%s&beginPosition=%s";
				/***文稿上传第三次请求接口*/
				Configs.ThirdUploadUrl=com.jwzt.caibian.application.Configs.ICON_URL+"/bvCaster_converge/phone/media/upload.jspx?requestType=finishNotify&uuid=%s&fileName=%s&fileTotalSize=%s&fileMD5=%s&infoType=%s";
				
				Intent intent_back=new Intent(PreviewActivity.this,BackTransferActivity.class);
				intent_back.putExtra("title", locationNoUploadBean.getTitle());
				intent_back.putExtra("content", locationNoUploadBean.getContent());
				TypeBean bean=new TypeBean();
				bean.setCategoryName(locationNoUploadBean.getTypeName());
				bean.setId(locationNoUploadBean.getTypeId());
				intent_back.putExtra("type", bean);
				intent_back.putExtra("imglist", selectImage);
				intent_back.putExtra("tag", "NoUpSend");
				intent_back.putExtra("infotype", "1");
				startActivity(intent_back);
				com.jwzt.caibian.application.Configs.isUpload=true;
				noUpLoadtask.deletePerson(locationNoUploadBean);
				
				this.finish();
			}
			finish();
			overridePendingTransition(R.anim.push_left_out,R.anim.push_right_out);
			break;
		}
	}
	
	private void addViews(final String[] pathArray){
		for (int i = 0; i < pathArray.length; i++) {
			View view=View.inflate(PreviewActivity.this, R.layout.media_item_layout, null);
			ImageView iv=(ImageView) view.findViewById(R.id.iv);
			ImageView iv_play=(ImageView) view.findViewById(R.id.iv_play);
			String endString=pathArray[i];
			if(endString.endsWith("mp3")||endString.endsWith("wav")){
				iv_play.setVisibility(View.GONE);
				iv.setImageResource(R.drawable.audio_bg);
				audioNum++;
			}else if(endString.endsWith("mp4")){
				iv_play.setVisibility(View.VISIBLE);
				if(pathArray[i].startsWith("file:///")||pathArray[i].startsWith("http://")){
					imageLoader.displayImage(pathArray[i], iv, options);
				}else{
					imageLoader.displayImage("file:///"+pathArray[i], iv, options);
				}
				
				videoNum++;
			}else if(endString.endsWith("png")||endString.endsWith("jpg")){
				iv_play.setVisibility(View.GONE);
				if(pathArray[i].startsWith("file:///")||pathArray[i].startsWith("http://")){
					imageLoader.displayImage(pathArray[i], iv, options);
				}else{
					imageLoader.displayImage("file:///"+pathArray[i], iv, options);
				}
				iv.setId(i);
				iv.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						//UIUtils.showToast(mList.get(mi).getPreviewUrl());
//						Intent intent = new Intent(PreviewActivity.this,ShowImageActivity.class);
//						intent.putExtra(GlobalContants.NEWSHOWIMAGE,"file:///"+pathArray[v.getId()]);
//						startActivity(intent);
						
						String fileRealPath="file:///"+pathArray[v.getId()];
						if(fileRealPath.endsWith("mp4")||fileRealPath.endsWith("mp3")){
							Intent videointent=new Intent(PreviewActivity.this, ShowVideoActivity.class);
							videointent.putExtra("playpath", fileRealPath);
							startActivity(videointent);
						}else{
							Intent intent=new Intent(PreviewActivity.this, ShowImageActivity.class);
//							if(IsNonEmptyUtils.isString(preViewImg)){
//								intent.putExtra(GlobalContants.NEWSHOWIMAGE, preViewImg);
//							}else if(IsNonEmptyUtils.isString(fileRealPath)){
								intent.putExtra(GlobalContants.NEWSHOWIMAGE, fileRealPath);
//							}
							startActivity(intent);
						}
					}
				});
				imgNum++;
			}else{
				iv_play.setVisibility(View.GONE);
				if(pathArray[i].startsWith("file:///")||pathArray[i].startsWith("http://")){
					imageLoader.displayImage(pathArray[i], iv, options);
				}else{
					imageLoader.displayImage("file:///"+pathArray[i], iv, options);
				}
				imgNum++;
			}
//			if(i==1){
//				iv_play.setVisibility(View.GONE);
//				iv.setImageResource(R.drawable.audio_bg);
//			}else if(i==2){
//				iv_play.setVisibility(View.GONE);
//				iv.setImageResource(R.drawable.bg0);
//			}
			ll.addView(view);
		}
		
		tv_image_num.setText("图片("+imgNum+")");
		tv_video_num.setText("视频("+videoNum+")");
		tv_audio_num.setText("音频("+audioNum+")");
		if(imgNum==0){
			tv_image_num.setVisibility(View.GONE);
		}if(videoNum==0){
			tv_video_num.setVisibility(View.GONE);
		}if(audioNum==0){
			tv_audio_num.setVisibility(View.GONE);
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
	
}
