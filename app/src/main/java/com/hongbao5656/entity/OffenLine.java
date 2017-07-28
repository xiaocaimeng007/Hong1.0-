package com.hongbao5656.entity;

import java.io.Serializable;

public class OffenLine implements Serializable {
    private static final long serialVersionUID = -2868717L;
    public String lineid;
    public String mobile;
    public int cityfrom;
    public int cityto;
    public int count;
    public String cityfromname;
    public String citytoname;
    public String cityfromparentname;
    public String citytoparentname;
    public String flaguse;

    @Override
    public boolean equals(Object o) {
        if (o instanceof String) {
            if (lineid == null) {
                return o == null;
            } else {
				return (cityfrom+""+cityto).equals(o);
                //return ((List<String>) o).indexOf(cityfrom + "" + cityto) > -1;
            }
        } else {
            return false;
        }
    }
}
