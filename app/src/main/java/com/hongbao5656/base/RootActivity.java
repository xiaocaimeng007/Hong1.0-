package com.hongbao5656.base;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.IntentCompat;
import android.widget.Toast;

import com.example.aaron.library.MLog;
import com.hongbao5656.R;
import com.hongbao5656.activity.LoginActivity;
import com.hongbao5656.util.Constants;
import com.hongbao5656.db.DBHelper;
import com.hongbao5656.jpush.JPushUtil;
import com.hongbao5656.okhttp.HttpDataHandlerListener;
import com.hongbao5656.okhttp.HttpException;
import com.hongbao5656.okhttp.HttpRequest;
import com.hongbao5656.okhttp.HttpUtil;
import com.hongbao5656.okhttp.ResultCallBack;
import com.hongbao5656.service.LocationServiceForGaoDe;
import com.hongbao5656.util.IMap;
import com.hongbao5656.util.LoadingDialog;
import com.hongbao5656.util.MailManager;
import com.hongbao5656.util.NU;
import com.hongbao5656.util.ResponseParams;
import com.hongbao5656.util.SPU;
import com.hongbao5656.util.TU;
import com.hongbao5656.util.UpdateManager;
import com.squareup.okhttp.Request;

import org.json.JSONException;

import java.util.LinkedHashMap;

