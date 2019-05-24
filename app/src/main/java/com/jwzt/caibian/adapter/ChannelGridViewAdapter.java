package com.jwzt.caibian.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jwzt.caibian.bean.ChannelItem;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.cb.product.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 电台页最上面的gridView的适配器
 *
 * @author howie
 */
public class ChannelGridViewAdapter extends BaseAdapter {
    private Context mContext;

    private List<ChannelItem> mListradio;
    private boolean mMore;// 是否“收起”

    public boolean ismMore() {
        return mMore;
    }

    public void setmMore(boolean mmore) {
        this.mMore = mmore;
    }

    private int blue;//被点击之后的颜色
    private int light_black;//灰色
//	private DisplayImageOptions options;
//	private ImageLoader imageLoader;

    public ChannelGridViewAdapter(Context mContext2,
                                  List<ChannelItem> listradio) {
        this.mContext = mContext2;
        this.mListradio = listradio;
        blue = mContext.getResources().getColor(R.color.blue);
        light_black = mContext.getResources().getColor(R.color.light_black);

//		options = new DisplayImageOptions.Builder()
//				.showImageOnLoading(R.drawable.music_radio) // 设置图片下载期间显示的图片
//				.showImageForEmptyUri(R.drawable.music_radio) // 设置图片Uri为空或是错误的时候显示的图片
//				.showImageOnFail(R.drawable.music_radio) // 设置图片加载或解码过程中发生错误显示的图片
//				.cacheInMemory(false) // 设置下载的图片是否缓存在内存中
//				.cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
//				/*
//				 * .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
//				 */.build(); // 构建完成
//		imageLoader = ImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if (mListradio.size() <= 8) {//不超过8个就直接返回
            return mListradio.size();
        } else {
            return mListradio.size() + 1;//超过8个就加1，多出的一个是留给“收起”的
        }


    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChannelItem channelItem = null;
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.subscribe_category_item, null);
            viewHolder.icon_new = (ImageView) convertView.findViewById(R.id.icon_new);
            viewHolder.tv = (TextView) convertView.findViewById(R.id.text_item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (getCount() <= 8) {//如果少于8个
            channelItem = mListradio.get(position);
            showimg(viewHolder.icon_new,channelItem.getId());
            viewHolder.tv.setText(channelItem.getName());
            boolean focused = channelItem.isFocused();
            viewHolder.tv.setTextColor(focused ? blue : light_black);//设置文字颜色

//			if(IsNonEmptyUtils.isString(radioStationBean.getPic())){
//				imageLoader.displayImage(Configs.Base_img + radioStationBean.getPic(), viewHolder.iv, options);
//			}else{
//				viewHolder.iv.setImageBitmap(BitmapUtils.readBitMap(mContext, R.drawable.music_radio));
//			}
//			return view;
        } else {//多于8个
            if (position != 7 && position != mListradio.size()) {// 如果不是第8项也不是最后一项
                channelItem = mListradio.get(position);
                showimg(viewHolder.icon_new,channelItem.getId());
                viewHolder.tv.setText(channelItem.getName());
                boolean focused = channelItem.isFocused();
                viewHolder.tv.setTextColor(focused ? blue : light_black);
//				if(IsNonEmptyUtils.isString(radioStationBean.getPic())){
//					imageLoader.displayImage(
//							Configs.Base_img + radioStationBean.getPic(), viewHolder.iv, options);
//				}else{
//					viewHolder.iv.setImageBitmap(BitmapUtils.readBitMap(mContext, R.drawable.music_radio));
//				}

            } else if (position == 7) {// 第8项
                if (mMore) {
                    channelItem = mListradio.get(7);
                    showimg(viewHolder.icon_new,channelItem.getId());
                    viewHolder.tv.setText(channelItem.getName());
                    boolean focused = channelItem.isFocused();
                    viewHolder.tv.setTextColor(focused ? blue : light_black);
//					if(IsNonEmptyUtils.isString(radioStationBean.getPic())){
//						imageLoader.displayImage(Configs.Base_img + radioStationBean.getPic(), viewHolder.iv, options);
//					}else{
//						viewHolder.iv.setImageBitmap(BitmapUtils.readBitMap(mContext, R.drawable.music_radio));
//					}
                } else {
                    viewHolder.tv.setText("更多");
                    viewHolder.icon_new.setImageResource(R.drawable.more);
                }

            } else {// 最后一项
                viewHolder.tv.setText("收起");
                viewHolder.icon_new.setImageResource(R.drawable.shouqi);
            }
        }

        return convertView;
    }

    private void showimg(ImageView icon_new, int id) {
        switch (id) {
            case 1:
                icon_new.setImageResource(R.drawable.plane);
//				convertView.setId(1);
                break;
            case 2:
                icon_new.setImageResource(R.drawable.video);
//				view.setId(2);
                break;
            case 3:
                icon_new.setImageResource(R.drawable.tv);
//				view.setId(3);
                break;
            case 4:
                icon_new.setImageResource(R.drawable.huichuan);
//				view.setId(4);
                break;
            case 5:
                icon_new.setImageResource(R.drawable.icon_shenhe_imgs);
//				view.setId(5);
                break;
            case 6:
                icon_new.setImageResource(R.drawable.icon_chuanliadan_imgs);
//				view.setId(6);
                break;
            case 7:
                icon_new.setImageResource(R.drawable.mygaojian);
//				view.setId(7);
                break;
            case 8:
                icon_new.setImageResource(R.drawable.videoresource);
//				view.setId(8);
                break;
            case 9:
                icon_new.setImageResource(R.drawable.xuanti);
//				view.setId(9);
                break;
            case 10:
                icon_new.setImageResource(R.drawable.xiansuo);
//				view.setId(10);
                break;
        }
    }

    /**
     * 刷新数据
     *
     * @param list
     */
    public void update(List<ChannelItem> list) {
        mListradio = list;
        notifyDataSetChanged();
    }

    public static class ViewHolder {
        public ImageView icon_new;
        public TextView tv;
    }

}
