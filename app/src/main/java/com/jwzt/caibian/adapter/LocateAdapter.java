package com.jwzt.caibian.adapter;

import java.util.List;

import com.baidu.mapapi.search.core.PoiInfo;
import com.jwzt.cb.product.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
/**
 * 定位信息的适配器
 * @author howie
 *
 */
public class LocateAdapter extends BaseAdapter {
	private Context mcontext;
	private List<PoiInfo> mList;
	
	public LocateAdapter(Context mcontext,List<PoiInfo> list) {
		super();
		this.mcontext = mcontext;
		this.mList=list;
	}
	/**只有一行文字的类型*/
	private static final int TYPE_SINGLE=0; 
	/**2行文字的类型*/
	private static final int TYPE_MULTI=1;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		SingleHolder sh=null;
		MultiHolder mh=null;
		int itemType = getItemViewType(position);
		if(convertView==null){
			switch (itemType) {
			case TYPE_SINGLE://单行
				sh=new SingleHolder();
				convertView=View.inflate(mcontext, R.layout.locaiton_item_layout, null);
				sh.tv=(TextView) convertView.findViewById(R.id.tv);
				convertView.setTag(sh);
				break;
			case TYPE_MULTI://多行
				mh=new MultiHolder();
				convertView=View.inflate(mcontext, R.layout.locaiton_item_layout2, null);
				mh.tv=(TextView) convertView.findViewById(R.id.tv);
				mh.tv1=(TextView) convertView.findViewById(R.id.tv1);
				convertView.setTag(mh);
				break;
			}
		}else{
			switch (itemType) {
			case TYPE_SINGLE:
				sh=(SingleHolder) convertView.getTag();
				break;
			case TYPE_MULTI:
				mh=(MultiHolder) convertView.getTag();
				break;
			}
		}
		
		switch (itemType) {
		case TYPE_SINGLE:
			if(position==1){
				sh.tv.setText(mList.get(position).name);
			}
			break;
		case TYPE_MULTI:
			mh.tv.setText(mList.get(position).name);
			mh.tv1.setText(mList.get(position).address);
			break;
		}
		return convertView;
	}
	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		if(position<2){
			return TYPE_SINGLE;
		}else{
			return TYPE_MULTI;
		}
	}
	@Override
	public int getViewTypeCount() {
		return 2;
	}
	/**
	 * 只有一行文字的适配器
	 * @author howie
	 *
	 */
	private static class SingleHolder{
		TextView tv;
	}
	/**
	 * 2行文字的适配器
	 * @author howie
	 *
	 */
	private static class MultiHolder{
		TextView tv,tv1;
	}
}
