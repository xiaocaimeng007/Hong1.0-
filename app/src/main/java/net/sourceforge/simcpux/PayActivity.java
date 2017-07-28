package net.sourceforge.simcpux;

import android.os.Bundle;
import android.widget.Toast;

import com.example.aaron.library.MLog;
import com.hongbao5656.base.App;
import com.hongbao5656.base.BaseActivity;
import com.hongbao5656.okhttp.HttpDataHandlerListener;
import com.hongbao5656.okhttp.HttpException;
import com.hongbao5656.util.NU;
import com.hongbao5656.util.ParamsService;
import com.hongbao5656.util.ResponseParams;
import com.hongbao5656.util.TU;
import com.hongbao5656.util.TimeUtils;
import com.squareup.okhttp.Request;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONException;

import java.util.LinkedHashMap;

public class PayActivity extends BaseActivity implements HttpDataHandlerListener {
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//		setContentView(R.layout.pay);
        api = WXAPIFactory.createWXAPI(this, "wxc10276fc4a4853ff");
//		Button appayBtn = (Button) findViewById(R.id.appay_btn);

        if (api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT) {
            paytest();
        } else {
            TU.show(this, "当前微信版本不支持支付，请升级后再试！");
        }
//		appayBtn.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				paytest();
//			}
//		});
//		Button checkPayBtn = (Button) findViewById(R.id.check_pay_btn);
//		checkPayBtn.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
//				Toast.makeText(PayActivity.this, String.valueOf(isPaySupported), Toast.LENGTH_SHORT).show();
//			}
//		});
    }

    private void paytest() {
        String url = "http://www.56hb.net/api/PayNotify";
//		Button payBtn = (Button) findViewById(R.id.appay_btn);
//		payBtn.setEnabled(false);
        Toast.makeText(PayActivity.this, "获取订单中...", Toast.LENGTH_SHORT).show();
        try {
//					byte[] buf = Util.httpGet(url);
//					if (buf != null && buf.length > 0) {
//						String content = new String(buf);
//						LU.i("get server pay params:", content);
//						JSONObject json = new JSONObject(content);
//						if(null != json && !json.has("retcode") ){
//							PayReq req = new PayReq();
//							//req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
//							req.appId			= json.getString("appid");//appId
//							req.partnerId		= json.getString("partnerid");//商户号
//							req.prepayId		= json.getString("prepayid");
//							req.nonceStr		= json.getString("noncestr");
//							req.timeStamp		= json.getString("timestamp");
//							req.packageValue	= json.getString("package");
//							req.sign			= json.getString("sign");//签名
//							req.extData			= "app data"; // optional
//							Toast.makeText(PayActivity.this, "正常调起支付", Toast.LENGTH_SHORT).show();
//							// 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
//							api.sendReq(req);
//						}else{
//							LU.i("PAY_GET", "返回错误" + json.getString("retmsg"));
//							Toast.makeText(PayActivity.this, "返回错误"+json.getString("retmsg"), Toast.LENGTH_SHORT).show();
//						}
//					}else{
//						LU.i("PAY_GET", "服务器请求错误");
//						Toast.makeText(PayActivity.this, "服务器请求错误", Toast.LENGTH_SHORT).show();
//					}
            requestServer();

        } catch (Exception e) {
            MLog.i("PAY_GET", "异常：" + e.getMessage());
            Toast.makeText(PayActivity.this, "异常：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
//		payBtn.setEnabled(true);
    }

    String body = "货源信息费";
    int total_fee = 1;

    private void requestServer() {
//		sendPostRequest(
//				com.hongbao5656.util.Constants.wxpay,
//				new ParamsService().wxpay(
//						body,total_fee, NU.getIpAddress(),
//						TimeUtils.getSureTime("yyyyMMddHHmmss",System.currentTimeMillis()),
//						TimeUtils.getSureTime("yyyyMMddHHmmss",System.currentTimeMillis()+1000*60*6),
//						"http://www.56hb.net/api/PayNotify"
//						),
//				this,
//				false);
    }

    public void setHandlerData(int connectionId, ResponseParams<LinkedHashMap> iParams, int errcode)
            throws JSONException {

    }

    @Override
    public void setHandlerPostDataSuccess(int connectionId, Object data) throws JSONException {
        ResponseParams<LinkedHashMap> iParams = jiexiData(data);
        if (connectionId == com.hongbao5656.util.Constants.wxpay) {
//                TU.show(mContext, "删除成功");
//                initDatas(1);
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
//				TU.show(this,bb+"");
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
