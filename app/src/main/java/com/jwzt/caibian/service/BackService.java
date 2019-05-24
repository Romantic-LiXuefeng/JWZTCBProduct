package com.jwzt.caibian.service;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.apache.http.util.ByteArrayBuffer;

import com.jwzt.caibian.application.Configs;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.StrictMode;
import android.util.Log;

public class BackService extends Service {
	private static final String TAG = "BackService";
	/** 心跳检测时间  */
	private static final long HEART_BEAT_RATE = 3 * 1000;
	/** 主机IP地址  */
//	private static final String HOST = "192.168.1.192";//御景测试环境
//	private static final String HOST = "192.168.1.166";
//	private static final String HOST = "192.168.1.66";
//	private static final String HOST = "47.93.187.130";//公网测试环境
	private static final String HOST = Configs.HTTPIP;//公网环境
	/** 端口号  */
	public static final int PORT = 9002;
	/** 消息广播  */
	public static final String MESSAGE_ACTION = "org.feng.message_ACTION";
	/** 心跳广播  */
	public static final String HEART_BEAT_ACTION = "org.feng.heart_beat_ACTION";
	/*重连广播*/
	public static final String ChongLian_BEAT_ACTION = "org.feng.chong_lian_ACTION";

	private long sendTime = 0L;

	/** 弱引用 在引用对象的同时允许对垃圾对象进行回收  */
	private WeakReference<Socket> mSocket; 
	
	private Socket socket;
	
	private String isFirst;
	ByteArrayBuffer buffers;
	private String  flag;
	private String token;

	private ReadThread mReadThread;

	private IBackService.Stub iBackService = new IBackService.Stub() {
		@Override
		public boolean sendMessage(String message) throws RemoteException {
			return sendMsg(message);
		}
	};

	@SuppressLint("NewApi")
	@Override
	public IBinder onBind(Intent arg0) {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
		String infirst=arg0.getStringExtra("isFirst");
		if(arg0!=null){
			isFirst=arg0.getStringExtra("isfirst");
			flag=arg0.getStringExtra("iskaiqi");
			token=arg0.getStringExtra("token");
			if(isFirst.equals("true")){
				try {
					socket = new Socket(HOST, PORT);
				} catch (UnknownHostException e) {
					if(buffers!=null){
					   	 buffers.clear();
					}
					//错误后重连机制
					Intent intent = new Intent(ChongLian_BEAT_ACTION);
					intent.putExtra("mess", "");
					sendBroadcast(intent);
				} catch (Exception e) {
					if(buffers!=null){
					   	 buffers.clear();
					}
					//错误后重连机制
					Intent intent = new Intent(ChongLian_BEAT_ACTION);
					intent.putExtra("mess", "");
					sendBroadcast(intent);
				}
				new InitSocketThread().start();
			}
		}
		return (IBinder) iBackService;
	}

	@Override
	public void onCreate() {
		super.onCreate();
//		System.out.println();
//		try {
//			socket = new Socket(HOST, PORT);
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		new InitSocketThread().start();
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
//		if(intent!=null){
//			isFirst=intent.getStringExtra("isfirst");
//			flag=intent.getStringExtra("iskaiqi");
//			if(isFirst.equals("true")){
//				try {
//					socket = new Socket(HOST, PORT);
//				} catch (UnknownHostException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				new InitSocketThread().start();
//			}
//		}
	}

	// 发送心跳包
	private Handler mHandler = new Handler();
	private Runnable heartBeatRunnable = new Runnable() {
		@Override
		public void run() {
//			if(flag.equals("false")){
				if (System.currentTimeMillis() - sendTime >= HEART_BEAT_RATE) {
					boolean isSuccess = sendMsg("{\"token\":\"abcd\"}");// 就发送一个\r\n过去, 如果发送失败，就重新初始化一个socket
					if (!isSuccess) {
						mHandler.removeCallbacks(heartBeatRunnable);
						mReadThread.release();
						releaseLastSocket(mSocket);
						new InitSocketThread().start();
					}
				}
				mHandler.postDelayed(this, HEART_BEAT_RATE);
//			}else{
//				
//			}
			
		}
	};

