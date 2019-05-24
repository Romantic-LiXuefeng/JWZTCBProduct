package com.jwzt.caibian.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jwzt.cb.product.R;
import com.rd.vecore.exception.InvalidArgumentException;
import com.rd.vecore.exception.InvalidStateException;
import com.rd.veuisdk.VideoView;
import com.rd.veuisdk.manager.VideoMetadataRetriever;

import java.util.Formatter;
import java.util.Locale;

public class VideoPlayerActivity extends Activity {
    private String TAG = "VideoPlayerActivity";
    public static final String ACTION_PATH = "视频路径";
    private TextView mVideoCurrentPos, mVideoDuration;
    private SeekBar mPlayControl;
    private ImageView mVideoPlayerState;
    private VideoView mVideoView;
    private ProgressDialog mLoadingDialog;
    private FrameLayout mVideoViewParent;
    private String mSupportAntiChange;
    private float mLastPlayerPosition = -1;
    private boolean mLastPlaying = true;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.toString();
        setContentView(R.layout.activity_video_player);
        String path = getIntent().getStringExtra(ACTION_PATH);

        // 检索视频信息
        VideoMetadataRetriever vmr = new VideoMetadataRetriever();
        vmr.setDataSource(path);

        // 读取视频信息
        Log.i(TAG,
                "video duration:"
                        + vmr.extractMetadata(VideoMetadataRetriever.METADATA_KEY_VIDEO_DURATION));
        mSupportAntiChange = vmr
                .extractMetadata(VideoMetadataRetriever.METADATA_KEY_IS_SUPPORT_ANTI_CHANGE);
        Log.i(TAG, "is support anti-change:" + mSupportAntiChange);
        Log.i(TAG,
                "video bit rate:"
                        + vmr.extractMetadata(VideoMetadataRetriever.METADATA_KEY_VIDEO_BIT_RATE));
        Log.i(TAG,
                "video width:"
                        + vmr.extractMetadata(VideoMetadataRetriever.METADATA_KEY_VIDEO_WIDHT));
        Log.i(TAG,
                "video height:"
                        + vmr.extractMetadata(VideoMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        Log.i(TAG,
                "video frame rate:"
                        + vmr.extractMetadata(VideoMetadataRetriever.METADATA_KEY_VIDEO_FRAME_RATE));

        mVideoView = (VideoView) findViewById(R.id.svpriview);
        mVideoView.setPlayerListener(playerListener);
        try {
            mVideoView.setVideoPath(path);
        } catch (InvalidStateException e) {
            Toast.makeText(VideoPlayerActivity.this,
                    getString(android.R.string.VideoView_error_text_unknown),
                    Toast.LENGTH_SHORT).show();
            return;
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }
        mVideoPlayerState = (ImageView) findViewById(R.id.ivPlayerState);
        mVideoViewParent = (FrameLayout) findViewById(R.id.videoParentGroup);
        mVideoPlayerState.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mVideoView.isPlaying()) {
                    pauseVideo();
                } else {
                    playVideo();
                }
            }
        });

        mVideoView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mVideoView.isPlaying()) {
                    pauseVideo();
                } else {
                    playVideo();
                }
            }
        });

        mVideoCurrentPos = (TextView) findViewById(R.id.tvEditorCurrentPos);
        mVideoDuration = (TextView) findViewById(R.id.tvEditorDuration);

        findViewById(R.id.left).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mPlayControl = (SeekBar) findViewById(R.id.sbEditor);
        mPlayControl.setOnSeekBarChangeListener(onSeekbarlistener);
        mPlayControl.setMax(100);
        mLoadingDialog = ProgressDialog.show(this, null, "正在加载视频...");
    }

    private void prepare() {
        mVideoCurrentPos.setText(getTimeString(0));
        mVideoDuration.setText(getTimeString((int) (mVideoView.getDuration() * 1000)));
        mPlayControl.setMax((int) (mVideoView.getDuration() * 1000));
    }

    private String getTimeString(int progress) {
        return stringForTime(progress, false);
    }

    private void onComplete() {
        mPlayControl.setProgress(0);
        mVideoPlayerState.setImageResource(R.drawable.btn_play);
        mVideoPlayerState.setVisibility(View.VISIBLE);
        mVideoCurrentPos.setText(getTimeString(0));
    }

    @Override
    protected void onPause() {
        mLastPlayerPosition = mVideoView.getCurrentPosition();
        mLastPlaying = mVideoView.isPlaying();
        mVideoView.stop();
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mLastPlaying) {
            playVideo();
        } else {
            pauseVideo();
        }
        mVideoView.seekTo(mLastPlayerPosition);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mVideoView.cleanUp();
        super.onDestroy();
    }


    private SeekBar.OnSeekBarChangeListener onSeekbarlistener = new SeekBar.OnSeekBarChangeListener() {
        private boolean m_bIsPlayingOnSeek; // Seek时是否播放中...

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            if (fromUser) {
                mVideoCurrentPos.setText(getTimeString(progress));
                mVideoView.seekTo(progress / 1000.0f);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            if (mVideoView.isPlaying()) {
                pauseVideo();
                m_bIsPlayingOnSeek = true;
            } else {
                m_bIsPlayingOnSeek = false;
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (m_bIsPlayingOnSeek) {
                playVideo();
            }
        }
    };

    private void playVideo() {
        mVideoView.start();
        mVideoPlayerState.setImageResource(R.drawable.btn_pause);
        Animation an = AnimationUtils.loadAnimation(getApplicationContext(),
                android.R.anim.fade_out);
        mVideoPlayerState.setAnimation(an);
        mVideoPlayerState.setVisibility(View.INVISIBLE);
    }

    private void pauseVideo() {
        if (mVideoView.isPlaying()) {
            mVideoView.pause();
        }
        mVideoPlayerState.setImageResource(R.drawable.btn_play);
        mVideoPlayerState.setVisibility(View.VISIBLE);
    }

    private VideoView.PlayerListener playerListener = new VideoView.PlayerListener() {

        @Override
        public void onPlayerPrepare(VideoView mediaPlayerControl) {
            prepare();
            mLoadingDialog.dismiss();
            if ("yes".equals(mSupportAntiChange)) {
                final TextView tv = new TextView(getApplicationContext());
                tv.setText(R.string.video_isAntiChang);
                final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
                lp.gravity = Gravity.LEFT | Gravity.BOTTOM;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mVideoViewParent.addView(tv, lp);
                    }
                });

            }
        }

        @Override
        public boolean onPlayerError(VideoView mediaPlayerControl,
                                     int what, int extra) {
            mLoadingDialog.dismiss();
            Toast.makeText(VideoPlayerActivity.this,
                    getString(android.R.string.VideoView_error_text_unknown),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public void onPlayerCompletion(VideoView mediaPlayerControl) {
            onComplete();
        }

        @Override
        public void onGetCurrentPosition(VideoView mediaPlayerControl,
                                         float position) {
            mPlayControl.setProgress((int) (position * 1000));
            mVideoCurrentPos.setText(getTimeString((int) (position * 1000)));
        }
    };

    private final StringBuilder m_sbFormator = new StringBuilder();
    private final Formatter m_formatter = new Formatter(m_sbFormator,
            Locale.getDefault());

    /**
     * 毫秒数转换为时间格式化字符串 支持是否显示小时
     *
     * @param timeMs
     * @return
     */
    private String stringForTime(long timeMs, boolean existsHours) {
        boolean bNegative = timeMs < 0;// 是否为负数
        if (bNegative) {
            timeMs = -timeMs;
        }
        int totalSeconds = (int) (timeMs / 1000);

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        m_sbFormator.setLength(0);
        try {
            if (hours > 0 || existsHours) {
                return m_formatter.format("%s%02d:%02d:%02d",
                        bNegative ? "-" : "", hours, minutes, seconds)
                        .toString();
            } else {
                return m_formatter.format("%s%02d:%02d", bNegative ? "- " : "",
                        minutes, seconds).toString();
            }
        } catch (Exception ex) {
            return "";
        }
    }
}
