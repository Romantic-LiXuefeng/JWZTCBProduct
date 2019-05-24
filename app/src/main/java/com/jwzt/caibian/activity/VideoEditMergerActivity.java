package com.jwzt.caibian.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.jwzt.caibian.adapter.VideoFootageAdapter;
import com.jwzt.caibian.adapter.VideoMergerAdapter;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.bean.AttachsBeen;
import com.jwzt.caibian.bean.LoginBean;
import com.jwzt.caibian.interfaces.EmptyListener;
import com.jwzt.caibian.util.Config;
import com.jwzt.caibian.util.FileOperateUtil;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.ToastUtils;
import com.jwzt.caibian.view.CustomProgressDialog;
import com.jwzt.caibian.widget.DragSortListView;
import com.jwzt.caibian.xiangce.Bimp;
import com.jwzt.cb.product.R;
import com.qiniu.pili.droid.shortvideo.PLShortVideoComposer;
import com.qiniu.pili.droid.shortvideo.PLVideoEncodeSetting;
import com.qiniu.pili.droid.shortvideo.PLVideoSaveListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class VideoEditMergerActivity extends Activity implements View.OnClickListener,
        EmptyListener {
    private CbApplication app;
    private LoginBean loginBean;
    /**合并生成的视频的名字*/
    private String mUserId;
    /** 视频附件的集合 */
    private ArrayList<AttachsBeen> attachList = new ArrayList<AttachsBeen>();
    /**暂无素材*/
    private RelativeLayout rl_empty;
    //合并删除
    private TextView tv_merge,tv_delete;
    //可支持上下拖动排序的listView
    private DragSortListView lv;
    //适配器
    private VideoMergerAdapter videoFootageAdapter;
    //保存选中的资源路径集合
    private ArrayList<String> mergePaths=new ArrayList<String>();
    private AlertDialog alertDialog;
    private ImageView img_back;
    //视频合并
    private PLShortVideoComposer mShortVideoComposer;
    private CustomProgressDialog mProcessingDialog;//视频合成进度条
    private String mSrcVideoPath;//合并完成后保存的新视频路径

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    VideoEditMergerActivity.this.finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        setContentView(R.layout.videomerger_activity);
        app = (CbApplication) getApplication();
        loginBean = app.getmLoginBean();
        mUserId=loginBean.getUserID();
        mProcessingDialog = new CustomProgressDialog(this);

        findView();

        if(IsNonEmptyUtils.isList(Bimp.tempSelectBitmap)){
            Bimp.tempSelectBitmap.clear();
            Bimp.tempSelectBitmap.add(null);
        }else{
            Bimp.tempSelectBitmap.add(null);
        }

        attachList = readCache();
        if(attachList!=null&&!attachList.isEmpty()){
            initAdapter();
            rl_empty.setVisibility(View.GONE);
        }else{
            rl_empty.setVisibility(View.GONE);
        }

        tv_merge.setVisibility(View.VISIBLE);
        tv_delete.setVisibility(View.VISIBLE);
        for (AttachsBeen attachBean : attachList) {
            attachBean.setStatus(AttachsBeen.STATUS_UNSELECTED);//未选择状态
        }
        videoFootageAdapter.setEditing(true);
        videoFootageAdapter.notifyDataSetChanged();

    }

    private void findView(){
        img_back=(ImageView) findViewById(R.id.img_back);
        img_back.setOnClickListener(this);
        rl_empty=(RelativeLayout) findViewById(R.id.rl_empty);
        tv_merge=(TextView)findViewById(R.id.tv_merge);
        tv_merge.setOnClickListener(this);
        tv_delete=(TextView)findViewById(R.id.tv_delete);
        tv_delete.setOnClickListener(this);
        lv = (DragSortListView) findViewById(R.id.lv);

        lv.setDropListener(onDrop);
        lv.setRemoveListener(onRemove);
    }

    /**
     * 读取之前的保存在sharedPreference里面的素材
     * @return
     */
    private ArrayList<AttachsBeen> readCache(){
        if(IsNonEmptyUtils.isString(mUserId)){
            SharedPreferences sp=getSharedPreferences(mUserId, Context.MODE_PRIVATE);
            String cache = sp.getString("videofootages", "");//根据userId进行区分
            ArrayList<AttachsBeen> list;
            List<File> files;
            if(!TextUtils.isEmpty(cache)){
                list=(ArrayList<AttachsBeen>) JSON.parseArray(cache, AttachsBeen.class);
                files = FileOperateUtil.listFiles(Config.VIDEO_STORAGE_DIR+"videos/", "");
                if(IsNonEmptyUtils.isList(list)){//表示有采集的数据
                    for (int i = 0; i < list.size(); i++) {
                        AttachsBeen attachsBeen = list.get(i);
                        String achsPath=attachsBeen.getAchsPath();
                        if(!TextUtils.isEmpty(achsPath)){
                            String attchs="";
                            if(achsPath.startsWith("file:///")){
                                attchs=achsPath.replaceAll("file:///", "");
                            }else{
                                attchs=achsPath;
                            }

                            File file=new File(attchs);
                            if(!file.exists()){//如果这个文件已经不存在了,就不要显示了
                                list.remove(i);
                                i--;
                            }else{
                                attachsBeen.setStatus(AttachsBeen.STATUS_NORMAL);//都置为正常状态
                            }
                        }
                    }


                    if (IsNonEmptyUtils.isList(files)) {//表示有采集的数据且直播录制的视频文件不为空那么将直播录制的视频添加到素材中
                        for (File file : files) {
                            AttachsBeen been = new AttachsBeen();
                            been.setAchsPath(file.getAbsolutePath());
                            been.setStatus(AttachsBeen.STATUS_NORMAL);
                            if(isData(list, file.getAbsolutePath())){//true表示已在集合中 则不用在次添加

                            }else{//false表示不再集合中，则添加到集合中
                                if (been.getAchsPath().contains("pl-section-")) {

                                } else {
                                    list.add(been);
                                }
                            }
                        }
                    }
                    return list;
                }else{//如果采集的数据为空
                    if(IsNonEmptyUtils.isList(files)){//表示直播录制的视频不为空
                        list=new ArrayList<AttachsBeen>();
                        for (File file : files) {
                            AttachsBeen been = new AttachsBeen();
                            been.setAchsPath(file.getAbsolutePath());
                            been.setStatus(AttachsBeen.STATUS_NORMAL);
                            if (been.getAchsPath().contains("pl-section-")) {

                            } else {
                                list.add(been);
                            }
                        }
                        return list;
                    }else{
                        return null;
                    }
                }
            }else{
                files = FileOperateUtil.listFiles(Config.VIDEO_STORAGE_DIR+"videos/", "");
                if(IsNonEmptyUtils.isList(files)){//表示直播录制的视频不为空
                    list=new ArrayList<AttachsBeen>();
                    for (File file : files) {
                        AttachsBeen been = new AttachsBeen();
                        been.setAchsPath(file.getAbsolutePath());
                        been.setStatus(AttachsBeen.STATUS_NORMAL);
                        if (been.getAchsPath().contains("pl-section-")) {

                        } else {
                            list.add(been);
                        }
                    }
                    return list;
                }else{
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * 判断该条数据是否已在集合中
     * @return
     */
    private boolean isData(ArrayList<AttachsBeen> list,String absolutePath){
        if(IsNonEmptyUtils.isList(list)){
            for(int i=0;i<list.size();i++){
                if(absolutePath.equals(list.get(i).getAchsPath())){
                    return true;
                }
            }
        }else{
            return false;
        }
        return false;
    }

    /**
     * 为适配器填充数据
     */
    private void initAdapter() {
        if (videoFootageAdapter == null) {
            videoFootageAdapter = new VideoMergerAdapter(VideoEditMergerActivity.this, attachList,loginBean.getUserID());
            videoFootageAdapter.setEmptyListener(VideoEditMergerActivity.this);
            lv.setAdapter(videoFootageAdapter);
        } else {
            videoFootageAdapter.update(attachList);
            lv.setSelection(0);
        }
    }

    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
        @Override
        public void drop(int from, int to) {
            AttachsBeen item = videoFootageAdapter.getItem(from);
            if(from==videoFootageAdapter.getCurrentPlaying()){//如果移动之前处于播放状态，就修改正在播放的index为to
                videoFootageAdapter.setCurrentPlaying(to);
            }
            videoFootageAdapter.remove(item);
            videoFootageAdapter.insert(item, to);
        }
    };

    private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
        @Override
        public void remove(int which) {
            videoFootageAdapter.remove(videoFootageAdapter.getItem(which));

        }
    };


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_back:
                VideoEditMergerActivity.this.finish();
                break;
            case R.id.tv_merge://合并
                if(IsNonEmptyUtils.isList(Bimp.tempSelectBitmap)){
                    mergePaths.clear();//清空之前的集合
                    for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
                        if(Bimp.tempSelectBitmap.get(i)!=null){
                            String selectPath = Bimp.tempSelectBitmap.get(i).getFilepath();
                            if(new File(selectPath).exists()){
                                mergePaths.add(selectPath);
                            }
                        }
                    }
                }

                if(!IsNonEmptyUtils.isList(mergePaths)){//如果一个视频都没有选中
                    Toast.makeText(VideoEditMergerActivity.this, "您还没有选择任何视频", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(mergePaths.size()==1){
                    Toast.makeText(VideoEditMergerActivity.this, "一个视频不能进行合并操作", Toast.LENGTH_SHORT).show();
                    return;
                }

                VideoMerger(mergePaths);
                break;
            case R.id.tv_delete://删除按钮
                if(attachList!=null&&!attachList.isEmpty()){
                    for (int i = 0; i < attachList.size(); i++) {
                        AttachsBeen attachsBeen = attachList.get(i);
                        if(attachsBeen.getStatus()==AttachsBeen.STATUS_SELECTED){//只有选中了视频素材的时候才弹出对话框
                            showTip();
                            break;
                        }
                    }
                }
                break;
            case R.id.tv_yes://确认删除
                alertDialog.dismiss();
                if(attachList!=null&&!attachList.isEmpty()){
                    for (int i = 0; i < attachList.size(); i++) {
                        AttachsBeen attachsBeen = attachList.get(i);
                        if(attachsBeen.getStatus()==AttachsBeen.STATUS_SELECTED){
                            String achsPath = attachsBeen.getAchsPath();//文件路径
                            File file1=new File(achsPath);
                            if(file1.exists()){
                                file1.delete();
                            }
                            attachList.remove(i);
                            i--;
                        }
                    }
//                    saveVideoFootages();
                }
                if(attachList!=null&&attachList.isEmpty()){//如果清空了数据
                    if(videoFootageAdapter!=null){
                        videoFootageAdapter.setEditing(false);
                        rl_empty.setVisibility(View.GONE);
                    }
                }
                videoFootageAdapter.notifyDataSetChanged();
                break;
            case R.id.tv_no://否
                alertDialog.dismiss();
                break;
        }
    }

    //合成视频
    private void VideoMerger(ArrayList<String> pathList){
        mProcessingDialog.setProgress(0);
        mProcessingDialog.setMessage("正在合并，请稍后...");
        mProcessingDialog.show();
        mShortVideoComposer = new PLShortVideoComposer(this);
        PLVideoEncodeSetting setting = new PLVideoEncodeSetting(VideoEditMergerActivity.this);
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
    }

    //合成状态监听
    private PLVideoSaveListener mVideoSaveListener = new PLVideoSaveListener() {
        @Override
        public void onSaveVideoSuccess(final String destFile) {
            mProcessingDialog.dismiss();
            Log.i("===合并状态===>>", "合并完成:"+destFile);
            mSrcVideoPath=destFile;
            mHandler.sendEmptyMessage(1);
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

    /**
     * 显示是否进行删除的提示
     */
    private void showTip(){
        if (alertDialog == null) {
            alertDialog = new AlertDialog.Builder(VideoEditMergerActivity.this).create();
            DialogInterface.OnKeyListener keylistener = new DialogInterface.OnKeyListener() {
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                        return true;
                    } else {
                        return false;
                    }
                }
            };
            alertDialog.setOnKeyListener(keylistener);//保证按返回键的时候alertDialog也不会消失


        }
        alertDialog.show();
        View tipView = View.inflate(VideoEditMergerActivity.this, R.layout.edit_alert_layout, null);
        TextView tv_tip=(TextView) tipView.findViewById(R.id.tv_tip);
        tv_tip.setText("确认要删除选中的素材吗？");
        View tv_yes = (TextView) tipView.findViewById(R.id.tv_yes);
        //不再提醒
        tv_yes.setOnClickListener(this);
        tipView.findViewById(R.id.tv_no).setOnClickListener(this);

        alertDialog.setContentView(tipView);

    }


    @Override
    public void empty() {
        rl_empty.setVisibility(View.GONE);
    }
}
