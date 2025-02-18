package com.hongbao5656.activity;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.services.weather.LocalDayWeatherForecast;
import com.amap.api.services.weather.LocalWeatherForecast;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearch.OnWeatherSearchListener;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.hongbao5656.R;
import com.hongbao5656.base.BaseActivity;
import com.hongbao5656.entity.Region;
import com.hongbao5656.util.SU;
import com.hongbao5656.util.TU;

public class WeatherSearchActivity extends BaseActivity implements OnWeatherSearchListener {
    private TextView forecasttv;
    private TextView reporttime1;
    private TextView reporttime2;
    private TextView weather;
    private TextView Temperature;
    private TextView wind;
    private TextView humidity;
    private WeatherSearchQuery mquery;
    private WeatherSearch mweathersearch;
    private LocalWeatherLive weatherlive; 
    private LocalWeatherForecast weatherforecast;
    private List<LocalDayWeatherForecast> forecastlist = null;
    private String cityname="上海市";//天气搜索的城市，可以写名称或adcode；
//    private EditText et_city;
    private Button btn_search;
    private Button btn_city;
    private TextView city;
    private Context mContext;
    private Region shengfrom;
    private Region shifrom;
    private Region xianqufrom;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.weather_activity);
            this.mContext = WeatherSearchActivity.this;
            init();  
        }
        private void init() {
            ((RelativeLayout)findViewById(R.id.leftBt)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
//            et_city = (EditText)findViewById(R.id.et_city);
            city =(TextView)findViewById(R.id.city);

            forecasttv=(TextView)findViewById(R.id.forecast);
            reporttime1 = (TextView)findViewById(R.id.reporttime1);
            reporttime2 = (TextView)findViewById(R.id.reporttime2);
            weather = (TextView)findViewById(R.id.weather);
            Temperature = (TextView)findViewById(R.id.temp);
            wind=(TextView)findViewById(R.id.wind);
            humidity = (TextView)findViewById(R.id.humidity);
            btn_search = (Button)findViewById(R.id.btn_search);
            btn_city = (Button)findViewById(R.id.btn_city);
            btn_city.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, SelectPlaceActivity.class);
                    intent.putExtra("type", 1);
                    intent.putExtra("shengfrom", shengfrom);
                    intent.putExtra("shifrom", shifrom);
                    intent.putExtra("xianqufrom", xianqufrom);
                    startActivityForResult(intent, 100);
                }
            });
            btn_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!SU.isEmpty(btn_city.getText().toString())) {
                        cityname = btn_city.getText().toString();
                        forecasttv.setText("");
                        reporttime1.setText("");
                        reporttime2.setText("");
                        weather.setText("");
                        Temperature.setText("");
                        wind.setText("");
                        humidity.setText("");
                        searchliveweather();
                        searchforcastsweather();
                    }
                }
            });
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == 100) {
            if (data != null) {
                Bundle bundle = data.getExtras();
//                info.cityfrom = bundle.getInt("didianId");
                shengfrom = (Region) bundle.getSerializable("sheng");
                shifrom = (Region) bundle.getSerializable("shi");
                xianqufrom = (Region) bundle.getSerializable("xianqu");
                btn_city.setText(bundle.getString("weather"));
            }
        }
        /*else if (requestCode == 200) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                info.cityto = bundle.getInt("didianId");
                shengto = (Region) bundle.getSerializable("sheng");
                shito = (Region) bundle.getSerializable("shi");
                btn_endsation.setText(bundle.getString("didian"));
            }
        }*/
        super.onActivityResult(requestCode, resultCode, data);
    }



    private void searchforcastsweather() {
            mquery = new WeatherSearchQuery(cityname, WeatherSearchQuery.WEATHER_TYPE_FORECAST);//检索参数为城市和天气类型，实时天气为1、天气预报为2
            mweathersearch=new WeatherSearch(this);
            mweathersearch.setOnWeatherSearchListener(this);
            mweathersearch.setQuery(mquery);
            mweathersearch.searchWeatherAsyn(); //异步搜索   
        }
        private void searchliveweather() {
            mquery = new WeatherSearchQuery(cityname, WeatherSearchQuery.WEATHER_TYPE_LIVE);//检索参数为城市和天气类型，实时天气为1、天气预报为2
            mweathersearch=new WeatherSearch(this);
            mweathersearch.setOnWeatherSearchListener(this);
            mweathersearch.setQuery(mquery);
            mweathersearch.searchWeatherAsyn(); //异步搜索   
        }
        /**
         * 实时天气查询回调
         */
        @Override
        public void onWeatherLiveSearched(LocalWeatherLiveResult weatherLiveResult ,int rCode) {
            if (rCode == 1000) {
                if (weatherLiveResult != null && weatherLiveResult.getLiveResult() != null) {
                    weatherlive = weatherLiveResult.getLiveResult();
                    reporttime1.setText(weatherlive.getReportTime()+"发布");
                    weather.setText(weatherlive.getWeather());
                    Temperature.setText(weatherlive.getTemperature()+"°");
                    wind.setText(weatherlive.getWindDirection()+"风     "+weatherlive.getWindPower()+"级");
                    humidity.setText("湿度         "+weatherlive.getHumidity()+"%");
                    city.setText(btn_city.getText().toString());
                }else {
                     TU.show(WeatherSearchActivity.this, R.string.no_result);
                }
            }else {
                TU.show(WeatherSearchActivity.this, rCode);
            } 
        }




        /**
         * 天气预报查询结果回调
         * */
        @Override
        public void onWeatherForecastSearched(
                LocalWeatherForecastResult weatherForecastResult,int rCode) {
            if (rCode == 1000) {
                if (weatherForecastResult!=null && weatherForecastResult.getForecastResult()!=null
                        && weatherForecastResult.getForecastResult().getWeatherForecast()!=null
                        && weatherForecastResult.getForecastResult().getWeatherForecast().size()>0) {
                     weatherforecast = weatherForecastResult.getForecastResult();
                     forecastlist= weatherforecast.getWeatherForecast();
                     fillforecast();
                     
                }else {
                    TU.show(WeatherSearchActivity.this, R.string.no_result);
                }
            }else {
                TU.show(WeatherSearchActivity.this, rCode);
            } 
        }
        private void fillforecast() {
            reporttime2.setText(weatherforecast.getReportTime()+"发布");
            String forecast="";
            for (int i = 0; i < forecastlist.size(); i++) {
                LocalDayWeatherForecast localdayweatherforecast=forecastlist.get(i);
                String week = null;
                switch (Integer.valueOf(localdayweatherforecast.getWeek())) {
                case 1:
                    week = "周一";
                    break;
                case 2:
                    week = "周二";
                    break;
                case 3:
                    week = "周三";
                    break;
                case 4:
                    week = "周四";
                    break;
                case 5:
                    week = "周五";
                    break;
                case 6:
                    week = "周六";
                    break;
                case 7:
                    week = "周日";
                    break;
                default:
                    break;
                }
                String temp =String.format("%-3s/%3s", 
                        localdayweatherforecast.getDayTemp()+"°",
                        localdayweatherforecast.getNightTemp()+"°");
                String date = localdayweatherforecast.getDate();
                forecast+=date+"  "+week+"                       "+temp+"\n\n";          
            }
            forecasttv.setText(forecast);
        }  
}
