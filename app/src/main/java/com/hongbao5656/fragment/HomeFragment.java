package com.hongbao5656.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.hongbao5656.R;
import com.hongbao5656.activity.AddOffenLineActivity;
import com.hongbao5656.activity.EmptyCarActivity;
import com.hongbao5656.activity.LoginActivity;
import com.hongbao5656.activity.MainActivity;
import com.hongbao5656.activity.PublishSupplyActivity;
import com.hongbao5656.activity.SeekCarShowActivity;
import com.hongbao5656.activity.SeekMyCarActivity;
import com.hongbao5656.activity.SetCarLenActivity;
import com.hongbao5656.base.App;
import com.hongbao5656.base.BaseFragment;
import com.hongbao5656.entity.Advertisement;
import com.hongbao5656.entity.Area;
import com.hongbao5656.entity.EmptyCar;
import com.hongbao5656.okhttp.HttpDataHandlerListener;
import com.hongbao5656.okhttp.HttpException;
import com.hongbao5656.util.Constants;
import com.hongbao5656.util.IMap;
import com.hongbao5656.util.ParamsService;
import com.hongbao5656.util.ResponseParams;
import com.hongbao5656.util.SPU;
import com.hongbao5656.util.TU;
import com.squareup.okhttp.Request;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 首页 2015/11/23
 *
 * @author dm
 */
