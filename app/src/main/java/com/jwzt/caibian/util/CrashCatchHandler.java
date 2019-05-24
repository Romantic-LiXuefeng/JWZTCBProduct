package com.jwzt.caibian.util;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast; 

/**
 * 
 * @author Administrator
 *������־ץȡ����
 */
public class CrashCatchHandler implements UncaughtExceptionHandler {

    /**
     * ϵͳĬ��UncaughtExceptionHandler
     */
    private UncaughtExceptionHandler mDefaultHandler;

    /**
     * context
     */
    private Context mContext;

    /**
     * �洢�쳣�Ͳ�����Ϣ
     */
    private Map<String, String> paramsMap = new HashMap<String, String>();

    /**
     * ��ʽ��ʱ��
     */
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    private String TAG = this.getClass().getSimpleName();

    private static CrashCatchHandler mInstance;

    private CrashCatchHandler() {

    }

    /**
     * ��ȡCrashHandlerʵ��
     */
    public static synchronized CrashCatchHandler getInstance() {
        if (null == mInstance) {
            mInstance = new CrashCatchHandler();
        }
        return mInstance;
    }

    public void init(Context context) {
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //���ø�CrashHandlerΪϵͳĬ�ϵ�
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * uncaughtException �ص�����
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            //����Լ�û������ϵͳ����
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            //�Լ�����
            try {//�ӳ�3��ɱ����
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }
            //�˳�����
            AppManager.getAppManager().AppExit(mContext);
        }

    }

    /**
     * �ռ�������Ϣ.���͵�������
     *
     * @return �����˸��쳣����true, ����false
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        //�ռ��豸������Ϣ
        collectDeviceInfo(mContext);
        //����Զ�����Ϣ
        addCustomInfo();
        //ʹ��Toast����ʾ�쳣��Ϣ
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                //�ڴ˴���������쳣�����
                Toast.makeText(mContext, "程序出小差了", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();
        //������־�ļ�
        saveCrashInfo2File(ex);
        return true;
    }


    /**
     * �ռ��豸������Ϣ
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        //��ȡversionName,versionCode
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                paramsMap.put("versionName", versionName);
                paramsMap.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
        //��ȡ����ϵͳ��Ϣ
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                paramsMap.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
                Log.e(TAG, "an error occured when collect crash info", e);
            }
        }
    }

    /**
     * ����Զ������
     */
    private void addCustomInfo() {

    }

    /**
     * ���������Ϣ���ļ���
     *
     * @param ex
     * @return �����ļ�����, ���ڽ��ļ����͵�������
     */
    private String saveCrashInfo2File(Throwable ex) {

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        try {
            long timestamp = System.currentTimeMillis();
            String time = format.format(new Date());
            String fileName = "productcrash-" + time + "-" + timestamp + ".log";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/crash/";
               
                
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(path + fileName);
                fos.write(sb.toString().getBytes());
                Log.i(TAG, "saveCrashInfo2File: "+sb.toString());
                fos.close();
            }
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        }
        return null;
    }


}
