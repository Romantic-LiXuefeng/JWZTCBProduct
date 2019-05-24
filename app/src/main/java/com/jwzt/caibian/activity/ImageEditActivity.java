package com.jwzt.caibian.activity;

import java.io.File;

import org.greenrobot.eventbus.EventBus;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.bean.StringMessage;
import com.jwzt.caibian.bean.StringMessageImage;
import com.jwzt.caibian.util.BitmapUtils;
import com.jwzt.caibian.util.FileOperateUtil;
import com.jwzt.caibian.widget.ImageViewTouch;
import com.jwzt.caibian.widget.ImageViewTouchBase;


/**
 * 图片的编辑界面
 * @author afnasdf
 *
 */
public class ImageEditActivity extends Activity {
	public static final String FILE_PATH = "file_path";
    public static final String EXTRA_OUTPUT = "extra_output";
    public static final String SAVE_FILE_PATH = "save_file_path";

    public static final String IMAGE_IS_EDIT = "image_is_edit";

    public static final int MODE_NONE = 0;
    public static final int MODE_STICKERS = 1;// 贴图模式
    public static final int MODE_FILTER = 2;// 滤镜模式
    public static final int MODE_CROP = 3;// 剪裁模式
    public static final int MODE_ROTATE = 4;// 旋转模式
    public static final int MODE_TEXT = 5;// 文字模式

    public String filePath;// 需要编辑图片路径
    public String saveFilePath;// 生成的新图片路径
    private int imageWidth, imageHeight;// 展示图片控件 宽 高
    private LoadImageTask mLoadImageTask;
	ImageViewTouch mainImage;
	public Bitmap mainBitmap;// 底层显示Bitmap
	
	
	private View stickerBtn;// 贴图按钮
    private View fliterBtn;// 滤镜按钮
    private View cropBtn;// 剪裁按钮
    private View rotateBtn;// 旋转按钮
    private View mTextBtn;//文字型贴图添加
	private View mSaveBtn;
	
