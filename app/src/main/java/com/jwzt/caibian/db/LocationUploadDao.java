package com.jwzt.caibian.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.jwzt.caibian.bean.DoTaskBean;
import com.jwzt.caibian.bean.LocationNoUploadBean;

/**
 * 操作本地保存的未上传任务的Dao对象
 * @author awq
 *
 */
public class LocationUploadDao {
	private Dao<LocationNoUploadBean, Integer> locationUploadDao;
	
	public LocationUploadDao(final DatabaseHelper databaseHelper) {
		this.locationUploadDao = getLocationUpload(databaseHelper);
	}
    
	/**
	 * 清除所有的person的对象
	 */
	public void clearData(int  userid) {
		final List<LocationNoUploadBean> tasks = getTasks(userid);
		for (final LocationNoUploadBean person : tasks) {
			deletePerson(person);
		}
	}
    /**  
     * 查询所有Task任务素材列表.根据taskid查询
     * @return
     */
	public List<LocationNoUploadBean> getTasks(int userid) {
		try {
			
			return this.locationUploadDao.queryForEq("userId", userid);
		}
		catch (final SQLException e) {
			e.printStackTrace();
		}
		return new ArrayList<LocationNoUploadBean>();
	}
    
	 /**  
     * 查询所有Task任务素材列表
     * @return
     */
	@SuppressWarnings("unchecked")
	public List<LocationNoUploadBean> getTasks(int taskid,int userid) {
		try {
			QueryBuilder builder = this.locationUploadDao.queryBuilder();
			builder.where().eq("taskid", taskid).and().eq("userid", userid);
			return builder.query();

			//return  this.doTaskDao.queryBuilder().where().eq("taskid", taskid).and().eq("taskid", taskid);
		}
		catch (final SQLException e) {
			e.printStackTrace();
		}
		return new ArrayList<LocationNoUploadBean>();
	}
    
    /**
     * 创建或者更新App对象
     * @param app
     */
	public void saveOrUpdateTask(final LocationNoUploadBean task) {
		try {
			this.locationUploadDao.createOrUpdate(task);
		}
		catch (final SQLException e) {
			e.printStackTrace();
		}
	}
	
	
    /**
     * 删除person对象
     * @param person
     */
	public void deletePerson(final LocationNoUploadBean task) {
		try {
			this.locationUploadDao.delete(task);
		}
		catch (final SQLException e) {
			e.printStackTrace();
		}
	}
   
	/**
	 * 任务表的操作Dao对象
	 * @param databaseHelper
	 * @return
	 */
	private Dao<LocationNoUploadBean, Integer> getLocationUpload(final DatabaseHelper databaseHelper) {
		if (null == this.locationUploadDao) {
			try {
				this.locationUploadDao = databaseHelper.getLocalUploadDao();
			}
			catch (final SQLException e) {
				e.printStackTrace();
			}
		}
		return this.locationUploadDao;
	}
	
}
