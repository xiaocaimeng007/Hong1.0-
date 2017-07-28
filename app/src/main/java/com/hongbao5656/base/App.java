package com.hongbao5656.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Looper;
import android.support.multidex.MultiDex;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.Toast;
import com.example.aaron.library.MLog;
import com.hongbao5656.BuildConfig;
import com.hongbao5656.R;
import com.hongbao5656.entity.OffenLine;
import com.hongbao5656.entity.SupplyItem;
import com.hongbao5656.jpush.JPushUtil;
import com.hongbao5656.util.MailManager;
import com.hongbao5656.util.NU;
import com.hongbao5656.util.SPU;
import com.squareup.leakcanary.LeakCanary;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
public class App extends android.app.Application
		implements Thread.UncaughtExceptionHandler {
	private static App myApp;
	public static App getInstance() {
		return myApp;
	}
	public static String LOGO_IMAGE;
	public static double Longitude;
	public static double Latitude;
	public static boolean fontsize = true;
	public static String token = "";
	public static String account = "";
	public static String username = "";
	public static String version ="1.6.2";
	public static String versionCode ="112";
	public static int cversionCode =0;
	public static String pwd ="";
	public static String pwd2 ="";
	public static String phone ="";
	public static String UER_TYPE ="";
	public static String UER_CHECK_STATE ="";

//	public static String BATE_URL = "https://api.56hb.net/api/HBInterface";//api地址
	public static String BATE_URL = "http://api.56hb.net/api/HBInterface";//api地址
	public static String BATE_URL_UPDOWN = "http://www.56hb.net/api/Version/hytx";//更新版本判断地址
	public static String URL_APKDOWNLOAD = "http://www.56hb.net/download/DownHYTX?version=";//apk下载地址
	public static String URL_FILEUPLOAD_M = "http://www.56hb.net/api/LogUploadBatch";//批量文件上传
	public static String URL_WXPAY = "http://www.56hb.net/api/alipaysign";//支付宝支付
	public static String URL_GERTX = "http://www.56hb.net/image?uid=";//个人图像
	public static String URL_OILPRVICE = "http://apicloud.mob.com/oil/price/province/query?key=140f983e39498";//今日油价

	//用来存储设备信息和异常信息
	private Map<String, String> infos = new HashMap<String, String>();
	/**
	 * 错误报告文件的扩展名
	 */
	public static final String CRASH_REPORTER_EXTENSION = ".cr";
	private DateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	/**
	 * 存放临时String类型数据
	 */
	public static HashMap<String, Object> datas = new HashMap<String, Object>();
	/*
	 * 给datas中添加数据
	 */
	public static void put(String key, Object value) {
		datas.put(key, value);
	}
	/*
	 * 从datas中取出数据
	 */
	public static Object get(String key) {
		return datas.get(key);
	}
	/*
	 * 清除datas中数据
	 */
	public static void clear() {
		datas.clear();
	}


	public static ArrayList<OffenLine> supplys;
	public static SupplyItem si;
	public static boolean flag = false;
	public static String updatetab = "tb_App_Unit:000,tb_App_CarLen:000,tb_App_CarType:000,tb_App_GoodsType:000,vw_EmptyTime:000,tb_App_Company:000";

	private final static float HEAP_UTILIZATION = 0.75f;
	private final static int MIN_HEAP_SIZE = 6* 1024* 1024 ;

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

	private static Context context = null;


	@Override
	public void onCreate() {
		super.onCreate();
		if (LeakCanary.isInAnalyzerProcess(this)) {
			// This process is dedicated to LeakCanary for heap analysis.
			// You should not init your app in this process.
			return;
		}
		LeakCanary.install(this);
		context = getApplicationContext();
		myApp = this;
//		Thread.setDefaultUncaughtExceptionHandler(this);
		initLog();
		initImagePath();

		// 优化内存,以下非必须！

//		VMRuntime.getRuntime().setTargetHeapUtilization(HEAP_UTILIZATION);
//		VMRuntime.getRuntime().setMinimumHeapSize(MIN_HEAP_SIZE);
		//changeMetrics(this);//修改屏幕Density
//		......
	}

	public static Context getAppContext() {
		return context;
	}

	private static final boolean DebugFlag = false;
	//修改屏幕Density
	public static void changeMetrics(Context context) {
		DisplayMetrics curMetrics = context.getResources().getDisplayMetrics();
		if(!DebugFlag) {
			if (curMetrics.densityDpi == DisplayMetrics.DENSITY_HIGH) {
				DisplayMetrics metrics = new DisplayMetrics();
				metrics.scaledDensity = 1.0f;
				metrics.density = 1.0f;
				metrics.densityDpi = DisplayMetrics.DENSITY_MEDIUM;
				metrics.xdpi = DisplayMetrics.DENSITY_MEDIUM;
				metrics.ydpi = DisplayMetrics.DENSITY_MEDIUM;
				metrics.heightPixels = curMetrics.heightPixels;
				metrics.widthPixels = curMetrics.widthPixels;
				context.getResources().getDisplayMetrics().setTo(metrics);
			}
		} else {
			DisplayMetrics metrics = new DisplayMetrics();
			metrics.scaledDensity = (float)(130/160.0);
			metrics.density = (float)(130/160.0);
			metrics.densityDpi = 130;
			metrics.xdpi = 130;
			metrics.ydpi = 130;
			metrics.heightPixels = curMetrics.heightPixels;
			metrics.widthPixels = curMetrics.widthPixels;
			context.getResources().getDisplayMetrics().setTo(metrics);
		}
	}

	// 异常处理，不需要处理时注释掉这两句即可！
	// 注册crashHandler
//	private void initCrash() {
//		CrashHandler crashHandler = CrashHandler.getInstance();
//		crashHandler.init(getApplicationContext());
//	}

	private void initLog() {
		if (BuildConfig.LOG_DEBUG == true) {
			MLog.init(true);
		} else {
			MLog.init(false);
		}
	}

	//保存logo到手机
	private void initImagePath() {
		try {
			String cachePath = com.mob.tools.utils.R.getCachePath(this, null);
			LOGO_IMAGE = cachePath + "logo.png";
			File file = new File(LOGO_IMAGE);
			if (!file.exists()) {
				file.createNewFile();
				Bitmap pic = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
				FileOutputStream fos = new FileOutputStream(file);
				pic.compress(Bitmap.CompressFormat.JPEG, 100, fos);
				fos.flush();
				fos.close();
			}
		} catch (Throwable t) {
			t.printStackTrace();
			LOGO_IMAGE = null;
		}
	}


	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		MLog.e("程序崩溃：");
		JPushUtil.getInstance().setMyTags(this,"clear,clear");
//        if (!handleException(ex) && mDefaultHandler != null) {
//            //如果用户没有处理（返回true）则让系统默认的异常处理器来处理
//            mDefaultHandler.uncaughtException(thread, ex);
//        } else {
		if (!SPU.getPreferenceBoolean(this,SPU.ONCE,false,SPU.STORE_SETTINGS)){
			handleException(ex);
			SPU.setPreferenceValueBoolean(this,SPU.ONCE,true,SPU.STORE_SETTINGS);
			try {
				Thread.sleep(800);
			} catch (InterruptedException e) {
				MLog.e(e.toString());
			}
		}

		//退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
//            SPU.setPreferenceValueBoolean(mContext,SPU.ONCE,false,SPU.STORE_SETTINGS);
//        }

// 		System.exit(0);
//		Intent intent = new Intent(this, LaunchActivity.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
//				Intent.FLAG_ACTIVITY_NEW_TASK);
//		startActivity(intent);
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 * @param ex
	 * @return true:如果处理了该异常信息;
	 * 否则返回false 让默认处理
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			MLog.w("handleException --- ex==null");
			return false;
		}