	public boolean sendMsg(String msg) {
//		if(flag){
//			
//		}else{
			if (null == mSocket || null == mSocket.get()) {
				return false;
			}
			Socket soc = mSocket.get();
			try {
				if (!soc.isClosed() && !soc.isOutputShutdown()) {
					if(isFirst.equals("true")){ //如果是第一次则发送token消息
					  OutputStream os = soc.getOutputStream();
					  PrintStream ps = new PrintStream(os);
					  ps.println("{\"token\":\"" +token + "\"}");
					  /*os.write(message.getBytes());
					  os.flush();*/
					  sendTime = System.currentTimeMillis();// 每次发送成功数据，就改一下最后成功发送的时间，节省心跳间隔时间
					  Log.i(TAG, "发送成功的时间：" + sendTime);
					  isFirst="false";
					}else if(flag.equals("true")){
						  OutputStream os = soc.getOutputStream();
						  PrintStream ps = new PrintStream(os);
						  ps.println("{\"token\":\"" +token + "\"}");
						  /*os.write(message.getBytes());
						  os.flush();*/
						  sendTime = System.currentTimeMillis();// 每次发送成功数据，就改一下最后成功发送的时间，节省心跳间隔时间
						  Log.i(TAG, "发送成功的时间：" + sendTime);
					}else{
						 OutputStream os = soc.getOutputStream();
						  PrintStream ps = new PrintStream(os);
						  ps.println(".");
						  /*os.write(message.getBytes());
						  os.flush();*/
						  sendTime = System.currentTimeMillis();// 每次发送成功数据，就改一下最后成功发送的时间，节省心跳间隔时间
						  Log.i(TAG, "发送成功的时间：" + sendTime);	
					}
				} else {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				//错误后重连机制
				Intent intent = new Intent(ChongLian_BEAT_ACTION);
				intent.putExtra("mess", "");
				sendBroadcast(intent);
				if(buffers!=null){
				   	 buffers.clear();
				}
				return false;
			}
//		}
		
		return true;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.out.println("service被销毁"+"被销毁");
	/*	if(mSocket!=null){
			releaseLastSocket(mSocket);
		} */
	}

	// 初始化socket
	private void initSocket() throws UnknownHostException, IOException {
		mSocket = new WeakReference<Socket>(socket);
		mReadThread = new ReadThread(socket);
		mReadThread.start();
		mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);// 初始化成功后，就准备发送心跳包
	}

	// 释放socket
	private void releaseLastSocket(WeakReference<Socket> mSocket) {
		try {
			if (null != mSocket) {
				Socket sk = mSocket.get();
				if(sk!=null){
					if (!sk.isClosed()) {
						sk.close();
					}
				}
				
				sk = null;
				mSocket = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			//错误后重连机制
			Intent intent = new Intent(ChongLian_BEAT_ACTION);
			intent.putExtra("mess", "");
			sendBroadcast(intent);
			if(buffers!=null){
		   	  buffers.clear();
			}
		}
	}

	class InitSocketThread extends Thread {
		@Override
		public void run() {
			super.run();
			try {
				initSocket();
			}catch (Exception e) {
				e.printStackTrace();
				//错误后重连机制
				Intent intent = new Intent(ChongLian_BEAT_ACTION);
				intent.putExtra("mess", "");
				sendBroadcast(intent);
				if(buffers!=null){
				   	 buffers.clear();
				}
			}
		}
	}

	public class ReadThread extends Thread {
		private WeakReference<Socket> mWeakSocket;
		private boolean isStart = true;

		public ReadThread(Socket socket) {
			mWeakSocket = new WeakReference<Socket>(socket);
		}

		public void release() {
			isStart = false;
			releaseLastSocket(mWeakSocket);
		}

		@SuppressLint("NewApi")
		@Override
		public void run() {
			super.run();
			Socket socket = mWeakSocket.get();
			if (null != socket) {
				try {
				    buffers=new ByteArrayBuffer(1024);
					InputStream is = socket.getInputStream();
					int length = 0;
					while (!socket.isClosed() && !socket.isInputShutdown()&& isStart) {
						//while (true) {
							int read = is.read();
							if(read!=-1){
							if(read == '\n'){
								System.out.println(read == '\n');
								String messages=new String(buffers.toByteArray());
								if (messages.equals("ok")) {// 处理心跳回复
									Intent intent = new Intent(HEART_BEAT_ACTION);
									sendBroadcast(intent);
								} else {
									// 其他消息回复
									Intent intent = new Intent(MESSAGE_ACTION);
									intent.putExtra("message", messages);
									sendBroadcast(intent);
								}
								//buffers=null;
								//buffers=new  ByteArrayBuffer(3*1024);
								buffers.clear();							
							}else{
								buffers.append(read);
								
							}
							}
						//}

						
						/*if (length > 0) {
							@SuppressWarnings("unused")
							String messages=new String(buffer);
							String message = new String(Arrays.copyOf(buffer,length)).trim(); 
							Log.i(TAG, "收到服务器发送来的消息："+message); 
							// 收到服务器过来的消息，就通过Broadcast发送出去  
							
						} */
					}
				} catch (Exception e) {
					e.printStackTrace();
					//错误后重连机制
					Intent intent = new Intent(ChongLian_BEAT_ACTION);
					sendBroadcast(intent);
					if(buffers!=null){
					   	 buffers.clear();
					}
					
				}
			}
		}
	}

}
