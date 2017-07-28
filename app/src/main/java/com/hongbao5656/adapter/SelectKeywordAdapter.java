package com.hongbao5656.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.hongbao5656.R;
import com.hongbao5656.entity.Region2;
import com.hongbao5656.util.SU;

import java.util.ArrayList;
import java.util.List;

public class SelectKeywordAdapter extends BaseAdapter {
	private List<Region2> datas;
	private LayoutInflater inflater;
	private onItemClickListener listener;
	public SelectKeywordAdapter(Context context, List<Region2> datas) {
		this.inflater = LayoutInflater.from(context);
		this.datas = datas;
		if (this.datas == null) {
			this.datas = new ArrayList<Region2>();
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
			convertView = inflater.inflate(R.layout.item_lv_selectplace, null);
			holder.gridview_item_name = (TextView) convertView.findViewById(R.id.gridview_item_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final Region2 vo = datas.get(position);
		String GrandpaNmae = "".equals(vo.GrandpaNmae)?"":vo.GrandpaNmae+"-";
		String ParentNmae = "".equals(vo.ParentNmae)?"":vo.ParentNmae;
		String pNmae = "".equals(GrandpaNmae)&&"".equals(ParentNmae)?"":"("+GrandpaNmae+ParentNmae+")";

		String voname = vo.Name;
		String pname = pNmae;
		if(!SU.isEmpty(vo.Name)){
			voname = vo.Name;
		}else{
			voname = "";
		}
		if(!SU.isEmpty(pname)){
			pname = pNmae;
		}else{
			pname = "";
		}
		holder.gridview_item_name.setText(vo.Name+" "+pNmae);

		convertView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (listener != null) {
					listener.itemClick(vo);
				}
			}
		});
		return convertView;
	}

	class ViewHolder {
		TextView gridview_item_name;
	}

	public void setItemClickListener(onItemClickListener listener) {
		this.listener = listener;
	}

	public interface onItemClickListener {
		void itemClick(Region2 info);
	}

	public List<Region2> getDatas() {
		return this.datas;
	}

	public void clearListView() {
		this.datas.clear();
	}

	public void update_list(List<Region2> datas) {
		this.datas.clear();
		this.datas.addAll(datas);
		notifyDataSetChanged();
	}
}
