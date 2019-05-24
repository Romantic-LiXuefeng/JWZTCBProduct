package com.jwzt.caibian.activity;

import static com.jwzt.caibian.util.RecordSettings.RECORD_SPEED_ARRAY;

import java.util.Stack;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.SensorManager;
import android.media.AudioFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jwzt.caibian.bean.ShortVideoConfig;
import com.jwzt.caibian.util.BitmapUtils;
import com.jwzt.caibian.util.Config;
import com.jwzt.caibian.util.GetPathFromUri;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.RecordSettings;
import com.jwzt.caibian.util.SPConstant;
import com.jwzt.caibian.util.ToastUtils;
import com.jwzt.caibian.view.CustomProgressDialog;
import com.jwzt.caibian.view.FocusIndicator;
import com.jwzt.caibian.view.SectionProgressBar;
import com.jwzt.caibian.widget.SelectPicDialog;
import com.jwzt.cb.product.R;
import com.qiniu.pili.droid.shortvideo.PLAudioEncodeSetting;
import com.qiniu.pili.droid.shortvideo.PLCameraSetting;
import com.qiniu.pili.droid.shortvideo.PLDraft;
import com.qiniu.pili.droid.shortvideo.PLDraftBox;
import com.qiniu.pili.droid.shortvideo.PLFaceBeautySetting;
import com.qiniu.pili.droid.shortvideo.PLFocusListener;
import com.qiniu.pili.droid.shortvideo.PLMicrophoneSetting;
import com.qiniu.pili.droid.shortvideo.PLRecordSetting;
import com.qiniu.pili.droid.shortvideo.PLRecordStateListener;
import com.qiniu.pili.droid.shortvideo.PLShortVideoRecorder;
import com.qiniu.pili.droid.shortvideo.PLVideoEncodeSetting;
import com.qiniu.pili.droid.shortvideo.PLVideoSaveListener;


