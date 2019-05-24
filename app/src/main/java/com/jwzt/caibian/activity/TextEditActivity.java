package com.jwzt.caibian.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.util.BitmapUtils;
import com.jwzt.caibian.util.StickerTask_2;
import com.jwzt.caibian.widget.ImageViewTouch;
import com.jwzt.caibian.widget.ImageViewTouchBase;
import com.jwzt.caibian.widget.TextStickerView;


/**
 * 图片的文字编辑
 * @author afnasdf
 *
 */
@SuppressLint("ResourceAsColor")
public class TextEditActivity extends BaseImageEditActivity implements TextWatcher {
	
	
	
	private int imageWidth;
	private int imageHeight;
	public String filePath;
	public ImageViewTouch mainImage;
	private View backToMenu;
	private TextStickerView mTextStickerView;
	private EditText mInputText;
	private LoadImageTask mLoadImageTask;
	private InputMethodManager imm;
	
	private int[] textColor={
			R.color.color0,
			R.color.color1,
			R.color.color2,
			R.color.color3,
			R.color.color4,
			R.color.color5,
			R.color.color6,
			R.color.color7,
			R.color.color8
	};
	private String new_filePath;
	public String new_filePath_sub;
	
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		 imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		setContentView(R.layout.activity_image_text);
		
		DisplayMetrics metrics = getResources().getDisplayMetrics();
	    imageWidth = metrics.widthPixels / 2;
	    imageHeight = metrics.heightPixels / 2;
		filePath = getIntent().getStringExtra("filePath");
		new_filePath = getIntent().getStringExtra("new_filePath");
		new_filePath_sub = getIntent().getStringExtra("new_filePath_sub");
//		if(new File(new_filePath).exists()){
//			filePath=new_filePath;
//		}
		 
		initView();
		 
	}


	/**
	 * 初始化页面控件
	 */
	private void initView() {
		mainImage = (ImageViewTouch) findViewById(R.id.main_image);
		mainImage.setScaleEnabled(false);
		backToMenu = findViewById(R.id.back_to_main);
		mTextStickerView = (TextStickerView) findViewById(R.id.text_sticker_panel);
		mTextStickerView.setVisibility(View.VISIBLE);
		mInputText = (EditText) findViewById(R.id.text_input);
		mInputText.addTextChangedListener(this);
		mTextStickerView.setEditText(mInputText);
		tv_show_select = (TextView)this.findViewById(R.id.tv_show_select);
		colorSelect();
		
		LinearLayout back_return=(LinearLayout)this.findViewById(R.id.back_return);
		
		//裁剪保存
		back_return.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadImage(filePath);
			}
		});
		//调回主菜单
		LinearLayout back_to_main=(LinearLayout)this.findViewById(R.id.back_to_main);
		back_to_main.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TextEditActivity.this.finish();
			}
		});
		//裁剪撤回
		LinearLayout back_apply=(LinearLayout)this.findViewById(R.id.back_apply);
		back_apply.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveTextImage();
			}
		});
		
		loadImage(filePath);
		
	}


	/**
	 * 颜色选择器
	 */
	private void colorSelect() {
		TextView color1=(TextView)this.findViewById(R.id.tv_color1);
		TextView color2=(TextView)this.findViewById(R.id.tv_color2);
		TextView color3=(TextView)this.findViewById(R.id.tv_color3);
		TextView color4=(TextView)this.findViewById(R.id.tv_color4);
		TextView color5=(TextView)this.findViewById(R.id.tv_color5);
		TextView color6=(TextView)this.findViewById(R.id.tv_color6);
		TextView color7=(TextView)this.findViewById(R.id.tv_color7);
		TextView color8=(TextView)this.findViewById(R.id.tv_color8);
		TextView color0=(TextView)this.findViewById(R.id.tv_color0);
		
		color0.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				tv_show_select.setBackgroundColor(getResources().getColor(R.color.color0));
		        mTextStickerView.setTextColor(getResources().getColor(R.color.color0));
			}
		});
		color1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				tv_show_select.setBackgroundColor(getResources().getColor(R.color.color1));
				mTextStickerView.setTextColor(getResources().getColor(R.color.color1));
			}
		});
		color5.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				tv_show_select.setBackgroundColor(getResources().getColor(R.color.color5));
				mTextStickerView.setTextColor(getResources().getColor(R.color.color5));
			}
		});
		color6.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				tv_show_select.setBackgroundColor(getResources().getColor(R.color.color6));
				mTextStickerView.setTextColor(getResources().getColor(R.color.color6));
			}
		});
		color7.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				tv_show_select.setBackgroundColor(getResources().getColor(R.color.color7));
				mTextStickerView.setTextColor(getResources().getColor(R.color.color7));
			}
		});
		color8.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				tv_show_select.setBackgroundColor(getResources().getColor(R.color.color8));
				mTextStickerView.setTextColor(getResources().getColor(R.color.color8));
			}
		});
		color2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tv_show_select.setBackgroundColor(getResources().getColor(R.color.color2));
		        mTextStickerView.setTextColor(getResources().getColor(R.color.color2));
			}
		});
		color3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tv_show_select.setBackgroundColor(getResources().getColor(R.color.color3));
		        mTextStickerView.setTextColor(getResources().getColor(R.color.color3));
				
			}
		});
		color4.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				Toast.makeText(TextEditActivity.this, "4", 0).show();
