package com.hongbao5656.util;

import android.content.Context;
import android.widget.Toast;

/**
 * ToastUtils
 *
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-12-9
 */
public class TU {

	private TU() {
		throw new AssertionError();
	}

	public static void show(Context context, int resId) {
		show(context, context.getResources().getText(resId), Toast.LENGTH_SHORT);
	}

//    public static void show(Context context, int resId, int duration) {
//        show(context, context.getResources().getText(resId), duration);
//    }

	public static void show(Context context, CharSequence text) {
		show(context, text, Toast.LENGTH_SHORT);
	}

//    public static void show(Context context, CharSequence text, int duration) {
//        Toast.makeText(context, text, duration).show();
//    }

	public static void show(Context context, int resId, Object... args) {
		show(context, String.format(context.getResources().getString(resId), args), Toast.LENGTH_SHORT);
	}

	public static void show(Context context, String format, Object... args) {
		show(context, String.format(format, args), Toast.LENGTH_SHORT);
	}

	public static void show(Context context, int resId, int duration, Object... args) {
		show(context, String.format(context.getResources().getString(resId), args), duration);
	}

	public static void show(Context context, String format, int duration, Object... args) {
		show(context, String.format(format, args), duration);
	}

	/**
	 * 短时间显示Toast
	 *
	 * @param context
	 * @param message
	 */
	public static void showShort(Context context, CharSequence message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 短时间显示Toast
	 *
	 * @param context
	 * @param message
	 */
	public static void showShort(Context context, int message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 长时间显示Toast
	 *
	 * @param context
	 * @param message
	 */
	public static void showLong(Context context, CharSequence message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

	/**
	 * 长时间显示Toast
	 *
	 * @param context
	 * @param message
	 */
	public static void showLong(Context context, int message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}


	private static Toast toast;

	/**
	 * 自定义显示Toast时间
	 *
	 * @param context
	 * @param message
	 * @param duration
	 */
	public static void show(Context context, CharSequence message, int duration) {
		if (toast == null){
			toast = Toast.makeText(context,message,duration);
		} else {
			toast.setText(message);
		}
		toast.show();
//		Toast.makeText(context, message, duration).show();
	}

	/**
	 * 自定义显示Toast时间
	 *
	 * @param context
	 * @param message
	 * @param duration
	 */
	public static void show(Context context, int message, int duration) {
		Toast.makeText(context, message, duration).show();
	}

}

