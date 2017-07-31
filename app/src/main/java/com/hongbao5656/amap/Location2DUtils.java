package com.hongbao5656.amap;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.MyLocationStyle;
import com.hongbao5656.R;

/**
 * 高德定位服务包含GPS和网络定位（Wi-Fi和基站定位）两种能力。
 * 定位SDK将GPS、网络定位能力进行了封装，以三种定位模式对外开放
 高精度定位模式：会同时使用网络定位和GPS定位，优先返回最高精度的定位结果；

 低功耗定位模式：不会使用GPS，只会使用网络定位（Wi-Fi和基站定位）；

 仅用设备定位模式：不需要连接网络，只使用GPS进行定位，这种模式下不支持室内环境的定位。
 // * LocationSource 控制是否显示系统小圆点
 * AMapLocationListener 监听地理位置发生改变/定位回调的监听器
 * @author HUAJUN
 *
 */
public class Location2DUtils implements AMapLocationListener, LocationSource {

	public static Location2DUtils lUtils;

	/**
	 * LocationSource.OnLocationChangedListener
	 */
	private LocationSource.OnLocationChangedListener mListener;
	/**
	 * 自定义的位置更新成功接口
	 */
	private OnLocationSuccessCallback locationCallback;
	/**
	 * 初始化定位客户端，设置监听
	 注：请在主线程中声明AMapLocationClient类对象，
	 需要传Context类型的参数。推荐用getApplicationConext()方法获取全进程有效的context。
	 */
	private AMapLocationClient mlocationClient;
	private AMapLocationClientOption mLocationOption;
	private Context context;

	public static Location2DUtils init(Context context,
									   OnLocationSuccessCallback locationCallback) {
		if (lUtils == null) {
			lUtils = new Location2DUtils(context, locationCallback);
		}
		return lUtils;
	}

	private Location2DUtils(Context context,
							OnLocationSuccessCallback locationCallback) {
		this.context = context;
		this.locationCallback = locationCallback;
		startLocation();
	}

	public static Location2DUtils init(Context context,
									   OnLocationSuccessCallback locationCallback, AMap aMap) {
		if (lUtils == null) {
			lUtils = new Location2DUtils(context, locationCallback, aMap);
		}
		return lUtils;
	}

	private Location2DUtils(Context context,
							OnLocationSuccessCallback locationCallback, AMap aMap) {
		this.context = context;
		this.locationCallback = locationCallback;
		setUpMap(aMap);
	}

	/**
	 * 定位
	 */
	public void startLocation() {
		if (mlocationClient == null) {
			// 初始化定位客户端
			mlocationClient = new AMapLocationClient(context);
			// 设置定位回调监听
			mlocationClient.setLocationListener(this);
			// 初始化定位参数
			mLocationOption = new AMapLocationClientOption();
			// 设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
			mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
			// 设置是否返回地址信息（默认返回地址信息）
			mLocationOption.setNeedAddress(true);
			// 设置是否只定位一次,默认为false
			mLocationOption.setOnceLocation(false);
			// 设置是否强制刷新WIFI，默认为强制刷新
			mLocationOption.setWifiActiveScan(true);
			// 设置是否允许模拟位置,默认为false，不允许模拟位置
			mLocationOption.setMockEnable(false);
			// 设置定位间隔,单位毫秒,默认为2000ms
			mLocationOption.setInterval(2000);
			// 给定位客户端对象设置定位参数
			mlocationClient.setLocationOption(mLocationOption);
		}
		// 启动定位
		mlocationClient.startLocation();
	}

	/**
	 * 设置一些amap的属性
	 */
	public void setUpMap(AMap aMap) {
		// 自定义系统定位按钮 小蓝点
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(
				BitmapDescriptorFactory.fromResource(R.drawable.location_marker));// 设置小蓝点的图标
		myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
		myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
		// myLocationStyle.anchor(int,int)//设置小蓝点的锚点
		myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
		aMap.setMyLocationStyle(myLocationStyle);

		aMap.setLocationSource(this);//设置定位监听
		aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
	}

	/**
	 * 销毁地图资源
	 */
	public static void deactivateMap() {
		if (lUtils != null) {
			lUtils.deactivate();
		}
	}

	/**
	 * AMapLocationListener接口只有onLocationChanged方法可以实现，
	 * 用于接收异步返回的定位结果，参数是AMapLocation类型
	 * 定位成功后回调函数
	 */
	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		locationCallback.onLocationSuccess(amapLocation);
		if (mListener != null && amapLocation != null) {
			if (amapLocation != null && amapLocation.getErrorCode() == 0) {
				//定位成功回调信息，设置相关消息
				mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
			} else {
				////显示错误信息  ErrCode是错误码，errInfo是错误信息，详见错误码表。
				String errText = "定位失败," + amapLocation.getErrorCode() + ": "
						+ amapLocation.getErrorInfo();
				Log.e("AmapErr", errText);
			}
		}
	}

	/**
	 * 激活定位
	 */
	@Override
	public void activate(LocationSource.OnLocationChangedListener arg0) {
		mListener = arg0;
		startLocation();
	}

	/**
	 * 停止定位
	 */
	@Override
	public void deactivate() {
		mListener = null;
		if (mlocationClient != null) {
			//停止定位
			mlocationClient.stopLocation();
			//销毁定位客户端 销毁定位客户端之后，若要重新开启定位请重新New一个AMapLocationClient对象
			mlocationClient.onDestroy();
		}
		mlocationClient = null;
	}

}
