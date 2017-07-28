package com.hongbao5656.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.hongbao5656.R;
import com.hongbao5656.base.BaseActivity;
import com.hongbao5656.okhttp.HttpException;
import com.hongbao5656.util.Constants;
import com.hongbao5656.okhttp.HttpDataHandlerListener;
import com.hongbao5656.util.ParamsService;
import com.hongbao5656.util.ResponseParams;
import com.hongbao5656.util.SU;
import com.hongbao5656.util.TU;
import com.squareup.okhttp.Request;

import org.json.JSONException;

import java.util.LinkedHashMap;

/**
 * 意见反馈
 */
public class ComplaintActivity extends BaseActivity
        implements HttpDataHandlerListener {
    private EditText et_complaint;
    private Button btn_complaint;
    private CheckBox cb1;
    private CheckBox cb2;
    private CheckBox cb3;
    private CheckBox cb4;
    private CheckBox cb5;
    private boolean cb1b;
    private boolean cb2b;
    private boolean cb3b;
    private boolean cb4b;
    private boolean cb5b;
    private String goodsid;
//    private TextView iv_right;
//    private RelativeLayout rl_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);
        if (getIntent().hasExtra("goodsid")) {
            goodsid = getIntent().getStringExtra("goodsid");
        }
        initView();
        initMonitor();
    }

    public void initView() {
        et_complaint = (EditText) findViewById(R.id.et_complaint);
//        iv_right = (TextView)findViewById(R.id.iv_right);
//        rl_right = (RelativeLayout)findViewById(R.id.rl_right);
        ((RelativeLayout) findViewById(R.id.leftBt)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_complaint = (Button) findViewById(R.id.btn_complaint);
        cb1 = (CheckBox) findViewById(R.id.cb1);
        cb2 = (CheckBox) findViewById(R.id.cb2);
        cb3 = (CheckBox) findViewById(R.id.cb3);
        cb4 = (CheckBox) findViewById(R.id.cb4);
        cb5 = (CheckBox) findViewById(R.id.cb5);
    }

    public void initMonitor() {
        cb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cb1.isChecked()) {
                    cb1b = true;
                } else {
                    cb1b = false;
                }
            }
        });
        cb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cb2.isChecked()) {
                    cb2b = true;
                } else {
                    cb2b = false;
                }
            }
        });
        cb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cb2.isChecked()) {
                    cb2b = true;
                } else {
                    cb2b = false;
                }
            }
        });
        cb3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cb3.isChecked()) {
                    cb3b = true;
                } else {
                    cb3b = false;
                }
            }
        });
        cb4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cb4.isChecked()) {
                    cb4b = true;
                } else {
                    cb4b = false;
                }
            }
        });
        cb5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cb5.isChecked()) {
                    cb5b = true;
                } else {
                    cb5b = false;
                }
            }
        });

        btn_complaint.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String msgtext = et_complaint.getText().toString();
                if (cb5.isChecked() && SU.isEmpty(msgtext.trim())) {
                    TU.show(ComplaintActivity.this, "选择其他请填写具体说明");
                    return;
                }
                StringBuffer msgcheck = new StringBuffer();

                if (cb1.isChecked()) {
                    msgcheck.append("虚假信息/咋骗信息费;");
                }
                if (cb2.isChecked()) {
                    msgcheck.append("信息描述与实际不符;");
                }
                if (cb3.isChecked()) {
                    msgcheck.append("货主态度差/不接电话;");
                }
                if (cb4.isChecked()) {
                    msgcheck.append("货已走未删除;");
                }
                if (cb5.isChecked()) {
                    msgcheck.append("其他;");
                }

                if (SU.isEmpty(msgcheck)) {
                    TU.show(ComplaintActivity.this, "请选择投诉类别");
                    return;
                }

                //TODO  反馈的请求接口
                sendPostRequest(
                        Constants.complaint,
                        new ParamsService().requestKV(
                                "msgcheck", msgcheck.toString(),
                                "msgtext", msgtext,
                                "goodsid", goodsid, Constants.complaint),
                        ComplaintActivity.this,
                        false);
            }
        });
    }

    @Override
    public void setHandlerPostDataSuccess(int connectionId, Object data) throws JSONException {
        ResponseParams<LinkedHashMap> iParams =jiexiData(data);
        if (connectionId == Constants.complaint) {
            TU.show(this, "投诉成功");
//                initDatas(1);
            finish();
        }

    }

    @Override
    public void setHandlerPostDataError(int connectedId, Request request, HttpException httpException, String params, String content) throws JSONException {


    }

    @Override
    public void setHandlerGetDataSuccess(int connectionId, Object data) throws JSONException {

    }

    @Override
    public void setHandlerGetDataError(int connectionId, Object data) throws JSONException {

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}