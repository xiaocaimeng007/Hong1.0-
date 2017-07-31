package com.hongbao5656.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.example.aaron.library.MLog;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.hongbao5656.R;
import com.hongbao5656.amap.AMapUtil;
import com.hongbao5656.base.BaseActivity;
import com.hongbao5656.okhttp.HttpException;
import com.hongbao5656.util.Constants;
import com.hongbao5656.entity.MyLocation;
import com.hongbao5656.okhttp.HttpDataHandlerListener;
import com.hongbao5656.util.IMap;
import com.hongbao5656.util.ParamsService;
import com.hongbao5656.util.ResponseParams;
import com.hongbao5656.util.SU;
import com.hongbao5656.util.TU;
import com.hongbao5656.util.TimeUtils;
import com.hongbao5656.view.DatePickerPre;
import com.hongbao5656.view.TimePicker;
import com.squareup.okhttp.Request;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

import static com.hongbao5656.amap.AMapUtil.convertToLatLng;

public class TZDWeiActivity extends BaseActivity implements HttpDataHandlerListener,
        AMapLocationListener, GeocodeSearch.OnGeocodeSearchListener {
    private Context mContext;
    private RouteSearch mRouteSearch;
    private DriveRouteResult mDriveRouteResult;
    private BusRouteResult mBusRouteResult;
    private WalkRouteResult mWalkRouteResult;
    private LatLonPoint latLonPoint = new LatLonPoint(32.253003, 120.794001);//起点，
    //    经度：121.32404维度：31.323162地点
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
    private int btn_dj;
    private double lat1;
    private double mlong1;
    private double lat2;
    private double mlong2;
    private Button btn_location;
    private EditText et_tel;
    private TextView tv_location;
    private TextView tv_location_time;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private AMapLocation locaton;
    private GeocodeSearch geocoderSearch;
    private RelativeLayout leftBt;
    private String lasttime;
    private LinearLayout rl_super;
    private Button publish_emptytime_btn;
    private Button publish_emptytime_btn_end;
    private PopupWindow pw;
    private String selectDate, selectTime;
    private DatePickerPre dp_test;
    private TimePicker tp_test;
    private TextView tv_ok, tv_cancel;    //确定、取消button
    //选择时间与当前时间，用于判断用户选择的是否是以前的时间
    private int currentHour, currentMinute, currentDay, selectHour, selectMinute, selectDay;
    //整体布局
    private LinearLayout container;
    private Calendar calendar;
    private AMap aMap;
    private MapView mapView;
    private Marker regeoMarker;
    private LocationSource.OnLocationChangedListener mListener;
    private LatLng latLng;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_tzdwei);
        mContext = this.getApplicationContext();
        setView();
        setListener();
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(bundle);// 此方法必须重写
        init();
        setInputUI(findViewById(R.id.rl_super), this);
    }

    void setView() {
        calendar = Calendar.getInstance();
        container = (LinearLayout) findViewById(R.id.rl_super);
        et_tel = (EditText) findViewById(R.id.et_tel);
        tv_location = (TextView) findViewById(R.id.tv_location);
        tv_location_time = (TextView) findViewById(R.id.tv_location_time);
        btn_location = (Button) findViewById(R.id.btn_location);
        leftBt = (RelativeLayout) findViewById(R.id.leftBt);
        rl_super = (LinearLayout) findViewById(R.id.rl_super);

        publish_emptytime_btn = (Button) findViewById(R.id.publish_emptytime_btn);
        publish_emptytime_btn_end = (Button) findViewById(R.id.publish_emptytime_btn_end);
    }

    void setListener() {
        leftBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                TZDWeiActivity.this.finish();
            }
        });
        btn_location.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String tel = et_tel.getText().toString().trim();
                String starttime = publish_emptytime_btn.getText().toString();
                String endtime = publish_emptytime_btn_end.getText().toString();
                if (!SU.isEmpty(tel) && SU.isPhoneNumber(tel)) {
                    aMap.clear();
                    location(tel, starttime, endtime);
                } else {
                    TU.show(mContext, "请输入正确的手机号码");
                }
            }
        });

        publish_emptytime_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                View view = View.inflate(TZDWeiActivity.this, R.layout.dialog_select_time_pre, null);
                selectDate = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-"
                        + calendar.get(Calendar.DAY_OF_MONTH) + " ";
