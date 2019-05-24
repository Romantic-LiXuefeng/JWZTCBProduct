package com.jwzt.caibian.util;


/**      
 * <p>project_name：contacts</p> 
 * <p>Description: </p> 
 * <p>Copyright: Copyright (c) 2011 by tl3shi.</p> 
 **/


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.jwzt.caibian.application.Configs;
import com.jwzt.caibian.bean.DoTaskBean;
import com.jwzt.caibian.bean.ManuscriptBean;
import com.jwzt.caibian.db.DoTaskDao;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

/**
 * <p>
 * Title: FileUtil.java
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author <a href="mailto:tanglei3shi@163.com">Administrator</a>
 * @date 2011-3-3 下午03:28:05
 * @version 1.0
 */
public class FileUtil {
	private String SDPATH;
	private int FILESIZE = 4 * 1024;
	private static String DST_FOLDER_NAME = "JCamera";
	public String getSDPATH() {
		return SDPATH;
	}
	private static String storagePath = "";
	private static final File parentPath = Environment.getExternalStorageDirectory();
	public FileUtil() {
		// 得到当前外部存储设备的目录( /SDCARD )
		SDPATH = Environment.getExternalStorageDirectory() + "/";
	}
	private static String initPath() {
		if (storagePath.equals("")) {
			storagePath = parentPath.getAbsolutePath() + File.separator + "haodiaoyu" + File.separator + DST_FOLDER_NAME;
			File f = new File(storagePath);
			if (!f.exists()) {
				f.mkdir();
			}
		}
		return storagePath;
	}
	public static String saveBitmap(String dir, Bitmap b) {
		DST_FOLDER_NAME = dir;
		String path = initPath();
		long dataTake = System.currentTimeMillis();
		String jpegName = path + File.separator + "picture_" + dataTake + ".jpg";
		try {
			FileOutputStream fout = new FileOutputStream(jpegName);
			BufferedOutputStream bos = new BufferedOutputStream(fout);
			b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
			return jpegName;
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}
	/**
	 * 在SD卡上创建文件
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public File createSDFile(String fileName) throws IOException {
		File file = new File(SDPATH + fileName);
		file.createNewFile();
		return file;
	}

	/**
	 * 在SD卡上创建目录
	 * 
	 * @param dirName
	 * @return
	 */
	public File createSDDir(String dirName) {
		File dir = new File(SDPATH + dirName);
		dir.mkdir();
		return dir;
	}

	/**
	 * 判断SD卡上的文件夹是否存在
	 * 
	 * @param fileName
	 * @return
	 */
	public boolean isFileExist(String fileName) {
		File file = new File(SDPATH + fileName);
		return file.exists();
	}

	/**
	 * 将一个InputStream里面的数据写入到SD卡中
	 * 
	 * @param path
	 * @param fileName
	 * @param input
	 * @return
	 */
	public File write2SDFromInput(String path, String fileName,InputStream input) {
		File file = null;
		OutputStream output = null;
		try {
			createSDDir(path);
			file = createSDFile(path + fileName);
			output = new FileOutputStream(file);
			byte[] buffer = new byte[FILESIZE];
			while ((input.read(buffer)) != -1) {
				output.write(buffer);
			}
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != output) {
					output.close();
				}
				if (null != input) {
					input.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	/**
	 * 读取源文件内容
	 * 
	 * @param filename
	 *            String 文件路径
	 * @throws IOException
	 * @return byte[] 文件内容
	 */
	public static byte[] readFile(String filename) {
		File file = new File(filename);
		long len = file.length();
		Log.i("userinfo", "file.length():" + len);
		byte[] bytes = new byte[(int) len];

		BufferedInputStream bufferedInputStream;
		try {
			bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
			int r = bufferedInputStream.read(bytes);
			if (r != len) {
				bufferedInputStream.close();
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytes;
	}

	/**
	 * 只适用于.zip 压缩制定文件/文件夹 ，压缩后的文件类型是zip；可以压缩中文内容的文本文件； 将文件或文件夹压缩成.zip文件的方法
	 * 
	 * @param zipBeforeFilePath
	 *            压缩前的文件路径，文件或文件夹，如：D:\或D:\win或D:\a.txt
	 * @param zipAfterFileName
	 *            压缩后的文件路径及名称，如：D:\yasuo.zip,如果没有给定路径名 则用当前时间作为压缩文件名
	 * @return 布尔类型值
	 */
	public boolean toZip(String zipBeforeFilePath, String zipAfterFileName) {
		// String zipFileName=zipAfterFileName;//打包后文件名字
		try {
			File inputFile = new File(zipBeforeFilePath);
			// 检查给定的压缩后的文件路径中是否有文件名，如果没有则取当前系统日期做为文件名
			if (zipAfterFileName.indexOf(".zip") == -1) {
				Date da = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				String zipName = sdf.format(da) + ".zip";
				// 截取路径中最后一个字符
				String lastChar = zipAfterFileName.substring(zipAfterFileName.length() - 1,zipAfterFileName.length());
				if (lastChar.indexOf("/") > -1 || lastChar.indexOf("\\") > -1) {
					zipAfterFileName = zipAfterFileName + zipName;
				} else {
					zipAfterFileName = zipAfterFileName + File.separator+ zipName;
				}
			}

			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipAfterFileName));
			// 判断一下要压缩的是文件还是文件夹
			if (inputFile.isDirectory()) {
				zip(out, inputFile, "");
			} else {
				zip(out, inputFile, inputFile.getName());
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void zip(ZipOutputStream out, File f, String base) throws Exception {
		if (f.isDirectory()) {
			File[] fl = f.listFiles();
			out.putNextEntry(new ZipEntry(base + "/"));
			base = base.length() == 0 ? "" : base + "/";
			for (int i = 0; i < fl.length; i++) {
				zip(out, fl[i], base + fl[i].getName());
			}
		} else {
			out.putNextEntry(new ZipEntry(base));
			FileInputStream in = new FileInputStream(f);
			int b;
			while ((b = in.read()) != -1) {
				out.write(b);
			}
			in.close();
		}
	}

	/**
	 * 通过 文件路径以及，文件名称 与 媒资集合，生成 rar 文件
	 * 
	 * @param filename
	 * @param xmlPath
	 * @param list
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static File packageFile(String filename,  List list) {
	//	System.out.println("filename:  " + filename  +  "  list: " + list);
		// 进行 打包操作
		List<File> allFile = new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			String path = (String) list.get(i);
            if(path!=null&&!path.equals("")){
            	path=path.replaceAll("file:///", "");
			File lisyFile = new File(path);
			//System.out.println(list.get(i) + ",name=" + lisyFile.getName()+ ",存在=" + lisyFile.exists());
			if (lisyFile.exists()) {
				allFile.add(lisyFile);
			} else {
			//	System.out.println("# " + list.get(i) + "文件，不存在。 已删除或者路径已经更改。");
			}
            }
		}
		String zipfile = filename + ".zip";
		ZipFiles(allFile, new File(zipfile));
		File zipFile = new File(zipfile);

		return zipFile;
	}
	/**
	 * 压缩 rar文件
	 * 
	 * @param srcfile
	 *            File[] 需要压缩的文件列表
	 * @param zipfile
	 *            File 压缩后的文件
	 */
	public static void ZipFiles(List<File> allFile, File zipfile) {
		byte[] buf = new byte[50 * 1024];
		try {
			// 创建 压缩文件
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));
			for (int i = 0; i < allFile.size(); i++) {
				FileInputStream in = new FileInputStream(allFile.get(i));
				out.putNextEntry(new ZipEntry(allFile.get(i).getName()));
				// out.putNextEntry(new ZipEntry(filename));
				// 向压缩包中添加文件
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				out.closeEntry();
				in.close();
			}
			out.close();
			System.out.println("压缩完成.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 复制文件
	 * 
	 * @param oldPath
	 *            原文件路径
	 * @param newPath
	 *            新文件路径
	 */
	public static void copyFile(String oldPath, String newPath) {
		try {
			//System.out.println("===============进行复制操作的文件路径为===============");
		//	System.out.println("oldPath:" + oldPath);
		//	System.out.println("newPath:" + newPath);
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) {
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1024];
				while ((byteread = inStream.read(buffer)) != -1) {
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				fs.close();
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param filePath
	 *            文件路径
	 */
	public static void delFile1(String filePath) {
		try {
			File myDelFile = new File(filePath);
			myDelFile.delete();
		} catch (Exception e) {
			System.out.println("删除文件操作出错");
			e.printStackTrace();
		}
	}
	
	public static String getFolderPath() {
		String path = Environment.getExternalStorageDirectory()+ "/caibian/Photo/";
		File filePath = new File(path);
		if (!filePath.exists()) {
			filePath.mkdirs();
		}
		return filePath.getPath();
	}
	
	
	/**
     * 读取图片属性：旋转的角度
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree  = 0;
        try {
                ExifInterface exifInterface = new ExifInterface(path);
                int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
        } catch (IOException e) {
                e.printStackTrace();
        }
        return degree;
    }
   /*
    * 旋转图片 
    * @param angle 
    * @param bitmap 
    * @return Bitmap 
    */  
   public static Bitmap rotaingImageView(int angle , Bitmap bitmap) {  
       //旋转图片 动作   
       Matrix matrix = new Matrix();;  
       matrix.postRotate(angle);  
      // System.out.println("angle2=" + angle);
       // 创建新的图片   
       Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(), bitmap.getHeight(), matrix, true);  
       return resizedBitmap;  
   }
   
   
	public static final String CACHE = "cache";
	public static final String ROOT = "XinXuanWei";
	
	
	/**
	 * 获取文件名
	 * @param pathandname
	 * @return
	 */
	public static String getFileName(String pathandname){  
		int start=pathandname.lastIndexOf("/");  
//	       int end=pathandname.lastIndexOf(".");  
	       if(start!=-1){  
	           return pathandname.substring(start+1,pathandname.length());    
	       }else{  
	           return null;  
	       }  
         
   } 
	
	
	/**
	 * 获取某文件夹下的视频资源
	 * @return
	 */
	public static List<String> getVideoPathFromSD(String path) {
		 List<String> picList = new ArrayList<String>();
		  File mfile = new File(path);
		  File[] files = mfile.listFiles();
		 for (int i = 0; i < files.length; i++) {
		  File file = files[i];
		   picList.add(file.getPath());
		 }
		 return picList;
		}
	
	
	public static  void saveLocal(String json, String url,File cacheDir) {
		
		BufferedWriter bw = null;
		try {
			File dir=getCacheDir(cacheDir);
			if(!dir.exists()){
				dir.mkdirs();
			}
			//在第一行写一个过期时间 
			File file = new File(dir, url); // /mnt/sdcard/googlePlay/cache/home_0
			if(!file.exists()){
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file);
			 bw = new BufferedWriter(fw);
//			bw.write(System.currentTimeMillis() + 1000 * 100 + "");
//			bw.newLine();// 换行
			bw.write(json);// 把整个json文件保存起来
			bw.flush();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static  String loadLocal(String url,File cacheDir) {
		//  如果发现文件已经过期了 就不要再去复用缓存了
		File dir=getCacheDir(cacheDir);// 获取缓存所在的文件夹
		File file = new File(dir, url); 
		try {
			FileReader fr=new FileReader(file);
			BufferedReader br=new BufferedReader(fr);
//			long outOfDate = Long.parseLong(br.readLine());
//			if(System.currentTimeMillis()>outOfDate){
//				return null;
//			}else{
				String str=null;
				StringWriter sw=new StringWriter();
				while((str=br.readLine())!=null){
				
					sw.write(str);
				}
				return sw.toString();
//			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public static  String loadLocalDraft(String url) {
		//  如果发现文件已经过期了 就不要再去复用缓存了
		
		File file = new File( url); 
		try {
			FileReader fr=new FileReader(file);
			BufferedReader br=new BufferedReader(fr);
//			long outOfDate = Long.parseLong(br.readLine());
//			if(System.currentTimeMillis()>outOfDate){
//				return null;
//			}else{
				String str=null;
				StringWriter sw=new StringWriter();
				while((str=br.readLine())!=null){
				
					sw.write(str);
				}
				return sw.toString();
//			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static boolean isSDAvailable() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * 获取一个文件的最后的修改日期
	 * @param filePath
	 */
	  public static String getModifiedTime(String filePath){  
	        File f = new File(filePath);              
	        Calendar cal = Calendar.getInstance();

	        long time = f.lastModified();  
	        cal.setTimeInMillis(time);

	        //此处toLocalString()方法是不推荐的，但是仍可输出  
//	        System.out.println("修改时间[1] " + cal.getTime().toLocaleString());   
	        //输出：修改时间[1]    2009-8-17 10:32:38
		  cal.get(Calendar.HOUR_OF_DAY);
		 // System.out.println("===="+);
		  SimpleDateFormat sdf = new SimpleDateFormat();
		  sdf.applyPattern("yyyy-MM-dd HH:mm:ss"); //设置显示时间格式
		  String formatDatesd = sdf.format(cal.getTime());

		  return formatDatesd;
	 }
    public static Date stringToDate(String dateString) {
        ParsePosition position = new ParsePosition(0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date dateValue = simpleDateFormat.parse(getModifiedTime(dateString), position);
        return dateValue;
    }
	  
	  /**
		 * 获取缓存路径
		 * @return
		 */
		public static File getCacheDir(File cacheDir) {
			return getDir(CACHE, cacheDir);
		}
		public static File getDir(String cache,File cacheDir) {
			StringBuilder path = new StringBuilder();
			if (isSDAvailable()) {
				path.append(Environment.getExternalStorageDirectory().getAbsolutePath());
				/*path.append(File.separator);// '/'
				path.append(ROOT);// /mnt/sdcard/GooglePlay
				path.append(File.separator);*/
				path.append(Configs.CAOGAO_PATH);
//				path.append(cache);// /mnt/sdcard/GooglePlay/cache
				
			}else{
//				File filesDir = UiUtils.getContext().getCacheDir();    //  cache  getFileDir file
				path.append(cacheDir.getAbsolutePath());// /data/data/com.itheima.googleplay/cache
				path.append(File.separator);///data/data/com.itheima.googleplay/cache/
				path.append(cache);///data/data/com.itheima.googleplay/cache/cache
				
			}
			File file = new File(path.toString());
			if (!file.exists() || !file.isDirectory()) {
				file.mkdirs();// 创建文件夹
			}
			return file;

		}
		
		 public static boolean checkFileExist(final String path){
		        if(TextUtils.isEmpty(path))
		            return false;

		        File file = new File(path);
		        return file.exists();
		    }
		 
		 
		 /**
		  * 文稿上传拼装XML
		  * @param title 标题
		  * @param userId 用户id
		  * @param categoryId 类型
		  * @param longitude 经度
		  * @param latitude 纬度
		  * @param address 地址（名称）
		  * @param content 正文内容
		  * @param zylb 资源列表
		  * @return
		  */
		 public static String assemblyXML(String title,String userId,String categoryId,double longitude,double latitude,String address,String content,List<ManuscriptBean> zylb){
			 String path="";
			 StringBuffer sb=new StringBuffer();
				try {
					if(IsNonEmptyUtils.isList(zylb)){
						for(int i=0;i<zylb.size();i++){
		 					String resource="<resource>"
		 							+"<name><![CDATA["+zylb.get(i).getName()+"]]></name>"
		 							+"<path><![CDATA["+zylb.get(i).getName()+"]]></path>"
		 							+"<type>"+zylb.get(i).getType()+"</type>"
		 							+"</resource>";
		 					sb.append(resource);
		 				}
					}
				 
				 //封装的xml内容
				 String str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" 
						 	+ "<root>"
						 		+"<title>"+title+"</title>"
						 		+"<userId>" +userId+ "</userId>"
						 		+"<categoryId>"+categoryId+"</categoryId>"
						 		+"<longitude>"+longitude+"</longitude>"
						 		+"<latitude>"+latitude+"</latitude>"
						 		+"<address>"+address+"</address>"
						 		+"<content><![CDATA["+content+"]]></content>"
						 		+"<resources>"
						 			+sb.toString()
						 		+"</resources>"
						 		+"</root>";
				 	String filepath=Environment.getExternalStorageDirectory()+"/CAIBIAN/WGuploading/";
				 	File path1 = new File(filepath);
				 	if(!path1.exists()){
				 		path1.mkdirs(); // 创建目录 之后再创建文件
				 	}
					// 获取当前时间
					String dateStr = System.currentTimeMillis()+"";
					// 文件名称 加 当前存储的日期时间
					String fileName = "";
					fileName =dateStr + ".xml";
					File file = new File(path1, fileName);
					// 如果文件存在
					FileOutputStream  fos = new FileOutputStream(file);
					fos.write(str.getBytes());
					fos.close();
					path = filepath + fileName;
					System.out.println("#######################  结束____添加草稿    #########################################"+path);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			 return path;
		 }
		 
		 /**
		  * 任务XML信息拼装
		  * @param dotask 任务数据库对象 如果时点击发送按钮则需要将数据库中未上传的全部上传，然后需要将这些为上传的数据在数据库中更新为已上传的
		  * @param taskId 任务id
		  * @param uploadtime 上传时间
		  * @param userId 用户id
		  * @param uuid 唯一标识
		  * @param recordtime 创建时间
		  * @param content 内容
		  * @param type 类型
		  * @param longitude 经度
		  * @param latitude 纬度
		  * @param zylb 资源路径
		  * @param deleted 是否删除标记
		  * @return
		  */
		 public static String taskassemblyXML(DoTaskDao dotask,String taskId,String uploadtime,String userId,String uuid,String recordtime,String content,int type,double longitude,double latitude,List<String> zylb,String deleted,List<DoTaskBean> mList){
			 String path="";
			 String dataString="";
			 if(IsNonEmptyUtils.isList(mList)){
					try {
						StringBuffer sb1=new StringBuffer();
						for(int j=0;j<mList.size();j++){
							StringBuffer sb=new StringBuffer();
							DoTaskBean doTaskBean=mList.get(j);//取出集合中的bean对象
							doTaskBean.setReaderStatue(0);//将该bean对象设置为已上传的状态
							dotask.saveOrUpdateTask(doTaskBean);
							
							String imgpath=doTaskBean.getThumbImages();
							if(IsNonEmptyUtils.isString(imgpath)){
								String[] imgpatharray=imgpath.split(",");
								if(imgpatharray!=null&&imgpatharray.length>0){
									for(int i=0;i<imgpatharray.length;i++){
										String fileName=FileUtil.getFileName(imgpatharray[i]);
					 					String resource="<resource>"
					 							+"<name><![CDATA["+fileName+"]]></name>"
					 							+"<path><![CDATA["+fileName+"]]></path>"
					 							+"</resource>";
					 					sb.append(resource);
					 				}
								}
							}
							
							
							dataString="<data>"
									+"<id>"+doTaskBean.getSucaiId()+"</id>"
									+"<recordtime>"+doTaskBean.getSendTime()+"</recordtime>"
									+"<content><![CDATA["+doTaskBean.getSucaiContent()+"]]></content>"
									+"<type>"+doTaskBean.getSucaiType()+"</type>"
									+"<longitude>"+doTaskBean.getLongitude()+"</longitude>"
									+"<latitude>"+doTaskBean.getLatitude()+"</latitude>"
									+"<resources>"
											+sb.toString()
									+"</resources>"
									+"<deleted>" +deleted+ "</deleted>"
									+"</data>";
							
							sb1.append(dataString);
						}
						
					 
					 //封装的xml内容
					 String str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" 
							 	+ "<root>"
							 		+"<id>"+taskId+"</id>"
							 		+"<uploadtime>"+uploadtime+"</uploadtime>"
							 		+"<userId>" +userId+ "</userId>"
							 		+"<datas>" 
							 			+sb1.toString()
							 		+"</datas>"
							 		+"</root>";
					 	String filepath=Environment.getExternalStorageDirectory()+"/CAIBIAN/Taskuploading/";
					 	File path1 = new File(filepath);
					 	if(!path1.exists()){
					 		path1.mkdirs(); // 创建目录 之后再创建文件
					 	}
						// 获取当前时间
						String dateStr = System.currentTimeMillis()+"";
						// 文件名称 加 当前存储的日期时间
						String fileName = "";
						fileName =dateStr + ".xml";
						File file = new File(path1, fileName);
						// 如果文件存在
						FileOutputStream  fos = new FileOutputStream(file);
						fos.write(str.getBytes());
						fos.close();
						path = filepath + fileName;
						System.out.println("#######################  结束____添加草稿    #########################################"+path);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
			 }else{
				 StringBuffer sb=new StringBuffer();
					try {
						if(IsNonEmptyUtils.isList(zylb)){
							for(int i=0;i<zylb.size();i++){
								String fileName=FileUtil.getFileName(zylb.get(i));
			 					String resource="<resource>"
			 							+"<name><![CDATA["+fileName+"]]></name>"
			 							+"<path><![CDATA["+fileName+"]]></path>"
			 							+"</resource>";
			 					sb.append(resource);
			 				}
						}
					 
					 //封装的xml内容
					 String str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" 
							 	+ "<root>"
							 		+"<id>"+taskId+"</id>"
							 		+"<uploadtime>"+uploadtime+"</uploadtime>"
							 		+"<userId>" +userId+ "</userId>"
							 		+"<datas>" 
							 			+"<data>"
							 				+"<id>"+uuid+"</id>"
							 				+"<recordtime>"+recordtime+"</recordtime>"
							 				+"<content><![CDATA["+content+"]]></content>"
							 				+"<type>"+type+"</type>"
							 				+"<longitude>"+longitude+"</longitude>"
							 				+"<latitude>"+latitude+"</latitude>"
							 				+"<resources>"
							 					+sb.toString()
							 				+"</resources>"
							 				+"<deleted>" +deleted+ "</deleted>"
							 			+"</data>"
							 		+"</datas>"
							 		+"</root>";
					 	String filepath=Environment.getExternalStorageDirectory()+"/CAIBIAN/Taskuploading/";
					 	File path1 = new File(filepath);
					 	if(!path1.exists()){
					 		path1.mkdirs(); // 创建目录 之后再创建文件
					 	}
						// 获取当前时间
						String dateStr = System.currentTimeMillis()+"";
						// 文件名称 加 当前存储的日期时间
						String fileName = "";
						fileName =dateStr + ".xml";
						File file = new File(path1, fileName);
						// 如果文件存在
						FileOutputStream  fos = new FileOutputStream(file);
						fos.write(str.getBytes());
						fos.close();
						path = filepath + fileName;
						System.out.println("#######################  结束____添加草稿    #########################################"+path);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
			 }
			 
			 return path;
		 }
		 
			/**
			 * 将文件大小转换成常用单位
			 * 
			 * @param fileS
			 * @return
			 */
			public static String FormetFileSize(long size) {// 转换文件大小
				DecimalFormat df = new DecimalFormat("#.00");
				String fileSizeString = "";
				if (size < 1024) {
					fileSizeString = df.format((double) size) + "B";
				} else if (size < 1048576) {
					fileSizeString = df.format((double) size / 1024) + "KB";
				} else if (size < 1073741824) {
					fileSizeString = df.format((double) size / 1048576) + "MB";
				} else {
					fileSizeString = df.format((double) size / 1073741824) + "GB";
				}
				return fileSizeString;
			}
			
		    public static void deleteFile(File file) {
		        if(file == null || !file.exists()) {
		            return;
		        }
		        if(file.isFile()) {
		            final File to = new File( file.getAbsolutePath() + System.currentTimeMillis());
		            file.renameTo( to);
		            to.delete();
		        }
		        else {
		            File[] files = file.listFiles();
		            if(files != null && files.length > 0) {
		                for(File innerFile: files) {
		                    deleteFile( innerFile);
		                }
		            }
		            final File to = new File( file.getAbsolutePath() + System.currentTimeMillis());
		            file.renameTo( to);
		            to.delete();
		        }
		    }
			
			//录音模块相关变量及方法的定义开始
			public static final String ROOT_PATH = "wxr/";
		    public static final String RECORD_DIR = "record/";
		    public static final String RECORD_PATH = ROOT_PATH + RECORD_DIR;
		    //获取文件存放根路径
		    public static File getAppDir(Context context) {
		        String dirPath = "";
		        //SD卡是否存在
		        boolean isSdCardExists = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
		        boolean isRootDirExists = Environment.getExternalStorageDirectory().exists();
		        if (isSdCardExists && isRootDirExists) {
		            dirPath = String.format("%s/%s/", Environment.getExternalStorageDirectory().getAbsolutePath(), ROOT_PATH);
		        } else {
		            dirPath = String.format("%s/%s/", context.getApplicationContext().getFilesDir().getAbsolutePath(), ROOT_PATH);
		        }
		        File appDir = new File(dirPath);
		        if (!appDir.exists()) {
		            appDir.mkdirs();
		        }
		        return appDir;
		    }
		    //获取录音存放路径
		    public static File getAppRecordDir(Context context) {
		        File appDir = getAppDir(context);
		        File recordDir = new File(appDir.getAbsolutePath(), RECORD_DIR);
		        if (!recordDir.exists()) {
		            recordDir.mkdir();
		        }
		        return recordDir;
		    }
		  //录音模块相关变量及方法的定义结束
}
