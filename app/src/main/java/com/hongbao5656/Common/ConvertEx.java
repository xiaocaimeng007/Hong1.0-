package com.hongbao5656.Common;

import android.text.TextUtils;

/**
 * Created by garso on 2017/4/19.
 */
public class ConvertEx {
    public static float ToFloat(String str) {
        if (TextUtils.isEmpty(str)) {
            return 0;
        } else {
            try {
                return Float.parseFloat(str.trim());
            } catch (Exception e) {
                return 0;
            }

        }
    }
}
