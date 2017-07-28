package com.hongbao5656.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hongbao5656.R;
import com.hongbao5656.activity.PublishSupplyActivity;
import com.hongbao5656.entity.SupplyItem;
import com.hongbao5656.util.SU;
import com.hongbao5656.util.TU;
import com.hongbao5656.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class OffenSupplyAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<SupplyItem> datas;
    private List<Boolean> items;
    private Context ctx;
    private OffenSupplyAdapterListener offenSupplyAdapterListener;

    public OffenSupplyAdapter(Context ctx, List<SupplyItem> datas) {
        this.ctx = ctx;
        this.mInflater = LayoutInflater.from(ctx);
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
        VH vh;
        if (cv == null) {
            cv = mInflater.inflate(R.layout.item_offensupply, null);
            vh = new VH();
            vh.include_topaddress_tv_begin = (TextView) cv.findViewById(R.id.include_topaddress_tv_begin);
            vh.include_topaddress_tv_end = (TextView) cv.findViewById(R.id.include_topaddress_tv_end);
            vh.item_tv_chechang = (TextView) cv.findViewById(R.id.item_tv_chechang);
            vh.item_tv_zhongliang = (TextView) cv.findViewById(R.id.item_tv_zhongliang);
            vh.item_tv_chexing = (TextView) cv.findViewById(R.id.item_tv_chexing);
            vh.item_tv_mark = (TextView) cv.findViewById(R.id.item_tv_mark);
            vh.item_tv_time = (TextView) cv.findViewById(R.id.item_tv_time);
            vh.item_tv_goodsname = (TextView) cv.findViewById(R.id.item_tv_goodsname);
            vh.shanchuhuoyuan = (RelativeLayout) cv.findViewById(R.id.shanchuhuoyuan);
            cv.setTag(vh);
        }
        vh = (VH) cv.getTag();
        final SupplyItem vo = datas.get(position);//值对象

        // 设置出发地
        if (!SU.isEmpty(vo.cityfromname)) {
            vh.include_topaddress_tv_begin.setText(vo.cityfromname);
        }else{
            vh.include_topaddress_tv_begin.setText("");
        }
        // 设置目的地
        if (!SU.isEmpty(vo.citytoname)) {
            vh.include_topaddress_tv_end.setText(vo.citytoname);
        }else{
            vh.include_topaddress_tv_begin.setText("");
        }

        if (!SU.isEmpty(vo.goodsname)) {//货物名称
            vh.item_tv_goodsname.setText(vo.goodsname);
        }else{
            vh.item_tv_goodsname.setText("");
        }


        if (!SU.isEmpty(vo.unit)) {//重量/体积单位
            vh.item_tv_zhongliang.setText(vo.wv + vo.unit);
        }else{
            vh.item_tv_zhongliang.setText(vo.wv + "");
        }


        if (!SU.isEmpty(vo.carlen)) {//车长
            vh.item_tv_chechang.setText(" " + vo.carlen);
        }else{
            vh.item_tv_chechang.setText("");
        }

        if (!SU.isEmpty(" " + vo.cartype)) {//车型
            vh.item_tv_chexing.setText(" " + vo.cartype);
        }else{
            vh.item_tv_chexing.setText("");
        }

        long ll = vo.lastupdate;
        String time = TimeUtils.getSureTime2("HH:mm", ll);
        if(!SU.isEmpty(time)){
            //右边时间
            vh.item_tv_time.setText(time);
        }else{
            vh.item_tv_time.setText("");
        }

        if(!SU.isEmpty(vo.remark)){
            vh.item_tv_mark.setText(vo.remark);
        }else{
            vh.item_tv_mark.setText("");
        }

        // 编辑重发
        cv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                offenSupplyAdapterListener.onCVClickListener(vo);
            }
        });

        // 删除货源
        vh.shanchuhuoyuan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                offenSupplyAdapterListener.onDeleteListener(vo.isid);
            }
        });
        return cv;
    }


    static class VH {
        TextView include_topaddress_tv_begin;
        TextView include_topaddress_tv_end;
        TextView item_tv_chechang;
        TextView item_tv_zhongliang;
        TextView item_tv_chexing;
        RelativeLayout shanchuhuoyuan;
        TextView item_tv_mark;
        TextView item_tv_time;
        TextView item_tv_goodsname;
    }


    public void update(){
        notifyDataSetChanged();
    }

    public void updateList(List<SupplyItem> list) {
        this.datas.addAll(list);
        notifyDataSetChanged();
    }

    public void clearListView() {
        this.datas.clear();
    }

    public void setOffenSupplyAdapterListener(OffenSupplyAdapterListener offenSupplyAdapterListener) {
        this.offenSupplyAdapterListener = offenSupplyAdapterListener;
    }

    public interface OffenSupplyAdapterListener {
        void onDeleteListener(String goodsid);
        void onCVClickListener(SupplyItem vo);
    }
}
