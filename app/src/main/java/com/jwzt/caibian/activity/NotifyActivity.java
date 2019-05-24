package com.jwzt.caibian.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jwzt.caibian.application.BaseActivity;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.application.Configs;
import com.jwzt.caibian.bean.LoginBean;
import com.jwzt.caibian.bean.MessageAllBean;
import com.jwzt.caibian.bean.NotivityBean;
import com.jwzt.caibian.util.BitmapUtils;
import com.jwzt.caibian.util.IsNonEmptyUtils;
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

import java.net.URLEncoder;
import java.util.regex.Pattern;

public class NotifyActivity extends BaseActivity {

    private TextView tv_title;
    private TextView tv_time;
    private TextView tv_jieshou;
    private TextView tv_send;
    private WebView tv_content;
    private CbApplication application;
    private LoginBean mLoginBean;
    private TextView tv_tuihui;
    private TextView tv_renling;
    private MessageAllBean messageAllBean;
private Handler mHandler=new Handler(){
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what){
            case 0:
                Intent intent = new Intent();
                intent.putExtra("result", "");
                /*
                 * 调用setResult方法表示我将Intent对象返回给之前的那个Activity，这样就可以在onActivityResult方法中得到Intent对象，
                 */
                setResult(500, intent);
                finish();
                break;
            case 2:
                String obj = (String) msg.obj;
                Gson gson = new Gson();
                NotivityBean notivityBean = gson.fromJson(obj, NotivityBean.class);
                if(null!=notivityBean){
                    NotivityBean.DataBean data = notivityBean.getData();
                    if(null!=data){
                        tv_title.setText(data.getTitle());
                        tv_time.setText(data.getCreateTime());
                        tv_send.setText(data.getSenderName());
                        tv_jieshou.setText(data.getRecipientName());
                        intData(data.getContent());
                    }
                }
                break;
        }
    }
};
    private String type;

    @Override
    protected void initRequestOnSuccess(String result, int code, int biaoshi) {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);
        application = (CbApplication) this
                .getApplication();
        mLoginBean = application.getmLoginBean();
        Intent intent = getIntent();
        messageAllBean = (MessageAllBean) intent.getSerializableExtra("messageAllBean");
        type = intent.getStringExtra("type");
        initView();

    }

    private void intData(String content) {

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
                +content
                + "</div>"
                + "</body>"
                + "</html>";
        System.out.println("url:" + url);

        tv_content.getSettings().setLayoutAlgorithm(
                WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        tv_content.getSettings().setJavaScriptEnabled(true);
        tv_content.getSettings().setDefaultTextEncodingName("utf-8");
        tv_content.setBackgroundColor(0);
        tv_content.getSettings().setLoadWithOverviewMode(true);
        tv_content.getSettings().setBlockNetworkImage(false);
        tv_content.getSettings().setUseWideViewPort(false);// 设置是否可以缩放
        tv_content.getSettings().setDomStorageEnabled(true);// 有可能是DOM储存API没有打开
        // wb_content.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        // wb_content.loadDataWithBaseURL(null, url, "text/html", "UTF-8",
        // null);
        tv_content.addJavascriptInterface(new InJavaScriptLocalObj(),
                "java_obj");

        tv_content.loadDataWithBaseURL(null, getNewContent(url), "text/html",
                "UTF-8", null);

//        tv_tuihui.setOnClickListener(new View.OnClickListener() {//退回
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//       //
//        tv_renling.setOnClickListener(new View.OnClickListener() {//认领
//            @Override
//            public void onClick(View v) {
//
//            }
//        });



    }
    private String getNewContent(String htmltext) {
        Document doc = Jsoup.parse(htmltext);
        Elements elements = doc.getElementsByTag("img");
        for (Element element : elements) {
            element.attr("style", "width:100%;height:auto;");
        }

        return doc.toString();
    }
    public final class InJavaScriptLocalObj {
        @JavascriptInterface
        public void showSource(String html) {
            System.out.println("====>html=" + html);
            CommitData();

        }

        @JavascriptInterface
        public void showDescription(String str) {
            System.out.println("====>html=" + str);
        }
    }
    private void CommitData() {
        // TODO Auto-generated method stub
        String userid=	mLoginBean.getUserID();
        showLoadingDialog(NotifyActivity.this, "", "");
        //详情内容获取
        String detailsUrl = String.format(Configs.bohuixuanti, userid, messageAllBean.getParentMessageId(), IsNonEmptyUtils.encode(messageAllBean.getContent().toString()));
        @SuppressWarnings("unused")
        String cacheKey_xiao_hengtu= detailsUrl + "get";
        RequestNoDateCache(detailsUrl, 2000, 2000);
    }



//    @SuppressWarnings("deprecation")
//    public void toUpxiugai(String content) {
//        String userid = mLoginBean.getUserID();
//        // showLoadingDialog(PreviewUploadedActivity.this, "", "");
//        //
//        // 详情内容获取
//        String detailsUrl = String.format(Configs.xiugaiContent, userid, id);
//        @SuppressWarnings("unused")
//        String cacheKey_xiao_hengtu = detailsUrl + "get";
//
//        HttpUtils utils = new HttpUtils();
//        RequestParams params = new RequestParams();
//        params.addBodyParameter("userId", userid);
//        params.addBodyParameter("newsId", id);
//        params.addBodyParameter("content", URLEncoder.encode(content));
//        utils.send(HttpRequest.HttpMethod.POST, Configs.xiugaiContent, params,
//                new RequestCallBack<String>() {
//                    @Override
//                    public void onFailure(HttpException arg0, String arg1) {
//                        System.out.println("responseInfo.result" + arg1);
//                        mHandler.sendEmptyMessage(15);
//                    }
//
//                    @Override
//                    public void onSuccess(ResponseInfo<String> arg0) {
//                        System.out.println("responseInfo.result" + arg0.result);
//                        try {
//                            org.json.JSONObject jsonObject = new org.json.JSONObject(
//                                    arg0.result);
//                            String message = jsonObject.getString("message");
//                            String status = jsonObject.getString("status");
//                            if (status.equals("100")) {
//                                mHandler.sendEmptyMessage(10);
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                });
//    }


    private void initView() {
        TextView tv_titles = findViewById(R.id.tv_titles);
        tv_titles.setText("通知详情");
        ImageView iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //标题
        tv_title = findViewById(R.id.tv_title);

        tv_time = findViewById(R.id.tv_time);

        tv_jieshou = findViewById(R.id.tv_jieshou);

        tv_send = findViewById(R.id.tv_send);
        getNotivity();
        tv_content = (WebView) findViewById(R.id.tv_content);
        LinearLayout ll = findViewById(R.id.ll);
        tv_tuihui = findViewById(R.id.tv_tuihui);
        tv_renling = findViewById(R.id.tv_renling);
        Intent intent = getIntent();
        String state = intent.getStringExtra("state");
        if("1".equals(state)){
            ll.setVisibility(View.VISIBLE);
        }else if("2".equals(state)){
            ll.setVisibility(View.GONE);
        }else{
            ll.setVisibility(View.GONE);
        }
        ImageView iv = findViewById(R.id.iv);
        if(null!=type) {
            switch (Integer.parseInt(type)) {
                case 5:
                 iv.setImageResource(R.drawable.zhi);
                    break;
                case 6:
                    iv.setImageResource(R.drawable.xin);
                    break;
                case 7:
                    iv.setImageResource(R.drawable.bao);
                    break;
                case 8:
                    iv.setImageResource(R.drawable.yin);
                    break;
                case 12:
                    iv.setImageResource(R.drawable.chuan2);
                    break;
            }
        }
        tv_tuihui.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        sendMessage("任务退回");
    }
});
        tv_renling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("任务认领");
            }
        });
    }
