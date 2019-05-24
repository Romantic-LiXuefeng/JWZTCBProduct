package com.jwzt.caibian.activity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jwzt.caibian.application.GlobalContants;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.activity.CLDetailShenheUploadedActivity.InJavaScriptLocalObj;
import com.jwzt.caibian.adapter.CheckStatusAdapter;
import com.jwzt.caibian.application.BaseActivity;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.application.Configs;
import com.jwzt.caibian.bean.LoginBean;
import com.jwzt.caibian.bean.NewUploadinglistBean;
import com.jwzt.caibian.bean.ResourcesBean;
import com.jwzt.caibian.bean.TestChildBean;
import com.jwzt.caibian.bean.TestGroupBean;
import com.jwzt.caibian.bean.UploadingDetailsBean;
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

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 稿件预览（已上传）
 * 
 * @author howie
 *
 */
public class PreviewGaojianxiugaiActivity extends BaseActivity implements
		OnClickListener {
	/** 标题 */
	private TextView tv_titles, titleName, tv_time, tv_author_name, tv_type,
			tv_location, tv_image_num, tv_video_num, tv_audio_num, tv_all;
	private WebView tv_content;
	private View iv_back;
	// private ListView lv
	private View tv_edit;
	private AlertDialog alertDialog;
	private LinearLayout ll;
	private TestGroupBean mNewUploadinglistBean;
	private String id, operateType;

	private int imgNum, videoNum, audioNum;
	private List<TestChildBean> mList;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private EditText tv_contents;
	private LinearLayout pinglinline;
	private PopupWindow popupWindow_pinglun;
	private CbApplication application;
	private LoginBean mLoginBean;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				if (mNewUploadinglistBean != null) {
					initView();
					mList = mNewUploadinglistBean.getNewsFileList();
					if (IsNonEmptyUtils.isList(mList)) {
						addViews();
					}
					else{
                        ll.setVisibility(View.GONE);
                        rl_va.setVisibility(View.GONE);
                    }
				}
				dismisLoadingDialog();
				break;
			case 11:
				Toast.makeText(PreviewGaojianxiugaiActivity.this, "审核通过成功", 1)
						.show();
				dismisLoadingDialog();
				PreviewGaojianxiugaiActivity.this.finish();
				break;
			case 12:
				Toast.makeText(PreviewGaojianxiugaiActivity.this, "审核驳回成功", 1)
						.show();
				dismisLoadingDialog();
				PreviewGaojianxiugaiActivity.this.finish();
				break;
			case 10:
				Toast.makeText(PreviewGaojianxiugaiActivity.this, "稿件修改成功", 1)
						.show();
				break;
			case 15:
				Toast.makeText(PreviewGaojianxiugaiActivity.this, "稿件修改失败", 1)
						.show();
				// dismisLoadingDialog();
				break;
			case 25:
				Toast.makeText(PreviewGaojianxiugaiActivity.this, "稿件提交失败", 1).show();
				break;
			case 26:
				Toast.makeText(PreviewGaojianxiugaiActivity.this, "稿件提交成功", 1).show();
				PreviewGaojianxiugaiActivity.this.finish();

			   break;
			}
		};
	};
	private int i;
	private RelativeLayout rl_va;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_previewtuihi_showdetail);
		application = (CbApplication) PreviewGaojianxiugaiActivity.this
				.getApplication();
		mLoginBean = application.getmLoginBean();
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.replace) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.replace) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.replace) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(false) // 设置下载的图片是否缓存在内存中
				.cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
				.bitmapConfig(Config.RGB_565)
				// .displayer(new FadeInBitmapDisplayer(100))
				.build(); // 构建完成
		imageLoader = ImageLoader.getInstance();

		id = getIntent().getStringExtra("id");
		operateType = getIntent().getStringExtra("operateType");
		mList = new ArrayList<TestChildBean>();
		findViews();
		initData();
	}

	/*** 数据请求 */
	private void initData() {
		if (CbApplication.getNetType() != -1) {
			showLoadingDialog(PreviewGaojianxiugaiActivity.this, "", "");
			String manuscriptDetailsUrl = String.format(
					Configs.gaojianxiangqing, id);
			System.out.println("manuscriptDetailsUrl:" + manuscriptDetailsUrl);
			RequestData(manuscriptDetailsUrl, Configs.manuscriptDetailsCode, -1);
		} else {
			UserToast.toSetToast(UIUtils.getContext(),
					getString(R.string.please_check_net));
		}

	}

	/**
	 * 控件实例化
	 */
	private void findViews() {
		tv_titles = (TextView) findViewById(R.id.tv_titles);
		tv_titles.setText("稿件");
		titleName = (TextView) findViewById(R.id.titleName);
		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_author_name = (TextView) findViewById(R.id.tv_author_name);
		tv_type = (TextView) findViewById(R.id.tv_type);
		TextView tv_zanNum = (TextView) findViewById(R.id.tv_zanNum);
		tv_zanNum.setText("提交保存");
		tv_location = (TextView) findViewById(R.id.tv_location);
		tv_content = (WebView) findViewById(R.id.tv_content);
		tv_image_num = (TextView) findViewById(R.id.tv_image_num);
		tv_video_num = (TextView) findViewById(R.id.tv_video_num);
		tv_audio_num = (TextView) findViewById(R.id.tv_audio_num);
		rl_va = findViewById(R.id.rl_va);
		tv_all = (TextView) findViewById(R.id.tv_all);
		tv_all.setOnClickListener(this);
		tv_all.setVisibility(View.GONE);
		ll = (LinearLayout) findViewById(R.id.ll);
		// lv=(ListView) findViewById(R.id.mlv);
		iv_back = findViewById(R.id.iv_back);
		tv_edit = findViewById(R.id.tv_edit);
		tv_edit.setOnClickListener(this);
		tv_edit.setVisibility(View.GONE);

		iv_back.setOnClickListener(this);
		pinglinline = (LinearLayout) findViewById(R.id.pinglinline);
		pinglinline.setOnClickListener(this);
		LinearLayout dianzanline = (LinearLayout) findViewById(R.id.dianzanline);

		dianzanline.setOnClickListener(this);

	}

	/**
	 * 控件适配
	 */
	private void initView() {
		titleName.setText(mNewUploadinglistBean.getTitle());
		tv_time.setText(mNewUploadinglistBean.getCreateTime().replaceAll(
				"2019-", ""));
		tv_author_name.setText(mNewUploadinglistBean.getAuthor());
		String leibie = mNewUploadinglistBean.getCategoryId();
		if (leibie.equals("39")) {// 39 新闻 41 生活 42 爆料 43 民生 44环境 45 交通 49公共栏目
			tv_type.setText("新闻");
		} else if (leibie.equals("41")) {
			tv_type.setText("生活");
		} else if (leibie.equals("42")) {
			tv_type.setText("爆料");
		} else if (leibie.equals("43")) {
			tv_type.setText("民生");
		} else if (leibie.equals("44")) {
			tv_type.setText("环境");
		} else if (leibie.equals("45")) {
			tv_type.setText("交通");
		} else if (leibie.equals("49")) {
			tv_type.setText("公共栏目");
		}

		// tv_type.setText(mNewUploadinglistBean.getKeword());
		tv_location.setText(mNewUploadinglistBean.getSource());
		// tv_content.setText(mNewUploadinglistBean.getContent());
		// lv.setVisibility(View.GONE);
		// lv.setAdapter(new
		// CheckStatusAdapter(PreviewUploadedActivity.this,operateType,mNewUploadinglistBean.getCreateTime()));

		showTip();
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
		tv_content.getSettings().setLayoutAlgorithm(
				LayoutAlgorithm.SINGLE_COLUMN);
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
		/*
		 * tv_content.fromHtml(url); tv_content.clearFocus();
		 */

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

	private String getNewContent(String htmltext) {
		Document doc = Jsoup.parse(htmltext);
		Elements elements = doc.getElementsByTag("img");
		for (Element element : elements) {
			element.attr("style", "width:100%;height:auto;");
		}
		return doc.toString();
	}

	/**
	 * 动态添加数据
	 */
	private void addViews() {
		if(mList.size()==0){
			ll.setVisibility(View.GONE);
			rl_va.setVisibility(View.GONE);

		}
		for (int i = 0; i < mList.size(); i++) {
			View view = View.inflate(PreviewGaojianxiugaiActivity.this,
					R.layout.media_item_layout, null);
			ImageView iv = (ImageView) view.findViewById(R.id.iv);
			ImageView iv_play = (ImageView) view.findViewById(R.id.iv_play);
			String endString = mList.get(i).getFileRealPath();
			if (endString.endsWith("mp3") || endString.endsWith("wav")) {
				iv_play.setVisibility(View.GONE);
				iv.setImageResource(R.drawable.audio_bg);
				audioNum++;
			} else if (endString.endsWith("mp4")) {
				iv_play.setVisibility(View.VISIBLE);
				imageLoader.displayImage(mList.get(i).getPreviewUrl(), iv,
						options);
				videoNum++;
			} else if (endString.endsWith("png") || endString.endsWith("jpg")) {
				iv_play.setVisibility(View.GONE);
				imageLoader.displayImage(mList.get(i).getFileRealPath(), iv,
						options);

				imgNum++;
			} else {
				iv_play.setVisibility(View.GONE);
				imageLoader.displayImage(mList.get(i).getPreviewUrl(), iv,
						options);
				imgNum++;
			}

			iv.setId(i);
			iv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// UIUtils.showToast(mList.get(mi).getPreviewUrl());
					// Intent intent = new
					// Intent(PreviewUploadedActivity.this,ShowImageActivity.class);
					// String path=mList.get(v.getId()).getFileRealPath();
					// if(path.endsWith("jpg")||path.endsWith("png")){
					// intent.putExtra(GlobalContants.NEWSHOWIMAGE,
					// mList.get(v.getId()).getFileRealPath());
					// }else{
					// intent.putExtra(GlobalContants.NEWSHOWIMAGE,
					// mList.get(v.getId()).getPreviewUrl());
					// }
					// startActivity(intent);
					String fileRealPath = mList.get(v.getId())
							.getFileRealPath();
					if (fileRealPath.endsWith("mp4")
							|| fileRealPath.endsWith("mp3")) {
						Intent videointent = new Intent(
								PreviewGaojianxiugaiActivity.this,
								ShowVideoActivity.class);
						videointent.putExtra("playpath", fileRealPath);
						startActivity(videointent);
					} else {
						Intent intent = new Intent(
								PreviewGaojianxiugaiActivity.this,
								ShowImageActivity.class);
						// if(IsNonEmptyUtils.isString(preViewImg)){
						// intent.putExtra(GlobalContants.NEWSHOWIMAGE,
						// preViewImg);
						// }else if(IsNonEmptyUtils.isString(fileRealPath)){
						intent.putExtra(GlobalContants.NEWSHOWIMAGE,
								fileRealPath);
						// }
						startActivity(intent);
					}
				}
			});
			ll.addView(view);
		}

		tv_image_num.setText("图片(" + imgNum + ")");
		tv_video_num.setText("视频(" + videoNum + ")");
		tv_audio_num.setText("音频(" + audioNum + ")");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			overridePendingTransition(R.anim.push_left_out,
					R.anim.push_right_out);
			break;
		case R.id.tv_edit:// 编辑按钮
			Intent intent = new Intent(PreviewGaojianxiugaiActivity.this,
					NewScriptActivity.class);
			// intent.putExtra("details", mNewUploadinglistBean);
			intent.putExtra("tag", "details");
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.iv_cancel:// 审核意见提示框中的取消按钮
			alertDialog.dismiss();
			break;
		case R.id.tv_all:
			if (mNewUploadinglistBean != null) {
				List<TestChildBean> list = mNewUploadinglistBean
						.getNewsFileList();
				if (IsNonEmptyUtils.isList(list)) {
					Intent intent_all = new Intent(
							PreviewGaojianxiugaiActivity.this,
							AllFootageActivity.class);
					intent_all.putExtra("alls", (Serializable) list);
					startActivity(intent_all);
					overridePendingTransition(R.anim.push_left_in,
							R.anim.push_left_out);
				} else {
					UserToast.toSetToast(PreviewGaojianxiugaiActivity.this,
							"没有全部内容");
				}
			}

			break;
		case R.id.dianzanline:// 提交修改
			tijiaobaocun();
			break;
		case R.id.pinglinline:// 提交审核
			tijiaoshenhe();
			// getPopupWindow_pinglun();
			break;
		}
	}

	/**
	 * 弹出审核意见的提示框
	 */
	private void showTip() {
		if (IsNonEmptyUtils.isString(mNewUploadinglistBean.getStatus())) {
			int status = new Integer(mNewUploadinglistBean.getStatus());
			if (status == 4) {// 表示退稿
				alertDialog = new AlertDialog.Builder(this).create();
				alertDialog.show();
				View view = View.inflate(PreviewGaojianxiugaiActivity.this,
						R.layout.check_message_layout, null);
				TextView tv_person = (TextView) view
						.findViewById(R.id.tv_person);
				TextView tv_time = (TextView) view.findViewById(R.id.tv_time);
				TextView tv_content = (TextView) view
						.findViewById(R.id.tv_content);
				view.findViewById(R.id.iv_cancel).setOnClickListener(this);
				tv_person.setVisibility(View.GONE);
				tv_time.setVisibility(View.GONE);
				tv_content.setText(mNewUploadinglistBean.getProp1());

				// tv_person.setText("审核人:"+mNewUploadinglistBean.getVerifyUserName());
				// tv_time.setText("审核日期:"+mNewUploadinglistBean.getVerifyTime());
				// try {
				// tv_content.setText("审核意见:"+URLDecoder.decode(mNewUploadinglistBean.getReason(),
				// "utf-8"));
				// } catch (UnsupportedEncodingException e) {
				// e.printStackTrace();
				// }
				// tv_content.setText("审核意见:"+mNewUploadinglistBean.getReason());
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

	}

	@Override
	protected void initRequestOnCache(String result, int code, int biaoshi) {
		// initDataParse(result, code);
	}

	private void initDataParse(String result, int code) {
		if (code == Configs.manuscriptDetailsCode) {
			JSONObject jsonObject = JSON.parseObject(result);
			String status = jsonObject.getString("status");
			if (status.equals("100")) {// 表示成功
				String dataresult = jsonObject.getString("data");
				mNewUploadinglistBean = JSON.parseObject(dataresult,
						TestGroupBean.class);
				if (mNewUploadinglistBean != null) {
					mHandler.sendEmptyMessage(1);
				} else {
					mHandler.sendEmptyMessage(0);
				}
			}
		} else if (code == 1000) {
			// {"data":"","message":"成功。","status":100}
			JSONObject json = JSONObject.parseObject(result);
			String status = json.getString("status");
			if (status.equals("100")) {
				mHandler.sendEmptyMessage(11);
			}
			// {"data":"","message":"成功。","status":100}
		} else if (code == 2000) {
			JSONObject json = JSONObject.parseObject(result);
			String status = json.getString("status");
			if (status.equals("100")) {
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
		View view = LinearLayout.inflate(PreviewGaojianxiugaiActivity.this,
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
					Toast.makeText(PreviewGaojianxiugaiActivity.this,
							"请填写驳回理由", 1).show();
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
		String userid = mLoginBean.getUserID();
		showLoadingDialog(PreviewGaojianxiugaiActivity.this, "", "");
		// 详情内容获取
		String detailsUrl = String.format(Configs.bohuixuanti, userid, id,
				IsNonEmptyUtils.encode(tv_contents.getText().toString()));
		@SuppressWarnings("unused")
		String cacheKey_xiao_hengtu = detailsUrl + "get";
		RequestNoDateCache(detailsUrl, 2000, 2000);
	}

	/**
	 * 请求网络获取留言列表中的数据 提交审核
	 */
	private void shenheData(String html) {
		String userid = mLoginBean.getUserID();
		// showLoadingDialog(PreviewGaojianxiugaiActivity.this, "", "");
		//
		// 详情内容获取
		String detailsUrl = String.format(Configs.xiugaiContent, userid, id);
		@SuppressWarnings("unused")
		String cacheKey_xiao_hengtu = detailsUrl + "get";

		HttpUtils utils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("userId", userid);
		params.addBodyParameter("newsId", id);
		params.addBodyParameter("content", URLEncoder.encode(html));
		
		utils.send(HttpRequest.HttpMethod.POST, Configs.xiugaiContent, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						System.out.println("responseInfo.result" + arg1);
						// dismisLoadingDialog();
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

	private void tijiaoshenhe() {
		 String userid=	mLoginBean.getUserID();
		HttpUtils utils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("newsId", id);
		String url=Configs.tijiaoshenhe+id+"&userId="+userid;
		utils.send(HttpRequest.HttpMethod.GET, url ,params,new RequestCallBack<String>(){
			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				

			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				System.out.println("responseInfo.result" + responseInfo.result);
				try {
					org.json.JSONObject jsonObject = new org.json.JSONObject(
							responseInfo.result);
					String message = jsonObject.getString("message");
					String status = jsonObject.getString("status");
					if (status.equals("100")) {
						mHandler.sendEmptyMessage(26);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onStart() {
				
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				mHandler.sendEmptyMessage(25);

			}
		});
		
	}
}
