package com.jwzt.caibian.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Semaphore;


import com.jwzt.caibian.application.Configs;
import com.jwzt.caibian.widget.InfiniteProgressView;
import com.jwzt.caibian.widget.ProgressView;



import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.CameraProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;

/**
 * Recorder controller, used to start,stop record, and combine all the videos
 * together
 * 
 *
 * 
 */

public class RecorderManager {
	/**是正常拍摄还是断点拍摄*/
	private boolean isNormal=true;
	
	private int currentResolution = Configs.RESOLUTION_480P;
	private MediaRecorder mediaRecorder = null;
	private CameraManager cameraManager = null;
	private String parentPath = null;
	/**用于记录最后一个断点的位置*/
	private int last;
	private ArrayList<String> videoTempFiles = new ArrayList<String>();
	private SurfaceView mySurfaceView = null;
	/**最长的录制时间*/
//	public int maxTime = 10000;
	public int maxTime = 20*60*1000;
	private boolean isMax = false;
	private long videoStartTime;
	private int totalTime = 0;
	private boolean isStart = false;
	Activity mainActivity = null;
	private final Semaphore semp = new Semaphore(1);
	/**用于记录正常拍摄模式下的视频保存路径*/
	private String finalNormalPath;
	private ProgressView pv;
	private InfiniteProgressView ipv;
	public RecorderManager(CameraManager cameraManager,
			SurfaceView mySurfaceView, Activity mainActivity) {
		this.cameraManager = cameraManager;
		this.mySurfaceView = mySurfaceView;
		this.mainActivity = mainActivity;
		resetPath();
		
		reset(currentResolution);
	}
	
	public InfiniteProgressView getIpv() {
		return ipv;
	}

	public void setIpv(InfiniteProgressView ipv) {
		this.ipv = ipv;
	}

	/**
	 *根据是正常拍摄还是断点拍摄设置不同的视频的保存路径
	 */
	private void resetPath() {
		if(isNormal){
			parentPath=generateFinalFolder();
		}else{
			parentPath = generateParentFolder();
		}
	}

	private Camera getCamera() {
		return cameraManager.getCamera();
	}

	public boolean isStart() {
		return isStart;
	}

	public long getVideoStartTime() {
		return videoStartTime;
	}

	/**
	 * 获取已经录制的时间
	 * 
	 * @param timeNow
	 * @return
	 */
	public int checkIfMax(long timeNow) {   
		int during = 0;
		if (isStart) {
			during = (int) (totalTime + (timeNow - videoStartTime));
//			last=during;
			if (during >= maxTime) {
				stopRecord();
				during = maxTime;
				last=during;
				isMax = true;
			}
		} else {
//			during = totalTime;
//			if (during >= maxTime) {
//				during = maxTime;
//			}
			during=last;
		}

//		Log.v("duration&last&total", "duration="+during+",last="+last+",total="+totalTime);
		if(pv!=null){
			ArrayList<Float> pausePoints = pv.getPausePoints();
			if(pausePoints!=null&&!pausePoints.isEmpty()){
				Float float1 = pausePoints.get(0);
				Log.v("第一个百分比", float1+"");
			}
		}
		return during;   
	}

	/**
	 * 根据指定的分辨率进行重置操作
	 * 
	 * @param resoulution
	 */
	public void reset(int resoulution) {
		videoTempFiles = new ArrayList<String>();
		isStart = false;
		totalTime = 0;
		isMax = false;
		currentResolution = resoulution;
		videoStartTime = 0;
	}

	/**
	 * 删除临时文件
	 */
	public void deleteTempFiles() {
		if (videoTempFiles != null && !videoTempFiles.isEmpty()) {
			for (String file : videoTempFiles) {
				File tempFile = new File(file);
				if (tempFile.exists()) {
					tempFile.delete();
				}
			}

		}
	}
	
	/**
	 * 重置操作
	 */
	public void reset() {
		// for (String file : videoTempFiles) {
		// File tempFile = new File(file);
		// if (tempFile.exists()) {
		// tempFile.delete();
		// }
		// }
//		ipv.doClear();
//		pv.doClear();
		deleteTempFiles();
		videoTempFiles = new ArrayList<String>();
		isStart = false;
		totalTime = 0;
		isMax = false;
		videoStartTime = 0;
	}

	public ArrayList<String> getVideoTempFiles() {
		return videoTempFiles;
	}

	public String getVideoParentpath() {
		return parentPath;
	}

