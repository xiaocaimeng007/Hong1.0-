package com.hongbao5656.util;

import java.util.List;
import java.util.TreeMap;

import com.google.common.collect.Lists;
/**
 * 请求接口通用参数Bean
 */
public class RequestParams <T>{
	/**
	 * 电话
	 */
	private String account;
	/**
	 * token
	 */
	private String token;
	/**
	 * 终端
	 */
	private int terminal;
	/**
	 * 业务系统
	 */
	private String client;
	/**
	 * 接口标识
	 */
	private int interfacecode;
	/**
	 * 增删改查等条件（EQ_tokenId,EQ_tokenTel,EQ_userId,EQ_lat,EQ_lon,EQ_pid）
	 */
	private TreeMap<String,Object> data1=new TreeMap<String, Object>();
	/**
	 * 集合（对象）
	 */
	private List<T> data2=Lists.newArrayList();
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public int getTerminal() {
		return terminal;
	}
	public void setTerminal(int terminal) {
		this.terminal = terminal;
	}
	public String getClient() {
		return client;
	}
	public void setClient(String client) {
		this.client = client;
	}
	public int getInterfacecode() {
		return interfacecode;
	}
	public void setInterfacecode(int interfacecode) {
		this.interfacecode = interfacecode;
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
		return "RequestParams [account=" + account + ", token=" + token
				+ ", terminal=" + terminal + ", client=" + client
				+ ", interfacecode=" + interfacecode + ", data1=" + data1
				+ ", data2=" + data2 + "]";
	}
}
