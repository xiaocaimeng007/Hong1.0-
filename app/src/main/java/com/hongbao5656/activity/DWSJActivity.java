package com.hongbao5656.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hongbao5656.R;
import com.hongbao5656.adapter.DWSJAdapter;
import com.hongbao5656.base.BaseActivity;
import com.hongbao5656.okhttp.HttpException;
import com.hongbao5656.util.Constants;
import com.hongbao5656.entity.SeekCarsShow;
import com.hongbao5656.entity.SupplyDetailEdite;
import com.hongbao5656.entity.SupplyItem;
import com.hongbao5656.okhttp.HttpDataHandlerListener;
import com.hongbao5656.refreshlistview.xlistview.XListView;
import com.hongbao5656.util.IMap;
import com.hongbao5656.util.ParamsService;
import com.hongbao5656.util.ResponseParams;
import com.hongbao5656.util.SU;
import com.hongbao5656.util.TU;
import com.squareup.okhttp.Request;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 货源
 */
public class DWSJActivity extends BaseActivity
        implements HttpDataHandlerListener {
    private int cPage = 1;
    private int totalpage = 0;
    private DWSJAdapter adapter;
    private List<SupplyItem> datas_supply = new ArrayList<SupplyItem>();
    private RelativeLayout rl_null;
    private RelativeLayout rl_fail;
    private XListView xListView;
    private Context mContext;
    private SupplyDetailEdite info = new SupplyDetailEdite();
    private RelativeLayout leftBt;
    private TextView tv_callcount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dwsj);
        this.mContext = DWSJActivity.this;
        initView();
        initMonitor();
        initDatas(cPage);
    }

    private void initDatas(int cPage) {
        sendPostRequest(
                Constants.seekcar,
                new ParamsService().seekcar(info, cPage),
                DWSJActivity.this, false);
    }

    protected void initView() {
        findViewById(R.id.leftBt).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        xListView = (XListView) findViewById(R.id.xListView);
        rl_null = (RelativeLayout) findViewById(R.id.rl_null);
        rl_fail = (RelativeLayout) findViewById(R.id.rl_fail);
        adapter = new DWSJAdapter(mContext, datas_supply);
        xListView.setAdapter(adapter);
        xListView.setPullLoadEnable(true);
        xListView.setPullRefreshEnable(true);
        tv_callcount = (TextView) findViewById(R.id.tv_callcount);
    }

    private void requestAddGuanZhu(String paccount) {
        sendPostRequest(
                Constants.addGaunZhu,
                new ParamsService().requestKV("toaccount", paccount, Constants.addGaunZhu),
                this, false);
    }

    protected void initMonitor() {
        adapter.setDWSJAdapterListener(new DWSJAdapter.DWSJAdapterListener() {
                                           @Override
                                           public void onWritePriceListener() {
                                               //填写报价
                                               AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                               View view = getLayoutInflater().inflate(R.layout.dialog_yqm, null);
                                               final EditText et_bj = ((EditText) view.findViewById(R.id.et_login_tel));
                                               et_bj.setHint("输入报价");
                                               final EditText et_login_tel = (EditText) view.findViewById(R.id.et_login_tel);
                                               builder.setView(view);
                                               builder.setTitle("填写报价：");
                                               builder.setCancelable(true);
                                               builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
                                                   @Override
                                                   public void onClick(DialogInterface dialog, int which) {
                                                       switch (which) {
                                                           case DialogInterface.BUTTON_POSITIVE:
                                                               String bj = et_bj.getText().toString();
                                                               if (SU.isEmpty(bj) || Integer.parseInt(bj) <= 0) {
                                                                   if (Integer.parseInt(bj) > 0)
                                                                       TU.show(DWSJActivity.this, "报价不对");
                                                                   return;
                                                               }
                                                               initInvite(bj);
                                                               break;
                                                           case DialogInterface.BUTTON_NEUTRAL:
                                                               TU.show(mContext, "取消");
                                                               dialog.cancel();
                                                               break;
                                                       }
                                                   }
                                               });
                                               builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
                                                   @Override
                                                   public void onClick(DialogInterface dialog, int which) {
                                                       switch (which) {
                                                           case DialogInterface.BUTTON_POSITIVE:
                                                               //TODO
                                                               break;
                                                           case DialogInterface.BUTTON_NEUTRAL:
                                                               dialog.cancel();
                                                               break;
                                                       }
                                                   }
                                               });
                                               AlertDialog dialog = builder.create();
                                               dialog.show();
                                           }

                                           @Override
                                           public void onAddOffenCarListener(String account) {
                                               //加入熟车
                                               if (account != null) {
                                                   requestAddGuanZhu(account);
                                               }
                                           }

                                           @Override
                                           public void onDWSJListener() {
                                               //途中定位
                                               //填写报价
                                               AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                               View view = getLayoutInflater().inflate(R.layout.dialog_yqm, null);
                                               final EditText et_bj = ((EditText) view.findViewById(R.id.et_login_tel));
                                               et_bj.setHint("输入报价");
                                               final EditText et_login_tel = (EditText) view.findViewById(R.id.et_login_tel);
                                               builder.setView(view);
                                               builder.setTitle("填写报价：");
                                               builder.setCancelable(true);
                                               builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
                                                   @Override
                                                   public void onClick(DialogInterface dialog, int which) {
                                                       switch (which) {
                                                           case DialogInterface.BUTTON_POSITIVE:
                                                               String bj = et_bj.getText().toString();
                                                               if (SU.isEmpty(bj) || Integer.parseInt(bj) <= 0) {
                                                                   if (Integer.parseInt(bj) > 0)
                                                                       TU.show(DWSJActivity.this, "报价不对");
                                                                   return;
                                                               }
                                                               initInvite(bj);
                                                               break;
                                                           case DialogInterface.BUTTON_NEUTRAL:
                                                               TU.show(mContext, "取消");
                                                               dialog.cancel();
                                                               break;
                                                       }
                                                   }
                                               });
                                               builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
                                                   @Override
                                                   public void onClick(DialogInterface dialog, int which) {
                                                       switch (which) {
                                                           case DialogInterface.BUTTON_POSITIVE:
                                                               //TODO
                                                               break;
                                                           case DialogInterface.BUTTON_NEUTRAL:
                                                               dialog.cancel();
                                                               break;
                                                       }
                                                   }
                                               });
                                               AlertDialog dialog = builder.create();
                                               dialog.show();
                                           }
                                       }

        );
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
                                       }

        );
    }

    public void initInvite(String yqm) {
        sendPostRequest(
                Constants.invite,
                new ParamsService().requestKV("InvitedCode", yqm, Constants.invite),
                DWSJActivity.this,
                false);
    }

    @Override
    public void setHandlerPostDataSuccess(int connectionId, Object data) throws JSONException {
        ResponseParams<LinkedHashMap> iParams =jiexiData(data);

        if (connectionId == Constants.seekcar) {//查找车源
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
        } else if (connectionId == Constants.addGaunZhu) {//添加关注
            TU.show(this, "加入成功");
        } else if (Constants.invite == connectionId) {
            //本地更新报价
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
