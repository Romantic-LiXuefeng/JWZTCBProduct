package com.jwzt.caibian.activity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
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
import android.os.Message;
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
import com.jwzt.caibian.application.GlobalContants;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.application.BaseActivity;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.application.Configs;
import com.jwzt.caibian.bean.LoginBean;
import com.jwzt.caibian.bean.TestChildBean;
import com.jwzt.caibian.bean.TestGroupBean;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.UIUtils;
import com.jwzt.caibian.util.UserToast;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
/**
 * 稿件预览（已上传）
 * @author howie
 *
 */
public class PreviewWanCHengActivity extends BaseActivity implements OnClickListener {
	/**标题*/
	private TextView tv_titles,titleName,tv_time,tv_type,tv_location,tv_image_num,tv_video_num,tv_audio_num,tv_all,tv_status;
	private WebView tv_content;
	private View iv_back;
	//private ListView lv
	private View tv_edit;
	private AlertDialog alertDialog;
	private LinearLayout ll;
	private TestGroupBean mNewUploadinglistBean;
	private String id,operateType, pid,createUserId;
	
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
						findViewById(R.id.RL).setVisibility(View.GONE);

							tv_image_num.setVisibility(View.GONE);


							tv_video_num.setVisibility(View.GONE);

							tv_audio_num.setVisibility(View.GONE);

					}
				}
				dismisLoadingDialog();
				break;
			case 11:
				Toast.makeText(PreviewWanCHengActivity.this, "审核通过成功", 1).show();
				dismisLoadingDialog();
				PreviewWanCHengActivity.this.finish();
				break;
			case 12:
                Toast.makeText(PreviewWanCHengActivity.this, "审核驳回成功", 1).show();
				dismisLoadingDialog();
				PreviewWanCHengActivity.this.finish();
				break;
			case 10:
                Toast.makeText(PreviewWanCHengActivity.this, "稿件修改成功", 1).show();
				//dismisLoadingDialog();
				break;
			case 15:
				Toast.makeText(PreviewWanCHengActivity.this, "稿件修改失败", 1).show();
				//dismisLoadingDialog();
				break;
				case 16:
					Intent intent = new Intent();
					intent.putExtra("result", "");
					/*
					 * 调用setResult方法表示我将Intent对象返回给之前的那个Activity，这样就可以在onActivityResult方法中得到Intent对象，
					 */
					setResult(500, intent);
					finish();
					break;
			}
		};
	};
	private int i;
	private TextView tv_creater;
	private TextView tv_form;
	private TextView play_state;
	private LinearLayout ll_creater;
	private LinearLayout ll_time;
	private LinearLayout ll_from;
	private LinearLayout ll_type;
	private LinearLayout ll_play;
	private LinearLayout ll_state;
	private LinearLayout ll_location;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_previewshenhe_showdetail);
		application=(CbApplication) PreviewWanCHengActivity.this.getApplication();
		mLoginBean=application.getmLoginBean();
		options = new DisplayImageOptions.Builder()  
        .showImageOnLoading(R.drawable.replace) // 设置图片下载期间显示的图片  
        .showImageForEmptyUri(R.drawable.replace) // 设置图片Uri为空或是错误的时候显示的图片  
        .showImageOnFail(R.drawable.replace) // 设置图片加载或解码过程中发生错误显示的图片  
        .cacheInMemory(false) // 设置下载的图片是否缓存在内存中  
        .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中 
        .bitmapConfig(Config.RGB_565)
