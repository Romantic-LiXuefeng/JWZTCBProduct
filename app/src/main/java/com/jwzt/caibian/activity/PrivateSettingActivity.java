package com.jwzt.caibian.activity;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.adapter.NumericWheelAdapter;
import com.jwzt.caibian.application.BaseActivity;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.application.Configs;
import com.jwzt.caibian.bean.LoginBean;
import com.jwzt.caibian.cutting.Crop;
import com.jwzt.caibian.cutting.CropImageActivity;
import com.jwzt.caibian.interfaces.OnAlarmSettingPopItemClickListener;
import com.jwzt.caibian.listener.OnWheelChangedListener;
import com.jwzt.caibian.util.AnimationUtils;
import com.jwzt.caibian.util.BitmapUtils;
import com.jwzt.caibian.util.CreateBmpFactory;
import com.jwzt.caibian.util.DateTimeUtils;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.PhotoChangeUtil;
import com.jwzt.caibian.util.TimeUtil;
import com.jwzt.caibian.util.UserToast;
import com.jwzt.caibian.view.WheelView;
import com.jwzt.caibian.widget.ListViewPopupWindow;
import com.jwzt.caibian.widget.PlacePickPopupWindow;
import com.jwzt.caibian.widget.RoundedImageView;
import com.jwzt.caibian.widget.SelectPhotoPopupWindow;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;


/**
 * 个人设置activity
 * 
 * @author howie
 * 
 */
@SuppressLint("HandlerLeak") public class PrivateSettingActivity extends BaseActivity implements OnClickListener ,OnWheelChangedListener{
	/***处理调用本地相机拍照和调取本地图片的工具类*/
	private CreateBmpFactory mCreateBmpFactory;
	/**车牌号textview*/
	private TextView ftv_car_number;
	private TextView tv_car_number;
	
	/**"职业"的集合*/
	private List<String> list_profession;
	/**职业textview*/
	private TextView ftv_profession;
	private TextView tv_profession;
	/**收入水平*/
	private String input_income="请选择";
	/**星座的集合*/
	private List<String> list_zodiacs;
	/**教育水平的集合*/
	private List<String> list_education;
	/** 返回按钮 **/
	private ImageView iv_back;
	/** 选择地址的popupWindow **/
	private PlacePickPopupWindow placePopWindow;
	/** 性别的集合 **/
	private List<String> list_genders;
	/**收入水平的集合*/
	 private List<String> list_income;
	/** popupWindow是否正在显示 **/
	private boolean isShowing;
	/** 点击更换头像弹出的PopupWindow **/
	private SelectPhotoPopupWindow photoWindow;
	/** 选择“性别”“出生地”等的时候底部弹出的popupWIndow **/
	private ListViewPopupWindow lvPopWindow;
	/***头像控件*/
	private RoundedImageView iv_headPhoto;
	/** 头部的标题 **/
	private TextView tv_name;
	/**显示具体星座的textView*/
	private TextView tv_zodiac;
	/** 右上角的“保存”按钮 **/
	private TextView tv_edit;
	/**收入水平的textView*/
	private TextView ftv_income;
	/**显示具体收入水平的textView*/
	private TextView tv_income;
	
	/** 一整条白色的写有“手机号”的textView **/
	private TextView ftv_phone_num;
	/** 一整条白色的写有“用户名”的textView **/
	private TextView ftv_username;
	/** 显示具体手机号如“1830123432”的textView **/
	private TextView tv_phone_num;
	/** 显示具体人名如“张全蛋”的textView **/
	private TextView tv_userName;
	/** 显示具体生日日期的textView **/
	private TextView tv_birthday;
	/** 性别 **/
	private TextView ftv_gender;
	/** 生日 **/
	private TextView ftv_birthday;
	/** 显示具体出生地的textView **/
	private TextView tv_birth_place;
	/**年龄*/
	private TextView ftv_age;
	/**显示具体年龄的textView*/

	private TextView tv_age;
	/** 居住地 **/
	private TextView ftv_live_place;
	/** 更换头像 **/
	private TextView ftv_change;
	/** popupWindow弹出来的时候的暗色背景区域 **/
	private View view_mask;
	/** 最外层的根布局 **/
	private RelativeLayout rl_root;
	/** 显示具体性别“男”“女”的textView **/
	private TextView tv_gender;

	/** 显示具体居住地如“香港”的textView **/
	private TextView tv_live_place;

