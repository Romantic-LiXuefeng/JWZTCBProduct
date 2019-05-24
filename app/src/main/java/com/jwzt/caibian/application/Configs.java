package com.jwzt.caibian.application;

import java.io.File;

import com.jwzt.caibian.interfaces.NetWorkContinueInterface;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Environment;

//import com.hebei.jiting.jwzt.bean.FindDetailBean;

@SuppressLint("SdCardPath")
public class Configs {
//	public static String poi="";//附近
//	public static String location="";//当前位置
//	public static double Latitude=0;//经度
//	public static double Longitude=0;//纬度
	
	
	public static boolean firstChecks=false;
	public static boolean secondChecks=false;
	public static boolean newsReadOnly=false;
	
	public static final int RESOLUTION_480P=0;
	public static final int RESOLUTION_720P=1;
	public static final int RESOLUTION_1080P=2;   
	public static final int RESOLUTION_1920P=3;
	public static final int RESOLUTION_default=4;
	/**合成的视频保存的路径*/
	public static final String COMPOSE_VIDEO_PATH="/CAIBIAN/MixVideo";
	/**最终合成的视频保存的路径*/
	public static final String FINAL_VIDEO_PATH="/CAIBIAN/MixVideos";
	/**添加完水印的视频的保存路径*/
	public static final String WATER_MARK_PATH="/CAIBIAN/WaterMark";
	/**裁剪过的音频保存的路径*/
	public static final String AUDIO_CUT_PATH="/CAIBIAN/AudioCut";
	/**拍照保存的路径*/
	public static final String PHOTO_PATH="/CAIBIAN/Photo";
	/**草稿的保存路径*/
	public static final String CAOGAO_PATH="/CAIBIAN/caogao/";
	/**文档的保存路径*/
	public static final String DOCUMENT_PATH="/CAIBIAN/document/";
	/**经过旋转的用于预览的视频的路径*/
	public static final String ROTATE_VIDEO_PATH="/CAIBIAN/Preview";
	/***图片保存位置*/
	public static final String DOWNLOAD_IMAGE_PATH="/CAIBIAN/download";
	public static int lastTime=0;
	/***头像上传时选择的需要裁剪的图片，由于图片可能比较大不能通过intent传递，所以定义到全局*/
	public static Bitmap bitmap;
	/***头像的图片裁剪后保存在本地的路径*/
	public static String filePath;
	/***拍摄的路径*/
	public static File tempFile;
	public static String saveCutFilePath;
	/***视频文件保存路径*/
	public static final String BLSavePath=Environment.getExternalStorageDirectory() + "/jwzt_recorder/";
	public static final String BLSavePath1=Environment.getExternalStorageDirectory() + "/jwzt_CAIBIAN/";
	/***图片文件保存路径*/
	public static final String BLSavePathImg=Environment.getExternalStorageDirectory() + "/CAIBIAN/Images/";
	/***音频保存路径*/
	public static final String BLSavePathAudio=Environment.getExternalStorageDirectory() + "/CAIBIAN/Audio/";
	/***视频编辑的时候分段裁剪的视频的保存路径*/
	public static final String BLEditSegments=Environment.getExternalStorageDirectory() + "/CAIBIAN/EditSegments/";
	/****/
	public static final int VIDEO_FRAMERATE = 20;
	public static final String BASE_DIR = "/org.easydarwin.video";
	public static final String TMP_DIR = BASE_DIR + "/tmp";
	public static final String VS_DIR = BASE_DIR + "/.vs";
	public static final String VIDEO_TMP_DIR = TMP_DIR + "/.video";
	public static final float MAX_RECORD_TIME = 15 * 1000f;
	public static final float MIN_RECORD_TIME = 2 * 1000f;
	
	public static final int STICKER_BTN_HALF_SIZE = 30;
	/***标识是否退出登录 true退出登录 false非退出登录*/
	public static boolean isExitLogin=false;
	/***标识未上传的是否上传*/
	public static boolean isUpload=false;
	/***断网后重新连接续传的接口回调*/
	public static NetWorkContinueInterface mContinueInterface;
	
