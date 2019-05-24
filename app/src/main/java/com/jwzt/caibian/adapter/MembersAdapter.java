package com.jwzt.caibian.adapter;

import java.util.List;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.bean.TaskListUserListBean;
import com.jwzt.caibian.widget.CircleImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap.Config;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
/**
 * 参与人员的适配器
 * @author howie
 *
 */
public class MembersAdapter extends BaseAdapter {
	private Context mContext;
	private List<TaskListUserListBean> mUserList;
	private ImageLoader imageLoader;
    private DisplayImageOptions options;
	
	public MembersAdapter(Context mContext,List<TaskListUserListBean> userList) {
		super();
		this.mContext = mContext;
		this.mUserList=userList;
		
		options = new DisplayImageOptions.Builder()  
        .showImageOnLoading(R.drawable.headdefault) // 设置图片下载期间显示的图片  
        .showImageForEmptyUri(R.drawable.headdefault) // 设置图片Uri为空或是错误的时候显示的图片  
        .showImageOnFail(R.drawable.headdefault) // 设置图片加载或解码过程中发生错误显示的图片  
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
		return mUserList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mUserList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View view=View.inflate(mContext, R.layout.mebmer_item_layout, null);
		CircleImageView headImg=(CircleImageView) view.findViewById(R.id.civ);
		TextView tv_name=(TextView) view.findViewById(R.id.tv_name);
		TextView tv_group=(TextView) view.findViewById(R.id.tv_group);
		
		imageLoader.displayImage(mUserList.get(arg0).getUserImg(), headImg, options);
		tv_name.setText(mUserList.get(arg0).getUsername());
		tv_group.setText(mUserList.get(arg0).getChannelName());
		return view;
	}

}
