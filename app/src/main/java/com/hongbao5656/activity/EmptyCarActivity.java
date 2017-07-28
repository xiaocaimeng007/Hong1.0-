package com.hongbao5656.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.hongbao5656.R;
import com.hongbao5656.base.App;
import com.hongbao5656.base.BaseActivity;
import com.hongbao5656.okhttp.HttpException;
import com.hongbao5656.util.Constants;
import com.hongbao5656.entity.CityTo;
import com.hongbao5656.entity.Region;
import com.hongbao5656.entity.SupplyDetailEdite;
import com.hongbao5656.okhttp.HttpDataHandlerListener;
import com.hongbao5656.util.ParamsService;
import com.hongbao5656.util.ResponseParams;
import com.hongbao5656.util.SPU;
import com.hongbao5656.util.SU;
import com.hongbao5656.util.TU;
import com.hongbao5656.view.DatePicker;
import com.hongbao5656.view.TimePicker;
import com.squareup.okhttp.Request;
import org.json.JSONException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

public class EmptyCarActivity extends BaseActivity implements
        HttpDataHandlerListener {
    private Button btn_startsation;
    private Button btn_endsation;
    private SupplyDetailEdite info = new SupplyDetailEdite();
    private EditText publish_hwmc_et;
    private EditText publish_zl_et;
    private EditText publish_xldd_sp;
    private EditText publish_bz_et;
    private Button publish_commit;
    private Context mContext;
    private String weightUnit = "吨";
    private RelativeLayout leftBt;
    private EditText publish_et_name;
    private Button publish_weightunit_btn;
    private Button publish_cc_btn;
    private Button publish_cx_btn;
    private Serializable sheng;
    private Serializable shi;
    private Region shengfrom;
    private Region shifrom;
    private Region xianqufrom;
    private Region shengto;
    private Region xianquto;
    private Region shito;
    private String cityfromname = "";
    private Button publish_emptytime_btn;
    private Button publish_emptytime_btn_end;
    private EditText publish_zyjia;
    private TextView iv_right;
    private EditText publish_cc_et;
    private String endtime;
    private String starttime;
    private LinearLayout ll_addunloading;
    private LinearLayout ll_addunloadingparent;


    private Calendar calendar;
    private DatePicker dp_test;
    private TimePicker tp_test;
    private TextView tv_ok, tv_cancel;    //确定、取消button
    private Button btn_naozhong;
    private PopupWindow pw;
    private String selectDate, selectTime;
    //选择时间与当前时间，用于判断用户选择的是否是以前的时间
    private int currentHour, currentMinute, currentDay, selectHour, selectMinute, selectDay;
    //整体布局
    private RelativeLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emptycar);
        this.mContext = EmptyCarActivity.this;
        initView();
        initListener();
    }

    private void requestEditSupply(String stringExtra) {
        sendPostRequest(
                Constants.editsupply2,
                new ParamsService().requestKV("goodsid", stringExtra, Constants.editsupply2),
                this,
                false);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private ArrayList<CityTo> cityto = new ArrayList<CityTo>();

    private void initListener() {
        btn_startsation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!App.flag) {
//                    TU.showShort(mContext, "正在初始化数据,请稍后再试...");
//                    return;
//                }
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
//                if (!App.flag) {
//                    TU.showShort(mContext, "正在初始化数据,请稍后再试...");
//                    return;
//                }
                if (ll_addunloading.getChildCount() > 4) {
                    TU.showShort(mContext, "最多只能添加5个目的地");
                    return;
                }
                Intent intent = new Intent(mContext, SelectPlaceActivity.class);
                intent.putExtra("shengto", shengto);
                intent.putExtra("shito", shito);
                intent.putExtra("xianquto", xianquto);
                intent.putExtra("type", 2);
                startActivityForResult(intent, 200);
            }
        });
