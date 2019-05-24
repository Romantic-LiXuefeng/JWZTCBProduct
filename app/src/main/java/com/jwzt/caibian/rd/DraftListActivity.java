package com.jwzt.caibian.rd;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jwzt.caibian.util.SDKUtils;
import com.jwzt.cb.product.R;
import com.rd.lib.utils.FileUtils;
import com.rd.vecore.VirtualVideo;
import com.rd.vecore.exception.InvalidArgumentException;
import com.rd.vecore.listener.ExportListener;
import com.rd.veuisdk.BaseActivity;
import com.rd.veuisdk.IShortVideoInfo;
import com.rd.veuisdk.SdkEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * 草稿箱列表
 */
public class DraftListActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private List<IShortVideoInfo> mVideoInfoList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = "DraftListActivity";
        setContentView(R.layout.activity_draft_list_layout);
        initView();
    }

    private DraftAdapter mAdapter;

    private void initView() {
        findViewById(com.rd.veuisdk.R.id.btnLeft).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mRecyclerView = (RecyclerView) findViewById(com.rd.veuisdk.R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new DraftAdapter(this);
        mVideoInfoList = new ArrayList<>();

        //设置适配器
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //导出成功，UIsdk会删除此草稿视频
        mVideoInfoList.clear();
        List<IShortVideoInfo> tmp = SdkEntry.getDraftList(this);
        if (null != tmp && tmp.size() > 0) {
            mVideoInfoList.addAll(tmp);
        }
        if (mVideoInfoList.size() == 0) {
            String str = getString(R.string.noDraftList);
            Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
        }
        mAdapter.notifyDataSetChanged();
    }


    private final int REQUEST_EDIT = 500;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_EDIT) {
                String mediaPath = data.getStringExtra(SdkEntry.EDIT_RESULT);
                if (mediaPath != null) {
                    Log.d(TAG, "onActivityResult:" + mediaPath);
                    Toast.makeText(this, mediaPath, Toast.LENGTH_LONG)
                            .show();
                }
            }

        }
    }


    /**
     * 导出视频的回调演示
     */
    private class ExportVideoLisenter implements ExportListener {
        private String mPath;
        private AlertDialog mDialog;
        private ProgressBar mProgressBar;
        private Button mBtnCancel;
        private TextView mTvTitle;

        public void setPath(String path) {
            mPath = path;
        }

        public ExportVideoLisenter() {
        }

        @Override
        public void onExportStart() {
            View v = LayoutInflater.from(DraftListActivity.this).inflate(
                    R.layout.progress_view, null);
            mTvTitle = (TextView) v.findViewById(R.id.tvTitle);
            mTvTitle.setText(R.string.exporting);
            mProgressBar = (ProgressBar) v.findViewById(R.id.pbCompress);
            mBtnCancel = (Button) v.findViewById(R.id.btnCancelCompress);
            mBtnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SdkEntry.cancelExport();
                }
            });
            mDialog = new AlertDialog.Builder(DraftListActivity.this).setView(v)
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
            Log.e(TAG, "onExportEnd: " + result + "   :" + mPath);
            mDialog.dismiss();
            if (result >= VirtualVideo.RESULT_SUCCESS) {
                // 导出成功，uisdk会删除草稿箱视频
                SDKUtils.onVideoExport(DraftListActivity.this, mPath);
                SDKUtils.onPlayVideo(DraftListActivity.this, mPath);
            } else if (result != VirtualVideo.RESULT_SAVE_CANCEL) {
                Log.e(TAG, "onExportEnd: " + result);
                String text = getString(R.string.exportFailed);
                Toast.makeText(DraftListActivity.this, text + result, Toast.LENGTH_LONG).show();
            }
        }
    }


    class DraftAdapter extends RecyclerView.Adapter<DraftAdapter.ViewHolder> {
        private LayoutInflater mLayoutInflater;

        DraftAdapter(Context context) {
            mLayoutInflater = LayoutInflater.from(context);
        }

        /***
         * 部分资源不存在（原始视频被删除）
         * @param info
         */
        private void somethingNotExits(IShortVideoInfo info) {
            String text = getString(R.string.somethingNotExits);
            Toast.makeText(DraftListActivity.this, text, Toast.LENGTH_SHORT).show();
            //部分媒体被删除，主动删除草稿箱视频，清理数据
            boolean re = SdkEntry.deleteDraft(DraftListActivity.this, info);
            if (re) {
                mVideoInfoList.clear();
                List<IShortVideoInfo> tmp = SdkEntry.getDraftList(DraftListActivity.this);
                if (null != tmp && tmp.size() > 0) {
                    mVideoInfoList.addAll(tmp);
                }
                notifyDataSetChanged();
            }
        }

        @Override
        public DraftAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = mLayoutInflater.inflate(R.layout.item_draft_layout, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(DraftAdapter.ViewHolder holder, int position) {


            final IShortVideoInfo info = mVideoInfoList.get(position);
//            Log.e(TAG, "onBindViewHolder: " + position + "  " + info);
            if (FileUtils.isExist(info.getCover())) {
                SDKUtils.setCover(holder.ivThumb, info.getCover());
            }
            holder.tvCreateTime.setText(SDKUtils.getDate(info.getCreateTime()));
            holder.tvDuration.setText(getString(R.string.duration_text, info.getDuration()));

            holder.btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        SdkEntry.onEditDraft(DraftListActivity.this, info, REQUEST_EDIT);
                    } catch (InvalidArgumentException e) {
                        e.printStackTrace();
                        somethingNotExits(info);
                    }

                }
            });
            holder.btnExport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ExportVideoLisenter exportVideoLisenter = new ExportVideoLisenter();
                    try {
                        String dst = SdkEntry.onExportDraft(DraftListActivity.this, info, exportVideoLisenter);
                        exportVideoLisenter.setPath(dst);
                    } catch (InvalidArgumentException e) {
                        e.printStackTrace();
                        somethingNotExits(info);
                    }
                }
            });
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //删除单个草稿箱视频
                    AlertDialog.Builder ab = new AlertDialog.Builder(DraftListActivity.this);
                    ab.setMessage(R.string.draft_dialog_title);
                    ab.setPositiveButton(getString(R.string.draft_dialog_pos), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            boolean re = SdkEntry.deleteDraft(DraftListActivity.this, info);
                            Toast.makeText(DraftListActivity.this, re ? getString(R.string.delete_success) : getString(R.string.delete_failed), Toast.LENGTH_SHORT).show();
                            if (re) {
                                mVideoInfoList.clear();
                                List<IShortVideoInfo> tmp = SdkEntry.getDraftList(DraftListActivity.this);
                                if (null != tmp && tmp.size() > 0) {
                                    mVideoInfoList.addAll(tmp);
                                }
                                notifyDataSetChanged();
                            }
                        }
                    });
                    ab.setNegativeButton(getString(R.string.draft_dialog_neg), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });
                    ab.show();
                }
            });


        }


        @Override
        public int getItemCount() {
            return mVideoInfoList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            SimpleDraweeView ivThumb;
            TextView tvCreateTime;
            TextView tvDuration;
            Button btnExport, btnEdit, btnDelete;

            public ViewHolder(View itemView) {
                super(itemView);
                ivThumb = itemView.findViewById(R.id.ivCover);
                tvCreateTime = itemView.findViewById(R.id.tvCreateTime);
                tvDuration = itemView.findViewById(R.id.tvDuration);
                btnExport = itemView.findViewById(R.id.btnExport);
                btnEdit = itemView.findViewById(R.id.btnEdit);
                btnDelete = itemView.findViewById(R.id.btnDelete);

            }
        }
    }

}
