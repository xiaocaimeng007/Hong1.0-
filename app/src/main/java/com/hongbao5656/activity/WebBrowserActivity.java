package com.hongbao5656.activity;

import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hongbao5656.R;
import com.hongbao5656.base.BaseActivity;
import com.hongbao5656.util.SPU;

/**
 * 浏览器界面
 *
 * @author huajun
 */
public class WebBrowserActivity extends BaseActivity {
    /**
     * 启动事件
     */
    public static final String ACTION_KEY = "action_key";

    /**
     * 事件:等级说明
     */
    public static final String ACTION_KEY_GRADE = "action_key_grade";

    /**
     * 事件:更新版本
     */
    public static final String ACTION_KEY_UPDATE = "action_key_update";
    /**
     * 事件:积分说明
     */
    public static final String ACTION_KEY_INTEGRAL = "action_key_integral";
    /**
     * 事件:打开网页(默认)
     */
    public static final String ACTION_KEY_OPEN_WEBPAGE = "action_key_open_webpage";
    /**
     * 关于货大大
     */
    public static final String ACTION_KEY_ABOUT_HUODADA = "action_about_huodada";
    /**
     * 使用协议
     */
    public static final String ACTION_KEY_DEAL = "action_deal";

    private WebView webview;
    private String actionKey;

    public static String URL = "url";
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webbrowser);
        initView();
        initListener();
    }

    public void initView() {
        webview = (WebView) findViewById(R.id.webview);
    }

    public void initListener() {
        findViewById(R.id.leftBt).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SPU.setPreferenceValueBoolean(WebBrowserActivity.this, SPU.IS_GRRZ, true, SPU.STORE_USERINFO);
                finish();
            }
        });
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        webview.setWebChromeClient(new WebChromeClient());
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setDefaultTextEncodingName("UTF-8");
//		webview.addJavascriptInterface(getApplicationContext(), "jsObj");
        webview.setWebViewClient(new WebViewClient() {
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed(); // 接受所有网站的证书
            }
        });

        Intent intent = getIntent();
        if (!intent.hasExtra(ACTION_KEY)) {
            return;
        }
        actionKey = intent.getStringExtra(ACTION_KEY);
        url = intent.getStringExtra(URL);

        //		if (actionKey.equals(ACTION_KEY_GRADE)) {
        //			setTitleName("等级说明");
        //			setLeftBg(R.drawable.image_return, "");
        //		} else if (actionKey.equals(ACTION_KEY_INTEGRAL)) {
        //			setTitleName("积分说明");
        //			setLeftBg(R.drawable.image_return, "");
        //		} else if (actionKey.equals(ACTION_KEY_OPEN_WEBPAGE)) {
        //
        //		} else if(actionKey.equals(ACTION_KEY_DEAL)){
        //			setTitleName("使用协议");
        //			setLeftBg(R.drawable.image_return, "");
        //		} else if(actionKey.equals(ACTION_KEY_ABOUT_HUODADA)){
        //			setTitleName("关于货大大");
        //			setLeftBg(R.drawable.image_return, "");
        //		} else if(actionKey.equals(ACTION_KEY_UPDATE)){
        //			setTitleName("下载更新版本");
        //			setLeftBg(R.drawable.image_return, "");
        //		}

        webview.setWebViewClient(new WebViewClient());
        webview.loadUrl(url);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 判断是否可以返回操作
        if (webview.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK) {
            webview.goBack();
            return true;
        } else if (!webview.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webview.destroy();
    }
}
