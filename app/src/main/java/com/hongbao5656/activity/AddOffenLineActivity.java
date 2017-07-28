package com.hongbao5656.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.hongbao5656.R;
import com.hongbao5656.base.App;
import com.hongbao5656.base.BaseActivity;
import com.hongbao5656.okhttp.HttpException;
import com.hongbao5656.util.Constants;
import com.hongbao5656.entity.OffenLine;
import com.hongbao5656.entity.Region;
import com.hongbao5656.okhttp.HttpDataHandlerListener;
import com.hongbao5656.util.IMap;
import com.hongbao5656.util.ParamsService;
import com.hongbao5656.util.ResponseParams;
import com.hongbao5656.util.SPU;
import com.hongbao5656.util.TU;
import com.squareup.okhttp.Request;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class AddOffenLineActivity extends BaseActivity implements
        HttpDataHandlerListener {
    private Button btn_startsation;
    private Button btn_endsation;
    private OffenLine info = new OffenLine();
    private ArrayList<OffenLine> lines = new ArrayList<OffenLine>();
    private Button publish_commit;
    private Context mContext;
    private RelativeLayout leftBt;
    private Region shengfrom;
    private Region shifrom;
    private Region xianqufrom;
    private Region shengto;
    private Region shito;
    private Region xianquto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addoffenline);
        this.mContext = AddOffenLineActivity.this;
        initView();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initListener() {
        btn_startsation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//				if (!App.flag) {
//					TU.showShort(mContext, "正在初始化数据,请稍后再试...");
//					return;
//				}
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
//				if (!App.flag) {
//					TU.showShort(mContext, "正在初始化数据,请稍后再试...");
//					return;
//				}
                Intent intent = new Intent(mContext, SelectPlaceActivity.class);
                intent.putExtra("type", 2);
                intent.putExtra("shengto", shengto);
                intent.putExtra("shito", shito);
                intent.putExtra("xianquto", xianquto);
                startActivityForResult(intent, 200);
            }
        });
        publish_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (25 <= SPU.getPreferenceValueInt(mContext, SPU.LINES, 0, SPU.STORE_USERINFO)) {
                    TU.show(mContext, "常跑线路最多添加25条");
                    return;
                }
                if (TextUtils.isEmpty(btn_startsation.getText().toString().trim())) {
                    TU.show(AddOffenLineActivity.this, "出发地为空");
                    return;
                }
                if (TextUtils.isEmpty(btn_endsation.getText().toString().trim())) {
                    TU.show(AddOffenLineActivity.this, "目的地为空");
                    return;
                }
                info.mobile = App.phone;
                sendPostRequest(
                        Constants.addoffenline,
                        new ParamsService().addoffenline(info),
                        AddOffenLineActivity.this,
                        false);
            }
        });

        leftBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        leftBt = (RelativeLayout) findViewById(R.id.leftBt);
        btn_startsation = (Button) findViewById(R.id.btn_startsation);
        btn_endsation = (Button) findViewById(R.id.btn_endsation);
        publish_commit = (Button) findViewById(R.id.publish_commit);
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
                String cityfromname = bundle.getString("didian");
                btn_startsation.setText(cityfromname);
                info.cityfromname = cityfromname;
            }
        } else if (requestCode == 200) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                info.cityto = bundle.getInt("didianId");
                shengto = (Region) bundle.getSerializable("sheng");
                shito = (Region) bundle.getSerializable("shi");
                xianquto = (Region) bundle.getSerializable("xianqu");
                String citytoname = bundle.getString("didian");
                btn_endsation.setText(citytoname);
                info.citytoname = citytoname;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setHandlerPostDataSuccess(int connectionId, Object data) throws JSONException {
        ResponseParams<LinkedHashMap> iParams =jiexiData(data);
        if (connectionId == Constants.addoffenline) {
            TU.show(mContext, "添加成功");
            int lines = SPU.getPreferenceValueInt(mContext, SPU.LINES, 0, SPU.STORE_USERINFO);
            if (lines >= 0) {
                SPU.setPreferenceValueInt(mContext, SPU.LINES, lines + 1, SPU.STORE_USERINFO);
            }
            App.supplys = null;
            App.supplys = (ArrayList<OffenLine>) IMap.getData2FromResponse(iParams, OffenLine.class);
            SPU.setPreferenceValueBoolean(mContext, SPU.ISOFFENLINE, true, SPU.STORE_SETTINGS);
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
}
