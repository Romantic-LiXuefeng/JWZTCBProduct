package com.jwzt.caibian.adapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.activity.PhotoActivity;
import com.jwzt.caibian.util.BitmapUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap.Config;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
/**
 * 任务详情页发送的多张图片的适配器
 * @author howie
 *
 */
public class MultiImageAdapter extends BaseAdapter {
	private Context mContext;
	private List<String> mList=new ArrayList<String>();
	private ImageLoader imageLoader;
    private DisplayImageOptions options;
	
	public MultiImageAdapter(Context mContext,String[] imgpath) {
		super();
		this.mContext = mContext;
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
		for(int i=0;i<imgpath.length;i++){
			mList.add(imgpath[i]);
		}
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
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View view=View.inflate(mContext, R.layout.grid_item_image_layout, null);
		ImageView riv_img=(ImageView) view.findViewById(R.id.riv_img);
		String imgpath=mList.get(arg0);
		if(imgpath.startsWith("file:///")){
			imageLoader.displayImage(mList.get(arg0), riv_img, options);
		}else{
			imageLoader.displayImage("file:///"+mList.get(arg0), riv_img, options);
		}
		
		riv_img.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(mContext, PhotoActivity.class);
				intent.putExtra("position", (arg0+1)+"");
				intent.putExtra("list", (Serializable)mList);
				mContext.startActivity(intent);
			}
		});
		return view;
	}

}
