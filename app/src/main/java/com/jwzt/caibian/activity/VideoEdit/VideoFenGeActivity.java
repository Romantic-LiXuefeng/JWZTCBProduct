package com.jwzt.caibian.activity.VideoEdit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.jwzt.caibian.bean.AttachsBeen;
import com.jwzt.caibian.bean.ComposeInfo;
import com.jwzt.caibian.db.RecordDao;
import com.jwzt.caibian.interfaces.ScrollViewListener;
import com.jwzt.caibian.util.BitmapUtils;
import com.jwzt.caibian.util.TimeFormatUtils;
import com.jwzt.caibian.util.UIUtils;
import com.jwzt.caibian.widget.EditVideoImageBar;
import com.jwzt.caibian.widget.ObservableScrollView;
import com.jwzt.caibian.widget.VideoEditProgressBar;
import com.jwzt.cb.product.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/***
 * 视频分割
 */
public class VideoFenGeActivity extends Activity implements ScrollViewListener, View.OnClickListener {
    private int mCurrentPosition = 0;
    private TextView currentTime;
    private ObservableScrollView mScrollView;
    private EditVideoImageBar mImageLists;
    private int screenWidth;
    private int mBottomLength;
    private VideoView mVideoView;
    private ImageView mPlayerController;
    private SeekBar mVedioBar;
private String mVedioPath="";
    private int mDuration;
    private TextView mVideoDuration;
    private ComposeInfo info;
    private RecordDao dao;
    private AttachsBeen bean;
    private boolean isPlaying;
    private RecyclerView mRecyclerView;
    private static final int MARGIN = UIUtils.dp2Px(0); //左右两边间距
    private int mDragPosition=0;//手势拖动的距离
    private int mMaxWidth; //可裁剪区域的最大宽度
    private Handler mHandler=new Handler(){

        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case 0://拖动更新
                    if(!mVideoView.isPlaying()&&!isPlaying){
                        //非播放状态
                        mVideoView.seekTo(mDragPosition);
                    }else{
                        //播放状态
                        int mDragPosition1 = mVideoView.getCurrentPosition();
                        int currentPosition=0;
                        if(mDuration!=0){
                            currentPosition=mBottomLength*mDragPosition1/mDuration;
                        }else{
                            if(currentPosition<=100){
                                currentPosition=mDragPosition1;
                            }
                        }
                        mScrollView.scrollTo(currentPosition==0?1:currentPosition, 0);
                        mCurrentPosition=currentPosition;
                        mHandler.sendEmptyMessageDelayed(0, 100);
                    }

                    int currentPosition = mVideoView.getCurrentPosition();
                    mVedioBar.setProgress((int) (currentPosition));
                    System.out.println((currentPosition)+"===============");

                    break;
                case 1://视频播放或者暂停action

                    break;
                case 3:
                    mDuration=mVideoView.getDuration();
                    mVideoView.pause();
                    break;
                case 100:
                 //   mererVideo();
                    break;
                case 101:
                //    reRreshUI();
                    break;
                default:
                    break;
            }

        }

    };
    private ArrayList keyFrameList;
    private TrimVideoBitAdapter videoEditAdapter;
    private LinearLayout ll_wave_content;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_fen_ge);
        WindowManager windowManager = (WindowManager) getApplication().
                getSystemService(getApplication().WINDOW_SERVICE);
        screenWidth = windowManager.getDefaultDisplay().getWidth();
        mMaxWidth = UIUtils.getScreenWidth() - MARGIN * 2;
        initView();
        initData();
    }

    private void initData() {
        mImageLists.clearPosition();

        initVideoInfo();
        info = new ComposeInfo();
        info.setPath(mVedioPath);
        if(dao==null){
            dao = new RecordDao(this);
        }

        bean = new AttachsBeen();
        bean.setAchsPath(mVedioPath);
        bean = dao.isExist(bean);
        info.setTips(bean.getTips());
        info.setFlags(bean.getFlags());
        final ArrayList<Float> pausePoints = new ArrayList<Float>();
        mPlayerController.setOnClickListener(this);
        mVideoView.setVideoPath(mVedioPath);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                mDuration = mp.getDuration();
//				mVideoView.seekTo(1);//避免显示黑屏


                if(info.getFlags()!=null&&!info.getFlags().isEmpty()){
                    for (int i = 0; i < info.getFlags().size(); i++) {
                        Integer integer = info.getFlags().get(i);
                        pausePoints.add(integer*1.0f/ mDuration);
                    }
                }
                if (pausePoints != null && pausePoints.size() > 0) {
                 //   mVideoEditProgressBar.setPausePoints(pausePoints);
                }
                mVideoView.start();
                mVedioBar.setMax(mVideoView.getDuration());
                mVideoDuration.setText(TimeFormatUtils.formatLongToTimeStr(mVideoView.getDuration())+"");
                mHandler.sendEmptyMessageDelayed(3, 100);

            }
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                System.out.println("播放完成===========");
                isPlaying=false;
            }
        });
        mImageLists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageLists.showSelectArea(false);
            }
        });
    }

    /***
     * 获取控件
     */
    private void initView() {
        mVedioPath = Environment.getExternalStorageDirectory() + "/3354.mp4";
        mVideoDuration = findViewById(R.id.tv_duration);
        mVedioBar = (SeekBar)this.findViewById(R.id.vedio_progress);
        mPlayerController = findViewById(R.id.iv_play_pause);
        mVideoView = (VideoView) this.findViewById(R.id.vv_vedio_show);
        currentTime = findViewById(R.id.tv_progress_time);
        mScrollView = (ObservableScrollView) this.findViewById(R.id.sl);
        mScrollView.setScrollViewListener(this);
        ll_wave_content = (LinearLayout) this.findViewById(R.id.ll_scroll);
        ll_wave_content.setPadding(screenWidth / 2, 0, screenWidth / 2, 0);

        mImageLists = (EditVideoImageBar) this.findViewById(R.id.iv_show);
        View iv_btn_cut = findViewById(R.id.iv_btn_cut);
        iv_btn_cut.setOnClickListener(this);
        mRecyclerView = findViewById(R.id.video_thumb_listview);
        mRecyclerView
                .setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        videoEditAdapter = new TrimVideoBitAdapter(this, screenWidth);
        mRecyclerView.setAdapter(videoEditAdapter);
       // mRecyclerView.addOnScrollListener(mOnScrollListener);
    }




    @Override
    public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy, boolean isByUser) {
        if (x >= 0) {
            this.mCurrentPosition = x;

            mImageLists.showSelectArea(true);
            mBottomLength = mRecyclerView.getWidth();
//            System.out.println("mCurrentPosition====="+mCurrentPosition+"======="+mBottomLength);
            if (mBottomLength != 0) {
                mDragPosition = mDuration * x / mBottomLength;
            }
            if (!mVideoView.isPlaying() && !isPlaying) {
                mHandler.sendEmptyMessage(0);
            }
            currentTime.setText(TimeFormatUtils.formatLongToTimeStr(mDragPosition) + "");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_play_pause:
                playVideo();
                break;
            case R.id.iv_btn_cut:
                setCurrentPostion();
                break;
        }
    }

    /**
     * 进行断点处理
     */
    private void setCurrentPostion(){
        int width = mImageLists.getWidth();
        System.out.println("width===="+width);
        int max = mVedioBar.getMax();
        int progress = mVedioBar.getProgress();
        System.out.println("progress====="+progress+"=======max=="+max+"====="+((progress*100/ max)));
        int v = (int) (((double) (progress * 100 / max)) / 100 * width);

        mImageLists.setCutPostion(mCurrentPosition*width/mBottomLength,v);
    }

    private boolean isPlayCompletion=false;
    /**
     * 播放控制
     */
    private void playVideo() {

        if(isPlayCompletion){
            mDragPosition=0;
            mScrollView.scrollTo(mDragPosition, 0);
            isPlayCompletion=false;
        }
        if(mVideoView.isPlaying()){
            isPlaying=false;
            mVideoView.pause();
            mPlayerController.getDrawable().setLevel(1);
        }else{
            isPlaying=true;
//			mVideoView.seekTo(mDragPosition);
            mPlayerController.getDrawable().setLevel(2);
            mVideoView.start();
        }

        mHandler.sendEmptyMessageDelayed(0, 100);

    }

    /**
     * 初始化视频文件信息
     */
    private void initVideoInfo(){
        File playFile=new File(mVedioPath);
        if(playFile.exists()){
            new ExtractTask(mVedioPath).execute();//执行抽取关键帧的异步任务
        }else{
            Toast.makeText(this, "请检查视频文件是否存在！", 0).show();
        }
    }


    /**
     * 抽取关键帧的异步任务
     * @author howie
     *
     */
    class ExtractTask extends AsyncTask<Void, Void, Bitmap> {
        private String path;
        private ProgressDialog progressDialog1;

        public ExtractTask(String path) {
            super();
            this.path = path;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(progressDialog1==null){
                progressDialog1 = ProgressDialog.show(VideoFenGeActivity.this,
                        "加载中...", "请稍等...", true);
            }else{
                progressDialog1.show();
            }

        }
        @Override
        protected Bitmap doInBackground(Void... arg0) {

            return BitmapUtils.addHBitmap(addFrames(path));
        }
        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(Bitmap bitmaps) {
            super.onPostExecute(bitmaps);
            progressDialog1.dismiss();
            progressDialog1.cancel();
            progressDialog1=null;
            mImageLists.setBackground(new BitmapDrawable(bitmaps));
            mHandler.sendEmptyMessage(0);
        }

    }

    private List<Bitmap> addFrames(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        // 取得视频的长度(单位为毫秒)
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        int duration = Integer.parseInt(time);
        int toatalFrames = duration / 1000;
        keyFrameList = new ArrayList<Integer>();
        int interval = 0;

        for (int i = 0; i < toatalFrames; i++) {//
            int frameTime = Integer.valueOf(interval) / 1000;
            keyFrameList.add(frameTime);
            interval += duration / toatalFrames;
        }
        List<Bitmap> bits=new ArrayList<Bitmap>();
        for (int i = 0; i < keyFrameList.size(); i++) {
            int o = (int) keyFrameList.get(i);

            Bitmap bitmap = retriever.getFrameAtTime((long) ( o* 1000 * 1000), MediaMetadataRetriever.OPTION_CLOSEST_SYNC);


            if(bitmap!=null){
                int bmpWidth=bitmap.getWidth();
                int bmpHeight=bitmap.getHeight();
                System.out.println("bmpWidth===="+bmpWidth+"====bmpHeight==="+bmpHeight);
                float scale=(float) 0.7;
                /* 产生reSize后的Bitmap对象 */
                Matrix matrix = new Matrix();
                matrix.postScale(scale, scale);
                Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bmpWidth,
                        bmpHeight,matrix,true);

                bits.add(resizeBmp);


            }


        }

        retriever.release();
        retriever=null;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                videoEditAdapter.addItemVideoInfo(bits);

            }
        });
        return bits;
    }
}
