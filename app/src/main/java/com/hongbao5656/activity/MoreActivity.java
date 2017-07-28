package com.hongbao5656.activity;


import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import com.example.aaron.library.MLog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.hongbao5656.R;
import com.hongbao5656.base.App;
import com.hongbao5656.base.BaseActivity;
import com.hongbao5656.okhttp.HttpException;
import com.hongbao5656.util.Constants;
import com.hongbao5656.okhttp.HttpDataHandlerListener;
import com.hongbao5656.okhttp.HttpRequest;
import com.hongbao5656.util.AllDialog;
import com.hongbao5656.util.ParamsService;
import com.hongbao5656.util.ResponseParams;
import com.hongbao5656.util.SPU;
import com.hongbao5656.util.SU;
import com.hongbao5656.util.TU;
import com.hongbao5656.util.UpdateManager;
import com.squareup.okhttp.Request;

import org.json.JSONException;

import java.util.LinkedHashMap;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class MoreActivity extends BaseActivity implements HttpDataHandlerListener{
    private Context mContext;
    private RelativeLayout rl_about;
    private RelativeLayout rl_feedback;
    private RelativeLayout rl_share;
    private Button exit;
    private RelativeLayout rl_version;
    private RelativeLayout rl_server;
    private UpdateManager mUpdateManager;
    private AllDialog allDialog;
    private TextView versionname;
    private LinearLayout rl_invite;
    private LinearLayout rl_fontsize;
    private LinearLayout rl_pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        this.mContext = MoreActivity.this;
        initView();
        initDatas();
        initListener();
    }


    public void initDatas() {
        sendGetRequest(
                Constants.download,
//                new HttpRequest(new Request.Builder().url("http://ww.56hb.net/api/Version/hytx").build()),
                new HttpRequest(new Request.Builder().url(App.BATE_URL_UPDOWN).build()),
                this);
    }

    public void initInvite(String yqm) {
        sendPostRequest(
                Constants.invite,
                new ParamsService().requestKV("InvitedCode",yqm,Constants.invite),
                MoreActivity.this,
                false);
    }

    private void initListener() {
        findViewById(R.id.leftBt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPU.setPreferenceValueBoolean(mContext, SPU.IS_GRRZ, true, SPU.STORE_SETTINGS);
                finish();
            }
        });
        rl_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, WebBrowserActivity.class);
                i.putExtra(WebBrowserActivity.ACTION_KEY, WebBrowserActivity.ACTION_KEY_ABOUT_HUODADA);
                i.putExtra(WebBrowserActivity.URL, "file:///android_asset/versions_driver.html");
                startActivity(i);
            }
        });
        rl_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare();
            }
        });
        rl_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(FeedBackActivity.class);
            }
        });

        rl_server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//				builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setTitle("温馨提示：");
                builder.setMessage("每天09:00-17:00竭诚为您服务！");
                builder.setCancelable(true);
                builder.setPositiveButton("拨打", listener);
                builder.setNegativeButton("取消", listener);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        rl_version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateApp();
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allDialog = new com.hongbao5656.util.AllDialog(mContext);
                allDialog.setContent("您确认要退出账户吗？");
                allDialog.show();
                allDialog.btn_ok.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (allDialog != null) {
                            allDialog.dismiss();
                        }
                        eixtAccount();
                        //销毁MainActivity和MoreActivity进入LoginActivity
                        Intent intent = new Intent(MoreActivity.this, LoginActivity.class);
                        ComponentName cn = intent.getComponent();
                        Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
                        startActivity(mainIntent);
                    }
                });
                allDialog.btn_fangqi.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        allDialog.dismiss();
                    }
                });
            }
        });

        rl_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//				builder.setIcon(android.R.drawable.ic_dialog_alert);
//                builder.setView(R.layout.dialog_yqm);
                View view = getLayoutInflater().inflate(R.layout.dialog_yqm,null);
                final EditText et_login_tel = (EditText)view.findViewById(R.id.et_login_tel);
                builder.setView(view);
                builder.setTitle("邀请码：");
