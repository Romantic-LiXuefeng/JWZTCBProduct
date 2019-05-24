package com.jwzt.caibian.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.jwzt.caibian.application.GlobalContants;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.adapter.QualityAdapter;
import com.jwzt.caibian.application.BaseActivity;
import com.jwzt.caibian.bean.NewBitrateBean;
import com.jwzt.caibian.bean.NewFramerateBean;
import com.jwzt.caibian.bean.NewResolutionBean;
import com.jwzt.caibian.bean.QualityBean;
import com.jwzt.caibian.util.UIUtils;
/**
 * 视频质量设置Activity
 * @author howie
 *
 */
public class VideoQualityActivity extends BaseActivity implements OnClickListener {
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
	private ArrayList<NewFramerateBean> newFramerateList;
	private ArrayList<NewResolutionBean> newResolutionList;
	private ArrayList<NewBitrateBean> newBitrateList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_quality);
		initList();
		findViews();
		framerateAdapter = new QualityAdapter(VideoQualityActivity.this,framerateList);
		resolutionAdapter=new QualityAdapter(VideoQualityActivity.this,resolutionList);
		bitrateAdapter=new QualityAdapter(VideoQualityActivity.this,bitrateList);
		gv_resolution.setAdapter(resolutionAdapter );
		
		gv_resolution.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int postion,
					long arg3) {
				// TODO Auto-generated method stub
				for (int i = 0; i < resolutionList.size(); i++) {
					if (i==postion) {
						resolutionList.get(i).setSelected(true);
						mSharePreferenceUtils.putInt(UIUtils.getContext(), GlobalContants.TARGETRESOLUTIONPOSION, postion);
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
						mSharePreferenceUtils.putInt(UIUtils.getContext(), GlobalContants.lIVEVIDEOFBSPOSION, position);
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
						mSharePreferenceUtils.putInt(UIUtils.getContext(), GlobalContants.VOICEBITRATEPOSION, position);
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
		int bitrate = mSharePreferenceUtils.getInt(UIUtils.getContext(), GlobalContants.VOICEBITRATEPOSION, 3);
		bitrateList.get(bitrate).setSelected(true);
		gv_resolution=(GridView) findViewById(R.id.gv_resolution);
		int resolution = mSharePreferenceUtils.getInt(UIUtils.getContext(), GlobalContants.TARGETRESOLUTIONPOSION, 2);
		resolutionList.get(resolution).setSelected(true);
		gv_framerate=(GridView) findViewById(R.id.gv_framerate);
		int framerate = mSharePreferenceUtils.getInt(UIUtils.getContext(), GlobalContants.lIVEVIDEOFBSPOSION, 0);
		framerateList.get(framerate).setSelected(true);
	}
	
	private void initList() {
		// TODO Auto-generated method stub
		newFramerateList=new ArrayList<NewFramerateBean>();
		framerateList=new ArrayList<QualityBean>();
		int[] frames = {20,25,30};
		for (int i = 0; i < frames.length; i++) {
			NewFramerateBean newFramerateBean = new NewFramerateBean();
			newFramerateList.add(newFramerateBean);
			newFramerateBean.videoFramera = frames[i];
			framerateList.add(new QualityBean(false,newFramerateBean.videoFramera + ""));
		}
		
		int[] newVideoWs = {352,512,640,768,1024,1280,1920};
		int[] newVideoHs = {288,288,480,432,576,720,1080};
		resolutionList=new ArrayList<QualityBean>();
		newResolutionList = new ArrayList<NewResolutionBean>();
		for (int i = 0; i < newVideoWs.length; i++) {
			NewResolutionBean newResolutionBean = new NewResolutionBean();
			newResolutionList.add(newResolutionBean);
			newResolutionBean.videoResolution_w = newVideoWs[i];
			newResolutionBean.videoResolution_h = newVideoHs[i];
			resolutionList.add(new QualityBean(false, 
					newResolutionBean.videoResolution_w + "*" + newResolutionBean.videoResolution_h));
		}
		int[] voiceBitrate = {500,800,1000,1200,1500,1800,2000};
		newBitrateList = new  ArrayList<NewBitrateBean>();
		bitrateList=new ArrayList<QualityBean>();
		for (int i = 0; i < voiceBitrate.length; i++) {
			NewBitrateBean newBitrateBean = new NewBitrateBean();
			newBitrateList.add(newBitrateBean);
			newBitrateBean.voiceBitrate = voiceBitrate[i];
			bitrateList.add(new QualityBean(false, newBitrateBean.voiceBitrate + "kbps"));
		}
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
	protected void onDestroy() {
		super.onDestroy();
		//这里存分辨率
		int TARGETRESOLUTIONPOSION = mSharePreferenceUtils.getInt(getApplicationContext(), GlobalContants.TARGETRESOLUTIONPOSION, 2);
			mSharePreferenceUtils.putInt(getApplicationContext(), GlobalContants.PREVIEWRESOLUTION_W,
					newResolutionList.get(TARGETRESOLUTIONPOSION).videoResolution_w);
			mSharePreferenceUtils.putInt(getApplicationContext(), GlobalContants.PREVIEWRESOLUTION_H,
					newResolutionList.get(TARGETRESOLUTIONPOSION).videoResolution_h);
		//这里存帧率
		int lIVEVIDEOFBSPOSION = mSharePreferenceUtils.getInt(UIUtils.getContext(), GlobalContants.lIVEVIDEOFBSPOSION, 0);
		mSharePreferenceUtils.putInt(UIUtils.getContext(), GlobalContants.lIVEVIDEOFBS, newFramerateList.get(lIVEVIDEOFBSPOSION).videoFramera);
			
		//这里存音频的码率
		int VOICEBITRATEPOSION = mSharePreferenceUtils.getInt(UIUtils.getContext(), GlobalContants.VOICEBITRATEPOSION, 3);
		mSharePreferenceUtils.putInt(UIUtils.getContext(), GlobalContants.VOICEBITRATE,newBitrateList.get(VOICEBITRATEPOSION).voiceBitrate);
	
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
