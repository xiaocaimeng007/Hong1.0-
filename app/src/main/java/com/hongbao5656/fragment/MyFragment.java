package com.hongbao5656.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.aaron.library.MLog;
import com.hongbao5656.R;
import com.hongbao5656.activity.DailyServiceActivity;
import com.hongbao5656.activity.DriverCertificationActivity;
import com.hongbao5656.activity.LCYJActivity;
import com.hongbao5656.activity.LoginActivity;
import com.hongbao5656.activity.MainActivity;
import com.hongbao5656.activity.MoreActivity;
import com.hongbao5656.activity.ScanQRActivity;
import com.hongbao5656.activity.ShipperCertificationActivity;
import com.hongbao5656.activity.ShowBaoJiaActivity;
import com.hongbao5656.activity.ShowLineActivity;
import com.hongbao5656.activity.TZDWeiActivity;
import com.hongbao5656.base.App;
import com.hongbao5656.base.BaseFragment;
import com.hongbao5656.okhttp.HttpException;
import com.hongbao5656.util.Constants;
import com.hongbao5656.okhttp.HttpDataHandlerListener;
import com.hongbao5656.util.IMap;
import com.hongbao5656.util.ImageUtils;
import com.hongbao5656.util.ParamsService;
import com.hongbao5656.util.ResponseParams;
import com.hongbao5656.util.TU;
import com.hongbao5656.view.RoundedImageView;
import com.hongbao5656.view.SelectPhotoWindow;
import com.hongbao5656.wxapi.MyMoneyActivity;
import com.squareup.okhttp.Request;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.LinkedHashMap;

