package com.jwzt.caibian.activity;


import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;





















import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.TextSize;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jwzt.caibian.util.TimeUtil;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.application.BaseActivity;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.application.Configs;
import com.jwzt.caibian.bean.InfoListBean;
import com.jwzt.caibian.bean.LoginBean;
import com.jwzt.caibian.bean.MessageAllBean;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.Parser;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zc.RecordDemo.MyAudioRecorder;
/**
 * 详情页展示界面
 * @author howie
 *
 */
public class ShowMessageDeatilsActivity extends BaseActivity implements OnClickListener{
	private boolean isText;
	/**麦克风的按钮*/
	private ImageView riv_input;
	private boolean hasAnimation;// “发送”是否播放出现的动画
	/**最外层的根布局*/
	private View root;
	/**头部标题*/
	private TextView tv_titlename;
	private static final int TAKE_PICTURE = 0x000001;
	private static final int TAKE_PICSELECT = 0x000002;
	public static final int PHOTOHRAPH = 100;// 拍照
	private String IMG_SAVE_NAME;
	/**返回按钮*/
	private ImageView iv_back;
	/**活动的id*/
	private String activityID;
//	/***留言的总条数*/
//	private String totolenum;
	private String silenced;
	private String closeCommunity;
	private String needCheck;

	private WebView wb_content;
	/***列表适配*/
	
	
	private RelativeLayout rl_zanwu;
	
	
	private String id;
	private String nodeId,newsId;
	
	private static DisplayImageOptions options;  
	private static ImageLoader imageLoader;
	private TextView tv_newsTitle,tv_source,tv_time,tv_zanNum,tv_fasongzhe;
    private CbApplication application;
    private LoginBean mLoginBean;
    private EditText tv_content;
    private MessageAllBean  messageban;
    private WebView web_huifu;
	@SuppressLint("HandlerLeak")
	private Handler mHandler=new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				initView();
				break;
		
			case 9://数据适配
				initView();
				if(rl_zanwu!=null){
					rl_zanwu.setVisibility(View.GONE);
				}
				
				wb_content.setVisibility(View.VISIBLE);
				dismisLoadingDialog();
				break;
			case 10://表示无数据失败
				if(rl_zanwu!=null){
					rl_zanwu.setVisibility(View.VISIBLE);
				}
				
				wb_content.setVisibility(View.GONE);
				dismisLoadingDialog();
				break;
			case 11:
				Toast.makeText(ShowMessageDeatilsActivity.this, "审核通过成功", 1).show();
				
				dismisLoadingDialog();
				ShowMessageDeatilsActivity.this.finish();
				break;
			case 12:
                Toast.makeText(ShowMessageDeatilsActivity.this, "审核驳回成功", 1).show();
				
