package com.bayer.gifts.process.utils;

import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 日期处理
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {
    /**
     * 时间格式(yyyy-MM-dd)
     */
    public final static String DATE_PATTERN = "yyyy-MM-dd";
    /**
     * 时间格式(yyyy-MM-dd HH:mm:ss)
     */
    public final static String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    /**
     * 时间格式(yyyy-MM-dd HH:mm)
     */
    public final static String TIME_PATTERN = "yyyy-MM-dd HH:mm";
    /**
     * 时间格式(HH:mm)
     */
    public static final String HOUR_MINUTES_PATTERN = "HH:mm";
    /**
     * 时间格式(yyyy)
     */
    public static final String YEAR_PATTERN = "yyyy";
    /**
     * 时间格式(MM)
     */
    public static final String MONTH_PATTERN_MM = "MM";
    /**
     * 时间格式(dd)
     */
    public static final String DAY_PATTERN = "dd";
    /**
     * 时间格式(HH)
     */
    public static final String HOUR_PATTERN = "HH";
    /**
     * 时间格式(mm)
     */
    public static final String MINUTE_PATTERN = "mm";
    /**
     * 时间格式(mm)
     */
    public static final String RUNNINGTIME_PATTERN = "yyyyMMddHHmm";

    /**
     * 日期格式化 日期格式为：yyyy-MM-dd
     *
     * @param date 日期
     * @return 返回yyyy-MM-dd格式日期
     */
    public static String format(Date date) {
        return format(date, DATE_PATTERN);
    }

    /**
     * 日期格式化 日期格式为：yyyy-MM-dd
     *
     * @param date    日期
     * @param pattern 格式，如：DateUtils.DATE_TIME_PATTERN
     * @return 返回yyyy-MM-dd格式日期
     */
    public static String format(Date date, String pattern) {
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return df.format(date);
        }
        return null;
    }

    /**
     * 字符串转换成日期
     *
     * @param strDate 日期字符串
     * @param pattern 日期的格式，如：DateUtils.DATE_TIME_PATTERN
     */
    public static Date stringToDate(String strDate, String pattern) {
        if (StringUtils.isBlank(strDate)) {
            return null;
        }

        DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
        return fmt.parseLocalDateTime(strDate).toDate();
    }

    /**
     * 根据周数，获取开始日期、结束日期
     *
     * @param week 周期  0本周，-1上周，-2上上周，1下周，2下下周
     * @return 返回date[0]开始日期、date[1]结束日期
     */
    public static Date[] getWeekStartAndEnd(int week) {
        DateTime dateTime = new DateTime();
        LocalDate date = new LocalDate(dateTime.plusWeeks(week));

        date = date.dayOfWeek().withMinimumValue();
        Date beginDate = date.toDate();
        Date endDate = date.plusDays(6).toDate();
        return new Date[]{beginDate, endDate};
    }

    /**
     * 对日期的【秒】进行加/减
     *
     * @param date    日期
     * @param seconds 秒数，负数为减
     * @return 加/减几秒后的日期
     */
    public static Date addDateSeconds(Date date, int seconds) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusSeconds(seconds).toDate();
    }

    /**
     * 对日期的【分钟】进行加/减
     *
     * @param date    日期
     * @param minutes 分钟数，负数为减
     * @return 加/减几分钟后的日期
     */
    public static Date addDateMinutes(Date date, int minutes) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusMinutes(minutes).toDate();
    }

    /**
     * 对日期的【小时】进行加/减
     *
     * @param date  日期
     * @param hours 小时数，负数为减
     * @return 加/减几小时后的日期
     */
    public static Date addDateHours(Date date, int hours) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusHours(hours).toDate();
    }

    /**
     * 对日期的【天】进行加/减
     *
     * @param date 日期
     * @param days 天数，负数为减
     * @return 加/减几天后的日期
     */
    public static Date addDateDays(Date date, int days) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusDays(days).toDate();
    }

    /**
     * 对日期的【周】进行加/减
     *
     * @param date  日期
     * @param weeks 周数，负数为减
     * @return 加/减几周后的日期
     */
    public static Date addDateWeeks(Date date, int weeks) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusWeeks(weeks).toDate();
    }

    /**
     * 对日期的【月】进行加/减
     *
     * @param date   日期
     * @param months 月数，负数为减
     * @return 加/减几月后的日期
     */
    public static Date addDateMonths(Date date, int months) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusMonths(months).toDate();
    }

    /**
     * 对日期的【年】进行加/减
     *
     * @param date  日期
     * @param years 年数，负数为减
     * @return 加/减几年后的日期
     */
    public static Date addDateYears(Date date, int years) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusYears(years).toDate();
    }

    public static Date strToDate(String dateStr) {
        if (StringUtils.isEmpty(dateStr) || "null".equals(dateStr.toLowerCase())) {
            return new Date();
        }
        int len = dateStr.length();
        SimpleDateFormat dateFormat = null;
        if (len == 8) {
            dateFormat = new SimpleDateFormat("yyyyMMdd");
        } else if (len == 10) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        } else if (len == 14) {
            dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        } else if (len == 19) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        } else if (len == 21) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        } else if (dateStr.contains("CST")) {
            dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'CST' yyyy", Locale.ENGLISH);// CST格式
        } else if (dateStr.contains("UTC")) {
            dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'UTC 0800' yyyy", Locale.ENGLISH);// UTC格式
        } else {
            return new Date();
        }
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取指定的日期，格式为yyyy-MM-dd
     * @param amount -1:昨天, 0:今天 ,1:明天
     * @return
     */
    public static String getCustomDate(int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, amount);
        return String.format("%tY-%<tm-%<td", calendar.getTime());
    }

    /**
     * 获取过去的分钟
     *
     * @param date
     * @return
     */
    public static long pastMinutes(Date date) {
        long time = System.currentTimeMillis() - date.getTime();
        return time / (60 * 1000);
    }

    /**
     * 获得格式为YYYY-MM-DD日期字符串
     *
     * @param amount -1:昨天, 0:今天 ,1:明天
     * @return
     */
    public static String getDate(int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, amount);
        return String.format("%tY-%<tm-%<td", calendar.getTime());
    }

    public static XMLGregorianCalendar convertToXMLGregorianCalendar(Date date) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        XMLGregorianCalendar gc = null;
        try {
            gc = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gc;
    }

    public static Date strToDate(String dateStr, String pattern) {
        if (StringUtils.isEmpty(dateStr) || "null".equals(dateStr.toLowerCase())) {
            return new Date();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String dateToStr(Date date, String pattern) {
        if (null == date) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * 获得格式为YYYYMMDD日期字符串
     *
     * @param amount -1:昨天, 0:今天 ,1:明天
     * @return
     */
    public static String getCurDate(int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, amount);
        return String.format("%tY%<tm%<td", calendar.getTime());
    }

    /**
     * 获取当前月天数
     *
     * @param date
     * @return
     */
    public static int getDaysOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 计算两个日期中的天数
     *
     * @param firstString
     * @param secondString
     * @return
     */
    @SneakyThrows
    public static int betweenDate(String firstString, String secondString) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date firstDate = df.parse(firstString);
        Date secondDate = df.parse(secondString);
        int dayNum = (int) ((secondDate.getTime() - firstDate.getTime()) / (24 * 60 * 60 * 1000));
        return dayNum;
    }

    /**
     * 获取日期
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static List<String> getDays(String startTime, String endTime) {

        // 返回的日期集合
        List<String> days = new ArrayList<String>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date start = dateFormat.parse(startTime);
            Date end = dateFormat.parse(endTime);

            Calendar tempStart = Calendar.getInstance();
            tempStart.setTime(start);

            Calendar tempEnd = Calendar.getInstance();
            tempEnd.setTime(end);
            tempEnd.add(Calendar.DATE, +1);// 日期加1(包含结束)
            while (tempStart.before(tempEnd)) {
                days.add(dateFormat.format(tempStart.getTime()));
                tempStart.add(Calendar.DAY_OF_YEAR, 1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;
    }



    /**
     * 比较时间是否重合
     *
     * @param list
     * @param startTime
     * @param endTime
     * @return 冲突:true,不冲突false
     */
    public static boolean slotDate(List<Map<String, Date>> list, Long startTime, Long endTime) {

        for (Map<String, Date> map : list) {
            long slotStartTime = map.get("startTime").getTime();
            long slotEndTime = map.get("endTime").getTime();
            if (startTime > slotStartTime && startTime < slotEndTime) {
                return true;
            } else if (startTime <= slotStartTime && endTime >= slotEndTime) {
                return true;
            } else if (endTime > slotStartTime && endTime < slotEndTime) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断当前时间是否在该时间段内
     *
     * @param nowDate
     * @param startDate
     * @param endDate
     * @return true:在时间段内,false不在
     * @throws Exception
     */
    public static boolean between(Date nowDate, Date startDate, Date endDate) {
        long nowTime = nowDate.getTime();
        long startTime = startDate.getTime();
        long endTime = endDate.getTime();
        return nowTime >= startTime && nowTime <= endTime;
    }

    public static Date dateFormate(Date date) {
        String str = format(date);
        return strToDate(str, DateUtils.DATE_PATTERN);
    }

    /**
     * 相差天数 包括当天 2014-05-06 ~ 2014-05-07 计算后是2天
     *
     * @param startTime
     * @param endTime
     * @param format
     * @return
     * @throws Exception
     */
    public static int dateDiffWithToday(String startTime, String endTime, String format) throws Exception {
        SimpleDateFormat sf = new SimpleDateFormat(format);
        double nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        double diff = sf.parse(endTime).getTime() - sf.parse(startTime).getTime();
        return new BigDecimal(new Double(diff / nd) + 1).setScale(0, BigDecimal.ROUND_UP).intValue();
    }

    public static int dateDiffWithToday(Date startTime, Date endTime) throws Exception{
        double nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        double diff = endTime.getTime() - startTime.getTime();
        return new BigDecimal(diff / nd + 1).setScale(0, RoundingMode.UP).intValue();
    }


    /**
     * 相差天数 不包括当天 2014-05-06 ~ 20140507 计算后是1天
     *
     * @param startTime
     * @param endTime
     * @param format
     * @return
     * @throws Exception
     */
    public static int dateDiff(String startTime, String endTime, String format) throws Exception {
        SimpleDateFormat sf = new SimpleDateFormat(format);
        double nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        double diff = sf.parse(endTime).getTime() - sf.parse(startTime).getTime();
        return new BigDecimal(new Double(diff / nd)).setScale(0, BigDecimal.ROUND_UP).intValue();
    }

    public static Date addDay(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }


    /**
     * 比较两个时间 时分秒 大小
     * @param s1
     * @param s2
     * @return
     */
    public static boolean compareTime(String s1,String s2){
        try {
            if (s1.indexOf(":")<0||s1.indexOf(":")<0) {
                return false;
            }else{
                String[]array1 = s1.split(":");
                int total1 = Integer.valueOf(array1[0])*3600+Integer.valueOf(array1[1])*60;
                String[]array2 = s2.split(":");
                int total2 = Integer.valueOf(array2[0])*3600+Integer.valueOf(array2[1])*60;
                return total1-total2>=0?true:false;
            }
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            return true;
        }
    }
}
