package com.jwzt.caibian.xiangce;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
  图片控制类
 */
public class PhotoUtils {

	/**
	 * 获取本地图片
	 * @param context
	 * @return
	 */
    public static Map<String, PhotoFloder> getPhotos(Context context) {
        Map<String, PhotoFloder> floderMap = new HashMap<String, PhotoFloder>();

        String allPhotosKey = "所有图片";
        PhotoFloder allFloder = new PhotoFloder();
        allFloder.setName(allPhotosKey);
        allFloder.setDirPath(allPhotosKey);
        allFloder.setPhotoList(new ArrayList<Photo>());
        floderMap.put(allPhotosKey, allFloder);

        Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = context.getContentResolver();

        // 只查询jpeg和png的图片
        Cursor mCursor = mContentResolver.query(imageUri, null,MediaStore.Images.Media.MIME_TYPE + " in(?, ?)", new String[] { "image/jpeg", "image/png" },MediaStore.Images.Media.DATE_MODIFIED + " desc");

        int pathIndex = mCursor.getColumnIndex(MediaStore.Images.Media.DATA);

        mCursor.moveToFirst();
        do{
            // 获取图片的路径
            String path = mCursor.getString(pathIndex);

            // 获取该图片的父路径名
            File parentFile = new File(path).getParentFile();
            if (parentFile == null) {
                continue;
            }
            String dirPath = parentFile.getAbsolutePath();

            if (floderMap.containsKey(dirPath)) {
                Photo photo = new Photo(path);
                PhotoFloder photoFloder = floderMap.get(dirPath);
                photoFloder.getPhotoList().add(photo);
                floderMap.get(allPhotosKey).getPhotoList().add(photo);
                continue;
            } else {// 初始化imageFloder
                PhotoFloder photoFloder = new PhotoFloder();
                List<Photo> photoList = new ArrayList<Photo>();
                Photo photo = new Photo(path);
                photoList.add(photo);
                photoFloder.setPhotoList(photoList);
                photoFloder.setDirPath(dirPath);
                photoFloder.setName(dirPath.substring(dirPath.lastIndexOf(File.separator) + 1, dirPath.length()));
                floderMap.put(dirPath, photoFloder);
                floderMap.get(allPhotosKey).getPhotoList().add(photo);
            }
        }while (mCursor.moveToNext());
        mCursor.close();
        return floderMap;
    }
    
