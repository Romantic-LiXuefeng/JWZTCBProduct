package com.jwzt.caibian.application;

import java.io.File;
import java.util.Map;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.android.apptools.OrmLiteBaseListActivity;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.db.DatabaseHelper;
import com.jwzt.caibian.util.SharePreferenceUtils;
import com.jwzt.caibian.widget.CustomProgressDialog;
import com.j256.ormlite.android.apptools.OrmLiteBaseListActivity;
import com.xdroid.request.XRequest;
import com.xdroid.request.base.Request;
import com.xdroid.request.config.DataType;
import com.xdroid.request.config.RequestCacheConfig;
import com.xdroid.request.config.TimeController;
import com.xdroid.request.ex.RequestParams;
import com.xdroid.request.impl.OnRequestListenerAdapter;
import com.xdroid.request.interfaces.OnRequestListener;
import com.xdroid.request.network.HttpError;
import com.xdroid.request.network.HttpException;
import com.xdroid.request.response.NetworkResponse;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

/**
 * 基类的Activity
 * @author awq
 * 修改继承的基类为
 * 
 */
public abstract class BaseActivity  extends OrmLiteBaseActivity<DatabaseHelper> {
	public SharePreferenceUtils mSharePreferenceUtils;
	private CustomProgressDialog progressDialog;
	//数据请求成功
	protected abstract void initRequestOnSuccess(String result,int code,int biaoshi);
	//数据请求开始
	protected abstract void initRequestOnStart(String result,int code,int biaoshi);
	//数据请求失败
	protected abstract void initRequestOnFailure(String failure,int code,int biaoshi);
	//加载缓存数据陈功
	protected abstract void initRequestOnCache(String result,int code,int biaoshi);
    private XRequest xrequest;
    /**
	 * 请求的tag，可根据此tag取消请求
	 */
	private Object mRequestTag = this;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		xrequest=XRequest.getInstance();
		xrequest.setRequestThreadPoolSize(10);
		if (mSharePreferenceUtils == null) {
			mSharePreferenceUtils = new SharePreferenceUtils();
		}
	}
	
	protected  void RequestData(String url,final int code,final int biaoshi) {
		//与GET请求的URL一样，为了避免同样的缓存key、这里重新指定缓存key
		RequestParams params = new RequestParams();
		String cacheKey = url + "post";  
		XRequest.getInstance().sendPost(mRequestTag, url, cacheKey, params, new OnRequestListenerAdapter<String>() {
			/**
			 * 数据请求失败
			 */
			@Override
			public void onRequestFailed(Request<?> request, HttpException httpException) {
				super.onRequestFailed(request, httpException);
				switch (httpException.getHttpErrorCode()) {
				case HttpError.ERROR_NOT_NETWORK:
					initRequestOnFailure("网络未连接，请检查", code, biaoshi);
					break;
				}
			}
            /**
             * 数据请求重试
             */
			@Override
			public void onRequestRetry(Request<?> request, int currentRetryCount, HttpException previousError) {
				Toast.makeText(BaseActivity.this, "获取信息失败，系统已经为您重试" + currentRetryCount+"次", Toast.LENGTH_SHORT).show();
			}
			
			/**
			 * 缓存加载完成
			 */
			@Override
			public void onCacheDataLoadFinish(Request<?> request,Map<String, String> headers, String result) {
				super.onCacheDataLoadFinish(request, headers, result);
				System.out.println("缓存记载完成============");
				initRequestOnCache(result, code, biaoshi);
			}
			/**
			 * 下载进度回调
			 */
			@Override
			public void onRequestDownloadProgress(Request<?> request, long transferredBytesSize, long totalSize) {
			}
			/**
			 * 上传进度回调
			 */
			@Override
			public void onRequestUploadProgress(Request<?> request, long transferredBytesSize, long totalSize, int currentFileIndex,
					File currentFile) {
			}
			@Override
			public void onDone(Request<?> request, Map<String, String> headers, String result, DataType dataType) {
				super.onDone(request, headers, result, dataType);
			}
			/**
			 *数据请求结束
			 */
			@Override
			public void onRequestFinish(Request<?> request,Map<String, String> headers, String result) {
				super.onRequestFinish(request, headers, result);
				
			}
			/**
			 * 网络数据加载完成
			 */
			@Override
			public boolean onParseNetworkResponse(Request<?> request,NetworkResponse networkResponse, String result) {
				super.onParseNetworkResponse(request, networkResponse, result);
				initRequestOnSuccess(result, code, biaoshi);
				System.out.println("网络数据记载完成============");
				return true;
			}			
		});

	}
	public void RequestNoDateCache(String url,final int code,final int biaoshi) {
		 TimeController timeController=new TimeController();
		 timeController.setTimeout(3000);
		 RequestParams params = new RequestParams();
		 String cacheKey = url + "get";  
		 RequestCacheConfig con=new RequestCacheConfig(false, false, false, false, false, false, false, timeController);
		 xrequest.sendGet(mRequestTag, url, cacheKey, new RequestParams(), con, new OnRequestListener<String>(){

			@Override
			public void onRequestPrepare(Request<?> request) {				
			}

			@Override
			public void onRequestFailed(Request<?> request,
					HttpException httpException) {
				System.out.println("httpException==="+httpException.getMessage());
				if("server error, Only throw ServerError for 5xx status codes.".equals(httpException.getMessage())){
				//	initRequestOnSuccess(result, code, biaoshi);
					dismisLoadingDialog();
				}else {
					initRequestOnFailure("请求失败", code, biaoshi);
				}
			}

			@Override
			public void onRequestRetry(Request<?> request,
					int currentRetryCount, HttpException previousError) {
				
			}

			@Override
			public void onRequestDownloadProgress(Request<?> request,
					long transferredBytesSize, long totalSize) {
				
			}

			@Override
			public void onRequestUploadProgress(Request<?> request,
					long transferredBytesSize, long totalSize,
					int currentFileIndex, File currentFile) {
				
			}

			@Override
			public void onRequestFinish(Request<?> request,
					Map<String, String> headers, String result) {
				
			}

			@Override
			public void onCacheDataLoadFinish(Request<?> request,
					Map<String, String> headers, String result) {
				
			}

			/**
			 * 用于数据的及时获取
			 * @return 
			 */
			@Override
			public boolean onParseNetworkResponse(Request<?> request,NetworkResponse networkResponse, String result) {
				initRequestOnSuccess(result, code, biaoshi);
				return true;
			}

			@Override
			public void onDone(Request<?> request, Map<String, String> headers,
					String result, DataType dataType) {				
			}
			
		});

		
	}

	/**
	 * 显示等待对话框
	 */
	public void showLoadingDialog(Context mContext,String titleStr,String contentStr){
		if (progressDialog == null) {
			progressDialog = CustomProgressDialog.createDialog(this);
			progressDialog.setMessage(contentStr);
			progressDialog.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					{
						if (keyCode == KeyEvent.KEYCODE_SEARCH) {
							return true;
						} else if (keyCode == KeyEvent.KEYCODE_BACK) {
							if (progressDialog != null) {
								progressDialog.dismiss();
								progressDialog = null;
							}
					    //BaseActivity.this.finish();
							return true; // 默认返回 false，这里false不能屏蔽返回键，改成true就可以了
						}else {
							return true;
						}
					}
				}
			});
			if(progressDialog!=null&&!progressDialog.isShowing()){
			   progressDialog.show();
			}
		}else{
			   progressDialog.show();
		}
		// pg = new ProgressDialog(this);
		// pg.setMessage(resources.getString(R.string.loading));
		// pg.show();
	}
	public void dismisLoadingDialog(){
		// pg.dismiss();
		if (progressDialog != null&&progressDialog.isShowing()) {
			progressDialog.dismiss();
			//progressDialog = null;
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			finish();
			/*overridePendingTransition(R.anim.push_left_out,
					R.anim.push_right_out);*/
			return false;
		}
		return false;
	}

}
