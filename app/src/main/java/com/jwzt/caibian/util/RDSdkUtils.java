package com.jwzt.caibian.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;

import com.jwzt.caibian.dialog.ConfigData;
import com.jwzt.caibian.rd.authpack;
import com.jwzt.cb.product.R;
import com.rd.vecore.models.Watermark;
import com.rd.veuisdk.SdkEntry;
import com.rd.veuisdk.SdkService;
import com.rd.veuisdk.manager.CameraConfiguration;
import com.rd.veuisdk.manager.ExportConfiguration;
import com.rd.veuisdk.manager.TrimConfiguration;
import com.rd.veuisdk.manager.UIConfiguration;
import com.rd.veuisdk.manager.VEOSDBuilder;

import static com.rd.veuisdk.SdkEntry.getSdkService;

/**
 * Created by Administrator on 2018/12/25.
 */

public class RDSdkUtils {
    /**
     * 测试用水印图片
     */
    public  String EDIT_WATERMARK_PATH = Environment
            .getExternalStorageDirectory() + "/watermark.png";
    public  String EDIT_PICTURE_PATH = Environment
            .getExternalStorageDirectory() + "/android.jpg";
    /**
     * 导出的横向16:9视频
     */
    public String EDIT_L_VIDEO_PATH =Environment.getExternalStorageDirectory() + "/JWZTCBProduct/videos/"+ System.currentTimeMillis()+".mp4";
    /**
     * REQUEST_CODE定义：<br>
     * 视频编辑
     */
    public  final int EDIT_REQUEST_CODE = 102;
    /**
     * 测试用1：1方型视频
     */
    private String EDIT_S_VIDEO_PATH =Environment.getExternalStorageDirectory() + "/JWZTCBProduct/videos/"+ System.currentTimeMillis()+".mp4";
    /**
     * 测试用竖向9:16视频
     */
    private String EDIT_P_VIDEO_PATH = Environment.getExternalStorageDirectory() + "/JWZTCBProduct/videos/"+ System.currentTimeMillis()+".mp4";


    private String MASK_PATH = Environment.
            getExternalStorageDirectory() + "/mask.png";
    Activity mContext;
    public   RDSdkUtils(){}
public    RDSdkUtils(Activity context){
    this.mContext=context;
}
    public  ConfigData configData;




