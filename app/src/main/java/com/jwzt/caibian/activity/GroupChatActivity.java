package com.jwzt.caibian.activity;

import java.util.Collections;
import java.util.List;

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

import com.jwzt.caibian.view.PullToRefreshLayout;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.adapter.GroupChatAdapter;
import com.jwzt.caibian.application.BaseActivity;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.bean.ChatMessageBean;
import com.jwzt.caibian.bean.GroupListBean;
import com.jwzt.caibian.bean.LoginBean;
import com.jwzt.caibian.db.ChatsDao;
/**
 * 群组聊天
 * @author howie
 *
 */
public class GroupChatActivity extends BaseActivity implements OnClickListener {
	private ImageView iv_back;
	private ListView lv;
	/**标题*/
	private TextView tv_titles;
	private PullToRefreshLayout prl_messagerefersh;
	private CbApplication application;
	private LoginBean mLoginBean;
	private ChatsDao chatsDao;
	private GroupChatAdapter mAdapter;
	private List<GroupListBean> mList;
	private List<ChatMessageBean> chatMessageList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activiity_group_chat);
		application=(CbApplication) getApplication();
		mLoginBean=application.getmLoginBean();
		mList=application.getGroupList();
		chatsDao=new ChatsDao(getHelper());
		findViews();
		initView();
	}
	
	private void initView(){
		Collections.reverse(mList);
		mAdapter=new GroupChatAdapter(GroupChatActivity.this,mList,chatsDao,mLoginBean.getUserID());
		lv.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
	}
	
	private void findViews() {
		tv_titles=(TextView) findViewById(R.id.tv_titles);
		tv_titles.setText("群组聊天");
		iv_back=(ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		prl_messagerefersh=(PullToRefreshLayout) findViewById(R.id.prl_messagerefersh);
		prl_messagerefersh.setOnRefreshListener(new MyListener());
		
		lv=(ListView) findViewById(R.id.lv);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(GroupChatActivity.this,ChatActivity.class);
				intent.putExtra("personNum", 5+"");
				intent.putExtra("groupListBean", mList.get(arg2));
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			}
		});
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
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()){
		case R.id.iv_back://返回按钮
			finish();
			overridePendingTransition(R.anim.push_left_out,R.anim.push_right_out);
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
