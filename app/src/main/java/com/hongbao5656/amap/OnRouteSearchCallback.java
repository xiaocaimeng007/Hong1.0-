package com.hongbao5656.amap;

import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.WalkRouteResult;

/**
 * 获取路线回调接口 2015/11/30
 * 
 * @author huajun
 * 
 */
public interface OnRouteSearchCallback {
	/*
	 * 驾车回调
	 */
	void onDriveSearch(DriveRouteResult result, int rCode);

	/*
	 * 公交回调
	 */
	void onBusSearch(BusRouteResult result, int rCode);

	/*
	 * 步行回调
	 */
	void onWalkSearch(WalkRouteResult result, int rCode);
}
