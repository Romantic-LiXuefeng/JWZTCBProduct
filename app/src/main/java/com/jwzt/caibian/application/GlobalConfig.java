package com.jwzt.caibian.application;

import android.os.Environment;

public class GlobalConfig {
	/**推流地址*/
	public static final String URL_RTMP_PUSH="rtmp://rtmppush.ejucloud.com/ehoush/liuy1";
	
	public static final String URL_RTMP_PLAY="rtmp://rtmppush.ejucloud.com/ehoush/liuy1";
	/**直播的时候的视频的保存路径*/
	public static final String PATH_LIVE_RECORD=Environment.getExternalStorageDirectory().getAbsolutePath()+"/JwztLiveRecord/";
	/**码率范围*/
	public static final String RANGE_BITRATE="RANGE_BITRATE";
	
	public static final String RESOLUTION_480P="640x480";
	
	public static final String RESOLUTION_720P="1280x720";
	
	public static final String RESOLUTION_1080P="1920x1080";
	
	public static final String MAX_BITRATE="MAX_BITRATE";
	
	public static final String MIN_BITRATE="MIN_BITRATE";
	
	public static final String CUR_BITRATE="CUR_BITRATE";
}