//        .displayer(new FadeInBitmapDisplayer(100))
        .build(); // 构建完成  
	    imageLoader = ImageLoader.getInstance();
	    
		id=getIntent().getStringExtra("id");
		pid=getIntent().getStringExtra("pid");
		operateType=getIntent().getStringExtra("operateType");
		createUserId=getIntent().getStringExtra("createUserId");
		mList=new ArrayList<TestChildBean>();
		findViews();
		initData();
	}
	
	/***数据请求*/
	private void initData(){
		if (CbApplication.getNetType() != -1) {
			showLoadingDialog(PreviewWanCHengActivity.this, "", "");
			String manuscriptDetailsUrl=String.format(Configs.gaojianxiangqing, id);
			System.out.println("manuscriptDetailsUrl:"+manuscriptDetailsUrl);
			RequestData(manuscriptDetailsUrl, Configs.manuscriptDetailsCode, -1);
		}else {
			UserToast.toSetToast(UIUtils.getContext(), getString(R.string.please_check_net));
		}
		
	}
	private String state;
	/**
	 * 控件实例化
	 */
	private void findViews() {
	String	name = getIntent().getStringExtra("name");
		state = getIntent().getStringExtra("state");
		tv_titles=(TextView) findViewById(R.id.tv_titles);
if(null==name){
	tv_titles.setText("审核");
}else{
	tv_titles.setText(name);
}

		titleName=(TextView) findViewById(R.id.titleName);
		//创建人
		tv_creater = findViewById(R.id.tv_creater);
		//来源
		tv_form = findViewById(R.id.tv_form);
		//播出状态
		play_state = findViewById(R.id.play_state);

		tv_time=(TextView) findViewById(R.id.tv_time);
		//tv_author_name=(TextView) findViewById(R.id.tv_author_name);
	//	tv_terminalType= (TextView) findViewById(R.id.tv_terminalType);
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
		iv_back=findViewById(R.id.ll_back);
		tv_edit=findViewById(R.id.tv_edit);
		tv_edit.setOnClickListener(this);
		//tv_edit.setVisibility(View.GONE);
		
		iv_back.setOnClickListener(this);
		pinglinline=(LinearLayout) findViewById(R.id.pinglinline);
		pinglinline.setOnClickListener(this);

		ll_control=(LinearLayout)findViewById(R.id.ll_control);
		LinearLayout	dianzanline=(LinearLayout) findViewById(R.id.dianzanline);


		View viewById = findViewById(R.id.ll_renling);
		ll_creater = findViewById(R.id.ll_creater);
		ll_time = findViewById(R.id.ll_time);
		ll_from = findViewById(R.id.ll_from);
		ll_type = findViewById(R.id.ll_type);
		ll_play = findViewById(R.id.ll_play);
		ll_play.setVisibility(View.GONE);
		findViewById(R.id.other4).setVisibility(View.VISIBLE);
		ll_state = findViewById(R.id.ll_state);
		ll_location = findViewById(R.id.ll_location);

		System.out.println("Configs.newsReadOnly==="+Configs.newsReadOnly);
		findViewById(R.id.tv_tuihui).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendMessage("任务退回");
			}
		});
		findViewById(R.id.tv_renling).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
		String type = getIntent().getStringExtra("Type");
		if(null!=state){
			System.out.println("state======"+state);
			if("1".equals(state)){
				//viewById.setVisibility(View.VISIBLE);
				tv_titles.setText("稿件详情");
				ll_control.setVisibility(View.VISIBLE);
			}else if("2".equals(state)){
				//	viewById.setVisibility(View.GONE);
				ll_control.setVisibility(View.GONE);
				tv_titles.setText("稿件详情");
			}
		}
		if(Configs.newsReadOnly){
			ll_control.setVisibility(View.GONE);
		}else{
			ll_control.setVisibility(View.VISIBLE);

			 if(operateType.equals("4")){
				tv_edit.setVisibility(View.GONE);
				ll_control.setVisibility(View.GONE);
			}
			System.out.println("operateType===="+operateType);
/**
 * else  if("1".equals(type)){

 ll_control.setVisibility(View.VISIBLE);
 }else{

 pinglinline.setVisibility(View.GONE);
 dianzanline.setVisibility(View.GONE);
 ll_control.setVisibility(View.GONE);
 }
 */
			System.out.println("firstChecks==="+Configs.firstChecks+"===secondChecks==="+Configs.secondChecks);
			if(operateType.equals("1")&&Configs.firstChecks){//如果是待审稿件，并且有一审权限
				tv_edit.setVisibility(View.VISIBLE);
				ll_control.setVisibility(View.VISIBLE);
			}else if(operateType.equals("2")&&Configs.secondChecks){//如果一审，并且有二审权限
				tv_edit.setVisibility(View.VISIBLE);
				ll_control.setVisibility(View.VISIBLE);
			}else {
				if(createUserId!=null){

//				if(createUserId.equals(mLoginBean.getUserID())){
//					tv_edit.setVisibility(View.GONE);
//					ll_control.setVisibility(View.GONE);
//				}else {
					tv_edit.setVisibility(View.GONE);
					ll_control.setVisibility(View.GONE);
			//	}

				}else{
					tv_edit.setVisibility(View.GONE);
					ll_control.setVisibility(View.GONE);
				}
			}


		}