public class RootActivity
        extends FragmentActivity
        implements ResultCallBack {
    private HttpDataHandlerListener mlistener;
    private LoadingDialog mDialog;
    private DBHelper dbHelper;
    private Dialog toastDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSPU();
        mDialog = new LoadingDialog(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        initSPU();
//        MLog.i("RootAct onRestoreInstanceState", "initSPU...");
    }

    private void initSPU() {
        App.token = SPU.getPreferenceValueString(this, SPU.EQ_tokenId, "", SPU.STORE_USERINFO);
        App.account = SPU.getPreferenceValueString(this, SPU.ACCOUNT, "", SPU.STORE_USERINFO);
        App.pwd = SPU.getPreferenceValueString(this, SPU.PWD, "", SPU.STORE_USERINFO);
        App.pwd2 = SPU.getPreferenceValueString(this, SPU.PWD2, "", SPU.STORE_USERINFO);
        App.phone = SPU.getPreferenceValueString(this, SPU.EQ_Tel, "", SPU.STORE_USERINFO);
        App.UER_TYPE = SPU.getPreferenceValueString(this, SPU.UER_TYPE, "", SPU.STORE_USERINFO);
        App.UER_CHECK_STATE = SPU.getPreferenceValueString(this, SPU.UER_CHECK_STATE, "", SPU.STORE_USERINFO);
        App.username = SPU.getPreferenceValueString(this, SPU.USERNAMENEW, "", SPU.STORE_USERINFO);
    }

    /**
     * 通过类名启动Activity
     *
     * @param pClass
     */
    public void openActivity(Class<?> pClass) {
        openActivity(pClass, null);
    }

    /**
     * 通过类名启动Activity
     *
     * @param pClass
     */
    public void openActivityAndFinish(Class<?> pClass) {
        openActivity(pClass, null);
        finish();
    }

    /**
     * 携带数据
     *
     * @param pClass
     */
    public void openActivity(Class<?> pClass, Bundle pBundle) {
        Intent intent = new Intent(this, pClass);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivity(intent);
    }

    public void openActivityForResult(Class<?> pClass, Bundle pBundle, int requestCode) {
        Intent intent = new Intent(this, pClass);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivityForResult(intent, requestCode);
    }

    /**
     * 通过Action启动Activity
     *
     * @param pAction
     */
    protected void openActivity(String pAction) {
        openActivity(pAction, null);
    }

    /**
     * 通过Action启动Activity，并且含有Bundle数据
     *
     * @param pAction
     * @param pBundle
     */
    protected void openActivity(String pAction, Bundle pBundle) {
        Intent intent = new Intent(pAction);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivity(intent);
    }


    /**
     * post请求
     */
    public void sendPostRequest(
            int connectionId, String params,
            HttpDataHandlerListener listener, boolean isShow) {
        if (!NU.checkNetworkAvailable(this)) {
            TU.show(this, getString(R.string.no_net), Toast.LENGTH_LONG);
            return;
        }
        if (isShow && !mDialog.isShowing()) {
            mDialog.show();
        }
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
     * 响应数据解析
     *
     * @param data
     * @return
     */
    public ResponseParams<LinkedHashMap> jiexiData(Object data) {
        ResponseParams<LinkedHashMap> iParams = null;
        if (null != data) {
            iParams = IMap.jieXiResponse(data.toString());
        }
        return iParams;
    }

    /**
     * 响应成功
     */
    @Override
    public void onResponse(int connectionId, Object data) {
        try {
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }

            if (Constants.download == connectionId) {
                mlistener.setHandlerGetDataSuccess(connectionId, data);
                return;
            }

            if (connectionId == Constants.checksupply_seek
                    || connectionId == Constants.requestAllLines) {//找货
                mlistener.setHandlerPostDataSuccess(connectionId, data);
            } else if (connectionId == Constants.oilprvice) {
                mlistener.setHandlerGetDataSuccess(connectionId, data);
            } else {
                int errcode = -1;
                ResponseParams<LinkedHashMap> iParams = null;
                if (null != data) {
                    iParams = IMap.jieXiResponse(data.toString());
                    errcode = iParams.getErrcode();
                }

                if (connectionId == Constants.btn_login && errcode == 90000) {
                    TU.show(this, "" + iParams.getErrmsg());
                    openActivity(LoginActivity.class);
                    finish();
                    return;
                }
                switch (errcode) {
                    case 50000://token过期   只清坐标
                        TU.show(this, "" + iParams.getErrmsg());
                        clearAndStopLocationData();//只清坐标
                        openActivity(LoginActivity.class);
                        finish();
                        break;
                    case 50001://第三方登录  清坐标 清个人信息 清推送
                        TU.show(this, "" + iParams.getErrmsg());
                        eixtAccount();
                        openActivity(LoginActivity.class);
                        finish();
                        break;
                    case 44444://接口异常 升级app
                        TU.show(this, iParams.getErrmsg() + "-" + errcode);
                        UpdateApp();
                        if (10013 == connectionId || 10014 == connectionId) {
                            clearAndStopLocationData();
                        }
                        break;
                    case 0://成功
                        mlistener.setHandlerPostDataSuccess(connectionId, data);
                        break;
                    default:
                        TU.show(this, iParams.getErrmsg() + "-" + errcode);
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * //请求失败或响应成功后解析失败
     */
    @Override
    public void onError(int connectedId, Request request,
                        HttpException httpException, String params, String content) {
        try {
            //请求失败消失对话框
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
            //打印错误日志
            MLog.i(logToMe(connectedId, httpException, params, content));
            //发送错误日志
            sendEMailDM("error", logToMe(connectedId, httpException, params, content), "");
            //toast显示
            if (999 == httpException.getResponseCode()) {
                TU.showLong(RootActivity.this,
                        getString(R.string.data_failure)
                                + "\n[" + App.version + "-" + connectedId + "-" + App.account + "]");
            } else {
                TU.showLong(RootActivity.this,
                        getString(R.string.no_error)
                                + "\n[" + App.version + "-" + connectedId + "-" + App.account + "]");
            }

            if (10003 == connectedId || 0 == connectedId || 10311 == connectedId) {
//			openActivity(LoginActivity.class);
//			this.finish();

//			Intent intent = new Intent(this, LoginActivity.class);
//			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			startActivity(intent);
//			finish();
                Intent intent = new Intent(this, LoginActivity.class);
                ComponentName cn = intent.getComponent();
                Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
                startActivity(mainIntent);
            }
            mlistener.setHandlerPostDataError(connectedId, request, httpException, params, content);
        } catch (Exception e) {
            e.printStackTrace();
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

    public void eixtAccount() {
        //销毁当前账户信息
        SPU.clearPreference(this, SPU.STORE_USERINFO);
        //清坐标
        clearAndStopLocationData();
        //清推送
        setMyTags("clear,clear");
    }

    public void clearAndStopLocationData() {
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

    //发邮件
    public void sendEMailDM(String tag, String content, String path) {
        MailManager.getInstance().sendMailWithFile(this, tag, content, path);
    }

    //隐藏对话框
    public void dismiss(LoadingDialog dialog, Object data) {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        if (data == null) {
            return;
        }
    }

    //升级
    public void UpdateApp() {
        UpdateManager.getInstance().checkUpdateInfo(this);
    }

    //推送
    public void setMyTags(String tag) {
        JPushUtil.getInstance().setMyTags(this, tag);
    }

    //温馨提示
    private void showToastDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("温馨提示：");
        builder.setMessage(msg);
        builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
//		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//				initData();
//			}
//		});
        builder.setCancelable(false);
        toastDialog = builder.create();
        toastDialog.show();
    }

    //启动高德定位服务
    public void initLocation() {
        startService(new Intent(this, LocationServiceForGaoDe.class));
    }

}
