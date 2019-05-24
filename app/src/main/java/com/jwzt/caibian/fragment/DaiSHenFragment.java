package com.jwzt.caibian.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jwzt.caibian.adapter.ShenheAdapter;
import com.jwzt.caibian.view.PullToRefreshLayout;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.activity.PreviewUploadedActivity;
import com.jwzt.caibian.application.BaseFragment;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.application.Configs;
import com.jwzt.caibian.bean.LoginBean;
import com.jwzt.caibian.bean.TestGroupBean;
import com.jwzt.caibian.util.BitmapUtils;
import com.jwzt.caibian.util.DialogHelp;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.TimeUtil;
import com.jwzt.caibian.util.UIUtils;
import com.jwzt.caibian.util.UserToast;
import com.jwzt.caibian.widget.MyListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 待审状态列表
 * 
 * @author howie
 * 
 */
public class DaiSHenFragment extends BaseFragment {
	
	private CbApplication app;
	private LoginBean loginBean;
    private String userId;
    private MyListView listview;
    private int pageTag;
    private List<TestGroupBean> mList;
    private List<TestGroupBean> mListMore;
//    private MessageAdapter messageAdapter;
    private ShenheAdapter messageAdapter;
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private PullToRefreshLayout prl_refersh;
	private Handler mHandler = new Handler() {
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
			case 0:
				DialogHelp.dismisLoadingDialog();
				break;
			case 1:
				messageAdapter = new ShenheAdapter(getActivity(),mList,"1");
				listview.setAdapter(messageAdapter);
				break;
			case 2:
				int size = mList.size();
				mList.addAll(mListMore);
				mListMore.clear();
				messageAdapter.setList(mList);
				listview.setSelection(size);
				DialogHelp.dismisLoadingDialog();
				break;
			default:
				break;
			}

		};

	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {	
		View view = View.inflate(getActivity(), R.layout.daishen_layout,null);
		app=(CbApplication) getActivity().getApplication();
		loginBean=app.getmLoginBean();
		userId=loginBean.getUserID();
		listview=(MyListView) view.findViewById(R.id.mlv);
		 prl_refersh=(PullToRefreshLayout) view.findViewById(R.id.prl_refersh);
			prl_refersh.setOnRefreshListener(new MyListener());
		mList=new ArrayList<TestGroupBean>();
		mListMore=new ArrayList<TestGroupBean>();
		options = new DisplayImageOptions.Builder()  
	        .showImageOnLoading(R.drawable.replace) // 设置图片下载期间显示的图片  
	        .showImageForEmptyUri(R.drawable.replace) // 设置图片Uri为空或是错误的时候显示的图片  
	        .showImageOnFail(R.drawable.replace) // 设置图片加载或解码过程中发生错误显示的图片  
	        .cacheInMemory(false) // 设置下载的图片是否缓存在内存中  
	        .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中 
	        .bitmapConfig(Config.RGB_565)
         //.displayer(new FadeInBitmapDisplayer(100))
	        .build(); // 构建完成  
		  imageLoader = ImageLoader.getInstance();
		  listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				Intent intent=new Intent(getActivity(),PreviewUploadedActivity.class);
				intent.putExtra("id", mList.get(position).getId());
				String status=mList.get(position).getStatus();
				intent.putExtra("operateType", mList.get(position).getStatus());
				getActivity().startActivity(intent);
			}
		});
	    //获取未审核数据
	    initdata();
	   
		return view;
	}

	private void initdata() {
		//userId=%s&type=%s&startId=%s";
		pageTag=0;
		String infoListUrl=String.format(Configs.daishenheListUrl, loginBean.getUserID(),"1","");
		System.out.println("infoListUrl:"+infoListUrl);
		RequestData(infoListUrl, Configs.infoListCode, pageTag);
	
		
	}
	class MyListener implements PullToRefreshLayout.OnRefreshListener {
		@SuppressLint("HandlerLeak")
		@Override
		public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
			initdata();
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
			moreData();
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
	
	private void moreData() {
		if (loginBean != null) {
			if (IsNonEmptyUtils.isList(mList)) {
				pageTag = 1;
				if (CbApplication.getNetType() != -1) {
					DialogHelp.showLoadingDialog(getActivity(), "", "");
					String infoListUrl=String.format(Configs.daishenheListUrl, loginBean.getUserID(),"1",mList.get(mList.size()-1).getId());;

					System.out.println("manuscriptUrl:"+infoListUrl);
					RequestData(infoListUrl, Configs.infoListCode, pageTag);
				}else {
					UserToast.toSetToast(UIUtils.getContext(), getString(R.string.please_check_net));
				}
			}
		}
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
//		initdata();
	}

	@Override
	protected void initRequestOnSuccess(String result, int code, int biaoshi) {
		initDataParse(result,code);
		
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

	private void initDataParse(String result, int code){
		if(code==Configs.infoListCode){
			if(pageTag==0){//刷新
				if(!result.equals("")){//表示获取成功

					    JSONObject jsonObject = JSON.parseObject(result);
					    if(mList.size()>0){
					    	mList.clear();	
					    }
				        mList=JSON.parseArray(jsonObject.getString("data"),TestGroupBean.class);
					if(IsNonEmptyUtils.isList(mList)){
						mHandler.sendEmptyMessage(1);
					}else{
						mHandler.sendEmptyMessage(0);
					}
				}else{
					mHandler.sendEmptyMessage(0);	
				}
			}else if(pageTag==1){//加载更多
				JSONObject jsonObject=JSON.parseObject(result);
				String data=jsonObject.getString("data");
				if(!data.equals("")){//表示获取成功
					/*JSONObject jsonObject2=JSONObject.parseObject(data);
					String list=jsonObject2.getString("list");*/
					mListMore=JSON.parseArray(data,TestGroupBean.class);
					if(IsNonEmptyUtils.isList(mListMore)){
						mHandler.sendEmptyMessage(2);
					}else{
						mHandler.sendEmptyMessage(0);
					}
				}else{
					mHandler.sendEmptyMessage(0);	
				}
			}
		}
	}
	
	public class MessageAdapter extends BaseAdapter {
		private Context mContext;
		private List<TestGroupBean> mLists;
		
		public MessageAdapter(Context mContext,List<TestGroupBean> list) {
			super();
			this.mContext = mContext;
			this.mLists=list;
		}

		@Override
		public int getCount() {
			return mLists.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mLists.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup viewGroup) {
			ViewHolder holder = null;
			if(convertView==null){
				convertView = View.inflate(mContext, R.layout.gaojiadaishen_item_layout,null);
				holder = new ViewHolder();
				holder.iv=(ImageView) convertView.findViewById(R.id.iv);
				holder.tv_title=(TextView) convertView.findViewById(R.id.tv_title);
				holder.tv_desc=(TextView) convertView.findViewById(R.id.tv_desc);
				holder.tv_time=(TextView) convertView.findViewById(R.id.tv_time);
				holder.tv_huifu=(TextView) convertView.findViewById(R.id.tv_huifu);
				holder.tv_shenhes=(TextView) convertView.findViewById(R.id.tv_shenhes);
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder) convertView.getTag();
			}
		     	holder.tv_title.setText(mLists.get(position).getTitle());
				holder.iv.setImageBitmap(BitmapUtils.readBitMap(mContext, R.drawable.shen));
				holder.tv_desc.setText(mLists.get(position).getCreateTime());
				holder.tv_shenhes.setText("审核");
				imageLoader.displayImage(mLists.get(position).getNewsImage(), holder.iv, options);
				String status=mList.get(position).getStatus();
				if(status.endsWith("0")){
					holder.tv_huifu.setText("待审核");
				}else if(status.endsWith("1")){
					holder.tv_huifu.setText("待审核");
				}else if(status.endsWith("2")){
					holder.tv_huifu.setText("一审");
				}else if(status.endsWith("3")){
					holder.tv_huifu.setText("二审");
				}else if(status.endsWith("4")){
					holder.tv_huifu.setText("退回");
				}

		
			holder.tv_time.setText(TimeUtil.getDateDiff(mLists.get(position).getCreateTime()));
			return convertView;
		}
		
		
		public class ViewHolder{
			/**条目左侧的圆形图片*/
			ImageView iv;
			/**标题信息和描述信息、审核通过的时间*/
			TextView tv_title,tv_desc,tv_time,tv_huifu,tv_shenhes;
		
		}
		public void setList(List<TestGroupBean> list2) {
			this.mLists=list2;
			notifyDataSetChanged();
		}

	}

	
}
