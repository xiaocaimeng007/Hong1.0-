package com.hongbao5656.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hongbao5656.R;
import com.hongbao5656.adapter.EmptyCarShowAdapter;
import com.hongbao5656.adapter.EmptyCarShowAdapter.DeleteListener;
import com.hongbao5656.base.BaseActivity;
import com.hongbao5656.okhttp.HttpException;
import com.hongbao5656.util.Constants;
import com.hongbao5656.entity.EmptyCarShowItem;
import com.hongbao5656.okhttp.HttpDataHandlerListener;
import com.hongbao5656.pulltorefresh.PullToRefreshListView;
import com.hongbao5656.refreshlistview.xlistview.XListView;
import com.hongbao5656.util.AllDialog;
import com.hongbao5656.util.IMap;
import com.hongbao5656.util.ParamsService;
import com.hongbao5656.util.ResponseParams;
import com.hongbao5656.util.TU;
import com.squareup.okhttp.Request;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 货
 */
public class EmptyCarShowActivity extends BaseActivity
        implements HttpDataHandlerListener {
    private int cPage = 1;
    private int totalpage = 0;
    private int g;
    private View sendSupplyFragmentTop;
    private TextView tv_fahuoleft;
    private TextView tv_fahuoright;
    private PullToRefreshListView plv_sendsupplylist;
    private ListView plv;
    private EmptyCarShowAdapter adapter;
    private List<EmptyCarShowItem> datas_supply = new ArrayList<EmptyCarShowItem>();
    private RelativeLayout rl_null;
    private RelativeLayout rl_fail;
    private XListView xListView;
    private Activity mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emptycarshow);
        this.mContext = EmptyCarShowActivity.this;
        initView();
        initMonitor();
//		lazyLoad();
        initDatas(1);
    }

    private void initDatas(int cPage) {
        sendPostRequest(Constants.emptycarseek,
                new ParamsService().requestKV(
                        "pageindex", cPage,
                        "pagesize", 10,
                        Constants.emptycarseek), this, false);
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
        xListView = (XListView) findViewById(R.id.xListView);
        rl_null = (RelativeLayout) findViewById(R.id.rl_null);
        rl_fail = (RelativeLayout) findViewById(R.id.rl_fail);
        adapter = new EmptyCarShowAdapter(mContext, datas_supply);
        xListView.setAdapter(adapter);
        xListView.setPullLoadEnable(true);
        xListView.setPullRefreshEnable(true);
    }

    protected void initMonitor() {
        adapter.setDeleteListener(new DeleteListener() {
            private AllDialog allDialog;

            @Override
            public void onDeleteListener(final String goodsid) {
                allDialog = new com.hongbao5656.util.AllDialog(mContext);
                allDialog.setContent("您确认要删除吗？");
                allDialog.show();
                allDialog.btn_ok.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        deleteRequest(goodsid);
                        if (allDialog != null) {
                            allDialog.dismiss();
                        }
                    }
                });
                allDialog.btn_fangqi.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        allDialog.dismiss();
                    }
                });
            }
        });
        xListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                xListView.setRefreshTime(System.currentTimeMillis());
                xListView.stopRefresh();
                cPage = 1;
                initDatas(cPage);
            }

            @Override
            public void onLoadMore() {
                xListView.setRefreshTime(System.currentTimeMillis());
                xListView.stopLoadMore();
                cPage++;
                initDatas(cPage);
            }
        });
    }


    void deleteRequest(String goodsid) {
        sendPostRequest(
                Constants.deleteemptycar,
                new ParamsService().requestKV("id", goodsid, Constants.deleteemptycar),
                this,
                false);
    }


    public void setHandlerData(int connectionId, ResponseParams<LinkedHashMap> iParams, int errcode)
            throws JSONException {

    }

    @Override
    public void setHandlerPostDataSuccess(int connectionId, Object data) throws JSONException {
        ResponseParams<LinkedHashMap> iParams =jiexiData(data);

        if (connectionId == Constants.emptycarseek) {
            totalpage = IMap.getUFromResponse(iParams);
            datas_supply = IMap.getData2FromResponse(iParams, EmptyCarShowItem.class);
            if (datas_supply.size() == 0 && cPage == 1) {//第一页加载数据为空时
                adapter.clearListView();
                adapter.notifyDataSetChanged();
                xListView.setEmptyView(rl_null);
            } else {//显示数据
                rl_null.setVisibility(View.GONE);
                m_updateListView(datas_supply);
            }
            judgement();//是否可以继续加载
        } else if (connectionId == Constants.deleteemptycar) {
            TU.show(mContext, "删除成功");
            initDatas(1);
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

    /**
     * 上拉加载下拉刷新要用的
     */
    public void m_updateListView(List<EmptyCarShowItem> list) {
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



