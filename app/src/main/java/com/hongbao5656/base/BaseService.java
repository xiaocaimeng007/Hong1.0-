package com.hongbao5656.base;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.example.aaron.library.MLog;
import com.hongbao5656.R;
import com.hongbao5656.db.DBHelper;
import com.hongbao5656.okhttp.HttpDataHandlerListener;
import com.hongbao5656.okhttp.HttpException;
import com.hongbao5656.okhttp.HttpRequest;
import com.hongbao5656.okhttp.HttpUtil;
import com.hongbao5656.okhttp.ResultCallBack;
import com.hongbao5656.service.LocationServiceForGaoDe;
import com.hongbao5656.util.IMap;
import com.hongbao5656.util.MailManager;
import com.hongbao5656.util.NU;
import com.hongbao5656.util.ResponseParams;
import com.hongbao5656.util.SPU;
import com.hongbao5656.util.SU;
import com.hongbao5656.util.TU;
import com.squareup.okhttp.Request;

import java.util.LinkedHashMap;


public class BaseService extends Service implements ResultCallBack {
    private HttpDataHandlerListener mlistener;
    private DBHelper dbHelper;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        if (SU.isEmpty(App.account) || SU.isEmpty(App.token)) {
            App.token = SPU.getPreferenceValueString(this, SPU.EQ_tokenId, "", SPU.STORE_USERINFO);
            App.account = SPU.getPreferenceValueString(this, SPU.ACCOUNT, "", SPU.STORE_USERINFO);
        }
        super.onCreate();
    }

    /**
     * post请求
     */
    public void sendRequest(int connectionId, String params,
                            HttpDataHandlerListener listener, boolean isShow) {
        if (!NU.checkNetworkAvailable(this)) {
            TU.show(this, getString(R.string.no_net), Toast.LENGTH_LONG);
            return;
        }
//        if (isShow && !mDialog.isShowing()) {
//            mDialog.show();
//        }
        this.mlistener = listener;
        HttpUtil.getInstance().sendPostRequest(
                connectionId,
                App.BATE_URL,
                params,
                this);
    }

    /**
     * get请求
     */
    public void sendGetRequest(int connectionId, HttpRequest request,
                               HttpDataHandlerListener listener) {
        if (!NU.checkNetworkAvailable(this)) {
            TU.show(this, getString(R.string.no_net), Toast.LENGTH_LONG);
            return;
        }
        this.mlistener = listener;
        HttpUtil.getInstance().sendGetRequest(connectionId, request, this);
    }

    /**
     * 请求成功
     */
    @Override
    public void onResponse(int connectionId, Object data) {
        try {
            MLog.i("dm", data != null ? "响应参数：" + connectionId + ">" + data.toString() : "响应参数为空");

            int errcode = -1;
            ResponseParams<LinkedHashMap> iParams = null;
            if (null != data) {
                iParams = IMap.jieXiResponse(data.toString());
                errcode = iParams.getErrcode();
            }

            if (44444 == errcode || 50000 == errcode || 50001 == errcode) {//50000 50001 清坐标数据
                //停掉服务
                locationDate();
                return;
            }else{
                TU.show(this, iParams.getErrmsg() + "-" + errcode);
            }
            if (errcode == 0) {
                mlistener.setHandlerPostDataSuccess(connectionId, data);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void locationDate() {
        //停止坐标服务
        Intent intentservice = new Intent(this, LocationServiceForGaoDe.class);
        stopService(intentservice);

        //清空数据库已有坐标
        if (dbHelper == null) {
            dbHelper = DBHelper.getInstance(this);
            dbHelper.clearTableData(this);
            dbHelper = null;
        }
    }

    /**
     * 请求失败
     */
    @Override
    public void onError(int connectedId, Request request,
                        HttpException httpException, String params, String content) {
        //打印错误日志
        MLog.i(logToMe(connectedId, httpException, params, content));
        //发送错误日志
        sendEMailDM("error", logToMe(connectedId, httpException, params, content), "");
        //toast显示
        if (999 == httpException.getResponseCode()) {
            TU.showLong(this,
                    getString(R.string.data_failure)
                            + "\n[" + App.version + "-" + connectedId + "-" + App.account + "]");
        } else {
            TU.showLong(this,
                    getString(R.string.no_error)
                            + "\n[" + App.version + "-" + connectedId + "-" + App.account + "]");
        }

    }

    private String logToMe(int connectedId, HttpException httpException, String params, String content) {
        return "[" + App.version + "-" + connectedId + "-" + App.account + "]"
                + "<<<params==" + params + "/>>>\n"
                + "<<<content==" + content + "/>>>\n"
                + "<<<M==" + httpException.getMessage() + "/>>>\n"
                + "<<<C==" + httpException.getCause() + "/>>>\n"
                + "<<<RC==" + httpException.getResponseCode() + "/>>>\n"
                + "<<<et==" + httpException.toString();
    }

    //发邮件
    public void sendEMailDM(String tag, String content, String path) {
        MailManager.getInstance().sendMailWithFile(this, tag, content, path);
    }

}
