package com.jwzt.caibian.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jwzt.caibian.adapter.AllFootageAdapter;
import com.jwzt.caibian.application.BaseActivity;
import com.jwzt.caibian.application.GlobalContants;
import com.jwzt.caibian.bean.ResourcesBean;
import com.jwzt.cb.product.R;

import java.util.List;
/**
 * 全部素材
 * @author howie
 *
 */
public class AllFootageActivity extends BaseActivity implements OnClickListener {
	/**标题*/
	private TextView tv_titles;
	private ListView lv;
	private ImageView iv_back;
	
	/**
	 * 已上传的图片数据集合
	 */
	private List<ResourcesBean> mList;
	private AllFootageAdapter allFootageAdapter; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_all_footage);
		mList=(List<ResourcesBean>) getIntent().getSerializableExtra("alls");
		findViews();
		
		initView();
	}
	
	private void initView(){
		allFootageAdapter = new AllFootageAdapter(AllFootageActivity.this,mList);
		lv.setAdapter(allFootageAdapter);
	}

	private void findViews() {
		tv_titles=(TextView) findViewById(R.id.tv_titles);
		tv_titles.setText("任务素材");
		lv=(ListView) findViewById(R.id.lv);
		iv_back=(ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
//				String preViewImg=mList.get(position).getPreviewUrl();
				String fileRealPath=mList.get(position).getFileRealPath();
				if(fileRealPath.endsWith("mp4")||fileRealPath.endsWith("mp3")){
					Intent videointent=new Intent(AllFootageActivity.this, ShowVideoActivity.class);
					videointent.putExtra("playpath", fileRealPath);
					startActivity(videointent);
				}else{
					Intent intent=new Intent(AllFootageActivity.this, ShowImageActivity.class);
//					if(IsNonEmptyUtils.isString(preViewImg)){
//						intent.putExtra(GlobalContants.NEWSHOWIMAGE, preViewImg);
//					}else if(IsNonEmptyUtils.isString(fileRealPath)){
						intent.putExtra(GlobalContants.NEWSHOWIMAGE, fileRealPath);
//					}
					startActivity(intent);
				}
				
			}
		});
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
