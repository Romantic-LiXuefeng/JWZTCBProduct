package com.jwzt.caibian.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.bean.RatioItem;
import com.jwzt.caibian.util.BitmapUtils;
import com.jwzt.caibian.util.Matrix3;
import com.jwzt.caibian.widget.CropImageView;
import com.jwzt.caibian.widget.ImageViewTouch;
import com.jwzt.caibian.widget.ImageViewTouchBase;

/**
 * 图片测裁剪操作
 * @author afnasdf
 *
 */
public class CropActivity extends  BaseImageEditActivity {
	private String filePath;
	private LinearLayout ratioList;
	public CropImageView mCropPanel;// 剪裁操作面板
	public Bitmap mainBitmap;// 底层显示Bitmap
	
	public static final int ADDVIEW=1;
	
	
	
	/**
	 * 图层的添加需要在mainBitmap初始化，并且添加到底层视图后才能添加（其他几种图片操作类同）
	 */
	private Handler mHandler=new Handler(){
		public void dispatchMessage(android.os.Message msg) {
			switch (msg.what) {
			case ADDVIEW:
				//添加剪贴图层
				mCropPanel.setVisibility(View.VISIBLE);
				mCropPanel.setRatioCropRect(mainImage.getBitmapRect(),-1);
				break;
			default:
				break;
			}
			
			
		};
		
	};
	
	private static List<RatioItem> dataList = new ArrayList<RatioItem>();
	private static int[] bgLists={
		R.drawable.ic_img_any,
		R.drawable.ic_img_any,
		R.drawable.ic_four_three,
		R.drawable.ic_three_four,
		R.drawable.ic_sixteen_nine,
		R.drawable.ic_nine_sixteen};
	static {
		dataList.add(new RatioItem("任意", -1f,0));
		dataList.add(new RatioItem("1:1", 1f,1));
		dataList.add(new RatioItem("4:3", 4 / 3f,2));
		dataList.add(new RatioItem("3:4", 3 / 4f,3));
		dataList.add(new RatioItem("16:9", 16 / 9f,4));
		dataList.add(new RatioItem("9:16", 9 / 16f,5));
		
	}
	private List<TextView> textViewList = new ArrayList<TextView>();
	private CropRationClick mCropRationClick = new CropRationClick();
	private LoadImageTask mLoadImageTask;
	private int imageWidth;
	private int imageHeight;
	private ImageViewTouch mainImage;
	private String new_filePath;//生成新图片的地址
	private String new_filePath_sub;//生成新图片缩略图的地址
	public static int SELECTED_COLOR = Color.YELLOW;
	public static int UNSELECTED_COLOR = Color.WHITE;
	private boolean isHave=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_crop);
		DisplayMetrics metrics = getResources().getDisplayMetrics();
	    imageWidth = metrics.widthPixels / 2;
	    imageHeight = metrics.heightPixels / 2;
		filePath = getIntent().getStringExtra("filePath");
		new_filePath = getIntent().getStringExtra("new_filePath");
		new_filePath_sub = getIntent().getStringExtra("new_filePath_sub");
