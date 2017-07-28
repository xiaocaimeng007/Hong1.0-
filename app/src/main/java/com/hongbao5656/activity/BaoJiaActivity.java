package com.hongbao5656.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.aaron.library.MLog;
import com.google.common.annotations.Beta;
import com.hongbao5656.Common.ConvertEx;
import com.hongbao5656.R;
import com.hongbao5656.base.App;
import com.hongbao5656.base.BaseActivity;
import com.hongbao5656.base.RootActivity;
import com.hongbao5656.entity.BaoJiaModel;
import com.hongbao5656.entity.SupplyDetailEdite;
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
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BaoJiaActivity extends BaseActivity {



    EditText editBaoJia;
    EditText editQiTa;
    EditText editBeiZhu;
    EditText editChePaiHao;
    EditText editXingMing;
    EditText editPhone;
    TextView txtBaoJiaStatus;
    Button btnCommit;
    LinearLayout llStatus;
    View.OnClickListener commit = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String goodsid = goodsID;
            String baojiaid = baojiaID;
            float baojia = ConvertEx.ToFloat(editBaoJia.getText().toString());
            float qita = ConvertEx.ToFloat(editQiTa.getText().toString());
            String beizhu = editBeiZhu.getText().toString();
            String chepaihao = editChePaiHao.getText().toString();
            String xingming = editXingMing.getText().toString();
            String phone = editPhone.getText().toString();
            sendPostRequest(
                    Constants.goodsbaojia,
                    new ParamsService().btn_baojia(baojiaid, goodsid, baojia, qita, beizhu, chepaihao, xingming, phone),
                    httpdata,
                    true);
        }
    };


    HttpDataHandlerListener httpdata = new HttpDataHandlerListener() {
        @Override
        public void setHandlerPostDataSuccess(int connectionId, Object data) throws JSONException {
            ResponseParams<LinkedHashMap> iParams = jiexiData(data);
            if (connectionId == Constants.goodsbaojia) {
                TU.show(getApplicationContext(), "报价成功");
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
    };

    private void initControl() {
        editBaoJia = (EditText) findViewById(R.id.txtBaoJia);
        editQiTa = (EditText) findViewById(R.id.txtQiTa);
        editBeiZhu = (EditText) findViewById(R.id.txtBeiZhu);
        editChePaiHao = (EditText) findViewById(R.id.txtChePaiHao);
        editXingMing = (EditText) findViewById(R.id.txtXingMing);
        editPhone = (EditText) findViewById(R.id.txtPhone);
        txtBaoJiaStatus = (TextView) findViewById(R.id.tvBaoJiaStatus);
        btnCommit = (Button) findViewById(R.id.publish_commit);
        llStatus = (LinearLayout) findViewById(R.id.ll_Status);

        btnCommit.setOnClickListener(commit);
    }

    String goodsID, baojiaID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baojia);
        //新页面接收数据
        Bundle bundle = this.getIntent().getExtras();
        //接收name值
        goodsID = bundle.getString("goodsid");
        baojiaID = bundle.getString("baojiaid");
//        ButterKnife.bind(this);
        initView();
        initControl();
//        TU.show(getApplicationContext(), "baojiaID=" + baojiaID);
        if (!TextUtils.isEmpty(baojiaID)) {
            sendPostRequest(
                    Constants.goodsbaojiaInfo,
                    new ParamsService().btn_baojiaXiangQing(baojiaID),
                    httpdataBaoJiaInfo,
                    true);
        } else {
            btnCommit.setVisibility(View.VISIBLE);
            llStatus.setVisibility(View.GONE);
        }
    }


    HttpDataHandlerListener httpdataBaoJiaInfo = new HttpDataHandlerListener() {
        @Override
        public void setHandlerPostDataSuccess(int connectionId, Object data) throws JSONException {
            ResponseParams<LinkedHashMap> iParams = jiexiData(data);
            String json = JSON.toJSON(iParams.getData1()).toString();
            BaoJiaModel baoJiaInfo = JSON.parseObject(json, BaoJiaModel.class);
//            TU.show(getApplicationContext(), "json=" + json);
            //TU.show(getApplicationContext(), "baoJiaInfo.drivername1=" + baoJiaInfo.drivername1);
            editBaoJia.setText(baoJiaInfo.price + "");
            editQiTa.setText(baoJiaInfo.otherprice + "");
            editBeiZhu.setText(baoJiaInfo.remark);
            editChePaiHao.setText(baoJiaInfo.carno);
            editXingMing.setText(baoJiaInfo.drivername1);
            editPhone.setText(baoJiaInfo.drivermob1);
            txtBaoJiaStatus.setText(baoJiaInfo.StatusName);
            switch (baoJiaInfo.Status) {
                case 5:
                case 6: {
                    editBaoJia.setEnabled(false);
                    editQiTa.setEnabled(false);
                    btnCommit.setVisibility(View.VISIBLE);
                }
                break;
                case 1:
                case 4:
                case 7:
                case 8: {
                    editBaoJia.setEnabled(false);
                    editQiTa.setEnabled(false);
                    editBeiZhu.setEnabled(false);
                    editChePaiHao.setEnabled(false);
                    editXingMing.setEnabled(false);
                    editPhone.setEnabled(false);
                }
                break;
                default:
                    btnCommit.setVisibility(View.VISIBLE);
                    break;
            }
            /*if (datasource.BJStatus != 1) {
                TU.showLong(SupplyDetailBaoJiaActivity.this, "当前报价状态不允许修改");
                return;
            }*/
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
    };


    private void initView() {
        //左上返回键
        findViewById(R.id.leftBt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

}
