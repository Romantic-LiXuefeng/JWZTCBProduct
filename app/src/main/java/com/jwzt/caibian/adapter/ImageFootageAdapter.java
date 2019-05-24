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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jwzt.caibian.util.TimeUtil;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.activity.ImageEditActivity;
import com.jwzt.caibian.activity.PreviewImageActivity;
import com.jwzt.caibian.bean.AttachsBeen;
import com.jwzt.caibian.interfaces.DeleteListener;
import com.jwzt.caibian.interfaces.EmptyListener;
import com.jwzt.caibian.interfaces.UploadIndexListener;
import com.jwzt.caibian.util.FileUtil;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.UserToast;
import com.jwzt.caibian.widget.SwipeMenuLayout;
import com.jwzt.caibian.xiangce.Bimp;
import com.jwzt.caibian.xiangce.ItemImage;
import com.jwzt.caibian.xiangce.OtherUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 图片素材的适配器
 * 
 * @author howie
 * 
 */
public class ImageFootageAdapter extends BaseAdapter implements OnClickListener{
	private ImageEditListener imageEditListener;
	/**需要编辑的条目的索引*/
	private int editIndex=-1;
	/**想要上传的条目的索引*/
	private int uploadIndex=-1;
	/**想要删除的条目的索引*/
	private int deleteIndex=-1;
	private SwipeMenuLayout deleteSML;
	private EmptyListener emptyListener;
	private DeleteListener deleteListener;
	private AlertDialog alertDialog;
	private Context mContext;
	private boolean isEditing;
	private int mWidth;
	private ArrayList<AttachsBeen> mList;
	private UploadIndexListener uploadIndexListener;
	
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	
	public ImageEditListener getImageEditListener() {
		return imageEditListener;
	}

	public void setImageEditListener(ImageEditListener imageEditListener) {
		this.imageEditListener = imageEditListener;
	}

	public void setUploadIndexListener(UploadIndexListener uploadIndexListener) {
		this.uploadIndexListener = uploadIndexListener;
	}

	public boolean isEditing() {
		return isEditing;
	}

	public void setEditing(boolean isEditing) {
		this.isEditing = isEditing;
	}

	public ImageFootageAdapter(Context mContext,ArrayList<AttachsBeen> attachList) {
		super();
		this.mContext = mContext;
		mList = attachList;
		int screenWidth = OtherUtils.getWidthInPx(mContext);
        mWidth = (screenWidth - OtherUtils.dip2px(mContext, 4))/3;
        
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
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final AttachsBeen attachsBeen = mList.get(position);
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(mContext,R.layout.image_footage_item_layout, null);
			holder = new ViewHolder();
			holder.sml=(SwipeMenuLayout) convertView.findViewById(R.id.sml);
			holder.iv_select = (ImageView) convertView.findViewById(R.id.iv_select);
			holder.iv_video = (ImageView) convertView.findViewById(R.id.iv_video);
			holder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
			holder.iv_share = (ImageView) convertView.findViewById(R.id.iv_share);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
			holder.rootView=(RelativeLayout)convertView.findViewById(R.id.rl_item_root_view);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		final String achsPath = attachsBeen.getAchsPath();
		System.out.println("filePathfilePath"+achsPath);
		if (achsPath != null) {
			String filePath="";
			if(achsPath.startsWith("file:///")){
				filePath=achsPath.replaceAll("file:///", "");
			}else{
				filePath=achsPath;
			}
			File file = new File(filePath);
			if (filePath.contains("http:")) {
				if (filePath.endsWith(".jpg") || filePath.endsWith(".png")|| filePath.endsWith(".jpeg")) {
					String endName = filePath.substring(filePath.lastIndexOf("/") + 1);
					holder.tv_name.setText(endName);
					long sss=file.length();
					System.out.println("ssssssssssssssssssssssssssssss"+sss);
					holder.tv_size.setText("大小：" + file.length() / 1024 + "KB");
				}
			} else {
				String fileName = file.getName();
				if (fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".jpeg")) {
					String endName = filePath.substring(filePath.lastIndexOf("/") + 1);
					holder.tv_name.setText(endName);
					long sss=file.length();
					System.out.println("ssssssssssssssssssssssssssssss"+sss);
					holder.tv_size.setText("大小：" + file.length() / 1024 + "KB");
				}
			}
			//设置创建时间
			final String modifiedTime = FileUtil.getModifiedTime(filePath);
			holder.tv_time.setText(TimeUtil.getMDHS(modifiedTime));
			if(attachsBeen.getAchsPath().startsWith("file:///")){
				imageLoader.displayImage(attachsBeen.getAchsPath(), holder.iv_video, options);
			}else{
				imageLoader.displayImage("file:///"+attachsBeen.getAchsPath(), holder.iv_video, options);
			}
			
			if(isEditing){
				if(holder.sml.isEnabled()){
					holder.sml.quickClose();
				}
				holder.sml.setSwipeEnable(false);//设置为不能左划
				holder.iv_select.setVisibility(View.VISIBLE);
				if(attachsBeen.getStatus()==AttachsBeen.STATUS_UNSELECTED){
					holder.iv_select.setImageResource(R.drawable.circle_right);
//					ItemImage itemImage = null;
					if(IsNonEmptyUtils.isList(Bimp.tempSelectBitmap)){
						for(int i=0;i<Bimp.tempSelectBitmap.size();i++){
							if(Bimp.tempSelectBitmap.get(i)!=null){
								String selectpath=Bimp.tempSelectBitmap.get(i).getFilepath();
								if(selectpath.startsWith("file:///")){
									String trimstartpath=selectpath.replaceFirst("file:///", "");
									if(filePath.equals(trimstartpath)){
//										itemImage=Bimp.tempSelectBitmap.get(i);
										Bimp.tempSelectBitmap.remove(i);
									}
								}else{
									if(filePath.equals(selectpath)){
//										itemImage=Bimp.tempSelectBitmap.get(i);
										Bimp.tempSelectBitmap.remove(i);
									}
								}
							}
						}
					}
//					holder.iv_select.setImageResource(R.drawable.footage_unselected);
				}else if(attachsBeen.getStatus()==AttachsBeen.STATUS_SELECTED){
					if(IsNonEmptyUtils.isList(Bimp.tempSelectBitmap)){
						if(Bimp.tempSelectBitmap.size()<10){
							holder.iv_select.setImageResource(R.drawable.right);
							if(isTaskGroup("file:///"+filePath)){//true表示存在该路径
								
							}else{//表示不存在该路径
								ItemImage itemImage=new ItemImage();
								itemImage.setBitmap(null);
								itemImage.setFilepath("file:///"+filePath);
								itemImage.setResult(true);
								Bimp.tempSelectBitmap.add(0,itemImage);
							}
						}else{
							UserToast.toSetToast(mContext, "您选择的资源已达上限");//9个资源为上限
						}
					}
//					holder.iv_select.setImageResource(R.drawable.footage_selected);
				}
				holder.iv_select.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(attachsBeen.getStatus()==AttachsBeen.STATUS_UNSELECTED){
							attachsBeen.setStatus(AttachsBeen.STATUS_SELECTED);
						}else if(attachsBeen.getStatus()==AttachsBeen.STATUS_SELECTED){
							attachsBeen.setStatus(AttachsBeen.STATUS_UNSELECTED);
						}
						notifyDataSetChanged();
					}
				});
			}else{
				holder.sml.setSwipeEnable(true);
				holder.iv_select.setVisibility(View.GONE);
			}
			
