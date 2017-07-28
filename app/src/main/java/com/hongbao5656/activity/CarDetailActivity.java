package com.hongbao5656.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.hongbao5656.R;
import com.hongbao5656.base.App;
import com.hongbao5656.base.BaseActivity;
import com.hongbao5656.entity.SupplyItem;
import com.hongbao5656.okhttp.HttpException;
import com.hongbao5656.util.Constants;
import com.hongbao5656.entity.UserInfo;
import com.hongbao5656.okhttp.HttpDataHandlerListener;
import com.hongbao5656.util.IMap;
import com.hongbao5656.util.ParamsService;
import com.hongbao5656.util.ResponseParams;
import com.hongbao5656.util.TU;
import com.hongbao5656.view.RoundedImageView;
import com.squareup.okhttp.Request;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.LinkedHashMap;

public class CarDetailActivity extends BaseActivity implements
        HttpDataHandlerListener {

    private CarDetailActivity mContext;
    private String paccount;
    private ImageView iv_guanzhu;
    private ImageView iv_youxuan;
    private RoundedImageView imagehead;
    private TextView username;
    private TextView befocus;
    private TextView carno;
    private TextView carlentype;
    private UserInfo info;
    private TextView location;
    private LinearLayout creditrating;
    private TextView midTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardetail);
        this.mContext = CarDetailActivity.this;
        if (getIntent().hasExtra("account")) {
            paccount = getIntent().getStringExtra("account");
            requestDetailCar(paccount);
        }
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void requestDetailCar(String paccount) {
        sendPostRequest(
                Constants.detailcar,
                new ParamsService().requestKV("toaccount", paccount, Constants.detailcar),
                this, false);
    }

    private void tel(String stringExtra) {
        sendPostRequest(
                Constants.tel,
                new ParamsService().requestKV("goodsid", stringExtra, Constants.tel),
                this, false);
    }

    private void initView() {
        ((RelativeLayout) findViewById(R.id.leftBt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CarDetailActivity.this.finish();
            }
        });
        //关注逻辑
        iv_guanzhu = (ImageView) findViewById(R.id.iv_guanzhu);
        iv_guanzhu.setTag(0);
        iv_guanzhu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关注
                if ((int)v.getTag() == 0) {//取消状态时
                    //发送请求成功后设置背景和tag为1
                    requestQuXiaoGuanZhu();
                } else if ((int)v.getTag() == 1) {//添加状态时
                    //发送请求成功后设置背景和tag为1
                    requestAddGuanZhu();
                }
            }
        });
        //优选逻辑
        iv_youxuan = (ImageView) findViewById(R.id.iv_youxuan);
        iv_youxuan.setTag(0);
        iv_youxuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //优选
                if ((int)v.getTag() == 0) {//取消状态时
                    //发送请求成功后设置背景和tag为1
                    requestAddYouXuan();
                } else if ((int)v.getTag() == 1) {//添加状态时
                    //发送请求成功后设置背景和tag为1
                    requestQuXiaoYouXuan();
                }
            }
        });

        imagehead = (RoundedImageView) findViewById(R.id.imagehead);
        username = (TextView) findViewById(R.id.username);
        befocus = (TextView) findViewById(R.id.befocus);
        carno = (TextView) findViewById(R.id.carno);
        carlentype = (TextView) findViewById(R.id.carlentype);
        ((Button) findViewById(R.id.btn_tell)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + info.phone));
                startActivity(intent);
            }
        });
        location = (TextView) findViewById(R.id.location);
        creditrating = (LinearLayout) findViewById(R.id.creditrating);
        creditrating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CarDetailActivity.this, CreditRatingActivity.class));
            }
        });

        midTitle = (TextView) findViewById(R.id.midTitle);
    }

    private void setQuXiaoYouXuanSucceedView() {
        iv_youxuan.setImageResource(R.drawable.xing_nor);
        iv_youxuan.setTag(0);
    }

    private void setAddYouXuanSucceedView() {
        iv_youxuan.setImageResource(R.drawable.xing_pre);
        iv_youxuan.setTag(1);
    }

    private void requestQuXiaoYouXuan() {
        sendPostRequest(
                Constants.cancleYouXuan,
                new ParamsService().requestKV("toaccount", paccount, Constants.cancleYouXuan),
                this, false);
    }

    private void requestAddYouXuan() {
        sendPostRequest(
                Constants.addYouXuan,
                new ParamsService().requestKV("toaccount", paccount, Constants.addYouXuan),
                this, false);
    }

    private void setQuXiaoGuanZhuSucceedView() {
        iv_guanzhu.setImageResource(R.drawable.tianjiaguanzhu);
        iv_guanzhu.setTag(1);
    }

    private void setAddGuanZhuSucceedView() {
        iv_guanzhu.setImageResource(R.drawable.quxiaoguanzhu);
        iv_guanzhu.setTag(0);
    }

    private void requestQuXiaoGuanZhu() {
        sendPostRequest(
                Constants.cancleGaunZhu,
                new ParamsService().requestKV("toaccount", paccount, Constants.cancleGaunZhu),
                this, false);
    }

    private void requestAddGuanZhu() {
        sendPostRequest(
                Constants.addGaunZhu,
                new ParamsService().requestKV("toaccount", paccount, Constants.addGaunZhu),
                this, false);
    }

    private void setView(UserInfo vo) {
        Picasso.with(this)
                .load(Uri.parse(vo.imagehead))
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(imagehead);
        username.setText(vo.username);
        midTitle.setText(vo.username);
        if (vo.isfocus == 0) {
            setQuXiaoGuanZhuSucceedView();
        } else if (vo.isfocus == 1) {
            setAddGuanZhuSucceedView();
        }
        if (vo.isbest == 0) {
            setQuXiaoYouXuanSucceedView();
        } else if (vo.isbest == 1) {
            setAddYouXuanSucceedView();
        }
        befocus.setText(vo.befocus + "");
        carno.setText(vo.carno);
        carlentype.setText(vo.carlen + " " + vo.cartype);
        location.setText(vo.msg);
    }

    @Override
    public void setHandlerPostDataSuccess(int connectionId, Object data) throws JSONException {

    }

    @Override
    public void setHandlerPostDataError(int connectedId, Request request, HttpException httpException, String params, String content) throws JSONException {


    }

    @Override
    public void setHandlerGetDataSuccess(int connectionId, Object data) throws JSONException {
        ResponseParams<LinkedHashMap> iParams =jiexiData(data);
        if (connectionId == Constants.detailcar) {
            info = (UserInfo) JSON.parseObject(iParams.getData1().get("userinfo").toString(), UserInfo.class);
            setView(info);
        } else if (connectionId == Constants.addGaunZhu) {
            setAddGuanZhuSucceedView();
            TU.show(this, "添加关注成功");
        } else if (connectionId == Constants.cancleGaunZhu) {
            setQuXiaoGuanZhuSucceedView();
            TU.show(this, "取消关注成功");
        } else if (connectionId == Constants.addYouXuan) {
            setAddYouXuanSucceedView();
            TU.show(this, "添加优选成功");
        } else if (connectionId == Constants.cancleYouXuan) {
            setQuXiaoYouXuanSucceedView();
            TU.show(this, "取消优选成功");
        }
    }

    @Override
    public void setHandlerGetDataError(int connectionId, Object data) throws JSONException {

    }


//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            SPU.setPreferenceValueBoolean(mContext, SPU.IS_SUPPLYDETAIL, true, SPU.STORE_SETTINGS);
//            finish();
//            return false;
//        }
//        return super.onKeyDown(keyCode, event);
//    }


}
