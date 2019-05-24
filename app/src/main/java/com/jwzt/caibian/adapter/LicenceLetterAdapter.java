package com.jwzt.caibian.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.util.AddressData;
/**
 * 选择车牌号字母的适配器
 * @author howie
 *
 */
public class LicenceLetterAdapter extends AbstractWheelTextAdapter {

			//大写英文字母
			private String letters[] = AddressData.LETTERS;

			/**
			 * Constructor
			 */
			public LicenceLetterAdapter(Context context) {
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
				return letters.length;
			}

			@Override
			protected CharSequence getItemText(int index) {
				return letters[index];
			}
		}