public class VideoRecordActivity extends Activity implements PLRecordStateListener,
        PLVideoSaveListener,
        PLFocusListener,
        View.OnClickListener {
    private static final String TAG = "VideoRecordActivity";
    public static final String PREVIEW_SIZE_RATIO = "PreviewSizeRatio";
    public static final String PREVIEW_SIZE_LEVEL = "PreviewSizeLevel";
    public static final String ENCODING_MODE = "EncodingMode";
    public static final String ENCODING_SIZE_LEVEL = "EncodingSizeLevel";
    public static final String ENCODING_BITRATE_LEVEL = "EncodingBitrateLevel";
    public static final String AUDIO_CHANNEL_NUM = "AudioChannelNum";
    public static final String DRAFT = "draft";

    /*** NOTICE: TUSDK needs extra cost*/
    private static final boolean USE_TUSDK = true;

    private PLShortVideoRecorder mShortVideoRecorder;

    private SectionProgressBar mSectionProgressBar;
    private CustomProgressDialog mProcessingDialog;
    private ImageView mRecordBtn;
    private View mDeleteBtn;
    private View mConcatBtn;
    private View mSwitchCameraBtn;
    private View mSwitchFlashBtn;
    private ImageView back, clock;
    private TextView tv_resolution;
    private RelativeLayout rl_root;
    private FocusIndicator mFocusIndicator;
    private SeekBar mAdjustBrightnessSeekBar;
    private GLSurfaceView preview;
    private LinearLayout ll_top,ll_control;
    private TextView tv_cutvideo, tv_addheadend, tv_addtext, tv_mergevideo, tv_addaudio, tv_addlogo;

    private boolean mFlashEnabled;
    private boolean mIsEditVideo = false;

    private GestureDetector mGestureDetector;

    private PLCameraSetting mCameraSetting;
    private PLMicrophoneSetting mMicrophoneSetting;
    private PLRecordSetting mRecordSetting;
    private PLVideoEncodeSetting mVideoEncodeSetting;
    private PLAudioEncodeSetting mAudioEncodeSetting;
    private PLFaceBeautySetting mFaceBeautySetting;

    private int mFocusIndicatorX;
    private int mFocusIndicatorY;

    private double mRecordSpeed;

    private Stack<Long> mDurationRecordStack = new Stack();
    private Stack<Double> mDurationVideoStack = new Stack();

    private OrientationEventListener mOrientationListener;
    private boolean mSectionBegan;

    private SelectPicDialog selectPicDialog;
    private long mSectionBeginTSMs;
    private boolean isRecord;//标识是否在录制true表示在录制
    private String saveFile;//保存录制地址

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1://裁剪
                    if(IsNonEmptyUtils.isString(saveFile)){
                        Intent intent=new Intent(VideoRecordActivity.this,VideoDivideActivity.class);
                        intent.putExtra("vedio_path", saveFile);
                        startActivity(intent);
                    }else{
                        ToastUtils.s(VideoRecordActivity.this,"请先保存视频");
                    }
                    VideoRecordActivity.this.finish();
                    break;
                case 2://片头片尾
                    if(IsNonEmptyUtils.isString(saveFile)){
                        Intent intent=new Intent(VideoRecordActivity.this,VideoAddHeadEndActivity.class);
                        intent.putExtra("vedio_path", saveFile);
                        startActivity(intent);
                    }else{
                        ToastUtils.s(VideoRecordActivity.this,"请先保存视频");
                    }
                    VideoRecordActivity.this.finish();
                    break;
                case 3://加字幕
                    if(IsNonEmptyUtils.isString(saveFile)){
                        Intent intent=new Intent(VideoRecordActivity.this,VideoEditTextActivity.class);
                        intent.putExtra("vedio_path", saveFile);
                        startActivity(intent);
                    }else{
                        ToastUtils.s(VideoRecordActivity.this,"请先保存视频");
                    }
                    VideoRecordActivity.this.finish();
                    break;
                case 4://合并
                    if(IsNonEmptyUtils.isString(saveFile)){
                        Intent intent=new Intent(VideoRecordActivity.this,VideoEditMergerActivity.class);
                        startActivity(intent);
                    }else{
                        ToastUtils.s(VideoRecordActivity.this,"请先保存视频");
                    }
                    VideoRecordActivity.this.finish();
                    break;
                case 5://添加叠音
                    if(IsNonEmptyUtils.isString(saveFile)){
                        Intent intent=new Intent(VideoRecordActivity.this,VideoEditAudioActivity.class);
                        intent.putExtra("vedio_path", saveFile);
                        startActivity(intent);
                    }else{
                        ToastUtils.s(VideoRecordActivity.this,"请先保存视频");
                    }
                    VideoRecordActivity.this.finish();
                    break;
                case 6://添加logo

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_record);

        findView();
        initView();
    }

    //实例化各个控价
    private void findView() {
        rl_root=(RelativeLayout) findViewById(R.id.rl_root);
        ll_top = findViewById(R.id.ll_top);
        mSectionProgressBar = (SectionProgressBar) findViewById(R.id.record_progressbar);
        preview = (GLSurfaceView) findViewById(R.id.preview);
        mRecordBtn = findViewById(R.id.record);
        mDeleteBtn = findViewById(R.id.delete);
        mConcatBtn = findViewById(R.id.concat);
        back = this.findViewById(R.id.iv_back);
        mSwitchCameraBtn = findViewById(R.id.switch_camera);
        mSwitchFlashBtn = findViewById(R.id.switch_flash);
        clock = (ImageView) this.findViewById(R.id.iv_clock);
        clock.setOnClickListener(this);
        tv_resolution = (TextView) this.findViewById(R.id.tv_resolution);
        mFocusIndicator = (FocusIndicator) findViewById(R.id.focus_indicator);
        mAdjustBrightnessSeekBar = (SeekBar) findViewById(R.id.adjust_brightness);

        ll_control=findViewById(R.id.ll_control);
        tv_cutvideo = findViewById(R.id.tv_cutvideo);
        tv_addheadend = findViewById(R.id.tv_addheadend);
        tv_addtext = findViewById(R.id.tv_addtext);
        tv_mergevideo = findViewById(R.id.tv_mergevideo);
        tv_addaudio = findViewById(R.id.tv_addaudio);
        tv_addlogo = findViewById(R.id.tv_addlogo);

        ll_control.setOnClickListener(this);
        tv_cutvideo.setOnClickListener(this);
        tv_addheadend.setOnClickListener(this);
        tv_addtext.setOnClickListener(this);
        tv_mergevideo.setOnClickListener(this);
        tv_addaudio.setOnClickListener(this);
        tv_addlogo.setOnClickListener(this);
    }

    //初始化各种配置
    private void initView() {
        mProcessingDialog = new CustomProgressDialog(this);
        mProcessingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mShortVideoRecorder.cancelConcat();
            }
        });

        mShortVideoRecorder = new PLShortVideoRecorder();
        mShortVideoRecorder.setRecordStateListener(this);
        mShortVideoRecorder.setFocusListener(this);


        mRecordSpeed = RECORD_SPEED_ARRAY[2];

        String draftTag = getIntent().getStringExtra(DRAFT);
        if (draftTag == null) {
            int previewSizeRatioPos = getIntent().getIntExtra(PREVIEW_SIZE_RATIO, 0);
            int previewSizeLevelPos = getIntent().getIntExtra(PREVIEW_SIZE_LEVEL, 0);
            int encodingModePos = getIntent().getIntExtra(ENCODING_MODE, 0);
            int encodingSizeLevelPos = getIntent().getIntExtra(ENCODING_SIZE_LEVEL, 0);
            int encodingBitrateLevelPos = getIntent().getIntExtra(ENCODING_BITRATE_LEVEL, 0);
            int audioChannelNumPos = getIntent().getIntExtra(AUDIO_CHANNEL_NUM, 0);

            mCameraSetting = new PLCameraSetting();
            PLCameraSetting.CAMERA_FACING_ID facingId = chooseCameraFacingId();
            mCameraSetting.setCameraId(facingId);
            mCameraSetting.setCameraPreviewSizeRatio(RecordSettings.PREVIEW_SIZE_RATIO_ARRAY[previewSizeRatioPos]);
//            mCameraSetting.setCameraPreviewSizeLevel(RecordSettings.PREVIEW_SIZE_LEVEL_ARRAY[previewSizeLevelPos]);
            mCameraSetting.setCameraPreviewSizeLevel(RecordSettings.PREVIEW_SIZE_LEVEL_ARRAY[2]);

            mMicrophoneSetting = new PLMicrophoneSetting();
            mMicrophoneSetting.setChannelConfig(RecordSettings.AUDIO_CHANNEL_NUM_ARRAY[audioChannelNumPos] == 1 ?
                    AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO);

            mVideoEncodeSetting = new PLVideoEncodeSetting(this);
//            mVideoEncodeSetting.setEncodingSizeLevel(RecordSettings.ENCODING_SIZE_LEVEL_ARRAY[encodingSizeLevelPos]);
            mVideoEncodeSetting.setEncodingSizeLevel(RecordSettings.ENCODING_SIZE_LEVEL_ARRAY[8]);
//            mVideoEncodeSetting.setEncodingBitrate(RecordSettings.ENCODING_BITRATE_LEVEL_ARRAY[encodingBitrateLevelPos]);
            mVideoEncodeSetting.setEncodingBitrate(RecordSettings.ENCODING_BITRATE_LEVEL_ARRAY[1]);
            mVideoEncodeSetting.setHWCodecEnabled(encodingModePos == 0);
            mVideoEncodeSetting.setConstFrameRateEnabled(true);

            mAudioEncodeSetting = new PLAudioEncodeSetting();
            mAudioEncodeSetting.setHWCodecEnabled(encodingModePos == 0);
            mAudioEncodeSetting.setChannels(RecordSettings.AUDIO_CHANNEL_NUM_ARRAY[audioChannelNumPos]);

            mRecordSetting = new PLRecordSetting();
            mRecordSetting.setMaxRecordDuration(RecordSettings.DEFAULT_MAX_RECORD_DURATION);//设置最大拍摄时长
            mRecordSetting.setRecordSpeedVariable(true);
            mRecordSetting.setVideoCacheDir(Config.VIDEO_STORAGE_DIR+"videos/");
//            mRecordSetting.setVideoFilepath(Config.RECORD_FILE_PATH);//视频录制路径
            mRecordSetting.setVideoFilepath(Config.VIDEO_STORAGE_DIR +"videos/"+ System.currentTimeMillis() + ".mp4");//视频录制路径

            mFaceBeautySetting = new PLFaceBeautySetting(1.0f, 0.5f, 0.5f);

            mShortVideoRecorder.prepare(preview, mCameraSetting, mMicrophoneSetting, mVideoEncodeSetting,
                    mAudioEncodeSetting, USE_TUSDK ? null : mFaceBeautySetting, mRecordSetting);
            mSectionProgressBar.setFirstPointTime(RecordSettings.DEFAULT_MIN_RECORD_DURATION);
            onSectionCountChanged(0, 0);
        } else {
            PLDraft draft = PLDraftBox.getInstance(this).getDraftByTag(draftTag);
            if (draft == null) {
                ToastUtils.s(this, getString(R.string.toast_draft_recover_fail));
                finish();
            }

            mCameraSetting = draft.getCameraSetting();
            mMicrophoneSetting = draft.getMicrophoneSetting();
            mVideoEncodeSetting = draft.getVideoEncodeSetting();
            mAudioEncodeSetting = draft.getAudioEncodeSetting();
            mRecordSetting = draft.getRecordSetting();
            mFaceBeautySetting = draft.getFaceBeautySetting();

            if (mShortVideoRecorder.recoverFromDraft(preview, draft)) {
                long draftDuration = 0;
                for (int i = 0; i < draft.getSectionCount(); ++i) {
                    long currentDuration = draft.getSectionDuration(i);
                    draftDuration += draft.getSectionDuration(i);
                    onSectionIncreased(currentDuration, draftDuration, i + 1);
                    if (!mDurationRecordStack.isEmpty()) {
                        mDurationRecordStack.pop();
                    }
                }
                mSectionProgressBar.setFirstPointTime(draftDuration);
                ToastUtils.s(this, getString(R.string.toast_draft_recover_success));
            } else {
                onSectionCountChanged(0, 0);
                mSectionProgressBar.setFirstPointTime(RecordSettings.DEFAULT_MIN_RECORD_DURATION);
                ToastUtils.s(this, getString(R.string.toast_draft_recover_fail));
            }
        }
        mShortVideoRecorder.setRecordSpeed(mRecordSpeed);
        mSectionProgressBar.setProceedingSpeed(mRecordSpeed);
        mSectionProgressBar.setTotalTime(this, mRecordSetting.getMaxRecordDuration());

        mShortVideoRecorder.switchCamera();
        mFocusIndicator.focusCancel();
        mFlashEnabled = !mFlashEnabled;
        mShortVideoRecorder.setFlashEnabled(mFlashEnabled);
        mSwitchFlashBtn.setActivated(mFlashEnabled);

        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                mFocusIndicatorX = (int) e.getX() - mFocusIndicator.getWidth() / 2;
                mFocusIndicatorY = (int) e.getY() - mFocusIndicator.getHeight() / 2;
                mShortVideoRecorder.manualFocus(mFocusIndicator.getWidth(), mFocusIndicator.getHeight(), (int) e.getX(), (int) e.getY());
                return false;
            }
        });
        preview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mGestureDetector.onTouchEvent(motionEvent);
                return true;
            }
        });

        mOrientationListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {
                int rotation = getScreenRotation(orientation);
                if (!mSectionProgressBar.isRecorded() && !mSectionBegan) {
                    mVideoEncodeSetting.setRotationInMetadata(rotation);
                }
            }
        };
        if (mOrientationListener.canDetectOrientation()) {
            mOrientationListener.enable();
        }
    }

    private int getScreenRotation(int orientation) {
        int screenRotation = 0;
        boolean isPortraitScreen = getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        if (orientation >= 315 || orientation < 45) {
            screenRotation = isPortraitScreen ? 0 : 90;
        } else if (orientation >= 45 && orientation < 135) {
            screenRotation = isPortraitScreen ? 90 : 180;
        } else if (orientation >= 135 && orientation < 225) {
            screenRotation = isPortraitScreen ? 180 : 270;
        } else if (orientation >= 225 && orientation < 315) {
            screenRotation = isPortraitScreen ? 270 : 0;
        }
        return screenRotation;
    }

    private void updateRecordingBtns(boolean isRecording) {
        mSwitchCameraBtn.setEnabled(!isRecording);
        mRecordBtn.setActivated(isRecording);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRecordBtn.setEnabled(false);
        mShortVideoRecorder.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateRecordingBtns(false);
        mShortVideoRecorder.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mShortVideoRecorder.destroy();
        mOrientationListener.disable();
    }

    //回删视频片段
    public void onClickDelete(View v) {
        if (!mShortVideoRecorder.deleteLastSection()) {
            ToastUtils.s(this, "回删视频段失败");
        }
    }

    //保存
    public void onClickConcat(View v) {
        new PopupWindows(VideoRecordActivity.this,rl_root);
//        mProcessingDialog.show();
//        mShortVideoRecorder.concatSections(VideoRecordActivity.this);
    }

    private String newName;
    public class PopupWindows extends PopupWindow {

        private EditText reName;

        public PopupWindows(Context mContext, View parent){
            View view = View.inflate(mContext, R.layout.layout_save_video_info, null);
            view.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_ins));
            LinearLayout ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
            View ll_save =  view.findViewById(R.id.tv_save);
            View ll_edit =  view.findViewById(R.id.tv_edit);
            View ll_upload = view.findViewById(R.id.tv_upload);

            ll_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    isUpload=false;
