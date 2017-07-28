package com.hongbao5656.jpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.example.aaron.library.MLog;
import com.hongbao5656.activity.MainActivity;
import com.hongbao5656.activity.SupplyDetailActivity;
import com.hongbao5656.util.SU;
import org.json.JSONException;
import org.json.JSONObject;
import cn.jpush.android.api.JPushInterface;
/**
 * 自定义接收器
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class JpushReceiver extends BroadcastReceiver {
	private static final String TAG = "JPush";
	public static final String KEY_TITLE = "title";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_EXTRAS = "extras";

	public static final String ACTION_NOTIFICATION_RECEIVED = "com.huodada.driver.ACTION_NOTIFICATION_RECEIVED";
	public static final String ACTION_NOTIFICATION_OPENED = "com.huodada.driver.ACTION_NOTIFICATION_OPENED";
	public static boolean isForeground = false;

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			MLog.i(TAG, "JPush用户注册成功: " + regId);
		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
			MLog.i(TAG,"推送的自定义消息: "+ bundle.getString(JPushInterface.EXTRA_MESSAGE));
			/*{
					"ImageURL": "http://www.56hb.net/image?uid=18792869521",
					"GoodsID": "16082916165408081",
					"CityFrom": 310110,
					"CityTo": 340500,
					"CityFromName": "杨浦",
					"CityToName": "马鞍山",
					"CityFromParentName": "(上海)",
					"CityToParentName": "(安徽)",
					"GoodsName": "食品",
					"WV": 9,
					"Unit": "吨",
					"CarLen": "12.5米",
					"CarType": "冷藏",
					"LastUpdate": "2016-08-30T10:32:23",
					"Remark": "测试",
					"Contact": "李刚",
					"ContactMobile": "18792869521",
					"ToView": 0,
					"JpushTags": "310000340000,310000340500,310110340500"
			}*/
			processCustomMessage(context, bundle);
		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
			MLog.i(TAG, "接收到推送下来的通知: ");
			int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
			MLog.i(TAG, "接收到推送下来的通知的ID: " + notifactionId);
//			intent.putExtra("action", ACTION_NOTIFICATION_RECEIVED);
//			saveMessage(context, intent);
			//打开自定义的Activity

		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
			MLog.i(TAG, "用户点击打开了通知"+bundle.getString(JPushInterface.EXTRA_EXTRA));
//			{"android":{"extras":{"goodsid":"16083011260729772"}},"alert":"嘉定--西安 食品 8吨"}
			Intent i = new Intent(context, SupplyDetailActivity.class);
			try{
				JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
				i.putExtra("vo",json.get("goodsid").toString());
				MLog.i(TAG, "goodsid:" + json.get("goodsid").toString());
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				context.startActivity(i);
			}catch(Exception e){e.printStackTrace();}
//			saveMessage(context, intent);
		} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
			MLog.i(TAG,"用户收到到RICH PUSH CALLBACK: "
							+ bundle.getString(JPushInterface.EXTRA_EXTRA));
			// 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity，
			// 打开一个网页等..

		} else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent
				.getAction())) {
			boolean connected = intent.getBooleanExtra(
					JPushInterface.EXTRA_CONNECTION_CHANGE, false);
			MLog.i(TAG, "[JpushReceiver]" + intent.getAction()
					+ " connected state change to " + connected);
		} else {
			MLog.i(TAG,"[JpushReceiver] Unhandled intent - " + intent.getAction());
		}

	}

//	private void saveMessage(Context context, Intent intent) {
//		Bundle bundle = intent.getExtras();
//		String content = bundle.getString(JPushInterface.EXTRA_ALERT);
//		String title = bundle
//				.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
//		String alert = bundle.getString(JPushInterface.EXTRA_EXTRA);
//		JSONObject obj = JSON.parseObject(alert);
//		if (obj == null || obj.equals("")) {
//			return;
//		}
//		String toAction = "";
//		if (obj.containsKey("action")) {
//			toAction = (String) obj.get("action");
//		}
//		long currentTimeMillis = 0l;
//		if (obj.containsKey("currentTimeMillis")) {
//			currentTimeMillis = Long.parseLong(String.valueOf(obj
//					.get("currentTimeMillis")));
//		}
//
//		String action = intent.getStringExtra("action");
//		if (JpushReceiver.ACTION_NOTIFICATION_OPENED.equals(action)) {
//			// Intent i = new Intent(context, MessageActivity.class);
//			// context.startActivity(i);
//		} else if (JpushReceiver.ACTION_NOTIFICATION_RECEIVED.equals(action)) {
//			// 存储数据库
////			ContentValues values = new ContentValues();
////			values.put("title", title);
////			values.put("content", content);
////			values.put("noti_time", TimeUtils.getSureTime("yyyy-MM-dd HH:mm",
////					currentTimeMillis));
////			values.put("is_read", "false");
////			DBHelper.getInstance(context).insertNotification(values);
////
////			messageHandler(context, toAction);
//		}
//	}

	/** action处理 */
	private void messageHandler(Context context, String toAction) {
		try {
			if (MsgRoute.MQ_DATA_CONSTANT_USER_CHECK.equals(toAction)) {
//				AppSettings.setCheckState(context, "1");
			} else {
				//
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	//send msg to MainActivity
	private void processCustomMessage(Context context, Bundle bundle) {
//		if (!MainActivity.isForeground) {
			String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
			Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);

			if (!SU.isEmpty(extras)) {
				try {
					JSONObject extraJson = new JSONObject(extras);
					if (null != extraJson && extraJson.length() > 0) {
						msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			context.sendBroadcast(msgIntent);
//		}
	}


}
