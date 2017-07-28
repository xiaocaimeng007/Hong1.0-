package com.hongbao5656.util;
import android.content.Context;
import android.content.SharedPreferences;
/**
 * 偏好设置工具类
 */
public class SPU {
	public static final String STORE_USERINFO = "user_Info";//储存用户信息的sp文件名
	public static final String STORE_SETTINGS = "settings";//存储程序信息的sp文件名
	public static final String EQ_Tel = "eq_Tel";
	public static final String startcarlen = "startcarlen";
	public static final String endcarlen = "endcarlen";
	public static final String USERNAME = "username";//1.5.1之前可能有问题
	public static final String USERNAMENEW = "USERNAMENEW";//1.5.1之前可能有问题  现在修改
	public static final String USERNAME2 = "username2";
	public static final String CityToNameID = "CityToNameID";
	public static final String CityFromNameID = "CityFromNameID";
	public static final String UER_TYPE = "uer_type";
	public static final String ACCOUNT = "user_name";
	public static final String UER_CHECK_STATE = "user_check_state";
	public static final String EQ_tokenId = "eq_tokenId";
	public static final String PWD = "PWD";
	public static final String PWD2 = "PWD2";
	public static final String CityFromName = "CityFromName";
	public static final String CityToName = "CityToName";
	public static final String NEW_LAST_LONGITUDE = "longitude";// 经度
	public static final String NEW_LAST_LATITUDE = "latitude";// 纬度
	public static final String CURRENTCITY = "currentcity";// 当前城市
	public static final String LASTCURRENTCITY = "lastcurrentcity";// 当前城市
	public static final String MY_TAGS = "MY_TAGS";// tag
	public static final String FIRST_START_APP = "first_start_app";//第一次启动APP
	public static final String SET_COMPANYID = "setCompanyId";
	public static final String SET_ROOMID = "setRoomId";
	public static final String sellcode = "sellcode";
	public static final String UPDATETAB = "Updatetab";
	public static final String CARLEN = "Carlen";
	public static final String HUOLEI = "Huolei";
	public static final String AREA = "area";
	public static final String FONTSIZE = "fontsize";
	public static final String LEN = "LEN";
	public static final String DM = "DM";
	public static final String DM2= "DM2";
	public static final String DM3= "DM3";
	public static final String ONLINE = "ONLINE";
	public static final String JPUSHNUM = "JPUSHNUM";//1.5.1版本使用的计数  因功能修改不在使用
	public static final String JPUSHNALLUM = "JPUSHNALLUM";//1.5.1版本使用的计数  因功能修改换用
	public static final String ONCE = "ONCE";//崩溃次数
	public static final String TOAST = "TOAST";//提示方式 一次还是每次都有
	public static final String TOASTMSG = "TOASTMSG";//提示方式 一次还是每次都有
	public static final String CRASHLOG = "crashlog";//大小写转化 ctrl + shift + u
	public static final String EMPTYCAR = "EmptyCar";
	public static final String AREA2 = "area2";
	public static final String AREA3 = "area3";
	public static final String EMPTYCAR2 = "EmptyCar2";
	public static final String CARTYPE = "CarType";
	public static final String WV = "WV";
	public static final  String IS_FABU_SUPPLIES = "IS_FABU_SUPPLIES";
	public static final  String IS_GRRZ = "IS_GRRZ";
	public static final  String LINES = "LINES";//
	public static final  String IS_SUPPLYDETAIL = "IS_SUPPLYDETAIL";
	public static final  String ISOFFENLINE = "isOffenLine";
	public static final  String U_HEIGHT = "U_HEIGHT";
	public static final  String U_WIDTH = "U_WIDTH";

	/**
	 * 存储字符串值
	 */
	public static void setPreferenceValueString(
			Context context, String preferenceKey,
			String preferenceValue,String preferenceName) {
		SharedPreferences preference = context.getSharedPreferences(preferenceName,
				Context.MODE_PRIVATE);
		preference.edit().putString(preferenceKey, preferenceValue).commit();
	}

