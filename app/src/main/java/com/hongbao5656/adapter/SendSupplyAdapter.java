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
import android.widget.TextView;

import com.hongbao5656.R;
import com.hongbao5656.activity.PublishSupplyActivity;
import com.hongbao5656.entity.SupplyItem;
import com.hongbao5656.util.SU;
import com.hongbao5656.util.TU;
import com.hongbao5656.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 已发布货源适配器
 *
 * @author dm
 */
public class SendSupplyAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<SupplyItem> datas;
    private List<Boolean> items;
    private Context ctx;
    private DeleteListener deleteListener;

    public SendSupplyAdapter(Context ctx, List<SupplyItem> datas) {
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
            cv = mInflater.inflate(R.layout.item_xlv_sendsupply_v2, null);
            vh = new VH();
            vh.include_topaddress_tv_begin = (TextView) cv.findViewById(R.id.include_topaddress_tv_begin);
            vh.include_topaddress_tv_end = (TextView) cv.findViewById(R.id.include_topaddress_tv_end);
//			vh.include_topaddress_tv_end_parent = (TextView) convertView.findViewById(R.id.include_topaddress_tv_end_parent);
            vh.item_tv_chechang = (TextView) cv.findViewById(R.id.item_tv_chechang);
            vh.item_tv_zhongliang = (TextView) cv.findViewById(R.id.item_tv_zhongliang);
            vh.item_tv_chexing = (TextView) cv.findViewById(R.id.item_tv_chexing);
            vh.item_tv_mark = (TextView) cv.findViewById(R.id.item_tv_mark);
            vh.item_tv_time = (TextView) cv.findViewById(R.id.item_tv_time);
            vh.item_tv_goodsname = (TextView) cv.findViewById(R.id.item_tv_goodsname);
            vh.tuzhongdingwei = (TextView) cv.findViewById(R.id.tuzhongdingwei);
            vh.bianjichongfa = (TextView) cv.findViewById(R.id.bianjichongfa);
            vh.lianxidesiji = (TextView) cv.findViewById(R.id.lianxidesiji);
            vh.item_tv_num = (TextView) cv.findViewById(R.id.item_tv_num);
            vh.shanchuhuoyuan = (ImageView) cv.findViewById(R.id.shanchuhuoyuan);
            vh.ll_siji = (LinearLayout)cv.findViewById(R.id.ll_siji);
//			holder.raiv_userphoto = (RoundedImageView) cv.findViewById(R.id.raiv_userphoto);
//			holder.tv_user = (TextView)cv.findViewById(R.id.tv_user);
//			holder.item_plv_sendsupply_btn_left = (Button) cv.findViewById(R.id.item_plv_sendsupply_btn_left);
//			holder.item_plv_sendsupply_btn_right = (Button) cv.findViewById(R.id.item_plv_sendsupply_btn_right);
            vh.ll_bottom = (LinearLayout) cv.findViewById(R.id.ll_bottom);
            cv.setTag(vh);
        }
        vh = (VH) cv.getTag();
        final SupplyItem vo = datas.get(position);//值对象

//		String tel = SPU.getPreferenceValueString(mContext,SPU.EQ_Tel,"",SPU.STORE_USERINFO);
//		Picasso.with(mContext).load(Uri.parse("http://www.56hb.net/image?uid=" + tel + "")).into(holder.raiv_userphoto);

//		if (!SU.isEmpty(vo.contact)){
//			holder.tv_user.setText(vo.contact);
//		}

        // 设置出发地
        if (!SU.isEmpty(vo.cityfromname)) {
//			holder.include_topaddress_tv_begin
//					.setText(vo.cityfromname+vo.cityfromparentname);
            vh.include_topaddress_tv_begin
                    .setText(vo.cityfromname);
        }else{
            vh.include_topaddress_tv_begin
                    .setText("");
        }
        // 设置目的地
        if (!SU.isEmpty(vo.citytoname)) {
            vh.include_topaddress_tv_end
                    .setText(vo.citytoname);
        }else{
            vh.include_topaddress_tv_end
                    .setText("");
        }

        // 设置目的地二级地名
