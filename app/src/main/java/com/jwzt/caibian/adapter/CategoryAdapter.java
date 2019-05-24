package com.jwzt.caibian.adapter;

import java.util.List;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.bean.TypeBean;
import com.jwzt.caibian.util.UIUtils;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import android.R.integer;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CategoryAdapter extends BaseAdapter {
	private Context mContext;
	private List<TypeBean> mList;
	private ImageLoader mImageLoader;
	private ImageLoaderConfiguration config;
	private int myPosition;
	private int filePosition;
	private TypeBean mTypeBean;
	public CategoryAdapter(Context mContext, List<TypeBean> mList) {
		super();
		myPosition = -1;
		this.mContext = mContext;
		this.mList = mList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if(convertView==null){
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.category_item_layout,null);
			holder.tv = (TextView) convertView.findViewById(R.id.tv);
			holder.iv = (ImageView) convertView.findViewById(R.id.iv);
			holder.iv_arrow=(ImageView) convertView.findViewById(R.id.iv_arrow);
			convertView.setTag(holder);
		}
		holder = (ViewHolder) convertView.getTag();
		holder.iv.setImageResource(R.drawable.news_c);
		holder.tv.setText(mList.get(position).getCategoryName());
		holder.iv_arrow.setVisibility(View.GONE);
		if (myPosition == position) {
			holder.iv_arrow.setVisibility(View.VISIBLE);
		}
		holder.setOnClick(convertView, position);
		/*convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mTypeBean = mList.get(position);
				holder.iv_arrow.setVisibility(View.VISIBLE);
				myPosition = position;
				CategoryAdapter.this.notifyDataSetChanged();
			}
		});*/
		return convertView;
	}
	
	public TypeBean getCategoryName() {
		return mTypeBean;
	}
	public class ViewHolder{
		TextView tv;
		ImageView iv;
		ImageView iv_arrow;
		
		public void setOnClick (View convertView,final int position){
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mTypeBean = mList.get(position);
					iv_arrow.setVisibility(View.VISIBLE);
					myPosition = position;
					CategoryAdapter.this.notifyDataSetChanged();
				}
			});
		}
	}

}