//                    newName = reName.getText().toString();
//                    String workingPath=getRecordFileFolder()+"";
//                    new RecordActivity.MergeVideos(workingPath, videosToMerge).execute();//开始合并操作
                    mProcessingDialog.show();
//                    ll_top.setVisibility(View.VISIBLE);
                    mShortVideoRecorder.concatSections(VideoRecordActivity.this);
                    dismiss();
                }
            });

            ll_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mProcessingDialog.show();
                    ll_top.setVisibility(View.VISIBLE);
                    mShortVideoRecorder.concatSections(VideoRecordActivity.this);
                    dismiss();
                }
            });

            ll_upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mProcessingDialog.show();
                    mShortVideoRecorder.concatSections(VideoRecordActivity.this);
//                    isUpload=true;
//                    newName = reName.getText().toString();
//                    if(newName!=null&&!"".equals(newName)){
//
//                    }else{
//                        newName=System.currentTimeMillis()+"";
//                    }
//                    String workingPath=getRecordFileFolder()+"";
//                    new RecordActivity.MergeVideos(workingPath, videosToMerge).execute();//开始合并操作
                    dismiss();
                }
            });

            reName = (EditText)view.findViewById(R.id.et_video_name);
            reName.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before,int count) {
                    String content=reName.getText().toString();
                    System.out.println("contentcontent:"+content);
                    if(IsNonEmptyUtils.isString(content)){
                        content.replaceAll("\n", "");
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,int after) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                }
            });


            ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.push_bottom_in_2));
            setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            setFocusable(true);
            setOutsideTouchable(true);

            setContentView(view);
            showAtLocation(parent, Gravity.BOTTOM, 0, 0);
            update();

        }
    }



    //开始结束录制
    public void onClickRecord(View v) {
        if (isRecord) {
            isRecord = false;
            if (mSectionBegan) {
                long sectionRecordDurationMs = System.currentTimeMillis() - mSectionBeginTSMs;
                long totalRecordDurationMs = sectionRecordDurationMs + (mDurationRecordStack.isEmpty() ? 0 : mDurationRecordStack.peek().longValue());
                double sectionVideoDurationMs = sectionRecordDurationMs / mRecordSpeed;
                double totalVideoDurationMs = sectionVideoDurationMs + (mDurationVideoStack.isEmpty() ? 0 : mDurationVideoStack.peek().doubleValue());
                mDurationRecordStack.push(new Long(totalRecordDurationMs));
                mDurationVideoStack.push(new Double(totalVideoDurationMs));
                if (mRecordSetting.IsRecordSpeedVariable()) {
                    Log.d(TAG, "SectionRecordDuration: " + sectionRecordDurationMs + "; sectionVideoDuration: " + sectionVideoDurationMs + "; totalVideoDurationMs: " + totalVideoDurationMs + "Section count: " + mDurationVideoStack.size());
                    mSectionProgressBar.addBreakPointTime((long) totalVideoDurationMs);
                } else {
                    mSectionProgressBar.addBreakPointTime(totalRecordDurationMs);
                }

                mSectionProgressBar.setCurrentState(SectionProgressBar.State.PAUSE);
                mShortVideoRecorder.endSection();
                mSectionBegan = false;
                mRecordBtn.getDrawable().setLevel(1);
            }
        } else {
            isRecord = true;
            if (!mSectionBegan && mShortVideoRecorder.beginSection()) {
                ll_top.setVisibility(View.GONE);
                mSectionBegan = true;
                mSectionBeginTSMs = System.currentTimeMillis();
                mSectionProgressBar.setCurrentState(SectionProgressBar.State.START);
                updateRecordingBtns(true);
                mRecordBtn.getDrawable().setLevel(2);
            } else {
                ToastUtils.s(VideoRecordActivity.this, "无法开始视频段录制");
            }
        }
    }

    //亮度调节
    public void onClickBrightness(View v) {
        boolean isVisible = mAdjustBrightnessSeekBar.getVisibility() == View.VISIBLE;
        mAdjustBrightnessSeekBar.setVisibility(isVisible ? View.GONE : View.VISIBLE);
    }

    //切换前后摄像头
    public void onClickSwitchCamera(View v) {
        mShortVideoRecorder.switchCamera();
        mFocusIndicator.focusCancel();
    }

    //闪光灯开关
    public void onClickSwitchFlash(View v) {
        mFlashEnabled = !mFlashEnabled;
        mShortVideoRecorder.setFlashEnabled(mFlashEnabled);
        mSwitchFlashBtn.setActivated(mFlashEnabled);
    }

