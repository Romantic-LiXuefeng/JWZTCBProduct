package com.jwzt.caibian.util;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import android.annotation.SuppressLint;

/**
 * 获取各种类型的日期处理类
 * 
 * @author hly
 * 
 */
@SuppressLint("SimpleDateFormat")
public class TimeUtil {

	/**
	 * 获得当天日期，显示格式为yyyy/MM/dd
	 * 
	 * @return
	 */
	public static String getDate() {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		String dateRel = format.format(date);
		return dateRel;
	}

	/**
	 * 获得当天日期，显示格式为yyyy-MM-dd2016-04-12 13:01:34
	 * 
	 * @return
	 */
	public static String getDate2() {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String dateRel = format.format(date);
		return dateRel;
	}

	/**
	 * 获取当前时间精确到时分秒yyyy-MM-dd HH:MM:SS
	 * 
	 * @return
	 */
	public static String getDate3() {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateRel = format.format(date);
		return dateRel;
	}
	
	/**
	 * 获取HH:mm yyyy/MM/dd格式
	 * @return
	 */
	public static String getDate4() {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("HH:mm yyyy/MM/dd");
		String dateRel = format.format(date);
		return dateRel;
	}
	
	/**
	 * 获取MM月dd日 HH:mm格式
	 * @return
	 */
	public static String getDate5() {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("MM月dd日 HH:mm");
		String dateRel = format.format(date);
		return dateRel;
	}
	
	/**
	 * 获取MM月dd日 HH:mm格式
	 * @return
	 */
	public static String getDate6() {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
		String dateRel = format.format(date);
		return dateRel;
	}
	
	/**
	 * 获取当前年份
	 * @return
	 */
	public static String getYear() {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy");
		String dateRel = format.format(date);
		return dateRel;
	}
	
	/**
	 * 获取当前时间精确到时分秒yyyy-MM-dd HH:MM:SS
	 * 
	 * @return
	 */
	public static String getDatelogin() {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String dateRel = format.format(date);
		return dateRel;
	}

	/**
	 * 获得当天日期，显示格式为yyyyMMdd
	 * 
	 * @param hour
	 *            :小时
	 * @param minute
	 *            :分钟
	 * @return
	 */
	public static String getDate3(String hour, String minute) {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String dateRel = format.format(date);
		return dateRel + hour + minute + "00";
	}

	/**
	 * 根据日期获得星期几,传的日期格式为yyyy-MM-dd
	 * 
	 * @param pTime
	 * @return
	 */
	public static String getWeek(String pTime) {
		String Week = "";
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Calendar c = Calendar.getInstance();
			c.setTime(format.parse(pTime));
			if (c.get(Calendar.DAY_OF_WEEK) == 1) {
				Week += "周日";
			}
			if (c.get(Calendar.DAY_OF_WEEK) == 2) {
				Week += "周一";
			}
			if (c.get(Calendar.DAY_OF_WEEK) == 3) {
				Week += "周二";
			}
			if (c.get(Calendar.DAY_OF_WEEK) == 4) {
				Week += "周三";
			}
			if (c.get(Calendar.DAY_OF_WEEK) == 5) {
				Week += "周四";
			}
			if (c.get(Calendar.DAY_OF_WEEK) == 6) {
				Week += "周五";
			}
			if (c.get(Calendar.DAY_OF_WEEK) == 7) {
				Week += "周六";
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return Week;
	}

	/**
	 * 根据日期获得星期几,传的日期格式为yyyy/MM/dd
	 * 
	 * @param pTime
	 * @return
	 */
	public static String getWeek1(String pTime) {
		String Week = "";
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
			Calendar c = Calendar.getInstance();
			c.setTime(format.parse(pTime));
			if (c.get(Calendar.DAY_OF_WEEK) == 1) {
				Week += "周日";
			}
			if (c.get(Calendar.DAY_OF_WEEK) == 2) {
				Week += "周一";
			}
			if (c.get(Calendar.DAY_OF_WEEK) == 3) {
				Week += "周二";
			}
			if (c.get(Calendar.DAY_OF_WEEK) == 4) {
				Week += "周三";
			}
			if (c.get(Calendar.DAY_OF_WEEK) == 5) {
				Week += "周四";
			}
			if (c.get(Calendar.DAY_OF_WEEK) == 6) {
				Week += "周五";
			}
			if (c.get(Calendar.DAY_OF_WEEK) == 7) {
				Week += "周六";
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return Week;
	}

	/**
	 * 获得昨天日期
	 * 
	 * @return
	 */
	public static String getYestadayDate() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd ");
		Calendar calendar = Calendar.getInstance();
		calendar.roll(Calendar.DAY_OF_YEAR, -1);
		String tomorrow = df.format(calendar.getTime());
		return tomorrow;
	}

	/**
	 * 获得明天日期
	 * 
	 * @return
	 */
	public static String getTomorrowDate() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd ");
		Calendar calendar = Calendar.getInstance();
		calendar.roll(Calendar.DAY_OF_YEAR, 1);
		String tomorrow = df.format(calendar.getTime());
		return tomorrow;
	}

