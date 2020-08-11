package com.tbex.idmpotent.server.utils;


import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author xuliang
 * @version V1.0
 * @Description: 日期时间工具类
 * @date 2017-07-05
 */
@Slf4j
public class DateUtil {

    public static final String FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_YYYYMMDDHHMMSSSSS = "yyyyMMddhhmmssSSS";
    public static final String FORMAT_YYYYMMDDHHMMSS = "yyyyMMddhhmmss";

    public static String getCurrentDate() {
        String formatPattern_Short = "yyyyMMddhhmmss";
        SimpleDateFormat format = new SimpleDateFormat(formatPattern_Short);
        return format.format(new Date());
    }

    public static String getSeqString() {
        SimpleDateFormat fm = new SimpleDateFormat("yyyyMMddHHmmss"); // "yyyyMMdd G
        return fm.format(new Date());
    }

    public static Timestamp getCurrentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * 获取当前时间，格式为 yyyyMMddHHmmss
     *
     * @return
     */
    public static String getCurrentTimeStr(String format) {
        format = StringUtils.isEmpty(format) ? FORMAT_YYYY_MM_DD_HH_MM_SS : format;
        Date now = new Date();
        return date2Str(now, format);
    }

    public static String date2Str(Date date) {
        return date2Str(date, FORMAT_YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 时间转换成 Date 类型
     *
     * @param date
     * @param format
     * @return
     */
    public static String date2Str(Date date, String format) {
        if ((format == null) || format.equals("")) {
            format = FORMAT_YYYY_MM_DD_HH_MM_SS;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        if (date != null) {
            return sdf.format(date);
        }
        return "";
    }

    /**
     * 获取批量付款预约时间
     *
     * @return
     */
    public static String getRevTime() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, 1);
        String dateString = new SimpleDateFormat(DateUtil.FORMAT_YYYYMMDDHHMMSS).format(cal.getTime());
        System.out.println(dateString);
        return dateString;
    }

    /**
     * 时间比较
     *
     * @param date1
     * @param date2
     * @return DATE1>DATE2返回1，DATE1<DATE2返回-1,等于返回0
     */
    public static int compareDate(String date1, String date2, String format) {
        DateFormat df = new SimpleDateFormat(format);
        try {
            Date dt1 = df.parse(date1);
            Date dt2 = df.parse(date2);
            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取两个时间内的相隔分钟
     */
    public static Long timeDifferenceMinutes(Long time) {
        Long minutes = (System.currentTimeMillis() - time) / (1000 * 60);
        return minutes;
    }


    /**
     * 把给定的时间减掉给定的分钟数
     *
     * @param date
     * @param minute
     * @return
     */
    public static Date minusDateByMinute(Date date, int minute) {
        Date newDate = new Date(date.getTime() - (minute * 60 * 1000));
        return newDate;
    }


    public static void main(String[] args) {

        long start = System.currentTimeMillis();
        try {
            Thread.sleep(1000 * 2);
        } catch (Exception ex) {

        }

//        System.out.println(timeDifferenceMinutes(String.valueOf(start)));


    }

}