//		if(new File(new_filePath).exists()){
//			filePath=new_filePath;
//			isHave=true;
//		}
		initView();
		 
	}


	/**
	 * 初始化页面控件
	 */
	private void initView() {
		mainImage = (ImageViewTouch) findViewById(R.id.main_image);
		ratioList = (LinearLayout) findViewById(R.id.ratio_list_group);
		mCropPanel = (CropImageView) findViewById(R.id.crop_panel);
//		
		
		LinearLayout back_return=(LinearLayout)this.findViewById(R.id.back_return);
		
		//裁剪撤回
		back_return.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//重新加载源图片
				
				loadImage(filePath);
			}
		});
		//调回主菜单
		LinearLayout back_to_main=(LinearLayout)this.findViewById(R.id.back_to_main);
		back_to_main.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CropActivity.this.finish();
			}
		});
		
		//裁剪保存
		LinearLayout back_apply=(LinearLayout)this.findViewById(R.id.back_apply);
		back_apply.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				saveCropImage();
			}
		});
		
		setUpRatioList();
		loadImage(filePath);
	}


	/**
	 * 初始化列表
	 */
	private void setUpRatioList() {

		ratioList.removeAllViews();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_VERTICAL;
		params.leftMargin = 10;
		params.rightMargin = 10;
		for (int i = 0, len = dataList.size(); i < len; i++) {
			
			View view=View.inflate(this, R.layout.view_crop_size, null);
			TextView cutSize = (TextView) view.findViewById(R.id.tv_cut_size);
			ImageView cutSizeImg=(ImageView)view.findViewById(R.id.iv_cut_size_img);
			
//			TextView text = new TextView(activity);
			cutSize.setTextColor(this.getResources().getColor(R.color.text_bule));
			cutSize.setTextSize(13);
			
			if(i==0){
				cutSize.setText("裁剪比例");
				cutSize.setTextColor(UNSELECTED_COLOR);
				cutSize.setBackgroundResource(R.drawable.shape_rect_blue);
				cutSizeImg.setVisibility(View.GONE);
			}else{
				cutSizeImg.setBackgroundResource(bgLists[i]);
				cutSize.setTextColor(this.getResources().getColor(R.color.text_bule));
				cutSize.setText(dataList.get(i).getText());
			}
			
			
			if(i==1){
				params.leftMargin = 40;
			}else{
				params.leftMargin = 10;
			}
			
			
			textViewList.add(cutSize);
			ratioList.addView(view, params);
			view.setTag(i);
			dataList.get(i).setIndex(i);
			view.setTag(dataList.get(i));
			view.setOnClickListener(mCropRationClick);
		}// end for i
		
	}
	
	
	/**
	 * 选择剪裁比率
	 * 
	 * @author
	 * 
	 */
	private final class CropRationClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			
			
			
			
			RatioItem dataItem = (RatioItem) v.getTag();
			
			for(int i=0;i<textViewList.size();i++){
				
				if(dataItem.getIndex()==i){
					if(i==0){
						textViewList.get(i).setTextColor(getResources().getColor(R.color.white));
					}else{
						textViewList.get(i).setTextColor(getResources().getColor(R.color.text_color_2A3552));
					}
				}else{
					if(i==0){
						textViewList.get(i).setTextColor(getResources().getColor(R.color.black));
					}else{
						textViewList.get(i).setTextColor(getResources().getColor(R.color.text_color_000000));
					}
					
				}
			}
			
			
			mCropPanel.setRatioCropRect(mainImage.getBitmapRect(),
					dataItem.getRatio());
		}
	}// end inner class
	
	
	
	/**
	 * 保存剪切图片
	 */
	public void saveCropImage() {
		CropImageTask task = new CropImageTask();
		task.execute(mainBitmap);
	}

	/**
	 * 图片剪裁生成 异步任务
	 * 
	 * @author panyi
	 * 
	 */
	private final class CropImageTask extends AsyncTask<Bitmap, Void, Bitmap> {
		private Dialog dialog;

		@Override
		protected void onCancelled() {
			super.onCancelled();
			dialog.dismiss();
		}

		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@Override
		protected void onCancelled(Bitmap result) {
			super.onCancelled(result);
			dialog.dismiss();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = BaseImageEditActivity.getLoadingDialog(CropActivity.this, R.string.saving_image,
					false);
			dialog.show();
		}

		@Override
		protected Bitmap doInBackground(Bitmap... params) {
			RectF cropRect = mCropPanel.getCropRect();// 剪切区域矩形
			Matrix touchMatrix = mainImage.getImageViewMatrix();
			// Canvas canvas = new Canvas(resultBit);
			float[] data = new float[9];
			touchMatrix.getValues(data);// 底部图片变化记录矩阵原始数据
			Matrix3 cal = new Matrix3(data);// 辅助矩阵计算类
			Matrix3 inverseMatrix = cal.inverseMatrix();// 计算逆矩阵
			Matrix m = new Matrix();
			m.setValues(inverseMatrix.getValues());
			m.mapRect(cropRect);// 变化剪切矩形

			Bitmap resultBit = Bitmap.createBitmap(params[0],
					(int) cropRect.left, (int) cropRect.top,
					(int) cropRect.width(), (int) cropRect.height());

			saveBitmap(resultBit, new_filePath);//保存操作的图片
			saveBitmap(resultBit, new_filePath_sub);//保存缩略图
			return resultBit;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			dialog.dismiss();
			if (result == null)
				return;

			if (mainBitmap != null
					&& !mainBitmap.isRecycled()) {
				mainBitmap.recycle();
			}
			mainBitmap = result;
			mainImage.setImageBitmap(mainBitmap);
			mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
			mCropPanel.setCropRect(mainImage.getBitmapRect());
//			backToMain();
			
			CropActivity.this.finish();
		}
	}// end inner class

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
            mainImage.clear();
            mainBitmap = result;
            mainImage.setImageBitmap(result);
            mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
            mainImage.setScaleEnabled(false);//裁剪过程中禁止缩放功能
            
            mHandler.sendEmptyMessage(ADDVIEW);
            
            //设置裁剪区域
//            RectF r = mainImage.getBitmapRect();
//            mCropPanel.setCropRect(r);
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

    

    
}