	/** 退出登录 **/
	private TextView tv_unlogin;
	/**
	 * 因为“出生地”与“居住地”的选择会弹出一模一样的popwindow,这里用这个变量来标记弹出的地址选择框是选择出生地还是居住地，true代表出生地
	 * ，false代表居住地
	 **/
	private boolean isBirthPlace = true;
	/** 点击“退出登录”的弹出窗口 **/
	private Dialog alertDialog;
	private AlertDialog alertDialog1;
	/***处理调用本地相机拍照和调取本地图片的工具类*/
	/***拍照或选取后获取的bitmap对象*/
	private Bitmap bmp;
	/**显示具体兴趣爱好的textView*/
	private TextView tv_hobby;
	/**兴趣爱好的textView*/
	private TextView ftv_hobby;
	/**星座textView*/
	private TextView ftv_zodiac;
	/**教育水平的*/
	private TextView ftv_education;
	/**显示具体教育水平*/
	private TextView tv_education;
	
	private CbApplication application;
	private LoginBean mLoginBean,loginbean;
	private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private String message;
    
    
    
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
	
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				if(IsNonEmptyUtils.isString(message)){
					UserToast.toSetToast(PrivateSettingActivity.this, message);
				}else{
					UserToast.toSetToast(PrivateSettingActivity.this, "修改失败");
				}
				break;
			case 1:
				application.setmLoginBean(mLoginBean);
				mLoginBean=application.getmLoginBean();
				if(mLoginBean!=null){
					if(IsNonEmptyUtils.isString(mLoginBean.getPhoto())){
						imageLoader.displayImage(mLoginBean.getPhoto(), iv_headPhoto, options);
					}
				}
				break;
			case 2:
				String pas=application.getmLoginBean().getPassword();
				loginbean.setPassword(pas);
				application.setmLoginBean(loginbean);
				UserToast.toSetToast(PrivateSettingActivity.this, "修改成功");
				PrivateSettingActivity.this.finish();
				break;
			}
		};
	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.private_setting_layout);
		mCreateBmpFactory=new CreateBmpFactory(PrivateSettingActivity.this,this);
		application=(CbApplication) getApplication();
		mLoginBean=application.getmLoginBean();
		loginbean=new LoginBean();
		options = new DisplayImageOptions.Builder()  
        .showImageOnLoading(R.drawable.headdefault) // 设置图片下载期间显示的图片  
        .showImageForEmptyUri(R.drawable.headdefault) // 设置图片Uri为空或是错误的时候显示的图片  
        .showImageOnFail(R.drawable.headdefault) // 设置图片加载或解码过程中发生错误显示的图片  
        .cacheInMemory(false) // 设置下载的图片是否缓存在内存中  
        .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中 
        .bitmapConfig(Config.RGB_565)
