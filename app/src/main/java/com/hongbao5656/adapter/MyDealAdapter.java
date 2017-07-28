package com.hongbao5656.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hongbao5656.R;
import com.hongbao5656.entity.SupplyItem;
import com.hongbao5656.util.SU;
import com.hongbao5656.util.TimeUtils;
import com.hongbao5656.view.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的交易适配器
 * @author dm
 */
public class MyDealAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<SupplyItem> datas;
	private Context mContext;
	private DeleteListener deleteListener;
	public MyDealAdapter(Context context, List<SupplyItem> datas) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		this.datas = datas;
		if (this.datas == null) {
			this.datas = new ArrayList<SupplyItem>();
		}
	}
	@Override
	public int getCount() {
		return datas.size();
	}
	@Override
	public SupplyItem getItem(int position) {
		return datas.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(final int position, View cv, ViewGroup parent) {
		ViewHolder vh = null;
		if (cv == null) {
			cv = mInflater.inflate(R.layout.item_xlv_mydeal,null);
			vh = new ViewHolder();
			vh.include_topaddress_tv_begin = (TextView) cv.findViewById(R.id.include_topaddress_tv_begin);
			vh.include_topaddress_tv_end = (TextView) cv.findViewById(R.id.include_topaddress_tv_end);
			vh.chechang = (TextView) cv.findViewById(R.id.chechang);
			vh.tv_time = (TextView) cv.findViewById(R.id.tv_time);
			vh.chexing = (TextView) cv.findViewById(R.id.chexing);
			vh.zhongliang = (TextView) cv.findViewById(R.id.zhongliang);
			vh.tv_user = (TextView) cv.findViewById(R.id.tv_user);
			vh.tv_carno = (TextView) cv.findViewById(R.id.tv_carno);
			vh.tv_money = (TextView) cv.findViewById(R.id.tv_money);
			vh.tv_laiyuan = (TextView) cv.findViewById(R.id.tv_laiyuan);
			vh.raiv_userphoto = (RoundedImageView) cv.findViewById(R.id.raiv_userphoto);
			vh.item_iv_call = (ImageView) cv.findViewById(R.id.item_iv_call);
			cv.setTag(vh);
		} else {
			vh = (ViewHolder) cv.getTag();
		}

		final SupplyItem vo = datas.get(position);


		// 设置出发地
		if(!SU.isEmpty(vo.cityfromname)) {
			vh.include_topaddress_tv_begin.setText(vo.cityfromname);
		}else{
			vh.include_topaddress_tv_begin.setText("");
		}
		// 设置目的地
		if(!SU.isEmpty(vo.citytoname)) {
			vh.include_topaddress_tv_end.setText(vo.citytoname);
		}else{
			vh.include_topaddress_tv_end.setText("");
		}

		//		long ll = 635957378450000000l;
		long ll = vo.lastupdate;
		String time = TimeUtils.getSureTime2("HH:mm", ll);
		if(!SU.isEmpty(time)){
			//右边时间
//		vh.item_tv_time.setText(TimeUtils.getSureTime2("MM-dd HH:mm",ll));
			vh.tv_time.setText(time);
		}else{
			vh.tv_time.setText("");
		}

		// 拨打电话
		vh.item_iv_call.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:" + vo.phone));
				mContext.startActivity(intent);
			}
		});

		if(!SU.isEmpty(vo.carlen)){
			vh.chechang.setText(vo.carlen);
		}else{
			vh.chechang.setText("");
		}

		if(!SU.isEmpty(vo.cartype)){
			vh.chexing.setText(vo.cartype);
		}else{
			vh.chexing.setText("");
		}

		if(!SU.isEmpty(vo.laiyuan)){
			vh.tv_laiyuan.setText(vo.laiyuan);
		}else{
			vh.tv_laiyuan.setText("");
		}


		if (0.0 == vo.wv){
			vh.zhongliang.setText("整车");
		}else{
			vh.zhongliang.setText(String.valueOf(vo.wv)+"吨");
		}

		if(!SU.isEmpty(vo.username)){
			vh.tv_user.setText(vo.username);
		}else{
			vh.tv_user.setText("");
		}

		Picasso.with(mContext).load(vo.ImageURL).into(vh.raiv_userphoto);

		if(!SU.isEmpty(vo.carno)){
			vh.tv_carno.setText(vo.carno);
		}else{
			vh.tv_carno.setText("");
		}

		if(!SU.isEmpty(vo.money)){
			vh.tv_money.setText(vo.money);
		}else{
			vh.tv_money.setText("");
		}
		//查看详情
		cv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				vh.v_ifread.setVisibility(View.GONE);
//				Intent intent = new Intent(mContext, CarDetailActivity.class);
//				intent.putExtra("account", vo.account);
//				mContext.startActivity(intent);
			}
		});
		return cv;
	}
	class ViewHolder {
		RoundedImageView raiv_userphoto;
		TextView include_topaddress_tv_begin;
		TextView include_topaddress_tv_end;
		TextView tv_time;
		TextView chechang;
		TextView zhongliang;
		TextView chexing;
		TextView tv_user;
		TextView tv_carno;
		TextView tv_money;
		TextView tv_laiyuan;
		ImageView item_iv_call;
	}
	public void updateList(List<SupplyItem> list) {
		this.datas.addAll(list);
		notifyDataSetChanged();
	}
	public void clearListView() {
		this.datas.clear();
	}

	public void setDeleteListener(DeleteListener deleteListener) {
		this.deleteListener = deleteListener;
	}

	public interface DeleteListener {
		void onDeleteListener(String goodsid);
	}
}
