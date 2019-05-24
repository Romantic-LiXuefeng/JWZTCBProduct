package com.jwzt.caibian.bean;
/**
 * 稿件
 * @author howie
 *
 */

public class ScriptBean {
	/**已采用*/
	public static final int STATUS_CAIYONG=0;
	/**已采用*/
	public static final int STATUS_TUIGAO=1;
	/**稿件状态*/
	private int status;
	public void setStatus(int status) {
		this.status = status;
	}
	public int getStatus() {
		return status;
	}
	
	
}
