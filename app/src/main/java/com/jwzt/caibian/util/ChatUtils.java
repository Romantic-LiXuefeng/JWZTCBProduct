package com.jwzt.caibian.util;

import java.io.File;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.application.Configs;
import com.jwzt.caibian.bean.ChatMessageBean;
import com.jwzt.caibian.bean.GroupListBean;
import com.jwzt.caibian.bean.LoginBean;
import com.jwzt.caibian.db.ChatsDao;
import com.jwzt.caibian.db.DatabaseHelper;
import com.jwzt.caibian.interfaces.ChatListRefreshInterface;
import com.jwzt.caibian.service.BackService;
import com.jwzt.caibian.service.IBackService;
import com.xdroid.request.XRequest;
import com.xdroid.request.base.Request;
import com.xdroid.request.config.DataType;
import com.xdroid.request.ex.RequestParams;
import com.xdroid.request.impl.OnRequestListenerAdapter;
import com.xdroid.request.network.HttpError;
import com.xdroid.request.network.HttpException;
import com.xdroid.request.response.NetworkResponse;

public class ChatUtils {
	private Context mContext;
	private List<GroupListBean> mList;
	private CbApplication application;
	private LoginBean mLoginBean;
	
	private Intent mServiceIntent;
	private IBackService iBackService;
	private DatabaseHelper mDatabaseHelper;
	private ChatsDao mChatsDao;
	private static ChatListRefreshInterface mChatListRefreshInterface;
	
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				if(mLoginBean!=null){
					initData(mLoginBean.getUserID());
					initService();
				}
				break;
			}
		};
	};
	
	
	public ChatUtils(Context context,DatabaseHelper databaseHelper) {
		// TODO Auto-generated constructor stub
		this.mContext=context;
		this.mDatabaseHelper=databaseHelper;
		Activity mActivity=(Activity)context;
		application=(CbApplication) mActivity.getApplication();
		mLoginBean=application.getmLoginBean();
		mChatsDao=new ChatsDao(databaseHelper);
	}
	
	public ChatUtils(Context context) {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 获取群组列表
	 * @param userId
	 */
	public void initData(String userId){
		if (CbApplication.getNetType() != -1) {
			String groupListUrl=String.format(Configs.groupListUrl, userId);
			System.out.println("grouplist:"+groupListUrl);
			RequestData(groupListUrl, Configs.groupListCode, -1);
		}else {
			UserToast.toSetToast(UIUtils.getContext(), UIUtils.getContext().getString(R.string.please_check_net));
		}
		
	}
	
	/**
	 * 获取token
	 */
	public void getToken(String userId,String userName){
		if (CbApplication.getNetType() !=-1) {
			String tockenUrl=String.format(Configs.tokenUrl, userId,userName);
			System.out.println("tokenUrl:"+tockenUrl);
			RequestData(tockenUrl, Configs.tokenCode, -1);
		}else {
			UserToast.toSetToast(UIUtils.getContext(), UIUtils.getContext().getString(R.string.please_check_net));
		}
		
	}
	
	/**
	 * 接收刷新消息列表的回调接口
	 * @param chatListRefreshInterface
	 */
	public static void setRefresh(ChatListRefreshInterface chatListRefreshInterface){
		mChatListRefreshInterface=chatListRefreshInterface;
	}
	
	/**
	 * 启动聊天服务
	 */
	private void initService() {
		mServiceIntent = new Intent(mContext, BackService.class);
		mServiceIntent.putExtra("isfirst", "true");
		mServiceIntent.putExtra("iskaiqi", "false");
		mServiceIntent.putExtra("token",application.getChatToken());
		mContext.bindService(mServiceIntent, conn, mContext.BIND_AUTO_CREATE);
		// 开始服务
		registerReceiver();
	}
	
	private ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// 未连接为空
			iBackService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// 已连接
			iBackService = IBackService.Stub.asInterface(service);
		}
	};
	
	// 注册广播
		private void registerReceiver() {
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(BackService.HEART_BEAT_ACTION);
			intentFilter.addAction(BackService.MESSAGE_ACTION);
			mContext.registerReceiver(mReceiver, intentFilter);
		}
		
		private BroadcastReceiver mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				// 消息广播
				if (action.equals(BackService.MESSAGE_ACTION)) {
					String stringExtra = intent.getStringExtra("message");
					if(stringExtra!=null&&!stringExtra.equals("")){
						  //对数据进行处理   
						  if(stringExtra.trim().startsWith("{")&&stringExtra.trim().endsWith("}")){	
							  System.out.println("stringExtra.trim()"+stringExtra.trim());
							  ChatMessageBean chatMessageBean=JSON.parseObject(stringExtra.trim(),ChatMessageBean.class);
							  if(chatMessageBean!=null){//入库操作
								  if(mLoginBean!=null){
									  chatMessageBean.setUserid(mLoginBean.getUserID());
									  String groupId=application.getGroupId();
									  if(IsNonEmptyUtils.isString(groupId)){
										  if(chatMessageBean.getGroupId().equals(groupId)){//表示该消息是对应的当前停留的聊天页面，则直接是已读状态
											  chatMessageBean.setIsRead("0");
										  }else{//表示该消息非当前聊天页面对应的消息，则改消息应为未读状态
											  chatMessageBean.setIsRead("1");
										  }
									  }else{//表示如果groupId为空则没有停留在当前任何一个聊天页面，那么所有入库的消息都是未读状态
										  chatMessageBean.setIsRead("1");
									  }
									  mChatsDao.saveOrUpdateTask(chatMessageBean);
									  if(mChatListRefreshInterface!=null){
										  mChatListRefreshInterface.setRefresh(chatMessageBean);
									  }
								  }
//								  mHandler.sendEmptyMessage(1);
							  }
//								String status=jsonObject.getString("status");
//								if(status.equals("100")){//表示登录成功
//									String dataresult=jsonObject.getString("data");
//									ChatMessageBean chatMessageBean=JSON.parseObject(dataresult,ChatMessageBean.class);
//									if(chatMessageBean!=null){//入库操作
//										mHandler.sendEmptyMessage(1);
//									}
//								}
						  }						  
						  //图文直播   msgType消息内容类型：1、文字，2、图片 
					}
					mServiceIntent.putExtra("isfirst", "false");
					mServiceIntent.putExtra("iskaiqi", "true");
					mContext.bindService(mServiceIntent, conn, mContext.BIND_AUTO_CREATE);
				} else if (action.equals(BackService.HEART_BEAT_ACTION)) {// 心跳广播
					//tv.setText("正常心跳");
				}
			}
		};
	
	/**
	 * 请求网络数据
	 * @param url
	 * @param code
	 * @param biaoshi
	 */
	private void RequestData(String url,final int code,final int biaoshi) {
		//与GET请求的URL一样，为了避免同样的缓存key、这里重新指定缓存key
		RequestParams params = new RequestParams();
		String cacheKey = url + "POST"; 
		String mRequestTag=cacheKey;
		XRequest.getInstance().sendPost(mRequestTag, url, cacheKey, params, new OnRequestListenerAdapter<String>() {
			/**
			 * 数据请求失败
			 */
			@Override
			public void onRequestFailed(Request<?> request, HttpException httpException) {
				super.onRequestFailed(request, httpException);
				switch (httpException.getHttpErrorCode()) {
				case HttpError.ERROR_NOT_NETWORK:
//					initRequestOnFailure("网络未连接，请检查", code, biaoshi);
					break;
				}
			}
            /**
             * 数据请求重试
             */
			@Override
			public void onRequestRetry(Request<?> request, int currentRetryCount, HttpException previousError) {
//				Toast.makeText(BaseActivity.this, "获取信息失败，系统已经为您重试" + currentRetryCount+"次", Toast.LENGTH_SHORT).show();
			}
			
			/**
			 * 缓存加载完成
			 */
			@Override
			public void onCacheDataLoadFinish(Request<?> request,Map<String, String> headers, String result) {
				super.onCacheDataLoadFinish(request, headers, result);
//				initRequestOnCache(result, code, biaoshi);
				System.out.println(result);
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
				System.out.println(result);
			}
			/**
			 *数据请求结束
			 */
			@Override
			public void onRequestFinish(Request<?> request,Map<String, String> headers, String result) {
				super.onRequestFinish(request, headers, result);
				System.out.println(result);
			}
			/**
			 * 网络数据加载完成
			 */
			@Override
			public boolean onParseNetworkResponse(Request<?> request,NetworkResponse networkResponse, String result) {
				super.onParseNetworkResponse(request, networkResponse, result);
				if(code==Configs.groupListCode){//表示群组列表
					JSONObject jsonObject=JSON.parseObject(result);
					String status=jsonObject.getString("status");
					if(status.equals("100")){//表示登录成功
						String dataresult=jsonObject.getString("data");
						mList=JSON.parseArray(dataresult,GroupListBean.class);
						if(IsNonEmptyUtils.isList(mList)){
							if(application!=null){
								application.setGroupList(mList);
							}
						}else{
							if(application!=null){
								application.setGroupList(null);
							}
						}
					}
				}else if(code==Configs.tokenCode){//获取token
					JSONObject jsonObject=JSON.parseObject(result);
					String status=jsonObject.getString("status");
					if(status.equals("100")){//表示登录成功
						String dataresult=jsonObject.getString("data");
						if(IsNonEmptyUtils.isString(dataresult)){
							if(application!=null){
								application.setChatToken(dataresult);
							}
							mHandler.sendEmptyMessage(1);
						}
						
					}
				}
				return true;
			}			
		});
	}
}
