package com.jwzt.caibian.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.activity.PhotoActivity;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.widget.DragImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;


public final class PhotoFragment extends Fragment {
	private static final String KEY_CONTENT = "photo";
//	private ImageLoader4DragImg loader;
	private DragImageView image;
	private RelativeLayout rl_image;
	private String imgPath = "";
	private View view;
	
	private static DisplayImageOptions options;  
	private static ImageLoader imageLoader;

	public static PhotoFragment newInstance() {
		return null;
	}

	public static PhotoFragment newInstance(String content,Context context) {
		PhotoFragment fragment = new PhotoFragment();
		fragment.imgPath = content;
		options = new DisplayImageOptions.Builder()  
        .showImageOnLoading(R.drawable.replace) // 设置图片下载期间显示的图片  
        .showImageForEmptyUri(R.drawable.replace) // 设置图片Uri为空或是错误的时候显示的图片  
        .showImageOnFail(R.drawable.replace) // 设置图片加载或解码过程中发生错误显示的图片  
        .cacheInMemory(true) // 设置下载的图片是否缓存在内存中  
        .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中  
/*                .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片  
*/        .build(); // 构建完成  
         imageLoader = ImageLoader.getInstance();
		
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if ((savedInstanceState != null)
				&& savedInstanceState.containsKey(KEY_CONTENT)) {
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 写一个布局文件，包含一个ImageView，通过Inflate的方式填充为View，返回，应该能解决图片不能全屏的问题
		if (view == null) {
			view = inflater.inflate(R.layout.showtuji, null);
		}
		ViewGroup p = (ViewGroup) view.getParent();
		if (p != null&&view!=null) {
			p.removeView(view);
		}
		image = (DragImageView) view.findViewById(R.id.newtujipho);
		rl_image=(RelativeLayout) view.findViewById(R.id.rl_image);
		rl_image.setOnClickListener(ClickListener);
		
//		DisplayMetrics dm = new DisplayMetrics();
//		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
//		FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, dm.heightPixels/3);
//		params.gravity = Gravity.CENTER;
//		image.setLayoutParams(params);
		
		if(IsNonEmptyUtils.isString(imgPath)){
			if(!imgPath.startsWith("file:///")&&!imgPath.startsWith("http://")){
				imageLoader.displayImage("file:///"+imgPath, image, options);  
			}else{
				imageLoader.displayImage(imgPath, image, options);  
			}
		}
		
		return view;
	}
	
	private OnClickListener ClickListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			((PhotoActivity)getActivity()).setFinish();
		}
	};
}
