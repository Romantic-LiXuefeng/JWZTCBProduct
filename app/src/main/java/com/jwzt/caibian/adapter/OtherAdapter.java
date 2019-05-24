package com.jwzt.caibian.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jwzt.caibian.bean.ChannelItem;
import com.jwzt.cb.product.R;

import java.util.List;


public class OtherAdapter extends BaseAdapter {
	private Context context;
	public List<ChannelItem> channelList;
	private TextView item_text;
	/** �Ƿ�ɼ� */
	boolean isVisible = true;
	/** Ҫɾ����position */
	public int remove_position = -1;
	private ImageView icon_new;

	public OtherAdapter(Context context, List<ChannelItem> channelList) {
		this.context = context;
		this.channelList = channelList;
	}

	@Override
	public int getCount() {
		return channelList == null ? 0 : channelList.size();
	}

	@Override
	public ChannelItem getItem(int position) {
		if (channelList != null && channelList.size() != 0) {
			return channelList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(R.layout.subscribe_category_item, null);
		item_text = (TextView) view.findViewById(R.id.text_item);
		icon_new = (ImageView) view.findViewById(R.id.icon_new);
		ChannelItem channel = getItem(position);
		item_text.setText(channel.getName());
		switch (channel.getId()) {
		case 1:
			icon_new.setImageResource(R.drawable.plane);
			break;
		case 2:
			icon_new.setImageResource(R.drawable.video);
			break;
		case 3:
			icon_new.setImageResource(R.drawable.tv);
			break;
		case 4:
			icon_new.setImageResource(R.drawable.huichuan);
			break;
		case 5:
			icon_new.setImageResource(R.drawable.icon_shenhe_imgs);
			break;
		case 6:
			icon_new.setImageResource(R.drawable.icon_chuanliadan_imgs);
			break;
		case 7:
			icon_new.setImageResource(R.drawable.mygaojian);
			break;
		case 8:
			icon_new.setImageResource(R.drawable.videoresource);
			break;
		case 9:
			icon_new.setImageResource(R.drawable.xuanti);
			break;
		case 10:
			icon_new.setImageResource(R.drawable.xiansuo);
			break;
		}
		
		if (!isVisible && (position == -1 + channelList.size())){
			item_text.setText("");
		}
		if(remove_position == position){
			item_text.setText("");
		}
		return view;
	}
	
	/** ��ȡƵ���б� */
	public List<ChannelItem> getChannnelLst() {
		return channelList;
	}
	
	/** ���Ƶ���б� */
	public void addItem(ChannelItem channel) {
		channelList.add(channel);
		notifyDataSetChanged();
	}

	/** ����ɾ����position */
	public void setRemove(int position) {
		remove_position = position;
		notifyDataSetChanged();
		//notifyDataSetChanged();
		// notifyDataSetChanged();
	}

	/** ɾ��Ƶ���б� */
	public void remove() {
		channelList.remove(remove_position);
		remove_position = -1;
		notifyDataSetChanged();
		//notifyDataSetChanged();
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
}