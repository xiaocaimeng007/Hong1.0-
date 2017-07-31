package com.hongbao5656.activity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.aaron.library.MLog;
import com.hongbao5656.R;
import com.hongbao5656.adapter.MainPagerAdaper;
import com.hongbao5656.base.App;
import com.hongbao5656.base.BaseActivity;
import com.hongbao5656.okhttp.HttpException;
import com.hongbao5656.util.Constants;
import com.hongbao5656.entity.Region;
import com.hongbao5656.entity.SupplyDetailEdite;
import com.hongbao5656.entity.SupplyItem;
import com.hongbao5656.fragment.HomeFragment;
import com.hongbao5656.fragment.InstantSupplyFragment;
import com.hongbao5656.fragment.MyFragment;
import com.hongbao5656.fragment.SeekSupplyFragment;
import com.hongbao5656.fragment.SendSupplyFragment;
import com.hongbao5656.okhttp.HttpDataHandlerListener;
import com.hongbao5656.service.LocationServiceForGaoDe;
import com.hongbao5656.util.GuideViewUtil;
import com.hongbao5656.util.ImageCompress;
import com.hongbao5656.util.ImageUtils;
import com.hongbao5656.util.ParamsService;
import com.hongbao5656.util.ResponseParams;
import com.hongbao5656.util.SPU;
import com.hongbao5656.util.SU;
import com.hongbao5656.util.TU;
import com.hongbao5656.view.IndexViewPager;
import com.hongbao5656.view.guide.GuideManager;
import com.squareup.okhttp.Request;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import cn.jpush.android.api.JPushInterface;

