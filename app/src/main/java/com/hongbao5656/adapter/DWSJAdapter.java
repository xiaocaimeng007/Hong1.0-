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
 * 定位司机适配器
 * @author dm
 */
public class DWSJAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<SupplyItem> datas;
	private Context mContext;
	public DWSJAdapter(Context context, List<SupplyItem> datas) {
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
			cv = mInflater.inflate(R.layout.item_xlv_dwsj,null);
			vh = new ViewHolder();
			vh.tv_isdeal = (TextView) cv.findViewById(R.id.tv_isdeal);
			vh.tv_time = (TextView) cv.findViewById(R.id.tv_time);
			vh.tv_user = (TextView) cv.findViewById(R.id.tv_user);
			vh.phonecarno = (TextView) cv.findViewById(R.id.phonecarno);
			vh.tv_hpl = (TextView) cv.findViewById(R.id.tv_hpl);
			vh.tv_txbj = (TextView) cv.findViewById(R.id.tv_txbj);
			vh.tv_jrsc = (TextView) cv.findViewById(R.id.tv_jrsc);
			vh.tv_dwsj = (TextView) cv.findViewById(R.id.tv_dwsj);
			vh.raiv_userphoto = (RoundedImageView) cv.findViewById(R.id.raiv_userphoto);
			vh.item_iv_call = (ImageView) cv.findViewById(R.id.item_iv_call);
			cv.setTag(vh);
		} else {
			vh = (ViewHolder) cv.getTag();
		}

		final SupplyItem vo = datas.get(position);

		long ll = vo.lastupdate;//		long ll = 635957378450000000l;
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

		if(!SU.isEmpty(vo.username)){
			vh.tv_user.setText(vo.username);
		}else{
			vh.tv_user.setText("");
		}


		Picasso.with(mContext).load(vo.ImageURL).into(vh.raiv_userphoto);

		if(!SU.isEmpty(vo.phone)||!SU.isEmpty(vo.carno)){
			vh.phonecarno.setText(vo.phone+"/"+vo.carno);
		}else{
			vh.phonecarno.setText("");
		}

		if(!SU.isEmpty(vo.hpl)){
			vh.tv_hpl.setText(vo.hpl);
		}else{
			vh.tv_txbj.setText("");
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

		//加入熟车
		vh.tv_jrsc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				dwsjAdapterListener.onAddOffenCarListener(vo.account);
			}
		});
		//定位司机
		vh.tv_dwsj.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				dwsjAdapterListener.onDWSJListener();
			}
		});
		//
		vh.tv_txbj.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				dwsjAdapterListener.onWritePriceListener();
			}
		});

		return cv;
	}
	class ViewHolder {
		RoundedImageView raiv_userphoto;
		TextView tv_user;
		TextView tv_isdeal;
		TextView tv_time;
		TextView phonecarno;
		ImageView item_iv_call;
		TextView tv_hpl;
		TextView tv_txbj;
		TextView tv_jrsc;
		TextView tv_dwsj;
	}
	public void updateList(List<SupplyItem> list) {
		this.datas.addAll(list);
		notifyDataSetChanged();
	}
	public void clearListView() {
		this.datas.clear();
	}



	public interface DWSJAdapterListener{
		void onWritePriceListener();
		void onAddOffenCarListener(String account);
		void onDWSJListener();
	}

	public void setDWSJAdapterListener(DWSJAdapterListener dwsjAdapterListener){
		this.dwsjAdapterListener = dwsjAdapterListener;
	}
	DWSJAdapterListener dwsjAdapterListener;

}
