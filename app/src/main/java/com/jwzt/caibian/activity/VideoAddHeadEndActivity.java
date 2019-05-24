package com.jwzt.caibian.activity;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.jwzt.caibian.util.Config;
import com.jwzt.caibian.util.GETImageUntils;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.ToastUtils;
import com.jwzt.caibian.view.CustomProgressDialog;
import com.jwzt.caibian.view.FrameSelectorView;
import com.jwzt.caibian.xiangce.Bimp;
import com.jwzt.caibian.xiangce.ItemImage;
import com.jwzt.cb.product.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qiniu.pili.droid.shortvideo.PLMediaFile;
import com.qiniu.pili.droid.shortvideo.PLShortVideoComposer;
import com.qiniu.pili.droid.shortvideo.PLShortVideoTrimmer;
import com.qiniu.pili.droid.shortvideo.PLVideoEncodeSetting;
import com.qiniu.pili.droid.shortvideo.PLVideoSaveListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VideoAddHeadEndActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "VideoDivideActivity";

    private PLShortVideoTrimmer mShortVideoTrimmer;
    private PLMediaFile mMediaFile;

    private CustomProgressDialog mProcessingDialog;
    private VideoView mPreview;
    private ImageButton mPlaybackButton;
    private TextView tv_hecheng;
    private RelativeLayout rl_piantou, rl_video, rl_pianwei, rl_baonews;
    private ImageView img_piantou, img_video, img_pianwei, img_baonews;

    private long mDurationMs;
    private int mVideoFrameCount;
    private long mShowFrameIntervalMs;
    private String mSrcVideoPath;

    private int mFrameWidth;
    private int mFrameHeight;


    private PLShortVideoComposer mShortVideoComposer;

    private DisplayImageOptions options;
    private String piantouPath, pianweiPath;
    private List<String> pathList = new ArrayList<String>();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Uri piantouUri = Uri.fromFile(new File(piantouPath));
                    ImageLoader.getInstance().displayImage(piantouUri + "", img_piantou, options);
                    break;
                case 2:
                    Uri pianweiUri = Uri.fromFile(new File(pianweiPath));
                    ImageLoader.getInstance().displayImage(pianweiUri + "", img_pianwei, options);
                    break;
                case 3:
                    init();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.video_addheadend_activity);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.replace) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.replace) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.replace) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .bitmapConfig(Bitmap.Config.RGB_565)
