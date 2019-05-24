package com.jwzt.caibian.util;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Build;
import android.view.SurfaceHolder;

/**
 * Camera manager that control the start,stop and refresh the camera
 * 
 * @author xiaodong
 * 
 */
@SuppressWarnings("deprecation")
public class CameraManager {
	
	private Camera camera = null;
	private Size defaultSize = null;
	private int cameraFacingType = CameraInfo.CAMERA_FACING_BACK;
	private Context mContext;
	public CameraManager(Context context) {
		mContext=context;
	}

	public void startCamera(SurfaceHolder holder) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			for (int i = 0; i < camera.getNumberOfCameras(); i++) {
				CameraInfo info = new CameraInfo();
				Camera.getCameraInfo(i, info);
				if (info.facing == cameraFacingType) {
					camera = Camera.open(i);
					break;
				}
			}
		} else {
			camera = Camera.open();
		}
		
		//设置拍摄参数
		Parameters parameters = camera.getParameters();
//		if(cameraFacingType==CameraInfo.CAMERA_FACING_FRONT){//ǰ������ͷ
//			camera.setDisplayOrientation(0);
//		}
		try {
			List<Size> sizeList = parameters.getSupportedPreviewSizes();
			int width = 0;
			for (Size s : sizeList) {
				System.out.println(s.width + "," + s.height);
				if (s.width > width && s.width <= 800) {
					width = s.width;
					defaultSize = s;
				}
			}
			camera.setDisplayOrientation(90);
			camera.setPreviewDisplay(holder);
		} catch (IOException e) {
			camera.release();
			camera = null;
		}
		
		try {
			parameters.setPreviewSize(1920, 1080);
			camera.setParameters(parameters);
			defaultSize = null;
		} catch (Exception e) {
			e.printStackTrace();
			parameters.setPreviewSize(defaultSize.width, defaultSize.height);
			camera.setParameters(parameters);
		}

	}

	public Size getDefaultSize() {
		return defaultSize;
	}

	public void closeCamera() {
		camera.stopPreview();
		camera.release();
		camera = null;
	}

	public void rePreview() {
		// Parameters parameters = camera.getParameters();
		// parameters.setPictureSize(300, 300);
		// camera.setParameters(parameters);
		camera.stopPreview();
		camera.startPreview();
	}

	
	public boolean isUseBackCamera() {
		return cameraFacingType == CameraInfo.CAMERA_FACING_BACK;
	}

	public void useBackCamera() {
		cameraFacingType = CameraInfo.CAMERA_FACING_BACK;
	}

	public void useFrontCamera() {
		cameraFacingType = CameraInfo.CAMERA_FACING_FRONT;
	}

	public void changeCamera(SurfaceHolder holder) {
		if (cameraFacingType == CameraInfo.CAMERA_FACING_BACK) {
			useFrontCamera();
		} else if (cameraFacingType == CameraInfo.CAMERA_FACING_FRONT) {
			useBackCamera();
		}
		closeCamera();
		startCamera(holder);
		rePreview();
	}

	public Camera getCamera() {
		return camera;
	}
	
	

}
