package com.hongbao5656.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;
/**
 * Created by xastdm on 2016/6/4 16:50.
 */
public abstract class CommonAdapter<T> extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context mContext;
    private List<T> datas;
    private int mItemlayoutId;//每一行item的布局文件的id

    public CommonAdapter(Context context,List<T> data,int itemlayoutId){
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.datas = data;
        this.mItemlayoutId = itemlayoutId;
    }


    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public T getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder =
                ViewHolder.getInstance(mContext,convertView,parent,mItemlayoutId,position);
        //填充数据
        getViewImplement(viewHolder,getItem(position));
        return viewHolder.getConvertView();
    }

    public abstract void getViewImplement(ViewHolder viewHolder,T item);

    public void clearListView() {
        this.datas.clear();
    }

}
