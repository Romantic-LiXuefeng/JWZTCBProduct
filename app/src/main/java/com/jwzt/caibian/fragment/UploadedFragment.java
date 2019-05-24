package com.jwzt.caibian.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jwzt.caibian.view.PullToRefreshLayout;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.adapter.UploadedsAdapter;
import com.jwzt.caibian.application.BaseFragment;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.application.Configs;
import com.jwzt.caibian.bean.LoginBean;
import com.jwzt.caibian.bean.TestGroupBean;
import com.jwzt.caibian.util.DialogHelp;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.UIUtils;
import com.jwzt.caibian.util.UserToast;

/**
 * 已上传
 * @author howie
 */
@SuppressLint("HandlerLeak")
public class UploadedFragment extends BaseFragment {
	private ListView lv;
	private UploadedsAdapter uploadedsAdapter;
	private List<TestGroupBean> mNewUploadinglist = new ArrayList<TestGroupBean>();
	private List<TestGroupBean> mNewUploadinglistMore;
	private CbApplication application;
	private LoginBean mLoginBean;
	private PullToRefreshLayout prl_uploadrefersh;
	private View view;

	private int pageSize = 10;// 表示每页一次加载的条目数
	private int pageTag = 0;// 表示是刷新状态还是加载更多状态0表示刷新，1表示加载更多

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				DialogHelp.dismisLoadingDialog();
				UIUtils.showToast("没有请求到数据");
				break;
			case 1:
				rl_show_pic.setVisibility(View.GONE);
				lv.setVisibility(View.VISIBLE);
				showUploadingList();
				break;
			case 2:
				rl_show_pic.setVisibility(View.GONE);
				lv.setVisibility(View.VISIBLE);
				int size = mNewUploadinglist.size();
				mNewUploadinglist.addAll(mNewUploadinglistMore);
				uploadedsAdapter.setList(mNewUploadinglist);
				lv.setSelection(size);
				DialogHelp.dismisLoadingDialog();
				break;
			}
		}

	};
	private View rl_show_pic;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = View.inflate(getActivity(), R.layout.upload_layout, null);
		application = (CbApplication) getActivity().getApplication();
		mLoginBean = application.getmLoginBean();
		findView();
		if (mNewUploadinglist != null && mNewUploadinglist.size() > 0) {

		} else {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					initData();
				}
			}, 1000);
		}
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	/**
	 * 实例化控件
	 */
	private void findView() {
		prl_uploadrefersh = (PullToRefreshLayout) view.findViewById(R.id.prl_uploadrefersh);
		prl_uploadrefersh.setOnRefreshListener(new MyListener());
		lv = (ListView) view.findViewById(R.id.lv);
		rl_show_pic = view.findViewById(R.id.rl_show_pic);
		lv = (ListView) view.findViewById(R.id.lv);
		rl_show_pic.setVisibility(View.VISIBLE);
		lv.setVisibility(View.GONE);
	}

	/**
	 * 请求网络数据
	 */
	private void initData(){
		if(mLoginBean!=null){
			pageTag=0;
			if (CbApplication.getNetType() != -1) {
				String manuscriptUrl=String.format(Configs.manuscriptUrl, mLoginBean.getUserID(),"",pageSize);
				System.out.println("manuscriptUrl:"+manuscriptUrl);
				RequestData(manuscriptUrl, Configs.manuscriptCode, pageTag);
			}

		}
	}

	private void moreData() {
		if (mLoginBean != null) {
			if (IsNonEmptyUtils.isList(mNewUploadinglist)) {
				pageTag = 1;
				if (CbApplication.getNetType() != -1) {
					DialogHelp.showLoadingDialog(getActivity(), "", "");
					String manuscriptUrl=String.format(Configs.manuscriptUrlmore, mLoginBean.getUserID(),mNewUploadinglist.get(mNewUploadinglist.size()-1).getId());
					System.out.println("manuscriptUrl:"+manuscriptUrl);
					RequestData(manuscriptUrl, Configs.manuscriptCode, pageTag);
				}else {
					UserToast.toSetToast(UIUtils.getContext(), getString(R.string.please_check_net));
				}
			}
		}
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
					pullToRefreshLayout
							.refreshFinish(PullToRefreshLayout.SUCCEED);
				}
			}.sendEmptyMessageDelayed(0, 2000);
		}

		@SuppressLint("HandlerLeak")
		@Override
		public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
			moreData();
			// 加载操作
			new Handler() {
				@Override
				public void handleMessage(Message msg) {
					// 千万别忘了告诉控件加载完毕了哦！
					pullToRefreshLayout
							.loadmoreFinish(PullToRefreshLayout.SUCCEED);
				}
			}.sendEmptyMessageDelayed(0, 2000);// 2秒之后触发viewPager的自动播放
		}
	}

	/**
	 * 请求的数据显示到列表上
	 */
	private void showUploadingList() {
		uploadedsAdapter = new UploadedsAdapter(getActivity(),
				mNewUploadinglist);
		// uploadedsAdapter.setmListener(UploadedFragment.this);
		/*
		 * animateDismissAdapter = new
		 * AnimateDismissAdapter<NewUploadinglistBean>(uploadedsAdapter, new
		 * MyOnDismissCallback()); animateDismissAdapter.setListView(lv);
		 */
		lv.setAdapter(uploadedsAdapter);
		// DialogHelp.dismisLoadingDialog();
	}

	@Override
	protected void initRequestOnSuccess(String result, int code, int biaoshi) {
		System.out.println("resultresultresult" + result);
		initDataParse(result, code);
	}

	@Override
	protected void initRequestOnStart(String result, int code, int biaoshi) {
		System.out.println("resultresultresult" + result);
	}

	@Override
	protected void initRequestOnFailure(String failure, int code, int biaoshi) {
		System.out.println("resultresultresult" + failure);
	}

	@Override
	protected void initRequestOnCache(String result, int code, int biaoshi) {
		System.out.println("resultresultresult" + result);
		// initDataParse(result, code);
	}

	private void initDataParse(String result, int code) {
		if (code == Configs.manuscriptCode) {
			if (pageTag == 0) {// 刷新
				JSONObject jsonObject = JSON.parseObject(result);
				String status = jsonObject.getString("status");
				if (status.equals("100")) {// 表示成功
					String dataresult = jsonObject.getString("data");
					mNewUploadinglist = JSON.parseArray(dataresult,
							TestGroupBean.class);
					if (IsNonEmptyUtils.isList(mNewUploadinglist)) {
						mHandler.sendEmptyMessage(1);
					} else {
						mHandler.sendEmptyMessage(0);
					}
				}

			} else if (pageTag == 1) {// 加载更多
				JSONObject jsonObject = JSON.parseObject(result);
				String status = jsonObject.getString("status");
				if (status.equals("100")) {// 表示成功
					String dataresult = jsonObject.getString("data");
					mNewUploadinglistMore = JSON.parseArray(dataresult,
							TestGroupBean.class);
					if (IsNonEmptyUtils.isList(mNewUploadinglistMore)) {
						mHandler.sendEmptyMessage(2);
					} else {
						mHandler.sendEmptyMessage(0);
					}
				}
			}
		}
	}
}
