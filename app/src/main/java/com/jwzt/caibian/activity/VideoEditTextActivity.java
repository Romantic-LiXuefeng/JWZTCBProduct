package com.jwzt.caibian.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jwzt.caibian.util.Config;
import com.jwzt.caibian.util.GetPathFromUri;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.ToastUtils;
import com.jwzt.caibian.view.AudioMixSettingDialog;
import com.jwzt.caibian.view.CustomProgressDialog;
import com.jwzt.caibian.view.FrameListViewEdit;
import com.jwzt.caibian.view.FrameSelectorViewEdit;
import com.jwzt.caibian.view.PaintSelectorPanel;
import com.jwzt.caibian.view.StrokedTextView;
import com.jwzt.caibian.view.TextSelectorPanel;
import com.jwzt.cb.product.R;
import com.qiniu.pili.droid.shortvideo.PLBuiltinFilter;
import com.qiniu.pili.droid.shortvideo.PLImageView;
import com.qiniu.pili.droid.shortvideo.PLPaintView;
import com.qiniu.pili.droid.shortvideo.PLShortVideoEditor;
import com.qiniu.pili.droid.shortvideo.PLTextView;
import com.qiniu.pili.droid.shortvideo.PLVideoEditSetting;
import com.qiniu.pili.droid.shortvideo.PLVideoSaveListener;
import com.qiniu.pili.droid.shortvideo.PLWatermarkSetting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import static com.jwzt.caibian.util.RecordSettings.RECORD_SPEED_ARRAY;


public class VideoEditTextActivity extends Activity implements PLVideoSaveListener {
    private static final String TAG = "VideoEditTextActivity";
    private static final String MP4_PATH = "vedio_path";

    private static final int REQUEST_CODE_PICK_AUDIO_MIX_FILE = 0;
    private static final int REQUEST_CODE_DUB = 1;

    // 视频编辑器预览状态
    private enum PLShortVideoEditorStatus {
        Idle,
        Playing,
        Paused,
    }

    private PLShortVideoEditorStatus mShortVideoEditorStatus = PLShortVideoEditorStatus.Idle;

    private GLSurfaceView mPreviewView;
    private RecyclerView mFiltersList;
    private TextSelectorPanel mTextSelectorPanel;
    private CustomProgressDialog mProcessingDialog;
    private ImageButton mPausePalybackButton;
    private PaintSelectorPanel mPaintSelectorPanel;
    private LinearLayout mSpeedPanel;

    private PLShortVideoEditor mShortVideoEditor;
    private String mSelectedFilter;
    private String mSelectedMV;
    private String mSelectedMask;
    private PLWatermarkSetting mWatermarkSetting;
    private PLPaintView mPaintView;

    private PLTextView mCurTextView;
    private PLImageView mCurImageView;

    private long mMixDuration = 5000; // ms
    private boolean mIsMuted = false;
    private boolean mIsMixAudio = false;
    private boolean mIsUseWatermark = true;

    private String mMp4path;

    private TextView mSpeedTextView,tv_style;

    private FrameListViewEdit mFrameListView;
    private TimerTask mScrollTimerTask;
    private Timer mScrollTimer;
    private View mCurView;

    private RelativeLayout rl_zimucontent;
    private StrokedTextView et_zimu;

    public static void start(Activity activity, String mp4Path) {
        Intent intent = new Intent(activity, VideoEditTextActivity.class);
        intent.putExtra(MP4_PATH, mp4Path);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        setContentView(R.layout.activity_editor);

        findView();

        initWatermarkSetting();
        initShortVideoEditor();
    }

