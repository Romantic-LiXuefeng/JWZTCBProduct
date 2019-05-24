package com.jwzt.caibian.util;

import java.io.File;

import android.os.Environment;


/**
 * ���ȫ�ֻ�����
 * 
 */
public class GlobalData {
	/** SD����·�� **/
	public static String SDcardPaht = getSDcardPath();

	public static final String CameraFile = SDcardPaht + "/MosaicImageView";
	public static final String CameraPhoto = SDcardPaht + "/MosaicImageView/temp.jpg";
	public static final String tempImagePaht = SDcardPaht + "/MosaicImageView/Temp";

	/** ��ȡSD��·�� **/
	public static String getSDcardPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED); // �ж�sd���Ƿ����
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// ��ȡ��Ŀ¼
		}
		if(sdDir ==null){
			return "/mnt/sdcard";
		}
		return sdDir.toString();
	}
	
	
}