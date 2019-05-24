package com.jwzt.caibian.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.jwzt.caibian.application.GlobalContants;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.application.BaseActivity;
import com.jwzt.caibian.application.Configs;
import com.jwzt.caibian.util.UIUtils;
import com.jwzt.caibian.widget.SwitchView;
import com.jwzt.caibian.widget.SwitchView.OnChangerListener;
/**
 * 直播回传设置Activity
 * @author howie
 *
 */
public class LiveSettingActivity extends BaseActivity implements OnClickListener,OnChangerListener {
	
	private AlertDialog alertDialog;
	/**网络设置*/
	private View tv_net;
	/**返回按钮*/
	private View iv_back;
	/**直播保存录像的按钮和直播启用logo的控制按钮*/
	private SwitchView sv_save,sv_logo;
	/**显示seekbar的进度值的textview*/
	private TextView tv_value;
	private SeekBar seekBar;
	private int seekBarWidth;
	/**直播logo设置*/
	private TextView tv_logo_setting;
	/**录制logo 设置*/
	private View tv_record_logo;
	/**直播质量*/
	private View tv_quality; 
	/**视频录制质量*/
	private View tv_record_quality;
	/**选中按钮和未选中按钮的背景图片*/
	private Drawable quality_check,quality_uncheck;
	

	private TextView tv_4g,/*tv_3g,tv_2g,*/tv_wifi,tv_wifi_4g;;
	/**标题*/
	private TextView tv_titles;
	private boolean isSvLogo = false;
	private boolean isSvSave = true;
	private TextView tv_show_net;
	
	/**网络使用*/
	private TextView tv_net_use;
	/**logo位置设置*/
	private TextView tv_logo_position_setting;
	
