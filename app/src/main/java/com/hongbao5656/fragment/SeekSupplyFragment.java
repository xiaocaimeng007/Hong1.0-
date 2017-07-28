package com.hongbao5656.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aaron.library.MLog;
import com.hongbao5656.R;
import com.hongbao5656.activity.LoginActivity;
import com.hongbao5656.activity.MainActivity;
import com.hongbao5656.activity.SelectPlaceActivity;
import com.hongbao5656.adapter.CityLevelAdapter2;
import com.hongbao5656.adapter.CityLevelAdapter3;
import com.hongbao5656.adapter.SeekSupplyAdapter;
import com.hongbao5656.adapter.SeekSupplyAdapter.TelListener;
import com.hongbao5656.base.App;
import com.hongbao5656.base.BaseFragment;
import com.hongbao5656.db.DBHelper;
import com.hongbao5656.okhttp.HttpException;
import com.hongbao5656.util.Constants;
import com.hongbao5656.entity.Region;
import com.hongbao5656.entity.SupplyDetailEdite;
import com.hongbao5656.entity.SupplyItem;
import com.hongbao5656.okhttp.HttpDataHandlerListener;
import com.hongbao5656.refreshlistview.xlistview.XListView;
import com.hongbao5656.util.IMap;
import com.hongbao5656.util.NU;
import com.hongbao5656.util.ParamsService;
import com.hongbao5656.util.ResponseParams;
import com.hongbao5656.util.SPU;
import com.hongbao5656.util.SU;
import com.hongbao5656.util.TU;
import com.squareup.okhttp.Request;
//import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 找货 2015/11/23
 *
 * @author dm
 */
