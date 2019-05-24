package com.jwzt.caibian.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.application.Configs;
import com.jwzt.caibian.bean.LoginBean;
import com.jwzt.caibian.util.BitmapUtils;
import com.jwzt.caibian.util.FileOperateUtil;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.StickerItem;
import com.jwzt.caibian.util.StickerTask_1;
import com.jwzt.caibian.widget.ImageViewTouch;
import com.jwzt.caibian.widget.ImageViewTouchBase;
import com.jwzt.caibian.widget.StickerView;
import com.jwzt.cb.product.R;
import com.xdroid.request.XRequest;
import com.xdroid.request.base.Request;
import com.xdroid.request.config.DataType;
import com.xdroid.request.ex.RequestParams;
import com.xdroid.request.impl.OnRequestListenerAdapter;
import com.xdroid.request.network.HttpError;
import com.xdroid.request.network.HttpException;
import com.xdroid.request.response.NetworkResponse;


/**
 * 贴图模式
 * @author afnasdf
 *
 */
public class StirckerActivity extends BaseImageEditActivity {
	private int imageWidth;
	private int imageHeight;
	public String filePath;
	public ImageViewTouch mainImage;
	private LinearLayout ratioList;
	public Bitmap mainBitmap;// 底层显示Bitmap
	private LoadImageTask mLoadImageTask;
	private StickerView mStickerView;
	private SaveStickersTask mSaveTask;
	public static int SELECTED_COLOR = Color.YELLOW;
	public static int UNSELECTED_COLOR = Color.WHITE;
	private CbApplication application;
	private LoginBean mLoginBean;
//	private List<ShuiYinBean> mList;
	private List<String> mList;
	
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				downloadSY();
				break;
			}
		};
	};
	
	
	List<Bitmap> mBitmapLists=new ArrayList<Bitmap>();
	private String new_filePath;
	public String new_filePath_sub;
	
	
	
	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_image_sticker);
		application=(CbApplication) getApplication();
		mLoginBean=application.getmLoginBean();
		
		DisplayMetrics metrics = getResources().getDisplayMetrics();
	    imageWidth = metrics.widthPixels / 2;
	    imageHeight = metrics.heightPixels / 2;
		filePath = getIntent().getStringExtra("filePath");
		new_filePath = getIntent().getStringExtra("new_filePath");
		new_filePath_sub = getIntent().getStringExtra("new_filePath_sub");
