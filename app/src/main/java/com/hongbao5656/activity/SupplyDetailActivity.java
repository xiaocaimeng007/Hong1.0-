package com.hongbao5656.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.hongbao5656.R;
import com.hongbao5656.base.BaseActivity;
import com.hongbao5656.okhttp.HttpException;
import com.hongbao5656.util.Constants;
import com.hongbao5656.db.DBHelper;
import com.hongbao5656.entity.Region;
import com.hongbao5656.entity.SupplyDetailEdite;
import com.hongbao5656.entity.User;
import com.hongbao5656.okhttp.HttpDataHandlerListener;
import com.hongbao5656.util.ParamsService;
import com.hongbao5656.util.ResponseParams;
import com.hongbao5656.util.SPU;
import com.hongbao5656.util.SU;
import com.hongbao5656.util.TU;
import com.hongbao5656.util.TimeUtils;
import com.hongbao5656.view.RoundedImageView;
import com.squareup.okhttp.Request;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.LinkedHashMap;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class SupplyDetailActivity extends BaseActivity implements
        HttpDataHandlerListener {
    private TextView include_topaddress_tv_begin;
    private TextView include_topaddress_tv_end;
    private Context mContext;
    private RelativeLayout leftBt;
    private TextView tv_publishtime2;
    private TextView tv_goodsname2;
    private TextView tv_goodsweight2;
    private TextView tv_carlong2;
    private TextView tv_xwh2;
    //    private TextView tv_tel2;
    private TextView tv_remark2;
    private TextView tv_hzname2;
    private Button btn_tell;
    private SupplyDetailEdite vo;
    private String goodsid = "";
    private String baojiaid = "";
    private TextView tv_right;
    private Button btn_zhifu;
    private Button btn_baojia;
    private TextView tv_glshu;
    private TextView tv_ditu;
    private RoundedImageView raiv_userphoto;
    private ImageView tv_soushu;
    private TextView tv_jyshu2;
    private TextView tv_fahuo2;
    //    private TextView tv_cartype;
    private SupplyDetailEdite supplyDetailEdite = null;
    private TextView tv_hide;
    private LinearLayout ll_detail_view;
    private RelativeLayout rl_hide;
    private TextView tv_tel2;
    private TextView tv_gsmc2;
    private LinearLayout ll_gsmc;
    private LinearLayout ll_xwh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        this.mContext = SupplyDetailActivity.this;
        if (getIntent().hasExtra("vo")) {
            goodsid = getIntent().getStringExtra("vo");
            requestEditSupply(goodsid);
        }
        initView();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void requestEditSupply(String goodsid) {
        sendPostRequest(
                Constants.detailsupply,
                new ParamsService().requestKV("goodsid", goodsid, Constants.detailsupply),
                this,
                false);
    }

    private void tel(String stringExtra) {
        sendPostRequest(
                Constants.tel,
                new ParamsService().requestKV("goodsid", stringExtra, Constants.tel),
                this, false);
    }

    private void initListener() {
        leftBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPU.setPreferenceValueBoolean(mContext, SPU.IS_SUPPLYDETAIL, true, SPU.STORE_SETTINGS);
                finish();
            }
        });
        btn_tell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ("N".equals(supplyDetailEdite.flagauthority)) {
                    TU.showLong(SupplyDetailActivity.this, "未3G充值，该功能无权使用");
                } else if ("Y".equals(supplyDetailEdite.flagauthority)) {
//                    Intent intent = new Intent(Intent.ACTION_CALL);
//                    Uri data = Uri.parse("tel:" + supplyDetailEdite.contactmobile);
//                    intent.setData(data);
//                  startActivity(intent);
                    tel(goodsid);
                    Uri uri = Uri.parse("tel:" + supplyDetailEdite.contactmobile);
//                  Uri uri = Uri.parse("tel:" + "18792869521");
                    Intent it = new Intent(Intent.ACTION_DIAL, uri);
                    mContext.startActivity(it);
                }
            }
        });
        tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                HashMap<String, String> params = new HashMap<String, String>();
