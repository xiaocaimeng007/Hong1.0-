package com.hongbao5656.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hongbao5656.R;
import com.hongbao5656.activity.SupplyDetailActivity;
import com.hongbao5656.activity.SupplyDetailBaoJiaActivity;
import com.hongbao5656.entity.SupplyItemBaoJia;
import com.hongbao5656.util.MyArrayList;
import com.hongbao5656.util.SPU;
import com.hongbao5656.util.SU;
import com.hongbao5656.util.TimeUtils;

import java.util.List;

/**
 * 查找货源适配器
 *
 * @author dm
 */
public class SeekSupplyBaoJiaAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    public MyArrayList<SupplyItemBaoJia> datas;
    private Context mContext;
    private TelListener telListener;


    public SeekSupplyBaoJiaAdapter(Context context, List<SupplyItemBaoJia> datas) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        if (this.datas == null)
            this.datas = new MyArrayList<SupplyItemBaoJia>();
        else
            this.datas.clear();
        if (datas != null)
            this.datas.addAll(datas);
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public SupplyItemBaoJia getItem(int position) {
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
            convertView = mInflater.inflate(R.layout.item_xlv_seeksupply_baojia, null);
            holder = new ViewHolder();
            holder.include_topaddress_tv_begin = (TextView) convertView.findViewById(R.id.include_topaddress_tv_begin);

            holder.include_topaddress_tv_end = (TextView) convertView.findViewById(R.id.include_topaddress_tv_end);

            holder.item_tv_chechang = (TextView) convertView.findViewById(R.id.item_tv_chechang);
            holder.item_tv_zhongliang = (TextView) convertView.findViewById(R.id.item_tv_zhongliang);
            holder.item_tv_chexing = (TextView) convertView.findViewById(R.id.item_tv_chexing);

            holder.item_tv_mark = (TextView) convertView.findViewById(R.id.item_tv_mark);

            holder.item_tv_time = (TextView) convertView.findViewById(R.id.item_tv_time);

            holder.item_tv_goodsname = (TextView) convertView.findViewById(R.id.item_tv_goodsname);

            holder.v_ifread = (View) convertView.findViewById(R.id.v_ifread);
            holder.item_tv_baojia = (TextView) convertView.findViewById(R.id.item_tv_baojia);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        final ViewHolder mHolder = holder;

        final SupplyItemBaoJia vo = datas.get(position);


        int fontsize = SPU.getPreferenceValueInt(mContext, SPU.FONTSIZE, -1, SPU.STORE_USERINFO);

        holder.v_ifread.setVisibility(View.VISIBLE);


        holder.include_topaddress_tv_begin
                .setText(vo.CityFromName);
       /* // 设置出发地
        if (!SU.isEmpty(vo.cityfromname)) {

            holder.include_topaddress_tv_begin
                    .setText(vo.cityfromname);

        } else {
            holder.include_topaddress_tv_begin
                    .setText("");
        }*/

        // 设置目的地
        holder.include_topaddress_tv_end
                .setText(vo.CityToName);
      /*  if (!SU.isEmpty(vo.citytoname)) {
            holder.include_topaddress_tv_end
                    .setText(vo.citytoname);
        } else {
            holder.include_topaddress_tv_end
                    .setText("");
        }*/

        holder.item_tv_goodsname.setText(vo.GoodsName);
       /* if (!SU.isEmpty(vo.goodsname)) {//货物名称
            holder.item_tv_goodsname.setText(vo.goodsname);
        } else {//特别是像这种不是必选显示的一定要加else置空 不然会乱掉
            holder.item_tv_goodsname.setText("");
        }*/

        holder.item_tv_zhongliang.setText(vo.WV + vo.Unit);
      /*  if (!SU.isEmpty(vo.unit)) {//重量/体积单位
            holder.item_tv_zhongliang.setText(vo.wv + vo.unit);
        } else {
            holder.item_tv_zhongliang.setText("");
        }*/
        holder.item_tv_chechang.setText(vo.CarLen);
       /* if (!SU.isEmpty(vo.carlen)) {//车长
            holder.item_tv_chechang.setText(vo.carlen);
        } else {
            holder.item_tv_chechang.setText("");
        }*/
        holder.item_tv_chexing.setText(" " + vo.CarType);
     /*   if (!SU.isEmpty(" " + vo.cartype)) {//车型
            holder.item_tv_chexing.setText(" " + vo.cartype);
        } else {
            holder.item_tv_chexing.setText("");
        }*/
        holder.item_tv_baojia.setText(vo.Price + "");
        long ll = vo.LastUpdate;//		long ll = 635957378450000000l;
        String timepre = TimeUtils.getSureTime2("yyyy-MM-dd HH:mm", ll);
        String time = TimeUtils.formatDateTime(timepre);
        holder.item_tv_time.setText(time);
       /* if (!SU.isEmpty(time)) {//右边时间
            holder.item_tv_time.setText(time);
        } else {
            holder.item_tv_time.setText("");
        }*/
        if (11 <= vo.Remark.length())
            holder.item_tv_mark.setText(" " + vo.Remark.substring(0, 11) + "...");
        else
            holder.item_tv_mark.setText(" " + vo.Remark);

        if (fontsize == -1) {
            holder.include_topaddress_tv_end.setTextSize(20);
            holder.include_topaddress_tv_begin.setTextSize(20);
            holder.item_tv_mark.setTextSize(16);
            holder.item_tv_goodsname.setTextSize(16);
            holder.item_tv_chexing.setTextSize(16);
            holder.item_tv_time.setTextSize(16);
        } else {
            holder.include_topaddress_tv_end.setTextSize(16 + fontsize * 2);
            holder.include_topaddress_tv_begin.setTextSize(16 + fontsize * 2);
            holder.item_tv_mark.setTextSize(12 + fontsize);
            holder.item_tv_goodsname.setTextSize(12 + fontsize);
            holder.item_tv_chexing.setTextSize(12 + fontsize);
            holder.item_tv_time.setTextSize(12 + fontsize);
        }


//		 查看详情
        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mHolder.v_ifread.setVisibility(View.GONE);
                Intent intent = new Intent(mContext, SupplyDetailBaoJiaActivity.class);
                intent.putExtra("bjid", vo.BJNO);
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    class ViewHolder {
        private TextView include_topaddress_tv_begin;
        private TextView include_topaddress_tv_end;
        private TextView item_tv_chechang;
        private TextView item_tv_zhongliang;
        private TextView item_tv_chexing;
        private ImageView item_iv_call;
        private TextView item_tv_mark;
        private TextView item_tv_time;
        private TextView item_tv_goodsname;
        private View v_ifread;
        private TextView item_tv_baojia;
    }

    public void updateList(List<SupplyItemBaoJia> list) {
        this.datas.addAll(list);
        notifyDataSetChanged();
    }


    public void updateListStart(SupplyItemBaoJia si) {
        this.datas.add(0, si);
        notifyDataSetChanged();
    }


    public void clearListView() {
        this.datas.clear();
    }

    public void setTelListener(TelListener telListener) {
        this.telListener = telListener;
    }

    public interface TelListener {
        void onTelListener(String goodsid, String contactmobile);
    }
}