private void  getNotivity(){
    String infoListUrl=String.format(Configs.message,messageAllBean.getId());
    RequestParams params = new RequestParams();
    HttpUtils http = new HttpUtils();
    http.configCurrentHttpCacheExpiry(1000); //设置超时时间   10s
    http.send(HttpRequest.HttpMethod.GET, infoListUrl ,params,new RequestCallBack<String>(){
        @Override
        public void onLoading(long total, long current, boolean isUploading) {

        }

        @Override
        public void onSuccess(ResponseInfo<String> responseInfo) {

            String result = responseInfo.result;
            Message obtain = Message.obtain();
            obtain.what=2;
            obtain.obj=result;
            mHandler.sendMessage(obtain);
            //  finish();
        }

        @Override
        public void onStart() {

        }

        @Override
        public void onFailure(HttpException error, String msg) {

        }
    });
}
    private void sendMessage(final String content){

        String infoListUrl=String.format(Configs.huifuUrl,mLoginBean.getUserID(),messageAllBean.getId(),content);
            RequestParams params = new RequestParams();
            HttpUtils http = new HttpUtils();
            http.configCurrentHttpCacheExpiry(1000); //设置超时时间   10s
            http.send(HttpRequest.HttpMethod.GET, infoListUrl ,params,new RequestCallBack<String>(){
                @Override
                public void onLoading(long total, long current, boolean isUploading) {

                }

                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    if("任务认领".equals(content)){
                        Toast.makeText(NotifyActivity.this,"任务认领成功！",Toast.LENGTH_SHORT).show();
                    }else if("任务退回".equals(content)){
                        Toast.makeText(NotifyActivity.this,"任务退回成功！",Toast.LENGTH_SHORT).show();
                    }

                    Message obtain = Message.obtain();
                    obtain.what=0;
                    mHandler.sendMessageDelayed(obtain,2000);
                    //  finish();
                }

                @Override
                public void onStart() {

                }

                @Override
                public void onFailure(HttpException error, String msg) {

                }
            });

    }
}