public class MainActivity
        extends BaseActivity
        implements HttpDataHandlerListener, OnPageChangeListener {
    /**
     * 底部导航相关
     */
    private LinearLayout ll_bottom;
    private RadioGroup rg_group;
    private RadioButton rb_home;
    private RadioButton rb_fahuo;
    private RadioButton rb_jishihuoyuan;
    private RadioButton rb_seeksupply;
    private RadioButton rb_me;

    /**
     * 顶部标题栏相关
     */
    private LinearLayout ll_top;
    private View[] topViews;
    private View homeFragmentTop;
    public View sendSupplyFragmentTop;
    public View instantSupplyFragmentTop;
    private View seekSupplyFragmentTop;
    private View myFragmentTop;
    public View cTopView;//当前顶部标题栏

    /**
     * 中间内容区
     */
    private IndexViewPager vPager;
    private ArrayList<Fragment> fms;
    private HomeFragment homeFragment;
    private SendSupplyFragment sendSupplyFragment;
    private InstantSupplyFragment instantSupplyFragment;
    private SeekSupplyFragment seekSupplyFragment;
    private MyFragment myFragment;
    private long exitTime = 0;
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.hongbao.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";
    public static boolean isForeground = false;
    Intent locationServiceIntent;
    public TextView rb_jishihuoyuan_count;
    private String localPath;
    private Bitmap bitmap;
    private String imgdata = "";
    private MediaPlayer mp;
    public int count = 0;
    private String s;
    private GuideViewUtil mGuideViewUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playMusic(2);
        initView();
        initJpush();
        initMonitor();
        initLocation();
        registerJpushBR();
//        xastdmtest();
//        s.equals("dm");
    }

    private void xastdmtest() {
        //写入私有文件
     try {
//            OutputStream os = openFileOutput("xastdm",MODE_PRIVATE);//没有会自动创建文件 再次执行会直接覆盖
            OutputStream os = openFileOutput("xastdm",MODE_APPEND);//没有会自动创建文件 再次执行会追加
            String s = "xastdm是该app的开发者";
            byte[] bytes = s.getBytes();
            os.write(bytes,0,bytes.length);//写入
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //读取私有文件
        try {
            InputStream in = openFileInput("xastdm");
            byte[] bytes = new byte[1024];
            int len = -1;
            StringBuffer sb = new StringBuffer();
            while((len = in.read(bytes))!= -1){
                sb.append(new String(bytes,0,len));
            }
            in.close();
            TU.show(this,sb);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //读取res/raw/目录下的原生文件 可以通过context.getResources().openRawResource()
        //传入一个R.raw.文件名就可以返回一个InputStream
        //该文件为只读文件

        //写入缓存文件
//        String temp = getCacheDir()+"hytxapp.tmp";
        try {
            File temp = File.createTempFile("hytxapp",null);
            FileOutputStream fos = new FileOutputStream(temp);
            PrintStream ps = new PrintStream(fos);
            ps.print("我可是要成为编程王的男人");
            ps.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void initJpush() {
        MLog.i("极光推送开启");
        JPushInterface.setDebugMode(true);    // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);
    }

    public void playMusic(int type) {
        mp = null;
        if (1 == type) {
            mp = MediaPlayer.create(getApplicationContext(), R.raw.supply);
        } else if (2 == type) {
            mp = MediaPlayer.create(getApplicationContext(), R.raw.denglu);
        }
        try {
            mp.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.start();
    }

    private void registerJpushBR() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }

    public void initView() {
        // 放置顶部标题栏topbar的容器
        ll_top = (LinearLayout) findViewById(R.id.ll_top);
        LayoutInflater mInflater = LayoutInflater.from(this);
        topViews = new View[5];
        // 首页标题栏
        homeFragmentTop = mInflater.inflate(R.layout.fragment_home_top, null);
        topViews[0] = homeFragmentTop;
        // 发货标题栏
        sendSupplyFragmentTop = mInflater.inflate(R.layout.fragment_sendsupply_top, null);
        topViews[1] = sendSupplyFragmentTop;
        // 即时货源标题栏
        instantSupplyFragmentTop = mInflater.inflate(R.layout.fragment_instantsupply_top, null);
        topViews[2] = instantSupplyFragmentTop;
        // 找货标题栏
        seekSupplyFragmentTop = mInflater.inflate(R.layout.fragment_seeksupply_top, null);
        topViews[3] = seekSupplyFragmentTop;
        // 我的标题栏
        myFragmentTop = mInflater.inflate(R.layout.fragment_my_top, null);
        topViews[4] = myFragmentTop;
        // 添加顶部4个标题栏到topViews集合

//      addAllViews(homeFragmentTop, sendSupplyFragmentTop, instantSupplyFragmentTop, seekSupplyFragmentTop, myFragmentTop);

        // 底部导航根容器
        ll_bottom = (LinearLayout) findViewById(R.id.ll_bottom);
        rg_group = (RadioGroup) findViewById(R.id.rg_group);
        rb_home = (RadioButton) findViewById(R.id.rb_home);
        rb_fahuo = (RadioButton) findViewById(R.id.rb_fahuo);
        rb_jishihuoyuan = (RadioButton) findViewById(R.id.rb_jishihuoyuan);
        rb_jishihuoyuan_count = (TextView) findViewById(R.id.rb_jishihuoyuan_count);
        int jpushnum = SPU.getPreferenceValueInt(App.getInstance(), SPU.JPUSHNALLUM, 0, SPU.STORE_USERINFO);
        rb_jishihuoyuan_count.setText(jpushnum + "");
        rb_seeksupply = (RadioButton) findViewById(R.id.rb_seeksupply);
        rb_me = (RadioButton) findViewById(R.id.rb_me);

        setCurrentTopTitle(0);// 设置MainActivity当前顶部的标题栏为HomeFragment的标题

        // 添加四个fragment到fms集合
        homeFragment = new HomeFragment();
        sendSupplyFragment = new SendSupplyFragment();
        instantSupplyFragment = new InstantSupplyFragment();
        seekSupplyFragment = new SeekSupplyFragment();
        myFragment = new MyFragment();
        fms = new ArrayList<Fragment>();
        addFragment(homeFragment, sendSupplyFragment, instantSupplyFragment, seekSupplyFragment, myFragment);

        // 给自定义的IndexViewPager设置适配器
        FragmentManager fragmentManager = getSupportFragmentManager();
        MainPagerAdaper adapter = new MainPagerAdaper(fragmentManager, fms);

        vPager = (IndexViewPager) findViewById(R.id.vp_fragment);
        vPager.setOffscreenPageLimit(5);// 指定ViewPage的预加载/缓存页数 避免被重复加载
        vPager.setAdapter(adapter);
//        initLayoutParams();
//        vPager.setLayoutParams(params[0]);
        vPager.setCurrentItem(0);// 在完成数据适配后设置    其初始显示页面为HomeFragment
        vPager.setOnPageChangeListener(this);

//        mGuideViewUtil=new GuideViewUtil(this, R.drawable.xinshou1);
//        rb_seeksupply.setOnClickListener(v -> GuideManager.get().show(this, this));
//        GuideManager.get().show(this, this);
    }

//    private void initLayoutParams() {
//        params = new LayoutParams[5];
//        for (int i = 0; i < 5; i++) {
//            //隐藏首页导航
//            if (i == 0) {
//                params[i] = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
//                        - DensityUtils.dip2px(this, 58));
//                continue;
//            }
//            params[i] = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
//                    - DensityUtils.dip2px(this, 106));
//        }
//    }

    /**
     * 获取当前顶部标题栏view
     */

//    public View getCurrentView(int id) {
//        return topViews[id];
//    }

    /**
     * 跳转至对应Fragment
     */
    public void toSkip(int position) {
        switch (position) {
            case R.id.rb_home:// 首页
                rb_home.setChecked(true);// 设置当前首页的RadioButton被选中
                vPager.setCurrentItem(0, false);// 设置当前vp显示的Fragment页面
                setCurrentTopTitle(0);// 设置当前顶部的标题栏为首页标题栏
//                vPager.setLayoutParams(params[0]);// 设置vp除去topbar和底部导航显示整屏
//                vPager.setLayoutParams(params[0]);// 设置vp除去topbar显示整屏 隐藏首页导航
                break;
            case R.id.rb_fahuo:// 发货
                rb_fahuo.setChecked(true);
                vPager.setCurrentItem(1, false);// 设置当前vp显示的Fragment页面
                setCurrentTopTitle(1);// 设置当前顶部的标题栏为首页标题栏
//                vPager.setLayoutParams(params[1]);// 设置vp除去topbar和底部导航显示整屏
                break;
            case R.id.rb_jishihuoyuan:// 即时货源
                rb_jishihuoyuan.setChecked(true);
                vPager.setCurrentItem(2, false);// 设置当前vp显示的Fragment页面
                setCurrentTopTitle(2);// 设置当前顶部的标题栏为首页标题栏
//                vPager.setLayoutParams(params[2]);// 设置vp除去topbar和底部导航显示整屏

                break;
            case R.id.rb_seeksupply:// 找货
//                badgeView.setBadgeCount(0);
                rb_seeksupply.setChecked(true);
                vPager.setCurrentItem(3, false);// 设置当前vp显示的Fragment页面
                setCurrentTopTitle(3);// 设置当前顶部的标题栏为首页标题栏
//                vPager.setLayoutParams(params[3]);// 设置vp除去topbar和底部导航显示整屏
                break;
            case R.id.rb_me:// 我的
                rb_me.setChecked(true);
                vPager.setCurrentItem(4, false);// 设置当前vp显示的Fragment页面
                setCurrentTopTitle(4);// 设置当前顶部的标题栏为首页标题栏
//                vPager.setLayoutParams(params[4]);// 设置vp除去topbar和底部导航显示整屏
                break;
        }
    }

    /**
     * 添加顶部所有views到集合
     * @param views
     */
    public void addAllViews(View[] views) {
        for (int i = 0; i < views.length; i++) {
            topViews[i] = views[i];
        }
    }

    /**
     * 添加fragment到fms集合
     * @param fragments
     */
    public void addFragment(Fragment... fragments) {
        for (int i = 0; i < fragments.length; i++) {
            fms.add(fragments[i]);
        }
    }

    /**
     * 设置当前顶部的标题栏
     */
    public void setCurrentTopTitle(int id) {
//        if (id == 0 || id == 4) {
        if (id == 0) {
            ll_top.setVisibility(View.GONE);
        } else {
            ll_top.setVisibility(View.VISIBLE);
            ll_top.removeAllViews();
            ll_bottom.setVisibility(View.VISIBLE);
            this.cTopView = topViews[id];
            ll_top.addView(this.cTopView);
        }
    }

    public void initMonitor() {
        // 导航按钮监听
        rg_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                toSkip(checkedId);// 跳转至选中RadioButton所对应的fragment
            }
        });
    }

    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
        if (SPU.getPreferenceBoolean(MainActivity.this, SPU.IS_FABU_SUPPLIES, false, SPU.STORE_SETTINGS)) {
            SPU.setPreferenceValueBoolean(MainActivity.this, SPU.IS_FABU_SUPPLIES, false, SPU.STORE_SETTINGS);
            rb_fahuo.setChecked(true);
            vPager.setCurrentItem(1, false);// 设置当前vp显示的Fragment页面
            setCurrentTopTitle(1);// 设置当前顶部的标题栏为首页标题
//            vPager.setLayoutParams(params[1]);
            sendSupplyFragment.lazyLoad();
            return;
        } else if (SPU.getPreferenceBoolean(MainActivity.this, SPU.ISOFFENLINE, false, SPU.STORE_SETTINGS)) {
            SPU.setPreferenceValueBoolean(MainActivity.this, SPU.ISOFFENLINE, false, SPU.STORE_SETTINGS);
            rb_jishihuoyuan.setChecked(true);
            vPager.setCurrentItem(2, false);// 设置当前vp显示的Fragment页面
            setCurrentTopTitle(2);// 设置当前顶部的标题栏为首页标题栏
//            vPager.setLayoutParams(params[2]);
            instantSupplyFragment.upDateItemAdd(MainActivity.this);
            return;
        } else if (SPU.getPreferenceBoolean(MainActivity.this, SPU.IS_GRRZ, false, SPU.STORE_SETTINGS)) {
            SPU.setPreferenceValueBoolean(MainActivity.this, SPU.IS_GRRZ, false, SPU.STORE_SETTINGS);
            rb_me.setChecked(true);
            vPager.setCurrentItem(4, false);// 设置当前vp显示的Fragment页面
            setCurrentTopTitle(4);// 设置当前顶部的标题栏为首页标题栏
//            vPager.setLayoutParams(params[4]);
            myFragment.lazyLoad();
            return;
        } else if (SPU.getPreferenceBoolean(MainActivity.this, SPU.IS_SUPPLYDETAIL, false, SPU.STORE_SETTINGS)) {
            SPU.setPreferenceValueBoolean(MainActivity.this, SPU.IS_SUPPLYDETAIL, false, SPU.STORE_SETTINGS);
            rb_seeksupply.setChecked(true);
            vPager.setCurrentItem(3, false);// 设置当前vp显示的Fragment页面
            setCurrentTopTitle(3);// 设置当前顶部的标题栏为首页标题栏
//            vPager.setLayoutParams(params[3]);
//            seekSupplyFragment.refreshLeftView();
            return;
        }
//        bindService(locationServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    public void setHandlerPostDataSuccess(int connectionId, Object data) throws JSONException {
        ResponseParams<LinkedHashMap> iParams = jiexiData(data);
        if (connectionId == Constants.txsc) {
            TU.show(this, "上传成功！");
            myFragment.updateTX();
        }
    }

    @Override
    public void setHandlerPostDataError(int connectedId, Request request, HttpException httpException, String params, String content) throws JSONException {

    }

    @Override
    public void setHandlerGetDataSuccess(int connectionId, Object data) throws JSONException {

    }

    @Override
    public void setHandlerGetDataError(int connectionId, Object data) throws JSONException {

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (seekSupplyFragment.isDismiss()) {
                seekSupplyFragment.dismiss();
            } else {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    Toast.makeText(getApplicationContext(), "再按一次退出程序",
                            Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                } else {
                    MLog.i("返回键退出程序时启动服务！");
                    App.fontsize = true;
                    startService(new Intent(this, LocationServiceForGaoDe.class));
                    finish();
                }
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int arg0) {
        switch (arg0) {
            case 0:
                rb_home.setChecked(true);
                break;
            case 1:
                rb_fahuo.setChecked(true);
                break;
            case 3:
                rb_seeksupply.setChecked(true);
                break;
            case 4:
                rb_me.setChecked(true);
                break;
            case 2:
                rb_jishihuoyuan.setChecked(true);
//			myFragment.loadMyInfo();
                break;
            default:
                setCurrentTopTitle(arg0);
                break;
        }
    }

    public SupplyDetailEdite info_main = new SupplyDetailEdite();
    private Region shengfrom;
    private Region shifrom;
    private Region xianqufrom;
    private Region shengto;
    private Region shito;
    private Region xianquto;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == 100) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                info_main.cityfrom = bundle.getInt("didianId");
                info_main.cityfromname = bundle.getString("didian");
                shengfrom = (Region) bundle.getSerializable("sheng");
                shifrom = (Region) bundle.getSerializable("shi");
                xianqufrom = (Region) bundle.getSerializable("xianqu");
                rb_seeksupply.setChecked(true);
                vPager.setCurrentItem(3, false);// 设置当前vp显示的Fragment页面
                setCurrentTopTitle(3);// 设置当前顶部的标题栏为首页标题栏