//		if(!SU.isEmpty(vo.citytoparentname)) {
//			holder.include_topaddress_tv_end_parent
//					.setText(vo.citytoparentname);
//		}

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

//		long ll = 635957378450000000l;
        long ll = vo.lastupdate;
        String time = TimeUtils.getSureTime2("HH:mm", ll);
        if(!SU.isEmpty(time)){
            //右边时间
//		vh.item_tv_time.setText(TimeUtils.getSureTime2("MM-dd HH:mm",ll));
            vh.item_tv_time.setText(time);
        }else{
            vh.item_tv_time.setText("");
        }

        if(!SU.isEmpty(vo.remark)){
            vh.item_tv_mark.setText(vo.remark);//备注
        }else{
            vh.item_tv_mark.setText("");//备注
        }
//        vh.item_tv_num.setText(vo.CallCount);//已联系司机数

        if (vo.flag == true) {
            vh.ll_bottom.setVisibility(View.VISIBLE);
        } else {
            vh.ll_bottom.setVisibility(View.GONE);
        }

        // 编辑重发
        vh.bianjichongfa.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, PublishSupplyActivity.class);
//				intent.putExtra("id", datas.get(position).getId());
                //改传货源id为货源对象
                intent.putExtra("vo", vo.goodsid);
                ctx.startActivity(intent);
//				if (mContext instanceof CheckHistroySupplyActivity) {
//					((CheckHistroySupplyActivity) mContext).finish();
//				}
//				TU.show(mContext, "跳转至发布信息界面");
            }
        });

        // 删除货源
        vh.lianxidesiji.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//				Intent intent = new Intent(mContext, PublishSupplyActivity.class);
//				intent.putExtra("id", datas.get(position).getId());
//				mContext.startActivity(intent);
                deleteListener.onDeleteListener(vo.goodsid);
            }
        });

//		// 途中定位
        vh.tuzhongdingwei.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//				Intent intent = new Intent(mContext, PublishSupplyActivity.class);
//				intent.putExtra("id", datas.get(position).getId());
//				mContext.startActivity(intent);
//				deleteListener.onDeleteListener(vo.goodsid);
                TU.show(ctx, "开发中");
            }
        });
//
////		// 联系司机
        vh.ll_siji.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//				Intent intent = new Intent(mContext, PublishSupplyActivity.class);
//				intent.putExtra("id", datas.get(position).getId());
//				mContext.startActivity(intent);
//				deleteListener.onDeleteListener(vo.goodsid);
                TU.show(ctx, "开发中");
            }
        });


        cv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                VH vh = (VH) v.getTag();
//                if (vh.ll_bottom.getVisibility() > 0) {
//                    vh.ll_bottom.setVisibility(View.VISIBLE);
//                } else {
//                    vh.ll_bottom.setVisibility(View.GONE);
//                }
                for (int i = 0;i<datas.size();i++){
                    if (position == i){
                        datas.get(position).flag = true;
                    }else{
                        datas.get(i).flag = false;
                    }
                }
                update();
            }
        });


        return cv;
    }


    static class VH {
        TextView include_topaddress_tv_begin;
        TextView include_topaddress_tv_end;
        //TextView include_topaddress_tv_end_parent;
//		Button item_plv_sendsupply_btn_left;
//		Button item_plv_sendsupply_btn_right;
        TextView item_tv_chechang;
        TextView item_tv_zhongliang;
        TextView item_tv_chexing;
        TextView tuzhongdingwei;
        TextView bianjichongfa;
        TextView lianxidesiji;
        ImageView shanchuhuoyuan;
        TextView item_tv_mark;
        TextView item_tv_time;
        TextView item_tv_goodsname;
        LinearLayout ll_siji;
        //		RoundedImageView raiv_userphoto;
//		TextView tv_user;
        LinearLayout ll_bottom;
        TextView item_tv_num;
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

    public void setDeleteListener(DeleteListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    public interface DeleteListener {
        void onDeleteListener(String goodsid);
    }
}