//                selectDate = calendar.get(Calendar.YEAR) + "年" + calendar.get(Calendar.MONTH) + "月"
//                        + calendar.get(Calendar.DAY_OF_MONTH) + "日"
//                        + DatePicker.getDayOfWeekCN(calendar.get(Calendar.DAY_OF_WEEK));
                //选择时间与当前时间的初始化，用于判断用户选择的是否是以前的时间，如果是，弹出toss提示不能选择过去的时间
                selectDay = currentDay = calendar.get(Calendar.DAY_OF_MONTH);
                selectMinute = currentMinute = calendar.get(Calendar.MINUTE);
                selectHour = currentHour = calendar.get(Calendar.HOUR_OF_DAY);

                selectTime = currentHour + ":" + ((currentMinute < 10) ? ("0" + currentMinute) : currentMinute);
//                selectTime = currentHour + "点" + ((currentMinute < 10) ? ("0" + currentMinute) : currentMinute) + "分";
                dp_test = (DatePickerPre) view.findViewById(R.id.dp_test);
                tp_test = (TimePicker) view.findViewById(R.id.tp_test);
                tv_ok = (TextView) view.findViewById(R.id.tv_ok);
                tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
                //设置滑动改变监听器
                dp_test.setOnChangeListener(dp_onchanghelistener);
                tp_test.setOnChangeListener(tp_onchanghelistener);
                pw = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
//				//设置这2个使得点击pop以外区域可以去除pop
                pw.setOutsideTouchable(true);
                pw.setBackgroundDrawable(new BitmapDrawable());

                //出现在布局底端
                pw.showAtLocation(container, 0, 0, Gravity.END);

                //点击确定
                tv_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        if (selectDay == currentDay) {    //在当前日期情况下可能出现选中过去时间的情况
                            if (selectHour > currentHour) {
                                Toast.makeText(getApplicationContext(), "不能选择未来的时间\n请重新选择", Toast.LENGTH_SHORT).show();
                            } else if ((selectHour == currentHour) && (selectMinute > currentMinute)) {
                                Toast.makeText(getApplicationContext(), "不能选择未来的时间\n        请重新选择", Toast.LENGTH_SHORT).show();
                            } else {
                                publish_emptytime_btn.setText(selectDate + selectTime);
                                Toast.makeText(getApplicationContext(), selectDate + selectTime, Toast.LENGTH_SHORT).show();
                                pw.dismiss();
                            }
                        } else {
                            publish_emptytime_btn.setText(selectDate + selectTime);
                            Toast.makeText(getApplicationContext(), selectDate + selectTime, Toast.LENGTH_SHORT).show();
                            pw.dismiss();
                        }
                    }
                });
                //点击取消
                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        pw.dismiss();
                    }
                });
            }
        });

        publish_emptytime_btn_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                View view = View.inflate(TZDWeiActivity.this, R.layout.dialog_select_time_pre, null);
                selectDate = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-"
                        + calendar.get(Calendar.DAY_OF_MONTH) + " ";
//                selectDate = calendar.get(Calendar.YEAR) + "年" + calendar.get(Calendar.MONTH) + "月"
//                        + calendar.get(Calendar.DAY_OF_MONTH) + "日"
//                        + DatePicker.getDayOfWeekCN(calendar.get(Calendar.DAY_OF_WEEK));
                //选择时间与当前时间的初始化，用于判断用户选择的是否是以前的时间，如果是，弹出toss提示不能选择过去的时间
                selectDay = currentDay = calendar.get(Calendar.DAY_OF_MONTH);
                selectMinute = currentMinute = calendar.get(Calendar.MINUTE);
                selectHour = currentHour = calendar.get(Calendar.HOUR_OF_DAY);

                selectTime = currentHour + ":" + ((currentMinute < 10) ? ("0" + currentMinute) : currentMinute);
//                selectTime = currentHour + "点" + ((currentMinute < 10) ? ("0" + currentMinute) : currentMinute) + "分";
                dp_test = (DatePickerPre) view.findViewById(R.id.dp_test);
                tp_test = (TimePicker) view.findViewById(R.id.tp_test);
                tv_ok = (TextView) view.findViewById(R.id.tv_ok);
                tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
                //设置滑动改变监听器
                dp_test.setOnChangeListener(dp_onchanghelistener);
                tp_test.setOnChangeListener(tp_onchanghelistener);
                pw = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
