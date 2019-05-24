package com.jwzt.caibian.adapter;

import java.util.List;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.bean.ResourcesBean;
import com.jwzt.caibian.util.FileUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap.Config;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 全部素材的适配器
 * @author howie
 *
 */
public class AllFootageAdapter extends BaseAdapter {
	private Context mContext;
	private List<ResourcesBean> mList;
	private ImageLoader imageLoader;
    private DisplayImageOptions options;
	
	public AllFootageAdapter(Context mContext,List<ResourcesBean> list) {
		super();
		this.mContext = mContext;
		this.mList=list;
		
		options = new DisplayImageOptions.Builder()  
        .showImageOnLoading(R.drawable.replace) // 设置图片下载期间显示的图片  
        .showImageForEmptyUri(R.drawable.replace) // 设置图片Uri为空或是错误的时候显示的图片  
        .showImageOnFail(R.drawable.replace) // 设置图片加载或解码过程中发生错误显示的图片  
        .cacheInMemory(false) // 设置下载的图片是否缓存在内存中  
        .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中 
        .bitmapConfig(Config.RGB_565)
//        .displayer(new FadeInBitmapDisplayer(100))
        .build(); // 构建完成  
	    imageLoader = ImageLoader.getInstance();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View view = View.inflate(mContext, R.layout.all_footage_item_layout, null);
		ImageView iv_play = (ImageView) view.findViewById(R.id.iv_play);
		ImageView iv = (ImageView) view.findViewById(R.id.iv);
		TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
		TextView tv_size = (TextView) view.findViewById(R.id.tv_size);
		tv_name.setText(mList.get(position).getResourceName());
		long ss=new Long(mList.get(position).getFileSize());
		System.out.println("ssssssssssssss==ssssssssssssss"+ss+"=="+mList.get(position).getFileSize());
		tv_size.setText(FileUtil.FormetFileSize(new Long(mList.get(position).getFileSize())));
		imageLoader.displayImage(mList.get(position).getFileRealPath(), iv, options);
		String endString=mList.get(position).getFileRealPath();
		if(endString.endsWith("mp3")){
			iv_play.setVisibility(View.VISIBLE);
			iv_play.setImageResource(R.drawable.circle_voice);
		}else if(endString.endsWith("mp4")){
			iv_play.setVisibility(View.VISIBLE);
			iv_play.setImageResource(R.drawable.circle_starts);
		}else if(endString.endsWith("png")||endString.endsWith("jpg")){
			iv_play.setVisibility(View.GONE);
		}else{
			iv_play.setVisibility(View.GONE);
		}
		return view;
	}

}
