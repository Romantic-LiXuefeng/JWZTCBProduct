package com.jwzt.caibian.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.jwzt.caibian.activity.VideoDivideActivity;
import com.jwzt.caibian.bean.AttachsBeen;
import com.jwzt.caibian.bean.LocationAudioBean;
import com.jwzt.caibian.db.DatabaseContext;
import com.jwzt.caibian.db.RecordDao;
import com.jwzt.caibian.interfaces.EmptyListener;
import com.jwzt.caibian.interfaces.PlayerVideoInterface;
import com.jwzt.caibian.interfaces.UploadIndexListener;
import com.jwzt.caibian.util.FileUtil;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.TimeUtil;
import com.jwzt.caibian.util.UserToast;
import com.jwzt.caibian.widget.SwipeMenuLayout;
import com.jwzt.caibian.xiangce.Bimp;
import com.jwzt.caibian.xiangce.ItemImage;
import com.jwzt.caibian.xiangce.OtherUtils;
import com.jwzt.cb.product.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 视频素材的适配器
 *
 * @author howie
 */
public class VideoAudioAdapter extends BaseAdapter{
    private Context mContext;
    private List<LocationAudioBean> mList;


    public VideoAudioAdapter(Context context, List<LocationAudioBean> strs) {
        super();
        this.mContext = context;
        this.mList = strs;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final LocationAudioBean attachsBeen = mList.get(position);
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.videoaudio_item_layout, null);
            holder = new ViewHolder();
            holder.iv_select_copy = (ImageView) convertView.findViewById(R.id.iv_select_copy);
            holder.tv_name_copy = (TextView) convertView.findViewById(R.id.tv_name_copy);
            holder.tv_time_copy = (TextView) convertView.findViewById(R.id.tv_time_copy);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_name_copy.setText(attachsBeen.getSong());
        //最后一次修改的日期
        String modifiedTime = TimeUtil.getYMDHMS(attachsBeen.getCreateTime());
        holder.tv_time_copy.setText(modifiedTime);

        return convertView;
    }

    public class ViewHolder {
        ImageView iv_select_copy;
        TextView tv_name_copy, tv_time_copy;
    }
}
