package com.hongbao5656.util;

import com.alibaba.fastjson.JSON;

import java.util.Date;

/**
 * Created by xastdm on 2016/5/14 12:27.
 */
public class Tools {

    public  String Test()
    {
        String json="{\n" +
                "    \"CreateDate\": \"2016-05-14 18:00\",\n" +
                "    \"GoodsName\": null,\n" +
                "    \"ID\": null,\n" +
                "    \"Price\": null\n" +
                "}";
        DataModel d=  JSON.parseObject(json, DataModel.class);
        return  d.getGoodsName();
    }

    public  class DataModel
    {
        public Date CreateDate;
        public  String GoodsName;
        public  long ID;
        public  int Price;

        public Date getCreateDate() {
            return CreateDate;
        }

        public void setCreateDate(Date createDate) {
            CreateDate = createDate;
        }

        public String getGoodsName() {
            return GoodsName;
        }

        public void setGoodsName(String goodsName) {
            GoodsName = goodsName;
        }

        public long getID() {
            return ID;
        }

        public void setID(long ID) {
            this.ID = ID;
        }

        public int getPrice() {
            return Price;
        }

        public void setPrice(int price) {
            Price = price;
        }
    }

}
