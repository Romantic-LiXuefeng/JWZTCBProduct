package com.jwzt.caibian.adapter;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jwzt.caibian.util.TimeUtil;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.activity.AudioEditActivity;
import com.jwzt.caibian.bean.AttachsBeen;
import com.jwzt.caibian.interfaces.AudioEditInterface;
import com.jwzt.caibian.interfaces.EmptyListener;
import com.jwzt.caibian.interfaces.UploadIndexListener;
import com.jwzt.caibian.util.FileUtil;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.UserToast;
import com.jwzt.caibian.widget.SwipeMenuLayout;
import com.jwzt.caibian.xiangce.Bimp;
import com.jwzt.caibian.xiangce.ItemImage;

/**
 * 音频素材的适配器
 * 
 * @author howie
 * 
 */
public class AudioFootageAdapter2 extends BaseAdapter{
	/**想要上传的条目的索引*/
	private int uploadIndex=-1;
	/** 想要删除的条目的索引 */
	private int deleteIndex = -1;
	/** 想要删除的条目对应的滑动删除布局 */
	private SwipeMenuLayout deleteSML;
	private EmptyListener emptyListener;
	private int currentPlaying = -1;// 用于记录当前正在播放的视频
	private AlertDialog alertDialog;
	private UploadIndexListener uploadIndexListener;
	
	private Context mContext;
	private ArrayList<AttachsBeen> mList;
	private AudioEditInterface mAudioEditInterface;
	
	public void setUploadIndexListener(UploadIndexListener uploadIndexListener) {
		this.uploadIndexListener = uploadIndexListener;
	}

	private boolean editing;

	public boolean isEditing() {
		return editing;
	}

	public void setEditing(boolean editing) {
		this.editing = editing;
	}

	public AudioFootageAdapter2(Context mContext, ArrayList<AttachsBeen> strs,AudioEditInterface audioEditInterface) {
		super();
		this.mContext = mContext;
		mList = strs;
		this.mAudioEditInterface=audioEditInterface;
	}

	@Override
	public int getCount() {
		System.out.println("音频个数============"+mList.size());
		return mList.size();
	}

	@Override
	public AttachsBeen getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final AttachsBeen attachsBeen = mList.get(position);
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(mContext,R.layout.audio_footage_item_layout2, null);
			holder.sml = (SwipeMenuLayout) convertView.findViewById(R.id.sml);
			holder.iv_select = (ImageView) convertView.findViewById(R.id.iv_select);
			holder.iv_video = (ImageView) convertView.findViewById(R.id.iv_video);
			holder.iv_play = (ImageView) convertView.findViewById(R.id.iv_play);
			holder.iv_arrow = (ImageView) convertView.findViewById(R.id.iv_arrow);

			holder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete_item);
			holder.iv_share = (ImageView) convertView.findViewById(R.id.iv_share);
			holder.iv_select_copy = (ImageView) convertView.findViewById(R.id.iv_select_copy);
			holder.iv_video_copy = (ImageView) convertView.findViewById(R.id.iv_video_copy);
			holder.iv_play_copy = (ImageView) convertView.findViewById(R.id.iv_play_copy);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.tv_mark_num = (TextView) convertView.findViewById(R.id.tv_mark_num);
			holder.tv_name_copy = (TextView) convertView.findViewById(R.id.tv_name_copy);
			holder.tv_time_copy = (TextView) convertView.findViewById(R.id.tv_time_copy);
			holder.tv_mark_num_copy = (TextView) convertView.findViewById(R.id.tv_mark_num_copy);
			holder.rl_copy = convertView.findViewById(R.id.rl_copy);
			holder.rl_root_view = convertView.findViewById(R.id.rl_root_view);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}

		//holder.iv_arrow.setVisibility(View.INVISIBLE);
		holder.tv_name.setText(attachsBeen.getAchsPath());
		holder.tv_name_copy.setText(attachsBeen.getAchsPath());
		// 最后一次修改的日期
		String modifiedTime = FileUtil.getModifiedTime(attachsBeen.getAchsPath());
		holder.tv_time.setText(TimeUtil.getMDHS(modifiedTime));
		holder.tv_time_copy.setText(TimeUtil.getMDHS(modifiedTime));
		String substring = attachsBeen.getAchsPath().substring(attachsBeen.getAchsPath().lastIndexOf("/") + 1).replace(".wav", "");
		int size=mContext.getSharedPreferences(substring, mContext.MODE_PRIVATE).getInt("size", 0);

		holder.tv_mark_num.setText("×"+size);
		holder.tv_mark_num_copy.setText("×"+size);

		final SwipeMenuLayout swipeMenuLayout=holder.sml;
		holder.iv_delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AttachsBeen attachsBeen2 = mList.get(position);
				showTip(attachsBeen2,position);
				deleteIndex = position;
				deleteSML = swipeMenuLayout;//先进行注释,等待操作
			}
		});
		holder.iv_share.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(uploadIndexListener!=null){
					uploadIndexListener.uploadIndex(position);
				}
			}
		});


		final String filePath = mList.get(position).getAchsPath();
		if (filePath != null) {
			File file = new File(filePath);
			if (file.exists()) {
				System.out.println("文件存在");
			} else {
				System.out.println("文件不存在");
			}
			if (filePath.contains("http:")) {
				if (filePath.endsWith(".mp3") || filePath.endsWith(".amr")
						|| filePath.endsWith(".wav")) {
					String endName = filePath.substring(filePath
							.lastIndexOf("/") + 1);
					holder.tv_name.setText(endName);
					holder.tv_name_copy.setText(endName);
				}
			} else {

				String fileName = file.getName();
				// if (fileName.endsWith(".mp3") || fileName.endsWith(".wav")
				// || fileName.endsWith(".amr")) {
				String endName = filePath
						.substring(filePath.lastIndexOf("/") + 1);
				holder.tv_name.setText(endName);
				holder.tv_name_copy.setText(endName);
				// }
			}
		}

		holder.rl_root_view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String achsPath = mList.get(position).getAchsPath();
				if(mAudioEditInterface!=null){
					mAudioEditInterface.setAudioEdit(achsPath);
				}
