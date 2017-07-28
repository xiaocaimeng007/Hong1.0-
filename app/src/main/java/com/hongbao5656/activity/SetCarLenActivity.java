package com.hongbao5656.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hongbao5656.R;
import com.hongbao5656.base.App;
import com.hongbao5656.base.BaseActivity;
import com.hongbao5656.okhttp.HttpException;
import com.hongbao5656.util.Constants;
import com.hongbao5656.entity.CityTo;
import com.hongbao5656.okhttp.HttpDataHandlerListener;
import com.hongbao5656.util.ParamsService;
import com.hongbao5656.util.ResponseParams;
import com.hongbao5656.util.SPU;
import com.hongbao5656.util.TU;
import com.squareup.okhttp.Request;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class SetCarLenActivity extends BaseActivity implements
        HttpDataHandlerListener {
    private Context mContext;
    private EditText et_endcarlen;
    private EditText et_startlen;
    private Button publish_commit;
    private String endcarlen;
    private String startcarlen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setcarlen);
        this.mContext = SetCarLenActivity.this;
        initView();
        initListener();
    }

    private void setCarlen(String startlen, String endlen) {
        SPU.setPreferenceValueString(this, SPU.LEN, startlen + "米->" + endlen + "米", SPU.STORE_USERINFO);
        sendPostRequest(
                Constants.esetcarlen,
                new ParamsService().requestKV(
                        "lenstart", startlen,
                        "lenend", endlen, Constants.esetcarlen
                ), this, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private ArrayList<CityTo> cityto = new ArrayList<CityTo>();

    private void initListener() {
        publish_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endcarlen = et_endcarlen.getText().toString();
                startcarlen = et_startlen.getText().toString();
                if (TextUtils.isEmpty(startcarlen)) {
                    TU.show(SetCarLenActivity.this, "起始车长不能为空");
                    return;
                }
                if (startcarlen.endsWith(".")) {
                    TU.show(SetCarLenActivity.this, "起始车长不能以.结尾");
                    return;
                }
                if (TextUtils.isEmpty(endcarlen)) {
                    TU.show(SetCarLenActivity.this, "结束车长不能为空");
                    return;
                }
                if (endcarlen.endsWith(".")) {
                    TU.show(SetCarLenActivity.this, "结束车长不能以.结尾");
                    return;
                }
                setCarlen(startcarlen, endcarlen);
            }
        });

    }

    private void initView() {
        findViewById(R.id.leftBt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        float startcarlen = SPU.getPreferenceValueFloat(this, SPU.startcarlen, 0.0f, SPU.STORE_USERINFO);
        float endcarlen = SPU.getPreferenceValueFloat(this, SPU.endcarlen, 0.0f, SPU.STORE_USERINFO);
        et_endcarlen = (EditText) findViewById(R.id.et_endcarlen);
        et_endcarlen.setText(endcarlen + "");
        et_startlen = (EditText) findViewById(R.id.et_startlen);
        et_startlen.setText(startcarlen + "");
        et_startlen.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_startlen.getText().toString().indexOf(".") >= 0) {
                    if (et_startlen.getText().toString().indexOf(".", et_startlen.getText().toString().indexOf(".") + 1) > 0) {
                        TU.show(SetCarLenActivity.this, "已经输入\".\"不能重复输入");
                        et_startlen.setText(et_startlen.getText().toString().substring(0, et_startlen.getText().toString().length() - 1));
                        et_startlen.setSelection(et_startlen.getText().toString().length());
                    }
                }
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);
                        et_startlen.setText(s);
                        et_startlen.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    et_startlen.setText(s);
                    et_startlen.setSelection(2);
                }
                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        et_startlen.setText(s.subSequence(0, 1));
                        et_startlen.setSelection(1);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        et_endcarlen.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_endcarlen.getText().toString().indexOf(".") >= 0) {
                    if (et_endcarlen.getText().toString().indexOf(".", et_endcarlen.getText().toString().indexOf(".") + 1) > 0) {
                        TU.show(SetCarLenActivity.this, "已经输入\".\"不能重复输入");
                        et_endcarlen.setText(et_endcarlen.getText().toString().substring(0, et_endcarlen.getText().toString().length() - 1));
                        et_endcarlen.setSelection(et_endcarlen.getText().toString().length());
                    }
                }
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);
                        et_endcarlen.setText(s);
                        et_endcarlen.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    et_endcarlen.setText(s);
                    et_endcarlen.setSelection(2);
                }

                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        et_endcarlen.setText(s.subSequence(0, 1));
                        et_endcarlen.setSelection(1);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        publish_commit = (Button) findViewById(R.id.publish_commit);
    }

    @Override
    public void setHandlerPostDataSuccess(int connectionId, Object data) throws JSONException {
        if (connectionId == Constants.esetcarlen) {
            TU.show(mContext, "保存成功");
            SPU.setPreferenceValueFloat(this, SPU.startcarlen, Float.parseFloat(startcarlen), SPU.STORE_USERINFO);
            SPU.setPreferenceValueFloat(this, SPU.endcarlen, Float.parseFloat(endcarlen), SPU.STORE_USERINFO);
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
}
