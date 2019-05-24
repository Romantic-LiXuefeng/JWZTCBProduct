package com.jwzt.caibian.activity;

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
/**
 * 串联单待审
 * @author howie
 *
 */
public class CLDetailShenheUploadedActivity extends BaseActivity implements OnClickListener {
	/**标题*/
	private TextView tv_titles,titleName,tv_time,tv_author_name,tv_type,tv_location,tv_image_num,tv_video_num,tv_audio_num,tv_all,tv_terminalType,tv_status;
	private WebView tv_content;
	private View iv_back;
	//private ListView lv
	private View tv_edit;
	private AlertDialog alertDialog;
	private LinearLayout ll;
	private TestGroupBean mNewUploadinglistBean;
	private String id,operateType;
	
	private int imgNum,videoNum,audioNum;
	private List<TestChildBean> mList;
	private ImageLoader imageLoader;
    private DisplayImageOptions options;
	private EditText	tv_contents;
	private LinearLayout pinglinline,ll_control;
    private PopupWindow popupWindow_pinglun;
    private CbApplication application;
    private LoginBean  mLoginBean;
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				if(mNewUploadinglistBean!=null){
					initView();
					mList=mNewUploadinglistBean.getNewsFileList();
					if(IsNonEmptyUtils.isList(mList)){
						addViews();
					}else{
					//	if(mList.size()==0){
							findViewById(R.id.rl_view2).setVisibility(View.GONE);
					//	}
					}
				}
				dismisLoadingDialog();
				break;
			case 11:
				Toast.makeText(CLDetailShenheUploadedActivity.this, "审核通过成功", 1).show();
				dismisLoadingDialog();
				CLDetailShenheUploadedActivity.this.finish();
				break;
			case 12:
                Toast.makeText(CLDetailShenheUploadedActivity.this, "审核驳回成功", 1).show();
				dismisLoadingDialog();
				CLDetailShenheUploadedActivity.this.finish();
				break;
			case 10:
                Toast.makeText(CLDetailShenheUploadedActivity.this, "稿件修改成功", 1).show();
				//dismisLoadingDialog();
				break;
			case 15:
				Toast.makeText(CLDetailShenheUploadedActivity.this, "稿件修改失败", 1).show();
				//dismisLoadingDialog();
				break;
			
			}
		};
	};
	private int i;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editweb_showdetail);
		application=(CbApplication) CLDetailShenheUploadedActivity.this.getApplication();
		mLoginBean=application.getmLoginBean();
		options = new DisplayImageOptions.Builder()  
          .showImageOnLoading(R.drawable.zanwu_shenhe) // 设置图片下载期间显示的图片
          .showImageForEmptyUri(R.drawable.zanwu_shenhe) // 设置图片Uri为空或是错误的时候显示的图片
          .showImageOnFail(R.drawable.zanwu_shenhe) // 设置图片加载或解码过程中发生错误显示的图片
          .cacheInMemory(false) // 设置下载的图片是否缓存在内存中  
          .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中 
          .bitmapConfig(Config.RGB_565)