//				 if(achsPath.endsWith(".wav")){
//					 Intent it = new Intent(mContext, AudioEditActivity.class);
//					 String filePath = achsPath;
//					 it.setData(Uri.parse(filePath));
//					 ((Activity) mContext).startActivityForResult(it, 3);
//				 }else{
//					 Toast.makeText(mContext, "暂不支持编辑其他格式音频!", 0).show();
//				 }
			}
		});

		if (editing) {// 如果处于编辑状态
			holder.rl_copy.setVisibility(View.VISIBLE);
			holder.sml.setVisibility(View.INVISIBLE);

			int status = attachsBeen.getStatus();
			if (status == AttachsBeen.STATUS_SELECTED) {
				if(IsNonEmptyUtils.isList(Bimp.tempSelectBitmap)){
					int selectsize=Bimp.tempSelectBitmap.size();
					if(Bimp.tempSelectBitmap.size()<10){
						holder.iv_select.setImageResource(R.drawable.right);
						holder.iv_select_copy.setImageResource(R.drawable.right);
						if(isTaskGroup(filePath)){//true表示存在该路径

						}else{//表示不存在该路径
							ItemImage itemImage=new ItemImage();
							itemImage.setBitmap(null);
							itemImage.setFilepath(filePath);
							itemImage.setResult(true);
							Bimp.tempSelectBitmap.add(0,itemImage);
						}
					}else{
						UserToast.toSetToast(mContext, "您选择的资源已达上限");//9个资源为上限
					}
				}
			} else if (status == AttachsBeen.STATUS_UNSELECTED) {
				holder.iv_select.setImageResource(R.drawable.circle_right);
				holder.iv_select_copy.setImageResource(R.drawable.circle_right);
				if(IsNonEmptyUtils.isList(Bimp.tempSelectBitmap)){
					for(int i=0;i<Bimp.tempSelectBitmap.size();i++){
						if(Bimp.tempSelectBitmap.get(i)!=null){
							if(filePath.equals(Bimp.tempSelectBitmap.get(i).getFilepath())){
//								itemImage=Bimp.tempSelectBitmap.get(i);
								Bimp.tempSelectBitmap.remove(i);
							}
						}
					}
				}
//				holder.iv_select_copy.setImageResource(R.drawable.footage_unselected);
			}
			holder.iv_select_copy.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int status2 = attachsBeen.getStatus();
					if (status2 == AttachsBeen.STATUS_UNSELECTED) {
						attachsBeen.setStatus(AttachsBeen.STATUS_SELECTED);
					} else if (status2 == AttachsBeen.STATUS_SELECTED) {
						attachsBeen.setStatus(AttachsBeen.STATUS_UNSELECTED);
					}
					notifyDataSetChanged();
				}
			});

		} else {
			holder.rl_copy.setVisibility(View.INVISIBLE);
			holder.sml.setVisibility(View.VISIBLE);
			holder.iv_select_copy
					.setImageResource(R.drawable.footage_unselected);
			if (attachsBeen.getStatus() == AttachsBeen.STATUS_NORMAL) {
				holder.iv_select
						.setImageResource(R.drawable.footage_video_play);
			} else if (attachsBeen.getStatus() == AttachsBeen.STATUS_PLAYING) {
				holder.iv_select
						.setImageResource(R.drawable.footage_video_pause);
			}
			holder.iv_select.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					editing = false;
					if (position == currentPlaying) {// 如果点击的是正在播放的那一个
						if (attachsBeen.getStatus() == AttachsBeen.STATUS_NORMAL) {
							attachsBeen.setStatus(AttachsBeen.STATUS_PLAYING);

						} else if (attachsBeen.getStatus() == AttachsBeen.STATUS_PLAYING) {
							attachsBeen.setStatus(AttachsBeen.STATUS_NORMAL);
						}
					} else {// 如果点击的和正在播放的不是同一个
						for (int i = 0; i < mList.size(); i++) {
							if (i != position) {
								mList.get(i).setStatus(
										AttachsBeen.STATUS_NORMAL);
							}
						}
						currentPlaying = position;
						attachsBeen.setStatus(AttachsBeen.STATUS_PLAYING);
					}
					notifyDataSetChanged();
				}
			});

		}
		System.out.println("==========================================");
		return convertView;

	}

	/**
	 * 判断该路径是否在所选的集合中 返回true表示集合中有改路径 false没有
	 * @return
	 */
	private boolean isTaskGroup(String resourcePath){
		if(IsNonEmptyUtils.isList(Bimp.tempSelectBitmap)){
			for(int i=0;i<Bimp.tempSelectBitmap.size();i++){
				if(Bimp.tempSelectBitmap.get(i)!=null){
					if(resourcePath.equals(Bimp.tempSelectBitmap.get(i).getFilepath())){
						return true;
					}
				}
			}
		}else{
			return false;
		}
		return false;
	}
	
	public void remove(AttachsBeen item) {
		mList.remove(item);
		notifyDataSetChanged();
	}

	public void insert(AttachsBeen item, int to) {
		mList.add(to, item);
		notifyDataSetChanged();
	}

	public class ViewHolder {
		SwipeMenuLayout sml;
		ImageView iv_select, iv_arrow,iv_video, iv_play, iv_delete, iv_share,iv_select_copy, iv_video_copy, iv_play_copy;
		TextView tv_name, tv_time, tv_mark_num, tv_name_copy, tv_time_copy,tv_mark_num_copy;
		View rl_copy, rl_root_view;

		
	}

	public void update(ArrayList<AttachsBeen> list) {
		mList = list;
		notifyDataSetChanged();
	}

	class MyClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

		}

	}

	public int getCurrentPlaying() {
		return currentPlaying;
	}

	public void setCurrentPlaying(int currentPlaying) {
		this.currentPlaying = currentPlaying;
	}

	/**
	 * 显示是否进行删除操作的提示
	 * @param attachsBeen2 
	 */
	private void showTip(final AttachsBeen attachsBeen2,final int position) {
		if (alertDialog == null) {
			alertDialog = new AlertDialog.Builder(mContext).create();
			OnKeyListener keylistener = new OnKeyListener() {
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK
							&& event.getRepeatCount() == 0) {
						return true;
					} else {
						return false;
					}
				}
			};
			alertDialog.setOnKeyListener(keylistener);// 保证按返回键的时候alertDialog也不会消失
			alertDialog.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface arg0) {
					// TODO Auto-generated method stub

				}
			});
		}
		alertDialog.show();
		View tipView = View.inflate(mContext, R.layout.edit_alert_layout, null);
		TextView tv_tip = (TextView) tipView.findViewById(R.id.tv_tip);
		tv_tip.setText("确认要删除选中的素材吗？");
		View tv_yes = (TextView) tipView.findViewById(R.id.tv_yes);
		// 不再提醒
		tv_yes.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String achsPath = attachsBeen2.getAchsPath();
				File file = new File(achsPath);
				if (file.exists())
					file.delete();
				deleteSML.quickClose();
				mList.remove(position);
				notifyDataSetChanged();
				alertDialog.dismiss();
				
				
			}
		});
		tipView.findViewById(R.id.tv_no).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});
		alertDialog.setContentView(tipView);

	}


	public void setEmptyListener(EmptyListener emptyListener) {
		this.emptyListener = emptyListener;
	}

	public void setList(ArrayList<AttachsBeen> attachList) {
		this.mList=attachList;
		notifyDataSetChanged();
	}
}
