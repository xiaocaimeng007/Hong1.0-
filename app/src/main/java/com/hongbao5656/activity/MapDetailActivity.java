package com.hongbao5656.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.BusRouteQuery;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.RouteSearch.WalkRouteQuery;
import com.amap.api.services.route.WalkRouteResult;
import com.hongbao5656.R;
import com.hongbao5656.amap.AMapUtil;
import com.hongbao5656.util.TU;

public class MapDetailActivity extends Activity implements  OnRouteSearchListener, AMap.OnMapClickListener, AMap.OnInfoWindowClickListener, AMap.InfoWindowAdapter, AMap.OnMarkerClickListener {
	private Context mContext;
	private RouteSearch mRouteSearch;
	private DriveRouteResult mDriveRouteResult;
	private BusRouteResult mBusRouteResult;
	private WalkRouteResult mWalkRouteResult;
	private LatLonPoint mStartPoint = new LatLonPoint(39.917636, 116.397743);//起点，
	private LatLonPoint mEndPoint = new LatLonPoint(31.231706, 121.472644);//终点，
	private String mCurrentCityName = "北京";
	private final int ROUTE_TYPE_BUS = 1;
	private final int ROUTE_TYPE_DRIVE = 2;
	private final int ROUTE_TYPE_WALK = 3;

	private LinearLayout mBusResultLayout;
	private RelativeLayout mBottomLayout;
	private TextView mRotueTimeDes, mRouteDetailDes;
	private ImageView mBus;
	private ImageView mDrive;
	private ImageView mWalk;
	private ListView mBusResultList;
	private ProgressDialog progDialog = null;// 搜索时进度条
	private String btn_startsation;
	private String btn_endsation;
	private double btn_dj;
	private double lat1;
	private double mlong1;
	private double lat2;
	private double mlong2;
	private MapView mapView;
	private AMap aMap;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_map_details2);
		btn_startsation = getIntent().getStringExtra("btn_startsation");
		btn_endsation = getIntent().getStringExtra("btn_endsation");
		btn_dj = Double.parseDouble(getIntent().getStringExtra("btn_dj"));
		lat1 = getIntent().getDoubleExtra("lat1",0);
		mlong1 = getIntent().getDoubleExtra("mlong1",0);
		lat2 = getIntent().getDoubleExtra("lat2",0);
		mlong2 = getIntent().getDoubleExtra("mlong2",0);
		mStartPoint = new LatLonPoint(lat1, mlong1);//起点，
		mEndPoint = new LatLonPoint(lat2, mlong2);//终点，
		mContext = this.getApplicationContext();
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(bundle);// 此方法必须重写
		init();
		//setfromandtoMarker();
	}

	/*private void setfromandtoMarker() {
		aMap.addMarker(new MarkerOptions()
				.position(AMapUtil.convertToLatLng(mStartPoint))
				.union(BitmapDescriptorFactory.fromResource(R.drawable.start)));
		aMap.addMarker(new MarkerOptions()
				.position(AMapUtil.convertToLatLng(mEndPoint))
				.union(BitmapDescriptorFactory.fromResource(R.drawable.end)));
	}*/

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		findViewById(R.id.leftBt).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MapDetailActivity.this.finish();
			}
		});
		if (aMap == null) {
			aMap = mapView.getMap();
//			aMap.setMapType(AMap.MAP_TYPE_SATELLITE);//设置为卫星地图
//			aMap.setMapType(AMap.MAP_TYPE_NORMAL);
		}

		registerListener();
		mRouteSearch = new RouteSearch(this);
		mRouteSearch.setRouteSearchListener(this);
		mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
		searchRouteResult(ROUTE_TYPE_DRIVE, RouteSearch.DrivingDefault);
		mRotueTimeDes = (TextView) findViewById(R.id.firstline);
		mRouteDetailDes = (TextView) findViewById(R.id.secondline);
		mapView.setVisibility(View.VISIBLE);