//			ImageLoader.getInstance().display(attachsBeen.getAchsPath(), holder.iv_video,mWidth, mWidth);
		}
		

		final SwipeMenuLayout swipeMenuLayout=holder.sml;
		holder.iv_delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				deleteSML=swipeMenuLayout;//先进行注释,等待操作   
				deleteIndex=position;
				showTip();
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
			holder.rootView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {  
					if(!isEditing){
						//获得原图地址
						String achsPath = mList.get(position).getAchsPicurl();
//						String achsPath = mList.get(position).getAchsPath();
						Intent it = new Intent(mContext, ImageEditActivity.class);
						String filePath = achsPath.replaceAll("Thumbnail", "image");
//						it.putExtra(ImageEditActivity.FILE_PATH, filePath);
						if(achsPath.startsWith("file:///")){
							filePath=achsPath.replaceAll("file:///", "");
						}else{
							filePath=achsPath;
						}
						it.putExtra("file_path", filePath);
					//	it.putExtra(ImageEditActivity.FILE_PATH, filePath);
						it.putExtra("pic_time", FileUtil.getModifiedTime(filePath));
						it.putExtra("pic_size", new File(filePath).length() / 1024 + "KB");
						((Activity)mContext).startActivity(it);
//						((Activity)mContext).startActivityForResult(it, 8);
						if(imageEditListener!=null){
							imageEditListener.imageEditIndex(position);
						}
					}
				}
			});
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
	
	public void update(ArrayList<AttachsBeen> attachList) {
		mList=attachList;
		notifyDataSetChanged();
	}
	
	public class ViewHolder {
		ImageView iv_select, iv_video, iv_delete, iv_share;
		TextView tv_name, tv_time, tv_size;
		RelativeLayout rootView;
		SwipeMenuLayout sml;
	
	}
	/**
	 * 显示是否进行删除操作的提示
	 */
	private void showTip(){
		if (alertDialog == null) {
			alertDialog = new AlertDialog.Builder(mContext)
					.create();
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

	@Override
	public void onClick(View view) {

		// TODO Auto-generated method stub
		switch(view.getId()){
		case R.id.tv_yes:
			//需要删除文件夹中的源文件
			String achsPicurl = mList.get(deleteIndex).getAchsPicurl();//原图
			String achsPath = mList.get(deleteIndex).getAchsPath();//缩略图
			File file1=new File(achsPath);
			File file2=new File(achsPicurl);
			if(file1.exists()&&file2.exists()){
				file1.delete();
				file2.delete();
			}
			deleteSML.quickClose();
			mList.remove(deleteIndex);
			if(deleteListener!=null){
				deleteListener.delete();
			}
			if(mList!=null&&mList.isEmpty()&&emptyListener!=null){//如果删除的是最后一条数据，删除之后就清空了
				emptyListener.empty();
			}
			notifyDataSetChanged();
//			saveVideoFootages();
			alertDialog.dismiss();
			break;
		case R.id.tv_no:
			alertDialog.dismiss();
			break;
		}
	
	}

	public void setEmptyListener(EmptyListener emptyListener) {
		this.emptyListener = emptyListener;
	}

	public void setDeleteListener(DeleteListener deleteListener) {
		this.deleteListener = deleteListener;
	}
	
	/**
	 * 回调图片编辑的索引的接口
	 * @author howie
	 *
	 */
	public interface ImageEditListener{
		void imageEditIndex(int index);
	}
	
}
