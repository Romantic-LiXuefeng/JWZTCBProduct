package com.jwzt.caibian.util;

import java.io.File;

import android.os.Environment;
import android.os.StatFs;
////////////////这是一个获取sd卡剩余空间 和总空间大小的类//////////////////////////
public class SdcardSize {
	//得到外部储存sdcard的状态
    private String sdcard=Environment.getExternalStorageState();
    //外部储存sdcard存在的情况
    private String state=Environment.MEDIA_MOUNTED;
	private StatFs statFs;
    //获取Sdcard的路径
    /**
     * SDCard 总容量大小 
     * @return MB
     */
    public long getTotalSize()
    {
    	//获得路径
        File file=Environment.getExternalStorageDirectory();
         statFs=new StatFs(file.getPath());
        if(sdcard.equals(state))
        {
            //获得sdcard上 block的总数
            long blockCount=statFs.getBlockCount();
            //获得sdcard上每个block 的大小
            long blockSize=statFs.getBlockSize();
            //计算标准大小使用：1024，当然使用1000也可以
            long bookTotalSize=blockCount*blockSize/1000/1000;
            return bookTotalSize;
            
        }else
        {
            return -1;
        }
        
    }
    
    
    /**
     * 计算Sdcard的剩余大小
     * @return MB
     */
    public long getAvailableSize()
    {
        if(sdcard.equals(state))
        {
            //获得Sdcard上每个block的size
            long blockSize=statFs.getBlockSize();
            //获取可供程序使用的Block数量
            long blockavailable=statFs.getAvailableBlocks();
            //计算标准大小使用：1024，当然使用1000也可以
            long blockavailableTotal=blockSize*blockavailable/1000/1000;
            return blockavailableTotal;
        }else
        {
            return -1;
        }
    }
  
}
