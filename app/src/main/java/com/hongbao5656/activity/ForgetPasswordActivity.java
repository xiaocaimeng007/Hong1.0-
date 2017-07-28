package com.hongbao5656.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hongbao5656.R;
import com.hongbao5656.base.App;
import com.hongbao5656.base.BaseActivity;
import com.hongbao5656.okhttp.HttpException;
import com.hongbao5656.util.Constants;
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

public class ForgetPasswordActivity extends BaseActivity implements
		HttpDataHandlerListener,OnClickListener,OnTimeFinshListener {
	private Button btnlogin;
	private Button btn_get_security_code;
	private EditText etphone;
	private EditText etyzm;
	private String yzm;
	private String imei;
	private TextView tv_web;
	private CheckBox checkBox_contact;
	private String etyzms;
	private String phone;
	private Button login_btn2;
	private RelativeLayout leftBt;
	private ImageButton delete;
	private ImageButton deletepwd;
	private EditText et_phone_number;
	private EditText et_login_pwd;
	private Button btn_submit;
	private String pwd;
	private Context mContext;
	private EditText et_security_code;
	private CheckBox cb_mm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgetpassword);
		this.mContext = ForgetPasswordActivity.this;
		initView();
		initListener();
	}

	private void initListener() {
		btn_submit.setOnClickListener(this);
		leftBt.setOnClickListener(this);
		delete.setOnClickListener(this);
		deletepwd.setOnClickListener(this);
		btn_get_security_code.setOnClickListener(this);
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
			private String pwd;
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
	}

	private void initView() {
		leftBt = (RelativeLayout)findViewById(R.id.leftBt);
		delete = (ImageButton)findViewById(R.id.delete);
		deletepwd = (ImageButton)findViewById(R.id.deletepwd);
		et_phone_number = (EditText) findViewById(R.id.et_phone_number);
		et_phone_number.setText(App.phone);
		et_login_pwd = (EditText)findViewById(R.id.et_login_pwd);
		btn_get_security_code = (Button) findViewById(R.id.btn_get_security_code);
		btn_submit = (Button)findViewById(R.id.btn_submit);
		et_security_code = (EditText)findViewById(R.id.et_security_code);
		cb_mm = (CheckBox)findViewById(R.id.cb_mm);
	}

	@Override
	public void setHandlerPostDataSuccess(int connectionId, Object data) throws JSONException {
		ResponseParams<LinkedHashMap> iParams =jiexiData(data);
		if (connectionId == Constants.btn_getyzm) {
				TimeUtils.build(this).start(60000, 1000, this);
				yzm = String.valueOf(iParams.getData1().get("code"));
//				et_security_code.setText(yzm);
		}else if(connectionId == Constants.btn_submit){
				TU.show(mContext, "修改密码成功！");
				openActivity(LoginActivity.class);
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
		phone = et_phone_number.getText().toString();
		etyzms = et_security_code.getText().toString();
		pwd = et_login_pwd.getText().toString();
		switch (v.getId()) {
			case R.id.btn_submit:
				if (!SU.isPhoneNumber(phone)) {
					TU.showLong(mContext, "请输入正确的手机号码");
					return;
				}
				if (SU.isEmpty(etyzms)) {
					TU.showLong(mContext, "验证码不能为空");
					return;
				}
				if(!SU.isEmpty(yzm)){
					if (!etyzms.equals(yzm)) {
						TU.showLong(mContext, "验证码输入错误");
						return;
					}
				}

				if (SU.isEmpty(pwd)) {
					TU.showLong(mContext, "密码不能为空");
					return;
				}

				sendPostRequest(
						Constants.btn_submit,
						new ParamsService().requestKV(
								"phone", phone,
								"yzm", etyzms,
								"pwd", MD5.getStringMD5(pwd),
								Constants.btn_submit),
						this,false);
				break;
			case R.id.leftBt:
//				finish();
				Intent intent = new Intent(this, LoginActivity.class);
				ComponentName cn = intent.getComponent();
				Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
				startActivity(mainIntent);
//				overridePendingTransition(R.anim.activity_close_enter,R.anim.activity_close_exit);
				break;
			case R.id.delete:
				et_phone_number.setText("");
				break;
			case R.id.deletepwd:
				et_login_pwd.setText("");
				break;
			case R.id.btn_get_security_code:
				phone = et_phone_number.getText().toString();
				if (!SU.isPhoneNumber(phone)) {
					TU.showLong(mContext, "请输入正确的手机号码");
					return;
				}

				sendPostRequest(
						Constants.btn_getyzm,
						new ParamsService().requestKV("phone",phone,"type",2,Constants.btn_getyzm),
						this,
						false);
		}

	}

	@Override
	public void onFinish() {
		btn_get_security_code.setText("重新获取验证码");
		btn_get_security_code.setClickable(true);
	}

	@Override
	public void onTick(long millisUntilFinished) {
		btn_get_security_code.setClickable(false);
		btn_get_security_code.setText(millisUntilFinished / 1000 + "秒后重新获取");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){
			Intent intent = new Intent(this, LoginActivity.class);
			ComponentName cn = intent.getComponent();
			Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
			startActivity(mainIntent);
			return  false;
		}
		return super.onKeyDown(keyCode, event);
	}


}
