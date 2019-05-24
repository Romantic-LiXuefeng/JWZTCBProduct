package com.jwzt.caibian.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.jwzt.caibian.util.AddressData;
import com.jwzt.cb.product.R;
/**
 * 选择城市的适配器
 * @author howie
 *
 */
public class CountryAdapter extends AbstractWheelTextAdapter {

	// Countries names
			private String countries[] = AddressData.PROVINCES;

			/**
			 * Constructor
			 */
			public CountryAdapter(Context context) {
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
				return countries.length;
			}

			@Override
			protected CharSequence getItemText(int index) {
				return countries[index];
			}
		}


