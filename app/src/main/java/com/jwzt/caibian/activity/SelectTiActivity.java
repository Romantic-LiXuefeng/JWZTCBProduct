package com.jwzt.caibian.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jwzt.caibian.adapter.SelectTiAdapter;
import com.jwzt.caibian.application.BaseActivity;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.application.Configs;
import com.jwzt.caibian.bean.LoginBean;
import com.jwzt.caibian.bean.TestGroupBean;
import com.jwzt.caibian.util.DialogHelp;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.UIUtils;
import com.jwzt.caibian.util.UserToast;
import com.jwzt.caibian.view.PullToRefreshLayout;
import com.jwzt.caibian.widget.MyListView;
import com.jwzt.cb.product.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 2018/9/26.
 */

public class SelectTiActivity extends BaseActivity implements View.OnClickListener{
    private TextView tv_titles;
    private ImageView iv_back,img_create;

    private MyListView listview;
    private PullToRefreshLayout prl_refersh;

    private List<TestGroupBean> mList;
    private List<TestGroupBean> mListMore;
    private SelectTiAdapter messageAdapter;
    private int pageTag;

    private CbApplication app;
    private LoginBean loginBean;

    private Handler mHandler = new Handler() {
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    DialogHelp.dismisLoadingDialog();
                    break;
                case 1:
                    messageAdapter = new SelectTiAdapter(SelectTiActivity.this, mList);
                    listview.setAdapter(messageAdapter);
                    break;
                case 2:
                    int size = mList.size();
                    mList.addAll(mListMore);
                    mListMore.clear();
                    messageAdapter.setList(mList);
                    listview.setSelection(size);
                    DialogHelp.dismisLoadingDialog();
                    break;
                default:
                    break;
            }

        }

        ;

    };



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectti_activity);
        app = (CbApplication) getApplication();
        loginBean = app.getmLoginBean();
        mList = new ArrayList<TestGroupBean>();
        mListMore = new ArrayList<TestGroupBean>();

        findView();
        initdata();
    }

    private void findView(){
        tv_titles=findViewById(R.id.tv_titles);
        tv_titles.setText("选题管理");
        iv_back=findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        img_create=findViewById(R.id.img_create);
        img_create.setOnClickListener(this);
        img_create.setVisibility(View.VISIBLE);
        listview=findViewById(R.id.mlv);
        prl_refersh = (PullToRefreshLayout) findViewById(R.id.prl_refersh);
        prl_refersh.setOnRefreshListener(new MyListener());
    }

    private void initdata() {
        String infoListUrl = String.format(Configs.daishenheListUrl, loginBean.getUserID(), "1", "");
        System.out.println("infoListUrl:" + infoListUrl);
        RequestData(infoListUrl, Configs.infoListCode, pageTag);
    }

    class MyListener implements PullToRefreshLayout.OnRefreshListener {
        @SuppressLint("HandlerLeak")
        @Override
        public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
            initdata();
            // 下拉刷新操作
            new Handler() {
                @Override
                public void handleMessage(Message msg) {// 千万别忘了告诉控件刷新完毕了哦！
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
                public void handleMessage(Message msg) {// 千万别忘了告诉控件加载完毕了哦！
                    pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            }.sendEmptyMessageDelayed(0, 2000);//2秒之后触发viewPager的自动播放
        }
    }

    private void moreData() {
        if (loginBean != null) {
            if (IsNonEmptyUtils.isList(mList)) {
                pageTag = 1;
                if (CbApplication.getNetType() != -1) {
                    DialogHelp.showLoadingDialog(SelectTiActivity.this, "", "");
                    String infoListUrl = String.format(Configs.daishenheListUrl, loginBean.getUserID(), "1", mList.get(mList.size() - 1).getId());

                    System.out.println("manuscriptUrl:" + infoListUrl);
                    RequestData(infoListUrl, Configs.infoListCode, pageTag);
                } else {
                    UserToast.toSetToast(UIUtils.getContext(), getString(R.string.please_check_net));
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                SelectTiActivity.this.finish();
                break;
            case R.id.img_create:
                Intent intent=new Intent(SelectTiActivity.this,SelectTiCreateAndEditActivity.class);
                startActivity(intent);
                break;
        }
    }


    @Override
    protected void initRequestOnSuccess(String result, int code, int biaoshi) {
        initDataParse(result,code);
    }

    @Override
    protected void initRequestOnStart(String result, int code, int biaoshi) {
    }
    @Override
    protected void initRequestOnFailure(String failure, int code, int biaoshi) {
    }
    @Override
    protected void initRequestOnCache(String result, int code, int biaoshi) {
    }

    private void initDataParse(String result, int code) {
        if (code == Configs.infoListCode) {
            if (pageTag == 0) {//刷新
                if (!result.equals("")) {//表示获取成功
                    JSONObject jsonObject = JSON.parseObject(result);
                    if (mList.size() > 0) {
                        mList.clear();
                    }
                    mList = JSON.parseArray(jsonObject.getString("data"), TestGroupBean.class);
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
                    mListMore = JSON.parseArray(data, TestGroupBean.class);
                    if (IsNonEmptyUtils.isList(mListMore)) {
                        mHandler.sendEmptyMessage(2);
                    } else {
                        mHandler.sendEmptyMessage(0);
                    }
                } else {
                    mHandler.sendEmptyMessage(0);
                }
            }
        }
    }
}
