package com.jwzt.caibian.adapter;

import java.util.ArrayList;
import java.util.List;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.activity.PreviewActivity;
import com.jwzt.caibian.bean.DoTaskBean;
import com.jwzt.caibian.bean.LocationNoUploadBean;
import com.jwzt.caibian.bean.ScriptBean;
import com.jwzt.caibian.db.DatabaseHelper;
import com.jwzt.caibian.db.LocationUploadDao;
import com.jwzt.caibian.interfaces.RemoveIndexListener;
import com.jwzt.caibian.util.BitmapUtils;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.widget.DonutProgress;
import com.jwzt.caibian.widget.SwipeMenuLayout;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 上传界面的适配器
 * @author howie
 *
 */
public class UploadsAdapter extends BaseAdapter implements OnClickListener{
	/**想要删除的条目的索引*/
	private int deleteIndex=-1;
	public SwipeMenuLayout mSml;
	/**移除条目的监听*/
	private RemoveIndexListener mListener;
	private Context mContext;
	private AlertDialog alertDialog;
	private List<LocationNoUploadBean> mList;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				notifyDataSetChanged();
				break;
			}
		};
	};
		
	public UploadsAdapter(Context mContext, List<LocationNoUploadBean> list) {
		super();
		this.mContext = mContext;
		this.mList=list;
		
		options = new DisplayImageOptions.Builder()
		 .showStubImage(R.drawable.replace)          // 设置图片下载期间显示的图片
		    .showImageForEmptyUri(R.drawable.replace)  // 设置图片Uri为空或是错误的时候显示的图片
	        .showImageOnFail(R.drawable.replace)       // 设置图片加载或解码过程中发生错误显示的图片
	        .cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中
	        .cacheOnDisk(true)                          // 设置下载的图片是否缓存在SD卡中
	        .build();  
		imageLoader = ImageLoader.getInstance();
		
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
	public View getView(final int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if(convertView==null){
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.uploading_item_layout,null);
			holder.iv_arrow = (ImageView) convertView.findViewById(R.id.iv_arrow);
			holder.dp=(DonutProgress) convertView.findViewById(R.id.dp);
			holder.rl=convertView.findViewById(R.id.rl);
			holder.sml=(SwipeMenuLayout) convertView.findViewById(R.id.sml);
			holder.tv_delete=(ImageView) convertView.findViewById(R.id.iv_delete);
			holder.tv_title=(TextView) convertView.findViewById(R.id.tv_title);
			holder.tv_date=(TextView) convertView.findViewById(R.id.tv_date);
			holder.tv_time=(TextView) convertView.findViewById(R.id.tv_time);
			holder.iv=(ImageView) convertView.findViewById(R.id.iv);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.dp.setVisibility(View.GONE);
		holder.iv_arrow.setVisibility(View.VISIBLE);
		holder.tv_title.setText(mList.get(position).getTitle());
		holder.tv_date.setText(mList.get(position).getSaveTime().replaceAll("月","/").replaceAll("日",""));
		String imgpath=mList.get(position).getImgpath();
		if(IsNonEmptyUtils.isString(imgpath)){
			String[] imagePath=imgpath.split(",");
			String img=imagePath[0];
			System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaa"+position+"==="+img);
			if(img.startsWith("file:///")||img.startsWith("http://")){
				imageLoader.displayImage(imagePath[0], holder.iv, options);
			}else{
				imageLoader.displayImage("file:///"+imagePath[0], holder.iv, options);
			}
		}else{
			holder.iv.setImageBitmap(BitmapUtils.readBitMap(mContext, R.drawable.replace));
		}
		holder.tv_time.setVisibility(View.GONE);
		holder.rl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent(mContext,PreviewActivity.class);//跳转预览界面
				intent.putExtra("preview", mList.get(position));
				mContext.startActivity(intent);
				((Activity)mContext).overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			}
		});
		
		final SwipeMenuLayout swipeMenuLayout=holder.sml;
		holder.tv_delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				deleteIndex=position;
				mSml=swipeMenuLayout;		//先进行注释,等待操作
				showTip();
			}
		});
		return convertView;
	}
	
	/**
	 * 显示是否进行删除操作的提示
	 */
	private void showTip(){
		if (alertDialog == null) {
			alertDialog = new AlertDialog.Builder(mContext).create();
			OnKeyListener keylistener = new OnKeyListener() {
		        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {  
		            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {  
		                return true;  
		            } else {  
		                return false;  
		            }  
		        }  
		    }; 
		    alertDialog.setOnKeyListener(keylistener);//保证按返回键的时候alertDialog也不会消失
			alertDialog.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface arg0) {
					// TODO Auto-generated method stub
					
				}
			});
			

		}
		alertDialog.show();
		View tipView = View.inflate(mContext, R.layout.edit_alert_layout, null);
		TextView tv_tip=(TextView) tipView.findViewById(R.id.tv_tip);
		tv_tip.setText("确认要删除选中的素材吗？");
		View tv_yes = (TextView) tipView.findViewById(R.id.tv_yes);
		//不再提醒
		tv_yes.setOnClickListener(this);
		tipView.findViewById(R.id.tv_no).setOnClickListener(this);
		
		alertDialog.setContentView(tipView);

	}
	
	public class ViewHolder{
		/**转发和删除*/
		TextView tv_title;
		TextView tv_date,tv_time;
		ImageView iv_arrow,iv,tv_delete;
		DonutProgress dp;//圆环进度条
		View rl;
		SwipeMenuLayout sml;
		
	}
	public void setmListener(RemoveIndexListener mListener) {
		this.mListener = mListener;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.tv_yes:
			mSml.quickClose();
//			mNoUpLoadtask.deletePerson(mList.get(deleteIndex));
//			mList.remove(deleteIndex);
//			if(mList!=null&&mList.isEmpty()&&emptyListener!=null){//如果删除的是最后一条数据，删除之后就清空了
//				emptyListener.empty();
//			}
//			notifyDataSetChanged();
//			saveVideoFootages();
//			mHandler.sendEmptyMessage(1);
			
			if(mListener!=null){
				mListener.remove(deleteIndex);
			}
			
			alertDialog.dismiss();
			break;
		case R.id.tv_no:
			alertDialog.dismiss();
			break;
		}
	}

	public void setList(List<LocationNoUploadBean> list) {
		// TODO Auto-generated method stub
		this.mList=list;
		notifyDataSetChanged();
	}
	
}
