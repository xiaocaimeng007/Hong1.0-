package com.hongbao5656.entity;

import java.io.Serializable;


public class Region implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public int Id;
	public int ParentId;
	public int Auxiliary;
	public String Name;
	public double lat;
	public double mlong;
}


