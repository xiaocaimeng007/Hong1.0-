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
import com.hongbao5656.entity.Record;
import com.hongbao5656.entity.SupplyItem;
import com.hongbao5656.util.SU;
import com.hongbao5656.util.TimeUtils;
import com.hongbao5656.view.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * 充值记录适配器
 * @author dm
 */
public class RecordAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<Record> datas;
	private Context mContext;
	private DeleteListener deleteListener;
	public RecordAdapter(Context context, List<Record> datas) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		this.datas = datas;
		if (this.datas == null) {
			this.datas = new ArrayList<Record>();
		}
	}
	@Override
	public int getCount() {
		return datas.size();
	}
	@Override
	public Record getItem(int position) {
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
			cv = mInflater.inflate(R.layout.item_xlv_record,null);
			vh = new ViewHolder();
			vh.productid = (TextView) cv.findViewById(R.id.productid);
			vh.description = (TextView) cv.findViewById(R.id.description);
			vh.price = (TextView) cv.findViewById(R.id.price);
			vh.paytime = (TextView) cv.findViewById(R.id.paytime);
			vh.months = (TextView) cv.findViewById(R.id.months);
			vh.ishandle = (TextView) cv.findViewById(R.id.ishandle);
			cv.setTag(vh);
		} else {
			vh = (ViewHolder) cv.getTag();
		}

		final Record vo = datas.get(position);


		// 设置出发地
		if(!SU.isEmpty(vo.productid)) {
			vh.productid.setText(vo.productid);
		}else{
			vh.productid.setText("");
		}
		// 设置目的地
		if(!SU.isEmpty(vo.description)) {
			vh.description.setText(vo.description);
		}else{
			vh.description.setText("");
		}

		//		long ll = 635957378450000000l;
		long ll = vo.paytime;
		String time = TimeUtils.getSureTime2("yyyy年MM月dd日 HH:mm", ll);
		if(!SU.isEmpty(time)){
			//右边时间
//		vh.item_tv_time.setText(TimeUtils.getSureTime2("MM-dd HH:mm",ll));
			vh.paytime.setText(time);
		}else{
			vh.paytime.setText("");
		}

		if(!SU.isEmpty(vo.price+"")){
			vh.price.setText(vo.price+"元");
		}else{
			vh.price.setText("0元");
		}

		if(!SU.isEmpty(vo.months+"")){
			vh.months.setText(vo.months+"月");
		}else{
			vh.months.setText("0月");
		}
		if(!SU.isEmpty(vo.ishandle)){
			if("Y".equals(vo.ishandle)){
				vh.ishandle.setText("支付成功");
			}else if("N".equals(vo.ishandle)){
				vh.ishandle.setText("支付失败");
			}
		}else{
			vh.ishandle.setText("");
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
		TextView productid;
		TextView description;
		TextView paytime;
		TextView price;
		TextView months;
		TextView ishandle;
	}
	public void updateList(List<Record> list) {
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
