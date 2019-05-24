package com.jwzt.caibian.rd;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;


import com.jwzt.caibian.util.SDKUtils;
import com.rd.veuisdk.SdkEntry;
import com.rd.veuisdk.SdkService;
import com.rd.veuisdk.manager.FaceuConfig;

import java.io.File;

/**
 * 导入人脸贴纸资源
 */
public class FaceHandler {
    // 自定义网络化加载人脸贴纸资源
    private static final String URL = "http://dianbook.17rd.com/api/shortvideo/getfaceprop";

    public static void initialize(final Context context) {
        new AsyncTask<Integer, Integer, Integer>() {
            AssetManager asset;
            String mRoot = null;
            FaceuConfig mConfig = null;

            @Override
            protected void onPreExecute() {
                asset = context.getAssets();
                mRoot = context.getExternalCacheDir() + "/faceu/";
                File f = new File(mRoot);
                if (!f.exists()) {
                    f.mkdirs();
                }
            }

            @Override
            protected Integer doInBackground(Integer... params) {
                mConfig = new FaceuConfig();
                // 设置开启美颜时美白的等级 (参数值0.0f-1.0f,开启人脸识别且开启美颜之后生效)
                mConfig.setColor_level(0.48f);
                // 设置开启美颜时磨皮的等级(参数值0.0f-4.0f,开启人脸识别且开启美颜之后生效)
                mConfig.setBlur_level(4.0f);
                // 设置开启美颜时瘦脸的等级 (有效参数值0.0f-2.0f,开启人脸识别且开启美颜之后生效)
                mConfig.setCheek_thinning(0.68f);
                // 设置美颜时大眼的等级(有效参数值0.0f-4.0f,开启人脸识别且开启美颜之后生效)
                mConfig.setEye_enlarging(1.53f);

                // 方式一: 加载本地资源
                String dog = mRoot + "BeagleDog.mp3", dogIcon = mRoot
                        + "beagledog.png";
                addItem(asset, dog, "BeagleDog.mp3", dogIcon, "beagledog.png",
                        "BeagleDog", mConfig);
                // 方式二:加载网络资源,设置网络化的人脸贴纸数据接口
                mConfig.enableNetFaceu(true, URL);

                return null;
            }

            @Override
            protected void onPostExecute(Integer result) {
                SdkService service = SdkEntry.getSdkService();
                if (null != service) {
                    service.initFaceuConfig(mConfig);
                }
            }
        }.execute();
    }

    private static void addItem(AssetManager asset, String path, String assMp3,
                                String Icon, String assIcon, String title, FaceuConfig config) {
        if (!new File(path).exists()) {
            SDKUtils.assetRes2File(asset, "faceu/" + assMp3, path);
        }
        if (!new File(Icon).exists()) {
            SDKUtils.assetRes2File(asset, "faceu/" + assIcon, Icon);
        }
        config.addFaceu(path, Icon, title);
    }
}