				dismisLoadingDialog();
				ShowMessageDeatilsActivity.this.finish();
				break;
			}
		};
	};
	private String type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shownemessagedetails_layout);   
		application=(CbApplication) ShowMessageDeatilsActivity.this.getApplication();
		mLoginBean=application.getmLoginBean();
		options = new DisplayImageOptions.Builder()  
        .showImageOnLoading(R.drawable.replace) // 设置图片下载期间显示的图片  
        .showImageForEmptyUri(R.drawable.replace) // 设置图片Uri为空或是错误的时候显示的图片  
        .showImageOnFail(R.drawable.replace) // 设置图片加载或解码过程中发生错误显示的图片  
        .cacheInMemory(true) // 设置下载的图片是否缓存在内存中  
        .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中  
        .build(); // 构建完成
		type = getIntent().getStringExtra("type");
         imageLoader = ImageLoader.getInstance();
		 findView();
		 newsId=getIntent().getStringExtra("ids");

		initData();
		 
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	public void initData(){
		String infoListUrl=String.format(Configs.xiaoxidetail,newsId);
		System.out.println("infoListUrl:"+infoListUrl);
		RequestData(infoListUrl, Configs.infoListCode, 0);
	}
	
	
	private void findView(){
		//头部标题
		tv_titlename=(TextView) findViewById(R.id.tv_name);
		TextView tv_edit=(TextView) findViewById(R.id.tv_edit);
		tv_edit.setVisibility(View.INVISIBLE);
		tv_titlename.setText("消息详情");
		wb_content=(WebView) findViewById(R.id.webView1);
	
		web_huifu=(WebView) findViewById(R.id.webView2);
		
		
		root=findViewById(R.id.root);
		
		//返回按钮
		iv_back=(ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		//暂无
		rl_zanwu=(RelativeLayout) findViewById(R.id.rl_zanwu);
		
		//点赞数
		tv_zanNum=(TextView) findViewById(R.id.tv_zanNum);
		
		tv_newsTitle=(TextView)findViewById(R.id.tv_newsTitle);
		tv_source=(TextView)findViewById(R.id.tv_source);
		tv_time=(TextView)findViewById(R.id.tv_time);

	    
	    
	    
	    tv_fasongzhe=(TextView)findViewById(R.id.tv_fasongzhe);
		if("0".equals(type)){
			findViewById(R.id.ll_me).setVisibility(View.GONE);
			findViewById(R.id.view_).setVisibility(View.GONE);
			findViewById(R.id.webView2).setVisibility(View.GONE);
		}
	}
	
	
	

	
	
	/**
	 * 初始化列表
	 */
	@SuppressLint("SetJavaScriptEnabled")
	private void initView(){
		tv_newsTitle.setText(messageban.getTitle());
		tv_time.setText(TimeUtil.getDateDiff1(messageban.getCreateTime()));
		String text = messageban.getContent();
		tv_source.setText("接收者："+messageban.getAuthor());
		tv_fasongzhe.setText("发送者："+messageban.getSenderName());
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
				+ text
				+ "</body>" + "</html>";
		System.out.println("url:" + url);
		wb_content.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN); 
		wb_content.getSettings().setJavaScriptEnabled(true);
		wb_content.getSettings().setDefaultTextEncodingName("utf-8");
		wb_content.setBackgroundColor(0);
		wb_content.getSettings().setLoadWithOverviewMode(true); 
		wb_content.getSettings().setBlockNetworkImage(false);
		wb_content.getSettings().setUseWideViewPort(false);// 设置是否可以缩放
		wb_content.getSettings().setDomStorageEnabled(true);//有可能是DOM储存API没有打开
	//	wb_content.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//		wb_content.loadDataWithBaseURL(null, url, "text/html", "UTF-8", null);
		wb_content.loadDataWithBaseURL(null, getNewContent(url), "text/html", "UTF-8", null);
		// wb_content.loadDataWithBaseURL(null,url, "text/html", "UTF-8", null);
		
		
		//回复内容 web_huifu
		// 获取内容加载成html界面
		List<MessageAllBean> list = messageban.getList();
		String context_huiuf="";
		if(list!=null) {
			 context_huiuf =list.get(0).getContent();
		}else{

		}
				String usls = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"  \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
						+ "<html xmlns=\"http://www.w3.org/1999/xhtml\" >"
						+ "<head>"
						+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"
						+ "<title>"
						+ ""
						+ "</title>"
						+ "</head>"
						+ "<body>"
						+ context_huiuf
						+ "</body>" + "</html>";
				System.out.println("url:" + url);
				web_huifu.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN); 
				web_huifu.getSettings().setJavaScriptEnabled(true);
				web_huifu.getSettings().setDefaultTextEncodingName("utf-8");
				web_huifu.setBackgroundColor(0);
				web_huifu.getSettings().setLoadWithOverviewMode(true); 
				web_huifu.getSettings().setBlockNetworkImage(false);
				web_huifu.getSettings().setUseWideViewPort(false);// 设置是否可以缩放
				web_huifu.getSettings().setDomStorageEnabled(true);//有可能是DOM储存API没有打开
			//	wb_content.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//				wb_content.loadDataWithBaseURL(null, url, "text/html", "UTF-8", null);
				web_huifu.loadDataWithBaseURL(null, getNewContent(usls), "text/html", "UTF-8", null);
		
	}
	
	private String getNewContent(String htmltext){  
	    Document doc=Jsoup.parse(htmltext);  
	    Elements elements=doc.getElementsByTag("img");  
	    for (Element element : elements) {  
            element.attr("style", "width:100%;height:auto;");
	    }  
	         
	    return doc.toString();  
	} 
	
	@SuppressLint("UseValueOf")
	@Override
	public void onClick(View v) {
		//TODO 各个按钮点击事件
		switch(v.getId()){
		
		case R.id.iv_back:
			finish();
		break;
	
		}
	}
	@Override
	protected void initRequestOnSuccess(String result, int code, int biaoshi) {
		System.out.println("result"+result);

		if(code== Configs.infoListCode){
			// {"data":"","message":"成功。","status":100}
			JSONObject jsonObject=JSON.parseObject(result);
			String data=jsonObject.getString("data");
			if(!data.equals("")){//表示获取成功
				//JSONObject jsonObject2=JSONObject.parseObject(data);
				//String list=jsonObject2.getString("list");
				//mList=JSON.parseArray(data,MessageAllBean.class);
				messageban=Parser.parserDataBean(result);
				if(messageban!=null){
					mHandler.sendEmptyMessage(1);
				}else{
					mHandler.sendEmptyMessage(0);
				}
			}else{
				mHandler.sendEmptyMessage(0);	
			}
			// {"data":"","message":"成功。","status":100}
		}else if(code==2000){
			 JSONObject json=JSONObject.parseObject(result);
			 String status=json.getString("status");
			 if(status.equals("100")){
				 mHandler.sendEmptyMessage(12); 
			 }
		}
		
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
	
	
}
