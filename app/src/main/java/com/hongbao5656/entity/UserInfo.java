package com.hongbao5656.entity;

import java.io.Serializable;

/**
 * Created by xastdm on 2016/7/27 11:16.
 */
public class UserInfo implements Serializable{
    public String account;
    public String username;
    public String phone;
    public String carno;//VARCHAR(20),--车牌号
    public String carlen;//VARCHAR(10),--车长
    public String cartype ;// VARCHAR(10),--车型
    public int driver ; //INT DEFAULT 0,--0 未提交 1 已提交但未审核 2 审核通过 -1 审核未通过
    public String driverappmsg ; //NVARCHAR(50),
    public int driving1 ; //INT DEFAULT 0,
    public String driving1appmsg;// NVARCHAR(50),
    public int carimage ; //INT DEFAULT 0,
    public String carimageappmsg ; //NVARCHAR(50),
    public int imagecid1 ;// INT DEFAULT 0,
    public String ImageCid1AppMsg ;// NVARCHAR(50),
    public int userlevel ; //int ,--用户等级
    public int befocus ;// INT ,--被关注数 粉丝
    public double lastlongitude ; //FLOAT, --上次定位坐标
    public double lastlatitude ; //FLOAT, --上次定位坐标
    public String imagehead;
    public int isfocus;
    public int isbest;
    public String msg;
}
