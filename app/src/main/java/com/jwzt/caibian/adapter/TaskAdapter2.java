package com.jwzt.caibian.adapter;

import java.util.List;

import com.baidu.mapapi.map.Text;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.bean.TaskListBean;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.TimeUtil;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
/**
 * 任务的适配器
 * @author howie
 *
 */
public class TaskAdapter2 extends BaseAdapter {
	private Context mCOntext;
	private List<TaskListBean> mList;
	
	public TaskAdapter2(Context mCOntext,List<TaskListBean> list) {
		super();
		this.mCOntext = mCOntext;
		this.mList=list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View view=View.inflate(mCOntext, R.layout.task_item_layout, null);
		TextView tv=(TextView) view.findViewById(R.id.tv);
		TextView tv_time=(TextView) view.findViewById(R.id.tv_time);
		TextView tv_going=(TextView) view.findViewById(R.id.tv_going);
		TextView tv_pop = view.findViewById(R.id.tv_pop);
		tv.setText(mList.get(arg0).getTitle());
		tv_time.setText(TimeUtil.getMDHS(mList.get(arg0).getCreateTime()));
		String taskStatus=mList.get(arg0).getTaskStatus();
		tv_pop.setText(mList.get(arg0).getCreator());
		if(IsNonEmptyUtils.isString(taskStatus)){
			if(taskStatus.equals("1")){
			//	tv_going.setBackgroundResource(R.drawable.drop_greenbg);
				tv_going.setText("进行中");
				tv_going.setTextColor(mCOntext.getResources().getColor(R.color.a5));
			}else if(taskStatus.equals("2")){
			//	tv_going.setBackgroundResource(R.drawable.drop_bg);
				tv_going.setText("已结束");
				tv_going.setTextColor(mCOntext.getResources().getColor(R.color.greyce));
			}else{
			//	tv_going.setBackgroundResource(R.drawable.drop_bg);
				tv_going.setText("已结束");
				tv_going.setTextColor(mCOntext.getResources().getColor(R.color.greyce));
			}
		}else{
		//	tv_going.setBackgroundResource(R.drawable.drop_bg);
			tv_going.setText("已结束");
			tv_going.setTextColor(mCOntext.getResources().getColor(R.color.greyce));
		}
		
		return view;
	}

}
