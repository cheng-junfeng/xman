package com.xman.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.xman.BuildConfig;

public class Logger {
    /**
     * 打印日志时显示的TAG
     */
    private static String TAG = "Logger";
    /**
     * 是否显示类的全路径名
     */
    private static boolean IS_FULL_CLASSNAME;

    /**
     * 设置是否打印类的全路径名，默认 false
     *
     * @param isFullClassName 是否打印类的全路径名，默认 false
     */
    public static void setFullClassName(boolean isFullClassName) {
        Logger.IS_FULL_CLASSNAME = isFullClassName;
    }

    /**
     * 设置打印的 TAG 名
     *
     * @param tag 打印的 TAG 名
     */
    public static void setAppTAG(@NonNull String tag) {
        Logger.TAG = tag;
    }

    public static void m(@NonNull String msg) {
        if (BuildConfig.DEBUG) {
            Log.d("Xman", msg);
        }
    }

    public static void v(@NonNull String msg) {
        if (BuildConfig.DEBUG) {
            Log.v(TAG, getLogTitle() + msg);
        }
    }

    public static void d(@NonNull String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, getLogTitle() + msg);
        }
    }

    public static void i(@NonNull String msg) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, getLogTitle() + msg);
        }
    }

    public static void w(@NonNull String msg) {
        if (BuildConfig.DEBUG) {
            Log.w(TAG, getLogTitle() + msg);
        }
    }

    public static void e(@NonNull String msg) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, getLogTitle() + msg);
        }
    }

    /**
     * 根据是否需要打印类的全路径名获取打印日志的基本信息
     *
     * @return 打印日志的基本信息
     */
    @NonNull
    private static String getLogTitle() {
        StackTraceElement elm = Thread.currentThread().getStackTrace()[4];
        String className = elm.getClassName();
        if (!IS_FULL_CLASSNAME) {
            int dot = className.lastIndexOf('.');
            if (dot != -1) {
                className = className.substring(dot + 1);
            }
        }
        return className + "." + elm.getMethodName() + "(" + elm.getLineNumber() + ")" + ": ";
    }
}