//                builder.setMessage("每天09:00-17:00竭诚为您服务！");
                builder.setCancelable(true);
                builder.setPositiveButton("提交", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //TODO
                                if (SU.isEmpty(et_login_tel.getText().toString())) {
                                    TU.show(MoreActivity.this,"邀请码不能为空！");
                                    return;
                                }
                                initInvite(et_login_tel.getText().toString());
//                                TU.show(mContext,""+et_login_tel.getText().toString());
                                break;
                            case DialogInterface.BUTTON_NEUTRAL:
                                TU.show(mContext,"取消");
                                dialog.cancel();
                                break;
                        }
                    }
                });
                builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //TODO
                                break;
                            case DialogInterface.BUTTON_NEUTRAL:
                                dialog.cancel();
                                break;
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        rl_fontsize.setOnClickListener(new View.OnClickListener() {//字体设置
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//				builder.setIcon(android.R.drawable.ic_dialog_alert);
//                builder.setView(R.layout.dialog_yqm);
                int size = SPU.getPreferenceValueInt(MoreActivity.this,SPU.FONTSIZE,-1,SPU.STORE_USERINFO);
                View view = getLayoutInflater().inflate(R.layout.dialog_fontsize,null);
                final TextView sbrtext = (TextView)view.findViewById(R.id.seekBartext);
                if(size == -1){
                    sbrtext.setTextSize(20);
                }else{
                    sbrtext.setTextSize(16+size*2);
                }
                final SeekBar sbr = (SeekBar)view.findViewById(R.id.seekBar);
                if(size == -1){
                    sbr.setProgress(2);
                }else{
                    sbr.setProgress(size);
                }
                sbr.setMax(8);
                sbr.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                        sbrtext.setText("数量"+i);
                        sbrtext.setTextSize(16+i*2);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                builder.setView(view);
                builder.setTitle("请拖动设置字体大小：");
//                builder.setMessage("每天09:00-17:00竭诚为您服务！");
                builder.setCancelable(true);
                builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //TODO
                                SPU.setPreferenceValueInt(MoreActivity.this,SPU.FONTSIZE,sbr.getProgress(),SPU.STORE_USERINFO);
                                TU.show(mContext,"设置成功");
                                App.fontsize = true;
                                break;
                            case DialogInterface.BUTTON_NEUTRAL:
                                TU.show(mContext,"取消");
                                dialog.cancel();
                                break;
                        }
                    }
                });
                builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //TODO
                                break;
                            case DialogInterface.BUTTON_NEUTRAL:
                                dialog.cancel();
                                break;
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        rl_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MoreActivity.this, ForgetPasswordActivity.class);
                ComponentName cn = intent.getComponent();
                Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
                startActivity(mainIntent);
            }
        });

    }



    private void initView() {
        rl_invite = (LinearLayout) findViewById(R.id.rl_invite);
        rl_fontsize = (LinearLayout) findViewById(R.id.rl_fontsize);
        rl_pwd = (LinearLayout) findViewById(R.id.rl_pwd);
        if ("Y".equals(SPU.getPreferenceValueString(mContext, SPU.sellcode, "N", SPU.STORE_USERINFO))){
//                    TU.show(this,"");
            rl_invite.setVisibility(View.GONE);
        }
        rl_about = (RelativeLayout) findViewById(R.id.rl_about);
        rl_share = (RelativeLayout) findViewById(R.id.rl_share);
        rl_feedback = (RelativeLayout) findViewById(R.id.rl_feedback);
        exit = (Button) findViewById(R.id.exit);
        rl_version = (RelativeLayout) findViewById(R.id.rl_version);
        rl_server = (RelativeLayout) findViewById(R.id.rl_server);
        versionname = (TextView)findViewById(R.id.versionname);
    }

    @Override
    public void setHandlerPostDataSuccess(int connectionId, Object data) throws JSONException {
        ResponseParams<LinkedHashMap> iParams =jiexiData(data);
        if (Constants.invite == connectionId){
                TU.show(mContext, "提交成功");
                SPU.setPreferenceValueString(mContext, SPU.sellcode, "Y", SPU.STORE_USERINFO);
        }
    }

    @Override
    public void setHandlerPostDataError(int connectedId, Request request, HttpException httpException, String params, String content) throws JSONException {

    }

    @Override
    public void setHandlerGetDataSuccess(int connectionId, Object data) throws JSONException {
        if (Constants.download == connectionId) {//{"version":"1.2.1","content":"1.界面优化\r\n2.bug修复"}
            MLog.i("get my fragment", data.toString() + "  " + JSON.parseObject(data.toString()).get("version"));
            App.cversionCode = Integer.parseInt((JSON.parseObject(data.toString()).get("version")).toString());
            String content = JSON.parseObject(data.toString()).get("content").toString();
            if (SU.getVersionCodeAndVersionName(mContext).versionCode >= App.cversionCode) {
                versionname.setText("已是最新版本(v" + SU.getVersionCodeAndVersionName(mContext).versionName + ")");
                versionname.setTextColor(getResources().getColor(R.color.version));
                rl_version.setClickable(false);
            } else {
                versionname.setText("更新版本(v" + content + ")");
                versionname.setTextColor(getResources().getColor(R.color.versionname));
                rl_version.setClickable(true);
            }
        }
    }

    @Override
    public void setHandlerGetDataError(int connectionId, Object data) throws JSONException {

    }


    private void showShare() {
        ShareSDK.initSDK(MoreActivity.this);//ShareSDK初始化
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权  单点登录关闭
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        // oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));

        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("鸿运天下-鸿宝全国版");
        // QQ好友的 QQ朋友圈的 点击链接 短信才可以
        oks.setTitleUrl("http://zhushou.360.cn/detail/index/soft_id/3299635?recrefer=SE_D_%E9%B8%BF%E8%BF%90%E5%A4%A9%E4%B8%8B");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("鸿运天下是上海鸿宝网络科技有限公司研发的一款全国版车货匹配类app," +
                "集货主版、司机版于一体，主要功能有发货、找货、空车上报、查找车源、" +
                "货源的个性化即时推送、交易双方的资金担保和途中实时定位、" +
                "服务于司机的天气信息、路况信息和线路规划与导航，" +
                "拥有面向全国的线下实体服务与强大的线上车货交易匹配，" +
                "绝对是您货运天下的强大神器。");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath(App.LOGO_IMAGE);//确保SDcard下面存在此张图片
        // 微信好友、朋友圈和收藏的点击链接  QQ好友 QQ朋友圈 短信不行
//		oks.setUrl("http://android.myapp.com/myapp/detail.htm?apkName=com.hongbao5656");
//		oks.setUrl("http://a.app.qq.com/o/simple.jsp?pkgname=com.hongbao5656");

        //网络图片地址
//		oks.setImageUrl("http://p16.qhimg.com/bdm/64_64_/t013d823147e29a8f6b.png");

        oks.setUrl("http://zhushou.360.cn/detail/index/soft_id/3299635?recrefer=SE_D_%E9%B8%BF%E8%BF%90%E5%A4%A9%E4%B8%8B");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("车货匹配神器");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite("鸿运天下");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://zhushou.360.cn/detail/index/soft_id/3299635?recrefer=SE_D_%E9%B8%BF%E8%BF%90%E5%A4%A9%E4%B8%8B");

// 启动分享GUI
        oks.show(mContext);
    }

    public void commentUs() {
        Uri uri = Uri.parse("market://details?id=" + mContext.getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent.resolveActivity(mContext.getPackageManager()) != null)
            startActivity(intent);
        else
            TU.show(mContext, "您还没有安装应用市场");
    }


    private android.content.DialogInterface.OnClickListener listener = new android.content.DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "02151656565")));
//				finish();//去拨打电话
                    dialog.cancel();
                    break;
                case DialogInterface.BUTTON_NEUTRAL:
                    dialog.cancel();
                    break;
            }
        }
    };

//    private android.content.DialogInterface.OnClickListener listener2 =

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            SPU.setPreferenceValueBoolean(mContext, SPU.IS_GRRZ, false, SPU.STORE_SETTINGS);
            finish();
            return  false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
