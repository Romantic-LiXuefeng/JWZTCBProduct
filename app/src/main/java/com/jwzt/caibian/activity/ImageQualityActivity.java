package com.jwzt.caibian.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.jwzt.caibian.adapter.QualityAdapter;
import com.jwzt.caibian.application.BaseActivity;
import com.jwzt.caibian.application.GlobalContants;
import com.jwzt.caibian.bean.QualityBean;
import com.jwzt.cb.product.R;

import java.util.ArrayList;
/**
 * 图片质量设置Activity
 * @author howie
 *
 */
public class ImageQualityActivity extends BaseActivity implements OnClickListener {
	/**返回按钮*/
	private View iv_back;
	private GridView gv;
	private ArrayList<QualityBean> mList;
	private QualityAdapter qualityAdapter;
	private String[] picQualitys;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_quality);
		initList();
		findViews();
		qualityAdapter = new QualityAdapter(ImageQualityActivity.this,mList);
		gv.setAdapter(qualityAdapter );
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int postion,
					long arg3) {
				// TODO Auto-generated method stub
				for (int i = 0; i < mList.size(); i++) {
					if (i==postion) {
						mList.get(i).setSelected(true);
						mSharePreferenceUtils.putInt(getApplicationContext(), GlobalContants.PICTUREQUALITYPOSION, postion);
						mSharePreferenceUtils.putString(getApplicationContext(), GlobalContants.PICTUREQUALITY, picQualitys[postion]);
					}else{
						mList.get(i).setSelected(false);
					}
				}
				qualityAdapter.notifyDataSetChanged();
				
			}
			
		});
	}
	private void findViews() {
		iv_back=findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		gv=(GridView) findViewById(R.id.gv);
		int position = mSharePreferenceUtils.getInt(getApplicationContext(), GlobalContants.PICTUREQUALITYPOSION, 0);
		mList.get(position).setSelected(true);
	}
	private void initList() {
		picQualitys = new String[] {"原画","高清","标清","普清"};
		mList=new ArrayList<QualityBean>();
		mList.add(new QualityBean(false,picQualitys[0]));
		mList.add(new QualityBean(false,picQualitys[1]));
		mList.add(new QualityBean(false,picQualitys[2]));
		mList.add(new QualityBean(false,picQualitys[3]));
		
	}
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()){
		case R.id.iv_back:
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
