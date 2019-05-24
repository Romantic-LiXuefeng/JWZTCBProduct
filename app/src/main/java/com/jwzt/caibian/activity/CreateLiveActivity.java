package com.jwzt.caibian.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;

import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.adapter.NumericWheelAdapter;
import com.jwzt.caibian.application.BaseActivity;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.application.Configs;
import com.jwzt.caibian.bean.LiveBackListBean;
import com.jwzt.caibian.bean.LoginBean;
import com.jwzt.caibian.listener.OnWheelChangedListener;
import com.jwzt.caibian.util.DateTimeUtils;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.UserToast;
import com.jwzt.caibian.view.WheelView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 创建直播
 * @author howie
 *
 */
public class CreateLiveActivity extends BaseActivity implements OnClickListener,OnWheelChangedListener {
	private TextView tv_titles;
	private EditText tv_theme;
	/**是选择开始时间还是结束时间，默认是选择开始时间*/
	private boolean isStartTime=true;
	private String startTime,endTime;
	private WheelView yearView,monthView, dayView,hourView,minuView;
	private static int END_YEAR = 2100; // 结束年份
	private int START_YEAR = 1900; // 起始年份

	private int currentYear, // 今年
			currentMonth, // 本月
			today, // 今天
	        currHour,  //当前小时
	        currMinute;

	private String[] monthBig = { "1", "3", "5", "7", "8", "10", "12" },// 大月 31天
			monthLitle = { "4", "6", "9", "11" };// 小月 30天

	private int textSize;//滚轮文字的大小



