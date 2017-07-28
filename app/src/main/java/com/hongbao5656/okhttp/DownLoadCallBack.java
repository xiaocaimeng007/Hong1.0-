package com.hongbao5656.okhttp;

/**
 * 下载回调
 *
 * @author dm
 *
 */
public interface DownLoadCallBack {

	public void onDownLoading(int prograss);

	public void onDownLoadSuccess();

	public void onDownLoadFail(HttpException ex);
}
