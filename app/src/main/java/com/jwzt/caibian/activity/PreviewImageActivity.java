package com.jwzt.caibian.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.greenrobot.eventbus.EventBus;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.activity.RecordActivity.PopupWindows;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.bean.AttachsBeen;
import com.jwzt.caibian.bean.LoginBean;
import com.jwzt.caibian.bean.StringMessage;
import com.jwzt.caibian.util.BitmapUtils;
import com.jwzt.caibian.util.DialogHelp;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.widget.ImageViewTouch;
import com.jwzt.caibian.widget.ImageViewTouchBase;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;


/**
 * 图片预览界面
 * @author afnasdf
 *
 */
public class PreviewImageActivity extends Activity{
	private ProgressDialog progressDialog;
	
	private String FILE_PATH = "file_path";
	private LoadImageTask mLoadImageTask;
	private int imageWidth;
	private int imageHeight;
	ImageViewTouch mainImage;
	public Bitmap mainBitmap;// 底层显示Bitmap
	private String filePath;
	private String pic_time;
	private String pic_size;
	private TextView picName;
	private TextView picSize;
	private TextView picTime;
    private LinearLayout layouts;
    private ImageView share;
    private RelativeLayout relatives;
    private EditText edit_img;
    private TextView tv_save,et_quxiao;
    private CbApplication app;
    private LoginBean loginBean;
    private String mUserId;
	private ArrayList<AttachsBeen> mList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_pre_image);
		
		app=(CbApplication) this.getApplication();
		loginBean=app.getmLoginBean();
		mUserId=loginBean.getUserID();
		mList=new ArrayList<AttachsBeen>();
		DisplayMetrics metrics = getResources().getDisplayMetrics();
	    imageWidth = metrics.widthPixels / 2;
	    imageHeight = metrics.heightPixels / 2;
		initView();
		
		
	}

	/**
	 * 初始化页面控件
	 */
	private void initView() {
		layouts=(LinearLayout) this.findViewById(R.id.linelayout);
		relatives=(RelativeLayout) this.findViewById(R.id.popuwindows);
		relatives.setVisibility(View.GONE);
		 mainImage = (ImageViewTouch) findViewById(R.id.main_image);
		
		 picName = (TextView)this.findViewById(R.id.tv_pic_name);
		 picSize = (TextView)this.findViewById(R.id.tv_pic_size);
		 picTime = (TextView)this.findViewById(R.id.tv_pic_time);
		 
		 /**
		  * 修改图片名称相关控件
		  */
		 edit_img=(EditText)this.findViewById(R.id.et_video_name);
		 //保存
		 tv_save=(TextView)this.findViewById(R.id.tv_save);
		 //取消
		 et_quxiao=(TextView)this.findViewById(R.id.et_save2);
		 et_quxiao.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				relatives.setVisibility(View.GONE);;
			}
		});
		 
		 tv_save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mList=	readCache(filePath, edit_img.getText().toString());
				saveVideoFootages();
				//chageFileName(filePath, edit_img.getText().toString());
				relatives.setVisibility(View.GONE);;
			}
		});
		 
		this.findViewById(R.id.iv_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PreviewImageActivity.this.finish();
				
			}
		});
	    share=(ImageView)this.findViewById(R.id.iv_share);
		share.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {//弹出修改文件名称
				relatives.setVisibility(View.VISIBLE);;

//				CbApplication app=(CbApplication) getApplication();
//				LoginBean loginBean = app.getmLoginBean();
//				if(loginBean!=null){
//					String userID = loginBean.getUserID();
//					ArrayList<String> list=new ArrayList<String>();
//					list.add(filePath);
////					toUpImg(userID, list);
//					if(progressDialog==null){
//						progressDialog=DialogHelp.getWaitDialog(PreviewImageActivity.this, "开始准备上传");
//					}
//					progressDialog.show();
////					Toast.makeText(PreviewImageActivity.this, "开始准备上传", 0).show();
//				}
				/*Intent intentUpload=new Intent(PreviewImageActivity.this, NewScriptActivity.class);
				intentUpload.putExtra("tag", "resources");
				intentUpload.putExtra("resourcespath", filePath);
				startActivity(intentUpload);*/
			}
		});
		this.findViewById(R.id.iv_edit).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent it = new Intent(PreviewImageActivity.this, ImageEditActivity.class);
				it.putExtra("file_path", filePath);
