package com.jwzt.caibian.activity;

import java.util.List;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jwzt.caibian.view.PullToRefreshLayout;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.adapter.ChuanlianListAdapter;
import com.jwzt.caibian.application.BaseActivity;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.application.Configs;
import com.jwzt.caibian.bean.ChuanlIanListBean;
import com.jwzt.caibian.bean.LoginBean;
import com.jwzt.caibian.util.DynamicRequestPermissionUtils;
import com.jwzt.caibian.util.IsNonEmptyUtils;
/**
 * 直播列表界面
 * @author howie
 *
 */
public class ChuanLiandanActivity extends BaseActivity implements OnClickListener {
	/**标题*/
	private TextView tv_titles;  
	private ListView lv;
	private ImageView iv_back;
	private PullToRefreshLayout prl_refersh;
	/**右上角的加号按钮*/
	private View iv_plus;
	private ChuanlianListAdapter liveListAdapter;
	private LoginBean mLoginBean;
	private CbApplication application;
	private List<ChuanlIanListBean> mList;
	private List<ChuanlIanListBean> mListMore;
	private int pageSize=10;
	private int pageTag=0;//表示是刷新状态还是加载更多状态0表示刷新，1表示加载更多
	
	private Handler mHandler=new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				initView();
				break;
			case 2:
				int size=mList.size();
				mList.addAll(mListMore);
				liveListAdapter.setList(mList);
				liveListAdapter.notifyDataSetChanged();
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_live_list);
		application=(CbApplication) getApplication();
		mLoginBean=application.getmLoginBean();
		showLoadingDialog(ChuanLiandanActivity.this, "", "");
		DynamicRequestPermissionUtils.requestPermissionRECORDAUDIO(ChuanLiandanActivity.this, new String[]{Manifest.permission.RECORD_AUDIO},2);
		findViews();
		initData();
	}
	
	/***请求数据*/
	private void initData(){
		if(mLoginBean!=null){
			pageTag=0;
			String liveListUrl=String.format(Configs.getChuanliandandaishen, mLoginBean.getUserID());
			System.out.println("liveListUrl"+liveListUrl);
			RequestData(liveListUrl, Configs.liveBackListCode, 1);
		}
	}
	
	/**
	 * 加载更多
	 */
	private void initDataMore(){
		if(mLoginBean!=null){
			pageTag=1;
			if(IsNonEmptyUtils.isList(mList)){
				String liveListUrl=String.format(Configs.liveBackListUrl, mLoginBean.getUserID(),mList.get(mList.size()-1).getId(),pageSize);
				System.out.println("liveListUrl"+liveListUrl);
				RequestData(liveListUrl, Configs.liveBackListCode, 1);
			}
		}
	}

	private void initView(){
	/*	liveListAdapter = new ChuanlianListAdapter(lv,ChuanLiandanActivity.this,mList);
		lv.setAdapter(liveListAdapter);
		dismisLoadingDialog();*/
	}
	
	private void findViews() {
		tv_titles=(TextView) findViewById(R.id.tv_titles);
		tv_titles.setText("串联单");
		lv=(ListView) findViewById(R.id.lv);
		
		prl_refersh=(PullToRefreshLayout) findViewById(R.id.prl_refersh);
		prl_refersh.setOnRefreshListener(new MyListener());
		iv_back=(ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		iv_plus=findViewById(R.id.iv_plus);
		iv_plus.setOnClickListener(this);
		iv_plus.setVisibility(View.GONE);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
			   Intent  intent=new Intent(ChuanLiandanActivity.this,ChuanLianGaodanActivity.class);
			   intent.putExtra("id", mList.get(position).getId());
			   startActivity(intent);
			
			}
		});
	}
	
	class MyListener implements PullToRefreshLayout.OnRefreshListener {
		@SuppressLint("HandlerLeak")
		@Override
		public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
			initData();
			// 下拉刷新操作
			new Handler() {
				@Override
				public void handleMessage(Message msg) {
					// 千万别忘了告诉控件刷新完毕了哦！
					pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
				}
			}.sendEmptyMessageDelayed(0, 2000);
		}
		@SuppressLint("HandlerLeak")
		@Override
		public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
			initDataMore();
			// 加载操作
			new Handler() {
				@Override
				public void handleMessage(Message msg) {
					// 千万别忘了告诉控件加载完毕了哦！
					pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
				}
			}.sendEmptyMessageDelayed(0, 2000);//2秒之后触发viewPager的自动播放
		}
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.iv_back://返回按钮
			finish();
			overridePendingTransition(R.anim.push_left_out,R.anim.push_right_out);
			break;
		case R.id.iv_plus://加号按钮
			Intent intent_create=new Intent(ChuanLiandanActivity.this,CreateLiveActivity.class);
			startActivity(intent_create);
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			break;
		}
	}

	@Override
	protected void initRequestOnSuccess(String result, int code, int biaoshi) {
		if(code==Configs.liveBackListCode){
			if(pageTag==0){//刷新
				JSONObject jsonObject=JSON.parseObject(result);
				String status=jsonObject.getString("status");
				if(status.equals("100")){//表示获取成功
					String data=jsonObject.getString("data");
					mList=JSON.parseArray(data,ChuanlIanListBean.class);
					//遍历循环数据
				
					if(IsNonEmptyUtils.isList(mList)){
						mHandler.sendEmptyMessage(1);
					}else{
						mHandler.sendEmptyMessage(0);
					}
				}
			}else if(pageTag==1){//加载更多
				JSONObject jsonObject=JSON.parseObject(result);
				String status=jsonObject.getString("status");
				if(status.equals("100")){//表示获取成功
					String data=jsonObject.getString("data");
					mListMore=JSON.parseArray(data,ChuanlIanListBean.class);
					if(IsNonEmptyUtils.isList(mListMore)){
						mHandler.sendEmptyMessage(2);
					}else{
						mHandler.sendEmptyMessage(0);
					}
				}
			}
		}
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
