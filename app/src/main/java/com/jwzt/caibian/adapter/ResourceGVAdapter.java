package com.jwzt.caibian.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.jwzt.caibian.bean.TestChildBean;
import com.jwzt.cb.product.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by pc on 2018/9/26.
 */

public class ResourceGVAdapter extends BaseAdapter {
    private Context mContext;
    private List<TestChildBean> mList;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    public ResourceGVAdapter(Context context, List<TestChildBean> list) {
        this.mContext = context;
        this.mList = list;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.replace) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.replace) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.replace) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(false) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .bitmapConfig(Bitmap.Config.RGB_565)
                // .displayer(new FadeInBitmapDisplayer(100))
                .build(); // 构建完成
        imageLoader = ImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View contentView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (contentView == null) {
            viewHolder = new ViewHolder();
            contentView = LayoutInflater.from(mContext).inflate(R.layout.media_item_layout, viewGroup, false);
            viewHolder.showResouce = contentView.findViewById(R.id.iv);
            viewHolder.iv_play = contentView.findViewById(R.id.iv_play);
            contentView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) contentView.getTag();
        }

        String endString = mList.get(i).getFileRealPath();
        if (endString.endsWith("mp3") || endString.endsWith("wav")) {
            viewHolder.iv_play.setVisibility(View.GONE);
            viewHolder.showResouce.setImageResource(R.drawable.audio_bg);
        } else if (endString.endsWith("mp4")) {
            viewHolder.iv_play.setVisibility(View.VISIBLE);
            imageLoader.displayImage(mList.get(i).getPreviewUrl(), viewHolder.showResouce, options);
        } else if (endString.endsWith("png") || endString.endsWith("jpg")) {
            viewHolder.iv_play.setVisibility(View.GONE);
            imageLoader.displayImage(mList.get(i).getFileRealPath(), viewHolder.showResouce, options);
        } else {
            viewHolder.iv_play.setVisibility(View.GONE);
            imageLoader.displayImage(mList.get(i).getPreviewUrl(), viewHolder.showResouce, options);
        }

        return contentView;
    }

    class ViewHolder {
        ImageView showResouce, iv_play;
    }
}
