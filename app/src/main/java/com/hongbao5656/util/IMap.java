package com.hongbao5656.util;

import java.util.LinkedHashMap;
import java.util.List;

import com.alibaba.fastjson.JSON;

/**
 * 获取响应数据（JSON型java对象IParams字符串）中相应成员的帮助类
 */
public class IMap {
	/**
	 * 先将JSON型字符串转为java对象ResponseParams
	 */
	public static ResponseParams<LinkedHashMap> jieXiResponse(String response) {
		if (response == null || response.equals("")) {
			return null;
		}
		ResponseParams<LinkedHashMap> iParams = JSON.parseObject(response, ResponseParams.class);
//		LU.i("响应参数解析：",iParams != null ? iParams.toString() : "");
		return iParams;
	}

	/**
	 * 获得ResponseParams对象中的data2字段(集合装对象)
	 * 即将JSONArray型字符串转为List<calss>类型的对象返回
	 */
	public static List getData2FromResponse(ResponseParams<LinkedHashMap> iParams, Class calss) {
		List<LinkedHashMap> data2 = iParams.getData2();
		List list = null;
		if (data2 != null) {
			list = JSON.parseArray(JSON.toJSONString(data2), calss);
			return list;
		} else {
			return null;
		}
	}

	/**
	 * 获取data1字段
	 */
	public static String getData1FromResponse(ResponseParams<LinkedHashMap> iParams) {
		return iParams.getData1().toString();
	}

	/**
	 * 获取总页数totalPage
	 */
	public static int getUFromResponse(ResponseParams<LinkedHashMap> iParams) {
		if (iParams != null) {
			return Integer.parseInt(String.valueOf(iParams.getData1().get("pagecount")));
		} else {
			return 0;
		}
	}

	/**
	 * 获取返回状态
	 */
	public static int getErrcodeFromResponse(ResponseParams<LinkedHashMap> iParams) {
		return iParams.getErrcode();
	}

	/**
	 * 获取提示信息
	 */
	public static String getAFromResponse(ResponseParams<LinkedHashMap> iParams) {
		return iParams.getErrmsg();
	}

}