//                vPager.setLayoutParams(params[3]);
                seekSupplyFragment.refreshView(info_main, shengfrom, shifrom, xianqufrom);
            }
        } else if (requestCode == 200) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                info_main.cityto = bundle.getInt("didianId");
                info_main.citytoname = bundle.getString("didian");
                shengto = (Region) bundle.getSerializable("sheng");
                shito = (Region) bundle.getSerializable("shi");
                xianquto = (Region) bundle.getSerializable("xianqu");
                rb_seeksupply.setChecked(true);
                vPager.setCurrentItem(3, false);// 设置当前vp显示的Fragment页面
                setCurrentTopTitle(3);// 设置当前顶部的标题栏为首页标题栏
//                vPager.setLayoutParams(params[3]);
                seekSupplyFragment.refreshView2(info_main, shengto, shito, xianquto);
            }
        }

        switch (requestCode) {
            case ImageUtils.GET_IMAGE_BY_CAMERA:// 拍照获取图片
                if (resultCode == this.RESULT_OK) {
                    // uri传入与否影响图片获取方式,以下二选一
                    // 方式一,自定义Uri(ImageUtils.imageUriFromCamera),用于保存拍照后图片地址
                    if (ImageUtils.imageUriFromCamera != null) {
                        // 可以直接显示图片,或者进行其他处理(如压缩或裁剪等)
//					 iv_image.setImageURI(ImageUtils.imageUriFromCamera);
                        // 对图片进行裁剪
                        ImageUtils.cropImage(this, ImageUtils.imageUriFromCamera);
                        break;
                    }
                }
                break;
            case ImageUtils.GET_IMAGE_FROM_PHONE:// 手机相册获取图片
                if (resultCode != this.RESULT_CANCELED) {
                    if (data != null && data.getData() != null) {
                        // 可以直接显示图片,或者进行其他处理(如压缩或裁剪等)
                        // iv.setImageURI(data.getData());
                        // 对图片进行裁剪
                        ImageUtils.cropImage(this, data.getData());
                    }
                }
                break;
            case ImageUtils.CROP_IMAGE:// 裁剪图片后结果
                if (resultCode == this.RESULT_OK) {
                    if (ImageUtils.cropImageUri != null) {
                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            Uri photouri = ImageUtils.cropImageUri;
//						photouri 》 content://media/external/images/media/124013
                            localPath = ImageUtils.getImagePathFromUri(this, photouri);
                            ///localPath > /storage/emulated/0/DCIM/Camera/1458277806433.jpg
                            //setPicToView2(ImageUtils.getImagePathFromUri(this, photouri));
                            //uploadImg();
                            //从图库拿到Bitmap压缩后给控件设置
                            ImageCompress.CompressOptions options = new ImageCompress.CompressOptions();
                            options.maxHeight = 40;
                            options.maxWidth = 40;
//						    options.uri = data.getData();
                            options.uri = photouri;
                            ImageCompress imageCompress = new ImageCompress();
                            bitmap = imageCompress.compressFromUri(this, options);

                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                            byte[] imageData = byteArrayOutputStream.toByteArray();
//						    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageData);
                            String datas = Base64.encodeToString(imageData, Base64.DEFAULT);//编码
//                            raiv_userphoto.setImageURI(photouri);
                            imgdata = datas;

                            sendPostRequest(
                                    Constants.txsc,
                                    new ParamsService().requestKV(
                                            "imgdata", imgdata,
                                            "imgtype", "headimage",
                                            "phone", App.phone,
                                            Constants.txsc),
                                    this,
                                    false);
                        }
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isForeground = false;
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setMyTags("clear,clear");
        MLog.d("MainActivity-onDestroy");
        unregisterReceiver(mMessageReceiver);
//      unbindService(serviceConnection);
    }

//    private LocationServiceForGaoDe locationService2;
//    ServiceConnection serviceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            locationService2 = ((LocationServiceForGaoDe.LocationBinder) service).getService();
//            updateStatus();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            locationService2 = null;
//        }
//    };


    private void updateStatus() {
//        if (locationService.isTracking()){
//            TU.show(mContext,String.format("Tracking ecabled. %d locations logged.",locationService.getLocationCount()));
//        }else{
//            TU.show(mContext,"Tracking not currently enabled.");
//        }
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
                String extras = intent.getStringExtra(KEY_EXTRAS);
//                MLog.i("JPush", "极光自定义消息: " + messge + " | " + extras);
//                极光自定义消息: Exit | null
                SupplyItem si = new SupplyItem();
                try {
                    si = JSON.parseObject(messge.toString(), SupplyItem.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!"不限".equals(si.carlen) && !"配货".equals(si.carlen) && null != si.carlen && si.carlen.length() >= 1) {
                    float startcarlen = SPU.getPreferenceValueFloat(MainActivity.this, SPU.startcarlen, 0.0f, SPU.STORE_USERINFO);
                    float endcarlen = SPU.getPreferenceValueFloat(MainActivity.this, SPU.endcarlen, 0.0f, SPU.STORE_USERINFO);

                    float clen = Float.parseFloat(si.carlen.substring(0, si.carlen.length() - 1));
                    if (endcarlen != 0.0) {//不等于0.0的需要用车长设置做判断
                        if (startcarlen <= clen && clen <= endcarlen) {
                            sendMSG(context, si);
                        }
                    } else {//等于0.0的直接推送
                        sendMSG(context, si);
                    }
                } else {//是 不限 配货 和 null 时发送
                    sendMSG(context, si);
                }

            }
        }
    }

    private void sendMSG(Context context, SupplyItem si) {
        //发送系统状态栏通知
        // 图标
        int icon = R.drawable.ic_launcher;
        // 内容
        String content = getNotificationContent(si);
        // 随机生成的通知ID
        int nid = (int)(Math.random()*50);
        // 通知的意图
        Intent intent = new Intent(context, SupplyDetailActivity.class);
        intent.putExtra("vo", si.goodsid);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(
                this,
                nid,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);//创建pi的方式

//        PendingIntent.FLAG_CANCEL_CURRENT  有的话先取消当前的pi 再创建
//        PendingIntent.FLAG_NO_CREATE 有就创建 没有不创建
//        PendingIntent.FLAG_ONE_SHOT 只使用一次
//        PendingIntent.FLAG_UPDATE_CURRENT 如果有更新intent  没有就创建


        //API 11 以前的用法
//        Notification notification = new Notification(icon,content,System.currentTimeMillis());
//        Notification notification = new Notification(icon, "鸿宝天下", si.lastupdate);

        //API 11 以后的用法
//        Notification.Builder builder = new Notification.Builder(this);

        //兼容11以前的用法 v4支持包

        NotificationCompat.Builder compat = new NotificationCompat.Builder(this);
        compat.setSmallIcon(icon);//设置小图标
        compat.setContentTitle("鸿运天下");//标题
        compat.setContentText(content);//内容
        compat.setAutoCancel(true);//设置点击或右划可被自动删除
//        compat.setOngoing(true);//设置为常驻通知
        compat.setDefaults(Notification.DEFAULT_ALL);//震动 铃声 + 闪烁都有
        compat.setNumber(nid);//右下角的数量
        compat.setTicker(content);//未下拉时显示的内容
        compat.setContentIntent(pi);//点击的事件处理
        Notification n = compat.build();//创建一个通知对象

        //通知管理器发送通知
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        nm.cancel(12);//取消指定ID的通知
        nm.notify(nid,n);


        //4.1以后的256的inbox风格通知
        //设置大视图样式

//        NotificationCompat.Builder nc = new NotificationCompat.Builder(this);
//        nc.setSmallIcon(icon);//设置小图标
//        nc.setContentTitle("鸿运天下");//标题
//        nc.setContentText(content);//内容

//        NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
//        style.setBigContentTitle("吟诗作对");
//        style.addLine("长亭外古道边");
//        style.addLine("一行白鹭上青天");
//        style.addLine("长亭外");
//        nc.setStyle(style);
//        style.setSummaryText("作者：xastdm");
//        Notification notification = nc.build();

        //通知管理器发送通知
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        nm.cancel(12);//取消指定ID的通知
//        notificationManager.notify(++nid,notification);



        //进度通知
//        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//        builder.setSmallIcon(icon);
//        builder.setContentText("新版本正在更新中...");
//        builder.setContentTitle("鸿运天下");
//        builder.setProgress(100,0,false);
//
//        final NotificationManager nmprogrees = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        nmprogrees.notify(35,builder.build());
//
//
//        //模拟更新线程
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for(int progress = 0;progress<=100;progress+=5){
//                    builder.setProgress(100,progress,false);
//                    nmprogrees.notify(35,builder.build());
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
////                builder.setProgress(0,0,true);//会一直动
//                builder.setProgress(0,0,false);
//                builder.setContentText("更新完成");
//                nmprogrees.notify(35,builder.build());
//            }
//        }).start();

        //自定义通知视图
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        //创建一个远程视图
//        RemoteViews rv = new RemoteViews(getPackageName(),R.layout.notification_custom);
//        rv.setTextViewText(R.id.geming,"第一滴泪");
//        rv.setImageViewResource();
//        rv.setTextViewText(R.id.play,"暂停");
//        rv.setOnClickFillInIntent();  //点击整个的
//        rv.setOnClickPendingIntent(R.id.play,);//设置按钮的单击事件
//        builder.setContent(rv);
//        builder.setOngoing(true);//设置常驻通知
//        builder.setTicker("鸿宝音乐");
//        builder.setSmallIcon(icon);
//        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        nm.notify(33,builder.build());

        App.si = si;

        //发广播更新货源
        Intent msgIntent = new Intent(ShowLineActivity.UPDATE_RECEIVED_ACTION);
//        msgIntent.putExtra(ShowLineActivity.KEY_UPDATE, si);
        context.sendBroadcast(msgIntent);



        //更新货源数量
        if (instantSupplyFragment != null) {
            if (si != null) {
                instantSupplyFragment.updateNotice(si);
            }
        }





    }

    private String getNotificationContent(SupplyItem si) {
        String goodsname = si.goodsname;
        String wv = String.valueOf(si.wv);
        String unit = String.valueOf(si.unit);
        if(!SU.isEmpty(goodsname)){
            goodsname = si.goodsname;
        }else{
            goodsname = "";
        }
        if(!SU.isEmpty(wv)){
            wv = String.valueOf(si.wv);
        }else{
            wv = "";
        }
        if(!SU.isEmpty(unit)){
            unit = si.unit;
        }else{
            unit = "";
        }
        if(wv.endsWith(".0")) wv = wv.substring(0,wv.length()-2);
        return si.cityfromname+"-"+si.citytoname+"  "+goodsname +" "+ wv+ unit;
    }
}
