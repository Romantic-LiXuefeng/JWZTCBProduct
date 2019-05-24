package com.jwzt.caibian.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.jwzt.caibian.bean.ChatMessageBean;
import com.jwzt.caibian.bean.DoTaskBean;

/**
 * 操作任务表的Dao对象类
 * @author awq
 */
public class ChatsDao {
	private Dao<ChatMessageBean, Integer> chatsDao;
	
	public ChatsDao(final DatabaseHelper databaseHelper) {
		this.chatsDao = getChatsDao(databaseHelper);
	}
    
	/**
	 * 清除所有的person的对象
	 */
	public void clearData(int  taskid) {
		final List<ChatMessageBean> tasks = getTasks(taskid);
		for (final ChatMessageBean person : tasks) {
			deletePerson(person);
		}
	}
    /**  
     * 查询所有Task任务素材列表.根据taskid查询
     * @return
     */
	public List<ChatMessageBean> getTasks(int taskid) {
		try {
			
			return this.chatsDao.queryForEq("taskid", taskid);
		}
		catch (final SQLException e) {
			e.printStackTrace();
		}
		return new ArrayList<ChatMessageBean>();
	}
	
    /**  
     * 查询所有Task任务素材列表.根据taskid查询
     * @return
     */
	public List<ChatMessageBean> getAllUserTasks(int userId) {
		try {
			
			return this.chatsDao.queryForEq("userid", userId);
		}
		catch (final SQLException e) {
			e.printStackTrace();
		}
		return new ArrayList<ChatMessageBean>();
	}
    
    /**  
     * 查询所有未上传任务素材列表
     * @return
     */
	public List<ChatMessageBean> getAllNoUploadTasks(int readerStatue) {
		try {
			
			return this.chatsDao.queryForEq("readerStatue", readerStatue);
		}
		catch (final SQLException e) {
			e.printStackTrace();
		}
		return new ArrayList<ChatMessageBean>();
	}
	
	 /**  
     * 查询所有Task任务素材列表
     * @return
     */
	@SuppressWarnings("unchecked")
	public List<ChatMessageBean> getTasks(int groupId,int userid) {
		try {
			QueryBuilder builder = this.chatsDao.queryBuilder();
			builder.where().eq("groupId", groupId).and().eq("userid", userid);
			return builder.query();

			//return  this.doTaskDao.queryBuilder().where().eq("taskid", taskid).and().eq("taskid", taskid);
		}
		catch (final SQLException e) {
			e.printStackTrace();
		}
		return new ArrayList<ChatMessageBean>();
	}
	
	/**
	 * 查询数据库中所有未读消息
	 * @param groupId
	 * @param userid
	 * @return
	 */
	public List<ChatMessageBean> getAllNoreadMessage(int userid,int isRead) {
		try {
			QueryBuilder builder = this.chatsDao.queryBuilder();
			builder.where().eq("userid", userid).and().eq("isRead", isRead);
			return builder.query();

			//return  this.doTaskDao.queryBuilder().where().eq("taskid", taskid).and().eq("taskid", taskid);
		}
		catch (final SQLException e) {
			e.printStackTrace();
		}
		return new ArrayList<ChatMessageBean>();
	}
	
	/**
	 * 查询数据库中该群组所有未读消息
	 * @param groupId
	 * @param userid
	 * @param isRead 0表示已读 1表示未读
	 * @return
	 */
	public List<ChatMessageBean> getNoreadMessage(int groupId,int userid,int isRead) {
		try {
			QueryBuilder builder = this.chatsDao.queryBuilder();
			builder.where().eq("groupId", groupId).and().eq("userid", userid).and().eq("isRead", isRead);
			return builder.query();
			//return  this.doTaskDao.queryBuilder().where().eq("taskid", taskid).and().eq("taskid", taskid);
		}
		catch (final SQLException e) {
			e.printStackTrace();
		}
		return new ArrayList<ChatMessageBean>();
	}
    
    /**
     * 创建或者更新App对象
     * @param app
     */
	public void saveOrUpdateTask(final ChatMessageBean task) {
		try {
			this.chatsDao.createOrUpdate(task);
		}
		catch (final SQLException e) {
			e.printStackTrace();
		}
	}
	
	
    /**
     * 删除person对象
     * @param person
     */
	public void deletePerson(final ChatMessageBean task) {
		try {
			this.chatsDao.delete(task);
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
	private Dao<ChatMessageBean, Integer> getChatsDao(final DatabaseHelper databaseHelper) {
		if (null == this.chatsDao) {
			try {
				this.chatsDao = databaseHelper.getChatDao();
			}
			catch (final SQLException e) {
				e.printStackTrace();
			}
		}
		return this.chatsDao;
	}
	
}