	/**
	 * 
	 * @param isFirst
	 * @param resolution
	 *            分辨率
	 */
	public void startRecord(boolean isFirst) {
		if (isMax) {
			return;
		}
		semp.acquireUninterruptibly();  
		getCamera().stopPreview();   
		mediaRecorder = new MediaRecorder();  
		cameraManager.getCamera().unlock();
		mediaRecorder.setCamera(cameraManager.getCamera());   
		if (cameraManager.isUseBackCamera()) {
			mediaRecorder.setOrientationHint(90);
		} else {// 前置摄像头
			mediaRecorder.setOrientationHint(270);
		}
		Size defaultSize = cameraManager.getDefaultSize();

		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		mediaRecorder.setProfile(CamcorderProfile
				.get(CameraProfile.QUALITY_MEDIUM));
		if (defaultSize != null) {
			mediaRecorder.setVideoSize(defaultSize.width, defaultSize.height);
		} else {
			switch (currentResolution) {
			case Configs.RESOLUTION_480P:
				mediaRecorder.setVideoSize(720, 480);// 在这里设置分辨率
				break;
			case Configs.RESOLUTION_720P:
				mediaRecorder.setVideoSize(1280, 720);// 在这里设置分辨率
				break;
			case Configs.RESOLUTION_1080P:
				mediaRecorder.setVideoSize(1920, 1080);// 在这里设置分辨率
				break;
			case Configs.RESOLUTION_1920P:

				break;
			}

		}
		// camera.getParameters().setPictureSize(cameraSize.width,
		// cameraSize.height);
		// camera.setParameters(parameters);
		if(!isNormal){//断点拍摄
			String fileName = parentPath + "/" + videoTempFiles.size() + ".mp4";
			mediaRecorder.setOutputFile(fileName);
			videoTempFiles.add(fileName);
		}else{//正常拍摄
			finalNormalPath = parentPath+"/"+new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date())+".mp4";
			mediaRecorder.setOutputFile(finalNormalPath);
		}
		
		mediaRecorder.setPreviewDisplay(mySurfaceView.getHolder().getSurface());
		try {
			mediaRecorder.prepare();
		} catch (Exception e) {
			e.printStackTrace();
			stopRecord();
		}

