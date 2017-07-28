package com.hongbao5656.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.aaron.library.MLog;
import com.hongbao5656.service.LocationServiceForGaoDe;

/**
 * Created by xastdm on 2016/7/7 13:09.
 */
public class NetReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
//        TU.show(context,"网络变化广播收到后重新启动！");
        MLog.i("网络变化广播收到后重新启动！");
        context.startService(new Intent(context, LocationServiceForGaoDe.class));
    }
}