//    public void onClickAddMixAudio(View v) {
//        Intent intent = new Intent();
//        if (Build.VERSION.SDK_INT < 19) {
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            intent.setType("audio/*");
//        } else {
//            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
//            intent.addCategory(Intent.CATEGORY_OPENABLE);
//            intent.setType("audio/*");
//        }
//        startActivityForResult(Intent.createChooser(intent, "请选择混音文件："), 0);
//    }

//    public void onClickSaveToDraft(View v) {
//        final EditText editText = new EditText(this);
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this)
//                .setView(editText)
//                .setTitle(getString(R.string.dlg_save_draft_title))
//                .setPositiveButton(getString(R.string.dlg_save_draft_yes), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        ToastUtils.s(VideoRecordActivity.this,
//                                mShortVideoRecorder.saveToDraftBox(editText.getText().toString()) ?
//                                        getString(R.string.toast_draft_save_success) : getString(R.string.toast_draft_save_fail));
//                    }
//                });
//        alertDialog.show();
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            String selectedFilepath = GetPathFromUri.getPath(this, data.getData());
            Log.i(TAG, "Select file: " + selectedFilepath);
            if (selectedFilepath != null && !"".equals(selectedFilepath)) {
                mShortVideoRecorder.setMusicFile(selectedFilepath);
            }
        }
    }

    @Override
    public void onReady() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSwitchFlashBtn.setVisibility(mShortVideoRecorder.isFlashSupport() ? View.VISIBLE : View.GONE);
                mFlashEnabled = false;
                mSwitchFlashBtn.setActivated(mFlashEnabled);
                mRecordBtn.setEnabled(true);
                refreshSeekBar();
                ToastUtils.s(VideoRecordActivity.this, "可以开始拍摄咯");
            }
        });
    }

    @Override
    public void onError(final int code) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.toastErrorCode(VideoRecordActivity.this, code);
            }
        });
    }

    @Override
    public void onDurationTooShort() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.s(VideoRecordActivity.this, "该视频段太短了");
            }
        });
    }

    @Override
    public void onRecordStarted() {
        Log.i(TAG, "record start time: " + System.currentTimeMillis());
    }

    @Override
    public void onRecordStopped() {
        Log.i(TAG, "record stop time: " + System.currentTimeMillis());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateRecordingBtns(false);
            }
        });
    }

    @Override
    public void onSectionIncreased(long incDuration, long totalDuration, int sectionCount) {
        double videoSectionDuration = mDurationVideoStack.isEmpty() ? 0 : mDurationVideoStack.peek().doubleValue();
        if ((videoSectionDuration + incDuration / mRecordSpeed) >= mRecordSetting.getMaxRecordDuration()) {
            videoSectionDuration = mRecordSetting.getMaxRecordDuration();
        }
        Log.d(TAG, "videoSectionDuration: " + videoSectionDuration + "; incDuration: " + incDuration);
        onSectionCountChanged(sectionCount, (long) videoSectionDuration);
    }

    @Override
    public void onSectionDecreased(long decDuration, long totalDuration, int sectionCount) {
        mSectionProgressBar.removeLastBreakPoint();
        if (!mDurationVideoStack.isEmpty()) {
            mDurationVideoStack.pop();
        }
        if (!mDurationRecordStack.isEmpty()) {
            mDurationRecordStack.pop();
        }
        double deletedDuration = mDurationVideoStack.isEmpty() ? 0 : mDurationVideoStack.peek().doubleValue();
        onSectionCountChanged(sectionCount, (long) deletedDuration);
    }

    @Override
    public void onRecordCompleted() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.s(VideoRecordActivity.this, "已达到拍摄总时长");
            }
        });
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

    @Override
    public void onSaveVideoFailed(final int errorCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProcessingDialog.dismiss();
                ToastUtils.s(VideoRecordActivity.this, "拼接视频段失败: " + errorCode);
            }
        });
    }

    @Override
    public void onSaveVideoCanceled() {
        mProcessingDialog.dismiss();
    }

    @Override
    public void onSaveVideoSuccess(final String filePath) {///storage/emulated/0/ShortVideo/record.mp4
        Log.i(TAG, "concat sections success filePath: " + filePath);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("视频保存成功======="+filePath);
                saveFile=filePath;
                mProcessingDialog.dismiss();
                ll_control.setVisibility(View.VISIBLE);
//                VideoRecordActivity.this.finish();
            }
        });
    }

    private void refreshSeekBar() {
        final int max = mShortVideoRecorder.getMaxExposureCompensation();
        final int min = mShortVideoRecorder.getMinExposureCompensation();
        boolean brightnessAdjustAvailable = (max != 0 || min != 0);
        Log.e(TAG, "max/min exposure compensation: " + max + "/" + min + " brightness adjust available: " + brightnessAdjustAvailable);

//        findViewById(R.id.brightness_panel).setVisibility(brightnessAdjustAvailable ? View.VISIBLE : View.GONE);
        findViewById(R.id.brightness_panel).setVisibility(brightnessAdjustAvailable ? View.GONE : View.GONE);
        mAdjustBrightnessSeekBar.setOnSeekBarChangeListener(!brightnessAdjustAvailable ? null : new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (i <= Math.abs(min)) {
                    mShortVideoRecorder.setExposureCompensation(i + min);
                } else {
                    mShortVideoRecorder.setExposureCompensation(i - max);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mAdjustBrightnessSeekBar.setMax(max + Math.abs(min));
        mAdjustBrightnessSeekBar.setProgress(Math.abs(min));
    }

    private void onSectionCountChanged(final int count, final long totalTime) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDeleteBtn.setEnabled(count > 0);
                mConcatBtn.setEnabled(totalTime >= (RecordSettings.DEFAULT_MIN_RECORD_DURATION));
            }
        });
    }

    private PLCameraSetting.CAMERA_FACING_ID chooseCameraFacingId() {
        if (PLCameraSetting.hasCameraFacing(PLCameraSetting.CAMERA_FACING_ID.CAMERA_FACING_3RD)) {
            return PLCameraSetting.CAMERA_FACING_ID.CAMERA_FACING_3RD;
        } else if (PLCameraSetting.hasCameraFacing(PLCameraSetting.CAMERA_FACING_ID.CAMERA_FACING_FRONT)) {
            return PLCameraSetting.CAMERA_FACING_ID.CAMERA_FACING_FRONT;
        } else {
            return PLCameraSetting.CAMERA_FACING_ID.CAMERA_FACING_BACK;
        }
    }

