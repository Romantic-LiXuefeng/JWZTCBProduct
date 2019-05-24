package com.jwzt.caibian.activity;

import java.util.ArrayList;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.adapter.QualityAdapter;
import com.jwzt.caibian.application.BaseActivity;
import com.jwzt.caibian.bean.QualityBean;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
/**
 * 视频质量设置Activity
 * @author howie
 *
 */
public class RecordVideoQualityActivity extends BaseActivity implements OnClickListener {
	/**标题*/
	private TextView tv_titles;
	/***分辨率/帧率、码率所在的gridview*/
	private GridView gv_resolution,gv_framerate,gv_bitrate;
	/**视频帧率的集合*/
	private ArrayList<QualityBean> framerateList;
	/**分辨率信息的集合*/
	private ArrayList<QualityBean> resolutionList;
	/**码率的集合*/
	private ArrayList<QualityBean> bitrateList;
	/**帧率的适配器*/
	private QualityAdapter framerateAdapter;
	/**视频分辨率的适配器*/
	private QualityAdapter resolutionAdapter;
	/**码率的适配器*/
	private QualityAdapter bitrateAdapter;
	/**返回按钮*/
	private View iv_back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_quality);
		initList();
		findViews();
		framerateAdapter = new QualityAdapter(RecordVideoQualityActivity.this,framerateList);
		resolutionAdapter=new QualityAdapter(RecordVideoQualityActivity.this,resolutionList);
		bitrateAdapter=new QualityAdapter(RecordVideoQualityActivity.this,bitrateList);
		gv_resolution.setAdapter(resolutionAdapter );
		
		gv_resolution.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int postion,
					long arg3) {
				// TODO Auto-generated method stub
				for (int i = 0; i < resolutionList.size(); i++) {
					if (i==postion) {
						resolutionList.get(i).setSelected(true);
					}else{
						resolutionList.get(i).setSelected(false);
					}
				}
				resolutionAdapter.notifyDataSetChanged();
				
			}
			
		});
		gv_framerate.setAdapter(framerateAdapter);
		gv_framerate.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				for (int i = 0; i < framerateList.size(); i++) {
					if (i==position) {
						framerateList.get(i).setSelected(true);
					}else{
						framerateList.get(i).setSelected(false);
					}
				}
				framerateAdapter.notifyDataSetChanged();
			}
		});
		gv_bitrate.setAdapter(bitrateAdapter);
		gv_bitrate.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				for (int i = 0; i < bitrateList.size(); i++) {
					if (i==position) {
						bitrateList.get(i).setSelected(true);
					}else{
						bitrateList.get(i).setSelected(false);
					}
				}
				bitrateAdapter.notifyDataSetChanged();
			}
		});
	}

	private void findViews() {
		tv_titles=(TextView) findViewById(R.id.tv_titles);
		tv_titles.setText("视频质量");
		iv_back=findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		gv_bitrate=(GridView) findViewById(R.id.gv_bitrate);
		gv_resolution=(GridView) findViewById(R.id.gv_resolution);
		gv_framerate=(GridView) findViewById(R.id.gv_framerate);
	}
	
	private void initList() {
		// TODO Auto-generated method stub
		framerateList=new ArrayList<QualityBean>();
		framerateList.add(new QualityBean(true,"20"));
		framerateList.add(new QualityBean(false,"25"));
		framerateList.add(new QualityBean(false,"30"));
		
		resolutionList=new ArrayList<QualityBean>();
		resolutionList.add(new QualityBean(true, "352*288"));
		resolutionList.add(new QualityBean(false, "512*288"));
		resolutionList.add(new QualityBean(false, "640*480"));
		resolutionList.add(new QualityBean(false, "768*432"));
		resolutionList.add(new QualityBean(false, "1024*576"));
		resolutionList.add(new QualityBean(false, "1280*720"));
		resolutionList.add(new QualityBean(false, "1920*1080"));
		
		bitrateList=new ArrayList<QualityBean>();
		bitrateList.add(new QualityBean(true, "500kbps"));
		bitrateList.add(new QualityBean(false, "800kbps"));
		bitrateList.add(new QualityBean(false, "1000kbps"));
		bitrateList.add(new QualityBean(false, "1200kbps"));
		bitrateList.add(new QualityBean(false, "1500kbps"));
		bitrateList.add(new QualityBean(false, "1800kbps"));
		bitrateList.add(new QualityBean(false, "2000kbps"));
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
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