//        .displayer(new FadeInBitmapDisplayer(100))
        .build(); // 构建完成  
	    imageLoader = ImageLoader.getInstance();
		
		findView();
		
		
		initData();

	}
	
	
	
	
	private void findView(){
		rl_root = (RelativeLayout) findViewById(R.id.rl_root);
		iv_back = (ImageView) findViewById(R.id.iv_back);// 返回按钮
		tv_name = (TextView) findViewById(R.id.tv_name);// 头部标题
		tv_name.setText("个人设置");
		tv_edit = (TextView) findViewById(R.id.tv_edit);// 保存按钮
		tv_edit.setText("保存");
		
		ftv_change = (TextView) findViewById(R.id.ftv_change);// 更换头像
		iv_headPhoto=(RoundedImageView) findViewById(R.id.iv_headPhoto);//头像
		ftv_username = (TextView) findViewById(R.id.ftv_username);// 用户名
		tv_userName = (TextView) findViewById(R.id.tv_username);
		ftv_car_number=(TextView) findViewById(R.id.ftv_car_number);//车牌号的
		tv_car_number=(TextView) findViewById(R.id.tv_car_num);
		ftv_phone_num = (TextView) findViewById(R.id.ftv_phone_number);//手机号
		tv_phone_num = (TextView) findViewById(R.id.tv_phone_num);
		ftv_age=(TextView) findViewById(R.id.ftv_age);//年龄
		tv_age=(TextView) findViewById(R.id.tv_age);
		ftv_zodiac=(TextView) findViewById(R.id.ftv_zodiac);//星座
		tv_zodiac=(TextView) findViewById(R.id.tv_zodiac);
		ftv_hobby=(TextView) findViewById(R.id.ftv_hobby);//兴趣爱好
		tv_hobby=(TextView) findViewById(R.id.tv_hobby);
		ftv_gender = (TextView) findViewById(R.id.ftv_gender);//性别
		tv_gender = (TextView) findViewById(R.id.tv_gender);
		ftv_birthday = (TextView) findViewById(R.id.ftv_birthday);//生日
		tv_birthday = (TextView) findViewById(R.id.tv_birthday);
		ftv_education=(TextView) findViewById(R.id.ftv_education);//教育水平
		tv_education=(TextView) findViewById(R.id.tv_education);		
		ftv_live_place = (TextView) findViewById(R.id.ftv_live_place);//所在地
		tv_live_place = (TextView) findViewById(R.id.tv_live_place);
		ftv_income=(TextView) findViewById(R.id.ftv_income);//收入水平
		tv_income=(TextView) findViewById(R.id.tv_income);
		ftv_profession=(TextView) findViewById(R.id.ftv_profession);//职业
		tv_profession=(TextView) findViewById(R.id.tv_profession);
		view_mask = findViewById(R.id.view_mask);// popupWindow弹出来的时候变暗的区域
		tv_unlogin = (TextView) findViewById(R.id.tv_unlogin);// 退出登录
		
		
		iv_back.setOnClickListener(this);
		view_mask.setOnClickListener(this);
		ftv_change.setOnClickListener(this);
		tv_edit.setOnClickListener(this);
		ftv_username.setOnClickListener(this);
		ftv_phone_num.setOnClickListener(this);
		ftv_car_number.setOnClickListener(this);
		ftv_age.setOnClickListener(this);
		ftv_zodiac.setOnClickListener(this);
		ftv_hobby.setOnClickListener(this);
		ftv_gender.setOnClickListener(this);
		ftv_birthday.setOnClickListener(this);
		ftv_education.setOnClickListener(this);
		ftv_live_place.setOnClickListener(this);
		ftv_income.setOnClickListener(this);
		ftv_profession.setOnClickListener(this);
		tv_unlogin.setOnClickListener(this);
		
		
		if(mLoginBean!=null){
			if(IsNonEmptyUtils.isString(mLoginBean.getPhoto())){
				imageLoader.displayImage(mLoginBean.getPhoto(), iv_headPhoto, options);
			}
			
			if(IsNonEmptyUtils.isString(mLoginBean.getNickname())){
				tv_userName.setText(mLoginBean.getNickname());
			}else{
				tv_userName.setText("请输入");
			}
			
			if(IsNonEmptyUtils.isString(mLoginBean.getPhoneNum())){
				tv_phone_num.setText(mLoginBean.getPhoneNum());
			}else{
				tv_phone_num.setText("请输入");
			}
			
			if(IsNonEmptyUtils.isString(mLoginBean.getLicensePlateNum())){
				tv_car_number.setText(mLoginBean.getLicensePlateNum());
			}else{
				tv_car_number.setText("请输入");
			}
			
			if(IsNonEmptyUtils.isString(mLoginBean.getAge()+"")){
				tv_age.setText(mLoginBean.getAge()+"");
			}else{
				tv_age.setText("请输入");
			}

			if(IsNonEmptyUtils.isString(mLoginBean.getZodiac())){
				tv_zodiac.setText(mLoginBean.getZodiac());
			}else{
				tv_zodiac.setText("请输入");
			}
			
			if(IsNonEmptyUtils.isString(mLoginBean.getHobby())){
				tv_hobby.setText(mLoginBean.getHobby());
			}else{
				tv_hobby.setText("请输入");
			}
			
			if(IsNonEmptyUtils.isString(mLoginBean.getGender())){
				tv_gender.setText(mLoginBean.getGender());
			}else{
				tv_gender.setText("保密");
			}
			
			if(IsNonEmptyUtils.isString(mLoginBean.getBirthday())){
				tv_birthday.setText(mLoginBean.getBirthday());
			}else{
				tv_birthday.setText("请选择");
			}
			
			if(IsNonEmptyUtils.isString(mLoginBean.getEducation())){
				tv_education.setText(mLoginBean.getEducation());
			}else{
				tv_education.setText("请选择");
			}
			
			if(IsNonEmptyUtils.isString(mLoginBean.getAddress())){
				tv_live_place.setText(mLoginBean.getAddress());//所在地
			}else{
				tv_live_place.setText("请选择");//所在地
			}
			
			if(IsNonEmptyUtils.isString(mLoginBean.getIncomeLevel())){
				tv_income.setText(mLoginBean.getIncomeLevel());
			}else{
				tv_income.setText("请选择");
			}
			
			if(IsNonEmptyUtils.isString(mLoginBean.getProfession())){
				tv_profession.setText(mLoginBean.getProfession());
			}else{
				tv_profession.setText("请选择");
			}
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
	/**
	 * 初始化“性别”等数据
	 */
	private void initData() {
		list_genders = new ArrayList<String>();
		list_genders.add("男");
		list_genders.add("女");
		list_genders.add("保密");
		list_income=new ArrayList<String>();
		list_income.add("2000元以下");
		list_income.add("2000-4000元");
		list_income.add("4000-6000元");
		list_income.add("6000元及以上");
		//从string文件读取星座的集合
		list_zodiacs=Arrays.asList(getResources().getStringArray(R.array.zodiacs));
		//从String文件读取教育水平的集合
		list_education=Arrays.asList(getResources().getStringArray(R.array.education));
	}
	
	/**
	 * 保存修改信息
	 * @param userId 用户id
	 * @param nickname 昵称
	 * @param realname 用户名
	 * @param mobile 手机号
	 * @param licensePlateNumber 车牌号
	 * @param age 年龄
	 * @param zodiac 星座
	 * @param hobby 爱好
	 * @param gander 性别
	 * @param birthday 生日
	 * @param education 教育
	 * @param address 所在地
	 * @param incomeLevel 收入水平
	 * @param userProfession 职业
	 */
	private void saveData(String userId,String nickname,String realname,
			String mobile,String licensePlateNumber,String age,
			String zodiac,String hobby,String gander,String birthday,
			String education,String address,String incomeLevel,String userProfession){
		
//		String updateUrl=String.format(Configs.personInfoUrl, userId,nickname,realname,mobile,
//				licensePlateNumber,age,zodiac,hobby,gander,birthday,education,address,incomeLevel
//				,userProfession);
//		System.out.println("updateUrl:"+updateUrl);
//		RequestData(updateUrl, Configs.personInfoCode, 1);
		
		RequestParams params=new RequestParams();
		HttpUtils httpUtils=new HttpUtils();
		params.addBodyParameter(new BasicNameValuePair("id", userId));     
		params.addBodyParameter(new BasicNameValuePair("nickname", nickname)); 
//		params.addBodyParameter(new BasicNameValuePair("realname", realname)); 
		params.addBodyParameter(new BasicNameValuePair("realname", realname)); 
		params.addBodyParameter(new BasicNameValuePair("mobile", mobile)); 
		params.addBodyParameter(new BasicNameValuePair("licensePlateNumber", licensePlateNumber)); 
		params.addBodyParameter(new BasicNameValuePair("age", age)); 
		params.addBodyParameter(new BasicNameValuePair("zodiac", zodiac)); 
		params.addBodyParameter(new BasicNameValuePair("hobby", hobby)); 
		params.addBodyParameter(new BasicNameValuePair("gander", gander)); 
		params.addBodyParameter(new BasicNameValuePair("birthday", birthday)); 
		params.addBodyParameter(new BasicNameValuePair("education", education)); 
		params.addBodyParameter(new BasicNameValuePair("address", address)); 
		params.addBodyParameter(new BasicNameValuePair("incomeLevel", incomeLevel)); 
		params.addBodyParameter(new BasicNameValuePair("userProfession", userProfession)); 
		httpUtils.send(HttpMethod.POST, Configs.personInfoUrl, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException c, String arg1) {
				System.out.println("==========="+c+arg1);
				Toast.makeText(getApplicationContext(),c.getMessage()+"修改失败",Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				System.out.println("========responseInfo.result===="+responseInfo.result);
				if(IsNonEmptyUtils.isString(responseInfo.result)){
					JSONObject jsonObject=JSON.parseObject(responseInfo.result);
					String status=jsonObject.getString("status");
					if(status.equals("100")){//表示登录成功
						String dataresult=jsonObject.getString("data");
						loginbean=JSON.parseObject(dataresult,LoginBean.class);
						if(loginbean!=null){
							mHandler.sendEmptyMessage(2);
						}else{
							mHandler.sendEmptyMessage(0);
						}
					}else{
						message=jsonObject.getString("message");
						mHandler.sendEmptyMessage(0);
					}
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// “保存”按钮
		case R.id.tv_edit:
			// 保存用户修改的个人信息
			String userName=tv_userName.getText().toString();
			String phoneNum=tv_phone_num.getText().toString();
			String carNumber=tv_car_number.getText().toString();
			String age=tv_age.getText().toString().replaceAll("岁", "");
			String zodiac=tv_zodiac.getText().toString();
			String hobby=tv_hobby.getText().toString();
			String gender=tv_gender.getText().toString();
			String birthday=tv_birthday.getText().toString();
			String education=tv_education.getText().toString();
			String address=tv_live_place.getText().toString();
			String income=tv_income.getText().toString();
			String profession=tv_profession.getText().toString();
			
			if(mLoginBean!=null){
				System.out.println("保存修改");
//				saveData(mLoginBean.getUserID(),encode(userName),encode(userName),phoneNum,encode(carNumber),age,
//						encode(zodiac),encode(hobby),encode(gender),birthday,encode(education),encode(address),encode(income),encode(profession));
				saveData(mLoginBean.getUserID(),userName,userName,phoneNum,carNumber,age,
						zodiac,hobby,gender,birthday,education,address,income,profession);
			}
			break;
		// 返回按钮
		case R.id.iv_back:
			backWithLogin();
			finish();
			overridePendingTransition(R.anim.push_left_out,R.anim.push_right_out);
			break;
		// 更改头像
		case R.id.ftv_change:
			if (isShowing == false) {
				isShowing = true;
				photoWindow = PhotoChangeUtil.getPicPopupWindow(PrivateSettingActivity.this,PrivateSettingActivity.this, rl_root);
				AnimationUtils.showAlpha(view_mask);
			} else {
				hidePopupWindow();
			}
			break;
		case R.id.tv_take_new://拍照按钮
			mCreateBmpFactory.OpenCamera();//调用系统相机
			photoWindow.dismiss();
			AnimationUtils.hideAlpha(view_mask);
			break;
		case R.id.tv_from_album://从相册中选择
			mCreateBmpFactory.OpenGallery();//打开本地相册
			photoWindow.dismiss();
			AnimationUtils.hideAlpha(view_mask);
			break;
		case R.id.tv_cancel:// 更换头像弹出窗口中”取消“
			hidePopupWindow();
		case R.id.view_mask:// 暗色区域
			hidePopupWindow();
			dismissListPopupWindow();
			dismissDatePickPopWindow();
			dismissPlacePopWindow();
			break;
		case R.id.ftv_username:// 用户名
			// 跳转修改用户名的界面，并把用户名传过去
			//TODO
			Intent intent = new Intent(PrivateSettingActivity.this,UserNameModifyActivity.class);
			intent.putExtra("username", mLoginBean.getUsername());
			startActivityForResult(intent, 5);
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			break;
		case R.id.ftv_phone_number:// 手机号
			//(现在用户手机号是不允许修改的，所以注释掉)
			// 跳转修改手机号的界面，并把手机号传过去
			Intent intent_phone = new Intent(PrivateSettingActivity.this,PhoneModifyActivity.class);
			intent_phone.putExtra("phonenum", mLoginBean.getPhoneNum());
			startActivityForResult(intent_phone, 6);
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			break;
		case R.id.ftv_car_number://车牌号
			Intent intent_licence=new Intent(PrivateSettingActivity.this,LicencePlateModifyActivity.class);
			intent_licence.putExtra("carnum", mLoginBean.getLicensePlateNum());
			startActivityForResult(intent_licence, 7);
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			break;
		case R.id.ftv_age://年龄
		//	UserToast.toSetToast(PrivateSettingActivity.this, "选择生日自动计算");
			Intent intent_age = new Intent(PrivateSettingActivity.this,AgeModifyActivity.class);
			intent_age.putExtra("age", mLoginBean.getAge());
			startActivityForResult(intent_age, 8);
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			break;	
		case R.id.ftv_zodiac://星座
			lvPopWindow=new ListViewPopupWindow(PrivateSettingActivity.this, PrivateSettingActivity.this, list_zodiacs, true, new OnAlarmSettingPopItemClickListener() {
				
				@Override
				public void onItemClick(int position) {
					tv_zodiac.setText(list_zodiacs.get(position));
					dismissListPopupWindow();
				}
			});
			// 显示窗口
			lvPopWindow.showAtLocation(rl_root, Gravity.BOTTOM| Gravity.CENTER_HORIZONTAL, 0, 0);
			AnimationUtils.showAlpha(view_mask);						
			setDismissListener();
			break;
		case R.id.ftv_hobby://兴趣爱好
			Intent intent_hobbty = new Intent(PrivateSettingActivity.this,HobbyActivity.class);
			intent_hobbty.putExtra("hobby", mLoginBean.getHobby());
			startActivityForResult(intent_hobbty, 9);
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			break;
		case R.id.ftv_gender://性别
			lvPopWindow = new ListViewPopupWindow(PrivateSettingActivity.this,
					PrivateSettingActivity.this, list_genders, true,
					new OnAlarmSettingPopItemClickListener() {
						@Override
						public void onItemClick(int position) {
							tv_gender.setText(list_genders.get(position));
							dismissListPopupWindow();
						}
					});
			// 显示窗口
			lvPopWindow.showAtLocation(rl_root, Gravity.BOTTOM| Gravity.CENTER_HORIZONTAL, 0, 0);
			AnimationUtils.showAlpha(view_mask);
			//popupWindow隐藏时候的监听
			setDismissListener();
			break;
		case R.id.ftv_birthday:// 生日
			show();
			AnimationUtils.showAlpha(view_mask);
			break;
		case R.id.ftv_education://教育水平
			lvPopWindow=new ListViewPopupWindow(PrivateSettingActivity.this, PrivateSettingActivity.this, list_education, true, new OnAlarmSettingPopItemClickListener() {
				
				@Override
				public void onItemClick(int position) {
					tv_education.setText(list_education.get(position));
					dismissListPopupWindow();
				}
			});
			
			// 显示窗口
			lvPopWindow.showAtLocation(rl_root, Gravity.BOTTOM| Gravity.CENTER_HORIZONTAL, 0, 0);
			AnimationUtils.showAlpha(view_mask);
			setDismissListener();
			break;
		case R.id.ftv_live_place://所在地
			isBirthPlace = false;
			showPlacePopupWindow();
			break;
		case R.id.ftv_income://收入水平
			lvPopWindow=new ListViewPopupWindow(PrivateSettingActivity.this, PrivateSettingActivity.this, list_income, true, new OnAlarmSettingPopItemClickListener() {
				@Override
				public void onItemClick(int position) {
					input_income=list_income.get(position);
					tv_income.setText(input_income);
					dismissListPopupWindow();
				}
			});
			// 显示窗口
			lvPopWindow.showAtLocation(rl_root, Gravity.BOTTOM| Gravity.CENTER_HORIZONTAL, 0, 0);
			AnimationUtils.showAlpha(view_mask);
			//设置popupWindow消失的监听
			setDismissListener();
			break;
		case R.id.ftv_profession://职业
			if(list_profession==null){
				list_profession=Arrays.asList(getResources().getStringArray(R.array.profession));
			}
			lvPopWindow=new ListViewPopupWindow(PrivateSettingActivity.this, PrivateSettingActivity.this, list_profession, true, new OnAlarmSettingPopItemClickListener() {
				@Override
				public void onItemClick(int position) {
					tv_profession.setText(list_profession.get(position));
					dismissListPopupWindow();
				}
			});
		
			// 显示窗口
			lvPopWindow.showAtLocation(rl_root, Gravity.BOTTOM| Gravity.CENTER_HORIZONTAL, 0, 0);
			AnimationUtils.showAlpha(view_mask);
			setDismissListener();
			break;
		case R.id.ftv_cancel:// 选择生日的弹出窗口中的“取消”
			dismissDatePickPopWindow();
			break;
		case R.id.ftv_confirm:// 选择生日的弹出窗口中的“确定”
			dismissDatePickPopWindow();
			String time = getSelectTime();
			String yearTime=time.substring(0, 4);
			int year=new Integer(yearTime);
			String nowYearTime=TimeUtil.getYear();
			int nowYear=new Integer(nowYearTime);
			tv_age.setText((nowYear-year)+"岁");
			tv_birthday.setText(time);
			break;
		case R.id.ftv_place_cancel:// 取消选择出生地按钮
			dismissPlacePopWindow();
			break;
		case R.id.ftv_place_confirm:// 确认出生地按钮
			dismissPlacePopWindow();
			String place = placePopWindow.getPlace();
			if (place.contains("请选择")) {// 如果用户没有选择准确的地址，就给出提示
				UserToast.toSetToast(PrivateSettingActivity.this, "请选择准确的地址！");
			} else {
				if (isBirthPlace) {// 如果是出生地选择框
					tv_birth_place.setText(place);
				} else {// 如果是居住地选择框
					tv_live_place.setText(place);
				}
			}
			break;
		case R.id.tv_unlogin:// 退出登录按钮
			alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.show();
			View view = View.inflate(PrivateSettingActivity.this,R.layout.alert_dialog_jiting_unlogin, null);
			View tv_confirm_unlogin = view.findViewById(R.id.tv_confirms);// “退出登录”弹出窗的“确定”按钮
			View tv_cancel_unlogin = view.findViewById(R.id.tv_cancel_unlogin);// “退出登录”弹出窗的“取消”按钮
			tv_confirm_unlogin.setOnClickListener(this);
			tv_cancel_unlogin.setOnClickListener(this);
			alertDialog.setContentView(view);
			break;
		case R.id.tv_confirms:// “退出登录”弹出窗口的“确定”按钮
			Configs.isExitLogin=true;
			if(alertDialog!=null){
				alertDialog.dismiss();
			}
			SharedPreferences preferences = getSharedPreferences("user",Context.MODE_PRIVATE);
			Editor editor = preferences.edit();
			editor.putString("name", "");
			editor.putString("pwd", "");
			editor.commit();
			Intent intentlogin=new Intent(PrivateSettingActivity.this, LoginActivity.class);
			startActivity(intentlogin);
			finish();
			//清除登录信息' 
//			SharedPreferences preferences=getSharedPreferences("loginname", MODE_PRIVATE);
//			preferences.edit().clear().commit(); //如果之前有存储则清除
			break;     
		case R.id.tv_cancel_unlogin:// “退出登录”弹出窗口的“取消”按钮
			alertDialog.dismiss();
			break;
		case R.id.tv_confirm://时间选择弹出窗的确定按钮
//			Toast.makeText(CreateLiveActivity.this, currentYear+"年"+currentMonth+"月"+today+"日"+currHour+"时"+currMinute+"分", 0).show();

            if(isStartTime){
				startTime=getSelectTime();
				tv_birthday.setText(startTime);  
			}else{
				endTime=getSelectTime();
                System.out.println("endTime==="+endTime);
                tv_birthday.setText(endTime);
			}
			//if(null!=alertDialog) {
            alertDialog1.dismiss();
		//	}
			break;
		}
	}
	
	private void show(){
//		getCurrentDate();
		alertDialog1 = new AlertDialog.Builder(this).create();
		alertDialog1.show();
		alertDialog1.setCanceledOnTouchOutside(true);
		
		View view = View.inflate(PrivateSettingActivity.this,R.layout.pop_date_pick, null);
		initView(view);
		getCurrentDate();
		
		//设置滚轮文字大小
		textSize=(int) (20*getResources().getDisplayMetrics().density);
		// 给控件设置初始数据
		setInitDataAddListener();
		alertDialog1.setContentView(view);
		alertDialog1.show();
		alertDialog1.setOnDismissListener(new DialogInterface.OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
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
	
/**
 * 给lvpopWindow设置隐藏的监听
 */
	private void setDismissListener() {
		lvPopWindow.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				AnimationUtils.hideAlpha(view_mask);
			}
		});
	}

	/**
	 * 在未退出登录的状态下返回“我的”页面
	 */
	private void backWithLogin() {
		// 返回上一个Activity
		Intent intentToMain = new Intent();
		// 是否点击的退出登录按钮（从个人设置返回MineFragment的另一种情况是按的“退出登录”按钮，故加以区分）
		intentToMain.putExtra("isUnLogin", false);
		setResult(2, intentToMain);
	}

	/**
	 * 弹出地址选择框
	 */
	private void showPlacePopupWindow() {
		placePopWindow = new PlacePickPopupWindow(PrivateSettingActivity.this,PrivateSettingActivity.this);
		placePopWindow.showAtLocation(rl_root, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
		AnimationUtils.showAlpha(view_mask);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==3){//表示拍照
			String picPath = mCreateBmpFactory.getBitmapFilePath(requestCode,resultCode, data);
			if(new File(picPath).exists()){
//				bmp = mCreateBmpFactory.getBitmapByOpt(picPath);
				bmp=BitmapUtils.yasuoimg(picPath);
//				bmp=BitmapUtils.zoomImage(mCreateBmpFactory.getBitmapByOpt(picPath), 500, 500);
				beginCrop(bmp,picPath);
//				toUpImg(picPath);
			}
		}else if(requestCode==4){//表示从相册选
//			beginCrop(data.getData());
			String picPath;
			if(data!=null){
				picPath = mCreateBmpFactory.getBitmapFilePath(requestCode,resultCode, data);
				if(picPath!=null){
					if(new File(picPath).exists()){
						if(picPath.endsWith(".gif")){
							UserToast.toSetToast(PrivateSettingActivity.this, "暂不支持该格式的文件上传");
						}else{
							bmp = mCreateBmpFactory.getBitmapByOpt(picPath);
//							bmp=BitmapUtils.yasuoimg(picPath);
//							bmp=BitmapUtils.zoomImage(mCreateBmpFactory.getBitmapByOpt(picPath), 500, 500);
//							handler.sendEmptyMessage(4);
							beginCrop(bmp,picPath);
//							toUpImg(picPath);
						}
					}
				}
			}
			//TODO
		}else if (requestCode == 5) {//用户名
			if (data != null) {// 如果不是通过点击“保存”返回而是通过按返回按钮返回的话，data会为null
				String userName = data.getStringExtra("username");
				if (userName != null) {
					tv_userName.setText(userName);
				}
			}
		} else if (requestCode == 6) {//修改手机号的Activity返回的
			if (data != null) {// 如果不是通过点击“保存”返回而是通过按返回按钮返回的话，data会为null
				String phoneNum=data.getStringExtra("phonenum");
				if (phoneNum != null) {
					// 中间四位用*表示
					tv_phone_num.setText(phoneNum);
				}
			}
		}else if(requestCode==7){//修改车牌号的界面返回的
			if(data!=null){
				String licencePlate = data.getStringExtra("carnum");
				if (licencePlate != null) {
					tv_car_number.setText(licencePlate);
				}
			}
		}else if(requestCode==8){//修改年龄的界面返回的
			if (data != null) {
				String age = data.getStringExtra("age");
				if (age != null) {
					tv_age.setText(age+"岁");
				}
			}
		}else if(requestCode==9){//表示是从修改兴趣爱好的界面返回的
			if(data!=null){
				String hobby = data.getStringExtra("hobby");
				if (hobby != null) {
					tv_hobby.setText(hobby);
				}
			}
		}else if(requestCode==Crop.REQUEST_CROP){
			handleCrop(resultCode, data);
		}
	};
	
	/**
	 * 上传完成后删除图片
	 * @param filePath
	 * @return
	 */
    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }
    
	//上传图片
	private void toUpImg(String absolutePath) {
		if(CbApplication.getNetType()!=-1){
			LoginBean loginResultBean=application.getmLoginBean();
			//获取应用版本号
			String versionName = null;
				try {
					PackageInfo info = PrivateSettingActivity.this.getPackageManager().getPackageInfo(PrivateSettingActivity.this.getPackageName(), 0);
					versionName=info.versionName;
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
			//获取手机识别码
			TelephonyManager TelephonyMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE); 
			String szImei = TelephonyMgr.getDeviceId(); 
			
			String url = String.format(Configs.userHeadUrl,absolutePath,loginResultBean.getUserID());
			RequestParams params = new RequestParams();

			params.addBodyParameter("file", new File(absolutePath));
			
			showLoadingDialog(PrivateSettingActivity.this, "", "");
			
			HttpUtils http = new HttpUtils();
			http.send(HttpMethod.POST,url, params, new RequestCallBack<String>() {

						@Override
						public void onStart() {
							
						}

						@Override
						public void onLoading(long total, long current,boolean isUploading) {
							if (isUploading) {
								int precent = (int) (current * 100) / (int) total;
								System.out.println(("uploaduploadupload: "+ current + "/" + total + "/" + precent));
							} else {
								
							}
						}

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
//							[{"message":"上传成功","status":1,"photo":"http://vod.fm.hebrbtv.com:9600/communityvod/2016_06/30/1467269159813.jpg"}]
							Configs.filePath="";
							String result=responseInfo.result;
							System.out.println("uploaduploadupload: "+result);
								JSONObject jsonObject=JSON.parseObject(result);
								String status=jsonObject.getString("status");
								if(status.equals("100")){//表示登录成功
									String dataresult=jsonObject.getString("data");
									mLoginBean=JSON.parseObject(dataresult,LoginBean.class);
									if(mLoginBean!=null){
										mHandler.sendEmptyMessage(1);
									}else{
										mHandler.sendEmptyMessage(0);
									}
								}
							dismisLoadingDialog();
						}

						@Override
						public void onFailure(HttpException error, String msg) {
							System.out.println("uploaduploadupload: "+ error.getExceptionCode() + ":" + msg);
							dismisLoadingDialog();
							Configs.filePath="";
//							handler.sendEmptyMessage(3);
						}
					});
		}else{
			UserToast.toSetToast(PrivateSettingActivity.this, "请检查网络");
		}
	}
	
	private void beginCrop(Bitmap bitmap,String filepath) {
		Configs.bitmap=bitmap;
		Intent intent=new Intent(PrivateSettingActivity.this,CropImageActivity.class);
        intent.putExtra("filePath", filepath);
        startActivityForResult(intent, 8);
        overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
    }
	
    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK) {
            System.out.println(" handleCrop: Crop.getOutput(result) "+Crop.getOutput(result));
            iv_headPhoto.setImageURI(Crop.getOutput(result));
//            mImageView.setImageURI(Crop.getOutput(result));
//            mCircleView.setImageBitmap( getCircleBitmap(Crop.getOutput(result)));
        } else if (resultCode == Crop.RESULT_ERROR) {
        }
    }
	
	@Override
	protected void onResume() {
		super.onResume();
		if(IsNonEmptyUtils.isString(Configs.filePath)){
			toUpImg(Configs.filePath);
			Configs.filePath="";
		}
	}
	
	/**
	 * 隐藏点击更改头像弹出的popupWindow
	 */
	private void hidePopupWindow() {
		if (photoWindow != null) {
			photoWindow.dismiss();
			AnimationUtils.hideAlpha(view_mask);
			isShowing = false;
		}
	}

	/**
	 * 隐藏显示listView的popupWIndow
	 */
	private void dismissListPopupWindow() {
		if (lvPopWindow != null) {
			lvPopWindow.dismiss();
			AnimationUtils.hideAlpha(view_mask);
		}
	}

	/**
	 * 隐藏选择出生日期的popWIndow
	 */
	private void dismissDatePickPopWindow() {
		if (alertDialog1 != null) {
			alertDialog1.dismiss();
			AnimationUtils.hideAlpha(view_mask);
		}
	}

	/**
	 * 隐藏选择出生地的popupWindow
	 */
	private void dismissPlacePopWindow() {
		if (placePopWindow != null) {
			placePopWindow.dismiss();
			AnimationUtils.hideAlpha(view_mask);
		}
	}




	@Override
	protected void initRequestOnSuccess(String result, int code, int biaoshi) {
		// TODO Auto-generated method stub
		if(code==Configs.personInfoCode){
			JSONObject jsonObject=JSON.parseObject(result);
			String status=jsonObject.getString("status");
			if(status.equals("100")){//表示登录成功
				String dataresult=jsonObject.getString("data");
				LoginBean loginbean=JSON.parseObject(dataresult,LoginBean.class);
				if(loginbean!=null){
					mHandler.sendEmptyMessage(2);
				}else{
					mHandler.sendEmptyMessage(0);
				}
			}else{
				message=jsonObject.getString("message");
				mHandler.sendEmptyMessage(0);
			}
		}
	}
	@Override
	protected void initRequestOnStart(String result, int code, int biaoshi) {
	}
	@Override
	protected void initRequestOnFailure(String failure, int code, int biaoshi) {
		System.out.println();
	}
	@Override
	protected void initRequestOnCache(String result, int code, int biaoshi) {
		System.out.println();
	}
}
