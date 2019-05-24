package com.jwzt.caibian.adapter;

import android.content.Context;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.jwzt.caibian.interfaces.CallBackUtilsInterface;
import com.jwzt.caibian.util.BitmapUtils;
import com.jwzt.caibian.xiangce.Bimp;
import com.jwzt.caibian.xiangce.ItemImage;
import com.jwzt.cb.product.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
/**
 * 稿件的适配器
 * @author howie
 *
 */
public class ScriptAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<ItemImage> mList;
	private CallBackUtilsInterface mCallBackUtilsInterface;
	private ImageLoader imageLoader;
    private DisplayImageOptions options;
    
	public ScriptAdapter(Context mContext, ArrayList<ItemImage> list,CallBackUtilsInterface callBackUtilsInterface) {
		super();
		this.mContext = mContext;
		this.mList=list;
		this.mCallBackUtilsInterface=callBackUtilsInterface;
		
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
		return mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if(convertView==null){
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.script_item_layout,null);
			holder.iv = (ImageView) convertView.findViewById(R.id.iv);
			holder.iv_close = (ImageView) convertView.findViewById(R.id.iv_close);
			holder.iv_type=(ImageView) convertView.findViewById(R.id.iv_type);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
        ItemImage  image=	Bimp.tempSelectBitmap.get(position);
        if(image!=null){
        	String filepath=mList.get(position).getFilepath();
        	 if(filepath.endsWith(".mp4")){
             	holder.iv_type.setVisibility(View.VISIBLE);
    			//视频缩略图的添加
             	if(mList.get(position).getBitmap()!=null){
             		holder.iv.setImageBitmap(mList.get(position).getBitmap()); 
             	}else{
//             		Bitmap srcBitmap=null;
             		if(filepath.startsWith("file:///")){
             			String lastFilePath=filepath.replaceFirst("file:///", "");
             			Uri uri = Uri.fromFile(new File(lastFilePath));
             			imageLoader.displayImage(uri+"", holder.iv, options);
//             			srcBitmap = ThumbnailUtils.createVideoThumbnail(lastFilePath, 0);
             		}else{
             			Uri uri = Uri.fromFile(new File(filepath));
             			imageLoader.displayImage(uri+"", holder.iv, options);
//             			srcBitmap = ThumbnailUtils.createVideoThumbnail(filepath, 0);
             		}
//        			srcBitmap = ThumbnailUtils.extractThumbnail(srcBitmap, 420, 300);
//        			holder.iv.setImageBitmap(srcBitmap); 
             	}
             	holder.iv_type.setImageBitmap(BitmapUtils.readBitMap(mContext, R.drawable.circle_starts));
             }else if(filepath.endsWith(".mp3")||filepath.endsWith(".wav")){
             	holder.iv_type.setVisibility(View.GONE);
             	holder.iv.setImageBitmap(BitmapUtils.readBitMap(mContext, R.drawable.audiodefalt));
             	holder.iv_type.setImageBitmap(BitmapUtils.readBitMap(mContext, R.drawable.circle_voice));
             }else{
             	holder.iv_type.setVisibility(View.GONE);
             	if(filepath.startsWith("file:///")){
             		imageLoader.displayImage(mList.get(position).getFilepath(), holder.iv, options);
         		}else{
         			imageLoader.displayImage("file:///"+mList.get(position).getFilepath(), holder.iv, options);
         		}
             }
        }else{
        	holder.iv.setImageResource(R.drawable.plus_white);//加号按钮
        }
	     
	     
		if(position==mList.size()-1){
			holder.iv.setImageResource(R.drawable.plus_white);//加号按钮
			holder.iv_close.setVisibility(View.GONE);
			holder.iv_type.setVisibility(View.GONE);
			holder.iv.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					mCallBackUtilsInterface.setCallbackAdapter();
				}
			});
		}else{
			holder.iv_close.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					mList.remove(position);
					notifyDataSetChanged();
				}
			});
		}
		return convertView;
	}
	
	
	static class ViewHolder{
		ImageView iv,iv_type,iv_close;
	}

}
