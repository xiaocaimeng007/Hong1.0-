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
import com.hongbao5656.base.App;
import com.hongbao5656.base.BaseActivity;
import com.hongbao5656.okhttp.HttpException;
import com.hongbao5656.util.Constants;
import com.hongbao5656.entity.OffenLine;
import com.hongbao5656.entity.SupplyItem;
import com.hongbao5656.okhttp.HttpDataHandlerListener;
import com.hongbao5656.pulltorefresh.PullToRefreshListView;
import com.hongbao5656.refreshlistview.xlistview.XListView;
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

public class ShowLineActivity extends BaseActivity
        implements HttpDataHandlerListener {

    private static final String TAG = "ShowLineActivity";
    private int cPage = 1;
    private int totalpage = 0;
    private int g;
    private View sendSupplyFragmentTop;
    private TextView tv_fahuoleft;
    private TextView tv_fahuoright;
    private PullToRefreshListView plv_sendsupplylist;
    private ListView plv;
    private SeekSupplyAdapter adapter;
    private List<SupplyItem> datas_supply = new ArrayList<SupplyItem>();
    private RelativeLayout rl_null;
    private RelativeLayout rl_fail;
    private XListView xListView;
    private Activity mContext;
    private OffenLine ol = new OffenLine();
    private TextView midTitle;
    private MessageReceiver mMessageReceiver;
    public static final String UPDATE_RECEIVED_ACTION = "com.hongbao.UPDATE_RECEIVED_ACTION";
    public static final String KEY_UPDATE = "KEY_UPDATE";
    private int allname;
    private Boolean refresh = true;
    private Boolean remore = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showline);
        this.mContext = ShowLineActivity.this;
        ol = (OffenLine) getIntent().getSerializableExtra("vo");
        allname = getIntent().getExtras().getInt("allname");
        initView();
        initMonitor();
        initDatas(cPage);
        App.flag = true;
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(UPDATE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        App.flag = false;
        unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }


    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            SupplyItem si = App.si;
            try {
                if (UPDATE_RECEIVED_ACTION.equals(intent.getAction())) {
//                    si = (SupplyItem) intent.getSerializableExtra(KEY_UPDATE);
                    if (allname == 1) {//全部线路
                        si.lastupdate += 1000*60*60*8;
                        adapter.updateListStart(si);
                    } else {//单条线路
                        if (null != si && !SU.isEmpty(si.JpushTags)) {
                            String[] str = si.JpushTags.split(",");
                            for (int i = 0; i < str.length; i++) {
                               if(str[i].trim().equals("1"+ol.cityfrom+ol.cityto)){
                                   si.lastupdate += 1000*60*60*8;
                                   adapter.updateListStart(si);
                                   break;
                               }
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                Writer writer = new StringWriter();
                PrintWriter printWriter = new PrintWriter(writer);
                ex.printStackTrace(printWriter);
                Throwable cause = ex.getCause();
                while (cause != null) {
                    cause.printStackTrace(printWriter);
                    cause = cause.getCause();
                }
                printWriter.close();
                String result = writer.toString();
                sendEMailDM("split_bug" + TAG, result, "");
                ex.printStackTrace();
            }
        }
    }

    private void requestAllLines(int cPage, float carlenstar, float carlenend) {
        sendPostRequest(
                Constants.requestAllLines,
                new ParamsService().requestAllLines(cPage, 10, carlenstar, carlenend),
                this, false);
    }

    private void initDatas(int cPage) {
        float carlenstar = SPU.getPreferenceValueFloat(this, SPU.startcarlen, 0.0f, SPU.STORE_USERINFO);
        float carlenend = SPU.getPreferenceValueFloat(this, SPU.endcarlen, 0.0f, SPU.STORE_USERINFO);
        if (allname == 1) {
            requestAllLines(cPage, carlenstar, carlenend);//30109
        } else {
            sendPostRequest(Constants.checksupply_seek,
                    new ParamsService().checksupply_seek(//30106
                            "", ol.cityfrom, ol.cityto, "", "", "", cPage, 10, carlenstar, carlenend),
                    this, false);
        }
    }

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
//        rl_right = (RelativeLayout)findViewById(R.id.rl_right);
        midTitle = (TextView) findViewById(R.id.midTitle);
        if (allname == 1) {
            midTitle.setText("全部常跑线路");
        } else {
            midTitle.setText(ol.cityfromname + ol.cityfromparentname + "--" + ol.citytoname + ol.citytoparentname);
        }
        xListView = (XListView) findViewById(R.id.xListView);
        rl_null = (RelativeLayout) findViewById(R.id.rl_null);
        rl_fail = (RelativeLayout) findViewById(R.id.rl_fail);
        adapter = new SeekSupplyAdapter(mContext, datas_supply);
//        adapter = new CommonAdapter<SupplyItem>(mContext,datas_supply,R.layout.item_xlv_seeksupply){
//            @Override
//            public void convert(ViewHolder viewHolder, SupplyItem item) {
//
//            }
//        };
        xListView.setAdapter(adapter);
        xListView.setPullLoadEnable(true);
        xListView.setPullRefreshEnable(true);
    }


    protected void initMonitor()  {
        adapter.setTelListener(new SeekSupplyAdapter.TelListener() {
            @Override
            public void onTelListener(String goodsid, String contactmobile) {
//				tel(goodsid);
                Uri uri = Uri.parse("tel:" + contactmobile);
                Intent it = new Intent(Intent.ACTION_DIAL, uri);
                mContext.startActivity(it);
            }
        });

        xListView.setXListViewListener(new XListView.IXListViewListener() {
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
            TU.show(this, "" + iParams.getErrmsg());
            openActivity(LoginActivity.class);
            finish();
            return;
        }
        switch (errcode) {
            case 50000://token过期   只清坐标
                TU.show(this, "" + iParams.getErrmsg());
                clearAndStopLocationData();//只清坐标
                openActivity(LoginActivity.class);
                finish();
                break;
            case 50001://第三方登录  清坐标 清个人信息 清推送
                TU.show(this, "" + iParams.getErrmsg());
                eixtAccount();
                openActivity(LoginActivity.class);
                finish();
                break;
            case 44444://接口异常 升级app
                TU.show(this, iParams.getErrmsg() + "-" + errcode);
                UpdateApp();
                if (10013 == connectionId || 10014 == connectionId) {
                    clearAndStopLocationData();
                }
                break;
            case 0://成功
                if (connectionId == Constants.checksupply_seek) {//找货
                    totalpage = IMap.getUFromResponse(iParams);
                    datas_supply = IMap.getData2FromResponse(iParams, SupplyItem.class);
                    if (datas_supply.size() == 0 && cPage == 1) {//第一页加载数据为空时
                        adapter.clearListView();
                        adapter.notifyDataSetChanged();
                        xListView.setEmptyView(rl_null);
                    } else {//显示数据
                        rl_null.setVisibility(View.GONE);
                        m_updateListView(datas_supply);
                    }
                    judgement();//是否可以继续加载
                } else if (connectionId == Constants.requestAllLines) {//查找所有常跑线路货源
                    totalpage = IMap.getUFromResponse(iParams);
                    datas_supply = IMap.getData2FromResponse(iParams, SupplyItem.class);
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
                break;
            default:
                TU.show(this, iParams.getErrmsg() + "-" + errcode);
                break;
        }

    }

    @Override
    public void setHandlerPostDataError(int connectedId, Request request, HttpException httpException, String params, String content) throws JSONException {
        refreshFlagError(connectedId);
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

    @Override
    public void setHandlerGetDataSuccess(int connectionId, Object data) throws JSONException {

    }

    @Override
    public void setHandlerGetDataError(int connectionId, Object data) throws JSONException {

    }

    /**
     * 上拉加载下拉刷新要用的
     */
    public void m_updateListView(List<SupplyItem> list) {
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
