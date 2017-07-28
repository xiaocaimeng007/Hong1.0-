package com.hongbao5656.util;

import android.app.Dialog;
import android.content.Context;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hongbao5656.R;

public class AllDialog {

	public static Context context;
	public static Dialogcallback dialogcallback;
	public static Dialog dialog;
	public static TextView textView;
	public static ImageButton ibtn_gift;
	public static RelativeLayout btn_ok;
	public static RelativeLayout btn_fangqi;

	/**
	 * init the dialog
	 *
	 * @param con
	 */
	public AllDialog(Context con) {
		this.context = con;
		dialog = new Dialog(context, R.style.Dialog);
		dialog.setContentView(R.layout.alldialog);
		textView = (TextView) dialog.findViewById(R.id.textview);
		btn_ok = (RelativeLayout) dialog.findViewById(R.id.btn_ok);
		btn_fangqi = (RelativeLayout) dialog.findViewById(R.id.btn_fangqi);
	}

	/**
	 * 设定一个interface接口,使mydialog可以处理activity定义的事情
	 * @author sfshine
	 *
	 */
	public interface Dialogcallback {
		public void dialogdo(String string);
	}

	public void setDialogCallback(Dialogcallback dialogcallback) {
		this.dialogcallback = dialogcallback;
	}

	/**
	 * @param content
	 * */
	public void setContent(String content) {
		textView.setText(content);
	}

	/**
	 * Get the Text of the EditText
	 * */
	public void show() {
		dialog.show();
	}

	public void hide() {
		dialog.hide();
	}

	public void dismiss() {
		dialog.dismiss();
	}
}

