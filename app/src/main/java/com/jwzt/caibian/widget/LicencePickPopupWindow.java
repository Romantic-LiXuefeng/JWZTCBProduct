package com.jwzt.caibian.widget;



import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jwzt.caibian.adapter.ArrayWheelAdapter;
import com.jwzt.caibian.listener.OnWheelChangedListener1;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.adapter.LicenceLetterAdapter;
import com.jwzt.caibian.adapter.LicenceProvinceAdapter;
import com.jwzt.caibian.listener.OnWheelChangedListener;
import com.jwzt.caibian.util.AddressData;
import com.jwzt.caibian.util.WheelMain;
import com.jwzt.caibian.widget.WheelView;

/**
 * 车牌号前两位的选择对应的PopupWindow
 * 
 * @author howie
 * 
 */
public class LicencePickPopupWindow extends PopupWindow {
	/**取消**/
	private TextView ftv_place_cancel;
	/**确定**/
	private TextView  ftv_place_confirm;

	private Context mContext;
	/**车牌信息的前两位如“冀A”**/
	private String licenceInfo="请选择";

	private WheelMain wheelMain;
	public LicencePickPopupWindow(Context context, OnClickListener onClickListener) {

		mContext=context;

		View contentView = LayoutInflater.from(context).inflate(
				R.layout.wheel_licence_layout, null);
		//取消按钮
		ftv_place_cancel=(TextView) contentView.findViewById(R.id.ftv_place_cancel);
		//确认按钮
		ftv_place_confirm=(TextView) contentView.findViewById(R.id.ftv_place_confirm);
		ftv_place_cancel.setOnClickListener(onClickListener);
		ftv_place_confirm.setOnClickListener(onClickListener);
		//省份简称滚动选择器
		final WheelView province = (WheelView) contentView
				.findViewById(R.id.wheel_province);
		province.setVisibleItems(3);
		province.setViewAdapter(new LicenceProvinceAdapter(mContext));

	/*	final String cities[][] = AddressData.CITIES;
		final String ccities[][][] = AddressData.COUNTIES;*/
		//大写字母滚动选择器
		final WheelView letter = (WheelView) contentView
				.findViewById(R.id.wheel_letter);
		letter.setVisibleItems(3);
		letter.setViewAdapter(new LicenceLetterAdapter(mContext));

		province.addChangingListener(new OnWheelChangedListener1() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				licenceInfo=AddressData.PROVINCE_SHORT[province.getCurrentItem()]+AddressData.LETTERS[letter.getCurrentItem()];
			}
		});


		letter.addChangingListener(new OnWheelChangedListener1() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {

				licenceInfo=AddressData.PROVINCE_SHORT[province.getCurrentItem()]+AddressData.LETTERS[letter.getCurrentItem()];
			}
		});


		province.setCurrentItem(1);// 设置北京
		letter.setCurrentItem(1);
//		ccity.setCurrentItem(1);
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
	 * 获取部分车牌号的信息如“冀A”、“蒙B”、“黑C”
	 * @return
	 */
	public String getLicenceInfo(){
		return licenceInfo;

	}



}
