package com.hongbao5656.entity;

import java.io.Serializable;
public class SupplyDetailEdite implements Serializable {
	private static final long serialVersionUID = -2868717L;
	public String goodsid;
	public String account;
	public int cityfrom;
	public int cityto;
	public String goodsname;
	public String carlen = "";
	public String cartype = "";
	public float wv;
	public String unit;
	public String contact;
	public String contactmobile;
	public String remark;
	public String cityfromname;
	public String citytoname;
	public String cityfromparentname;
	public String citytoparentname;
	public long lastupdate;
	public String company;
	public String room;
	public String flagauthority;
	public String UserCompany;
	public String DeviceType;


	@Override
	public boolean equals(Object obj) {
		if(obj==null) return false;
		if(obj==this) return true ;
		if(!(obj instanceof SupplyDetailEdite))  return false;

		SupplyDetailEdite s=(SupplyDetailEdite)obj;
		return
						cityfrom==s.cityfrom &&
						cityto==s.cityto &&
						wv==s.wv &&
						lastupdate==s.lastupdate&&
						goodsid.equals(s.goodsid) &&
//						account.equals(s.account) &&
						goodsname.equals(s.goodsname) &&
						carlen.equals(s.carlen) &&
						cartype.equals(s.cartype) &&
						unit.equals(s.unit) &&
						contact.equals(s.contact) &&
						contactmobile.equals(s.contactmobile) &&
						remark.equals(s.remark) &&
						cityfromname.equals(s.cityfromname) &&
						cityfromparentname.equals(s.cityfromparentname) &&
						citytoparentname.equals(s.citytoparentname) &&
						citytoname.equals(s.citytoname) &&
						room.equals(s.room) &&
						company.equals(s.company);
	}
}
