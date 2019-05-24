package com.jwzt.caibian.rd;

import android.content.Context;
import android.content.Intent;
import android.util.Log;


import com.jwzt.caibian.util.SDKUtils;
import com.rd.veuisdk.SdkEntry;
import com.rd.veuisdk.callback.ISdkCallBack;

/**
 * SDK回调演示用handler
 */
public class SdkHandler {
    private String TAG;

    public SdkHandler() {

        TAG = "SdkHandler";
    }

    public ISdkCallBack getCallBack() {
        return isdk;
    }


    private ISdkCallBack isdk = new ISdkCallBack() {

        /**
         * 目标视频的路径
         *
         * @param context
         *            应用上下文
         * @param exportType
         *            回调类型 来自简单录制 {@link SdkEntry#CAMERA_EXPORT}<br>
         *            来自录制编辑{@link SdkEntry#CAMERA_EDIT_EXPORT}<br>
         *            来自编辑导出{@link SdkEntry#EDIT_EXPORT}<br>
         *            来自普通截取视频导出{@link SdkEntry#TRIMVIDEO_EXPORT}<br>
         *            来自定长截取视频导出{@link SdkEntry#TRIMVIDEO_DURATION_EXPORT}<br>
         * @param videoPath
         */
        @Override
        public void onGetVideoPath(Context context, int exportType,
                                   String videoPath) {

            if (exportType == SdkEntry.EDIT_EXPORT) {
                Log.i(TAG, "getvideoPath  ordinary editor");
            } else if (exportType == SdkEntry.CAMERA_EXPORT) {
                Log.i(TAG, "getvideoPath  simple recording");
            } else if (exportType == SdkEntry.CAMERA_EDIT_EXPORT) {
                Log.i(TAG, "getvideoPath  record and edit");
            } else if (exportType == SdkEntry.TRIMVIDEO_EXPORT) {
                Log.i(TAG, "getvideoPath  ordinary trim");
            } else if (exportType == SdkEntry.TRIMVIDEO_DURATION_EXPORT) {
                Log.i(TAG, "getvideoPath  fixed duration trim " + videoPath);
            } else {
                Log.e(TAG, "getvideoPath  unknown");
            }
            SDKUtils.onVideoExport(context, videoPath);
            // 播放该视频
            SDKUtils.onPlayVideo(context, videoPath);
        }

        /**
         * 响应截取视频时间
         *
         * @param context
         *            应用上下文
         * @param exportType
         *            回调类型 来自普通截取视频的时间导出{@link SdkEntry#TRIMVIDEO_EXPORT}<br>
         *            来自定长截取视频的时间导出{@link SdkEntry#TRIMVIDEO_DURATION_EXPORT}<br>
         * @param startTime
         *            开始时间 单位:秒
         * @param endTime
         *            结束时间 单位:秒
         */
        @Override
        public void onGetVideoTrimTime(Context context, int exportType,
                                       float startTime, float endTime) {
            if (exportType == SdkEntry.TRIMVIDEO_EXPORT) {
                Log.i(TAG, "onGetVideoTrimTime  ordinary trim");
            } else if (exportType == SdkEntry.TRIMVIDEO_DURATION_EXPORT) {
                Log.i(TAG, "onGetVideoTrimTime   fixed duration trim ");
            } else {
                Log.e(TAG, "onGetVideoTrimTime  unknown");
            }
        }

        /**
         * 响应确认截取按钮
         *
         * @param context
         *            应用上下文
         * @param exportType
         *            来自普通截取的确认 {@link SdkEntry#TRIMVIDEO_EXPORT}<br>
         *            来自定长截取的确认 {@link SdkEntry#TRIMVIDEO_DURATION_EXPORT}<br>
         */
        @Override
        public void onGetVideoTrim(Context context, int exportType) {
            if (exportType == SdkEntry.TRIMVIDEO_EXPORT) {
                Log.i(TAG, "onGetVideoTrimTime  normal trim confirmation button");
            } else if (exportType == SdkEntry.TRIMVIDEO_DURATION_EXPORT) {
                Log.i(TAG, "onGetVideoTrimTime  fixed duration trim confirmation button");
            } else {
                Log.e(TAG, "onGetVideoTrimTime  unknown");
            }
            Intent intent = new Intent(context, DialogActivity.class);
            context.startActivity(intent);
        }

        /**
         * 响应进入相册（只显示照片、图片）
         *
         * @param context
         *            应用上下文
         */
        @Override
        public void onGetPhoto(Context context) {
            // 自定义相册调用位置
            Intent intent = new Intent(context, AlbumActivity.class);
            intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            context.startActivity(intent);
        }

        /**
         * 响应进入相册（只显示视频）
         *
         * @param context
         *            应用上下文
         */
        @Override
        public void onGetVideo(Context context) {
            // 自定义视频集调用位置
            Intent intent = new Intent(context, AlbumActivity.class);
            intent.setData(android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            context.startActivity(intent);
        }

    };


}
