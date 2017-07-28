package com.hongbao5656.activity;

import android.os.Bundle;
import android.view.View;
import com.hongbao5656.R;
import com.hongbao5656.base.BaseActivity;

public class RechargeActivity extends BaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recgarge);
        findViewById(R.id.leftBt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
