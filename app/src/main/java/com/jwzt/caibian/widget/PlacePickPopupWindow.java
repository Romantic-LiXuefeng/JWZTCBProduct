package com.jwzt.caibian.widget;





import com.jwzt.caibian.adapter.ArrayWheelAdapter;
import com.jwzt.caibian.adapter.CountryAdapter;

import com.jwzt.caibian.listener.OnWheelChangedListener1;
import com.jwzt.caibian.util.AddressData;
import com.jwzt.caibian.util.WheelMain;
import com.jwzt.caibian.widget.WheelView;
import com.jwzt.cb.product.R;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * 个人设置页面“出生地”与“居住地”选择对应的PopupWindow
 * 
 * @author howie
 * 
 */
public class PlacePickPopupWindow extends PopupWindow {
	/**取消**/
	private TextView ftv_place_cancel;
	/**确定**/
	private TextView  ftv_place_confirm;
	/**年**/
	private String year;
	/**月**/
	private String month;
	/**日**/
	private String day;
	private Context mContext;
	/**城市名称**/
	private String cityTxt;
	private WheelMain wheelMain;
	public PlacePickPopupWindow(Context context, OnClickListener onClickListener) {
		
		mContext=context;
		
		View contentView = LayoutInflater.from(context).inflate(
				R.layout.wheelcity_cities_layout, null);
		ftv_place_cancel=(TextView) contentView.findViewById(R.id.ftv_place_cancel);
		ftv_place_confirm=(TextView) contentView.findViewById(R.id.ftv_place_confirm);
		ftv_place_cancel.setOnClickListener(onClickListener);
		ftv_place_confirm.setOnClickListener(onClickListener);
		final WheelView country = (WheelView) contentView
				.findViewById(R.id.wheelcity_country);
		country.setVisibleItems(3);
		country.setViewAdapter(new CountryAdapter(context));

		final String cities[][] = AddressData.CITIES;
		final String ccities[][][] = AddressData.COUNTIES;
		final WheelView city = (WheelView) contentView
				.findViewById(R.id.wheelcity_city);
		city.setVisibleItems(0);

		// 地区选择
		final WheelView ccity = (WheelView) contentView
				.findViewById(R.id.wheelcity_ccity);
		ccity.setVisibleItems(0);// 不限城市
		country.addChangingListener(new OnWheelChangedListener1() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				updatecCities(city, cities, newValue);
				cityTxt = AddressData.PROVINCES[country.getCurrentItem()]

						+ AddressData.CITIES[country.getCurrentItem()][city
						.getCurrentItem()]

						+ AddressData.COUNTIES[country.getCurrentItem()][city
						.getCurrentItem()][ccity.getCurrentItem()];
			}
		});


		city.addChangingListener(new OnWheelChangedListener1() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				updatecCities(ccity, ccities, country.getCurrentItem(),
						newValue);
				cityTxt = AddressData.PROVINCES[country.getCurrentItem()]
						
						+ AddressData.CITIES[country.getCurrentItem()][city
								.getCurrentItem()]
						
						+ AddressData.COUNTIES[country.getCurrentItem()][city
								.getCurrentItem()][ccity.getCurrentItem()];
			}
		});

		ccity.addChangingListener(new OnWheelChangedListener1() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				cityTxt = AddressData.PROVINCES[country.getCurrentItem()]
						
						+ AddressData.CITIES[country.getCurrentItem()][city
								.getCurrentItem()]
					
						+ AddressData.COUNTIES[country.getCurrentItem()][city
								.getCurrentItem()][ccity.getCurrentItem()];
			}
		});

		country.setCurrentItem(1);// 设置北京
		city.setCurrentItem(1);
		ccity.setCurrentItem(1);
//		return contentView;
	

		this.setContentView(contentView);
		this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
		this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		/** 点击popupWindow范围以外的地方,让popupWindow消失 */
		this.setOutsideTouchable(true);
		this.setBackgroundDrawable(new BitmapDrawable());
	
		// 加入动画
		this.setAnimationStyle(R.style.AnimBottom);

	}
	/**
	 * Updates the ccity wheel
	 */
	public void updatecCities(WheelView city, String ccities[][][], int index,
			int index2) {
		/*ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(mContext,
				ccities[index][index2]);*/
//		ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>() {
//		}; 
		ArrayWheelAdapter<String> adapter=new ArrayWheelAdapter<String>(mContext, R.layout.wheelcity_country_layout, R.id.wheelcity_country_name, ccities[index][index2]);
		adapter.setTextSize(18);
		city.setViewAdapter(adapter);
		city.setCurrentItem(0);
	}
	/**
	 * Updates the city wheel
	 */
	private void updatecCities(WheelView city, String cities[][], int index) {
//		ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(mContext,
//				cities[index]);
		ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(mContext, R.layout.wheelcity_country_layout, R.id.wheelcity_country_name, cities[index]);
		adapter.setTextSize(18);
		city.setViewAdapter(adapter);
		city.setCurrentItem(0);
	}
	/**
	 * 获取选择的地址
	 * @return
	 */
	public String getPlace(){
		return cityTxt;
		
	}
	
	
	

}
