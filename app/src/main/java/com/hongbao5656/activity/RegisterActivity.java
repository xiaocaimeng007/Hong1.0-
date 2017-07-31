package com.hongbao5656.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;

import com.example.aaron.library.MLog;

import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.hongbao5656.R;
import com.hongbao5656.base.App;
import com.hongbao5656.base.BaseActivity;
import com.hongbao5656.okhttp.HttpException;
import com.hongbao5656.util.Constants;
import com.hongbao5656.entity.Area;
import com.hongbao5656.okhttp.HttpDataHandlerListener;
import com.hongbao5656.util.MD5;
import com.hongbao5656.util.ParamsService;
import com.hongbao5656.util.ResponseParams;
import com.hongbao5656.util.SPU;
import com.hongbao5656.util.SU;
import com.hongbao5656.util.TU;
import com.hongbao5656.util.TimeUtils;
import com.hongbao5656.util.TimeUtils.OnTimeFinshListener;
import com.squareup.okhttp.Request;

import org.json.JSONException;

import java.util.LinkedHashMap;
import java.util.List;

public class RegisterActivity extends BaseActivity implements
        OnClickListener, HttpDataHandlerListener, OnTimeFinshListener, AMapLocationListener {
    private Button btn_register;
    private Button btn_getyzm;
    private EditText et_phone_number;
    private EditText etyzm;
    private String yzm;
    private TextView tv_web;
    private TextView tv_select_area;
    private CheckBox checkBox_contact;
    private String etyzms;
    private String phone;
    private Context mContext;
    private ImageButton delete;
    private LinearLayout ll_select_area;
    // private TextView tv_select_area;
    private Button btn_login;
    private EditText et_login_pwd;
    private EditText et_login_yqm;
    private String pwd;
    private String area;
    private String yqm;
    private RelativeLayout leftBt;
    private RadioGroup rg_mode;
    private ImageButton deletepwd;
    private RadioButton rb_hz;
    private RadioButton rb_sj;
    private int type = 0;
    private CheckBox cb_mm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.mContext = RegisterActivity.this;
        initView();
        initListener();
        updateversion();
    }

    private void initListener() {
        findViewById(R.id.leftBt).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_register.setOnClickListener(this);
        delete.setOnClickListener(this);
        deletepwd.setOnClickListener(this);
        btn_getyzm.setOnClickListener(this);
        tv_web.setOnClickListener(this);
        ll_select_area.setOnClickListener(this);
        btn_login.setOnClickListener(this);
//		leftBt.setOnClickListener(this);
        et_phone_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                phone = et_phone_number.getText().toString().trim();
                if (SU.isEmpty(phone)) {
                    delete.setClickable(false);
                    delete.setVisibility(View.GONE);
                } else {
                    delete.setClickable(true);
                    delete.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        et_login_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                pwd = et_login_pwd.getText().toString().trim();
                if (SU.isEmpty(pwd)) {
                    deletepwd.setClickable(false);
                    deletepwd.setVisibility(View.GONE);
                } else {
                    deletepwd.setClickable(true);
                    deletepwd.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        rg_mode.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
//				tv_select_area.setText("货主请选择区域");
                switch (checkedId) {
                    case R.id.rb_hz:
                        type = 2;
                        break;
                    case R.id.rb_sj:
                        type = 1;
                        break;
                }
            }
        });

        cb_mm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_mm.isChecked()) {
                    //如果选中，显示密码
                    et_login_pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //否则隐藏密码
                    et_login_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                et_login_pwd.postInvalidate();
                //切换后将EditText光标置于末尾
                CharSequence charSequence = et_login_pwd.getText();
                if (charSequence instanceof Spannable) {
                    Spannable spanText = (Spannable) charSequence;
                    Selection.setSelection(spanText, charSequence.length());
                }
            }
        });
    }

    private void initView() {
        btn_register = (Button) findViewById(R.id.btn_register);
        btn_login = (Button) findViewById(R.id.btn_login);
        et_phone_number = (EditText) findViewById(R.id.et_phone_number);
        btn_getyzm = (Button) findViewById(R.id.btn_getyzm);
        etyzm = (EditText) findViewById(R.id.et_security_code);
        et_login_pwd = (EditText) findViewById(R.id.et_login_pwd);
        et_login_yqm = (EditText) findViewById(R.id.et_login_yqm);
        tv_web = (TextView) findViewById(R.id.tv_web);
        checkBox_contact = (CheckBox) findViewById(R.id.checkBox_contact);
        delete = (ImageButton) findViewById(R.id.delete);
        deletepwd = (ImageButton) findViewById(R.id.deletepwd);
        ll_select_area = (LinearLayout) findViewById(R.id.ll_select_area);
        ll_select_area.setClickable(false);
        tv_select_area = (TextView) findViewById(R.id.tv_select_area);
//		leftBt = (RelativeLayout)findViewById(R.id.leftBt);
        rg_mode = (RadioGroup) findViewById(R.id.rg_mode);
        rb_hz = (RadioButton) findViewById(R.id.rb_hz);
        rb_sj = (RadioButton) findViewById(R.id.rb_sj);
        cb_mm = (CheckBox) findViewById(R.id.cb_mm);
    }


    @Override
    public void setHandlerPostDataSuccess(int connectionId, Object data) throws JSONException {
        ResponseParams<LinkedHashMap> iParams = jiexiData(data);
        if (connectionId == Constants.btn_getyzm) {
            yzm = String.valueOf(iParams.getData1().get("code"));
        } else if (connectionId == Constants.btn_register) {
            TU.show(mContext, "注册成功");
            MLog.i("注册成功");
            //储存服务器返回的token  用户信息（用户类型、权限等）
            SPU.setPreferenceValueString(mContext, SPU.EQ_Tel, phone, SPU.STORE_USERINFO);
            App.phone = phone;
            SPU.setPreferenceValueString(mContext, SPU.PWD, MD5.getStringMD5(pwd), SPU.STORE_USERINFO);
            App.pwd = MD5.getStringMD5(pwd);
            SPU.setPreferenceValueString(mContext, SPU.PWD2, pwd, SPU.STORE_USERINFO);
            App.pwd2 = pwd;
            String token = String.valueOf(iParams.getData1().get("token"));
            if (!SU.isEmpty(token)) {
                SPU.setPreferenceValueString(mContext, SPU.EQ_tokenId, token, SPU.STORE_USERINFO);
                App.token = token;
            }

            String usertype = String.valueOf(iParams.getData1().get("usertype"));
            if (!SU.isEmpty(usertype)) {
                SPU.setPreferenceValueString(mContext, SPU.UER_TYPE, usertype, SPU.STORE_USERINFO);
                App.UER_TYPE = usertype;
            }

            String status = String.valueOf(iParams.getData1().get("status"));
            if (!SU.isEmpty(status)) {
                SPU.setPreferenceValueString(mContext, SPU.UER_CHECK_STATE, status, SPU.STORE_USERINFO);
                App.UER_CHECK_STATE = status;
            }

            String account = String.valueOf(iParams.getData1().get("account"));
            if (!SU.isEmpty(account)) {
                SPU.setPreferenceValueString(mContext, SPU.ACCOUNT, account, SPU.STORE_USERINFO);
                App.account = account;
            }

            String username = String.valueOf(iParams.getData1().get("username"));
            if (!SU.isEmpty(username)) {
                SPU.setPreferenceValueString(mContext, SPU.USERNAMENEW, username, SPU.STORE_USERINFO);
                App.username = username;
            }


            SPU.setPreferenceValueString(mContext, SPU.SET_COMPANYID, String.valueOf(iParams.getData1().get("companyid")), SPU.STORE_USERINFO);
            SPU.setPreferenceValueString(mContext, SPU.SET_ROOMID, String.valueOf(iParams.getData1().get("roomid")), SPU.STORE_USERINFO);
            SPU.setPreferenceValueString(mContext, SPU.sellcode, String.valueOf(iParams.getData1().get("sellcode")), SPU.STORE_USERINFO);

            MLog.i("SET_COMPANYID:" + SPU.getPreferenceValueString(mContext, SPU.SET_COMPANYID, "", SPU.STORE_USERINFO) +
                    "SET_ROOMID:" + SPU.getPreferenceValueString(mContext, SPU.SET_ROOMID, "", SPU.STORE_USERINFO));
            openActivity(MainActivity.class);
            finish();
        } else if (connectionId == Constants.UPDATETAB) {
            if (!(iParams.getData1().get("tb_app_company").toString()).equals("[]")) {
                List<Area> list = JSON.parseArray(
                        JSON.toJSONString(iParams.getData1().get("tb_app_company")), Area.class);
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < list.size(); i++) {
                    Area area = list.get(i);
                    sb.append(area.companyname);
                    if (i != list.size() - 1) {
                        sb.append(",");
                    }
                }
                StringBuffer sb2 = new StringBuffer();
                for (int i = 0; i < list.size(); i++) {
                    Area area = list.get(i);
                    sb2.append(area.companyid + "");
                    if (i != list.size() - 1) {
                        sb2.append(",");
                    }
                }
                StringBuffer sb3 = new StringBuffer();
                for (int i = 0; i < list.size(); i++) {
                    Area area = list.get(i);
                    sb3.append(area.cityname + "");
                    if (i != list.size() - 1) {
                        sb3.append(",");
                    }
                }
                SPU.setPreferenceValueString(mContext, SPU.AREA, sb.toString(), SPU.STORE_SETTINGS);
                SPU.setPreferenceValueString(mContext, SPU.AREA2, sb2.toString(), SPU.STORE_SETTINGS);
                SPU.setPreferenceValueString(mContext, SPU.AREA3, sb3.toString(), SPU.STORE_SETTINGS);
                ll_select_area.setClickable(true);
                startLocation();
            }
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

    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

    /**
     * 定位
     */
    public void startLocation() {
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(getApplicationContext());
            // 设置定位监听
            mlocationClient.setLocationListener(this);
            // 初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            // 设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            // 设置是否返回地址信息（默认返回地址信息）
            mLocationOption.setNeedAddress(true);
            // 设置是否只定位一次,默认为false
            mLocationOption.setOnceLocation(true);
            // 设置是否强制刷新WIFI，默认为强制刷新
            mLocationOption.setWifiActiveScan(true);
            // 设置是否允许模拟位置,默认为false，不允许模拟位置
            mLocationOption.setMockEnable(false);
            // 设置定位间隔,单位毫秒,默认为2000ms
            mLocationOption.setInterval(2000);
            // 给定位客户端对象设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
        }
        // 启动定位
        mlocationClient.startLocation();
    }



    private String city = "";

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null && amapLocation.getErrorCode() == 0) {
            //定位成功
            city = amapLocation.getCity();
            String[] area3 = SPU.getPreferenceValueString(this, SPU.AREA3, "", SPU.STORE_SETTINGS).split(",");
            String[] area2 = SPU.getPreferenceValueString(this, SPU.AREA2, "", SPU.STORE_SETTINGS).split(",");
            String[] area = SPU.getPreferenceValueString(this, SPU.AREA, "", SPU.STORE_SETTINGS).split(",");

            for (int i = 0; i < area3.length; i++) {
                if (city.equals(area3[i])) {
                    tv_select_area.setText(area[i]);
                    which_area = area2[i];
                    MLog.i("匹配成功");
                    break;
                }
            }
//            ll_select_area.setClickable(true);
//                updateversion();
        } else {
            //定位失败
            ll_select_area.setClickable(true);
            MLog.i("定位失败,请手动选择所属区域");
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                phone = et_phone_number.getText().toString();
                etyzms = etyzm.getText().toString();
                pwd = et_login_pwd.getText().toString();
                yqm = et_login_yqm.getText().toString();
                area = tv_select_area.getText().toString();
                if (!SU.isPhoneNumber(phone)) {
                    TU.showLong(mContext, "请输入正确的手机号码");
                    return;
                }
                if (SU.isEmpty(etyzms)) {
                    TU.showLong(mContext, "验证码不能为空");
                    return;
                }
                if (!etyzms.equals(yzm)) {
                    TU.showLong(mContext, "验证码输入错误");
                    return;
                }
                if (!etyzms.equals(yzm)) {
                    TU.showLong(mContext, "验证码输入错误");
                    return;
                }
                if (SU.isEmpty(pwd)) {
                    TU.showLong(mContext, "密码不能为空");
                    return;
                }
                if (!rb_hz.isChecked() && !rb_sj.isChecked()) {
                    TU.showLong(mContext, "请选择用户类型为货主或者司机");
                    return;
                }
                if ("请选择所属区域".equals(area) || (SU.isEmpty(area))) {
                    TU.showLong(mContext, "请选择所属区域");
                    return;
                }
                if (!checkBox_contact.isChecked()) {
                    TU.showLong(mContext, "请阅读并同意鸿宝服务协议");
                    return;
                }
                sendPostRequest(
                        Constants.btn_register,
                        new ParamsService().btn_register(
                                phone, MD5.getStringMD5(pwd), type, yzm, ((TelephonyManager) getSystemService(TELEPHONY_SERVICE))
                                        .getDeviceId(), which_area, yqm),
                        this,
                        false);
                break;
            case R.id.btn_getyzm:
                phone = et_phone_number.getText().toString();
                if (!SU.isPhoneNumber(phone)) {
                    TU.showLong(mContext, "请输入正确的手机号码");
                    return;
                }
                TimeUtils.build(this).start(60000, 1000, this);
                sendPostRequest(
                        Constants.btn_getyzm,
                        new ParamsService().requestKV("phone", phone, "type", 1, Constants.btn_getyzm),
                        this,
                        false);
                break;
            case R.id.tv_web:
                Intent intent = new Intent(mContext, WebBrowserActivity.class);
                intent.putExtra(WebBrowserActivity.ACTION_KEY, WebBrowserActivity.ACTION_KEY_DEAL);
                intent.putExtra(WebBrowserActivity.URL, "file:///android_asset/deal_driver.html");
                startActivity(intent);
                break;
            case R.id.delete:
                et_phone_number.setText("");
                break;
            case R.id.deletepwd:
                et_login_pwd.setText("");
                break;
            case R.id.btn_login:
                startActivity(new Intent(mContext, LoginActivity.class));
                finish();
                break;
            case R.id.ll_select_area:
                creatDialog_area(tv_select_area);
                break;
        }
    }

    private void updateversion() {
        sendPostRequest(
                Constants.UPDATETAB,
                new ParamsService().requestKV("updateversion", App.updatetab, Constants.UPDATETAB),
                this,
                true);
    }


//    public void tv_select_area(final View view) {//返回值是this 责任面模式
//        creatDialog_area(view);
//    }

    private boolean isChecked = false;

    @Override
    public void onFinish() {
        btn_getyzm.setText("重新获取验证码");
        btn_getyzm.setClickable(true);
    }

    @Override
    public void onTick(long millisUntilFinished) {
        btn_getyzm.setClickable(false);
        btn_getyzm.setText(millisUntilFinished / 1000 + "秒后重新获取");
    }

}