		try {
			mediaRecorder.start();
			isStart = true;
			videoStartTime = new Date().getTime();

		} catch (Exception e) {
			e.printStackTrace();
			if (isFirst) {
				startRecord(false);
			} else {
				stopRecord();
			}
		}
	}

	public void stopRecord() {
		if (!isMax) {
			totalTime += new Date().getTime() - videoStartTime;
			videoStartTime = 0;
		}
		//
		isStart = false;

		//
		if (mediaRecorder == null) {
			return;
		}
		try {
			mediaRecorder.setPreviewDisplay(null);
			mediaRecorder.stop();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				mediaRecorder.reset();
				mediaRecorder.release();
				mediaRecorder = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				getCamera().reconnect();
			} catch (Exception e) {
				// TODO: handle this exception...
			}
			getCamera().lock();
			semp.release();
		}

	}

	public String generateParentFolder() {
		String parentPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + Configs.COMPOSE_VIDEO_PATH/* "/mycapture/video/temp" */;
		File tempFile = new File(parentPath);
		if (!tempFile.exists()) {
			tempFile.mkdirs();
		}
		return parentPath;

	}
	public String generateFinalFolder() {
		String parentPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + Configs.FINAL_VIDEO_PATH/* "/mycapture/video/temp" */;
		File tempFile = new File(parentPath);
		if (!tempFile.exists()) {
			tempFile.mkdirs();
		}
		return parentPath;

	}

	public boolean changeFlash() {
		boolean flashOn = false;
		if (flashEnable()) {
			Parameters params = cameraManager.getCamera().getParameters();
			if (Parameters.FLASH_MODE_TORCH.equals(params.getFlashMode())) {
				params.setFlashMode(Parameters.FLASH_MODE_OFF);
				flashOn = false;
			} else {
				params.setFlashMode(Parameters.FLASH_MODE_TORCH);
				flashOn = true;
			}
			cameraManager.getCamera().setParameters(params);
		}
		return flashOn;
	}

	public void closeFlash() {
		if (flashEnable()) {
			Parameters params = cameraManager.getCamera().getParameters();
			if (Parameters.FLASH_MODE_TORCH.equals(params.getFlashMode())) {
				params.setFlashMode(Parameters.FLASH_MODE_OFF);
				cameraManager.getCamera().setParameters(params);
			}
		}
	}

	/**
	 * 是否允许开启闪光灯
	 * 
	 * @return
	 */
	public boolean flashEnable() {
		return mainActivity.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA_FLASH)
				&& cameraManager.isUseBackCamera();

	}

	public int getCurrentResolution() {
		return currentResolution;
	}

	public void setCurrentResolution(int currentResolution) {
		this.currentResolution = currentResolution;
	}

	public void setMaxTime(int maxTime) {   
		this.maxTime = maxTime;
	}

	public void setLast(int last) {
		this.last = last;
	}

	public boolean isNormal() {
		return isNormal;
	}

	public void setNormal(boolean isNormal) {
		this.isNormal = isNormal;
		resetPath();
	}
	public int getTotalTime() {
		return totalTime;
	}
	public void setTotalTime(int totalTime) {
		this.totalTime = totalTime;
	}
	/**
	 * 获取正常拍摄的视频的保存路径
	 * @return
	 */
	public String getFinalNormalPath() {
		return finalNormalPath;
	}
	public void setProgressView(ProgressView pv) {
		this.pv = pv;
	}
	/**
	 * 删除最后一段视频的时长并进行一些重置的操作
	 */
	public void deleteLastDuration(){
		if(pv!=null){
			ArrayList<Float> pausePoints = pv.getPausePoints();
			if(pausePoints!=null&&!pausePoints.isEmpty()){
				if(pausePoints.size()>1){
					Float lastPause = pausePoints.get(pausePoints.size()-1);//最后一个
					Float lastSecond = pausePoints.get(pausePoints.size()-2);//倒数第二个
					totalTime-=maxTime*(lastPause-lastSecond);
					last-=maxTime*(lastPause-lastSecond);
				
					ArrayList<Float> tipPoints = pv.getTipPoints();
					for (int i = 0; i < tipPoints.size(); i++) {//删除介于这两个值之间的记录点
						Float f1 = tipPoints.get(i);
						if(f1>=lastSecond&&f1<=lastPause){
							tipPoints.remove(i);
							i--;
						}
						
					}
					pausePoints.remove(pausePoints.size()-1);//移除最后一个百分点
				}else{//如果只有一个断点
					totalTime=0;
					last=0;
					pausePoints.clear();
					ArrayList<Float> tipPoints = pv.getTipPoints();
					if(tipPoints!=null){//清空所有的记录点
						tipPoints.clear();
					}
				}
			}
		}
		
	}
	/**
	 * 删除最后一段视频的时长并进行一些重置的操作
	 */
	public void deleteLast(){
		if(ipv!=null){
//			ArrayList<Float> pausePoints = pv.getPausePoints();
			ArrayList<Integer> tips = ipv.getTips();
			if(tips!=null&&!tips.isEmpty()){
				if(tips.size()>1){
					Integer last = tips.get(tips.size()-1);//最后一个
					Integer lastSecond = tips.get(tips.size()-2);
					totalTime-=last-lastSecond;
					this.last-=last-lastSecond;
					//删除介于这两个值之间的记录点❤❤❤❤❤❤❤❤❤❤
					ArrayList<Integer> flags = ipv.getFlags();
					if(flags!=null&&!flags.isEmpty()){
						for (int i = 0; i < flags.size(); i++) {
							Integer flag = flags.get(i);
							if(flag>=lastSecond&&flag<=last){//如果位于这两个值之间，就删除掉
								flags.remove(i);
								i--;
							}
						}
					}
					
					tips.remove(tips.size()-1);//移除最后一个
				}else{
					totalTime=0; 
					last=0;
					tips.clear();
					//删除介于这两个值之间的标记点❤❤❤❤❤❤❤❤❤❤
					
					ArrayList<Integer> flags = ipv.getFlags();
					if(flags!=null&&!flags.isEmpty()){
						flags.clear();
					}
				}
			}
			/*if(pausePoints!=null&&!pausePoints.isEmpty()){
				if(pausePoints.size()>1){
					Float lastPause = pausePoints.get(pausePoints.size()-1);//最后一个
					Float lastSecond = pausePoints.get(pausePoints.size()-2);//倒数第二个
					totalTime-=maxTime*(lastPause-lastSecond);
					last-=maxTime*(lastPause-lastSecond);
				
					ArrayList<Float> tipPoints = pv.getTipPoints();
					for (int i = 0; i < tipPoints.size(); i++) {//删除介于这两个值之间的记录点
						Float f1 = tipPoints.get(i);
						if(f1>=lastSecond&&f1<=lastPause){
							tipPoints.remove(i);
							i--;
						}
						
					}
					pausePoints.remove(pausePoints.size()-1);//移除最后一个百分点
				}else{//如果只有一个断点
					totalTime=0;
					last=0;
					pausePoints.clear();
					ArrayList<Float> tipPoints = pv.getTipPoints();
					if(tipPoints!=null){//清空所有的记录点
						tipPoints.clear();
					}
				}
			}*/
		}
		
	}
	

}
