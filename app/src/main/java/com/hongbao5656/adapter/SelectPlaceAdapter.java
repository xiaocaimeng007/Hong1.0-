package com.hongbao5656.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hongbao5656.R;
import com.hongbao5656.entity.Region;
import com.hongbao5656.util.SU;

import java.util.ArrayList;
import java.util.List;
public class SelectPlaceAdapter extends BaseAdapter {
	private List<Region> datas;
	private LayoutInflater inflater;
	private onTwiceClickListener listener;
	public SelectPlaceAdapter(Context context, List<Region> datas) {
		this.inflater = LayoutInflater.from(context);
		this.datas = datas;
		if (this.datas == null) {
			this.datas = new ArrayList<Region>();
		}
	}
	@Override
	public int getCount() {
		return datas.size();
	}
	@Override
	public Object getItem(int position) {
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
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_gv_selectplace, null);
			holder.gridview_item_name = (TextView) convertView.findViewById(R.id.gridview_item_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final Region vo = datas.get(position);
		if(!SU.isEmpty(vo.Name)){
			holder.gridview_item_name.setText(vo.Name);
		}else{
			holder.gridview_item_name.setText("");
		}

		convertView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (listener != null) {
					listener.twiceClick(vo);
				}
			}
		});
		return convertView;
	}

	class ViewHolder {
		TextView gridview_item_name;
	}

	public void setOnTwiceClickListener(onTwiceClickListener listener) {
		this.listener = listener;
	}

	public interface onTwiceClickListener {
		void twiceClick(Region info);
	}

	public List<Region> getDatas() {
		return this.datas;
	}

	public void clearListView() {
		this.datas.clear();
	}

	public void update_list(List<Region> datas) {
		this.datas.clear();
		this.datas.addAll(datas);
		notifyDataSetChanged();
	}
}
