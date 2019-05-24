package com.jwzt.caibian.adapter;

import java.util.List;

import com.jwzt.caibian.fragment.PhotoFragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


/**
 * 图集适配
 * @author hanliyu
 *
 */
public class MyViewPagerAdapter extends FragmentPagerAdapter {

	private static List<String> list;
	private static int mCount = 0;
	private Context mContext;

	public MyViewPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	public MyViewPagerAdapter(FragmentManager fm,Context context,List<String> lists) {
		super(fm);
		mContext=context;
		MyViewPagerAdapter.list = lists;
		MyViewPagerAdapter.mCount = lists.size();
		
	}
	
	@Override
	public Fragment getItem(int position) {
		return PhotoFragment.newInstance(list.get(position % list.size()),mContext);
	}

	@Override
	public int getCount() {
		return mCount;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		// return TestFragmentAdapter.CONTENT[position % CONTENT.length] + "";
		return "";
	}

	public void setCount(int count) {
		if (count > 0 && count <= 10) {
			mCount = count;
			notifyDataSetChanged();
		}
	}
}