package com.hongbao5656.service;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;

import com.example.aaron.library.MLog;
import com.hongbao5656.base.BaseService;
import com.hongbao5656.util.TU;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by xastdm on 2016/7/6 12:21.
 */
public class LocationServiceForGPS extends BaseService implements LocationListener{


    private LocationManager lm;
    private ArrayList<Location> storedLocations;

    @Override
    public void onCreate() {
        lm = (LocationManager)getSystemService(LOCATION_SERVICE);
        storedLocations = new ArrayList<Location>();

        TU.show(this,"LocationServiceForGPS onCreate");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startLocation();
        TU.show(this,"LocationServiceForGPS onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        lm.removeUpdates(this);
        TU.show(this,"LocationServiceForGPS onDestroy");
    }

//    public class LocationBinder extends Binder{
//        public LocationServiceForGPS getService(){
//            return LocationServiceForGPS.this;
//        }
//    }
//
//    private final IBinder binder = new LocationBinder();
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return binder;
//    }




    public void startLocation(){

        // 判断GPS是否正常启动
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            TU.show(this, "请手动开启GPS");
            // 返回开启GPS导航设置界面
//            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            getApplication().startActivityForResult(intent, 0);
            return;
        }

        // 查找到服务信息
        Criteria criteria = new Criteria();
        // 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
        // 设置是否要求速度
        criteria.setSpeedRequired(false);
        // 设置是否需要海拔信息
        criteria.setAltitudeRequired(false);
        // 设置是否需要方位信息
        criteria.setBearingRequired(false);
        // 设置是否允许运营商收费
        criteria.setCostAllowed(true);
        // 设置对电源的需求
        criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗
        // 为获取地理位置信息时设置查询条件
        String provider = lm.getBestProvider(criteria, true);
        TU.show(this, "GPS 获取方式：" + provider);
        MLog.i("GPS 获取方式：" + provider);

        // 获取位置信息
        // 如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER
//        Location location = lm.getLastKnownLocation(provider);
//        updateView(location);

        // 监听状态
        lm.addGpsStatusListener(listener);

        // 绑定监听，有4个参数
        // 参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种
        // 参数2，位置信息更新周期，单位毫秒
        // 参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息
        // 参数4，监听
        // 备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新

        // 1秒更新一次，或最小位移变化超过1米更新一次；
        // 注意：此处更新准确度非常低，推荐在service里面启动一个Thread，
        // 在run中sleep(10000);然后执行handler.sendMessage(),更新位置
        lm.requestLocationUpdates(provider, 1000 * 10, 0, this);
    }


    @Override
    public void onLocationChanged(Location location) {
        //http://www.aiuxian.com/article/p-1836803.html
        TU.show(this, "GPS 经度：" + location.getLongitude() + ",纬度：" + location.getLatitude());
        MLog.i("GPS 经度：" + location.getLongitude() + ",纬度：" + location.getLatitude());
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            //GPS状态为可见时
            case LocationProvider.AVAILABLE:
                TU.show(this, "当前GPS状态为可见状态");
                break;
            //GPS状态为服务区外时
            case LocationProvider.OUT_OF_SERVICE:
                TU.show(this, "当前GPS状态为服务区外状态");
                break;
            //GPS状态为暂停服务时
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                TU.show(this, "当前GPS状态为暂停服务状态");
                break;
        }
    }
    @Override
    public void onProviderEnabled(String provider) {
        TU.show(this, "GPS开启时触发");
        MLog.i("GPS开启时触发");
    }
    @Override
    public void onProviderDisabled(String provider) {
        TU.show(this, "GPS禁用时触发");
        MLog.i("GPS禁用时触发");
    }


    //状态监听
    GpsStatus.Listener listener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            switch (event) {
                //第一次定位
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    TU.show(LocationServiceForGPS.this, "第一次定位");
                    break;
                //卫星状态改变
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    TU.show(LocationServiceForGPS.this, "卫星状态改变");
                    //获取当前状态
                    GpsStatus gpsStatus = lm.getGpsStatus(null);
                    //获取卫星颗数的默认最大值
                    int maxSatellites = gpsStatus.getMaxSatellites();
                    //创建一个迭代器保存所有卫星
                    Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
                    int count = 0;
                    while (iters.hasNext() && count <= maxSatellites) {
                        GpsSatellite s = iters.next();
                        count++;
                    }
                    TU.show(LocationServiceForGPS.this, "搜索到：" + count + "颗卫星");
                    break;
                //定位启动
                case GpsStatus.GPS_EVENT_STARTED:
                    TU.show(LocationServiceForGPS.this, "定位启动");
                    break;
                //定位结束
                case GpsStatus.GPS_EVENT_STOPPED:
                    TU.show(LocationServiceForGPS.this, "定位结束");
                    break;
            }
        };
    };

}
