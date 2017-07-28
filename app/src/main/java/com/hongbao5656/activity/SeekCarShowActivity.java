package com.hongbao5656.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hongbao5656.R;
import com.hongbao5656.adapter.CityLevelAdapter2;
import com.hongbao5656.adapter.CityLevelAdapter3;
import com.hongbao5656.adapter.SeekCarsShowAdapter;
import com.hongbao5656.base.App;
import com.hongbao5656.base.BaseActivity;
import com.hongbao5656.okhttp.HttpException;
import com.hongbao5656.util.Constants;
import com.hongbao5656.entity.Region;
import com.hongbao5656.entity.SeekCarsShow;
import com.hongbao5656.entity.SupplyDetailEdite;
import com.hongbao5656.okhttp.HttpDataHandlerListener;
import com.hongbao5656.pulltorefresh.PullToRefreshListView;
import com.hongbao5656.refreshlistview.xlistview.XListView;
import com.hongbao5656.util.AllDialog;
import com.hongbao5656.util.IMap;
import com.hongbao5656.util.ParamsService;
import com.hongbao5656.util.ResponseParams;
import com.hongbao5656.util.SPU;
import com.hongbao5656.util.TU;
import com.squareup.okhttp.Request;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 货源
 */
public class SeekCarShowActivity extends BaseActivity
        implements HttpDataHandlerListener {
    private int cPage = 1;
    private int totalpage = 0;
    private int g;
    private View sendSupplyFragmentTop;
    private TextView tv_fahuoleft;
    private TextView tv_fahuoright;
    private PullToRefreshListView plv_sendsupplylist;
    private ListView plv;
    private SeekCarsShowAdapter adapter;
    private List<SeekCarsShow> datas_supply = new ArrayList<SeekCarsShow>();
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
        setContentView(R.layout.activity_seekcarshow);
        this.mContext = SeekCarShowActivity.this;
        initView();
        initMonitor();
//		info = (SupplyDetailEdite)getIntent().getSerializableExtra("info");
        initDatas(cPage);
//		lazyLoad();
    }

    private void initDatas(int cPage) {
        sendPostRequest(
                Constants.seekcar,
                new ParamsService().seekcar(info, cPage),
                SeekCarShowActivity.this, false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    protected void initView() {
        xListView = (XListView) findViewById(R.id.xListView);
        rl_null = (RelativeLayout) findViewById(R.id.rl_null);
        rl_fail = (RelativeLayout) findViewById(R.id.rl_fail);
        adapter = new SeekCarsShowAdapter(mContext, datas_supply);
        xListView.setAdapter(adapter);
        xListView.setPullLoadEnable(true);
        xListView.setPullRefreshEnable(true);
        leftBt = (RelativeLayout) findViewById(R.id.leftBt);

        tv_start = (TextView) findViewById(R.id.tv_start);
        tv_end = (TextView) findViewById(R.id.tv_end);
        tv_cccx = (TextView) findViewById(R.id.tv_cccx);
        initPPW();
    }

    private View view_car;
    private Button queding;
    private String carlen = "";
    private String cartype = "";
    private CityLevelAdapter2 cityLevelAdapter2;
    private CityLevelAdapter3 cityLevelAdapter3;
    private int page = 1;
    private int pagesize = 10;
    private GridView gv_ppw_carlong;
    private GridView gv_ppw_cartype;
    private PopupWindow mWindow_car;

    private void initPPW() {
        view_car = LayoutInflater.from(this).inflate(R.layout.ppw_carlongtype_layout, null);
        queding = (Button) view_car.findViewById(R.id.queding);
        queding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carlen = cityLevelAdapter2.getSelectItem();
                info.carlen = carlen;
                cartype = cityLevelAdapter3.getSelectItem();
                info.cartype = cartype;
                dismissAll();
                tv_cccx.setText(carlen + "  " + cartype);
                page = 1;
                pagesize = 10;
                initDatas(1);
            }
        });
        gv_ppw_carlong = (GridView) view_car.findViewById(R.id.gv_ppw_carlong);
        gv_ppw_cartype = (GridView) view_car.findViewById(R.id.gv_ppw_cartype);

        mWindow_car = new PopupWindow(view_car, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
    }

    protected void initMonitor() {
        adapter.setDeleteListener(new SeekCarsShowAdapter.DeleteListener() {
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

        leftBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_start.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAll();
                Intent intent = new Intent(SeekCarShowActivity.this, SelectPlaceActivity.class);
                intent.putExtra("type", 1);
                intent.putExtra("shengfrom", shengfrom);
                intent.putExtra("shifrom", shifrom);
                intent.putExtra("xianqufrom", xianqufrom);
                SeekCarShowActivity.this.startActivityForResult(intent, 100);
            }
        });
        tv_end.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAll();
                Intent intent = new Intent(SeekCarShowActivity.this, SelectPlaceActivity.class);
                intent.putExtra("shengto", shengto);
                intent.putExtra("shito", shito);
                intent.putExtra("xianquto", xianquto);
                intent.putExtra("type", 2);
                SeekCarShowActivity.this.startActivityForResult(intent, 200);
            }
        });

        tv_cccx.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (mWindow_car != null && mWindow_car.isShowing()) {
                    mWindow_car.dismiss();
                    return;
                }
                ArrayList<String> list = new ArrayList<>();
                String[] ss = SPU.getPreferenceValueString
                        (SeekCarShowActivity.this, SPU.CARLEN, "",
                                SPU.STORE_SETTINGS).split(",");
                for (int i = 0; i < ss.length; i++) {
                    list.add(ss[i]);
                }

                ArrayList<String> list2 = new ArrayList<>();
                String[] ss2 = SPU.getPreferenceValueString
                        (SeekCarShowActivity.this, SPU.CARTYPE, "",
                                SPU.STORE_SETTINGS).split(",");
                for (int i = 0; i < ss2.length; i++) {
                    list2.add(ss2[i]);
                }

                if (cityLevelAdapter2 == null) {
                    cityLevelAdapter2 = new CityLevelAdapter2(SeekCarShowActivity.this, list);
                    gv_ppw_carlong.setAdapter(cityLevelAdapter2);
                } else {
                    cityLevelAdapter2.clearListView();
                    cityLevelAdapter2.upateList(list);
                }


                if (cityLevelAdapter3 == null) {
                    cityLevelAdapter3 = new CityLevelAdapter3(SeekCarShowActivity.this, list2);
                    gv_ppw_cartype.setAdapter(cityLevelAdapter3);
                } else {
                    cityLevelAdapter3.clearListView();
                    cityLevelAdapter3.upateList(list2);
                }

                mWindow_car.setAnimationStyle(R.style.PopupWindowAnimation);
                mWindow_car.setOutsideTouchable(false);
                mWindow_car.setFocusable(false);
                mWindow_car.setBackgroundDrawable(getResources().getDrawable(R.color.base_top_bg_color));
                mWindow_car.showAsDropDown(tv_cccx, 0, 0);
//				creatDialog_cc(view);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == 100) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                info.cityfrom = bundle.getInt("didianId");
                shengfrom = (Region) bundle.getSerializable("sheng");
                shifrom = (Region) bundle.getSerializable("shi");
                xianqufrom = (Region) bundle.getSerializable("xianqu");
                tv_start.setText(bundle.getString("didian"));
                initDatas(1);
            }
        } else if (requestCode == 200) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                info.cityto = bundle.getInt("didianId");
                shengto = (Region) bundle.getSerializable("sheng");
                shito = (Region) bundle.getSerializable("shi");
                xianquto = (Region) bundle.getSerializable("xianqu");
                tv_end.setText(bundle.getString("didian"));
                initDatas(1);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
    public void m_updateListView(List<SeekCarsShow> list) {
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
