package com.hongbao5656.alipay;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import com.example.aaron.library.MLog;
import com.alipay.sdk.app.PayTask;
import com.hongbao5656.R;
import com.hongbao5656.base.App;
import com.hongbao5656.okhttp.HttpDataHandlerListener;
import com.hongbao5656.okhttp.HttpException;
import com.hongbao5656.okhttp.HttpRequest;
import com.hongbao5656.okhttp.HttpRequestBuilder;
import com.hongbao5656.util.ResponseParams;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import org.json.JSONException;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Random;


/*支付宝支付*/
public class PayDemoActivity
        extends FragmentActivity
        implements HttpDataHandlerListener {
    // 商户PID
    public static final String PARTNER = "2088221482098297";
    // 商户收款账号
    public static final String SELLER = "492658210@qq.com";
    // 商户私钥，pkcs8格式
//	public static final String RSA_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAK+PwGbxpxNm4j5yMUquvTzoNPfQYldDiPw+M54/kljBjqEo04fcjM6gUSdBmBzLWQ34UoIid5chXRLTh3MVLBNwYBq6XvCAdRVKHuh8eI1k20FHnDplI+ZA2Gkpx8vR+Y8E9rmfHs8MplEIGtnh84HGJDfj8aMLIhvO695ia7GJAgMBAAECgYBhTtdA9+44Nc1u+VJS57miGb7KIKlxXtB9p2hq7SeLpHUO5Jcidd9h48A3dwb95sQMlzf8Q2hvzyFMfwxTgEe17wkMMsAIolMNkxvoIS95uUHOszzi0auj/ugCsbCKL+ikht/WKwVLk8AiqTYUGR3HuqkbUt45Av2P6v1N84TVlQJBAOKmK2UgmMBQ1nQOur+yeBILDoTrtOcvPxY9edoECA6bOycW7Pub/HxCHkfgKqHP2fQqGRZBJST3KEfNOico6LMCQQDGS++hYgkctmk7lDDGMvQagAa/pd9O7n0hoG14hMg8Muerhj8j5+vrqlpj/Itw3dDnaqlp5KTgkzgx1FVO04LTAkEA2tOK9FF7ibrCwzzxa2H5cZpBQ257s/W22Fc0jZz01n1jqEMHPYqGRMZljAnPv3j5uzbmD8mH/l+vAVwROHvT5wJAIL9ToJw3EwXQ1SUA9QpfI6028BltinR6N75ttbR/+OkTN1FhRgW8AVxNtytyv73yDPWharp2vBuz8Q620+C8PwJBANVHEHady9Z5ACKyhsKU8RyD6hyRNc/4G/3Sq7mThFsRA/QruLQwMWhPjjIs0hgdoB4dyzJ0NSV1L6BCOZ9zGuo=";//私钥
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
                        Toast.makeText(PayDemoActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(PayDemoActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(PayDemoActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
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
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
//    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_main);
        pay();
//        pay2();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void pay2() {

//        sendPostRequest(
//                com.hongbao5656.util.Constants.alipay,
//                new ParamsService().wxpay(
//                        ProductID
////						body,total_fee, NU.getIpAddress(),
////						TimeUtils.getSureTime("yyyyMMddHHmmss",System.currentTimeMillis()),
////						TimeUtils.getSureTime("yyyyMMddHHmmss",System.currentTimeMillis()+1000*60*6),
////						"http://www.56hb.net/api/PayNotify"
//                ),DemoActivity.this,false);

    }

    /**
     * call alipay sdk pay. 调用SDK支付
     */
    public void pay() {
//        if (TextUtils.isEmpty(PARTNER) || TextUtils.isEmpty(RSA_PRIVATE) || TextUtils.isEmpty(SELLER)) {
//            new AlertDialog.Builder(this).setTitle("警告").setMessage("需要配置PARTNER | RSA_PRIVATE| SELLER")
//                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialoginterface, int i) {
//                            //
//                            finish();
//                        }
//                    }).show();
//            return;
//        }
        final String orderInfo = getOrderInfo("货源信息费", "完成交易会入账货主方(含1元手续费)", "0.01");
        MLog.i("dm", orderInfo);
        new Thread() {
            public void run() {
                try {
                    RequestBody body = RequestBody.create(MediaType.parse("application/text; charset=utf-8"), URLEncoder.encode(orderInfo, "UTF-8"));
                    HttpRequest request = new HttpRequestBuilder().url(App.URL_WXPAY)
                            .addHeader("cookie", "df").method(HttpRequestBuilder.HttpMethod.POST).build("\"" + URLEncoder.encode(orderInfo, "UTF-8") + "\"");
//                    Request request = new Request.Builder()
//                            .url("http://www.56hb.net/api/alipaysign")
//                            .post(body)
//                            .build();
                    Call c = new OkHttpClient().newCall(request.getRequest());
                    c.enqueue(new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {

                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            if (response.code() == 200) {
                                String sign = response.body().string();
                                MLog.i("dm", sign);
                                /**
                                 * 完整的符合支付宝参数规范的订单信息
                                 */
                                final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();
                                Runnable payRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        // 构造PayTask 对象
                                        PayTask alipay = new PayTask(PayDemoActivity.this);
                                        // 调用支付接口，获取支付结果
                                        String result = alipay.pay(payInfo, true);

                                        Message msg = new Message();
                                        msg.what = SDK_PAY_FLAG;
                                        msg.obj = result;
                                        mHandler.sendMessage(msg);
                                    }
                                };
                                // 必须异步调用
                                Thread payThread = new Thread(payRunnable);
                                payThread.start();
//						System.out.println(response.body().string());
                                //	    return response.body().string();
                            }
                        }
                    });
//                    c.enqueue(new Callback() {
//                        @Override
//                        public void onResponse(Response response) throws IOException {
//                            if (response.code() == 200) {
//                                String sign = response.body().string();
//                                MLog.i("dm", sign);
//                                /**
//                                 * 完整的符合支付宝参数规范的订单信息
//                                 */
//                                final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();
//                                Runnable payRunnable = new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        // 构造PayTask 对象
//                                        PayTask alipay = new PayTask(PayDemoActivity.this);
//                                        // 调用支付接口，获取支付结果
//                                        String result = alipay.pay(payInfo, true);
//
//                                        Message msg = new Message();
//                                        msg.what = SDK_PAY_FLAG;
//                                        msg.obj = result;
//                                        mHandler.sendMessage(msg);
//                                    }
//                                };
//                                // 必须异步调用
//                                Thread payThread = new Thread(payRunnable);
//                                payThread.start();
////						System.out.println(response.body().string());
//                                //	    return response.body().string();
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Request arg0, IOException arg1) {
//
//                        }
//                    });
//					if(response.isSuccessful()) {
//						String sign = response.body().string();
//						MLog.i("dm",sign);
//						/**
//						 * 完整的符合支付宝参数规范的订单信息
//						 */
//						final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();
//
//						Runnable payRunnable = new Runnable() {
//
//							@Override
//							public void run() {
//								// 构造PayTask 对象
//								PayTask alipay = new PayTask(PayDemoActivity.this);
//								// 调用支付接口，获取支付结果
//								String result = alipay.pay(payInfo, true);
//
//								Message msg = new Message();
//								msg.what = SDK_PAY_FLAG;
//								msg.obj = result;
//								mHandler.sendMessage(msg);
//							}
//						};
//
//						// 必须异步调用
//						Thread payThread = new Thread(payRunnable);
//						payThread.start();
////						System.out.println(response.body().string());
//					//	    return response.body().string();
//					} else {
//						throw new IOException("Unexpected code " + response);
//					}
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            ;
        }.start();

        /**
         * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
         */
//		String sign = sign(orderInfo);
//		try {
//			/**
//			 * 仅需对sign 做URL编码
//			 */
//			sign = URLEncoder.encode(sign, "UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}


    }

    /**
     * get the sdk version. 获取SDK版本号
     */
    public void getSDKVersion() {
        PayTask payTask = new PayTask(this);
        String version = payTask.getVersion();
        Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
    }

    /**
     * 原生的H5（手机网页版支付切natvie支付） 【对应页面网页支付按钮】
     *
     * @param v
     */
    public void h5Pay(View v) {
        Intent intent = new Intent(this, H5PayDemoActivity.class);
        Bundle extras = new Bundle();
        /**
         * url是测试的网站，在app内部打开页面是基于webview打开的，demo中的webview是H5PayDemoActivity，
         * demo中拦截url进行支付的逻辑是在H5PayDemoActivity中shouldOverrideUrlLoading方法实现，
         * 商户可以根据自己的需求来实现
         */
        String url = "http://m.meituan.com";
        // url可以是一号店或者美团等第三方的购物wap站点，在该网站的支付过程中，支付宝sdk完成拦截支付
        extras.putString("url", url);
        intent.putExtras(extras);
        startActivity(intent);

    }

    /**
     * create the order info. 创建订单信息
     */
    private String getOrderInfo(String subject, String body, String price) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + "www.56hb.net/api/alipay" + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    /**
     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     */
    private String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content
     *            待签名订单信息
     */
//	private String sign(String content) {
//		return SignUtils.sign(content, RSA_PRIVATE);
//	}

    /**
     * get the sign type we use. 获取签名方式
     */
    private String getSignType() {
        return "sign_type=\"RSA\"";
    }


    @Override
    public void setHandlerPostDataSuccess(int connectionId, Object data) throws JSONException {

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

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client.connect();
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "PayDemo Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://com.hongbao5656.alipay/http/host/path")
//        );
//        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "PayDemo Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://com.hongbao5656.alipay/http/host/path")
//        );
//        AppIndex.AppIndexApi.end(client, viewAction);
//        client.disconnect();
    }
}
