package com.hongbao5656.okhttp;


import com.squareup.okhttp.Request;

/**
 * okhttp数据回调
 */
public interface ResultCallBack {

	//响应成功并解析成功
	public void onResponse(int connectedId, Object response);
	//请求失败或响应成功后解析失败
	public void onError(int connectedId, Request request, HttpException httpException, String params, String content);
}
