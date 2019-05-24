package com.jwzt.caibian.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.rd.lib.utils.BitmapUtils;
import com.rd.lib.utils.ThreadPoolUtils;
import com.rd.vecore.VirtualVideo;
import com.rd.vecore.exception.InvalidArgumentException;
import com.rd.vecore.models.MediaObject;
import com.rd.vecore.models.Scene;
import com.rd.vecore.models.VisualFilterConfig;

import java.io.File;
import java.io.FilenameFilter;

/**
 * 生成lookup的图标
 */
public class TestLookup {

    private String TAG = "TestLookUp";
    private Context mContext;

    private String mBaseMedia;

    /**
     * 演示：如何生成lookup图标
     *
     * @param context
     * @param baseMedia
     */
    public TestLookup(Context context, String baseMedia) {
        this.mBaseMedia = baseMedia;
        mContext = context;

        File fSrc = new File(Environment.getExternalStorageDirectory(), "ZlookupSrc");

        if (!fSrc.exists()) {
            fSrc.mkdir();
        }
        File fDst = new File(Environment.getExternalStorageDirectory(), "ZlookupDst");
        if (!fDst.exists()) {
            fDst.mkdir();
        }

        //下载lookup文件(不开放代码和服务器地址 (uisdk/doc/*.java))
//        new TestLookupDownload(context, fSrc, fDst);

        //下载完成 再根据lookup文件和基本图片生成
        create(fSrc, fDst);


    }

    /**
     * 生成图标
     *
     * @param file
     * @param ftarget
     */
    private void create(final File file, final File ftarget) {
        ThreadPoolUtils.executeEx(new Runnable() {
            @Override
            public void run() {
                File[] fs = file.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        return filename.contains("png");
                    }
                });
                int len = fs.length;
                for (int i = 0; i < len; i++) {
                    Log.e(TAG, "run: " + i);
                    iconImp(ftarget, fs[i]);
                }
            }
        });
    }

    /**
     * 封面
     *
     * @param fDstDir
     * @param fSrc
     */
    private void iconImp(File fDstDir, File fSrc) {
        String src = fSrc.getAbsolutePath();
        String name = fSrc.getName();

        File sdPath = new File(fDstDir, name);
        if (!sdPath.exists()) {
            try {
                VirtualVideo virtualVideo = new VirtualVideo();
                Scene scene = VirtualVideo.createScene();
                MediaObject img = new MediaObject(mBaseMedia);
                scene.addMedia(img);
                virtualVideo.addScene(scene);
                //lookup
                virtualVideo.changeFilter(new VisualFilterConfig(src));
                Log.e(TAG, "   iconImp:  prepare getSnapsHot >" + ">>" + src + ">>" + mBaseMedia);
                Bitmap tmp = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
                //*********************************************************************************************************此方法会出现卡死的现象(bug 待解决)，如果出现请杀进程再创建
                boolean result = virtualVideo.getSnapshot(mContext, 2f, tmp, true);
                Log.e(TAG, "iconImp: icon:Y/N :" + result);
                if (result) {
                    //获取滤镜icon成功
                    BitmapUtils.saveBitmapToFile(tmp, true, 100, sdPath.getAbsolutePath());
                    Log.e(TAG, "iconImp: icon：" + sdPath);
                }
                virtualVideo.release();
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