    /**
     * 获取本地视频
     * @param context
     * @return
     */
    public static Map<String, PhotoFloder> getVideo(Context context) {
        Map<String, PhotoFloder> floderMap = new HashMap<String, PhotoFloder>();
        String allPhotosKey = "所有视频";
        PhotoFloder allFloder = new PhotoFloder();
        allFloder.setName(allPhotosKey);
        allFloder.setDirPath(allPhotosKey);
        allFloder.setPhotoList(new ArrayList<Photo>());
        floderMap.put(allPhotosKey, allFloder);

        Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = context.getContentResolver();

        // 只查询jpeg和png的图片
        Cursor mCursor = mContentResolver.query(imageUri, null,MediaStore.Images.Media.MIME_TYPE + " in(?, ?)", new String[] { "image/jpeg", "image/png" },MediaStore.Images.Media.DATE_MODIFIED + " desc");

        int pathIndex = mCursor.getColumnIndex(MediaStore.Video.Media.DATA);

        mCursor.moveToFirst();
        while (mCursor.moveToNext()) {
            // 获取图片的路径
            String path = mCursor.getString(pathIndex);

            // 获取该图片的父路径名
            File parentFile = new File(path).getParentFile();
            if (parentFile == null) {
                continue;
            }
            String dirPath = parentFile.getAbsolutePath();

            if (floderMap.containsKey(dirPath)) {
                Photo photo = new Photo(path);
                PhotoFloder photoFloder = floderMap.get(dirPath);
                photoFloder.getPhotoList().add(photo);
                floderMap.get(allPhotosKey).getPhotoList().add(photo);
                continue;
            } else {// 初始化imageFloder
                PhotoFloder photoFloder = new PhotoFloder();
                List<Photo> photoList = new ArrayList<Photo>();
                Photo photo = new Photo(path);
                photoList.add(photo);
                photoFloder.setPhotoList(photoList);
                photoFloder.setDirPath(dirPath);
                photoFloder.setName(dirPath.substring(dirPath.lastIndexOf(File.separator) + 1, dirPath.length()));
                floderMap.put(dirPath, photoFloder);
                floderMap.get(allPhotosKey).getPhotoList().add(photo);
            }
        }
        mCursor.close();
        return floderMap;
    }
    
    
    /**
     * 获取视频文件
     * @param list
     * @param file
     * @return
     */
    public static Map<String, PhotoFloder> getVideoFile1(Context context) {
    	final Map<String, PhotoFloder> floderMap = new HashMap<String, PhotoFloder>();
    	final String allPhotosKey = "所有视频";
        PhotoFloder allFloder = new PhotoFloder();
        allFloder.setName(allPhotosKey);
        allFloder.setDirPath(allPhotosKey);
        allFloder.setPhotoList(new ArrayList<Photo>());
        floderMap.put(allPhotosKey, allFloder);
//        File files=Environment.getExternalStorageDirectory();
        File files=new File(Environment.getExternalStorageDirectory()+"/jwzt_recorder/merge");
        File fileArray[] = files.listFiles();  
//        if(files != null){
//        	for (File f : fileArray){  
//                if(f.isDirectory()){  
//                    
//                }else{  `
//                    System.out.println("adfsgkjadsgjkjkadsgh"+f);  
                    files.listFiles(new FileFilter() {
                    	
                    	@Override
                    	public boolean accept(File file) {
                    		String name = file.getName();
                    		System.out.println("namename:"+name);
                    		int i = name.indexOf('.');
                    		if (i != -1) {
                    			name = name.substring(i);
                    			if (name.equalsIgnoreCase(".mp4")
                    					|| name.equalsIgnoreCase(".3gp")
                    					|| name.equalsIgnoreCase(".wmv")
                    					|| name.equalsIgnoreCase(".rmvb")
                    					|| name.equalsIgnoreCase(".mov")
                    					|| name.equalsIgnoreCase(".avi")
                    					|| name.equalsIgnoreCase(".m3u8")
                    					|| name.equalsIgnoreCase(".flv")
                    					|| name.equalsIgnoreCase(".swf")) {
                    				
                    				//获取视频文件绝对路径
                    				String filePath=file.getAbsolutePath();
                    				// 获取该视频的父路径名
                    				File parentFile = new File(filePath).getParentFile();
                    				String dirPath = parentFile.getAbsolutePath();
                    				
                    				if (floderMap.containsKey(dirPath)) {
                    					Photo photo = new Photo(filePath);
                    					PhotoFloder photoFloder = floderMap.get(dirPath);
                    					photoFloder.getPhotoList().add(photo);
                    					floderMap.get(allPhotosKey).getPhotoList().add(photo);
                    				} else {// 初始化imageFloder
                    					PhotoFloder photoFloder = new PhotoFloder();
                    					List<Photo> photoList = new ArrayList<Photo>();
                    					Photo photo = new Photo(filePath);
                    					photoList.add(photo);
                    					photoFloder.setPhotoList(photoList);
                    					photoFloder.setDirPath(dirPath);
                    					photoFloder.setName(dirPath.substring(dirPath.lastIndexOf(File.separator) + 1, dirPath.length()));
                    					floderMap.put(dirPath, photoFloder);
                    					floderMap.get(allPhotosKey).getPhotoList().add(photo);
                    				}
                    			}
                    		} 
                    		return false;
                    	}
                    });
//                }  
//            }
//        }
        return floderMap;
    }
    
    /**
     * 获取视频文件
     * @param list
     * @param file
     * @return
     */
    public static Map<String, PhotoFloder> getAudio(Context context) {
    	final Map<String, PhotoFloder> floderMap = new HashMap<String, PhotoFloder>();
    	final String allPhotosKey = "所有视频";
        PhotoFloder allFloder = new PhotoFloder();
        allFloder.setName(allPhotosKey);
        allFloder.setDirPath(allPhotosKey);
        allFloder.setPhotoList(new ArrayList<Photo>());
        floderMap.put(allPhotosKey, allFloder);
        File files=Environment.getExternalStorageDirectory();
//        File files=new File(Environment.getExternalStorageDirectory()+"/DCIM/Camera");
        File fileArray[] = files.listFiles();  
//        if(files != null){
//        	for (File f : fileArray){  
//                if(f.isDirectory()){  
//                    
//                }else{  `
//                    System.out.println("adfsgkjadsgjkjkadsgh"+f);  
                    files.listFiles(new FileFilter() {
                    	
                    	@Override
                    	public boolean accept(File file) {
                    		String name = file.getName();
                    		System.out.println("namename:"+name);
                    		int i = name.indexOf('.');
                    		if (i != -1) {
                    			name = name.substring(i);
                    			if (name.equalsIgnoreCase(".mp3")
                    					|| name.equalsIgnoreCase(".wav")
                    					|| name.equalsIgnoreCase(".pcm")) {
                    				
                    				//获取视频文件绝对路径
                    				String filePath=file.getAbsolutePath();
                    				// 获取该视频的父路径名
                    				File parentFile = new File(filePath).getParentFile();
                    				String dirPath = parentFile.getAbsolutePath();
                    				
                    				if (floderMap.containsKey(dirPath)) {
                    					Photo photo = new Photo(filePath);
                    					PhotoFloder photoFloder = floderMap.get(dirPath);
                    					photoFloder.getPhotoList().add(photo);
                    					floderMap.get(allPhotosKey).getPhotoList().add(photo);
                    				} else {// 初始化imageFloder
                    					PhotoFloder photoFloder = new PhotoFloder();
                    					List<Photo> photoList = new ArrayList<Photo>();
                    					Photo photo = new Photo(filePath);
                    					photoList.add(photo);
                    					photoFloder.setPhotoList(photoList);
                    					photoFloder.setDirPath(dirPath);
                    					photoFloder.setName(dirPath.substring(dirPath.lastIndexOf(File.separator) + 1, dirPath.length()));
                    					floderMap.put(dirPath, photoFloder);
                    					floderMap.get(allPhotosKey).getPhotoList().add(photo);
                    				}
                    			}
                    		} 
                    		return false;
                    	}
                    });
//                }  
//            }
//        }
        return floderMap;
    }
    
