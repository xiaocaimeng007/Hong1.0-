package com.hongbao5656.util;

import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.example.aaron.library.MLog;
import com.google.common.collect.Lists;
import com.hongbao5656.base.App;
import com.hongbao5656.entity.CityTo;
import com.hongbao5656.entity.MyLocation;
import com.hongbao5656.entity.OffenLine;
import com.hongbao5656.entity.SupplyDetailEdite;

import java.util.List;
import java.util.TreeMap;

/**
 * 组装接口参数bean以JSON形式的字符串返回
 */
public class ParamsService {
    /**
     * 请求条件
     */
    private TreeMap<String, Object> data1 = new TreeMap<String, Object>();
    /**
     * 集合装对象
     */
    private List data2 = Lists.newArrayList();

    @NonNull
    private String getParamsJsonString(TreeMap<String, Object> data1, int interfacecode) {
        RequestParams data = new RequestParams();
        data.setTerminal(1);
        data.setClient("android");
        data.setToken(App.token);
        data.setAccount(App.account);
        data.setData1(data1);
        data.setInterfacecode(interfacecode);
        return JSON.toJSONString(data);
    }

    @NonNull
    private String getParamsJsonString(int interfacecode) {
        RequestParams data = new RequestParams();
        data.setTerminal(1);
        data.setClient("android");
        data.setToken(App.token);
        data.setAccount(App.account);
        data.setInterfacecode(interfacecode);
        return JSON.toJSONString(data);
    }

    /**
     * 不带参请求
     */
    public String request(int interfacecode) {
        return getParamsJsonString(interfacecode);
    }

    public String requestKV(String keyname, String keyvalue,
                            int interfacecode) {
        data1.put(keyname, keyvalue);
        return getParamsJsonString(data1, interfacecode);
    }


    public String requestKV(String keyname, String keyvalue,
                            String keyname2, int keyvalue2,
                            int interfacecode) {
        data1.put(keyname, keyvalue);
        data1.put(keyname2, keyvalue2);
        return getParamsJsonString(data1, interfacecode);
    }

    public String requestKV(String keyname, String keyvalue,
                            String keyname2, String keyvalue2,
                            int interfacecode) {
        data1.put(keyname, keyvalue);
        data1.put(keyname2, keyvalue2);
        return getParamsJsonString(data1, interfacecode);
    }

    public String requestKV(String keyname, String keyvalue,
                            String keyname2, String keyvalue2,
                            String keyname3, String keyvalue3,
                            int interfacecode) {
        data1.put(keyname, keyvalue);
        data1.put(keyname2, keyvalue2);
        data1.put(keyname3, keyvalue3);
        return getParamsJsonString(data1, interfacecode);
    }

    public String requestKV(String keyname, int keyvalue,
                            String keyname2, int keyvalue2,
                            int interfacecode) {
        data1.put(keyname, keyvalue);
        data1.put(keyname2, keyvalue2);
        return getParamsJsonString(data1, interfacecode);
    }

    public String requestKV(String keyname, String keyvalue,
                            String keyname2, int keyvalue2,
                            String keyname3, int keyvalue3,
                            int interfacecode) {
        data1.put(keyname, keyvalue);
        data1.put(keyname2, keyvalue2);
        data1.put(keyname3, keyvalue3);
        return getParamsJsonString(data1, interfacecode);
    }

    /**
     * 货主认证
     */
    public String hzyz(String username, String realname, String cardID, String datas_realphoto, String datas_sfzzm, String datas_sfzfm, String phone) {
        data1.put("username", username);
        data1.put("companyname", realname);
        data1.put("companyaddress", cardID);
        data1.put("yingyezhizhao", datas_realphoto);
        data1.put("imagecid1", datas_sfzzm);
        data1.put("shuiwudengjizheng", datas_sfzfm);
        data1.put("phone", phone);
        return getParamsJsonString(data1, Constants.hzyz);
    }

    /**
     * 个人认证
     */
    public String grzz(String realname, String datas_realphoto, String datas_sfzzm, String phone) {
        data1.put("username", realname);
        data1.put("imagehead", datas_realphoto);
        data1.put("imagecid1", datas_sfzzm);
        data1.put("phone", phone);
        return getParamsJsonString(data1, Constants.grzz);
    }

