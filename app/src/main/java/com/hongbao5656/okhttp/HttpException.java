package com.hongbao5656.okhttp;

import java.io.Serializable;

/**
 *
 * @author huajun
 *
 */
public class HttpException implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private int responseCode;
	private Exception ex;

	public HttpException() {
		ex = new Exception();
	}

	public HttpException(int code) {
		this.responseCode = code;
		ex = new Exception(code + " error!");
	}

	public HttpException(int code, String message) {
		this.responseCode = code;
		this.ex = new Exception(message);
	}

	public HttpException(Exception ex) {
		this.responseCode = -1;
		this.ex = ex;
	}

	public HttpException(Exception ex,int code) {
		this.responseCode = code;
		this.ex = ex;
	}

	public String getMessage() {
		return ex.getMessage();
	}

	public Throwable getCause(){
		return ex.getCause();
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}


}