	/**
	 * 获得后天日期
	 * 
	 * @return
	 */
	public static String getDayAfterTomorrow() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd ");
		Calendar calendar = Calendar.getInstance();
		calendar.roll(Calendar.DAY_OF_YEAR, 2);
		String DayAfterTomorrow = df.format(calendar.getTime());
		return DayAfterTomorrow;
	}
	
	/**
	 * 将传入的2017-05-09转成2017年5月9日
	 * @param time
	 * @return
	 */
	public static String getYMD(String time){
		String mouth,day;
		String[] times=time.split("-");
		if(times[1].startsWith("0")){
			mouth=times[1].substring(1);
		}else{
			mouth=times[1];
		}
		
		if(times[2].startsWith("0")){
			day=times[2].substring(1);
		}else{
			day=times[2];
		}
		
		return times[0]+"年"+mouth+"月"+day+"日";
	}
	
	/**
	 * 将传入的2017-05-09 16:38:25转成5月9日 16:38
	 * @param time
	 * @return
	 */
	public static String getMDHS(String time){
		String mouth,day,hour,seconds;
		String[] timesplit=time.split(" ");
		String[] times=timesplit[0].split("-");
		String[] hours=timesplit[1].split(":");
		
		if(times[1].startsWith("0")){
			mouth=times[1].substring(1);
		}else{
			mouth=times[1];
		}
		
		if(times[2].startsWith("0")){
			day=times[2].substring(1);
		}else{
			day=times[2];
		}
		
		if(hours[0].startsWith("0")){
			hour=hours[0].substring(1);
		}else{
			hour=hours[0];
		}
		
		if(hours[1].startsWith("0")){
			seconds=hours[1].substring(1);
		}else{
			seconds=hours[1];
		}
		
		return mouth+"/"+day+" "+hours[0]+":"+hours[1];
	}

	/**
	 * 将传入的2017-05-09 16:38:25转成5月9日 16:38
	 * @param time
	 * @return
	 */
	public static String getHSMD(String time){
		String mouth,day,hour,seconds;
		String[] timesplit=time.split(" ");
		String[] times=timesplit[0].split("-");
		String[] hours=timesplit[1].split(":");
		
		if(times[1].startsWith("0")){
			mouth=times[1].substring(1);
		}else{
			mouth=times[1];
		}
		
		if(times[2].startsWith("0")){
			day=times[2].substring(1);
		}else{
			day=times[2];
		}
		
		if(hours[0].startsWith("0")){
			hour=hours[0].substring(1);
		}else{
			hour=hours[0];
		}
		
		if(hours[1].startsWith("0")){
			seconds=hours[1].substring(1);
		}else{
			seconds=hours[1];
		}
		
		return hours[0]+":"+hours[1]+" "+times[1]+"/"+times[2];
	}
	
	/**
	 * 根据传入的日期判断是今天 明天 昨天
	 * 
	 * @param date
	 * @return 返回 今天 明天 昨天
	 */
	public static String getDay(String date) {
		String day = null;
		if (date.equals(getTomorrowDate())) {
			day = "明天";
		} else if (date.equals(getYestadayDate())) {
			day = "昨天";
		} else if (date.equals(getDate2())) {
			day = "今天";
		} else {
			day = date;
		}
		return day;
	}

	/**
	 * 获得星期几
	 * 
	 * @return
	 */
	public static String getWeek() {
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		String week = "";
		int position = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		if (position < 0)
			position = 0;

		switch (position) {
		case 0:
			week = "星期日";
			break;
		case 1:
			week = "星期一";
			break;
		case 2:
			week = "星期二";
			break;
		case 3:
			week = "星期三";
			break;
		case 4:
			week = "星期四";
			break;
		case 5:
			week = "星期五";
			break;
		case 6:
			week = "星期六";
			break;
		default:
			break;
		}
		return week;
	}

	/**
	 * 获得当前时间的小时
	 * 
	 * @return
	 */
	public static String getHour() {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("HH");
		String hour = format.format(date);
		return hour;
	}

	/**
	 * 获得当前时间的分钟
	 * 
	 * @return
	 */
	public static String getMinute() {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("mm");
		String minute = format.format(date);
		return minute;
	}

	/**
	 * 获得当前时间的秒数
	 * 
	 * @return
	 */
	public static String getSecond() {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("ss");
		String second = format.format(date);
		return second;
	}

	/**
	 * 获得当前时间的秒
	 * 
	 * @return
	 */
	public static String getMiao() {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("ss");
		String minute = format.format(date);
		return minute;
	}

	/**
	 * 根据日期或得之后的一周
	 * 
	 * @param date
	 * @return
	 */
	public static List<String> getAfterWeek(String date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-M-d");
		Date now = null;
		try {
			now = format.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);

		ArrayList<String> weeks = new ArrayList<String>();
		int position = calendar.get(Calendar.DAY_OF_WEEK) - 1;

		if (position < 0)
			position = 0;

		switch (position) {
		case 0:
			weeks.add("星期一");
			weeks.add("星期二");
			weeks.add("星期三");
			weeks.add("星期四");
			weeks.add("星期五");
			break;
		case 1:
			weeks.add("星期二");
			weeks.add("星期三");
			weeks.add("星期四");
			weeks.add("星期五");
			weeks.add("星期六");
			break;
		case 2:
			weeks.add("星期三");
			weeks.add("星期四");
			weeks.add("星期五");
			weeks.add("星期六");
			weeks.add("星期日");
			break;
		case 3:
			weeks.add("星期四");
			weeks.add("星期五");
			weeks.add("星期六");
			weeks.add("星期日");
			weeks.add("星期一");
			break;
		case 4:
			weeks.add("星期五");
			weeks.add("星期六");
			weeks.add("星期日");
			weeks.add("星期一");
			weeks.add("星期二");
			break;
		case 5:
			weeks.add("星期六");
			weeks.add("星期日");
			weeks.add("星期一");
			weeks.add("星期二");
			weeks.add("星期三");
			break;
		case 6:
			weeks.add("星期日");
			weeks.add("星期一");
			weeks.add("星期二");
			weeks.add("星期三");
			weeks.add("星期四");
			break;
		default:
			break;
		}
		return weeks;
	}

	@SuppressLint("SimpleDateFormat")
	public static String GetFormatTime(int time) {
		SimpleDateFormat sim = new SimpleDateFormat("hh:mm:ss");
		return sim.format(time);
	}

	/**
	 * 将传入的特定时间time与当前时间作比较，然后转换成类似“1分钟前”、“1小时前”“2天前”这样的形式，
	 * 如果当前时间与传入的时间差值超过5天直接返回“X月X日”来表示
	 * 
	 * @param time传入的时间字符串
	 *            ，注意必须是“xxxx-xx-xx
	 *            xx-xx-xx（年月日时分秒）”如"2016-04-12 13:01:34（24小时制）"这样的形式的字符串，
	 *            如果是其它表示形式的时间形式请自行修改此方法
	 * @return
	 * @author JinPengFei
	 * @throws ParseException
	 */
	public static String toAnotherForm(String time) throws ParseException {

		// String myTime="2016-04-12 13:01:34";
		String[] split = time.split(" ");// 用空格符号拆分字符串,得到2个数组

		String[] split_YMD = split[0].split("-");// 用"-"拆分字符串
		String[] split_HSS = split[1].split(":");// 用冒号拆分字符串
		StringBuffer buffer = new StringBuffer();

		for (int j = 0; j < split_YMD.length; j++) {
			buffer.append(split_YMD[j]);
		}
		for (int j = 0; j < split_HSS.length; j++) {
			buffer.append(split_HSS[j]);
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmm");

		long millionSeconds = sdf.parse(buffer.toString()).getTime();// 根据传入的时间得到相应的毫秒数

		long current = System.currentTimeMillis();// 得到当前的时刻毫秒数
		long diff = current - millionSeconds;// 计算当前时刻与传入的时间的差值，然后进行比较

		if (diff > 5 * 24 * 60 * 60 * 1000) {// 如果差值大于5天
			return split_YMD[1] + "月" + split_YMD[2] + "日";
		} else if (diff > 4 * 24 * 60 * 60 * 1000) {// 如果差值小于5天大于4天
			return "4天前";
		} else if (diff > 3 * 24 * 60 * 60 * 1000) {// 如果差值小于4天大于3天
			return "3天前";
		} else if (diff > 2 * 24 * 60 * 60 * 1000) {// 如果差值小于3天大于2天
			return "2天前";
		} else if (diff > 1 * 24 * 60 * 60 * 1000) {// 如果差值小于2天大于1天
			return "1天前";
		} else if (diff > 60 * 60 * 1000) {// 小于1天大于1小时
			return diff / (3600000) + "小时前";// 3600000=60*60*1000
		} else if (diff > 60 * 1000) {// 小于1小时大于1分钟
			return diff / 60000 + "分钟前";// 60000=60*1000；
		} else {
			return "1分钟前";
		}

	}

	/**
	 * 根据传入的具体时间返回一个毫秒的数值
	 * 
	 * @param hour小时
	 * @param minute分钟
	 * @return 毫秒值
	 */
	public static int getMillSecondsByTime(String hour, String minute) {
		int mHour = Integer.parseInt(hour);
		int mMinute = Integer.parseInt(minute);

		System.out.println("小时" + mHour + ",分钟" + mMinute);
		return 0;

	}

	/**
	 * 
	 * @param 要转换的毫秒数
	 * @return 该毫秒数转换为 * days * hours * minutes * seconds 后的格式
	 * @author fy.zhang
	 */
	public static String formatDuring(long mss) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		// 毫秒转日期
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(mss);
		Date date = c.getTime();
		System.out.println(sdf.format(date));
		return sdf.format(date);
	}

	/**
	 * 传入时分秒，计算出距离下一周该时间点还剩的秒数
	 * 
	 * @param hour
	 * @param miut
	 * @param miao
	 * @return
	 */
	public static int getToNextMiao2(int weeks, int hour, int miut, int miao) {
		int dayWeek = 0;// 当前天的星期几
		int toWeek = 0;// 响铃天的星期几
		int day = 0;// 距离响铃的天数
		int toHour = 0;// 距离下次响铃的小时
		int toMinue = 0;// 距离响铃的分钟
		int toMiao = 0;// 距离响铃的秒数
		toWeek = weeks;
		/*
		 * if(weeks.equals("星期一")){ toWeek=1; }else if(weeks.equals("星期二")){
		 * toWeek=2; }else if(weeks.equals("星期三")){ toWeek=3; }else
		 * if(weeks.equals("星期四")){ toWeek=4; }else if(weeks.equals("星期五")){
		 * toWeek=5; }else if(weeks.equals("星期六")){ toWeek=6; }else
		 * if(weeks.equals("星期日")){ toWeek=7; }
		 */

		// 获得今日为星期几
		String week = getWeek(getDate2());
		if (week.equals("周一")) {
			dayWeek = 1;
		} else if (week.equals("周二")) {
			dayWeek = 2;
		} else if (week.equals("周三")) {
			dayWeek = 3;
		} else if (week.equals("周四")) {
			dayWeek = 4;
		} else if (week.equals("周五")) {
			dayWeek = 5;
		} else if (week.equals("周六")) {
			dayWeek = 6;
		} else if (week.equals("周日")) {
			dayWeek = 7;
		}

		if (dayWeek < toWeek) {
			day = toWeek - dayWeek - 1;
		} else if (dayWeek > toWeek) {
			day = (7 - dayWeek) + toWeek - 1;
		} else if (dayWeek == toWeek) {
			day = 0;
		}

		if (Integer.valueOf(getHour()) < hour) {
			toHour = hour - Integer.valueOf(getHour()) - 1;
		} else if (Integer.valueOf(getHour()) > hour) {
			toHour = (24 - Integer.valueOf(getHour())) + hour - 1;
		} else if (Integer.valueOf(getHour()) == hour) {
			toHour = 0;
		}

		if (Integer.valueOf(getMinute()) < miut) {
			toMinue = miut - Integer.valueOf(getMinute()) - 1;
		} else if (Integer.valueOf(getMinute()) > miut) {
			toMinue = (60 - Integer.valueOf(getMinute())) + miut - 1;
		} else if (Integer.valueOf(getMinute()) == miut) {
			toMinue = 0;
		}

		if (Integer.valueOf(getMiao()) < miao) {
			toMiao = miao - Integer.valueOf(getMiao());
		} else if (Integer.valueOf(getMiao()) > miao) {
			toMiao = 60 - Integer.valueOf(getMiao()) + miao;
		}

		// 计算距离下一个该星期几的天数为6天转换成秒
		int zhengtian = day * 3600 * 24;
		// 计算当天已过去的小时
		int hourMiao = toHour * 3600;
		// 已过去的分钟
		int minesMiao = toMinue * 60;

		return zhengtian + hourMiao + minesMiao + toMiao;
	}

	/**
	 * 传入时分秒，计算出距离下一周该时间点还剩的秒数
	 * 
	 * @param hour
	 * @param miut
	 * @param miao
	 * @return
	 */
	public static int getToNextMiao(String weeks, int hour, int miut, int miao) {
		int dayWeek = 0;// 当前天的星期几
		int toWeek = 0;// 响铃天的星期几
		int day = 0;// 距离响铃的天数
		int toHour = 0;// 距离下次响铃的小时
		int toMinue = 0;// 距离响铃的分钟
		int toMiao = 0;// 距离响铃的秒数
		if (weeks.equals("星期一")) {
			toWeek = 1;
		} else if (weeks.equals("星期二")) {
			toWeek = 2;
		} else if (weeks.equals("星期三")) {
			toWeek = 3;
		} else if (weeks.equals("星期四")) {
			toWeek = 4;
		} else if (weeks.equals("星期五")) {
			toWeek = 5;
		} else if (weeks.equals("星期六")) {
			toWeek = 6;
		} else if (weeks.equals("星期日")) {
			toWeek = 7;
		}

		// 获得今日为星期几
		String week = getWeek(getDate2());
		if (week.equals("周一")) {
			dayWeek = 1;
		} else if (week.equals("周二")) {
			dayWeek = 2;
		} else if (week.equals("周三")) {
			dayWeek = 3;
		} else if (week.equals("周四")) {
			dayWeek = 4;
		} else if (week.equals("周五")) {
			dayWeek = 5;
		} else if (week.equals("周六")) {
			dayWeek = 6;
		} else if (week.equals("周日")) {
			dayWeek = 7;
		}

		if (dayWeek < toWeek) {
			day = toWeek - dayWeek - 1;
		} else if (dayWeek > toWeek) {
			day = (7 - dayWeek) + toWeek - 1;
		} else if (dayWeek == toWeek) {
			day = 0;
		}

		if (Integer.valueOf(getHour()) < hour) {
			toHour = hour - Integer.valueOf(getHour()) - 1;
		} else if (Integer.valueOf(getHour()) > hour) {
			toHour = (24 - Integer.valueOf(getHour())) + hour - 1;
		} else if (Integer.valueOf(getHour()) == hour) {
			toHour = 0;
		}

		if (Integer.valueOf(getMinute()) < miut) {
			toMinue = miut - Integer.valueOf(getMinute()) - 1;
		} else if (Integer.valueOf(getMinute()) > miut) {
			toMinue = (60 - Integer.valueOf(getMinute())) + miut - 1;
		} else if (Integer.valueOf(getMinute()) == miut) {
			toMinue = 0;
		}

		if (Integer.valueOf(getMiao()) < miao) {
			toMiao = miao - Integer.valueOf(getMiao());
		} else if (Integer.valueOf(getMiao()) > miao) {
			toMiao = 60 - Integer.valueOf(getMiao()) + miao;
		}

		// 计算距离下一个该星期几的天数为6天转换成秒
		int zhengtian = day * 3600 * 24;
		// 计算当天已过去的小时
		int hourMiao = toHour * 3600;
		// 已过去的分钟
		int minesMiao = toMinue * 60;

		return zhengtian + hourMiao + minesMiao + toMiao;
	}

	public static long getMillSeconds(String hour, String minute)
			throws ParseException {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

		String dateRel = format.format(date);
		System.out.println("日期" + dateRel);
		dateRel += hour + minute + "00";
		System.out.println("测试" + dateRel);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmm");

		long millionSeconds = sdf.parse(dateRel).getTime();

		System.out.println("得到的毫秒值" + millionSeconds);
		System.out.println("差值======"+ (millionSeconds - System.currentTimeMillis()));
		return millionSeconds - System.currentTimeMillis();
	}

	/**
	 * 计算距离给定的时间还有多少秒
	 * 
	 * @param hour小时字符串
	 * @param minute分钟字符串
	 */
	public static int calculateSeconds(String hour, String minute) {
		int mHour = Integer.parseInt(hour);
		int mMinute = Integer.parseInt(minute);

		int givnMinute = mHour * 60 + mMinute;// 转换成分钟
		int currentMinute = Integer.parseInt(getHour()) * 60
				+ Integer.parseInt(getMinute());// 转换成分钟

		return (givnMinute - currentMinute) * 60
				- Integer.parseInt(getSecond());
	}

	/**
	 * 把重复模式转为数字,1代表“仅一次”，2代表“每天”，3代表“工作日”
	 * 
	 * @param day
	 * @return
	 */
	public static int dayToNumber(String day) {
		/*
		 * if("星期一".equals(day)){ return 1; }else if("星期二".equals(day)){ return
		 * 2; }else if("星期三".equals(day)){ return 3; }else
		 * if("星期四".equals(day)){ return 4; }else if("星期五".equals(day)){ return
		 * 5;
		 * 
		 * }else if("星期六".equals(day)){ return 6; }else if("星期日".equals(day)){
		 * return 7; }
		 */
		if ("仅一次".equals(day)) {
			return 1;
		} else if ("每天".equals(day)) {
			return 2;
		} else {
			return 3;
		}

	}

	/**
	 * 将秒数转换成分钟
	 * 
	 * @param seccond
	 * @return
	 */
	public static String getTime(int seccond) {
		String time;
		int miao;
		int fen;
		if (seccond >= 60) {
			miao = seccond % 60;
			fen = seccond / 60;
			if (miao < 10) {
				time = fen + "'0" + miao + "″";
			} else {
				time = fen + "'" + miao + "″";
			}
		} else {
			time = seccond + "″";
		}
		return time;
	}

	/**
	 * 将秒数转换成分钟
	 * 
	 * @param seccond
	 * @return
	 */
	public static String getTimeSecond(int seccond) {
		System.out.println("seccond" + seccond);
		String time;
		int miao;
		int fen;
		if (seccond >= 60) {
			miao = seccond % 60;
			fen = seccond / 60;
			if (miao < 10) {
				if (fen < 10) {
					time = "0" + fen + ":0" + miao + "";
				} else {
					time = fen + ":" + "0" + miao + "";
				}
			} else {
				if (fen < 10) {
					time = "0" + fen + ":" + miao + "";
				} else {
					time = fen + ":" + miao + "";
				}

			}
		} else {
			if (seccond < 10) {
				time = "00:" + "0" + seccond + "";

			} else {

				time = "00:" + seccond + "";
			}
		}
		return time;
	}

	/**
	 * 根据重复提醒的模式算出距离下次提醒的秒数
	 * 
	 * @param repeat
	 *            1代表仅一次，2代表每天，3代表工作日
	 * @param hour
	 * @param minute
	 * @return
	 */
	public static int getRepeatSeconds(int repeat, int hour, int minute) {
		return minute;

	}

	/**
	 * 获得第一次提醒的时间
	 * 
	 * @param repeat重复模式
	 *            ，1代表仅一次，2代表每天，3代表工作日
	 * @param hour
	 *            小时
	 * @param minute
	 *            分钟
	 * @return 秒数
	 */
	@SuppressWarnings("deprecation")
	public static int getFirstSeconds(int repeat, int hour, int minute) {
		Date date = new Date();
		date.setHours(hour);
		date.setMinutes(minute);
		// 把秒值设置为0
		date.setSeconds(0);
		// 得到传入的时间对应的毫秒数
		long given_time = date.getTime();
		// 当前时间对应的毫秒数
		long currentTimeMillis = System.currentTimeMillis();
		if (repeat == 1 || repeat == 2) {
			if (given_time < currentTimeMillis) {// 说明时间已过，需要到才能明天提醒
				return (int) (60 * 60 * 24 - (currentTimeMillis - given_time) * 0.001);
			} else {// 提醒的时间还没到
				return (int) ((given_time - currentTimeMillis) * 0.001);// 转化为秒
			}
		} else if (repeat == 3) {// 工作日
			String week = getWeek();
			if ("星期五".equals(week)) {
				if (given_time < currentTimeMillis) {// 说明时间已过,需要到3天之后的周一才提醒
					return (int) (60 * 60 * 24 * 3 - (currentTimeMillis - given_time) * 0.001);
				} else {// 提醒的时间还没到
					return (int) ((given_time - currentTimeMillis) * 0.001);
				}
			} else if ("星期六".equals(getWeek())) {// 星期六，两天之后提醒

				return (int) (60 * 60 * 24 * 2 + (given_time - currentTimeMillis) * 0.001);
			} else if ("星期日".equals(getWeek())) {// 星期日，一天之后提醒

				return (int) (60 * 60 * 24 + (given_time - currentTimeMillis) * 0.001);
			} else {
				if (given_time < currentTimeMillis) {// 说明时间已过，需要到才能明天提醒
					return (int) (60 * 60 * 24 - (currentTimeMillis - given_time) * 0.001);
				} else {// 提醒的时间还没到
					return (int) ((given_time - currentTimeMillis) * 0.001);// 转化为秒
				}
			}
		}
		return 0;

	}

	/**
	 * 将传入的特定时间time与当前时间作比较，然后转换成类似1-5分钟为“刚刚”，
	 * 5-59分钟之前，1-23小时之前，1-30天之前，1-5个月前，显示具体时间 年月日这样的形式，
	 * 如果当前时间与传入的时间差值超过5个月直接返回“XXXX年X月X日”来表示
	 * 
	 * @param time传入的时间字符串
	 *            ，注意必须是“xxxx-xx-xx
	 *            xx-xx-xx（年月日时分秒）”如"2016-04-12 13:01:34（24小时制）"这样的形式的字符串，
	 *            如果是其它表示形式的时间形式请自行修改此方法
	 * @return
	 * @author JinPengFei
	 * @throws ParseException
	 */
	public static String toOtherForm(String time) throws ParseException {

		// String myTime="2016-04-12 13:01:34";
		String[] split = time.split(" ");// 用空格符号拆分字符串,得到2个数组

		String[] split_YMD = split[0].split("-");// 用"-"拆分字符串
		String[] split_HSS = split[1].split(":");// 用冒号拆分字符串
		StringBuffer buffer = new StringBuffer();

		for (int j = 0; j < split_YMD.length; j++) {
			buffer.append(split_YMD[j]);
		}
		for (int j = 0; j < split_HSS.length; j++) {
			buffer.append(split_HSS[j]);
		}
		long current = System.currentTimeMillis();// 得到当前的时刻毫秒数
		// 先除以1000，免得后续操作超出long的取值范围
		current = current / 1000;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		// 根据传入的时间得到相应的毫秒数
		long millionSeconds = sdf.parse(buffer.toString()).getTime();
		// 先除以1000，免得后续操作超出long的取值范围
		millionSeconds = millionSeconds / 1000;
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 6);

		// 得到6个月前的毫秒数
		long sixMonthAgo = calendar.getTimeInMillis();
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 5);
		// 得到一个月前对应的毫秒数
		long oneMonthAgo = calendar.getTimeInMillis();

		// 计算当前时间与传入的时间的差值，单位为秒
		long diff = current - millionSeconds;

		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 4);
		// 得到5个月之前的时间毫秒
		long fiveMonthAgo = calendar.getTimeInMillis();

		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
		// 得到4个月之前的毫秒数
		long fourMonthAgo = calendar.getTimeInMillis();
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
		// 得到3个月之前的毫秒数
		long threeMonthAgo = calendar.getTimeInMillis();
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
		// 得到2个月之前的毫秒数
		long twoMonthAgo = calendar.getTimeInMillis();

		if (millionSeconds > current - 60 * 5) {
			return "刚刚";
		} else if (millionSeconds > current - 60 * 60) {// 小于1小时大于5分钟
			for (int i = 59; i >= 5; i--) {
				if (diff > 60 * i) {
					return i + "分钟前";
				}
			}
		} else if (millionSeconds > current - 60 * 60 * 24) {// 小于1天大于1小时
			for (int i = 23; i > 0; i--) {
				if (diff > 60 * 60 * i) {
					return i + "小时前";
				}
			}
		} else if (millionSeconds > current - 60 * 60 * 24 * 30) {// 大于一天小于30天

			for (int i = 1; i <= 30; i++) {
				if (diff < 60 * 60 * 24 * (i+1)) {
					return i + "天前";
				}
			}
		} else if (millionSeconds > twoMonthAgo / 1000) {// 大于一个月小于2个月
			return "1个月前";
		} else if (millionSeconds > threeMonthAgo / 1000) {// 大于2个月小于3个月
			return "2个月前";
		} else if (millionSeconds > fourMonthAgo / 1000) {// 大于3个月小于4个月
			return "3个月前";
		} else if (millionSeconds > fiveMonthAgo / 1000) {// 大于4个月小于5个月
			return "4个月前";
		} else if (millionSeconds > sixMonthAgo / 1000) {// 大于5个月小于6个月
			return "5个月前";
		}
		// 直接返回日期
		return split_YMD[0] + "年" + split_YMD[1] + "月" + split_YMD[2] + "日";
	}

	private static long minute = 1000 * 60;
	private static long hour = minute * 60;
	private static long day = hour * 24;
