package com.hongbao5656.wxapi;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.alipay.sdk.app.PayTask;
import com.example.aaron.library.MLog;
import com.hongbao5656.R;
import com.hongbao5656.activity.RecordActivity;
import com.hongbao5656.adapter.TaoCanAdapter;
import com.hongbao5656.alipay.PayResult;
import com.hongbao5656.base.BaseActivity;
import com.hongbao5656.entity.VO;
import com.hongbao5656.okhttp.HttpDataHandlerListener;
import com.hongbao5656.okhttp.HttpException;
import com.hongbao5656.util.IMap;
import com.hongbao5656.util.ParamsService;
import com.hongbao5656.util.ResponseParams;
import com.hongbao5656.util.SU;
import com.hongbao5656.util.TU;
import com.hongbao5656.util.TimeUtils;
import com.hongbao5656.view.NoScrollListView;
import com.squareup.okhttp.Request;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import net.sourceforge.simcpux.Constants;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
/*支付(微信 支付宝)*/
public class MyMoneyActivity
        extends BaseActivity
        implements HttpDataHandlerListener, IWXAPIEventHandler {

    private RelativeLayout rl_3gonline;
    private List<VO> datas_3gonlin;
    private TextView text_online_time;
    private RelativeLayout rl_3gonline_record;
    private View view_zhifu;
    private NoScrollListView lv_ppw_taocan;
    private PopupWindow pw_zhifu;
    private TaoCanAdapter mTaoCanAdapter;
    private Button queding;
    private String taocan;
    private RadioButton rb_zhifubao;
    private RadioButton rb_weixin;
    private LinearLayout ll_zhifubao;
    private LinearLayout ll_weixin;
    private IWXAPI api;
    private LinearLayout ll_yinlian;
    private RadioButton rb_yinlian;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mymoney);
        initViews();
        initDatas();
    }
    private void initViews() {
        //左侧返回
        findViewById(R.id.leftBt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pw_zhifu != null && pw_zhifu.isShowing()) {
                    pw_zhifu.dismiss();
                } else {
                    finish();
                }
            }
        });
        //提现
        findViewById(R.id.publish_commit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TU.show(MyMoneyActivity.this, "开发中...");
            }
        });
        //3G在线充值
        rl_3gonline = (RelativeLayout) findViewById(R.id.rl_3gonline);
        //3G到期时间
        text_online_time = (TextView) findViewById(R.id.text_online_time);
        //3G充值记录
        rl_3gonline_record = (RelativeLayout) findViewById(R.id.rl_3gonline_record);

        //3G充值界面
        view_zhifu = LayoutInflater.from(this).inflate(R.layout.ppw_taocanzhifutype_layout, null);
//        //套餐gv
        lv_ppw_taocan = (NoScrollListView) view_zhifu.findViewById(R.id.lv_ppw_carlong);

        //ll支付宝
        ll_zhifubao = (LinearLayout)view_zhifu.findViewById(R.id.ll_zhifubao);
        ll_weixin = (LinearLayout)view_zhifu.findViewById(R.id.ll_weixin);
        ll_yinlian = (LinearLayout)view_zhifu.findViewById(R.id.ll_yinlian);

        //rb支付宝
        rb_zhifubao= (RadioButton)view_zhifu.findViewById(R.id.rb_zhifubao);
        //rb微信
        rb_weixin= (RadioButton)view_zhifu.findViewById(R.id.rb_weixin);
        rb_yinlian = (RadioButton)view_zhifu.findViewById(R.id.rb_yinlian);

        rb_zhifubao.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    rb_weixin.setChecked(false);
                }
            }
        });
        rb_weixin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    rb_zhifubao.setChecked(false);
                }
            }
        });
        rb_yinlian.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    rb_yinlian.setChecked(false);
                }
            }
        });

        ll_zhifubao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_zhifubao.setChecked(true);
                rb_weixin.setChecked(false);
//                rb_yi
            }
        });

        ll_weixin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_zhifubao.setChecked(false);
                rb_weixin.setChecked(true);
            }
        });