public class MyFragment extends BaseFragment
        implements OnClickListener, HttpDataHandlerListener {
    private static final String TAG = "MyFragment";
    private View root;
    private MainActivity mActivity;
    protected boolean isPrepared = false;
    private RelativeLayout rl_hz;
    private RelativeLayout rl_sj;
    private RelativeLayout rl_sjdwei;
    private RelativeLayout rl_more;
    private TextView tv_sjrz;
    private TextView tv_hzrz;
    private RelativeLayout rl_cyfw;
    private RelativeLayout ll_myqianbao;
    private RelativeLayout btn_me_myteam;

    private RelativeLayout rl_baojiazhong;
    private RelativeLayout rl_yizhongbiao;
    private RelativeLayout rl_weizhongbiao;

    RoundedImageView raiv_userphoto;
    TextView tv_username;
    TextView tv_usertel;
    private ImageView iv_scan2;
    private SelectPhotoWindow selectphotoWindow;
    private ScrollView sll;
    private String localPath;
    private Bitmap bitmap;
    private String imgdata = "";
    private RelativeLayout rl_rcfwu;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof MainActivity) {
//            MLog.i("fragment生命周期：onAttach" + TAG);
            mActivity = (MainActivity) activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
//        MLog.i("fragment生命周期：onCreate" + TAG);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        MLog.i("fragment生命周期：onCreateView" + TAG);
        root = inflater.inflate(R.layout.fragment_my, null);
        initView();
        initMonitor();
        isPrepared = true;
        lazyLoad();
        return root;
    }

    public void loadMyInfo() {
        sendPostRequest(
                Constants.rzhz,
                new ParamsService().request(Constants.rzhz),
                this,
                false);
    }

    @Override
    protected void initView() {
        iv_scan2 = (ImageView) root.findViewById(R.id.iv_scan2);
        rl_hz = (RelativeLayout) root.findViewById(R.id.rl_hz);
        rl_sj = (RelativeLayout) root.findViewById(R.id.rl_sj);
        rl_cyfw = (RelativeLayout) root.findViewById(R.id.rl_cyfw);
        rl_sjdwei = (RelativeLayout) root.findViewById(R.id.rl_sjdwei);
        rl_more = (RelativeLayout) root.findViewById(R.id.rl_more);
        rl_rcfwu = (RelativeLayout) root.findViewById(R.id.rl_rcfwu);
        tv_sjrz = (TextView) root.findViewById(R.id.tv_sjrz);
        tv_hzrz = (TextView) root.findViewById(R.id.tv_hzrz);
        ll_myqianbao = (RelativeLayout) root.findViewById(R.id.ll_myqianbao);//我的钱包

        rl_baojiazhong = (RelativeLayout) root.findViewById(R.id.rl_baojiazhong);
        rl_yizhongbiao = (RelativeLayout) root.findViewById(R.id.rl_yizhongbiao);
        rl_weizhongbiao = (RelativeLayout) root.findViewById(R.id.rl_weizhongbiao);


        btn_me_myteam = (RelativeLayout) root.findViewById(R.id.btn_me_myteam);//我的钱包
        raiv_userphoto = (RoundedImageView) root.findViewById(R.id.raiv_userphoto);
        Picasso.with(mActivity)
                .load(Uri.parse(App.URL_GERTX + App.phone + ""))
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(raiv_userphoto);
//        NO_CACHE是指图片加载时放弃在内存缓存中查找
//        NO_STORE是指图片加载完不缓存在内存中
        tv_username = (TextView) root.findViewById(R.id.tv_username);
        tv_usertel = (TextView) root.findViewById(R.id.tv_usertel);
        sll = (ScrollView) root.findViewById(R.id.sll);
    }

    @Override
    protected void initMonitor() {
        rl_hz.setOnClickListener(this);
        rl_sj.setOnClickListener(this);
        rl_cyfw.setOnClickListener(this);
        ll_myqianbao.setOnClickListener(this);
        btn_me_myteam.setOnClickListener(this);
        rl_sjdwei.setOnClickListener(this);
        rl_more.setOnClickListener(this);
        rl_rcfwu.setOnClickListener(this);
        iv_scan2.setOnClickListener(this);

        rl_baojiazhong.setOnClickListener(this);
        rl_yizhongbiao.setOnClickListener(this);
        rl_weizhongbiao.setOnClickListener(this);
      /*  rl_baojiazhong = (RelativeLayout) root.findViewById(R.id.rl_baojiazhong);
        rl_yizhongbiao = (RelativeLayout) root.findViewById(R.id.rl_yizhongbiao);
        rl_weizhongbiao = (RelativeLayout) root.findViewById(R.id.rl_weizhongbiao);*/

        raiv_userphoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectphotoWindow = new SelectPhotoWindow(mActivity, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.tv_pz://拍照
                                ImageUtils.openCameraImage(mActivity);
                                selectphotoWindow.dismiss();
                                break;
                            case R.id.tv_xc://相册
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");// 相片类型
                                mActivity.startActivityForResult(intent, ImageUtils.GET_IMAGE_FROM_PHONE);
                                selectphotoWindow.dismiss();
                                break;
                        }
                    }
                });
                selectphotoWindow.showAtLocation(sll, Gravity.BOTTOM
                        | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
            }
        });
    }

    @Override
    public void lazyLoad() {
//        MLog.i("fragment生命周期：执行了->lazyLoad()方法" + TAG);

        if (!isPrepared || !super.isVisible) {
            return;//如果没有执行onVreatView方法或者fragment不可见不执行当前方法
            //那也就是当前fragment可见或者执行了onVreatView方法就执行当前方法
        }
//        MLog.i("fragment生命周期：fragment可见或者onCreateView执行了才执行->lazyLoad()方法" + TAG);
        loadMyInfo();// 加载数据
        String nametype = "";
        if ("1".equals(App.UER_TYPE)) {
            nametype = "司机";
        } else if ("2".equals(App.UER_TYPE)) {
            nametype = "货主";
        } else if ("3".equals(App.UER_TYPE)) {
            nametype = "不限";
        }
        tv_username.setText(App.username + " | " + nametype);
        tv_usertel.setText(App.phone);
    }


    public void updateTX() {
        Picasso.with(mActivity)
                .load(Uri.parse(App.URL_GERTX + App.phone + ""))
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(raiv_userphoto);
    }

    @Override
    public void onClick(View v) {
        Intent i = null;
        switch (v.getId()) {
            case R.id.rl_hz://货主认证
                openActivity(ShipperCertificationActivity.class);
                break;
            case R.id.rl_sj://司机认证
                openActivity(DriverCertificationActivity.class);
                break;
            case R.id.rl_cyfw://里程运价
                openActivity(LCYJActivity.class);
                break;
            case R.id.rl_sjdwei://途中定位
                openActivity(TZDWeiActivity.class);
                break;
            case R.id.rl_more://更多
                openActivity(MoreActivity.class);
                break;
            case R.id.rl_rcfwu://日常服务
//                TU.shctivity, "开发中...");
                openActivity(DailyServiceActivity.class);
                break;
            case R.id.iv_scan2:
                Intent in = new Intent(mActivity, ScanQRActivity.class);
                in.putExtra(ScanQRActivity.ACTION_KEY, ScanQRActivity.ACTION_KEY_WEBPAGE);
                startActivityForResult(in, 100);
                break;
            case R.id.ll_myqianbao://我的钱包
                openActivity(MyMoneyActivity.class);
                break;
            case R.id.btn_me_myteam:
                TU.show(mActivity, "开发中...");
//				openActivity(MyDealActivity.class);//
                break;
            case R.id.tv_pz://拍照
                ImageUtils.openCameraImage(mActivity);
                selectphotoWindow.dismiss();
                break;
            case R.id.tv_xc://相册
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");// 相片类型
                mActivity.startActivityForResult(intent, ImageUtils.GET_IMAGE_FROM_PHONE);
                selectphotoWindow.dismiss();
                break;
            case R.id.rl_baojiazhong://报价中
                OpenBaoJiaList(1);
                break;
            case R.id.rl_yizhongbiao://已中标
                OpenBaoJiaList(2);
                break;
            case R.id.rl_weizhongbiao://未中标
                OpenBaoJiaList(3);
                break;
        }
    }

    void OpenBaoJiaList(int type) {
//        Intent intent = new Intent(mActivity, ShowLineActivity.class);
//        intent.putExtra("vo", vo);
//        startActivity(intent);
//        Bundle b=new
        //用Bundle携带数据
        Bundle bundle = new Bundle();
        //传递name参数为tinyphp
        bundle.putInt("baojiatype", type);
        openActivity(ShowBaoJiaActivity.class, bundle);
    }

    @Override
    public void setHandlerPostDataSuccess(int connectionId, Object data) throws JSONException {
        int errcode = -1;
        ResponseParams<LinkedHashMap> iParams = null;
        if (null != data) {
            iParams = IMap.jieXiResponse(data.toString());
            errcode = iParams.getErrcode();
        }
        switch (errcode) {
            case 50000://token过期   只清坐标
                TU.show(mActivity, "" + iParams.getErrmsg());
                mActivity.clearAndStopLocationData();//只清坐标
                openActivity(LoginActivity.class);
                mActivity.finish();
                break;
            case 50001://第三方登录  清坐标 清个人信息 清推送
                TU.show(mActivity, "" + iParams.getErrmsg());
                eixtAccount();
                openActivity(LoginActivity.class);
                mActivity.finish();
                break;
            case 44444://接口异常 升级app
                TU.show(mActivity, iParams.getErrmsg() + "-" + errcode);
                UpdateApp();
                if (10013 == connectionId || 10014 == connectionId) {
                    clearAndStopLocationData();
                }
                break;
            case 0://成功
                if (connectionId == Constants.rzhz) {//认证汇总
                    if (errcode == 0) {//AuthUserText
//				tv_grrz.setText(iParams.getData1().get("authusertext").toString());
                        tv_sjrz.setText(iParams.getData1().get("authdrivertext").toString());
                        tv_hzrz.setText(iParams.getData1().get("authcompanytext").toString());
                    }
                } else if (connectionId == Constants.txsc) {//上传个人头像
                    if (errcode == 0) {
                        TU.show(mActivity, "上传成功！");
                        Picasso.with(mActivity).load(Uri.parse(App.URL_GERTX + App.phone + "")).into(raiv_userphoto);
                    }
                }
                break;
            default:
                TU.show(mActivity, iParams.getErrmsg() + "-" + errcode);
                break;
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
//        MLog.i("fragment生命周期：onActivityCreated" + TAG);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
//        MLog.i("fragment生命周期：onStart" + TAG);
        super.onStart();
    }

    public void onResume() {
//        MLog.i("fragment生命周期：onResume");
        super.onResume();
    }

    public void onPause() {
        MLog.i("fragment生命周期：onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
//        MLog.i("fragment生命周期：onStop" + TAG);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        MLog.i("fragment生命周期：onDestroyView" + TAG);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        MLog.i("fragment生命周期：onDestroy" + TAG);
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        MLog.i("fragment生命周期：onDetach" + TAG);
        super.onDetach();
    }

}