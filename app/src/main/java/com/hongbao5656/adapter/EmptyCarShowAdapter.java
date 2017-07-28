package com.hongbao5656.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.hongbao5656.R;
import com.hongbao5656.entity.EmptyCarShowItem;
import com.hongbao5656.util.SU;
import com.hongbao5656.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;
/**
 * 已发布货源适配器
 * @author dm
 */
public class EmptyCarShowAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<EmptyCarShowItem> datas;
	private Context mContext;
	private DeleteListener deleteListener;
	public EmptyCarShowAdapter(Context context, List<EmptyCarShowItem> datas) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		this.datas = datas;
		if (this.datas == null) {
			this.datas = new ArrayList<EmptyCarShowItem>();
		}
	}
	@Override
	public int getCount() {
		return datas.size();
	}
	@Override
	public EmptyCarShowItem getItem(int position) {
		return datas.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_xlv_emptycar,null);
			holder = new ViewHolder();
			holder.include_topaddress_tv_begin = (TextView) convertView.findViewById(R.id.include_topaddress_tv_begin);
			holder.include_topaddress_tv_end = (TextView) convertView.findViewById(R.id.include_topaddress_tv_end);
			holder.include_topaddress_tv_end_parent = (TextView) convertView.findViewById(R.id.include_topaddress_tv_end_parent);
			holder.item_plv_sendsupply_tv_leftcontent = (TextView) convertView.findViewById(R.id.item_plv_sendsupply_tv_leftcontent);
			holder.item_plv_sendsupply_tv_leftcontentRmarks = (TextView) convertView.findViewById(R.id.item_plv_sendsupply_tv_leftcontentRmarks);
//			holder.item_plv_sendsupply_tv_righttime = (TextView) convertView.findViewById(R.id.item_plv_sendsupply_tv_righttime);
//			holder.item_plv_sendsupply_btn_left = (Button) convertView.findViewById(R.id.item_plv_sendsupply_btn_left);
			holder.item_plv_sendsupply_btn_right = (Button) convertView.findViewById(R.id.item_plv_sendsupply_btn_right);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final EmptyCarShowItem vo = datas.get(position);
		// 设置出发地
		if (!SU.isEmpty(vo.cityfromname)) {
//			holder.include_topaddress_tv_begin
//					.setText(vo.cityfromname+vo.cityfromparentname);
			holder.include_topaddress_tv_begin
					.setText(vo.cityfromname);
		}else{
			holder.include_topaddress_tv_begin
					.setText("");
		}
		// 设置目的地
		if(!SU.isEmpty(vo.citytoname)) {
			holder.include_topaddress_tv_end
					.setText(vo.citytoname);
		}else{
			holder.include_topaddress_tv_end
					.setText("");
		}

		// 设置目的地二级地名
		if(!SU.isEmpty(vo.citytoparentname)) {
			holder.include_topaddress_tv_end_parent
					.setText(vo.citytoparentname);
		}else{
			holder.include_topaddress_tv_end_parent
					.setText("");
		}



		// 空车开始时间
		long ls = vo.starttime;
		String stime = TimeUtils.getSureTime2("MM-dd HH:mm",ls);
		if(ls!=0 && !SU.isEmpty(stime)) {
			holder.item_plv_sendsupply_tv_leftcontent.setText("空车开始时间："+stime);
		}else{
			holder.item_plv_sendsupply_tv_leftcontent.setText("空车开始时间：");
		}
		// 空车结束时间
		long le = vo.endtime;
		String etime = TimeUtils.getSureTime2("MM-dd HH:mm",le);
		if (le==0){
			holder.item_plv_sendsupply_tv_leftcontentRmarks.setText("空车结束时间：装车为止");
		}else if(le!=0&&!SU.isEmpty(etime)){
			holder.item_plv_sendsupply_tv_leftcontentRmarks.setText("空车结束时间："+etime);
		}else{
			holder.item_plv_sendsupply_tv_leftcontentRmarks.setText("空车结束时间：");
		}

		// 删除记录
		holder.item_plv_sendsupply_btn_right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				Intent intent = new Intent(mContext, PublishSupplyActivity.class);
//				intent.putExtra("id", datas.get(position).getId());
//				mContext.startActivity(intent);
				deleteListener.onDeleteListener(vo.id);
			}
		});
		return convertView;
	}
	class ViewHolder {
		private TextView include_topaddress_tv_begin;
		private TextView include_topaddress_tv_end;
		private TextView include_topaddress_tv_end_parent;
		private TextView item_plv_sendsupply_tv_leftcontent;
		private Button item_plv_sendsupply_btn_right;
		private TextView item_plv_sendsupply_tv_leftcontentRmarks;
	}
	public void updateList(List<EmptyCarShowItem> list) {
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