//        final String msg = ex.getLocalizedMessage();
		final String msg = ex.getCause().toString();
		if (msg == null) {
			return false;
		}
		//使用Toast来显示异常信息
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(App.this, "程序出错，即将退出重启...\r\n" + msg,
						Toast.LENGTH_LONG).show();
//              MsgPrompt.showMsg(mContext, "程序出错啦", msg+"\n点确认退出");
				Looper.loop();
			}
		}.start();

		//收集设备参数信息
		collectDeviceInfo(App.getInstance());
		//保存日志文件
		String filepath = saveCrashInfo2File(ex);
		//发送错误报告到服务器
//        sendCrashReportsToServer(mContext);
		//发邮件
		MailManager.getInstance().sendMailWithFile(App.getInstance(),"cr from Application","", filepath);
		return true;
	}



	/**
	 * 收集设备参数信息
	 *
	 * @param ctx
	 */
	public String collectDeviceInfo(Context ctx) {
		infos.put("timestamp", System.currentTimeMillis() + "");
		infos.put("date", df.format(new Date()));

		infos.put("IP", NU.getIpAddress());
		infos.put("versionName", App.version);
		infos.put("versionCode", App.versionCode);
		infos.put("phone", App.phone);
		infos.put("tip", App.pwd2);

		if(null != ctx){
			infos.put("nettype", (NU.getConnectedType(ctx) == 0 ? "mobile-data" : "wifi"));
			DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
			infos.put("density",displayMetrics.density+"");
			infos.put("densityDpi",displayMetrics.densityDpi+"");
			infos.put("xdpi",displayMetrics.xdpi+"");
			infos.put("ydpi",displayMetrics.ydpi+"");
			infos.put("height",displayMetrics.heightPixels+"");
			infos.put("width",displayMetrics.widthPixels+"");
		}

//        try {
//            PackageManager pm = ctx.getPackageManager();
//            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
//            if (pi != null) {
//                String versionName = pi.versionName == null ? "null" : pi.versionName;
//                String versionCode = pi.versionCode + "";
//            }
//        } catch (NameNotFoundException e) {
//            MLog.e("CrashHandleran.NameNotFoundException---> error occured when collect package info", e);
//        }

		//使用反射来收集设备信息.在Build类中包含各种设备信息,
		//例如: 系统版本号,设备生产商 等帮助调试程序的有用信息
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				if ("MANUFACTURER".equals(field.getName())) {
					infos.put("Vendor", field.get(null).toString());
				} else if ("MODEL".equals(field.getName())) {
					infos.put("MODEL", field.get(null).toString());
				} else if ("CPU_ABI".equals(field.getName())) {
					infos.put("CPU_ABI", field.get(null).toString());
				}
			} catch (Exception e) {
				MLog.e("CrashHandler.NameNotFoundException---> an error occured when collect crash info", e);
			}
		}

		StringBuffer sb = new StringBuffer();
		sb.append("\n---------------------start--------------------------\n");
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + ":>" + value + "\n");
		}
		sb.append("--------------------end---------------------------");
		return sb.toString();
	}

	/**
	 * 保存错误信息到文件中
	 * @param ex
	 * @return 返回文件名称, 便于将文件传送到服务器
	 */
	private String saveCrashInfo2File(Throwable ex) {

		StringBuffer sb = new StringBuffer();
		sb.append("\n---------------------start--------------------------\n");
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + ":" + value + "\n");
		}

		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}