//        .displayer(new FadeInBitmapDisplayer(100))
                .build(); // 构建完成

        mSrcVideoPath = getIntent().getStringExtra("vedio_path");
        mSrcVideoPath = "/storage/emulated/0/JWZTCBProduct/1538126093478.mp4";
        Log.i("===path===>>", mSrcVideoPath);
        initView();

        mProcessingDialog = new CustomProgressDialog(this);

        if (mSrcVideoPath != null && !"".equals(mSrcVideoPath)) {
            init();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        play();
    }

    public void onClickBack(View view) {
        finish();
    }


    /**
     * 添加选取分割线
     *
     * @param view
     */
    public void onClickAdd(View view) {
        pausePlayback();
    }

    public void onClickPlayback(View view) {
        if (mPreview.isPlaying()) {
            pausePlayback();
        } else {
            startPlayback();
        }
    }

    private void init() {
        if (IsNonEmptyUtils.isString(mSrcVideoPath)) {
            Uri videoUri = Uri.fromFile(new File(mSrcVideoPath));
            ImageLoader.getInstance().displayImage(videoUri + "", img_video, options);
        }
        initMediaInfo();
        initVideoPlayer();
    }

    private void initView() {
        mPreview = (VideoView) findViewById(R.id.preview);
        mPlaybackButton = (ImageButton) findViewById(R.id.pause_playback);

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mFrameWidth = mFrameHeight = wm.getDefaultDisplay().getWidth() / 6;

        tv_hecheng = findViewById(R.id.tv_hecheng);
        rl_piantou = findViewById(R.id.rl_piantou);
        rl_video = findViewById(R.id.rl_video);
        rl_pianwei = findViewById(R.id.rl_pianwei);
        rl_baonews = findViewById(R.id.rl_baonews);
        img_piantou = findViewById(R.id.img_piantou);
        img_video = findViewById(R.id.img_video);
        img_pianwei = findViewById(R.id.img_pianwei);
        img_baonews = findViewById(R.id.img_baonews);

        tv_hecheng.setOnClickListener(this);
        rl_piantou.setOnClickListener(this);
//        rl_video.setOnClickListener(this);
        rl_pianwei.setOnClickListener(this);
        rl_baonews.setOnClickListener(this);


//        img_piantou.setOnClickListener(this);
//        img_video.setOnClickListener(this);
//        img_pianwei.setOnClickListener(this);
//        img_baonews.setOnClickListener(this);
    }

    private void initMediaInfo() {
        mMediaFile = new PLMediaFile(mSrcVideoPath);
        mDurationMs = mMediaFile.getDurationMs();
        // if the duration time > 10s, the interval time is 3s, else is 1s
        mShowFrameIntervalMs = (mDurationMs >= 1000 * 10) ? 3000 : 1000;
        Log.i(TAG, "video duration: " + mDurationMs);
        mVideoFrameCount = mMediaFile.getVideoFrameCount(false);
        Log.i(TAG, "video frame count: " + mVideoFrameCount);

        mProcessingDialog = new CustomProgressDialog(this);
        mProcessingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mShortVideoTrimmer.cancelTrim();
            }
        });
    }

    private void initVideoPlayer() {
        mPreview.setVideoPath(mSrcVideoPath);
        mPreview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                play();
            }
        });
        play();
    }


    private void startPlayback() {
        mPreview.start();
        mPlaybackButton.setImageResource(R.drawable.btn_pause);
    }

    private void pausePlayback() {
        mPreview.pause();
        mPlaybackButton.setImageResource(R.drawable.btn_play);
    }

    private void play() {
        if (mPreview != null) {
            mPreview.seekTo(0);
            startPlayback();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_piantou:
                Intent intentPiantou = new Intent();
                intentPiantou.setType("video/*"); //选择视频 （mp4 3gp 是android支持的视频格式）
                intentPiantou.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intentPiantou, 100);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
//            case R.id.rl_video:
//
//                break;
            case R.id.rl_pianwei:
                Intent intentPianWei = new Intent();
                intentPianWei.setType("video/*"); //选择视频 （mp4 3gp 是android支持的视频格式）
                intentPianWei.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intentPianWei, 101);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.rl_baonews:

                break;
            case R.id.tv_hecheng://合成
                if (IsNonEmptyUtils.isString(piantouPath)) {
                    pathList.add(piantouPath);
                }
                if (IsNonEmptyUtils.isString(mSrcVideoPath)) {
                    pathList.add(mSrcVideoPath);
                }
                if (IsNonEmptyUtils.isString(pianweiPath)) {
                    pathList.add(pianweiPath);
                }
                if (pathList.size() <= 1) {
                    ToastUtils.s(VideoAddHeadEndActivity.this, "请选择片头或片尾");
                    return;
                }

                mProcessingDialog.setProgress(0);
                mProcessingDialog.setMessage("正在合并，请稍后...");
                mProcessingDialog.show();
                mShortVideoComposer = new PLShortVideoComposer(this);
                PLVideoEncodeSetting setting = new PLVideoEncodeSetting(VideoAddHeadEndActivity.this);
                setting.setEncodingSizeLevel(PLVideoEncodeSetting.VIDEO_ENCODING_SIZE_LEVEL.VIDEO_ENCODING_SIZE_LEVEL_480P_1);
                String savePath = Config.VIDEO_STORAGE_DIR+"videos/" + System.currentTimeMillis() + ".mp4";
                File file = new File(savePath);
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (mShortVideoComposer.composeVideos(pathList, savePath, setting, mVideoSaveListener)) {

                } else {
                    ToastUtils.s(this, "开始拼接失败！");
                }
                break;
//            case R.id.img_piantou:
//
//                break;
//            case R.id.img_video:
//
//                break;
//            case R.id.img_pianwei:
//
//                break;
//            case R.id.img_baonews:
//
//                break;
        }
    }

    private PLVideoSaveListener mVideoSaveListener = new PLVideoSaveListener() {
        @Override
        public void onSaveVideoSuccess(final String destFile) {
            mProcessingDialog.dismiss();
            Log.i("===合并状态===>>", "合并完成:"+destFile);
            mSrcVideoPath=destFile;
            mHandler.sendEmptyMessage(3);
//            Intent intent = new Intent(VideoAddHeadEndActivity.this, VideoDivideActivity.class);
//            intent.putExtra("vedio_path", destFile);
//            startActivity(intent);
        }

        @Override
        public void onSaveVideoFailed(int errorCode) {
            System.out.print("" + errorCode);
        }

        @Override
        public void onSaveVideoCanceled() {
            System.out.print("");
        }

        @Override
        public void onProgressUpdate(final float percentage) {
            System.out.print("" + percentage);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProcessingDialog.setProgress((int) (100 * percentage));
                }
            });
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {//本地选择视频
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    piantouPath = GETImageUntils.getPath(VideoAddHeadEndActivity.this, uri);
                    mHandler.sendEmptyMessage(1);
                }
            }
        } else if (requestCode == 101) {//本地选择视频
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    pianweiPath = GETImageUntils.getPath(VideoAddHeadEndActivity.this, uri);
                    mHandler.sendEmptyMessage(2);
                }
            }
        }
    }
}
