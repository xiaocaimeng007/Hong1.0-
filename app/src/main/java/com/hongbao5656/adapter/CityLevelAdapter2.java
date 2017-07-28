package com.hongbao5656.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hongbao5656.R;
import com.hongbao5656.util.SU;

import java.util.ArrayList;
import java.util.List;

public class CityLevelAdapter2 extends BaseAdapter {
	private Context context;
	private List<String> cities;
//	private CitySupplyListener2 supplyListener;

	public CityLevelAdapter2(Context context, List<String> cities) {
		this.context = context;
		this.cities = cities;
		if (this.cities == null) {
			this.cities = new ArrayList<String>();
		}
	}

	@Override
	public int getCount() {
		return cities.size();
	}

	@Override
	public String getItem(int position) {
		return cities.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private String selectItem = "";
	public String getSelectItem() {
		return selectItem;
	}
	public void setSelectItem(String selectItem) {
		this.selectItem = selectItem == null ? "" : selectItem;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_carlongtype, null);
			holder = new ViewHolder();
			holder.btn_shi = (TextView) convertView.findViewById(R.id.btn_shi);
			holder.itemBlock = (LinearLayout) convertView.findViewById(R.id.itemblock);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final String item = cities.get(position);
		if(!SU.isEmpty(item)){
			holder.btn_shi.setText(item);
		}else{
			holder.btn_shi.setText("");
		}

		if(item.equals(selectItem)){
			holder.itemBlock.setBackgroundResource(R.color.base_color2);
			holder.btn_shi.setTextColor(context.getResources().getColor(R.color.white));
			preBlock=holder.itemBlock;
		}else{
			holder.itemBlock.setBackgroundResource(R.color.white);
			holder.btn_shi.setTextColor(context.getResources().getColor(R.color.black));
		}
		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setSelectItem(item);

				ViewHolder cholder = (ViewHolder) v.getTag();
				if (preBlock != null)
					preBlock.setBackgroundResource(R.color.white);
				cholder.itemBlock.setBackgroundResource(R.color.base_color2);
				cholder.btn_shi.setTextColor(context.getResources().getColor(R.color.white));
				preBlock = cholder.itemBlock;
//				if (supplyListener != null) {
//					supplyListener.send2(item);
//				}
				notifyDataSetChanged();
			}
		});
		return convertView;
	}

	LinearLayout preBlock = null;

	class ViewHolder {
		TextView btn_shi;
		LinearLayout itemBlock;
	}

	public void upateList(List<String> list) {
		this.cities.addAll(list);
		notifyDataSetChanged();
	}

	public void clearListView() {
		this.cities.clear();
	}
	

//	public void setSupplyListener2(CitySupplyListener2 supplyListener) {
//		this.supplyListener = supplyListener;
//	}
//
//	public interface CitySupplyListener2 {
//		void send2(String aInfo);
//	}

}
