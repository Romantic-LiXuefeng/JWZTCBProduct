package com.jwzt.caibian.db;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.jwzt.caibian.bean.ChatMessageBean;
import com.jwzt.caibian.bean.DoTaskBean;
import com.jwzt.caibian.bean.LocationNoUploadBean;

/**
 * 数据库类
 * @author awq
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	//数据库名称
	private static final String DATABASE_NAME = "/mnt/sdcard/TestORM.db";
	//数据库版本
	private static final int DATABASE_VERSION = 2;
    
	public DatabaseHelper(final Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	//我们使用访问数据表的DAO对象 做任务的Dao对象
	private Dao<DoTaskBean, Integer> doTaskDao = null;
	
	//本地上传的Dao对象
	private Dao<LocationNoUploadBean, Integer> locationUploadDao = null;
	
	//本地聊天记录的Dao对象
	private Dao<ChatMessageBean, Integer> chatsDao = null;
	
	@Override
	public void onCreate(final SQLiteDatabase db, final ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, DoTaskBean.class);
			TableUtils.createTable(connectionSource, LocationNoUploadBean.class);
			TableUtils.createTable(connectionSource, ChatMessageBean.class);

		}
		catch (final SQLException e) {
		//	System.out.println("test"+"Can't create database");
			throw new RuntimeException(e);
		}
	}

	/**
	 * 当应用程序升级时，它具有较高的版本号。这允许您调整各种数据到*匹配新版本号。
	 */
	@Override
	public void onUpgrade(final SQLiteDatabase db, final ConnectionSource connectionSource, final int oldVersion, final int newVersion) {
		try {
		//	System.out.println("test"+"onUpgrade");
			TableUtils.dropTable(connectionSource, DoTaskBean.class, true);
			TableUtils.dropTable(connectionSource, LocationNoUploadBean.class, true);
			TableUtils.dropTable(connectionSource, ChatMessageBean.class, true);

			onCreate(db, connectionSource);
		}
		catch (final SQLException e) {
		//	System.out.println("test"+"Can't drop databases");
			throw new RuntimeException(e);
		}
	}
    
	/**
	 * 对外提供做任务的Dao对象
	 * @return
	 * @throws SQLException
	 */
	public Dao<DoTaskBean, Integer> getDoTaskDao() throws SQLException {
		if (this.doTaskDao == null) {
			this.doTaskDao = getDao(DoTaskBean.class);
		}
		return this.doTaskDao;
	}
	
	/**
	 * 对外提供做本地任务保存的bean对象
	 * @return
	 * @throws SQLException
	 */
	public Dao<LocationNoUploadBean, Integer> getLocalUploadDao() throws SQLException {
		if (this.locationUploadDao == null) {
			this.locationUploadDao = getDao(LocationNoUploadBean.class);
		}
		return this.locationUploadDao;
	}
	
	/**
	 * 对外提供聊天表的Dao操作对象
	 * chatsDao
	 */
	public Dao<ChatMessageBean, Integer> getChatDao() throws SQLException {
		if (this.chatsDao == null) {
			this.chatsDao = getDao(ChatMessageBean.class);
		}
		return this.chatsDao;
	}
	
	
	/**
	 * 关闭数据库连接和清除所有缓存DAOs。
	 */
	@Override
	public void close() {
		super.close();
		this.doTaskDao = null;
		this.locationUploadDao=null;
		this.chatsDao=null;
		
	}

}
