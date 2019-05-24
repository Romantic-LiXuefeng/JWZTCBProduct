package com.jwzt.caibian.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.adapter.RecyclerViewAdapter.ItemViewHolder;
import com.jwzt.caibian.bean.BitmapBean;
import com.jwzt.caibian.util.BitmapUtils;


/**
 * 视频编辑页面recyclerview的适配器
 * 
 * @author howie
 * 
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<ItemViewHolder> {
	private ArrayList<BitmapBean> list;
	private  int itemWidth;
	private Context mContext;
	
	
	public void setItemWidth(int itemWidth,Context context) {
		this.itemWidth = itemWidth;
		this.mContext=context;
	}

	public RecyclerViewAdapter(ArrayList<BitmapBean> bitmaps) {
		super();
		this.list = bitmaps;
	}

	public final static class ItemViewHolder extends RecyclerView.ViewHolder {
		ImageView iv;
		/*TextView title;
		TextView description;
		TextView time;*/

		public ItemViewHolder(View itemView,int itemWidth) {
			super(itemView);
			iv=(ImageView) itemView.findViewById(R.id.iv);
//			RelativeLayout.LayoutParams param=(LayoutParams) iv.getLayoutParams();
//			param.width=itemWidth;
//			param.height=LayoutParams.MATCH_PARENT;
//			iv.setLayoutParams(param);
			
			/*
			 * img = (ImageView) itemView.findViewById(R.id.img); title =
			 * (TextView) itemView.findViewById(R.id.title); description =
			 * (TextView) itemView.findViewById(R.id.description); time =
			 * (TextView) itemView.findViewById(R.id.time);
			 */
		}
	}

	@Override
	public int getItemCount() {
		return list.size();
	}

	@Override
	public void onBindViewHolder(ItemViewHolder itemViewHolder, int position) {
		BitmapBean bitmapBean = list.get(position);
		if(bitmapBean.getType()==BitmapBean.TYPE_NORMAL){
			LayoutParams param=(LayoutParams) itemViewHolder.iv.getLayoutParams();
			param.width=bitmapBean.getImgWidth();
			param.height=LayoutParams.MATCH_PARENT;
			itemViewHolder.iv.setLayoutParams(param);
			itemViewHolder.iv.setImageBitmap(bitmapBean.getBitmap());
		}else{
			LayoutParams param=(LayoutParams) itemViewHolder.iv.getLayoutParams();
			param.width=itemWidth;
			param.height=300;
			itemViewHolder.iv.setLayoutParams(param);
			itemViewHolder.iv.setImageBitmap(BitmapUtils.readBitMap(mContext, R.drawable.blackimg));
		}
	}

	@Override
	public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_item, viewGroup, false);
		return new ItemViewHolder(view,itemWidth);
	}

	
}
