package com.hongbao5656.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * Created by xastdm on 2016/6/4 15:14.
 * 每一行里面控件的父类View
 * 也就是lv的item
 */
public class ViewHolder {
    private SparseArray<View> mViews;//稀疏数组里面放的是一行里面所有的控件
    private int mPosition;
    private View mConvertView;//每一行对应的布局

    //采用单例模式
    private ViewHolder(Context context, ViewGroup parent,int layoutId,int position){
        this.mPosition = position;
        this.mViews = new SparseArray<View>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId,parent,false);
        mConvertView.setTag(this);
    }

    /**
     * 获取Viewholder对象
     */
    public static ViewHolder getInstance(Context context,View convertView, ViewGroup parent,int layoutId,int position){
        if (convertView == null) {
            return new ViewHolder(context,parent,layoutId,position);
        }
        return (ViewHolder)convertView.getTag();
    }

    /**
     * 通过控件的id得到相应的控件
     * 不知道控件类型
     * 要用到泛型
     */
    public <T extends View> T getView(int viewId){
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId,view);
        }
        return (T)view;
    }

    /**
     * 为TextView赋值
     */
    public ViewHolder setText(int viewId,String text){
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    /**
     * 为ImageView设置图片
     */
    public ViewHolder setImageBitmap(int viewId, Bitmap bm){
        ((ImageView)getView(viewId)).setImageBitmap(bm);
        return this;
    }


    public int getPosition(){
        return mPosition;
    }

    public View getConvertView(){
        return mConvertView;
    }


}