//	private static long halfamonth = day * 15;
	private static long month = day * 30;

	/**
	 * 转换日期格式,返回刚刚，N分钟前 N小时前 N天前 N月前
	 * @param datetime
	 * @return
	 */
	public static String getDateDiff(String datetime) {
		String result;
		try {
			String[] split = datetime.split(" ");// 用空格符号拆分字符串,得到2个数组
			String[] split_YMD = split[0].split("-");// 用"-"拆分字符串
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			long  millionSeconds = sdf.parse(datetime).getTime();
			long now = new Date().getTime();
			long diffValue = now - millionSeconds;
			if (diffValue < 0) {
				// toast("结束日期不能小于开始日期！");
			}
			long monthC = diffValue / month;
			long weekC = diffValue / (7 * day);
			long dayC = diffValue / day;
			long hourC = diffValue / hour;
			long minC = diffValue / minute;
			if (monthC >= 1&&monthC <= 5) {
				result =Integer.parseInt(monthC + "") + "个月前";
				return result;
			} else if (weekC >= 1) {
				result =Integer.parseInt(weekC + "") + "周前";
				return result;
			} else if (dayC >= 1) {
				result =Integer.parseInt(dayC + "") + "天前";
				return result;
			} else if (hourC >= 1) {
				result =Integer.parseInt(hourC + "") + "小时前";
				return result;
			} else if (minC >= 5) {
				result =Integer.parseInt(minC + "") + "分钟前";
				return result;
			} else if(minC <= 5){
				result = "刚刚";
				return result;
			}else{
				result = split_YMD[0]+"年"+split_YMD[1]+"月"+split_YMD[2]+"日";
				return result;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String getDateDiff1(String datetime) {
		String result;
		try {
			String[] split = datetime.split(" ");// 用空格符号拆分字符串,得到2个数组
			String[] split_YMD = split[0].split("-");// 用"-"拆分字符串
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			long  millionSeconds = sdf.parse(datetime).getTime();
			long now = new Date().getTime();
			long diffValue = now - millionSeconds;
			if (diffValue < 0) {
				// toast("结束日期不能小于开始日期！");
			}
			long monthC = diffValue / month;
			long weekC = diffValue / (7 * day);
			long dayC = diffValue / day;
			long hourC = diffValue / hour;
			long minC = diffValue / minute;
			if (monthC >= 1&&monthC <= 5) {
				result =Integer.parseInt(monthC + "") + "个月前";
				return result;
			} else if (weekC >= 1) {
				result =Integer.parseInt(weekC + "") + "周前";
				return result;
			} else if (dayC >= 1) {
				result =Integer.parseInt(dayC + "") + "天前";
				return result;
			} else if (hourC >= 1) {
				result =Integer.parseInt(hourC + "") + "小时前";
				return result;
			} else if (minC >= 5) {
				result =Integer.parseInt(minC + "") + "分钟前";
				return result;
			} else if(minC <= 5){
				result = "刚刚";
				return result;
			}else{
				result = split_YMD[1]+"-"+split_YMD[2]+" "+split[1];
				return result;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 计算出输入的时间和当前的时间差
	 * @param dataTime 传入时间，统一为yyyy-MM-dd HH:MM:SS格式
	 * @return
	 */
	public static long getTimeDifference(String dataTime){
		long shicha = 0;
		//获得当前的时间
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String dateRel = format.format(date)+" "+getHour()+":"+getMinute()+":"+getMiao();
		
		DateFormat df = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		try {
			Date d1 = df.parse(dataTime);
			Date d2 = df.parse(dateRel);
			if(timeCompare(dataTime)){//表示传入的时间大于当前时间
				shicha=d1.getTime()-d2.getTime();
			}else{
				shicha=d2.getTime()-d1.getTime();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return shicha;
	} 
	
	/**
	 * 将毫秒转换成时分秒的形式
	 * @param miao
	 * @return
	 */
	public static String getHMS(long haomiao){
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");//初始化Formatter的转换格式。  
		formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));//不加这句时如果小于1小时会出现小时位显示08字样 
		String hms = formatter.format(haomiao); 
		return hms;
	}

	/**
	 * 将毫秒转换成时分秒的形式
	 * @param miao
	 * @return
	 */
	public static String getYMDHMS(long haomiao){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//初始化Formatter的转换格式。
		formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));//不加这句时如果小于1小时会出现小时位显示08字样
		String hms = formatter.format(haomiao);
		return hms;
	}
	
	/**
	 * 判断输入的时间和当前时间的大小 true表示输入的时间比当前时间大，false表示输入的时间比当前时间小
	 * @param time
	 * @return
	 */
	public static boolean timeCompare(String time){
		boolean isCompare = false;
//		String s1="2008-01-25 09:12:09";
		//获得当前的时间
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String dateRel = format.format(date)+" "+getHour()+":"+getMinute()+":"+getMiao();
		
		DateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar c1= Calendar.getInstance();
		Calendar c2= Calendar.getInstance();
		try{
			c1.setTime(df.parse(time));
			c2.setTime(df.parse(dateRel));
		}catch(ParseException e){
			System.err.println("格式不正确");
		}
		int result=c1.compareTo(c2);
//		if(result==0){
//			System.out.println("c1相等c2");
//		}else 
		if(result>0){
			isCompare=true;
			System.out.println("c1大于c2");
		}else{
			isCompare=false;
			System.out.println("c1小于c2");
		}
		
		return isCompare;
	}
	/**
	   * 将短时间格式字符串转换为时间 yyyy-MM-dd 
	   * 
	   * @param strDate
	   * @return
	   */
	public static Date strToDate(String strDate) {
	   SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	   ParsePosition pos = new ParsePosition(0);
	   Date strtodate = formatter.parse(strDate, pos);
	   return strtodate;
	}
}
