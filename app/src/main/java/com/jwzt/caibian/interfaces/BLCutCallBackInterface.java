package com.jwzt.caibian.interfaces;

/**
 * 视频裁剪的接口回�?
 * @author hly
 *
 */
public interface BLCutCallBackInterface {
	/**
	 * 裁剪视频后返回的裁剪视频段的而保存地�?
	 * @param path
	 */
	public void setCutSavePath(String path);
	
	/**
	 * 合成完成后保存在本地的路�?
	 * @param path
	 */
	public void setComposeSavePath(String path);

}
