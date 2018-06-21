package com.xman.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {

    public static final SimpleDateFormat DEFAULT_SDF = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());

    /**
     * 将时间戳转为时间字符串
     * <p>格式为yyyy-MM-dd HH:mm:ss</p>
     *
     * @param milliseconds 毫秒时间戳
     * @return 时间字符串
     */
    public static String millisecondsString(long milliseconds) {
        return DEFAULT_SDF.format(new Date(milliseconds));
    }

    /**
     * 将时间字符串转为时间戳
     * <p>格式为用户自定义</p>
     *
     * @param time   时间字符串
     * @param format 时间格式
     * @return 毫秒时间戳
     */
    public static long stringMilliseconds(String time, SimpleDateFormat format) {
        if (!TextUtils.isEmpty(time)) {
            try {
                return format.parse(time).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }
}
