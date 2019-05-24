package com.jwzt.caibian.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
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

import com.jwzt.cb.product.R;
import com.jwzt.caibian.bean.RatioItem;
import com.jwzt.caibian.util.BitmapUtils;
import com.jwzt.caibian.widget.ImageViewTouch;
import com.jwzt.caibian.widget.ImageViewTouchBase;
import com.jwzt.caibian.widget.ImageViewTouchBase.OnLayoutChangeListener;
import com.jwzt.caibian.widget.RotateImageView;


/**
 * 图片测裁剪操作
 * @author afnasdf
 *
 */
public class RotateActivity extends BaseImageEditActivity {
	
	
	private String filePath;
	private View backToMenu;
	private LinearLayout ratioList;
	public RotateImageView mRotatePanel;// 旋转操作控件
	public Bitmap mainBitmap;// 底层显示Bitmap
	private static List<RatioItem> dataList = new ArrayList<RatioItem>();
	private static int[] bgLists={
		R.drawable.ic_img_left,
		R.drawable.ic_img_right,
		R.drawable.ic_img_left_right,
		R.drawable.ic_img_up_down};
	private CropRationClick mCropRationClick = new CropRationClick();
	private LoadImageTask mLoadImageTask;
	private int imageWidth;
	private int imageHeight;
	private ImageViewTouch mainImage;
	private String new_filePath;
	private String new_filePath_sub;
	private boolean isHave=false;;
	public static int SELECTED_COLOR = Color.YELLOW;
	public static int UNSELECTED_COLOR = Color.WHITE;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_image_rotate);
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
		backToMenu = findViewById(R.id.back_to_main);
		ratioList = (LinearLayout) findViewById(R.id.ratio_list_group);
		mRotatePanel = (RotateImageView) findViewById(R.id.rotate_panel);
		
		
		LinearLayout back_return=(LinearLayout)this.findViewById(R.id.back_return);
		
		//撤回
		back_return.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mRotatePanel.rotateImage(0);//返回到0角度
			}
		});
		//调回主菜单
		LinearLayout back_to_main=(LinearLayout)this.findViewById(R.id.back_to_main);
		back_to_main.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				RotateActivity.this.finish();
			}
		});
		//操作保存
		LinearLayout back_apply=(LinearLayout)this.findViewById(R.id.back_apply);
		back_apply.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				saveRotateImage();
				
			}
		});
		
		setUpRatioList();
		loadImage(filePath);
		
		mainImage.setOnLayoutChangeListener(new OnLayoutChangeListener() {
			
			@Override
			public void onLayoutChanged(boolean changed, int left, int top, int right,
					int bottom) {
				
				System.out.println(left+"=="+top+"=="+right+"=="+bottom);
				
			}
		});
		
	}
	 private int currentRotateSize=0;

	 private void setUpRatioList() {
			// init UI
			ratioList.removeAllViews();
			ratioList.setWeightSum(4);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER_VERTICAL;
			params.leftMargin = 10;
			params.rightMargin = 10;
			for (int i = 0 ;i < 4; i++) {
				
				View view=View.inflate(this, R.layout.view_rotate_size, null);
				TextView cutSize = (TextView) view.findViewById(R.id.tv_cut_size);
				ImageView cutSizeImg=(ImageView)view.findViewById(R.id.iv_cut_size_img);
				cutSize.setVisibility(View.INVISIBLE);
				cutSizeImg.setBackgroundResource(bgLists[i]);
				ratioList.addView(view, params);
				view.setTag(i);
				view.setOnClickListener(mCropRationClick);
			}// end for i
			
	 }
			/**
			 * 图片翻转角度
			 * @author
			 */
			private final class CropRationClick implements OnClickListener {
				@Override
				public void onClick(View v) {
					int dataItem = (Integer) v.getTag();
					switch (dataItem) {
					case 0:
						//像左转
							currentRotateSize=currentRotateSize-10;
							mRotatePanel.rotateImage(currentRotateSize);
						break;
					case 1:
						//向右转
						currentRotateSize=currentRotateSize+10;
						mRotatePanel.rotateImage(currentRotateSize);
						break;
					case 2:
						//左右翻转
						currentRotateSize=currentRotateSize-180;
						mRotatePanel.rotateImage(currentRotateSize);
						break;
					case 3:
						//上下翻转
						currentRotateSize=currentRotateSize+180;
						mRotatePanel.rotateImage(currentRotateSize);
						break;

					default:
						break;
					}
				}
			}// end inner class

		    
	
	
		    /**
		     * 保存旋转图片
		     */
		    public void saveRotateImage() {
		        // System.out.println("保存旋转图片");
		        if (currentRotateSize == 0 || currentRotateSize == 360) {// 没有做旋转
		        	finish();
		            return;
		        } else {// 保存图片
		            SaveRotateImageTask task = new SaveRotateImageTask();
		            task.execute(mainBitmap);
		        }// end if
		    }

		    /**
		     * 保存图片线程
		     *
		     * @author panyi
		     */
		    private final class SaveRotateImageTask extends
		            AsyncTask<Bitmap, Void, Bitmap> {
		        private Dialog dialog;

		        @Override
		        protected void onCancelled() {
		            super.onCancelled();
		            dialog.dismiss();
		        }

		        @Override
		        protected void onCancelled(Bitmap result) {
		            super.onCancelled(result);
		            dialog.dismiss();
		        }

		        @Override
		        protected void onPreExecute() {
		            super.onPreExecute();
		            dialog = BaseImageEditActivity.getLoadingDialog(RotateActivity.this, R.string.saving_image,
		                    false);
		            dialog.show();
		        }

		        @Override
		        protected Bitmap doInBackground(Bitmap... params) {
		            RectF imageRect = mRotatePanel.getImageNewRect();
		            Bitmap originBit = params[0];
		            Bitmap result = Bitmap.createBitmap((int) imageRect.width(),
		                    (int) imageRect.height(), Bitmap.Config.ARGB_4444);
		            Canvas canvas = new Canvas(result);
		            int w = originBit.getWidth() >> 1;
		            int h = originBit.getHeight() >> 1;
		            float centerX = imageRect.width() / 2;
		            float centerY = imageRect.height() / 2;

		            float left = centerX - w;
		            float top = centerY - h;

		            RectF dst = new RectF(left, top, left + originBit.getWidth(), top
		                    + originBit.getHeight());
		            canvas.save();
		            canvas.scale(mRotatePanel.getScale(), mRotatePanel.getScale(),
		                    imageRect.width() / 2, imageRect.height() / 2);
		            canvas.rotate(mRotatePanel.getRotateAngle(), imageRect.width() / 2,
		                    imageRect.height() / 2);

		            canvas.drawBitmap(originBit, new Rect(0, 0, originBit.getWidth(),
		                    originBit.getHeight()), dst, null);
		            canvas.restore();

		            saveBitmap(result, new_filePath);// 保存图片
		            saveBitmap(result, new_filePath_sub);// 保存图片对应的缩略图
		            return result;
		        }

		        @Override
		        protected void onPostExecute(Bitmap result) {
		            super.onPostExecute(result);
		            dialog.dismiss();
		            if (result == null)
		                return;

		            // 切换新底图
		            if (mainBitmap != null
		                    && !mainBitmap.isRecycled()) {
		                mainBitmap.recycle();
		            }
		            mainBitmap = result;
		            mainImage.setImageBitmap(mainBitmap);
		            mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
//		            backToMain();
		            RotateActivity.this.finish();
		        }
		    }// end inner class

		    /**
		     * 保存Bitmap图片到指定文件
		     *
		     * @param bm
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
		        // System.out.println("保存文件--->" + f.getAbsolutePath());
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
            mainImage.setScaleEnabled(true);//旋转操作禁止图片缩放
            mHandler.sendEmptyMessageDelayed(ADDVIEW, 1000);
            
            
            
//            mRotatePanel.setVisibility(View.VISIBLE);
            // mainImage.setDisplayType(DisplayType.FIT_TO_SCREEN);
        }
    }// end inner class
 
    
    
    private static final int ADDVIEW=1;
    private Handler mHandler=new Handler(){
    	public void dispatchMessage(android.os.Message msg) {
    		
    		switch (msg.what) {
			case ADDVIEW:
				//添加旋转图层
				mainImage.setVisibility(View.GONE);
	            mRotatePanel.addBit(mainBitmap,
	                    mainImage.getBitmapRect());
	            mRotatePanel.reset();
				mRotatePanel.setVisibility(View.VISIBLE);
				
				break;
			default:
				break;
			}
    		
    		
    	};
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoadImageTask != null) {
            mLoadImageTask.cancel(true);
        }
    }

    
    
}
