package com.jwzt.caibian.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.jwzt.caibian.util.Config;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.ToastUtils;
import com.jwzt.caibian.view.CustomProgressDialog;
import com.jwzt.caibian.view.FrameSelectorView;
import com.jwzt.caibian.view.ObservableHorizontalScrollView;
import com.jwzt.cb.product.R;
import com.qiniu.pili.droid.shortvideo.PLMediaFile;
import com.qiniu.pili.droid.shortvideo.PLShortVideoComposer;
import com.qiniu.pili.droid.shortvideo.PLShortVideoTrimmer;
import com.qiniu.pili.droid.shortvideo.PLVideoEncodeSetting;
import com.qiniu.pili.droid.shortvideo.PLVideoFrame;
import com.qiniu.pili.droid.shortvideo.PLVideoSaveListener;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

public class VideoDivideActivity extends Activity {
    private static final String TAG = "VideoDivideActivity";

    private PLShortVideoTrimmer mShortVideoTrimmer;
    private PLMediaFile mMediaFile;

    private CustomProgressDialog mProcessingDialog;
    private VideoView mPreview;
    private RecyclerView mFrameList;
    private RelativeLayout mFrameListParent;
    private ObservableHorizontalScrollView mScrollView;
    private FrameLayout mScrollViewParent;
    private ImageButton mPlaybackButton;
    private FrameSelectorView mCurSelectorView;

    private long mDurationMs;
    private int mVideoFrameCount;
    private long mShowFrameIntervalMs;
    private String mSrcVideoPath;

    private FrameListAdapter mFrameListAdapter;

    private int mFrameWidth;
    private int mFrameHeight;
    private int mCurTrimNum;

    private ArrayList<SectionItem> mSectionList = new ArrayList<>();//分割线的集合
    private ArrayList<SectionItem> mSectionList1 = new ArrayList<>();//实际选中的区域
    private ArrayList<SectionItem> mSectionList2 = new ArrayList<>();//处理相连片段后的数据集合
    private ArrayList<SectionItem> mInvertSectionList = new ArrayList<>();//剔除选中区域后的反选数据集合
    private ArrayList<String> mPathList = new ArrayList<>();

    private TimerTask mScrollTimerTask;
    private Timer mScrollTimer;

    private AlertDialog alertDialog;
    private PLShortVideoComposer mShortVideoComposer;

    private float xDown;
    private float xUp;
    private int recycleViewWidth;
    private int leftBorder;//半屏宽度


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mSrcVideoPath = getIntent().getStringExtra("vedio_path");


