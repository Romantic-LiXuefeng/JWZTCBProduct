package com.jwzt.caibian.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.bean.InfoListBean;
import com.jwzt.caibian.bean.MessageAllBean;
import com.jwzt.caibian.util.BitmapUtils;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.TimeUtil;
/**
 * 首页稿件上传信息的列表的适配器
 * @author howie
 *
 */
public class MessageAdapter extends BaseAdapter {
	private Context mContext;
	private List<MessageAllBean> mList;
	
	public MessageAdapter(Context mContext,List<MessageAllBean> list) {
		super();
		this.mContext = mContext;
		this.mList=list;
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
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		ViewHolder holder = null;
		if(convertView==null){
			convertView = View.inflate(mContext, R.layout.message_item_layout,null);
			holder = new ViewHolder();
			holder.iv=(ImageView) convertView.findViewById(R.id.iv);
			holder.tv_title=(TextView) convertView.findViewById(R.id.tv_title);
			holder.tv_title.setTextSize(16);
			holder.tv_desc=(TextView) convertView.findViewById(R.id.tv_desc);
			holder.tv_time=(TextView) convertView.findViewById(R.id.tv_time);
			holder.tv_huifu=(TextView) convertView.findViewById(R.id.tv_huifu);
			
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		//状态：0：初审、1：二审、2：终审、3：退回
		/*if(mList.get(position).getStatus().equals("1")){//上传
			holder.iv.setImageBitmap(BitmapUtils.readBitMap(mContext, R.drawable.chuan));
		}else if(mList.get(position).getStatus().equals("10")){//选用
			holder.iv.setImageBitmap(BitmapUtils.readBitMap(mContext, R.drawable.caiyong));
		}else*/ if(mList.get(position).getMessageDescribe().contains("通过")){//审核通过
			holder.iv.setImageBitmap(BitmapUtils.readBitMap(mContext, R.drawable.shen));
			holder.tv_desc.setText(mList.get(position).getMessageDescribe());
	/*	}else if(mList.get(position).getStatus().equals("1")){
			holder.iv.setImageBitmap(BitmapUtils.readBitMap(mContext, R.drawable.shen));
			holder.tv_desc.setText("二审");
		}else if(mList.get(position).getStatus().equals("2")){
			holder.iv.setImageBitmap(BitmapUtils.readBitMap(mContext, R.drawable.shen));
			holder.tv_desc.setText("终审");
		}else if(mList.get(position).getStatus().equals("3")){//审核不通过
			holder.iv.setImageBitmap(BitmapUtils.readBitMap(mContext, R.drawable.noshen));
			holder.tv_desc.setText("终审");*/
		}else{
			holder.iv.setImageBitmap(BitmapUtils.readBitMap(mContext, R.drawable.chuan));
			holder.tv_desc.setText(mList.get(position).getMessageDescribe());
		}
		/*String highLight=mList.get(position).getHighLight();
		if(IsNonEmptyUtils.isString(highLight)){//表示有高亮字段显示
*/			holder.tv_title.setText(mList.get(position).getContent());
		/*}else{//表示没有高亮字段
			String titles=mList.get(position).getTitle();
			if(titles.length()>13){
			   holder.tv_title.setText(titles.substring(0, 12)+"...");
			}else{
				 holder.tv_title.setText(titles);
			}
		}*/
        holder.tv_huifu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				
			}
		});
	
		holder.tv_time.setText(TimeUtil.getDateDiff(mList.get(position).getCreateTime()));
		return convertView;
	}
	
	/**
	 * 在字符串中显示高亮文字
	 * @param str1 要高亮显示的文字
	 * @param str2 包含高亮文字的字符串
	 * @return
	 */
    private SpannableString setTextHighLight(String str1, String str2) {  
        SpannableString sp = new SpannableString(str2);  
        // 遍历要显示的文字  
        for (int i = 0 ; i < str1.length() ; i ++){  
            // 得到单个文字  
            String s1 = str1.charAt(i) + "";  
            // 判断字符串是否包含高亮显示的文字  
            if (str2.contains(s1)){  
                // 查找文字在字符串的下标  
                int index = str2.indexOf(s1);  
                // 循环查找字符串中所有该文字并高亮显示  
                while (index != -1) {  
                    ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#1983d1"));  
                    sp.setSpan(colorSpan, index, index + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);  
                    // s1从这个索引往后开始第一个出现的位置  
                    index = str2.indexOf(s1, index + 1);  
                }  
            }  
        }  
        return sp;
    } 
	public class ViewHolder{
		
		/**条目左侧的圆形图片*/
		ImageView iv;
		/**标题信息和描述信息、审核通过的时间*/
		TextView tv_title,tv_desc,tv_time,tv_huifu;
	
	}
	public void setList(List<MessageAllBean> list2) {
		// TODO Auto-generated method stub
		this.mList=list2;
		notifyDataSetChanged();
	}

}
