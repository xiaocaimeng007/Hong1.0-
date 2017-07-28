package com.hongbao5656.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hongbao5656.R;
import com.hongbao5656.util.SU;

import java.util.ArrayList;
import java.util.List;

public class CityLevelAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<String> cities;
    private CitySupplyListener supplyListener;

    public CityLevelAdapter(Context context, List<String> cities) {
        this.inflater = LayoutInflater.from(context);
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_supplytype, null);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        String city = cities.get(position);
        if (!SU.isEmpty(city)) {
            holder.btn_shi.setText(city);
        } else {
            holder.btn_shi.setText("");
        }
        if (supplyListener != null) {
            supplyListener.send(convertView, cities.get(position));
        }
        return convertView;
    }

    class ViewHolder {
        private TextView btn_shi;
        public ViewHolder(View item) {
            btn_shi = (TextView) item.findViewById(R.id.btn_shi);
        }
    }

    public void upateList(List<String> list) {
        this.cities.addAll(list);
        notifyDataSetChanged();
    }

    public void clearListView() {
        this.cities.clear();
    }


    public void setSupplyListener(CitySupplyListener supplyListener) {
        this.supplyListener = supplyListener;
    }

    public interface CitySupplyListener {
        void send(View view, String aInfo);
    }

}
