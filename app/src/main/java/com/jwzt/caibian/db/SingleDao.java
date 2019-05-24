package com.jwzt.caibian.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 录制的视频只有一段的视频路径数据库表的操作类
 * @author howie
 *
 */
public class SingleDao {
	private SQLHelper helper;
	public SingleDao(Context context){
		helper=SQLHelper.getInstance(context);
	}
	public void add(String path){
		SQLiteDatabase db = helper.getReadableDatabase();
		db.execSQL("insert into singles  (path) values (?)",new Object[]{path});
		db.close();
	}
	/**
	 * 根据路径判断是否是通过app录制的单段视频
	 * @param path
	 * @return
	 */
	public boolean isExist(String path){
		boolean exist=false;
		SQLiteDatabase db = helper.getReadableDatabase();
//		db.execSQL("select * from programmes where name =?",new Object[]{bean.getName()});
		Cursor cursor = db.rawQuery("select * from singles where path =?", new String[]{path});
		while(cursor.moveToNext()){
			exist=true;
			
			break;
		}
		db.close();
		return exist;
		
	}

}
