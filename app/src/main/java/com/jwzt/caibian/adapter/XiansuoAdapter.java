package com.jwzt.caibian.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jwzt.caibian.activity.PreviewWanCHengActivity;
import com.jwzt.caibian.activity.XianSuoDetailsActivity;
import com.jwzt.caibian.bean.TestGroupBean;
import com.jwzt.caibian.interfaces.RemoveIndexListener;
import com.jwzt.caibian.util.TimeUtil;
import com.jwzt.caibian.util.UserToast;
import com.jwzt.caibian.widget.SwipeMenuLayout;
import com.jwzt.cb.product.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * 审核的适配器
 *
 * @author howie
 */
public class XiansuoAdapter extends BaseAdapter {
    /***移除条目的监听*/
    private RemoveIndexListener mListener;
    private Context mContext;
    private List<TestGroupBean> mNewUploadinglistBean;
                                // 创建配置过得DisplayImageOption对象

    public XiansuoAdapter(Context mContext, List<TestGroupBean> newUploadinglistBean) {
        super();
        this.mContext = mContext;
        this.mNewUploadinglistBean = newUploadinglistBean;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mNewUploadinglistBean.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return mNewUploadinglistBean.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(final int position, View convertView, ViewGroup arg2) {
        // TODO Auto-generated method stub
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.xiansuo_item_layout, null);
            holder.rl = convertView.findViewById(R.id.rl);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_share = (TextView) convertView.findViewById(R.id.tv_share);
            holder.tv_share.setVisibility(View.GONE);
            holder.tv_delete = (TextView) convertView.findViewById(R.id.tv_delete);
            holder.sml = (SwipeMenuLayout) convertView.findViewById(R.id.sml);
            holder.iv_flag = (ImageView) convertView.findViewById(R.id.iv_flag);
            holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.rl.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent=new Intent(mContext,XianSuoDetailsActivity.class);
                intent.putExtra("id", mNewUploadinglistBean.get(position).getId());
                String status=mNewUploadinglistBean.get(position).getStatus();
                intent.putExtra("operateType", mNewUploadinglistBean.get(position).getStatus());
                mContext.startActivity(intent);
            }
        });

        final SwipeMenuLayout swipeMenuLayout = holder.sml;
        holder.tv_delete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                UserToast.toSetToast(mContext, "不允许删除");
            }
        });

        holder.tv_title.setText(mNewUploadinglistBean.get(position).getTitle());
        holder.tv_date.setText(TimeUtil.getMDHS(mNewUploadinglistBean.get(position).getCreateTime()));
        return convertView;
    }

    public class ViewHolder {
        View rl;
        TextView tv_share, tv_delete;
        SwipeMenuLayout sml;
        ImageView iv_flag;//稿件状态的图标（退稿和已采用）
        TextView tv_title;
        TextView tv_date;
    }

    /**
     * 移除操作
     */
    public void remove(int position) {
        mNewUploadinglistBean.remove(position);
        notifyDataSetChanged();
    }

    public void setmListener(RemoveIndexListener mListener) {
        this.mListener = mListener;
    }

    public void setList(List<TestGroupBean> newUploadinglist) {
        // TODO Auto-generated method stub
        mNewUploadinglistBean = newUploadinglist;
        notifyDataSetChanged();
    }


}