	/**
	 * 获取字符串值
	 */
	public static String getPreferenceValueString(
			Context context, String preferenceKey,
			String defaultValue, String preferenceName) {
		SharedPreferences preference = context.getSharedPreferences(preferenceName,
				Context.MODE_PRIVATE);
		return preference.getString(preferenceKey, defaultValue);
	}

	/**
	 * 存储int值
	 */
	public static void setPreferenceValueInt(
			Context context, String preferenceKey,
			int preferenceValue,String preferenceName) {
		SharedPreferences preference = context.getSharedPreferences(preferenceName,
				Context.MODE_PRIVATE);
		preference.edit().putInt(preferenceKey, preferenceValue).commit();
	}

	/**
	 * 获取int值
	 */
	public static int getPreferenceValueInt(
			Context context, String preferenceKey,
			int defaultValue,String preferenceName) {
		SharedPreferences preference = context.getSharedPreferences(preferenceName,
				Context.MODE_PRIVATE);
		return preference.getInt(preferenceKey, defaultValue);
	}

	/**
	 * 存储long值
	 */
	public static void setPreferenceValueLong(
			Context context, String preferenceKey,
			long preferenceValue,String preferenceName) {
		SharedPreferences preference = context.getSharedPreferences(
				preferenceName,Context.MODE_PRIVATE);
		preference.edit().putLong(preferenceKey, preferenceValue).commit();
	}
	/**
	 * 获取long值
	 */
	public static long getPreferenceValueLong(
			Context context, String preferenceKey,
			long defaultValue, String preferenceName) {
		SharedPreferences preference = context.getSharedPreferences(preferenceName,
				Context.MODE_PRIVATE);
		return preference.getLong(preferenceKey, defaultValue);
	}


	/**
	 * 存储Float值
	 */
	public static void setPreferenceValueFloat(
			Context context, String preferenceKey,
			float preferenceValue,String preferenceName) {
		SharedPreferences preference = context.getSharedPreferences(
				preferenceName,Context.MODE_PRIVATE);
		preference.edit().putFloat(preferenceKey, preferenceValue).commit();
	}
	/**
	 * 获取Float值
	 */
	public static float getPreferenceValueFloat(
			Context context, String preferenceKey,
			float defaultValue, String preferenceName) {
		SharedPreferences preference = context.getSharedPreferences(preferenceName,
				Context.MODE_PRIVATE);
		return preference.getFloat(preferenceKey,defaultValue);
	}


	/**
	 * 存储boolean值
	 */
	public static void setPreferenceValueBoolean(
			Context context, String preferenceKey,
			boolean preferenceValue,String preferenceName) {
		SharedPreferences preference = context.getSharedPreferences(preferenceName,
				Context.MODE_PRIVATE);
		preference.edit().putBoolean(preferenceKey, preferenceValue).commit();
	}

	/**
	 * 获取boolean值
	 */
	public static boolean getPreferenceBoolean(
			Context context, String preferenceKey,
			boolean defaultValue, String preferenceName) {
		SharedPreferences preference = context.getSharedPreferences(preferenceName,
				Context.MODE_PRIVATE);
		return preference.getBoolean(preferenceKey, defaultValue);
	}


	/**
	 * 清除字符串值
	 * @param context
	 * @param preferenceName
	 * @param preferenceKey
	 */
	public static void clearPreferenceStringValue(Context context, String preferenceName, String preferenceKey) {
		SharedPreferences preference = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = preference.edit();
		edit.putString(preferenceKey, "");
		edit.commit();
	}

	/**
	 * 清除偏好设置
	 * @param context
	 * @param preferenceName
	 */
	public static void clearPreference(Context context, String preferenceName) {
		SharedPreferences preference = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = preference.edit();
		edit.clear();
		edit.apply();
	}





//	public static String arrayListToString(ArrayList<Object> arryList)
//			throws IOException {
//		Log.i("arrayListToString", JSON.toJSONString(arryList));
//		return null;
//	}
//
//	public static ArrayList<Object> stringToArrayList(String stringList)
//			throws StreamCorruptedException, IOException,
//			ClassNotFoundException {
//		Log.i("stringToArrayList", JSON.parseObject(stringList, String.class));
//		return null;
//	}

}
