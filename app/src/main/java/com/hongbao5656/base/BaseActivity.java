package com.hongbao5656.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hongbao5656.R;
import com.hongbao5656.util.SPU;

import butterknife.ButterKnife;

public abstract class BaseActivity extends RootActivity {

	public int which1;
	public String which_area;
	public Resources r;
	public LayoutInflater mInflater;
	/**
	 * 默认标题栏
	 */
	private LinearLayout defualtTitle;
	/**
	 * 内容区域容器
	 */
	private LinearLayout contentLayout = null;
	/**
	 * 左边布局
	 */
	protected RelativeLayout leftBtLayout = null;
	/**
	 * 左边按钮
	 */
	protected TextView iv_leff = null;

	/**
	 * 标题栏显示
	 */
	private TextView titleTv = null;

	/**
	 * 右边按钮
	 */
	public TextView rightBt = null;

	/**
	 * 标题栏容器
	 */
	private LinearLayout titleLayout = null;

	private View divider = null;
	private Intent intent = null;

	/**
	 * 记录页面统计
	 */
	private String pageName = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBase();
//		int fontsize = SPU.getPreferenceValueInt(this,SPU.FONTSIZE,-1,SPU.STORE_USERINFO);
//		switch (fontsize){
//			case 0:this.setTheme(R.style.Theme_Text_0);break;
//			case 1:this.setTheme(R.style.Theme_Text_1);break;
//			case 2:case -1:this.setTheme(R.style.Theme_Text_2);break;
//			case 3:this.setTheme(R.style.Theme_Text_3);break;
//			case 4:this.setTheme(R.style.Theme_Text_4);break;
//			case 5:this.setTheme(R.style.Theme_Text_5);break;
//			case 6:this.setTheme(R.style.Theme_Text_6);break;
//			case 7:this.setTheme(R.style.Theme_Text_7);break;
//			case 8:this.setTheme(R.style.Theme_Text_8);break;
//		}
		setContentView(R.layout.activity_base);

		r = getResources();
		mInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		titleLayout = (LinearLayout) findViewById(R.id.title);
		contentLayout = (LinearLayout) findViewById(R.id.content);
		divider = findViewById(R.id.divider);
	}

	public String getStr(int resId) {
		if (r == null) {
			return "";
		}
		return r.getString(resId);
	}

	/**
	 * 设置标题栏和内容
	 * @param titleResID
	 * @param contentResID
	 */
	public void setContentViewWithTitle(final int titleResID,
										final int contentResID) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = null;

		if (titleResID > 0) {
			titleLayout.removeAllViews();
			v = inflater.inflate(titleResID, null);
			titleLayout.addView(v);
		} else {
			titleLayout.setVisibility(View.GONE);
		}

		if (contentResID > 0) {
			v = inflater.inflate(contentResID, null);
			contentLayout.removeAllViews();
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			contentLayout.addView(v, params);
		}
	}

	/**
	 * 使用默认标题栏
	 * @param contentResID
	 */
	public void setContentViewWithDefaultTitle(final int contentResID) {
		leftBtLayout = (RelativeLayout) findViewById(R.id.leftBt);
		iv_leff = (TextView) findViewById(R.id.iv_leff);
		titleTv = (TextView) findViewById(R.id.midTitle);
		rightBt = (TextView) findViewById(R.id.rightBt);
		defualtTitle = (LinearLayout) titleLayout.findViewById(R.id.default_title_bg);
		leftBtLayout.setOnClickListener(onClickListener);

		View v = null;

		if (contentResID > 0) {
			v = mInflater.inflate(contentResID, null);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.FILL_PARENT);
			contentLayout.addView(v, params);
		}
	}

	/**
	 * 设置标题
	 *
	 * @param title
	 */
	public void setTitleName(final String title) {
		if (titleTv != null && title != null) {
			titleTv.setText(title);
			// titleTv.setText(Html.fromHtml("<u>"+title+"</u>"));
		}
	}

	/**
	 * 设置右边按钮背景
	 * @param title
	 */
	public void setRightBg(int resBg, String title, int resColor) {
		if (rightBt != null) {
			rightBt.setBackgroundResource(resBg);
			rightBt.setText(title);
			rightBt.setTextColor(resColor == 0 ? (r.getColor(R.color.back)) : r
					.getColor(resColor));
		}
	}


	/**
	 * 设置右侧字体及颜色
	 */
	public TextView setRightTextStyle(String text, int res) {
		if (rightBt != null) {
			rightBt.setText(text);
			rightBt.setTextColor(res);
		}
		return rightBt;
	}

	/**
	 * 设置左边按钮背景
	 * @param title
	 */
	public TextView setLeftBg(int resBg, String title) {
		if (iv_leff != null) {
			iv_leff.setBackgroundResource(resBg);
		}
		return iv_leff;
	}

	/**
	 * 设置左边按钮背景 传值-1表示不设置
	 *
	 * @param resBg
	 * @param title
	 * @param resColor
	 * @return
	 */
	public TextView setLeftBg(int resBg, String title, int resColor) {
		iv_leff.setText(title);
		if (resBg != -1) {
			iv_leff.setBackgroundResource(resBg);
		}
		if (resColor != -1) {
			iv_leff.setTextColor(resColor);
		}
		return iv_leff;
	}

	/**
	 * 获取版本号
	 * @return 当前应用的版本号
	 */
	public String getVersion() {
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 按钮监听事件
	 */
	OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.leftBt: {
					BaseActivity.this.finish();
					break;
				}
				case R.id.rightBt: {
					break;
				}
			}
		}

	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 可能全屏或者没有ActionBar等
	 */
	private void setBase() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	protected void creatDialog_cx(final View view) {
		final String[] cartypes = SPU.getPreferenceValueString(BaseActivity.this,SPU.CARTYPE,"",SPU.STORE_SETTINGS).split(",");
//		List.
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setIcon(R.drawable.red_dot)
				.setTitle("请选择车型：")
				.setSingleChoiceItems(cartypes,0,new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
//				isChecked = true;
						((Button)view).setText(cartypes[which]);
						dialog.cancel();
					}
				})
