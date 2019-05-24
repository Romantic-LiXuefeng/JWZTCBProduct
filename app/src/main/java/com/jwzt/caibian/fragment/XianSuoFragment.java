package com.jwzt.caibian.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jwzt.caibian.activity.XianSuoDetailsActivity;
import com.jwzt.caibian.adapter.XiansuoAdapter;
import com.jwzt.caibian.application.BaseFragment;
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
 * 待审状态列表
 *
 * @author howie
 */
public class XianSuoFragment extends BaseFragment {
    private CbApplication app;
    private LoginBean loginBean;
    private MyListView listview;
    private int pageTag;
    private List<TestGroupBean> mList;
    private List<TestGroupBean> mListMore;
    private XiansuoAdapter messageAdapter;

    private PullToRefreshLayout prl_refersh;
    private View view;
    private Handler mHandler = new Handler() {
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    DialogHelp.dismisLoadingDialog();
                    break;
                case 1:
                    messageAdapter = new XiansuoAdapter(getActivity(), mList);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = View.inflate(getActivity(), R.layout.xiansuo_layout, null);
        app = (CbApplication) getActivity().getApplication();
        loginBean = app.getmLoginBean();
        mList = new ArrayList<TestGroupBean>();
        mListMore = new ArrayList<TestGroupBean>();


        findView();
        //获取未审核数据
        initdata();

        return view;
    }

    private void findView() {
        listview = (MyListView) view.findViewById(R.id.mlv);
        prl_refersh = (PullToRefreshLayout) view.findViewById(R.id.prl_refersh);
        prl_refersh.setOnRefreshListener(new MyListener());
        listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), XianSuoDetailsActivity.class);
                intent.putExtra("id", mList.get(position).getId());
                String status = mList.get(position).getStatus();
                intent.putExtra("operateType", mList.get(position).getStatus());
                getActivity().startActivity(intent);
            }
        });
    }

    private void initdata() {
        //userId=%s&type=%s&startId=%s";
        pageTag = 0;
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
                    DialogHelp.showLoadingDialog(getActivity(), "", "");
                    String infoListUrl = String.format(Configs.daishenheListUrl, loginBean.getUserID(), "1", mList.get(mList.size() - 1).getId());
                    ;

                    System.out.println("manuscriptUrl:" + infoListUrl);
                    RequestData(infoListUrl, Configs.infoListCode, pageTag);
                } else {
                    UserToast.toSetToast(UIUtils.getContext(), getString(R.string.please_check_net));
                }
            }
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
