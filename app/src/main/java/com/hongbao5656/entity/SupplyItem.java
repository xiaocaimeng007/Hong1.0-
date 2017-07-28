package com.hongbao5656.entity;

import java.io.Serializable;

public class SupplyItem implements Serializable {
//	private static final long serialVersionUID = -2868717L;
	public String goodsid="";
	public String isid = "";
	public int cityfrom;
	public int cityto;
	public String goodsname="";
	public String carlen="";
	public String cartype="";
	public float wv;
	public String unit="";
	public String contact="";
	public String contactmobile="";
	public String remark="";
	public String cityfromname="";
	public String citytoname="";
	public String cityfromparentname="";
	public String citytoparentname="";
	public long lastupdate;
	public String ImageURL="";
	public String JpushTags;
    public int toview;
	public boolean flag;
	public String phone;
	public String username;
	public String carno;
	public String money;
	public String laiyuan;
	public String hpl;
	public String txbj;
	public int CallCount;
	public String account;//添加关注即添加到熟车



	@Override
	public boolean equals(Object o) {
		if (o instanceof String) {
				return (cityfrom+""+cityto).equals(o);
				//return ((List<String>) o).indexOf(cityfrom + "" + cityto) > -1;
		} else {
			return false;
		}
	}

}
