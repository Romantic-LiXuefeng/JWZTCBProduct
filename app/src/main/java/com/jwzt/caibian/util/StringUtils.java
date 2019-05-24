package com.jwzt.caibian.util;

/**
 * 字符串处理类
 * @author afnasdf
 *
 */
public class StringUtils {

	
	
	
	/**
	 * 根据文件的地址，截取文件的名称
	 */
	public static String getFileName(String path){
		return path.substring(path.lastIndexOf("/"));
		
	}
	
	
}
