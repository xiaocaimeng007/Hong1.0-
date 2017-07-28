package com.hongbao5656.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.hongbao5656.R;
import com.hongbao5656.base.BaseActivity;
import com.hongbao5656.util.SU;
import com.hongbao5656.util.TU;
import com.hongbao5656.zxing.camera.CameraManager;
import com.hongbao5656.zxing.decoding.CaptureActivityHandler;
import com.hongbao5656.zxing.decoding.InactivityTimer;
import com.hongbao5656.zxing.view.ViewfinderView;

import java.io.IOException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 二维码扫描activity
 */
public class ScanQRActivity extends BaseActivity implements Callback {
	/** 跳转的action */
	public static String ACTION_KEY = "action_key";
	/** 跳转的到网页的action */
	public static String ACTION_KEY_WEBPAGE = "action_key_web_page";
	/** 取出数据的action */
	public static String ACTION_KEY_GETDATA = "action_key_get_data";
	/** 打印action */
	public static String ACTION_TYPO = "action_typo";
	/** 扫描二维码装车 */
	public static String ACTION_LOADCAR = "action_loadcar";

	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;

	private String actionKey;
	private String t;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_scan);
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);

		Intent intent = getIntent();
		if (!intent.hasExtra(ACTION_KEY)) {
			throw new RuntimeException("你没有放置要跳转的action");
		}
		actionKey = intent.getStringExtra(ACTION_KEY);
		t = intent.getStringExtra("t");
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	/**
	 * 根据action进行跳转
	 */
	public void actionTo(String actionKey, String resultString) {
		if (SU.isEmpty(resultString)) {
			return;
		}
		// http://wx.huodada.com/ad?product=9b3cda3b7f5447e1b907987ccd75e8bf&timestamp=1453368492382
		if (actionKey.equals(ACTION_KEY_WEBPAGE)) {
			Intent intent = new Intent(this, WebBrowserActivity.class);
			intent.putExtra(WebBrowserActivity.ACTION_KEY,WebBrowserActivity.ACTION_KEY_OPEN_WEBPAGE);
			intent.putExtra(WebBrowserActivity.URL, resultString);
			startActivity(intent);
		} else if (actionKey.equals(ACTION_KEY_GETDATA)) {
			Log.i(ACTION_KEY_GETDATA, resultString);
		} else if (actionKey.equals(ACTION_TYPO)) {
			if (resultString.contains("print") && resultString.contains("&")) {
				// 扫描出识别打印机
				int indexStart = resultString.indexOf("print");
				int endIndex = resultString.indexOf("&");
				String print = resultString.substring(indexStart + 6, endIndex);

				Intent intent = new Intent();
//				intent.setClass(this, PointMessageActivity.class);
				intent.putExtra("tNo", print);
				intent.putExtra("t", t);
				ScanQRActivity.this.finish();
				startActivity(intent);
			} else {
				Intent intent = new Intent();
//				intent.setClass(this, PointMessageActivity.class);
				intent.putExtra("tNo", ACTION_LOADCAR);
				intent.putExtra("message", "扫描失败，无法识别！");
				ScanQRActivity.this.finish();
				startActivity(intent);
				finish();
			}
		} else if (actionKey.equals(ACTION_LOADCAR)) {
			if (resultString.contains("product") && resultString.contains("&")) {
				// 扫描出识别货源id
				int indexStart = resultString.indexOf("product");
				int endIndex = resultString.indexOf("&");
				String product = resultString.substring(indexStart + 8,
						endIndex);
				Intent i = new Intent();
				i.putExtra("product", product);
				setResult(Activity.RESULT_OK, i);
				finish();
			} else if (resultString.contains("print")
					&& resultString.contains("&")) {
				// 扫描出识别打印机
				int indexStart = resultString.indexOf("print");
				int endIndex = resultString.indexOf("&");
				String print = resultString.substring(indexStart + 6, endIndex);
				Intent i = new Intent();
				i.putExtra("print", print);
				setResult(Activity.RESULT_OK, i);
				finish();
			} else {
				Intent intent = new Intent();
//				intent.setClass(this, PointMessageActivity.class);
				intent.putExtra("tNo", ACTION_LOADCAR);
				intent.putExtra("message", "扫描失败，无法识别！");
				ScanQRActivity.this.finish();
				startActivity(intent);
				finish();
			}
		}
	}

	/**
	 * 处理扫描结果 camera
	 * 
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		System.out.println("扫描结果：" + result);
		String resultString = result.getText();
		if (resultString.equals("")) {
			TU.show(ScanQRActivity.this, "扫描失败!");
			return;
		} else {
			actionTo(actionKey, resultString);
		}
		// if {
		// // http://wx.huodada.com/ad?t=1234567890
		// long l = Long.parseLong(resultString.substring(resultString
		// .indexOf("=") + 1));
		// Intent intent = new Intent();
		// intent.setClass(this, PointMessageActivity.class);
		// intent.putExtra("tNo", l + "");
		// startActivity(intent);
		// // 匹配在'uid='后面,且在'&'前面的数字.
		// String regularExpression = "(?<=uid=)(\\d+)(?=\\&)";
		// Pattern patter = Pattern.compile(regularExpression,
		// Pattern.CASE_INSENSITIVE);
		// Matcher matcher = patter.matcher(resultString);
		//
		// if (matcher.find()) {
		// // String key = matcher.group();
		// // Intent intent = new Intent(this,
		// // PointMessageActivity.class);
		// // intent.putExtra("fid", key);
		// // startActivity(intent);
		// // Bundle bundle = new Bundle();
		// // bundle.putString("result", resultString);
		// // bundle.putParcelable("bitmap", barcode);
		// // resultIntent.putExtras(bundle);
		// this.finish();
		// } else if (isVerifyUrl(resultString)) {
		//
		// // Intent intent = new Intent(this, WebBrowserActivity.class);
		// // intent.putExtra(WebBrowserActivity.ACTION_KEY,
		// // WebBrowserActivity.ACTION_KEY_ANY_URL);
		// // intent.putExtra(WebBrowserActivity.URL, resultString);
		// // startActivity(intent);
		// // this.finish();
		// } else {
		// // ToastUtil.makeShortText(ScanQRActivity.this,
		// // getString(R.string.invalid_content));
		//
		// }
		// }
	}

	private boolean isVerifyUrl(String url) {
		Pattern p = Pattern
				.compile(
						"^(http|www|ftp|)?(://)?(\\w+(-\\w+)*)(\\.(\\w+(-\\w+)*))*((:\\d+)?)(/(\\w+(-\\w+)*))*(\\.?(\\w)*)(\\?)?(((\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*(\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*)*(\\w*)*)$",
						Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(url);
		return m.find();
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};



}
