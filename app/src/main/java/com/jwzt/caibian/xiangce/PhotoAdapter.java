package com.jwzt.caibian.xiangce;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.activity.PhotoPickerActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;


/**
 * @Class: PhotoAdapter
 * @Description: 图片适配器

 */
public class PhotoAdapter extends BaseAdapter {

    private static final int TYPE_CAMERA = 0;
    private static final int TYPE_PHOTO = 1;

    private List<Photo> mDatas;
    //存放已选中的Photo数据
    private List<String> mSelectedPhotos;
    private Context mContext;
    private int mWidth;
    //是否显示相机，默认不显示
    private boolean mIsShowCamera = false;
    //照片选择模式，默认单选
    private int mSelectMode = PhotoPickerActivity.MODE_SINGLE;
    //图片选择数量
    private int mMaxNum = PhotoPickerActivity.DEFAULT_NUM;

    private View.OnClickListener mOnPhotoClick;
    private PhotoClickCallBack mCallBack;
    
    private DisplayImageOptions options;

    public PhotoAdapter(Context context, List<Photo> mDatas) {
        this.mDatas = mDatas;
        this.mContext = context;
        int screenWidth = OtherUtils.getWidthInPx(mContext);
        mWidth = (screenWidth - OtherUtils.dip2px(mContext, 4))/3;
        options = new DisplayImageOptions.Builder()  
        .showImageOnLoading(R.drawable.ic_photo_loading) // 设置图片下载期间显示的图片  
        .showImageForEmptyUri(R.drawable.ic_photo_loading) // 设置图片Uri为空或是错误的时候显示的图片  
        .showImageOnFail(R.drawable.ic_photo_loading) // 设置图片加载或解码过程中发生错误显示的图片  
        .cacheInMemory(false) // 设置下载的图片是否缓存在内存中  
        .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中 
        .bitmapConfig(Config.RGB_565)
//        .displayer(new FadeInBitmapDisplayer(100))
        .build(); // 构建完成  
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0 && mIsShowCamera) {
            return TYPE_CAMERA;
        } else {
            return TYPE_PHOTO;
        }
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Photo getItem(int position) {
        if(mIsShowCamera) {
            if(position == 0){
                return null;
            }
            return mDatas.get(position-1);
        }else{
            return mDatas.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return mDatas.get(position).getId();
    }

    public void setDatas(List<Photo> mDatas) {
        this.mDatas = mDatas;
    }

    public void setIsShowCamera(boolean isShowCamera) {
        this.mIsShowCamera = isShowCamera;
    }

    public boolean isShowCamera() {
        return mIsShowCamera;
    }

    public void setMaxNum(int maxNum) {
        this.mMaxNum = maxNum;
    }

    public void setPhotoClickCallBack(PhotoClickCallBack callback) {
        mCallBack = callback;
    }


    /**
     * 获取已选中相片
     * @return
     */
    public List<String> getmSelectedPhotos() {
        return mSelectedPhotos;
    }

    public void setSelectMode(int selectMode) {
        this.mSelectMode = selectMode;
        if(mSelectMode == PhotoPickerActivity.MODE_MULTI) {
            initMultiMode();
        }
    }

    /**
     * 初始化多选模式所需要的参数
     */
    private void initMultiMode() {
        mSelectedPhotos = new ArrayList<String>();
        mOnPhotoClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = v.findViewById(R.id.imageview_photo).getTag().toString();
                if(mSelectedPhotos.contains(path)) {
                    v.findViewById(R.id.mask).setVisibility(View.GONE);
                    v.findViewById(R.id.checkmark).setSelected(false);
                    mSelectedPhotos.remove(path);
                } else {
                    if(mSelectedPhotos.size() >= mMaxNum) {
                        Toast.makeText(mContext, R.string.msg_maxi_capacity,
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mSelectedPhotos.add(path);
                    v.findViewById(R.id.mask).setVisibility(View.VISIBLE);
                    v.findViewById(R.id.checkmark).setSelected(true);
                }
                if(mCallBack != null) {
                    mCallBack.onPhotoClick();
                }
            }
        };
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(getItemViewType(position) == TYPE_CAMERA) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_camera_layout, null);
            convertView.setTag(null);
            //设置高度等于宽度
            GridView.LayoutParams lp = new GridView.LayoutParams(mWidth, mWidth);
            convertView.setLayoutParams(lp);
        } else {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_photo_layout, null);
                holder.photoImageView = (ImageView) convertView.findViewById(R.id.imageview_photo);
                holder.selectView = (ImageView) convertView.findViewById(R.id.checkmark);
                holder.maskView = convertView.findViewById(R.id.mask);
                holder.wrapLayout = (FrameLayout) convertView.findViewById(R.id.wrap_layout);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.photoImageView.setImageResource(R.drawable.ic_photo_loading);
            Photo photo = getItem(position);
            String resourcePath=photo.getPath();
            if(mSelectMode == PhotoPickerActivity.MODE_MULTI) {
                holder.wrapLayout.setOnClickListener(mOnPhotoClick);
                holder.photoImageView.setTag(photo.getPath());
                holder.selectView.setVisibility(View.VISIBLE);
                if(mSelectedPhotos != null && mSelectedPhotos.contains(photo.getPath())) {
                    holder.selectView.setSelected(true);
                    holder.maskView.setVisibility(View.VISIBLE);
                } else {
                    holder.selectView.setSelected(false);
                    holder.maskView.setVisibility(View.GONE);
                }
            } else {
                holder.selectView.setVisibility(View.GONE);
            }
            
            
            if(resourcePath.endsWith(".mp4") || resourcePath.endsWith(".3gp")
            		|| resourcePath.endsWith(".wmv")|| resourcePath.endsWith(".rmvb")
                    || resourcePath.endsWith(".mov")|| resourcePath.endsWith(".avi")
                    || resourcePath.endsWith(".m3u8")|| resourcePath.endsWith(".mkv") 
                    || resourcePath.endsWith(".flv")){//表示是视频
            	//视频缩略图的添加
            	Uri uri=Uri.fromFile(new File(resourcePath));
            	com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(uri+"", holder.photoImageView, options);
//        		Bitmap srcBitmap = ThumbnailUtils.createVideoThumbnail(resourcePath, 0);
//        		srcBitmap = ThumbnailUtils.extractThumbnail(srcBitmap, mWidth, mWidth);
//        		holder.photoImageView.setImageBitmap(srcBitmap);
            }else if(resourcePath.endsWith(".mp3")){
            	holder.photoImageView.setImageResource(R.drawable.ic_photo_loading);
            }else{
            	System.out.println("photopath:"+photo.getPath());;
                ImageLoader.getInstance().display(photo.getPath(), holder.photoImageView,mWidth, mWidth);
            }
        }
        return convertView;
    }

    private class ViewHolder {
        private ImageView photoImageView;
        private ImageView selectView;
        private View maskView;
        private FrameLayout wrapLayout;
    }

    /**
     * 多选时，点击相片的回调接口
     */
    public interface PhotoClickCallBack {
        void onPhotoClick();
    }
}
