package com.hongbao5656.jpush;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.example.aaron.library.MLog;
import com.hongbao5656.R;
import com.hongbao5656.util.SPU;
import com.hongbao5656.util.SU;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class JPushUtil {

	private JPushUtil(){
	}
	private static  JPushUtil instance;
	public static JPushUtil getInstance(){
		if(null == instance){
			synchronized(JPushUtil.class){
				if (instance == null) {
					instance = new JPushUtil();
				}
			}
		}
		return instance;
	}

	private Context cxt;

	public void setMyTags(Context cxt,String tag) {
		this.cxt = cxt;
		Set<String> tagSet = new LinkedHashSet<String>();
		if (!SU.isEmpty(tag)) {
//            MLog.i("设置的tag:" + tag);
			// ","隔开的多个 转换成 Set
			String[] sArray = tag.split(",");
			for (String sTagItme : sArray) {
				if (!JPushUtil.isValidTagAndAlias(sTagItme.trim())) {
					Toast.makeText(cxt, R.string.error_tag_gs_empty, Toast.LENGTH_SHORT).show();
					return;
				}
				tagSet.add(sTagItme.trim());
			}
		}
		mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, tagSet));
	}

	private static final int MSG_SET_ALIAS = 1001;

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case MSG_SET_ALIAS:
					MLog.d("调用 JPush 接口来设置别名");
					// 调用 JPush 接口来设置别名。
					JPushInterface.setTags(cxt.getApplicationContext(),
							(Set<String>) msg.obj, mAliasCallback);
					break;
				default:
					MLog.i("Unhandled msg - " + msg.what);
			}
		}
	};

	public final TagAliasCallback mAliasCallback = new TagAliasCallback() {
		@Override
		public void gotResult(int code, String alias, Set<String> tags) {
			String logs;
			switch (code) {
				case 0:
					String tag = tags.toString().substring(1, tags.toString().length() - 1).trim();
					SPU.clearPreferenceStringValue(cxt, SPU.STORE_USERINFO, SPU.MY_TAGS);
					SPU.setPreferenceValueString(cxt, SPU.MY_TAGS, tag, SPU.STORE_USERINFO);
					logs = "设置tag成功" + SPU.getPreferenceValueString(cxt, SPU.MY_TAGS, "", SPU.STORE_USERINFO);
					MLog.i(logs);
					// 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
					break;
				case 6002:
					logs = "设置tag失败 8秒后再次设置" + tags.toString();
					MLog.i(logs);
//                    TU.show(mActivity, logs);
					// 延迟 60 秒来调用 Handler 设置别名
					String tagss = SPU.getPreferenceValueString(cxt, SPU.MY_TAGS, "", SPU.STORE_USERINFO);
					Set<String> tagSets = new LinkedHashSet<String>();
					if (!SU.isEmpty(tagss)) {
//            MLog.i("设置的tag:" + tag);
						// ","隔开的多个 转换成 Set
						String[] sArray = tagss.split(",");
						for (String sTagItme : sArray) {
							if (!JPushUtil.isValidTagAndAlias(sTagItme.trim())) {
								Toast.makeText(cxt, R.string.error_tag_gs_empty, Toast.LENGTH_SHORT).show();
								return;
							}
							tagSets.add(sTagItme.trim());
						}
					}
					mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, tagSets), 1000 * 8);
					break;
				default:
					logs = "失败的errorCode = " + code;
					MLog.i(logs);
//                    TU.show(mActivity, logs);
			}
		}
	};



//====================================================================

	public static final String PREFS_NAME = "JPUSH_EXAMPLE";
	public static final String PREFS_DAYS = "JPUSH_EXAMPLE_DAYS";
	public static final String PREFS_START_TIME = "PREFS_START_TIME";
	public static final String PREFS_END_TIME = "PREFS_END_TIME";
	public static final String KEY_APP_KEY = "JPUSH_APPKEY";

	public static boolean isEmpty(String s) {
		if (null == s)
			return true;
		if (s.length() == 0)
			return true;
		if (s.trim().length() == 0)
			return true;
		return false;
	}

	// 校验Tag Alias 只能是数字,英文字母和中文
	public static boolean isValidTagAndAlias(String s) {
		Pattern p = Pattern.compile("^[\u4E00-\u9FA50-9a-zA-Z_-]{0,}$");
		Matcher m = p.matcher(s);
		return m.matches();
	}

	// 取得AppKey
	public static String getAppKey(Context context) {
		Bundle metaData = null;
		String appKey = null;
		try {
			ApplicationInfo ai = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			if (null != ai)
				metaData = ai.metaData;
			if (null != metaData) {
				appKey = metaData.getString(KEY_APP_KEY);
				if ((null == appKey) || appKey.length() != 24) {
					appKey = null;
				}
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return appKey;
	}

	// 取得版本号
	public static String GetVersion(Context context) {
		try {
			PackageInfo manager = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return manager.versionName;
		} catch (NameNotFoundException e) {
			return "Unknown";
		}
	}

	public static void showToast(final String toast, final Context context) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
				Looper.loop();
			}
		}).start();
	}

	public static boolean isConnected(Context context) {
		ConnectivityManager conn = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = conn.getActiveNetworkInfo();
		return (info != null && info.isConnected());
	}

	public static String getImei(Context context, String imei) {
		try {
			TelephonyManager telephonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			imei = telephonyManager.getDeviceId();
		} catch (Exception e) {
			Log.e(JPushUtil.class.getSimpleName(), e.getMessage());
		}
		return imei;
	}







}