//		if(new File(new_filePath).exists()){
//			filePath=new_filePath;
//		}
		
		List<File> listFiles = FileOperateUtil.listFiles(BitmapUtils.path, ".jpg");
		if(listFiles!=null){
			for(int i=0;i<listFiles.size();i++){
				mBitmapLists.add(BitmapUtils.getDiskBitmap(listFiles.get(i).getAbsolutePath()));
			}
		}
		
		mBitmapLists.add(BitmapUtils.readBitMap(this, R.drawable.addbtn_normal));
		initView();
		
		getShuiYingData();
	}

	/**
	 * 获得水印图片数据
	 */
	private void getShuiYingData(){
		RequestData(Configs.shuiyingUrl, Configs.shuiyingCode, -1);
	}
	
	/**
	 * 下载水印图片
	 */
	private void downloadSY(){
		if(mList!=null&&mList.size()>0){
			new MyTask().execute();
			for(int i=0;i<mList.size();i++){
				String filename = mList.get(i).hashCode() + "";
				boolean is = BitmapUtils.isPicExist(filename);
				if (is) {
					Toast.makeText(StirckerActivity.this, "图片已保存", Toast.LENGTH_SHORT).show();
				} else {
					
				}
			}
		}
	}
	
	
	 public class MyTask extends AsyncTask<String,Integer,Void>{

		@Override
		protected Void doInBackground(String... params) {
			for(int i=0;i<mList.size();i++){
				Bitmap bitmap = BitmapUtils.returnBitMap(mList.get(i));
				String filename = mList.get(i).hashCode() + "";
				BitmapUtils.saveImageToGallery(getApplicationContext(), bitmap,filename);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			//需要重新填充，水印图片
//			List<File> listFiles = FileOperateUtil.listFiles(BitmapUtils.path, ".jpg");
			List<File> listFiles = FileOperateUtil.listFiles(BitmapUtils.getStorage+BitmapUtils.imagepath, ".jpg");
			mBitmapLists.clear();
			if(listFiles!=null){
				for(int i=0;i<listFiles.size();i++){
					mBitmapLists.add(BitmapUtils.getDiskBitmap(listFiles.get(i).getAbsolutePath()));
				}
			}
			mBitmapLists.add(BitmapUtils.readBitMap(StirckerActivity.this, R.drawable.addbtn_normal));
			setUpRatioList();
		}
	 }
	
	/**
	 * 初始化页面控件
	 */
	private void initView() {
		mainImage = (ImageViewTouch) findViewById(R.id.main_image);
		ratioList = (LinearLayout) findViewById(R.id.ratio_list_group);
		mStickerView = (StickerView) findViewById(R.id.sticker_panel);
		LinearLayout back_return=(LinearLayout)this.findViewById(R.id.back_return);
		//裁剪保存
		back_return.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//撤回按钮（重新加载图片）
				loadImage(filePath);
			}
		});
		//调回主菜单
		LinearLayout back_to_main=(LinearLayout)this.findViewById(R.id.back_to_main);
		back_to_main.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				StirckerActivity.this.finish();
			}
		});
		//印章应用按钮
		LinearLayout back_apply=(LinearLayout)this.findViewById(R.id.back_apply);
		back_apply.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				saveStickers();
			}
		});
		setUpRatioList();
		loadImage(filePath);
	}


	/**
     * 保存贴图任务
     *
     * @author panyi
     */
    private final class SaveStickersTask extends StickerTask_1 {
        public SaveStickersTask(StirckerActivity activity) {
            super(activity);
        }

        @Override
        public void handleImage(Canvas canvas, Matrix m) {
            LinkedHashMap<Integer, StickerItem> addItems = mStickerView.getBank();
            for (Integer id : addItems.keySet()) {
                StickerItem item = addItems.get(id);
                item.matrix.postConcat(m);// 乘以底部图片变化矩阵
                canvas.drawBitmap(item.bitmap, item.matrix, null);
            }// end for
        }

        @Override
        public void onPostResult(Bitmap result) {
            mStickerView.clear();
            StirckerActivity.this.finish();
        }
    }// end inner class

    /**
     * 保存贴图层 合成一张图片
     */
    public void saveStickers() {
        // System.out.println("保存 合成图片");
        if (mSaveTask != null) {
            mSaveTask.cancel(true);
        }
        mSaveTask = new SaveStickersTask(StirckerActivity.this);
        mSaveTask.execute(mainBitmap);
    }

	/**
	 * 初始化列表
	 */
	private void setUpRatioList() {

		// init UI
		ratioList.removeAllViews();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_VERTICAL;
		params.leftMargin = 10;
		params.rightMargin = 10;
		for (int i = 0, len = mBitmapLists.size(); i < len; i++) {
			View view=View.inflate(this, R.layout.view_crop_size, null);
			TextView cutSize = (TextView) view.findViewById(R.id.tv_cut_size);
			ImageView cutSizeImg=(ImageView)view.findViewById(R.id.iv_cut_size_img);
			cutSize.setVisibility(View.GONE);
			cutSizeImg.setImageBitmap(mBitmapLists.get(i));
			ratioList.addView(view, params);
			view.setTag(i);
			view.setOnClickListener(new ImageClick());
		}// end for i
	}
	
	/**
	 * 选择贴图
	 * 
	 * @author panyi
	 * 
	 */
	private final class ImageClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			int data =  (Integer) v.getTag();
			if(data==(mBitmapLists.size()-1)){
				//添加图片
//				getShuiYingData(mLoginBean.getUserID());
			}else{
				selectedStickerItem(mBitmapLists.get(data));
			}
			
		}
	}// end inner class
	
	
	/**
     * 选择贴图加入到页面中
     *
     * @param path
     */
    public void selectedStickerItem(Bitmap drawableId) {
        mStickerView.addBitImage(drawableId);
    }
	
	/**
	 * 保存Bitmap图片到指定文件
	 */
	public static void saveBitmap(Bitmap bm, String filePath) {
		File f = new File(filePath);
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
            return BitmapUtils.getSampledBitmap(params[0], imageWidth,imageHeight);
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
            mStickerView.setVisibility(View.VISIBLE);
            
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
    
	protected  void RequestData(String url,final int code,final int biaoshi) {
		//与GET请求的URL一样，为了避免同样的缓存key、这里重新指定缓存key
		RequestParams params = new RequestParams();
		String cacheKey = url + "post";  
		XRequest.getInstance().sendPost("shuiying", url, cacheKey, params, new OnRequestListenerAdapter<String>() {
			/**
			 * 数据请求失败
			 */
			@Override
			public void onRequestFailed(Request<?> request, HttpException httpException) {
				super.onRequestFailed(request, httpException);
				switch (httpException.getHttpErrorCode()) {
				case HttpError.ERROR_NOT_NETWORK:
//					initRequestOnFailure("网络未连接，请检查", code, biaoshi);
					break;
				}
			}
            /**
             * 数据请求重试
             */
			@Override
			public void onRequestRetry(Request<?> request, int currentRetryCount, HttpException previousError) {
//				Toast.makeText(BaseActivity.this, "获取信息失败，系统已经为您重试" + currentRetryCount+"次", Toast.LENGTH_SHORT).show();
			}
			
			/**
			 * 缓存加载完成
			 */
			@Override
			public void onCacheDataLoadFinish(Request<?> request,Map<String, String> headers, String result) {
				super.onCacheDataLoadFinish(request, headers, result);
//				initRequestOnCache(result, code, biaoshi);
			}
			/**
			 * 下载进度回调
			 */
			@Override
			public void onRequestDownloadProgress(Request<?> request, long transferredBytesSize, long totalSize) {
			}
			/**
			 * 上传进度回调
			 */
			@Override
			public void onRequestUploadProgress(Request<?> request, long transferredBytesSize, long totalSize, int currentFileIndex,
					File currentFile) {
			}
			@Override
			public void onDone(Request<?> request, Map<String, String> headers, String result, DataType dataType) {
				super.onDone(request, headers, result, dataType);
			}
			/**
			 *数据请求结束
			 */
			@Override
			public void onRequestFinish(Request<?> request,Map<String, String> headers, String result) {
				super.onRequestFinish(request, headers, result);
				
			}
			/**
			 * 网络数据加载完成
			 */
			@Override
			public boolean onParseNetworkResponse(Request<?> request,NetworkResponse networkResponse, String result) {
				super.onParseNetworkResponse(request, networkResponse, result);
//				initRequestOnSuccess(result, code, biaoshi);
				if(code==Configs.shuiyingCode){//表示是水印数据
					try {
						JSONObject jsonObject = new JSONObject(result);
						if(jsonObject.getString("message").contains("成功")){
							JSONArray jsonArray=new JSONArray(jsonObject.getString("data"));
							mList=new ArrayList<String>();
							for(int i=0;i<jsonArray.length();i++){
								String shuiyinpic=(String) jsonArray.get(i);
								mList.add(shuiyinpic);
							}
							
							if(IsNonEmptyUtils.isList(mList)){
								mHandler.sendEmptyMessage(1);
							}
//							 http://47.93.185.248:9000//2017_08/06/1502003958835.jpg, 
//							   http://47.93.185.248:9000//2017_08/03/1501745475022.png, 
//								http://47.93.185.248:9000//2017_07/31/1501466784337.jpg, 
//									http://47.93.185.248:9000//2017_07/18/1500366263072.png]
//							mList=JSON.parseArray;
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return true;
			}			
		});
	}
}