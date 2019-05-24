package com.jwzt.caibian.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.jwzt.caibian.fragment.BaseFootageFragment;
import com.jwzt.caibian.fragment.FootageAudioFragment;
import com.jwzt.caibian.fragment.FootageImageFragment;
import com.jwzt.caibian.fragment.FootageVideoFragment;
import com.jwzt.caibian.interfaces.FragmentToParentInterface;

public class FootageFragmentPagerAdapter extends FragmentPagerAdapter {
	private  ArrayList<BaseFootageFragment> fragments;
	
	public ArrayList<BaseFootageFragment> getFragments() {
		return fragments;
	}
	public FootageFragmentPagerAdapter(FragmentManager fm,FragmentToParentInterface fragmentToParentInterface) {
		super(fm);
		// TODO Auto-generated constructor stub
		fragments=new ArrayList<BaseFootageFragment>();
		fragments.add(new FootageVideoFragment());
		fragments.add(new FootageAudioFragment());
		fragments.add(new FootageImageFragment());
		fragmentToParentInterface.setListFragment(fragments);
	}

	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub
		return fragments.get(position);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return fragments.size();
	}
	/**
	 * 设置选用
	 * @param index 索引
	 */
	public void setSelect(int index){
		BaseFootageFragment bff=fragments.get(index);
		boolean editing = bff.isEditing();   
		bff.setEditing(!editing);
		
	}
	/**
	 * 将所有的子fragment都置为编辑状态
	 */
	public void setAllEditing(){
		for (BaseFootageFragment fragment : fragments) {
			fragment.setEditing(true);
		}
	}

}
