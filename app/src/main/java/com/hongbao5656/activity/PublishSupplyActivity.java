package com.hongbao5656.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;

import com.example.aaron.library.MLog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.hongbao5656.R;
import com.hongbao5656.adapter.CityLevelAdapter;
import com.hongbao5656.adapter.CityLevelAdapter2;
import com.hongbao5656.adapter.CityLevelAdapter3;
import com.hongbao5656.base.App;
import com.hongbao5656.base.BaseActivity;
import com.hongbao5656.okhttp.HttpException;
import com.hongbao5656.util.Constants;
import com.hongbao5656.db.DBHelper;
import com.hongbao5656.entity.Region;
import com.hongbao5656.entity.SupplyDetailEdite;
import com.hongbao5656.entity.SupplyItem;
import com.hongbao5656.okhttp.HttpDataHandlerListener;
import com.hongbao5656.util.IMap;
import com.hongbao5656.util.ParamsService;
import com.hongbao5656.util.ResponseParams;
import com.hongbao5656.util.SPU;
import com.hongbao5656.util.SU;
import com.hongbao5656.util.TU;
import com.squareup.okhttp.Request;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class PublishSupplyActivity extends BaseActivity implements
        HttpDataHandlerListener, CityLevelAdapter.CitySupplyListener {
    private Button btn_startsation;
    private Button btn_endsation;
    private SupplyDetailEdite info = new SupplyDetailEdite();
    private SupplyDetailEdite lastinfo = new SupplyDetailEdite();
    private EditText publish_hwmc_et;
    private EditText publish_zl_et;
    private EditText publish_xldd_sp;
    private EditText publish_bz_et;
    private Button publish_commit;
    private Context mContext;
    private EditText publish_et_name;
    private Button publish_weightunit_btn;
    private Region shengfrom;
    private Region shifrom;
    private Region xianqufrom;
    private Region shengto;
    private Region shito;
    private Region xianquto;
    private String cityfromname = "";
    private String citytoname = "";
    private LinearLayout huozhuxingming;
    private LinearLayout lianxidianhua;
    private LinearLayout beizhu;
    private int flag = 0;
    private View view;
    private View view_car;
    private GridView gv_ppw;
    private PopupWindow mWindow;
    private CityLevelAdapter cityLevelAdapter;
    private CityLevelAdapter2 cityLevelAdapter2;
    private CityLevelAdapter3 cityLevelAdapter3;
    private PopupWindow mWindow_car;
    private GridView gv_ppw_carlong;
    private GridView gv_ppw_cartype;
    private Button queding;
    private String carlong = "";
    private String cartype = "";
    private TextView tv_select;
    private Button publish_cx_btn;
    private TextView tv_cfhy;
    private RelativeLayout rl_cfhy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publishsupply);
        this.mContext = PublishSupplyActivity.this;

        if (getIntent().hasExtra("vo")) {
            requestEditSupply(getIntent().getStringExtra("vo"));
        } else {
            cityfromname = SPU.getPreferenceValueString(mContext, SPU.CityFromName, "", SPU.STORE_USERINFO);
            info.cityfrom = SPU.getPreferenceValueInt(mContext, SPU.CityFromNameID, 0, SPU.STORE_USERINFO);
            citytoname = SPU.getPreferenceValueString(mContext, SPU.CityToName, "", SPU.STORE_USERINFO);
            info.cityto = SPU.getPreferenceValueInt(mContext, SPU.CityToNameID, 0, SPU.STORE_USERINFO);
        }
        initView();
        initListener();
        if (getIntent().hasExtra("offen")) {
            SupplyItem vv = (SupplyItem) getIntent().getSerializableExtra("offen");
            setView(vv);
        }
    }

    private void requestEditSupply(String stringExtra) {
        sendPostRequest(
                Constants.editsupply2,
                new ParamsService().requestKV("goodsid", stringExtra, Constants.editsupply2),
                this,
                false);
    }


    private void initListener() {
        btn_startsation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SelectPlaceActivity.class);
                intent.putExtra("type", 1);
                intent.putExtra("shengfrom", shengfrom);
                intent.putExtra("shifrom", shifrom);
                intent.putExtra("xianqufrom", xianqufrom);
                startActivityForResult(intent, 100);
            }
        });
        btn_endsation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SelectPlaceActivity.class);
                intent.putExtra("shengto", shengto);
                intent.putExtra("shito", shito);
                intent.putExtra("xianquto", xianquto);
                intent.putExtra("type", 2);
                startActivityForResult(intent, 200);
            }
        });
        publish_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String cfd = btn_startsation.getText().toString().trim();
                String mdd = btn_endsation.getText().toString().trim();
                if (TextUtils.isEmpty(cfd)) {
                    TU.show(PublishSupplyActivity.this, "出发地为空");
                    return;
                }

                if (
                        !cfd.equals("北京") && !cfd.equals("天津") && !cfd.equals("上海") && !cfd.equals("重庆")
                                && !cfd.equals("台湾") && !cfd.equals("香港") && !cfd.equals("澳门")
                                && !cfd.equals("北京市-") && !cfd.equals("天津市-") && !cfd.equals("上海市-") && !cfd.equals("重庆市-")
                                && !cfd.equals("台湾省-") && !cfd.equals("香港特别行政区-") && !cfd.equals("澳门特别行政区-")
                                && 0 == DBHelper.getInstance(mContext).queryParentId(mContext, info.cityfrom)) {
                    TU.show(PublishSupplyActivity.this, "出发地不能为省份");
                    return;
                }

