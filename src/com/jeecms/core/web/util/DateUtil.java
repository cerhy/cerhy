/**
 * @{#} DateUtil.java Created on 2006-10-18 上午10:35:08
 *
 * Copyright (c) 2006 by WASU.
 */
package com.jeecms.core.web.util;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 * 
 * @author <a href="mailto:chengj@onewaveinc.com">wilson</a>
 * @version 1.0
 */
public class DateUtil {
	/**
	 * 定义一天的毫妙数
	 */
	public static final long MILLSECOND_OF_DAY = 86400000;

	/**
	 * 时间格式化字符串
	 */
	public static final String time24Formatter = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 格式化日期
	 * 
	 * @param strDate
	 *            符合格式的字符串
	 * @return 格式后的日期
	 */
	public static Date parser(String strDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return sdf.parse(strDate);
		} catch (Exception e) {
			return null;
		}
	}
	public static String  stringYM(Date strDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
	  return sdf.format(strDate);
	}
	public static String  stringY(Date strDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
	  return sdf.format(strDate);
	}


	public static Date parser(String strDate, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			return sdf.parse(strDate);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 格式化日期
	 * 
	 * @param strDate
	 *            符合格式的字符串
	 * @return 格式后的日期
	 */
	public static Date parser(Date testDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(testDate);
		calendar.clear(Calendar.MILLISECOND);
		calendar.clear(Calendar.SECOND);
		calendar.clear(Calendar.MINUTE);
		calendar.clear(Calendar.HOUR_OF_DAY);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return sdf.parse(sdf.format(calendar.getTime()));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 格式化日期精确到秒
	 * 
	 * @param strDate
	 *            符合格式的字符串
	 * @return 格式后的日期
	 */
	public static Date parserToSecond(Date testDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(testDate);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return sdf.parse(sdf.format(calendar.getTime()));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 格式化日期
	 * 
	 * @param strDate
	 *            符合格式的字符串
	 * @return 格式后的日期
	 */
	public static Date parserTo(String strDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return sdf.parse(strDate);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 得到当前月份的周期开始日期
	 * 
	 * @param currentDate
	 *            当前日期
	 * @return 当前月份的周期开始日期
	 */
	public static Date getCurBeginCycleDate(Date currentDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);

		String year = "" + calendar.get(Calendar.YEAR);
		String month = (calendar.get(Calendar.MONTH) + 1) + "";
		if (month.length() < 2) {
			month = "0" + month;
		}
		String dateStr = year + "-" + month + "-01 00:00:00";
		return DateUtil.parser(dateStr);
	}

	/**
	 * 取得当前日期的最后一秒 yyyy-MM-dd 23:59:59
	 * 
	 * @param currentDate
	 * @return
	 */
	// addby zhouxh 20071214
	public static Date getCurrEnd(Date currentDate) {
		currentDate.setHours(23);
		currentDate.setMinutes(59);
		currentDate.setSeconds(59);
		return currentDate;
	}

	/**
	 * 取得当前周期的周期结束日期
	 * 
	 * @param currentDate
	 *            当前日期
	 * @return 当前周期的周期结束日期
	 */
	public static Date getCurEndCycleDate(Date currentDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);

		String year = "" + calendar.get(Calendar.YEAR);
		String month = (calendar.get(Calendar.MONTH) + 2) + "";
		if (month.length() < 2) {
			month = "0" + month;
		}
		String dateStr = year + "-" + month + "-01 23:59:59";
		calendar.setTime(DateUtil.parser(dateStr));
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		return calendar.getTime();
	}

	/**
	 * 得到下nextCycle周期的月份
	 * 
	 * @param currentDate
	 *            当前日期
	 * @param nextCycle
	 *            下nextCycle周期
	 * @return 下nextCycle周期
	 */
	public static Date getNextCycleDate(Date currentDate, long nextCycle) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);

		String year = "" + calendar.get(Calendar.YEAR);
		nextCycle++;
		String month = (calendar.get(Calendar.MONTH) + nextCycle) + "";
		if (month.length() < 2) {
			month = "0" + month;
		}
		String dateStr = year + "-" + month + "-01 00:00:00";
		return DateUtil.parser(dateStr);
	}

	/**
	 * 获取开始和结束日期之间的间隔日期
	 * 
	 * @param startDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @param roundingMode
	 *            舍入方式 见 BigDecimal的定义
	 * @return 相隔的日期数
	 */
	public static long getDaysBetweenDate(Date startDate, Date endDate,
			int roundingMode) {

		BigDecimal bStart = new BigDecimal(startDate.getTime());
		BigDecimal bEnd = new BigDecimal(endDate.getTime());
		BigDecimal bUnit = new BigDecimal(MILLSECOND_OF_DAY);
		return (bEnd.subtract(bStart)).divide(bUnit, roundingMode).longValue();
	}

	/**
	 * 获取开始和结束日期之间的间隔日期
	 * 
	 * @param startDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @return 相隔的日期数
	 */
	public static long getDaysBetweenDateWithoutTime(Date startDate,
			Date endDate) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(startDate);
		cal2.setTime(endDate);

		cal1.clear(Calendar.MILLISECOND);
		cal1.clear(Calendar.SECOND);
		cal1.clear(Calendar.MINUTE);
		cal1.clear(Calendar.HOUR_OF_DAY);

		cal2.clear(Calendar.MILLISECOND);
		cal2.clear(Calendar.SECOND);
		cal2.clear(Calendar.MINUTE);
		cal2.clear(Calendar.HOUR_OF_DAY);

		return (cal2.getTime().getTime() - cal1.getTime().getTime())
				/ (24 * 60 * 60 * 1000);
	}

	/**
	 * 
	 * @param date
	 * @return
	 */
	public static Date getTomorrowDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}

	/**
	 * 获取两个日期之间相差的月份数
	 * 
	 * @param startDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @param flag
	 *            false 为全月舍
	 * @return 返回的月份数
	 */
	public static long getMonthsBetweenDate(Date startDate, Date endDate,
			boolean flag) {
		Calendar cal1 = Calendar.getInstance();

		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(startDate);
		cal2.setTime(endDate);
		if (endDate.before(startDate)) {
			cal1.setTime(endDate);
			cal2.setTime(startDate);
		}

		cal1.clear(Calendar.MILLISECOND);
		cal1.clear(Calendar.SECOND);
		cal1.clear(Calendar.MINUTE);
		cal1.clear(Calendar.HOUR_OF_DAY);

		cal2.clear(Calendar.MILLISECOND);
		cal2.clear(Calendar.SECOND);
		cal2.clear(Calendar.MINUTE);
		cal2.clear(Calendar.HOUR_OF_DAY);

		return getMonthsBetweenDate(cal1, cal2, flag);

	}

	/**
	 * 获取两个日期之间相差的月份数
	 * 
	 * @param cal1
	 *            开始日期
	 * @param cal2
	 *            结束日期
	 * @param flag
	 *            false 为全月舍
	 * @return 返回的月份数
	 */
	public static long getMonthsBetweenDate(Calendar cal1, Calendar cal2,
			boolean flag) {
		long month = 0L;
		while (cal1.before(cal2)) {
			cal1.add(Calendar.MONTH, 1);
			month++;
			if (flag) {
				if ((cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH))
						&& (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR))
						&& (cal1.get(Calendar.DAY_OF_MONTH) > cal2
								.get(Calendar.DAY_OF_MONTH))) {
					month--;
					break;
				}
				if ((cal1.get(Calendar.MONTH) > cal2.get(Calendar.MONTH))
						&& (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR))

				) {
					month--;
					break;
				}
			}
		}
		return month;
	}

	/**
	 * 
	 * @param date
	 * @param field
	 * @return
	 */
	public static long getDateField(Date date, int field) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		if (field == Calendar.MONTH)
			return cal.get(field) + 1;
		else
			return cal.get(field);

	}

	/**
	 * 
	 * @param date
	 * @param field
	 * @return
	 */
	public static Date getNextDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, 1);
		return cal.getTime();

	}

	public static Date getAfterDate(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days);
		return cal.getTime();
	}

	/**
	 * 当前的日期 date months月数
	 * 
	 * @param date
	 * @param months
	 * @return
	 */
	public static Date getAfterMonth(Date date, int months) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, months);
		return cal.getTime();
	}

	/**
	 * 计算时间差
	 * 
	 * @param endDate
	 * @param beginDate
	 * @return
	 */
	public static int diffDate(Date endDate, Date beginDate) {
		return (int) ((endDate.getTime() - beginDate.getTime()) / (24 * 60 * 60 * 1000));
	}

	/**
	 * 根据传入的日期，欲转换成的日期样式，返回转换后的日期字符串
	 * 
	 * @param ldate
	 *            - 日期
	 * @param pattern
	 *            - 具体的值范围见java.text.SimpleDateFormat的说明
	 * @see java.text.SimpleDateFormat
	 * @return String 转换后的日期字符串
	 */
	public static String format(Date ldate, String pattern) {

		DateFormat dateFormat = new SimpleDateFormat(pattern);
		return dateFormat.format(ldate);
	}

	public static void main22(String[] args) {
		/*
		 * Calendar cal1 = Calendar.getInstance(); Calendar cal2 =
		 * Calendar.getInstance(); cal1.set(2006, 11, 13, 10, 10);
		 * cal2.set(2006, 6, 18, 10, 30); //
		 * System.out.println(DateUtil.getMonthsBetweenDate(cal1,cal2,false));
		 * System.out.println(DateUtil.parser(cal1.getTime()));
		 * /*System.out.println(DateUtil.getDateField(new Date(),
		 * Calendar.YEAR)); System.out.println(DateUtil.getDateField(new Date(),
		 * Calendar.DATE)); System.out.println(DateUtil.getDateField(new Date(),
		 * Calendar.MONTH));
		 */
		// System.out.println(DateUtil.getTomorrowDate(new Date()));
		Date date = DateUtil.getNextDate(new Date());
		System.out.println(date);
		Date date2 = DateUtil.getNextDate(date);
		System.out.println(date2);

	}

	public static void main(String[] args) {
		/*
		 * Date date = DateUtil.getNextDate(new Date());
		 * System.out.println(date); Date date2 = DateUtil.getNextDate(date);
		 * System.out.println(date2); Date date3 = DateUtil.getAfterDate((new
		 * Date(),2); System.out.println(date3);
		 */

	}

}
