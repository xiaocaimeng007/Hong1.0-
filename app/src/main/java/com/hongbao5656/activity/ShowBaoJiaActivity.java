package com.hongbao5656.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hongbao5656.R;
import com.hongbao5656.adapter.SeekSupplyAdapter;
import com.hongbao5656.adapter.SeekSupplyBaoJiaAdapter;
import com.hongbao5656.base.App;
import com.hongbao5656.base.BaseActivity;
import com.hongbao5656.entity.OffenLine;
import com.hongbao5656.entity.SupplyItem;
import com.hongbao5656.entity.SupplyItemBaoJia;
import com.hongbao5656.okhttp.HttpDataHandlerListener;
import com.hongbao5656.okhttp.HttpException;
import com.hongbao5656.pulltorefresh.PullToRefreshListView;
import com.hongbao5656.refreshlistview.xlistview.XListView;
import com.hongbao5656.util.Constants;
import com.hongbao5656.util.IMap;
import com.hongbao5656.util.NU;
import com.hongbao5656.util.ParamsService;
import com.hongbao5656.util.ResponseParams;
import com.hongbao5656.util.SPU;
import com.hongbao5656.util.SU;
import com.hongbao5656.util.TU;
import com.squareup.okhttp.Request;

import org.json.JSONException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ShowBaoJiaActivity extends BaseActivity {

    private int cPage = 1;
    private int totalpage = 0;
    private int g;
    private View sendSupplyFragmentTop;
    private TextView tv_fahuoleft;
    private TextView tv_fahuoright;
    private PullToRefreshListView plv_sendsupplylist;
    private ListView plv;
    private SeekSupplyBaoJiaAdapter adapter;
    private List<SupplyItemBaoJia> datas_supply = new ArrayList<SupplyItemBaoJia>();
    private RelativeLayout rl_null;
    private RelativeLayout rl_fail;
    private XListView xListView;
    private Activity mContext;
    private TextView midTitle;
    public static final String UPDATE_RECEIVED_ACTION = "com.hongbao.UPDATE_RECEIVED_ACTION";
    public static final String KEY_UPDATE = "KEY_UPDATE";
    private int baojiaType;
    private Boolean refresh = true;
    private Boolean remore = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showbaojia);
        this.mContext = ShowBaoJiaActivity.this;
        baojiaType = getIntent().getExtras().getInt("baojiatype");

        initView();
        initMonitor();
        initDatas(cPage);
        App.flag = true;
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(UPDATE_RECEIVED_ACTION);
    }


    @Override
    protected void onDestroy() {
        App.flag = false;
        super.onDestroy();
    }


    private void initDatas(int cPage) {
        //加载数据
        sendPostRequest(Constants.goodsbaojiaList,
                new ParamsService().goodslist_baojia(baojiaType, cPage, 10),
                httpdata, false);

    }

    HttpDataHandlerListener httpdata = new HttpDataHandlerListener() {
        @Override
        public void setHandlerPostDataSuccess(int connectionId, Object data) throws JSONException {
            refreshFlagSuccess(connectionId);
            int errcode = -1;
            ResponseParams<LinkedHashMap> iParams = null;
            if (null != data) {
                iParams = IMap.jieXiResponse(data.toString());
                errcode = iParams.getErrcode();
            }

            if (connectionId == Constants.btn_login && errcode == 90000) {
                //TU.show(this, "" + iParams.getErrmsg());
                openActivity(LoginActivity.class);
                finish();
                return;
            }
            switch (errcode) {
                case 50000://token过期   只清坐标
//                    TU.show(this, "" + iParams.getErrmsg());
                    clearAndStopLocationData();//只清坐标
                    openActivity(LoginActivity.class);
                    finish();
                    break;
                case 50001://第三方登录  清坐标 清个人信息 清推送
//                    TU.show(this, "" + iParams.getErrmsg());
                    eixtAccount();
                    openActivity(LoginActivity.class);
                    finish();
                    break;
                case 44444://接口异常 升级app
//                    TU.show(this, iParams.getErrmsg() + "-" + errcode);
                    UpdateApp();
                    if (10013 == connectionId || 10014 == connectionId) {
                        clearAndStopLocationData();
                    }
                    break;
                case 0://成功
                    if (connectionId == Constants.goodsbaojiaList) {//找货
                        totalpage = IMap.getUFromResponse(iParams);
                        datas_supply = IMap.getData2FromResponse(iParams, SupplyItemBaoJia.class);
                        if (datas_supply.size() == 0 && cPage == 1) {//第一页加载数据为空时
                            adapter.clearListView();
                            adapter.notifyDataSetChanged();
                            xListView.setEmptyView(rl_null);
                        } else {//显示数据
                            rl_null.setVisibility(View.GONE);
                            m_updateListView(datas_supply);
                        }
                        judgement();//是否可以继续加载
                    }
                default:
                    //TU.show(this, iParams.getErrmsg() + "-" + errcode);
                    break;
            }
        }

        @Override
        public void setHandlerPostDataError(int connectedId, Request request, HttpException httpException, String params, String content) throws JSONException {
            refreshFlagError(connectedId);
        }

        @Override
        public void setHandlerGetDataSuccess(int connectionId, Object data) throws JSONException {

        }

        @Override
        public void setHandlerGetDataError(int connectionId, Object data) throws JSONException {

        }
    };

    @Override
    public void onResume() {
        super.onResume();
    }

    protected void initView() {
        findViewById(R.id.leftBt).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.finish();
            }
        });
        midTitle = (TextView) findViewById(R.id.midTitle);
        switch (baojiaType) {
            case 1:
                midTitle.setText("报价中");
                break;
            case 2:
                midTitle.setText("已中标");
                break;
            case 3:
                midTitle.setText("未中标");
                break;
        }

        xListView = (XListView) findViewById(R.id.xListView);
        rl_null = (RelativeLayout) findViewById(R.id.rl_null);
        rl_fail = (RelativeLayout) findViewById(R.id.rl_fail);
        adapter = new SeekSupplyBaoJiaAdapter(mContext, datas_supply);

        xListView.setAdapter(adapter);
        xListView.setPullLoadEnable(true);
        xListView.setPullRefreshEnable(true);
    }


    protected void initMonitor() {
        adapter.setTelListener(new SeekSupplyBaoJiaAdapter.TelListener() {
            @Override
            public void onTelListener(String goodsid, String contactmobile) {
                Uri uri = Uri.parse("tel:" + contactmobile);
                Intent it = new Intent(Intent.ACTION_DIAL, uri);
                mContext.startActivity(it);
            }
        });

        xListView.setXListViewListener(new XListView.IXListViewListener() {
            //下拉刷新
            @Override
            public void onRefresh() {
                xListView.setRefreshTime(System.currentTimeMillis());
                xListView.stopRefresh();
                if (refresh) {
                    if (!NU.checkNetworkAvailable(mContext)) {
                        TU.show(mContext, getString(R.string.no_net), Toast.LENGTH_LONG);
                        return;
                    }
                    cPage = 1;
                    initDatas(cPage);
                    refresh = false;
                }
            }

            //上拉加载更多
            @Override
            public void onLoadMore() {
                xListView.setRefreshTime(System.currentTimeMillis());
                xListView.stopLoadMore();
                if (remore) {
                    if (!NU.checkNetworkAvailable(mContext)) {
                        TU.show(mContext, getString(R.string.no_net), Toast.LENGTH_LONG);
                        return;
                    }
                    cPage++;
                    initDatas(cPage);
                    remore = false;
                }
            }
        });
    }


    //请求成功或失败 解析错误 内网环境等情况下标志的重置
    private void refreshFlagSuccess(int connectedId) {
        if (connectedId == Constants.checksupply_seek || connectedId == Constants.requestAllLines) {
            if (1 == cPage) {
                refresh = true;
            } else {
                remore = true;
            }
        }
    }

    //请求成功或失败 解析错误 内网环境等情况下标志的重置
    private void refreshFlagError(int connectedId) {
        if (connectedId == Constants.checksupply_seek || connectedId == Constants.requestAllLines) {
            if (1 == cPage) {
                refresh = true;
            } else {
                cPage--;
                remore = true;
            }
        }
    }


    /**
     * 上拉加载下拉刷新要用的
     */
    public void m_updateListView(List<SupplyItemBaoJia> list) {
        if (cPage == 1) {
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
        if (totalpage <= cPage) {
            xListView.setPullLoadEnable(false);
            xListView.setPullRefreshEnable(true);
        } else {
            xListView.setPullLoadEnable(true);
            xListView.setPullRefreshEnable(true);
        }
    }
}
