package com.jwzt.caibian.fragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jwzt.caibian.activity.ChuanLianDaishenGaodanActivity;
import com.jwzt.caibian.activity.ChuanLianGaodanYiShenActivity;
import com.jwzt.caibian.activity.HomeGvShowActivity;
import com.jwzt.caibian.activity.MyGaojianActivity;
import com.jwzt.caibian.activity.NotifyActivity;
import com.jwzt.caibian.activity.PreviewUploadedActivity;
import com.jwzt.caibian.activity.PreviewWanCHengActivity;
import com.jwzt.caibian.activity.SelectTiActivity;
import com.jwzt.caibian.activity.TaskDetailActivity;
import com.jwzt.caibian.activity.XianSuoActivity;
import com.jwzt.caibian.adapter.DragAdapter;
import com.jwzt.caibian.bean.ChannelItem;
import com.jwzt.caibian.bean.ChannelManage;
import com.jwzt.caibian.dialog.AlbumConfigDialog;
import com.jwzt.caibian.dialog.AudioConfigDialog;
import com.jwzt.caibian.dialog.CameraConfigDialog;
import com.jwzt.caibian.dialog.CompressConfigDialog;
import com.jwzt.caibian.dialog.ConfigData;
import com.jwzt.caibian.dialog.ConfigDialogListener;
import com.jwzt.caibian.dialog.EditorUIAndExportConfigDialog;
import com.jwzt.caibian.dialog.OsdConfigDialog;
import com.jwzt.caibian.dialog.VideoTrimConfigDialog;
import com.jwzt.caibian.rd.CameraWatermarkBuilder;
import com.jwzt.caibian.rd.DraftListActivity;
import com.jwzt.caibian.rd.authpack;
import com.jwzt.caibian.util.DensityUtil;
import com.jwzt.caibian.util.SDKUtils;
import com.jwzt.caibian.view.PullToRefreshLayout;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.activity.BackTransferActivity;
import com.jwzt.caibian.activity.ChuanLianShenheActivity;
import com.jwzt.caibian.activity.FeedBackActivity;
import com.jwzt.caibian.activity.LiveListActivity;
import com.jwzt.caibian.activity.MainActivity;
import com.jwzt.caibian.activity.NewScriptActivity;
import com.jwzt.caibian.activity.ShenheActivity;
import com.jwzt.caibian.activity.ShowMessageDeatilsActivity;
import com.jwzt.caibian.activity.TaskActivity;
import com.jwzt.caibian.application.BaseFragment;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.application.Configs;
import com.jwzt.caibian.bean.AttachsBeen;
import com.jwzt.caibian.bean.ChatMessageBean;
import com.jwzt.caibian.bean.IndexCountBean;
import com.jwzt.caibian.bean.LocationNoUploadBean;
import com.jwzt.caibian.bean.LoginBean;
import com.jwzt.caibian.bean.MessageAllBean;
import com.jwzt.caibian.bean.TaskListBean;
import com.jwzt.caibian.db.ChatsDao;
import com.jwzt.caibian.db.DatabaseHelper;
import com.jwzt.caibian.db.LocationUploadDao;
import com.jwzt.caibian.util.BitmapUtils;
import com.jwzt.caibian.util.DialogHelp;
import com.jwzt.caibian.util.FileOperateUtil;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.Parser;
import com.jwzt.caibian.util.TimeUtil;
import com.jwzt.caibian.util.UIUtils;
import com.jwzt.caibian.util.UserToast;
import com.jwzt.caibian.widget.CircleImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rd.lib.utils.CoreUtils;
import com.rd.vecore.VirtualVideo;
import com.rd.vecore.exception.InvalidArgumentException;
import com.rd.vecore.listener.ExportListener;
import com.rd.vecore.models.Trailer;
import com.rd.vecore.models.VideoConfig;
import com.rd.vecore.models.Watermark;
import com.rd.veuisdk.SdkEntry;
import com.rd.veuisdk.SdkService;
import com.rd.veuisdk.callback.ICompressVideoCallback;
import com.rd.veuisdk.manager.CameraConfiguration;
import com.rd.veuisdk.manager.CompressConfiguration;
import com.rd.veuisdk.manager.EditObject;
import com.rd.veuisdk.manager.ExportConfiguration;
import com.rd.veuisdk.manager.FaceuInfo;
import com.rd.veuisdk.manager.TrimConfiguration;
import com.rd.veuisdk.manager.UIConfiguration;
import com.rd.veuisdk.manager.VEOSDBuilder;
import com.rd.veuisdk.manager.VideoMetadataRetriever;

import static android.app.Activity.RESULT_OK;
import static com.rd.veuisdk.SdkEntry.editMedia;
import static com.rd.veuisdk.SdkEntry.getSdkService;
import static com.rd.veuisdk.SdkEntry.trimVideo;
import static com.rd.veuisdk.manager.CameraConfiguration.SQUARE_SCREEN_CAN_CHANGE;

@SuppressLint({"HandlerLeak", "ValidFragment"})
public class HomeFragment extends BaseFragment implements OnClickListener {
    private static final String TAG = "HomeFragment";
    private ListView lv;
    private ScrollView sv;
    private TextView tv_allNum, tv_sent, tv_caiji, tv_inter;
    /*** 设置按钮*/
    private View iv_setting;
    /***暂无*/
    private RelativeLayout rl_title,rl_zanwu;
    /**
     * 测试用水印图片
     */
    private String EDIT_WATERMARK_PATH = Environment
            .getExternalStorageDirectory() + "/watermark.png";
    /**
     * 导出的横向16:9视频
     */
    private String EDIT_L_VIDEO_PATH = Environment.getExternalStorageDirectory() + "/JWZTCBProduct/videos/"+ System.currentTimeMillis()+".mp4";
    /**
     * 测试用1：1方型视频
     */
    private String EDIT_S_VIDEO_PATH = Environment.getExternalStorageDirectory() + "/JWZTCBProduct/videos/"+ System.currentTimeMillis()+".mp4";
    /**
     * 测试用竖向9:16视频
     */
    private String EDIT_P_VIDEO_PATH =Environment.getExternalStorageDirectory() + "/JWZTCBProduct/videos/"+ System.currentTimeMillis()+".mp4";


    private String MASK_PATH = Environment.
            getExternalStorageDirectory() + "/mask.png";


    /**
     * REQUEST_CODE定义：<br>
     * 录制
     */
    private final int CAMERA_REQUEST_CODE = 100;
    /**
     * REQUEST_CODE定义：<br>
     * 相册
     */
    private final int ALBUM_REQUEST_CODE = 101;

    /**
     * 从相册选折要压缩的文件
     */
    private final int ALBUM_COMPRESS_REQUEST_CODE = 1011;
    /**
     * 从相册选折要播放的视频
     */
    private final int ALBUM_PLAYER_REQUEST_CODE = 1012;

    /**
     * 防篡改录制演示
     */
    private final int CAMERA_ANTI_CHANGE_REQUEST_CODE = 1013;


    /**
     * 动画演示
     */
    private final int ALBUM_ANIMATION_REQUEST_CODE = 1014;

    /**
     * 异形显示
     */
    private final int ALBUM_POINTF_REQUEST_CODE = 1040;


    /**
     * 仿quik
     */
    private final int ALBUM_QUIK_REQUEST_CODE = 1050;

    /**
     * 动画导出完成
     */
    private final int ANIMATION_RESULT_CODE = 1015;

    /**
     * 从相册选择设置音效的视频
     */
    private final int ALBUM_SOUND_EFFECT_REQUEST_CODE = 1016;

    /**
     * 从相册选择剪影视频
     */
    private final int ALBUM_SILHOUETT_REQUEST_CODE = 1017;

    /**
     * 从相册选择AE动画原图（仅支持图片）
     */
    private final int ALBUM_AE_IMAGE_REQUEST_CODE = 1018;

    /**
     * 从相册选择异形原图
     */
    private final int ALBUM_ALIEN_REQUEST_CODE = 1020;


    /**
     * 仿剪影流程
     */
    private final int REQUEST_AE_LIST = 1021;

    /**
     * REQUEST_CODE定义：<br>
     * 视频编辑
     */
    private final int EDIT_REQUEST_CODE = 102;
    /**
     * REQUEST_CODE定义：<br>
     * 读取外置存储
     */
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSIONS = 1;

    /**
     * REQUEST_CODE定义：<br>
     * 视频截取
     */
    private final int TRIM_REQUEST_CODE = 103;
    /**
     * REQUEST_CODE定义：<br>
     * 视频截取相册选择
     */
    private final int TRIM_ALBUM_REQUEST_CODE = 104;

    /**
     * 短视频录制演示request_code
     */
    private final int SHORTVIDEO_CAMERA_REQUEST_CODE = 110;
    /**
     * 短视频录制进入相册演示request_code
     */
    private final int SHORTVIDEO_ALBUM_REQUEST_CODE = 111;
    /**
     * 短视频录制进入相册进入截取演示request_code
     */
    private final int SHORTVIDEO_TRIM_REQUEST_CODE = 112;


    /**
     * 选折导出的文件(仅视频)
     */
    private final int ALBUM_REQUEST_EXPORT_CODE = 114;



    private PullToRefreshLayout prl_refersh;

    private CircleImageView mHeadImg;
    private TextView tv_group, username;
//    private TextView  tv_messagenum;

    private ChatsDao chatsDao;
    private List<ChatMessageBean> chatmessageList;
    private MessageAdapter messageAdapter;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private CbApplication application;
    private LoginBean mLoginBean;
    private List<TaskListBean> mListTask;
    private List<MessageAllBean> mList;//获取首页列表
    private List<MessageAllBean> mListMore;//获取首页列表加载更多
    private IndexCountBean indexCountBean;//统计首页数据
    private int pageSize = 20;//表示每页一次加载的条目数
    private int pageTag = 0;//表示是刷新状态还是加载更多状态0表示刷新，1表示加载更多
    private int currentPage = 1;
    private List<LocationNoUploadBean> LocationNoUploadList;
    private int headTop;
    private RelativeLayout rl_head;
private int OnClickItemIndex=-1;
    private boolean more;// 是否是收起的

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (!IsNonEmptyUtils.isList(mList)) {
                        rl_zanwu.setVisibility(View.VISIBLE);
                        lv.setVisibility(View.GONE);
                    }
                    DialogHelp.dismisLoadingDialog();
                    break;
                case 1:
                    initView();
                    break;
                case 2:
                    if (messageAdapter != null) {
                        int size = mList.size();
                        mList.addAll(mListMore);
                        messageAdapter.setList(mList);
                        lv.setSelection(size);
                        messageAdapter.notifyDataSetChanged();
                        DialogHelp.dismisLoadingDialog();
                    }
                    break;
                case 3://更新首页统计数据UI
                    initViewTop();
                    DialogHelp.dismisLoadingDialog();
                    break;
                case 4:
                    if (IsNonEmptyUtils.isList(mListTask)) {
                        tv_inter.setText(mListTask.size() + "");
                    } else {
                        tv_inter.setText(0 + "");
                    }
                    break;
                case 88:
                    ChannelItem radioStationBean = userChannelList.get(7);
//                    imageLoader.displayImage(Configs.Base_img + radioStationBean.getPic(), iv8,options);
                    tv8.setText(radioStationBean.getName());
                    break;
                case 77:
                    iv8.setImageResource(R.drawable.more);
                    tv8.setText("更多");
                    break;
                case 100:
                    rl_move.setPadding(0, 0, 0, 0);
                    break;
                case 101:
                    rl_move.setPadding(0, 0, 0, move_height);
                    break;
                case 11:
                    RelativeLayout.LayoutParams layoutParams = (android.widget.RelativeLayout.LayoutParams) rl_move.getLayoutParams();
                    // 在布局文件中rl_move的margin_top值设置的是-167dp即两行的高度；
                    // 动态设置rl_move这个相对布局的margin_top
                    if (userChannelList.size() < 9) {
                        // 如果不超过8条，就没有"展开”的功能，因此就设置margin_top为0
                        layoutParams.topMargin = 0;
                    } else {
                        // 经测算，电台页最上面的gridView一行的高度是83.5dp;
                        // list.size()/4-1=(list.size()/4+1)-2
                        move_height = DensityUtil.dip2px((float) 83.5) * (userChannelList.size() / 4 - 1);
                        layoutParams.topMargin = -1 * move_height;
                    }
                    // 得到move_height之后去根据move_height初始化动画资源
                    init();
                    // 重置rl_move的marginTop
                    rl_move.setLayoutParams(layoutParams);
                    initViewGridView();
                    break;
            }
        }
    };


    private DatabaseHelper mDatabaseHelper;

    private RelativeLayout rl_move;
    private GridView gv_main_home;
    private ArrayList<ChannelItem> userChannelList;
    private DragAdapter userAdapter;
    //    private ChannelGridViewAdapter userAdapter;
    private ImageView iv8;// 第8项对应的imageView
    private TextView tv8;// 第8项对应的textView
    /*** 垂直向下移动的位移动画 */
    private ObjectAnimator oa_down;
    /*** 垂直向上移动的位移动画 */
    private ObjectAnimator oa_up;
    //判断是否点击item
    private boolean isClick;
    /*** 半个动画的周期 */
    private static final int HALF_DURATION = 150;// 250毫秒,全局控制
    /*** rl_move这个相对布局做垂直方向的动画的时候垂直方向移动的距离 */
    private int move_height;
    private ImageView img_addplus;

    public HomeFragment(DatabaseHelper databaseHelper) {
        this.mDatabaseHelper = databaseHelper;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.mine_layout, null);
        application = (CbApplication) getActivity().getApplication();
        mLoginBean = application.getmLoginBean();
        chatsDao = new ChatsDao(mDatabaseHelper);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.headdefault) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.headdefault) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.headdefault) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(false) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .bitmapConfig(Config.RGB_565)
