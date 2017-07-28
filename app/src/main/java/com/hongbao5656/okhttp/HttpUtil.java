package com.hongbao5656.okhttp;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.example.aaron.library.MLog;
import com.hongbao5656.R;
import com.hongbao5656.base.App;
import com.hongbao5656.util.TU;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;

import okio.Buffer;

public class HttpUtil {
    private static HttpUtil mHttpUtil;
    private static OkHttpClient mOkHttpClient;
    private ResultCallBackHandler mResultCallBackHandler;

    public static HttpUtil getInstance() {
        if (mHttpUtil == null) {
            synchronized (HttpUtil.class) {
                mHttpUtil = new HttpUtil();
            }
        }
        return mHttpUtil;
    }

    private HttpUtil() {
        String CER_12306 = "-----BEGIN CERTIFICATE-----\n" +
                "MIICmjCCAgOgAwIBAgIIbyZr5/jKH6QwDQYJKoZIhvcNAQEFBQAwRzELMAkGA1UEBhMCQ04xKTAn\n" +
                "BgNVBAoTIFNpbm9yYWlsIENlcnRpZmljYXRpb24gQXV0aG9yaXR5MQ0wCwYDVQQDEwRTUkNBMB4X\n" +
                "DTA5MDUyNTA2NTYwMFoXDTI5MDUyMDA2NTYwMFowRzELMAkGA1UEBhMCQ04xKTAnBgNVBAoTIFNp\n" +
                "bm9yYWlsIENlcnRpZmljYXRpb24gQXV0aG9yaXR5MQ0wCwYDVQQDEwRTUkNBMIGfMA0GCSqGSIb3\n" +
                "DQEBAQUAA4GNADCBiQKBgQDMpbNeb34p0GvLkZ6t72/OOba4mX2K/eZRWFfnuk8e5jKDH+9BgCb2\n" +
                "9bSotqPqTbxXWPxIOz8EjyUO3bfR5pQ8ovNTOlks2rS5BdMhoi4sUjCKi5ELiqtyww/XgY5iFqv6\n" +
                "D4Pw9QvOUcdRVSbPWo1DwMmH75It6pk/rARIFHEjWwIDAQABo4GOMIGLMB8GA1UdIwQYMBaAFHle\n" +
                "tne34lKDQ+3HUYhMY4UsAENYMAwGA1UdEwQFMAMBAf8wLgYDVR0fBCcwJTAjoCGgH4YdaHR0cDov\n" +
                "LzE5Mi4xNjguOS4xNDkvY3JsMS5jcmwwCwYDVR0PBAQDAgH+MB0GA1UdDgQWBBR5XrZ3t+JSg0Pt\n" +
                "x1GITGOFLABDWDANBgkqhkiG9w0BAQUFAAOBgQDGrAm2U/of1LbOnG2bnnQtgcVaBXiVJF8LKPaV\n" +
                "23XQ96HU8xfgSZMJS6U00WHAI7zp0q208RSUft9wDq9ee///VOhzR6Tebg9QfyPSohkBrhXQenvQ\n" +
                "og555S+C3eJAAVeNCTeMS3N/M5hzBRJAoffn3qoYdAO1Q8bTguOi+2849A==\n" +
                "-----END CERTIFICATE-----";



        mResultCallBackHandler = new ResultCallBackHandler(Looper.getMainLooper());
        mOkHttpClient = new OkHttpClient();
        mOkHttpClient.setConnectTimeout(15, TimeUnit.SECONDS);
        mOkHttpClient.setReadTimeout(15, TimeUnit.SECONDS);
        mOkHttpClient.setWriteTimeout(15, TimeUnit.SECONDS);
        //设置https
        InputStream[] iss = new InputStream[]{App.getInstance().getResources().openRawResource(R.raw.api56hbnet)};
//        InputStream[] iss12306 = new InputStream[]{new Buffer().writeUtf8(CER_12306).inputStream()};
//        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(iss, null, null);
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(iss, null, null);
        mOkHttpClient.setSslSocketFactory(sslParams.sSLSocketFactory);
        mOkHttpClient.setHostnameVerifier(new HostnameVerifier(){
            @Override
            public boolean verify(String hostname, SSLSession session){
//                session.getPeerCertificateChain()[0].getSubjectDN().toString()
                try {
                    String ss = session.getPeerCertificateChain()[0].getSubjectDN().toString();
//                    TU.show(App.getInstance(),ss+"  "+hostname);
                    if("CN=api.56hb.net, C=CN".equals(ss)&&"api.56hb.net".equals(hostname)){//CN=api.56hb.net, C=CN
                        return true;//验证域名
                    }else{
                        return false;//验证域名
                    }
                } catch (SSLPeerUnverifiedException e) {
                    e.printStackTrace();
//                    TU.show(App.getInstance(),"e");
                    return false;
                }
//                return true;
            }

        });
    }

