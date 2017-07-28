package com.hongbao5656.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
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
import android.widget.TextView;

import com.example.aaron.library.MLog;
import com.hongbao5656.R;
import com.hongbao5656.base.App;
import com.hongbao5656.base.BaseActivity;
import com.hongbao5656.okhttp.HttpDataHandlerListener;
import com.hongbao5656.okhttp.HttpException;
import com.hongbao5656.util.Constants;
import com.hongbao5656.util.MD5;
import com.hongbao5656.util.ParamsService;
import com.hongbao5656.util.ResponseParams;
import com.hongbao5656.util.SPU;
import com.hongbao5656.util.SU;
import com.hongbao5656.util.TU;
import com.squareup.okhttp.Request;

import org.json.JSONException;

import java.util.LinkedHashMap;

import butterknife.BindView;

public class LoginActivity extends BaseActivity implements
        OnClickListener, HttpDataHandlerListener {
    private String phone;
    private Button login_btn2;
    private Button forgetPasswordButton;
    @BindView(R.id.btn_login) Button btn_login;
    private Context mContext;
    private ImageButton delete;
    private ImageButton deletePassword;
    private EditText et_login_tel;
    private EditText et_login_pwd;
    private TextView agreement;
    private Button btn_server;
    private CheckBox checkBox_contact;
    private String pwd;
    private CheckBox cb_mm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.mContext = LoginActivity.this;
        initView();
        initListener();
    }

    private void initListener() {
        btn_login.setOnClickListener(this);
        login_btn2.setOnClickListener(this);
        delete.setOnClickListener(this);
        deletePassword.setOnClickListener(this);
        forgetPasswordButton.setOnClickListener(this);
        btn_server.setOnClickListener(this);
        agreement.setOnClickListener(this);
        et_login_tel.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                phone = et_login_tel.getText().toString().trim();
                if (SU.isEmpty(phone)) {
                    delete.setClickable(false);
                    delete.setVisibility(View.GONE);
                } else {
                    delete.setClickable(true);
                    delete.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count,int after) {
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
                    deletePassword.setClickable(false);
                    deletePassword.setVisibility(View.GONE);
                } else {
                    deletePassword.setClickable(true);
                    deletePassword.setVisibility(View.VISIBLE);
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


        cb_mm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cb_mm.isChecked()){
                    //如果选中，显示密码
                    et_login_pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
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

//        cb_mm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked){
//                    //如果选中，显示密码
//                    et_login_pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//                }else{
//                    //否则隐藏密码
//                    et_login_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                }
//            }
//        });
    }

    private void initView() {
        forgetPasswordButton = (Button) findViewById(R.id.forgetPasswordButton);
        login_btn2 = (Button) findViewById(R.id.login_btn2);
        btn_login = (Button) findViewById(R.id.btn_login);
        delete = (ImageButton) findViewById(R.id.delete);
        deletePassword = (ImageButton) findViewById(R.id.deletePassword);
        et_login_tel = (EditText) findViewById(R.id.et_login_tel);
        et_login_tel.setText(App.phone);
        et_login_pwd = (EditText) findViewById(R.id.et_login_pwd);
        et_login_pwd.setText(App.pwd2);
        agreement = (TextView) findViewById(R.id.agreement);
        btn_server = (Button) findViewById(R.id.btn_server);
        checkBox_contact = (CheckBox) findViewById(R.id.checkBox_contact);
        cb_mm = (CheckBox)findViewById(R.id.cb_mm);
    }

    @Override
    public void setHandlerPostDataSuccess(int connectionId, Object data) throws JSONException {
        ResponseParams<LinkedHashMap> iParams =jiexiData(data);
        if (connectionId == Constants.btn_login) {
                TU.show(mContext, "登录成功");
                MLog.i("登录页登录成功");
                //储存服务器返回的token  用户信息（用户类型、权限等）
                SPU.setPreferenceValueString(mContext, SPU.EQ_Tel, phone, SPU.STORE_USERINFO);
                App.phone = phone;

                SPU.setPreferenceValueString(mContext, SPU.PWD, MD5.getStringMD5(pwd), SPU.STORE_USERINFO);
                App.pwd = MD5.getStringMD5(pwd);

                SPU.setPreferenceValueString(mContext, SPU.PWD2, pwd, SPU.STORE_USERINFO);
                App.pwd2 = pwd;

                String token = String.valueOf(iParams.getData1().get("token"));
                if (!SU.isEmpty(token)){
                    SPU.setPreferenceValueString(mContext,SPU.EQ_tokenId, token, SPU.STORE_USERINFO);
                    App.token = token;
                }

                String usertype = String.valueOf(iParams.getData1().get("usertype"));
                if (!SU.isEmpty(usertype)){
                    SPU.setPreferenceValueString(mContext, SPU.UER_TYPE, usertype, SPU.STORE_USERINFO);
                    App.UER_TYPE = usertype;
                }

                String status = String.valueOf(iParams.getData1().get("status"));
                if (!SU.isEmpty(status)){
                    SPU.setPreferenceValueString(mContext, SPU.UER_CHECK_STATE,status, SPU.STORE_USERINFO);
                    App.UER_CHECK_STATE = status;
                }

                String account = String.valueOf(iParams.getData1().get("account"));
                if (!SU.isEmpty(account)){
                    SPU.setPreferenceValueString(mContext, SPU.ACCOUNT, account, SPU.STORE_USERINFO);
                    App.account = account;
                }

                String username = String.valueOf(iParams.getData1().get("username"));
                if (!SU.isEmpty(username)){
                    SPU.setPreferenceValueString(mContext, SPU.USERNAMENEW, username, SPU.STORE_USERINFO);
                    App.username = username;
                }

                SPU.setPreferenceValueString(mContext, SPU.SET_COMPANYID, String.valueOf(iParams.getData1().get("companyid")), SPU.STORE_USERINFO);
                SPU.setPreferenceValueString(mContext, SPU.SET_ROOMID, String.valueOf(iParams.getData1().get("roomid")), SPU.STORE_USERINFO);
                SPU.setPreferenceValueString(mContext, SPU.sellcode, String.valueOf(iParams.getData1().get("sellcode")), SPU.STORE_USERINFO);

                MLog.i("[companyid]:" + SPU.getPreferenceValueString(mContext, SPU.SET_COMPANYID, "", SPU.STORE_USERINFO) +
                        "   [roomid]:" + SPU.getPreferenceValueString(mContext, SPU.SET_ROOMID, "", SPU.STORE_USERINFO));
                openActivity(MainActivity.class);
                finish();
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login://登录
                phone = et_login_tel.getText().toString().trim();
                pwd = et_login_pwd.getText().toString().trim();
                if (!SU.isPhoneNumber(phone)) {
                    TU.showLong(mContext, "请输入正确的手机号码");
                    return;
                }
                SPU.setPreferenceValueString(mContext, SPU.EQ_Tel, phone, SPU.STORE_USERINFO);
                App.phone = phone;
                if (SU.isEmpty(pwd)) {
                    TU.showLong(mContext, "密码不能为空");
                    return;
                }
//				if (!checkBox_contact.isChecked()) {
//					TU.showLong(mContext, "请阅读并同意鸿宝服务协议");
//					return;
//				}
                sendPostRequest(
                        Constants.btn_login,
                        new ParamsService().btn_login(
                                phone, MD5.getStringMD5(pwd), ((TelephonyManager) getSystemService(TELEPHONY_SERVICE))
                                        .getDeviceId()),
                        this,
                        true);
                break;
            case R.id.login_btn2://注册
                openActivity(RegisterActivity.class);
//                finish();
                break;
            case R.id.forgetPasswordButton://忘记密码
                openActivity(ForgetPasswordActivity.class);
//                overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
//                overridePendingTransition(R.anim.activity_open_enter,R.anim.activity_close_exit);
//			    finish();
                break;
            case R.id.delete:
                et_login_tel.setText("");
                break;
            case R.id.deletePassword:
                et_login_pwd.setText("");
                break;
            case R.id.agreement:
                Intent intent = new Intent(mContext, WebBrowserActivity.class);
                intent.putExtra(WebBrowserActivity.ACTION_KEY, WebBrowserActivity.ACTION_KEY_DEAL);
                intent.putExtra(WebBrowserActivity.URL, "file:///android_asset/deal_driver.html");
                startActivity(intent);
                break;
            case R.id.btn_server:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setTitle("温馨提示：");
                builder.setMessage("每天09:00-17:00竭诚为您服务！");
                builder.setCancelable(true);
                builder.setPositiveButton("拨打", listener);
                builder.setNegativeButton("取消", listener);
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }
    }

    private android.content.DialogInterface.OnClickListener listener = new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "02151656565")));
//				finish();//去拨打电话
                        dialog.cancel();
                        break;
                    case DialogInterface.BUTTON_NEUTRAL:
                        dialog.cancel();
                        break;
                }
            }
    };
}
