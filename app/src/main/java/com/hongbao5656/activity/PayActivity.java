package com.hongbao5656.activity;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.hongbao5656.R;

public class PayActivity extends Activity {
    private TextView tv_alipay;
    private LinearLayout ll_alipay;
    private TextView tv_wxpay;
    private LinearLayout ll_wxpay;
    private TextView tv_fkbxian;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        initView();
    }

    private void initView() {
        //左上返回键
        findViewById(R.id.leftBt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //购买10元放空保险
        tv_fkbxian = (TextView)findViewById(R.id.tv_fkbxian);
        tv_fkbxian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (0 == Integer.parseInt(tv_fkbxian.getTag().toString())) {
                    tv_fkbxian.setTag(1);
                    Drawable drawable = getResources().getDrawable(R.drawable.dagou_pre);
                    /// 这一步必须要做,否则不会显示.
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    tv_fkbxian.setCompoundDrawables(drawable, null, null, null);
                } else {
                    tv_fkbxian.setTag(0);
                    Drawable drawable = getResources().getDrawable(R.drawable.dagou_nor);
                    /// 这一步必须要做,否则不会显示.
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    tv_fkbxian.setCompoundDrawables(drawable, null, null, null);
                }
            }
        });
        //支付方式
        tv_alipay = (TextView) findViewById(R.id.tv_alipay);
        ll_alipay = (LinearLayout) findViewById(R.id.ll_alipay);
        tv_wxpay = (TextView) findViewById(R.id.tv_wxipay);
        ll_wxpay = (LinearLayout) findViewById(R.id.ll_wxpay);
        ll_alipay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (0 == Integer.parseInt(tv_alipay.getTag().toString())) {
                    selected(tv_alipay);
                    unSelected(tv_wxpay);
                } else {
                    unSelected(tv_alipay);
                    selected(tv_wxpay);
                }
            }
        });
        ll_wxpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (0 == Integer.parseInt(tv_wxpay.getTag().toString())) {
                    selected(tv_wxpay);
                    unSelected(tv_alipay);
                } else {
                    unSelected(tv_wxpay);
                    selected(tv_alipay);
                }
            }
        });
    }

    private void unSelected(TextView tv) {
        tv.setTag(0);
        Drawable drawable = getResources().getDrawable(R.drawable.white_dot);
        /// 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        tv.setCompoundDrawables(drawable, null, null, null);
    }

    private void selected(TextView tv) {
        tv.setTag(1);
        Drawable drawable = getResources().getDrawable(R.drawable.red_dot);
        /// 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        tv.setCompoundDrawables(drawable, null, null, null);
    }
}
