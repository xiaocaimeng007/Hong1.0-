package com.hongbao5656.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.hongbao5656.R;
public class LoadingDialog extends Dialog {
//	private Context context = null;
//	private static LoadingDialog customProgressDialog = null;
	private LoadingDialog customProgressDialog = null;

//	public static synchronized LoadingDialog getInstance(Context context) {
//		if (customProgressDialog == null) {
//			customProgressDialog = new LoadingDialog(context);
//		}
//		return customProgressDialog;
//	}
	public LoadingDialog(Context context) {
		super(context);
		createDialog(context);
	}

	public LoadingDialog(Context context, int theme) {
		super(context, theme);
	}

	public  LoadingDialog createDialog(Context context) {
//	public static LoadingDialog createDialog(Context context) {
		customProgressDialog = new LoadingDialog(context,R.style.CustomProgressDialog);
		customProgressDialog.setContentView(R.layout.customprogressdialog);
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		customProgressDialog.setCanceledOnTouchOutside(true);//澶栭儴鐐瑰嚮鏈夋晥
//		customProgressDialog.setOnKeyListener(keylistener);
		return customProgressDialog;
	}

//	static OnKeyListener keylistener = new OnKeyListener() {
//
//		@Override
//		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//			if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//				return true;
//			}else {
//				return true;//杩斿洖false锛氳姹傛暟鎹椂锛岀偣鍑昏繑鍥炴寜閽笉鎵ц浠讳綍鎿嶄綔
//			}
//		}
//	};

	public void onWindowFocusChanged(boolean hasFocus) {
		if (customProgressDialog == null) {
			return;
		}
		ImageView imageView = (ImageView) customProgressDialog.findViewById(R.id.loadingImageView);
		if (imageView != null) {
			AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
			animationDrawable.start();
		}
	}


	/**
	 * 设置底部文字提示
	 */
	public LoadingDialog setMessage(String strMessage) {
		TextView tvMsg = (TextView) customProgressDialog.findViewById(R.id.id_tv_loadingmsg);
		if (tvMsg != null) {
			tvMsg.setText(strMessage);
		}
		return customProgressDialog;
	}

}
