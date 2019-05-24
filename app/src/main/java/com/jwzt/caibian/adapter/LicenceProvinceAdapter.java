package com.jwzt.caibian.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.jwzt.caibian.util.AddressData;
import com.jwzt.cb.product.R;

/**
 * 选择车牌号省份简称的适配器
 * 
 * @author howie
 * 
 */
public class LicenceProvinceAdapter extends AbstractWheelTextAdapter {

	// 省份的简称
	private String provinces_short[] = AddressData.PROVINCE_SHORT;

	/**
	 * Constructor
	 */
	public LicenceProvinceAdapter(Context context) {
		super(context, R.layout.wheelcity_country_layout, NO_RESOURCE);
		setItemTextResource(R.id.wheelcity_country_name);
	}

	@Override
	public View getItem(int index, View cachedView, ViewGroup parent) {
		View view = super.getItem(index, cachedView, parent);
		return view;
	}

	@Override
	public int getItemsCount() {
		System.out.println("provinces_short.length===="+provinces_short.length);
		return provinces_short.length;
	}

	@Override
	protected CharSequence getItemText(int index) {
		System.out.println("provinces_short[index]==="+provinces_short[index]);
		return provinces_short[index];
	}
}
