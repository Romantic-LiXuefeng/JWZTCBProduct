package com.jwzt.caibian.adapter;

import android.content.Context;
import android.graphics.Bitmap.Config;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jwzt.caibian.bean.LogoBean;
import com.jwzt.cb.product.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
/**
 * logo位置的适配器
 * @author howie
 *
 */
public class LogoPositionAdapter extends BaseAdapter {
	private Context mContext;
//	private ArrayList<LogoPositionBean> mList;
	ArrayList<LogoBean> mlist;
	
	
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	
	public LogoPositionAdapter(Context mContext,ArrayList<LogoBean> list) {
		super();
		this.mContext = mContext;
		this.mlist=list;
		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()  
        .showImageOnLoading(R.drawable.replace) // 设置图片下载期间显示的图片  
        .showImageForEmptyUri(R.drawable.replace) // 设置图片Uri为空或是错误的时候显示的图片  
        .showImageOnFail(R.drawable.replace) // 设置图片加载或解码过程中发生错误显示的图片  
        .cacheInMemory(false) // 设置下载的图片是否缓存在内存中  
        .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中 
        .bitmapConfig(Config.RGB_565)
//        .displayer(new FadeInBitmapDisplayer(100))
        .build(); // 构建完成  

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 4;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(final int position, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View view=View.inflate(mContext, R.layout.logo_position_item_layout, null);
		View tv_logo=view.findViewById(R.id.tv_logo);
		ImageView iv=(ImageView) view.findViewById(R.id.iv);
		ImageView iv_selected=(ImageView) view.findViewById(R.id.iv_selected);//对号的图片
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tv_logo.getLayoutParams();
		LogoBean bean = mlist.get(position);
		switch(position){
		case 1:
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);//设置为右上角
			break;
		case 2:
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);//设置为左下角
			break;
		case 3:
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);//设置为右下角
			break;
		}
		tv_logo.setLayoutParams(layoutParams);
		iv_selected.setImageResource(bean.isSelected()?R.drawable.logo_selected:R.drawable.logo_unselected);
			
		
		imageLoader.displayImage(mlist.get(position).getLogopath(), iv, options);
//		view.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				for (int i = 0; i < mlist.size(); i++) {
//					if(i==position){
//						mlist.get(i).setSelected(true);
//						mSharePreferenceUtils.putInt(UIUtils.getContext(), GlobalContants.LOGOPOSITION, position);
//					}else{
//						mlist.get(i).setSelected(false);
//					}
//				}
//				notifyDataSetChanged();
//			}
//		});	
		return view;
	}

}
