package com.jwzt.caibian.application;


import java.io.File;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.os.Vibrator;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.baidu.mapapi.SDKInitializer;
import com.jwzt.caibian.bean.GroupListBean;
import com.jwzt.caibian.bean.LoginBean;
import com.jwzt.caibian.channldb.SQLHelper;
import com.jwzt.caibian.rd.FaceHandler;
import com.jwzt.caibian.rd.SdkHandler;
import com.jwzt.caibian.receiver.MyNetReceiver;
import com.jwzt.caibian.util.CrashCatchHandler;
import com.jwzt.caibian.util.ExceptionHandler;
import com.jwzt.caibian.util.LocationService;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.rd.vecore.RdVECore;
import com.rd.veuisdk.MyCrashHandler;
import com.rd.veuisdk.SdkEntry;
import com.rd.veuisdk.manager.ChangeLanguageHelper;
import com.xdroid.request.XRequest;

import static android.R.attr.debuggable;

public class CbApplication extends Application {
	public static String urerName = null;
	private static Context mContext;
	public LocationService locationService;
    public Vibrator mVibrator;
    
 	private static Toast toast;
	private static int mainThreadId;
	private static Handler handler;
	
	/***监听网络类型0表示数据网络，1表示wifi，2表示有线网络，-1表示断开网络*/
	public static int netType;
	private MyNetReceiver myNetReceiver;
	
	/***当前位置信息*/
	private String poi;//附近
	private String location;//当前地址
	private double Longitude;//经度
	private double Latitude;//纬度
	
	/***当前用户所对应的群组列表*/
	private List<GroupListBean> groupList;
	/***聊天时对应的token*/
	private String chatToken;
	/***当前聊天对应的groupid 用来判断该条消息是否已读*/
	private String groupId;
	/**
	 * 已获取的AppKey
	 */
	public static final String APP_KEY = "650aef6ad3abd49f";
	/**
	 * 已获取的AppSecret
	 */
	public static final String APP_SECRET = "dcc99d65f3d302d55aefe119b9d592b0EAiz0+bEzzMGKxnjKFSn0bhc2UTFosStc85GwiCa1D1kL9EaUw49lWj6Gq514rH+8gjtUI0EPV9mk3u+FukgthOxHh9mY3Wvw6K8X3oPYfdO04K3WYrHvCxAo14ad8BiGio9Xh66mRUUfc9eEpaxXe0SwCOAdaivZRXKLKHnl4hHBdRJ4UnBmFAvwkeYE2hccsGVAJlNo/5UdTSvFNWriYFwOV32mRqIjkcF3uSYHX4r8NyJ+zTU2SZoYseojZm5uUoP+pPmJCM4qN4c9ZC29srLYN6DPsMtLAaNGWGVLdl5vF9TK1tCgQhPphDSurYx/zhZxqQtBAOXmOlhV3N8qZp/+sLALa1c9Xj3as7QM1hLB31poRkgwY2wfi6mYmLGq4+jKFI/KNdfWJGjlbb7UuO6YDgMzoFgyD5IRkwjno8oulYekXDN+GluQebwEP7txSPOI17KL6o4S04xGjHySZMI2ce0jAYRRTD8XjDUTog=";
/**
 *  MD5: 38:15:A5:2E:22:25:29:9E:A9:7E:AC:C7:BA:06:0A:E9
 SHA1: 98:F3:4F:01:81:65:56:7E:A8:E1:92:CA:C3:4B:95:A0:3D:4A:E3:95
 SHA256: 35:76:63:0D:18:8F:1E:51:FE:DF:CD:7C:69:56:63:85:12:96:04:AA:AB:D0:27:AC:8F:15:3E:04:EB:27:D2:B7


//发布版
 MD5: A4:BB:42:78:B8:69:0F:54:FC:5A:E4:30:88:2C:F3:03
 SHA1: 42:C7:C2:62:50:7A:E2:EC:A3:C1:5A:99:6F:55:CA:1F:8D:18:84:B2
 SHA256: A3:93:A0:DA:0D:CF:FD:D0:5D:D9:94:87:AB:75:89:58:61:E7:F4:DE:57:EB:4F:4E:EB:33:52:A1:92:65:D1:7C

 * */

