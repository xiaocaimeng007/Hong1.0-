package com.hongbao5656.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hongbao5656.R;
import com.hongbao5656.base.BaseActivity;
import com.hongbao5656.db.DBHelper;
import com.hongbao5656.entity.Region;
import com.hongbao5656.entity.SupplyDetailEdite;
import com.hongbao5656.util.TU;

import java.io.Serializable;


public class LCYJActivity extends BaseActivity{
	private Button btn_startsation;
	private Button btn_endsation;
	private Context mContext;
	private Serializable sheng;
	private Serializable shi;
	private Region shengfrom;
	private Region shifrom;
	private Region xianqufrom;
	private Region shengto;
	private Region shito;
	private Region xianquto;
	private String cityfromname="";
	private SupplyDetailEdite info= new SupplyDetailEdite();
	private EditText btn_dj;
	private double lat1;
	private double mlong1;
	private double lat2;
	private double mlong2;
	private Button publish_commit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lcjs);
//		ButterKnife.bind(this);
		this.mContext = LCYJActivity.this;
		initView();
		initListener();
		setInputUI(findViewById(R.id.rl_super),this);
	}

	private void initListener() {
		findViewById(R.id.leftBt).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		btn_startsation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				if (!App.flag) {
//					TU.showShort(mContext, "正在初始化数据,请稍后再试...");
//					return;
//				}
				Intent intent = new Intent(mContext,SelectPlaceActivity.class);
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
//				if (!App.flag) {
//					TU.showShort(mContext, "正在初始化数据,请稍后再试...");
//					return;
//				}
				Intent intent = new Intent(mContext,SelectPlaceActivity.class);
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
				if (TextUtils.isEmpty(btn_startsation.getText().toString().trim())) {
					TU.show(LCYJActivity.this, "出发地不能为空");
					return;
				}
				if (TextUtils.isEmpty(btn_endsation.getText().toString().trim())) {
					TU.show(LCYJActivity.this, "目的地不能为空");
					return;
				}
				if (TextUtils.isEmpty(btn_dj.getText().toString())) {
					TU.show(LCYJActivity.this, "单价不能为空");
					return;
				}
				Intent intent = new Intent();
				intent.setClass(mContext, MapDetailActivity.class);
				intent.putExtra("lat1", lat1);
				intent.putExtra("mlong1", mlong1);
				intent.putExtra("lat2", lat2);
				intent.putExtra("mlong2", mlong2);
				intent.putExtra("btn_startsation", btn_startsation.getText().toString().trim());
				intent.putExtra("btn_endsation", btn_endsation.getText().toString().trim());
				intent.putExtra("btn_dj", btn_dj.getText().toString().trim());
				mContext.startActivity(intent);
//				mContext.startActivity(new Intent(mContext,MapDetailActivity.class));
			}
		});

	}



	private void initView() {
		btn_startsation = (Button)findViewById(R.id.btn_startsation);
		btn_endsation = (Button)findViewById(R.id.btn_endsation);
		btn_startsation.setText(cityfromname);
		btn_dj = (EditText)findViewById(R.id.btn_dj);
		btn_dj.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (btn_dj.getText().toString().indexOf(".") >= 0) {
					if (btn_dj.getText().toString().indexOf(".", btn_dj.getText().toString().indexOf(".") + 1) > 0) {
						TU.show(LCYJActivity.this,"已经输入\".\"不能重复输入");
						btn_dj.setText(btn_dj.getText().toString().substring(0, btn_dj.getText().toString().length() - 1));
						btn_dj.setSelection(btn_dj.getText().toString().length());
					}
				}
				if (s.toString().contains(".")) {
					if (s.length() - 1 - s.toString().indexOf(".") > 2) {
						s = s.toString().subSequence(0,s.toString().indexOf(".") + 3);
						btn_dj.setText(s);
						btn_dj.setSelection(s.length());
					}
				}
				if (s.toString().trim().substring(0).equals(".")) {
					s = "0" + s;
					btn_dj.setText(s);
					btn_dj.setSelection(2);
				}

				if (s.toString().startsWith("0")&& s.toString().trim().length() > 1) {
					if (!s.toString().substring(1, 2).equals(".")) {
						btn_dj.setText(s.subSequence(0, 1));
						btn_dj.setSelection(1);
					}
				}
//				if (s.toString().endsWith(".")&& s.toString().trim().length() > 1) {
//					if (!s.toString().substring(1, 2).equals(".")) {
//						publish_zl_et.setText(s.subSequence(0, 1));
//						publish_zl_et.setSelection(1);
//					}
//				}
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		publish_commit = (Button)findViewById(R.id.publish_commit);
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
				Region r = DBHelper.getInstance(mContext).queryLatLong(mContext,info.cityfrom);
				lat1 = r.lat;
				mlong1 = r.mlong;
				shengfrom = (Region)bundle.getSerializable("sheng");
				shifrom =(Region)bundle.getSerializable("shi");
				xianqufrom =(Region)bundle.getSerializable("xianqu");
				btn_startsation.setText(bundle.getString("didian"));
			}
		} else if (requestCode == 200) {
			if (data != null) {
				Bundle bundle = data.getExtras();
				info.cityto = bundle.getInt("didianId");
				Region r = DBHelper.getInstance(mContext).queryLatLong(mContext,info.cityto);
				lat2 = r.lat;
				mlong2 = r.mlong;
				shengto = (Region)bundle.getSerializable("sheng");
				shito = (Region)bundle.getSerializable("shi");
				xianquto = (Region)bundle.getSerializable("xianqu");
				btn_endsation.setText(bundle.getString("didian"));
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
