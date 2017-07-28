package com.hongbao5656.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;
import com.hongbao5656.R;
import com.hongbao5656.activity.CarDetailActivity;
import com.hongbao5656.entity.SeekCarsShow;
import com.hongbao5656.util.SPU;
import com.hongbao5656.util.SU;
import com.hongbao5656.util.TimeUtils;
import com.hongbao5656.view.AlwaysMarqueeTextView;
import com.hongbao5656.view.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * 已发布货源适配器 
 * @author dm
 */
public class SeekCarsShowAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<SeekCarsShow> datas;
	private Context mContext;
	private DeleteListener deleteListener;
	public SeekCarsShowAdapter(Context context, List<SeekCarsShow> datas) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		this.datas = datas;
		if (this.datas == null) {
			this.datas = new ArrayList<SeekCarsShow>();
		}
	}
	@Override
	public int getCount() {
		return datas.size();
	}
	@Override
	public SeekCarsShow getItem(int position) {
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
			convertView = mInflater.inflate(R.layout.item_xlv_seekcarsshow,null);
			holder = new ViewHolder();
			holder.include_topaddress_tv_begin = (TextView) convertView.findViewById(R.id.include_topaddress_tv_begin);
			holder.include_topaddress_tv_end = (TextView) convertView.findViewById(R.id.include_topaddress_tv_end);
			holder.chechang = (TextView) convertView.findViewById(R.id.chechang);
			holder.chexing = (TextView) convertView.findViewById(R.id.chexing);
			holder.zhongliang = (TextView) convertView.findViewById(R.id.zhongliang);
			holder.tv_user = (TextView) convertView.findViewById(R.id.tv_user);
			holder.raiv_userphoto = (RoundedImageView) convertView.findViewById(R.id.raiv_userphoto);
			holder.kongcheshijian = (TextView) convertView.findViewById(R.id.kongcheshijian);
			holder.item_iv_call = (ImageView) convertView.findViewById(R.id.item_iv_call);
			holder.location = (AlwaysMarqueeTextView) convertView.findViewById(R.id.location);
			holder.juli = (TextView) convertView.findViewById(R.id.juli);
//			holder.item_plv_sendsupply_tv_leftcontent = (TextView) convertView.findViewById(R.id.item_plv_sendsupply_tv_leftcontent);
//			holder.item_plv_sendsupply_tv_leftcontentRmarks = (TextView) convertView.findViewById(R.id.item_plv_sendsupply_tv_leftcontentRmarks);
//			holder.item_plv_sendsupply_tv_righttime = (TextView) convertView.findViewById(R.id.item_plv_sendsupply_tv_righttime);
//			holder.item_plv_sendsupply_btn_left = (Button) convertView.findViewById(R.id.item_plv_sendsupply_btn_left);
//			holder.item_plv_sendsupply_btn_right = (ImageButton) convertView.findViewById(R.id.item_plv_sendsupply_btn_right);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final SeekCarsShow vo = datas.get(position);


//		String ss = "上海市嘉定区蕴北路靠近鸿宝物流信息园时间";
//		int sheng = ss.indexOf("省");
//		int shi = ss.indexOf("市");
//		int xian = ss.indexOf("县");
//		int qu = ss.indexOf("区");
//		String lsheng =  ss.substring(0,qu);
//		String location = lsheng;


		// 设置出发地
		if(!SU.isEmpty(vo.cityfromname)) {
			holder.include_topaddress_tv_begin.setText(vo.cityfromname);
			if (!SU.isEmpty(vo.msg)){
				holder.location.setText(vo.msg);
			}else{
				holder.location.setText(vo.cityfromname);
			}
		}else{
			holder.include_topaddress_tv_begin.setText("");
			holder.location.setText("");
		}


		// 设置目的地
		if(!SU.isEmpty(vo.citytoname)) {
			holder.include_topaddress_tv_end.setText(vo.citytoname);
		}else{
			holder.include_topaddress_tv_end.setText("");
		}
		// 空车开始时间
		long ls = vo.starttime;
		String kssj = "";
		String jssj = "";
		if(ls!=0) {
			kssj = TimeUtils.getSureTime2("MM-dd HH:mm",ls);
		}
		// 空车结束时间
		long le = vo.endtime;
		if (le==0){
			jssj = "装车为止";
		}else{
			jssj = TimeUtils.getSureTime2("MM-dd HH:mm",le);
		}

		holder.kongcheshijian.setText("空车时间："+kssj+" 到 "+jssj);

		// 拨打电话
		holder.item_iv_call.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:" + vo.phone));
				mContext.startActivity(intent);
			}
		});

		if(!SU.isEmpty(vo.carlen)){
			holder.chechang.setText(vo.carlen);
		}else{
			holder.chechang.setText("");
		}

		if(!SU.isEmpty(vo.cartype)){
			holder.chexing.setText(vo.cartype);
		}else{
			holder.chexing.setText("");
		}

		if (0.0 == vo.wt){
			holder.zhongliang.setText("整车");
		}else{
			holder.zhongliang.setText(String.valueOf(vo.wt)+"吨");
		}

		if(!SU.isEmpty(vo.username)){
			holder.tv_user.setText(vo.username);
		}else{
			holder.tv_user.setText("");
		}

		Picasso.with(mContext).load(vo.ImageURL).into(holder.raiv_userphoto);


		LatLng latLng;
		if (vo.lastLongitude == 0.0 || vo.lastLatitude==0.0){
			latLng = new LatLng(vo.fromLatitude,vo.fromLongitude);
		}else{
			latLng = new LatLng(vo.lastLongitude,vo.lastLatitude);
//			latLng = new LatLng(27.9333344,120.571877);
		}

		double lastLongitude = Double.parseDouble(SPU.getPreferenceValueString(mContext, SPU.NEW_LAST_LONGITUDE, "0.0", SPU.STORE_USERINFO));
		double lastLatitude = Double.parseDouble(SPU.getPreferenceValueString(mContext, SPU.NEW_LAST_LATITUDE, "0.0", SPU.STORE_USERINFO));
		LatLng latLng2;
		if (lastLongitude == 0.0 || lastLatitude==0.0){
			latLng2 = new LatLng(31.32318,121.323944);
		}else{
			latLng2 = new LatLng(lastLongitude,lastLatitude);
		}
		String  jl = String .valueOf(Math.round(AMapUtils.calculateLineDistance(latLng, latLng2)/100));
		holder.juli.setText("约"+jl+"km");


		//查看详情
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				holder.v_ifread.setVisibility(View.GONE);
				Intent intent = new Intent(mContext, CarDetailActivity.class);
				intent.putExtra("account", vo.account);
				mContext.startActivity(intent);
			}
		});

		return convertView;
	}
	class ViewHolder {
		private TextView include_topaddress_tv_begin;
		private TextView include_topaddress_tv_end;
//		private TextView item_plv_sendsupply_tv_leftcontent;
//		private ImageButton item_plv_sendsupply_btn_right;
//		private TextView item_plv_sendsupply_tv_leftcontentRmarks;
		TextView chechang;
		TextView zhongliang;
		TextView chexing;
		TextView tv_user;
		RoundedImageView raiv_userphoto;
		TextView kongcheshijian;
		ImageView item_iv_call;
		AlwaysMarqueeTextView location;
		TextView juli;
	}
	public void updateList(List<SeekCarsShow> list) {
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