	private static CbApplication mAppApplication;
	private SQLHelper sqlHelper;
	private String mStrCustomPath;

	@Override
	public void onCreate() {
		super.onCreate();
		mAppApplication = this;
		mContext = getApplicationContext();
		mainThreadId = android.os.Process.myTid();// 获取当前主线程id
		handler = new Handler();
		JPushInterface.setDebugMode(true);
		JPushInterface.init(this);     		// 初始化 JPush
		StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
		StrictMode.setVmPolicy(builder.build());
		builder.detectFileUriExposure();

		initializeSdk();
		ChangeLanguageHelper.init(this);
	/*	ExceptionHandler crashHandler = ExceptionHandler.getInstance();
		crashHandler.init(getApplicationContext());*/
		
		configXReqeustCache();
		initImageLoader(getApplicationContext());
		
		/***注册网络状态监听广播*/
		myNetReceiver=new MyNetReceiver();
		initBroadcastNetWork();
		
        locationService = new LocationService(getApplicationContext());
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        SDKInitializer.initialize(getApplicationContext());


	//	RdVECore.initialize(this, Configs.BLSavePath1, "", "", true);
	//	CrashCatchHandler.getInstance().init(this);
	}

	/**
	 * 初始化SDK
	 */
	public void initializeSdk() {
		// 这里确定是否启用日志，在调试阶段可开启该选项，方便定位问题。
		SdkEntry.enableDebugLog(true);
		// 自定义根目录，如果为空则默认为/sdcard/Android/data/com.rd.ve.demo/files/rdve
	//	mStrCustomPath = getExternalFilesDirEx(this, "rdve").getAbsolutePath();
		mStrCustomPath = Environment.getExternalStorageDirectory() + "/JWZTCBProduct/";
		// sdk初始
		SdkEntry.initialize(this, mStrCustomPath, APP_KEY, APP_SECRET,
				new SdkHandler().getCallBack());
		// 自定义Crash handler,实际项目可不加入
		MyCrashHandler.getInstance().init(this);
		FaceHandler.initialize(this);
	}
	@Override
	protected void attachBaseContext(Context base) {
		//7.0 以上，处理初始化时切换语言环境
		super.attachBaseContext(ChangeLanguageHelper.attachBaseContext(base,
				ChangeLanguageHelper.getAppLanguage(base)));
	}
	/**
	 * 获取自定义根目录
	 */
	public String getCustomPath() {
		return mStrCustomPath;
	}
	private File getExternalFilesDirEx(Context context, String type) {
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			File ef = context.getExternalFilesDir(type);
			if (ef != null && ef.isDirectory()) {
				return ef;
			}
		}
		return new File(Environment.getExternalStorageDirectory(), type);
	}
	public String getPoi() {
		return poi;
	}

	public void setPoi(String poi) {
		this.poi = poi;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public double getLatitude() {
		return Latitude;
	}

	public void setLatitude(double latitude) {
		Latitude = latitude;
	}

	public double getLongitude() {
		return Longitude;
	}

	public void setLongitude(double longitude) {
		Longitude = longitude;
	}

	public List<GroupListBean> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<GroupListBean> groupList) {
		this.groupList = groupList;
	}

	public String getChatToken() {
		return chatToken;
	}

	public void setChatToken(String chatToken) {
		this.chatToken = chatToken;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}



	/**
     * 返回上下文对象
     * @return
     */
    public static Context getContext() {
    	if(mContext==null){
    		return getContext();
    	}else{

            return mContext;
    	}
    }
    /**
     *设置上下文对象 *
     * @param context
     */
    public static void setContext(Context context) {
        mContext = context;
    }
    
    /**
     * 获取用户信息
     * @return
     */
    public LoginBean getmLoginBean() {
 	   LoginBean loginbean=new LoginBean();
 	   SharedPreferences sharedPreferences=getSharedPreferences("login", Context.MODE_PRIVATE);
 	   String qq=sharedPreferences.getString("qq", "");
 	   String birthday=sharedPreferences.getString("birthday", "");
 	   String profession=sharedPreferences.getString("profession", "");
 	   String education=sharedPreferences.getString("education", "");
 	   String address=sharedPreferences.getString("address", "");
 	   String gender=sharedPreferences.getString("gender", "");
 	   String level=sharedPreferences.getString("level", "");
 	   String zodiac=sharedPreferences.getString("zodiac", "");
 	   String photo=sharedPreferences.getString("photo", "");
 	   String phoneNum=sharedPreferences.getString("phoneNum", "");
 	   String bloodType=sharedPreferences.getString("bloodType", "");
 	   String userID=sharedPreferences.getString("userID", "");
 	   String point=sharedPreferences.getString("point", "");
 	   String incomeLevel=sharedPreferences.getString("incomeLevel", "");
 	   String licensePlateNum=sharedPreferences.getString("licensePlateNum", "");
 	   String nickname=sharedPreferences.getString("nickname", "");
 	   String exp=sharedPreferences.getString("exp", "");
 	   String department=sharedPreferences.getString("department", "");
 	   String email=sharedPreferences.getString("email", "");
 	   String age=sharedPreferences.getString("age", "");
 	   String username=sharedPreferences.getString("username", "");
 	   String hobby=sharedPreferences.getString("hobby", "");
 	   String password=sharedPreferences.getString("password", "");
 	   String departmentId=sharedPreferences.getString("departmentId", "");
 	   String departmentName=sharedPreferences.getString("departmentName", "");
 	   String licensePlateNum1=sharedPreferences.getString("licensePlateNum1", "");
 	   String licensePlateNum2=sharedPreferences.getString("licensePlateNum2", "");
 	   String licensePlateNum3=sharedPreferences.getString("licensePlateNum3", "");
 	   
 	   loginbean.setQq(qq);
 	   loginbean.setBirthday(birthday);
 	   loginbean.setProfession(profession);
 	   loginbean.setEducation(education);
 	   loginbean.setAddress(address);
 	   loginbean.setGender(gender);
 	   loginbean.setLevel(level);
 	   loginbean.setZodiac(zodiac);
 	   loginbean.setPhoto(photo);
 	   loginbean.setPhoneNum(phoneNum);
 	   loginbean.setBloodType(bloodType);
 	   loginbean.setUserID(userID);
 	   loginbean.setPoint(point);
 	   loginbean.setIncomeLevel(incomeLevel);
 	   loginbean.setLicensePlateNum(licensePlateNum);
 	   loginbean.setNickname(nickname);
 	   loginbean.setExp(exp);
 	   loginbean.setDepartment(department);
 	   loginbean.setEmail(email);
 	   loginbean.setAge(age);
 	   loginbean.setUsername(username);
 	   loginbean.setHobby(hobby);
 	   loginbean.setPassword(password);
 	   loginbean.setDepartmentId(departmentId);
 	   loginbean.setDepartmentName(departmentName);
 	   loginbean.setLicensePlateNum1(licensePlateNum1);
 	   loginbean.setLicensePlateNum2(licensePlateNum2);
 	   loginbean.setLicensePlateNum3(licensePlateNum3);
 		return loginbean;
 	}

    /**
     * 保存用户信息
     * @param mLoginBean
     */
 	public void setmLoginBean(LoginBean mLoginBean) {
 		SharedPreferences sharedPreferences=getSharedPreferences("login", Context.MODE_PRIVATE);
 		Editor editor = sharedPreferences.edit();
 		editor.putString("qq", mLoginBean.getQq());
 		editor.putString("birthday", mLoginBean.getBirthday());
 		editor.putString("profession", mLoginBean.getProfession());
 		editor.putString("education", mLoginBean.getEducation());
 		editor.putString("address", mLoginBean.getAddress());
 		editor.putString("gender", mLoginBean.getGender());
 		editor.putString("level", mLoginBean.getLevel());
 		editor.putString("zodiac", mLoginBean.getZodiac());
 		editor.putString("photo", mLoginBean.getPhoto());
 		editor.putString("phoneNum", mLoginBean.getPhoneNum());
 		editor.putString("bloodType", mLoginBean.getBloodType());
 		editor.putString("userID", mLoginBean.getUserID());
 		editor.putString("point", mLoginBean.getPoint());
 		editor.putString("incomeLevel", mLoginBean.getIncomeLevel());
 		editor.putString("licensePlateNum", mLoginBean.getLicensePlateNum());
 		editor.putString("nickname", mLoginBean.getNickname());
 		editor.putString("exp", mLoginBean.getExp());
 		editor.putString("department", mLoginBean.getDepartment());
 		editor.putString("email", mLoginBean.getEmail());
 		editor.putString("age", mLoginBean.getAge());
 		editor.putString("username", mLoginBean.getUsername());
 		editor.putString("hobby", mLoginBean.getHobby());
 		editor.putString("password", mLoginBean.getPassword());
 		editor.putString("departmentId", mLoginBean.getDepartmentId());
 		editor.putString("departmentName", mLoginBean.getDepartmentName());
 		editor.putString("licensePlateNum1", mLoginBean.getLicensePlateNum1());
 		editor.putString("licensePlateNum2", mLoginBean.getLicensePlateNum2());
 		editor.putString("licensePlateNum3", mLoginBean.getLicensePlateNum3());
 		editor.commit();
 	}
 	
	public static int getNetType() {
		return netType;
	}
	public static void setNetType(int netType) {
		CbApplication.netType = netType;
	}

	public static int getMainThreadId() {
		return mainThreadId;
	}

	public static Handler getHandler() {
		return handler;
	}
	
	public static void initImageLoader(Context context) {  
        //缓存文件的目录  
        File cacheDir = StorageUtils.getOwnCacheDirectory(context, "/CAIBIAN/images/");   
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)  
                .memoryCacheExtraOptions(400, 300) // max width, max height，即保存的每个缓存文件的最大长宽   
                .threadPoolSize(12) //线程池内线程的数量  
                .threadPriority(Thread.NORM_PRIORITY)  
                .denyCacheImageMultipleSizesInMemory()  
                .memoryCache(new WeakMemoryCache()) 
                .diskCacheFileNameGenerator(new Md5FileNameGenerator()) //将保存的时候的URI名称用MD5 加密  
                .memoryCache(new UsingFreqLimitedMemoryCache(12 * 1024 * 1024))  
                .memoryCacheSize(5* 1024 * 1024) // 内存缓存的最大值  
                .diskCacheSize(200 * 1024 * 1024)  // SD卡缓存的最大值  
                .tasksProcessingOrder(QueueProcessingType.LIFO)  
                // 由原先的discCache -> diskCache  
                .diskCache(new UnlimitedDiskCache(cacheDir))//自定义缓存路径    
                .imageDownloader(new BaseImageDownloader(context, 3 * 1000, 15 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间    
                .build();  
        //全局初始化此配置    
        ImageLoader.getInstance().init(config);  
    }
	
	@SuppressLint("SdCardPath")
	private void configXReqeustCache() {
		//磁盘缓存路径
		File DISK_CACHE_DIR_PATH = new File("/sdcard/caibianData/diskcache");
		//磁盘缓存最大值
		int DISK_CACHE_MAX_SIZE = 50*1024*1024;
		//XRequest.initXRequest(getApplicationContext());
		XRequest.initXRequest(getApplicationContext(), DISK_CACHE_MAX_SIZE, DISK_CACHE_DIR_PATH);
	}
	
	/**
	 * 网络类型切换时的监听
	 */
	private void initBroadcastNetWork(){
		 IntentFilter mFilter = new IntentFilter();  
	     mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);  
	     registerReceiver(myNetReceiver, mFilter); 
	}

	public static Toast getToast(){ return toast;}

	public static CbApplication getApp() {
		return mAppApplication;
	}

	/**
	 * 获取数据库Helper
	 */
	public SQLHelper getSQLHelper() {
		if (sqlHelper == null)
			sqlHelper = new SQLHelper(mAppApplication);
		return sqlHelper;
	}
}
