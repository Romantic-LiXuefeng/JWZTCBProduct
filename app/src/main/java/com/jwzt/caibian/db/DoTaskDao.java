package com.jwzt.caibian.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.jwzt.caibian.bean.DoTaskBean;

/**
 * 操作任务表的Dao对象类
 * @author awq
 *
 */
public class DoTaskDao {
	private Dao<DoTaskBean, Integer> doTaskDao;
	
	public DoTaskDao(final DatabaseHelper databaseHelper) {
		this.doTaskDao = getDoTaskDao(databaseHelper);
	}
    
	/**
	 * 清除所有的person的对象
	 */
	public void clearData(int  taskid) {
		final List<DoTaskBean> tasks = getTasks(taskid);
		for (final DoTaskBean person : tasks) {
			deletePerson(person);
		}
	}
    /**  
     * 查询所有Task任务素材列表.根据taskid查询
     * @return
     */
	public List<DoTaskBean> getTasks(int taskid) {
		try {
			
			return this.doTaskDao.queryForEq("taskid", taskid);
		}
		catch (final SQLException e) {
			e.printStackTrace();
		}
		return new ArrayList<DoTaskBean>();
	}
    
    /**  
     * 查询所有未上传任务素材列表
     * @return
     */
	public List<DoTaskBean> getAllNoUploadTasks(int readerStatue) {
		try {
			
			return this.doTaskDao.queryForEq("readerStatue", readerStatue);
		}
		catch (final SQLException e) {
			e.printStackTrace();
		}
		return new ArrayList<DoTaskBean>();
	}
	
	 /**  
     * 查询该任务下所有未上传的或已上传的
     * @param taskid 任务id
     * @param userid userId;
     * @param readerStatue 0已经发送  1未发送
     * @return
     */
	@SuppressWarnings("unchecked")
	public List<DoTaskBean> getTasks(int taskid,int userid,int readerStatue) {
		try {
			QueryBuilder builder = this.doTaskDao.queryBuilder();
			builder.where().eq("taskid", taskid).and().eq("userid", userid).and().eq("readerStatue", readerStatue);
			return builder.query();
		}
		catch (final SQLException e) {
			e.printStackTrace();
		}
		return new ArrayList<DoTaskBean>();
	}
	
	 /**  
     * 查询所有Task任务素材列表
     * @return
     */
	@SuppressWarnings("unchecked")
	public List<DoTaskBean> getTasks(int taskid,int userid) {
		try {
			QueryBuilder builder = this.doTaskDao.queryBuilder();
			builder.where().eq("taskid", taskid).and().eq("userid", userid);
			return builder.query();

			//return  this.doTaskDao.queryBuilder().where().eq("taskid", taskid).and().eq("taskid", taskid);
		}
		catch (final SQLException e) {
			e.printStackTrace();
		}
		return new ArrayList<DoTaskBean>();
	}
    
    /**
     * 创建或者更新App对象
     * @param app
     */
	public void saveOrUpdateTask(final DoTaskBean task) {
		try {
			this.doTaskDao.createOrUpdate(task);
		}
		catch (final SQLException e) {
			e.printStackTrace();
		}
	}
	
	
    /**
     * 删除person对象
     * @param person
     */
	public void deletePerson(final DoTaskBean task) {
		try {
			this.doTaskDao.delete(task);
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
	private Dao<DoTaskBean, Integer> getDoTaskDao(final DatabaseHelper databaseHelper) {
		if (null == this.doTaskDao) {
			try {
				this.doTaskDao = databaseHelper.getDoTaskDao();
			}
			catch (final SQLException e) {
				e.printStackTrace();
			}
		}
		return this.doTaskDao;
	}
	
}