//        //支付gv
//        gv_ppw_zhifu = (GridView) view_zhifu.findViewById(R.id.gv_ppw_cartype);
        //支付界面的确定
        queding = (Button) view_zhifu.findViewById(R.id.queding);
        queding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taocan = mTaoCanAdapter.getSelectItem();
                if (pw_zhifu != null && pw_zhifu.isShowing()) {
                    if (SU.isEmpty(taocan)) {
                        TU.show(MyMoneyActivity.this, "请选择套餐");
                        return;
                    }else {
                        pw_zhifu.dismiss();
                    }
                }
                if(rb_zhifubao.isChecked()){
                    alipayrequest(taocan.substring(0,8));
                }else if(rb_weixin.isChecked()){
                    wxpayrequest(taocan.substring(0,8));
                }
                if (pw_zhifu != null && pw_zhifu.isShowing()) {
                    pw_zhifu.dismiss();
                }
            }
        });

        pw_zhifu = new PopupWindow(view_zhifu, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        rl_3gonline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPostRequest(
                        com.hongbao5656.util.Constants.online_3g,
                        new ParamsService().request(com.hongbao5656.util.Constants.online_3g),
                        MyMoneyActivity.this,
                        false);
            }
        });
        rl_3gonline_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyMoneyActivity.this, RecordActivity.class));
            }
        });
    }

    private void initDatas() {
        sendPostRequest(
                com.hongbao5656.util.Constants.online_3g_trading_endtime,
                new ParamsService().request(com.hongbao5656.util.Constants.online_3g_trading_endtime),
                this,
                false);
    }

    @Override
    public void setHandlerPostDataSuccess(int connectionId, Object data) throws JSONException {
        ResponseParams<LinkedHashMap> iParams = jiexiData(data);
        if (connectionId == com.hongbao5656.util.Constants.wxpay) {//微信支付
            PayReq req = new PayReq();
            //req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
            req.appId = iParams.getData1().get("appid").toString();//appId
            req.partnerId = iParams.getData1().get("partnerid").toString();//商户号
            req.prepayId = iParams.getData1().get("prepayid").toString();
            req.nonceStr = iParams.getData1().get("noncestr").toString();
            req.timeStamp = iParams.getData1().get("timestamp").toString();
            req.packageValue = iParams.getData1().get("package").toString();
            req.sign = iParams.getData1().get("sign").toString();//签名
//				req.extData = "app data"; // optional
//				Toast.makeText(PayActivity.this, "正常调起支付", Toast.LENGTH_SHORT).show();
            // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
            api.registerApp(Constants.APP_ID);
            api.sendReq(req);
        } else if (connectionId == com.hongbao5656.util.Constants.alipay) {//支付宝支付
            final String sign = iParams.getData1().get("paramsstr").toString();
            Runnable payRunnable = new Runnable() {
                @Override
                public void run() {
                    // 构造PayTask 对象
                    PayTask alipay = new PayTask(MyMoneyActivity.this);
                    // 调用支付接口，获取支付结果
                    String result = alipay.pay(sign, true);
                    Message msg = new Message();
                    msg.what = SDK_PAY_FLAG;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                }
            };
            // 必须异步调用
            Thread payThread = new Thread(payRunnable);
            payThread.start();
        } else if (connectionId == com.hongbao5656.util.Constants.online_3g) {
            datas_3gonlin = IMap.getData2FromResponse(iParams, VO.class);
            if (datas_3gonlin != null) {
                int size = datas_3gonlin.size();
                if (size > 0) {
                    if (pw_zhifu != null && pw_zhifu.isShowing()) {
                        pw_zhifu.dismiss();
                        return;
                    }
                    ArrayList<String> list = new ArrayList<>();
                    for (int i = 0; i < size; i++) {
                        VO vo = datas_3gonlin.get(i);
                        String price = String.valueOf(vo.price);
                        if(price.endsWith(".0")){
                            price = price.substring(0,price.length()-2);
                        }
                        list.add(vo.id+vo.description + price + "元");
                    }
//                    list.add("V2016006一年VIP套餐380元");
//                    list.add("V2016007二年VIP套餐580元");
//                    list.add("V2016008二年VIP套餐580元");
//                    list.add("V2016002二年VIP套餐580元");
                    if (mTaoCanAdapter == null) {
                        mTaoCanAdapter = new TaoCanAdapter(MyMoneyActivity.this, list);
                        lv_ppw_taocan.setAdapter(mTaoCanAdapter);
                    } else {
                        mTaoCanAdapter.clearListView();
                        mTaoCanAdapter.upateList(list);
                    }
                    pw_zhifu.setAnimationStyle(R.style.PopupWindowAnimation);
                    pw_zhifu.setOutsideTouchable(false);
                    pw_zhifu.setFocusable(false);
                    pw_zhifu.setBackgroundDrawable(getResources().getDrawable(R.color.base_top_bg_color));
                    pw_zhifu.showAsDropDown(findViewById(R.id.default_title_bg), 0, 0);
                }
            }
        } else if (connectionId == com.hongbao5656.util.Constants.online_3g_trading_endtime) {
            datas_3gonlin = IMap.getData2FromResponse(iParams, VO.class);
            if (datas_3gonlin != null) {
                int size = datas_3gonlin.size();
                if (size > 0) {
                    String time = TimeUtils.getSureTime2("yyyy-MM-dd HH:mm:ss", datas_3gonlin.get(0).useexpiredate);
                    if (!SU.isEmpty(time)) {
                        text_online_time.setText(time + " 到期");
                    }
                } else {
                    text_online_time.setText("");
                }
            }
        }
    }

    @Override
    public void setHandlerPostDataError(int connectedId, Request request, HttpException httpException, String params, String content) throws JSONException {

    }

    @Override
    public void setHandlerGetDataSuccess(int connectionId, Object data) throws JSONException {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }
    /**
     * 微信支付回掉
     * 0	成功	展示成功页面
     * -1	错误	可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
     * -2	用户取消	无需处理。发生场景：用户不支付了，点击取消，返回APP。
     * @param resp
     */
    @Override
    public void onResp(BaseResp resp) {
        MLog.i("onPayFinish, errCode = " + resp.errCode);
//		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setTitle(R.string.app_tip);
//			builder.setMessage(getString(R.string.pay_result_callback_msg, String.valueOf(resp.errCode)));
//			builder.show();
//		}
        try {
            if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
                int code = resp.errCode;
                if (code == 0) {
                    Toast.makeText(this, "微信支付成功", Toast.LENGTH_SHORT).show();
                } else if (code == -2) {
//                    Toast.makeText(this, "微信支付取消", Toast.LENGTH_SHORT).show();
//					finish();
                } else {
                    Toast.makeText(this, "微信支付失败", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            finish();
        }

    }

    //////////////////////////////////////////////////////////////////////////////
    // 商户PID
    public static final String PARTNER = "2088221482098297";
    // 商户收款账号
    public static final String SELLER = "492658210@qq.com";
//     商户私钥，pkcs8格式
	public static final String RSA_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAK+PwGbxpxNm4j5yMUquvTzoNPfQYldDiPw+M54/kljBjqEo04fcjM6gUSdBmBzLWQ34UoIid5chXRLTh3MVLBNwYBq6XvCAdRVKHuh8eI1k20FHnDplI+ZA2Gkpx8vR+Y8E9rmfHs8MplEIGtnh84HGJDfj8aMLIhvO695ia7GJAgMBAAECgYBhTtdA9+44Nc1u+VJS57miGb7KIKlxXtB9p2hq7SeLpHUO5Jcidd9h48A3dwb95sQMlzf8Q2hvzyFMfwxTgEe17wkMMsAIolMNkxvoIS95uUHOszzi0auj/ugCsbCKL+ikht/WKwVLk8AiqTYUGR3HuqkbUt45Av2P6v1N84TVlQJBAOKmK2UgmMBQ1nQOur+yeBILDoTrtOcvPxY9edoECA6bOycW7Pub/HxCHkfgKqHP2fQqGRZBJST3KEfNOico6LMCQQDGS++hYgkctmk7lDDGMvQagAa/pd9O7n0hoG14hMg8Muerhj8j5+vrqlpj/Itw3dDnaqlp5KTgkzgx1FVO04LTAkEA2tOK9FF7ibrCwzzxa2H5cZpBQ257s/W22Fc0jZz01n1jqEMHPYqGRMZljAnPv3j5uzbmD8mH/l+vAVwROHvT5wJAIL9ToJw3EwXQ1SUA9QpfI6028BltinR6N75ttbR/+OkTN1FhRgW8AVxNtytyv73yDPWharp2vBuz8Q620+C8PwJBANVHEHady9Z5ACKyhsKU8RyD6hyRNc/4G/3Sq7mThFsRA/QruLQwMWhPjjIs0hgdoB4dyzJ0NSV1L6BCOZ9zGuo=";//私钥
    // 支付宝公钥
//	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";//公钥
    private static final int SDK_PAY_FLAG = 1;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(MyMoneyActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        initDatas();
//                        finish();
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(MyMoneyActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(MyMoneyActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    public void alipayrequest(String id) {//支付宝请求本地服务器
        sendPostRequest(
                com.hongbao5656.util.Constants.alipay,
                new ParamsService().alipay(id), MyMoneyActivity.this, false);
    }

    private void wxpayrequest(String id) {//微信请求本地服务器
        api = WXAPIFactory.createWXAPI(this, "wxc10276fc4a4853ff");
        api.handleIntent(getIntent(), this);
        if (api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT) {
            sendPostRequest(
                    com.hongbao5656.util.Constants.wxpay,
                    new ParamsService().wxpay(id), MyMoneyActivity.this, false);
        } else {
            TU.show(this, "当前微信版本不支持支付，请升级后再试！");
        }
    }


    @Override
    public void setHandlerGetDataError(int connectionId, Object data) throws JSONException {

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (pw_zhifu != null && pw_zhifu.isShowing()) {
                pw_zhifu.dismiss();
            } else {
                finish();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

}
