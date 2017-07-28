package com.hongbao5656.util;
import android.app.Activity;
import android.util.DisplayMetrics;

import com.example.aaron.library.MLog;

/**
 * Created by xastdm on 2016/6/6 14:24.
 */
public class UU {

    public static void getUIInfo(Activity activity) {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        MLog.i(
                         "当前客户机屏幕参数：density:" + displayMetrics.density
                        +" densityDpi:" + displayMetrics.densityDpi
                        +" xdpi:"+displayMetrics.xdpi
                        +" ydpi:"+displayMetrics.ydpi
                        +" height: "+ displayMetrics.heightPixels
                        +" width: "+ displayMetrics.widthPixels);
    }
}
