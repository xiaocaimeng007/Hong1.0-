package com.hongbao5656.activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hongbao5656.R;
import com.hongbao5656.base.BaseActivity;
import com.hongbao5656.util.SPU;

import java.util.ArrayList;

/**
 * 引导页
 * @author dm
 */
public class GuideActivity extends BaseActivity {
	private ArrayList<View> pageViews;
	private ViewGroup main;
	private ViewPager dmviewPager;
	private ImageView[] mDotViews;
	private LinearLayout group;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		LayoutInflater inflater = getLayoutInflater();
		LayoutInflater inflater = LayoutInflater.from(this);
		pageViews = new ArrayList<View>();
		View help1 = inflater.inflate(R.layout.view_help1, null);
		pageViews.add(help1);
		View help2 = inflater.inflate(R.layout.view_help2, null);
		pageViews.add(help2);
		View help3 = inflater.inflate(R.layout.view_help3, null);
		pageViews.add(help3);
//		try{
		main = (ViewGroup)inflater.inflate(R.layout.activity_guide, null);
//		}
//		catch (Exception ex){
//			String msg=ex.getMessage();
//		}
		dmviewPager = (ViewPager)main.findViewById(R.id.guidePages0);
		group = (LinearLayout)main.findViewById(R.id.ll_dot);
		((TextView)main.findViewById(R.id.tv_skip)).setOnClickListener(new OnClickListener() {//跳过
			@Override
			public void onClick(View v) {
				SPU.setPreferenceValueBoolean(mContext, SPU.FIRST_START_APP, true, SPU.STORE_SETTINGS);
				openActivity(LoginActivity.class);
				overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
				finish();
			}
		});
		help3.findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {//立即体验
			@Override
			public void onClick(View v) {
				SPU.setPreferenceValueBoolean(mContext, SPU.FIRST_START_APP, true, SPU.STORE_SETTINGS);
				openActivity(LoginActivity.class);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				finish();
			}
		});
		setContentView(main);
		this.mContext = GuideActivity.this;
		initRecommendImgs();
		dmviewPager.setAdapter(new GuidePageAdapter());
	}

	protected void initRecommendImgs() {
		try {
			/* 将点点加入到ViewGroup中 */
			mDotViews = new ImageView[3];
			for (int i = 0; i < mDotViews.length; i++) {
				ImageView imageView = new ImageView(this);
				LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
				lp1.leftMargin = 25;
				imageView.setLayoutParams(lp1);
				mDotViews[i] = imageView;
				if (i == 0) {
					mDotViews[i].setImageResource(R.drawable.dot_red);
				} else {
					mDotViews[i].setImageResource(R.drawable.dot_white);
				}
				group.addView(imageView);
			}
			// 设置监听，主要是设置点点的背景
			dmviewPager.setOnPageChangeListener(new OnPageChangeListener() {
				@Override
				public void onPageSelected(int arg0) {
					setImageBackground(arg0);
				}
				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
				}
				@Override
				public void onPageScrollStateChanged(int arg0) {
				}
			});
			setImageBackground(0);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	/**
	 *  设置选中的tip的背景
	 * @param selectItems
	 */
	private void setImageBackground(int selectItems) {
		for (int i = 0; i < mDotViews.length; i++) {
			if (i == selectItems) {
				mDotViews[i].setImageResource(R.drawable.dot_red);
			} else {
				mDotViews[i].setImageResource(R.drawable.dot_white);
			}
		}
	}

	class GuidePageAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			return pageViews.size();
		}
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
		@Override
		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}
		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(pageViews.get(arg1));
		}
		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(pageViews.get(arg1));
			return pageViews.get(arg1);
		}
		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}
		@Override
		public Parcelable saveState() {
			return null;
		}
		@Override
		public void startUpdate(View arg0) {
		}
		@Override
		public void finishUpdate(View arg0) {
		}
	}
}
