package com.jwzt.caibian.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 封装需要合并的视频信息的类
 */
public class MergeBean implements Serializable{ 
	
	private ArrayList<AttachsBeen> list;

	public ArrayList<AttachsBeen> getList() {
		return list;
	}
	/**视频分辨率的信息*/
	private ResolutionBean resolutionBean;
	

	public ResolutionBean getResolutionBean() {
		return resolutionBean;
	}


	public void setResolutionBean(ResolutionBean resolutionBean) {
		this.resolutionBean = resolutionBean;
	}


	public void setList(ArrayList<AttachsBeen> list) {
		this.list = list;
	}
	
}
