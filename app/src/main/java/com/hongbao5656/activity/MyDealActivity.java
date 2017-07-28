package com.hongbao5656.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hongbao5656.R;
import com.hongbao5656.adapter.MyDealAdapter;
import com.hongbao5656.base.BaseActivity;
import com.hongbao5656.okhttp.HttpException;
import com.hongbao5656.util.Constants;
import com.hongbao5656.entity.Region;
import com.hongbao5656.entity.SeekCarsShow;
import com.hongbao5656.entity.SupplyDetailEdite;
import com.hongbao5656.entity.SupplyItem;
import com.hongbao5656.okhttp.HttpDataHandlerListener;
import com.hongbao5656.pulltorefresh.PullToRefreshListView;
import com.hongbao5656.refreshlistview.xlistview.XListView;
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
 * 货源
 */
public class MyDealActivity extends BaseActivity
        implements HttpDataHandlerListener {
    private int cPage = 1;
    private int totalpage = 0;
    private int g;
    private View sendSupplyFragmentTop;
    private TextView tv_fahuoleft;
    private TextView tv_fahuoright;
    private PullToRefreshListView plv_sendsupplylist;
    private ListView plv;
    private MyDealAdapter adapter;
    private List<SupplyItem> datas_supply = new ArrayList<SupplyItem>();
    private RelativeLayout rl_null;
    private RelativeLayout rl_fail;
    private XListView xListView;
    private Context mContext;
    private SupplyDetailEdite info = new SupplyDetailEdite();
    private RelativeLayout leftBt;
    private TextView tv_start;
    private TextView tv_end;
    private TextView tv_cccx;
    private Region shengfrom;
    private Region shifrom;
    private Region xianqufrom;
    private Region shengto;
    private Region shito;
    private Region xianquto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mydeal);
        this.mContext = MyDealActivity.this;
        initView();
        initMonitor();
        initDatas(cPage);
    }

    private void initDatas(int cPage) {
        sendPostRequest(
                Constants.seekcar,
                new ParamsService().seekcar(info, cPage),
                MyDealActivity.this, false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    protected void initView() {
        xListView = (XListView) findViewById(R.id.xListView);
        rl_null = (RelativeLayout) findViewById(R.id.rl_null);
        rl_fail = (RelativeLayout) findViewById(R.id.rl_fail);
        adapter = new MyDealAdapter(mContext, datas_supply);
        xListView.setAdapter(adapter);
        xListView.setPullLoadEnable(true);
        xListView.setPullRefreshEnable(true);
        leftBt = (RelativeLayout) findViewById(R.id.leftBt);
    }

    protected void initMonitor() {
        /*adapter.setDeleteListener(new MyDealAdapter.DeleteListener() {
			private AllDialog allDialog;
			@Override
			public void onDeleteListener(final String goodsid) {
				allDialog = new AllDialog(mContext);
				allDialog.setContent("您确认要删除吗？");
				allDialog.show();
				allDialog.btn_ok.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						deleteRequest(goodsid);
						if(allDialog!=null){
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
		});*/
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

        leftBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    void deleteRequest(String goodsid) {
        sendPostRequest(
                Constants.deletesupply,
                new ParamsService().requestKV("goodsid", goodsid, Constants.deletesupply), this, false);
    }

    @Override
    public void setHandlerPostDataSuccess(int connectionId, Object data) throws JSONException {
        ResponseParams<LinkedHashMap> iParams = jiexiData(data);
        if (connectionId == Constants.seekcar) {
            totalpage = IMap.getUFromResponse(iParams);
            datas_supply = IMap.getData2FromResponse(iParams, SeekCarsShow.class);
            if (datas_supply.size() == 0 && cPage == 1) {//第一页加载数据为空时
                adapter.clearListView();
                adapter.notifyDataSetChanged();
                xListView.setEmptyView(rl_null);
            } else {//显示数据
                rl_null.setVisibility(View.GONE);
                m_updateListView(datas_supply);
            }
            judgement();//是否可以继续加载
        } else if (connectionId == Constants.deletesupply) {
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