//        publish_emptytime_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                String time = TimeUtils.getSureTime("yyyy年MM月dd日 HH:mm",System.currentTimeMillis());
////                DateTimePickDialogUtil dtu = new DateTimePickDialogUtil(EmptyCarActivity.this,time);
////                dtu.dateTimePicKDialog((Button) v, true);
//
//
//
//
//            }
//        });

        publish_emptytime_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                View view = View.inflate(EmptyCarActivity.this, R.layout.dialog_select_time, null);
                selectDate = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-"
                        + calendar.get(Calendar.DAY_OF_MONTH) + " ";
//                selectDate = calendar.get(Calendar.YEAR) + "年" + calendar.get(Calendar.MONTH) + "月"
//                        + calendar.get(Calendar.DAY_OF_MONTH) + "日"
//                        + DatePicker.getDayOfWeekCN(calendar.get(Calendar.DAY_OF_WEEK));
                //选择时间与当前时间的初始化，用于判断用户选择的是否是以前的时间，如果是，弹出toss提示不能选择过去的时间
                selectDay = currentDay = calendar.get(Calendar.DAY_OF_MONTH);
                selectMinute = currentMinute = calendar.get(Calendar.MINUTE);
                selectHour = currentHour = calendar.get(Calendar.HOUR_OF_DAY);

                selectTime = currentHour + ":" + ((currentMinute < 10) ? ("0" + currentMinute) : currentMinute);
//                selectTime = currentHour + "点" + ((currentMinute < 10) ? ("0" + currentMinute) : currentMinute) + "分";
                dp_test = (DatePicker) view.findViewById(R.id.dp_test);
                tp_test = (TimePicker) view.findViewById(R.id.tp_test);
                tv_ok = (TextView) view.findViewById(R.id.tv_ok);
                tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
                //设置滑动改变监听器
                dp_test.setOnChangeListener(dp_onchanghelistener);
                tp_test.setOnChangeListener(tp_onchanghelistener);
                pw = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
//				//设置这2个使得点击pop以外区域可以去除pop
                pw.setOutsideTouchable(true);
                pw.setBackgroundDrawable(new BitmapDrawable());

                //出现在布局底端
                pw.showAtLocation(container, 0, 0, Gravity.END);

                //点击确定
                tv_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        if (selectDay == currentDay) {    //在当前日期情况下可能出现选中过去时间的情况
                            if (selectHour < currentHour) {
                                Toast.makeText(getApplicationContext(), "不能选择过去的时间\n请重新选择", Toast.LENGTH_SHORT).show();
                            }
//                            else if ((selectHour == currentHour) && (selectMinute < currentMinute)) {
//                                Toast.makeText(getApplicationContext(), "不能选择过去的时间\n        请重新选择", Toast.LENGTH_SHORT).show();
//                            }
                            else {
                                publish_emptytime_btn.setText(selectDate + selectTime);
                                Toast.makeText(getApplicationContext(), selectDate + selectTime, Toast.LENGTH_SHORT).show();
                                pw.dismiss();
                            }
                        } else {
                            publish_emptytime_btn.setText(selectDate + selectTime);
                            Toast.makeText(getApplicationContext(), selectDate + selectTime, Toast.LENGTH_SHORT).show();
                            pw.dismiss();
                        }

                    }
                });

                //点击取消
                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        pw.dismiss();
                    }
                });
            }
        });

        publish_emptytime_btn_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                View view = View.inflate(EmptyCarActivity.this, R.layout.dialog_select_time, null);
                selectDate = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-"
                        + calendar.get(Calendar.DAY_OF_MONTH) + " ";
//                selectDate = calendar.get(Calendar.YEAR) + "年" + calendar.get(Calendar.MONTH) + "月"
//                        + calendar.get(Calendar.DAY_OF_MONTH) + "日"
//                        + DatePicker.getDayOfWeekCN(calendar.get(Calendar.DAY_OF_WEEK));
                //选择时间与当前时间的初始化，用于判断用户选择的是否是以前的时间，如果是，弹出toss提示不能选择过去的时间
                selectDay = currentDay = calendar.get(Calendar.DAY_OF_MONTH);
                selectMinute = currentMinute = calendar.get(Calendar.MINUTE);
                selectHour = currentHour = calendar.get(Calendar.HOUR_OF_DAY);

                selectTime = currentHour + ":" + ((currentMinute < 10) ? ("0" + currentMinute) : currentMinute);