//        StackTraceElement[] stackTrace = ex.getStackTrace();
//        printWriter.write("_________________________________________A\n");
//        printWriter.write(ex.getMessage() + "\n");
//        for (int i = 0; i < stackTrace.length; i++) {
//            printWriter.write("file: " + stackTrace[i].getFileName() + " class: "
//                    + stackTrace[i].getClassName() + " method:"
//                    + stackTrace[i].getMethodName() + " line:"
//                    + stackTrace[i].getLineNumber() + "\n");
//        }
//        printWriter.write("\n_________________________________________B\n");

		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		sb.append("--------------------end---------------------------");
		MLog.e(sb.toString());

		try {
			long timestamp = System.currentTimeMillis();
			String time = df.format(new Date());
			String dirpath = "";
			String fileName = App.version+"crash-" + time + "-" + timestamp + CRASH_REPORTER_EXTENSION;
//            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//                dirpath = Constants.SDCARD_PATH;
//                File dir = new File(dirpath);
//                if (!dir.exists()) {
//                    dir.mkdirs();
//                }
//                File file = new File(dirpath + fileName);
//                if (!file.exists()) {
//                    file.createNewFile();
//                }
//                FileOutputStream fos = new FileOutputStream(file);
//                fos.write(sb.toString().getBytes());
//                //发送给开发人员
////                sendCrashLog2PM(dirpath + fileName);
//                fos.close();
//            }else{
			FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
			fos.write(sb.toString().getBytes());
			fos.flush();
			fos.close();
//            }
			File cfile = new File(getFilesDir(), fileName);
			MLog.d("保存崩溃日志:"+fileName+"文件大小："+cfile.length());
			return cfile.getAbsolutePath();
		} catch (Exception e) {
			MLog.e("an error occured while writing file...", e);
		}
		return null;
	}


	public static int dip2px(float value) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, myApp.getResources().getDisplayMetrics());
	}

}
