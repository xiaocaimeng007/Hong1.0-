package com.hongbao5656.util;

import java.util.List;
import java.util.TreeMap;

import com.google.common.collect.Lists;
/**
 * 响应接口通用参数Bean
 */
public class ResponseParams <T>{

	private int errcode;
	private String errmsg;
	private TreeMap<String,Object> data1=new TreeMap<String, Object>();
	private List<T> data2=Lists.newArrayList();
	public int getErrcode() {
		return errcode;
	}
	public void setErrcode(int errcode) {
		this.errcode = errcode;
	}
	public String getErrmsg() {
		return errmsg;
	}
	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}
	public TreeMap<String, Object> getData1() {
		return data1;
	}
	public void setData1(TreeMap<String, Object> data1) {
		this.data1 = data1;
	}
	public List<T> getData2() {
		return data2;
	}
	public void setData2(List<T> data2) {
		this.data2 = data2;
	}
	@Override
	public String toString() {
		return "ResponseParams [errcode=" + errcode + ", errmsg=" + errmsg
				+ ", data1=" + data1 + ", data2=" + data2 + "]";
	}
}