    /**
     * 获取视频文件
     * @param list
     * @param file
     * @return
     */
    public static Map<String, PhotoFloder> getVideoFile(Context context) {
    	final Map<String, PhotoFloder> floderMap = new HashMap<String, PhotoFloder>();
    	final String allPhotosKey = "所有视频";
        PhotoFloder allFloder = new PhotoFloder();
        allFloder.setName(allPhotosKey);
        allFloder.setDirPath(allPhotosKey);
        allFloder.setPhotoList(new ArrayList<Photo>());
        floderMap.put(allPhotosKey, allFloder);
        File files=new File(Environment.getExternalStorageDirectory()+"/DCIM/Camera");
//        getAllFiles(Environment.getExternalStorageDirectory());
        files.listFiles(new FileFilter() {

            @Override
            public boolean accept(File file) {
                String name = file.getName();
                System.out.println("namename:"+name);
                int i = name.indexOf('.');
                if (i != -1) {
                    name = name.substring(i);
                    if (name.equalsIgnoreCase(".mp4")
                            || name.equalsIgnoreCase(".3gp")
                            || name.equalsIgnoreCase(".wmv")
                            || name.equalsIgnoreCase(".ts")
                            || name.equalsIgnoreCase(".rmvb")
                            || name.equalsIgnoreCase(".mov")
                            || name.equalsIgnoreCase(".m4v")
                            || name.equalsIgnoreCase(".avi")
                            || name.equalsIgnoreCase(".m3u8")
                            || name.equalsIgnoreCase(".3gpp")
                            || name.equalsIgnoreCase(".3gpp2")
                            || name.equalsIgnoreCase(".mkv")
                            || name.equalsIgnoreCase(".flv")
                            || name.equalsIgnoreCase(".divx")
                            || name.equalsIgnoreCase(".f4v")
                            || name.equalsIgnoreCase(".rm")
                            || name.equalsIgnoreCase(".asf")
                            || name.equalsIgnoreCase(".ram")
                            || name.equalsIgnoreCase(".mpg")
                            || name.equalsIgnoreCase(".v8")
                            || name.equalsIgnoreCase(".swf")
                            || name.equalsIgnoreCase(".m2v")
                            || name.equalsIgnoreCase(".asx")
                            || name.equalsIgnoreCase(".ra")
                            || name.equalsIgnoreCase(".ndivx")
                            || name.equalsIgnoreCase(".xvid")) {
                    	
                    	//获取视频文件绝对路径
                    	String filePath=file.getAbsolutePath();
                    	 // 获取该视频的父路径名
                        File parentFile = new File(filePath).getParentFile();
//                        if (parentFile == null) {
//                            continue;
//                        }
                        String dirPath = parentFile.getAbsolutePath();

                        if (floderMap.containsKey(dirPath)) {
                            Photo photo = new Photo(filePath);
                            PhotoFloder photoFloder = floderMap.get(dirPath);
                            photoFloder.getPhotoList().add(photo);
                            floderMap.get(allPhotosKey).getPhotoList().add(photo);
//                            continue;
                        } else {// 初始化imageFloder
                            PhotoFloder photoFloder = new PhotoFloder();
                            List<Photo> photoList = new ArrayList<Photo>();
                            Photo photo = new Photo(filePath);
                            photoList.add(photo);
                            photoFloder.setPhotoList(photoList);
                            photoFloder.setDirPath(dirPath);
                            photoFloder.setName(dirPath.substring(dirPath.lastIndexOf(File.separator) + 1, dirPath.length()));
                            floderMap.put(dirPath, photoFloder);
                            floderMap.get(allPhotosKey).getPhotoList().add(photo);
                        }
                    }
                } 
                return false;
            }
        });

        return floderMap;
    }
    
    // 遍历接收一个文件路径，然后把文件子目录中的所有文件遍历并输出来 
//    private static void getAllFiles(File root){  
//        File files[] = root.listFiles();  
//        if(files != null){  
//            for (File f : files){  
//                if(f.isDirectory()){  
//                    getAllFiles(f);  
//                }else{  
//                    System.out.println("adfsgkjadsgjkjkadsgh"+f);  
//                }  
//            }  
//        }  
//    } 

}