	/***网络请求域名（内网）*/
//	public static String ICON_URL="http://192.168.1.192:8080";//御景测试环境
//	public static String ICON_URL="http://192.168.1.166:8080";
//	public static String ICON_URL="http://192.168.1.66:8080";
//	public static String +ICON_URL="http://47.93.187.130:8080";//公网测试环境
//	public static String ICON_URL="http://192.168.1.176:8080";//公网环境
//	public static String ICON_URL="http://47.93.185.248:9080";//公网环境
//	public static String ICON_URL="http://60.164.165.248:8484";//敦煌外网
	//public static String ICON_URL="http://caiji.todaydunhuang.com:8484";//敦煌外网
	//public static String ICON_URL="http://192.168.1.12:8484";//敦煌外网
	//public static String ICON_URL="http://172.16.5.16:8484";//敦煌外网
	//public static String ICON_URL="http://www.iptv-soft.com:8484";
	public static String ICON_URL="http://demo2.iptv-soft.com:8484";//浪潮
	//public static String ICON_URL="http://Manage.iptv-soft.com:8484";//公司外网

//	public static String ICON_URL="http://47.93.185.248:8095";//公网环境和第三方平台对接是用的端口
//	public static String ICON_URL="http://192.168.1.52:9080";//嘉华测试环境
//	public static String ICON_URL="http://192.168.1.164:";//公网环境
	
	/***聊天服务的host*/
//	public static String HTTPIP="192.168.1.192";//御景测试环境
//	public static String HTTPIP="192.168.1.166";
//	public static String HTTPIP="192.168.1.66";
//	public static String HTTPIP="47.93.187.130";//公网测试环境
//	public static String HTTPIP="47.93.185.248";//公网环境
//	public static String HTTPIP="47.93.185.248";//敦煌外网
	public static String HTTPIP="caiji.todaydunhuang.com";//敦煌外网
//	public static String HTTPIP="192.168.1.52";//嘉华测试环境
	
	
	/***登录接口*/
	public static String loginUrl=ICON_URL+"/bvCaster_user/phone/loginNoCap.jspx?phoneNum=%s&password=%s";
	public static int loginCode=5000;
	
	/***修改用户头像*/
	public static String userHeadUrl=ICON_URL+"/bvCaster_user/phone/phoneUserImgUpdate.jspx?file=%s&userID=%s";
	public static int userHeadCode=5001;
	
	/***分类接口*/
	public static String typeUrl=ICON_URL+"/bvCaster_converge/phone/category/tree.jspx?userId=%s";
	public static int typeCode=5002;
	
	/*** 文稿列表（已上传列表）*/  
	public static String manuscriptUrl= ICON_URL+"/bvCaster_content/phone/phoneNews/v_newsListGetByUserId.do?userId=%s";
	public static int manuscriptCode=5003;
	/**
	 * 
	 */
	public static String manuscriptUrlmore= ICON_URL+"/bvCaster_content/phone/phoneNews/v_newsListGetByUserId.do?userId=%s&startId=%s&size=15";

	
	/*** 稿件已上传信息接口*/
	public static String manuscriptDetailsUrl= ICON_URL+"/bvCaster_converge/phone/media/view.jspx?id=%s";
	public static int manuscriptDetailsCode=5004;
	
	/***任务列表*/
//	public static String taskListUrl=ICON_URL+"/bvCaster_direct/admin/phoneTaskList.jspx?userId=%s";
	public static String taskListUrl=ICON_URL+"/bvCaster_direct/phone/phoneTaskList.jspx?userId=%s";
	public static String xiaoxiListUrl=ICON_URL+"//bvCaster_content/phone/phoneContentMessage/v_getList.do?userId=%s&size=%s&startId=%s";
	//待审核
	public static String daishenheListUrl=ICON_URL+"/bvCaster_content/phone/phoneNews/v_getList.do?userId=%s&type=%s&startId=%s&size=10";
	