	private String newFilePath;//新生成的图片路径
	private String newFilePath_sub;//新生成图片的缩略图地址
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_image_edit_my);
		DisplayMetrics metrics = getResources().getDisplayMetrics();
	    imageWidth = metrics.widthPixels / 2;
	    imageHeight = metrics.heightPixels / 2;
		initView();
		filePath = getIntent().getStringExtra(FILE_PATH);
		//源文件地址
		newFilePath=FileOperateUtil.getFolderPath(this, FileOperateUtil.TYPE_IMAGE, "test")+"/"+SystemClock.currentThreadTimeMillis()+"_"+(filePath.substring(filePath.lastIndexOf("/")+1));
		newFilePath_sub=FileOperateUtil.getFolderPath(this, FileOperateUtil.TYPE_THUMBNAIL, "test")+"/"+SystemClock.currentThreadTimeMillis()+"_"+(filePath.substring(filePath.lastIndexOf("/")+1));
	}
	
	
	/**
	 * 初始化页面控件
	 */
	 private void initView() {
		 	stickerBtn = this.findViewById(R.id.btn_stickers);
	        fliterBtn = this.findViewById(R.id.btn_fliter);
	        cropBtn = this.findViewById(R.id.btn_crop);
	        rotateBtn = this.findViewById(R.id.btn_rotate);
	        mTextBtn = this.findViewById(R.id.btn_text);
	        mSaveBtn = this.findViewById(R.id.save_btn);
		// this.fin
	        
	        
	        stickerBtn.setOnClickListener(new StickerClick());
	        fliterBtn.setOnClickListener(new FliterClick());
	        cropBtn.setOnClickListener(new CropClick());
	        rotateBtn.setOnClickListener(new RotateClick());
	        mTextBtn.setOnClickListener(new AddTextClick());
	        mSaveBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					File file1=new File(newFilePath);
					File file2=new File(newFilePath_sub);
					if(file1.exists()&&file2.exists()){
						EventBus.getDefault().post(new StringMessageImage(newFilePath));
					}
//					Intent intent=new Intent();
//					intent.putExtra("mPath", newFilePath);
//					intent.putExtra("subPath", newFilePath_sub);
//					
//					setResult(1, intent);
					finish();
				}
			});
		 
		 
		 
		 mainImage = (ImageViewTouch) findViewById(R.id.main_image);
	        View backBtn = findViewById(R.id.back_btn);// 退出按钮
	        View btn_crop = findViewById(R.id.btn_crop);// 退出按钮

		 btn_crop.setOnClickListener(new OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                forceReturnBack();
	            }
	        });
	        backBtn.setOnClickListener(new OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                forceReturnBack();
	            }
	        });

	    mainImage.setFlingListener(new ImageViewTouch.OnImageFlingListener() {
	            @Override
	            public void onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
	                //System.out.println(e1.getAction() + " " + e2.getAction() + " " + velocityX + "  " + velocityY);
	                if (velocityY > 1) {
//	                    closeInputMethod();
	                }
	            }
	        });
		
	}

	 
	 
	 @Override
	protected void onResume() {
		super.onResume();
		getData();
	}
	 
	 
	 
	 /**
	     * 马赛克模式
	     *
	     * @author panyi
	     */
	    private final class StickerClick implements OnClickListener {
	        @Override
	        public void onClick(View v) {
	        	
	        	Intent intent=new Intent(ImageEditActivity.this,MasacActivity.class);
	        	intent.putExtra("filePath", filePath);
	        	intent.putExtra("new_filePath", newFilePath);
	        	intent.putExtra("new_filePath_sub", newFilePath_sub);
	        	startActivity(intent);
	        }
	    }// end inner class

	    /**
	     * 滤镜模式
	     *
	     * @author panyi
	     */
	    private final class FliterClick implements OnClickListener {
	        @Override
	        public void onClick(View v) {
	        	
	        	Intent intent=new Intent(ImageEditActivity.this,StirckerActivity.class);
	        	intent.putExtra("filePath", filePath);
	        	intent.putExtra("new_filePath", newFilePath);
	        	intent.putExtra("new_filePath_sub", newFilePath_sub);
	        	startActivity(intent);
	        }
	    }// end inner class

	    /**
	     * 裁剪模式
	     *
	     * @author panyi
	     */
	    private final class CropClick implements OnClickListener {
	        @Override
	        public void onClick(View v) {
	        	Intent intent=new Intent(ImageEditActivity.this,CropActivity.class);
	        	intent.putExtra("filePath", filePath);
	        	intent.putExtra("new_filePath", newFilePath);
	        	intent.putExtra("new_filePath_sub", newFilePath_sub);
	        	startActivity(intent);
	        }
	    }// end inner class

	    /**
	     * 图片旋转模式
	     *
	     * @author panyi
	     */
	    private final class RotateClick implements OnClickListener {
	        @Override
	        public void onClick(View v) {
	        	
	        	Intent intent=new Intent(ImageEditActivity.this,RotateActivity.class);
	        	intent.putExtra("filePath", filePath);
	        	intent.putExtra("new_filePath", newFilePath);
	        	intent.putExtra("new_filePath_sub", newFilePath_sub);
	        	startActivity(intent);
	        }
	    }// end inner class

	    /**
	     * 插入文字模式
	     *
	     * @author panyi
	     */
	    private final class AddTextClick implements OnClickListener {
	        @Override
	        public void onClick(View v) {
	        	
	        	Intent intent=new Intent(ImageEditActivity.this,TextEditActivity.class);
	        	intent.putExtra("filePath", filePath);
	        	intent.putExtra("new_filePath", newFilePath);
	        	intent.putExtra("new_filePath_sub", newFilePath_sub);
	        	startActivity(intent);
	        	
	        }
	    }// end inner class
	 
	 
	 
	 
	/**
	* 强制推出
	*/
	private void forceReturnBack() {
	    this.finish();
	 }

	private void getData() {
		
		if(new File(newFilePath).exists()){
			//新生成的文件存在，则加载新生成的文件
			filePath=newFilePath;
		}
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
			try {
				mLoadImageTask = new LoadImageTask();
				mLoadImageTask.execute(filepath);
			} catch (Exception e) {
				e.printStackTrace();
			}
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
	            // mainImage.setDisplayType(DisplayType.FIT_TO_SCREEN);
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