    /**
     * post请求
     */
    public <T> void sendPostRequest(final int connectionId,
                                    final String url,
                                    final String params,
                                    final ResultCallBack resultcallback) {
        MLog.i("【" + connectionId + "】post请求参数：" + params + "url：" + url);
        HttpRequest request = new HttpRequestBuilder()
                .url(url)
                .addHeader("cookie", "xastdm")
                .method(HttpRequestBuilder.HttpMethod.POST)
                .build(params);

        if (resultcallback == null) {
            throw new NullPointerException("callback must not be null");
        }

        Call c = mOkHttpClient.newCall(request.getRequest());
        c.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                MLog.i(
                        "【" + connectionId + "】post请求失败！："
                                + " 参数:" + params
                                + " url:" + url
                                + " e.getMessage():" + e.getMessage()
                                + " e.getCause():" + e.getCause()
                                + " e.toString():" + e.toString()
                );
                ObjectStruct ost = new ObjectStruct(ResultCallBackHandler.SEND_FAIL);
                ost.ex = new HttpException(e);
                ost.obj = request;
                ost.callBack = resultcallback;
                ost.params = params;
                ost.content = "post请求失败";
                ost.connectionId = connectionId;
                mResultCallBackHandler.sendMessage(ost.getMessage());
                return;
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.code() == 200) {//响应成功
                    String body = response.body().string();//响应参数转为字符串
                    MLog.i(
                            "【" + connectionId + "】post响应成功！："
                                    + " 参数:" + params
                                    + " url:" + url
                                    + " 响应:" + body
                    );
                    ObjectStruct ost = new ObjectStruct(ResultCallBackHandler.SEND_SUCCESS);
                    ost.callBack = resultcallback;
                    ost.content = body;
                    ost.params = params;
                    ost.connectionId = connectionId;
                    mResultCallBackHandler.sendMessage(ost.getMessage());
                } else {//响应失败
                    MLog.i(
                            "【" + connectionId + "】post响应失败！："
                                    + " 参数:" + params
                                    + " url:" + url
                                    + " 响应码:" + response.code()
                    );
                    ObjectStruct ost = new ObjectStruct(ResultCallBackHandler.SEND_FAIL);
                    ost.ex = new HttpException(response.code());
                    ost.callBack = resultcallback;
                    ost.content = response.body().string();
                    ost.params = params;
                    ost.connectionId = connectionId;
                    mResultCallBackHandler.sendMessage(ost.getMessage());
                }
            }
        });

    }

    /**
     * get请求
     */
    public <T> void sendGetRequest(
                    final int connectionId,
                    HttpRequest request,
                    final ResultCallBack resultcallback) {
        final String url = request.getRequest().url().toString();
        MLog.i("【" + connectionId + "】get请求：" + "url：" + url);

        if (resultcallback == null) {
            throw new NullPointerException("callback must not be null");
        }
        Call c = mOkHttpClient.newCall(request.getRequest());
        c.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                MLog.i(
                        "【" + connectionId + "】get请求失败！："
                                + " url:" + url
                                + " e.getMessage():" + e.getMessage()
                                + " e.getCause():" + e.getCause()
                                + " e.toString():" + e.toString()
                );
                ObjectStruct ost = new ObjectStruct(ResultCallBackHandler.SEND_FAIL);
                ost.ex = new HttpException(e);
                ost.obj = request;
                ost.callBack = resultcallback;
                ost.params = "get请求无参";
                ost.content = "get请求失败";
                ost.connectionId = connectionId;
                mResultCallBackHandler.sendMessage(ost.getMessage());
                return;
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.code() == 200) {
                    String body = response.body().string();
                    MLog.i(
                            "【" + connectionId + "】get响应成功！："
                                    + " url:" + url
                                    + " 响应:" + body
                    );
                    ObjectStruct ost = new ObjectStruct(ResultCallBackHandler.SEND_SUCCESS);
                    ost.callBack = resultcallback;
                    ost.content = body;
                    ost.connectionId = connectionId;
                    mResultCallBackHandler.sendMessage(ost.getMessage());
                } else {
                    MLog.i(
                            "【" + connectionId + "】get响应失败！："
                                    + " url:" + url
                                    + " 响应码:" + response.code()
                    );
                    ObjectStruct ost = new ObjectStruct(ResultCallBackHandler.SEND_FAIL);
                    ost.ex = new HttpException(response.code());
                    ost.callBack = resultcallback;
                    ost.connectionId = connectionId;
                    mResultCallBackHandler.sendMessage(ost.getMessage());
                }
            }
        });
    }

    class ResultCallBackHandler extends Handler {
        public static final int SEND_SUCCESS = 1;
        public static final int SEND_FAIL = 2;
        public static final int ON_DOWNLOAD_PROGRASS = 3;
        public static final int ON_DOWNLOAD_SUCESS = 4;
        public static final int ON_DOWNLOAD_FAIL = 5;

        public ResultCallBackHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ObjectStruct ost = (ObjectStruct) msg.obj;
            int connectedId = ost.connectionId;
            if (msg.what == SEND_SUCCESS) {
                try {
                    ost.callBack.onResponse(connectedId, ost.content);
                } catch (Exception ex) {
                    MLog.i("【" + connectedId + "】:响应成功后解析错误：" + ex.getCause() + "\n" + ex.getMessage());
                    Request request = (Request) ost.obj;
                    HttpException he = new HttpException(ex);
                    he.setResponseCode(999);
                    ost.callBack.onError(connectedId, request, he, ost.params, ost.content);
                }
            } else if (msg.what == SEND_FAIL) {
                Request request = (Request) ost.obj;
                ost.callBack.onError(connectedId, request, ost.ex, ost.params, ost.content);
            }

            else if (msg.what == ON_DOWNLOAD_PROGRASS) {
                ost.downLoadCallBack.onDownLoading(ost.prograss);
            } else if (msg.what == ON_DOWNLOAD_SUCESS) {
                ost.downLoadCallBack.onDownLoadSuccess();
            } else if (msg.what == ON_DOWNLOAD_FAIL) {
                ost.downLoadCallBack.onDownLoadFail(ost.ex);
            }
        }
    }

    private class ObjectStruct {
        private ResultCallBack callBack;
        private DownLoadCallBack downLoadCallBack;
        private String content;
        private HttpException ex;
        private Object obj;
        private Message msg;
        private int prograss;
        private int state;
        private int connectionId;// 请求ID

        private String params;

        public ObjectStruct(int state) {
            this.msg = new Message();
            this.state = state;
        }

        public Message getMessage() {
            this.msg.what = state;
            this.msg.obj = this;
            return msg;
        }
    }
}
