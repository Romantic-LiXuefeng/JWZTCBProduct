package com.jwzt.caibian.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jwzt.caibian.adapter.ResourceGVAdapter;
import com.jwzt.caibian.application.BaseActivity;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.application.Configs;
import com.jwzt.caibian.application.GlobalContants;
import com.jwzt.caibian.bean.LoginBean;
import com.jwzt.caibian.bean.TestChildBean;
import com.jwzt.caibian.bean.TestGroupBean;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.UIUtils;
import com.jwzt.caibian.util.UserToast;
import com.jwzt.cb.product.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.List;

/**
 * 稿件预览（已上传）
 *
 * @author howie
 */
public class XianSuoDetailsActivity extends BaseActivity implements OnClickListener {
    private TextView tv_titles, titleName,tv_edit,tv_location;
    private WebView tv_content;
    private ImageView iv_back;
    private GridView gv_resource;

    private TestGroupBean mNewUploadinglistBean;
    private String id;
    private List<TestChildBean> resourceList;
    private CbApplication application;
    private LoginBean mLoginBean;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    initView();
                    dismisLoadingDialog();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xiansuo_details);
        application = (CbApplication) XianSuoDetailsActivity.this.getApplication();
        mLoginBean = application.getmLoginBean();

        id = getIntent().getStringExtra("id");
        findViews();
        initData();
    }

    /*** 数据请求 */
    private void initData() {
        if (CbApplication.getNetType() != -1) {
            showLoadingDialog(XianSuoDetailsActivity.this, "", "");
            String manuscriptDetailsUrl = String.format(Configs.gaojianxiangqing, id);
            System.out.println("manuscriptDetailsUrl:" + manuscriptDetailsUrl);
            RequestData(manuscriptDetailsUrl, Configs.manuscriptDetailsCode, -1);
        } else {
            UserToast.toSetToast(UIUtils.getContext(),getString(R.string.please_check_net));
        }
    }

    /**
     * 控件实例化
     */
    private void findViews() {
        tv_titles = (TextView) findViewById(R.id.tv_titles);
        tv_titles.setText("线索详情");
        titleName = (TextView) findViewById(R.id.titleName);
        tv_location=findViewById(R.id.tv_location);
        tv_content = (WebView) findViewById(R.id.tv_content);
        tv_content.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });
        iv_back = findViewById(R.id.iv_back);
        tv_edit = findViewById(R.id.tv_edit);
        tv_edit.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        gv_resource = findViewById(R.id.gv_resource);
        gv_resource.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String fileRealPath = resourceList.get(position).getFileRealPath();
                if (fileRealPath.endsWith("mp4") || fileRealPath.endsWith("mp3")) {
                    Intent videointent = new Intent(XianSuoDetailsActivity.this, ShowVideoActivity.class);
                    videointent.putExtra("playpath", fileRealPath);
                    startActivity(videointent);
                } else {
                    Intent intent = new Intent(XianSuoDetailsActivity.this, ShowImageActivity.class);
                    intent.putExtra(GlobalContants.NEWSHOWIMAGE, fileRealPath);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * 控件适配
     */
    private void initView() {
        titleName.setText(mNewUploadinglistBean.getTitle());
        tv_location.setText(mNewUploadinglistBean.getSource());
        resourceList = mNewUploadinglistBean.getNewsFileList();
        if (IsNonEmptyUtils.isList(resourceList)) {
            gv_resource.setAdapter(new ResourceGVAdapter(XianSuoDetailsActivity.this, resourceList));
        }

        // 获取内容加载成html界面
        String url = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"  \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
                + "<html xmlns=\"http://www.w3.org/1999/xhtml\" >"
                + "<head>"
                + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"
                + "<title>"
                + ""
                + "</title>"
                + "</head>"
                + "<body>"
                + "<div id=\"editor\" contentEditable=\"true\">"
                + mNewUploadinglistBean.getContent()
                + "</div>"
                + "</body>"
                + "</html>";
        System.out.println("url:" + url);

        tv_content.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        tv_content.getSettings().setJavaScriptEnabled(true);
        tv_content.getSettings().setDefaultTextEncodingName("utf-8");
        tv_content.setBackgroundColor(0);
        tv_content.getSettings().setLoadWithOverviewMode(true);
        tv_content.getSettings().setBlockNetworkImage(false);
        tv_content.getSettings().setUseWideViewPort(false);// 设置是否可以缩放
        tv_content.getSettings().setDomStorageEnabled(true);// 有可能是DOM储存API没有打开
        // wb_content.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        // wb_content.loadDataWithBaseURL(null, url, "text/html", "UTF-8",null);
        tv_content.addJavascriptInterface(new InJavaScriptLocalObj(), "java_obj");
        tv_content.loadDataWithBaseURL(null, getNewContent(url), "text/html", "UTF-8", null);

    }

    public final class InJavaScriptLocalObj {
        @JavascriptInterface
        public void showSource(String html) {
            System.out.println("====>html=" + html);
            toUpxiugai(html);

        }

        @JavascriptInterface
        public void showDescription(String str) {
            System.out.println("====>html=" + str);
        }
    }

    @SuppressWarnings("deprecation")
    public void toUpxiugai(String content) {
        String userid = mLoginBean.getUserID();
        // showLoadingDialog(PreviewUploadedActivity.this, "", "");
        //
        // 详情内容获取
        String detailsUrl = String.format(Configs.xiugaiContent, userid, id);
        @SuppressWarnings("unused")
        String cacheKey_xiao_hengtu = detailsUrl + "get";

        HttpUtils utils = new HttpUtils();
        RequestParams params = new RequestParams();
        params.addBodyParameter("userId", userid);
        params.addBodyParameter("newsId", id);
        params.addBodyParameter("content", URLEncoder.encode(content));
        utils.send(HttpRequest.HttpMethod.POST, Configs.xiugaiContent, params,
                new RequestCallBack<String>() {
                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        System.out.println("responseInfo.result" + arg1);
                        mHandler.sendEmptyMessage(15);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> arg0) {
                        System.out.println("responseInfo.result" + arg0.result);
                        try {
                            org.json.JSONObject jsonObject = new org.json.JSONObject(
                                    arg0.result);
                            String message = jsonObject.getString("message");
                            String status = jsonObject.getString("status");
                            if (status.equals("100")) {
                                mHandler.sendEmptyMessage(10);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                });
    }

    private String getNewContent(String htmltext) {
        Document doc = Jsoup.parse(htmltext);
        Elements elements = doc.getElementsByTag("img");
        for (Element element : elements) {
            element.attr("style", "width:100%;height:auto;");
        }

        return doc.toString();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                overridePendingTransition(R.anim.push_left_out, R.anim.push_right_out);
                break;
            case R.id.tv_edit:// 编辑按钮
                Intent intent = new Intent(XianSuoDetailsActivity.this, XianSuoCreateAndEditActivity.class);
                intent.putExtra("tag", "details");
                intent.putExtra("id",id);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.tv_all:
                if (mNewUploadinglistBean != null) {
                    List<TestChildBean> list = mNewUploadinglistBean.getNewsFileList();
                    if (IsNonEmptyUtils.isList(list)) {
                        Intent intent_all = new Intent(XianSuoDetailsActivity.this, AllFootageActivity.class);
                        intent_all.putExtra("alls", (Serializable) list);
                        startActivity(intent_all);
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    } else {
                        UserToast.toSetToast(XianSuoDetailsActivity.this, "没有全部内容");
                    }
                }
                break;
        }
    }

    @Override
    protected void initRequestOnSuccess(String result, int code, int biaoshi) {
        initDataParse(result, code);
    }

    @Override
    protected void initRequestOnStart(String result, int code, int biaoshi) {
        System.out.println();
    }

    @Override
    protected void initRequestOnFailure(String failure, int code, int biaoshi) {
        System.out.println();
    }

    @Override
    protected void initRequestOnCache(String result, int code, int biaoshi) {
        initDataParse(result, code);
    }

    private void initDataParse(String result, int code) {
        if (code == Configs.manuscriptDetailsCode) {
            JSONObject jsonObject = JSON.parseObject(result);
            String status = jsonObject.getString("status");
            if (status.equals("100")) {// 表示成功
                String dataresult = jsonObject.getString("data");
                mNewUploadinglistBean = JSON.parseObject(dataresult, TestGroupBean.class);
                if (mNewUploadinglistBean != null) {
                    mHandler.sendEmptyMessage(1);
                } else {
                    mHandler.sendEmptyMessage(0);
                }
            }
        }
    }
}
