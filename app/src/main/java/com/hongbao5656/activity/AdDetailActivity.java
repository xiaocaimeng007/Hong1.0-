package com.hongbao5656.activity;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.hongbao5656.R;
/**
 */
public class AdDetailActivity extends Activity{
	private WebView wv;
//	private TextView top_bar;
	private String requestType;
	private int requestCode=1;
	private String info;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_addetail);
		wv = (WebView)findViewById(R.id.web_wv);
//		top_bar = (TextView)findViewById(R.id.top_bar);
		initView();
//		top_bar.setText("详情");
		info =getIntent().getStringExtra("info");
		setListener();
		// 得到设置属性的对象
		WebSettings webSettings = wv.getSettings();
		// 使能JavaScript
		webSettings.setJavaScriptEnabled(true);
		wv.loadDataWithBaseURL(null, info, "text/html", "UTF-8", null);
	}

	private void initView() {
	}

	private void setListener() {
//		findViewById(R.id.rl_back).setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				AdDetailActivity.this.finish();
//			}
//		});
	}
}
