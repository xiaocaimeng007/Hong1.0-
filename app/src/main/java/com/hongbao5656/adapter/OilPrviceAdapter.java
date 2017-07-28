package com.hongbao5656.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.hongbao5656.R;
import com.hongbao5656.entity.OilPrvice;
import com.hongbao5656.util.SU;
import com.hongbao5656.util.TU;

import java.util.ArrayList;
import java.util.List;

public class OilPrviceAdapter extends BaseAdapter {
	private Context context;
	private List<OilPrvice> cities;

	public OilPrviceAdapter(Context context, List<OilPrvice> cities) {
		this.context = context;
		this.cities = cities;
		if (this.cities == null) {
			this.cities = new ArrayList<OilPrvice>();
		}
	}

	@Override
	public int getCount() {
		return cities.size();
	}

	@Override
	public OilPrvice getItem(int position) {
		return cities.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.list_item_toydayoilprvice, null);
			holder = new ViewHolder();
			holder.tv_90 = (TextView) convertView.findViewById(R.id.tv_90);
			holder.tv_93 = (TextView) convertView.findViewById(R.id.tv_93);
			holder.tv_97 = (TextView) convertView.findViewById(R.id.tv_97);
			holder.tv_0 = (TextView) convertView.findViewById(R.id.tv_0);
			holder.tv_region = (TextView) convertView.findViewById(R.id.tv_region);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		OilPrvice oilPrvice = cities.get(position);
		if(!SU.isEmpty(oilPrvice.province)){
			holder.tv_region.setText(oilPrvice.province);
		}else{
			holder.tv_region.setText("");
		}
		if(!SU.isEmpty(oilPrvice.gasoline90)){
			holder.tv_90.setText(oilPrvice.gasoline90);
		}else{
			holder.tv_90.setText("");
		}
		if(!SU.isEmpty(oilPrvice.gasoline93)){
			holder.tv_93.setText(oilPrvice.gasoline93);
		}else{
			holder.tv_93.setText("");
		}
		if(!SU.isEmpty(oilPrvice.gasoline97)){
			holder.tv_97.setText(oilPrvice.gasoline97);
		}else{
			holder.tv_97.setText("");
		}
		if(!SU.isEmpty(oilPrvice.dieselOil0)){
			holder.tv_0.setText(oilPrvice.dieselOil0);
		}else{
			holder.tv_0.setText("");
		}
		return convertView;
	}

	class ViewHolder {
		private TextView tv_region;
		private TextView tv_90;
		private TextView tv_93;
		private TextView tv_97;
		private TextView tv_0;
	}

	public void upateList(List<OilPrvice> list) {
		this.cities.clear();
		this.cities.addAll(list);
		notifyDataSetChanged();
	}

}