//		.setPositiveButton("确定", new OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				if (!isChecked) {
//					((Button)view).setText(AppSettings.getCarType(PublishSupplyActivity.this)[0]);
//				}
//				isChecked = false;
//				dialog.cancel();
//			}
//		})
				.create();
		dialog.show();
	}

	protected void creatDialog_cc(final View view) {
		final String[] carlens = SPU.getPreferenceValueString(BaseActivity.this,SPU.CARLEN,"",SPU.STORE_SETTINGS).split(",");
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setIcon(R.drawable.red_dot)
				.setTitle("请选择车长：")
				.setSingleChoiceItems(carlens,0,new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
//				isChecked2 = true;
						((Button)view).setText(carlens[which]);
						dialog.cancel();
					}
				})
				.create();
		dialog.show();
	}

	protected void creatDialog_et(final View view) {
		final String[] emptycars = SPU.getPreferenceValueString(BaseActivity.this,SPU.EMPTYCAR,"",SPU.STORE_SETTINGS).split(",");
		final String[] emptycars2 = SPU.getPreferenceValueString(BaseActivity.this,SPU.EMPTYCAR2,"",SPU.STORE_SETTINGS).split(",");
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setIcon(R.drawable.red_dot)
				.setTitle("请选择空车时间：")
				.setSingleChoiceItems(emptycars,0,new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
//				isChecked2 = true;
						((Button)view).setText(emptycars[which]);
						which1 = Integer.parseInt(emptycars2[which]);
						dialog.cancel();
					}
				})
				.create();
		dialog.show();
	}

	protected void creatDialog_area(final View view) {
		final String[] area = SPU.getPreferenceValueString(BaseActivity.this,SPU.AREA,"",SPU.STORE_SETTINGS).split(",");
		final String[] area2 = SPU.getPreferenceValueString(BaseActivity.this,SPU.AREA2,"",SPU.STORE_SETTINGS).split(",");
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setIcon(R.drawable.red_dot)
				.setTitle("请选择所属区域：")
				.setSingleChoiceItems(area,0,new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
//				isChecked2 = true;
						((TextView)view).setText(area[which]);
						which_area = area2[which];
						dialog.cancel();
					}
				})
				.create();
		dialog.show();
	}

	protected void creatDialog_hl(final View view,final EditText publish_hwmc_et) {
		final String[] huoleis = SPU.getPreferenceValueString(BaseActivity.this,SPU.HUOLEI,"",SPU.STORE_SETTINGS).split(",");
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setIcon(R.drawable.red_dot)
				.setTitle("请选择货类：")
				.setSingleChoiceItems(huoleis,0,new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
//				isChecked2 = true;
						publish_hwmc_et.setText(huoleis[which]);
						dialog.cancel();
					}
				})
				.create();
		dialog.show();
	}

	protected void creatDialog_wv(final View view) {
		final String[] wvs = SPU.getPreferenceValueString(BaseActivity.this,SPU.WV,"",SPU.STORE_SETTINGS).split(",");
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setIcon(R.drawable.red_dot)
				.setTitle("请选择重量/体积单位：")
				.setSingleChoiceItems(wvs,0,new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
//				isChecked2 = true;
						((Button)view).setText(wvs[which]);
						dialog.cancel();
					}
				})
				.create();
		dialog.show();
	}

	public void setInputUI(View view,final Activity activity) {
		// Set up touch listener for non-text box views to hide keyboard.
		if (!(view instanceof EditText)) {
			view.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					InputMethodManager inputMethodManager = (InputMethodManager) activity
							.getSystemService(Activity.INPUT_METHOD_SERVICE);
					inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
					return false;
				}
			});
		}
		// If a layout container, iterate over children and seed recursion.
		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				View innerView = ((ViewGroup) view).getChildAt(i);
				setInputUI(innerView,activity);
			}
		}
	}


	public void hideStatus() {
		// 经测试在代码里直接声明透明状态栏更有效
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			getWindow().getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE
							| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_FULLSCREEN
							| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
							| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_IMMERSIVE
			);
		}
	}

}