//        .displayer(new FadeInBitmapDisplayer(100))
                .build(); // 构建完成
        imageLoader = ImageLoader.getInstance();

        fiidViews(view);
        DialogHelp.showLoadingDialog(getActivity(), "", "");
        initMyData();
        restoreConfigInstanceState();
        boolean hasM = CoreUtils.hasM();
        if (hasM && !SdkEntry.isInitialized()) {
            int re = getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (re != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSIONS);
            }
        } else {
            System.out.println();
            exportDemoResource();
        }


        // 初始化秀拍客配置
        initEditorUIAndExportConfig();
        registerAllResultHandlers();
        return view;
    }
    private String EDIT_PICTURE_PATH = Environment
            .getExternalStorageDirectory() + "/android.jpg";




    private void initMyData() {
        userChannelList = ((ArrayList<ChannelItem>) ChannelManage.getManage(CbApplication.getApp().getSQLHelper()).getUserChannel());
        userAdapter = new DragAdapter(getActivity(), userChannelList);
        gv_main_home.setAdapter(userAdapter);
    }

    private void init() {
        //获取手机像素密度
        DisplayMetrics metric = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
        float density = metric.density;
        // 垂直向下移动的动画
        oa_down = ObjectAnimator.ofFloat(rl_move, "translationY", DensityUtil.dip2px(move_height / density));
        oa_down.setDuration(HALF_DURATION * 2);// 半个周期乘以2即一个周期
        // 垂直向上移动的动画
        oa_up = ObjectAnimator.ofFloat(rl_move, "translationY", 0);
        oa_up.setDuration(HALF_DURATION * 2);// 半个周期乘以2即一个周期
    }

    /**
     * 初始化电台列表
     */
    private void initViewGridView() {
        if (userAdapter == null) {
            userAdapter = new DragAdapter(getActivity(), userChannelList);
            gv_main_home.setAdapter(userAdapter);
        } else {
            userAdapter = new DragAdapter(getActivity(), userChannelList);
            gv_main_home.setAdapter(userAdapter);
//            userAdapter.setmMore(more);
        }
    }

    /**
     * 计算当前用户的素材个数
     *
     * @param mUserId
     * @return
     */
    private int sucaiSize(String mUserId) {
        SharedPreferences sp = getActivity().getSharedPreferences(mUserId, Context.MODE_PRIVATE);
        String cacheVideo = sp.getString("videofootages", "");//根据userId进行区分
        String cacheImg = sp.getString("imagefootages_" + mLoginBean.getUsername(), "");//根据userId进行区分
        String cacheAudio = sp.getString("audiofootages_" + mLoginBean.getUsername(), "");//根据userId进行区分
        int videoSize, imgSize, audioSize, liveSize;
        if (IsNonEmptyUtils.isString(cacheVideo)) {//视频个数
            ArrayList<AttachsBeen> listVideo = (ArrayList<AttachsBeen>) JSON.parseArray(cacheVideo, AttachsBeen.class);
            videoSize = listVideo.size();
        } else {
            videoSize = 0;
        }

        if (IsNonEmptyUtils.isString(cacheImg)) {//图片个数
            ArrayList<AttachsBeen> listImg = (ArrayList<AttachsBeen>) JSON.parseArray(cacheImg, AttachsBeen.class);
            imgSize = listImg.size();
        } else {
            imgSize = 0;
        }

        if (IsNonEmptyUtils.isString(cacheAudio)) {//音频个数
            ArrayList<AttachsBeen> listAudio = (ArrayList<AttachsBeen>) JSON.parseArray(cacheAudio, AttachsBeen.class);
            audioSize = listAudio.size();
        } else {
            audioSize = 0;
        }

        List<File> files = FileOperateUtil.listFiles(Configs.BLSavePath, "");
        if (IsNonEmptyUtils.isList(files)) {
            liveSize = files.size();
        } else {
            liveSize = 0;
        }

        return videoSize + imgSize + audioSize + liveSize;
    }

    /**
     * 请求网络数据
     */
    private void initData() {
        pageTag = 0;
        if (mLoginBean != null) {
            if (CbApplication.getNetType() != -1) {
                DialogHelp.showLoadingDialog(getActivity(), "", "");
                /*String infoListUrl=String.format(Configs.infoListUrl, mLoginBean.getUserID());
                System.out.println("infoListUrl:"+infoListUrl);
				RequestData(infoListUrl, Configs.infoListCode, pageTag);*/

                String infoListUrl = String.format(Configs.xiaoxiListUrl, mLoginBean.getUserID(), pageSize, "");
                System.out.println("infoListUrl:" + infoListUrl);
                RequestData(infoListUrl, Configs.infoListCode, pageTag);

                String countUrl = String.format(Configs.countUrl, mLoginBean.getUserID());
                System.out.println("countUrl" + countUrl);
                RequestData(countUrl, Configs.countCode, -1);
                //任务列表
                /*String taskUrl=String.format(Configs.taskListUrl, mLoginBean.getUserID());
                System.out.println("taskListUrl:"+taskUrl);
				RequestData(taskUrl, Configs.taskListCode, -1);*/
            } else {
                UserToast.toSetToast(UIUtils.getContext(), getString(R.string.please_check_net));
            }
        }
    }

    /**
     * 加载更多
     */
    private void moreData() {
        pageTag = 1;
        if (mLoginBean != null) {
            if (CbApplication.getNetType() != -1) {
                if (mList != null && mList.size() > 0) {
                    ++currentPage;
                    String startId = mList.get(mList.size() - 1).getId();
                    String infoListUrl = String.format(Configs.xiaoxiListUrl, mLoginBean.getUserID(), pageSize, startId);
                    RequestData(infoListUrl, Configs.infoListCode, pageTag);
                } else {
                    UserToast.toSetToast(UIUtils.getContext(), "暂无更多数据");
                }
            } else {
                UserToast.toSetToast(UIUtils.getContext(), getString(R.string.please_check_net));
            }
        }
    }

    /**
     * 初始化列表
     */
    private void initView() {
        if (IsNonEmptyUtils.isList(mList)) {
            rl_zanwu.setVisibility(View.GONE);
            lv.setVisibility(View.VISIBLE);
            messageAdapter = new MessageAdapter(getActivity(), mList);
            lv.setAdapter(messageAdapter);
        }
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(getActivity(), ShowMessageDeatilsActivity.class);
//                intent.putExtra("userid", mLoginBean.getUserID());
//                intent.putExtra("ids", mList.get(position).getId());
//                if(mList.get(position).getState().equals("1")) {
//                    intent.putExtra("type", "0");
//                }else{
//                    intent.putExtra("type", "1");
//                }
//                intent.putExtra("messageid", mList.get(position).getId());
//                getActivity().startActivity(intent);
                OnClickItemIndex=position;
                MessageAllBean messageAllBean1 = mList.get(position);
                String parentType = messageAllBean1.getParentType();
                System.out.println("parentType====="+parentType);
                Intent intent = new Intent();

                switch (Integer.parseInt(parentType)){
                    case 1:
                        intent.setClass(getActivity(), TaskDetailActivity.class);
//                        intent.putExtra("id",messageAllBean1.getParentId());
                        intent.putExtra("id",messageAllBean1.getParentId());
                        intent.putExtra("pid",messageAllBean1.getId());
                        intent.putExtra("state",messageAllBean1.getState());
                        System.out.println("state======="+messageAllBean1.getState());
                        getActivity().startActivityForResult(intent,500);
                        break;
                    case 2:
                        intent.setClass(getActivity(),PreviewWanCHengActivity.class);
                        System.out.println("pid====="+messageAllBean1.getId());
                        intent.putExtra("id",messageAllBean1.getParentId());
                        intent.putExtra("state",messageAllBean1.getState());
                        intent.putExtra("pid",messageAllBean1.getId());

                        intent.putExtra("operateType",messageAllBean1.getParentType());
                        getActivity().startActivityForResult(intent,500);
                        break;
                    case 3:
                        intent.setClass(getActivity(),PreviewWanCHengActivity.class);
                        System.out.println("pid====="+messageAllBean1.getId());
                        intent.putExtra("pid",messageAllBean1.getId());
                        intent.putExtra("id",messageAllBean1.getParentId());
                        intent.putExtra("state",messageAllBean1.getState());
                        intent.putExtra("operateType",messageAllBean1.getParentType());
                        getActivity().startActivityForResult(intent,500);
                        break;
                    case 4:
                        intent.setClass(getActivity(),PreviewWanCHengActivity.class);
                        intent.putExtra("pid",messageAllBean1.getId());
                        System.out.println("pid====="+messageAllBean1.getId());
                        intent.putExtra("id",messageAllBean1.getParentId());
                        intent.putExtra("state",messageAllBean1.getState());
                        intent.putExtra("operateType",messageAllBean1.getParentType());

                        getActivity().startActivityForResult(intent,500);
                        break;
                    case 5://通知
                        intent.setClass(getActivity(),NotifyActivity.class);

                        intent.putExtra("state",messageAllBean1.getState());
                        intent.putExtra("messageAllBean",messageAllBean1);
                        intent.putExtra("type","5");
                        getActivity().startActivityForResult(intent,500);
                        break;
                    case 6://通知
                        intent.setClass(getActivity(),NotifyActivity.class);
                        System.out.println("state===="+messageAllBean1.getState());
                        intent.putExtra("state",messageAllBean1.getState());
                        intent.putExtra("messageAllBean",messageAllBean1);
                        intent.putExtra("type","6");
                        getActivity().startActivityForResult(intent,500);
                        break;
                    case 7://通知
                        intent.setClass(getActivity(),NotifyActivity.class);
                        intent.putExtra("type","7");
                        intent.putExtra("state",messageAllBean1.getState());
                        intent.putExtra("messageAllBean",messageAllBean1);
                        getActivity().startActivityForResult(intent,500);
                        break;
                    case 8://通知
                        intent.setClass(getActivity(),NotifyActivity.class);
                        intent.putExtra("type","8");
                        intent.putExtra("state",messageAllBean1.getState());
                        intent.putExtra("messageAllBean",messageAllBean1);
                        getActivity().startActivityForResult(intent,500);
                        break;
                    case 9://串联单
                        intent.setClass(getActivity(),ChuanLianDaishenGaodanActivity.class);

                        intent.putExtra("type","2");
                        intent.putExtra("id",messageAllBean1.getParentId());
                        intent.putExtra("messageAllBean",messageAllBean1);
                        getActivity().startActivityForResult(intent,500);
                        break;

                    case 10://串联单
                        intent.setClass(getActivity(),ChuanLianGaodanYiShenActivity.class);

                        intent.putExtra("type","2");
                        intent.putExtra("id",messageAllBean1.getParentId());
                        intent.putExtra("messageAllBean",messageAllBean1);
                        getActivity().startActivityForResult(intent,500);
                        break;
                    case 11://串联单
                        intent.setClass(getActivity(),ChuanLianGaodanYiShenActivity.class);

                        intent.putExtra("type","2");
                        intent.putExtra("id",messageAllBean1.getParentId());
                        intent.putExtra("messageAllBean",messageAllBean1);
                        getActivity().startActivityForResult(intent,500);
                        break;
                    case 12://通知
                        intent.setClass(getActivity(),NotifyActivity.class);
                        intent.putExtra("type","12");
                        intent.putExtra("state",messageAllBean1.getState());
                        intent.putExtra("messageAllBean",messageAllBean1);
                        getActivity().startActivityForResult(intent,500);
                        break;
                }
            }
        });
        DialogHelp.dismisLoadingDialog();
    }

    /**
     * 更新头部数据
     */
    private void initViewTop() {
        LocationUploadDao noUpLoadtask = new LocationUploadDao(mDatabaseHelper);
        int sucaiSize = sucaiSize(mLoginBean.getUserID());
        LocationNoUploadList = noUpLoadtask.getTasks(new Integer(mLoginBean.getUserID()));
        if (indexCountBean != null) {
            int mediaNum = indexCountBean.getMediaNum();
            int locationsize = LocationNoUploadList.size();
            tv_allNum.setText((mediaNum + locationsize) + "");
            tv_sent.setText(mediaNum + "");
            tv_caiji.setText(indexCountBean.getResourceNum() + sucaiSize + "");
            tv_inter.setText(indexCountBean.getTaskNum() + "");
        }

        headTop=gv_main_home.getTop();
        Log.i("===headTop===>>",headTop+"");
    }

    /**
     * 实例化控件
     *
     * @param view
     */
    @SuppressLint("NewApi")
    private void fiidViews(View view) {
        rl_head = (RelativeLayout) view.findViewById(R.id.rl_head);
        rl_head.setBackgroundResource(R.drawable.bg0);

        mHeadImg = (CircleImageView) view.findViewById(R.id.civ);
        tv_group = (TextView) view.findViewById(R.id.tv_group);
        username = (TextView) view.findViewById(R.id.username);
        lv = (ListView) view.findViewById(R.id.mlv);
        lv.setFocusable(false);
        sv = (ScrollView) view.findViewById(R.id.sv);
        sv.scrollTo(0, 20);
        sv.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int x, int y, int oldx, int oldy) {
                Log.i("===scroll===>>",x+"==="+y+"==="+oldx+"==="+oldy);
                if(y>=headTop){
                    rl_title.setVisibility(View.VISIBLE);
                }else{
                    rl_title.setVisibility(View.GONE);
                }
            }
        });
        rl_title=view.findViewById(R.id.rl_title);
        rl_title.setVisibility(View.GONE);
        iv_setting = view.findViewById(R.id.iv_setting);
        iv_setting.setOnClickListener(this);
        img_addplus = view.findViewById(R.id.img_addplus);
        img_addplus.setOnClickListener(this);
        gv_main_home = (GridView) view.findViewById(R.id.gv_main_home);
        gv_main_home.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gv_main_home.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String name=userChannelList.get(position).getName();
                if(name.equals("快速发稿")){
                    Intent intent = new Intent(getActivity(), NewScriptActivity.class);
                    intent.putExtra("isFromIndex", true);
                    intent.putExtra("tag", "speediness");
                    startActivityForResult(intent, 1);
                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }else if(name.equals("任务管理")){
                    Intent intent_task = new Intent(getActivity(), TaskActivity.class);
                    startActivity(intent_task);
                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }else if(name.equals("直播回传")){
                    Intent intent_live = new Intent(getActivity(), LiveListActivity.class);
                    startActivity(intent_live);
                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }else if(name.equals("回传管理")){
                    Intent intent_back = new Intent(getActivity(), BackTransferActivity.class);
                    intent_back.putExtra("tag", "backmanager");
                    startActivity(intent_back);
                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }else if(name.equals("移动审核")){//name.equals("稿件审核")
                    Intent intent_hen = new Intent(getContext(), ShenheActivity.class);
                    startActivity(intent_hen);
                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }else if(name.equals("串联单")){
                    Intent intent_chuan = new Intent(getActivity(), ChuanLianShenheActivity.class);
                    startActivity(intent_chuan);
                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }else if(name.equals("我的稿件")){
                    Intent intent_mygaojian=new Intent(getActivity(), MyGaojianActivity.class);
                    startActivity(intent_mygaojian);
                }else if(name.equals("视频素材")){

                }else if(name.equals("选题")){
                    Intent intent_xuanti=new Intent(getActivity(), SelectTiActivity.class);
                    startActivity(intent_xuanti);
                }else if(name.equals("线索")){
                    Intent intent_xiansuo=new Intent(getActivity(), XianSuoActivity.class);
                    startActivity(intent_xiansuo);
                }else if(name.equals("视频录制")){
                    SdkEntry.registerOSDBuilder(CameraWatermarkBuilder.class);
                    CameraWatermarkBuilder.setText("北京经纬中天");// 可自定义水印显示文本
                    initCameraConfig(SQUARE_SCREEN_CAN_CHANGE);
                    SdkEntry.record(getContext(), CAMERA_REQUEST_CODE);
//                    initEditorUIAndExportConfig();
//                    initCameraConfig(SQUARE_SCREEN_CAN_CHANGE);
////                SdkEntry.getSdkService().initConfiguration(
////                        new CameraConfiguration.Builder().get());
//                    ArrayList<String> list = new ArrayList<String>();
//                    list.add(EDIT_PICTURE_PATH);
//                    list.add(EDIT_L_VIDEO_PATH);
//                    try {
//                        editMedia(getActivity(), list, EDIT_REQUEST_CODE);
//                    } catch (InvalidArgumentException e) {
//                        e.printStackTrace();
//                    }
                }else if(name.equals("电子相册")){

                    UIConfiguration.Builder builder = new UIConfiguration.Builder()
                            // 设置是否使用自定义相册
                            .useCustomAlbum(false)
                            // 设置秀拍客相册支持格式
                            .setAlbumSupportFormat(UIConfiguration.ALBUM_SUPPORT_DEFAULT)
                            // 设置滤镜界面风格
                            // 设置相册媒体选择数量上限(目前只对相册接口生效)
                            .setMediaCountLimit(50)
                            // 设置相册是否显示跳转拍摄按钮(目前只对相册接口生效)
                            .enableAlbumCamera(false);


                    ConfigData configData1 = new ConfigData();
                    configData1.enableMV = true;
                    configData1.customApi = configData.customApi;
                    initThridServer(builder, configData1);

                    // 获取秀拍客配置服务器
                    SdkService sdkService = getSdkService();
                    if (null != sdkService) {
                        // 初始化所有配置
                        sdkService.initConfiguration(null, builder.get());
                    }
                    SdkEntry.openAlbum(getContext(), UIConfiguration.ALBUM_SUPPORT_DEFAULT, ALBUM_QUIK_REQUEST_CODE);

                }else if(name.equals("短视频")){
                    initCameraShortVideoConfig();
                    SdkEntry.record(getContext(), SHORTVIDEO_CAMERA_REQUEST_CODE);
                }
            }
        });
        prl_refersh = (PullToRefreshLayout) view.findViewById(R.id.prl_refersh);
        prl_refersh.setOnRefreshListener(new MyListener());
        tv_allNum = (TextView) view.findViewById(R.id.tv_allNum);
        tv_sent = (TextView) view.findViewById(R.id.tv_sent);
        tv_caiji = (TextView) view.findViewById(R.id.tv_caiji);
        tv_inter = (TextView) view.findViewById(R.id.tv_inter);
        rl_zanwu = (RelativeLayout) view.findViewById(R.id.rl_zanwu);
