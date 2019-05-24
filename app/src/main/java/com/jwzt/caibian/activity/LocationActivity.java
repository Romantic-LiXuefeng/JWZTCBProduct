package com.jwzt.caibian.activity;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.adapter.LocateAdapter;
import com.jwzt.caibian.application.BaseActivity;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.application.Configs;
import com.jwzt.caibian.util.IsNonEmptyUtils;

/**
 * 定位
 * 
 * @author howie
 *
 */
public class LocationActivity extends BaseActivity implements OnClickListener,
		OnGetPoiSearchResultListener {
	/** 标题 */
	private TextView tv_titles;
	private EditText et_input;
	private ImageView iv_back;
	private View tv_cancel;
	private ListView lv;

	private PoiSearch mPoiSearch = null;
	private List<PoiInfo> listPoiInfo;
	private LocateAdapter adapter;
	private CbApplication application;
	int resultyi = 0;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				initView();
				dismisLoadingDialog();
				break;
			case 10:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);
		application = (CbApplication) getApplication();
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		findViews();
		showLoadingDialog(this, null, null);
		//application.getLatitude(), application.getLongitude()
		searchPoi(application.getLocation(), application.getLatitude(), application.getLongitude());
		//searchPoi("酒泉市北大街",40.15, 94.66);
		//searchPoi("沙州乐园",40.15, 94.66);
		//Toast.makeText(LocationActivity.this, application.getLocation()+"lat"+application.getLatitude()+"Long"+application.getLongitude(), 1).show();
	}

	private void findViews() {
		tv_titles = (TextView) findViewById(R.id.tv_titles);
		tv_titles.setText("所在位置");
		tv_cancel = findViewById(R.id.tv_cancel);
		tv_cancel.setOnClickListener(this);
		et_input = (EditText) findViewById(R.id.et_input);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		lv = (ListView) findViewById(R.id.lv);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				//if (position != 0) {
					Intent intent = new Intent();
					if (position == 0) {
						intent.putExtra("info",listPoiInfo.get(position).city);
					} else {
						intent.putExtra("info", listPoiInfo.get(position).name);
					}
					setResult(6, intent);
					finish();
					overridePendingTransition(R.anim.push_left_out,R.anim.push_right_out);
				//}
			}
		});
	}

	/*** 适配搜索的结果数据 */
	private void initView() {
		adapter = new LocateAdapter(LocationActivity.this, listPoiInfo);
		lv.setAdapter(adapter);
	}

	/*** 搜索方法 */
	private void searchPoi(String location, double lat, double git) {
		PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption()
				.keyword(location)
				.sortType(PoiSortType.distance_from_near_to_far)
				.location(new LatLng(lat, git)).radius(500).pageNum(0);
		mPoiSearch.searchNearby(nearbySearchOption);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.iv_back:
			finish();
			overridePendingTransition(R.anim.push_left_out,
					R.anim.push_right_out);
			break;
		case R.id.tv_cancel:
			String content = et_input.getText().toString();
			if (IsNonEmptyUtils.isString(content)) {
				searchPoi(content, application.getLatitude(), application.getLongitude());
			} else {
				Toast.makeText(LocationActivity.this, "请输入搜索内容", 1).show();
			}
			et_input.setText("");
			// 隐藏软键盘
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(et_input.getWindowToken(), 0);
			break;
		}
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult result) {
		if (result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(LocationActivity.this, "抱歉，未找到结果",
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(LocationActivity.this,
					result.getName() + ": " + result.getAddress(),
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onGetPoiIndoorResult(PoiIndoorResult arg0) {
	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		// TODO Auto-generated method stub
		if (result == null
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			Toast.makeText(LocationActivity.this, "未找到结果", Toast.LENGTH_LONG)
					.show();
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			listPoiInfo = result.getAllPoi();
			mHandler.sendEmptyMessage(0);
			return;
		}
	}
	@Override
	protected void initRequestOnSuccess(String result, int code, int biaoshi) {
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


}
