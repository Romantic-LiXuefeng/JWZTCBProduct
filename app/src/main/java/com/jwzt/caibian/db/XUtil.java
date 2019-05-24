package com.jwzt.caibian.db;

import java.io.File;

import org.xutils.DbManager;
import org.xutils.DbManager.DaoConfig;
import org.xutils.DbManager.DbUpgradeListener;

import android.os.Environment;

/**
 * 数据库管理
 * @author afnasdf
 *
 */
public class XUtil {
	
	static DaoConfig daoConfig;
    public static DaoConfig getDaoConfig(){
        File file=new File(Environment.getExternalStorageDirectory().getPath());
        if(daoConfig==null){
            daoConfig=new DaoConfig()
            .setDbName("jwzt_procaibian.db")
            .setDbDir(file)
            .setDbVersion(1)
            .setAllowTransaction(true)
            .setDbUpgradeListener(new DbUpgradeListener() {
                @Override
                public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                	//数据库的升级
                	
                }
            });
        }
        return daoConfig;
    }
	
	

}
