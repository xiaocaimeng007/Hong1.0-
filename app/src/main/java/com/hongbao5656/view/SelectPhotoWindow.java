package com.hongbao5656.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.hongbao5656.R;

/***
 * 璇勫垎popwindow
 * @author wdd
 */
public class SelectPhotoWindow extends PopupWindow{

	private TextView tv_pz,tv_xc,cancel;
	private View mMenuView;
	private Activity mcontext;

	public SelectPhotoWindow(Activity context, OnClickListener itemsOnClick) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.ppw_selectphotowindow, null);
		tv_pz = (TextView) mMenuView.findViewById(R.id.tv_pz);
		tv_xc = (TextView) mMenuView.findViewById(R.id.tv_xc);

		cancel = (TextView) mMenuView.findViewById(R.id.tv_cancel);
		tv_pz.setOnClickListener(itemsOnClick);
		tv_xc.setOnClickListener(itemsOnClick);

		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		// 璁剧疆SelectPicPopupWindow鐨刅iew
		this.setContentView(mMenuView);
		// 璁剧疆SelectPicPopupWindow寮瑰嚭绐椾綋鐨勫
		this.setWidth(LayoutParams.FILL_PARENT);
		// 璁剧疆SelectPicPopupWindow寮瑰嚭绐椾綋鐨勯珮
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 璁剧疆SelectPicPopupWindow寮瑰嚭绐椾綋鍙偣锟�
		this.setFocusable(true);
		// 璁剧疆SelectPicPopupWindow寮瑰嚭绐椾綋鍔ㄧ敾鏁堟灉
		this.setAnimationStyle(R.style.PopupAnimation);
		// 瀹炰緥鍖栦竴涓狢olorDrawable棰滆壊涓哄崐閫忔槑
		ColorDrawable dw = new ColorDrawable(0x33000000);
		// 璁剧疆SelectPicPopupWindow寮瑰嚭绐椾綋鐨勮儗锟�
		this.setBackgroundDrawable(dw);
		// mMenuView娣诲姞OnTouchListener鐩戝惉鍒ゆ柇鑾峰彇瑙﹀睆浣嶇疆濡傛灉鍦拷?鎷╂澶栭潰鍒欓攢姣佸脊鍑烘
		mMenuView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int height = mMenuView.findViewById(R.id.pop_layout).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});
	}
}
