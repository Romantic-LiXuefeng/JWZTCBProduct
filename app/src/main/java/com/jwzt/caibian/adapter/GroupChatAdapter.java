package com.jwzt.caibian.adapter;

import java.util.List;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.bean.ChatMessageBean;
import com.jwzt.caibian.bean.GroupListBean;
import com.jwzt.caibian.db.ChatsDao;
import com.jwzt.caibian.util.IsNonEmptyUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 群组交流的适配器
 * 
 * @author howie
 * 
 */
public class GroupChatAdapter extends BaseAdapter {
	private Context mContext;
	private List<GroupListBean> mList;
	private ChatsDao mChatsDao;
	private String mUserId;

	public GroupChatAdapter(Context mContext,List<GroupListBean> list,ChatsDao chatsDao,String userId) {
		super();
		this.mContext = mContext;
		this.mList=list;
		this.mChatsDao=chatsDao;
		this.mUserId=userId;
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
	public View getView(int position, View arg1, ViewGroup arg2) {
		ViewHolder viewHolder=null;
		if(arg1==null){
			viewHolder=new ViewHolder();
			arg1=LayoutInflater.from(mContext).inflate(R.layout.group_chat_item_layout, null);
			viewHolder.iv_bell=(ImageView) arg1.findViewById(R.id.iv_bell);
			viewHolder.tv_title=(TextView) arg1.findViewById(R.id.tv_title);
			viewHolder.tv_message_num=(TextView) arg1.findViewById(R.id.tv_message_num);
			viewHolder.tv_time=(TextView) arg1.findViewById(R.id.tv_time);
			viewHolder.tv_num=(TextView) arg1.findViewById(R.id.tv_num);
			arg1.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder) arg1.getTag();
		}
		viewHolder.tv_title.setText(mList.get(position).getGroupName());
		List<ChatMessageBean> mListBean=mChatsDao.getNoreadMessage(new Integer(mList.get(position).getGroupId()), new Integer(mUserId),1);
		if(IsNonEmptyUtils.isList(mListBean)){
			viewHolder.iv_bell.setVisibility(View.VISIBLE);
			viewHolder.tv_message_num.setVisibility(View.VISIBLE);
			viewHolder.tv_time.setVisibility(View.VISIBLE);
			viewHolder.tv_num.setVisibility(View.VISIBLE);
			viewHolder.tv_message_num.setText(mListBean.size()+"");
			viewHolder.tv_time.setText(mListBean.get(mListBean.size()-1).getCreateTime());
			viewHolder.tv_num.setText("["+mListBean.size()+"]"+mListBean.get(mListBean.size()-1).getContent());
		}else{
			viewHolder.iv_bell.setVisibility(View.GONE);
			viewHolder.tv_message_num.setVisibility(View.INVISIBLE);
			viewHolder.tv_time.setVisibility(View.INVISIBLE);
			viewHolder.tv_num.setVisibility(View.INVISIBLE);
		}
		return arg1;
	}
	
	class ViewHolder{
		ImageView iv_bell;
		TextView tv_title,tv_message_num,tv_time,tv_num;
	}
}
