package com.hongbao5656.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hongbao5656.R;
import com.hongbao5656.entity.JpushLine;
import com.hongbao5656.entity.OffenLine;
import com.hongbao5656.util.MyArrayList;
import com.hongbao5656.util.SPU;
import com.hongbao5656.util.SU;

import java.util.ArrayList;
import java.util.List;

public class OffenLineAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    public MyArrayList<OffenLine> datas;
    private List<JpushLine> lists = new ArrayList<JpushLine>();
    private Boolean isCancle = false; //取消
    private Context mContext;
    private DeleteListener deleteListener;

    public OffenLineAdapter(Context context, List<OffenLine> datas) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
//        this.datas = new MyArrayList<OffenLine>();
//        if (datas != null)
//            this.datas.addAll(datas);

        if (this.datas == null)
            this.datas = new MyArrayList<OffenLine>();
        else
            this.datas.clear();
        if (datas != null)
            this.datas.addAll(datas);

//		this.datas = datas;
//		if (this.datas == null) {
//			this.datas = new MyArrayList<OffenLine>();
//			if(datas!=null)
//				this.datas.addAll(datas);
//		}
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public OffenLine getItem(int position) {
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
            convertView = mInflater.inflate(R.layout.item_lv_offenline_v2, null);
            holder = new ViewHolder();
            holder.include_topaddress_tv_begin = (TextView) convertView.findViewById(R.id.include_topaddress_tv_begin);
            holder.include_topaddress_tv_end = (TextView) convertView.findViewById(R.id.include_topaddress_tv_end);
            holder.item_delete = (TextView) convertView.findViewById(R.id.item_delete);
//            holder.tv_notice = (TextView) convertView.findViewById(R.id.tv_notice);
            holder.item_notice = (TextView) convertView.findViewById(R.id.item_notice);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final OffenLine vo = datas.get(position);


        // 设置出发地
        if (!SU.isEmpty(vo.cityfromname)) {
            holder.include_topaddress_tv_begin
                    .setText(vo.cityfromname);
        }else{
            holder.include_topaddress_tv_begin
                    .setText("");
        }

        // 设置目的地
        if (!SU.isEmpty(vo.citytoname)) {
            holder.include_topaddress_tv_end
//                    .setText(vo.citytoname+vo.cityfromparentname);
                    .setText(vo.citytoname);
        }else{
            holder.include_topaddress_tv_end
//                    .setText(vo.citytoname+vo.cityfromparentname);
                    .setText("");
        }


        if (isCancle) {
            holder.item_delete.setVisibility(View.VISIBLE);
            holder.item_notice.setVisibility(View.GONE);
        } else {
            holder.item_delete.setVisibility(View.GONE);
            holder.item_notice.setVisibility(View.VISIBLE);
        }

        // 删除路线
        holder.item_delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteListener.onDeleteListener(vo);
            }
        });
        final String lines = vo.cityfrom+""+vo.cityto;
        final int num = SPU.getPreferenceValueInt(mContext,lines,0,SPU.STORE_USERINFO);
        holder.item_notice.setText(num + "");
        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCancle) {
                    if(0!=num){
                        SPU.setPreferenceValueInt(mContext,lines,0,SPU.STORE_USERINFO);
                        notifyDataSetChanged();
                    }
                    deleteListener.onSelectItemListener(vo,num);
                }
            }
        });
        return convertView;
    }

    class ViewHolder {
        private TextView include_topaddress_tv_begin;
        private TextView include_topaddress_tv_end;
        private TextView item_delete;
        private TextView item_notice;
    }

    public void updateList(List<OffenLine> list) {
        this.datas.addAll(list);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this.datas.size(); i++) {
            OffenLine ol = this.datas.get(i);
            String addtags = "1"+ol.cityfrom + "" + ol.cityto;
            if (i == this.datas.size() - 1) {
                sb.append(addtags);
            } else {
                sb.append(addtags + ",");
            }
        }
        //保存tag
        SPU.setPreferenceValueString(mContext, SPU.MY_TAGS, sb.toString().trim(), SPU.STORE_USERINFO);
        notifyDataSetChanged();
    }

    public void updateIsCancle(Boolean b) {
        this.isCancle = b;
        notifyDataSetChanged();
    }

    public void clearListView() {
        this.datas.clear();
    }

    public void setDeleteListener(DeleteListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    public interface DeleteListener {
        void onDeleteListener(OffenLine vo);
        void onSelectItemListener(OffenLine vo,int n);
    }
}