	//已经审核
	public static String  yijingshenhe=ICON_URL+"/bvCaster_content/phone/phoneNews/v_getList.do?userId=%s&type=%s&startId=%s";
	
	
	//退回
	public static String  tuihuishenhe=ICON_URL+"/bvCaster_content/phone/phoneNews/v_getList.do?userId=%s&type=%s&startId=%s";

	
    public static String huifuUrl=ICON_URL+"/bvCaster_content/phone/phoneContentMessage/o_messageReply.do?userId=%s&messageId=%s&message=%s";


    public static String message=ICON_URL+"/bvCaster_content/phone/phoneContentMessage/v_edit.do?id=%s";
	
	/***根据不同的任务状态获取数据*/
//	public static String taskListStatusUrl=ICON_URL+"/bvCaster_direct/admin/phoneTaskList.jspx?userId=%s&taskStatus=%s";
	public static String taskListStatusUrl=ICON_URL+"/bvCaster_direct/phone/phoneTaskList.jspx?userId=%s&taskStatus=%s";
	public static int taskListCode=5005;
	
	/***任务详情*/
//	public static String taskDetailsUrl=ICON_URL+"/bvCaster_direct/admin/phoneTaskView.jspx?id=%s";
	public static String taskDetailsUrl=ICON_URL+"/bvCaster_direct/phone/phoneTaskView.jspx?id=%s";
	public static int taskDetailsCode=5006;
	
	/***首页获取信息列表*/
	//public static String infoListUrl=ICON_URL+"/bvCaster_converge/phone/log/v_list.do?userId=%s&startId=%s&size=%s";
	public static String infoListUrl=ICON_URL+"/bvCaster_direct/phone/topicSelection/getCheckPage.jspx?userId=%s&pageSize=%s&pageNo=%s";
	public static int infoListCode=5007;
	
	/***获取房间列表*/
//	public static String groupListUrl=ICON_URL+"/bvCaster_direct/admin/phoneGetGroupList.jspx?userId=%s";
	public static String groupListUrl=ICON_URL+"/bvCaster_direct/phone/phoneGetGroupList.jspx?userId=%s";
	public static int groupListCode=5008;
	
	/***获取token*/
//	public static String tokenUrl=ICON_URL+"/bvCaster_direct/admin/phoneIMLogin.jspx?userId=%s&username=%s";
	public static String tokenUrl=ICON_URL+"/bvCaster_direct/phone/phoneIMLogin.jspx?userId=%s&username=%s";
	public static int tokenCode=5009;
	
	/***群组交流中发送消息的接口*/
//	public static String sendMessageUrl=ICON_URL+"/bvCaster_direct/admin/phoneSendMessage.jspx?userId=%s&groupId=%s&content=%s&msgType=%s&IMEI=%s";
	public static String sendMessageUrl=ICON_URL+"/bvCaster_direct/phone/phoneSendMessage.jspx?userId=%s&groupId=%s&content=%s&msgType=%s&IMEI=%s";
	public static int sendMessageCode=5010;
	
	/***获取首页统计数量接口*/
	public static String countUrl=ICON_URL+"/bvCaster_converge/phone/media/count.jspx?userId=%s";
	public static int countCode=5011;
	
	/***修改个人信息*/
	public static String personInfoUrl=ICON_URL+"/bvCaster_user/phone/phoneUserInfoUpdate.jspx";
	public static int personInfoCode=5012;
	
	/***直播回传列表*/
	public static String liveBackListUrl=ICON_URL+"/bvCaster_live/phone/live/activity/v_list.jspx?userId=%s&startId=%s&size=%s";
	public static int liveBackListCode=5013;
	