//				startActivityForResult(it, 1);
				startActivity(it);
				finish();
			}
		});
		
		getData();		
	}
	
	private void saveVideoFootages(){
	
		
		if(IsNonEmptyUtils.isString(mUserId)){
			SharedPreferences sp=this.getSharedPreferences(mUserId, Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			String json=JSON.toJSONString(mList);
			editor.putString("imagefootages_"+loginBean.getUsername(), json);//根据userId进行区分
			editor.commit();
		}
	}
	
	
	private ArrayList<AttachsBeen> readCache(String filenameoid,String filenamenew){
		if(IsNonEmptyUtils.isString(mUserId)){
			SharedPreferences sp=this.getSharedPreferences(mUserId, Context.MODE_PRIVATE);
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
							//判断如果文件名称一样 则进行修改
							if(achsPath.equals(filenameoid)){
								
							   String newspath=	chageFileName(attchs, filenamenew);
							   attachsBeen.setAchsPath(newspath);
							   attachsBeen.setAchsPicurl(newspath);
							   filePath=newspath;
						       picName.setText(filePath.substring(filePath.lastIndexOf("/")+1)+"");

							}
							
						}
					}
					return list;
				}
			}
		}
		return null;
		
	}

	///storage/emulated/0/JWZT_PHOTOS/test/Thumbnail/20180505214206.jpg
	 public String chageFileName(String filePath,String reName){  
	        File file = new File(filePath);  
	        //前面路径必须一样才能修改成功  
	        String path = filePath.substring(0, filePath.lastIndexOf("/")+1)+reName+filePath.substring(filePath.lastIndexOf("."), filePath.length());  
	        File newFile = new File(path);  
	        file.renameTo(newFile);  
	        return newFile.getAbsolutePath();
	    }  
	private void getData() {
        filePath = getIntent().getStringExtra(FILE_PATH);
        picName.setText(filePath.substring(filePath.lastIndexOf("/")+1)+"");
        
        pic_time = getIntent().getStringExtra("pic_time");
        picTime.setText(pic_time+"");
        pic_size = getIntent().getStringExtra("pic_size");
        picSize.setText(pic_size+"");
       
        loadImage(filePath);
    }

 
 	/**
     * 异步载入编辑图片
     *
     * @param filepath
     */
    public void loadImage(String filepath) {
        if (mLoadImageTask != null) {
            mLoadImageTask.cancel(true);
        }
        mLoadImageTask = new LoadImageTask();
        mLoadImageTask.execute(filepath);
    }

    private final class LoadImageTask extends AsyncTask<String, Void, Bitmap> {

		@Override
        protected Bitmap doInBackground(String... params) {

            return BitmapUtils.getSampledBitmap(params[0], imageWidth,
                    imageHeight);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (mainBitmap != null) {
                mainBitmap.recycle();
                mainBitmap = null;
                System.gc();
            }
            mainBitmap = result;
            mainImage.setImageBitmap(result);
            mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        }
    }// end inner class
 
    /**
     * 切换底图Bitmap
     *
     * @param newBit
     */
    public void changeMainBitmap(Bitmap newBit) {
        if (mainBitmap != null) {
            if (!mainBitmap.isRecycled()) {// 回收
                mainBitmap.recycle();
            }
            mainBitmap = newBit;
        } else {
            mainBitmap = newBit;
        }// end if
        mainImage.setImageBitmap(mainBitmap);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoadImageTask != null) {
            mLoadImageTask.cancel(true);
        }
    }
//	private void toUpImg(String userID,List<String> list) {
//		
////		HttpRequestUtils.UploadRequest(userID, columnId, msgType, content,list, this);//这里节目的id写死为1
//		HttpRequestUtils.simpleUploadRequest(userID, list, "3",this);//3代表图片类型
//	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(data!=null){
			setResult(resultCode, data);
			EventBus.getDefault().post(new StringMessage(data.getStringExtra("mPath")));
			finish();
		}
	}
	
	
}