//                selectTime = currentHour + "点" + ((currentMinute < 10) ? ("0" + currentMinute) : currentMinute) + "分";
                dp_test = (DatePicker) view.findViewById(R.id.dp_test);
                tp_test = (TimePicker) view.findViewById(R.id.tp_test);
                tv_ok = (TextView) view.findViewById(R.id.tv_ok);
                tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
                //设置滑动改变监听器
                dp_test.setOnChangeListener(dp_onchanghelistener);
                tp_test.setOnChangeListener(tp_onchanghelistener);
                pw = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
//				//设置这2个使得点击pop以外区域可以去除pop
                pw.setOutsideTouchable(true);
                pw.setBackgroundDrawable(new BitmapDrawable());

                //出现在布局底端
                pw.showAtLocation(container, 0, 0, Gravity.END);

                //点击确定
                tv_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        if (selectDay == currentDay) {    //在当前日期情况下可能出现选中过去时间的情况
                            if (selectHour < currentHour) {
                                Toast.makeText(getApplicationContext(), "不能选择过去的时间\n请重新选择", Toast.LENGTH_SHORT).show();
                            } else if ((selectHour == currentHour) && (selectMinute <= currentMinute)) {
                                Toast.makeText(getApplicationContext(), "不能选择过去或当前的时间\n请重新选择", Toast.LENGTH_SHORT).show();
                            } else {
                                publish_emptytime_btn_end.setText(selectDate + selectTime);
                                Toast.makeText(getApplicationContext(), selectDate + selectTime, Toast.LENGTH_SHORT).show();
                                pw.dismiss();
                            }
                        } else {
                            publish_emptytime_btn_end.setText(selectDate + selectTime);
                            Toast.makeText(getApplicationContext(), selectDate + selectTime, Toast.LENGTH_SHORT).show();
                            pw.dismiss();
                        }

                    }
                });

                //点击取消
                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        pw.dismiss();
                    }
                });
            }
        });

//        publish_emptytime_btn_end.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String time = TimeUtils.getSureTime("yyyy年MM月dd日 HH:mm",System.currentTimeMillis());
//                DateTimePickDialogUtil dtu = new DateTimePickDialogUtil(EmptyCarActivity.this, time);
//                dtu.dateTimePicKDialog((Button) v, false);
//            }
//        });


        publish_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                starttime = publish_emptytime_btn.getText().toString();
                endtime = publish_emptytime_btn_end.getText().toString();
                if (TextUtils.isEmpty(btn_startsation.getText().toString().trim())) {
                    TU.show(EmptyCarActivity.this, "出发地不能为空");
                    return;
                }
