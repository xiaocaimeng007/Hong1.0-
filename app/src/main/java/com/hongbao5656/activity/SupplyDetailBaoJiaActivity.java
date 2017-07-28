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
import com.hongbao5656.db.DBHelper;
import com.hongbao5656.entity.Region;
import com.hongbao5656.entity.SupplyDetailEdite;
import com.hongbao5656.entity.SupplyItemBaoJia;
import com.hongbao5656.entity.User;
import com.hongbao5656.okhttp.HttpDataHandlerListener;
import com.hongbao5656.okhttp.HttpException;
import com.hongbao5656.util.Constants;
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

public class SupplyDetailBaoJiaActivity extends BaseActivity {

        /*
          include_topaddress_tv_begin.setText(info.CityFromName);//起始地
        include_topaddress_tv_end.setText(info.CityToName);//目的地
        tv_publishtime2.setText( TimeUtils.getSureTime2("MM-dd HH:mm",  info.LastUpdate));//发布时间
        tv_goodsname2.setText(info.GoodsName + " " + info.WV +  info.Unit);//货源名称
        tv_carlong2.setText( info.CarLen + "  " + info.CarType);//车长车型
        tv_gsmc2.setText(info.UserCompany);//公司名称
        tv_tel2.setText(info.PublisherContact+" "+info.FuYouMobile);//联系人/联系电话
        tv_remark2.setText(SU.isEmpty(info.Remark)?getString(R.string.remark):info.Remark);//备注
        * */

    private TextView include_topaddress_tv_begin;
    private TextView include_topaddress_tv_end;
    private Context mContext;
    private RelativeLayout leftBt;
    private TextView tv_publishtime2;
    private TextView tv_goodsname2;
    private TextView tv_carlong2;
    private TextView tv_remark2;
    private Button btn_tell;
    private SupplyDetailEdite vo;
    //private String baojiaid = "";
    private TextView tv_right;
    private Button btn_zhifu;
    private Button btn_baojia;
    private TextView tv_ditu;
    private SupplyItemBaoJia datasource = null;

    private TextView tv_tel2;
    private TextView tv_gsmc2;
    private TextView tv_baojia;
    private TextView tv_otherbaojia;
    private TextView tv_baojiashijian;
    private TextView tv_baojiastatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailbaojia);
        this.mContext = SupplyDetailBaoJiaActivity.this;
        if (getIntent().hasExtra("bjid")) {
            String baojiaid = getIntent().getStringExtra("bjid");
            requestEditSupply(baojiaid);
        }
        initView();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void requestEditSupply(String baojiaid) {
        sendPostRequest(
                Constants.detailsupplyBaoJia,
                new ParamsService().requestKV("baojiaid", baojiaid, Constants.detailsupplyBaoJia),
                httpDataHandlerListener,
                false);
    }

    HttpDataHandlerListener httpDataHandlerListener = new HttpDataHandlerListener() {
        @Override
        public void setHandlerPostDataSuccess(int connectionId, Object data) throws JSONException {
            ResponseParams<LinkedHashMap> iParams = jiexiData(data);
            if (connectionId == Constants.detailsupplyBaoJia) {
                datasource = JSON.parseObject(JSON.toJSON(iParams.getData1()).toString(), SupplyItemBaoJia.class);

                setView(datasource);
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
                Uri uri = Uri.parse("tel:" + datasource.FuYouMobile);
                Intent it = new Intent(Intent.ACTION_DIAL, uri);
                mContext.startActivity(it);

            }
        });
        tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        btn_zhifu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(PayActivity.class);
            }
        });

        btn_baojia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (datasource == null) {
                    TU.showLong(SupplyDetailBaoJiaActivity.this, "数据还未准备好");
                    return;
                }

                //用Bundle携带数据
                Bundle bundle = new Bundle();
                //传递name参数为tinyphp
                bundle.putString("goodsid", datasource.GoodsID);
                bundle.putString("baojiaid", datasource.BJNO);
                openActivity(BaoJiaActivity.class, bundle);
            }
        });

        tv_ditu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (datasource == null) {
                    TU.showLong(SupplyDetailBaoJiaActivity.this, "数据还未准备好");
                    return;
                }
                Intent intent = new Intent();
                intent.setClass(mContext, MapDetailActivity.class);
                Region r = DBHelper.getInstance(mContext).queryLatLong(mContext, datasource.CityFrom);
                Region r2 = DBHelper.getInstance(mContext).queryLatLong(mContext, datasource.CityTo);
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

    }


    private void initView() {

        leftBt = (RelativeLayout) findViewById(R.id.leftBt);
        include_topaddress_tv_begin = (TextView) findViewById(R.id.include_topaddress_tv_begin);//起始地
        include_topaddress_tv_end = (TextView) findViewById(R.id.include_topaddress_tv_end);//目的地
        tv_publishtime2 = (TextView) findViewById(R.id.tv_publishtime2);//发布时间
        tv_goodsname2 = (TextView) findViewById(R.id.tv_goodsname2);//货物名称

        tv_carlong2 = (TextView) findViewById(R.id.tv_carlong2);//车长车型

        tv_gsmc2 = (TextView) findViewById(R.id.tv_gsmc2);//公司名称

        tv_tel2 = (TextView) findViewById(R.id.tv_tel2);
        tv_remark2 = (TextView) findViewById(R.id.tv_remark2);//备注


        btn_tell = (Button) findViewById(R.id.btn_tell);//电话咨询
        tv_right = (TextView) findViewById(R.id.tv_right);
        btn_zhifu = (Button) findViewById(R.id.btn_zhifu);//支付信息费

        tv_ditu = (TextView) findViewById(R.id.tv_ditu);//查看地图


        btn_baojia = (Button) findViewById(R.id.btn_BaoJia);


        tv_baojia = (TextView) findViewById(R.id.tvBaoJia);
        tv_otherbaojia = (TextView) findViewById(R.id.tvOtherPrice);
        tv_baojiashijian = (TextView) findViewById(R.id.tvBaoJiaShiJian);
        tv_baojiastatus = (TextView) findViewById(R.id.tvBaoJiaStatus);
    }

    private void setView(SupplyItemBaoJia info) {

        include_topaddress_tv_begin.setText(info.CityFromName);//起始地
        include_topaddress_tv_end.setText(info.CityToName);//目的地
        tv_publishtime2.setText(TimeUtils.getSureTime2("MM-dd HH:mm", info.LastUpdate));//发布时间
        tv_goodsname2.setText(info.GoodsName + " " + info.WV + info.Unit);//货源名称
        tv_carlong2.setText(info.CarLen + "  " + info.CarType);//车长车型
        tv_gsmc2.setText(info.UserCompany);//公司名称
        tv_tel2.setText(info.PublisherContact + " " + info.FuYouMobile);//联系人/联系电话
        tv_remark2.setText(SU.isEmpty(info.Remark) ? getString(R.string.remark) : info.Remark);//备注

        tv_baojiashijian.setText(TimeUtils.getSureTime2("MM-dd HH:mm", info.DocDate));//报价时间
        tv_baojia.setText(info.Price + "");//报价
        tv_otherbaojia.setText(info.otherPrice + "");//其他费用
        tv_baojiastatus.setText(info.BJStatusName);//报价状态
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
