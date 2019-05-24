package com.jwzt.caibian.activity;

import java.io.File;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.jwzt.caibian.application.GlobalContants;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.adapter.LogoPositionAdapter;
import com.jwzt.caibian.application.BaseActivity;
import com.jwzt.caibian.bean.LogoBean;
import com.jwzt.caibian.util.BitmapUtils;
import com.jwzt.caibian.util.SharePreferenceUtils;
import com.jwzt.caibian.util.UIUtils;

/**
 * logo位置设置Activity
 * @author howie
 *
 */
public class LogoPositionActivity extends BaseActivity implements OnClickListener {
	/**标题*/
	private TextView tv_titles;
	private View iv_back;
	private GridView gv;
	LogoPositionAdapter locationadapter;
	private ArrayList<LogoBean> mlist;
	private ArrayList<String> logoList;
	private String picpath;
	public static File getStorage = Environment.getExternalStorageDirectory();
	public static String imagepath="/HB/images/";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logo_position);
		findViews();
		
		logoList=getIntent().getStringArrayListExtra("logolist");
		mlist=new ArrayList<LogoBean>();
		mlist=initList(logoList);
		
		initView();
	}
	
	private void initView(){
		int position = mSharePreferenceUtils.getInt(UIUtils.getContext(), GlobalContants.LOGOPOSITION, 0);
		mlist.get(position).setSelected(true);
		locationadapter=new LogoPositionAdapter(LogoPositionActivity.this,mlist);
		gv.setAdapter(locationadapter);
		
		gv.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				for (int i = 0; i < mlist.size(); i++) {
					if(i==position){
						mlist.get(i).setSelected(true);
						downloadimage(mlist.get(i).getLogopath());
						
						mSharePreferenceUtils.putInt(UIUtils.getContext(), GlobalContants.LOGOPOSITION, position);
					}else{
						mlist.get(i).setSelected(false);
					}
				}
				locationadapter.notifyDataSetChanged();
			}
		});
	}
	/**
	 * 下载图片
	 * @param path
	 */
	public void downloadimage(String path){
		picpath=path;
		String filename = path.hashCode()
				+ "";
		boolean is = BitmapUtils.isPicExist(filename);
		
			new Thread(saveFileRunnable).start();
		
		
	}
	
	private Runnable saveFileRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				Bitmap bitmap = BitmapUtils.returnBitMap(picpath);
				String filename =picpath.hashCode()+ "";
				BitmapUtils.saveImageToGallery(getApplicationContext(), bitmap,
						filename);
				mSharePreferenceUtils.putString(UIUtils.getContext(), GlobalContants.LIVELOGOPATH, getStorage+imagepath+filename+".jpg");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	/**
	 * 初始化数据
	 * @param list
	 * @return
	 */
	private ArrayList<LogoBean> initList(ArrayList<String> list) {
		ArrayList<LogoBean> mlist = new ArrayList<LogoBean>();
		for (int i = 0; i < 4; i++) {
			LogoBean logoBean=new LogoBean();
			logoBean.setSelected(false);
			logoBean.setLogopath(list.get(0));
			mlist.add(logoBean);
		}
		return mlist;
	}
	
	private void findViews() {
		tv_titles=(TextView) findViewById(R.id.tv_titles);
		tv_titles.setText("LOGO位置");
		iv_back=findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		gv=(GridView) findViewById(R.id.gv);
	}
	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.iv_back:
			finish();
			overridePendingTransition(R.anim.push_left_out,
					R.anim.push_right_out);
			break;
		}
	}
	@Override
	protected void initRequestOnSuccess(String result, int code, int biaoshi) {		
	}
	@Override
	protected void initRequestOnStart(String result, int code, int biaoshi) {		
	}
	@Override
	protected void initRequestOnFailure(String failure, int code, int biaoshi) {		
	}
	@Override
	protected void initRequestOnCache(String result, int code, int biaoshi) {		
	}

}
