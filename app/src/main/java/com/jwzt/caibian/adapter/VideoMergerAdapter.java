package com.jwzt.caibian.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.jwzt.caibian.activity.VideoDivideActivity;
import com.jwzt.caibian.bean.AttachsBeen;
import com.jwzt.caibian.db.DatabaseContext;
import com.jwzt.caibian.db.RecordDao;
import com.jwzt.caibian.interfaces.EmptyListener;
import com.jwzt.caibian.interfaces.PlayerVideoInterface;
import com.jwzt.caibian.interfaces.UploadIndexListener;
import com.jwzt.caibian.util.FileUtil;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.UserToast;
import com.jwzt.caibian.widget.SwipeMenuLayout;
import com.jwzt.caibian.xiangce.Bimp;
import com.jwzt.caibian.xiangce.ItemImage;
import com.jwzt.caibian.xiangce.OtherUtils;
import com.jwzt.cb.product.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 视频素材的适配器
 *
 * @author howie
 */
public class VideoMergerAdapter extends BaseAdapter implements OnClickListener {
    private EmptyListener emptyListener;
    private UploadIndexListener uploadIndexListener;
    /**
     * 想要删除的条目的索引
     */
    private int deleteIndex = -1;
    /**
     * 想要删除的条目对应的滑动删除布局
     */
    private SwipeMenuLayout deleteSML;
    private AlertDialog alertDialog;
    private VideoEditListener listener;
    private int currentPlaying = -1;//用于记录当前正在播放的视频
    private RecordDao dao;
    private Context mContext;
    //	private Gson gson;
    private String mUserId;
    private ArrayList<AttachsBeen> mList;
    private boolean editing;
    private int mWidth;
    private PlayerVideoInterface mPlayervideo;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    saveVideoFootages();
                    break;
            }
        }

        ;
    };
    private DisplayImageOptions options;

//	private ImageLoader imageLoader;
//    private DisplayImageOptions options;

    public void setUploadIndexListener(UploadIndexListener uploadIndexListener, PlayerVideoInterface playervideo) {
        this.uploadIndexListener = uploadIndexListener;
        this.mPlayervideo = playervideo;
    }

    public boolean isEditing() {
        return editing;
    }

    public void setEditing(boolean editing) {

        this.editing = editing;
    }

    public VideoMergerAdapter(Context mContext, ArrayList<AttachsBeen> strs, String userId) {
        super();
        this.mContext = mContext;
        this.mList = strs;
        this.mUserId = userId;
        int screenWidth = OtherUtils.getWidthInPx(mContext);
        mWidth = (screenWidth - OtherUtils.dip2px(mContext, 4)) / 3;
        dao = new RecordDao(new DatabaseContext(mContext));
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.replace) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.replace) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.replace) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .bitmapConfig(Config.RGB_565)
//        .displayer(new FadeInBitmapDisplayer(100))
                .build(); // 构建完成
