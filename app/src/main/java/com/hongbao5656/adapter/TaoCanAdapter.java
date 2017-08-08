package com.hongbao5656.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.hongbao5656.R;
import com.hongbao5656.util.SU;

import java.util.ArrayList;
import java.util.List;

public class TaoCanAdapter extends BaseAdapter {
    private Context context;
    private List<String> cities;

    public TaoCanAdapter(Context context, List<String> cities) {
        this.context = context;
        this.cities = cities;
        if (this.cities == null) {
            this.cities = new ArrayList<String>();
        }
    }

    @Override
    public int getCount() {
        if(cities.size()<=3){
            return cities.size();
        }else{
            return 3;
        }
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

    RadioButton preBlock = null;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_zhifutaocan, null);
            holder = new ViewHolder();
            holder.tv_taocan = (TextView) convertView.findViewById(R.id.tv_taocan);
            holder.itemBlock = (LinearLayout) convertView.findViewById(R.id.itemblock);
            holder.rb_taocan = (RadioButton) convertView.findViewById(R.id.rb_taocan);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        final String item = cities.get(position);
        if (!SU.isEmpty(item)) {
            holder.tv_taocan.setText(item);
        } else {
            holder.tv_taocan.setText("");
        }

//		holder.rb_taocan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				if(isChecked){
////					holder.rb_taocan.setChecked(true);
//				}
//			}
//		});

//		if(item.equals(selectItem)){
//			holder.rb_taocan.setChecked(true);
////			holder.itemBlock.setBackgroundResource(R.color.base_color2);
////			holder.tv_taocan.setTextColor(context.getResources().getColor(R.color.white));
//			preBlock=holder.rb_taocan;
//		}else{
//			preBlock.setChecked(false);
//			holder.rb_taocan.setChecked(false);
////			holder.itemBlock.setBackgroundResource(R.color.white);
////			holder.tv_taocan.setTextColor(context.getResources().getColor(R.color.black));
//		}

//        if(0 == position){
//            setSelectItem(item);
//            holder.rb_taocan.setChecked(true);
//            preBlock = holder.rb_taocan;
//        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewHolder cholder = (ViewHolder) v.getTag();
                if (preBlock != null) {
//					preBlock.setBackgroundResource(R.color.white);
//				cholder.itemBlock.setBackgroundResource(R.color.base_color2);
//				cholder.tv_taocan.setTextColor(context.getResources().getColor(R.color.white));
//                    if (preBlock.isChecked()) {
                    preBlock.setChecked(false);
//                    }
//                    preBlock = cholder.rb_taocan;
//				if (supplyListener != null) {
//					supplyListener.send2(item);
                }
                if (!cholder.rb_taocan.isChecked()) {
                    cholder.rb_taocan.setChecked(true);
                    setSelectItem(cholder.tv_taocan.getText().toString());
                    preBlock = cholder.rb_taocan;
                }else{
                    cholder.rb_taocan.setChecked(false);
                }
                notifyDataSetChanged();
            }
        });
        return convertView;
    }


    class ViewHolder {
        TextView tv_taocan;
        LinearLayout itemBlock;
        RadioButton rb_taocan;
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