    /**
     * 还原持续久化保存的配置
     */
    public  void restoreConfigInstanceState() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("demo",
                Context.MODE_PRIVATE);
        configData = SdkService.restoreObject(sharedPreferences,
                "CONFIG_DATA", initAndGetConfigData());
    }
    /**
     * 初始化并返回配置
     */
    private ConfigData initAndGetConfigData() {
        if (configData == null) {
            configData = new ConfigData();
        }
        return configData;
    }

    /**
     * 短视频录制推荐参数
     */
    public  void initCameraShortVideoConfig() {

        CameraConfiguration cameraConfig = new CameraConfiguration.Builder()
                // 为true代表多次拍摄，拍摄完成一段之后，将保存至相册并开始下一段拍摄，默认为false单次拍摄，拍摄完成后返回资源地址
                .useMultiShoot(false)
                /**
                 * 设置录制时默认界面:<br>
                 * 默认16：9录制:<br>
                 * CameraConfiguration. WIDE_SCREEN_CAN_CHANGE<br>
                 * 默认1：1:<br>
                 * CameraConfiguration. SQUARE_SCREEN_CAN_CHANGE<br>
                 * 仅1：1录制:<br>
                 * CameraConfiguration.ONLY_SQUARE_SCREEN
                 */
                .setCameraUIType(CameraConfiguration.ONLY_SQUARE_SCREEN)
                // 设置拍摄完成后，是否保存至相册（仅单次拍摄方式有效），同时通过onActivityResult及SIMPLE_CAMERA_REQUEST_CODE返回
                .setSingleCameraSaveToAlbum(true)
                // 设置录制时是否静音，true代表录制后无声音
                .setAudioMute(false)
                // 设置是否启用人脸贴纸
                .enableFaceu(false)
                // 设置启用人脸贴纸鉴权证书
                .setPack(authpack.A())
                // 设置是否默认为后置摄像头
                .setDefaultRearCamera(false)
                // 是否显示相册按钮
                .enableAlbum(true)
                // 是否使用自定义相册
                .useCustomAlbum(false)
                // 设置隐藏拍摄功能（全部隐藏将强制开启视频拍摄）
                .hideMV(false).hidePhoto(true).hideRec(true)
                // 设置mv最小时长
                .setCameraMVMinTime(3)
                // 设置mv最大时长
                .setCameraMVMaxTime(15)
                //录制的云音乐
                .setCloudMusicUrl(configData.enableNewApi ? configData.customApi : "")
                //滤镜(lookup)
                .setFilterUrl(configData.enableNewApi ? configData.customApi : "")
                // 强制美颜
                .enableBeauty(true).get();
        // 视频编辑UI配置

        UIConfiguration.Builder builder = new UIConfiguration.Builder()
                // 设置是否使用自定义相册
                .useCustomAlbum(false);
        if (configData.enableNewApi) {
            //是否启用新的网络接口方式(资源放到自己服务器或锐动服务器)
            ConfigData configData = new ConfigData();
            configData.enableMV = true;
            configData.customApi = configData.customApi;

            initThridServer(builder, configData);
        } else {
            //网络音乐
            builder.setMusicUrl(ConfigData.MUSIC_URL)
                    // 设置MV和mv网络地
                    .enableMV(true, ConfigData.WEB_MV_URL)
                    //云音乐
                    .setCloudMusicUrl(ConfigData.CLOUDMUSIC_URL);
        }


        // 设置画面比例为1:1
        builder.setVideoProportion(UIConfiguration.PROPORTION_SQUARE)
                // 相册仅支持视频
                .setAlbumSupportFormat(UIConfiguration.ALBUM_SUPPORT_VIDEO_ONLY)
                // 设置视频选择最大数量
                .setMediaCountLimit(1)
                // 设置隐藏相册中的拍摄按钮
                .enableAlbumCamera(false)
                //显示配乐
                .setEditAndExportModuleVisibility(UIConfiguration.EditAndExportModules.SOUNDTRACK, true)
                //关闭配音
                .setEditAndExportModuleVisibility(UIConfiguration.EditAndExportModules.DUBBING, false)
                // 隐藏字幕
                .setEditAndExportModuleVisibility(UIConfiguration.EditAndExportModules.TITLING,
                        false)
                // 隐藏片段编辑
                .setEditAndExportModuleVisibility(
                        UIConfiguration.EditAndExportModules.CLIP_EDITING, false)
                // 隐藏特效
                .setEditAndExportModuleVisibility(
                        UIConfiguration.EditAndExportModules.SPECIAL_EFFECTS, false)
                // 启用自动重播
                .enableAutoRepeat(true);


        UIConfiguration uiConfig = builder.get();

        TrimConfiguration trimConfig = new TrimConfiguration.Builder()
                //设置实际截取时视频导出最大边,不设置时，默认为640
                .setVideoMaxWH(640)
                //设置实际截取时视频导出码率，Mbps为单位,不设置时，默认为4
                .setVideoBitRate(4)
                // 设置默认裁剪区域为1:1
                .setDefault1x1CropMode(true)
                // 设置截取返回类型
                .setTrimReturnMode(TrimConfiguration.TRIM_RETURN_TIME)
                // 设置截取类型
                .setTrimType(TrimConfiguration.TRIM_TYPE_SINGLE_FIXED)
                // 设置是否显示1：1按钮
                .enable1x1(false)
                // 设置定长截取时间
                .setTrimDuration(15).get();
        getSdkService().initConfiguration(null, uiConfig,
                cameraConfig);
        getSdkService().initTrimConfiguration(trimConfig);
    }


    /**
     * 初始标准编辑及导出配置
     */
    public  void initEditorUIAndExportConfig() {

        initAndGetConfigData();
        // 视频编辑UI配置


        UIConfiguration.Builder builder = new UIConfiguration.Builder()
                // 设置是否使用自定义相册
                .useCustomAlbum(configData.useCustomAlbum)
                // 设置向导化
                .enableWizard(configData.enableWizard)
                // 设置自动播放
                .enableAutoRepeat(configData.enableAutoRepeat)
                // 配音模式
                .setVoiceLayoutType(configData.voiceLayoutType)
                // 设置秀拍客相册支持格式
                .setAlbumSupportFormat(configData.albumSupportFormatType)
                // 设置默认进入界面画面比例
                .setVideoProportion(configData.videoProportionType)
                // 设置滤镜界面风格
                .setFilterType(configData.filterLayoutType)
                // 设置相册媒体选择数量上限(目前只对相册接口生效)
                .setMediaCountLimit(configData.albumMediaCountLimit)
                // 设置相册是否显示跳转拍摄按钮(目前只对相册接口生效)
                .enableAlbumCamera(configData.enableAlbumCamera)
                // 编辑与导出模块显示与隐藏（默认不设置为显示）
                .setEditAndExportModuleVisibility(
                        UIConfiguration.EditAndExportModules.SOUNDTRACK,
                        configData.enableSoundTrack)

                .setEditAndExportModuleVisibility(UIConfiguration.EditAndExportModules.DUBBING,
                        configData.enableDubbing)
                .setEditAndExportModuleVisibility(UIConfiguration.EditAndExportModules.FILTER,
                        configData.enableFilter)
                .setEditAndExportModuleVisibility(UIConfiguration.EditAndExportModules.TITLING,
                        configData.enableTitling)
                .setEditAndExportModuleVisibility(
                        UIConfiguration.EditAndExportModules.SPECIAL_EFFECTS,
                        configData.enableSpecialEffects)
                .setEditAndExportModuleVisibility(
                        UIConfiguration.EditAndExportModules.CLIP_EDITING,
                        configData.enableClipEditing)
                // 片段编辑模块显示与隐藏（默认不设置为显示）
                .setClipEditingModuleVisibility(
                        UIConfiguration.ClipEditingModules.IMAGE_DURATION_CONTROL,
                        configData.enableImageDuration)
                .setClipEditingModuleVisibility(UIConfiguration.ClipEditingModules.EDIT,
                        configData.enableEdit)
                .setClipEditingModuleVisibility(UIConfiguration.ClipEditingModules.TRIM,
                        configData.enableTrim)
                .setClipEditingModuleVisibility(
                        UIConfiguration.ClipEditingModules.VIDEO_SPEED_CONTROL,
                        configData.enableVideoSpeed)
                .setClipEditingModuleVisibility(UIConfiguration.ClipEditingModules.SPLIT,
                        configData.enableSplit)
                .setClipEditingModuleVisibility(UIConfiguration.ClipEditingModules.COPY,
                        configData.enableCopy)
                .setClipEditingModuleVisibility(UIConfiguration.ClipEditingModules.PROPORTION,
                        configData.enableProportion)
                .setClipEditingModuleVisibility(UIConfiguration.ClipEditingModules.SORT,
                        configData.enableSort)
                .setClipEditingModuleVisibility(UIConfiguration.ClipEditingModules.TEXT,
                        configData.enableText)
                .setClipEditingModuleVisibility(UIConfiguration.ClipEditingModules.REVERSE,
                        configData.enableReverse)
                .setClipEditingModuleVisibility(UIConfiguration.ClipEditingModules.TRANSITION, true);

        if (configData.enableNewApi) {
            initThridServer(builder, configData);
        } else {
            // 设置MV和mv网络地址
            builder.enableMV(configData.enableMV, ConfigData.WEB_MV_URL)
                    // 设置自定义的网络音乐
                    .setMusicUrl(ConfigData.MUSIC_URL)
                    //云音乐
                    .setCloudMusicUrl(ConfigData.CLOUDMUSIC_URL);
        }
        UIConfiguration uiConfig = builder
                //是否显示本地音乐
                .enableLocalMusic(configData.enableLocalMusic)
                // 字幕、特效在mv的上面
                .enableTitlingAndSpecialEffectOuter(configData.enableTitlingAndSpecialEffectOuter)
                .get();

        // 导出视频参数配置
        ExportConfiguration exportConfig = new ExportConfiguration.Builder()
                // 设置保存路径，传null或不设置
                // 将保存至默认路径(即调用SdkEntry.initialize初始时自定义路径）
                 .setSavePath(Environment.getExternalStorageDirectory() + "/JWZTCBProduct/videos/")
                //设置导出时最大边,不设置时，默认为640
                .setVideoMaxWH(640)
                //设置视频导出码率，Mbps为单位,不设置时，默认为4
                .setVideoBitRate(4)
                //设置视频导出帧率，,不设置时，默认为30
                .setVideoFrameRate(30)
                // 设置片尾图片路径，传null或者不设置 将没有片尾
                .setTrailerPath(configData.videoTrailerPath)
                // 设置片尾时长 单位s 默认2s
                .setTrailerDuration(2)
                // 设置导出视频时长 单位s 传0或者不设置 将导出完整视频
                .setVideoDuration(configData.exportVideoDuration)
                // 设置添加媒体时长限制 单位s 传0或者不设置 将没有限制
                .setImportVideoDuration(0)
                // 设置图片水印路径
                .setWatermarkPath(configData.enableWatermark ? EDIT_WATERMARK_PATH : null)
                // 设置水印显示模式
                .setWatermarkShowMode(Watermark.MODE_DEFAULT)
                // 设置是否使用文字水印（使用文字水印，将不再显示图片水印）
                .enableTextWatermark(configData.enableTextWatermark)
                // 设置文字水印内容（开启文字水印才生效）
                .setTextWatermarkContent("北京经纬中天")
                // 设置文字水印大小（开启文字水印才生效）
                .setTextWatermarkSize(10)
                // 设置文字水印颜色（开启文字水印才生效）
                .setTextWatermarkColor(Color.WHITE)
                // 设置文字水印阴影颜色（开启文字水印才生效）
                .setTextWatermarkShadowColor(Color.BLACK)
                // 设置水印位置 (文字或图片水印开启才生效)
                .setWatermarkPosition(configData.watermarkShowRectF).get();

        // 获取秀拍客配置服务器
        SdkService sdkService = getSdkService();
        if (null != sdkService) {
            // 初始化所有配置
            sdkService.initConfiguration(exportConfig, uiConfig);
        }
    }

    /**
     * 初始视频截取配置
     */
    public  void initTrimConfig() {
        SdkService sdkService = getSdkService();
        if (null != sdkService) {
            sdkService
                    .initTrimConfiguration(new TrimConfiguration.Builder()
                            //设置实际截取时视频导出最大边,不设置时，默认为640
                            .setVideoMaxWH(640)
                            //设置实际截取时视频导出码率，Mbps为单位,不设置时，默认为4
                            .setVideoBitRate(4)
                            // 设置默认裁剪区域为1:1
                            .setDefault1x1CropMode(
                                    configData.default1x1CropMode)
                            // 设置是否显示1:1裁剪按钮
                            .enable1x1(configData.enable1x1)
                            // 设置截取返回类型
                            .setTrimReturnMode(configData.mTrimReturnMode)
                            // 设置截取类型
                            .setTrimType(configData.mTrimType)
                            // 设置两定长截取时间
                            .setTrimDuration(configData.trimTime1, configData.trimTime2)
                            // 设置单个定长截取时间
                            .setTrimDuration(configData.trimSingleFixedDuration)
                            .get());
        }
    }

    /**
     * 导出测试资源
     */
    public  void exportDemoResource() {

        if (!SDKUtils.isValidFile(EDIT_PICTURE_PATH)
                || !SDKUtils.isValidFile(EDIT_L_VIDEO_PATH)
                || !SDKUtils.isValidFile(EDIT_S_VIDEO_PATH)
                || !SDKUtils.isValidFile(EDIT_P_VIDEO_PATH)
                || !SDKUtils.isValidFile(EDIT_WATERMARK_PATH)
                || !SDKUtils.isValidFile(MASK_PATH)
                ) {
            new AsyncTask<Integer, Integer, Integer>() {
                private ProgressDialog m_dlgProgress;


                @Override
                protected void onPreExecute() {
                    m_dlgProgress = ProgressDialog.show(mContext,
                            null, mContext.getString(R.string.export_asset));
                }

                @Override
                protected Integer doInBackground(Integer... params) {

                    SDKUtils.assetRes2File(mContext.getAssets(), "demomedia/android.jpg", EDIT_PICTURE_PATH);
                    SDKUtils.assetRes2File(mContext.getAssets(), "demomedia/demoVideo1.mp4", EDIT_L_VIDEO_PATH);
                    SDKUtils.assetRes2File(mContext.getAssets(), "demomedia/demoVideo2.mp4", EDIT_S_VIDEO_PATH);
                    SDKUtils.assetRes2File(mContext.getAssets(), "demomedia/demoVideo3.mp4", EDIT_P_VIDEO_PATH);
                    SDKUtils.assetRes2File(mContext.getAssets(), "watermark.png", EDIT_WATERMARK_PATH);
                    SDKUtils.assetRes2File(mContext.getAssets(), "mask.png", MASK_PATH);
                    return null;
                }

                @Override
                protected void onPostExecute(Integer result) {
                    m_dlgProgress.dismiss();
                    m_dlgProgress = null;
                }
            }.execute();
        }

        //部分版本由于缺少内置资源不支持此功能
        if (SdkEntry.isLite(mContext)) {
//            mContext.findViewById(R.id.btnQuik).setVisibility(View.GONE);
//            mContext.findViewById(R.id.imageTrans).setVisibility(View.GONE);
//            mContext.findViewById(R.id.apiVideoTemp).setVisibility(View.GONE);
        }

    }
    /**
     * 新的网络接口方式(资源放到自己服务器或锐动服务器)
     *
     * @param builder
     * @param configData
     */
    private void initThridServer(UIConfiguration.Builder builder, ConfigData configData) {

        String url = configData.customApi;
        // 设置MV和mv网络地址
        builder.enableNewMV(configData.enableMV, url)
                //设置字幕URL
                .setTitlingUrl(url)
                //设置字体URL
                .setFontUrl(url)
                //特效URL
                .setSpecialEffectsUrl(url)
                //滤镜URL(必须是lookup滤镜)
                .setFilterUrl(url)
                //转场URL
                .setTransitionUrl(url)
                // 设置自定义的网络音乐
                .setNewMusicUrl(url)
                //云音乐
//                .setNewCloudMusicUrl(url);
                .setNewCloudMusicUrl(url, "Jason Shaw", "audionautix.com", "https://audionautix.com",
                        mContext.getString(R.string.yunmusic_sign), "http://d.56show.com/accredit/accredit.jpg");


    }
    /**
     * 初始拍摄配置
     *
     * @param UIType * 设置录制时默认界面:<br>
     *               默认16：9录制:<br>
     *               CameraConfiguration#WIDE_SCREEN_CAN_CHANGE<br>
     *               默认1：1:<br>
     *               CameraConfiguration. SQUARE_SCREEN_CAN_CHANGE<br>
     *               仅16：9录制:<br>
     *               CameraConfiguration.ONLY_WIDE_SCREEN<br>
     *               仅1：1录制:<br>
     *               CameraConfiguration.ONLY_SQUARE_SCREEN
     */

    public  void initCameraConfig(int UIType) {
        getSdkService().initConfiguration(
                new CameraConfiguration.Builder()
                        // 可设置最小录制时长,0代表不限制
                        .setVideoMinTime(configData.cameraMinTime)
                        // 可设置最大录制时长,0代表不限制
                        .setVideoMaxTime(configData.cameraMaxTime)
                        // 为true代表多次拍摄，拍摄完成一段之后，将保存至相册并开始下一段拍摄，默认为false单次拍摄，拍摄完成后返回资源地址
                        .useMultiShoot(configData.useMultiShoot)
                        /**
                         * 设置录制时默认界面:<br>
                         * 默认16：9录制:<br>
                         * CameraConfiguration. WIDE_SCREEN_CAN_CHANGE<br>
                         * 默认1：1:<br>
                         * CameraConfiguration. SQUARE_SCREEN_CAN_CHANGE<br>
                         * 仅16：9录制:<br>
                         * CameraConfiguration.ONLY_SCREEN_SCREEN 仅1：1录制:<br>
                         * CameraConfiguration.ONLY_SQUARE_SCREEN
                         */
                        .setCameraUIType(UIType)
                        // 设置拍摄完成后，是否保存至相册（仅单次拍摄方式有效），同时通过onActivityResult及SIMPLE_CAMERA_REQUEST_CODE返回
                        .setSingleCameraSaveToAlbum(configData.isSaveToAlbum)
                        // 设置录制时是否静音，true代表录制后无声音
                        .setAudioMute(false)
                        // 设置是否启用人脸贴纸功能
                        .enableFaceu(configData.isDefaultFace)
                        // 设置人脸贴纸鉴权证书
                        .setPack(authpack.A())
                        // 设置是否默认为后置摄像头
                        .setDefaultRearCamera(configData.isDefaultRearCamera)
                        // 是否显示相册按钮
                        .enableAlbum(configData.enableAlbum)
                        // 是否使用自定义相册
                        .useCustomAlbum(configData.useCustomAlbum)
                        // 设置隐藏拍摄功能（全部隐藏将强制开启视频拍摄）
                        .hideMV(configData.hideMV)
                        .hidePhoto(configData.hidePhoto)
                        .hideRec(configData.hideRec)
                        // 设置mv最小时长
                        .setCameraMVMinTime(configData.cameraMVMinTime)
                        // 设置mv最大时长
                        .setCameraMVMaxTime(configData.cameraMVMaxTime)
                        // 开启相机水印时需注册水印
                        // SdkEntry.registerOSDBuilder(CameraWatermarkBuilder.class);
                        // 相机录制水印
                        .enableWatermark(configData.enableCameraWatermark)
                        // 相机水印片头
                        .setCameraTrailerTime(VEOSDBuilder.OSDState.header, 2f)
                        // 相机录制结束时片尾水印时长(0-1.0 单位：秒)
                        .setCameraTrailerTime(VEOSDBuilder.OSDState.end,
                                configData.cameraWatermarkEnd)
                        // 是否启用防篡改录制
                        .enableAntiChange(configData.enableAntiChange)
                        // 启用前置输出时镜像
                        .enableFrontMirror(configData.enableFrontMirror)
                        // 固定录制界面的方向
                        .setOrientation(configData.mRecordOrientation)
                        // 是否支持录制时播放音乐
                        .enablePlayMusic(configData.enablePlayMusic)
                        // 是否美颜
                        .enableBeauty(configData.enableBeauty)
                        //录制的云音乐
//                        .setCloudMusicUrl(configData.enableNewApi ? configData.customApi : "")
                        .setCloudMusicUrl(configData.enableNewApi ? configData.customApi : "", "Jason Shaw", "audionautix.com", "https://audionautix.com",
                                mContext.getString(R.string.yunmusic_sign), "http://d.56show.com/accredit/accredit.jpg")
                        //滤镜(lookup)
                        .setFilterUrl(configData.enableNewApi ? configData.customApi : "")
                        .get());
    }
}
