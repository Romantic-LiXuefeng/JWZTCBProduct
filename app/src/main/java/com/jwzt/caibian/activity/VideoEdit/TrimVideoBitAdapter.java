package com.jwzt.caibian.activity.VideoEdit;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.jwzt.cb.product.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LLhon
 * @Project Android-Video-Editor
 * @Package com.marvhong.videoeditor.adapter
 * @Date 2018/8/22 10:10
 * @description
 */
public class TrimVideoBitAdapter extends RecyclerView.Adapter {

    private List<Bitmap> lists = new ArrayList<>();
    private LayoutInflater inflater;

    private int itemW;
    private Context context;

    public TrimVideoBitAdapter(Context context, int itemW) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.itemW = itemW;
    }

    public List<Bitmap> getDatas() {
        return lists;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VideoHolder(inflater.inflate(R.layout.video_thumb_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VideoHolder viewHolder = (VideoHolder) holder;


//        Glide.with(context).as
//            .load(lists.get(position))
//            .into(viewHolder.img);
        viewHolder.img.setImageBitmap(lists.get(position));
    }

    @Override
    public int getItemCount() {
        System.out.println("lists====="+lists.size());
        return lists.size();
    }

    private final class VideoHolder extends RecyclerView.ViewHolder {

        public ImageView img;

        VideoHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.thumb);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) img
                .getLayoutParams();
//            layoutParams.width = itemW/lists.size();
////            layoutParams.height=400;
            System.out.println("itemW=========="+itemW);
            img.setLayoutParams(layoutParams);
        }
    }

    public void addItemVideoInfo(List<Bitmap>  info) {
        lists.addAll(info);
        notifyItemInserted(lists.size());
    }
}