	private ArrayList<String> logoList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_live_setting);
		logoList=new ArrayList<String>();
		findViews();
		initDrawable();
		initData();
	}
	
	private void initData(){
		RequestData(Configs.liveLogoUrl, Configs.liveLogoCode, 1);
	}
	
	private void initDrawable() {
		quality_check=getResources().getDrawable(R.drawable.quality_check);
		quality_check.setBounds(0,0,quality_check.getMinimumWidth(),quality_check.getMinimumHeight());
		quality_uncheck=getResources().getDrawable(R.drawable.quality_uncheck);
		quality_uncheck.setBounds(0,0,quality_uncheck.getMinimumWidth(),quality_uncheck.getMinimumHeight());
//		tv_type.setCompoundDrawables(null,null,grey_arrow_up,null);
	}
	/**
	 * 设置选中状态
	 * @param tv
	 * @param isSelected
	 */
	private void setSelected(TextView tv,boolean isSelected){
		if(isSelected){
			tv.setCompoundDrawables(null,null,quality_check,null);
		}else{
			tv.setCompoundDrawables(null,null,quality_uncheck,null);
		}
	}
	private void findViews() {
		tv_show_net = (TextView) findViewById(R.id.tv_show_net);
		tv_logo_position_setting=(TextView) findViewById(R.id.tv_logo_position_setting);
		tv_logo_position_setting.setOnClickListener(this);
		tv_net_use=(TextView) findViewById(R.id.tv_show_net);
		tv_titles=(TextView) findViewById(R.id.tv_titles);
		tv_titles.setText("直播回传设置");
		tv_net=findViewById(R.id.tv_net);
		tv_net.setOnClickListener(this);
		tv_record_quality=findViewById(R.id.tv_record_quality);
		tv_record_quality.setOnClickListener(this);
		tv_quality=findViewById(R.id.tv_quality);
		tv_quality.setOnClickListener(this);
		tv_logo_setting=(TextView) findViewById(R.id.tv_logo_setting);
		tv_logo_setting.setOnClickListener(this);
		tv_record_logo=findViewById(R.id.tv_record_logo);
		tv_record_logo.setOnClickListener(this);
		iv_back=findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		sv_logo=(SwitchView) findViewById(R.id.sv_logo);
		boolean isSvLogo = mSharePreferenceUtils.getBoolean(getApplicationContext(), GlobalContants.ISSTARTUSIGLOGO, false);
		sv_logo.toggleSwitch(isSvLogo);
//		sv_logo.setOnClickListener(this);
		boolean isSvSave = mSharePreferenceUtils.getBoolean(getApplicationContext(), GlobalContants.ISLOCALVIDEO, true);
		sv_save=(SwitchView) findViewById(R.id.sv_save);
		sv_save.toggleSwitch(isSvSave);
//		sv_save.setOnClickListener(this);
		//sv_logo.setOpened(false);
		//sv_save.setOpened(true);
		sv_save.setOnSwitchChangeListener(this);
		sv_logo.setOnSwitchChangeListener(this);
		tv_value=(TextView) findViewById(R.id.tv_value);
		seekBar=(SeekBar) findViewById(R.id.seekBar);
		
		seekBar.post(new Runnable() {
			
			@Override
			public void run() {
				seekBarWidth=seekBar.getWidth();
				int progress = mSharePreferenceUtils.getInt(UIUtils.getContext(), GlobalContants.MICROPHONEVOLUME,0);
				seekBar.setProgress(progress);
				RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tv_value.getLayoutParams();
				
				int dx = getTinyDx(progress);
				//TextView tv_value = (TextView) findViewById(R.id.tv_value);
				layoutParams.leftMargin=(int) ((seekBarWidth*progress*(1.0f)/100)+dx+5);
				/*if(tv_value.getText().toString().length()==1){
					layoutParams.leftMargin+=5;
				}else if(tv_value.getText().toString().length()==3){
					layoutParams.leftMargin+=-5;
				}*/
				tv_value.setLayoutParams(layoutParams);
				tv_value.setText(progress+"");
			}
		});
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				mSharePreferenceUtils.putInt(UIUtils.getContext(), GlobalContants.MICROPHONEVOLUME, seekBar.getProgress());
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
				RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tv_value.getLayoutParams();
				int dx = getTinyDx(value);
				layoutParams.leftMargin=(int) (seekBarWidth*value*(1.0f)/100)+dx+5;
				/*if(tv_value.getText().toString().length()==1){
					layoutParams.leftMargin+=5;
				}else if(tv_value.getText().toString().length()==3){
					layoutParams.leftMargin+=-5;
				}*/
				tv_value.setLayoutParams(layoutParams);
				tv_value.setText(value+"");
			}
			/**
			 * 根据百分比的值得到一个对应的dx的值,用于微调显示百分比的文字的位置
			 * @param value
			 * @param dx
			 * @return
			 */
			
		});
		
		
		
		int netState = mSharePreferenceUtils.getInt(getApplicationContext(), GlobalContants.NETSETING, 1);
		switch (netState) {
		case 0:
			tv_net_use.setText("使用3G/4G网络");
			break;
		case 1:
			tv_show_net.setText("仅wifi");
			break;
		case 2:
			tv_net_use.setText("使用wifi和4G网络");
			break;

		default:
			break;
		}
	}
	@Override
	public void onClick(View view) {
		switch(view.getId()){
		//logo位置设置
		case R.id.tv_logo_position_setting:
			Intent intent_position=new Intent(LiveSettingActivity.this,LogoPositionActivity.class);
			intent_position.putStringArrayListExtra("logolist", logoList);
			startActivity(intent_position);
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out); 
			break;
		//网络设置
		case R.id.tv_net:
			showNetDialog(); 
			break;
		//视频录制质量
		case R.id.tv_record_quality:
			Intent inent_record_quality=new Intent(LiveSettingActivity.this,RecordVideoQualityActivity.class);
			startActivity(inent_record_quality);
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			break;
		case R.id.tv_quality://直播质量
			Intent inent_live_quality=new Intent(LiveSettingActivity.this,VideoQualityActivity.class);
			startActivity(inent_live_quality);
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			break;
		case R.id.iv_back://返回按钮
			finish();
			overridePendingTransition(R.anim.push_left_out,R.anim.push_right_out);
			break;
		case R.id.tv_logo_setting://直播logo设置
			Intent intent=new Intent(LiveSettingActivity.this,LogoSettingActivity.class);
			intent.putStringArrayListExtra("logolist", logoList);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			break;
		case R.id.tv_record_logo:
			Intent intent_record=new Intent(LiveSettingActivity.this,LogoSettingActivity.class);
			intent_record.putStringArrayListExtra("logolist", logoList);
			startActivity(intent_record);
			overridePendingTransition(R.anim.push_left_in,
					R.anim.push_left_out);
			break;
		case R.id.tv_4g://使用移动网络
			mSharePreferenceUtils.putInt(UIUtils.getContext(), GlobalContants.NETSETING, 0);
			setSelected(tv_4g, true);
//			setSelected(tv_3g, false);
//			setSelected(tv_2g, false);
			setSelected(tv_wifi, false);
			tv_show_net.setText("仅数据");
			setSelected(tv_wifi_4g, false);
			alertDialog.dismiss();
			tv_net_use.setText("使用3G/4G网络");
			break;
	
	
//		case R.id.tv_wifi://使用WiFi
//			setSelected(tv_4g, false);
//			setSelected(tv_wifi, true);
//			setSelected(tv_wifi_4g, false);
//			alertDialog.dismiss();
//			tv_net_use.setText("使用wifi网络");
//			break;
		case R.id.tv_wifi_4g://使用wifi和4G
			mSharePreferenceUtils.putInt(UIUtils.getContext(), GlobalContants.NETSETING, 2);
			setSelected(tv_wifi_4g, true);
			setSelected(tv_4g, false);
			setSelected(tv_wifi, false);
			alertDialog.dismiss();
			tv_net_use.setText("使用wifi和4G网络");
			alertDialog.dismiss();
			break;

		case R.id.tv_wifi:
			mSharePreferenceUtils.putInt(UIUtils.getContext(), GlobalContants.NETSETING, 1);
			setSelected(tv_4g, false);
//			setSelected(tv_3g, false);
//			setSelected(tv_2g, false);
			setSelected(tv_wifi, true);
			tv_show_net.setText("仅wifi");
			alertDialog.dismiss();
			break;

		case R.id.sv_logo:
			if (isSvLogo ) {
				sv_logo.setOpened(!isSvLogo);
				isSvLogo = !isSvLogo;
			}else {
				sv_logo.setOpened(!isSvLogo);
				isSvLogo = !isSvLogo;
			}
			
			break;
			
		case R.id.sv_save:
			if (isSvSave ) {
				sv_save.setOpened(!isSvSave);
				isSvSave = !isSvSave;
			}else {
				sv_save.setOpened(!isSvSave);
				isSvSave = !isSvSave;
			}
			mSharePreferenceUtils.putBoolean(UIUtils.getContext(), GlobalContants.ISLOCALVIDEO, isSvSave);
			break;
		}
		
		
	}
	/**
	 * 显示网络设置的对话框
	 */
	private void showNetDialog(){
		alertDialog=new AlertDialog.Builder(LiveSettingActivity.this).create();
		alertDialog.show();
		View view=View.inflate(LiveSettingActivity.this, R.layout.net_set_dialog_layout, null);
		alertDialog.setContentView(view);
		view.findViewById(R.id.tv_4g).setOnClickListener(this);
		tv_4g=(TextView) view.findViewById(R.id.tv_4g);
//		tv_3g=(TextView) view.findViewById(R.id.tv_3g);
//		tv_2g=(TextView) view.findViewById(R.id.tv_2g);
		tv_wifi=(TextView) view.findViewById(R.id.tv_wifi);
		tv_wifi_4g = (TextView) view.findViewById(R.id.tv_wifi_4g);
		tv_wifi_4g.setOnClickListener(this);
		tv_4g.setOnClickListener(this);
//		tv_3g.setOnClickListener(this);
//		tv_2g.setOnClickListener(this);
		tv_wifi.setOnClickListener(this);
		int netState = mSharePreferenceUtils.getInt(getApplicationContext(), GlobalContants.NETSETING, 1);
		switch (netState) {
		case 0:
			setSelected(tv_4g, true);
			setSelected(tv_wifi, false);
			setSelected(tv_wifi_4g, false);
			break;
		case 1:
			setSelected(tv_4g, false);
			setSelected(tv_wifi, true);
			setSelected(tv_wifi_4g, false);
			break;
		case 2:
			setSelected(tv_4g, false);
			setSelected(tv_wifi, false);
			setSelected(tv_wifi_4g, true);
			break;

		default:
			break;
		}
		
		
		
	}
	@Override
	public void onSwitchChanged(SwitchView switchView,boolean open) {
		// TODO Auto-generated method stub
		if(switchView==sv_logo){//直播时启用logo
			if(open){
				Toast.makeText(LiveSettingActivity.this, "直播时启用logo开", 0).show();
				mSharePreferenceUtils.putBoolean(getApplicationContext(), GlobalContants.ISSTARTUSIGLOGO, true);
			}else{
				Toast.makeText(LiveSettingActivity.this, "直播时启用logo关", 0).show();
				mSharePreferenceUtils.putBoolean(getApplicationContext(), GlobalContants.ISSTARTUSIGLOGO, false);
			}
			
		}else if(switchView==sv_save){//直播时保存录像
			if(open){
				Toast.makeText(LiveSettingActivity.this, "直播时保存录像开", 0).show();
				mSharePreferenceUtils.putBoolean(getApplicationContext(), GlobalContants.ISLOCALVIDEO, true);
			}else{
				Toast.makeText(LiveSettingActivity.this, "直播时保存录像关", 0).show();
				mSharePreferenceUtils.putBoolean(getApplicationContext(), GlobalContants.ISLOCALVIDEO, false);
			}
		}
		
	}
	
	private int getTinyDx(int value) {
		int dx=0;
		if(value<10){  
			dx=7;
		}else if(value<20){  
			dx=0;
		}else if(value<30){
			dx=-3;
		}else if(value<40){
			dx=-5;
		}else if(value<50){
			dx=-7;
		}else if(value<60){
			dx=-9;
		}else if(value<70){
			dx=-11;
		}else if(value<80){
			dx=-14;
		}
		else if(value<90){
			dx=-18;
		}else if(value<100){
			dx=-21;
		}else if(value==100){
			dx=-24;
		}
		return dx;
	}
	@Override
	protected void initRequestOnSuccess(String result, int code, int biaoshi) {
		// TODO Auto-generated method stub
		if(code==Configs.liveLogoCode){
			try {
				JSONObject jsonObject = new JSONObject(result);
				String status=jsonObject.getString("status");
				if(status.equals("100")){//表示获取成功
					String data=jsonObject.getString("data");
					JSONArray array=new JSONArray(data);
					for(int i=0;i<array.length();i++){
						String logopath=(String) array.get(i);
						System.out.println("logopath:"+logopath);
						logoList.add(logopath);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
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
