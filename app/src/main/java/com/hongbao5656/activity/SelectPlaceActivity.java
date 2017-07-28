package com.hongbao5656.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hongbao5656.R;
import com.hongbao5656.adapter.SelectKeywordAdapter;
import com.hongbao5656.adapter.SelectPlaceAdapter;
import com.hongbao5656.adapter.SelectPlaceAdapter.onTwiceClickListener;
import com.hongbao5656.base.BaseActivity;
import com.hongbao5656.db.DBHelper;
import com.hongbao5656.entity.Region;
import com.hongbao5656.entity.Region2;
import com.hongbao5656.util.SU;
import com.hongbao5656.util.TU;

import java.util.ArrayList;
import java.util.List;
public class SelectPlaceActivity extends BaseActivity{
	private Long quxianId;
	private Intent intent;
	private List<Region> datas= new ArrayList<Region>();
	private int shengId;
	private int shiId;
	private int type;
	private int g;
	private EditText et_search;
	private ImageButton ib_search_delete;
	private GridView gv_station;
	private SelectPlaceAdapter adapter_gv_station;
	private String keyWord;
	private Context mContext;
	private RelativeLayout rl_null;
	private TextView midTitle;
	private ArrayList<Region> region = new ArrayList<Region>();
	private ArrayList<Region2> region_keyword = new ArrayList<Region2>();
	private View btn_sure;
	private RadioGroup rg;
	private RadioButton btn_sheng;
	private RadioButton btn_shi;
	private RadioButton btn_xian;
	private Region vo = new Region();
	private Region sheng = new Region();
	private Region shi= new Region();
	private Region xianqu= new Region();
	private LinearLayout bottomlayout;
	private ListView lv_keyword;
	private SelectKeywordAdapter adapter_lv_keyword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selectplacev);
		initView();
		initMonitor();
		mContext=SelectPlaceActivity.this;
		intent = SelectPlaceActivity.this.getIntent();
		int type = intent.getIntExtra("type", 0);
		Region regionsheng = null;
		Region regionshi = null;
		Region regionxianqu = null;
		if (type==1) {
			regionsheng = (Region)intent.getSerializableExtra("shengfrom");
			regionshi = (Region)intent.getSerializableExtra("shifrom");
			regionxianqu = (Region)intent.getSerializableExtra("xianqufrom");
		}else if(type==2){
			regionsheng = (Region)intent.getSerializableExtra("shengto");
			regionshi = (Region)intent.getSerializableExtra("shito");
			regionxianqu = (Region)intent.getSerializableExtra("xianquto");
		}

		if (regionsheng!=null&&regionshi!=null&&regionxianqu!=null) {//县区/市(有省有市)
			sheng = regionsheng;
			shi = regionshi;
			btn_sheng.setChecked(false);
			btn_sheng.setText(regionsheng.Name);
			btn_shi.setChecked(false);
			btn_shi.setText(regionshi.Name);
			btn_xian.setChecked(true);
			btn_xian.setText("区县");
		}else if(regionsheng!=null&&regionshi!=null&&regionxianqu==null){
			sheng = regionsheng;

			btn_sheng.setChecked(false);
			btn_sheng.setText(regionsheng.Name);
			btn_shi.setChecked(true);
			btn_shi.setText("城市");
			btn_xian.setChecked(false);
			btn_xian.setText("区县");
		}else if(regionsheng!=null&&regionshi==null){//市(有省无市)
			sheng = regionsheng;

			btn_sheng.setChecked(false);
			btn_sheng.setText(regionsheng.Name);
			btn_shi.setChecked(true);
			btn_shi.setText("城市");
			btn_xian.setChecked(false);
			btn_xian.setText("区县");
		}else{//省（无省无市）
			getDBDatas(0);
			adapter_gv_station.clearListView();
			adapter_gv_station.update_list(region);//(显示该市下的所有县区)
			btn_sheng.setChecked(true);
			btn_sheng.setText("省份");
			btn_shi.setChecked(false);
			btn_shi.setText("城市");
			sheng = null;
			shi = null;
			btn_xian.setChecked(false);
			btn_xian.setText("区县");
		}
	}

	private void initView() {
		gv_station = (GridView) findViewById(R.id.gv_station);
		lv_keyword = (ListView)findViewById(R.id.lv_keyword);
		adapter_gv_station = new SelectPlaceAdapter(this, region);
		adapter_lv_keyword = new SelectKeywordAdapter(this, region_keyword);
		lv_keyword.setAdapter(adapter_lv_keyword);
		gv_station.setAdapter(adapter_gv_station);
		rl_null = (RelativeLayout)findViewById(R.id.rl_null);
		midTitle = (TextView)findViewById(R.id.midTitle);
		btn_sure = (Button)findViewById(R.id.btn_sure);
		rg = (RadioGroup)findViewById(R.id.rg);
		btn_sheng = (RadioButton)findViewById(R.id.btn_sheng);
		btn_shi = (RadioButton)findViewById(R.id.btn_shi);
		btn_xian = (RadioButton)findViewById(R.id.btn_xian);
		bottomlayout = (LinearLayout)findViewById(R.id.bottomlayout);
		et_search = (EditText)findViewById(R.id.et_search);
		ib_search_delete = (ImageButton)findViewById(R.id.ib_search_delete);
	}

	private void initMonitor() {
		et_search.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				keyWord = et_search.getText().toString();
				if (keyWord.length()>0) {
					bottomlayout.setVisibility(View.GONE);
					lv_keyword.setVisibility(View.VISIBLE);
					//TODO 用关键词去检索省市县区数据
					getDBDatas4Keyword(keyWord);
					adapter_lv_keyword.update_list(region_keyword);
				}else{
					bottomlayout.setVisibility(View.VISIBLE);
					lv_keyword.setVisibility(View.GONE);
				}
				if (SU.isEmpty(keyWord)) {
					ib_search_delete.setClickable(false);
					ib_search_delete.setVisibility(View.GONE);
				} else {
					ib_search_delete.setClickable(true);
					ib_search_delete.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		ib_search_delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				et_search.setText("");
			}
		});
		adapter_gv_station.setOnTwiceClickListener(new onTwiceClickListener() {
			@Override
			public void twiceClick(Region info) {
				vo = info;
				if (info.ParentId==0) {//当前点击的是(所有一级行政区中的)省
					// 拿到点击的省Id再去查市
					getDBDatas(info.Id);
					adapter_gv_station.clearListView();
					adapter_gv_station.update_list(region);//(显示该省下的所有市)
					btn_sheng.setChecked(false);
					btn_sheng.setText(info.Name);
					sheng = info;
					shi = null;
					xianqu = null;
					btn_shi.setChecked(true);
					btn_shi.setText("城市");
				} else if(info.Auxiliary==2&&info.ParentId!=0){//当前点击的是(所有一级行政区中的)直辖市
					getDBDatas(info.Id);
					adapter_gv_station.clearListView();
					adapter_gv_station.update_list(region);//(显示该市下的所有县区)
					btn_shi.setChecked(false);
					btn_shi.setText(info.Name);
					shi = info;
					xianqu = null;
					btn_xian.setChecked(true);
					btn_xian.setText("区县");
				}else if(info.Auxiliary==3){//（显示省+所有市）
					xianqu = info;
					Intent intent = new Intent();
					if(shi==null || SU.isEmpty(shi.Name)){
						intent.putExtra("didian", sheng.Name+"-"+info.Name);
					}else{
						intent.putExtra("didian", sheng.Name+"-"+shi.Name+"-"+info.Name);
					}
					intent.putExtra("weather", info.Name);
					intent.putExtra("didianId", info.Id);
					intent.putExtra("sheng", sheng);
					intent.putExtra("shi", shi);
					intent.putExtra("xianqu", xianqu);
					SelectPlaceActivity.this.setResult(Activity.RESULT_OK,intent);
					finish();
					return;
				}
			}
		});

		adapter_lv_keyword.setItemClickListener(new SelectKeywordAdapter.onItemClickListener() {
			@Override
			public void itemClick(Region2 info) {
				vo.Name = info.Name;
				vo.Id = info.Id;
				vo.ParentId = info.ParentId;
				vo.Auxiliary = info.Auxiliary;
				vo.lat = info.lat;
				vo.mlong = info.mlong;

				if (info.ParentId==0) {//当前点击的是(所有一级行政区中的)省
					// 拿到点击的省Id再去查市
//					getDBDatas(info.Id);
//					adapter_gv_station.clearListView();
//					adapter_gv_station.update_list(region);//(显示该省下的所有市)
//					btn_sheng.setChecked(false);
//					btn_sheng.setText(info.Name);
					Region s = new Region();
					s.Name = info.Name;
					s.Id = info.Id;
					s.ParentId = info.ParentId;
					s.Auxiliary = info.Auxiliary;
					s.lat = info.lat;
					s.mlong = info.mlong;
					sheng = s;
					shi = null;
					xianqu = null;
//					btn_shi.setChecked(true);
//					btn_shi.setText("城市");
				} else if(info.Auxiliary==2&&info.ParentId!=0){//当前点击的是(所有一级行政区中的)直辖市
//					getDBDatas(info.Id);
//					adapter_gv_station.clearListView();
//					adapter_gv_station.update_list(region);//(显示该市下的所有县区)
//					btn_shi.setChecked(false);
//					btn_shi.setText(info.Name);
					Region s = new Region();
					s.Name = info.ParentNmae;
					s.Id = info.ParentId;
					s.ParentId = info.ParentId;
					s.Auxiliary = info.Auxiliary;
					s.lat = info.lat;
					s.mlong = info.mlong;
					sheng = s;

					Region s2 = new Region();
					s2.Name = info.Name;
					s2.Id = info.Id;
					s2.ParentId = info.ParentId;
					s2.Auxiliary = info.Auxiliary;
					s2.lat = info.lat;
					s2.mlong = info.mlong;
					shi = s2;

					xianqu = null;
//					btn_xian.setChecked(true);
//					btn_xian.setText("区县");
				}else if(info.Auxiliary==3){//（显示省+所有市）
					Region s = new Region();
					s.Name = info.GrandpaNmae;
					s.Id = info.GrandpaId;
					s.ParentId = info.ParentId;
					s.Auxiliary = info.Auxiliary;
					s.lat = info.lat;
					s.mlong = info.mlong;
					sheng = s;

					Region s2 = new Region();
					s2.Name = info.ParentNmae;
					s2.Id = info.ParentId;
					s2.ParentId = info.ParentId;
					s2.Auxiliary = info.Auxiliary;
					s2.lat = info.lat;
					s2.mlong = info.mlong;
					shi = s2;

					Region s3 = new Region();
					s3.Name = info.Name;
					s3.Id = info.Id;
					s3.ParentId = info.ParentId;
					s3.Auxiliary = info.Auxiliary;
					s3.lat = info.lat;
					s3.mlong = info.mlong;
					xianqu = s3;
				}

				Intent intent = new Intent();
				if(shi==null){
					intent.putExtra("didian", vo.Name);
				}else if(xianqu == null){
					intent.putExtra("didian", sheng.Name+"-"+vo.Name);
				}else {
					intent.putExtra("didian", sheng.Name+"-"+shi.Name+"-"+xianqu.Name);
				}
				intent.putExtra("weather", vo.Name);
				intent.putExtra("didianId", vo.Id);
				intent.putExtra("sheng", sheng);
				intent.putExtra("shi", shi);
				intent.putExtra("xianqu", xianqu);
				SelectPlaceActivity.this.setResult(Activity.RESULT_OK,intent);
				finish();
				return;
			}
		});

		btn_sure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btn_xian.isChecked()) {
					if (sheng!=null&&shi!=null) {
						Intent intent = new Intent();
						intent.putExtra("didian", sheng.Name+"-"+shi.Name);
						intent.putExtra("weather", shi.Name);
						intent.putExtra("didianId", shi.Id);
						intent.putExtra("sheng", sheng);
						intent.putExtra("shi", shi);
						intent.putExtra("xianqu", xianqu);
						SelectPlaceActivity.this.setResult(Activity.RESULT_OK,intent);
						finish();
						return;
					}else{
						TU.show(mContext, "请选择省市");
						btn_sheng.setChecked(true);
						return;
					}
				}
				if (btn_shi.isChecked()&&sheng!=null) {
					Intent intent = new Intent();
					intent.putExtra("didian", sheng.Name);
					intent.putExtra("weather", sheng.Name);
					intent.putExtra("didianId", sheng.Id);
					intent.putExtra("sheng", sheng);
					intent.putExtra("shi", shi);
					intent.putExtra("xianqu", xianqu);
					SelectPlaceActivity.this.setResult(Activity.RESULT_OK,intent);
					finish();
					return;
				}

				if (btn_sheng.isChecked()&&sheng==null) {
					finish();
					return;
				}

				if (btn_sheng.isChecked()&&sheng!=null) {
					Intent intent = new Intent();
					intent.putExtra("didian", sheng.Name);
					intent.putExtra("weather", sheng.Name);
					intent.putExtra("didianId", sheng.Id);
					intent.putExtra("sheng", sheng);
					intent.putExtra("shi", shi);
					intent.putExtra("xianqu", xianqu);
					SelectPlaceActivity.this.setResult(Activity.RESULT_OK,intent);
					finish();
					return;
				}
			}
		});

		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
					case R.id.btn_sheng:
						btn_sheng.setChecked(true);
						btn_shi.setChecked(false);
						btn_xian.setChecked(false);
						getDBDatas(0);
						adapter_gv_station.clearListView();
						adapter_gv_station.update_list(region);
						break;
					case R.id.btn_shi:
						btn_sheng.setChecked(false);
						btn_shi.setChecked(true);
						btn_xian.setChecked(false);
						if (sheng==null&&(btn_sheng.getText().toString()).equals("省份")) {
							TU.show(mContext, "您还没有选择省份");
							return;
						}
						if (sheng!=null) {
							getDBDatas(sheng.Id);
							adapter_gv_station.clearListView();
							adapter_gv_station.update_list(region);
						}
						break;
					case R.id.btn_xian:
						if (shi==null&&sheng==null) {
							TU.show(mContext, "您还没有选择省份");
							return;
						}
						if (shi==null) {
							TU.show(mContext, "您还没有选择城市");
							return;
						}
						if (shi!=null) {
							btn_sheng.setChecked(false);
							btn_shi.setChecked(false);
							btn_xian.setChecked(true);
							getDBDatas(shi.Id);
							adapter_gv_station.clearListView();
							adapter_gv_station.update_list(region);
						}
						break;
				}
			}
		});



		findViewById(R.id.leftBt).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	private void getDBDatas(int type) {
		region = DBHelper.getInstance(mContext).queryDatas(mContext, type);
	}

	private void getDBDatas4Keyword(String keyword){
		region_keyword = DBHelper.getInstance(mContext).queryDatas4Keyword(mContext, "'%"+keyword+"%'");
	}

}
