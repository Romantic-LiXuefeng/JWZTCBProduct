package com.jwzt.caibian.activity;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.adapter.TaskAdapter2;
import com.jwzt.caibian.application.BaseActivity;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.application.Configs;
import com.jwzt.caibian.bean.LoginBean;
import com.jwzt.caibian.bean.TaskListBean;
import com.jwzt.caibian.bean.TypeBean;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.UIUtils;
import com.jwzt.caibian.util.UserToast;
/**
 * 任务列表
 * @author howie
 *
 */
public class TaskActivity extends BaseActivity implements OnClickListener {
	
	/**动画时长*/
	private static final int DURATION=300;
	/**收起动画是否结束了*/
	private boolean isHideFinished=true;
	/**下拉选择列表是否正在显示*/
	private boolean isShowing;
	private ListView lv;
	private View iv_arrow;
	private View view_mask;
	private View view_pop;
	private TextView tv1,tv3;
	private TextView tv_titles;
	private View iv_back;
	
	private CbApplication application;
	private LoginBean mLoginBean;
	private List<TaskListBean> mList=new ArrayList<>();
	private TaskAdapter2 mAdapter;
	private String status;//任务状态1：进行中 2：已结束
	
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				initView();
				dismisLoadingDialog();
				break;
			case 1:
				initView();
				break;
			}
		};
	};
	private ImageView rl_renwu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_layout);
		application=(CbApplication) getApplication();
		mLoginBean=application.getmLoginBean();
		findViews();
		
		initData();
	}
	
	/**
	 * 请求网络数据
	 */
	private void initData(){
		if(mLoginBean!=null){
			if (CbApplication.getNetType() !=-1) {
				showLoadingDialog(TaskActivity.this, "", "");
				String taskUrl=String.format(Configs.taskListUrl, mLoginBean.getUserID());
				System.out.println("taskListUrl:"+taskUrl);
				RequestData(taskUrl, Configs.taskListCode, -1);
			}else {
				UserToast.toSetToast(UIUtils.getContext(), getString(R.string.please_check_net));
			}
			
		}
	}
	
	/**
	 * 根据不同的任务状态获取数据
	 */
	private void initDataStatus(){
		if(mLoginBean!=null){
			if (CbApplication.getNetType() != -1) {
				showLoadingDialog(TaskActivity.this, "", "");
				String taskUrl=String.format(Configs.taskListStatusUrl, mLoginBean.getUserID(),status);
				System.out.println("taskListUrl:"+taskUrl);
				RequestData(taskUrl, Configs.taskListCode, -1);
			}else {
				UserToast.toSetToast(UIUtils.getContext(), getString(R.string.please_check_net));
			}
			
		}
	}
	
	/**
	 * 初始化控件
	 */
	private void initView(){
		if(mList.size()<=0){
			rl_renwu.setVisibility(View.VISIBLE);
		}else{
			rl_renwu.setVisibility(View.GONE);
		}
		mAdapter=new TaskAdapter2(TaskActivity.this,mList);
		lv.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
		dismisLoadingDialog();
	}
	
	private void findViews() {
		rl_renwu = findViewById(R.id.rl_renwu);
		iv_back=findViewById(R.id.ll_back);
		iv_back.setOnClickListener(this);
		tv_titles=(TextView) findViewById(R.id.tv_titles);
		tv_titles.setOnClickListener(this);
		iv_arrow=findViewById(R.id.iv_arrow);
		iv_arrow.setOnClickListener(this);
		lv=(ListView) findViewById(R.id.lv);
		view_pop=findViewById(R.id.view_pop);
		view_mask=findViewById(R.id.view_mask);
		view_mask.setOnClickListener(this);
		tv1=(TextView) findViewById(R.id.tv1);
		//tv2=(TextView) findViewById(R.id.tv2);
		tv3=(TextView) findViewById(R.id.tv3);
		tv1.setOnClickListener(this);
		//tv2.setOnClickListener(this);
		tv3.setOnClickListener(this);
//		rv.setLayoutManager(new LinearLayoutManager(TaskActivity.this, LinearLayoutManager.VERTICAL, false));
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
				Intent intent=new Intent(TaskActivity.this,TaskDetailActivity.class);
				intent.putExtra("id", mList.get(position).getId());
				intent.putExtra("TaskStatus", mList.get(position).getTaskStatus());
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			}
		});
		
	}
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()){
		case R.id.iv_arrow://箭头按钮
			if(isShowing){
				hide();
			}else{
				show();
			}
			break;
		case R.id.tv_titles:
			
			if(isShowing){
				hide();
			}else{
				show();
			}
			break;
		case R.id.ll_back://返回按钮
			finish();
			overridePendingTransition(R.anim.push_left_out,R.anim.push_right_out);
			break;
		case R.id.tv1://进行中
			status="1";
			tv1.setTextColor(getResources().getColor(R.color.blue3));
			//tv2.setTextColor(getResources().getColor(R.color.grey3));
			tv3.setTextColor(getResources().getColor(R.color.grey3));
			hide();
			initDataStatus();
			break;
		case R.id.tv2://未开始
			tv1.setTextColor(getResources().getColor(R.color.grey3));
			//tv2.setTextColor(getResources().getColor(R.color.blue3));
			tv3.setTextColor(getResources().getColor(R.color.grey3));
			hide();
			break;
		case R.id.tv3://已结束
			status="2";
			tv1.setTextColor(getResources().getColor(R.color.grey3));
			//tv2.setTextColor(getResources().getColor(R.color.grey3));
			tv3.setTextColor(getResources().getColor(R.color.blue3));
			hide();
			initDataStatus();
			break;
		case R.id.view_mask://灰色蒙版区域
			if(isHideFinished&&isShowing){
				hide();
			}
			break;
			
		}
		
	}
	/**
	 * 显示下拉菜单
	 */
	private void show(){
		tv1.setVisibility(View.VISIBLE);
		//tv2.setVisibility(View.VISIBLE);
		tv3.setVisibility(View.VISIBLE);
		view_mask.setVisibility(View.VISIBLE);
		view_pop.setVisibility(View.VISIBLE);
		
		ScaleAnimation sa=new ScaleAnimation(1, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0);
		sa.setDuration(DURATION);
		sa.setFillAfter(true);

		ObjectAnimator oa = ObjectAnimator.ofFloat(view_mask, "alpha", 0,1);
		oa.setDuration(DURATION);
		oa.start();
		
		view_pop.startAnimation(sa);
		RotateAnimation ra=new RotateAnimation(0, 180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		
		ra.setDuration(DURATION);
		ra.setFillAfter(true);
		iv_arrow.startAnimation(ra);
		isShowing=true;
	}
	/**
	 * 隐藏下拉菜单
	 */
	private void hide(){
		ScaleAnimation sa=new ScaleAnimation(1, 1, 1, 0, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0);
		sa.setDuration(DURATION);
		sa.setFillAfter(true);

		ObjectAnimator oa = ObjectAnimator.ofFloat(view_mask, "alpha", 1,0);
		oa.setDuration(DURATION);
		oa.start();
		
		view_pop.startAnimation(sa);
		RotateAnimation ra=new RotateAnimation(180,360 , RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		
		ra.setDuration(DURATION);
		ra.setFillAfter(true);
		iv_arrow.startAnimation(ra);
		oa.addListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator arg0) {
				// TODO Auto-generated method stub
				isHideFinished=false;
			}
			
			@Override
			public void onAnimationRepeat(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animator arg0) {
				// TODO Auto-generated method stub
				isHideFinished=true;
				view_mask.setVisibility(View.GONE);
				view_pop.setVisibility(View.GONE);
				tv1.setVisibility(View.GONE);
				//tv2.setVisibility(View.GONE);
				tv3.setVisibility(View.GONE);
			}
			
			@Override
			public void onAnimationCancel(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	
		
		isShowing=false;
	}
	@Override
	protected void initRequestOnSuccess(String result, int code, int biaoshi) {
		// TODO Auto-generated method stub
		initDataParse(result, code);
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

		initDataParse(result, code);
	}

	private void initDataParse(String result, int code){
		if(code==Configs.taskListCode){
			JSONObject jsonObject=JSON.parseObject(result);
			String status=jsonObject.getString("status");
			if(status.equals("100")){//表示获取成功
				String data=jsonObject.getString("data");
				mList=JSON.parseArray(data,TaskListBean.class);
				if(IsNonEmptyUtils.isList(mList)){
					mHandler.sendEmptyMessage(1);
				}else{
					mHandler.sendEmptyMessage(0);
				}
			}
		}
	}

}