    /**
     * 司机认证
     */
    public String sjyz(String username, String datas_sfzzm,
                       String cph, String cc, String cx,
                       String datas_jsz, String datas_xszzm, String datas_clz, String phone) {
        data1.put("username", username);
        data1.put("carno", cph);
        data1.put("carlen", cc);
        data1.put("cartype", cx);
        data1.put("driver", datas_jsz);
        data1.put("driving1", datas_xszzm);
        data1.put("carimage", datas_clz);
        data1.put("imagecid1", datas_sfzzm);
        data1.put("phone", phone);
        RequestParams data = new RequestParams();
        data.setTerminal(1);
        data.setClient("android");
        data.setToken(App.token);
        data.setAccount(App.account);
        data.setInterfacecode(Constants.sjyz);
        data.setData1(data1);
        return JSON.toJSONString(data);
    }

    /**
     * 注册
     */
    public String btn_register(String phone, String pwd, int type, String yzm, String imei, String which_area, String yqm) {
        data1.put("pwd", pwd);
        data1.put("phone", phone);
        data1.put("usertype", type);
        data1.put("yzm", yzm);
        data1.put("MIDMobile", imei);
        data1.put("CompanyID", which_area);
        data1.put("SourcePhone", yqm);
        return getParamsJsonString(data1, Constants.btn_register);
    }

    /**
     * 登录
     */
    public String btn_login(String phone, String pwd, String imei) {
        data1.put("pwd", pwd);
        data1.put("phone", phone);
        data1.put("DeviceMID", imei);
        data1.put("version", App.version);
        return getParamsJsonString(data1, Constants.btn_login);
    }

    /**
     * 发布货源
     */
    public String sendsupply(SupplyDetailEdite info, String isoften) {
        data1.put("goodsid", info.goodsid);
        data1.put("cityfrom", info.cityfrom);
        data1.put("cityto", info.cityto);
        data1.put("goodsname", info.goodsname);
        data1.put("carlen", info.carlen);
        data1.put("cartype", info.cartype);
        data1.put("wv", info.wv);
        data1.put("unit", info.unit);
        data1.put("contact", info.contact);
        data1.put("contactmobile", info.contactmobile);
        data1.put("remark", info.remark);
        data1.put("isoften", isoften);
        return getParamsJsonString(data1, Constants.sendsupply);
    }

    /**
     * 报价
     */
    public String btn_baojia(String baojiaid, String goodsid, float baojia, float qita, String beizhu, String chepaihao, String xingming, String phone) {
        data1.put("goodsid", goodsid);
        data1.put("baojiaid", baojiaid);
        data1.put("baojia", baojia);
        data1.put("qita", qita);
        data1.put("beizhu", beizhu);
        data1.put("chepaihao", chepaihao);
        data1.put("xingming", xingming);
        data1.put("phone", phone);
        return getParamsJsonString(data1, Constants.goodsbaojia);
    }

    /**
     * 报价
     */
    public String btn_baojiaXiangQing(String baojiaid) {

        data1.put("baojiaid", baojiaid);

        return getParamsJsonString(data1, Constants.goodsbaojiaInfo);
    }

    /**
     * 查找车源
     */
    public String seekcar(SupplyDetailEdite info, int page) {
        data1.put("cityfrom", info.cityfrom);
        data1.put("cityto", info.cityto);
        data1.put("pageindex", page);
        data1.put("pagesize", Constants.page);
        data1.put("carlen", info.carlen);
        data1.put("cartype", info.cartype);
        return getParamsJsonString(data1, Constants.seekcar);
    }

    /**
     * 查找我的熟车
     */
    public String seekmycar(SupplyDetailEdite info, int page) {
        data1.put("cityfrom", info.cityfrom);
        data1.put("cityto", info.cityto);
        data1.put("pageindex", page);
        data1.put("pagesize", Constants.page);
        data1.put("carlen", info.carlen);
        data1.put("cartype", info.cartype);
        return getParamsJsonString(data1, Constants.seekmycar);
    }

    /**
     * 空车上报
     */
    public String emptycar(int cityfrom, List<CityTo> cityto, String starttime, String endtime, double wt, String carlen) {
        data1.put("cityfrom", cityfrom);
        data1.put("starttime", starttime);
        data1.put("endtime", endtime);
        data1.put("wt", wt);
        data1.put("carlen", carlen);
        RequestParams data = new RequestParams();
        data.setTerminal(1);
        data.setClient("android");
        data.setToken(App.token);
        data.setAccount(App.account);
        data.setInterfacecode(Constants.emptycar);
        data.setData1(data1);
        data.setData2(cityto);
        MLog.i("dm", "2object:" + JSON.toJSONString(data));
        return JSON.toJSONString(data);
    }

