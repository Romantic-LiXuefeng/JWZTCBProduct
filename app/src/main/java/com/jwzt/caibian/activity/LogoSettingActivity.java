package com.jwzt.caibian.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.jwzt.caibian.application.GlobalContants;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.adapter.LogoAdapter;
import com.jwzt.caibian.application.BaseActivity;
import com.jwzt.caibian.bean.LoginBean;
import com.jwzt.caibian.bean.LogoBean;
import com.jwzt.caibian.util.BitmapUtils;
import com.jwzt.caibian.util.CreateBmpFactory;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.UIUtils;
/**
 * logo设置界面
 * @author howie
 *
 */
public class LogoSettingActivity extends BaseActivity implements OnClickListener {
	/**标题*/
	private TextView tv_titles;
	/***处理调用本地相机拍照和调取本地图片的工具类*/
	private CreateBmpFactory mCreateBmpFactory;
	/**返回按钮和加号按钮*/
	private View iv_back,iv_plus;
	private GridView gv;
	private ArrayList<LogoBean> mlist;
	private LogoAdapter logoAdapter;
	
	private ArrayList<String> logoList;
	private String picpath;
	public static File getStorage = Environment.getExternalStorageDirectory();
	public static String imagepath="/HB/images/";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logo_setting);
		findViews();
		
		logoList=getIntent().getStringArrayListExtra("logolist");
		mlist=new ArrayList<LogoBean>();
		mlist=initList(logoList);

		initView();
	}
	
	private void initView(){
		if(IsNonEmptyUtils.isList(mlist)){
			int i = mSharePreferenceUtils.getInt(UIUtils.getContext(), GlobalContants.LIVELOGOSETING, 0);
			mlist.get(i).setSelected(true);
			
			logoAdapter = new LogoAdapter(LogoSettingActivity.this,mlist);
			gv.setAdapter(logoAdapter);
			gv.setOnItemClickListener(new OnItemClickListener() {
				
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int position,
						long arg3) {
					// TODO Auto-generated method stub
					for (int i = 0; i < mlist.size(); i++) {
						if(position==i){
							mlist.get(i).setSelected(true);
							mSharePreferenceUtils.putInt(UIUtils.getContext(), GlobalContants.LIVELOGOSETING, position);
							//下载图片
							downloadimage(mlist.get(i).getLogopath());
						}else{
							mlist.get(i).setSelected(false);
						}
					}
					logoAdapter.notifyDataSetChanged();
				}
			});
		}
	}
	
	private void findViews() {
		tv_titles=(TextView) findViewById(R.id.tv_titles);
		tv_titles.setText("logo设置");
		iv_back=findViewById(R.id.iv_back);
		iv_plus=findViewById(R.id.iv_plus);
		iv_back.setOnClickListener(this);
		iv_plus.setOnClickListener(this);
		gv=(GridView) findViewById(R.id.gv);
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
				String filename =picpath.hashCode()
						+ "";
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
		for (int i = 0; i < list.size(); i++) {
			LogoBean logoBean=new LogoBean();
			logoBean.setSelected(false);
			logoBean.setLogopath(list.get(i));
			mlist.add(logoBean);
		}
		return mlist;
	}
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()){
		case R.id.iv_back://返回按钮
			finish();
			overridePendingTransition(R.anim.push_left_out,
					R.anim.push_right_out);
			break;
		case R.id.iv_plus://加号按钮
			mCreateBmpFactory=new CreateBmpFactory(LogoSettingActivity.this,this);
			mCreateBmpFactory.OpenGallery();//
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