//    public void onSpeedClicked(View view) {
//        if (!mVideoEncodeSetting.IsConstFrameRateEnabled() || !mRecordSetting.IsRecordSpeedVariable()) {
//            if (mSectionProgressBar.isRecorded()) {
//                ToastUtils.s(this, "变帧率模式下，无法在拍摄中途修改拍摄倍数！");
//                return;
//            }
//        }
//
//        if (mSpeedTextView != null) {
//            mSpeedTextView.setTextColor(getResources().getColor(R.color.speedTextNormal));
//        }
//
//        TextView textView = (TextView) view;
//        textView.setTextColor(getResources().getColor(R.color.colorAccent));
//        mSpeedTextView = textView;
//
//        switch (view.getId()) {
//            case R.id.super_slow_speed_text:
//                mRecordSpeed = RECORD_SPEED_ARRAY[0];
//                break;
//            case R.id.slow_speed_text:
//                mRecordSpeed = RECORD_SPEED_ARRAY[1];
//                break;
//            case R.id.normal_speed_text:
//                mRecordSpeed = RECORD_SPEED_ARRAY[2];
//                break;
//            case R.id.fast_speed_text:
//                mRecordSpeed = RECORD_SPEED_ARRAY[3];
//                break;
//            case R.id.super_fast_speed_text:
//                mRecordSpeed = RECORD_SPEED_ARRAY[4];
//                break;
//        }
//
//        mShortVideoRecorder.setRecordSpeed(mRecordSpeed);
//        if (mRecordSetting.IsRecordSpeedVariable() && mVideoEncodeSetting.IsConstFrameRateEnabled()) {
//            mSectionProgressBar.setProceedingSpeed(mRecordSpeed);
//            mRecordSetting.setMaxRecordDuration(RecordSettings.DEFAULT_MAX_RECORD_DURATION);
//            mSectionProgressBar.setFirstPointTime(RecordSettings.DEFAULT_MIN_RECORD_DURATION);
//        } else {
//            mRecordSetting.setMaxRecordDuration((long) (RecordSettings.DEFAULT_MAX_RECORD_DURATION * mRecordSpeed));
//            mSectionProgressBar.setFirstPointTime((long) (RecordSettings.DEFAULT_MIN_RECORD_DURATION * mRecordSpeed));
//        }
//
//        mSectionProgressBar.setTotalTime(this, mRecordSetting.getMaxRecordDuration());
//    }

    @Override
    public void onManualFocusStart(boolean result) {
        if (result) {
            Log.i(TAG, "manual focus begin success");
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFocusIndicator.getLayoutParams();
            lp.leftMargin = mFocusIndicatorX;
            lp.topMargin = mFocusIndicatorY;
            mFocusIndicator.setLayoutParams(lp);
            mFocusIndicator.focus();
        } else {
            mFocusIndicator.focusCancel();
            Log.i(TAG, "manual focus not supported");
        }
    }

    @Override
    public void onManualFocusStop(boolean result) {
        Log.i(TAG, "manual focus end result: " + result);
        if (result) {
            mFocusIndicator.focusSuccess();
        } else {
            mFocusIndicator.focusFail();
        }
    }

    @Override
    public void onManualFocusCancel() {
        Log.i(TAG, "manual focus canceled");
        mFocusIndicator.focusCancel();
    }

    @Override
    public void onAutoFocusStart() {
        Log.i(TAG, "auto focus start");
    }

    @Override
    public void onAutoFocusStop() {
        Log.i(TAG, "auto focus stop");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                VideoRecordActivity.this.finish();
                break;
            case R.id.iv_clock:
                showSelectDialog();
                break;
            case R.id.tv_resolution:

                break;
            case R.id.ll_control://操作模块
                ll_control.setVisibility(View.GONE);
                VideoRecordActivity.this.finish();
                break;
            case R.id.tv_cutvideo://裁剪视频
                mHandler.sendEmptyMessageDelayed(1,500);
                break;
            case R.id.tv_addheadend://添加片头片尾
                mHandler.sendEmptyMessageDelayed(2,500);
                break;
            case R.id.tv_addtext://加字幕
                mHandler.sendEmptyMessageDelayed(3,500);
                break;
            case R.id.tv_mergevideo://合并视频
                mHandler.sendEmptyMessageDelayed(4,500);
                break;
            case R.id.tv_addaudio://叠加音频
                mHandler.sendEmptyMessageDelayed(5,500);
                break;
            case R.id.tv_addlogo://加logo

                break;
        }
    }

    private void showSelectDialog() {
        if (selectPicDialog == null) {
            selectPicDialog = new SelectPicDialog(this);
            selectPicDialog.setCancelable(true);
            selectPicDialog.setCanceledOnTouchOutside(true);
            selectPicDialog.setOnSelectPicClickListener(new SelectPicDialog.OnSelectPicOptionClick() {
                @Override
                public void OnPicSelect(int id) {
                    switch (id) {
                        case R.id.tv_10s:
                            VideoRecordActivity.this.getSharedPreferences(SPConstant.RECORD_MAXTIME_NAME, VideoRecordActivity.this.MODE_PRIVATE).edit()
                                    .putInt(SPConstant.RECORD_MAXTIME_DURATION, 10 * 1000).commit();
                            mRecordSetting.setMaxRecordDuration(10 * 1000);//定时拍摄设置最大时间长度
                            break;
                        case R.id.tv_30s:
                            VideoRecordActivity.this.getSharedPreferences(SPConstant.RECORD_MAXTIME_NAME, VideoRecordActivity.this.MODE_PRIVATE).edit()
                                    .putInt(SPConstant.RECORD_MAXTIME_DURATION, 30 * 1000).commit();
                            mRecordSetting.setMaxRecordDuration(30 * 1000);//定时拍摄设置最大时间长度
                            break;
                        case R.id.tv_60s:
                            VideoRecordActivity.this.getSharedPreferences(SPConstant.RECORD_MAXTIME_NAME, VideoRecordActivity.this.MODE_PRIVATE).edit()
                                    .putInt(SPConstant.RECORD_MAXTIME_DURATION, 60 * 1000).commit();
                            mRecordSetting.setMaxRecordDuration(60 * 1000);//定时拍摄设置最大时间长度
                            break;
                        case R.id.tv_90s:
                            VideoRecordActivity.this.getSharedPreferences(SPConstant.RECORD_MAXTIME_NAME, VideoRecordActivity.this.MODE_PRIVATE).edit()
                                    .putInt(SPConstant.RECORD_MAXTIME_DURATION, 120 * 1000).commit();
                            mRecordSetting.setMaxRecordDuration(120 * 1000);//定时拍摄设置最大时间长度
                            break;
                        case R.id.tv_120s:

                            break;
                    }

                    mShortVideoRecorder.prepare(preview, mCameraSetting, mMicrophoneSetting, mVideoEncodeSetting,
                            mAudioEncodeSetting, USE_TUSDK ? null : mFaceBeautySetting, mRecordSetting);
                }
            });
        }

        selectPicDialog.show();
    }
}
