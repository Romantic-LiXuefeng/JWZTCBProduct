package com.jwzt.caibian.adapter;

import java.util.ArrayList;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.bean.LogoBean;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap.Config;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
/**
 * 布标的适配器
 * @author howie
 *
 */
public class LogoAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<LogoBean> list;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	
	public LogoAdapter(Context mContext,ArrayList<LogoBean> list) {
		super();
		this.mContext = mContext;
		this.list=list;
		
		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()  
        .showImageOnLoading(R.drawable.replace) // 设置图片下载期间显示的图片  
        .showImageForEmptyUri(R.drawable.replace) // 设置图片Uri为空或是错误的时候显示的图片  
        .showImageOnFail(R.drawable.replace) // 设置图片加载或解码过程中发生错误显示的图片  
        .cacheInMemory(true) // 设置下载的图片是否缓存在内存中  
        .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中 
        .bitmapConfig(Config.RGB_565)
//        .displayer(new FadeInBitmapDisplayer(100))
        .build(); // 构建完成  
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if(convertView==null){
			convertView = View.inflate(mContext, R.layout.logo_item_layout,null);
			holder = new ViewHolder();
			holder.riv = (ImageView) convertView.findViewById(R.id.riv);
			holder.iv_select = (ImageView) convertView.findViewById(R.id.iv_select);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		
//		View view=View.inflate(mContext, R.layout.logo_item_layout, null);
		LogoBean logoBean = list.get(position);
		imageLoader.displayImage(logoBean.getLogopath(), holder.riv, options);
		if(logoBean.isSelected()){
			holder.iv_select.setImageResource(R.drawable.quality_check);
		}else{
			holder.iv_select.setImageResource(R.drawable.quality_uncheck);
		}
		return convertView;
	}
	static class ViewHolder{
//		TextView name,first_word;a
		ImageView riv,iv_select;
	}

}