//        tv_messagenum = (TextView) view.findViewById(R.id.tv_messagenum);

        if (mHeadImg != null) {
            imageLoader.displayImage(mLoginBean.getPhoto(), mHeadImg, options);
        }

        tv_group.setText(mLoginBean.getDepartmentName());
        username.setText(mLoginBean.getUsername());


//        if (mLoginBean != null) {
//            chatmessageList = chatsDao.getAllNoreadMessage(new Integer(mLoginBean.getUserID()), 1);
//            if (tv_messagenum != null) {
//                if (IsNonEmptyUtils.isList(chatmessageList)) {
//                    tv_messagenum.setVisibility(View.VISIBLE);
//                    tv_messagenum.setText(chatmessageList.size() + "");
//                } else {
//                    tv_messagenum.setVisibility(View.GONE);
//                }
//            }
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        application = (CbApplication) getActivity().getApplication();
        mLoginBean = application.getmLoginBean();
        String photo = mLoginBean.getPhoto();
        if (IsNonEmptyUtils.isString(photo)) {
            imageLoader.displayImage(mLoginBean.getPhoto(), mHeadImg, options);
        }

        if (mLoginBean != null) {
            if (IsNonEmptyUtils.isString(mLoginBean.getUsername())) {
                username.setText(mLoginBean.getUsername());
            }

            if (IsNonEmptyUtils.isString(mLoginBean.getDepartmentName())) {
                tv_group.setText(mLoginBean.getDepartmentName());
            }
        }
        initData();
        initMyData();
    }

    class MyListener implements PullToRefreshLayout.OnRefreshListener {
        @SuppressLint("HandlerLeak")
        @Override
        public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
            initData();
            // 下拉刷新操作
            new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    // 千万别忘了告诉控件刷新完毕了哦！
                    pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                }
            }.sendEmptyMessageDelayed(0, 2000);
        }

        @SuppressLint("HandlerLeak")
        @Override
        public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
            moreData();
            // 加载操作
            new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    // 千万别忘了告诉控件加载完毕了哦！
                    pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            }.sendEmptyMessageDelayed(0, 2000);//2秒之后触发viewPager的自动播放
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_setting:// 设置按钮
                MainActivity indexActivity = (MainActivity) getActivity();
                indexActivity.switchToMine();
                break;
            case R.id.img_addplus:
                Intent intent = new Intent(getActivity(), HomeGvShowActivity.class);
                startActivity(intent);
                break;

        }
    }

    @Override
    protected void initRequestOnSuccess(String result, int code, int biaoshi) {
        initDataParse(result, code);
    }

    @Override
    protected void initRequestOnStart(String result, int code, int biaoshi) {
    }

    @Override
    protected void initRequestOnFailure(String failure, int code, int biaoshi) {
    }

    @Override
    protected void initRequestOnCache(String result, int code, int biaoshi) {
        // TODO Auto-generated method stub
//		initDataParse(result, code);
    }

    private void initDataParse(String result, int code) {
        if (code == Configs.infoListCode) {
            if (pageTag == 0) {//刷新
                JSONObject jsonObject = JSON.parseObject(result);
                String data = jsonObject.getString("data");
                if (!data.equals("")) {//表示获取成功
                    mList = Parser.parserData(result);
                    if (IsNonEmptyUtils.isList(mList)) {
                        mHandler.sendEmptyMessage(1);
                    } else {
                        mHandler.sendEmptyMessage(0);
                    }
                } else {
                    mHandler.sendEmptyMessage(0);
                }
            } else if (pageTag == 1) {//加载更多
                JSONObject jsonObject = JSON.parseObject(result);
                String data = jsonObject.getString("data");
                if (!data.equals("")) {//表示获取成功
                    mListMore = Parser.parserData(result);
                    if (IsNonEmptyUtils.isList(mListMore)) {
                        mHandler.sendEmptyMessage(2);
                    } else {
                        mHandler.sendEmptyMessage(0);
                    }
                } else {
                    mHandler.sendEmptyMessage(0);
                }
            }
        } else if (code == Configs.countCode) {//获取首页数量
            JSONObject jsonObject = JSON.parseObject(result);
            String status = jsonObject.getString("status");
            if (status.equals("100")) {//表示获取成功
                String data = jsonObject.getString("data");
                indexCountBean = JSON.parseObject(data, IndexCountBean.class);
                if (indexCountBean != null) {
                    mHandler.sendEmptyMessage(3);
                } else {
                    mHandler.sendEmptyMessage(0);
                }
            } else {
                mHandler.sendEmptyMessage(0);
            }
        }
        if (code == Configs.taskListCode) {
            JSONObject jsonObject = JSON.parseObject(result);
            String status = jsonObject.getString("status");
            if (status.equals("100")) {//表示获取成功
                String data = jsonObject.getString("data");
                mListTask = JSON.parseArray(data, TaskListBean.class);
                if (IsNonEmptyUtils.isList(mListTask)) {
                    mHandler.sendEmptyMessage(4);
                } else {
                    mHandler.sendEmptyMessage(0);
                }
            }
        }
    }

    public class MessageAdapter extends BaseAdapter {
        private Context mContext;
        private List<MessageAllBean> mList;

        public MessageAdapter(Context mContext, List<MessageAllBean> list) {
            super();
            this.mContext = mContext;
            this.mList = list;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int arg0) {
            return mList.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.message_item_to_layout, null);
                holder = new ViewHolder();
                holder.iv = (ImageView) convertView.findViewById(R.id.iv);
                holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                holder.tv_title.setTextSize(16);
                holder.tv_desc = (TextView) convertView.findViewById(R.id.tv_desc);
                holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                holder.tv_huifu = (TextView) convertView.findViewById(R.id.tv_huifu);
                holder.tv_chakan = (TextView) convertView.findViewById(R.id.tv_chakan);
                holder.iv_xiaoxi = (ImageView)convertView.findViewById(R.id.iv_xiaoxi);
                holder.tv_sendername = (TextView) convertView.findViewById(R.id.tv_sendername);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //状态：0：初审、1：二审、2：终审、3：退回
			/*if(mList.get(position).getStatus().equals("1")){//上传
				holder.iv.setImageBitmap(BitmapUtils.readBitMap(mContext, R.drawable.chuan));
			}else if(mList.get(position).getStatus().equals("10")){//选用
				holder.iv.setImageBitmap(BitmapUtils.readBitMap(mContext, R.drawable.caiyong));
			}else*/
//            if (mList.get(position).getMessageDescribe().contains("通过")) {//审核通过
//                holder.iv.setImageBitmap(BitmapUtils.readBitMap(mContext, R.drawable.shen));
//                holder.tv_desc.setText(mList.get(position).getMessageDescribe());
//
//            } else {
            MessageAllBean messageAllBean = mList.get(position);
            String parentType = messageAllBean.getParentType();

            switch (Integer.parseInt(parentType)){
                case 1:
                    holder.iv.setImageBitmap(BitmapUtils.readBitMap(mContext, R.drawable.ren));
                    break;
                case 2:
                    holder.iv.setImageBitmap(BitmapUtils.readBitMap(mContext, R.drawable.shen2));
                    break;
                case 3:
                    holder.iv.setImageBitmap(BitmapUtils.readBitMap(mContext, R.drawable.shen2));
                    break;
                case 4:
                    holder.iv.setImageBitmap(BitmapUtils.readBitMap(mContext, R.drawable.tuishen));
                    break;
                case 5:
                    holder.iv.setImageBitmap(BitmapUtils.readBitMap(mContext, R.drawable.zhi));
                    break;
                case 6:
                    holder.iv.setImageBitmap(BitmapUtils.readBitMap(mContext, R.drawable.xin));
                    break;
                case 7:
                    holder.iv.setImageBitmap(BitmapUtils.readBitMap(mContext, R.drawable.bao));
                    break;
                case 8:
                    holder.iv.setImageBitmap(BitmapUtils.readBitMap(mContext, R.drawable.yin));
                    break;
                case 9:
                    holder.iv.setImageBitmap(BitmapUtils.readBitMap(mContext, R.drawable.chuan2));
                    break;
                case 10:
                    holder.iv.setImageBitmap(BitmapUtils.readBitMap(mContext, R.drawable.chuan2));
                    break;
                case 11:
                    holder.iv.setImageBitmap(BitmapUtils.readBitMap(mContext, R.drawable.tuichuan));
                    break;
                case 12:
                    holder.iv.setImageBitmap(BitmapUtils.readBitMap(mContext, R.drawable.chuan2));
                    break;
            }

         //   holder.iv.setVisibility(View.GONE);
                holder.tv_desc.setText(mList.get(position).getTitle());
          //  }

            if (mList.get(position).getState().trim().equals("1")) {//未回复
                holder.iv_xiaoxi.setBackgroundResource(R.drawable.xiaoxi_home);
//                holder.tv_huifu.setVisibility(View.VISIBLE);
//                holder.tv_chakan.setVisibility(View.GONE);
            } else if (mList.get(position).getState().equals("2")) { //回复
//                holder.tv_huifu.setVisibility(View.GONE);
//                holder.tv_chakan.setVisibility(View.VISIBLE);
                holder.iv_xiaoxi.setBackgroundResource(R.drawable.chakan);
            }
            holder.iv_xiaoxi.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mList.get(position).getState().trim().equals("1")){
                        Intent intent = new Intent(getActivity(), FeedBackActivity.class);
                        intent.putExtra("userid", mLoginBean.getUserID());
                        intent.putExtra("ids", mList.get(position).getId());
                        intent.putExtra("messageid", mList.get(position).getId());
                        getActivity().startActivity(intent);
                    }else if (mList.get(position).getState().equals("2")) {
                        Intent intent = new Intent(getActivity(), ShowMessageDeatilsActivity.class);
                        intent.putExtra("userid", mLoginBean.getUserID());
                        intent.putExtra("ids", mList.get(position).getId());
                        intent.putExtra("messageid", mList.get(position).getId());
                        getActivity().startActivity(intent);
                    }
                    }
            });
            holder.tv_sendername.setText(mList.get(position).getSenderName());

            holder.tv_title.setText(mList.get(position).getMessageDescribe());
            holder.tv_chakan.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {//id=getIntent().getStringExtra("id");
//                    Intent intent = new Intent(getActivity(), ShowMessageDeatilsActivity.class);
//                    intent.putExtra("userid", mLoginBean.getUserID());
//                    intent.putExtra("ids", mList.get(position).getId());
//                    intent.putExtra("messageid", mList.get(position).getId());
//                    getActivity().startActivity(intent);


                }
            });
            holder.tv_huifu.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //显示pipuwindow
                    Intent intent = new Intent(getActivity(), FeedBackActivity.class);
                    intent.putExtra("userid", mLoginBean.getUserID());
                    intent.putExtra("ids", mList.get(position).getId());
                    intent.putExtra("messageid", mList.get(position).getId());
                    getActivity().startActivity(intent);
                }
            });

            holder.tv_time.setText(TimeUtil.getDateDiff(mList.get(position).getCreateTime()));
            return convertView;
        }

        /**
         * 在字符串中显示高亮文字
         *
         * @param str1 要高亮显示的文字
         * @param str2 包含高亮文字的字符串
         * @return
         */
        private SpannableString setTextHighLight(String str1, String str2) {
            SpannableString sp = new SpannableString(str2);
            // 遍历要显示的文字
            for (int i = 0; i < str1.length(); i++) {
                // 得到单个文字
                String s1 = str1.charAt(i) + "";
                // 判断字符串是否包含高亮显示的文字
                if (str2.contains(s1)) {
                    // 查找文字在字符串的下标
                    int index = str2.indexOf(s1);
                    // 循环查找字符串中所有该文字并高亮显示
                    while (index != -1) {
                        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#1983d1"));
                        sp.setSpan(colorSpan, index, index + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        // s1从这个索引往后开始第一个出现的位置
                        index = str2.indexOf(s1, index + 1);
                    }
                }
            }
            return sp;
        }

        public class ViewHolder {

            /**
             * 条目左侧的圆形图片
             */
            ImageView iv,iv_xiaoxi;
            /**
             * 标题信息和描述信息、审核通过的时间
             */
            TextView tv_title, tv_desc, tv_time, tv_huifu, TextView;
            TextView tv_sendername, tv_chakan;

        }

        public void setList(List<MessageAllBean> list2) {
            // TODO Auto-generated method stub
            this.mList = list2;
            notifyDataSetChanged();
        }

    }





    /*************************************************************************************/





    @Override
    public void onDestroy() {
        super.onDestroy();
        SdkEntry.onExitApp(getActivity());
    }

    /**
     * 新的网络接口方式(资源放到自己服务器或锐动服务器)
     *
     * @param builder
     * @param configData
     */
    private void initThridServer(UIConfiguration.Builder builder, ConfigData configData) {

        String url = configData.customApi;
        System.out.println("url==="+url);
        // 设置MV和mv网络地址
        builder.enableNewMV(configData.enableMV, url)
                //设置字幕URL
                .setTitlingUrl(url)
                //设置字体URL
                .setFontUrl(url)
                //特效URL
                .setSpecialEffectsUrl(url)
                //滤镜URL(必须是lookup滤镜)
                .setFilterUrl(url)
                //转场URL
                .setTransitionUrl(url)
                // 设置自定义的网络音乐
                .setNewMusicUrl(url)
                //云音乐
//                .setNewCloudMusicUrl(url);
                .setNewCloudMusicUrl(url, "Jason Shaw", "audionautix.com", "https://audionautix.com",
                        getString(R.string.yunmusic_sign), "http://d.56show.com/accredit/accredit.jpg");


    }

    /**
     * 初始标准编辑及导出配置
     */
    private void initEditorUIAndExportConfig() {

        initAndGetConfigData();
        // 视频编辑UI配置


        UIConfiguration.Builder builder = new UIConfiguration.Builder()
                // 设置是否使用自定义相册
                .useCustomAlbum(configData.useCustomAlbum)
                // 设置向导化
                .enableWizard(configData.enableWizard)
                // 设置自动播放
                .enableAutoRepeat(configData.enableAutoRepeat)
                // 配音模式
                .setVoiceLayoutType(configData.voiceLayoutType)
                // 设置秀拍客相册支持格式
                .setAlbumSupportFormat(configData.albumSupportFormatType)
                // 设置默认进入界面画面比例
                .setVideoProportion(configData.videoProportionType)
                // 设置滤镜界面风格
                .setFilterType(configData.filterLayoutType)
                // 设置相册媒体选择数量上限(目前只对相册接口生效)
                .setMediaCountLimit(configData.albumMediaCountLimit)
                // 设置相册是否显示跳转拍摄按钮(目前只对相册接口生效)
                .enableAlbumCamera(configData.enableAlbumCamera)
                // 编辑与导出模块显示与隐藏（默认不设置为显示）
                .setEditAndExportModuleVisibility(
                        UIConfiguration.EditAndExportModules.SOUNDTRACK,
                        configData.enableSoundTrack)

                .setEditAndExportModuleVisibility(UIConfiguration.EditAndExportModules.DUBBING,
                        configData.enableDubbing)
                .setEditAndExportModuleVisibility(UIConfiguration.EditAndExportModules.FILTER,
                        configData.enableFilter)
                .setEditAndExportModuleVisibility(UIConfiguration.EditAndExportModules.TITLING,
                        configData.enableTitling)
                .setEditAndExportModuleVisibility(
                        UIConfiguration.EditAndExportModules.SPECIAL_EFFECTS,
                        configData.enableSpecialEffects)
                .setEditAndExportModuleVisibility(
                        UIConfiguration.EditAndExportModules.CLIP_EDITING,
                        configData.enableClipEditing)
                // 片段编辑模块显示与隐藏（默认不设置为显示）
                .setClipEditingModuleVisibility(
                        UIConfiguration.ClipEditingModules.IMAGE_DURATION_CONTROL,
                        configData.enableImageDuration)
                .setClipEditingModuleVisibility(UIConfiguration.ClipEditingModules.EDIT,
                        configData.enableEdit)
                .setClipEditingModuleVisibility(UIConfiguration.ClipEditingModules.TRIM,
                        configData.enableTrim)
                .setClipEditingModuleVisibility(
                        UIConfiguration.ClipEditingModules.VIDEO_SPEED_CONTROL,
                        configData.enableVideoSpeed)
                .setClipEditingModuleVisibility(UIConfiguration.ClipEditingModules.SPLIT,
                        configData.enableSplit)
                .setClipEditingModuleVisibility(UIConfiguration.ClipEditingModules.COPY,
                        configData.enableCopy)
                .setClipEditingModuleVisibility(UIConfiguration.ClipEditingModules.PROPORTION,
                        configData.enableProportion)
                .setClipEditingModuleVisibility(UIConfiguration.ClipEditingModules.SORT,
                        configData.enableSort)
                .setClipEditingModuleVisibility(UIConfiguration.ClipEditingModules.TEXT,
                        configData.enableText)
                .setClipEditingModuleVisibility(UIConfiguration.ClipEditingModules.REVERSE,
                        configData.enableReverse)
                .setClipEditingModuleVisibility(UIConfiguration.ClipEditingModules.TRANSITION, true);

        if (configData.enableNewApi) {
            initThridServer(builder, configData);
        } else {
            // 设置MV和mv网络地址
            builder.enableMV(configData.enableMV, ConfigData.WEB_MV_URL)
                    // 设置自定义的网络音乐
                    .setMusicUrl(ConfigData.MUSIC_URL)
                    //云音乐
                    .setCloudMusicUrl(ConfigData.CLOUDMUSIC_URL);
        }
        UIConfiguration uiConfig = builder
                //是否显示本地音乐
                .enableLocalMusic(configData.enableLocalMusic)
                // 字幕、特效在mv的上面
                .enableTitlingAndSpecialEffectOuter(configData.enableTitlingAndSpecialEffectOuter)
                .get();

        // 导出视频参数配置
        ExportConfiguration exportConfig = new ExportConfiguration.Builder()
                // 设置保存路径，传null或不设置
                // 将保存至默认路径(即调用SdkEntry.initialize初始时自定义路径）
                .setSavePath(Environment.getExternalStorageDirectory() + "/JWZTCBProduct/videos/")
                //设置导出时最大边,不设置时，默认为640
                .setVideoMaxWH(640)
                //设置视频导出码率，Mbps为单位,不设置时，默认为4
                .setVideoBitRate(4)
                //设置视频导出帧率，,不设置时，默认为30
                .setVideoFrameRate(30)
                // 设置片尾图片路径，传null或者不设置 将没有片尾
                .setTrailerPath(configData.videoTrailerPath)
                // 设置片尾时长 单位s 默认2s
                .setTrailerDuration(2)
                // 设置导出视频时长 单位s 传0或者不设置 将导出完整视频
                .setVideoDuration(configData.exportVideoDuration)
                // 设置添加媒体时长限制 单位s 传0或者不设置 将没有限制
                .setImportVideoDuration(0)
                // 设置图片水印路径
                .setWatermarkPath(configData.enableWatermark ? EDIT_WATERMARK_PATH : null)
                // 设置水印显示模式
                .setWatermarkShowMode(Watermark.MODE_DEFAULT)
                // 设置是否使用文字水印（使用文字水印，将不再显示图片水印）
                .enableTextWatermark(configData.enableTextWatermark)
                // 设置文字水印内容（开启文字水印才生效）
                .setTextWatermarkContent("watarmark")
                // 设置文字水印大小（开启文字水印才生效）
                .setTextWatermarkSize(10)
                // 设置文字水印颜色（开启文字水印才生效）
                .setTextWatermarkColor(Color.WHITE)
                // 设置文字水印阴影颜色（开启文字水印才生效）
                .setTextWatermarkShadowColor(Color.BLACK)
                // 设置水印位置 (文字或图片水印开启才生效)
                .setWatermarkPosition(configData.watermarkShowRectF).get();

        // 获取秀拍客配置服务器
        SdkService sdkService = getSdkService();
        if (null != sdkService) {
            // 初始化所有配置
            sdkService.initConfiguration(exportConfig, uiConfig);
        }
    }

    /**
     * 选折相册资源
     */
    private void initUIAblumConfig(int mediaLimit) {
        // 视频编辑UI配置
        UIConfiguration uiConfig = new UIConfiguration.Builder()
                // 设置是否使用自定义相册
                .useCustomAlbum(false)
                // 设置秀拍客相册支持格式
                .setAlbumSupportFormat(UIConfiguration.ALBUM_SUPPORT_DEFAULT)
                // 设置滤镜界面风格
                // 设置相册媒体选择数量上限(目前只对相册接口生效)
                .setMediaCountLimit(mediaLimit)
                .setClipEditingModuleVisibility(UIConfiguration.ClipEditingModules.SORT, false)
                .setClipEditingModuleVisibility(UIConfiguration.ClipEditingModules.PROPORTION, false)
                .setClipEditingModuleVisibility(UIConfiguration.ClipEditingModules.IMAGE_DURATION_CONTROL, false)
                .setClipEditingModuleVisibility(UIConfiguration.ClipEditingModules.TRANSITION, false)
                .setClipEditingModuleVisibility(UIConfiguration.ClipEditingModules.EDIT, false)
                .setClipEditingModuleVisibility(UIConfiguration.ClipEditingModules.COPY, false)
                .setFilterType(UIConfiguration.FILTER_LAYOUT_3)
                // 设置相册是否显示跳转拍摄按钮(目前只对相册接口生效)
                .enableAlbumCamera(false).get();

        // 获取秀拍客配置服务器
        SdkService sdkService = getSdkService();
        if (null != sdkService) {
            // 初始化所有配置
            sdkService.initConfiguration(null, uiConfig);
        }
    }

    /**
     * 初始拍摄配置
     *
     * @param UIType * 设置录制时默认界面:<br>
     *               默认16：9录制:<br>
     *               CameraConfiguration#WIDE_SCREEN_CAN_CHANGE<br>
     *               默认1：1:<br>
     *               CameraConfiguration. SQUARE_SCREEN_CAN_CHANGE<br>
     *               仅16：9录制:<br>
     *               CameraConfiguration.ONLY_WIDE_SCREEN<br>
     *               仅1：1录制:<br>
     *               CameraConfiguration.ONLY_SQUARE_SCREEN
     */
    private void initCameraConfig(int UIType) {
        getSdkService().initConfiguration(
                new CameraConfiguration.Builder()
                        // 可设置最小录制时长,0代表不限制
                        .setVideoMinTime(configData.cameraMinTime)
                        // 可设置最大录制时长,0代表不限制
                        .setVideoMaxTime(configData.cameraMaxTime)
                        // 为true代表多次拍摄，拍摄完成一段之后，将保存至相册并开始下一段拍摄，默认为false单次拍摄，拍摄完成后返回资源地址
                        .useMultiShoot(configData.useMultiShoot)
                        /**
                         * 设置录制时默认界面:<br>
                         * 默认16：9录制:<br>
                         * CameraConfiguration. WIDE_SCREEN_CAN_CHANGE<br>
                         * 默认1：1:<br>
                         * CameraConfiguration. SQUARE_SCREEN_CAN_CHANGE<br>
                         * 仅16：9录制:<br>
                         * CameraConfiguration.ONLY_SCREEN_SCREEN 仅1：1录制:<br>
                         * CameraConfiguration.ONLY_SQUARE_SCREEN
                         */
                        .setCameraUIType(UIType)
                        // 设置拍摄完成后，是否保存至相册（仅单次拍摄方式有效），同时通过onActivityResult及SIMPLE_CAMERA_REQUEST_CODE返回
                        .setSingleCameraSaveToAlbum(configData.isSaveToAlbum)
                        // 设置录制时是否静音，true代表录制后无声音
                        .setAudioMute(false)
                        // 设置是否启用人脸贴纸功能
                        .enableFaceu(configData.isDefaultFace)
                        // 设置人脸贴纸鉴权证书
                        .setPack(authpack.A())
                        // 设置是否默认为后置摄像头
                        .setDefaultRearCamera(configData.isDefaultRearCamera)
                        // 是否显示相册按钮
                        .enableAlbum(configData.enableAlbum)
                        // 是否使用自定义相册
                        .useCustomAlbum(configData.useCustomAlbum)
                        // 设置隐藏拍摄功能（全部隐藏将强制开启视频拍摄）
                        .hideMV(configData.hideMV)
                        .hidePhoto(configData.hidePhoto)
                        .hideRec(configData.hideRec)
                        // 设置mv最小时长
                        .setCameraMVMinTime(configData.cameraMVMinTime)
                        // 设置mv最大时长
                        .setCameraMVMaxTime(configData.cameraMVMaxTime)
                        // 开启相机水印时需注册水印
                        // SdkEntry.registerOSDBuilder(CameraWatermarkBuilder.class);
                        // 相机录制水印
                        .enableWatermark(configData.enableCameraWatermark)
                        // 相机水印片头
                        .setCameraTrailerTime(VEOSDBuilder.OSDState.header, 2f)
                        // 相机录制结束时片尾水印时长(0-1.0 单位：秒)
                        .setCameraTrailerTime(VEOSDBuilder.OSDState.end,
                                configData.cameraWatermarkEnd)
                        // 是否启用防篡改录制
                        .enableAntiChange(configData.enableAntiChange)
                        // 启用前置输出时镜像
                        .enableFrontMirror(configData.enableFrontMirror)
                        // 固定录制界面的方向
                        .setOrientation(configData.mRecordOrientation)
                        // 是否支持录制时播放音乐
                        .enablePlayMusic(configData.enablePlayMusic)
                        // 是否美颜
                        .enableBeauty(configData.enableBeauty)
                        //录制的云音乐
//                        .setCloudMusicUrl(configData.enableNewApi ? configData.customApi : "")
                        .setCloudMusicUrl(configData.enableNewApi ? configData.customApi : "", "Jason Shaw", "audionautix.com", "https://audionautix.com",
                                getString(R.string.yunmusic_sign), "http://d.56show.com/accredit/accredit.jpg")
                        //滤镜(lookup)
                        .setFilterUrl(configData.enableNewApi ? configData.customApi : "")
                        .get());
    }

    /**
     * 防篡改录制的相机配置
     */
    private void initCameraAntiChangeConfig() {

        CameraConfiguration config = new CameraConfiguration.Builder()
                // 可设置最小录制时长,0代表不限制
                .setVideoMinTime(configData.cameraMinTime)
                // 可设置最大录制时长,0代表不限制
                .setVideoMaxTime(configData.cameraMaxTime)
                // 为true代表多次拍摄，拍摄完成一段之后，将保存至相册并开始下一段拍摄，默认为false单次拍摄，拍摄完成后返回资源地址
                .useMultiShoot(false)
                /*
                 * 仅16：9录制:<br> CameraConfiguration.ONLY_WIDE_SCREEN
                 */
                .setCameraUIType(CameraConfiguration.ONLY_WIDE_SCREEN)
                // 设置拍摄完成后，是否保存至相册（仅单次拍摄方式有效），同时通过onActivityResult及SIMPLE_CAMERA_REQUEST_CODE返回
                .setSingleCameraSaveToAlbum(true)
                // 设置录制时是否静音，true代表录制后无声音
                .setAudioMute(false)
                // 设置是否启用人脸贴纸功能
                .enableFaceu(configData.isDefaultFace)
                // 设置人脸贴纸鉴权证书
                .setPack(authpack.A())
                // 设置是否默认为后置摄像头
                .setDefaultRearCamera(false)
                // 是否显示相册按钮
                .enableAlbum(true)
                // 是否使用自定义相册
                // .useCustomAlbum(configData.useCustomAlbum)
                // 设置隐藏拍摄功能（全部隐藏将强制开启视频拍摄）
                .hideMV(true)
                .hidePhoto(true)
                .hideRec(false)
                // 设置mv最小时长
                .setCameraMVMinTime(configData.cameraMVMinTime)
                // 设置mv最大时长
                .setCameraMVMaxTime(configData.cameraMVMaxTime)
                // 开启相机水印时需注册水印
                // SdkEntry.registerOSDBuilder(CameraWatermarkBuilder.class);
                // 相机录制水印
                .enableWatermark(true)
                // 相机录制结束时片尾水印时长(0-1.0 单位：秒)
                .setCameraTrailerTime(VEOSDBuilder.OSDState.header, 2f)
                .setCameraTrailerTime(VEOSDBuilder.OSDState.end, 1f)
                // 是否启用防篡改录制
                .enableAntiChange(true).enableFrontMirror(true)
                //录制的云音乐
                .setCloudMusicUrl(configData.enableNewApi ? configData.customApi : "")
                //滤镜(lookup)
                .setFilterUrl(configData.enableNewApi ? configData.customApi : "")
                .enableBeauty(true).get();

        SdkService sdkService = getSdkService();
        if (null != sdkService) {
            // 初始化所有配置
            sdkService.initConfiguration(config);
        }
    }

    /**
     * 短视频录制推荐参数
     */
    private void initCameraShortVideoConfig() {

        CameraConfiguration cameraConfig = new CameraConfiguration.Builder()
                // 为true代表多次拍摄，拍摄完成一段之后，将保存至相册并开始下一段拍摄，默认为false单次拍摄，拍摄完成后返回资源地址
                .useMultiShoot(false)
                /**
                 * 设置录制时默认界面:<br>
                 * 默认16：9录制:<br>
                 * CameraConfiguration. WIDE_SCREEN_CAN_CHANGE<br>
                 * 默认1：1:<br>
                 * CameraConfiguration. SQUARE_SCREEN_CAN_CHANGE<br>
                 * 仅1：1录制:<br>
                 * CameraConfiguration.ONLY_SQUARE_SCREEN
                 */
                .setCameraUIType(CameraConfiguration.ONLY_SQUARE_SCREEN)
                // 设置拍摄完成后，是否保存至相册（仅单次拍摄方式有效），同时通过onActivityResult及SIMPLE_CAMERA_REQUEST_CODE返回
                .setSingleCameraSaveToAlbum(true)
                // 设置录制时是否静音，true代表录制后无声音
                .setAudioMute(false)
                // 设置是否启用人脸贴纸
                .enableFaceu(false)
                // 设置启用人脸贴纸鉴权证书
                .setPack(authpack.A())
                // 设置是否默认为后置摄像头
                .setDefaultRearCamera(false)
                // 是否显示相册按钮
                .enableAlbum(true)
                // 是否使用自定义相册
                .useCustomAlbum(false)
                // 设置隐藏拍摄功能（全部隐藏将强制开启视频拍摄）
                .hideMV(false).hidePhoto(true).hideRec(true)
                // 设置mv最小时长
                .setCameraMVMinTime(3)
                // 设置mv最大时长
                .setCameraMVMaxTime(15)
                //录制的云音乐
                .setCloudMusicUrl(configData.enableNewApi ? configData.customApi : "")
                //滤镜(lookup)
                .setFilterUrl(configData.enableNewApi ? configData.customApi : "")
                // 强制美颜
                .enableBeauty(true).get();
        // 视频编辑UI配置

        UIConfiguration.Builder builder = new UIConfiguration.Builder()
                // 设置是否使用自定义相册
                .useCustomAlbum(false);
        if (configData.enableNewApi) {
            //是否启用新的网络接口方式(资源放到自己服务器或锐动服务器)
            ConfigData configData = new ConfigData();
            configData.enableMV = true;
            configData.customApi = configData.customApi;

            initThridServer(builder, configData);
        } else {
            //网络音乐
            builder.setMusicUrl(ConfigData.MUSIC_URL)
                    // 设置MV和mv网络地
                    .enableMV(true, ConfigData.WEB_MV_URL)
                    //云音乐
                    .setCloudMusicUrl(ConfigData.CLOUDMUSIC_URL);
        }


        // 设置画面比例为1:1
        builder.setVideoProportion(UIConfiguration.PROPORTION_SQUARE)
                // 相册仅支持视频
                .setAlbumSupportFormat(UIConfiguration.ALBUM_SUPPORT_VIDEO_ONLY)
                // 设置视频选择最大数量
                .setMediaCountLimit(1)
                // 设置隐藏相册中的拍摄按钮
                .enableAlbumCamera(false)
                //显示配乐
                .setEditAndExportModuleVisibility(UIConfiguration.EditAndExportModules.SOUNDTRACK, true)
                //关闭配音
                .setEditAndExportModuleVisibility(UIConfiguration.EditAndExportModules.DUBBING, false)
                // 隐藏字幕
                .setEditAndExportModuleVisibility(UIConfiguration.EditAndExportModules.TITLING,
                        false)
                // 隐藏片段编辑
                .setEditAndExportModuleVisibility(
                        UIConfiguration.EditAndExportModules.CLIP_EDITING, false)
                // 隐藏特效
                .setEditAndExportModuleVisibility(
                        UIConfiguration.EditAndExportModules.SPECIAL_EFFECTS, false)
                // 启用自动重播
                .enableAutoRepeat(true);


        UIConfiguration uiConfig = builder.get();

        TrimConfiguration trimConfig = new TrimConfiguration.Builder()
                //设置实际截取时视频导出最大边,不设置时，默认为640
                .setVideoMaxWH(640)
                //设置实际截取时视频导出码率，Mbps为单位,不设置时，默认为4
                .setVideoBitRate(4)
                // 设置默认裁剪区域为1:1
                .setDefault1x1CropMode(true)
                // 设置截取返回类型
                .setTrimReturnMode(TrimConfiguration.TRIM_RETURN_TIME)
                // 设置截取类型
                .setTrimType(TrimConfiguration.TRIM_TYPE_SINGLE_FIXED)
                // 设置是否显示1：1按钮
                .enable1x1(false)
                // 设置定长截取时间
                .setTrimDuration(15).get();
        getSdkService().initConfiguration(null, uiConfig,
                cameraConfig);
        getSdkService().initTrimConfiguration(trimConfig);
    }

    /**d
     * 初始压缩配置
     */
    private void initCompressConfig() {
        SdkService sdkService = getSdkService();
        if (null != sdkService) {
            sdkService
                    .initCompressConfiguration(new CompressConfiguration.Builder()
                            // 设置是否使用硬件加速
                            .enableHWCode(configData.enableHWCode)
                            // 设置压缩视频码率
                            .setBitRate(configData.compressBitRate)
                            // 是否显示水印
                            .enableWatermark(configData.enableCompressWatermark)
                            // 显示水印的位置
                            .setWatermarkPosition(
                                    configData.compressWatermarkShowRect)
                            // 设置视频分辨率
                            .setVideoSize(configData.compressVideoWidth,
                                    configData.compressVideoHeight)
                            // 设置保存路径
                            .setSavePath(
                                    Environment.getExternalStorageDirectory()
                                            + "/xpkVideos/").get());
        }
    }

    /**
     * 初始视频截取配置
     */
    private void initTrimConfig() {
        SdkService sdkService = getSdkService();
        if (null != sdkService) {
            sdkService
                    .initTrimConfiguration(new TrimConfiguration.Builder()
                            //设置实际截取时视频导出最大边,不设置时，默认为640
                            .setVideoMaxWH(640)
                            //设置实际截取时视频导出码率，Mbps为单位,不设置时，默认为4
                            .setVideoBitRate(4)
                            // 设置默认裁剪区域为1:1
                            .setDefault1x1CropMode(
                                    configData.default1x1CropMode)
                            // 设置是否显示1:1裁剪按钮
                            .enable1x1(configData.enable1x1)
                            // 设置截取返回类型
                            .setTrimReturnMode(configData.mTrimReturnMode)
                            // 设置截取类型
                            .setTrimType(configData.mTrimType)
                            // 设置两定长截取时间
                            .setTrimDuration(configData.trimTime1, configData.trimTime2)
                            // 设置单个定长截取时间
                            .setTrimDuration(configData.trimSingleFixedDuration)
                            .get());
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSIONS: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        if (!SdkEntry.isInitialized()) {
                            ((CbApplication)getActivity(). getApplication()).initializeSdk();
                        }
                        exportDemoResource();
                    } else {
                        Toast.makeText(getActivity(), "未允许读写存储！", Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
            }
        }
    }


    /**
     * 响应点击
     *
     * @param v
     */
    public void onVideo(View v) {
        switch (v.getId()) {
            case R.id.draftList: {
                startActivity(new Intent(getActivity(), DraftListActivity.class));
            }
            break;
            case R.id.btnQuik: {

                UIConfiguration.Builder builder = new UIConfiguration.Builder()
                        // 设置是否使用自定义相册
                        .useCustomAlbum(false)
                        // 设置秀拍客相册支持格式
                        .setAlbumSupportFormat(UIConfiguration.ALBUM_SUPPORT_DEFAULT)
                        // 设置滤镜界面风格
                        // 设置相册媒体选择数量上限(目前只对相册接口生效)
                        .setMediaCountLimit(50)
                        // 设置相册是否显示跳转拍摄按钮(目前只对相册接口生效)
                        .enableAlbumCamera(false);


                ConfigData configData = new ConfigData();
                configData.enableMV = true;
                configData.customApi =configData.customApi;
                initThridServer(builder, configData);

                // 获取秀拍客配置服务器
                SdkService sdkService = getSdkService();
                if (null != sdkService) {
                    // 初始化所有配置
                    sdkService.initConfiguration(null, builder.get());
                }
                SdkEntry.openAlbum(getActivity(), UIConfiguration.ALBUM_SUPPORT_DEFAULT, ALBUM_QUIK_REQUEST_CODE);
            }
            break;
            case R.id.testRectFAnim:
            case R.id.testPointF: {
                //不规则四边形（视频+图片）
                boolean isAnimMode = (v.getId() == R.id.testRectFAnim); //false 顶点动画；true 矩形动画
                UIConfiguration uiConfig = new UIConfiguration.Builder()
                        // 设置是否使用自定义相册
                        .useCustomAlbum(false)
                        // 设置秀拍客相册支持格式
                        .setAlbumSupportFormat(UIConfiguration.ALBUM_SUPPORT_DEFAULT)
                        // 设置滤镜界面风格
                        // 设置相册媒体选择数量上限(目前只对相册接口生效)
                        .setMediaCountLimit(isAnimMode ? 8 : 2)
                        // 设置相册是否显示跳转拍摄按钮(目前只对相册接口生效)
                        .enableAlbumCamera(false).get();

                // 获取秀拍客配置服务器
                SdkService sdkService = getSdkService();
                if (null != sdkService) {
                    // 初始化所有配置
                    sdkService.initConfiguration(null, uiConfig);
                }
                //照片电影，或异形显示
                SdkEntry.openAlbum(getActivity(), UIConfiguration.ALBUM_SUPPORT_DEFAULT,
                        isAnimMode ? ALBUM_ANIMATION_REQUEST_CODE : ALBUM_POINTF_REQUEST_CODE);
            }
            break;
            case R.id.aeImageTemp:
                initUIAblumConfig(20);
                SdkEntry.openAlbum(getActivity(), UIConfiguration.ALBUM_SUPPORT_IMAGE_ONLY,
                        ALBUM_AE_IMAGE_REQUEST_CODE);
                break;
            case R.id.aeDefaultTempList:
                //回调直接播放导出的视频
                SdkEntry.AEList(getActivity(), REQUEST_AE_LIST);
                break;
            case R.id.record_square: // 正方形录制视频，编辑录制后的视频，如果有导出时，导出视频的路径
                SdkEntry.registerOSDBuilder(CameraWatermarkBuilder.class);
                CameraWatermarkBuilder.setText("北京经纬中天");// 可自定义水印显示文本
                initCameraConfig(CameraConfiguration.ONLY_SQUARE_SCREEN);
                SdkEntry.record(getActivity(), CAMERA_REQUEST_CODE);
                break;
            case R.id.record_wide: // 长方形录制视频，编辑录制后的视频，如果有导出时，导出视频的路径
                SdkEntry.registerOSDBuilder(CameraWatermarkBuilder.class);
                CameraWatermarkBuilder.setText("北京经纬中天");// 可自定义水印显示文本
                initCameraConfig(CameraConfiguration.ONLY_WIDE_SCREEN);
                SdkEntry.record(getActivity(), CAMERA_REQUEST_CODE);
                break;
            case R.id.record: // 正方形，长方形可切换录制视频，编辑录制后的视频，如果有导出时，导出视频的路径
                SdkEntry.registerOSDBuilder(CameraWatermarkBuilder.class);
                CameraWatermarkBuilder.setText("北京经纬中天");// 可自定义水印显示文本
                initCameraConfig(SQUARE_SCREEN_CAN_CHANGE);
                SdkEntry.record(getActivity(), CAMERA_REQUEST_CODE);
                break;
            case R.id.recordAntiChange: // 全屏录制，防篡改录制
                SdkEntry.registerOSDBuilder(CameraWatermarkBuilder.class);
                String osd = TextUtils.isEmpty(configData.antiChangeText) ? getString(R.string.osd_content)
                        : configData.antiChangeText;
                CameraWatermarkBuilder.setText(osd);
                initCameraAntiChangeConfig();
                SdkEntry.record(getActivity(), CAMERA_ANTI_CHANGE_REQUEST_CODE);
                break;

            case R.id.directEdit: // 传递资源路径，进入视频编辑，返回导出的视频的地址
                initEditorUIAndExportConfig();
                initCameraConfig(SQUARE_SCREEN_CAN_CHANGE);
//                SdkEntry.getSdkService().initConfiguration(
//                        new CameraConfiguration.Builder().get());
                ArrayList<String> list = new ArrayList<String>();
                list.add(EDIT_PICTURE_PATH);
                list.add(EDIT_L_VIDEO_PATH);
                try {
                    editMedia(getActivity(), list, EDIT_REQUEST_CODE);
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.selectMediaAndEdit: // 进入sdk媒体资源的选择界面(图片、视频)
                initEditorUIAndExportConfig();
                initCameraConfig(SQUARE_SCREEN_CAN_CHANGE);
                SdkEntry.selectMedia(getActivity(), EDIT_REQUEST_CODE);
                break;
            case R.id.player: // 播放器
                initUIAblumConfig(1);
                SdkEntry.openAlbum(getActivity(), UIConfiguration.ALBUM_SUPPORT_VIDEO_ONLY,
                        ALBUM_PLAYER_REQUEST_CODE);
                break;
            case R.id.imageTrans: // 图片变形
                SdkEntry.openImageTrans(getActivity());
                break;
            case R.id.export:
                initEditorUIAndExportConfig();
                getSdkService().initConfiguration(
                        new CameraConfiguration.Builder().get());
                SdkEntry.openAlbum(getActivity(), UIConfiguration.ALBUM_SUPPORT_VIDEO_ONLY,
                        ALBUM_REQUEST_EXPORT_CODE);
                break;
            case R.id.trim_l: // 截取横屏视频
                initTrimConfig();
                try {
                    SdkEntry.trimVideo(getActivity(), EDIT_L_VIDEO_PATH, TRIM_REQUEST_CODE);
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.trim_p: // 截取竖屏视频
                initTrimConfig();
                try {
                    SdkEntry.trimVideo(getActivity(), EDIT_P_VIDEO_PATH, TRIM_REQUEST_CODE);
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.trim_s: // 截取正方型视频
                initTrimConfig();
                try {
                    SdkEntry.trimVideo(getActivity(), EDIT_S_VIDEO_PATH, TRIM_REQUEST_CODE);
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.trim_alum:
                initUIAblumConfig(1);
                SdkEntry.openAlbum(getActivity(), UIConfiguration.ALBUM_SUPPORT_VIDEO_ONLY,
                        TRIM_ALBUM_REQUEST_CODE);
                break;
            case R.id.default_album: // 默认相册（支持图片和视频）
                initEditorUIAndExportConfig();
                getSdkService().initConfiguration(
                        new CameraConfiguration.Builder().get());
                SdkEntry.openAlbum(getActivity(), UIConfiguration.ALBUM_SUPPORT_DEFAULT,
                        ALBUM_REQUEST_CODE);
                break;
            case R.id.only_video: // 相册只显示视频
                initEditorUIAndExportConfig();
                getSdkService().initConfiguration(
                        new CameraConfiguration.Builder().get());
                SdkEntry.openAlbum(getActivity(), UIConfiguration.ALBUM_SUPPORT_VIDEO_ONLY,
                        ALBUM_REQUEST_CODE);
                break;
            case R.id.only_photo: // 相册只显示图片
                initEditorUIAndExportConfig();
                getSdkService().initConfiguration(
                        new CameraConfiguration.Builder().get());
                SdkEntry.openAlbum(getActivity(), UIConfiguration.ALBUM_SUPPORT_IMAGE_ONLY,
                        ALBUM_REQUEST_CODE);
                break;
            case R.id.compress: // 压缩视频
                // 选折要压缩的视频
                initUIAblumConfig(1);
                SdkEntry.openAlbum(getActivity(), UIConfiguration.ALBUM_SUPPORT_VIDEO_ONLY,
                        ALBUM_COMPRESS_REQUEST_CODE);
                break;
            case R.id.soundEffect: // 音效示例
                initUIAblumConfig(2);
                SdkEntry.openAlbum(getActivity(), UIConfiguration.ALBUM_SUPPORT_VIDEO_ONLY,
                        ALBUM_SOUND_EFFECT_REQUEST_CODE);
                break;
            case R.id.apiVideoTemp: // 剪影示例 （搞怪视频）
                initUIAblumConfig(1);
                SdkEntry.openAlbum(getActivity(), UIConfiguration.ALBUM_SUPPORT_DEFAULT,
                        ALBUM_SILHOUETT_REQUEST_CODE);
                break;
            case R.id.alien: // 异形示例
                initUIAblumConfig(2);
                SdkEntry.openAlbum(getActivity(), UIConfiguration.ALBUM_SUPPORT_DEFAULT,
                        ALBUM_ALIEN_REQUEST_CODE);
                break;
            case R.id.ui_config: // ui配置
                if (isConfigDialogShow) {
                    return;
                }
                new EditorUIAndExportConfigDialog(getActivity(), new ConfigDialogListener() {

                    @Override
                    public void onSaveConfigData(ConfigData configData) {
                        initAndGetConfigData().setConfig(configData);
                        initEditorUIAndExportConfig();
                        initCameraConfig(SQUARE_SCREEN_CAN_CHANGE);
                        saveConfigInstanceState();
                    }

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        isConfigDialogShow = false;
                    }
                }, configData);
                isConfigDialogShow = true;
                break;
            case R.id.camera_config: // 拍摄配置
                if (isConfigDialogShow) {
                    return;
                }
                new CameraConfigDialog(getActivity(), new ConfigDialogListener() {

                    @Override
                    public void onSaveConfigData(ConfigData configData) {
                        initAndGetConfigData().setConfig(configData);
                        saveConfigInstanceState();
                    }

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        isConfigDialogShow = false;
                    }
                }, configData);
                isConfigDialogShow = true;
                break;
            case R.id.trimConfig: // 截取配置
                if (isConfigDialogShow) {
                    return;
                }
                new VideoTrimConfigDialog(getActivity(), new ConfigDialogListener() {

                    @Override
                    public void onSaveConfigData(ConfigData configData) {
                        initAndGetConfigData().setConfig(configData);
                        saveConfigInstanceState();
                    }

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        isConfigDialogShow = false;
                    }
                }, configData);
                isConfigDialogShow = true;
                break;
            case R.id.album_config: // 相册配置
                if (isConfigDialogShow) {
                    return;
                }
                new AlbumConfigDialog(getActivity(), new ConfigDialogListener() {

                    @Override
                    public void onSaveConfigData(ConfigData configData) {
                        initAndGetConfigData().setConfig(configData);
                        saveConfigInstanceState();
                    }

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        isConfigDialogShow = false;
                    }
                }, configData);
                isConfigDialogShow = true;
                break;
            case R.id.compress_config: // 压缩配置
                if (isConfigDialogShow) {
                    return;
                }
                new CompressConfigDialog(getActivity(), new ConfigDialogListener() {

                    @Override
                    public void onSaveConfigData(ConfigData configData) {
                        initAndGetConfigData().setConfig(configData);
                        initCompressConfig();
                        saveConfigInstanceState();
                    }

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        isConfigDialogShow = false;
                    }
                }, configData);
                isConfigDialogShow = true;
                break;

            case R.id.shortVideoEdit:// 15秒短视频
                initCameraShortVideoConfig();
                SdkEntry.record(getActivity(), SHORTVIDEO_CAMERA_REQUEST_CODE);
                break;
            case R.id.reset_config:
                resetConfigInstanceState();
                break;
            case R.id.AntiChange_config:
                if (isConfigDialogShow) {
                    return;
                }
                new OsdConfigDialog(getActivity(), new ConfigDialogListener() {

                    @Override
                    public void onSaveConfigData(ConfigData configData) {
                        configData.antiChangeText = configData.antiChangeText;
                        initCameraAntiChangeConfig();
                        saveConfigInstanceState();
                    }

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        isConfigDialogShow = false;
                    }
                }, configData);
                isConfigDialogShow = true;
                break;
            case R.id.audio_config:
                if (isConfigDialogShow) {
                    return;
                }
                new AudioConfigDialog(getActivity(), new ConfigDialogListener() {

                    @Override
                    public void onSaveConfigData(ConfigData configData) {

                    }

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        isConfigDialogShow = false;
                    }
                });
                isConfigDialogShow = true;
                break;
            default:
                break;
        }
    }

    /**
     * TODO:registerAllResultHandlers
     */
    private void registerAllResultHandlers() {
        registerActivityResultHandler(CAMERA_REQUEST_CODE,
                cameraResultHandler);
        registerActivityResultHandler(CAMERA_ANTI_CHANGE_REQUEST_CODE,
                cameraAntiChangeResultHandler);

        registerActivityResultHandler(ALBUM_REQUEST_CODE,
                albumResultHandler);
        registerActivityResultHandler(ALBUM_COMPRESS_REQUEST_CODE,
                albumCompressResultHandler);
        //照片电影
        registerActivityResultHandler(ALBUM_ANIMATION_REQUEST_CODE,
                ablumAnimationResultHandler);
        //异形显示(顶点动画)
        registerActivityResultHandler(ALBUM_POINTF_REQUEST_CODE,
                ablumPointFResultHandler);

        //quik
        registerActivityResultHandler(ALBUM_QUIK_REQUEST_CODE,
                ablumQuikResultHandler);


        registerActivityResultHandler(ALBUM_SOUND_EFFECT_REQUEST_CODE,
                albumSoundEffectResultHandler);
        registerActivityResultHandler(ALBUM_ALIEN_REQUEST_CODE,
                albumAlienResultHandler);
        registerActivityResultHandler(ALBUM_SILHOUETT_REQUEST_CODE,
                albumSilhouetteResultHandler);
        registerActivityResultHandler(ALBUM_AE_IMAGE_REQUEST_CODE,
                albumAEImageResultHandler);
        registerActivityResultHandler(EDIT_REQUEST_CODE, editResultHandler);
        registerActivityResultHandler(REQUEST_AE_LIST, aeListResultHanlder);
        registerActivityResultHandler(ALBUM_PLAYER_REQUEST_CODE,
                albumPlayerResultHandler);
        registerActivityResultHandler(TRIM_REQUEST_CODE, trimResultHandler);
        registerActivityResultHandler(TRIM_ALBUM_REQUEST_CODE, trimAlbumResultHandler);


        registerActivityResultHandler(SHORTVIDEO_CAMERA_REQUEST_CODE,
                shortvideoCameraResultHandler);
        registerActivityResultHandler(SHORTVIDEO_ALBUM_REQUEST_CODE,
                shortvideoAlbumResultHandler);
        registerActivityResultHandler(SHORTVIDEO_TRIM_REQUEST_CODE,
                shortvideoTrimResultHandler);
        registerActivityResultHandler(ALBUM_REQUEST_EXPORT_CODE, albumExportResultHandler);
        registerActivityResultHandler(ANIMATION_RESULT_CODE, mAnimResultHandler);

    }

    private ActivityResultHandler mAnimResultHandler = new ActivityResultHandler() {
        @Override
        public void onActivityResult1(Context context, int resultCode, Intent data) {

            if (resultCode == RESULT_OK) {
                String path = data.getStringExtra(SdkEntry.EDIT_RESULT);
                //保存到系统图库
                SDKUtils.onVideoExport(getActivity(), path);
                SdkEntry.playVideo(getActivity(), path);
            }

        }
    };

    private ActivityResultHandler albumSoundEffectResultHandler = new ActivityResultHandler() {

        @Override
        public void onActivityResult1(Context context, int resultCode,
                                     Intent data) {
            if (resultCode == RESULT_OK) {
                // 返回选择的图片视频地址list
                ArrayList<String> arrMediaListPath = data
                        .getStringArrayListExtra(SdkEntry.ALBUM_RESULT);
                if (arrMediaListPath.size() > 0) {
                    if (!TextUtils.isEmpty(arrMediaListPath.get(0))) {
                        try {
                            SdkEntry.musicFilter(context, arrMediaListPath, 0);
                        } catch (InvalidArgumentException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    };

    private ActivityResultHandler albumSilhouetteResultHandler = new ActivityResultHandler() {

        @Override
        public void onActivityResult1(Context context, int resultCode,
                                     Intent data) {
            if (resultCode == RESULT_OK) {
                // 返回选择的图片视频地址list
                ArrayList<String> arrMediaListPath = data
                        .getStringArrayListExtra(SdkEntry.ALBUM_RESULT);
                if (arrMediaListPath.size() > 0) {
                    if (!TextUtils.isEmpty(arrMediaListPath.get(0))) {
                        try {
                            SdkEntry.silhouette(context, arrMediaListPath.get(0), 0);
                        } catch (InvalidArgumentException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    };


    private ActivityResultHandler albumAlienResultHandler = new ActivityResultHandler() {

        @Override
        public void onActivityResult1(Context context, int resultCode,
                                     Intent data) {
            if (resultCode == RESULT_OK) {
                // 返回选择的图片视频地址list
                ArrayList<String> arrMediaListPath = data
                        .getStringArrayListExtra(SdkEntry.ALBUM_RESULT);
                if (arrMediaListPath.size() > 0) {
                    if (!TextUtils.isEmpty(arrMediaListPath.get(0))) {
                        try {
                            SdkEntry.alien(context, arrMediaListPath, 0);
                        } catch (InvalidArgumentException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    };

    private ActivityResultHandler albumAEImageResultHandler = new ActivityResultHandler() {

        @Override
        public void onActivityResult1(Context context, int resultCode,
                                     Intent data) {
            if (resultCode == RESULT_OK) {
                // 返回选择的图片视频地址list
                ArrayList<String> arrMediaListPath = data
                        .getStringArrayListExtra(SdkEntry.ALBUM_RESULT);
                if (arrMediaListPath.size() > 0) {
                    if (!TextUtils.isEmpty(arrMediaListPath.get(0))) {
                        try {
                            SdkEntry.AEAnimation(context, arrMediaListPath, 0, true);
                        } catch (InvalidArgumentException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    };


    private ActivityResultHandler cameraResultHandler = new ActivityResultHandler() {

        @Override
        public void onActivityResult1(Context context, int resultCode,
                                     Intent data) {
            if (resultCode == SdkEntry.RESULT_CAMERA_TO_ALBUM) {
                // 点击拍摄的相册按钮，将返回在此，并在这里做进入相册界面操作
                SdkEntry.openAlbum(context,
                        UIConfiguration.ALBUM_SUPPORT_DEFAULT,
                        ALBUM_REQUEST_CODE);
            } else if (resultCode == RESULT_OK) {
                Toast.makeText(getContext(),"视频保存到素材库",Toast.LENGTH_SHORT).show();
                // 美颜参数回调
                FaceuInfo info = data
                        .getParcelableExtra(SdkEntry.INTENT_KEY_FACEU);
                if (null != info) {
                    Log.e("faceu美颜参数", info.toString());
                }
                // 拍摄后返回的媒体路径
                ArrayList<String> arrMediaListPath = new ArrayList<String>();
                String videoPath = data
                        .getStringExtra(SdkEntry.INTENT_KEY_VIDEO_PATH);
                String picPath = data
                        .getStringExtra(SdkEntry.INTENT_KEY_PICTURE_PATH);
                arrMediaListPath.add(videoPath);
                arrMediaListPath.add(picPath);

                System.out.println("picPath==="+picPath);
                System.out.println("videoPath==="+videoPath);
                ///storage/emulated/0/Android/data/com.jwzt.cb.product/files/DCIM/shotImages/PIC_20190121_151129.jpg
                //Environment.getExternalStorageDirectory()+ "/Media/Photo/"
if(picPath!=null&&!"null".equals(picPath)){

    String thumbFolder=FileOperateUtil.getFolderPath(getActivity(), FileOperateUtil.TYPE_THUMBNAIL, "test");

    String dstFolder=FileOperateUtil.getFolderPath(getActivity(), FileOperateUtil.TYPE_IMAGE, "test");
    String  thumb=thumbFolder+"/"+picPath.substring(picPath.lastIndexOf("/")+1,picPath.length());
    System.out.println("thumb==="+thumb);
    copyFile(picPath,thumb);
    copyFile(picPath,dstFolder+"/"+picPath.substring(picPath.lastIndexOf("/")+1,picPath.length()));
    deleteSingleFile(picPath);
}
                String logInfo = String.format("Video path：%s,Picture path：%s",
                        videoPath, picPath);
                Log.e(TAG, logInfo);


                if (configData.albumSupportFormatType == UIConfiguration.ALBUM_SUPPORT_IMAGE_ONLY) {

                    if (videoPath != null) {
                        SdkEntry.selectMedia(context);
                        return;
                    }
                } else if (configData.albumSupportFormatType == UIConfiguration.ALBUM_SUPPORT_VIDEO_ONLY) {
                    if (picPath != null) {
                        SdkEntry.selectMedia(context);
                        return;
                    }
                }

//                if (data.getBooleanExtra(SdkEntry.INTENT_KEY_USE_MV_EDIT, false)) {
//                    Log.e(TAG, "onActivityResult: mv");
//                    initCameraShortVideoConfig();
//                } else {
//                    Log.e(TAG, "onActivityResult: false");
//                    initEditorUIAndExportConfig();
//                }
//                try {
//                    editMedia(context, arrMediaListPath);
//                } catch (InvalidArgumentException e) {
//                    e.printStackTrace();
//                }
            }
        }
    };


    private ActivityResultHandler cameraAntiChangeResultHandler = new ActivityResultHandler() {

        @Override
        public void onActivityResult1(Context context, int resultCode,
                                     Intent data) {
            if (resultCode == RESULT_OK) {
                String logInfo = String.format("Video path：%s,Picture path：%s",
                        data.getStringExtra(SdkEntry.INTENT_KEY_VIDEO_PATH),
                        data.getStringExtra(SdkEntry.INTENT_KEY_PICTURE_PATH));
                Log.d(TAG, logInfo);
                SDKUtils.onPlayVideo(context, data.getStringExtra(SdkEntry.INTENT_KEY_VIDEO_PATH));
            }
        }
    };
    private ActivityResultHandler albumResultHandler = new ActivityResultHandler() {

        @Override
        public void onActivityResult1(Context context, int resultCode,
                                     Intent data) {
            if (resultCode == SdkEntry.RESULT_ALBUM_TO_CAMERA) {
                // 点击相册的拍摄按钮，将返回在此，并在这里做进入拍摄界面操作
                SdkEntry.record(context, CAMERA_REQUEST_CODE);
            } else if (resultCode == RESULT_OK) {
                // 返回选择的图片视频地址list
                ArrayList<String> arrMediaListPath = data
                        .getStringArrayListExtra(SdkEntry.ALBUM_RESULT);
                String logInfo = "";
                for (String path : arrMediaListPath) {
                    Log.d(TAG, path);
                    logInfo += path + "\n";
                }
                Toast.makeText(context, logInfo, Toast.LENGTH_LONG).show();
            }
        }
    };
    /**
     * 要导出的视频
     */
    private ActivityResultHandler albumExportResultHandler = new ActivityResultHandler() {

        @Override
        public void onActivityResult1(Context context, int resultCode,
                                     Intent data) {
            if (resultCode == RESULT_OK) {
                // 返回选择的图片视频地址list
                ArrayList<String> arrMediaListPath = data
                        .getStringArrayListExtra(SdkEntry.ALBUM_RESULT);

                for (String path : arrMediaListPath) {
                    VideoConfig videoConfig = new VideoConfig();
                    float durationS = VirtualVideo.getMediaInfo(path, videoConfig, true);
                    Log.d(TAG, path + "," + durationS + "," + videoConfig);
                }

                VideoConfig videoConfig = new VideoConfig();
                videoConfig.setVideoEncodingBitRate(4000 * 1000);

                int numChannel = AudioConfigDialog.audioNumChannel;
                if (numChannel == 0) {
                    numChannel = videoConfig.getAudioNumChannels();
                }
                int sampleRate = AudioConfigDialog.audioSampleRate;
                if (sampleRate == 0) {
                    sampleRate = videoConfig.getAudioSampleRate();
                }
                int audioBitRate = AudioConfigDialog.audioBitRate;
                if (audioBitRate == 0) {
                    audioBitRate = videoConfig.getAudioBitrate();
                }
                videoConfig.setAudioEncodingParameters(numChannel, sampleRate, audioBitRate);

                //.水印,左下角
                Watermark watermark = new Watermark(EDIT_WATERMARK_PATH);
                watermark.setShowRect(new RectF(0, 1f, 1f, 1.0f));
                //片尾
                Trailer trailer = new Trailer(SDKUtils.createVideoTrailerImage(getActivity(), "秀友", 480, 50, 50), 2f, 0.5f);

                String exportPath = Environment.getExternalStorageDirectory() + "/export.mp4";
                ExportVideoLisenter mExportListener = new ExportVideoLisenter(exportPath);
                try {
                    SdkEntry.exportVideo(getActivity(), videoConfig, arrMediaListPath, exportPath, watermark, trailer, mExportListener);
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * 导出视频的回调演示
     */
    private class ExportVideoLisenter implements ExportListener {
        private String mPath;
        private AlertDialog mDialog;
        private ProgressBar mProgressBar;
        private Button mBtnCancel;
        private TextView mTvTitle;

        public ExportVideoLisenter(String videoPath) {
            mPath = videoPath;
        }

        @Override
        public void onExportStart() {
            View v = LayoutInflater.from(getActivity()).inflate(
                    R.layout.progress_view, null);
            mTvTitle = (TextView) v.findViewById(R.id.tvTitle);
            mTvTitle.setText("正在导出...");
            mProgressBar = (ProgressBar) v.findViewById(R.id.pbCompress);
            mBtnCancel = (Button) v.findViewById(R.id.btnCancelCompress);
            mBtnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SdkEntry.cancelExport();
                }
            });
            mDialog = new AlertDialog.Builder(getActivity()).setView(v)
                    .show();
            mDialog.setCanceledOnTouchOutside(false);
        }

        /**
         * 导出进度回调
         *
         * @param progress 当前进度
         * @param max      最大进度
         * @return 返回是否继续执行，false为终止导出
         */
        @Override
        public boolean onExporting(int progress, int max) {
            if (mProgressBar != null) {
                mProgressBar.setMax(max);
                mProgressBar.setProgress(progress);
            }
            return true;
        }

        @Override
        public void onExportEnd(int result) {
            mDialog.dismiss();
            if (result >= VirtualVideo.RESULT_SUCCESS) {
                SDKUtils.onVideoExport(getActivity(), mPath);
                SDKUtils.onPlayVideo(getActivity(), mPath);
            } else if (result != VirtualVideo.RESULT_SAVE_CANCEL) {
                Log.e(TAG, "onExportEnd: " + result);
                Toast.makeText(getActivity(), "导出失败" + result, Toast.LENGTH_LONG).show();
            }
        }
    }

    private ActivityResultHandler editResultHandler = new ActivityResultHandler() {

        @Override
        public void onActivityResult1(Context context, int resultCode,
                                     Intent data) {
            if (resultCode == RESULT_OK && null != data) {
                String mediaPath = data.getStringExtra(SdkEntry.EDIT_RESULT);
                if (mediaPath != null) {
                    Log.d(TAG, mediaPath);
                    Toast.makeText(context, mediaPath, Toast.LENGTH_LONG)
                            .show();
                }
            }
        }
    };
    private ActivityResultHandler aeListResultHanlder = new ActivityResultHandler() {

        @Override
        public void onActivityResult1(Context context, int resultCode,
                                     Intent data) {
            if (resultCode == RESULT_OK && null != data) {
                String mediaPath = data.getStringExtra(SdkEntry.EDIT_RESULT);
                if (mediaPath != null) {
                    Log.d(TAG, mediaPath);
                    SDKUtils.onVideoExport(getActivity(), mediaPath);
                    SDKUtils.onPlayVideo(getActivity(), mediaPath);
                }
            }
        }
    };

    private ActivityResultHandler trimResultHandler = new ActivityResultHandler() {

        @Override
        public void onActivityResult1(Context context, int resultCode,
                                     Intent data) {
            if (resultCode == RESULT_OK) {
                float startTime = data.getFloatExtra(SdkEntry.TRIM_START_TIME, 0f);
                float endTime = data.getFloatExtra(SdkEntry.TRIM_END_TIME, 0);
                String path = data.getStringExtra(SdkEntry.TRIM_MEDIA_PATH);
                Rect rect = data.getParcelableExtra(SdkEntry.TRIM_CROP_RECT);
                String logInfo = "截取开始时间:" + startTime + "s" + ",结束时间:"
                        + endTime + "s\n裁剪区域：" + rect + "...视频:" + path;
                Log.d(TAG, logInfo);
                Toast.makeText(context, logInfo, Toast.LENGTH_LONG).show();
            }
        }
    };

    private ActivityResultHandler trimAlbumResultHandler = new ActivityResultHandler() {

        @Override
        public void onActivityResult1(Context context, int resultCode,
                                     Intent data) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> arrCameraMediaListPath = data
                        .getStringArrayListExtra(SdkEntry.ALBUM_RESULT);
                if (arrCameraMediaListPath != null
                        && arrCameraMediaListPath.get(0) != null) {
                    String path = arrCameraMediaListPath.get(0);
                    initTrimConfig();
                    try {
                        SdkEntry.trimVideo(context, path, TRIM_REQUEST_CODE);
                    } catch (InvalidArgumentException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };


    private ActivityResultHandler shortvideoCameraResultHandler = new ActivityResultHandler() {

        @Override
        public void onActivityResult1(Context context, int resultCode,
                                     Intent data) {
            if (resultCode == SdkEntry.RESULT_CAMERA_TO_ALBUM) {
                // TODO: 按下拍摄的相册按钮，在此进入相册
                SdkEntry.openAlbum(context,
                        UIConfiguration.ALBUM_SUPPORT_VIDEO_ONLY,
                        SHORTVIDEO_ALBUM_REQUEST_CODE);
            } else if (resultCode == RESULT_OK) {
                // TODO: 拍摄完成，在此进入编辑界面
                FaceuInfo info = data
                        .getParcelableExtra(SdkEntry.INTENT_KEY_FACEU);
                if (null != info) {
                    Log.e("faceu美颜参数", info.toString());
                }

                ArrayList<String> arrMediaListPath = new ArrayList<String>();
                arrMediaListPath.add(data
                        .getStringExtra(SdkEntry.INTENT_KEY_VIDEO_PATH));
                try {
                    editMedia(context, arrMediaListPath);
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private ActivityResultHandler shortvideoAlbumResultHandler = new ActivityResultHandler() {

        @Override
        public void onActivityResult1(Context context, int resultCode,
                                     Intent data) {
            if (resultCode == SdkEntry.RESULT_ALBUM_TO_CAMERA) {
                // TODO: 按下相册的拍摄按钮，在此进入拍摄界面
                SdkEntry.record(context, SHORTVIDEO_CAMERA_REQUEST_CODE);
            } else if (resultCode == RESULT_OK) {
                // TODO: 选择媒体结束后，在此进入截取界面
                ArrayList<String> arrCameraMediaListPath = data
                        .getStringArrayListExtra(SdkEntry.ALBUM_RESULT);
                if (arrCameraMediaListPath != null) {
                    if (arrCameraMediaListPath.get(0) != null) {
                        String path = arrCameraMediaListPath.get(0);
                        // 获取视频信息
                        VideoMetadataRetriever vmr = new VideoMetadataRetriever();
                        vmr.setDataSource(path);
                        float duration = Float
                                .valueOf(vmr
                                        .extractMetadata(VideoMetadataRetriever.METADATA_KEY_VIDEO_DURATION));
                        int videoHeight = Integer
                                .valueOf(vmr
                                        .extractMetadata(VideoMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                        int videoWidth = Integer
                                .valueOf(vmr
                                        .extractMetadata(VideoMetadataRetriever.METADATA_KEY_VIDEO_WIDHT));

                        if (duration < 15 && videoHeight == videoWidth) {
                            // TODO: 视频小于15秒且为正方形视频，将跳过截取，进入编辑界面
                            try {
                                editMedia(getActivity(),
                                        arrCameraMediaListPath,
                                        EDIT_REQUEST_CODE);
                            } catch (InvalidArgumentException e) {
                                e.printStackTrace();
                            }
                            return;
                        }
                        try {
                            trimVideo(getActivity(),
                                    arrCameraMediaListPath.get(0),
                                    SHORTVIDEO_TRIM_REQUEST_CODE);
                        } catch (InvalidArgumentException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    };

    private ActivityResultHandler shortvideoTrimResultHandler = new ActivityResultHandler() {

        @Override
        public void onActivityResult1(Context context, int resultCode,
                                     Intent data) {
            if (resultCode == RESULT_OK) {
                // TODO: 截取完成，在此进入编辑界面
                EditObject eo = new EditObject(
                        data.getStringExtra(SdkEntry.TRIM_MEDIA_PATH));
                Rect clip = data.getParcelableExtra(SdkEntry.TRIM_CROP_RECT);
                if (null != clip) {
                    eo.setCropRect(new RectF(clip));
                }
                eo.setStartTime(data.getFloatExtra(SdkEntry.TRIM_START_TIME, 0f));
                eo.setEndTime(data.getFloatExtra(SdkEntry.TRIM_END_TIME, 0f));
                try {
                    editMedia(context, eo, EDIT_REQUEST_CODE);
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private ActivityResultHandler albumPlayerResultHandler = new ActivityResultHandler() {

        @Override
        public void onActivityResult1(Context context, int resultCode,
                                     Intent data) {
            if (resultCode == RESULT_OK) {
                // 返回选择的视频地址list
                ArrayList<String> arrMediaListPath = data
                        .getStringArrayListExtra(SdkEntry.ALBUM_RESULT);
                if (arrMediaListPath.size() > 0) {
                    if (!TextUtils.isEmpty(arrMediaListPath.get(0))) {
                        SDKUtils.onPlayVideo(context, arrMediaListPath.get(0));
                    }
                }
            }
        }
    };

    /**
     * 选择要压缩的视频资源
     */
    private ActivityResultHandler albumCompressResultHandler = new ActivityResultHandler() {

        @Override
        public void onActivityResult1(Context context, int resultCode,
                                     Intent data) {
            if (resultCode == RESULT_OK) {
                // 返回选择的图片视频地址list
                ArrayList<String> arrMediaListPath = data
                        .getStringArrayListExtra(SdkEntry.ALBUM_RESULT);
                if (arrMediaListPath.size() > 0) {
                    if (!TextUtils.isEmpty(arrMediaListPath.get(0))) {
                        SdkEntry.onCompressVideo(context,
                                arrMediaListPath.get(0), iCompressVideoCallback);
                    }
                }
            }
        }
    };
    /**
     * 照片电影模式
     */
    private ActivityResultHandler ablumAnimationResultHandler = new ActivityResultHandler() {

        @Override
        public void onActivityResult1(Context context, int resultCode,
                                     Intent data) {
            if (resultCode == RESULT_OK) {
                // 返回选择的图片视频地址list
                ArrayList<String> arrMediaListPath = data
                        .getStringArrayListExtra(SdkEntry.ALBUM_RESULT);
                if (arrMediaListPath.size() > 0) {
                    SdkEntry.onAnimation(getActivity(), arrMediaListPath, true, ANIMATION_RESULT_CODE);
                }
            }
        }
    };
    /**
     * quik
     */
    private ActivityResultHandler ablumQuikResultHandler = new ActivityResultHandler() {

        @Override
        public void onActivityResult1(Context context, int resultCode,
                                     Intent data) {
            System.out.println("RESULT_OK===="+RESULT_OK+"============="+resultCode);
            if (resultCode == RESULT_OK) {
                // 返回选择的图片视频地址list
                ArrayList<String> arrMediaListPath = data
                        .getStringArrayListExtra(SdkEntry.ALBUM_RESULT);
                for (int i = 0; i < arrMediaListPath.size(); i++) {
                    System.out.println("arrMediaListPath===="+arrMediaListPath.get(i));
                }

                if (arrMediaListPath.size() > 0) {
                    try {
                        SdkEntry.quik(getActivity(), arrMediaListPath, ANIMATION_RESULT_CODE);
                    } catch (InvalidArgumentException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };
    /**
     * 异形显示
     */
    private ActivityResultHandler ablumPointFResultHandler = new ActivityResultHandler() {

        @Override
        public void onActivityResult1(Context context, int resultCode,
                                     Intent data) {
            if (resultCode == RESULT_OK) {
                // 返回选择的图片视频地址list
                ArrayList<String> arrMediaListPath = data
                        .getStringArrayListExtra(SdkEntry.ALBUM_RESULT);
                if (arrMediaListPath.size() > 0) {
                    SdkEntry.onAnimation(getActivity(), arrMediaListPath, false, ANIMATION_RESULT_CODE);
                }
            }
        }
    };

    /**
     * 压缩视频回调函数
     */
    private ICompressVideoCallback iCompressVideoCallback = new ICompressVideoCallback() {
        private AlertDialog dialog;
        private ProgressBar progressBar;
        private Button btnCancel;
        private Date startDate;

        @Override
        public void onCompressStart() {
            startDate = new Date(System.currentTimeMillis());
            View v = LayoutInflater.from(getActivity()).inflate(
                    R.layout.progress_view, null);
            progressBar = (ProgressBar) v.findViewById(R.id.pbCompress);
            btnCancel = (Button) v.findViewById(R.id.btnCancelCompress);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SdkEntry.cancelCompressVideo();
                }
            });
            dialog = new AlertDialog.Builder(getActivity()).setView(v)
                    .show();
            dialog.setCanceledOnTouchOutside(false);
        }

        @Override
        public void onProgress(int progress, int max) {
            if (progressBar != null) {
                progressBar.setMax(max);
                progressBar.setProgress(progress);
            }
        }

        @Override
        public void onCompressError(String errorLog) {
            Log.e(TAG, errorLog);
            if (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }
        }

        @Override
        public void onCompressComplete(String path) {
            Date endDate = new Date(System.currentTimeMillis());
            long diff = endDate.getTime() - startDate.getTime();
            VideoMetadataRetriever vmr = new VideoMetadataRetriever();
            vmr.setDataSource(path);
            float duration = Float
                    .valueOf(vmr
                            .extractMetadata(VideoMetadataRetriever.METADATA_KEY_VIDEO_DURATION));
            Toast.makeText(getActivity(), "压缩倍速: " + String.format("%.2f", (duration * 1000) / diff) + "x", Toast.LENGTH_LONG).show();
            SDKUtils.onPlayVideo(getActivity(), path);
            if (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }
        }
    };

    // 防止多次弹出对话框
    private boolean isConfigDialogShow;
    private ConfigData configData;

    /**
     * 初始化并返回配置
     */
    private ConfigData initAndGetConfigData() {
        if (configData == null) {
            configData = new ConfigData();
        }
        return configData;
    }

    /**
     * 导出测试资源
     */
    private void exportDemoResource() {

        if (!SDKUtils.isValidFile(EDIT_PICTURE_PATH)
                || !SDKUtils.isValidFile(EDIT_L_VIDEO_PATH)
                || !SDKUtils.isValidFile(EDIT_S_VIDEO_PATH)
                || !SDKUtils.isValidFile(EDIT_P_VIDEO_PATH)
                || !SDKUtils.isValidFile(EDIT_WATERMARK_PATH)
                || !SDKUtils.isValidFile(MASK_PATH)
                ) {
            new AsyncTask<Integer, Integer, Integer>() {
                private ProgressDialog m_dlgProgress;


                @Override
                protected void onPreExecute() {
                    m_dlgProgress = ProgressDialog.show(getActivity(),
                            null, getString(R.string.export_asset));
                }

                @Override
                protected Integer doInBackground(Integer... params) {
                    SDKUtils.assetRes2File(getActivity().getAssets(), "demomedia/android.jpg", EDIT_PICTURE_PATH);
                    SDKUtils.assetRes2File(getActivity().getAssets(), "demomedia/demoVideo1.mp4", EDIT_L_VIDEO_PATH);
                    SDKUtils.assetRes2File(getActivity().getAssets(), "demomedia/demoVideo2.mp4", EDIT_S_VIDEO_PATH);
                    SDKUtils.assetRes2File(getActivity().getAssets(), "demomedia/demoVideo3.mp4", EDIT_P_VIDEO_PATH);
                    SDKUtils.assetRes2File(getActivity().getAssets(), "watermark.png", EDIT_WATERMARK_PATH);
                    SDKUtils.assetRes2File(getActivity().getAssets(), "mask.png", MASK_PATH);
                    return null;
                }

                @Override
                protected void onPostExecute(Integer result) {
                    m_dlgProgress.dismiss();
                    m_dlgProgress = null;
                }
            }.execute();
        }

        //部分版本由于缺少内置资源不支持此功能
        if (SdkEntry.isLite(getActivity())) {
//            getActivity().findViewById(R.id.btnQuik).setVisibility(View.GONE);
//            getActivity().findViewById(R.id.imageTrans).setVisibility(View.GONE);
//            getActivity().findViewById(R.id.apiVideoTemp).setVisibility(View.GONE);
        }

    }


    /**
     * 重置持续化保存的配置
     */
    private void resetConfigInstanceState() {
        configData = null;
        Toast.makeText(getActivity(),
                "Reset all " + (saveConfigInstanceState() ? "success！" : "failures！"),
                Toast.LENGTH_SHORT).show();
    }

    /**
     * 持续化保存配置
     */
    private boolean saveConfigInstanceState() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("demo",
                Context.MODE_PRIVATE);
        return SdkService.saveObject(sharedPreferences, "CONFIG_DATA",
                initAndGetConfigData());
    }

    /**
     * 还原持续久化保存的配置
     */
    private void restoreConfigInstanceState() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("demo",
                Context.MODE_PRIVATE);
        configData = SdkService.restoreObject(sharedPreferences,
                "CONFIG_DATA", initAndGetConfigData());
    }

    private SparseArray<ActivityResultHandler> registeredActivityResultHandlers;

    /**
     * 注册响应activity result
     *
     * @param requestCode
     * @param handler
     */
    private void registerActivityResultHandler(int requestCode,
                                               ActivityResultHandler handler) {
       // System.out.println("requestCode===="+requestCode);
        if (null == registeredActivityResultHandlers) {
            registeredActivityResultHandlers = new SparseArray<HomeFragment.ActivityResultHandler>();
        }
        registeredActivityResultHandlers.put(requestCode, handler);
    }

    private interface ActivityResultHandler {
        /**
         * 响应
         *
         * @param context
         * @param resultCode The integer result code returned by the child activity
         *                   through its setResult().
         * @param data       An Intent, which can return result data to the caller
         *                   (various data can be attached to Intent "extras").
         */
        void onActivityResult1(Context context, int resultCode, Intent data);
    }

    @Override
    public  void onActivityResult(int requestCode, int resultCode, Intent data) {
      //  super.onActivityResult(requestCode, resultCode, data);
        System.out.println("onActivityResult====requestCode="+requestCode+"===resultCode=="+resultCode);

           if(resultCode==500){

               MessageAllBean messageAllBean = mList.get(OnClickItemIndex);
               messageAllBean.setState("2");
               mList.set(OnClickItemIndex,messageAllBean);
               System.out.println("更新了数据==="+OnClickItemIndex+"===="+messageAllBean.getState());
               messageAdapter.notifyDataSetChanged();
           }

        if (null != registeredActivityResultHandlers) {
            ActivityResultHandler handler = registeredActivityResultHandlers
                    .get(requestCode);
            if (null != handler) {
                handler.onActivityResult1(getActivity(), resultCode, data);
            }
        }else{
        }
    }


    /**
     *  复制单个文件
     * @param oldPath 原文件路径 如：c:/fqf.txt String
     * @param newPath 复制后路径 如：f:/fqf.txt
     */ public  void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {//文件存在时
                 InputStream inStream = new FileInputStream(oldPath);
                //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                int value = 0 ;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;
                    //字节数 文件大小
                    value ++ ;
                    //计数

                    fs.write(buffer, 0, byteread);


                    }
                    inStream.close();
                System.out.println("复制单个文件操作完成");
            }

        } catch (Exception e) { System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
     }


    /**
     * 删除单个文件
     *
     * @param filePath 被删除文件的文件名
     */
    public  void deleteSingleFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            file.delete();
        }
    }



}
