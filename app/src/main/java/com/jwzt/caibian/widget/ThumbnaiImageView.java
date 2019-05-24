package com.jwzt.caibian.widget;

import com.jwzt.cb.product.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;

/** 
 * @ClassName: AlbumItemView 
 * @Description:  ���Item�� ��ȡ������Ҫ��Ϊ��ʵ�ֵ��ImageView�䰵Ч��
 * @author LinJ
 * @date 2015-1-5 ����5:39:35 
 *  
 */
public class ThumbnaiImageView extends FrameLayout  {
	public static  final String TAG="AlbumItemView";
	private final ViewHolder mViewHolder;
	private final ImageLoader mImageLoader;
	private final DisplayImageOptions mOptions;
	private String mPath;
	private int mPosition;

	public ThumbnaiImageView(Context context,ImageLoader imageLoader,DisplayImageOptions options) {
		super(context);
		inflate(context, R.layout.item_album_grid, this);
		FilterImageView imageView=(FilterImageView) findViewById(R.id.imgThumbnail);
		CheckBox checkBox=(CheckBox) findViewById(R.id.checkbox);
		ImageView icon=(ImageView)findViewById(R.id.videoicon);
		mViewHolder=new ViewHolder(imageView,checkBox,icon);
		this.mImageLoader=imageLoader;
		this.mOptions=options;
	}

	/**  
	 *  ���ñ�ǩ
	 *  @param path ����itemָ����ļ�·�� ��ͬʱ��checkbox�ı�ǩ����Ϊ��ֵ
	 *  @param editable �Ƿ�ɱ༭״̬
	 *  @param checked  checkbox�Ƿ�ѡ��
	 */
	public void setTags(String path,int position,boolean editable,boolean checked){
		//�ɱ༭״̬����ʾcheckbox
		if (editable) {
			mViewHolder.checkBox.setVisibility(View.VISIBLE);
			mViewHolder.checkBox.setChecked(checked);
		}else {
			mViewHolder.checkBox.setVisibility(View.GONE);
		}
		//ԭ·���͵�ǰ·����ͬ������ͼƬ
		if (mPath==null||!mPath.equals(path)) {
			mImageLoader.displayImage(path, mViewHolder.imageView, mOptions);
//			mImageLoader.loadImage(path, mViewHolder.imageView, mOptions);
			mPath=path;
			//��checkbox����tag,���Լ�¼��ǰѡ����
			mViewHolder.checkBox.setTag(path);
			setTag(path);
			if(mPath.contains("video")){
				mViewHolder.videoIconView.setVisibility(View.VISIBLE);
			}else {
				mViewHolder.videoIconView.setVisibility(View.GONE);
			}
			mPosition=position;
		}
	}

	public int getPosition(){
		return mPosition;
	}
	/**  
	 * ����checkbox��״̬�ı��¼�
	 *  @param listener   
	 */
	public void setOnCheckedChangeListener(OnCheckedChangeListener listener){
		mViewHolder.checkBox.setOnCheckedChangeListener(listener);
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		//��дclick�¼�������View��clickת��imageview����
		mViewHolder.imageView.setOnClickListener(l);
	}

	public class ViewHolder {
		public ViewHolder(ImageView imageView,CheckBox checkBox,ImageView icon){
			this.imageView=imageView;
			this.checkBox=checkBox;
			this.videoIconView=icon;
		}
		ImageView imageView;//����ͼ
		ImageView videoIconView;//������Ƶͼ��
		CheckBox checkBox;//��ѡ��

	}
}
