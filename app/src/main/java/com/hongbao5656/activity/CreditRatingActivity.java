package com.hongbao5656.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.hongbao5656.R;

public class CreditRatingActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creditrating);
        ((RelativeLayout)findViewById(R.id.leftBt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreditRatingActivity.this.finish();
            }
        });
    }


}