//                if (TextUtils.isEmpty(btn_endsation.getText().toString().trim())) {
//                    TU.show(EmptyCarActivity.this, "目的地不能为空");
//                    return;
//                }
                if (cityto.size() == 0) {
                    TU.show(EmptyCarActivity.this, "目的地不能为空");
                    return;
                }
                if ("请选择空车开始时间".equals(starttime) || (SU.isEmpty(starttime))) {
                    TU.show(EmptyCarActivity.this, "开始时间不能为空");
                    return;
                }

                try {
                    Date d1 = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(starttime);
//                    if (d1.getTime()<System.currentTimeMillis()-900) {
//                        TU.show(mContext,"空车的开始时间不能早于当前时间");
//                        return;
//                    }
                    Date d2 = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(endtime);
                    if (d2.getTime() <= System.currentTimeMillis() + 1000 * 60 * 30) {
                        TU.show(mContext, "结束时间最少应在半个小时后");
                        return;
                    }
                    if (d2.getTime() <= d1.getTime()) {
                        TU.show(mContext, "结束时间不能早于开始时间");
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if ("请选择空车结束时间".equals(endtime) || (SU.isEmpty(endtime))) {
                    TU.show(EmptyCarActivity.this, "空车结束时间不能为空");
                    return;
                }

//                if (TextUtils.isEmpty(publish_cc_et.getText().toString())) {
//                    TU.show(EmptyCarActivity.this, "车长为空");
//                    return;
//                }
//                if (TextUtils.isEmpty(publish_zl_et.getText().toString())) {
//                    TU.show(EmptyCarActivity.this, "承重不能为空");
//                    return;
//                }

                if (publish_zl_et.getText().toString().toString().endsWith(".")) {
                    TU.show(EmptyCarActivity.this, "承重不能以.结尾");
                    return;
                }

                if (publish_cc_et.getText().toString().toString().endsWith(".")) {
                    TU.show(EmptyCarActivity.this, "车长不能以.结尾");
                    return;
                }

                if ("装车为至".equals(endtime)) {
                    endtime = "";
                }
                String zl = publish_zl_et.getText().toString();
                double wt = 0;
                if (!"".equals(zl)) {
                    wt = Double.parseDouble(zl);
                }
                sendPostRequest(
                        Constants.emptycar,
                        new ParamsService().emptycar(info.cityfrom, cityto, starttime, endtime, wt, publish_cc_et.getText().toString()),
                        EmptyCarActivity.this,
                        true);
            }
        });

        leftBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        iv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, EmptyCarShowActivity.class));
            }
        });
    }

    private void initView() {
        calendar = Calendar.getInstance();
        container = (RelativeLayout) findViewById(R.id.container);
        leftBt = (RelativeLayout) findViewById(R.id.leftBt);
        iv_right = (TextView) findViewById(R.id.iv_right);
        ll_addunloading = (LinearLayout) findViewById(R.id.ll_addunloading);
        ll_addunloadingparent = (LinearLayout) findViewById(R.id.ll_addunloadingparent);
//        ll_addunloading.setVisibility(View.GONE);
        btn_startsation = (Button) findViewById(R.id.btn_startsation);
        btn_endsation = (Button) findViewById(R.id.btn_endsation);
        publish_emptytime_btn = (Button) findViewById(R.id.publish_emptytime_btn);
        publish_emptytime_btn_end = (Button) findViewById(R.id.publish_emptytime_btn_end);
        publish_cc_et = (EditText) findViewById(R.id.publish_cc_et);
        publish_zl_et = (EditText) findViewById(R.id.publish_zl_et);
        publish_zl_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (publish_zl_et.getText().toString().indexOf(".") >= 0) {
                    if (publish_zl_et.getText().toString().indexOf(".", publish_zl_et.getText().toString().indexOf(".") + 1) > 0) {
                        TU.show(EmptyCarActivity.this, "已经输入\".\"不能重复输入");
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
        publish_cc_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (publish_cc_et.getText().toString().indexOf(".") >= 0) {
                    if (publish_cc_et.getText().toString().indexOf(".", publish_cc_et.getText().toString().indexOf(".") + 1) > 0) {
                        TU.show(EmptyCarActivity.this, "已经输入\".\"不能重复输入");
                        publish_cc_et.setText(publish_cc_et.getText().toString().substring(0, publish_cc_et.getText().toString().length() - 1));
                        publish_cc_et.setSelection(publish_cc_et.getText().toString().length());
                    }
                }

                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);
                        publish_cc_et.setText(s);
                        publish_cc_et.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    publish_cc_et.setText(s);
                    publish_cc_et.setSelection(2);
                }

                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        publish_cc_et.setText(s.subSequence(0, 1));
                        publish_cc_et.setSelection(1);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        publish_commit = (Button) findViewById(R.id.publish_commit);
    }

    private void setView(SupplyDetailEdite info) {
        this.info.goodsid = info.goodsid;
        this.info.cityfrom = info.cityfrom;
        this.info.cityto = info.cityto;
        btn_startsation.setText(info.cityfromname);
        btn_endsation.setText(info.citytoname);
        publish_hwmc_et.setText(info.goodsname);
        publish_cc_btn.setText(info.carlen);
        String cartype2 = publish_cx_btn.getText().toString().trim();

        publish_cx_btn.setText(cartype2);
        publish_zl_et.setText(String.valueOf(info.wv));
        publish_weightunit_btn.setText(info.unit);
        String username = App.username;
        String username2 = SPU.getPreferenceValueString(mContext, SPU.USERNAME2, "", SPU.STORE_USERINFO);
        if (!SU.isEmpty(username)) {
            publish_et_name.setText(username);
            publish_et_name.setFocusable(false);
            publish_et_name.setTextColor(Color.parseColor("#E0E0E0"));
        } else if (!SU.isEmpty(username2)) {
            publish_et_name.setText(username2);
            publish_et_name.setFocusable(false);
            publish_et_name.setTextColor(Color.parseColor("#E0E0E0"));
        }
        if (!"".equals(App.phone)) {
            publish_xldd_sp.setText(App.phone);
            publish_xldd_sp.setFocusable(false);
            publish_xldd_sp.setTextColor(Color.parseColor("#E0E0E0"));
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
                final CityTo ct = new CityTo();
                ct.cityto = info.cityto;

                if (ll_addunloading.getChildCount() == 0 && cityto.size() == 0) {
                    ll_addunloadingparent.setVisibility(View.VISIBLE);
                }
                cityto.add(ct);
                ViewGroup.LayoutParams Params = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                Button et = new Button(EmptyCarActivity.this);
//                et.setBackground(R.drawable.btn_city_hubs);
//                et.setBackgroundResource(R.drawable.btn_city_hubs);
                et.setTag(ct);
                et.setLayoutParams(Params);
//                et.setPadding(
//                        DensityUtils.dip2px(EmptyCarActivity.this, 4),0,0,0);
//                et.setPadding(DensityUtils.dip2px(EmptyCarActivity.this, 28), 0, 0, 0);
                et.setText(bundle.getString("didian"));
//                et.setHint("输入卸货地址");
//                et.setHintTextColor(Color.parseColor("#A7AEBC"));
//                et.setBackgroundColor(Color.parseColor("#FFFFFF"));
                et.setTextColor(Color.parseColor("#f05b33"));
                et.setTextSize(17);
                et.setGravity(Gravity.CENTER);
//                et.setGravity(Gravity.CENTER_VERTICAL);
                et.setSingleLine();
//                ViewGroup.LayoutParams Params2 = new ViewGroup.LayoutParams(
//                        ViewGroup.LayoutParams.MATCH_PARENT,DensityUtils.dip2px(EmptyCarActivity.this, 1));
//                View view = new View(EmptyCarActivity.this);
//                view.setLayoutParams(Params2);
//                view.setBackgroundColor(Color.parseColor("#DDE2EB"));
//                view.setPadding(DensityUtils.dip2px(EmptyCarActivity.this, 18), 0, 0, 0);
                et.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ll_addunloading.removeView(v);
                        cityto.remove(ct);
                        if (ll_addunloading.getChildCount() == 0 && cityto.size() == 0) {
                            ll_addunloadingparent.setVisibility(View.GONE);
                        }
                    }
                });
                ll_addunloading.addView(et);