	private AlertDialog alertDialog;
	private View iv_back;
	/**开始时间和结束时间*/
	private TextView tv_start,tv_end;
	/**有上角的完成按钮*/
	private View tv_complete;
	private LoginBean mLoginBean;
	private CbApplication application;
	private LiveBackListBean createLiveBean;
	
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				UserToast.toSetToast(CreateLiveActivity.this, "创建失败");
				break;
			case 1:
				if(createLiveBean!=null){
					Intent intent=new Intent(CreateLiveActivity.this,NewLiveActivity.class);
					intent.putExtra("liveId", createLiveBean.getId());
					startActivity(intent);
					CreateLiveActivity.this.finish();
					overridePendingTransition(R.anim.push_left_out,R.anim.push_right_out);
				}
				break;
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_live1);
		application=(CbApplication) getApplication();
		mLoginBean=application.getmLoginBean();
		
		findViews();
		Calendar a=Calendar.getInstance();
		START_YEAR=a.get(Calendar.YEAR);
		
	}
	private void findViews() {
		tv_theme=(EditText) findViewById(R.id.tv_theme);
		tv_titles=(TextView) findViewById(R.id.tv_titles);
		tv_titles.setText("创建直播");
		tv_complete=findViewById(R.id.tv_complete);
		iv_back=findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		tv_start=(TextView) findViewById(R.id.tv_start);
		tv_end=(TextView) findViewById(R.id.tv_end);
		tv_start.setOnClickListener(this);
		tv_end.setOnClickListener(this);
		tv_complete.setOnClickListener(this);
	}
	
	private void createLive(String name,String startTime,String endTime){
		// TODO
		if(mLoginBean!=null){
//			try {
//				String createUrl = String.format(Configs.createLiveUrl, mLoginBean.getUserID(),URLEncoder.encode(name, "UTF-8"),startTime,endTime);
//				RequestData(createUrl, Configs.createLiveCode, 1);
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			}
			RequestParams params=new RequestParams();
			HttpUtils httpUtils=new HttpUtils();
//			?userId=%s&name=%s&startTime=%s&endTime=%s
			params.addBodyParameter(new BasicNameValuePair("userId", mLoginBean.getUserID()));     
			params.addBodyParameter(new BasicNameValuePair("name", name)); 
			params.addBodyParameter(new BasicNameValuePair("startTime", startTime)); 
			params.addBodyParameter(new BasicNameValuePair("endTime", endTime)); 
			httpUtils.send(HttpMethod.POST, Configs.createLiveUrl, params, new RequestCallBack<String>() {

				@Override
				public void onFailure(HttpException c, String arg1) {
					System.out.println("==========="+c+arg1);
				}

				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					System.out.println("========responseInfo.result===="+responseInfo.result);
					if(IsNonEmptyUtils.isString(responseInfo.result)){
						JSONObject jsonObject=JSON.parseObject(responseInfo.result);
						String status=jsonObject.getString("status");
						if(status.equals("100")){//表示获取成功
							String data=jsonObject.getString("data");
							createLiveBean=JSON.parseObject(data,LiveBackListBean.class);
							if(createLiveBean!=null){
								mHandler.sendEmptyMessage(1);
							}else{
								mHandler.sendEmptyMessage(0);
							}
						}else if(status.equals("500")){
							UserToast.toSetToast(CreateLiveActivity.this, jsonObject.getString("message"));
						}else{
							mHandler.sendEmptyMessage(0);
						}
					}
				}
			});
		}
	}
	
	/**
	 * 将传入的文本编码为“UTF-8”的格式
	 * @param str 需要进行编码的文本
	 * @param isInput “请输入”还是“请选择”，true代表“请输入”，false代表“请选择”
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	private String encode(String str){
		if(IsNonEmptyUtils.isString(str)){
			try {
				return URLEncoder.encode(str, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.tv_complete://右上角的完成按钮
			String name=tv_theme.getText().toString();
//			String startTime=tv_start.getText().toString();
//			String endTime=tv_end.getText().toString();
			
			if(name.isEmpty()){
				UserToast.toSetToast(CreateLiveActivity.this, "请输入直播主题");
				return ;
			}
			
			if(startTime!=null&&!startTime.equals("")&&startTime.isEmpty()){
				
				
				UserToast.toSetToast(CreateLiveActivity.this, "请选择开始时间");
				return ;
			}
			
			if(endTime!=null&&!endTime.equals("")&&endTime.isEmpty()){
				UserToast.toSetToast(CreateLiveActivity.this, "请选择结束时间");
				return ;
			}
			
			createLive(name, startTime, endTime);
			
//			Intent intent=new Intent(this,NewLiveActivity.class);
//			startActivity(intent);
//			overridePendingTransition(R.anim.push_left_out,R.anim.push_right_out);
			break;
		case R.id.iv_back:
			finish();
			overridePendingTransition(R.anim.push_left_out,R.anim.push_right_out);
			break;
		case R.id.tv_start://开始时间
//			pvTime.show();
//			alertDialog.show();
			isStartTime=true;
			show();
			break;
		case R.id.tv_end://结束时间
			isStartTime=false;
			show();
			break;
		case R.id.rl_outside://时间选择弹出窗的黑色区域
			alertDialog.dismiss();
			break;
		case R.id.tv_confirm://时间选择弹出窗的确定按钮
//			Toast.makeText(CreateLiveActivity.this, currentYear+"年"+currentMonth+"月"+today+"日"+currHour+"时"+currMinute+"分", 0).show();
			if(isStartTime){
				startTime=getSelectTime();
				tv_start.setText(""+startTime);
			}else{
				endTime=getSelectTime();
				tv_end.setText(""+endTime);
			}
			alertDialog.dismiss();
			break;
		}
	}
	/**
	 * 获取用户选择的时间
	 * @return
	 */
	private String getSelectTime() {
		String hour ="";
		String minute="";
		if(currHour==0){
			hour="00";
		}else{
			hour=getTexts(currHour);
		}
		if(currMinute==0){
			minute="00";
		}else{
			minute=getTexts(currMinute);  
		}
		return currentYear+"-"+getTexts(currentMonth)+"-"+getTexts(today)+" "+hour+":"+minute+":00";
		
		
	}
	/**
	 * 将数字转换为字符串，如果数字小于10就在前面加上一个0
	 * @param number
	 * @return
	 */
	private String getTexts(int number){
		if(number<10){
			return "0"+number;
		}else{
			return ""+number;
		}
	}

	private void show(){
//		getCurrentDate();
		alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.show();
		alertDialog.setCanceledOnTouchOutside(true);
		
		View view = View.inflate(CreateLiveActivity.this,R.layout.pop_date_pick, null);
		initView(view);
		getCurrentDate();
		
		//设置滚轮文字大小
				textSize=(int) (20*getResources().getDisplayMetrics().density);
		// 给控件设置初始数据
		setInitDataAddListener();
		alertDialog.setContentView(view);
		alertDialog.show();
		alertDialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface arg0) {
				isStartTime=true;
			}
		});
	}
	/** 初始化控件 */
	private void initView(View view) {

		view.findViewById(R.id.rl_outside).setOnClickListener(this);
		yearView = (WheelView) view. findViewById(R.id.year);
		yearView.setCyclic(false);
		monthView = (WheelView) view. findViewById(R.id.month);
		dayView = (WheelView)  view.findViewById(R.id.day);
		hourView=(WheelView) view. findViewById(R.id.hour);
		minuView=(WheelView)  view.findViewById(R.id.minute);
		view.findViewById(R.id.tv_confirm).setOnClickListener(this);

		int themColor=getResources().getColor(android.R.color.black);//中间文字的颜色

		dayView.setSelectItemTextColor(themColor);
		hourView.setSelectItemTextColor(themColor);
		minuView.setSelectItemTextColor(themColor);

	}
	/** 给控件设置初始数据 */
	private void setInitDataAddListener() {

		
		// 设置年份
		yearView.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));
		yearView.setCyclic(false);
		yearView.setCurrentItem(currentYear - START_YEAR);
		yearView.addChangingListener(this);
		yearView.TEXT_SIZE = textSize;

		// 设置月份
		monthView.setAdapter(new NumericWheelAdapter(1, 12));
		monthView.setCyclic(true);
		monthView.setCurrentItem(currentMonth-1);
		monthView.addChangingListener(this);
		monthView.TEXT_SIZE = textSize;

		// 设置日期
		setDay(String.valueOf(currentMonth));
		dayView.setCyclic(true);
		dayView.setCurrentItem(today - 1);
		dayView.addChangingListener(this);
		dayView.TEXT_SIZE = textSize;
		
		hourView.setAdapter(new NumericWheelAdapter(0, 23));
		hourView.setCurrentItem(currHour);
		hourView.setCyclic(true);