//	    imageLoader = ImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public AttachsBeen getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final AttachsBeen attachsBeen = mList.get(position);
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.videomerger_item_layout, null);
            holder = new ViewHolder();
            holder.rl = convertView.findViewById(R.id.rl);
            holder.sml = (SwipeMenuLayout) convertView.findViewById(R.id.sml);
            holder.iv_select = (ImageView) convertView.findViewById(R.id.iv_select);
            holder.iv_video = (ImageView) convertView.findViewById(R.id.iv_video);
            holder.iv_play = (ImageView) convertView.findViewById(R.id.iv_play);
            holder.iv_arrow = (ImageView) convertView.findViewById(R.id.iv_arrow);
            holder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
            holder.iv_share = (ImageView) convertView.findViewById(R.id.iv_share);
            holder.iv_select_copy = (ImageView) convertView.findViewById(R.id.iv_select_copy);
            holder.iv_video_copy = (ImageView) convertView.findViewById(R.id.iv_video_copy);
            holder.iv_play_copy = (ImageView) convertView.findViewById(R.id.iv_play_copy);
            holder.iv_mark = (ImageView) convertView.findViewById(R.id.iv_mark);
            holder.iv_mark_copy = (ImageView) convertView.findViewById(R.id.iv_mark_copy);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tv_mark_num = (TextView) convertView.findViewById(R.id.tv_mark_num);
            holder.tv_name_copy = (TextView) convertView.findViewById(R.id.tv_name_copy);
            holder.tv_time_copy = (TextView) convertView.findViewById(R.id.tv_time_copy);
            holder.tv_mark_num_copy = (TextView) convertView.findViewById(R.id.tv_mark_num_copy);
            holder.rl_copy = convertView.findViewById(R.id.rl_copy);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_name.setText(attachsBeen.getAchsPath());
        holder.tv_name_copy.setText(attachsBeen.getAchsPath());
        //最后一次修改的日期
        String modifiedTime = FileUtil.getModifiedTime(attachsBeen.getAchsPath());
        holder.tv_time.setText(modifiedTime);
        holder.tv_time_copy.setText(modifiedTime);


        //视频缩略图的添加
        String achpath = attachsBeen.getAchsPath();
        if (isContainChinese(achpath)) {//表示含有中文
            Bitmap srcBitmap = ThumbnailUtils.createVideoThumbnail(attachsBeen.getAchsPath(), 0);
//			srcBitmap = ThumbnailUtils.extractThumbnail(srcBitmap, 420, 300);
            holder.iv_video.setImageBitmap(srcBitmap);
            holder.iv_video_copy.setImageBitmap(srcBitmap);
        } else {
            Uri uri = Uri.fromFile(new File(attachsBeen.getAchsPath()));
            ImageLoader.getInstance().displayImage(uri + "", holder.iv_video, options);
            ImageLoader.getInstance().displayImage(uri + "", holder.iv_video_copy, options);
        }

        String filepath = attachsBeen.getAchsPath();
        System.out.println("filepathfilepath" + filepath);
        AttachsBeen bean = dao.isExist(attachsBeen);
        if (bean.getFlags() == null) {//说明不是通过这个app录制的
            holder.iv_mark.setVisibility(View.INVISIBLE);
            holder.iv_mark_copy.setVisibility(View.INVISIBLE);
            holder.tv_mark_num.setVisibility(View.INVISIBLE);
            holder.tv_mark_num_copy.setVisibility(View.INVISIBLE);//隐藏标记的图标和标记个数的文字
        } else {
            holder.iv_mark.setVisibility(View.VISIBLE);
            holder.iv_mark_copy.setVisibility(View.VISIBLE);
            holder.tv_mark_num.setVisibility(View.VISIBLE);
            holder.tv_mark_num_copy.setVisibility(View.VISIBLE);//隐藏标记的图标和标记个数的文字
            holder.tv_mark_num.setText("x" + bean.getFlags().size());
            holder.tv_mark_num_copy.setText("x" + bean.getFlags().size());
        }
        holder.rl.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                goToEdit(position);
            }
        });

        final SwipeMenuLayout swipeMenuLayout = holder.sml;
        holder.iv_delete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                deleteIndex = position;
                deleteSML = swipeMenuLayout;   //先进行注释,等待操作
                showTip();
            }
        });
        holder.iv_share.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (uploadIndexListener != null) {
                    uploadIndexListener.uploadIndex(position);//回调
                }
            }
        });


        final String filePath = mList.get(position).getAchsPath();
        if (filePath != null) {
            File file = new File(filePath);
            if (file.exists()) {
                System.out.println("文件存在");
            } else {
                System.out.println("文件不存在");
            }
            if (filePath.contains("http:")) {
                if (filePath.endsWith(".3gp") || filePath.endsWith(".mp4")
                        || filePath.endsWith(".wmv")
                        || filePath.endsWith(".flv")
                        || filePath.endsWith(".rmvb")
                        || filePath.endsWith(".avi")
                        || filePath.endsWith(".ts")) {

                    String endName = filePath.substring(filePath.lastIndexOf("/") + 1);
                    holder.tv_name.setText(endName);
                    holder.tv_name_copy.setText(endName);
//					holder.tv_file_size.setText("大小："+file.length()/1024+"KB");
                }
            } else {

                String fileName = file.getName();
                if (fileName.endsWith(".3gp") || fileName.endsWith(".mp4")
                        || fileName.endsWith(".wmv")
                        || fileName.endsWith(".flv")
                        || fileName.endsWith(".rmvb")
                        || fileName.endsWith(".avi")
                        || fileName.endsWith(".ts")) {

                    String endName = filePath.substring(filePath.lastIndexOf("/") + 1);
                    holder.tv_name.setText(endName);
                    holder.tv_name_copy.setText(endName);
//					holder.tv_file_size.setText("大小："+file.length()/1024+"KB");
                }
            }
        }


        if (editing) {//如果处于编辑状态
            holder.rl_copy.setVisibility(View.VISIBLE);
            holder.sml.setVisibility(View.INVISIBLE);


            int status = attachsBeen.getStatus();


            System.out.println("===============" + status);


            if (status == AttachsBeen.STATUS_SELECTED) {
                if (IsNonEmptyUtils.isList(Bimp.tempSelectBitmap)) {
                    if (Bimp.tempSelectBitmap.size() < 10) {
                        holder.iv_select.setImageResource(R.drawable.right);
                        holder.iv_select_copy.setImageResource(R.drawable.right);
                        if (isTaskGroup(filePath)) {//true表示存在该路径

                        } else {//表示不存在该路径
                            ItemImage itemImage = new ItemImage();
//							itemImage.setBitmap(attachsBeen.getAchsBitmap());
                            itemImage.setBitmap(null);
                            itemImage.setFilepath(filepath);
                            itemImage.setResult(true);
                            Bimp.tempSelectBitmap.add(0, itemImage);
                        }
                    } else {
                        UserToast.toSetToast(mContext, "您选择的资源已达上限");//9个资源为上限
                    }
                }
//				holder.iv_select_copy.setImageResource(R.drawable.footage_selected);
            } else if (status == AttachsBeen.STATUS_UNSELECTED) {
                holder.iv_select.setImageResource(R.drawable.circle_right);
                holder.iv_select_copy.setImageResource(R.drawable.circle_right);
//				ItemImage itemImage = null;
                if (IsNonEmptyUtils.isList(Bimp.tempSelectBitmap)) {
                    for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
                        if (Bimp.tempSelectBitmap.get(i) != null) {
                            if (filepath.equals(Bimp.tempSelectBitmap.get(i).getFilepath())) {
//								itemImage=Bimp.tempSelectBitmap.get(i);
                                Bimp.tempSelectBitmap.remove(i);
                            }
                        }
                    }
                }
//				holder.iv_select_copy.setImageResource(R.drawable.footage_unselected);
            }
            holder.iv_select_copy.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    int status2 = attachsBeen.getStatus();
                    if (status2 == AttachsBeen.STATUS_UNSELECTED) {
                        attachsBeen.setStatus(AttachsBeen.STATUS_SELECTED);
                    } else if (status2 == AttachsBeen.STATUS_SELECTED) {
                        attachsBeen.setStatus(AttachsBeen.STATUS_UNSELECTED);
                    }

                    System.out.println(attachsBeen.getStatus() + "==========");

                    notifyDataSetChanged();
                }
            });

        } else {//如果处于非编辑状态
            holder.rl_copy.setVisibility(View.INVISIBLE);
            holder.sml.setVisibility(View.VISIBLE);
            holder.iv_select_copy.setImageResource(R.drawable.footage_unselected);
            if (attachsBeen.getStatus() == AttachsBeen.STATUS_NORMAL) {
                holder.iv_select.setImageResource(R.drawable.footage_video_play);
            } else if (attachsBeen.getStatus() == AttachsBeen.STATUS_PLAYING) {
                holder.iv_select.setImageResource(R.drawable.footage_video_pause);
            }
            holder.iv_select.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    editing = false;
                /*	for (AttachsBeen attachBean : mList) {
						attachBean.setStatus(AttachsBeen.STATUS_NORMAL);
					}*/
                    if (position == currentPlaying) {//如果点击的是正在播放的那一个
                        if (attachsBeen.getStatus() == AttachsBeen.STATUS_NORMAL) {
                            attachsBeen.setStatus(AttachsBeen.STATUS_PLAYING);

                        } else if (attachsBeen.getStatus() == AttachsBeen.STATUS_PLAYING) {
                            attachsBeen.setStatus(AttachsBeen.STATUS_NORMAL);

                        }
                    } else {//如果点击的和正在播放的不是同一个
                        for (int i = 0; i < mList.size(); i++) {
                            if (i != position) {
                                mList.get(i).setStatus(AttachsBeen.STATUS_NORMAL);
                            } else {
                                mList.get(i).setStatus(AttachsBeen.STATUS_PLAYING);
                            }
                        }
                        currentPlaying = position;
//						attachsBeen.setStatus(AttachsBeen.STATUS_PLAYING);
                    }
                    if (mPlayervideo != null) {
                        mPlayervideo.setPlayVideo(attachsBeen.getAchsPath(), position);
                    }
//					MyWindowManager.createSmallWindow(mContext,attachsBeen.getAchsPath(),position);
                    notifyDataSetChanged();
                }
            });

        }


        return convertView;

    }

    // 判断一个字符串是否含有中文
    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 判断该路径是否在所选的集合中 返回true表示集合中有改路径 false没有
     *
     * @return
     */
    private boolean isTaskGroup(String resourcePath) {
        if (IsNonEmptyUtils.isList(Bimp.tempSelectBitmap)) {
            for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
                if (Bimp.tempSelectBitmap.get(i) != null) {
                    if (resourcePath.equals(Bimp.tempSelectBitmap.get(i).getFilepath())) {
                        return true;
                    }
                }
            }
        } else {
            return false;
        }
        return false;
    }

    public void remove(AttachsBeen item) {
        mList.remove(item);
        notifyDataSetChanged();
    }

    public void insert(AttachsBeen item, int to) {
        mList.add(to, item);
        notifyDataSetChanged();
    }

    public class ViewHolder {
        SwipeMenuLayout sml;
        ImageView iv_select, iv_video, iv_play, iv_delete, iv_share, iv_select_copy, iv_video_copy, iv_play_copy, iv_mark, iv_mark_copy, iv_arrow;
        TextView tv_name, tv_time, tv_mark_num, tv_name_copy, tv_time_copy, tv_mark_num_copy;
        View rl_copy, rl;

    }

    public void update(ArrayList<AttachsBeen> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public int getCurrentPlaying() {
        return currentPlaying;
    }

    public void setCurrentPlaying(int currentPlaying) {
        this.currentPlaying = currentPlaying;
    }

    private void saveVideoFootages() {
        if (IsNonEmptyUtils.isString(mUserId)) {
            SharedPreferences sp = mContext.getSharedPreferences(mUserId, Context.MODE_PRIVATE);
            Editor editor = sp.edit();
            String json = JSON.toJSONString(mList);
            editor.putString("videofootages", json);
            editor.commit();
        }
    }


    /**
     * 跳转视频编辑界面
     *
     * @param position
     */
    private void goToEdit(int position) {
        final String achsPath = mList.get(position).getAchsPath();
        if (!achsPath.endsWith(".mp4")) {
            Toast.makeText(mContext, "暂不支持该格式的视频编辑", Toast.LENGTH_SHORT).show();
            return;
        }
        if (listener != null) {
            listener.edit(position);
        }
        if (!TextUtils.isEmpty(achsPath) && new File(achsPath).exists()) {//如果视频文件存在
//			Intent intent=new Intent(mContext,EditVedioActivity2.class);
            Intent intent = new Intent(mContext, VideoDivideActivity.class);
            intent.putExtra("vedio_path", achsPath);
            mContext.startActivity(intent);

        }

    }

    public interface VideoEditListener {
        /**
         * 正在编辑的条目的索引
         *
         * @param position
         */
        void edit(int position);
    }

    public void setListener(VideoEditListener listener) {
        this.listener = listener;
    }

    /**
     * 显示是否进行删除操作的提示
     */
    private void showTip() {
        if (alertDialog == null) {
            alertDialog = new AlertDialog.Builder(mContext).create();
            OnKeyListener keylistener = new OnKeyListener() {
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                        return true;
                    } else {
                        return false;
                    }
                }
            };
            alertDialog.setOnKeyListener(keylistener);//保证按返回键的时候alertDialog也不会消失
            alertDialog.setOnDismissListener(new OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface arg0) {
                    // TODO Auto-generated method stub

                }
            });


        }
        alertDialog.show();
        View tipView = View.inflate(mContext, R.layout.edit_alert_layout, null);
        TextView tv_tip = (TextView) tipView.findViewById(R.id.tv_tip);
        tv_tip.setText("确认要删除选中的素材吗？");
        View tv_yes = (TextView) tipView.findViewById(R.id.tv_yes);
        //不再提醒
        tv_yes.setOnClickListener(this);
        tipView.findViewById(R.id.tv_no).setOnClickListener(this);

        alertDialog.setContentView(tipView);

    }

    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub
        switch (view.getId()) {
            case R.id.tv_yes:
                String achsPath = mList.get(deleteIndex).getAchsPath();//缩略图
                File file1 = new File(achsPath);
                if (file1.exists()) {
                    file1.delete();
                }
                deleteSML.quickClose();
                mList.remove(deleteIndex);
                if (mList != null && mList.isEmpty() && emptyListener != null) {//如果删除的是最后一条数据，删除之后就清空了
                    emptyListener.empty();
                }
                notifyDataSetChanged();
//			saveVideoFootages();
                mHandler.sendEmptyMessage(1);
                alertDialog.dismiss();
                break;
            case R.id.tv_no:
                alertDialog.dismiss();
                break;
        }
    }

    public void setEmptyListener(EmptyListener emptyListener) {
        this.emptyListener = emptyListener;
    }


}
