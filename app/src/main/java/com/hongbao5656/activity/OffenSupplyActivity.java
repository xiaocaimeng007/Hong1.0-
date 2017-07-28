package com.hongbao5656.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.hongbao5656.R;
import com.hongbao5656.adapter.OffenSupplyAdapter;
import com.hongbao5656.base.BaseActivity;
import com.hongbao5656.okhttp.HttpException;
import com.hongbao5656.util.Constants;
import com.hongbao5656.entity.SupplyItem;
import com.hongbao5656.okhttp.HttpDataHandlerListener;
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

public class OffenSupplyActivity extends BaseActivity implements HttpDataHandlerListener {
    private int cPage = 1;
    private int totalpage = 0;
    private RelativeLayout rl_null;
    private List<SupplyItem> datas_supply = new ArrayList<SupplyItem>();
    private OffenSupplyAdapter adapter;
    private ListView lv_offensupply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offensupply);
        initView();
        initMonitor();
    }


    private void initView() {
        findViewById(R.id.leftBt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initDatas();
        lv_offensupply = (ListView) findViewById(R.id.lv_offensupply);
        rl_null = (RelativeLayout) findViewById(R.id.rl_null);
        adapter = new OffenSupplyAdapter(this, datas_supply);
        lv_offensupply.setAdapter(adapter);
    }

    private void initMonitor() {
        adapter.setOffenSupplyAdapterListener(new OffenSupplyAdapter.OffenSupplyAdapterListener() {
            private AllDialog allDialog;

            @Override
            public void onDeleteListener(final String goodsid) {
                allDialog = new AllDialog(OffenSupplyActivity.this);
                allDialog.setContent("您确认要删除吗？");
                allDialog.show();
                allDialog.btn_ok.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        deleteRequest(goodsid);
                        if (allDialog != null) {
                            allDialog.dismiss();
                        }
                    }
                });
                allDialog.btn_fangqi.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        allDialog.dismiss();
                    }
                });
            }

            @Override
            public void onCVClickListener(SupplyItem vo) {
                //A-B-C再到-B   销毁BC 重建B  对A没影响
                Intent intent = new Intent(OffenSupplyActivity.this, PublishSupplyActivity.class);
                intent.putExtra("offen", vo);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initDatas() {
        sendPostRequest(
                Constants.OFFENSUPPLY,
                new ParamsService().request(Constants.OFFENSUPPLY),
                this, false);
    }

    void deleteRequest(String isid) {
        sendPostRequest(
                Constants.DELETEOFFENSUPPLY,
                new ParamsService().requestKV("isid", isid, Constants.DELETEOFFENSUPPLY),
                this, false);
    }

    @Override
    public void setHandlerPostDataSuccess(int connectionId, Object data) throws JSONException {
        ResponseParams<LinkedHashMap> iParams = jiexiData(data);
        if (connectionId == Constants.OFFENSUPPLY) {
            datas_supply = IMap.getData2FromResponse(iParams, SupplyItem.class);
            if (datas_supply.size() == 0 && cPage == 1) {//第一页加载数据为空时
                adapter.clearListView();
                adapter.notifyDataSetChanged();
                lv_offensupply.setEmptyView(rl_null);
            } else {//显示数据
                rl_null.setVisibility(View.GONE);
                m_updateListView(datas_supply);
            }
        } else if (connectionId == Constants.DELETEOFFENSUPPLY) {
            TU.show(OffenSupplyActivity.this, "删除成功");
            initDatas();
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
        adapter.clearListView();
        adapter.updateList(list);
    }


}
