package com.hongbao5656.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.example.aaron.library.MLog;
import com.hongbao5656.R;
import com.hongbao5656.adapter.CityLevelAdapter;
import com.hongbao5656.adapter.OilPrviceAdapter;
import com.hongbao5656.base.App;
import com.hongbao5656.base.BaseActivity;
import com.hongbao5656.entity.OilPrvice;
import com.hongbao5656.okhttp.HttpDataHandlerListener;
import com.hongbao5656.okhttp.HttpException;
import com.hongbao5656.okhttp.HttpRequest;
import com.hongbao5656.util.Constants;
import com.hongbao5656.util.SU;
import com.squareup.okhttp.Request;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DailyServiceActivity extends BaseActivity
        implements HttpDataHandlerListener
        ,PoiSearch.OnPoiSearchListener {
    private RelativeLayout rl_today_soil_prices;
    private OilPrviceAdapter mOilPrviceAdapter;
    private RelativeLayout default_title_bg;
    private PopupWindow mWindow;
    private ListView lv_ppw;
    private RelativeLayout rl_fjjyou;
    private PoiSearch.Query query;
    private PoiSearch poiSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dailyservice);
//        initViews();




    }

    private void initViews() {
//        返回
        findViewById(R.id.leftBt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWindow != null && mWindow.isShowing()) {
                    mWindow.dismiss();
                }else {
//                    SPU.setPreferenceValueBoolean(mContext, SPU.IS_FABU_SUPPLIES, true, SPU.STORE_SETTINGS);
                    finish();
                }
            }
        });
        //标题栏
        default_title_bg = (RelativeLayout)findViewById(R.id.default_title_bg);

        //今日油价
        rl_today_soil_prices = (RelativeLayout) findViewById(R.id.rl_today_soil_prices);
        rl_today_soil_prices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendGetRequest(
                        Constants.oilprvice,
                        new HttpRequest(new Request.Builder().url(App.URL_OILPRVICE).build()),
                        DailyServiceActivity.this);
            }
        });
        View ppw_todaysoilprice_layout = LayoutInflater.from(DailyServiceActivity.this).inflate(R.layout.ppw_todaysoilprice_layout, null);
        lv_ppw = (ListView) ppw_todaysoilprice_layout.findViewById(R.id.lv_ppw);
        mWindow = new PopupWindow(ppw_todaysoilprice_layout,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        //附近加油
        rl_fjjyou = (RelativeLayout)findViewById(R.id.rl_fjjyou);
//        rl_fjjyou.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                query = new PoiSearch.Query("加油站", "", cityCode);
////keyWord表示搜索字符串，
////第二个参数表示POI搜索类型，二者选填其一，
////POI搜索类型共分为以下20种：汽车服务|汽车销售|
////汽车维修|摩托车服务|餐饮服务|购物服务|生活服务|体育休闲服务|医疗保健服务|
////住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|交通设施服务|
////金融保险服务|公司企业|道路附属设施|地名地址信息|公共设施
////cityCode表示POI搜索区域，可以是城市编码也可以是城市名称，也可以传空字符串，空字符串代表全国在全国范围内进行搜索
//                query.setPageSize(10);// 设置每页最多返回多少条poiitem
//                query.setPageNum(1);//设置查询页码
//
//                poiSearch = new PoiSearch(DailyServiceActivity.this, query);
//                poiSearch.setOnPoiSearchListener(DailyServiceActivity.this);
//
//                poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(locationMarker.getPosition().latitude,
//                        locationMarker.getPosition().longitude), 1000));//设置周边搜索的中心点以及半径
//
//                poiSearch.searchPOIAsyn();
//            }
//        });
    }

    @Override
    public void setHandlerPostDataSuccess(int connectionId, Object data) throws JSONException {

    }

    @Override
    public void setHandlerPostDataError(int connectedId, Request request, HttpException httpException, String params, String content) throws JSONException {

    }
    List<OilPrvice> datas;
    @Override
    public void setHandlerGetDataSuccess(int connectionId, Object data) throws JSONException {
        if (Constants.oilprvice == connectionId) {
            datas = new ArrayList<OilPrvice>();
            org.json.JSONObject jsonObject = new org.json.JSONObject(JSON.parseObject(data.toString()).get("result").toString());
            Iterator iterator = jsonObject.keys();
            while(iterator.hasNext()){
                String value = jsonObject.getString((String) iterator.next());
                JSONObject jo = JSON.parseObject(value);
                OilPrvice oilPrvice = new OilPrvice();
                oilPrvice.province = jo.get("province").toString();
                oilPrvice.gasoline90= jo.get("gasoline90").toString();
                oilPrvice.gasoline93 = jo.get("gasoline93").toString();
                oilPrvice.gasoline97 = jo.get("gasoline97").toString();
                oilPrvice.dieselOil0 = jo.get("dieselOil0").toString();
                datas.add(oilPrvice);
            }

            if (mWindow != null && mWindow.isShowing()) {
                mWindow.dismiss();
                return;
            }

            if (mOilPrviceAdapter == null) {
                mOilPrviceAdapter = new OilPrviceAdapter(DailyServiceActivity.this, datas);
                lv_ppw.setAdapter(mOilPrviceAdapter);
            }else{
                mOilPrviceAdapter.upateList(datas);
            }

            mWindow.setAnimationStyle(R.style.PopupWindowAnimation);
            mWindow.setOutsideTouchable(true);
            mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            mWindow.setFocusable(true);
            mWindow.setTouchable(true);
            mWindow.setBackgroundDrawable(getResources().getDrawable(R.color.base_top_bg_color));
            mWindow.showAsDropDown(default_title_bg, 0, 0);
        }
    }

    @Override
    public void setHandlerGetDataError(int connectionId, Object data) throws JSONException {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWindow != null && mWindow.isShowing()) {
                mWindow.dismiss();
            }else {
                finish();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
//        dissmissProgressDialog();// 隐藏对话框
        if (rCode == 1000) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
//                    poiResult = result;
//                    // 取得搜索到的poiitems有多少页
//                    List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
//                    List<SuggestionCity> suggestionCities = poiResult
//                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
//
//                    if (poiItems != null && poiItems.size() > 0) {
//                        aMap.clear();// 清理之前的图标
//                        PoiOverlay poiOverlay = new PoiOverlay(aMap, poiItems);
//                        poiOverlay.removeFromMap();
//                        poiOverlay.addToMap();
//                        poiOverlay.zoomToSpan();
//                    } else if (suggestionCities != null
//                            && suggestionCities.size() > 0) {
//                        showSuggestCity(suggestionCities);
//                    } else {
//                        ToastUtil.show(PoiKeywordSearchActivity.this,
//                                R.string.no_result);
//                    }
                }
            } else {
//                ToastUtil.show(PoiKeywordSearchActivity.this,
//                        R.string.no_result);
            }
        } else {
//            ToastUtil.showerror(this, rCode);
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }
}
