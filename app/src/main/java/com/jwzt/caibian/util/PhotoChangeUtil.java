package com.jwzt.caibian.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;

import com.jwzt.caibian.widget.PhotoShowPopupWindow;
import com.jwzt.caibian.widget.SelectAudioPopupWindow;
import com.jwzt.caibian.widget.SelectPhotoPopupWindow;
import com.jwzt.caibian.widget.SelectVideoPopupWindow;

/***
 * billy修改版
 * 头像上传工具类 调用 getPhoto 在onactivityResult 调用
 * onPhotoFromCamera
 * onPhotoFromPick
 */
public class PhotoChangeUtil {

	public static SelectPhotoPopupWindow getPicPopupWindow(Context context, OnClickListener itemsOnClick,View viewAttach) {
		//实例化SelectPicPopupWindow
		SelectPhotoPopupWindow menuWindow = new SelectPhotoPopupWindow(context, itemsOnClick);
		//显示窗口
		menuWindow.showAtLocation(viewAttach, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
		return menuWindow;
	}

	/**
	 * 选择图片弹出窗
	 * @param context
	 * @param itemsOnClick
	 * @param viewAttach
	 * @return
	 */
	public static PhotoShowPopupWindow getPicPopupphoto(Context context, OnClickListener itemsOnClick,View viewAttach) {
		//实例化SelectPicPopupWindow
		PhotoShowPopupWindow menuWindow = new PhotoShowPopupWindow(context, itemsOnClick);
		//显示窗口
		menuWindow.showAtLocation(viewAttach, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
		return menuWindow;
	}
	
	/**
	 * 选择视频弹出窗
	 * @param context
	 * @param itemsOnClick
	 * @param viewAttach
	 * @return
	 */
	public static SelectVideoPopupWindow getVideoPopupWindow(Context context, OnClickListener itemsOnClick,View viewAttach) {
		//实例化SelectPicPopupWindow
		SelectVideoPopupWindow menuWindow = new SelectVideoPopupWindow(context, itemsOnClick);
		//显示窗口
		menuWindow.showAtLocation(viewAttach, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
		return menuWindow;
	}
	
	/**
	 * 选择音频弹出窗
	 * @param context
	 * @param itemsOnClick
	 * @param viewAttach
	 * @return
	 */
	public static SelectAudioPopupWindow getAudioPopupWindow(Context context, OnClickListener itemsOnClick,View viewAttach) {
		//实例化SelectPicPopupWindow
		SelectAudioPopupWindow menuWindow = new SelectAudioPopupWindow(context, itemsOnClick);
		//显示窗口
		menuWindow.showAtLocation(viewAttach, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
		return menuWindow;
	}
	
	public static boolean takePhoto(Activity activity, int takePhotoCode, File tempFile) {
		Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
		getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
		activity.startActivityForResult(getImageByCamera, takePhotoCode);
		return true;
	}

	public static boolean pickPhoto(Activity activity, int imageCode, File tempFile) {
		Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
		getImage.addCategory(Intent.CATEGORY_OPENABLE);
		getImage.setType("image/jpeg");
		activity.startActivityForResult(getImage, imageCode);
		return true;
	}

	/**
	 * 拍照获取图片的方式 用于切割的图片大小被限制在500,500
	 * 
	 * @param context
	 * @param zoomCode
	 * @param temppath 拍照前生成的临时路径
	 * @return 新的路劲
	 */
	public static String onPhotoFromCamera(final Activity context, final int zoomCode, final String temppath,
			final int aspectX, final int aspectY) {
		try {
			Bitmap btp = getLocalImage(new File(temppath), 1000, 1000);
			compressImage(btp, new File(temppath + "temp.jpg"), 30);
			photoZoom(context, Uri.fromFile(new File(temppath + "temp.jpg")), Uri.fromFile(new File(temppath)),
					zoomCode, aspectX, aspectY);
		} catch (Exception e) {
//			UserToast.toSetToast(context, "图片加载失败");
		}

		return temppath;
	}

	/**
	 * 图片切割完调用 如果还需要 Bitmap 调用getLocalImage
	 * 
	 * @param path
	 * @param rw
	 * @param rh
	 * @param compress
	 * @return
	 */
	public static File onPhotoZoom(String path, int rw, int rh, int compress) {

		File f = new File(path);
		Bitmap btp = PhotoChangeUtil.getLocalImage(f, rw, rh);
		compressImage(btp, f, compress);
		return f;
	}

	/**
	 * 相册获取图片,用于切割的图片大小被限制在500,500
	 * 
	 * @param context
	 * @param zoomCode
	 * @param temppath
	 *            希望生成的路劲
	 * @param data
	 */
	public static void onPhotoFromPick(final Activity context, final int zoomCode, final String temppath,
			final Intent data, final int aspectX, final int aspectY) {
		try {
			Bitmap btp = checkImage(context, data);
			compressImage(btp, new File(temppath + "temp.jpg"), 30);
			PhotoChangeUtil.photoZoom(context, Uri.fromFile(new File(temppath + "temp.jpg")),
					Uri.fromFile(new File(temppath)), zoomCode, aspectX, aspectY);
		} catch (Exception e) {
//			UserToast.toSetToast(context, "图片加载失败");
		}
	}

	/**
	 * data 中检出图片
	 * 
	 * @param activity
	 * @param data
	 * @return
	 */
	public static Bitmap checkImage(Activity activity, Intent data) {
		Bitmap bitmap = null;
		try {
			Uri originalUri = data.getData();
			String path = getRealPathFromURI(activity, originalUri);
			File f = activity.getExternalCacheDir();
			String pp = f.getAbsolutePath();
			if (path.indexOf(pp) != -1) {
				path = path.substring(path.indexOf(pp), path.length());
			}
			bitmap = getLocalImage(new File(path), 1000, 1000);
		} catch (Exception e) {
		} finally {
			return bitmap;
		}
	}

	/**
	 * 通过URI 获取真实路劲
	 * 
	 * @param activity
	 * @param contentUri
	 * @return
	 */
	public static String getRealPathFromURI(Activity activity, Uri contentUri) {
		Cursor cursor = null;
		String result = contentUri.toString();
		String[] proj = { MediaStore.Images.Media.DATA };
		cursor = activity.managedQuery(contentUri, proj, null, null, null);
		if (cursor == null)
			throw new NullPointerException("reader file field");
		if (cursor != null) {
			int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
			cursor.moveToFirst();
			result = cursor.getString(column_index);
			if (Integer.parseInt(Build.VERSION.SDK) < 14) {
				cursor.close();
			}
		}
		return result;
	}

	/**
	 * 图片压缩 上传图片时建议compress为30
	 * 
	 * @param bm
	 * @param f
	 */
	public static void compressImage(Bitmap bm, File f, int compress) {
		if (bm == null)
			return;
		File file = f;
		try {
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			OutputStream outStream = new FileOutputStream(file);
			bm.compress(Bitmap.CompressFormat.JPEG, compress, outStream);
			outStream.flush();
			outStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 由本地文件获取希望大小的文件
	 * 
	 * @param f
	 * @return
	 */
	public static Bitmap getLocalImage(File f, int swidth, int sheight) {
		File file = f;
		if (file.exists()) {
			try {
				file.setLastModified(System.currentTimeMillis());
				FileInputStream in = new FileInputStream(file);

				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeStream(in, null, options);
				int sWidth = swidth;
				int sHeight = sheight;
				int mWidth = options.outWidth;
				int mHeight = options.outHeight;
				int s = 1;
				while ((mWidth / s > sWidth * 2) || (mHeight / s > sHeight * 2)) {
					s *= 2;
				}
				options = new BitmapFactory.Options();
				options.inSampleSize = s;
				options.inPreferredConfig = Bitmap.Config.RGB_565;
				options.inPurgeable = true;
				options.inInputShareable = true;
				try {
					// 4. inNativeAlloc 属性设置为true，可以不把使用的内存算到VM里
					BitmapFactory.Options.class.getField("inNativeAlloc").setBoolean(options, true);
				} catch (Exception e) {
				}
				in.close();
				// 再次获取
				in = new FileInputStream(file);
				Bitmap bitmap = BitmapFactory.decodeStream(in, null, options);
				in.close();
				return bitmap;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} catch (Error e) {
				System.gc();
				return null;
			}
		}
		return null;
	}

	/**
	 * aspectY Y对于X的比例 outputX X 的宽
	 * **/
	public static void photoZoom(Activity activity, Uri uri, Uri outUri, int photoResoultCode, int aspectX, int aspectY) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		if (aspectY > 0) {
			intent.putExtra("aspectX", aspectX);
			intent.putExtra("aspectY", aspectY);
		}
		intent.putExtra("scale", aspectX == aspectY);
		intent.putExtra("return-data", true);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri);
		intent.putExtra("noFaceDetection", true); //
		activity.startActivityForResult(intent, photoResoultCode);
	}

	/**
	 * 保存zoom之后的图片
	 * @param data zoom后的intent
	 * @param context 上下文
	 * @return
	 */
	public static Bitmap getZoomBitMap(Intent data, Context context) {
		try {
			Bundle extras = data.getExtras();
			if (extras != null) {
				Bitmap bitmap = extras.getParcelable("data");
				return bitmap;
			}
		} catch (Exception e) {
			e.printStackTrace();
//			UserToast.toSetToast(context, "出现未知异常,请尝试其他图片");
		}
		return null;
	}
}
