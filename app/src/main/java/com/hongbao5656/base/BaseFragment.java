package com.hongbao5656.base;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.aaron.library.MLog;
import com.hongbao5656.R;
import com.hongbao5656.util.Constants;
import com.hongbao5656.db.DBHelper;
import com.hongbao5656.jpush.JPushUtil;
import com.hongbao5656.okhttp.HttpDataHandlerListener;
import com.hongbao5656.okhttp.HttpException;
import com.hongbao5656.okhttp.HttpRequest;
import com.hongbao5656.okhttp.HttpUtil;
import com.hongbao5656.okhttp.ResultCallBack;
import com.hongbao5656.service.LocationServiceForGaoDe;
import com.hongbao5656.util.LoadingDialog;
import com.hongbao5656.util.MailManager;
import com.hongbao5656.util.NU;
import com.hongbao5656.util.SPU;
import com.hongbao5656.util.TU;
import com.hongbao5656.util.UpdateManager;
import com.squareup.okhttp.Request;
import org.json.JSONException;

public abstract class BaseFragment
        extends Fragment
        implements ResultCallBack {

    private HttpDataHandlerListener mlistener;
    protected LoadingDialog mDialog;
    /**
     * Fragment当前状态是否可见
     */
    protected boolean isVisible;
    protected Activity mActivity;
    public Resources r;
    private DBHelper dbHelper;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = activity;// 拿到所属的Activity
        r = activity.getResources();
//        mDialog = LoadingDialog.createDialog(activity);
        mDialog = new LoadingDialog(activity);
    }

    /**
     * 一个Activity中以viewpager（或其他容器）与多个Fragment来组合使用，
     * 而如果每个fragment都需要去（本地/网络）加载数据， 那么在这个activity刚创建的时候就变成需要初始化大量资源。
     * <p/>
     * 该方法用于告诉系统当前Fragment的UI是否是可见的。 所以我们只需要继承Fragment并重写该方法，
     * 即可实现在fragment可见时才进行数据加载操作，即Fragment数据的懒加载/缓加载。
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {// fragment可见
            isVisible = true;
            onVisible();
        } else {// fragment不可见
            isVisible = false;
            onInvisible();
        }
    }

    /**
     * fragment被设置为可见时调用
     */
    protected void onVisible() {
        lazyLoad();
    }

    /**
     * fragment被设置为不可见时调用
     */
    protected void onInvisible() {
    }

    /**
     * 创建视图
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * 通过类名启动Activity
     */
    public void openActivity(Class<?> pClass) {
        openActivity(pClass, null);
    }

    /**
     * 携带数据启动Activity
     */
    public void openActivity(Class<?> pClass, Bundle pBundle) {
        Intent intent = new Intent(mActivity, pClass);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivity(intent);
    }

    /**
     * 通过Action匿名启动Activity
     */
    protected void openActivity(String pAction) {
        openActivity(pAction, null);
    }

    /**
     * 通过Action匿名启动Activity并携带Bundle数据
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
    public void sendPostRequest(int connectionId, String params,
                                HttpDataHandlerListener listener, boolean isShow) {
        if (!NU.checkNetworkAvailable(mActivity)) {
            TU.show(mActivity, getString(R.string.no_net), Toast.LENGTH_LONG);
            return;
        }
//		if (isShow && !mDialog.isShowing()) {
//			mDialog.show();
//		}
        this.mlistener = listener;

        if (Constants.download == connectionId) {
            HttpUtil.getInstance().sendPostRequest(connectionId,
                    App.BATE_URL_UPDOWN, params, this);
        } else {
            HttpUtil.getInstance().sendPostRequest(connectionId,
                    App.BATE_URL, params, this);
        }

    }

    /**
     * get请求
     */
    public void sendGetRequest(int connectionId, HttpRequest request,
                               HttpDataHandlerListener listener, boolean isShow) {
        if (!NU.checkNetworkAvailable(mActivity)) {
            TU.show(mActivity, getString(R.string.no_net), Toast.LENGTH_LONG);
            return;
        }
//		if (isShow && !mDialog.isShowing()) {
//			mDialog.show();
//		}
        this.mlistener = listener;
        HttpUtil.getInstance().sendGetRequest(connectionId, request, this);
    }

    //隐藏对话框
    public void dismiss(Object data) {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        if (data == null || data.equals("")) {
            return;
        }
    }

    /**
    * 请求成功
    */
    @Override
    public void onResponse(int connectionId, Object data) {
        try {
//            MLog.i("dm", data != null ? "响应参数：" + connectionId + ">" + data.toString() : "响应参数为空");

            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }

            if (Constants.download == connectionId) {
                mlistener.setHandlerGetDataSuccess(connectionId, data);
                return;
            }
            mlistener.setHandlerPostDataSuccess(connectionId,data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     *请求失败
     */
    @Override
    public void onError(int connectedId, Request request,
                        HttpException httpException,
                        String params,String content) {
        try{
            //请求失败消失对话框
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
            //打印错误日志
            MLog.i(logToMe(connectedId, httpException, params, content));
            //发送错误日志
            sendEMailDM("error",logToMe(connectedId, httpException, params, content),"");
            //toast显示
            if (999 == httpException.getResponseCode()) {
                TU.showLong(mActivity,
                        getString(R.string.data_failure)
                                + "\n[" + App.version + "-" + connectedId + "-" + App.account + "]");
            } else {
                TU.showLong(mActivity,
                        getString(R.string.no_error)
                                + "\n[" + App.version + "-" + connectedId + "-" + App.account + "]");
            }

            mlistener.setHandlerPostDataError(connectedId,request,httpException,params,content);

        }catch(Exception e){
            e.printStackTrace();
        }

    }


    public void clearAndStopLocationData() {
        //停止坐标服务
        Intent intentservice = new Intent(mActivity, LocationServiceForGaoDe.class);
        mActivity.stopService(intentservice);

        //清空数据库已有坐标
        if (dbHelper == null) {
            dbHelper = DBHelper.getInstance(mActivity);
            dbHelper.clearTableData(mActivity);
            dbHelper = null;
        }
    }

    private String logToMe(int connectedId, HttpException httpException, String params, String content) {
        return "["+ App.version + "-" + connectedId + "-" + App.account + "]"
                + "<<<params==" + params + "/>>>\n"
                + "<<<content==" + content + "/>>>\n"
                + "<<<M==" + httpException.getMessage() + "/>>>\n"
                + "<<<C==" + httpException.getCause() + "/>>>\n"
                + "<<<RC==" + httpException.getResponseCode()+ "/>>>\n"
                + "<<<et==" + httpException.toString();
    }

    /**
     * 抽象方法lazyLoad在onVisible里面调用 延迟到fragment可见时加载数据 子类重写即可实现懒加载
     */
    protected abstract void lazyLoad();

    /**
     * 初始化控件
     */
    protected abstract void initView();

    /**
     * 设置监听
     */
    protected abstract void initMonitor();


    //第三方登录  清坐标 清个人信息 清推送
    public void eixtAccount() {
        //销毁当前账户信息
        SPU.clearPreference(mActivity, SPU.STORE_USERINFO);
        //清坐标
        clearAndStopLocationData();
        //清推送
        setMyTags("clear,clear");
    }

    //发邮件
    public void sendEMailDM(String tag, String content, String path) {
        MailManager.getInstance().sendMailWithFile(mActivity,tag,content,path);
    }
    //推送
    public void setMyTags(String tag){
        JPushUtil.getInstance().setMyTags(mActivity,tag);
    }
    //升级
    public void UpdateApp() {
        UpdateManager.getInstance().checkUpdateInfo(mActivity);
    }

}