//				changeTextColor(3);
				tv_show_select.setBackgroundColor(getResources().getColor(R.color.color4));
		        mTextStickerView.setTextColor(getResources().getColor(R.color.color4));
			}
		});
	}


	
	
	
	
	/**
     * 修改字体颜色
     *
     * @param newColor
     */
    private void changeTextColor(int newColor) {
        tv_show_select.setBackgroundColor(textColor[newColor]);
        mTextStickerView.setTextColor(textColor[newColor]);
    }

    public void hideInput() {
        if (getCurrentFocus() != null && isInputMethodShow()) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public boolean isInputMethodShow() {
        return imm.isActive();
    }

	
	
	

	
	
	
	private Bitmap mainBitmap;
	private SaveTextStickerTask mSaveTask;
	private TextView tv_show_select;
	
	
	
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
            
            mTextStickerView.setVisibility(View.VISIBLE);
            mInputText.clearFocus();
            
            // mainImage.setDisplayType(DisplayType.FIT_TO_SCREEN);
        }
    }// end inner class
 
   

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoadImageTask != null) {
            mLoadImageTask.cancel(true);
        }
    }

    
    
    /**
     * 保存贴图图片
     */
    public void saveTextImage() {
        if (mSaveTask != null) {
            mSaveTask.cancel(true);
        }

        //启动任务
        mSaveTask = new SaveTextStickerTask(this);
        mSaveTask.execute(mainBitmap);
    }

    /**
     * 文字合成任务
     * 合成最终图片
     */
    private final class SaveTextStickerTask extends StickerTask_2 {

        public SaveTextStickerTask(TextEditActivity activity) {
            super(activity);
        }

        @Override
        public void handleImage(Canvas canvas, Matrix m) {
            float[] f = new float[9];
            m.getValues(f);
            int dx = (int) f[Matrix.MTRANS_X];
            int dy = (int) f[Matrix.MTRANS_Y];
            float scale_x = f[Matrix.MSCALE_X];
            float scale_y = f[Matrix.MSCALE_Y];
            canvas.save();
            canvas.translate(dx, dy);
            canvas.scale(scale_x, scale_y);
            //System.out.println("scale = " + scale_x + "       " + scale_y + "     " + dx + "    " + dy);
            mTextStickerView.drawText(canvas, mTextStickerView.layout_x,
                    mTextStickerView.layout_y, mTextStickerView.mScale, mTextStickerView.mRotateAngle);
            canvas.restore();
        }

        @Override
        public void onPostResult(Bitmap result) {
            mTextStickerView.clearTextContent();
            mTextStickerView.resetView();
            //保存并结束当前的界面操作
            TextEditActivity.this.finish();
       
        }
    }//end inner class
    
    

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		
	}
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		
	}
	@Override
	public void afterTextChanged(Editable s) {
		String text = s.toString().trim();
        mTextStickerView.setText(text);
		
	}
}