	/**
	 * 获取串联单待审
	 */
	public static String getChuanliandandaishen=ICON_URL+"/bvCaster_content/phone/phoneProgramme/v_getList.do?userId=%s&type=1&startId=%s&size=%s";
/**
 * 获取串联单的个数
 * */
public static String getChuanlianDanNum=ICON_URL+"/bvCaster_content/phone/phoneNews/v_getListCount.do?userId=%s&type=%s";
	/**
	 * 获取串联单已经审核
	 */
	public static String getChuanliandanyishen=ICON_URL+"/bvCaster_content/phone/phoneProgramme/v_getList.do?userId=%s&type=2&startId=%s&size=%s";
	/**
	 * 获取串联单稿件
	 */
	public static String getChuanliandangao=ICON_URL+"/bvCaster_content/phone/phoneProgramme/v_getProgrammeBean.do?id=%s";
	public static String getChuandiandanMessage=ICON_URL+"/bvCaster_content/phone/phoneProgramme/getSinglebean.do?id=%s";
	
	
	
	/***创建直播*/
	public static String createLiveUrl=ICON_URL+"/bvCaster_live/phone/live/activity/o_save.jspx";
	public static int createLiveCode=5014;
	
	/***获取直播详情包含直播流地址*/
	public static String liveDetailsUrl=ICON_URL+"/bvCaster_live/phone/live/activity/v_view.jspx";
	public static int liveDetailsCode=5015;
	
	/***直播logo数据*/
	public static String liveLogoUrl=ICON_URL+"/bvCaster_live/phone/live/logo/v_list.jspx";
	public static int liveLogoCode=5016;
	
	/***图片水印数据*/
	public static String shuiyingUrl=ICON_URL+"/bvCaster_converge/phone/watermark/v_list.jspx";
	public static int shuiyingCode=5017;
	//&status=4&meaasge=111
	//审核选题
	public static String shenhexuanti=ICON_URL+"/bvCaster_content/phone/phoneNews/o_newsCheck.do?userId=%s&newsId=%s&status=1";
	
	
	
	//稿件提交审核http://caiji.todaydunhuang.com:8484/bvCaster_content/phone/phoneNews/o_newsSubmit.do?newsId=115
	public static String gaojiantijiao=ICON_URL+"/bvCaster_content/phone/phoneNews/o_newsSubmit.do?newsId=%s&userId=%s";

	//驳回选题
	public static String bohuixuanti=ICON_URL+"/bvCaster_content/phone/phoneNews/o_newsCheck.do?userId=%s&newsId=%s&status=4&meaasge=%s";

	//串联单审核驳回
	public static String chuanliadnabohuixuanti=ICON_URL+"/bvCaster_content/phone/phoneProgramme/o_programmeCheck.do?userId=%s&programmeId=%s&status=4&meaasge=%s";
	//串联单审核通过
	public static String chuanliadshenhetongguo=ICON_URL+"/bvCaster_content/phone/phoneProgramme/o_programmeCheck.do?userId=%s&programmeId=%s&status=1";

	
	
	//获取稿件详情：
	public static String gaojianxiangqing=ICON_URL+"/bvCaster_content/phone/phoneNews/v_getNewsBean.do?&newsId=%s";

	//http://caiji.todaydunhuang.com:8484/
	
	//串联单排序
	public static String chuanliandanpaixu=ICON_URL+"/bvCaster_content/phone/phoneProgramme/o_update.do?newsIds=%s&programmeId=%s&userId=%s";
	
	//获取消息详情接口
	public static String xiaoxidetail=ICON_URL+"/bvCaster_content/phone/phoneContentMessage/v_edit.do?id=%s";

	//修改稿件内容接口
	public static String xiugaiContent=ICON_URL+"/bvCaster_content/phone/phoneNews/o_newsUpdate.do";
	
	//提交审核
	public static String tijiaoshenhe=ICON_URL+"/bvCaster_content/phone/phoneNews/o_newsSubmit.do?newsId=";
	
	//获取审核权限
	public static String shenhequanxian=ICON_URL+"/bvCaster_content/phone/phoneUser/v_getUserPermission.do?userId=";
}
