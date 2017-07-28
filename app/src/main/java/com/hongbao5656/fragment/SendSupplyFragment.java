package com.hongbao5656.fragment;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hongbao5656.R;
import com.hongbao5656.activity.LoginActivity;
import com.hongbao5656.activity.MainActivity;
import com.hongbao5656.activity.PublishSupplyActivity;
import com.hongbao5656.adapter.SendSupplyAdapter;
import com.hongbao5656.adapter.SendSupplyAdapter.DeleteListener;
import com.hongbao5656.base.BaseFragment;
import com.hongbao5656.okhttp.HttpException;
import com.hongbao5656.util.Constants;
import com.hongbao5656.entity.SupplyItem;
import com.hongbao5656.okhttp.HttpDataHandlerListener;
import com.hongbao5656.util.AllDialog;
import com.hongbao5656.util.IMap;
import com.hongbao5656.util.ParamsService;
import com.hongbao5656.util.ResponseParams;
import com.hongbao5656.util.TU;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
/**
 * 货源
 */
public class SendSupplyFragment extends BaseFragment
		implements HttpDataHandlerListener,OnClickListener{
	private static final String TAG = "SendSupplyFragment";
	/** 是否初始化完控件 */
	protected boolean isPrepared;
	/** 是否是第一次加载 */
	private View rootView;
	private int cPage = 1;
	private int totalpage = 0;
	private MainActivity mActivity;
	private RelativeLayout rl_fahuoleft;
	private TextView tv_fahuoright;
	private SendSupplyAdapter adapter;
	private List<SupplyItem> datas_supply = new ArrayList<SupplyItem>();
//	private XListView xListView;
	private View sendSupplyFragmentTop;
	private RelativeLayout rl_null;
	private ListView lv;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof MainActivity) {
			mActivity = (MainActivity) activity;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_sendsupply, null);
		initView();
		initMonitor();
		isPrepared = true;
		//isFirstLoad = true;
		lazyLoad();
		return rootView;
	}

	private void initDatas(int cPage) {
		sendPostRequest(
				Constants.checksupply,
				new ParamsService().requestKV(
						"sort", "",
						"cityfrom",0,
						"cityto", 0,
				Constants.checksupply)
				,this,false);
	}


	@Override
	protected void initView() {
		View topview = mActivity.sendSupplyFragmentTop;
		if(topview!=null){
			rl_fahuoleft = (RelativeLayout) topview.findViewById(R.id.rl_fahuoleft);
		}
//		tv_fahuoright = (TextView) sendSupplyFragmentTop.findViewById(R.id.tv_fahuoright);
//		xListView = (XListView)rootView.findViewById(R.id.xListView);
		rl_null = (RelativeLayout)rootView.findViewById(R.id.rl_null);
		adapter = new SendSupplyAdapter(mActivity, datas_supply);
//		xListView.setAdapter(adapter);
//		// 设置xlistview可以加载、刷新
//		xListView.setPullLoadEnable(true);
//		xListView.setPullRefreshEnable(true);

		lv = (ListView)rootView.findViewById(R.id.lv);
		lv.setAdapter(adapter);
	}

	@Override
	protected void initMonitor() {
		rl_fahuoleft.setOnClickListener(this);
//		tv_fahuoright.setOnClickListener(this);
		adapter.setDeleteListener(new DeleteListener() {

			private AllDialog allDialog;

			@Override
			public void onDeleteListener(final String goodsid) {
				allDialog = new com.hongbao5656.util.AllDialog(mActivity);
				allDialog.setContent("您确认要删除吗？");
				allDialog.show();
				allDialog.btn_ok.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						deleteRequest(goodsid);
						if(allDialog!=null){
							allDialog.dismiss();
						}
					}
				});
				allDialog.btn_fangqi.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						allDialog.dismiss();
					}
				});
			}
		});
//		xListView.setXListViewListener(new XListView.IXListViewListener() {
//			@Override
//			public void onRefresh() {
//				xListView.setRefreshTime(System.currentTimeMillis());
//				xListView.stopRefresh();
//				cPage =1;
//				initDatas(cPage);
//			}
//
//			@Override
//			public void onLoadMore() {
//				xListView.setRefreshTime(System.currentTimeMillis());
//				xListView.stopLoadMore();
//				cPage++;
//				initDatas(cPage);
//			}
//		});
	}


	void deleteRequest(String goodsid){
		sendPostRequest(
				Constants.deletesupply,
				new ParamsService().requestKV("goodsid",goodsid,Constants.deletesupply),
				this,
				false);
	}

	@Override
	public void lazyLoad() {
		if (!isPrepared || !super.isVisible) {
			return;//控件没有初始化完成或者fragment不可见时直接结束该方法
		}
		cPage = 1;
		initDatas(cPage);
	}
	@Override
	public void setHandlerPostDataSuccess(int connectionId, Object data) throws JSONException {
		int errcode = -1;
		ResponseParams<LinkedHashMap> iParams = null;
		if (null != data) {
			iParams = IMap.jieXiResponse(data.toString());
			errcode = iParams.getErrcode();
		}
		switch (errcode) {
			case 50000://token过期   只清坐标
				TU.show(mActivity, "" + iParams.getErrmsg());
				mActivity.clearAndStopLocationData();//只清坐标
				openActivity(LoginActivity.class);
				mActivity.finish();
				break;
			case 50001://第三方登录  清坐标 清个人信息 清推送
				TU.show(mActivity, "" + iParams.getErrmsg());
				eixtAccount();
				openActivity(LoginActivity.class);
				mActivity.finish();
				break;
			case 44444://接口异常 升级app
				TU.show(mActivity, iParams.getErrmsg() + "-" + errcode);
				UpdateApp();
				if (10013 == connectionId || 10014 == connectionId) {
					clearAndStopLocationData();
				}
				break;
			case 0://成功
				if (connectionId==Constants.checksupply) {//查询已发货源
					totalpage  = IMap.getUFromResponse(iParams);
					if (errcode==0) {
						datas_supply = IMap.getData2FromResponse(iParams,SupplyItem.class);
						if(datas_supply.size()==0 && cPage==1){//第一页加载数据为空时
							adapter.clearListView();
							adapter.notifyDataSetChanged();
//					xListView.setEmptyView(rl_null);
							lv.setEmptyView(rl_null);
						}else{//显示数据
							datas_supply.get(0).flag = true;
							rl_null.setVisibility(View.GONE);
							m_updateListView(datas_supply);
						}
//				judgement();//是否可以继续加载
					}
				}else if(connectionId==Constants.deletesupply){//删除货源
					if (errcode==0) {
						TU.show(mActivity, "删除成功");
						initDatas(1);
					}
				}
				break;
			default:
				TU.show(mActivity, iParams.getErrmsg() + "-" + errcode);
				break;
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.rl_fahuoleft:
				Intent it=new Intent(mActivity, PublishSupplyActivity.class);
				mActivity.startActivity(it);
				break;
		}
	}
	/**
	 * 上拉加载下拉刷新要用的
	 */
	public void m_updateListView(List<SupplyItem> list) {
		if (cPage==1) {
			adapter.clearListView();
			adapter.updateList(list);
		}else{
			adapter.updateList(list);
		}
	}

	/**
	 * 判断加载到最后一页时只允许下拉刷新
	 */
//	public void judgement(){
//		if (totalpage <= cPage) {
//			xListView.setPullLoadEnable(false);
//			xListView.setPullRefreshEnable(true);
//		} else {
//			xListView.setPullLoadEnable(true);
//			xListView.setPullRefreshEnable(true);
//		}
//	}


}
