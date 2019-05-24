package com.jwzt.caibian.activity;

import java.util.ArrayList;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.adapter.TagGridAdapter;
import com.jwzt.caibian.application.BaseActivity;
import com.jwzt.caibian.widget.FlowPopulateLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 选择标签
 * @author howie
 *
 */
public class SelectTagActivity extends BaseActivity implements OnClickListener {
	private GridView gv;
	private ArrayList<String> list;
	/**热门标签*/
	private ArrayList<String> hotList;
	private FlowPopulateLayout fpl;
	private View iv_back;
	/**标题*/
	private TextView tv_titles;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_tag);
		tv_titles=(TextView) findViewById(R.id.tv_titles);
		tv_titles.setText("选择标签");
		gv=(GridView) findViewById(R.id.gv);
		fpl=(FlowPopulateLayout) findViewById(R.id.fpl);
		iv_back=findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		initList();
		gv.setAdapter(new TagGridAdapter(SelectTagActivity.this, list));
		addViews();
		
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.putExtra("tag", list.get(position));
				setResult(6, intent);
				finish();
				overridePendingTransition(R.anim.push_left_out,
						R.anim.push_right_out);
			}
		});
		
	}
	/**
	 * 向fpl中添加view
	 */
	private void addViews() {
		// TODO Auto-generated method stub
		for (int i = 0; i < hotList.size(); i++) {
			View view=View.inflate(SelectTagActivity.this, R.layout.hot_tag_item_layout, null);
			TextView tv = (TextView) view.findViewById(R.id.tv);
			tv.setText(hotList.get(i));
			if(i==hotList.size()-1){
				tv.setTextColor(getResources().getColor(R.color.hot_bg));
			}
			final int j=i;
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					// TODO Auto-generated method stub
					Intent intent=new Intent();
					intent.putExtra("tag", hotList.get(j));
					setResult(5, intent);
					finish();
				}
			});
			fpl.addView(view,RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		}
	}
	private void initList() {
		// TODO Auto-generated method stub
		list=new ArrayList<String>();
		list.add("新闻");
		list.add("时政");
		list.add("体育");
		list.add("文化");
		list.add("汽车");
		list.add("娱乐");
		list.add("教育");
		list.add("房产");
		list.add("其他");
		hotList=new ArrayList<String>();
		hotList.add("三生三世十里桃花");
		hotList.add("杨幂");
		hotList.add("变形记");
		hotList.add("古力娜扎");
		hotList.add("租房不是人过的日子");
		hotList.add("张杰谢娜炒作门");
		hotList.add("撒哈拉沙漠");
		
		
	}
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()){
		case R.id.iv_back://返回按钮
			finish();
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
