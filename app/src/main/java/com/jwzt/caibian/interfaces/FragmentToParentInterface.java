package com.jwzt.caibian.interfaces;

import java.util.List;

import com.jwzt.caibian.fragment.BaseFootageFragment;

import android.support.v4.app.Fragment;

public interface FragmentToParentInterface {
	/**
	 * 将fragmentadapter中的fragment集合回传到其父fragment中
	 * @param list
	 */
	public void setListFragment(List<BaseFootageFragment> list);
	
	public void reset();

}