//                if (0== DBHelper.getInstance(mContext).queryParentId(mContext, info.cityfrom)){
//                    TU.show(PublishSupplyActivity.this, "出发地不能为省份或直辖市");
//                    return;
//                }

                if (TextUtils.isEmpty(btn_endsation.getText().toString().trim())) {
                    TU.show(PublishSupplyActivity.this, "目的地为空");
                    return;
                }

                if (
                        !mdd.equals("北京") && !mdd.equals("天津") && !mdd.equals("上海") && !mdd.equals("重庆")
                                && !mdd.equals("台湾") && !mdd.equals("香港") && !mdd.equals("澳门")
                                && !mdd.equals("北京市-") && !mdd.equals("天津市-") && !mdd.equals("上海市-") && !mdd.equals("重庆市-")
                                && !mdd.equals("台湾省-") && !mdd.equals("香港特别行政区-") && !mdd.equals("澳门特别行政区-")
                                && 0 == DBHelper.getInstance(mContext).queryParentId(mContext, info.cityto)) {
                    TU.show(PublishSupplyActivity.this, "目的地不能为省份");
                    return;
                }


//                if (TextUtils.isEmpty(publish_hwmc_et.getText().toString())) {
//                    TU.show(PublishSupplyActivity.this, "货物名称为空");
////                    return;
//                }