//				//设置这2个使得点击pop以外区域可以去除pop
                pw.setOutsideTouchable(true);
                pw.setBackgroundDrawable(new BitmapDrawable());

                //出现在布局底端
                pw.showAtLocation(container, 0, 0, Gravity.END);

                //点击确定
                tv_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        if (selectDay == currentDay) {    //在当前日期情况下可能出现选中过去时间的情况
                            if (selectHour > currentHour) {
                                Toast.makeText(getApplicationContext(), "不能选择未来的时间\n请重新选择", Toast.LENGTH_SHORT).show();
                            } else if ((selectHour == currentHour) && (selectMinute > currentMinute)) {
                                Toast.makeText(getApplicationContext(), "不能选择未来的时间\n请重新选择", Toast.LENGTH_SHORT).show();
                            } else {
                                publish_emptytime_btn_end.setText(selectDate + selectTime);
                                Toast.makeText(getApplicationContext(), selectDate + selectTime, Toast.LENGTH_SHORT).show();
                                pw.dismiss();
                            }
                        } else {
                            publish_emptytime_btn_end.setText(selectDate + selectTime);
                            Toast.makeText(getApplicationContext(), selectDate + selectTime, Toast.LENGTH_SHORT).show();
                            pw.dismiss();
                        }

                    }
                });

                //点击取消
                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        pw.dismiss();
                    }
                });
            }
        });
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            regeoMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            setUpMap();
        }
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        progDialog = new ProgressDialog(this);
    }

    /**
     * 显示进度条对话框
     */
    public void showDialog() {
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在获取地址");
        progDialog.show();
    }

    /**
     * 隐藏进度条对话框
     */
    public void dismissDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    /**
     * 响应逆地理编码
     */
    public void getAddress(final LatLonPoint latLonPoint) {
        showDialog();
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 400, GeocodeSearch.AMAP);
        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    }

    private String addressName;

    /**
     * 逆地理编码回调
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        dismissDialog();
        if (rCode == 1000) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                addressName = result.getRegeocodeAddress().getFormatAddress() + "附近";
                LatLng latLng1 = AMapUtil.convertToLatLng(latLonPoint);
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        latLng, 15));
                regeoMarker.setPosition(convertToLatLng(latLonPoint));
                tv_location.setText(addressName);
                tv_location_time.setText(lasttime);
                TU.show(TZDWeiActivity.this, addressName);
            } else {
                TU.show(TZDWeiActivity.this, R.string.no_result);
            }
        } else {
            TU.show(TZDWeiActivity.this, rCode);
        }
    }

    private Marker geoMarker;

    /**
     * 地理编码查询回调
     */
    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {
        dismissDialog();
        if (rCode == 1000) {
            if (result != null && result.getGeocodeAddressList() != null
                    && result.getGeocodeAddressList().size() > 0) {
                GeocodeAddress address = result.getGeocodeAddressList().get(0);
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        convertToLatLng(address.getLatLonPoint()), 15));
                geoMarker.setPosition(convertToLatLng(address.getLatLonPoint()));
                addressName = "经纬度值:" + address.getLatLonPoint() + "\n位置描述:"
                        + address.getFormatAddress();
                TU.show(TZDWeiActivity.this, addressName);
            } else {
                TU.show(TZDWeiActivity.this, R.string.no_result);
            }

        } else {
            TU.show(TZDWeiActivity.this, rCode);
        }
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.location_marker));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
//        aMap.setLocationSource( new LocationSource(this));// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // aMap.setMyLocationType()
    }

    /**
     * 激活定位
     */
    //@Override
    public void activate(LocationSource.OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 设置是否只定位一次,默认为false
            mLocationOption.setOnceLocation(true);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
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
        deactivate();
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


    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        this.locaton = amapLocation;
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                tv_location.setText(amapLocation.getAddress());
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                MLog.e("AmapErr", errText);
            }
        }
    }

    String StartDate = "";
    String End = "";

    void location(String tel, String StartDate, String EndDate) {


        sendPostRequest(
                Constants.locatonlast,
                new ParamsService().requestKV(
                        "phone", tel,
                        "StartDate", StartDate,
                        "EndDate", EndDate,
                        Constants.locatonlast),
                TZDWeiActivity.this, false);
    }

    @Override
    public void setHandlerPostDataSuccess(int connectionId, Object data) throws JSONException {
        ResponseParams<LinkedHashMap> iParams = jiexiData(data);
        if (connectionId == Constants.locatonlast) {
            List<LinkedHashMap> data2 = iParams.getData2();
            if (data2 == null || data2.toString().equals("[]")) {
                TU.show(mContext, "请确认对方是否开启定位！");
            } else {
                ArrayList<MyLocation> locations = (ArrayList<MyLocation>) IMap.getData2FromResponse(iParams, MyLocation.class);
//                    TU.show(this,"locations:"+locations.size());
                MyLocation vo = locations.get(0);
//                    TU.show(mContext, "对方定位成功");
                TU.show(mContext, "该号码定位成功:\n" +
                        vo.msg + "\n时间：" +
                        TimeUtils.getSureTime2("MM-dd HH:mm", vo.clienttime * 1000 + 16 * 60 * 60 * 1000) + "\n经度：" +
                        vo.longitude + "\n纬度：" + vo.latitude);
                LatLonPoint llp = new LatLonPoint(vo.latitude, vo.longitude);
//                    TU.show(this,"vo.latitude:"+vo.latitude+" vo.longitude:"+vo.latitude);
                this.latLonPoint = llp;
                lasttime = TimeUtils.getSureTime2("MM-dd HH:mm", vo.clienttime * 1000 + 16 * 60 * 60 * 1000);
                PolylineOptions po = new PolylineOptions();
                po.width(6);
                po.color(getResources().getColor(R.color.versionname));
                po.setDottedLine(true);

                //声明一个动画帧集合。
                ArrayList giflist = new ArrayList();
                //添加每一帧图片。
                giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.sd));
                giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.red));

                for (int i = locations.size() - 1; i >= 0; i--) {
                    MyLocation vb = locations.get(i);
                    LatLng latLng = new LatLng(vb.latitude, vb.longitude);
                    po.add(latLng);
                    if (0 == i) {
                        aMap.addMarker(new MarkerOptions()
                                .anchor(0.5f, 0.5f)
                                .position(latLng)
                                .title(TimeUtils.getSureTime2("MM-dd HH:mm", vb.clienttime * 1000 + 16 * 60 * 60 * 1000))
                                .snippet(vb.msg + "(" + vb.latitude + "," + vb.longitude + ")" + "[" + i + "]")
                                .icons(giflist)
                                .draggable(true)
                                .period(50)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    }

                    if (i > 0 && i < locations.size() - 1) {
                        aMap.addMarker(new MarkerOptions()
                                .anchor(0.5f, 0.5f)
                                .position(latLng)
                                .title(TimeUtils.getSureTime2("MM-dd HH:mm", vb.clienttime * 1000 + 16 * 60 * 60 * 1000))
                                .snippet(vb.msg + "(" + vb.latitude + "," + vb.longitude + ")" + "[" + i + "]")
                                .icons(giflist)
                                .draggable(true)
                                .period(50)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
                    }
//                        07-22 19:24:10.441 19092-19092/com.hongbao5656 I/保存一次位置：: 经度：121.32393维度：31.323189地点：上海市嘉定区蕴北路靠近鸿宝物流信息园时间：1469157850
//                        07-22 19:28:31.981 25960-25960/com.hongbao5656 I/保存一次位置：: 经度：121.32396维度：31.323195地点：上海市嘉定区蕴北路靠近鸿宝物流信息园时间：1469158111

//                        {"reporttime":1469215718000,"clienttime":1469158111,"longitude":121.324,"latitude":31.3232,"msg":"上海市嘉定区蕴北路靠近鸿宝物流信息园"},
//                       {"reporttime":1469215718000,"clienttime":1469157850,"longitude":121.324,"latitude":31.3232,"msg":"上海市嘉定区蕴北路靠近鸿宝物流信息园"},
                    if (locations.size() - 1 == i) {
                        aMap.addMarker(new MarkerOptions()
                                .anchor(0.5f, 0.5f)
                                .position(latLng)
                                .title(TimeUtils.getSureTime2("MM-dd HH:mm", vb.clienttime * 1000 + 16 * 60 * 60 * 1000))
                                .snippet(vb.msg + "(" + vb.latitude + "," + vb.longitude + ")" + "[" + i + "]")
                                .icons(giflist)
                                .draggable(true)
                                .period(50)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                    }
                }
                aMap.addPolyline(po);

                if (0.0 == llp.getLongitude() || 0.0 == llp.getLatitude()) {
                    TU.show(this, "位置获取异常，请稍后重试！");
                } else {
                    getAddress(llp);
                }

            }
        }
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

    //listeners
    DatePickerPre.OnChangeListener dp_onchanghelistener = new DatePickerPre.OnChangeListener() {
        @Override
        public void onChange(int year, int month, int day, int day_of_week) {
            selectDay = day;
            selectDate = year + "-" + (month + 1) + "-" + day + " ";
//            selectDate = year + "年" + month + "月" + day + "日" + DatePicker.getDayOfWeekCN(day_of_week);
        }
    };
    TimePicker.OnChangeListener tp_onchanghelistener = new TimePicker.OnChangeListener() {
        @Override
        public void onChange(int hour, int minute) {
            selectTime = hour + ":" + ((minute < 10) ? ("0" + minute) : minute);
//            selectTime = hour + "点" + ((minute < 10) ? ("0" + minute) : minute) + "分";
            selectHour = hour;
            selectMinute = minute;
        }
    };

}