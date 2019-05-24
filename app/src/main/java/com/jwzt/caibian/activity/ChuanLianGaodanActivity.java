package com.jwzt.caibian.activity;

import java.util.List;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jwzt.caibian.view.PullToRefreshLayout;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.application.BaseActivity;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.application.Configs;
import com.jwzt.caibian.bean.LoginBean;
import com.jwzt.caibian.bean.TestGroupBean;
import com.jwzt.caibian.util.BitmapUtils;
import com.jwzt.caibian.util.DynamicRequestPermissionUtils;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.TimeUtil;
import com.jwzt.caibian.widget.DragSortListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
/**
 * 串联单稿单
 * @author howie
 *
 */
public class ChuanLianGaodanActivity extends BaseActivity implements OnClickListener {
	/**标题*/
	private TextView tv_titles;  
	private DragSortListView lv;
	private ImageView iv_back;
	private PullToRefreshLayout prl_refersh;
	/**右上角的加号按钮*/
	private View iv_plus;
	private MessageAdapter liveListAdapter;
	private LoginBean mLoginBean;
	private CbApplication application;
	private List<TestGroupBean> mList;
	private List<TestGroupBean> mListMore;
	private int pageSize=10;
	private int pageTag=0;//表示是刷新状态还是加载更多状态0表示刷新，1表示加载更多
	private String id;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private LinearLayout linelayout,pinglinline;
	private Handler mHandler=new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				initView();
				break;
			case 2:
				int size=mList.size();
				mList.addAll(mListMore);
				//liveListAdapter.setList(mList);
				liveListAdapter.notifyDataSetChanged();
				break;
			case 3:
				dismisLoadingDialog();
				Toast.makeText(ChuanLianGaodanActivity.this, "排序保存成功", 1).show();
				break;
			case 6:
				dismisLoadingDialog();
				Toast.makeText(ChuanLianGaodanActivity.this, "排序保存失败", 1).show();
				break;
			case 5:
				dismisLoadingDialog();
				Toast.makeText(ChuanLianGaodanActivity.this, "审核通过", 1).show();
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
		id=getIntent().getStringExtra("id");
		options = new DisplayImageOptions.Builder()  
        .showImageOnLoading(R.drawable.replace) // 设置图片下载期间显示的图片  
        .showImageForEmptyUri(R.drawable.replace) // 设置图片Uri为空或是错误的时候显示的图片  
        .showImageOnFail(R.drawable.replace) // 设置图片加载或解码过程中发生错误显示的图片  
        .cacheInMemory(true) // 设置下载的图片是否缓存在内存中  
        .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中 
        .bitmapConfig(Config.RGB_565)
     //.displayer(new FadeInBitmapDisplayer(100))
        .build(); // 构建完成  
	    imageLoader = ImageLoader.getInstance();
		showLoadingDialog(ChuanLianGaodanActivity.this, "", "");
		DynamicRequestPermissionUtils.requestPermissionRECORDAUDIO(ChuanLianGaodanActivity.this, new String[]{Manifest.permission.RECORD_AUDIO},2);
		findViews();
		initData();
	}
	
	/***请求数据*/
	private void initData(){
		if(mLoginBean!=null){
			pageTag=0;
			String liveListUrl=String.format(Configs.getChuanliandangao,id);
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
		liveListAdapter = new MessageAdapter(ChuanLianGaodanActivity.this,mList);
		lv.setAdapter(liveListAdapter);
		dismisLoadingDialog();
		lv.setDropListener(onDrop);
	}
	 private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
			@Override
			public void drop(int from, int to) {
				TestGroupBean  item = (TestGroupBean )liveListAdapter.getItem(from);//得到listview的适配器
				 liveListAdapter.remove(from);//在适配器中”原位置“的数据。
				 liveListAdapter.insert(item, to);//在目标位置中插入被拖动的控件。
				// listview.moveCheckState(from,to);
				//mList.set(to, mList.get(from));
				 liveListAdapter.notifyDataSetChanged();
				System.out.println("mlist"+mList);
			}
		};

		private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
			@Override
			public void remove(int which) {
				//videoFootageAdapter.remove(videoFootageAdapter.getItem(which));
				
			}
		};
	private void findViews() {
		tv_titles=(TextView) findViewById(R.id.tv_titles);
		tv_titles.setText("串联单列表");
		lv=(DragSortListView) findViewById(R.id.lv);
		//审核通过
		linelayout=(LinearLayout)this.findViewById(R.id.dianzanline);
		linelayout.setOnClickListener(this);
		//调整排序
		pinglinline=(LinearLayout)this.findViewById(R.id.pinglinline);
		pinglinline.setOnClickListener(this);
		
		/*prl_refersh=(PullToRefreshLayout) findViewById(R.id.prl_refersh);
		prl_refersh.setOnRefreshListener(new MyListener());*/
		iv_back=(ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		iv_plus=findViewById(R.id.iv_plus);
		iv_plus.setOnClickListener(this);
		iv_plus.setVisibility(View.GONE);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
			  /* Intent  intent=new Intent(ChuanLianGaodanActivity.this,ShowNewsDeatilsActivity.class);
			   intent.putExtra("ids", mList.get(position).getId());
			   intent.putExtra("title",  mList.get(position).getTitle());
			   intent.putExtra("createtime", mList.get(position).getCreateTime());
			   intent.putExtra("contents", mList.get(position).getContent());
				startActivity(intent);*/
			   
				Intent intent=new Intent(ChuanLianGaodanActivity.this,CLDShenheUploadedActivity.class);
				intent.putExtra("id", mList.get(position).getId());
				String status=mList.get(position).getStatus();
				intent.putExtra("operateType", mList.get(position).getStatus());
				ChuanLianGaodanActivity.this.startActivity(intent); 
			
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
			Intent intent_create=new Intent(ChuanLianGaodanActivity.this,CreateLiveActivity.class);
			startActivity(intent_create);
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			break;
		case R.id.dianzanline://审核通过
			showLoadingDialog(ChuanLianGaodanActivity.this, "", "");
			shenhetongguo();
			break;
		case R.id.pinglinline: //调整排序
			showLoadingDialog(ChuanLianGaodanActivity.this, "", "");
			paixu();
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
					    String sjjj=	jsonObject.getString("data");
						JSONObject jsont=JSON.parseObject(sjjj);

				     mList=JSON.parseArray(jsont.getString("contentNewsList"),TestGroupBean.class);
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
					mListMore=JSON.parseArray(data,TestGroupBean.class);
					if(IsNonEmptyUtils.isList(mListMore)){
						mHandler.sendEmptyMessage(2);
					}else{
						mHandler.sendEmptyMessage(0);
					}
				}
			}
		}else if(code==1000111){
			JSONObject jsonObject=JSON.parseObject(result);
			String status=jsonObject.getString("status");
			if(status.equals("100")){//表示获取成功
				mHandler.sendEmptyMessage(3);
			}else{
				mHandler.sendEmptyMessage(6);
			}
		}else if(code==1000112){
			JSONObject jsonObject=JSON.parseObject(result);
			String status=jsonObject.getString("status");
			if(status.equals("100")){//表示获取成功
				mHandler.sendEmptyMessage(5);
			}else{
				mHandler.sendEmptyMessage(6);
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
	
	public class MessageAdapter extends BaseAdapter {
		private Context mContext;
		private List<TestGroupBean> mList;
		
		public MessageAdapter(Context mContext,List<TestGroupBean> list) {
			super();
			this.mContext = mContext;
			this.mList=list;
		}

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup viewGroup) {
			ViewHolder holder = null;
			if(convertView==null){
				convertView = View.inflate(mContext, R.layout.daishen_item_layout,null);
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
		     	holder.tv_title.setText(mList.get(position).getTitle());
				holder.iv.setImageBitmap(BitmapUtils.readBitMap(mContext, R.drawable.shen));
				holder.tv_desc.setText(mList.get(position).getCreateTime());
				
				imageLoader.displayImage(mList.get(position).getNewsImage(), holder.iv, options);
				holder.tv_shenhes.setVisibility(View.GONE);
				String status=mList.get(position).getStatus();
				if(status.endsWith("0")){
					holder.tv_huifu.setText("待提交");
				}else if(status.endsWith("1")){
					holder.tv_huifu.setText("已提交");
				}else if(status.endsWith("2")){
					holder.tv_huifu.setText("一审");
				}else if(status.endsWith("3")){
					holder.tv_huifu.setText("二审");
				}else if(status.endsWith("4")){
					holder.tv_huifu.setText("退回");
				}
				
				holder.iv.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent=new Intent(ChuanLianGaodanActivity.this,CLDShenheUploadedActivity.class);
						intent.putExtra("id", mList.get(position).getId());
						String status=mList.get(position).getStatus();
						intent.putExtra("operateType", mList.get(position).getStatus());
						ChuanLianGaodanActivity.this.startActivity(intent); 
					}
				});
				holder.tv_title.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent=new Intent(ChuanLianGaodanActivity.this,CLDShenheUploadedActivity.class);
						intent.putExtra("id", mList.get(position).getId());
						String status=mList.get(position).getStatus();
						intent.putExtra("operateType", mList.get(position).getStatus());
						ChuanLianGaodanActivity.this.startActivity(intent); 
					}
				});
				
				holder.tv_time.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent=new Intent(ChuanLianGaodanActivity.this,CLDShenheUploadedActivity.class);
						intent.putExtra("id", mList.get(position).getId());
						String status=mList.get(position).getStatus();
						intent.putExtra("operateType", mList.get(position).getStatus());
						ChuanLianGaodanActivity.this.startActivity(intent); 
					}
				});
				holder.tv_huifu.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent=new Intent(ChuanLianGaodanActivity.this,CLDShenheUploadedActivity.class);
						intent.putExtra("id", mList.get(position).getId());
						String status=mList.get(position).getStatus();
						intent.putExtra("operateType", mList.get(position).getStatus());
						ChuanLianGaodanActivity.this.startActivity(intent); 
					}
				});
	           /*   
				
	            * 
	            * holder.tv_huifu.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
				  //显示pipuwindow
					Intent intent=new Intent(getActivity(),FeedBackActivity.class);
					//intent.putExtra("userid", mLoginBean.getUserID());
					intent.putExtra("messageid", mList.get(position).getId());
				    getActivity().startActivity(intent);
				}
			});*/
		
			holder.tv_time.setText(TimeUtil.getDateDiff(mList.get(position).getCreateTime()));
			return convertView;
		}
		 public void remove(int arg0) {//删除指定位置的item
		        mList.remove(arg0);
		        this.notifyDataSetChanged();//不要忘记更改适配器对象的数据源
		    }

		    public void insert(TestGroupBean item, int arg0) {//在指定位置插入item
		    	mList.add(arg0, item);
		        this.notifyDataSetChanged();
		    }
		
		
		public class ViewHolder{
			/**条目左侧的圆形图片*/
			ImageView iv;
			/**标题信息和描述信息、审核通过的时间*/
			TextView tv_title,tv_desc,tv_time,tv_huifu,tv_shenhes;
		
		}
		public void setList(List<TestGroupBean> list2) {
			this.mList=list2;
			notifyDataSetChanged();
		}

	}
	//串联单审核通过?userId=%s&programmeId=%s&status=1
	private void shenhetongguo(){
			String liveListUrl=String.format(Configs.chuanliadshenhetongguo,mLoginBean.getUserID(),id);
			System.out.println("liveListUrl"+liveListUrl);
			RequestData(liveListUrl,1000112, 1);
		
	}
	//串联单调整排序
		private void paixu(){
			StringBuffer buffer=new StringBuffer();
			    for(int i=0;i<mList.size();i++){
			    	buffer.append(mList.get(i).getId());
			    	buffer.append(",");
			    }
				String liveListUrl=String.format(Configs.chuanliandanpaixu,buffer.toString(),id,mLoginBean.getUserID());
				System.out.println("liveListUrl"+liveListUrl);
				RequestData(liveListUrl, 1000111, 1);
			
		}

}