//                if (TextUtils.isEmpty(carlong)) {
//                    TU.show(PublishSupplyActivity.this, "车长为空");
//                    return;
//                }
//                if (TextUtils.isEmpty(cartype)) {
//                    TU.show(PublishSupplyActivity.this, "车型为空");
//                    return;
//                }
                if (TextUtils.isEmpty(publish_zl_et.getText().toString())) {
                    TU.show(PublishSupplyActivity.this, "重量/体积为空");
                    return;
                }

                if (publish_zl_et.getText().toString().toString().endsWith(".")) {
                    TU.show(PublishSupplyActivity.this, "重量/体积不能以.结尾");
                    return;
                }

                if (TextUtils.isEmpty(publish_et_name.getText().toString())) {
                    TU.show(PublishSupplyActivity.this, "货主姓名为空");
                    return;
                }

                SPU.setPreferenceValueString(mContext, SPU.USERNAME2, publish_et_name.getText().toString(), SPU.STORE_USERINFO);
                if (TextUtils.isEmpty(publish_xldd_sp.getText().toString())) {
                    TU.show(PublishSupplyActivity.this, "联系电话为空");
                    return;
                }

                info.goodsname = publish_hwmc_et.getText().toString().trim();
                info.carlen = carlong;
                info.cartype = cartype;
                info.wv = Float.parseFloat(publish_zl_et.getText().toString().trim());
                info.unit = publish_weightunit_btn.getText().toString();
                info.contact = publish_et_name.getText().toString().trim();
                info.contactmobile = publish_xldd_sp.getText().toString().trim();
                info.remark = publish_bz_et.getText().toString().trim();

                MLog.i("发布货源的对象：" + JSON.toJSONString(info));
                try {
                    if (PublishSupplyActivity.this.lastinfo.equals(info)) {
                        TU.show(mContext, "请勿重复发布！");
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                copyObject(info);
                sendPostRequest(
                        Constants.sendsupply,
                        new ParamsService().sendsupply(info, Integer.parseInt(tv_cfhy.getTag().toString()) == 1 ? "Y" : "N"),
                        PublishSupplyActivity.this,
                        true);
            }
        });

        publish_hwmc_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String t = publish_hwmc_et.getText().toString();
                String editable = publish_hwmc_et.getText().toString();
                String str = SU.StringFilter(editable.toString());
                if (!editable.equals(str)) {
                    publish_hwmc_et.setText(str);
                    publish_hwmc_et.setSelection(str.length()); //光标置后
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        publish_zl_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (publish_zl_et.getText().toString().indexOf(".") >= 0) {
                    if (publish_zl_et.getText().toString().indexOf(".", publish_zl_et.getText().toString().indexOf(".") + 1) > 0) {
                        TU.show(PublishSupplyActivity.this, "已经输入\".\"不能重复输入");
                        publish_zl_et.setText(publish_zl_et.getText().toString().substring(0, publish_zl_et.getText().toString().length() - 1));
                        publish_zl_et.setSelection(publish_zl_et.getText().toString().length());
                    }
                }

                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);
                        publish_zl_et.setText(s);
                        publish_zl_et.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    publish_zl_et.setText(s);
                    publish_zl_et.setSelection(2);
                }

                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        publish_zl_et.setText(s.subSequence(0, 1));
                        publish_zl_et.setSelection(1);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        publish_et_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String t = publish_et_name.getText().toString();
                String editable = publish_et_name.getText().toString();
                String str = SU.StringFilter(editable.toString());
                if (!editable.equals(str)) {
                    publish_et_name.setText(str);
                    publish_et_name.setSelection(str.length()); //光标置后
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void copyObject(SupplyDetailEdite info) {
        lastinfo.cityfrom = info.cityfrom;
        lastinfo.cityto = info.cityto;
        lastinfo.wv = info.wv;
        lastinfo.lastupdate = info.lastupdate;
        lastinfo.goodsid = info.goodsid;
        lastinfo.account = info.account;
        lastinfo.goodsname = info.goodsname;
        lastinfo.carlen = info.carlen;
        lastinfo.cartype = info.cartype;
        lastinfo.unit = info.unit;
        lastinfo.contact = info.contact;
        lastinfo.contactmobile = info.contactmobile;
        lastinfo.remark = info.remark;
        lastinfo.cityfromname = info.cityfromname;
        lastinfo.cityfromparentname = info.cityfromparentname;
        lastinfo.citytoparentname = info.citytoparentname;
        lastinfo.citytoname = info.citytoname;
        lastinfo.room = info.room;
        lastinfo.company = info.company;
    }

    private void initView() {
        tv_cfhy = (TextView) findViewById(R.id.tv_cfhy);
        rl_cfhy = (RelativeLayout) findViewById(R.id.rl_cfhy);
        rl_cfhy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (0 == Integer.parseInt(tv_cfhy.getTag().toString())) {
                    tv_cfhy.setTag(1);
                    Drawable drawable = getResources().getDrawable(R.drawable.dagou_pre);
                    /// 这一步必须要做,否则不会显示.
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    tv_cfhy.setCompoundDrawables(drawable, null, null, null);
                } else {
                    tv_cfhy.setTag(0);
                    Drawable drawable = getResources().getDrawable(R.drawable.dagou_nor);
                    /// 这一步必须要做,否则不会显示.
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    tv_cfhy.setCompoundDrawables(drawable, null, null, null);
                }
            }
        });
        findViewById(R.id.leftBt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWindow != null && mWindow.isShowing()) {
                    mWindow.dismiss();
                } else if (mWindow_car != null && mWindow_car.isShowing()) {
                    mWindow_car.dismiss();
                } else {
//                    SPU.setPreferenceValueBoolean(mContext, SPU.IS_FABU_SUPPLIES, true, SPU.STORE_SETTINGS);
                    finish();
                }
            }
        });
        findViewById(R.id.iv_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(OffenSupplyActivity.class);
            }
        });
        beizhu = (LinearLayout) findViewById(R.id.beizhu);
        lianxidianhua = (LinearLayout) findViewById(R.id.lianxidianhua);
        huozhuxingming = (LinearLayout) findViewById(R.id.huozhuxingming);

        btn_startsation = (Button) findViewById(R.id.btn_startsation);
        btn_endsation = (Button) findViewById(R.id.btn_endsation);
        publish_cx_btn = (Button) findViewById(R.id.publish_cx_btn);
        btn_startsation.setText(cityfromname);
        btn_endsation.setText(citytoname);
        publish_hwmc_et = (EditText) findViewById(R.id.publish_hwmc_et);

        publish_weightunit_btn = (Button) findViewById(R.id.publish_weightunit_btn);
        publish_zl_et = (EditText) findViewById(R.id.publish_zl_et);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) beizhu.getLayoutParams();
        params.setMargins(0, 0, 0, 0);
        publish_xldd_sp = (EditText) findViewById(R.id.publish_xldd_sp);
        if (!"".equals(App.phone)) {
            publish_xldd_sp.setText(App.phone);
            publish_xldd_sp.setFocusable(false);
            publish_xldd_sp.setTextColor(Color.parseColor("#E0E0E0"));
            publish_xldd_sp.setVisibility(View.GONE);
            lianxidianhua.setVisibility(View.GONE);
            beizhu.setLayoutParams(params);
        }
        publish_et_name = (EditText) findViewById(R.id.publish_et_name);

        String username = App.username;
        String username2 = SPU.getPreferenceValueString(mContext, SPU.USERNAME2, "", SPU.STORE_USERINFO);
        if (!SU.isEmpty(username)) {
            publish_et_name.setText(username);
            publish_et_name.setFocusable(false);
            publish_et_name.setTextColor(Color.parseColor("#E0E0E0"));
            publish_et_name.setVisibility(View.GONE);
            huozhuxingming.setVisibility(View.GONE);
            beizhu.setLayoutParams(params);
        } else if (!SU.isEmpty(username2)) {
            publish_et_name.setText(username2);
            publish_et_name.setFocusable(false);
            publish_et_name.setTextColor(Color.parseColor("#E0E0E0"));
            publish_et_name.setVisibility(View.GONE);
            huozhuxingming.setVisibility(View.GONE);
            beizhu.setLayoutParams(params);
        }
        publish_bz_et = (EditText) findViewById(R.id.publish_bz_et);