    private void findView(){
        tv_style=(TextView) findViewById(R.id.tv_style);
        mSpeedTextView = (TextView) findViewById(R.id.normal_speed_text);
        mPausePalybackButton = (ImageButton) findViewById(R.id.pause_playback);
        mSpeedPanel = (LinearLayout) findViewById(R.id.speed_panel);
        mFrameListView = (FrameListViewEdit) findViewById(R.id.frame_list_view);

        rl_zimucontent=(RelativeLayout) findViewById(R.id.rl_zimucontent);
        et_zimu=(StrokedTextView) findViewById(R.id.et_zimu);

        mPreviewView = (GLSurfaceView) findViewById(R.id.preview);
        mPreviewView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideViewBorder();
                checkToAddRectView();
            }
        });

        mTextSelectorPanel = (TextSelectorPanel) findViewById(R.id.text_selector_panel);
        mTextSelectorPanel.setOnTextSelectorListener(new TextSelectorPanel.OnTextSelectorListener() {
            @Override
            public void onTextSelected(StrokedTextView textView) {
                addText(textView);
            }

            @Override
            public void onViewClosed() {
                setPanelVisibility(mTextSelectorPanel, false);
            }
        });

        mPaintSelectorPanel = (PaintSelectorPanel) findViewById(R.id.paint_selector_panel);
        mPaintSelectorPanel.setOnPaintSelectorListener(new PaintSelectorPanel.OnPaintSelectorListener() {
            @Override
            public void onViewClosed() {
                setPanelVisibility(mPaintSelectorPanel, false);

                mPaintView.setPaintEnable(false);
            }

            @Override
            public void onPaintColorSelected(int color) {
                mPaintView.setPaintColor(color);
            }

            @Override
            public void onPaintSizeSelected(int size) {
                mPaintView.setPaintSize(size);
            }

            @Override
            public void onPaintUndoSelected() {
                mPaintView.undo();
            }

            @Override
            public void onPaintClearSelected() {
                mPaintView.clear();
            }
        });

        mProcessingDialog = new CustomProgressDialog(this);
        mProcessingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mShortVideoEditor.cancelSave();
            }
        });
    }

    /**
     * 启动预览
     */
    private void startPlayback() {
        if (mShortVideoEditorStatus == PLShortVideoEditorStatus.Idle) {
            mShortVideoEditor.startPlayback();
            mShortVideoEditorStatus = PLShortVideoEditorStatus.Playing;
        } else if (mShortVideoEditorStatus == PLShortVideoEditorStatus.Paused) {
            mShortVideoEditor.resumePlayback();
            mShortVideoEditorStatus = PLShortVideoEditorStatus.Playing;
        }
        mPausePalybackButton.setImageResource(R.mipmap.btn_pause);
    }

    /**
     * 停止预览
     */
    private void stopPlayback() {
        mShortVideoEditor.stopPlayback();
        mShortVideoEditorStatus = PLShortVideoEditorStatus.Idle;
        mPausePalybackButton.setImageResource(R.mipmap.btn_play);
    }

    /**
     * 暂停预览
     */
    private void pausePlayback() {
        mShortVideoEditor.pausePlayback();
        mShortVideoEditorStatus = PLShortVideoEditorStatus.Paused;
        mPausePalybackButton.setImageResource(R.mipmap.btn_play);
    }

    public void onClickShowSpeed(View view) {
        mSpeedPanel.setVisibility((mSpeedPanel.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE);
    }

    public void onSpeedClicked(View view) {
        mSpeedTextView.setTextColor(getResources().getColor(R.color.speedTextNormal));

        TextView textView = (TextView) view;
        textView.setTextColor(getResources().getColor(R.color.colorAccent));
        mSpeedTextView = textView;

        double recordSpeed = 1.0;
        switch (view.getId()) {
            case R.id.super_slow_speed_text:
                recordSpeed = RECORD_SPEED_ARRAY[0];
                break;
            case R.id.slow_speed_text:
                recordSpeed = RECORD_SPEED_ARRAY[1];
                break;
            case R.id.normal_speed_text:
                recordSpeed = RECORD_SPEED_ARRAY[2];
                break;
            case R.id.fast_speed_text:
                recordSpeed = RECORD_SPEED_ARRAY[3];
                break;
            case R.id.super_fast_speed_text:
                recordSpeed = RECORD_SPEED_ARRAY[4];
                break;
        }

        mShortVideoEditor.setSpeed(recordSpeed);
    }

    private void addSelectorView(View view) {
        View selectorView = mFrameListView.addSelectorView();
        view.setTag(R.id.selector_viewedit, selectorView);
    }


    private void initShortVideoEditor() {
//        mMp4path = getIntent().getStringExtra(MP4_PATH);
        mMp4path = "/storage/emulated/0/JWZTCBProduct/1539413241956.mp4";
        Log.i(TAG, "editing file: " + mMp4path);

        PLVideoEditSetting setting = new PLVideoEditSetting();
        setting.setSourceFilepath(mMp4path);
//        setting.setDestFilepath(Config.EDITED_FILE_PATH);
        setting.setDestFilepath(Config.VIDEO_STORAGE_DIR+"videos/"+System.currentTimeMillis()+".mp4");

        mShortVideoEditor = new PLShortVideoEditor(mPreviewView, setting);
        mShortVideoEditor.setVideoSaveListener(this);

        mMixDuration = mShortVideoEditor.getDurationMs();

        mFrameListView.setVideoPath(mMp4path);
        mFrameListView.setOnVideoFrameScrollListener(new FrameListViewEdit.OnVideoFrameScrollListener() {
            @Override
            public void onVideoFrameScrollChanged(long timeMs) {
                if (mShortVideoEditorStatus == PLShortVideoEditorStatus.Playing) {
                    pausePlayback();
                }
                mShortVideoEditor.seekTo((int) timeMs);
            }
        });

        initTimerTask();
    }

    private void initTimerTask() {
        mScrollTimerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mShortVideoEditorStatus == PLShortVideoEditorStatus.Playing) {
                            int position = mShortVideoEditor.getCurrentPosition();
                            mFrameListView.scrollToTime(position);
                        }
                    }
                });
            }
        };
        mScrollTimer = new Timer();
        // scroll fps:20
        mScrollTimer.schedule(mScrollTimerTask, 50, 50);
    }

    private void initWatermarkSetting() {
        mWatermarkSetting = new PLWatermarkSetting();
        mWatermarkSetting.setResourceId(R.drawable.touming);
        mWatermarkSetting.setPosition(0.01f, 0.01f);
        mWatermarkSetting.setAlpha(128);
    }


    private void checkToAddRectView() {
        if (mCurView != null) {
            View rectView = mFrameListView.addSelectedRect((View) mCurView.getTag(R.id.selector_viewedit));
            mCurView.setTag(R.id.rect_viewedit, rectView);
            FrameListViewEdit.SectionItem sectionItem = mFrameListView.getSectionByRectView(rectView);
            mShortVideoEditor.setViewTimeline(mCurView, sectionItem.getStartTime(), (sectionItem.getEndTime() - sectionItem.getStartTime()));
            mCurView = null;
        }
    }



    //添加字幕
    public void onClickTextSelect(View v) {
        rl_zimucontent.setVisibility(View.VISIBLE);
//        setPanelVisibility(mTextSelectorPanel, true);
    }

    //上一个
    public void onClickTextSelectUp(View v) {
        ToastUtils.s(VideoEditTextActivity.this,"敬请期待");
    }

    //下一个
    public void onClickTextSelectNext(View v) {
        ToastUtils.s(VideoEditTextActivity.this,"敬请期待");
    }

    //字幕取消
    public void zimuCanncel(View v) {
        rl_zimucontent.setVisibility(View.GONE);
    }

    //字幕确定
    public void zimuConfirm(View v) {
        rl_zimucontent.setVisibility(View.GONE);
        String text=et_zimu.getText().toString();
        if(IsNonEmptyUtils.isString(text)){
            et_zimu.setText(text);
        }else{
            et_zimu.setText("点击输入字幕内容");
        }

        et_zimu.setTextColor(getResources().getColor(R.color.text1));
        et_zimu.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);

        addText(et_zimu);
    }



    public void onClickBack(View v) {
        finish();
    }


    public void addText(StrokedTextView selectText) {
        checkToAddRectView();

        final StrokedTextView textView = new StrokedTextView(this);
        textView.setText(selectText.getText());
        textView.setTextSize(30);
        textView.setTypeface(selectText.getTypeface());
        textView.setTextColor(selectText.getTextColors());
        textView.setShadowLayer(selectText.getShadowRadius(), selectText.getShadowDx(), selectText.getShadowDy(), selectText.getShadowColor());
        textView.setAlpha(selectText.getAlpha());
        textView.setStrokeWidth(selectText.getStrokeWidth());
        textView.setStrokeColor(selectText.getStrokeColor());

        mShortVideoEditor.addTextView(textView);

        addSelectorView(textView);

        showTextViewBorder(textView);
        textView.setOnTouchListener(new ViewTouchListener(textView));//点击弹出输入框重新修改输入的文字

        ToastUtils.s(this, "触摸文字右下角控制缩放与旋转，双击移除。");
    }

    private void createTextDialog(final PLTextView textView) {
        final EditText edit = new EditText(VideoEditTextActivity.this);
        edit.setText(textView.getText());

        AlertDialog.Builder builder = new AlertDialog.Builder(VideoEditTextActivity.this);
        builder.setView(edit);
        builder.setTitle("请输入文字");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                textView.setText(edit.getText());
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void hideViewBorder() {
        if (mCurTextView != null) {
            mCurTextView.setBackgroundResource(0);
            mCurTextView = null;
        }

        if (mCurImageView != null) {
            mCurImageView.setBackgroundResource(0);
            mCurImageView = null;
        }
    }

    private void showTextViewBorder(PLTextView textView) {
        hideViewBorder();
        mCurTextView = textView;
        mCurTextView.setBackgroundResource(R.drawable.border_text_view);

        mCurView = textView;

        FrameSelectorViewEdit selectorView = (FrameSelectorViewEdit) mCurView.getTag(R.id.selector_viewedit);
        selectorView.setVisibility(View.VISIBLE);

        View rectView = (View) mCurView.getTag(R.id.rect_viewedit);
        if (rectView != null) {
            mFrameListView.showSelectorByRectView(selectorView, rectView);
            mFrameListView.removeRectView(rectView);
            mShortVideoEditor.setViewTimeline(mCurView, 0, mShortVideoEditor.getDurationMs());
        }

        pausePlayback();
    }

    private void showImageViewBorder(PLImageView imageView) {
        hideViewBorder();
        mCurImageView = imageView;
        mCurImageView.setBackgroundResource(R.drawable.border_text_view);

        mCurView = imageView;

        FrameSelectorViewEdit selectorView = (FrameSelectorViewEdit) mCurView.getTag(R.id.selector_viewedit);
        selectorView.setVisibility(View.VISIBLE);

        View rectView = (View) mCurView.getTag(R.id.rect_viewedit);
        if (rectView != null) {
            mFrameListView.showSelectorByRectView(selectorView, rectView);
            mFrameListView.removeRectView(rectView);
            mShortVideoEditor.setViewTimeline(mCurView, 0, mShortVideoEditor.getDurationMs());
        }

        pausePlayback();
    }

    private class ViewTouchListener implements View.OnTouchListener {
        private float lastTouchRawX;
        private float lastTouchRawY;
        private boolean scale;
        private boolean isViewMoved;
        private View mView;

        public ViewTouchListener(View view) {
            mView = view;
        }

        GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (mView instanceof PLTextView) {
                    mShortVideoEditor.removeTextView((PLTextView) mView);
                    if (mCurTextView != null) {
                        mCurTextView = null;
                    }
                } else if (mView instanceof PLImageView) {
                    mShortVideoEditor.removeImageView((PLImageView) mView);
                    if (mCurImageView != null) {
                        mCurImageView = null;
                    }
                }

                View rectView = (View) mView.getTag(R.id.rect_viewedit);
                if (rectView != null) {
                    mFrameListView.removeRectView((View) mView.getTag(R.id.rect_viewedit));
                }
                FrameSelectorViewEdit selectorView = (FrameSelectorViewEdit) mView.getTag(R.id.selector_viewedit);
                if (selectorView != null) {
                    mFrameListView.removeSelectorViewEdit(selectorView);
                }
                mCurView = null;
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (isViewMoved) {
                    return true;
                }
                if (mView instanceof PLTextView) {
                    createTextDialog((PLTextView) mView);
                }
                return true;
            }
        };
        final GestureDetector gestureDetector = new GestureDetector(VideoEditTextActivity.this, simpleOnGestureListener);

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (gestureDetector.onTouchEvent(event)) {
                return true;
            }

            int action = event.getAction();
            float touchRawX = event.getRawX();
            float touchRawY = event.getRawY();
            float touchX = event.getX();
            float touchY = event.getY();

            if (action == MotionEvent.ACTION_DOWN) {
                boolean xOK = touchX >= v.getWidth() * 3 / 4 && touchX <= v.getWidth();
                boolean yOK = touchY >= v.getHeight() * 2 / 4 && touchY <= v.getHeight();
                scale = xOK && yOK;

                if (mCurView != v) {
                    checkToAddRectView();
                }
                if (v instanceof PLTextView) {
                    showTextViewBorder((PLTextView) v);
                } else if (v instanceof PLImageView) {
                    showImageViewBorder((PLImageView) v);
                }
            }

            if (action == MotionEvent.ACTION_MOVE) {
                float deltaRawX = touchRawX - lastTouchRawX;
                float deltaRawY = touchRawY - lastTouchRawY;

                if (scale) {
                    // rotate
                    float centerX = v.getX() + (float) v.getWidth() / 2;
                    float centerY = v.getY() + (float) v.getHeight() / 2;
                    double angle = Math.atan2(touchRawY - centerY, touchRawX - centerX) * 180 / Math.PI;
                    v.setRotation((float) angle - 45);

                    // scale
                    float xx = (touchRawX >= centerX ? deltaRawX : -deltaRawX);
                    float yy = (touchRawY >= centerY ? deltaRawY : -deltaRawY);
                    float sf = (v.getScaleX() + xx / v.getWidth() + v.getScaleY() + yy / v.getHeight()) / 2;
                    v.setScaleX(sf);
                    v.setScaleY(sf);
                } else {
                    // translate
                    v.setTranslationX(v.getTranslationX() + deltaRawX);
                    v.setTranslationY(v.getTranslationY() + deltaRawY);
                }
                isViewMoved = true;
            }

            if (action == MotionEvent.ACTION_UP) {
                isViewMoved = false;
            }

            lastTouchRawX = touchRawX;
            lastTouchRawY = touchRawY;
            return true;
        }
    }

    public void onClickShowFilters(View v) {
        setPanelVisibility(mFiltersList, true);

        mFiltersList.setAdapter(new FilterListAdapter(mShortVideoEditor.getBuiltinFilterList()));
    }


    public void onClickShowPaint(View v) {
        setPanelVisibility(mPaintSelectorPanel, true);

        if (mPaintView == null) {
            mPaintView = new PLPaintView(this, mPreviewView.getWidth(), mPreviewView.getHeight());
            mShortVideoEditor.addPaintView(mPaintView);
        }
        mPaintView.setPaintEnable(true);
        mPaintSelectorPanel.setup();
    }

    private void setPanelVisibility(View panel, boolean isVisible) {
        setPanelVisibility(panel, isVisible, false);
    }

    private void setPanelVisibility(View panel, boolean isVisible, boolean isEffect) {
        if (panel instanceof TextSelectorPanel || panel instanceof PaintSelectorPanel) {
            if (isVisible) {
                panel.setVisibility(View.VISIBLE);
//                mVisibleView = mImageSelectorPanel.getVisibility() == View.VISIBLE ? mImageSelectorPanel : mFiltersList;
//                mVisibleView.setVisibility(View.GONE);
            } else {
                panel.setVisibility(View.GONE);
//                mVisibleView.setVisibility(View.VISIBLE);
            }
        } else {
            if (isVisible) {
//                mImageSelectorPanel.setVisibility(View.GONE);
                mFiltersList.setVisibility(View.GONE);
            }
            panel.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }

    public void onClickShowMVs(View v) {
        setPanelVisibility(mFiltersList, true);
        try {
            File dir = new File(Environment.getExternalStorageDirectory() + "/ShortVideo/mvs");
            // copy mv assets to sdcard
            if (!dir.exists()) {
                dir.mkdirs();
                String[] fs = getAssets().list("mvs");
                for (String file : fs) {
                    InputStream is = getAssets().open("mvs/" + file);
                    FileOutputStream fos = new FileOutputStream(new File(dir, file));
                    byte[] buffer = new byte[1024];
                    int byteCount;
                    while ((byteCount = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, byteCount);
                    }
                    fos.flush();
                    is.close();
                    fos.close();
                }
            }

            FileReader jsonFile = new FileReader(new File(dir, "plsMVs.json"));
            StringBuilder sb = new StringBuilder();
            int read;
            char[] buf = new char[2048];
            while ((read = jsonFile.read(buf, 0, 2048)) != -1) {
                sb.append(buf, 0, read);
            }
            Log.i(TAG, sb.toString());
            JSONObject json = new JSONObject(sb.toString());
            mFiltersList.setAdapter(new MVListAdapter(json.getJSONArray("MVs")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClickTogglePlayback(View v) {
        if (mShortVideoEditorStatus == PLShortVideoEditorStatus.Playing) {
            pausePlayback();
        } else {
            startPlayback();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_PICK_AUDIO_MIX_FILE) {
            String selectedFilepath = GetPathFromUri.getPath(this, data.getData());
            Log.i(TAG, "Select file: " + selectedFilepath);
            if (!TextUtils.isEmpty(selectedFilepath)) {
                mShortVideoEditor.setAudioMixFile(selectedFilepath);
//                mAudioMixSettingDialog.setMixMaxPosition(mShortVideoEditor.getAudioMixFileDuration());
                mIsMixAudio = true;
            }
        } else if (requestCode == REQUEST_CODE_DUB) {
//            String dubMp4Path = data.getStringExtra(VideoDubActivity.DUB_MP4_PATH);
//            if (!TextUtils.isEmpty(dubMp4Path)) {
//                finish();
//                VideoEditTextActivity.start(this, dubMp4Path);
//            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPlayback();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mShortVideoEditor.setBuiltinFilter(mSelectedFilter);
        mShortVideoEditor.setMVEffect(mSelectedMV, mSelectedMask);
        mShortVideoEditor.setWatermark(mIsUseWatermark ? mWatermarkSetting : null);
        startPlayback();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mScrollTimer != null) {
            mScrollTimer.cancel();
            mScrollTimer = null;
        }
        if (mScrollTimerTask != null) {
            mScrollTimerTask.cancel();
            mScrollTimerTask = null;
        }
    }

    public void onSaveEdit(View v) {
        checkToAddRectView();
        mProcessingDialog.show();
        mShortVideoEditor.save();
        hideViewBorder();
    }

    @Override
    public void onSaveVideoSuccess(String filePath) {
        //编辑完成后返回的路径
        Log.i(TAG, "save edit success filePath: " + filePath);
        mProcessingDialog.dismiss();
        VideoEditTextActivity.this.finish();
//        PlaybackActivity.start(VideoEditTextActivity.this, filePath);
    }

    @Override
    public void onSaveVideoFailed(final int errorCode) {
        Log.e(TAG, "save edit failed errorCode:" + errorCode);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProcessingDialog.dismiss();
                ToastUtils.toastErrorCode(VideoEditTextActivity.this, errorCode);
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

    private class FilterItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView mIcon;
        public TextView mName;

        public FilterItemViewHolder(View itemView) {
            super(itemView);
            mIcon = (ImageView) itemView.findViewById(R.id.icon);
            mName = (TextView) itemView.findViewById(R.id.name);
        }
    }

    private class FilterListAdapter extends RecyclerView.Adapter<FilterItemViewHolder> {
        private PLBuiltinFilter[] mFilters;

        public FilterListAdapter(PLBuiltinFilter[] filters) {
            this.mFilters = filters;
        }

        @Override
        public FilterItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View contactView = inflater.inflate(R.layout.filter_item, parent, false);
            FilterItemViewHolder viewHolder = new FilterItemViewHolder(contactView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(FilterItemViewHolder holder, int position) {
            try {
                if (position == 0) {
                    holder.mName.setText("None");
                    Bitmap bitmap = BitmapFactory.decodeStream(getAssets().open("filters/none.png"));
                    holder.mIcon.setImageBitmap(bitmap);
                    holder.mIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mSelectedFilter = null;
                            mShortVideoEditor.setBuiltinFilter(null);
                        }
                    });
                    return;
                }

                final PLBuiltinFilter filter = mFilters[position - 1];
                holder.mName.setText(filter.getName());
                InputStream is = getAssets().open(filter.getAssetFilePath());
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                holder.mIcon.setImageBitmap(bitmap);
                holder.mIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSelectedFilter = filter.getName();
                        mShortVideoEditor.setBuiltinFilter(mSelectedFilter);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mFilters != null ? mFilters.length + 1 : 0;
        }
    }

    private class MVListAdapter extends RecyclerView.Adapter<FilterItemViewHolder> {
        private JSONArray mMVArray;

        public MVListAdapter(JSONArray mvArray) {
            this.mMVArray = mvArray;
        }

        @Override
        public FilterItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View contactView = inflater.inflate(R.layout.filter_item, parent, false);
            FilterItemViewHolder viewHolder = new FilterItemViewHolder(contactView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(FilterItemViewHolder holder, int position) {
            final String mvsDir = Config.VIDEO_STORAGE_DIR+"videos/" + "mvs/";

            try {
                if (position == 0) {
                    holder.mName.setText("None");
                    Bitmap bitmap = BitmapFactory.decodeFile(mvsDir + "none.png");
                    holder.mIcon.setImageBitmap(bitmap);
                    holder.mIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mSelectedMV = null;
                            mSelectedMask = null;
                            mShortVideoEditor.setMVEffect(null, null);
                        }
                    });
                    return;
                }

                final JSONObject mv = mMVArray.getJSONObject(position - 1);
                holder.mName.setText(mv.getString("name"));
                Bitmap bitmap = BitmapFactory.decodeFile(mvsDir + mv.getString("coverDir") + ".png");
                holder.mIcon.setImageBitmap(bitmap);
                holder.mIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            mSelectedMV = mvsDir + mv.getString("colorDir") + ".mp4";
                            mSelectedMask = mvsDir + mv.getString("alphaDir") + ".mp4";
                            mShortVideoEditor.setMVEffect(mSelectedMV, mSelectedMask);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mMVArray != null ? mMVArray.length() + 1 : 0;
        }
    }

    private AudioMixSettingDialog.OnAudioVolumeChangedListener mOnAudioVolumeChangedListener = new AudioMixSettingDialog.OnAudioVolumeChangedListener() {
        @Override
        public void onAudioVolumeChanged(int fgVolume, int bgVolume) {
            Log.i(TAG, "fg volume: " + fgVolume + " bg volume: " + bgVolume);
            mShortVideoEditor.setAudioMixVolume(fgVolume / 100f, bgVolume / 100f);
            mIsMuted = fgVolume == 0;
        }
    };

    private AudioMixSettingDialog.OnPositionSelectedListener mOnPositionSelectedListener = new AudioMixSettingDialog.OnPositionSelectedListener() {
        @Override
        public void onPositionSelected(long position) {
            Log.i(TAG, "selected position: " + position);
            mShortVideoEditor.setAudioMixFileRange(position, position + mMixDuration);
        }
    };
}
