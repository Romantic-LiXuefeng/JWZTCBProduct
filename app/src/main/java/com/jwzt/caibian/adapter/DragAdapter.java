package com.jwzt.caibian.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jwzt.caibian.bean.ChannelItem;
import com.jwzt.cb.product.R;

import java.util.List;

public class DragAdapter extends BaseAdapter {
	/** TAG*/
	private final static String TAG = "DragAdapter";
	/** �Ƿ���ʾ�ײ���ITEM */
	private boolean isItemShow = false;
	private Context context;
	/** ���Ƶ�postion */
	private int holdPosition;
	/** �Ƿ�ı� */
	private boolean isChanged = false;
	/** �Ƿ�ɼ� */
	boolean isVisible = true;
	/** �����϶����б����û�ѡ���Ƶ���б� */
	public List<ChannelItem> channelList;
	/** TextView Ƶ������ */
	private TextView item_text;
	/** Ҫɾ����position */
	public int remove_position = -1;
	private ImageView icon_new;

	public DragAdapter(Context context, List<ChannelItem> channelList) {
		this.context = context;
		this.channelList = channelList;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return channelList == null ? 0 : channelList.size();
	}

	@Override
	public ChannelItem getItem(int position) {
		// TODO Auto-generated method stub
		if (channelList != null && channelList.size() != 0) {
			return channelList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	@SuppressLint("ResourceType")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(R.layout.subscribe_category_item, null);
		item_text = (TextView) view.findViewById(R.id.text_item);
		icon_new = (ImageView) view.findViewById(R.id.icon_new);
		ChannelItem channel = getItem(position);
		item_text.setText(channel.getName());
		switch (channel.getId()) {
		case 1:
			icon_new.setBackgroundResource(R.drawable.fagao_news);
			view.setId(1);
			break;
		case 2:
			icon_new.setBackgroundResource(R.drawable.zhibo_news);
			view.setId(2);
			break;
		case 3:
			icon_new.setBackgroundResource(R.drawable.renwu_news);
			view.setId(3);
			break;
		case 4:
			icon_new.setBackgroundResource(R.drawable.shenhe_news);
			view.setId(4);
			break;
		case 5:
			icon_new.setBackgroundResource(R.drawable.chuanliandan_news);
			view.setId(5);
			break;
		case 6:
			icon_new.setBackgroundResource(R.drawable.shipinluzhi_news);
			view.setId(6);
			break;
		case 7:
			icon_new.setBackgroundResource(R.drawable.dianzixc_news);
			view.setId(7);
			break;
		case 8:
			icon_new.setBackgroundResource(R.drawable.duanshipin_news);
			view.setId(8);
			break;
		case 9:
			icon_new.setBackgroundResource(R.drawable.xuanti);
			view.setId(9);
			break;
		case 10:
			icon_new.setBackgroundResource(R.drawable.xiansuo);
			view.setId(10);
			break;
//		case 11:
//			icon_new.setImageResource(R.drawable.home_gv_11);
//			view.setId(11);
//			break;
//
//		case 12:
//			icon_new.setImageResource(R.drawable.home_gv_12);
//			view.setId(12);
//			break;
//
//		case 13:
//			icon_new.setImageResource(R.drawable.home_gv_13);
//			view.setId(13);
//			break;
//		case 14:
//			icon_new.setImageResource(R.drawable.home_gv_14);
//			view.setId(14);
//			break;
		}
		
		
		if ((position == 0) || (position == 1)){
//			item_text.setTextColor(context.getResources().getColor(R.color.black));
			item_text.setEnabled(false);
		}
		if (isChanged && (position == holdPosition) && !isItemShow) {
			item_text.setText("");
			item_text.setSelected(true);
			item_text.setEnabled(true);
			isChanged = false;
		}
		if (!isVisible && (position == -1 + channelList.size())) {
			item_text.setText("");
			item_text.setSelected(true);
			item_text.setEnabled(true);
		}
		if(remove_position == position){
			item_text.setText("");
		}
		return view;
	}

	/** ���Ƶ���б� */
	public void addItem(ChannelItem channel) {
		channelList.add(channel);
		notifyDataSetChanged();
	}

	/** �϶����Ƶ������ */
	public void exchange(int dragPostion, int dropPostion) {
		holdPosition = dropPostion;
		ChannelItem dragItem = getItem(dragPostion);
		Log.d(TAG, "startPostion=" + dragPostion + ";endPosition=" + dropPostion);
		if (dragPostion < dropPostion) {
			channelList.add(dropPostion + 1, dragItem);
			channelList.remove(dragPostion);
		} else {
			channelList.add(dropPostion, dragItem);
			channelList.remove(dragPostion + 1);
		}
		isChanged = true;
		notifyDataSetChanged();
	}
	
	/** ��ȡƵ���б� */
	public List<ChannelItem> getChannnelLst() {
		return channelList;
	}

	/** ����ɾ����position */
	public void setRemove(int position) {
		remove_position = position;
		notifyDataSetChanged();
	}

	/** ɾ��Ƶ���б� */
	public void remove() {
		channelList.remove(remove_position);
		remove_position = -1;
		notifyDataSetChanged();
	}
	
	/** ����Ƶ���б� */
	public void setListDate(List<ChannelItem> list) {
		channelList = list;
	}
	
	/** ��ȡ�Ƿ�ɼ� */
	public boolean isVisible() {
		return isVisible;
	}
	
	/** �����Ƿ�ɼ� */
	public void setVisible(boolean visible) {
		isVisible = visible;
	}
	/** ��ʾ���µ�ITEM */
	public void setShowDropItem(boolean show) {
		isItemShow = show;
	}
}