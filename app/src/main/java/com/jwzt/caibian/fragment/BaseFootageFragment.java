package com.jwzt.caibian.fragment;

import android.support.v4.app.Fragment;
/**
 * 素材fragment的基类
 * @author howie
 *
 */
public  class BaseFootageFragment extends Fragment {

	public boolean isEditing() {
		return false;
	}

	public void setEditing(boolean isEditing) {
		
	}
	/**
	 * 获取列表中被选中的个数
	 * @return
	 */
	public int getSelectedCount(){
		return 0;
		
	}
	
	
}
