package com.jwzt.caibian.activity;

import java.util.List;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.adapter.MyViewPagerAdapter;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.util.BitmapUtils;
import com.jwzt.caibian.util.UserToast;


/**
 * 
 * @author
 * 
 */
public class PhotoActivity extends FragmentActivity {
	
	private ViewPager mViewPager;
	private MyViewPagerAdapter adapter;
	private int position = 0;
	private int mPosition = 0;
	private TextView tv_num, tv_save;
	private List<String> list;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				PhotoActivity.this.finish();
				break;
			case 1:
				UserToast.toSetToast(PhotoActivity.this, "保存成功");
				break;
			case 2:
				UserToast.toSetToast(PhotoActivity.this, "保存失败");
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photoactivity);
		
		position = Integer.valueOf(getIntent().getStringExtra("position"));
		list = getIntent().getStringArrayListExtra("list");

		findViews();
		initView();
	}

	private void initView() {// 初始化fragment适配
		adapter = new MyViewPagerAdapter(getSupportFragmentManager(),PhotoActivity.this, list);
		mViewPager.setAdapter(adapter);
		mViewPager.setCurrentItem(position-1);
	}

	/**
	 * 加载更多
	 */
	private void findViews() {// 初始化控件
		mViewPager = (ViewPager) this.findViewById(R.id.viewpager);
		tv_num = (TextView) this.findViewById(R.id.tv_num);
		tv_num.setText(position + "/" + list.size() + "");
		tv_save = (TextView) this.findViewById(R.id.tv_save);
		mViewPager.setOnPageChangeListener(new PagerListener());
		mViewPager.setOffscreenPageLimit(1);

		tv_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				UserToast.toSetToast(PhotoActivity.this, "保存");
				String filename = list.get(mPosition).hashCode() + "";
				boolean is = BitmapUtils.isPicExist(filename);
				if (is) {
					UserToast.toSetToast(PhotoActivity.this, "已经保存");
					
				} else {
					new Thread(saveFileRunnable).start();
				}
			}
		});
		
		
	}

	private Runnable saveFileRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				Bitmap bitmap = BitmapUtils.returnBitMap(list.get(mPosition).trim());
				String filename = list.get(mPosition).hashCode() + "";
				BitmapUtils.saveImageToGallery(getApplicationContext(), bitmap,filename);
				Message message = mHandler.obtainMessage();
				message.what = 1;
				mHandler.sendMessage(message);
			} catch (Exception e) {
				Message message = mHandler.obtainMessage();
				message.what = 2;
				mHandler.sendMessage(message);
				e.printStackTrace();
			}
		}
	};

	/**
	 * 添加收藏
	 * 
	 * @param questionId
	 */
	// 启动线程获取时间

	private class PagerListener implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int position) {
			mPosition = position;
			tv_num.setText((position + 1) + "/" + list.size());
		}
	};

	public void setFinish() {
		mHandler.sendEmptyMessage(0);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
}