public class HomeFragment extends BaseFragment implements
        OnClickListener, HttpDataHandlerListener {
    private static final String TAG = "HomeFragment";

    private View rootView;
    /**
     * 标记是否初始化完成 即是否执行了onCreateView方法
     */
    protected boolean isPrepared;
    private MainActivity mActivity;
    /**
     * 广告条相关
     */
    private ImageView[] indicators;//指示器集合
    private LinearLayout ll_vptop_indicator;//指示器的线性布局
    protected int mImgSelect = 0;
    private List<String> imgUrlList = new ArrayList<String>();//图片url集合
    private ImageView[] ads;//广告集合
    private ViewPager vp_topad;//广告的vp
    private ScheduledExecutorService sechExecutorService;
    private ImageView iv_fbhy;
    private ImageView iv_czcy;
    private ImageView iv_xxdd1;
    private ImageView iv_kcsb;
    private ImageView iv_cpxl;
    private ImageView iv_xxdd2;

    @Override
    public void onAttach(Activity activity) {
//		onAttach一般是用来获取对父activity的引用
        super.onAttach(activity);
        if (activity instanceof MainActivity) {
            mActivity = (MainActivity) activity;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, null);
        initView();
        initMonitor();
        isPrepared = true;
        lazyLoad();
        initads();
        return rootView;
    }

    private void initads() {
        try {
            /* 初始化指示器 */
            indicators = new ImageView[3];
            for (int i = 0; i < indicators.length; i++) {
                ImageView imageView = new ImageView(mActivity);
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
                lp1.leftMargin = 20;
                imageView.setLayoutParams(lp1);
                indicators[i] = imageView;
                if (i == 0) {
                    indicators[i].setImageResource(R.drawable.dot_red);
                } else {
                    indicators[i].setImageResource(R.drawable.dot_white);
                }
                ll_vptop_indicator.addView(imageView);
            }

			/* 初始化广告条 */
            ads = new ImageView[3];
            for (int i = 0; i < ads.length; i++) {
                ImageView imageView = new ImageView(mActivity);
                imageView.setScaleType(ScaleType.CENTER_CROP);
                imageView.setClickable(true);
                imageView.setFocusable(true);
                ads[i] = imageView;
            }
            // 设置Adapter
            vp_topad.setAdapter(new AdAdapter());
            // 设置监听，主要是设置点点的背景
            vp_topad.setOnPageChangeListener(new OnPageChangeListener() {
                @Override
                public void onPageSelected(int arg0) {
                    mImgSelect = arg0;
                    setImageBackground(arg0);
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {
                }

                @Override
                public void onPageScrollStateChanged(int arg0) {
                }
            });
            vp_topad.setCurrentItem(ads.length * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        sechExecutorService = Executors.newSingleThreadScheduledExecutor();
        sechExecutorService.scheduleAtFixedRate(new ScrollTask(), 3, 3, TimeUnit.SECONDS);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (sechExecutorService != null) {
            sechExecutorService.shutdown();
        }
    }

    @Override
    public void onClick(View view) {

    }

    /**
     * 广告图切换的Task
     */
    private class ScrollTask implements Runnable {
        public void run() {
            synchronized (vp_topad) {
                mImgSelect++;
                mHandler.sendEmptyMessage(1);
            }
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    vp_topad.setCurrentItem(mImgSelect);
                    setImageBackground(mImgSelect);
                    break;
            }

        }
    };

    /**
     * 设置选中的指示器的背景
     *
     * @param selectItems
     */
    private void setImageBackground(int selectItems) {
        for (int i = 0; i < indicators.length; i++) {
            if (i == selectItems % ads.length) {
                indicators[i].setImageResource(R.drawable.dot_red);
            } else {
                indicators[i].setImageResource(R.drawable.dot_white);
            }
        }
    }

    @Override
    public void lazyLoad() {
//		if (!isPrepared || !isVisible) {
//			return;// 即没有执行onCreatView方法或者fragment不可见时不继续执行下面的代码
//		}
        // 填充各控件的数据 仅在初始化完成且可见的时才继续加载，这样的避免了未初始化完成就使用而带来的问题。

        // ===============广告墙================
//		adveriseRequest();
//		updateversion();
        //int count = dbHelper.queryNotifications(activity, "false");
        //badgeView.setBadgeCount(count);// 设置提醒的数字


        if (super.isVisible && isPrepared) {
            isPrepared = false;
//            requestOffenLine();
            adveriseRequest();
            updateversion();
            toast();
//            MLog.i("fragment生命周期：fragment可见&&onCreateView执行了才执行->lazyLoad()方法");
        }
    }

    public void updateversion() {
        sendPostRequest(
                Constants.UPDATETAB,
                new ParamsService().requestKV("updateversion", SPU.getPreferenceValueString(mActivity, SPU.UPDATETAB, App.updatetab, SPU.STORE_SETTINGS), Constants.UPDATETAB),
                this,
                false);
    }

    /**
     * 主页信息提示
     */
    private void toast() {
        sendPostRequest(
                Constants.TOAST,
                new ParamsService().request(Constants.TOAST),
                this,
                false
        );
    }

    /**
     * 首页广告数据接口
     */
    private void adveriseRequest() {
        sendPostRequest(
                Constants.ADVERISEREQUEST,
                new ParamsService().request(Constants.ADVERISEREQUEST),
                this,
                false);
    }

    @Override
    protected void initMonitor() {
//		rl_seeksupply.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				mActivity.toSkip(R.id.rb_information_page);
//			}
//		});
        iv_fbhy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.startActivity(new Intent(mActivity, PublishSupplyActivity.class));
            }
        });
        iv_cpxl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.startActivity(new Intent(mActivity, AddOffenLineActivity.class));
            }
        });
        iv_czcy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.startActivity(new Intent(mActivity, SeekCarShowActivity.class));
            }
        });
        iv_kcsb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.startActivity(new Intent(mActivity, EmptyCarActivity.class));
            }
        });
        iv_xxdd2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.startActivity(new Intent(mActivity, SetCarLenActivity.class));
            }
        });
        iv_xxdd1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//				TU.show(mActivity,"开发中");
                mActivity.startActivity(new Intent(mActivity, SeekMyCarActivity.class));
            }
        });
    }

    @Override
    public void setHandlerPostDataSuccess(int connectionId, Object data) throws JSONException {
        int errcode = -1;
        ResponseParams<LinkedHashMap> iParams = null;
        if (null != data) {
            iParams = IMap.jieXiResponse(data.toString());
            errcode = iParams.getErrcode();
        }
        switch (errcode) {
            case 50000://token过期   只清坐标
                TU.show(mActivity, "" + iParams.getErrmsg());
                mActivity.clearAndStopLocationData();//只清坐标
                openActivity(LoginActivity.class);
                mActivity.finish();
                break;
            case 50001://第三方登录  清坐标 清个人信息 清推送
                TU.show(mActivity, "" + iParams.getErrmsg());
                eixtAccount();
                openActivity(LoginActivity.class);
                mActivity.finish();
                break;
            case 44444://接口异常 升级app
                TU.show(mActivity, iParams.getErrmsg() + "-" + errcode);
                UpdateApp();
                if (10013 == connectionId || 10014 == connectionId) {
                    clearAndStopLocationData();
                }
                break;
            case 0://成功
                if (connectionId == Constants.ADVERISEREQUEST) {// 首页广告
                    List<Advertisement> list = JSON.parseArray(
                            JSON.toJSONString(iParams.getData2()),
                            Advertisement.class);
                    for (int i = 0; i < list.size(); i++) {
                        imgUrlList.add(list.get(i).getUrl());
                    }
                } else if (connectionId == Constants.UPDATETAB) {//表数据更新
                    SPU.setPreferenceValueString(mActivity,
                            SPU.UPDATETAB, iParams.getData1().get("updateversion").toString(), SPU.STORE_SETTINGS);
                    if (!(iParams.getData1().get("tb_app_unit").toString()).equals("")) {//重量体积单位
                        SPU.setPreferenceValueString(mActivity,
                                SPU.WV, iParams.getData1().get("tb_app_unit").toString(), SPU.STORE_SETTINGS);
                    }
                    if (!(iParams.getData1().get("tb_app_carlen").toString()).equals("")) {//车长
                        SPU.setPreferenceValueString(mActivity,
                                SPU.CARLEN, iParams.getData1().get("tb_app_carlen").toString(), SPU.STORE_SETTINGS);
                    }
                    if (!(iParams.getData1().get("tb_app_cartype").toString()).equals("")) {//车型
                        SPU.setPreferenceValueString(mActivity,
                                SPU.CARTYPE, iParams.getData1().get("tb_app_cartype").toString(), SPU.STORE_SETTINGS);
                    }
                    if (!(iParams.getData1().get("tb_app_goodstype").toString()).equals("")) {//货物
                        SPU.setPreferenceValueString(mActivity,
                                SPU.HUOLEI, iParams.getData1().get("tb_app_goodstype").toString(), SPU.STORE_SETTINGS);
                    }
                    if (!(iParams.getData1().get("vw_emptytime").toString()).equals("[]")) {//空车上报时间
                        List<EmptyCar> list = JSON.parseArray(
                                JSON.toJSONString(iParams.getData1().get("vw_emptytime")),
                                EmptyCar.class);
                        StringBuffer sb = new StringBuffer();
                        for (int i = 0; i < list.size(); i++) {
                            EmptyCar emptyCar = list.get(i);
                            sb.append(emptyCar.displaytext);
                            if (i != list.size() - 1) {
                                sb.append(",");
                            }
                        }
                        StringBuffer sb2 = new StringBuffer();
                        for (int i = 0; i < list.size(); i++) {
                            EmptyCar emptyCar = list.get(i);
                            sb2.append(emptyCar.value + "");
                            if (i != list.size() - 1) {
                                sb2.append(",");
                            }
                        }
                        SPU.setPreferenceValueString(mActivity,
                                SPU.EMPTYCAR, sb.toString(), SPU.STORE_SETTINGS);
                        SPU.setPreferenceValueString(mActivity,
                                SPU.EMPTYCAR2, sb2.toString(), SPU.STORE_SETTINGS);
                    }
                    if (!(iParams.getData1().get("tb_app_company").toString()).equals("[]")) {//区域列表
                        List<Area> list = JSON.parseArray(
                                JSON.toJSONString(iParams.getData1().get("tb_app_company")),
                                Area.class);
                        StringBuffer sb = new StringBuffer();
                        for (int i = 0; i < list.size(); i++) {
                            Area area = list.get(i);
                            sb.append(area.companyname);
                            if (i != list.size() - 1) {
                                sb.append(",");
                            }
                        }
                        StringBuffer sb2 = new StringBuffer();
                        for (int i = 0; i < list.size(); i++) {
                            Area area = list.get(i);
                            sb2.append(area.companyid + "");
                            if (i != list.size() - 1) {
                                sb2.append(",");
                            }
                        }
                        SPU.setPreferenceValueString(mActivity,
                                SPU.AREA, sb.toString(), SPU.STORE_SETTINGS);
                        SPU.setPreferenceValueString(mActivity,
                                SPU.AREA2, sb2.toString(), SPU.STORE_SETTINGS);
//				if(!(iParams.getData1().get("tb_app_place").toString()).equals("[]")){
//					new Thread(){
//						public void run() {
//							List<Region> list = JSON.parseArray(
//									JSON.toJSONString(iParams.getData1().get("tb_app_place")),
//									Region.class);
//							DBHelper.getInstance(mActivity).deleteDatabase(mActivity);
//							App.flag = false;
//							DBHelper dbHelper = DBHelper.getInstance(mActivity);
//							for (int i = 0; i < list.size(); i++) {
//								Region region = list.get(i);
//								ContentValues values = new ContentValues();
//								values.put("Id", region.Id);
//								values.put("ParentId", region.ParentId);
//								values.put("Auxiliary", region.Auxiliary);
//								values.put("Name", region.Name);
//								values.put("lat", region.lat);
//								values.put("mlong", region.mlong);
//								dbHelper.insertDatas(values);
//							}
//							App.flag = true;
//						};
//					}.start();
//				}
                    }
                } else if (connectionId == Constants.TOAST) {//温馨提示
                    if (errcode == 0) {
                        String msg = iParams.getData1().get("msgtext").toString();
                        String isshow = iParams.getData1().get("isshow").toString();
                        if ("2".equals(isshow)) {//不弹内容
                            SPU.setPreferenceValueBoolean(mActivity, SPU.TOAST, false, SPU.STORE_SETTINGS);
                            return;
                        } else if ("0".equals(isshow)) {//无数次
                            SPU.setPreferenceValueBoolean(mActivity, SPU.TOAST, false, SPU.STORE_SETTINGS);
                            showToastDialog(msg);
                        }else if("1".equals(isshow)){
                            if (!SPU.getPreferenceBoolean(mActivity, SPU.TOAST, true, SPU.STORE_SETTINGS)
                                    ||!SPU.getPreferenceValueString(mActivity, SPU.TOASTMSG, "", SPU.STORE_SETTINGS).equals(msg)) {
                                showToastDialog(msg);
                                SPU.setPreferenceValueBoolean(mActivity, SPU.TOAST, true, SPU.STORE_SETTINGS);
                                SPU.setPreferenceValueString(mActivity, SPU.TOASTMSG, msg, SPU.STORE_SETTINGS);
                            }
                        }
                    }
                }
                break;
            default:
                TU.show(mActivity, iParams.getErrmsg() + "-" + errcode);
                break;
        }

    }

    @Override
    public void setHandlerPostDataError(int connectedId, Request request, HttpException httpException, String params, String content) throws JSONException {

    }

    @Override
    public void setHandlerGetDataSuccess(int connectionId, Object data) throws JSONException {

    }


    public void showToastDialog(String msg) {
        Dialog toastDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("温馨提示：");
        builder.setMessage(msg);
        builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
//		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//				initData();
//			}
//		});
        builder.setCancelable(false);
        toastDialog = builder.create();
        toastDialog.show();
    }



    @Override
    public void setHandlerGetDataError(int connectionId, Object data) throws JSONException {

    }

    /**
     * 广告的viewpager
     *
     * @author dm
     */
    public class AdAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //((ViewPager)container).removeView((View) object);
        }

        /**
         * 载入图片进去，用当前的position 除以 图片数组长度取余数
         */
        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView v = ads[position % ads.length];
            if (imgUrlList.size() == 0) {
                v.setBackgroundResource(R.drawable.banner1);
            } else {
                //			Glide.with(mActivity).load(imgUrlList.get(position%ads.length)).asBitmap()
                //			.override(800, 480).placeholder(R.drawable.ad_img).into(v);
                //				Glide.with(mActivity).load(imgUrlList.get(position%ads.length)).asBitmap().placeholder(R.drawable.ad_img).into(v);
                Picasso.with(mActivity).load(imgUrlList.get(position % ads.length)).into(v);
            }

            ViewParent vp = v.getParent();
            if (vp != null) {
                ViewGroup parent = (ViewGroup) vp;
                parent.removeView(v);
            }
            ((ViewPager) container).addView(v);
            v.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
//                    					Intent intent = new Intent(mActivity,AdDetailActivity.class);
//                    					intent.putExtra("info", adList.get(position%ads.length).getContent());
//                    					mActivity.startActivity(intent);
//                    					mActivity.overridePendingTransition(R.anim.push_left_in,
//                    							R.anim.push_left_out);
//                    					TU.show(mActivity, "广告条点击事件");
                }
            });
            return v;
        }
    }

    @Override
    protected void initView() {
        vp_topad = (ViewPager) rootView.findViewById(R.id.vp_topad);
        ll_vptop_indicator = (LinearLayout) rootView.findViewById(R.id.ll_vptop_indicator);
        iv_fbhy = (ImageView) rootView.findViewById(R.id.iv_fbhy);
        iv_czcy = (ImageView) rootView.findViewById(R.id.iv_czcy);
        iv_xxdd1 = (ImageView) rootView.findViewById(R.id.iv_xxdd1);
        iv_kcsb = (ImageView) rootView.findViewById(R.id.iv_kcsb);
        iv_cpxl = (ImageView) rootView.findViewById(R.id.iv_cpxl);
        iv_xxdd2 = (ImageView) rootView.findViewById(R.id.iv_xxdd2);
    }

}
