package com.hongbao5656.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.LocationSource;

import com.example.aaron.library.MLog;
import com.hongbao5656.base.App;
import com.hongbao5656.base.BaseService;
import com.hongbao5656.okhttp.HttpException;
import com.hongbao5656.util.Constants;
import com.hongbao5656.db.DBHelper;
import com.hongbao5656.entity.MyLocation;
import com.hongbao5656.okhttp.HttpDataHandlerListener;
import com.hongbao5656.receiver.AlarmReceiver;
import com.hongbao5656.util.ParamsService;
import com.hongbao5656.util.SPU;
import com.hongbao5656.util.SU;
import com.hongbao5656.util.TU;
import com.hongbao5656.util.TimeUtils;
import com.squareup.okhttp.Request;

import org.json.JSONException;

import java.util.ArrayList;

import static com.amap.api.location.AMapLocation.LOCATION_TYPE_AMAP;
import static com.amap.api.location.AMapLocation.LOCATION_TYPE_CELL;
import static com.amap.api.location.AMapLocation.LOCATION_TYPE_FIX_CACHE;
import static com.amap.api.location.AMapLocation.LOCATION_TYPE_GPS;
import static com.amap.api.location.AMapLocation.LOCATION_TYPE_OFFLINE;
import static com.amap.api.location.AMapLocation.LOCATION_TYPE_SAME_REQ;
import static com.amap.api.location.AMapLocation.LOCATION_TYPE_WIFI;

/**
 * 高德定位服务
 */
