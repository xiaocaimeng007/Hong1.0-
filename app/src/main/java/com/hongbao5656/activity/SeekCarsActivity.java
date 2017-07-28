package com.hongbao5656.activity;

import java.io.Serializable;

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
import android.widget.RelativeLayout;
import com.hongbao5656.R;
import com.hongbao5656.base.BaseActivity;
import com.hongbao5656.entity.Region;
import com.hongbao5656.entity.SupplyDetailEdite;
import com.hongbao5656.util.TU;

public class SeekCarsActivity extends BaseActivity{
	private Button btn_startsation;
	private Button btn_endsation;
	private SupplyDetailEdite info= new SupplyDetailEdite();
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
	private Region shito;
	private Region xianquto;
	private String cityfromname="";
	private Button publish_emptytime_btn;
	private EditText publish_zyjia;
	//	private TextView tv_lssb;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_seekcars);
		this.mContext = SeekCarsActivity.this;
		initView();
		initListener();
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
				if (TextUtils.isEmpty(btn_startsation.getText().toString().trim())) {
					TU.show(SeekCarsActivity.this, "出发地不能为空");
					return;
				}
				if (TextUtils.isEmpty(btn_endsation.getText().toString().trim())) {
					TU.show(SeekCarsActivity.this, "目的地不能为空");
					return;
				}
//				if (TextUtils.isEmpty(publish_zyjia.getText().toString())) {
//					TU.show(SeekCarsActivity.this, "总运价不能为空");
//					return;
//				}
//				if (TextUtils.isEmpty(publish_emptytime_btn.getText().toString())) {
//					TU.show(SeekCarsActivity.this, "空车时间不能为空");
//					return;
//				}
//				if (TextUtils.isEmpty(publish_cc_btn.getText().toString())) {
//					TU.show(SeekCarsActivity.this, "车长为空");
//					return;
//				}
//				if (TextUtils.isEmpty(publish_zl_et.getText().toString())) {
//					TU.show(SeekCarsActivity.this, "承重不能为空");
//					return;
//				}
//
//				if (publish_zl_et.getText().toString().toString().endsWith(".")) {
//					TU.show(SeekCarsActivity.this, "承重不能以.结尾");
//					return;
//				}
//
//				if (TextUtils.isEmpty(publish_et_name.getText().toString())) {
//					TU.show(SeekCarsActivity.this, "司机姓名不能为空");
//					return;
//				}
//				AppSettings.setUserName2(SeekCarsActivity.this, publish_et_name.getText().toString());
//				if (TextUtils.isEmpty(publish_xldd_sp.getText().toString())) {
//					TU.show(SeekCarsActivity.this, "联系电话不能为空");
//					return;
//				}
//				info.goodsname = publish_hwmc_et.getText().toString().trim();
//				info.carlen = publish_cc_btn.getText().toString().trim();
//				String cartype2 = publish_cx_btn.getText().toString().trim();
//				info.cartype = cartype2;
//				info.wv = Float.parseFloat(publish_zl_et.getText().toString().trim());
//				info.unit = weightUnit;
//				info.contact = publish_et_name.getText().toString().trim();
//				info.contactmobile = publish_xldd_sp.getText().toString().trim();
//				info.remark = publish_bz_et.getText().toString().trim();
//				LU.i("发布空车 对象："+JSON.toJSONString(info));
				Intent intent = new Intent(mContext,SeekCarShowActivity.class);
				intent.putExtra("info",info);
				SeekCarsActivity.this.startActivity(intent);
//				TU.show(mContext, "此功能暂未开放，敬请期待");
			}
		});

		leftBt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

//		tv_lssb.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				TU.show(mContext, "历史上报");
////				startActivity(new Intent(PublishSupplyActivity.this,CheckHistroySupplyActivity.class));
////				finish();//改成栈顶重用模式
//			}
//		});
	}

	private void initView() {
//		tv_lssb = (TextView)findViewById(R.id.tv_lssb);
		leftBt = (RelativeLayout)findViewById(R.id.leftBt);
		btn_startsation = (Button)findViewById(R.id.btn_startsation);
		btn_endsation = (Button)findViewById(R.id.btn_endsation);
		publish_cc_btn = (Button)findViewById(R.id.publish_cc_btn);
		publish_cx_btn = (Button)findViewById(R.id.publish_cx_btn);
		publish_weightunit_btn = (Button)findViewById(R.id.publish_weightunit_btn);
		publish_zl_et = (EditText)findViewById(R.id.publish_zl_et);
		publish_zl_et.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (publish_zl_et.getText().toString().indexOf(".") >= 0) {
					if (publish_zl_et.getText().toString().indexOf(".", publish_zl_et.getText().toString().indexOf(".") + 1) > 0) {
						TU.show(SeekCarsActivity.this,"已经输入\".\"不能重复输入");
						publish_zl_et.setText(publish_zl_et.getText().toString().substring(0, publish_zl_et.getText().toString().length() - 1));
						publish_zl_et.setSelection(publish_zl_et.getText().toString().length());
					}
				}

				if (s.toString().contains(".")) {
					if (s.length() - 1 - s.toString().indexOf(".") > 2) {
						s = s.toString().subSequence(0,s.toString().indexOf(".") + 3);
						publish_zl_et.setText(s);
						publish_zl_et.setSelection(s.length());
					}
				}
				if (s.toString().trim().substring(0).equals(".")) {
					s = "0" + s;
					publish_zl_et.setText(s);
					publish_zl_et.setSelection(2);
				}

				if (s.toString().startsWith("0")&& s.toString().trim().length() > 1) {
					if (!s.toString().substring(1, 2).equals(".")) {
						publish_zl_et.setText(s.subSequence(0, 1));
						publish_zl_et.setSelection(1);
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
				shengfrom = (Region)bundle.getSerializable("sheng");
				shifrom =(Region)bundle.getSerializable("shi");
				xianqufrom =(Region)bundle.getSerializable("xianqu");
				btn_startsation.setText(bundle.getString("didian"));
			}
		} else if (requestCode == 200) {
			if (data != null) {
				Bundle bundle = data.getExtras();
				info.cityto = bundle.getInt("didianId");
				shengto = (Region)bundle.getSerializable("sheng");
				shito = (Region)bundle.getSerializable("shi");
				xianquto = (Region)bundle.getSerializable("xianqu");
				btn_endsation.setText(bundle.getString("didian"));
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void publish_cx_btn(final View view){//返回值是this 责任面模式
		creatDialog_cx(view);
	}

	public void publish_cc_btn(final View view){//返回值是this 责任面模式
		creatDialog_cc(view);
	}

	public void publish_weightunit_btn(final View view){//返回值是this 责任面模式
		creatDialog_wv(view);
	}


}
