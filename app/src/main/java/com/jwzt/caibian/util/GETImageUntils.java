package com.jwzt.caibian.util;

import java.lang.ref.WeakReference;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
/**
 * android4.4之上读取相册
 * @author AnYufan
 *
 */
public class GETImageUntils {
	 @TargetApi(Build.VERSION_CODES.KITKAT)
	      public static String getPath(final Context context, final Uri uri) {
	          final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
	          // DocumentProvider
	          if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
	              // ExternalStorageProvider
	              if (isExternalStorageDocument(uri)) {
	                  final String docId = DocumentsContract.getDocumentId(uri);
	                 final String[] split = docId.split(":");
	                  final String type = split[0];
	                 if ("primary".equalsIgnoreCase(type)) {
	                     return Environment.getExternalStorageDirectory() + "/"
	                              + split[1];
	                  }
	                  // TODO handle non-primary volumes
	              }
	              // DownloadsProvider
	              else if (isDownloadsDocument(uri)) {
	                  final String id = DocumentsContract.getDocumentId(uri);
	                  final Uri contentUri = ContentUris.withAppendedId(
	                          Uri.parse("content://downloads/public_downloads"),
	                          Long.valueOf(id));
	                 return getDataColumn(context, contentUri, null, null);
	              }
	              // MediaProvider
	              else if (isMediaDocument(uri)) {
	                  final String docId = DocumentsContract.getDocumentId(uri);
	                  final String[] split = docId.split(":");
	                  final String type = split[0];
	                  Uri contentUri = null;
	                  if ("image".equals(type)) {
	                      contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	                  } else if ("video".equals(type)) {
	                      contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
	                  } else if ("audio".equals(type)) {
	                      contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
	                  }
	                  final String selection = BaseColumns._ID + "=?";
	                  final String[] selectionArgs = new String[] { split[1] };
	                  return getDataColumn(context, contentUri, selection,
	                          selectionArgs);
	              }
	          }
	          // MediaStore (and general)
	          else if ("content".equalsIgnoreCase(uri.getScheme())) {
	              // Return the remote address
	              if (isGooglePhotosUri(uri))
	                  return uri.getLastPathSegment();
	              return getDataColumn(context, uri, null, null);
	          }
	          // File
	          else if ("file".equalsIgnoreCase(uri.getScheme())) {
	              return uri.getPath();
	          }
	          return null;
	      }
	  
	      /**
	 72      * Get the value of the data column for this Uri . This is useful for
	 73      * MediaStore Uris , and other file - based ContentProviders.
	 74      * 
	 75      * @param context
	 76      *            The context.
	 77      * @param uri
	 78      *            The Uri to query.
	 79      * @param selection
	 80      *            (Optional) Filter used in the query.
	 81      * @param selectionArgs
	 82      *            (Optional) Selection arguments used in the query.
	 83      * @return The value of the _data column, which is typically a file path.
	 84      */
	      public static String getDataColumn(Context context, Uri uri,
	              String selection, String[] selectionArgs) {
	          Cursor cursor = null;
	          final String column = MediaColumns.DATA;
	          final String[] projection = { column };
	          try {
	              cursor = context.getContentResolver().query(uri, projection,
	                      selection, selectionArgs, null);
	              if (cursor != null && cursor.moveToFirst()) {
	                  final int index = cursor.getColumnIndexOrThrow(column);
	                  return cursor.getString(index);
	              }
	          } finally {
	              if (cursor != null)
	                  cursor.close();
	         }
	         return null;
	     }
	 
	     /**
	105      * @param uri
	106      *            The Uri to check.
	107      * @return Whether the Uri authority is ExternalStorageProvider.
	108      */
	     public static boolean isExternalStorageDocument(Uri uri) {
	         return "com.android.externalstorage.documents".equals(uri
	                 .getAuthority());
	     }
	 
	     /**
	115      * @param uri
	116      *            The Uri to check.
	117      * @return Whether the Uri authority is DownloadsProvider.
	118      */
	     public static boolean isDownloadsDocument(Uri uri) {
	         return "com.android.providers.downloads.documents".equals(uri
	                 .getAuthority());
	     }
	 
	     /**
	125      * @param uri
	126      *            The Uri to check.
	127      * @return Whether the Uri authority is MediaProvider.
	128      */
	     public static boolean isMediaDocument(Uri uri) {
	         return "com.android.providers.media.documents".equals(uri
	                 .getAuthority());
	     }
	 
	     /**
	135      * @param uri
	136      *            The Uri to check.
	137      * @return Whether the Uri authority is Google Photos.
	138      */
	     public static boolean isGooglePhotosUri(Uri uri) {
	         return "com.google.android.apps.photos.content".equals(uri
	                 .getAuthority());
	     }
	     
	     
	     public static Bitmap convertToBitmap(String path, int w, int h) { 
	         BitmapFactory.Options opts = new BitmapFactory.Options(); 
	     // 设置为ture只获取图片大小 
	       opts.inJustDecodeBounds = true; 
	        opts.inPreferredConfig = Bitmap.Config.ARGB_8888; 
	        // 返回为空 
	       BitmapFactory.decodeFile(path, opts); 
	      int width = opts.outWidth; 
	       int height = opts.outHeight; 
	       float scaleWidth = 0.f, scaleHeight = 0.f; 
	       if (width > w || height > h) { 
	          // 缩放 
	          scaleWidth = ((float) width) / w; 
	          scaleHeight = ((float) height) / h; 
	      } 
	     opts.inJustDecodeBounds = false; 
	       float scale = Math.max(scaleWidth, scaleHeight); 
	        opts.inSampleSize = (int)scale; 
	        WeakReference<Bitmap> weak = new WeakReference<Bitmap>(BitmapFactory.decodeFile(path, opts)); 
	       return Bitmap.createScaledBitmap(weak.get(), w, h, true); 
	     } 
	
	
}
