package com.jwzt.caibian.util;

import java.util.Stack;

import android.app.Activity;
import android.content.Context;

public class AppManager {
	private static Stack<Activity> activityStack;
    private static AppManager instance;

    private AppManager() {
    }

    /**
     * ��һʵ��
     */
    public static AppManager getAppManager() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }

    /**
     * ���Activity����ջ
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * ��ȡ��ǰActivity����ջ�����һ��ѹ��ģ�
     */
    public Activity currentActivity() {
        return activityStack.lastElement();
    }

    /**
     * ������ǰActivity����ջ�����һ��ѹ��ģ�
     */
    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * ����ָ����Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null && !activity.isFinishing()) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * ����ָ��������Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
                break;
            }
        }
    }

    /**
     * ��������Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                finishActivity(activityStack.get(i));
                break;
            }
        }
        activityStack.clear();
    }

    /**
     * ��ȡָ����Activity
     *
     * @author kymjs
     */
    public static Activity getActivity(Class<?> cls) {
        if (activityStack != null)
            for (Activity activity : activityStack) {
                if (activity.getClass().equals(cls)) {
                    return activity;
                }
            }
        return null;
    }

    public static Stack<Activity> getActivitys() {
        return activityStack;
    }

    /**
     * �˳�Ӧ�ó���
     */
    public void AppExit(Context context) {
        try {
            finishAllActivity();
            // ɱ����Ӧ�ý���
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } catch (Exception ignored) {
        }
    }

    /**
     * ���ص�ǰActivityջ��Activity������
     *
     * @return
     */
    public int getActivityCount() {
        int count = activityStack.size();
        return count;
    }

    /**
     * ��ջ���Ƴ�Activity
     */
    public void removeActivity(Activity activity) {
        if (activityStack == null) {
            return;
        } else if (activityStack.contains(activity)) {
            activityStack.remove(activity);
        }

        if (activity != null && !activity.isFinishing()) {
            activity.finish();
            activity = null;
        }
}

}