//		hourView.setCurrentItem(0);
		hourView.addChangingListener(this);
		hourView.TEXT_SIZE = textSize;
		
		minuView.setAdapter(new NumericWheelAdapter(0, 59));
		minuView.setCyclic(true);
		minuView.setCurrentItem(currMinute);
		minuView.addChangingListener(this);
		minuView.TEXT_SIZE = textSize;
	}
	/**
	 * 需要设置的月份
	 * 
	 * @param str
	 */
	private void setDay(String str) {
		// 1,判断是大月，小月
		if (isHas(monthBig, str)) {
			dayView.setAdapter(new NumericWheelAdapter(1, 31));
		} else if (isHas(monthLitle, str)) {
			dayView.setAdapter(new NumericWheelAdapter(1, 30));
			// 如果从大月变到小月，需要设定小月选定的日期day
			if(dayView.getCurrentItem()>=30){
				dayView.setCurrentItem(0);
		}

			// 2,都没有, 说明是2月
			// 判断是平年, 闰年	
		}else if ((currentYear % 4 == 0 && currentYear % 100 != 0)
				|| currentYear % 400 == 0) {
			dayView.setAdapter(new NumericWheelAdapter(1, 29));
			if (today>29) {
				dayView.setCurrentItem(0);
			}
		} else {
			dayView.setAdapter(new NumericWheelAdapter(1, 28));
			if (today>28) {
				dayView.setCurrentItem(0);
			}
		}
	}
	/** 获取当前时间 */
	private void getCurrentDate() {
		String timeStr = DateTimeUtils.getStringDateTime(System.currentTimeMillis());
		String[] dates = timeStr.substring(0,10).split("-");
		currentYear= Integer.parseInt(dates[0]);
		currentMonth= Integer.parseInt(dates[1]);
		today= Integer.parseInt(dates[2]);
		String[] hourMinute = timeStr.substring(11).split(":");
		currHour=Integer.parseInt(hourMinute[0]);
		if(currHour>23){
			currHour=0;
		}
		
		currMinute=Integer.parseInt(hourMinute[1]);
		if(currMinute>59){
			currMinute=0;
		}
	}
	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		int wheelId = wheel.getId();
		if(wheelId == R.id.year){
			// 选中的年份
			currentYear = newValue + START_YEAR;
		}if(wheelId == R.id.month){
			// 选中的月份
			currentMonth = newValue+1;
		}if(wheelId == R.id.day){
			// 天
			today=newValue+1;
		}
		if(wheelId==R.id.hour){//小时
			currHour=newValue; //当前小时
	       
		}
		if(wheelId==R.id.minute){//分钟
			 currMinute=newValue;
		}
		
		
		setDay(String.valueOf(currentMonth));
		
	}
	/**
	 * 判断是否包含指定的字符串
	 * 
	 * @param strs
	 *            字符串集合
	 * @param str
	 *            指定的字符
	 * @return if contain return true, or else return false.
	 * 
	 */
	private boolean isHas(String[] strs, String str) {
		if (strs != null) {
			for (String string : strs) {
				if (TextUtils.equals(str, string))
					return true;
			}
		}
		return false;
	}
	@Override
	protected void initRequestOnSuccess(String result, int code, int biaoshi) {
		// TODO Auto-generated method stub
		if(code==Configs.createLiveCode){
			JSONObject jsonObject=JSON.parseObject(result);
			String status=jsonObject.getString("status");
			if(status.equals("100")){//表示获取成功
				String data=jsonObject.getString("data");
				createLiveBean=JSON.parseObject(data,LiveBackListBean.class);
				if(createLiveBean!=null){
					mHandler.sendEmptyMessage(1);
				}else{
					mHandler.sendEmptyMessage(0);
				}
			}
		}
	}
	@Override
	protected void initRequestOnStart(String result, int code, int biaoshi) {
	}
	@Override
	protected void initRequestOnFailure(String failure, int code, int biaoshi) {
	}
	@Override
	protected void initRequestOnCache(String result, int code, int biaoshi) {
		// TODO Auto-generated method stub
		if(code==Configs.createLiveCode){
			JSONObject jsonObject=JSON.parseObject(result);
			String status=jsonObject.getString("status");
			if(status.equals("100")){//表示获取成功
				String data=jsonObject.getString("data");
				createLiveBean=JSON.parseObject(data,LiveBackListBean.class);
				if(createLiveBean!=null){
					mHandler.sendEmptyMessage(1);
				}else{
					mHandler.sendEmptyMessage(0);
				}
			}
		}
	}
}
