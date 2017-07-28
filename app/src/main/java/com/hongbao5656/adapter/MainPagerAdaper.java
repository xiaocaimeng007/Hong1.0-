package com.hongbao5656.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 *（继承自FragmentPagerAdapter）的自定义ViewPager适配器 2013/11/23
 *
 * @author dm
 *
 */
public class MainPagerAdaper extends FragmentPagerAdapter {
	private ArrayList<Fragment> fragments;

	public MainPagerAdaper(FragmentManager fm) {
		super(fm);
	}

	public MainPagerAdaper(FragmentManager fm, ArrayList<Fragment> fragments) {
		super(fm);
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(int arg0) {
		return fragments.get(arg0);
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

}
