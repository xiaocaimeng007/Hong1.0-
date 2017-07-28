package com.hongbao5656.util;
import android.content.Context;
import android.os.CountDownTimer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 时间操作工具类 2013/11/23
 * @author huajun
 *
 */
public class TimeUtils {
	private static TimeUtils mInstance;

	public TimeUtils(Context mCtx) {
	}

	public static TimeUtils build(Context mCtx) {
		if (mInstance == null) {
			mInstance = new TimeUtils(mCtx);
		}
		return mInstance;
	}

	public void start(long millisInFuture, long countDownInterval,
					  final OnTimeFinshListener onFinish) {
		mInstance.startTimer(millisInFuture, countDownInterval, onFinish);
	}

	/**
	 * 定时器
	 * @param millisInFuture  截至时间
	 * @param countDownInterval 循环时间间隔
	 * @param onFinish
	 */
	private void startTimer(long millisInFuture, long countDownInterval,
							final OnTimeFinshListener onFinish) {
		new CountDownTimer(millisInFuture, countDownInterval) {

			@Override
			public void onTick(long millisUntilFinished) {
				if (onFinish == null) {
					return;
				}
				onFinish.onTick(millisUntilFinished);
			}

			@Override
			public void onFinish() {
				if (onFinish == null) {
					return;
				}
				onFinish.onFinish();
			}
		}.start();
	}

	/**
	 * 计时器监听(对外提供)
	 *
	 * @author huajun
	 *
	 */
	public interface OnTimeFinshListener {
		void onFinish();

		void onTick(long millisUntilFinished);
	}

	/**
	 *
	 * @param @param style 显示类型
	 * @param @param time long类型时间
	 * @return String 返回类型
	 */
	public static String getSureTime(String style, long time) {
		SimpleDateFormat sdf = new SimpleDateFormat(style);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
		return sdf.format(time);
	}

	/**
	 *
	 * @param @param style 显示类型
	 * @param @param time long类型时间
	 * @return String 返回类型
	 */
	public static String getSureTime2(String style, long time) {
		SimpleDateFormat sdf = new SimpleDateFormat(style);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
		return sdf.format(time);
	}

	public static Long getTime(String user_time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
		Date d;
		long l = 0l;
		try {
			d = sdf.parse(user_time);
			l = d.getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return l;
	}


	public static Long getTimeHG(String user_time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd日 HH:mm");
		Date d;
		long l = 0l;
		try {
			d = sdf.parse(user_time);
			l = d.getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return l;
	}

	// 将时间戳转为字符串
	public static String getStrTime(String cc_time) {
		String re_StrTime = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
		// 例如：cc_time=1291778220
		long lcc_time = Long.valueOf(cc_time);
		re_StrTime = sdf.format(new Date(lcc_time * 1000L));
		return re_StrTime;
	}

	public static long getUTCTime(){
		//1、取得本地时间：
		final java.util.Calendar cal = java.util.Calendar.getInstance();
		//2、取得时间偏移量：
		final int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
		//3、取得夏令时差：
		final int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
		//4、从本地时间里扣除这些差量，即可以取得UTC时间：
		cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
		return cal.getTimeInMillis();
	}


	public static String parseDate(String createTime){
		try {
			String ret = "";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			long create = sdf.parse(createTime).getTime();
			Calendar now = Calendar.getInstance();
			long ms  = 1000*(now.get(Calendar.HOUR_OF_DAY)*3600+now.get(Calendar.MINUTE)*60+now.get(Calendar.SECOND));//毫秒数
			long ms_now = now.getTimeInMillis();
			if(ms_now-create<ms){
				ret = "今天";
			}else if(ms_now-create<(ms+24*3600*1000)){
				ret = "昨天";
			}else if(ms_now-create<(ms+24*3600*1000*2)){
				ret = "前天";
			}else{
				ret= "更早";
			}
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * @param args
	 *//*
	public static void main(String[] args) {

		String time = formatDateTime("2013-08-13 15:41");
		System.out.println("time:"+time);
		time = formatDateTime("2013-08-12 15:45");
		System.out.println("time:"+time);
		time = formatDateTime("2013-08-11 15:43");
		System.out.println("time:"+time);
	}*/

	/**
	 * 格式化时间
	 * @param time
	 * @return
	 */
	public static String formatDateTime(String time) {
		SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
		if(time==null ||"".equals(time)){
			return "";
		}
		Date date = null;
		try {
			date = format.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Calendar current = Calendar.getInstance();

		Calendar today = Calendar.getInstance();	//今天

		today.set(Calendar.YEAR, current.get(Calendar.YEAR));
		today.set(Calendar.MONTH, current.get(Calendar.MONTH));
		today.set(Calendar.DAY_OF_MONTH,current.get(Calendar.DAY_OF_MONTH));
		//  Calendar.HOUR——12小时制的小时数 Calendar.HOUR_OF_DAY——24小时制的小时数
		today.set( Calendar.HOUR_OF_DAY, 0);
		today.set( Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);

		Calendar yesterday = Calendar.getInstance();	//昨天

		yesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));
		yesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));
		yesterday.set(Calendar.DAY_OF_MONTH,current.get(Calendar.DAY_OF_MONTH)-1);
		yesterday.set( Calendar.HOUR_OF_DAY, 0);
		yesterday.set( Calendar.MINUTE, 0);
		yesterday.set(Calendar.SECOND, 0);

		current.setTime(date);

		if(current.after(today)){
			return "今天 "+time.split(" ")[1];
		}else if(current.before(today) && current.after(yesterday)){

			return "昨天 "+time.split(" ")[1];
		}else{
			int index = time.indexOf("-")+1;
			return time.substring(index, time.length());
		}
	}



}