        if (mSrcVideoPath != null && !"".equals(mSrcVideoPath)) {
            init();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        play();
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

    public void onClickBack(View view) {
        finish();
    }

    /**
     * 添加选取并开始裁剪
     *
     * @param view
     */
    public void onClickDone(View view) {
        resetData();
//        if (mSectionList.size() > 0) {
//        集合整体是0-recycleViewWidth
        if (IsNonEmptyUtils.isList(mSectionList1)) {
//            1.先将该集合按照mLeftPosition升序
            Collections.sort(mSectionList1, new Comparator() {//排序 将集合按照mLeftPosition升序排列
                @Override
                public int compare(Object o1, Object o2) {
                    if (o1 instanceof SectionItem && o2 instanceof SectionItem) {
                        SectionItem e1 = (SectionItem) o1;
                        SectionItem e2 = (SectionItem) o2;
                        return e1.mLeftPosition - e2.mLeftPosition;
                    }
                    throw new ClassCastException("不能转换为Emp类型");
                }
            });

//            2.判断集合中有无相连片段
            if (mSectionList1.size() >= 2) {//表示有相连片段存在的可能
                for (int i = 0; i < mSectionList1.size() - 1; i++) {
                    if (mSectionList1.get(i + 1).mLeftPosition - mSectionList1.get(i).mRightPosition < 20) {//表示相连片段那么将两个片段合并成一个片段
//                        mSectionList1.get(i).mLeftPosition--mSectionList1.get(i+1).mRightPosition
                        long startTime = getTimeByPosition(mSectionList1.get(i).mLeftPosition);
                        long endTime = getTimeByPosition(mSectionList1.get(i + 1).mRightPosition);
                        SectionItem sectionItem = new SectionItem(mSectionList1.get(i).mLeftPosition, mSectionList1.get(i + 1).mRightPosition, startTime, endTime, "");
                        mSectionList1.remove(i + 1);
                        mSectionList1.remove(i);
                        mSectionList2.add(sectionItem);
//                            mSectionList1.set(i,sectionItem);
                        Log.i("===处理后===>>", mSectionList1.size() + "");
//                        mSectionList1.add(i,sectionItem);
                    } else {//否则直接添加
                        mSectionList2.add(mSectionList1.get(i));
                    }
                }
                System.out.print("====>>" + mSectionList2.size());
            } else {
                mSectionList2 = mSectionList1;
            }
            //获取没有被选中的区间
            mInvertSectionList = new ArrayList<SectionItem>();
            for (int i = 0; i < mSectionList2.size(); i++) {
                if (i == 0) {//表示第一次循环
                    if (mSectionList2.get(i).mLeftPosition == (leftBorder + 4)) {//表示从头开始
                        if (i < mSectionList2.size() - 1) {
//                          mSectionList2.get(i).mRightPosition - mSectionList2.get(i + 1).mLeftPosition
                            String path = Config.VIDEO_STORAGE_TRIM + "pl-trim-" + System.currentTimeMillis() + ".mp4";
                            long startTime = getTimeByPosition(mSectionList2.get(i).mRightPosition);
                            long endTime = getTimeByPosition(mSectionList2.get(i + 1).mLeftPosition);
                            SectionItem item = new SectionItem(mSectionList2.get(i).mRightPosition, mSectionList2.get(i + 1).mLeftPosition, startTime, endTime, path);
                            mInvertSectionList.add(item);
                        } else {//表示就裁剪了一段且从0开始
//                          mSectionList2.get(i).mRightPosition - recycleViewWidth
                            String path = Config.VIDEO_STORAGE_TRIM + "pl-trim-" + System.currentTimeMillis() + ".mp4";
                            long startTime = getTimeByPosition(mSectionList2.get(i).mRightPosition);
                            long endTime = getTimeByPosition(recycleViewWidth - (leftBorder + 4));
                            SectionItem item = new SectionItem(mSectionList2.get(i).mRightPosition, recycleViewWidth - (leftBorder + 4), startTime, endTime, path);
                            mInvertSectionList.add(item);
                        }
                    } else {//表示没有从0开始
//                        0 - mSectionList2.get(i).mLeftPosition
                        String path = Config.VIDEO_STORAGE_TRIM + "pl-trim-" + System.currentTimeMillis() + ".mp4";
                        long startTime = getTimeByPosition((leftBorder + 4));
                        long endTime = getTimeByPosition(mSectionList2.get(i).mLeftPosition);
                        SectionItem item = new SectionItem((leftBorder + 4), mSectionList2.get(i).mLeftPosition, startTime, endTime, path);
                        mInvertSectionList.add(item);

                        if (i < mSectionList2.size() - 1) {
//                        mSectionList2.get(i).mRightPosition - mSectionList2.get(i + 1).mLeftPosition
                            String path1 = Config.VIDEO_STORAGE_TRIM + "pl-trim-" + System.currentTimeMillis() + ".mp4";
                            long startTime1 = getTimeByPosition(mSectionList2.get(i).mRightPosition);
                            long endTime1 = getTimeByPosition(mSectionList2.get(i + 1).mLeftPosition);
                            SectionItem item1 = new SectionItem(mSectionList2.get(i).mRightPosition, mSectionList2.get(i + 1).mLeftPosition, startTime1, endTime1, path1);
                            mInvertSectionList.add(item1);
                        } else {//表示没有从0开始,且在中间只选择了一段  当只选择一段需要判断是否选的最后一段
                            if (mSectionList2.get(i).mRightPosition == recycleViewWidth - (leftBorder + 4)) {//表示选择了最后一段则不做处理

                            } else {
//                              mSectionList2.get(i).mRightPosition -recycleViewWidth
                                String path1 = Config.VIDEO_STORAGE_TRIM + "pl-trim-" + System.currentTimeMillis() + ".mp4";
                                long startTime1 = getTimeByPosition(mSectionList2.get(i).mRightPosition);
                                long endTime1 = getTimeByPosition(recycleViewWidth - (leftBorder + 4));
                                SectionItem item1 = new SectionItem(mSectionList2.get(i).mRightPosition, recycleViewWidth - (leftBorder + 4), startTime1, endTime1, path1);
                                mInvertSectionList.add(item1);
                            }
                        }
                    }
                } else if (i == mSectionList2.size() - 1) {
                    if (mSectionList2.get(i).mRightPosition == recycleViewWidth - (leftBorder + 4)) {//表示最后一段选中的结尾的则不做操作

                    } else {
//                        mSectionList2.get(i).mRightPosition - recycleViewWidth
                        String path = Config.VIDEO_STORAGE_TRIM + "pl-trim-" + System.currentTimeMillis() + ".mp4";
                        long startTime = getTimeByPosition(mSectionList2.get(i).mRightPosition);
                        long endTime = getTimeByPosition(recycleViewWidth - (leftBorder + 4));
                        SectionItem item = new SectionItem(mSectionList2.get(i).mRightPosition, recycleViewWidth - (leftBorder + 4), startTime, endTime, path);
                        mInvertSectionList.add(item);
                    }
                } else {
//                    mSectionList2.get(i).mRightPosition - mSectionList2.get(i + 1).mLeftPosition
                    String path = Config.VIDEO_STORAGE_TRIM + "pl-trim-" + System.currentTimeMillis() + ".mp4";
                    long startTime = getTimeByPosition(mSectionList2.get(i).mRightPosition);
                    long endTime = getTimeByPosition(mSectionList2.get(i + 1).mLeftPosition);
                    SectionItem item = new SectionItem(mSectionList2.get(i).mRightPosition, mSectionList2.get(i + 1).mLeftPosition, startTime, endTime, path);
                    mInvertSectionList.add(item);
                }
            }

//          3.判断有没有相连片段
            System.out.print("" + mInvertSectionList.size());
//            2.调用SDK方法进行实际的操作裁剪
            trimOnce();
        } else {
            ToastUtils.s(VideoDivideActivity.this, "请选择删除区域");
//            noTrimJump();
        }
    }

    public void onClickMerge(View view) {
        showTip();
    }

    /**
     * 添加选取分割线
     *
     * @param view
     */
    public void onClickAdd(View view) {
        pausePlayback();
        addSelectedRect();
//        addSelectorView();
    }

    public void onClickPlayback(View view) {
        if (mPreview.isPlaying()) {
            pausePlayback();
        } else {
            startPlayback();
        }
    }

    private void init() {
        initView();
        initMediaInfo();
        initVideoPlayer();
        initFrameList();
        initTimerTask();
        addSelectorView();
    }

    private void initView() {
        setContentView(R.layout.activity_video_divide);

        mPreview = (VideoView) findViewById(R.id.preview);
        mPlaybackButton = (ImageButton) findViewById(R.id.pause_playback);
        mFrameListParent = (RelativeLayout) findViewById(R.id.recycler_parent);
        mFrameList = (RecyclerView) findViewById(R.id.recycler_frame_list);
        mScrollViewParent = (FrameLayout) findViewById(R.id.scroll_view_parent);
        mScrollView = (ObservableHorizontalScrollView) findViewById(R.id.scroll_view);

        ImageView middleLineImage = (ImageView) findViewById(R.id.middle_line_image);
        ViewGroup topGroup = (ViewGroup) findViewById(R.id.top_group);

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mFrameWidth = mFrameHeight = wm.getDefaultDisplay().getWidth() / 6;
//        middleLineImage.getLayoutParams().height = mFrameHeight;

        mScrollView.setOnScrollListener(new OnViewScrollListener());
        topGroup.setOnTouchListener(new OnTopViewTouchListener());
        mFrameList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    xDown = motionEvent.getX();
                    Log.v("OnTouchListener", "Down");
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {// 松开处理
                    //获取松开时的x坐标
                    xUp = motionEvent.getX();
                    Log.v("OnTouchListener", "Up");
                    //按下和松开绝对值差当大于20时滑动，否则不显示
                    int abs = (int) Math.abs(xDown - xUp);
                    if (abs > 30) {//表示滑动
                        //添加要处理的内容
                    } else {//表示点击
                        recycleViewWidth = mFrameList.getWidth();
                        addSelected(xDown, recycleViewWidth);
                    }
                }
                return false;
            }
        });
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

    private void initTimerTask() {
        mScrollTimerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final int position = mPreview.getCurrentPosition();
                        if (mPreview.isPlaying()) {
                            scrollToTime(position);
                        }
                    }
                });
            }
        };
        mScrollTimer = new Timer();
        // scroll fps:20
        mScrollTimer.schedule(mScrollTimerTask, 50, 50);
    }

    private void initFrameList() {
        mFrameListAdapter = new FrameListAdapter();
        mFrameList.setAdapter(mFrameListAdapter);
        mFrameList.setItemViewCacheSize(getShowFrameCount());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mFrameList.setLayoutManager(layoutManager);
    }

    private void resetData() {
        mCurTrimNum = 0;
        mPathList.clear();
    }

    private void trimOnce() {
        if (mCurTrimNum < mInvertSectionList.size()) {
            mProcessingDialog.setProgress(0);
            mProcessingDialog.setMessage("正在处理第 " + (mCurTrimNum + 1) + "/" + mInvertSectionList.size() + " 段视频 ...");
            mProcessingDialog.show();

            SectionItem item = mInvertSectionList.get(mCurTrimNum);
            if (mShortVideoTrimmer != null) {
                mShortVideoTrimmer.destroy();
            }
            mShortVideoTrimmer = new PLShortVideoTrimmer(this, mSrcVideoPath, item.mVideoPath);
            mShortVideoTrimmer.trim(item.mStartTime, item.mEndTime, PLShortVideoTrimmer.TRIM_MODE.ACCURATE, mSaveListener);
            mCurTrimNum += 1;
        } else {
            //TODO 视频分段裁剪完成 下面开始合并
            mProcessingDialog.dismiss();
            jumpToActivity();
            //开始合并
            startcomposeVideos();
//            resetData();
//            startcomposeVideos();

        }
    }

    private void startcomposeVideos() {
        mProcessingDialog.setProgress(0);
        mProcessingDialog.setMessage("正在合并，请稍后...");
        mProcessingDialog.show();
        mShortVideoComposer = new PLShortVideoComposer(this);
        PLVideoEncodeSetting setting = new PLVideoEncodeSetting(VideoDivideActivity.this);
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
        if (mShortVideoComposer.composeVideos(mPathList, savePath, setting, mVideoSaveListener)) {
//            mProcessingDialog.show();
        } else {
//                ToastUtils.s(this, "开始拼接失败！");
        }
    }

    private PLVideoSaveListener mVideoSaveListener = new PLVideoSaveListener() {
        @Override
        public void onSaveVideoSuccess(final String destFile) {
            mProcessingDialog.dismiss();
            resetData();
            Log.i("===合并状态===>>", "合并完成");
            Intent intent = new Intent(VideoDivideActivity.this, VideoDivideActivity.class);
            intent.putExtra("vedio_path", destFile);
            startActivity(intent);
            VideoDivideActivity.this.finish();
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

    private void noTrimJump() {
        mPathList.add(mSrcVideoPath);
        jumpToActivity();
        resetData();
    }

    private void jumpToActivity() {
        ToastUtils.s(VideoDivideActivity.this, "裁剪完成");
        System.out.print("=========>>" + mPathList);
    }

    //添加选取浮层
    private void addSelectorView() {
        mCurSelectorView = new FrameSelectorView(this);
        final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, mFrameListParent.getHeight());
        mCurSelectorView.setVisibility(View.INVISIBLE);
        mScrollViewParent.addView(mCurSelectorView, layoutParams);

        mCurSelectorView.post(new Runnable() {
            @Override
            public void run() {
                layoutParams.leftMargin = (mScrollViewParent.getWidth() - mCurSelectorView.getWidth()) / 2;
                mCurSelectorView.setLayoutParams(layoutParams);
                mCurSelectorView.setVisibility(View.VISIBLE);
            }
        });
    }

    //添加选区
    @SuppressLint("NewApi")
    private void addSelected(float touchX1, int recyleviewWidth) {
        if (IsNonEmptyUtils.isList(mSectionList)) {
            testComparatorSortAge();//排序 将集合按照mLeftPosition升序排列
            for (int i = 0; i < mSectionList.size(); i++) {
                if (touchX1 > 0 && touchX1 < mSectionList.get(0).mRightPosition) {//选中第一块区域
                    final View rectView = new View(this);
                    rectView.setBackground(getResources().getDrawable(R.drawable.frame_selector_rect));
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mSectionList.get(0).mRightPosition - leftBorder, mFrameListParent.getHeight());
                    rectView.setOnTouchListener(new RectViewTouchListener());
                    layoutParams.leftMargin = leftBorder;
                    mFrameListParent.addView(rectView, layoutParams);

                    SectionItem selectItem = addSection1(leftBorder + 4, mSectionList.get(0).mRightPosition);
                    rectView.setTag(selectItem);
                    return;
                } else if (touchX1 > mSectionList.get(mSectionList.size() - 1).mLeftPosition && touchX1 < recyleviewWidth) {//表示选中最后一块区域
                    final View rectView = new View(this);
                    rectView.setBackground(getResources().getDrawable(R.drawable.frame_selector_rect));
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(recyleviewWidth - mSectionList.get(mSectionList.size() - 1).mLeftPosition - leftBorder, mFrameListParent.getHeight());
                    rectView.setOnTouchListener(new RectViewTouchListener());
                    layoutParams.leftMargin = mSectionList.get(mSectionList.size() - 1).mLeftPosition;
                    mFrameListParent.addView(rectView, layoutParams);

                    SectionItem selectItem = addSection1(mSectionList.get(mSectionList.size() - 1).mLeftPosition, recyleviewWidth - leftBorder - 4);
                    rectView.setTag(selectItem);
                    return;
                } else if (touchX1 > mSectionList.get(i).mLeftPosition && touchX1 < mSectionList.get(i + 1).mRightPosition) {//表示该区域被选中
                    final View rectView = new View(this);
                    rectView.setBackground(getResources().getDrawable(R.drawable.frame_selector_rect));
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mSectionList.get(i + 1).mRightPosition - mSectionList.get(i).mLeftPosition, mFrameListParent.getHeight());
                    rectView.setOnTouchListener(new RectViewTouchListener());
                    layoutParams.leftMargin = mSectionList.get(i).mLeftPosition;
                    mFrameListParent.addView(rectView, layoutParams);

                    SectionItem selectItem = addSection1(mSectionList.get(i).mLeftPosition, mSectionList.get(i + 1).mRightPosition);
                    rectView.setTag(selectItem);
                }
            }
        }
    }

    //使用Comparator比较器按age升序排序
    public void testComparatorSortAge() {
        Collections.sort(mSectionList, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                if (o1 instanceof SectionItem && o2 instanceof SectionItem) {
                    SectionItem e1 = (SectionItem) o1;
                    SectionItem e2 = (SectionItem) o2;
                    return e1.mLeftPosition - e2.mLeftPosition;
                }
                throw new ClassCastException("不能转换为Emp类型");
            }
        });
    }

    //添加线
    @SuppressLint("NewApi")
    public void addSelectedRect() {
        if (mCurSelectorView == null) {
            return;
        }

        leftBorder = mCurSelectorView.getBodyLeft();
        int rightBorder = mCurSelectorView.getBodyRight();
        int width = mCurSelectorView.getBodyWidth();

        boolean outOfLeft = leftBorder <= getHalfGroupWidth() - mScrollView.getScrollX();
        boolean outOfRight = rightBorder >= getHalfGroupWidth() + (getTotalScrollLength() - mScrollView.getScrollX());

        if (outOfLeft && !outOfRight) {
            leftBorder = getHalfGroupWidth() - mScrollView.getScrollX();
            width = rightBorder - leftBorder;
        } else if (!outOfLeft && outOfRight) {
            width = width - (rightBorder - getHalfGroupWidth() - (getTotalScrollLength() - mScrollView.getScrollX()));
        } else if (outOfLeft && outOfRight) {
            leftBorder = getHalfGroupWidth() - mScrollView.getScrollX();
            width = getTotalScrollLength();
        }

        if (width <= 0) {
            mCurSelectorView.setVisibility(View.GONE);
            return;
        }

        final View rectView = new View(this);
        rectView.setBackground(getResources().getDrawable(R.drawable.frame_selector_rect));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, mFrameListParent.getHeight());
        rectView.setOnTouchListener(new RectViewTouchListener());

        int a = mScrollView.getScrollX();
        int leftPosition = leftBorder + a;
        int rightPosition = leftPosition + width;

        layoutParams.leftMargin = leftPosition;
        mFrameListParent.addView(rectView, layoutParams);

        mCurSelectorView.setVisibility(View.GONE);

        SectionItem item = addSection(leftPosition, rightPosition);
        rectView.setTag(item);
    }

    //添加分割线的数据集合
    private SectionItem addSection(int leftPosition, int rightPosition) {
        String path = Config.VIDEO_STORAGE_TRIM + "pl-trim-" + System.currentTimeMillis() + ".mp4";
        long startTime = getTimeByPosition(leftPosition);
        long endTime = getTimeByPosition(rightPosition);
        SectionItem sectionItem = new SectionItem(leftPosition, rightPosition, startTime, endTime, path);
        mSectionList.add(sectionItem);
        return sectionItem;
    }

    //添加实际选中的数据集合
    private SectionItem addSection1(int leftPosition, int rightPosition) {
        long startTime = getTimeByPosition(leftPosition);
        long endTime = getTimeByPosition(rightPosition);
        SectionItem sectionItem = new SectionItem(leftPosition, rightPosition, startTime, endTime, "");
        mSectionList1.add(sectionItem);
        return sectionItem;
    }

    //根据滑动距离获得当前视频的时间
    private long getTimeByPosition(int position) {
        position = position - getHalfGroupWidth();
        return (long) ((float) mDurationMs * position / getTotalScrollLength());
    }

    private int getTotalScrollLength() {
        return getShowFrameCount() * mFrameWidth;
    }

    private int getHalfGroupWidth() {
        return mFrameWidth * 3;
    }

    private int getScrollLengthByTime(long time) {
        return (int) ((float) getTotalScrollLength() * time / mDurationMs);
    }

    private void scrollToTime(long time) {
        int scrollLength = getScrollLengthByTime(time);
        mScrollView.smoothScrollTo(scrollLength, 0);
    }

    private int getShowFrameCount() {
        return (int) Math.ceil((float) mDurationMs / mShowFrameIntervalMs);
    }

    private void startPlayback() {
        mPreview.start();
        mPlaybackButton.setImageResource(R.drawable.btn_pause);
    }

    private void pausePlayback() {
        mPreview.pause();
        mPlaybackButton.setImageResource(R.drawable.btn_play);
    }

    private boolean isInRangeOfView(View view, MotionEvent ev) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        if (ev.getX() < x ||
                ev.getX() > (x + view.getWidth()) ||
                ev.getY() < y ||
                ev.getY() > (y + view.getHeight())) {
            return false;
        }
        return true;
    }

    private void play() {
        if (mPreview != null) {
            mPreview.seekTo(0);
            startPlayback();
        }
    }

    private PLVideoSaveListener mSaveListener = new PLVideoSaveListener() {
        @Override
        public void onSaveVideoSuccess(String destFile) {
            mPathList.add(destFile);
            Log.i("===裁剪状态===>>", mPathList.size() + "==" + destFile);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    trimOnce();
                }
            });
        }

        @Override
        public void onSaveVideoFailed(int errorCode) {
            Log.i("===裁剪状态===>>", errorCode + "");
        }

        @Override
        public void onSaveVideoCanceled() {
            Log.i("===裁剪状态===>>", "取消");
        }

        @Override
        public void onProgressUpdate(final float percentage) {
            Log.i("===裁剪状态===>>", percentage + "");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProcessingDialog.setProgress((int) (100 * percentage));
                }
            });
        }
    };

    private class RectViewTouchListener implements View.OnTouchListener {
        private View mRectView;
        private GestureDetector.SimpleOnGestureListener mSimpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                SectionItem item = (SectionItem) mRectView.getTag();
                mSectionList1.remove(item);
                mFrameListParent.removeView(mRectView);
                return false;
            }
        };
        private GestureDetector mGestureDetector = new GestureDetector(VideoDivideActivity.this, mSimpleOnGestureListener);

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            mRectView = view;
            if (mGestureDetector.onTouchEvent(event)) {
                return true;
            }
            return true;
        }
    }

    private class OnTopViewTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (!isInRangeOfView(mScrollViewParent, event)) {
                if (mCurSelectorView != null) {
                    addSelectedRect();
                    mCurSelectorView = null;
                }
            }
            return false;
        }
    }

    private class OnViewScrollListener implements ObservableHorizontalScrollView.OnScrollListener {
        @Override
        public void onScrollChanged(ObservableHorizontalScrollView scrollView, final int x, int y, int oldX, int oldY, boolean dragScroll) {
            if (dragScroll) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mPreview.isPlaying()) {
                            pausePlayback();
                        }
//                        Log.i("===滑动距离===>>", x + "");
                        float index = (float) x / mFrameWidth;

                        Log.i("===滑动距离===>>", x + "===" + index + "==" + ((int) (index * mShowFrameIntervalMs)));
//                        int seek=0;
//                        seek=seek+500;
//                        mPreview.seekTo(seek);
                        mPreview.seekTo((int) (index * mShowFrameIntervalMs));
                    }
                });
            }
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.thumbnail);
        }
    }

    private class FrameListAdapter extends RecyclerView.Adapter<ItemViewHolder> {

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View contactView = inflater.inflate(R.layout.item_devide_frame, parent, false);
            ItemViewHolder viewHolder = new ItemViewHolder(contactView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ItemViewHolder holder, final int position) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(mFrameWidth, mFrameHeight);
            params.width = mFrameWidth;
            holder.mImageView.setLayoutParams(params);

            // there are 6 dark frames in begin and end sides
            if (position == 0 || position == 1 || position == 2 ||
                    position == getShowFrameCount() + 3 ||
                    position == getShowFrameCount() + 4 ||
                    position == getShowFrameCount() + 5) {
                return;
            }

            long frameTime = (position - 3) * mShowFrameIntervalMs;
            new ImageViewTask(holder.mImageView, frameTime, mFrameWidth, mFrameHeight, mMediaFile).execute();
        }

        @Override
        public int getItemCount() {
            return getShowFrameCount() + 6;
        }

    }

    private static class ImageViewTask extends AsyncTask<Void, Void, PLVideoFrame> {
        private WeakReference<ImageView> mImageViewWeakReference;
        private long mFrameTime;
        private int mFrameWidth;
        private int mFrameHeight;
        private PLMediaFile mMediaFile;

        ImageViewTask(ImageView imageView, long frameTime, int frameWidth, int frameHeight, PLMediaFile mediaFile) {
            mImageViewWeakReference = new WeakReference<>(imageView);
            mFrameTime = frameTime;
            mFrameWidth = frameWidth;
            mFrameHeight = frameHeight;
            mMediaFile = mediaFile;
        }

        @Override
        protected PLVideoFrame doInBackground(Void... v) {
            PLVideoFrame frame = mMediaFile.getVideoFrameByTime(mFrameTime, false, mFrameWidth, mFrameHeight);
            return frame;
        }

        @Override
        protected void onPostExecute(PLVideoFrame frame) {
            super.onPostExecute(frame);
            ImageView mImageView = mImageViewWeakReference.get();
            if (mImageView == null) {
                return;
            }
            if (frame != null) {
                int rotation = frame.getRotation();
                Bitmap bitmap = frame.toBitmap();
                mImageView.setImageBitmap(bitmap);
                mImageView.setRotation(rotation);
            }
        }
    }

    private class SectionItem {
        int mLeftPosition;
        int mRightPosition;
        long mStartTime;
        long mEndTime;
        String mVideoPath;

        public SectionItem(int leftPosition, int rightPosition, long startTime, long endTime, String videoPath) {
            mLeftPosition = leftPosition;
            mRightPosition = rightPosition;
            mStartTime = startTime;
            mEndTime = endTime;
            mVideoPath = videoPath;
        }
    }

    //显示是否保存编辑的提示
    private void showTip() {
        if (alertDialog == null) {
            alertDialog = new AlertDialog.Builder(this).create();
        }
        alertDialog.show();
        View tipView = View.inflate(this, R.layout.edit_alert_layout, null);
        View tv_yes = (TextView) tipView.findViewById(R.id.tv_yes);
        View tv_no = (TextView) tipView.findViewById(R.id.tv_no);
        //不再提醒
        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
//                startcomposeVideos();
            }
        });
        tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setContentView(tipView);

    }
}