    /**
     * 查询货源(找货)
     */
    public String checksupply_seek(String lineid, int cityfrom, int cityto,
                                   String carlen, String cartype,
                                   String sort, int page, int pagesize,
                                   float carlenstar, float carlenend) {
        data1.put("lineid", lineid);
        data1.put("cityfrom", cityfrom);
        data1.put("cityto", cityto);
        data1.put("carlen", carlen);
        data1.put("cartype", cartype);
        data1.put("sort", sort);
        data1.put("page", page);
        data1.put("pagesize", pagesize);
        data1.put("carlenstar", carlenstar);
        data1.put("carlenend", carlenend);
        return getParamsJsonString(data1, Constants.checksupply_seek);
    }

    /**
     * 查询货源 报价货源
     */
    public String goodslist_baojia(int type, int page, int pagesize) {
        data1.put("type", type);

        data1.put("pageindex", page);
        data1.put("pagesize", pagesize);
        return getParamsJsonString(data1, Constants.goodsbaojiaList);
    }

    /**
     * 查询货源(找全部线路货源)
     */
    public String requestAllLines(int page, int pagesize,
                                  float carlenstar, float carlenend) {
        data1.put("pageindex", page);
        data1.put("pagesize", pagesize);
        data1.put("carlenstar", carlenstar);
        data1.put("carlenend", carlenend);
        return getParamsJsonString(data1, Constants.requestAllLines);
    }

    /**
     * 微信支付
     */
    public String wxpay(
            String ProductID
//                        String body,int total_fee,
//                        String spbill_create_ip,String time_start,
//                        String time_expire,String notify_url
    ) {
//        data1.put("body", body);
//        data1.put("total_fee", total_fee);
//        data1.put("spbill_create_ip", spbill_create_ip);
//        data1.put("time_start", time_start);
//        data1.put("time_expire", time_expire);
//        data1.put("notify_url", notify_url);
//        data1.put("trade_type", "APP");
        data1.put("ProductID", ProductID);
        return getParamsJsonString(data1, Constants.wxpay);
    }

    /**
     * 银联支付
     */
    public String alipay(
            String ProductID
//                        String body,int total_fee,
//                        String spbill_create_ip,String time_start,
//                        String time_expire,String notify_url
    ) {
//        data1.put("body", body);
//        data1.put("total_fee", total_fee);
//        data1.put("spbill_create_ip", spbill_create_ip);
//        data1.put("time_start", time_start);
//        data1.put("time_expire", time_expire);
//        data1.put("notify_url", notify_url);
//        data1.put("trade_type", "APP");
        data1.put("ProductID", ProductID);
        return getParamsJsonString(data1, Constants.unionpay);
    }
    /**
     * 支付宝支付
     */
    public String unionpay(
            String ProductID
//                        String body,int total_fee,
//                        String spbill_create_ip,String time_start,
//                        String time_expire,String notify_url
    ) {
//        data1.put("body", body);
//        data1.put("total_fee", total_fee);
//        data1.put("spbill_create_ip", spbill_create_ip);
//        data1.put("time_start", time_start);
//        data1.put("time_expire", time_expire);
//        data1.put("notify_url", notify_url);
//        data1.put("trade_type", "APP");
        data1.put("ProductID", ProductID);
        return getParamsJsonString(data1, Constants.alipay);
    }

    /**
     * 上传经纬度
     */
    public String locaton(List<MyLocation> myLocation) {
        RequestParams data = new RequestParams();
        data.setTerminal(1);
        data.setClient("android");
        data.setToken(App.token);
        data.setAccount(App.account);
        data.setInterfacecode(Constants.location);
        data.setData2(myLocation);
        return JSON.toJSONString(data);
    }

    /**
     * 添加常跑路线
     */
    public String addoffenline(OffenLine line) {
        data2.add(line);
        RequestParams data = new RequestParams();
        data.setTerminal(1);
        data.setClient("android");
        data.setToken(App.token);
        data.setAccount(App.account);
        data.setInterfacecode(Constants.addoffenline);
        data.setData2(data2);
        return JSON.toJSONString(data);
    }

}
