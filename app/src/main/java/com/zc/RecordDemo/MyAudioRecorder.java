package com.zc.RecordDemo;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;

import java.io.*;

import com.jwzt.caibian.interfaces.VolumeListener;

/**
 * 7/1/14  11:00 AM
 * Created by JustinZhang.
 */
public class MyAudioRecorder {
	
	/**音量监听器**/
	private VolumeListener mVolumeListener;
	private static final String TAG = "MyAudioRecorder";
    private AudioRecord mRecorder = null;
    public static final int SAMPLE_RATE = 16000;
    private Mp3Conveter mConveter;
    private short[] mBuffer;
    private boolean mIsRecording = false;
    private File mRawFile;
    private File mEncodedFile;
    private String mVoiceFrom;

    public void setmVolumeListener(VolumeListener mVolumeListener) {
		this.mVolumeListener = mVolumeListener;
	}
    
    /**
     * 构造函数用于区别录音
     * @param voiceFrom
     */
    public MyAudioRecorder(String voiceFrom){
		this.mVoiceFrom=voiceFrom;
	}
    
    public void prepare(){
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);
        mBuffer = new short[bufferSize];
        mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        mConveter = new Mp3Conveter();
    }

    /**
     * 开始录音
     */
    public void startRecording() {
        if(mIsRecording){
            return;
        }
        
        Log.e(TAG, "startRcording");
        if (mRecorder == null) {
            Log.e(TAG,"mRocorder is nul this should not happen");
            return;
        }
        mIsRecording = true;
        mRecorder.startRecording();
        mRawFile = getFile("raw");
        startBufferedWrite(mRawFile);
    }

    private void startBufferedWrite(final File file) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DataOutputStream output = null;
                try {
                    output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
                    long time1 = System.currentTimeMillis();  
                    while (mIsRecording) {
                        if(mIsPause){
                            continue;
                        }
                        int v = 0; 
                        int readSize = mRecorder.read(mBuffer, 0, mBuffer.length);
                        for (int i = 0; i < readSize; i++) {
                            output.writeShort(mBuffer[i]);
                            v += mBuffer[i]*mBuffer[i]; 
                        }
                        long time2 = System.currentTimeMillis(); 
                        if (time2-time1 >100) {  
                            if (!String.valueOf(v / (float) readSize).equals("NaN")) {  
                                float f = (int) (Math.abs((int)(v /(float)readSize)/10000) >> 1);  
                                if (null == mVolumeListener) {  
                                    return;  
                                }  
                                if (f<=10) {  
                                	mVolumeListener.onCurrentVoice(1);  
                                }else if (f<=20&&f>10) {  
                                	mVolumeListener.onCurrentVoice(2);  
                                }else if (f<=30&&f>20) {  
                                	mVolumeListener.onCurrentVoice(3);  
                                }else if (f<=40&&f>30) {  
                                	mVolumeListener.onCurrentVoice(4);  
                                }else if (f<=50&&f>40) {  
                                	mVolumeListener.onCurrentVoice(5);  
                                }else if (f<=60&&f>50) {  
                                	mVolumeListener.onCurrentVoice(6);  
                                }else if (f<=70&&f>60) {  
                                	mVolumeListener.onCurrentVoice(7);  
                                }  else  {  
                              	mVolumeListener.onCurrentVoice(8);  
                              } 
                            }  
                            time1 = time2;  
                        }  
                    }  
                   
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (output != null) {
                        try {
                            output.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                output.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }).start();
    }

    private boolean mIsPause=false;
    public void pauseRecording(){
            mIsPause = true;
    }

    public void restartRecording(){
            mIsPause = false;
    }

    /**
     * 停止录音，并返回转化成MP3的存储路径
     */
    public String stopRecording() {
        Log.e(TAG, "stopRecording");
        if (mRecorder == null) {
            return null;
        }
        if(!mIsRecording){
            return null;
        }
        mRecorder.stop();
        mIsPause = false;
        mIsRecording = false;
        mEncodedFile = getFile("mp3");
        mConveter.encodeFile(mRawFile.getAbsolutePath(), mEncodedFile.getAbsolutePath());
        return mEncodedFile.getAbsolutePath();
    }

    /*
    public void startPlaying() {
        Log.e(TAG, "startPlayingstartPlaying");
        if (mPlayer != null) {
            return;
        }
        mPlayer = new MediaPlayer();
        try {
            Log.e("DDD", "DATA SOURCE: " + mEncodedFile.getAbsolutePath());
            mPlayer.setDataSource(mEncodedFile.getAbsolutePath());
            mPlayer.prepare();
            mPlayer.start();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mPlayer.release();
                    mPlayer = null;
                }
            });
        } catch (IOException e) {
            Log.e(TAG, e.toString() + "\nprepare() failed");
        }
    }
    */

    /*
    public void stopPlaying() {
        Log.e(TAG, "stopPlaying");
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
    */

    public void release() {
        if (mRecorder != null) {
            mRecorder.release();
            mIsPause = false;
            mIsRecording = false;
        }

        if(mConveter!=null){
            mConveter.destroyEncoder();
        }
    }

    private File getFile(final String suffix) {
        Time time = new Time();
        time.setToNow();
        File f= new File(Environment.getExternalStorageDirectory(), mVoiceFrom+ "." + suffix);
        Log.e(TAG,"file address:"+f.getAbsolutePath());
        return f;
    }
    
    
    /**
     * 返回录音之后保存的路径
     * @return
     */
    public String getMP3Path(){
    	if(mEncodedFile.exists()){//录音文件存在
    		return mEncodedFile.getAbsolutePath();
    	}else{
    		return null;
    	}
    }
}
