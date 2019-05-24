package com.jwzt.caibian.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jwzt.caibian.view.PullToRefreshLayout;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.adapter.OnDismissCallback;
import com.jwzt.caibian.adapter.UploadsAdapter;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.application.Configs;
import com.jwzt.caibian.bean.LocationNoUploadBean;
import com.jwzt.caibian.bean.LoginBean;
import com.jwzt.caibian.db.DatabaseHelper;
import com.jwzt.caibian.db.LocationUploadDao;
import com.jwzt.caibian.interfaces.RemoveIndexListener;
import com.jwzt.caibian.util.IsNonEmptyUtils;
/**
 * 未上传
 * @author howie
 *
 */
@SuppressLint("ValidFragment")
public class UploadingFragment extends Fragment implements RemoveIndexListener{
	private View view;
	private ListView lv;
	private UploadsAdapter uploadsAdapter;
	//获取的数据库操作对象
	private LocationUploadDao noUpLoadtask;
	private List<LocationNoUploadBean> mList;
	private View rl_show_pic;
	private PullToRefreshLayout prl_uploadrefersh;
	private DatabaseHelper mDatabaseHelper;
	private CbApplication application;
	private LoginBean mLoginBean;
	
	private Handler mHandler=new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				initRefresh();
				if(uploadsAdapter!=null){
					uploadsAdapter.notifyDataSetChanged();
				}
				break;
			}
		};
	};
	@SuppressLint("ValidFragment")
	public UploadingFragment(DatabaseHelper databaseHelper) {
		this.mDatabaseHelper=databaseHelper;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view=View.inflate(getActivity(), R.layout.upload_layout, null);
		mList=new ArrayList<LocationNoUploadBean>();
		application=(CbApplication) getActivity().getApplication();
		mLoginBean=application.getmLoginBean();
		findView();
//		initView();
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		initView();
		if(Configs.isUpload){//标识未上传的已上传
			Configs.isUpload=false;
			initRefresh();
			if(uploadsAdapter!=null){
				uploadsAdapter.notifyDataSetChanged();
			}
		}
	}

	private void initView(){
		noUpLoadtask=new LocationUploadDao(mDatabaseHelper);
		mList=noUpLoadtask.getTasks(new Integer(mLoginBean.getUserID()));
		
		if(IsNonEmptyUtils.isList(mList)){
			rl_show_pic.setVisibility(View.GONE);
			lv.setVisibility(View.VISIBLE);
			//prl_uploadrefersh.setVisibility(View.VISIBLE);
			if(uploadsAdapter==null){
				uploadsAdapter = new UploadsAdapter(getActivity(),mList);
				uploadsAdapter.setmListener(UploadingFragment.this);
				//uploadsAdapter.setmListener(UploadingFragment.this);
				/*animateDismissAdapter = new AnimateDismissAdapter<LocationNoUploadBean>(uploadsAdapter, new MyOnDismissCallback());
				animateDismissAdapter.setListView(lv);*/
				lv.setAdapter(uploadsAdapter);
			}else{
				uploadsAdapter.setList(mList);
			}

		}else{
			rl_show_pic.setVisibility(View.VISIBLE);
//			prl_uploadrefersh.setVisibility(View.GONE);
			lv.setVisibility(View.GONE);
		}
	}
	
	private void initRefresh(){
		if(noUpLoadtask!=null){
			mList=noUpLoadtask.getTasks(new Integer(mLoginBean.getUserID()));
			if(uploadsAdapter!=null){
				uploadsAdapter.setList(mList);
			}else{
				uploadsAdapter = new UploadsAdapter(getActivity(),mList);
				lv.setAdapter(uploadsAdapter);
			}
		}
	}
	
	/**
	 * 实例化控件
	 */
	private void findView(){
		rl_show_pic = view.findViewById(R.id.rl_show_pic);
		prl_uploadrefersh = (PullToRefreshLayout) view.findViewById(R.id.prl_uploadrefersh);
		prl_uploadrefersh.setOnRefreshListener(new MyListener());
		lv=(ListView) view.findViewById(R.id.lv);
	}
	
	class MyListener implements PullToRefreshLayout.OnRefreshListener {
		@SuppressLint("HandlerLeak")
		@Override
		public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
			initView();
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
//			moreData();
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
	
	private class MyOnDismissCallback implements OnDismissCallback {

		@Override
		public void onDismiss(ListView listView, int[] reverseSortedPositions) {
//			for (int position : reverseSortedPositions) {
//				mAdapter.remove(position);
//			}
		}
	}
	/*@Override
	public void remove(int index) {
		// TODO Auto-generated method stub
		ArrayList<Integer> list=new ArrayList<Integer>();
		list.add(index);
		animateDismissAdapter.animateDismiss(list);
		if(uploadsAdapter.mSml!=null){
			uploadsAdapter.mSml.quickClose();
		}
		
	}*/

	@Override
	public void remove(int index) {
		// TODO Auto-generated method stub
		if(noUpLoadtask!=null){
			noUpLoadtask.deletePerson(mList.get(index));
			mHandler.sendEmptyMessageDelayed(1, 1000);
		}
	}
}
