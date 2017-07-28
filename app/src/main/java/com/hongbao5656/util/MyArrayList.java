package com.hongbao5656.util;

import java.util.ArrayList;

/**
 * Created by xastdm on 2016/5/18 11:58.
 */
public  class  MyArrayList<T> extends ArrayList<T>
{

    @Override public int indexOf(Object object) {
        int s = size();
        if (object != null) {
            for (int i = 0; i < s; i++) {
                if (get(i).equals(object)) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < s; i++) {
                if (get(i) == null) {
                    return i;
                }
            }
        }
        return -1;
    }

    public  T FindFirstOrDeault(Object o)
    {
        int index=this.indexOf(o);
        if(index < 0){
            return null;
        }
        return get(index);
    }
}

