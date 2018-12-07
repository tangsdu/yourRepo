package com.cignacmb.iuss.web.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 * 描述：日期工具类<br>
 * 作者：万鸿辉 <br>
 * 修改日期：2013-1-16上午10:03:19 <br>
 * E-mail: wanhonghui@126.com <br>
 */
@Slf4j
public class DateUtil {
    public static final String YYYYMMDD = "yyyyMMdd";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YMDHMS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_year_MM_month_DD_day = "yyyy年MM月dd日";
    public static final String HHMMSS = "HH:mm:ss";
    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String YYYYMMDDHHMMSSS = "yyyyMMddHHmmsss";
    public static final String YMD_HMS = "yyyy/MM/dd HH:mm:ss";
    public static final String YMD = "yyyyMMdd";
    public static final String YMD_SLASH = "yyyy/MM/dd";
    public static final String YMD_DASH = "yyyy-MM-dd";



    /**
     * 取得当前系统日期
     * @return yyyy-MM-dd
     */
    public static String formatCurrrentDate() {
        return formatCurrrentDate(YYYY_MM_DD);
    }

    /**
     * 格式化系统日期时间
     * @param format 格式
     * @return 字符串形式的系统日期时间
     */
    public static String formatCurrrentDate(String format) {
        return formatDate(getSysDate(), format);
    }

    /**
     * 格式化日期成字符串
     * @param date 日期
     * @param format 格式
     * @return 字符串
     */
    public static String formatDate(Date date, String format) {
        if(date==null) {return "";}
        DateFormat dateformat = new SimpleDateFormat(format);
        return dateformat.format(date);
    }
    /**
     * 字符串转Date类型
     *
     * @param str     时间字符串
     * @param pattern 时间格式
     * @return Date类型
     * @throws ParseException
     */
    public static Date strToDate(String str, String pattern) throws ParseException {
        return DateUtils.parseDate(str, pattern);
    }

    public static Date getSysDate() {
        return new Date(System.currentTimeMillis());
    }

    /**
     * 精确到秒的时间戳
     */
    public static String timeStampOfSecondUnit() {
        String s = String.valueOf(System.currentTimeMillis());
        return s.substring(0, 10);
    }


    public static Date strToDateNoCheck(String str, String pattern) {
        try {
            return DateUtils.parseDate(str, pattern);
        } catch (ParseException e) {
            log.error("strToDateNoCheck error", e);
        }

        return null;
    }

    /**
     * Date类型转换成字符串
     *
     * @param date    Date类型
     * @param pattern 时间格式
     * @return 时间字符串
     */
    public static String format(Date date, String pattern) {
        return DateFormatUtils.format(date, pattern);
    }


    /**
     * 指定日期增加多少天 。
     *
     * @param date 指定的时间
     * @param n    增加的天数
     * @return
     */
    public static Date addDate(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, n);
        return cal.getTime();
    }


    /**
     * 计算周岁
     *
     * @param birthDay
     * @return
     */
    public static int getAge(Date birthDay) {
        LocalDate end = LocalDate.now().plusDays(1); //从次日开始计算周岁
        LocalDate start = dateTolocalDate(birthDay);
        Period period = Period.between(start, end);
        if (period.getYears() < 0) {
            return 0;
        }

        return period.getYears();
    }

    /**
     * 次日开始时间
     *
     * @return
     */
    public static Date tomorrowStartOfDay() {
        return localDateToDate(LocalDate.now().plusDays(1));
    }


    /**
     * 日期d1 是否在d2之前 (无时分秒)
     *
     * @param d1
     * @param d2
     * @return
     */
    public static boolean isBefore(LocalDate d1, LocalDate d2) {
        return d1.isBefore(d2);
    }


    /**
     * 日期d1 是否在d2之前 (无时分秒)
     *
     * @param d1
     * @param d2
     * @return
     */
    public static boolean isBefore(Date d1, Date d2) {
        return isBefore(dateTolocalDate(d1), dateTolocalDate(d2));
    }

    /**
     * java.util.Date转java.time.LocalDate
     *
     * @param date
     * @return
     */
    private static LocalDate dateTolocalDate(Date date) {
        Instant instant = date.toInstant();
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }


    /**
     * java.time.LocalDate转java.util.Date
     *
     * @param localDate
     * @return
     */
    private static Date localDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }


    /**
     * 获取当前时间
     *
     * @param pattern 输出指定日期格式样式
     * @return
     */
    public static String getCurrentDate(String pattern) {
        return format(new Date(), pattern);
    }


    /**
     * @return Date类型的转Timestamp
     */
    public static Timestamp strToTimestamp(String date) {
        return Timestamp.valueOf(date);
    }

    /**
     * 日期字符串转日期格式
     *
     * @param dateStr
     *            日期字符串
     * @param format
     *            日期格式
     * @return 指定格式的日期
     */
    public static Date getStrToDate(String dateStr, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = null;
        if (null != dateStr && !"".equals(dateStr)) {
            try {
                date = sdf.parse(dateStr);
            } catch (ParseException e) {
                log.error(e.getMessage(),e);

            }
        }
        return date;
    }

    /**
     * 日期转字符串
     *
     * @param date
     *            日期
     * @param format
     *            格式
     * @return 指定格式的字符串
     */
    public static String getDateToStr(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String result = "";
        try {
            if (date != null) {
                result = sdf.format(date);
            }
        } catch (Exception e) {
            log.error(e.getMessage(),e);

        }
        return result;
    }
}