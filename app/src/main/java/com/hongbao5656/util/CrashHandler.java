package com.hongbao5656.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.Toast;

import com.example.aaron.library.MLog;
import com.hongbao5656.activity.LaunchActivity;
import com.hongbao5656.base.App;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
/**
 * UncaughtException处理类,
 * 当程序发生Uncaught异常的时候,
 * 由该类来接管程序,并记录发送错误报告.
 * <p/>
 * Created by yuyuhang on 15/12/7.
 */
public class CrashHandler implements UncaughtExceptionHandler {

    //系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    //程序的Context对象
    private Context mContext;
    //用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<String, String>();

    /**
     * 错误报告文件的扩展名
     */
    public static final String CRASH_REPORTER_EXTENSION = ".cr";

    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    //CrashHandler实例
    private static CrashHandler INSTANCE;
    //构造方法私有化
    private CrashHandler() {
    }
    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static  synchronized CrashHandler getInstance() {
        if (INSTANCE == null)
            INSTANCE = new CrashHandler();
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;

//        //获取系统默认的UncaughtException处理器
//        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
//        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);

    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        MLog.e("程序崩溃："+ex.getMessage());
//        if (!handleException(ex) && mDefaultHandler != null) {
//            //如果用户没有处理（返回true）则让系统默认的异常处理器来处理
//            mDefaultHandler.uncaughtException(thread, ex);
//        } else {
            if (!SPU.getPreferenceBoolean(mContext,SPU.ONCE,false,SPU.STORE_SETTINGS)){
                handleException(ex);
                SPU.setPreferenceValueBoolean(mContext,SPU.ONCE,true,SPU.STORE_SETTINGS);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    MLog.e(e.toString());
                }
            }

            //退出程序
//            android.os.Process.killProcess(android.os.Process.myPid());
//            System.exit(1);
//            SPU.setPreferenceValueBoolean(mContext,SPU.ONCE,false,SPU.STORE_SETTINGS);
//        }

        System.exit(0);
        Intent intent = new Intent(mContext, LaunchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
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
                Toast.makeText(mContext, "程序出错，即将退出重启...\r\n" + msg,
                        Toast.LENGTH_LONG).show();
//              MsgPrompt.showMsg(mContext, "程序出错啦", msg+"\n点确认退出");
                Looper.loop();
            }
        }.start();

        //收集设备参数信息
        collectDeviceInfo(mContext);
        //保存日志文件
        saveCrashInfo2File(ex);
        //发送错误报告到服务器
//        sendCrashReportsToServer(mContext);
        return true;
    }


    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        infos.put("timestamp", System.currentTimeMillis() + "");
        infos.put("date", df.format(new Date()));
        infos.put("当前网络", (NU.getConnectedType(ctx) == 0 ? "移动流量" : "wifi"));
        infos.put("当前IP", NU.getIpAddress());
        infos.put("versionName", App.version);
        infos.put("versionCode", App.versionCode);
        DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
        infos.put("density",displayMetrics.density+"");
        infos.put("densityDpi",displayMetrics.densityDpi+"");
        infos.put("xdpi",displayMetrics.xdpi+"");
        infos.put("ydpi",displayMetrics.ydpi+"");
        infos.put("height",displayMetrics.heightPixels+"");
        infos.put("width",displayMetrics.widthPixels+"");
        infos.put("sdkint", Build.VERSION.SDK_INT+"");
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
            String fileName = "crash-" + time + "-" + timestamp + CRASH_REPORTER_EXTENSION;
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
            FileOutputStream fos = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(sb.toString().getBytes());
            fos.flush();
            fos.close();
//            }
            MLog.d("保存崩溃日志:"+fileName);
            return fileName;
        } catch (Exception e) {
            MLog.e("an error occured while writing file...", e);
        }
        return null;
    }


    /**
     * 将捕获的导致崩溃的错误信息发送给开发人员
     * 目前只将log日志保存在sdcard 和输出到LogCat中，并未发送给后台。
     */
    private void sendCrashLog2PM(String fileName) {
        //      if (!new File(fileName).exists()) {
        //          Toast.makeText(mContext, "日志文件不存在！", Toast.LENGTH_SHORT).show();
        //          return;
        //      }
        //      FileInputStream fis = null;
        //      BufferedReader reader = null;
        //      String s = null;
        //      try {
        //          fis = new FileInputStream(fileName);
        //          reader = new BufferedReader(new InputStreamReader(fis, "GBK"));
        //          while (true) {
        //              s = reader.readLine();
        //              if (s == null)
        //                  break;
        //              //由于目前尚未确定以何种方式发送，所以先打出log日志。
        //              Log.i("info", s.toString());
        //          }
        //      } catch (FileNotFoundException e) {
        //          e.printStackTrace();
        //      } catch (IOException e) {
        //          e.printStackTrace();
        //      } finally { // 关闭流
        //          try {
        //              reader.close();
        //              fis.close();
        //          } catch (IOException e) {
        //              e.printStackTrace();
        //          }
        //      }
    }
}