public class LocationServiceForGaoDe
        extends BaseService
        implements AMapLocationListener,
        LocationSource, HttpDataHandlerListener {

    //声明AMapLocationClient类对象，定位发起端
    private AMapLocationClient mLocationClient;
    //声明AMapLocationClientOption对象，定位参数
    private AMapLocationClientOption mLocationOption;
    //声明OnLocationChangedListener对象，定位监听器
    private LocationSource.OnLocationChangedListener mLocationChangedListener = null;
    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private boolean isFirstLoc = true;

    private double Latitude;
    private double Longitude;
    private long clienttime;
    private String msg;
    //DBHelper dbHelper = null;
//    private AlarmManager manager;
//    private PendingIntent pi;

    @Override
    public void onCreate() {
        MLog.i("onCreate 高德定位服务");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MLog.i("onStartCommand 高德定位服务");

        startLocation();

        //获取系统服务AlarmManager
//        manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        // 5分钟起一次闹钟去发定位广播
//        int anHour = 5 * 60 * 1000;
        //从开机到现在的毫秒值（手机睡眠(sleep)的时间也包括在内）
//        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
//        pi = PendingIntent.getBroadcast(this, 0, new Intent(this, AlarmReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
        //设置在某个时间执行闹钟
        //AlarmManager.ELAPSED_REALTIME_WAKEUP  设置闹钟时间从系统启动开始，如果设备休眠则唤醒 真实时间流逝
//        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);//只会执行一次的闹钟
//        manager.setRepeating(AlarmManager.RTC,triggerAtTime,60000,pi);//每五分钟起一次
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        startService(new Intent(this, LocationServiceForGaoDe.class));
//        stopForeground(true);
//        Intent intent = new Intent("com.service.xastdm.servicedestroyreceiver");
//        sendBroadcast(intent);
//        if (manager != null) {//销毁闹钟
//            manager.cancel(pi);
//        }
        if (mLocationClient != null) {
            mLocationClient.stopLocation();//停止定位
            mLocationClient.onDestroy();//销毁定位客户端
            //销毁定位客户端之后，若要重新开启定位请重新New一个AMapLocationClient对象
        }
        TU.show(this, "onDestroy 高德定位服务被销毁");
        MLog.i("onDestroy 高德定位服务被销毁");
    }

//    public class LocationBinder extends Binder {
//        public LocationServiceForGaoDe getService() {
//            return LocationServiceForGaoDe.this;
//        }
//    }
//
//    private final IBinder binder = new LocationBinder();
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return binder;
//    }

    /**
     * 定位
     */
    public void startLocation() {
        if (mLocationClient == null) {
            //获得Application以初始化定位客户端
            mLocationClient = new AMapLocationClient(getApplicationContext());
            // 设置定位监听 即按周期或者单次定位返回数据后回调的函数，我们也根据这句话获取需要的相关信息
            mLocationClient.setLocationListener(this);
            // 初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            // 设置定位模式
            // 高精度模式Hight_Accuracy   混合模式 会同时使用高德网络定位和GPS定位,优先返回精度高的定位
            // Battery_Saving为低功耗模式 只使用高德网络定位 省电模式
            // Device_Sensors是仅设备模式 只使用GPS定位

//            高德定位的高精度模式如何设置为GPS优先，
//            它默认是30秒内如果GPS没有返回数据，则调用网络定位，
//            怎么能设置GPS定位的时间呢，例如10秒内GPS没有返回数据，就用网络定位

            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            // 设置是否返回地址信息（默认返回地址信息）
            mLocationOption.setNeedAddress(true);
            /**
             * 设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
             * 注`这里写代码片`意：只有在高精度模式下的单次定位有效，其他方式无效
             */
            mLocationOption.setGpsFirst(true);
            // 设置是否只定位一次,默认为false
            mLocationOption.setOnceLocation(false);
            // 设置是否强制刷新WIFI，默认为强制刷新
            mLocationOption.setWifiActiveScan(true);
            // 设置是否允许模拟位置,默认为false，不允许模拟位置
            mLocationOption.setMockEnable(false);
            // 设置定位请求的间隔时间,单位毫秒,默认为2000ms
            //mLocationOption.setInterval(1000*10);//测试用
            mLocationOption.setInterval(1000 * 60 * 5);//3分钟定位一次
            //设置连接超时时间
            mLocationOption.setHttpTimeOut(1000 * 60 * 10);
            // 给定位客户端对象设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
        }
        // 启动定位
        mLocationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        if (location != null) {
            if (location.getErrorCode() == 0) {
                int locationtype = location.getLocationType();//获取当前定位结果来源，如网络定位结果，详见官方定位类型表
                Latitude = location.getLatitude();//31.323126  第一次是0.0
                Longitude = location.getLongitude();//121.323928 第一次是0.0
//              在setNeedAddress(true)的情况下网络定位结果中会有地址信息，GPS定位不返回地址信息
                msg = location.getAddress();//上海市嘉定区蕴北路靠近鸿宝物流信息园 第一次地址是空
                String city = location.getCity();
                SPU.setPreferenceValueString(this, SPU.CURRENTCITY, city.substring(0,city.length()-1), SPU.STORE_USERINFO);
                clienttime = TimeUtils.getUTCTime() / 1000;//1478468704
                //clienttime = location.getTime();
                String type = "";
                switch (locationtype){
                    case LOCATION_TYPE_GPS: type = "LOCATION_TYPE_GPS"; break;
                    case LOCATION_TYPE_SAME_REQ: type = "LOCATION_TYPE_SAME_REQ"; break;
                    case LOCATION_TYPE_FIX_CACHE: type = "LOCATION_TYPE_FIX_CACHE"; break;
                    case LOCATION_TYPE_WIFI: type = "LOCATION_TYPE_WIFI"; break;
                    case LOCATION_TYPE_CELL: type = "LOCATION_TYPE_CELL"; break;
                    case LOCATION_TYPE_AMAP: type = "LOCATION_TYPE_AMAP"; break;
                    case LOCATION_TYPE_OFFLINE: type = "LOCATION_TYPE_OFFLINE"; break;
                }

                MLog.i("高德定位：","来源" +type+ "经度：" + Longitude + "维度："
                        + Latitude + "城市："+city+"地点：" + location.getAddress() + "时间：" + clienttime);

                if (Latitude == 0.0 || Latitude == 0 || Longitude == 0.0 || Longitude == 0) {
                    MLog.i("无效的高德定位：", "经度：" + Longitude + "维度：" + Latitude + "地点：" + location.getAddress() + "时间：" + clienttime);
                    return;
                }

                double lastLongitude = Double.parseDouble(SPU.getPreferenceValueString(this, SPU.NEW_LAST_LONGITUDE, "0.0", SPU.STORE_USERINFO));//0.0
                double lastLatitude = Double.parseDouble(SPU.getPreferenceValueString(this, SPU.NEW_LAST_LATITUDE, "0.0", SPU.STORE_USERINFO));//0.0
                MLog.i("最后一次保存的位置：", "经度：" + lastLongitude + "维度：" + lastLatitude);

//                TU.show(this, "高德定位  经度：" + Longitude + "维度：" + Latitude + "地点：" + location.getAddress() + "时间：" + clienttime);
                if (lastLongitude != Longitude || lastLatitude != Latitude) {
//                    if (dbHelper == null) {
//                        dbHelper = DBHelper.getInstance(getApplicationContext());
//                    }
                    ContentValues values = new ContentValues();
                    values.put("Latitude", Latitude);
                    values.put("Longitude", Longitude);
                    values.put("clienttime", clienttime);
                    values.put("msg", msg);
                    //dbHelper.insertLocationDatas(values);

                    MLog.i("最新定位保存的位置：", "经度：" + Longitude + "维度：" + Latitude + "地点：" + msg + "时间：" + clienttime);
                    SPU.setPreferenceValueString(this, SPU.NEW_LAST_LONGITUDE, String.valueOf(Longitude), SPU.STORE_USERINFO);
                    SPU.setPreferenceValueString(this, SPU.NEW_LAST_LATITUDE, String.valueOf(Latitude), SPU.STORE_USERINFO);

                    ArrayList<MyLocation> locations = new ArrayList<MyLocation>();
                    MyLocation l = new MyLocation();
                    l.clienttime=clienttime;
                    l.msg=msg;
                    l.latitude=Latitude;
                    l.longitude=Longitude;
                    locations.add(l);
                    if (!SU.isEmpty(App.account) && !SU.isEmpty(App.token)) {
                        sendRequest(
                                Constants.location,
                                new ParamsService().locaton(locations),
                                this, false);
                    }

                  /*  if (dbHelper != null) {
                       // ArrayList<MyLocation> myLocations = dbHelper.queryLocationDatas(this);
                        MLog.i("判断已保存的坐标数量：", myLocations.size() + "");
                        if (myLocations.size() > 10) {
                            dbHelper.clearTableData(this);
                            return;
                        }
                        if (myLocations.size() >= 5) {
                            if (SU.isEmpty(App.account) || SU.isEmpty(App.token)) {
                                App.token = SPU.getPreferenceValueString(this, SPU.EQ_tokenId, "", SPU.STORE_USERINFO);
                                App.account = SPU.getPreferenceValueString(this, SPU.ACCOUNT, "", SPU.STORE_USERINFO);
                            }

                            if (!SU.isEmpty(App.account) && !SU.isEmpty(App.token)) {
                                sendRequest(
                                        Constants.location,
                                        new ParamsService().locaton(myLocations),
                                        this, false);
                            }
                        }
                    }*/
                }

            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                MLog.e("AmapError", "location Error, ErrCode:"
                        + location.getErrorCode() + ", errInfo:"
                        + location.getErrorInfo());
                //TU.show(getApplicationContext(), "定位失败");

//                if(4==location.getErrorCode()&&"网络连接异常".equals(location.getErrorInfo())){
//                    startService(new Intent(this, LocationServiceForGPS.class));
//                }
            }
        }

    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mLocationChangedListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mLocationChangedListener = null;
    }

    @Override
    public void setHandlerPostDataSuccess(int connectionId, Object data) throws JSONException {
       /* if (connectionId == Constants.location) {
            if (dbHelper != null) {
                dbHelper.clearTableData(this);
                MLog.i("定位发送成功后清理一次数据表:" + dbHelper.queryLocationDatas(this).size());
            }
        }*/
    }

    @Override
    public void setHandlerPostDataError(int connectedId, Request request, HttpException httpException, String params, String content) throws JSONException {

    }

    @Override
    public void setHandlerGetDataSuccess(int connectionId, Object data) throws JSONException {

    }

    @Override
    public void setHandlerGetDataError(int connectionId, Object data) throws JSONException {

    }

}