//        publish_bz_et.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before,int count) {
//                String t = publish_bz_et.getText().toString();
//                String editable = publish_bz_et.getText().toString();
//                String str = SU.StringFilter(editable.toString());
//                if (!editable.equals(str)) {
//                    publish_bz_et.setText(str);
//                    publish_bz_et.setSelection(str.length()); //光标置后
//                }
//            }
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
//            @Override
//            public void afterTextChanged(Editable s) {}
//        });
        publish_commit = (Button) findViewById(R.id.publish_commit);

        // 创建ppw的布局
        initPPW();
    }

    private void initPPW() {
        view = LayoutInflater.from(this).inflate(R.layout.ppw_supplytypeandwv_layout, null);
        tv_select = (TextView) view.findViewById(R.id.tv_select);
        view_car = LayoutInflater.from(this).inflate(R.layout.ppw_carlongtype_layout, null);
        queding = (Button) view_car.findViewById(R.id.queding);
        queding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carlong = cityLevelAdapter2.getSelectItem();
                cartype = cityLevelAdapter3.getSelectItem();
                if (mWindow_car != null && mWindow_car.isShowing()) {
                    if (SU.isEmpty(carlong)) {
                        TU.show(PublishSupplyActivity.this, "请选择车长");
                        return;
                    } else if (SU.isEmpty(cartype)) {
                        TU.show(PublishSupplyActivity.this, "请选择车型");
                        return;
                    } else {
                        mWindow_car.dismiss();
                    }
                }
                publish_cx_btn.setText(carlong + "  " + cartype);
            }
        });
        gv_ppw = (GridView) view.findViewById(R.id.gv_ppw);
        gv_ppw_carlong = (GridView) view_car.findViewById(R.id.gv_ppw_carlong);
        gv_ppw_cartype = (GridView) view_car.findViewById(R.id.gv_ppw_cartype);

        mWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mWindow_car = new PopupWindow(view_car, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
    }

    private void initPopuWindow(List<String> datas, View view) {
        if (mWindow != null && mWindow.isShowing()) {
            mWindow.dismiss();
            return;
        }

        if (cityLevelAdapter == null) {
            cityLevelAdapter = new CityLevelAdapter(PublishSupplyActivity.this, datas);
            cityLevelAdapter.setSupplyListener(this);
            gv_ppw.setAdapter(cityLevelAdapter);
        } else {
            cityLevelAdapter.clearListView();
            cityLevelAdapter.upateList(datas);
        }

        mWindow.setAnimationStyle(R.style.PopupWindowAnimation);
        mWindow.setOutsideTouchable(true);
        mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mWindow.setFocusable(true);
        mWindow.setTouchable(true);
        mWindow.setBackgroundDrawable(getResources().getDrawable(R.color.base_top_bg_color));
        mWindow.showAsDropDown(view, 0, 0);
    }

    private void setView(SupplyDetailEdite info) {
        this.info.goodsid = info.goodsid;
        this.info.cityfrom = info.cityfrom;
        this.info.cityto = info.cityto;
        btn_startsation.setText(info.cityfromname + "-" + info.cityfromparentname);
        btn_endsation.setText(info.citytoname + "-" + info.citytoparentname);
        publish_hwmc_et.setText(info.goodsname);
        carlong = info.carlen;
        cartype = info.cartype;
        publish_cx_btn.setText(carlong + "  " + cartype);

        publish_zl_et.setText(String.valueOf(info.wv));
        publish_weightunit_btn.setText(info.unit);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) beizhu.getLayoutParams();
        params.setMargins(0, 0, 0, 0);
        String username = App.username;
        String username2 = SPU.getPreferenceValueString(mContext, SPU.USERNAME2, "", SPU.STORE_USERINFO);
        if (!SU.isEmpty(username)) {
            publish_et_name.setText(username);
            publish_et_name.setFocusable(false);
            publish_et_name.setTextColor(Color.parseColor("#E0E0E0"));
            publish_et_name.setVisibility(View.GONE);
            lianxidianhua.setVisibility(View.GONE);
            beizhu.setLayoutParams(params);
        } else if (!SU.isEmpty(username2)) {
            publish_et_name.setText(username2);
            publish_et_name.setFocusable(false);
            publish_et_name.setTextColor(Color.parseColor("#E0E0E0"));
            publish_et_name.setVisibility(View.GONE);
            huozhuxingming.setVisibility(View.GONE);
            beizhu.setLayoutParams(params);
        }
        if (!"".equals(App.phone)) {
            publish_xldd_sp.setText(App.phone);
            publish_xldd_sp.setFocusable(false);
            publish_xldd_sp.setTextColor(Color.parseColor("#E0E0E0"));
            publish_xldd_sp.setVisibility(View.GONE);
            huozhuxingming.setVisibility(View.GONE);
            beizhu.setLayoutParams(params);
        }
        publish_bz_et.setText(info.remark);
    }

    private void setView(SupplyItem info) {
        this.info.goodsid = info.goodsid;
        this.info.cityfrom = info.cityfrom;
        this.info.cityto = info.cityto;
//        btn_startsation.setText(info.cityfromname+"-"+info.cityfromparentname);
//        btn_endsation.setText(info.citytoname+"-"+info.citytoparentname);
        btn_startsation.setText(info.cityfromname);
        btn_endsation.setText(info.citytoname);
        publish_hwmc_et.setText(info.goodsname);
        carlong = info.carlen;
        cartype = info.cartype;
        publish_cx_btn.setText(carlong + "  " + cartype);

        publish_zl_et.setText(String.valueOf(info.wv));
        publish_weightunit_btn.setText(info.unit);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) beizhu.getLayoutParams();
        params.setMargins(0, 0, 0, 0);
        String username = App.username;
        String username2 = SPU.getPreferenceValueString(mContext, SPU.USERNAME2, "", SPU.STORE_USERINFO);
        if (!SU.isEmpty(username)) {
            publish_et_name.setText(username);
            publish_et_name.setFocusable(false);
            publish_et_name.setTextColor(Color.parseColor("#E0E0E0"));
            publish_et_name.setVisibility(View.GONE);
            lianxidianhua.setVisibility(View.GONE);
            beizhu.setLayoutParams(params);
        } else if (!SU.isEmpty(username2)) {
            publish_et_name.setText(username2);
            publish_et_name.setFocusable(false);
            publish_et_name.setTextColor(Color.parseColor("#E0E0E0"));
            publish_et_name.setVisibility(View.GONE);
            huozhuxingming.setVisibility(View.GONE);
            beizhu.setLayoutParams(params);
        }
        if (!"".equals(App.phone)) {
            publish_xldd_sp.setText(App.phone);
            publish_xldd_sp.setFocusable(false);
            publish_xldd_sp.setTextColor(Color.parseColor("#E0E0E0"));
            publish_xldd_sp.setVisibility(View.GONE);
            huozhuxingming.setVisibility(View.GONE);
            beizhu.setLayoutParams(params);
        }
        publish_bz_et.setText(info.remark);
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
                btn_startsation.setText(bundle.getString("didian"));
            }
        } else if (requestCode == 200) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                info.cityto = bundle.getInt("didianId");
                shengto = (Region) bundle.getSerializable("sheng");
                shito = (Region) bundle.getSerializable("shi");
                xianquto = (Region) bundle.getSerializable("xianqu");
                btn_endsation.setText(bundle.getString("didian"));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void publish_cx_btn(final View view) {//车长车型
        if (mWindow_car != null && mWindow_car.isShowing()) {
            mWindow_car.dismiss();
            return;
        }

        flag = 2;
        ArrayList<String> list = new ArrayList<>();
        String[] ss = SPU.getPreferenceValueString
                (PublishSupplyActivity.this, SPU.CARLEN, "",
                        SPU.STORE_SETTINGS).split(",");
        for (int i = 0; i < ss.length; i++) {
            list.add(ss[i]);
        }

        ArrayList<String> list2 = new ArrayList<>();
        String[] ss2 = SPU.getPreferenceValueString
                (PublishSupplyActivity.this, SPU.CARTYPE, "",
                        SPU.STORE_SETTINGS).split(",");
        for (int i = 0; i < ss2.length; i++) {
            list2.add(ss2[i]);
        }

        if (cityLevelAdapter2 == null) {
            cityLevelAdapter2 = new CityLevelAdapter2(PublishSupplyActivity.this, list);
            gv_ppw_carlong.setAdapter(cityLevelAdapter2);
        } else {
            cityLevelAdapter2.clearListView();
            cityLevelAdapter2.upateList(list);
        }


        if (cityLevelAdapter3 == null) {
            cityLevelAdapter3 = new CityLevelAdapter3(PublishSupplyActivity.this, list2);
            gv_ppw_cartype.setAdapter(cityLevelAdapter3);
        } else {
            cityLevelAdapter3.clearListView();
            cityLevelAdapter3.upateList(list2);
        }

        mWindow_car.setAnimationStyle(R.style.PopupWindowAnimation);
        mWindow_car.setOutsideTouchable(true);
        mWindow_car.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mWindow_car.setFocusable(true);
        mWindow_car.setTouchable(true);
        mWindow_car.setBackgroundDrawable(getResources().getDrawable(R.color.base_top_bg_color));
        mWindow_car.showAsDropDown(findViewById(R.id.default_title_bg), 0, 0);
//        creatDialog_cx(view);
    }

    public void publish_huoelei_btn(final View view) {//货物名称
        flag = 1;
        ArrayList<String> list = new ArrayList<>();
        String[] ss = SPU.getPreferenceValueString
                (PublishSupplyActivity.this, SPU.HUOLEI, "",
                        SPU.STORE_SETTINGS).split(",");
        for (int i = 0; i < ss.length; i++) {
            list.add(ss[i]);
        }
        tv_select.setText("选择货物类型：");
        initPopuWindow(list, view);
//        creatDialog_hl(view, publish_hwmc_et);
    }

    public void publish_weightunit_btn(final View view) {//重量单位
        flag = 3;
        ArrayList<String> list = new ArrayList<>();
        String[] ss = SPU.getPreferenceValueString
                (PublishSupplyActivity.this, SPU.WV, "",
                        SPU.STORE_SETTINGS).split(",");
        for (int i = 0; i < ss.length; i++) {
            list.add(ss[i]);
        }
        tv_select.setText("选择重量单位：");
        initPopuWindow(list, view);
//        creatDialog_wv(view);
    }

    @Override
    public void setHandlerPostDataSuccess(int connectionId, Object data) throws JSONException {
        ResponseParams<LinkedHashMap> iParams = jiexiData(data);
        if (connectionId == Constants.sendsupply) {
            TU.show(mContext, "发布成功");
            SPU.setPreferenceValueBoolean(mContext, SPU.IS_FABU_SUPPLIES, true, SPU.STORE_SETTINGS);
            SPU.setPreferenceValueString(mContext, SPU.CityFromName, btn_startsation.getText().toString(), SPU.STORE_USERINFO);
            SPU.setPreferenceValueInt(mContext, SPU.CityFromNameID, info.cityfrom, SPU.STORE_USERINFO);
            SPU.setPreferenceValueString(mContext, SPU.CityToName, btn_endsation.getText().toString(), SPU.STORE_USERINFO);
            SPU.setPreferenceValueInt(mContext, SPU.CityToNameID, info.cityto, SPU.STORE_USERINFO);
            finish();


        } else if (connectionId == Constants.editsupply2) {
            ArrayList<SupplyDetailEdite> supplys = (ArrayList<SupplyDetailEdite>) IMap.getData2FromResponse(iParams, SupplyDetailEdite.class);
            setView(supplys.get(0));
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

    @Override
    public void send(View view, final String aInfo) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
                if (flag == 1) {
                    publish_hwmc_et.setText(aInfo);
                } else if (flag == 3) {
                    publish_weightunit_btn.setText(aInfo);
                }

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWindow != null && mWindow.isShowing()) {
                mWindow.dismiss();
            } else if (mWindow_car != null && mWindow_car.isShowing()) {
                mWindow_car.dismiss();
            } else {
                finish();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

}
