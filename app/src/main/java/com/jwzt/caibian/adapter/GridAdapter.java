package com.jwzt.caibian.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.xiangce.Bimp;
import com.jwzt.caibian.xiangce.ImageLoader;
import com.jwzt.caibian.xiangce.ItemImage;


@SuppressLint("HandlerLeak")
public class GridAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private int selectedPosition = -1;
	private boolean shape;
    private Context context;
	public boolean isShape() {
		return shape;
	}

	public void setShape(boolean shape) {
		this.shape = shape;
	}

	public GridAdapter(Context context) {
		this.context=context;
		inflater = LayoutInflater.from(context);
	}

	public void update() {
		loading();
	}

	public int getCount() {
		/*if(Bimp.tempSelectBitmap.size() == 9){
			return 9;
		}*/
		return (Bimp.tempSelectBitmap.size());
	}

	public Object getItem(int arg0) {
		return null;
	}

	public long getItemId(int arg0) {
		return 0;
	}

	public void setSelectedPosition(int position) {
		selectedPosition = position;
	}

	public int getSelectedPosition() {
		return selectedPosition;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_published_grida,
					parent, false);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView
					.findViewById(R.id.item_grida_image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		/*if (position ==Bimp.tempSelectBitmap.size()) {
			holder.image.setImageBitmap(BitmapFactory.decodeResource(
					context.getResources(), R.drawable.icon_addpic_unfocused));
			holder.image.setVisibility(View.VISIBLE);
			if (position == 9) {
				holder.image.setVisibility(View.VISIBLE);
			}
		} else {*/
		     /* int degree = FileUtil.readPictureDegree(Bimp.tempSelectBitmap.get(position));  
	           if(degree!=0){
	               BitmapFactory.Options opts=new BitmapFactory.Options();//获取缩略图显示到屏幕上
	               opts.inSampleSize=2;
	               Bitmap cbitmap=BitmapFactory.decodeFile(Bimp.tempSelectBitmap.get(position),opts);
	           
	             *//** 
	             * 把图片旋转为正的方向 
	            *//*  
	            Bitmap bm = FileUtil.rotaingImageView(degree, cbitmap);    //控制旋转角度
		         holder.image.setImageBitmap(bm);

	           }else{*/
	          ItemImage  image=	Bimp.tempSelectBitmap.get(position);
		     if(image.isResult()){
		         holder.image.setImageBitmap(image.getBitmap());
 
		     }else{
		            
	               ImageLoader.getInstance().display(Bimp.tempSelectBitmap.get(position).getFilepath(), holder.image, 98, 78);
		     }
	          // }
			
           // ImageLoader.getInstance().display(Bimp.tempSelectBitmap.get(position), holder.image, 98, 78);

			//holder.image.setImageBitmap(Bimp.tempSelectBitmap.get(position).getBitmap());
		//}

		return convertView;
	}

	public class ViewHolder {
		public ImageView image;
	}

	public void loading() {
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					if (Bimp.max == Bimp.tempSelectBitmap.size()) {
						Message message = new Message();
						message.what = 1;
						//handler.sendMessage(message);
						break;
					} else {
						Bimp.max += 1;
						Message message = new Message();
						message.what = 1;
						//handler.sendMessage(message);
					}
				}
			}
		}).start();
	}
}
