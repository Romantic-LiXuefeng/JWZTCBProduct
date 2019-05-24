package com.jwzt.caibian.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.adapter.MembersAdapter;
import com.jwzt.caibian.application.BaseActivity;
import com.jwzt.caibian.bean.TaskListUserListBean;
/**
 * 任务群组
 * @author howie
 *
 */
public class TaskGroupActivity extends BaseActivity implements OnClickListener {
	private GridView gv;
	private View iv_back;
	/**查看聊天内容*/
	private View tv_view_chat;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_group);
		findVifews();
		List<TaskListUserListBean> mUserList=new ArrayList<TaskListUserListBean>();
		gv.setAdapter(new MembersAdapter(TaskGroupActivity.this,mUserList));
	}
	private void findVifews() {
		tv_view_chat=findViewById(R.id.tv_view_chat);
		tv_view_chat.setOnClickListener(this);
		gv=(GridView) findViewById(R.id.gv);
		iv_back=findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
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
		case R.id.tv_view_chat://查看聊天内容
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
