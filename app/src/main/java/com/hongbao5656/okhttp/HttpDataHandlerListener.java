package com.hongbao5656.okhttp;

import com.squareup.okhttp.Request;

import org.json.JSONException;

/**
 * 数据处理接口
 */
public interface HttpDataHandlerListener {
	//post请求成功
	void setHandlerPostDataSuccess(int connectionId, Object data) throws JSONException;

	//post请求失败
	void setHandlerPostDataError(int connectedId, Request request,
								 HttpException httpException,
								 String params, String content) throws JSONException;
	//get请求成功
	void setHandlerGetDataSuccess(int connectionId,Object data) throws JSONException;
	//get请求失败
	void setHandlerGetDataError(int connectionId,Object data) throws JSONException;
}
