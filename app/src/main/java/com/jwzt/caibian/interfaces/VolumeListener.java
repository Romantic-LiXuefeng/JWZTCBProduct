package com.jwzt.caibian.interfaces;
/**
 * 监听音量大小的接口
 * @author howie
 *
 */
public interface VolumeListener {
	/**
	 * 回调音量大小的方法
	 * @param currentVolume 音量
	 */
	 public void onCurrentVoice(int currentVolume); 
}
