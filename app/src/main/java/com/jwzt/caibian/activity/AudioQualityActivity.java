package com.jwzt.caibian.activity;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.jwzt.caibian.application.GlobalContants;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.adapter.QualityAdapter;
import com.jwzt.caibian.application.BaseActivity;
import com.jwzt.caibian.bean.QualityBean;
/**
 * 音频质量设置Activity
 * @author howie
 *
 */
public class AudioQualityActivity extends BaseActivity implements OnClickListener {
	/**标题*/
	private TextView tv_titles;
	/**返回按钮*/
	private View iv_back;
	private GridView gv;
	private ArrayList<QualityBean> mList;
	private QualityAdapter qualityAdapter;
	private int[] mListNumbers;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audio_quality);
		initList();
		findViews();
		qualityAdapter = new QualityAdapter(AudioQualityActivity.this,mList);
		gv.setAdapter(qualityAdapter );
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int postion,
					long arg3) {
				// TODO Auto-generated method stub
				for (int i = 0; i < mList.size(); i++) {
					if (i==postion) {
						mList.get(i).setSelected(true);
						mSharePreferenceUtils.putInt(getApplicationContext(), GlobalContants.VOICEQUALITYPOSION, postion);
						mSharePreferenceUtils.putInt(getApplicationContext(), GlobalContants.VOICEQUALITY, mListNumbers[postion]);
					}else{
						mList.get(i).setSelected(false);
					}
				}
				qualityAdapter.notifyDataSetChanged();
				
			}
			
		});
	}
	private void findViews() {
		tv_titles=(TextView) findViewById(R.id.tv_titles);
		tv_titles.setText("音频质量");
		iv_back=findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		gv=(GridView) findViewById(R.id.gv);
		int posion = mSharePreferenceUtils.getInt(getApplicationContext(), GlobalContants.VOICEQUALITYPOSION, 0);
		mList.get(posion).setSelected(true);
	}
	private void initList() {
		mListNumbers = new int[] {320,256,192,128};
		mList=new ArrayList<QualityBean>();
		for (int i = 0; i < mListNumbers.length; i++) {
			mList.add(new QualityBean(false,mListNumbers[i]+"kbps"));
		}
		
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