/**
 * else if(operateType.equals("2")&&Configs.secondChecks){
 tv_edit.setVisibility(View.VISIBLE);
 ll_control.setVisibility(View.VISIBLE);
 }
 */
		
		dianzanline.setOnClickListener(this);


	}
	
	/**
	 * 控件适配
	 */
	private void initView(){
		String createUserId = mNewUploadinglistBean.getCreateUserId();
		if(null!=createUserId){
			if(createUserId.equals(mLoginBean.getUserID())){
				ll_control.setVisibility(View.GONE);
			}
		}


		titleName.setText(mNewUploadinglistBean.getTitle());
		String s = mNewUploadinglistBean.getCreateTime().replaceAll("2019-", "");
		String substring = s.substring(0, s.lastIndexOf(":"));
		if(null==substring||"".equals(substring)){
			ll_time.setVisibility(View.GONE);
			findViewById(R.id.other2).setVisibility(View.VISIBLE);
		}else {
			ll_time.setVisibility(View.VISIBLE);
			tv_time.setText(substring);
		}
		//tv_author_name.setText(mNewUploadinglistBean.getAuthor());
		if(IsNonEmptyUtils.isString(mNewUploadinglistBean.getTerminalType())){
			String terminalType=mNewUploadinglistBean.getTerminalType();
			if(terminalType.equals("1")){
				tv_type.setText("新媒体");
			}else if(terminalType.endsWith("2")){
				tv_type.setText("全媒体");
			}else if(terminalType.endsWith("3")){
				tv_type.setText("电视");
			}
		}else{
			ll_type.setVisibility(View.GONE);
findViewById(R.id.other3).setVisibility(View.VISIBLE);
			//tv_terminalType.setVisibility(View.GONE);
		}
		
		if(IsNonEmptyUtils.isString(mNewUploadinglistBean.getStatus())){
			String status=mNewUploadinglistBean.getStatus();

			if(status.equals("0")){
				tv_status.setText("待提交");
			}else if(status.equals("1")){
				tv_status.setText("待审核");
			}else if(status.equals("2")){
				tv_status.setText("一审  "+mNewUploadinglistBean.getCheckLevelUser());
			}else if(status.equals("3")){
				tv_status.setText("二审  "+mNewUploadinglistBean.getCheckLevelUser());
			}
		}else{
			ll_state.setVisibility(View.GONE);
			findViewById(R.id.other3).setVisibility(View.VISIBLE);
			tv_status.setVisibility(View.GONE);
		}


//		if(leibie.equals("39")){//39 新闻 41 生活 42 爆料 43 民生 44环境 45 交通 49公共栏目
//	    	tv_type.setText("新闻");
//	    }else if(leibie.equals("41")){
//	    	tv_type.setText("生活");
//	    }else if(leibie.equals("42")){
//	    	tv_type.setText("爆料");
//	    }else if(leibie.equals("43")){
//	    	tv_type.setText("民生");
//	    }else if(leibie.equals("44")){
//	    	tv_type.setText("环境");
//	    }else if(leibie.equals("45")){
//	    	tv_type.setText("交通");
//	    }else if(leibie.equals("49")){
//	    	tv_type.setText("公共栏目");
//	    }
		
	//	tv_type.setText(mNewUploadinglistBean.getKeword());
		if(null==mNewUploadinglistBean.getAddress()||"".equals(mNewUploadinglistBean.getAddress())){
			ll_location.setVisibility(View.GONE);
		}else{
			tv_location.setText(mNewUploadinglistBean.getAddress());
		}

		


		if(null==mNewUploadinglistBean.getSource()||"".equals(mNewUploadinglistBean.getSource())){
			ll_from.setVisibility(View.GONE);
			findViewById(R.id.other1).setVisibility(View.VISIBLE);
		}else {
			ll_from.setVisibility(View.VISIBLE);
			tv_form.setText(mNewUploadinglistBean.getSource());
		}
		if(null==mNewUploadinglistBean.getAuthor()||"".equals(mNewUploadinglistBean.getAuthor())){
			ll_creater.setVisibility(View.GONE);
			findViewById(R.id.other1).setVisibility(View.VISIBLE);
		}else {
			tv_creater.setText(mNewUploadinglistBean.getAuthor());
		}
	//	tv_content.setText(mNewUploadinglistBean.getContent());
	//	lv.setVisibility(View.GONE);
		//lv.setAdapter(new CheckStatusAdapter(PreviewUploadedActivity.this,operateType,mNewUploadinglistBean.getCreateTime()));
		
		//showTip();
		// 获取内容加载成html界面
		String url;
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
				+ "</body>"+"</html>";
		}else{
			//进行权限判断
			if(operateType.equals("1")&&Configs.firstChecks){//如果是待审稿件，并且有一审权限
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
						+ "</div>"+"</body>"+"</html>";
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
						+ "</div>"+"</body>"+"</html>";
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
							+ "</body>"+"</html>";
			}
            /**
             * else if(operateType.equals("3")&&Configs.secondChecks){
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
             + "</div>"+"</body>"+"</html>";
             }
             */

		}
		System.out.println("url:" + url);
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
		/*tv_content.fromHtml(url.replace("<p>", "").replace("</p>", ""));
		tv_content.clearFocus();*/
		
	}
	public final class InJavaScriptLocalObj {
		@JavascriptInterface
		public void showSource(String html) {
			System.out.println("====>html=" + html);
			shenhetijiao(html);

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
		for (int i = 0; i < mList.size(); i++) {
			View view=View.inflate(PreviewWanCHengActivity.this, R.layout.media_item_layout, null);
			ImageView iv=(ImageView) view.findViewById(R.id.iv);
			ImageView iv_play=(ImageView) view.findViewById(R.id.iv_play);
			View viewById = view.findViewById(R.id.ll_video_audio);
			String endString=mList.get(i).getFileRealPath();
			if(endString.endsWith("mp3")||endString.endsWith("wav")){
				iv_play.setVisibility(View.GONE);

				iv.setImageResource(R.drawable.audio_bg);
				viewById.setVisibility(View.VISIBLE);
				audioNum++;
			}else if(endString.endsWith("mp4")){
				iv_play.setVisibility(View.VISIBLE);
				viewById.setVisibility(View.VISIBLE);
				imageLoader.displayImage(mList.get(i).getPreviewUrl(), iv, options);
				videoNum++;
			}else if(endString.endsWith("png")||endString.endsWith("jpg")){
				iv_play.setVisibility(View.GONE);
				imageLoader.displayImage(mList.get(i).getFileRealPath(), iv, options);
				viewById.setVisibility(View.GONE);
				
				imgNum++;
			}else{
				iv_play.setVisibility(View.GONE);
				viewById.setVisibility(View.GONE);
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
						Intent videointent=new Intent(PreviewWanCHengActivity.this, ShowVideoActivity.class);
						videointent.putExtra("playpath", fileRealPath);
						startActivity(videointent);
					}else{
						Intent intent=new Intent(PreviewWanCHengActivity.this, ShowImageActivity.class);
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
		if(0==imgNum){
			tv_image_num.setVisibility(View.GONE);
		}
		if(0==videoNum){
			tv_video_num.setVisibility(View.GONE);
		}
		if(0==audioNum){
			tv_audio_num.setVisibility(View.GONE);
		}
		tv_image_num.setText("图片("+imgNum+")");
		tv_video_num.setText("视频("+videoNum+")");
		tv_audio_num.setText("音频("+audioNum+")");
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){ 
		case R.id.ll_back:
			finish();
			overridePendingTransition(R.anim.push_left_out,R.anim.push_right_out);
			break;
		case R.id.tv_edit://编辑按钮
//			Intent intent=new Intent(PreviewWanCHengActivity.this,NewScriptActivity.class);
//			//intent.putExtra("details", mNewUploadinglistBean);
//			intent.putExtra("tag", "details");
//			startActivity(intent);
//			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			tijiaobaocun();
			break;
		case R.id.iv_cancel://审核意见提示框中的取消按钮
			alertDialog.dismiss();
			break;
		case R.id.tv_all:
			if(mNewUploadinglistBean!=null){
				List<TestChildBean> list=mNewUploadinglistBean.getNewsFileList();
				if(IsNonEmptyUtils.isList(list)){
					Intent intent_all=new Intent(PreviewWanCHengActivity.this,AllFootageActivity.class);
					intent_all.putExtra("alls",  (Serializable)list);
					startActivity(intent_all);
					overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
				}else{
					UserToast.toSetToast(PreviewWanCHengActivity.this, "没有全部内容");
				}
			}
			
			break;
		case R.id.dianzanline://审核通过
			if("1".equals(state)){
				sendMessage("任务认领");
			}else{
				shenheData();
			}

			break;
		case R.id.pinglinline://审核驳回
			if("1".equals(state)){
				sendMessage("任务退回");
			}else {
				getPopupWindow_pinglun();
			}
			break;

		}
	}
	/**
	 * 弹出审核意见的提示框
	 */
	private void showTip(){
		if(IsNonEmptyUtils.isString(mNewUploadinglistBean.getStatus())){
			int status=new Integer(mNewUploadinglistBean.getStatus());
			if(status==2){//表示退稿
				alertDialog = new AlertDialog.Builder(this).create();
				alertDialog.show();
				View view = View.inflate(PreviewWanCHengActivity.this,R.layout.check_message_layout, null);
				TextView tv_person=(TextView) view.findViewById(R.id.tv_person);
				TextView tv_time=(TextView) view.findViewById(R.id.tv_time);
				TextView tv_content=(TextView) view.findViewById(R.id.tv_content);
				view.findViewById(R.id.iv_cancel).setOnClickListener(this);

				alertDialog.setContentView(view);
				alertDialog.show();
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
		Toast.makeText(PreviewWanCHengActivity.this, "审核失败", 1).show();
		dismisLoadingDialog();
	}
	@Override
	protected void initRequestOnCache(String result, int code, int biaoshi) {
//		initDataParse(result, code);
	}
	private void tijiaobaocun() {
		tv_content.loadUrl("javascript:window.java_obj.showSource("
				+ "document.getElementsByTagName('html')[0].innerHTML);");

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
			//{"data":"","message":"成功。","status":100}
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
			View view = LinearLayout.inflate(PreviewWanCHengActivity.this,
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
						Toast.makeText(PreviewWanCHengActivity.this, "请填写驳回理由", 1).show();
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
				showLoadingDialog(PreviewWanCHengActivity.this, "", "");
				//详情内容获取
				String detailsUrl = String.format(Configs.bohuixuanti, userid, id,IsNonEmptyUtils.encode(tv_contents.getText().toString()));
				@SuppressWarnings("unused")
				String cacheKey_xiao_hengtu= detailsUrl + "get";
				RequestNoDateCache(detailsUrl, 2000, 2000);
		}
		
		
		/**
		 * 请求网络获取留言列表中的数据
		 */
		private void shenheData(){
		    String userid=	mLoginBean.getUserID();
			showLoadingDialog(PreviewWanCHengActivity.this, "", "");
			//详情内容获取
			String detailsUrl = String.format(Configs.shenhexuanti, userid, id);
			System.out.println("detailsUrl===="+detailsUrl);
			@SuppressWarnings("unused")
			String cacheKey_xiao_hengtu= detailsUrl + "get";

			RequestNoDateCache(detailsUrl, 1000, 1000);
		}
		
		@SuppressWarnings("deprecation")
		private void shenhetijiao(String content){
			String userid=	mLoginBean.getUserID();
		   //showLoadingDialog(PreviewWanCHengActivity.this, "", "");
			//
			//详情内容获取
			String detailsUrl = String.format(Configs.xiugaiContent, userid, id);
			@SuppressWarnings("unused")
			String cacheKey_xiao_hengtu= detailsUrl + "get";
			
			
			HttpUtils	utils = new HttpUtils();
			RequestParams params = new RequestParams();
			params.addBodyParameter("userId", userid);
			params.addBodyParameter("newsId", id);
			try {
				params.addBodyParameter("content",URLEncoder.encode((content),"utf-8"));
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			@SuppressWarnings("unused")
			String ts=URLEncoder.encode((content));
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
		}
	private void sendMessage(final String content){

		String infoListUrl=String.format(Configs.huifuUrl,mLoginBean.getUserID(),pid,content);
        System.out.println("pid====="+pid);
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
					Toast.makeText(PreviewWanCHengActivity.this,"任务认领成功！",Toast.LENGTH_SHORT).show();
				}else if("任务退回".equals(content)){
					Toast.makeText(PreviewWanCHengActivity.this,"任务退回成功！",Toast.LENGTH_SHORT).show();
				}

				Message obtain = Message.obtain();
				obtain.what=16;
				mHandler.sendMessageDelayed(obtain,2000);
				//  finish();
			}

			@Override
			public void onStart() {

			}

			@Override
			public void onFailure(HttpException error, String msg) {
                System.out.println("请求失败====="+msg);
			}
		});

	}
}