//                params.put(ShareUtils.TITLE,
//                        vo.cityfromname + vo.cityfromparentname
//                        + "至"+
//                        vo.citytoname + vo.citytoparentname);
//                params.put(ShareUtils.TEXT, "400-681-7878");
//                params.put(ShareUtils.URL,"http://www.56hb.net");
//                params.put(ShareUtils.TITLEURL,"http://www.56hb.net");
//                params.put(ShareUtils.IMAGEURL,"http://www.56hb.net/Images/ico.png");
//                SharePopupWindow sharePw = new SharePopupWindow();
//                sharePw.showPopupWindow(SupplyDetailActivity.this, params);
//
//                showShare();
            }
        });
        btn_zhifu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(PayActivity.class);
//                TU.show(SupplyDetailActivity.this, "开发中...");
            }
        });

        btn_baojia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //用Bundle携带数据
                Bundle bundle = new Bundle();
                //传递name参数为tinyphp
                bundle.putString("goodsid", goodsid);
                bundle.putString("baojiaid", baojiaid);

                //openActivity(PayActivity.class);
                openActivity(BaoJiaActivity.class, bundle);
                //openActivityForResult(BaoJiaActivity.class,bundle,0);
            }
        });

        tv_soushu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SupplyDetailActivity.this, ComplaintActivity.class);
                intent.putExtra("goodsid", goodsid);
                startActivity(intent);
            }
        });
        tv_ditu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, MapDetailActivity.class);
                Region r = DBHelper.getInstance(mContext).queryLatLong(mContext, supplyDetailEdite.cityfrom);
                Region r2 = DBHelper.getInstance(mContext).queryLatLong(mContext, supplyDetailEdite.cityto);
                intent.putExtra("lat1", r.lat);
                intent.putExtra("mlong1", r.mlong);
                intent.putExtra("lat2", r2.lat);
                intent.putExtra("mlong2", r2.mlong);
                intent.putExtra("btn_startsation", include_topaddress_tv_begin.getText().toString().trim());
                intent.putExtra("btn_endsation", include_topaddress_tv_end.getText().toString().trim());
                intent.putExtra("btn_dj", 0 + "");
                mContext.startActivity(intent);
            }
        });
        rl_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("0".equals(tv_hide.getTag().toString())) {
                    tv_hide.setBackgroundResource(R.drawable.arrow_zbove);
                    ll_detail_view.setVisibility(View.VISIBLE);
                    tv_hide.setTag(1);
                } else {
                    tv_hide.setBackgroundResource(R.drawable.arrow_bottom);
                    ll_detail_view.setVisibility(View.GONE);
                    tv_hide.setTag(0);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String baojiaid = null;
            if (bundle != null)
                baojiaid = bundle.getString("baojiaid");
//            Log.d("text",text);
//            editText.setText(text);
            //补充报价
        }
    }

    private void initView() {
        leftBt = (RelativeLayout) findViewById(R.id.leftBt);
        include_topaddress_tv_begin = (TextView) findViewById(R.id.include_topaddress_tv_begin);//起始地
        include_topaddress_tv_end = (TextView) findViewById(R.id.include_topaddress_tv_end);//目的地
        tv_publishtime2 = (TextView) findViewById(R.id.tv_publishtime2);//发布时间
        tv_goodsname2 = (TextView) findViewById(R.id.tv_goodsname2);//货物名称
        tv_goodsweight2 = (TextView) findViewById(R.id.tv_goodsweight2);//重量体积
        tv_carlong2 = (TextView) findViewById(R.id.tv_carlong2);//车长车型
//        tv_cartype = (TextView) findViewById(R.id.tv_cartype);
        tv_xwh2 = (TextView) findViewById(R.id.tv_xwh2);//大厅席位
        ll_xwh = (LinearLayout) findViewById(R.id.ll_xwh);//大厅席位
        tv_gsmc2 = (TextView) findViewById(R.id.tv_gsmc2);//公司名称
        ll_gsmc = (LinearLayout) findViewById(R.id.ll_gsmc);//公司名称
        tv_tel2 = (TextView) findViewById(R.id.tv_tel2);
        tv_remark2 = (TextView) findViewById(R.id.tv_remark2);//备注
        tv_hzname2 = (TextView) findViewById(R.id.tv_hzname2);//货主名称

        raiv_userphoto = (RoundedImageView) findViewById(R.id.raiv_userphoto);
        tv_soushu = (ImageView) findViewById(R.id.tv_soushu);//投诉
        tv_jyshu2 = (TextView) findViewById(R.id.tv_jyshu2);//交易数
        tv_fahuo2 = (TextView) findViewById(R.id.tv_fahuo2);//交易数

        btn_tell = (Button) findViewById(R.id.btn_tell);//电话咨询
        tv_right = (TextView) findViewById(R.id.tv_right);
        btn_zhifu = (Button) findViewById(R.id.btn_zhifu);//支付信息费
        tv_glshu = (TextView) findViewById(R.id.tv_glshu);//公里数
        tv_ditu = (TextView) findViewById(R.id.tv_ditu);//查看地图


        tv_hide = (TextView) findViewById(R.id.tv_hide);//隐藏
        ll_detail_view = (LinearLayout) findViewById(R.id.ll_detail_view);//
        rl_hide = (RelativeLayout) findViewById(R.id.rl_hide);//隐藏

        btn_baojia = (Button) findViewById(R.id.btn_BaoJia);
    }

    private void setView(SupplyDetailEdite info, User user) {
//      include_topaddress_tv_begin.setText(info.cityfromname + info.cityfromparentname);
        if (!SU.isEmpty(info.cityfromname)) {
            include_topaddress_tv_begin.setText(info.cityfromname);
        } else {
            include_topaddress_tv_begin.setText("");
        }
//      include_topaddress_tv_end.setText(info.citytoname + info.citytoparentname);
        if (!SU.isEmpty(info.citytoname)) {
            include_topaddress_tv_end.setText(info.citytoname);
        } else {
            include_topaddress_tv_end.setText("");
        }
        long ll = info.lastupdate;
        String time = TimeUtils.getSureTime2("MM-dd HH:mm", ll);
        if (!SU.isEmpty(time)) {
            tv_publishtime2.setText(time);
        } else {
            tv_publishtime2.setText("");
        }
        String goodsname = info.goodsname;
        String wv = String.valueOf(info.wv);
        String unit = String.valueOf(info.unit);
        if (!SU.isEmpty(goodsname)) {
            goodsname = info.goodsname;
        } else {
            goodsname = "";
        }
        if (!SU.isEmpty(wv)) {
            wv = String.valueOf(info.wv);
        } else {
            wv = "";
        }
        if (!SU.isEmpty(unit)) {
            unit = info.unit;
        } else {
            unit = "";
        }
        tv_goodsname2.setText(goodsname + " " + wv + unit);
        String carlen = info.carlen;
        String cartype = info.cartype;
        if (!SU.isEmpty(carlen)) {
            carlen = info.carlen;
        } else {
            carlen = "";
        }
        if (!SU.isEmpty(cartype)) {
            cartype = info.cartype;
        } else {
            cartype = "";
        }
//        tv_goodsweight2.setText();
        tv_carlong2.setText(carlen + "  " + cartype);
//      tv_cartype.setText(info.cartype);
        String company = "";
        String room = "";
        if (info.company == null || "null".equals(info.company)) {
            company = "";
        } else {
            company = info.company;
        }
        if (info.room == null || "null".equals(info.room) || "".equals(info.room)) {
            room = "";
        } else {
            room = info.room;
        }
        if ("N".equals(info.flagauthority)) {
            tv_xwh2.setText("未交费(余额不足)");
            tv_tel2.setText("未充值(无法显示)");
        } else if ("Y".equals(info.flagauthority)) {
            if (!SU.isEmpty(company + "" + room)) {
                ll_xwh.setVisibility(View.VISIBLE);
                tv_xwh2.setText(company + "" + room);
            } else {
                ll_xwh.setVisibility(View.GONE);
                tv_xwh2.setText(company + "" + room);
            }
            tv_tel2.setText(info.contactmobile);
        }
        if (!SU.isEmpty(info.UserCompany)) {
            ll_gsmc.setVisibility(View.VISIBLE);
            tv_gsmc2.setText(info.UserCompany);
        } else {
            ll_gsmc.setVisibility(View.GONE);
            tv_gsmc2.setText("");
        }
        if (!SU.isEmpty(info.remark)) {
            tv_remark2.setText(info.remark);
        } else {
            tv_remark2.setText(getString(R.string.remark));
        }
        if (!SU.isEmpty(info.contact)) {
            tv_hzname2.setText(info.contact);
        } else {
            tv_hzname2.setText("");
        }
        if ("FIRS".equals(info.DeviceType))
            btn_baojia.setVisibility(View.VISIBLE);

        if (user != null) {
            Picasso.with(this).load(Uri.parse(user.imageurl)).into(raiv_userphoto);//头像
            tv_jyshu2.setText("" + user.jycount);//交易数
            tv_fahuo2.setText("" + user.goodscount);//发货数
        }


    }


    @Override
    public void setHandlerPostDataSuccess(int connectionId, Object data) throws JSONException {
        ResponseParams<LinkedHashMap> iParams = jiexiData(data);
        if (connectionId == Constants.detailsupply) {
            User user = null;
            supplyDetailEdite = JSON.parseObject(iParams.getData1().get("goods").toString(), SupplyDetailEdite.class);
            if (iParams.getData1().get("user") != null) {
                user = JSON.parseObject(iParams.getData1().get("user").toString(), User.class);
            }
            setView(supplyDetailEdite, user);
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

    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        // oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("鸿运天下-鸿宝全国版");
        // QQ好友的 QQ朋友圈的 点击链接 短信才可以
        oks.setTitleUrl("http://android.myapp.com/myapp/detail.htm?apkName=com.hongbao5656");
        // text是分享文本，所有平台都需要这个字段
        oks.setText(
                "出发地：" + vo.cityfromname + vo.cityfromparentname + "\n" +
                        "目的地：" + vo.citytoname + vo.citytoparentname + "\n" +
                        "发布时间：" + TimeUtils.getSureTime2("MM-dd HH:mm", vo.lastupdate) + "\n" +
                        "货物名称：" + vo.goodsname + "\n" +
                        "重量/体积: " + String.valueOf(vo.wv) + String.valueOf(vo.unit) + "\n" +
                        "车长：" + vo.carlen + "\n" +
                        "车型：" + vo.cartype + "\n" +
                        "席位号：" + vo.company + " " + vo.room + "\n" +
                        "联系电话：" + vo.contactmobile + "\n" +
                        "货主姓名：" + vo.contact + "\n" +
                        "备注：" + vo.remark);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        oks.setImagePath(App.LOGO_IMAGE);//确保SDcard下面存在此张图片
        // 微信好友、朋友圈和收藏的点击链接  QQ好友 QQ朋友圈 短信不行
        oks.setUrl("http://android.myapp.com/myapp/detail.htm?apkName=com.hongbao5656");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("车货匹配神器");
        // site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite("鸿运天下");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//        oks.setSiteUrl("http://android.myapp.com/myapp/detail.htm?apkName=com.hongbao5656");

// 启动分享GUI
        oks.show(this);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            SPU.setPreferenceValueBoolean(mContext, SPU.IS_SUPPLYDETAIL, true, SPU.STORE_SETTINGS);
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


}
