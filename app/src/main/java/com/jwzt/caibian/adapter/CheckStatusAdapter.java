package com.jwzt.caibian.adapter;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.util.TimeUtil;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 稿件审核状态的适配器
 * @author howie
 *
 */
public class CheckStatusAdapter extends BaseAdapter {
	private Context mContext;
	private String operateType;
	private String mTime;
	
	public CheckStatusAdapter(Context mContext,String operatetype,String time) {
		super();
		this.mContext = mContext;
		this.operateType=operatetype;
		this.mTime=time;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView=View.inflate(mContext, R.layout.check_status_item_layout, null);
			holder.iv = (ImageView) convertView.findViewById(R.id.iv);
			holder.tv = (TextView) convertView.findViewById(R.id.tv);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.tv_time.setText(TimeUtil.getHSMD(mTime));
		if(operateType.equals("1")){//上传
			holder.iv.setImageResource(R.drawable.chuan);
			holder.tv.setText("稿件上传成功");
		}else if(operateType.equals("4")){//选用
			holder.iv.setImageResource(R.drawable.dui);
			holder.tv.setText("稿件已选用");
		}else if(operateType.equals("6")){//审核通过
			holder.iv.setImageResource(R.drawable.shen);
			holder.tv.setText("稿件审核成功");
		}else if(operateType.equals("7")){//审核不通过
			holder.iv.setImageResource(R.drawable.noshen);
			holder.tv.setText("稿件审核失败");
		}else{
			holder.iv.setImageResource(R.drawable.chuan);
		}
		return convertView;
	}
	
	public class  ViewHolder{
		ImageView iv;
		TextView tv,tv_time;
	}

}
