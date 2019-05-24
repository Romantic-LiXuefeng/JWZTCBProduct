package com.jwzt.caibian.util;

import java.io.File;
import java.io.FileOutputStream;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * 动态申请权限的工具类
 * 兼容android6.0以上系统
 * @author hly
 *
 */
public class DynamicRequestPermissionUtils {
	
	/**
	 * 动态申请权限 读写sd卡权限
	 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
	 * @param activity activity对象
	 * @param requsetpermission 需要申请的权限的数据
	 * @param requestCode 申请权限的请求码
	 */
	public static void requestPermissionSDcard(Activity activity,String[] requsetpermission,int requestCode){
		if(Build.VERSION.SDK_INT>=23){
	        //判断是否有这个权限
	        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
	            //2、申请权限: 参数二：权限的数组；参数三：请求码
	            ActivityCompat.requestPermissions(activity,requsetpermission,requestCode);
	        }
		}
	}
	
	/**
	 * 判断是否有SD卡的读写权限
	 * @param activity
	 * @return true表示有 false表示没有
	 */
	public static boolean isReadSDcard(Activity activity){
	   if(ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
	      return false;
	   }else{
	      return true;
	   }
	}
	
	/**
	 * 获取手机信息设备号等权限
	 * <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
	 * @param activity
	 * @param requsetpermission
	 * @param requestCode
	 */
	public static void requestPermissionREADPHONESTATE(Activity activity,String[] requsetpermission,int requestCode){
		if(Build.VERSION.SDK_INT>=23){
	        //判断是否有这个权限
	        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED){
	            //2、申请权限: 参数二：权限的数组；参数三：请求码
	            ActivityCompat.requestPermissions(activity,requsetpermission,requestCode);
	        }
		}
	}
	
	/**
	 * 相机权限
	 * <uses-permission android:name="android.permission.CAMERA"/>
	 * @param activity
	 * @param requsetpermission
	 * @param requestCode
	 */
	public static void requestPermissionCAMERA(Activity activity,String[] requsetpermission,int requestCode){
		if(Build.VERSION.SDK_INT>=23){
	        //判断是否有这个权限
	        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
	            //2、申请权限: 参数二：权限的数组；参数三：请求码
	            ActivityCompat.requestPermissions(activity,requsetpermission,requestCode);
	        }
		}
	}
	
	/**
	 * 判断是否有拍照权限
	 * @param activity
	 * @return
	 */
	public static boolean isCAMERA(Activity activity){
		//判断是否有这个权限
		if(ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * 位置权限
	 * <uses-permissionandroid:name="android.permission.ACCESS_FINE_LOCATION"/>
	 * @param activity
	 * @param requsetpermission
	 * @param requestCode
	 */
	public static void requestPermissionACCESSFINELOCATION(Activity activity,String[] requsetpermission,int requestCode){
		if(Build.VERSION.SDK_INT>=23){
	        //判断是否有这个权限
	        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
	            //2、申请权限: 参数二：权限的数组；参数三：请求码
	            ActivityCompat.requestPermissions(activity,requsetpermission,requestCode);
	        }
		}
	}
	
	/**
	 * 判断是否有定位权限
	 * @param activity
	 * @param requsetpermission
	 * @param requestCode
	 * @return
	 */
	public static boolean isLOCATION(Activity activity,String[] requsetpermission,int requestCode){
		//判断是否有这个权限
		if(ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * 位置权限
	 * <uses-permissionandroid:name="android.permission.ACCESS_FINE_LOCATION"/>
	 * @param activity
	 * @param requsetpermission
	 * @param requestCode
	 */
	public static void requestPermissionACCESSCOARSELOCATION(Activity activity,String[] requsetpermission,int requestCode){
		if(Build.VERSION.SDK_INT>=23){
	        //判断是否有这个权限
	        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
	            //2、申请权限: 参数二：权限的数组；参数三：请求码
	            ActivityCompat.requestPermissions(activity,requsetpermission,requestCode);
	        }
		}
	}
	
	/**
	 * 读取联系人权限
	 * <uses-permissionandroid:name="android.permission.READ_CONTACTS"/>
	 * @param activity
	 * @param requsetpermission
	 * @param requestCode
	 */
	public static void requestPermissionREADCONTACTS(Activity activity,String[] requsetpermission,int requestCode){
		if(Build.VERSION.SDK_INT>=23){
	        //判断是否有这个权限
	        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS)!=PackageManager.PERMISSION_GRANTED){
	            //2、申请权限: 参数二：权限的数组；参数三：请求码
	            ActivityCompat.requestPermissions(activity,requsetpermission,requestCode);
	        }
		}
	}
	
	/**
	 * 拨打电话权限
	 * <uses-permissionandroid:name="android.permission.CALL_PHONE"/>
	 * @param activity
	 * @param requsetpermission
	 * @param requestCode
	 */
	public static void requestPermissionCALLPHONE(Activity activity,String[] requsetpermission,int requestCode){
		if(Build.VERSION.SDK_INT>=23){
	        //判断是否有这个权限
	        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE)!=PackageManager.PERMISSION_GRANTED){
	            //2、申请权限: 参数二：权限的数组；参数三：请求码
	            ActivityCompat.requestPermissions(activity,requsetpermission,requestCode);
	        }
		}
	}
	
	/**
	 * 麦克风权限
	 * <uses-permission android:name="android.permission.RECORD_AUDIO"/>
	 * @param activity
	 * @param requsetpermission
	 * @param requestCode
	 */
	public static void requestPermissionRECORDAUDIO(Activity activity,String[] requsetpermission,int requestCode){
		if(Build.VERSION.SDK_INT>=23){
	        //判断是否有这个权限
	        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED){
	            //2、申请权限: 参数二：权限的数组；参数三：请求码
	            ActivityCompat.requestPermissions(activity,requsetpermission,requestCode);
	        }
		}
	}
	
	/**
	 * 判断是否有录音权限
	 * @param activity
	 * @return
	 */
	public static boolean isRECORDAUDIO(Activity activity){
		//判断是否有这个权限
		if(ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * 震动权限
	 * <uses-permission android:name="android.permission.VIBRATE"/>
	 * @param activity
	 * @param requsetpermission
	 * @param requestCode
	 */
	public static void requestPermissionVIBRATE(Activity activity,String[] requsetpermission,int requestCode){
		if(Build.VERSION.SDK_INT>=23){
			//判断是否有这个权限
			if(ContextCompat.checkSelfPermission(activity, Manifest.permission.VIBRATE)!=PackageManager.PERMISSION_GRANTED){
				//2、申请权限: 参数二：权限的数组；参数三：请求码
				ActivityCompat.requestPermissions(activity,requsetpermission,requestCode);
			}
		}
	}
	
    //写数据
//    public void writeToSdCard(){
//        //1、判断sd卡是否可用
//        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
//            //sd卡可用
//            //2、获取sd卡路径
//            File sdFile=Environment.getExternalStorageDirectory();
//            File path=new File(sdFile,"c.txt");//sd卡下面的a.txt文件  参数 前面 是目录 后面是文件
//            try {
//            	//当第二个参数为true时表示可追加写入内容 否则表示覆盖源文件写入内容
//                FileOutputStream fileOutputStream=new FileOutputStream(path,true);
//                fileOutputStream.write("hello".getBytes());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

}
