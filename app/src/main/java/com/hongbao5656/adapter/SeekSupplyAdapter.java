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
import com.hongbao5656.entity.SupplyItem;
import com.hongbao5656.util.MyArrayList;
import com.hongbao5656.util.SPU;
import com.hongbao5656.util.SU;
import com.hongbao5656.util.TimeUtils;
import java.util.List;
/**
 * 查找货源适配器 
 * @author dm
 */
public class SeekSupplyAdapter extends BaseAdapter{
	private LayoutInflater mInflater;
	public MyArrayList<SupplyItem> datas;
	private Context mContext;
	private TelListener telListener;


	public SeekSupplyAdapter(Context context, List<SupplyItem> datas) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		if (this.datas == null)
			this.datas = new MyArrayList<SupplyItem>();
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
	public SupplyItem getItem(int position) {
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
			convertView = mInflater.inflate(R.layout.item_xlv_seeksupply_v2,null);
			holder = new ViewHolder();
			holder.include_topaddress_tv_begin = (TextView) convertView.findViewById(R.id.include_topaddress_tv_begin);

			holder.include_topaddress_tv_end = (TextView) convertView.findViewById(R.id.include_topaddress_tv_end);

//			holder.include_topaddress_tv_end_parent = (TextView) convertView.findViewById(R.id.include_topaddress_tv_end_parent);
			holder.item_tv_chechang = (TextView) convertView.findViewById(R.id.item_tv_chechang);
			holder.item_tv_zhongliang = (TextView) convertView.findViewById(R.id.item_tv_zhongliang);
			holder.item_tv_chexing = (TextView) convertView.findViewById(R.id.item_tv_chexing);

			holder.item_tv_mark = (TextView) convertView.findViewById(R.id.item_tv_mark);

			holder.item_tv_time = (TextView) convertView.findViewById(R.id.item_tv_time);

			holder.item_tv_goodsname = (TextView) convertView.findViewById(R.id.item_tv_goodsname);

			holder.v_ifread = (View) convertView.findViewById(R.id.v_ifread);
//			holder.raiv_userphoto = (RoundedImageView) convertView.findViewById(R.id.raiv_userphoto);
//			holder.tv_user = (TextView)convertView.findViewById(R.id.tv_user);
//			holder.item_iv_call = (ImageView) convertView.findViewById(R.id.item_iv_call);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}


		final ViewHolder mHolder =  holder;

		final SupplyItem vo = datas.get(position);

//		Picasso.with(mContext).load(vo.ImageURL).into(holder.raiv_userphoto);

//		if (!SU.isEmpty(vo.contact)){
//			holder.tv_user.setText(vo.contact);
//		}

		int fontsize = SPU.getPreferenceValueInt(mContext,SPU.FONTSIZE,-1, SPU.STORE_USERINFO);

		if (vo.toview==0){
			holder.v_ifread.setVisibility(View.VISIBLE);
		}else{
			holder.v_ifread.setVisibility(View.GONE);
		}


		// 设置出发地
		if (!SU.isEmpty(vo.cityfromname)) {
//			holder.include_topaddress_tv_begin
//					.setText(vo.cityfromname+vo.cityfromparentname);
			holder.include_topaddress_tv_begin
					.setText(vo.cityfromname);

		}else{
			holder.include_topaddress_tv_begin
					.setText("");
		}

		// 设置目的地
		if(!SU.isEmpty(vo.citytoname)) {
			holder.include_topaddress_tv_end
					.setText(vo.citytoname);
		}else{
			holder.include_topaddress_tv_end
					.setText("");
		}

		// 设置目的地二级地名
//		if(!SU.isEmpty(vo.citytoparentname)) {
//			holder.include_topaddress_tv_end_parent
//					.setText(vo.citytoparentname);
//		}
		if(!SU.isEmpty(vo.goodsname)) {//货物名称
			holder.item_tv_goodsname.setText(vo.goodsname);
		}else{//特别是像这种不是必选显示的一定要加else置空 不然会乱掉
			holder.item_tv_goodsname.setText("");
		}


		if(!SU.isEmpty(vo.unit)) {//重量/体积单位
			holder.item_tv_zhongliang.setText(vo.wv+vo.unit);
		}else{
			holder.item_tv_zhongliang.setText("");
		}

		if(!SU.isEmpty(vo.carlen)) {//车长
			holder.item_tv_chechang.setText(vo.carlen);
		}else{
			holder.item_tv_chechang.setText("");
		}

		if(!SU.isEmpty(" "+vo.cartype)) {//车型
			holder.item_tv_chexing.setText(" "+vo.cartype);
		}else{
			holder.item_tv_chexing.setText("");
		}

		long ll = vo.lastupdate;//		long ll = 635957378450000000l;
//		String time = TimeUtils.getSureTime2("HH:mm",ll);
		String timepre = TimeUtils.getSureTime2("yyyy-MM-dd HH:mm",ll);
		String time = TimeUtils.formatDateTime(timepre);

		if(!SU.isEmpty(time)){
			//右边时间
//		holder.item_tv_time.setText(TimeUtils.getSureTime2("MM-dd HH:mm",ll));
			holder.item_tv_time.setText(time);
//		holder.item_plv_sendsupply_tv_righttime.setText(vo.lastupdate);
		}else{
			holder.item_tv_time.setText("");
		}

		if(!SU.isEmpty(" "+vo.remark)) {//备注
			if(11<=vo.remark.length()){
				holder.item_tv_mark.setText(" "+vo.remark.substring(0,11)+"...");
			}else{
				holder.item_tv_mark.setText(" "+vo.remark);
			}
		}else{
			holder.item_tv_mark.setText("");
		}

		if(fontsize == -1){
			holder.include_topaddress_tv_end.setTextSize(20);
			holder.include_topaddress_tv_begin.setTextSize(20);
			holder.item_tv_mark.setTextSize(16);
			holder.item_tv_goodsname.setTextSize(16);
			holder.item_tv_chexing.setTextSize(16);
			holder.item_tv_time.setTextSize(16);
		}else{
			holder.include_topaddress_tv_end.setTextSize(16+fontsize*2);
			holder.include_topaddress_tv_begin.setTextSize(16+fontsize*2);
			holder.item_tv_mark.setTextSize(12+fontsize);
			holder.item_tv_goodsname.setTextSize(12+fontsize);
			holder.item_tv_chexing.setTextSize(12+fontsize);
			holder.item_tv_time.setTextSize(12+fontsize);
		}



		// 拨打电话
//		holder.item_iv_call.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
////				Uri uri = Uri.parse("tel:"+vo.contactmobile);
////				Intent it = new Intent(Intent.ACTION_DIAL, uri);
////				mContext.startActivity(it);
//				telListener.onTelListener(vo.goodsid,vo.contactmobile);
//			}
//		});
//		 查看详情
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mHolder.v_ifread.setVisibility(View.GONE);
//				holder.v_ifread.setVisibility(View.GONE);
//				vo.toview=0;
//				notifyDataSetChanged();
				Intent intent = new Intent(mContext, SupplyDetailActivity.class);
				intent.putExtra("vo", vo.goodsid);
				mContext.startActivity(intent);
			}
		});
		return convertView;
	}

	class ViewHolder {
		private TextView include_topaddress_tv_begin;
		private TextView include_topaddress_tv_end;
//		private TextView include_topaddress_tv_end_parent;
		private TextView item_tv_chechang;
		private TextView item_tv_zhongliang;
		private TextView item_tv_chexing;
		private ImageView item_iv_call;
		private TextView item_tv_mark;
		private TextView item_tv_time;
		private TextView item_tv_goodsname;
//		private RoundedImageView raiv_userphoto;
//		private TextView tv_user;
		private View v_ifread;
	}
	public void updateList(List<SupplyItem> list) {
		this.datas.addAll(list);
		notifyDataSetChanged();
	}


	public void updateListStart(SupplyItem si) {
		this.datas.add(0,si);
		notifyDataSetChanged();
	}

//	public void updateList(SupplyItem si) {
//		String[] str = si.JpushTags.split(",");
//// else{//推来的货源id在datas中没有直接对应的
//		for (int i = 0; i < str.length; i++) {
//			SupplyItem o2 = this.datas.FindFirstOrDeault(str[i].trim());
//			if (o2!=null){
//				this.datas.add(0, si);
//				notifyDataSetChanged();
//				break;
//			}
//		}
//
//	}
	public void clearListView() {
		this.datas.clear();
	}

	public void setTelListener(TelListener telListener) {
		this.telListener = telListener;
	}

	public interface TelListener {
		void onTelListener(String goodsid,String contactmobile);
	}
}