//                ll_addunloading.addView(view);
                ll_addunloading.invalidate();
//                int n =ll_addunloading.getChildCount();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setHandlerPostDataSuccess(int connectionId, Object data) throws JSONException {
        ResponseParams<LinkedHashMap> iParams =jiexiData(data);

        if (connectionId == Constants.emptycar) {
            TU.show(mContext, "上报成功");
            startActivity(new Intent(mContext, EmptyCarShowActivity.class));
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

    //listeners
    DatePicker.OnChangeListener dp_onchanghelistener = new DatePicker.OnChangeListener() {
        @Override
        public void onChange(int year, int month, int day, int day_of_week) {
            selectDay = day;
            selectDate = year + "-" + (month) + "-" + day + " ";
//            selectDate = year + "年" + month + "月" + day + "日" + DatePicker.getDayOfWeekCN(day_of_week);
        }
    };
    TimePicker.OnChangeListener tp_onchanghelistener = new TimePicker.OnChangeListener() {
        @Override
        public void onChange(int hour, int minute) {
            selectTime = hour + ":" + ((minute < 10) ? ("0" + minute) : minute);
//            selectTime = hour + "点" + ((minute < 10) ? ("0" + minute) : minute) + "分";
            selectHour = hour;
            selectMinute = minute;
        }
    };
}
