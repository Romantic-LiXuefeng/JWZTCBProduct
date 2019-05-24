package com.jwzt.caibian.application;

import java.io.File;
import java.util.Map;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

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
/**
 * 基类的Fragment
 * @author jwzt
 */
@SuppressLint("NewApi")
public abstract class BaseFragment extends Fragment {
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
	public FragmentManager  fragmagmener;
	
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		xrequest=XRequest.getInstance();
		XRequest.getInstance().setRequestThreadPoolSize(10);
		fragmagmener = getFragmentManager();

	}
	
	protected  void RequestData(String url, int code, int biaoshi) {
		  final int  cdoes=code;
		 final int biaoshis=biaoshi;
		//与GET请求的URL一样，为了避免同样的缓存key、这里重新指定缓存key
		RequestParams params = new RequestParams();
		String cacheKey = url + "post";

		XRequest.getInstance().sendGet(cacheKey,url , params, new OnRequestListenerAdapter<String>() {
			/**
			 *数据请求失败
			 */
			@Override
			public void onRequestFailed(Request<?> request, HttpException httpException) {
				super.onRequestFailed(request, httpException);
				switch (httpException.getHttpErrorCode()) {
				case HttpError.ERROR_NOT_NETWORK:
					initRequestOnFailure("网络未连接，请检查", cdoes, biaoshis);
					break;
				}
			}
            /**
             * 数据请求重试
             */
			@Override
			public void onRequestRetry(Request<?> request, int currentRetryCount, HttpException previousError) {
				//Toast.makeText(getActivity(), "获取信息失败，系统已经为您重试" + currentRetryCount+"次", Toast.LENGTH_SHORT).show();
			}
			
			/**
			 * 缓存加载完成
			 */
			@Override
			public void onCacheDataLoadFinish(Request<?> request,Map<String, String> headers, String result) {
				super.onCacheDataLoadFinish(request, headers, result);
				initRequestOnCache(result, cdoes, biaoshis);
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
				initRequestOnCache(result, cdoes, biaoshis);

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
			public boolean onParseNetworkResponse(Request<?> request,
					NetworkResponse networkResponse, String result) {
				super.onParseNetworkResponse(request, networkResponse, result);
				initRequestOnSuccess(result, cdoes, biaoshis);
				return true;
				
			}
			
			
		});

	}
	protected  void RequestDataTp(String url,final int code,final int biaoshi) {
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
//				Toast.makeText(getActivity(), "获取信息失败，系统已经为您重试" + currentRetryCount+"次", Toast.LENGTH_SHORT).show();
			}
			
			/**
			 * 缓存加载完成
			 */
			@Override
			public void onCacheDataLoadFinish(Request<?> request,Map<String, String> headers, String result) {
				super.onCacheDataLoadFinish(request, headers, result);
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
				initRequestOnCache(result, code, biaoshi);

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
			public boolean onParseNetworkResponse(Request<?> request,
					NetworkResponse networkResponse, String result) {
				super.onParseNetworkResponse(request, networkResponse, result);
				initRequestOnSuccess(result, code, biaoshi);
				return true;
				
			}
			
			
		});

	}
	public void RequestNoDateCache(String url,final int code,final int biaoshi) {
		 TimeController timeController=new TimeController();
		 timeController.setTimeout(5000);
		 RequestParams params = new RequestParams();
		 String cacheKey = url + "post";  
		 RequestCacheConfig con=new RequestCacheConfig(false, false, false, false, false, false, false, timeController);
		 xrequest.sendGet(mRequestTag, url, cacheKey, new RequestParams(), con, new OnRequestListener<String>(){

			@Override
			public void onRequestPrepare(Request<?> request) {				
			}

			@Override
			public void onRequestFailed(Request<?> request,
					HttpException httpException) {				
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
	
	

}
