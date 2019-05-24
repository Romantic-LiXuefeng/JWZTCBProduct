package com.jwzt.caibian.activity;

import tcking.github.com.giraffeplayer.GiraffePlayer;
import tcking.github.com.giraffeplayer.GiraffePlayer.VideoExpandListener;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.util.IsNonEmptyUtils;
@SuppressLint("NewApi")
public class ShowVideoActivity extends Activity implements VideoExpandListener{
    GiraffePlayer player;
    RelativeLayout app_video_box;
    CheckBox app_video_more;
    int playerheight;
    private String playPath;

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showvideo);
        findView();
        playPath=getIntent().getStringExtra("playpath");
        if(IsNonEmptyUtils.isString(playPath)){
        	 player = new GiraffePlayer(this);
             player.setAdverMode(false); //配置是不是广告模式
             player.onComplete(new Runnable() {
                 @Override
                 public void run() {
                    
                 }
             }).onInfo(new GiraffePlayer.OnInfoListener() {
                 @Override
                 public void onInfo(int what, int extra) {
                     switch (what) {
                         case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                             //do something when buffering start
                             break;
                         case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                             //do something when buffering end
                             break;
                         case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
                             //download speed
//                             ((TextView) findViewById(R.id.tv_speed)).setText(Formatter.formatFileSize(getApplicationContext(),extra)+"/s");
                             break;
                         case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                             //do something when video rendering
//                             findViewById(R.id.tv_speed).setVisibility(View.GONE);
                             break;
                     }
                 }
             }).onError(new GiraffePlayer.OnErrorListener() {
                 @Override
                 public void onError(int what, int extra) {
                     Toast.makeText(getApplicationContext(), "video play error",Toast.LENGTH_SHORT).show();
                 }
             }).setShowNavIcon(true);
//             String url="http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4";
//             String url="/storage/emulated/0/DCIM/Camera/VID_20170619_154208.mp4";
             player.setShowDanMuBtn(false);
             player.setShowNavIcon(true); 
             player.play(playPath);
             player.setTitle(playPath);
        }
    }

    private void findView(){
    	app_video_box=(RelativeLayout) findViewById(R.id.app_video_box);
    	app_video_more=(CheckBox) findViewById(R.id.app_video_more);
        findViewById(R.id.app_video_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    	app_video_more.setVisibility(View.GONE);
    }
  
	@Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.onDestroy();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
		if (player != null) {
//			player.onConfigurationChanged(newConfig);
			if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
				app_video_box.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, playerheight));
			}else{
				playerheight=app_video_box.getHeight();
				app_video_box.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			}
		}
    }

    @Override
    public void onBackPressed() {
        if (player != null && player.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }
    /**
     * 实现分享功能
     */
	@Override
	public void share() {
		// TODO Auto-generated method stub
		Toast.makeText(this, "分享", 1).show();
	}
    //实现开启弹幕和关闭弹幕
	@Override
	public void openDanmuku(int biao) {
	}
	
	@Override
	public void collection() {		
	}

	@Override
	public void prisetovide() {		
	}

	@Override
	public void unLocked() {		
	}

	/**
	 * 标高清切换播放实现
	 * @param biao
	 * 1标 2高
	 */
	@Override
	public void playStandardHD(int biao) {
	}
	
   //用来同步播放器//播放暂停 1 播放开始2  同步弹幕-->需要先判断弹幕库的开关设置
	@Override
	public void OnPauseOrPlayDammu(int biao) {//实现开启弹幕和关闭  开启弹幕 1 弹幕关闭2
	}

    /**
     * 增加弹幕
     */
	@Override
	public void addDanmu(String danmuContents) {
	}

    ////1是跳过广告  2是详情点击  3是静音  4是开启音量
	@Override
	public void dealAdvers(int action) {
		switch (action) {
		case 1:  //跳过广告或者是播放完成
			break;
		case 2: //广告详情处理
			break;
		case 3://静音
			AudioManager	audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
	        int mMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
			break;
		case 4: //开启音量
			AudioManager	audioManagers = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
	        int mVolumes = audioManagers.getStreamVolume(AudioManager.STREAM_MUSIC);
	        audioManagers.setStreamVolume(AudioManager.STREAM_MUSIC, 40, 0);
			break;
		}
	}

}