//		mBusResultLayout.setVisibility(View.GONE);
	}

	/**
	 * 注册监听
	 */
	private void registerListener() {
		aMap.setOnMapClickListener(MapDetailActivity.this);
		aMap.setOnMarkerClickListener(MapDetailActivity.this);
		aMap.setOnInfoWindowClickListener(MapDetailActivity.this);
		aMap.setInfoWindowAdapter(MapDetailActivity.this);
	}

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
	public View getInfoContents(Marker arg0) {
		return null;
	}


	@Override
	public void onMapClick(LatLng arg0) {
	}

	/**
	 * 开始搜索路径规划方案
	 */
	public void searchRouteResult(int routeType, int mode) {
		if (mStartPoint == null) {
			TU.show(mContext, "定位中，稍后再试...");
			return;
		}
		if (mEndPoint == null) {
			TU.show(mContext, "终点未设置");
		}
		showProgressDialog();
		final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
				mStartPoint, mEndPoint);
		if (routeType == ROUTE_TYPE_BUS) {// 公交路径规划
			BusRouteQuery query = new BusRouteQuery(fromAndTo, mode,
					mCurrentCityName, 0);// 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
			mRouteSearch.calculateBusRouteAsyn(query);// 异步路径规划公交模式查询
		} else if (routeType == ROUTE_TYPE_DRIVE) {// 驾车路径规划
			DriveRouteQuery query = new DriveRouteQuery(fromAndTo, mode, null,
					null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
			mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
		} else if (routeType == ROUTE_TYPE_WALK) {// 步行路径规划
			WalkRouteQuery query = new WalkRouteQuery(fromAndTo, mode);
			mRouteSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
		}
	}

	@Override
	public void onBusRouteSearched(BusRouteResult result, int errorCode) {
		dissmissProgressDialog();
		mBottomLayout.setVisibility(View.GONE);
		aMap.clear();// 清理地图上的所有覆盖物
//		if (errorCode == 1000) {
//			if (result != null && result.getPaths() != null) {
//				if (result.getPaths().size() > 0) {
//					mBusRouteResult = result;
//					BusResultListAdapter mBusResultListAdapter = new BusResultListAdapter(mContext, mBusRouteResult);
//					mBusResultList.setAdapter(mBusResultListAdapter);		
//				} else if (result != null && result.getPaths() == null) {
//					ToastUtil.show(mContext, R.string.no_result);
//				}
//			} else {
//				ToastUtil.show(mContext, R.string.no_result);
//			}
//		} else {
//			ToastUtil.showerror(this.getApplicationContext(), errorCode);
//		}
	}

	@Override
	public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
		dissmissProgressDialog();
		aMap.clear();// 清理地图上的所有覆盖物
		if (errorCode == 1000) {
			if (result != null && result.getPaths() != null) {
				if (result.getPaths().size() > 0) {
					mDriveRouteResult = result;
					final DrivePath drivePath = mDriveRouteResult.getPaths()
							.get(0);
					/*DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
							this, aMap, drivePath,
							mDriveRouteResult.getStartPos(),
							mDriveRouteResult.getTargetPos());
					drivingRouteOverlay.removeFromMap();
					drivingRouteOverlay.addToMap();
					drivingRouteOverlay.zoomToSpan();*/
					mBottomLayout.setVisibility(View.VISIBLE);
					int dis = (int) drivePath.getDistance();//1409694
					int dur = (int) drivePath.getDuration();
					String des = AMapUtil.getFriendlyTime(dur)+"("+AMapUtil.getFriendlyLength(dis)+")";
					mRotueTimeDes.setText(btn_startsation+"->"+btn_endsation+" 约"+des);
					mRouteDetailDes.setVisibility(View.VISIBLE);
//					int taxiCost = (int) mDriveRouteResult.getTaxiCost();
					mRouteDetailDes.setText("费用约"+btn_dj*dis/1000+"元");
					mBottomLayout.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mContext,DriveRouteDetailActivity.class);
							intent.putExtra("drive_path", drivePath);
							intent.putExtra("drive_result",mDriveRouteResult);
							startActivity(intent);
						}
					});
				} else if (result != null && result.getPaths() == null) {
					TU.show(mContext, R.string.no_result);
				}


			} else {
				TU.show(mContext, R.string.no_result);
			}
		} else {
			TU.show(this.getApplicationContext(), errorCode);
		}


	}

	@Override
	public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {
		dissmissProgressDialog();
		aMap.clear();// 清理地图上的所有覆盖物
//		if (errorCode == 1000) {
//			if (result != null && result.getPaths() != null) {
//				if (result.getPaths().size() > 0) {
//					mWalkRouteResult = result;
//					final WalkPath walkPath = mWalkRouteResult.getPaths()
//							.get(0);
//					WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(
//							this, aMap, walkPath,
//							mWalkRouteResult.getStartPos(),
//							mWalkRouteResult.getTargetPos());
//					walkRouteOverlay.removeFromMap();
//					walkRouteOverlay.addToMap();
//					walkRouteOverlay.zoomToSpan();
//					mBottomLayout.setVisibility(View.VISIBLE);
//					int dis = (int) walkPath.getDistance();
//					int dur = (int) walkPath.getDuration();
//					String des = AMapUtil.getFriendlyTime(dur)+"("+AMapUtil.getFriendlyLength(dis)+")";
//					mRotueTimeDes.setText(des);
//					mRouteDetailDes.setVisibility(View.GONE);
//					mBottomLayout.setOnClickListener(new OnClickListener() {
//						@Override
//						public void onClick(View v) {
//							Intent intent = new Intent(mContext,
//									WalkRouteDetailActivity.class);
//							intent.putExtra("walk_path", walkPath);
//							intent.putExtra("walk_result",
//									mWalkRouteResult);
//							startActivity(intent);
//						}
//					});
//				} else if (result != null && result.getPaths() == null) {
//					ToastUtil.show(mContext, R.string.no_result);
//				}
//
//			} else {
//				ToastUtil.show(mContext, R.string.no_result);
//			}
//		} else {
//			ToastUtil.showerror(this.getApplicationContext(), errorCode);
//		}
	}

	@Override
	public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

	}


	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = new ProgressDialog(this);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setMessage("正在搜索");
		progDialog.show();
	}

	/**
	 * 隐藏进度框
	 */
	private void dissmissProgressDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}
