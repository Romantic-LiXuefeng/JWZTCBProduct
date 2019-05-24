package com.jwzt.caibian.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.jwzt.caibian.adapter.VideoAudioAdapter;
import com.jwzt.caibian.bean.LocationAudioBean;
import com.jwzt.caibian.util.Config;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.ToastUtils;
import com.jwzt.caibian.view.CustomProgressDialog;
import com.jwzt.cb.product.R;
import com.qiniu.pili.droid.shortvideo.PLShortVideoEditor;
import com.qiniu.pili.droid.shortvideo.PLVideoEditSetting;
import com.qiniu.pili.droid.shortvideo.PLVideoSaveListener;
import com.qiniu.pili.droid.shortvideo.PLWatermarkSetting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 2018/10/10.
 */

public class VideoEditAudioActivity extends Activity implements View.OnClickListener, PLVideoSaveListener {
    private ListView lv_locationaudio;
    private ImageView img_back;
    private List<LocationAudioBean> mListAudio;

    private String videoPath;

    private GLSurfaceView mPreviewView;
    private CustomProgressDialog mProcessingDialog;
    private PLShortVideoEditor mShortVideoEditor;
    private boolean mIsUseWatermark = true;
    private PLWatermarkSetting mWatermarkSetting;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (mProcessingDialog != null && mShortVideoEditor != null) {
                        mProcessingDialog.show();
                        mShortVideoEditor.save();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        setContentView(R.layout.videoaudio_activity);
//        videoPath=getIntent().getStringExtra("vedio_path");
//        videoPath = "/storage/emulated/0/JWZTCBProduct/1538126093478.mp4";
        videoPath = "/storage/emulated/0/ShortVideo/trimmed.mp4";


        findView();

        getMusic();

        if (IsNonEmptyUtils.isList(mListAudio)) {
            VideoAudioAdapter videoAudioAdapter = new VideoAudioAdapter(VideoEditAudioActivity.this, mListAudio);
            lv_locationaudio.setAdapter(videoAudioAdapter);
        }
    }

    private void findView() {
        mPreviewView = findViewById(R.id.preview);
        mProcessingDialog = new CustomProgressDialog(this);
        mProcessingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mShortVideoEditor.cancelSave();
            }
        });
        lv_locationaudio = (ListView) findViewById(R.id.lv_locationaudio);
        img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setOnClickListener(this);


        PLVideoEditSetting setting = new PLVideoEditSetting();
        setting.setSourceFilepath(videoPath);
        setting.setDestFilepath(Config.VIDEO_STORAGE_DIR +"videos/"+ System.currentTimeMillis() + ".mp4");
        mShortVideoEditor = new PLShortVideoEditor(mPreviewView, setting);
        mShortVideoEditor.setVideoSaveListener(this);

        lv_locationaudio.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedFilepath = mListAudio.get(i).getPath();
                mShortVideoEditor.setAudioMixFile(selectedFilepath);
//                /storage/emulated/0/def.mp3  /storage/emulated/0/abc.mp3
                mHandler.sendEmptyMessageDelayed(1, 1000);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mShortVideoEditor.setWatermark(mIsUseWatermark ? mWatermarkSetting : null);
    }


    //获取SDcard中mp3文件
    public List<LocationAudioBean> getMusic() {
        mListAudio = new ArrayList<LocationAudioBean>();
        // 获取音乐列表
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                LocationAudioBean locationAudioBean = new LocationAudioBean();
                //歌曲编号
                //int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                //歌曲标题
                String tilte = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                locationAudioBean.setSong(tilte);
                //歌曲的专辑名：MediaStore.Audio.Media.ALBUM
                //String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                //歌曲的歌手名： MediaStore.Audio.Media.ARTIST
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                locationAudioBean.setSinger(artist);
                //歌曲文件的路径 ：MediaStore.Audio.Media.DATA
                String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                locationAudioBean.setPath(url);
                //歌曲的总播放时长 ：MediaStore.Audio.Media.DURATION
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                locationAudioBean.setDuration(duration);
                //歌曲文件的大小 ：MediaStore.Audio.Media.SIZE
                Long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                locationAudioBean.setSize(size);
                Long dateTime = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)) * 1000;
                locationAudioBean.setCreateTime(dateTime);
                if (url.endsWith(".mp3")) {
//                  把歌曲名字和歌手切割开
                    if (IsNonEmptyUtils.isString(locationAudioBean.getSong())) {
                        if (locationAudioBean.getSong().contains("-")) {
                            String[] str = locationAudioBean.getSong().split("-");
                            locationAudioBean.setSinger(str[0]);
                            locationAudioBean.setSong(str[1]);
                        }
                        mListAudio.add(locationAudioBean);
                    }
                }
                cursor.moveToNext();
            }
        }

        cursor.close();
        return mListAudio;

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                VideoEditAudioActivity.this.finish();
                break;
        }
    }

    @Override
    public void onSaveVideoSuccess(String filePath) {
        //编辑完成后返回的路径
        Log.i("===添加音频完成===>>", "save edit success filePath: " + filePath);
        mProcessingDialog.dismiss();
        VideoEditAudioActivity.this.finish();
    }

    @Override
    public void onSaveVideoFailed(final int errorCode) {
        Log.e("===添加音频失败===>>", "save edit failed errorCode:" + errorCode);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProcessingDialog.dismiss();
                ToastUtils.toastErrorCode(VideoEditAudioActivity.this, errorCode);
            }
        });
    }

    @Override
    public void onSaveVideoCanceled() {
        mProcessingDialog.dismiss();
    }

    @Override
    public void onProgressUpdate(final float percentage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProcessingDialog.setProgress((int) (100 * percentage));
            }
        });
    }
}
