package com.jwzt.caibian.interfaces;

/**
 * 当断网后重新联网时自动开始续传
 * @author pc
 *
 */
public interface NetWorkContinueInterface {
	/**
	 * 断网后联网重新续传
	 * @param iscontinue
	 */
	void setContinueUp(boolean iscontinue);

}