public class SeekSupplyFragment extends BaseFragment
        implements HttpDataHandlerListener {
    private static final String TAG = "SeekSupplyFragment";
    protected boolean isPrepared;
    private View rootView;
    private int totalpage = 0;
    private MainActivity mActivity;
    private SeekSupplyAdapter adapter;
    private List<SupplyItem> datas_supply = new ArrayList<SupplyItem>();
    private TextView tv_start;
    private TextView tv_end;
    private TextView tv_cccx;
    private XListView xListView;
    private String lineid = "";
    private int cityfrom = 0;
    private int cityto = 0;
    private String carlen = "";
    private String cartype = "";
    private String sort = "";
    private int page = 1;
    private int pagesize = 10;
    private RelativeLayout rl_null;
    private PopupWindow mWindow_car;
    private View view_car;
    private GridView gv_ppw_carlong;
    private GridView gv_ppw_cartype;
    private CityLevelAdapter2 cityLevelAdapter2;
    private CityLevelAdapter3 cityLevelAdapter3;
    private Button queding;
    private Boolean refresh = true;
    private Boolean remore = true;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof MainActivity) {
            mActivity = (MainActivity) activity;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MLog.i("找货");
        rootView = inflater.inflate(R.layout.fragment_seeksupply, null);
        isPrepared = true;
        initView();
        initMonitor();
        lazyLoad();
        return rootView;
    }

    protected void initView() {
        tv_start = (TextView) rootView.findViewById(R.id.tv_start);
        tv_end = (TextView) rootView.findViewById(R.id.tv_end);
        tv_cccx = (TextView) rootView.findViewById(R.id.tv_cccx);
        xListView = (XListView) rootView.findViewById(R.id.xListView);
        rl_null = (RelativeLayout) rootView.findViewById(R.id.rl_null);
        adapter = new SeekSupplyAdapter(mActivity, datas_supply);
        xListView.setAdapter(adapter);
        xListView.setPullLoadEnable(true);
        xListView.setPullRefreshEnable(true);
        initPPW();
    }

    private void initPPW() {
        view_car = LayoutInflater.from(mActivity).inflate(R.layout.ppw_carlongtype_layout, null);
        queding = (Button) view_car.findViewById(R.id.queding);
        queding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carlen = cityLevelAdapter2.getSelectItem();
                cartype = cityLevelAdapter3.getSelectItem();
                dismissAll();
                tv_cccx.setText(carlen + "  " + cartype);
                page = 1;
                pagesize = 10;
                initDatas(lineid, cityfrom, cityto, carlen, cartype, sort, page, pagesize);
            }
        });
        gv_ppw_carlong = (GridView) view_car.findViewById(R.id.gv_ppw_carlong);
        gv_ppw_cartype = (GridView) view_car.findViewById(R.id.gv_ppw_cartype);

        mWindow_car = new PopupWindow(view_car, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
    }

    private void dismissAll() {
        if (mWindow_car != null && mWindow_car.isShowing()) {
//					if (SU.isEmpty(carlen)||SU.isEmpty(cartype)){
//						TU.show(mActivity,"请选择车长");
//						return;
//					}else if(){
//						TU.show(mActivity,"请选择车型");
//						return;
//					}else{
            mWindow_car.dismiss();
            return;
//					}
        }
    }

    public boolean isDismiss() {
        return mWindow_car != null && mWindow_car.isShowing();
    }

    public void dismiss() {
        mWindow_car.dismiss();
    }

    protected void initMonitor() {
        tv_cccx.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (mWindow_car != null && mWindow_car.isShowing()) {
                    mWindow_car.dismiss();
                    return;
                }
                ArrayList<String> list = new ArrayList<>();
                String[] ss = SPU.getPreferenceValueString
                        (mActivity, SPU.CARLEN, "",
                                SPU.STORE_SETTINGS).split(",");
                for (int i = 0; i < ss.length; i++) {
                    list.add(ss[i]);
                }

                ArrayList<String> list2 = new ArrayList<>();
                String[] ss2 = SPU.getPreferenceValueString
                        (mActivity, SPU.CARTYPE, "",
                                SPU.STORE_SETTINGS).split(",");
                for (int i = 0; i < ss2.length; i++) {
                    list2.add(ss2[i]);
                }

                if (cityLevelAdapter2 == null) {
                    cityLevelAdapter2 = new CityLevelAdapter2(mActivity, list);
                    gv_ppw_carlong.setAdapter(cityLevelAdapter2);
                } else {
                    cityLevelAdapter2.clearListView();
                    cityLevelAdapter2.upateList(list);
                }


                if (cityLevelAdapter3 == null) {
                    cityLevelAdapter3 = new CityLevelAdapter3(mActivity, list2);
                    gv_ppw_cartype.setAdapter(cityLevelAdapter3);
                } else {
                    cityLevelAdapter3.clearListView();
                    cityLevelAdapter3.upateList(list2);
                }

                mWindow_car.setAnimationStyle(R.style.PopupWindowAnimation);
                mWindow_car.setOutsideTouchable(true);
                mWindow_car.setFocusable(true);
                mWindow_car.setTouchable(true);
                mWindow_car.setBackgroundDrawable(getResources().getDrawable(R.color.base_top_bg_color));
                mWindow_car.showAsDropDown(tv_cccx, 0, 0);
//				creatDialog_cc(view);
            }
        });
        xListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                xListView.setRefreshTime(System.currentTimeMillis());
                xListView.stopRefresh();
                if (refresh) {
                    if (!NU.checkNetworkAvailable(mActivity)) {
                        TU.show(mActivity, getString(R.string.no_net), Toast.LENGTH_LONG);
                        return;
                    }
                    initDatas(lineid, cityfrom, cityto, carlen, cartype, sort, page, pagesize);
                    refresh = false;
                    page = 1;
                }
            }

            @Override
            public void onLoadMore() {
                xListView.setRefreshTime(System.currentTimeMillis());
                xListView.stopLoadMore();
                if (remore) {
                    if (!NU.checkNetworkAvailable(mActivity)) {
                        TU.show(mActivity, getString(R.string.no_net), Toast.LENGTH_LONG);
                        return;
                    }
                    initDatas(lineid, cityfrom, cityto, carlen, cartype, sort, page, pagesize);
                    remore = false;
                    page++;
                }
            }
        });

        tv_start.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAll();
                Intent intent = new Intent(SeekSupplyFragment.this.getActivity(), SelectPlaceActivity.class);
                intent.putExtra("type", 1);
                intent.putExtra("shengfrom", shengfrom);
                intent.putExtra("shifrom", shifrom);
                intent.putExtra("xianqufrom", xianqufrom);
                SeekSupplyFragment.this.getActivity().startActivityForResult(intent, 100);
            }
        });
        tv_end.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAll();
                Intent intent = new Intent(SeekSupplyFragment.this.getActivity(), SelectPlaceActivity.class);
                intent.putExtra("shengto", shengto);
                intent.putExtra("shito", shito);
                intent.putExtra("xianquto", xianquto);
                intent.putExtra("type", 2);
                SeekSupplyFragment.this.getActivity().startActivityForResult(intent, 200);
            }
        });

        adapter.setTelListener(new TelListener() {
            @Override
            public void onTelListener(String goodsid, String contactmobile) {
                tel(goodsid);
                Uri uri = Uri.parse("tel:" + contactmobile);
                Intent it = new Intent(Intent.ACTION_DIAL, uri);
                mActivity.startActivity(it);
            }
        });
    }

    private void tel(String stringExtra) {
        sendPostRequest(
                Constants.tel,
                new ParamsService().requestKV("goodsid", stringExtra, Constants.tel),
                this,
                false);
    }

    @Override
    public void lazyLoad() {
        MLog.i("找货2");
//        MLog.i("fragment生命周期：执行了->lazyLoad()方法 测试返回还会执行lazyLoad方法1");
        String city = SPU.getPreferenceValueString(mActivity.getApplicationContext(), SPU.CURRENTCITY, "", SPU.STORE_USERINFO);
        String lastcity = SPU.getPreferenceValueString(mActivity.getApplicationContext(), SPU.LASTCURRENTCITY, "", SPU.STORE_USERINFO);
        if(!SU.isEmpty(city)){
            if(!city.equals(lastcity)){
                MLog.i("找货3");
                isPrepared = true;
                App.fontsize = true;
            }
        }
//        if (isVisible && isPrepared) {
//            isPrepared = false;
            if (App.fontsize) {
                MLog.i("找货4");
                App.fontsize = false;
                //if (!SU.isEmpty(city)) {
                if (!SU.isEmpty(city)) {//去掉定位
                    MLog.i("找货5");
                    Region sde = DBHelper.getInstance(
                            mActivity.getApplicationContext())
                            .queryRegion(mActivity.getApplicationContext(), city);
                    if (sde != null) {
                        if (sde.Id > 0 && sde.Id < 820001) {
                            cityfrom = sde.Id;
                            tv_start.setText(city);
                            SPU.setPreferenceValueString(mActivity.getApplicationContext(), SPU.LASTCURRENTCITY, city, SPU.STORE_USERINFO);
                        }
                    }
                } else {
                    MLog.i("找货6");
                    tv_start.setText("出发地");
                    cityfrom = 0;
                }
//
                tv_end.setText("目的地");
                tv_cccx.setText("车长车型");
//                cityfrom = 0;
                cityto = 0;
                carlen = "";
                cartype = "";
                page = 1;
                pagesize = 10;
                MLog.i("找货7");
                initDatas(lineid, cityfrom, cityto, carlen, cartype, sort, page, pagesize);
//                MLog.i("fragment生命周期：执行了->lazyLoad()方法 测试返回还会执行lazyLoad方法2");
                MLog.i("找货8");
            }
//        }
    }


    public void refreshLeftView() {
        if (null != adapter) {
            adapter.notifyDataSetChanged();
        }
    }


    private void initDatas(String lineid, int cityfrom, int cityto, String carlen, String cartype, String sort, int page, int pagesize) {
        sendPostRequest(
                Constants.checksupply_seek,
                new ParamsService().checksupply_seek(lineid, cityfrom, cityto, carlen, cartype, sort, page, pagesize, 0.0f, 0.0f),
                this, false);
    }

    /**
     * 上拉加载下拉刷新要用的
     */
    public void m_updateListView(List<SupplyItem> list) {
        if (page == 1) {
            adapter.clearListView();
            adapter.updateList(list);
        } else {
            adapter.updateList(list);
        }
    }

    /**
     * 判断加载到最后一页时只允许下拉刷新
     */
    public void judgement() {
        if (totalpage <= page) {
            xListView.setPullLoadEnable(false);
            xListView.setPullRefreshEnable(true);
        } else {
            xListView.setPullLoadEnable(true);
            xListView.setPullRefreshEnable(true);
        }
    }

    private Region shengfrom;
    private Region shifrom;
    private Region xianqufrom;
    private Region shengto;
    private Region shito;
    private Region xianquto;

    public void refreshView(SupplyDetailEdite info_main, Region shengfrom, Region shifrom, Region xianqufrom) {
        cityfrom = info_main.cityfrom;
        this.shengfrom = shengfrom;
        this.shifrom = shifrom;
        this.xianqufrom = xianqufrom;
        if (null != tv_start) {
            tv_start.setText(info_main.cityfromname);
            page = 1;
            pagesize = 10;
            initDatas(lineid, cityfrom, cityto, carlen, cartype, sort, page, pagesize);
        }
    }

    public void refreshView2(SupplyDetailEdite info_main, Region shengto, Region shito, Region xianquto) {
        cityto = info_main.cityto;
        this.shengto = shengto;
        this.shito = shito;
        this.xianqufrom = xianqufrom;
        if (null != tv_end) {
            tv_end.setText(info_main.citytoname);
            page = 1;
            pagesize = 10;
            initDatas(lineid, cityfrom, cityto, carlen, cartype, sort, page, pagesize);
        }
    }

    @Override
    public void setHandlerPostDataSuccess(int connectionId, Object data) throws JSONException {
        refreshFlagSuccess(connectionId);
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
                if (connectionId == Constants.checksupply_seek) {
                    totalpage = IMap.getUFromResponse(iParams);
                    datas_supply = IMap.getData2FromResponse(iParams, SupplyItem.class);
                    if (datas_supply.size() == 0 && page == 1) {//第一页加载数据为空时
                        adapter.clearListView();
                        adapter.notifyDataSetChanged();
                        xListView.setEmptyView(rl_null);
                    } else {//显示数据
                        rl_null.setVisibility(View.GONE);
                        m_updateListView(datas_supply);
                    }
                    judgement();//是否可以继续加载
                } else if (connectionId == Constants.tel) {
                    if (errcode == 0) {
                        //				TU.show(mActivity, "拨打电话");
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
        refreshFlagError(connectedId);
    }

    //请求成功或失败 解析错误 内网环境等情况下标志的重置
    private void refreshFlagSuccess(int connectedId) {
        if (connectedId == Constants.checksupply_seek) {
            if (1 == page) {
                refresh = true;
            } else {
                remore = true;
            }
        }
    }

    //请求成功或失败 解析错误 内网环境等情况下标志的重置
    private void refreshFlagError(int connectedId) {
        if (connectedId == Constants.checksupply_seek) {
            if (1 == page) {
                refresh = true;
            } else {
                page--;
                remore = true;
            }
        }
    }

    @Override
    public void setHandlerGetDataSuccess(int connectionId, Object data)
            throws JSONException {
    }

    @Override
    public void setHandlerGetDataError(int connectionId, Object data) throws JSONException {

    }


}
