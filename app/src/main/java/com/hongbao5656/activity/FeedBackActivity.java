package com.hongbao5656.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hongbao5656.R;
import com.hongbao5656.base.App;
import com.hongbao5656.base.BaseActivity;
import com.hongbao5656.okhttp.HttpException;
import com.hongbao5656.util.Constants;
import com.hongbao5656.okhttp.HttpDataHandlerListener;
import com.hongbao5656.util.IMap;
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
public class FeedBackActivity extends BaseActivity implements HttpDataHandlerListener {
    private EditText et_feedback;
    private TextView iv_right;
    private RelativeLayout rl_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initView();
        initMonitor();
    }

    public void initView() {
        et_feedback = (EditText) findViewById(R.id.et_feedback);
        iv_right = (TextView) findViewById(R.id.iv_right);
        rl_right = (RelativeLayout) findViewById(R.id.rl_right);
        ((RelativeLayout) findViewById(R.id.leftBt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public void initMonitor() {
        rl_right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = et_feedback.getText().toString();
                if (SU.isEmpty(content.trim())) {
                    TU.show(FeedBackActivity.this, "反馈的内容不能为空!");
                    return;
                }
                //TODO  反馈的请求接口
                sendPostRequest(
                        Constants.feedBack,
                        new ParamsService().requestKV("Opinion", content, Constants.feedBack),
                        FeedBackActivity.this,
                        false);
            }
        });
    }

    @Override
    public void setHandlerPostDataSuccess(int connectionId, Object data) throws JSONException {
        ResponseParams<LinkedHashMap> iParams =jiexiData(data);
        if (connectionId == Constants.feedBack) {
            TU.show(this, "提交成功");
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