//        .displayer(new FadeInBitmapDisplayer(100))
          .build(); // 构建完成  
	    imageLoader = ImageLoader.getInstance();
	    
		id=getIntent().getStringExtra("id");
		operateType=getIntent().getStringExtra("operateType");
		mList=new ArrayList<TestChildBean>();
		findViews();
		initData();
	}
	
	/***数据请求*/
	private void initData(){
		if (CbApplication.getNetType() != -1) {
			showLoadingDialog(CLDetailShenheUploadedActivity.this, "", "");
			String manuscriptDetailsUrl=String.format(Configs.gaojianxiangqing, id);
			System.out.println("manuscriptDetailsUrl:"+manuscriptDetailsUrl);
			RequestData(manuscriptDetailsUrl, Configs.manuscriptDetailsCode, -1);
		}else {
			UserToast.toSetToast(UIUtils.getContext(), getString(R.string.please_check_net));
		}
		
	}
	
	/**
	 * 控件实例化
	 */
	private void findViews() {
		tv_titles=(TextView) findViewById(R.id.tv_titles);
		tv_titles.setText("稿件");
		titleName=(TextView) findViewById(R.id.titleName);
		tv_time=(TextView) findViewById(R.id.tv_time);
		tv_author_name=(TextView) findViewById(R.id.tv_author_name);
		tv_terminalType= (TextView) findViewById(R.id.tv_terminalType);
		tv_status= (TextView) findViewById(R.id.tv_status);
		tv_type=(TextView) findViewById(R.id.tv_type);
		tv_location=(TextView) findViewById(R.id.tv_location);
		tv_content=(WebView) findViewById(R.id.tv_content);
		tv_content.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
				return true;
			}
		});
		tv_image_num=(TextView) findViewById(R.id.tv_image_num);
		tv_video_num=(TextView) findViewById(R.id.tv_video_num);
		tv_audio_num=(TextView) findViewById(R.id.tv_audio_num);
		tv_all=(TextView) findViewById(R.id.tv_all);
		tv_all.setOnClickListener(this);
		tv_all.setVisibility(View.GONE);
		ll=(LinearLayout) findViewById(R.id.ll);
		//lv=(ListView) findViewById(R.id.mlv);
		iv_back=findViewById(R.id.iv_back);
		tv_edit=findViewById(R.id.tv_edit);
		tv_edit.setOnClickListener(this);
		tv_edit.setVisibility(View.GONE);
		
		iv_back.setOnClickListener(this);
		pinglinline=(LinearLayout) findViewById(R.id.pinglinline);
		pinglinline.setOnClickListener(this);
		pinglinline.setVisibility(View.GONE);
		ll_control=(LinearLayout)findViewById(R.id.ll_control);
		if(Configs.newsReadOnly){
			ll_control.setVisibility(View.GONE);
		}else{
			ll_control.setVisibility(View.VISIBLE);
		}
		LinearLayout	dianzanline=(LinearLayout) findViewById(R.id.dianzanline);
		if(operateType.equals("4")){
			dianzanline.setVisibility(View.GONE);
		}
		
		if(operateType.equals("2")&&Configs.firstChecks){//如果是一审稿件，并且有一审权限
			dianzanline.setVisibility(View.VISIBLE);
		}else if(operateType.equals("3")&&Configs.secondChecks){
			dianzanline.setVisibility(View.VISIBLE);
		}else if(operateType.equals("2")&&Configs.secondChecks){
			dianzanline.setVisibility(View.VISIBLE);
		}else{
			dianzanline.setVisibility(View.GONE);
		}
		//dianzanline.setVisibility(View.VISIBLE);
		dianzanline.setOnClickListener(this);
		/*lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
				// TODO Auto-generated method stub
				showTip();
			}
		});*/
	}
	
	/**
	 * 控件适配
	 */
	private void initView(){
		titleName.setText(mNewUploadinglistBean.getTitle());
		tv_time.setText(mNewUploadinglistBean.getCreateTime().replaceAll("2017-", ""));
		tv_author_name.setText(mNewUploadinglistBean.getAuthor());
		if(IsNonEmptyUtils.isString(mNewUploadinglistBean.getTerminalType())){
			String terminalType=mNewUploadinglistBean.getTerminalType();
			if(terminalType.equals("1")){
				tv_terminalType.setText("新媒体");
			}else if(terminalType.endsWith("2")){
				tv_terminalType.setText("全媒体");
			}else if(terminalType.endsWith("3")){
				tv_terminalType.setText("电视");
			}
		}else{
			tv_terminalType.setVisibility(View.GONE);
		}
		
		if(IsNonEmptyUtils.isString(mNewUploadinglistBean.getStatus())){
			String status=mNewUploadinglistBean.getStatus();
			if(status.equals("0")){
				tv_status.setText("待提交");
			}else if(status.equals("1")){
				tv_status.setText("已提交");
			}else if(status.equals("2")){
				tv_status.setText("一审");
			}else if(status.equals("3")){
				tv_status.setText("二审");
			}
		}else{
			tv_status.setVisibility(View.GONE);
		}
		tv_type.setText(mNewUploadinglistBean.getKeword());
		tv_location.setText(mNewUploadinglistBean.getSource());
	//	tv_content.setText(mNewUploadinglistBean.getContent());
	//	lv.setVisibility(View.GONE);
		//lv.setAdapter(new CheckStatusAdapter(PreviewUploadedActivity.this,operateType,mNewUploadinglistBean.getCreateTime()));
		
		showTip();
		String url;
		// 获取内容加载成html界面
		if(operateType.equals("4")){//标示退回稿件
			 url = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"  \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
					+ "<html xmlns=\"http://www.w3.org/1999/xhtml\" >"
					+ "<head>"
					+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"
					+ "<title>"
					+ ""
					+ "</title>"
					+ "</head>"
					+ "<body>"
					+ mNewUploadinglistBean.getContent()
					+ "</body>" + "</html>";
			}else{
				//进行权限判断
				if(operateType.equals("2")&&Configs.firstChecks){//如果是一审稿件，并且有一审权限
					url = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"  \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
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
							+ "</body>" + "</html>";		
				}else if(operateType.equals("3")&&Configs.secondChecks){
					url = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"  \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
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
							+ "</body>" + "</html>";	
				}else if(operateType.equals("2")&&Configs.secondChecks){
					url = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"  \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
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
							+ "</body>" + "</html>";	
				}else{
					 url = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"  \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
								+ "<html xmlns=\"http://www.w3.org/1999/xhtml\" >"
								+ "<head>"
								+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"
								+ "<title>"
								+ ""
								+ "</title>"
								+ "</head>"
								+ "<body>"
								+ mNewUploadinglistBean.getContent()
								+ "</body>" + "</html>";
				}
				
			}
		tv_content.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN); 
		tv_content.getSettings().setJavaScriptEnabled(true);
		tv_content.getSettings().setDefaultTextEncodingName("utf-8");
		tv_content.setBackgroundColor(0);
		tv_content.getSettings().setLoadWithOverviewMode(true); 
		tv_content.getSettings().setBlockNetworkImage(false);
		tv_content.getSettings().setUseWideViewPort(false);// 设置是否可以缩放
		tv_content.getSettings().setDomStorageEnabled(true);//有可能是DOM储存API没有打开
	//	wb_content.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//		wb_content.loadDataWithBaseURL(null, url, "text/html", "UTF-8", null);
		tv_content.addJavascriptInterface(new InJavaScriptLocalObj(),
				"java_obj");
		tv_content.loadDataWithBaseURL(null, getNewContent(url), "text/html", "UTF-8", null);
		
		/*tv_content.fromHtml(url);
		tv_content.clearFocus();*/
		//tv_content.setSelection(tv_content.getEditableText().length());
		
	}
	private void tijiaobaocun() {
		tv_content.loadUrl("javascript:window.java_obj.showSource("
				+ "document.getElementsByTagName('html')[0].innerHTML);");

	}
	public final class InJavaScriptLocalObj {
		@JavascriptInterface
		public void showSource(String html) {
			System.out.println("====>html=" + html);
			shenheData(html);

		}

		@JavascriptInterface
		public void showDescription(String str) {
			System.out.println("====>html=" + str);
		}
	}
	
	private String getNewContent(String htmltext){  
	    Document doc=Jsoup.parse(htmltext);  
	    Elements elements=doc.getElementsByTag("img");  
	    for (Element element : elements) {  
            element.attr("style", "width:100%;height:auto;");
	    }  
	         
	    return doc.toString();  
	} 
	/**
	 * 动态添加数据
	 */
	private void addViews(){
		System.out.println("mList.size()===="+mList.size());
	if(mList.size()==0){
		findViewById(R.id.rl_view2).setVisibility(View.GONE);
	}

		for (int i = 0; i < mList.size(); i++) {
			View view=View.inflate(CLDetailShenheUploadedActivity.this, R.layout.media_item_layout, null);
			ImageView iv=(ImageView) view.findViewById(R.id.iv);
			ImageView iv_play=(ImageView) view.findViewById(R.id.iv_play);
			String endString=mList.get(i).getFileRealPath();
			if(endString.endsWith("mp3")||endString.endsWith("wav")){
				iv_play.setVisibility(View.GONE);
				iv.setImageResource(R.drawable.audio_bg);
				audioNum++;
			}else if(endString.endsWith("mp4")){
				iv_play.setVisibility(View.VISIBLE);
				imageLoader.displayImage(mList.get(i).getPreviewUrl(), iv, options);
				videoNum++;
			}else if(endString.endsWith("png")||endString.endsWith("jpg")){
				iv_play.setVisibility(View.GONE);
				imageLoader.displayImage(mList.get(i).getFileRealPath(), iv, options);
				
				
				imgNum++;
			}else{
				iv_play.setVisibility(View.GONE);
				imageLoader.displayImage(mList.get(i).getPreviewUrl(), iv, options);
				imgNum++;
			}
			
			
			iv.setId(i);
			iv.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//UIUtils.showToast(mList.get(mi).getPreviewUrl());
//					Intent intent = new Intent(PreviewUploadedActivity.this,ShowImageActivity.class);
//					String path=mList.get(v.getId()).getFileRealPath();
//					if(path.endsWith("jpg")||path.endsWith("png")){
//						intent.putExtra(GlobalContants.NEWSHOWIMAGE, mList.get(v.getId()).getFileRealPath());
//					}else{
//						intent.putExtra(GlobalContants.NEWSHOWIMAGE, mList.get(v.getId()).getPreviewUrl());
//					}
//					startActivity(intent);
					String fileRealPath=mList.get(v.getId()).getFileRealPath();
					if(fileRealPath.endsWith("mp4")||fileRealPath.endsWith("mp3")){
						Intent videointent=new Intent(CLDetailShenheUploadedActivity.this, ShowVideoActivity.class);
						videointent.putExtra("playpath", fileRealPath);
						startActivity(videointent);
					}else{
						Intent intent=new Intent(CLDetailShenheUploadedActivity.this, ShowImageActivity.class);
//						if(IsNonEmptyUtils.isString(preViewImg)){
//							intent.putExtra(GlobalContants.NEWSHOWIMAGE, preViewImg);
//						}else if(IsNonEmptyUtils.isString(fileRealPath)){
							intent.putExtra(GlobalContants.NEWSHOWIMAGE, fileRealPath);
//						}
						startActivity(intent);
					}
				}
			});
			ll.addView(view);
		}
		
		tv_image_num.setText("图片("+imgNum+")");
		tv_video_num.setText("视频("+videoNum+")");
		tv_audio_num.setText("音频("+audioNum+")");
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){ 
		case R.id.iv_back:
			finish();
			overridePendingTransition(R.anim.push_left_out,R.anim.push_right_out);
			break;
		case R.id.tv_edit://编辑按钮
			Intent intent=new Intent(CLDetailShenheUploadedActivity.this,NewScriptActivity.class);
			//intent.putExtra("details", mNewUploadinglistBean);
			intent.putExtra("tag", "details");
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			break;
		case R.id.iv_cancel://审核意见提示框中的取消按钮
			alertDialog.dismiss();
			break;
		case R.id.tv_all:
			if(mNewUploadinglistBean!=null){
				List<TestChildBean> list=mNewUploadinglistBean.getNewsFileList();
				if(IsNonEmptyUtils.isList(list)){
					Intent intent_all=new Intent(CLDetailShenheUploadedActivity.this,AllFootageActivity.class);
					intent_all.putExtra("alls",  (Serializable)list);
					startActivity(intent_all);
					overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
				}else{
					UserToast.toSetToast(CLDetailShenheUploadedActivity.this, "没有全部内容");
				}
			}
			
			break;
		case R.id.dianzanline://审核通过
			tijiaobaocun();;
			break;
		case R.id.pinglinline://审核驳回
			getPopupWindow_pinglun();
			break;
		}
	}
	/**
	 * 弹出审核意见的提示框
	 */
	private void showTip(){
		/*if(IsNonEmptyUtils.isString(mNewUploadinglistBean.getStatus())){
			int status=new Integer(mNewUploadinglistBean.getStatus());
			if(status==2){//表示退稿
				alertDialog = new AlertDialog.Builder(this).create();
				alertDialog.show();
				View view = View.inflate(CLDShenheUploadedActivity.this,R.layout.check_message_layout, null);
				TextView tv_person=(TextView) view.findViewById(R.id.tv_person);
				TextView tv_time=(TextView) view.findViewById(R.id.tv_time);
				TextView tv_content=(TextView) view.findViewById(R.id.tv_content);
				view.findViewById(R.id.iv_cancel).setOnClickListener(this);
				//tv_person.setText("审核人:"+mNewUploadinglistBean.getVerifyUserName());
				//tv_time.setText("审核日期:"+mNewUploadinglistBean.getVerifyTime());
//				try {
//					tv_content.setText("审核意见:"+URLDecoder.decode(mNewUploadinglistBean.getReason(), "utf-8"));
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				}
				//tv_content.setText("审核意见:"+mNewUploadinglistBean.getReason());
				alertDialog.setContentView(view);
				alertDialog.show();
			}
		}*/
	}
	@Override
	protected void initRequestOnSuccess(String result, int code, int biaoshi) {
		// TODO Auto-generated method stub
		initDataParse(result, code);
	}
	@Override
	protected void initRequestOnStart(String result, int code, int biaoshi) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void initRequestOnFailure(String failure, int code, int biaoshi) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void initRequestOnCache(String result, int code, int biaoshi) {
		// TODO Auto-generated method stub
//		initDataParse(result, code);
	}
	
	private void initDataParse(String result, int code){
		if(code==Configs.manuscriptDetailsCode){
			JSONObject jsonObject=JSON.parseObject(result);
			String status=jsonObject.getString("status");
			if(status.equals("100")){//表示成功
				String dataresult=jsonObject.getString("data");
				mNewUploadinglistBean = JSON.parseObject(dataresult,TestGroupBean.class);
				if(mNewUploadinglistBean!=null){
					mHandler.sendEmptyMessage(1);
				}else{
					mHandler.sendEmptyMessage(0);
				}
			}
		}else if(code==1000){
			// {"data":"","message":"成功。","status":100}
			 JSONObject json=JSONObject.parseObject(result);
			 String status=json.getString("status");
			 if(status.equals("100")){
				 mHandler.sendEmptyMessage(11); 
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
	
	
	public void getPopupWindow_pinglun() {
		if (null != popupWindow_pinglun) {
			if (popupWindow_pinglun.isShowing()) {
				popupWindow_pinglun.dismiss();
			} else {
				popupWindow_pinglun.showAsDropDown(pinglinline, 0, 10);
			}
			return;
		} else {
			showWindow();
		}
	}
	 /**
	    * 显示popuwindow
	    */
		private void showWindow() {
			// TODO Auto-generated method stub
			WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			int h = wm.getDefaultDisplay().getHeight() / 4;// 屏幕高度
			int w = wm.getDefaultDisplay().getWidth();// 屏幕宽度
			View view = LinearLayout.inflate(CLDetailShenheUploadedActivity.this,
					R.layout.live_comment, null);
			tv_contents = (EditText) view.findViewById(R.id.et_content);
			// 处理点击事件
			ImageButton imageButton_quxiao = (ImageButton) view
					.findViewById(R.id.pinglun_quxiao);
			imageButton_quxiao.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					tv_contents.setText("");
					popupWindow_pinglun.dismiss();
				}
			});
			// 确定事件
			ImageButton imageButton_queding = (ImageButton) view
					.findViewById(R.id.pinglun_tijiao);
			imageButton_queding.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {

					if ("".equals(tv_contents.getText().toString())) {
						Toast.makeText(CLDetailShenheUploadedActivity.this, "请填写驳回理由", 1).show();
						return;
					}
					CommitData();
					tv_contents.setText("");
					popupWindow_pinglun.dismiss();
				}

				
			});
			popupWindow_pinglun = new PopupWindow(view, w, h, false);
			popupWindow_pinglun.setFocusable(true);
			popupWindow_pinglun.setBackgroundDrawable(new BitmapDrawable());
			popupWindow_pinglun
					.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
			// popupWindow_pinglun.showAsDropDown(ll_comment, 0, 10);
			// 在底部显示
			popupWindow_pinglun.showAtLocation(pinglinline, Gravity.BOTTOM, 0, 0);
		}
		
		private void CommitData() {
			// TODO Auto-generated method stub
			      String userid=	mLoginBean.getUserID();
				showLoadingDialog(CLDetailShenheUploadedActivity.this, "", "");
				//详情内容获取
				String detailsUrl = String.format(Configs.chuanliadnabohuixuanti, userid, id,tv_contents.getText().toString());
				@SuppressWarnings("unused")
				String cacheKey_xiao_hengtu= detailsUrl + "get";
				RequestNoDateCache(detailsUrl, 2000, 2000);
		}
		
		
		/**
		 * 请求网络获取留言列表中的数据
		 */
		private void shenheData(String html){
		    String userid=	mLoginBean.getUserID();
			//showLoadingDialog(CLDetailShenheUploadedActivity.this, "", "");
			//
			//详情内容获取
			String detailsUrl = String.format(Configs.xiugaiContent, userid, id);
			@SuppressWarnings("unused")
			String cacheKey_xiao_hengtu= detailsUrl + "get";
			
			HttpUtils		utils = new HttpUtils();
			RequestParams params = new RequestParams();
			params.addBodyParameter("userId", userid);
			params.addBodyParameter("newsId", id);
			params.addBodyParameter("content",URLEncoder.encode(html));
			utils.send(HttpRequest.HttpMethod.POST, Configs.xiugaiContent, params, new RequestCallBack<String>() {

				@Override
				public void onFailure(HttpException arg0, String arg1) {
					System.out.println("responseInfo.result"+arg1);
					mHandler.sendEmptyMessage(15);
				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					System.out.println("responseInfo.result"+arg0.result);
					try {
						org.json.JSONObject  jsonObject=new org.json.JSONObject(arg0.result);
						String message=jsonObject.getString("message");
						String status=jsonObject.getString("status");
						if(status.equals("100")){
							mHandler.sendEmptyMessage(10);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				
			});
			
			//RequestNoDateCache(detailsUrl, 1000, 1000);
		}
}
