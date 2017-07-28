package com.hongbao5656.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hongbao5656.R;
import com.hongbao5656.adapter.CityLevelAdapter;
import com.hongbao5656.adapter.CityLevelAdapter2;
import com.hongbao5656.adapter.CityLevelAdapter3;
import com.hongbao5656.base.App;
import com.hongbao5656.base.BaseActivity;
import com.hongbao5656.okhttp.HttpException;
import com.hongbao5656.util.Constants;
import com.hongbao5656.okhttp.HttpDataHandlerListener;
import com.hongbao5656.util.ImageCompress;
import com.hongbao5656.util.ImageCompress.CompressOptions;
import com.hongbao5656.util.ImageUtils;
import com.hongbao5656.util.ParamsService;
import com.hongbao5656.util.ResponseParams;
import com.hongbao5656.util.SPU;
import com.hongbao5656.util.SU;
import com.hongbao5656.util.TU;
import com.hongbao5656.view.RoundAngleImageView;
import com.hongbao5656.view.SelectPhotoWindow;
import com.squareup.okhttp.Request;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class DriverCertificationActivity extends BaseActivity implements
        HttpDataHandlerListener, OnClickListener, CityLevelAdapter.CitySupplyListener {
    private RelativeLayout rl_image;
    private InputMethodManager inputMethodManager;
    private SelectPhotoWindow selectphotoWindow;
    private String localPath;
    private RoundAngleImageView iv_person_photo;
    private Bitmap bitmap;
    private boolean isChecked;
    private boolean isChecked2;
    private boolean isChecked3;
    private Button btn_cc;
    private Button btn_cx;
    private RelativeLayout rl_jsz;
    private RelativeLayout rl_xszzm;
    //    private RelativeLayout rl_xszfm;
    private RelativeLayout rl_clz;
    private ImageView iv_jsz;
    private ImageView iv_xszzm;
    //    private ImageView iv_xszfm;
    private ImageView iv_clz;
    private EditText et_cph;
    //    private EditText et_weight;
    private boolean rl_image_b1;
    private boolean rl_image_b2;
    private boolean rl_image_b3;
    private String datas_jsz = "";
    private String datas_xszzm = "";
    private String datas_sfzzm = "";
    private String datas_clz = "";
    //    private Button btn_wv;
    private TextView iv_right;
    private TextView tv_jsz;
    private TextView tv_xszzm;
    private TextView tv_clz;
    //    private TextView tv_xszfm;
    private Context mContext;
    private View view;
    private TextView tv_select;
    private View view_car;
    private Button queding;
    private PopupWindow mWindow_car;
    private PopupWindow mWindow;
    private String carlong = "";
    private String cartype = "";
    //    private EditText publish_cccx_et;
    private GridView gv_ppw;
    private GridView gv_ppw_carlong;
    private GridView gv_ppw_cartype;
    private CityLevelAdapter cityLevelAdapter;
    private CityLevelAdapter2 cityLevelAdapter2;
    private CityLevelAdapter3 cityLevelAdapter3;
    private Button publish_cx_btn;
    private EditText et_real_name;
    private RelativeLayout rl_image2;
    private ImageView iv_sfzzm;
    private TextView tv_sfzzm;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = DriverCertificationActivity.this;
        setContentView(R.layout.activity_drivercertification);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        initView();
        initListener();
        initDatas();
        setInputUI(findViewById(R.id.rl_super), this);
    }

    private void initListener() {
        rl_jsz.setOnClickListener(this);
        rl_xszzm.setOnClickListener(this);
        rl_image2.setOnClickListener(this);
//        rl_xszfm.setOnClickListener(this);
        rl_clz.setOnClickListener(this);
        iv_right.setOnClickListener(this);
        et_cph.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String t = et_cph.getText().toString();
                String editable = et_cph.getText().toString();
                String str = SU.StringFilter(editable.toString());
                if (!editable.equals(str)) {
                    et_cph.setText(str);
                    et_cph.setSelection(str.length()); //光标置后
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
//        et_weight.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (et_weight.getText().toString().indexOf(".") >= 0) {
//                    if (et_weight.getText().toString().indexOf(".", et_weight.getText().toString().indexOf(".") + 1) > 0) {
//                        TU.show(DriverCertificationActivity.this, "已经输入\".\"不能重复输入");
//                        et_weight.setText(et_weight.getText().toString().substring(0, et_weight.getText().toString().length() - 1));
//                        et_weight.setSelection(et_weight.getText().toString().length());
//                    }
//                }
//
//                if (s.toString().contains(".")) {
//                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
//                        s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);
//                        et_weight.setText(s);
//                        et_weight.setSelection(s.length());
//                    }
//                }
//                if (s.toString().trim().substring(0).equals(".")) {
//                    s = "0" + s;
//                    et_weight.setText(s);
//                    et_weight.setSelection(2);
//                }
//
//                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
//                    if (!s.toString().substring(1, 2).equals(".")) {
//                        et_weight.setText(s.subSequence(0, 1));
//                        et_weight.setSelection(1);
//                    }
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
    }

    private void initDatas() {
        sendPostRequest(
                Constants.sjrzmx,
                new ParamsService().request(Constants.sjrzmx),
                this,
                false);
    }

    private void initView() {
        findViewById(R.id.leftBt).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWindow != null && mWindow.isShowing()) {
                    mWindow.dismiss();
                } else if (mWindow_car != null && mWindow_car.isShowing()) {
                    mWindow_car.dismiss();
                } else {
                    SPU.setPreferenceValueBoolean(mContext, SPU.IS_GRRZ, true, SPU.STORE_SETTINGS);
                    finish();
                }

            }
        });
        publish_cx_btn = (Button) findViewById(R.id.publish_cx_btn);
        rl_jsz = (RelativeLayout) findViewById(R.id.rl_jsz);
        rl_xszzm = (RelativeLayout) findViewById(R.id.rl_xszzm);
//        rl_xszfm = (RelativeLayout) findViewById(R.id.rl_xszfm);
        rl_clz = (RelativeLayout) findViewById(R.id.rl_clz);
        iv_jsz = (ImageView) findViewById(R.id.iv_jsz);
        iv_xszzm = (ImageView) findViewById(R.id.iv_xszzm);
//        iv_xszfm = (ImageView) findViewById(R.id.iv_xszfm);
        iv_clz = (ImageView) findViewById(R.id.iv_clz);
        et_cph = (EditText) findViewById(R.id.et_cph);
//        et_weight = (EditText) findViewById(R.id.et_weight);
//        publish_cccx_et = (EditText) findViewById(R.id.publish_cccx_et);
//        btn_cc = (Button) findViewById(R.id.btn_cc);
//        btn_cx = (Button) findViewById(R.id.btn_cx);
//        btn_wv = (Button) findViewById(R.id.btn_wv);
        iv_right = (TextView) findViewById(R.id.iv_right);
        tv_jsz = (TextView) findViewById(R.id.tv_jsz);
        tv_xszzm = (TextView) findViewById(R.id.tv_xszzm);
//        tv_xszfm = (TextView) findViewById(R.id.tv_xszfm);
        tv_clz = (TextView) findViewById(R.id.tv_clz);
        et_real_name = (EditText) findViewById(R.id.et_real_name);
        et_real_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String t = et_real_name.getText().toString();
                String editable = et_real_name.getText().toString();
                String str = SU.StringFilter(editable.toString());
                if (!editable.equals(str)) {
                    et_real_name.setText(str);
                    et_real_name.setSelection(str.length()); //光标置后
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        rl_image2 = (RelativeLayout) findViewById(R.id.rl_image2);
        iv_sfzzm = (ImageView) findViewById(R.id.iv_sfzzm);
        tv_sfzzm = (TextView) findViewById(R.id.tv_sfzzm);
        initPPW();
    }

    private void initPPW() {
        view = LayoutInflater.from(this).inflate(R.layout.ppw_supplytypeandwv_layout, null);
        tv_select = (TextView) view.findViewById(R.id.tv_select);
        view_car = LayoutInflater.from(this).inflate(R.layout.ppw_carlongtype_layout, null);
        queding = (Button) view_car.findViewById(R.id.queding);
        queding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carlong = cityLevelAdapter2.getSelectItem();
                cartype = cityLevelAdapter3.getSelectItem();
                if (mWindow_car != null && mWindow_car.isShowing()) {
                    if (SU.isEmpty(carlong)) {
                        TU.show(DriverCertificationActivity.this, "请选择车长");
                        return;
                    } else if (SU.isEmpty(cartype)) {
                        TU.show(DriverCertificationActivity.this, "请选择车型");
                        return;
                    } else {
                        mWindow_car.dismiss();
                    }
                }
                publish_cx_btn.setText(carlong + "  " + cartype);
            }
        });
        gv_ppw = (GridView) view.findViewById(R.id.gv_ppw);
        gv_ppw_carlong = (GridView) view_car.findViewById(R.id.gv_ppw_carlong);
        gv_ppw_cartype = (GridView) view_car.findViewById(R.id.gv_ppw_cartype);

        mWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mWindow_car = new PopupWindow(view_car, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
    }

    private void initPopuWindow(List<String> datas, View view) {
        if (mWindow != null && mWindow.isShowing()) {
            mWindow.dismiss();
            return;
        }
        if (cityLevelAdapter == null) {
            cityLevelAdapter = new CityLevelAdapter(DriverCertificationActivity.this, datas);
            cityLevelAdapter.setSupplyListener(this);
            gv_ppw.setAdapter(cityLevelAdapter);
        } else {
            cityLevelAdapter.clearListView();
            cityLevelAdapter.upateList(datas);
        }

        mWindow.setAnimationStyle(R.style.PopupWindowAnimation);
        mWindow.setOutsideTouchable(true);
        mWindow.setFocusable(true);
        mWindow.setTouchable(true);
        mWindow.setBackgroundDrawable(getResources().getDrawable(R.color.base_top_bg_color));
        mWindow.showAsDropDown(view, 0, 0);
    }

    @Override
    public void setHandlerPostDataSuccess(int connectionId, Object data) throws JSONException {
        ResponseParams<LinkedHashMap> iParams =jiexiData(data);
        if (connectionId == Constants.sjyz) {//司机验证
            TU.show(this, "保存成功");
            SPU.setPreferenceValueBoolean(mContext, SPU.IS_GRRZ, true, SPU.STORE_SETTINGS);
            SPU.setPreferenceValueString(mContext, SPU.USERNAMENEW, username, SPU.STORE_USERINFO);
            App.username = username;
            DriverCertificationActivity.this.finish();
        } else if (connectionId == Constants.sjrzmx) {//司机认证明细
            et_real_name.setText(iParams.getData1().get("username").toString());
            et_cph.setText(iParams.getData1().get("carno").toString());
            carlong = iParams.getData1().get("carlen").toString();
            cartype = iParams.getData1().get("cartype").toString();
            if (!SU.isEmpty(carlong) || !SU.isEmpty(cartype)) {
                publish_cx_btn.setText(carlong + "  " + cartype);
            }
            if ("1".equals(iParams.getData1().get("driver").toString())) {
                iv_jsz.setBackgroundResource(R.drawable.dsh);
            } else if ("2".equals(iParams.getData1().get("driver").toString())) {
                iv_jsz.setBackgroundResource(R.drawable.ysh);
                rl_jsz.setClickable(false);
            } else if ("-1".equals(iParams.getData1().get("driver").toString())) {
                tv_jsz.setVisibility(View.VISIBLE);
                tv_jsz.setText("注:" + iParams.getData1().get("driverappmsg").toString());
                tv_jsz.setTextColor(Color.parseColor("#C73F3F"));
                iv_jsz.setBackgroundResource(R.drawable.wtg);
            } else {
                tv_jsz.setVisibility(View.GONE);
            }

            if ("1".equals(iParams.getData1().get("driving1").toString())) {
                iv_xszzm.setBackgroundResource(R.drawable.dsh);
            } else if ("2".equals(iParams.getData1().get("driving1").toString())) {
                iv_xszzm.setBackgroundResource(R.drawable.ysh);
                rl_xszzm.setClickable(false);
            } else if ("-1".equals(iParams.getData1().get("driving1").toString())) {
                tv_xszzm.setVisibility(View.VISIBLE);
                tv_xszzm.setText("注:" + iParams.getData1().get("driving1appmsg").toString());
                tv_xszzm.setTextColor(Color.parseColor("#C73F3F"));
                iv_xszzm.setBackgroundResource(R.drawable.wtg);
            } else {
                tv_xszzm.setVisibility(View.GONE);
            }

            if ("1".equals(iParams.getData1().get("carimage").toString())) {
                iv_clz.setBackgroundResource(R.drawable.dsh);
            } else if ("2".equals(iParams.getData1().get("carimage").toString())) {
                iv_clz.setBackgroundResource(R.drawable.ysh);
                rl_clz.setClickable(false);
            } else if ("-1".equals(iParams.getData1().get("carimage").toString())) {
                tv_clz.setVisibility(View.VISIBLE);
                tv_clz.setText("注:" + iParams.getData1().get("carimageappmsg").toString());
                tv_clz.setTextColor(Color.parseColor("#C73F3F"));
                iv_clz.setBackgroundResource(R.drawable.wtg);
            } else {
                tv_clz.setVisibility(View.GONE);
            }

            if ("1".equals(iParams.getData1().get("imagecid1").toString())) {
                iv_sfzzm.setBackgroundResource(R.drawable.dsh);
            } else if ("2".equals(iParams.getData1().get("imagecid1").toString())) {
                iv_sfzzm.setBackgroundResource(R.drawable.ysh);
                rl_image2.setClickable(false);
            } else if ("-1".equals(iParams.getData1().get("imagecid1").toString())) {
                tv_sfzzm.setVisibility(View.VISIBLE);
                tv_sfzzm.setText("注:" + iParams.getData1().get("imagecid1appmsg").toString());
                tv_sfzzm.setTextColor(Color.parseColor("#C73F3F"));
                iv_sfzzm.setBackgroundResource(R.drawable.wtg);
            } else {
                tv_sfzzm.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void setHandlerPostDataError(int connectedId, Request request, HttpException httpException, String params, String content) throws JSONException {


    }

    @Override
    public void setHandlerGetDataSuccess(int connectionId, Object data) throws JSONException {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_jsz://驾驶证正面
                switchClick(1);
//				hideSoftKeyboard();
                selectphotoWindow = new SelectPhotoWindow(this, this);
                selectphotoWindow.showAtLocation(
                        this.findViewById(R.id.rl_super), Gravity.BOTTOM
                                | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
                break;
            case R.id.rl_xszzm://行驶证正面
                switchClick(2);
//				hideSoftKeyboard();
                selectphotoWindow = new SelectPhotoWindow(this, this);
                selectphotoWindow.showAtLocation(
                        this.findViewById(R.id.rl_super), Gravity.BOTTOM
                                | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
                break;
//            case R.id.rl_xszfm:
//                switchClick(3);
////				hideSoftKeyboard();
//                selectphotoWindow = new SelectPhotoWindow(this, this);
//                selectphotoWindow.showAtLocation(
//                        this.findViewById(R.id.rl_super), Gravity.BOTTOM
//                                | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
//                break;
            case R.id.rl_clz://车辆照正面
                switchClick(4);
//				hideSoftKeyboard();
                selectphotoWindow = new SelectPhotoWindow(this, this);
                selectphotoWindow.showAtLocation(
                        this.findViewById(R.id.rl_super), Gravity.BOTTOM
                                | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
                break;

            case R.id.rl_image2://身份证正面
                switchClick(3);
//                hideSoftKeyboard();
                selectphotoWindow = new SelectPhotoWindow(this, this);
                selectphotoWindow.showAtLocation(
                        this.findViewById(R.id.rl_super), Gravity.BOTTOM
                                | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
                break;

            case R.id.tv_pz://拍照
                ImageUtils.openCameraImage(this);
                selectphotoWindow.dismiss();
                break;
            case R.id.tv_xc://相册
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");// 相片类型
                startActivityForResult(intent, ImageUtils.GET_IMAGE_FROM_PHONE);
                selectphotoWindow.dismiss();
                break;

            case R.id.iv_right://保存
                String cph = et_cph.getText().toString();
//                String wv = et_weight.getText().toString();
                username = et_real_name.getText().toString();

                if (SU.isEmpty(username)) {
                    TU.showShort(this, "姓名不能为空");
                    return;
                }

                if (SU.isEmpty(cph)) {
                    TU.showShort(this, "车牌号不能为空");
                    return;
                }
                sendPostRequest(
                        Constants.sjyz,
                        new ParamsService().sjyz(
                                username, datas_sfzzm,
                                cph, carlong, cartype,
                                datas_jsz, datas_xszzm, datas_clz, App.phone),
                        this, false);
                break;
        }
    }


    public void publish_cx_btn(final View view) {//车长车型
        if (mWindow_car != null && mWindow_car.isShowing()) {
            mWindow_car.dismiss();
            return;
        }

        flag = 2;
        ArrayList<String> list = new ArrayList<>();
        String[] ss = SPU.getPreferenceValueString
                (DriverCertificationActivity.this, SPU.CARLEN, "",
                        SPU.STORE_SETTINGS).split(",");
        for (int i = 0; i < ss.length; i++) {
            list.add(ss[i]);
        }

        ArrayList<String> list2 = new ArrayList<>();
        String[] ss2 = SPU.getPreferenceValueString
                (DriverCertificationActivity.this, SPU.CARTYPE, "",
                        SPU.STORE_SETTINGS).split(",");
        for (int i = 0; i < ss2.length; i++) {
            list2.add(ss2[i]);
        }

        if (cityLevelAdapter2 == null) {
            cityLevelAdapter2 = new CityLevelAdapter2(DriverCertificationActivity.this, list);
            gv_ppw_carlong.setAdapter(cityLevelAdapter2);
        } else {
            cityLevelAdapter2.clearListView();
            cityLevelAdapter2.upateList(list);
        }


        if (cityLevelAdapter3 == null) {
            cityLevelAdapter3 = new CityLevelAdapter3(DriverCertificationActivity.this, list2);
            gv_ppw_cartype.setAdapter(cityLevelAdapter3);
        } else {
            cityLevelAdapter3.clearListView();
            cityLevelAdapter3.upateList(list2);
        }

        mWindow_car.setAnimationStyle(R.style.PopupWindowAnimation);
        mWindow_car.setOutsideTouchable(true);
        mWindow_car.setFocusable(true);
        mWindow_car.setTouchable(true);
        mWindow_car.setBackgroundDrawable(getResources().getDrawable(R.color.base_top_bg_color));
        mWindow_car.showAsDropDown(findViewById(R.id.default_title_bg), 0, 0);
//        creatDialog_cx(view);
    }


    private int flag = 0;

    public void publish_weightunit_btn(final View view) {//重量单位
        flag = 3;
        ArrayList<String> list = new ArrayList<>();
        String[] ss = SPU.getPreferenceValueString
                (DriverCertificationActivity.this, SPU.WV, "",
                        SPU.STORE_SETTINGS).split(",");
        for (int i = 0; i < ss.length; i++) {
            list.add(ss[i]);
        }
        tv_select.setText("请选择重量单位：");
        initPopuWindow(list, view);
//        creatDialog_wv(view);
    }


    private void switchClick(int i) {
        switch (i) {
            case 1:
                rl_image_b1 = true;
                rl_image_b2 = false;
                rl_image_b3 = false;
                break;
            case 2:
                rl_image_b1 = false;
                rl_image_b2 = true;
                rl_image_b3 = false;
                break;
            case 3:
                rl_image_b1 = false;
                rl_image_b2 = false;
                rl_image_b3 = true;
                break;
            case 4:
                rl_image_b1 = false;
                rl_image_b2 = false;
                rl_image_b3 = false;
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ImageUtils.GET_IMAGE_BY_CAMERA:// 拍照获取图片
                if (resultCode == this.RESULT_OK) {
                    // uri传入与否影响图片获取方式,以下二选一
                    // 方式一,自定义Uri(ImageUtils.imageUriFromCamera),用于保存拍照后图片地址
                    if (ImageUtils.imageUriFromCamera != null) {
                        // 可以直接显示图片,或者进行其他处理(如压缩或裁剪等)
//					 iv_image.setImageURI(ImageUtils.imageUriFromCamera);
                        // 对图片进行裁剪
                        ImageUtils.cropImage(this, ImageUtils.imageUriFromCamera);
                        break;
                    }
                }
                break;
            case ImageUtils.GET_IMAGE_FROM_PHONE:// 手机相册获取图片
                if (resultCode != this.RESULT_CANCELED) {
                    if (data != null && data.getData() != null) {
                        // 可以直接显示图片,或者进行其他处理(如压缩或裁剪等)
                        // iv.setImageURI(data.getData());
                        // 对图片进行裁剪
                        ImageUtils.cropImage(this, data.getData());
                    }
                }
                break;
            case ImageUtils.CROP_IMAGE:// 裁剪图片后结果
                if (resultCode == this.RESULT_OK) {
                    if (ImageUtils.cropImageUri != null) {
                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            Uri photouri = ImageUtils.cropImageUri;
//						photouri 》 content://media/external/images/media/124013
                            localPath = ImageUtils.getImagePathFromUri(this, photouri);
                            ///localPath > /storage/emulated/0/DCIM/Camera/1458277806433.jpg
                            //setPicToView2(ImageUtils.getImagePathFromUri(this, photouri));
                            //uploadImg();
                            //从图库拿到Bitmap压缩后给控件设置
                            ImageCompress.CompressOptions options = new CompressOptions();
                            options.maxHeight = 40;
                            options.maxWidth = 40;
//						options.uri = data.getData();
                            options.uri = photouri;
                            ImageCompress imageCompress = new ImageCompress();
                            bitmap = imageCompress.compressFromUri(this, options);

                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            bitmap.compress(CompressFormat.PNG, 100, byteArrayOutputStream);
                            byte[] imageData = byteArrayOutputStream.toByteArray();
//						ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageData);
                            String datas = Base64.encodeToString(imageData, Base64.DEFAULT);//编码
                            if (rl_image_b1) {
                                iv_jsz.setImageURI(photouri);
                                datas_jsz = datas;
                            } else if (rl_image_b2) {
                                datas_xszzm = datas;
                                iv_xszzm.setImageURI(photouri);
                            } else if (rl_image_b3) {
                                datas_sfzzm = datas;
                                iv_sfzzm.setImageURI(photouri);
                            } else {
                                datas_clz = datas;
                                iv_clz.setImageURI(photouri);
                            }
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void setHandlerGetDataError(int connectionId, Object data) throws JSONException {

    }

    void hideSoftKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    @Override
    public void send(View view, final String aInfo) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (cityLevelAdapter != null) {
//                    cityLevelAdapter.clearListView();
//                }
                mWindow.dismiss();
//                btn_wv.setText(aInfo);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWindow != null && mWindow.isShowing()) {
                mWindow.dismiss();
            } else if (mWindow_car != null && mWindow_car.isShowing()) {
                mWindow_car.dismiss();
            } else {
                SPU.setPreferenceValueBoolean(mContext, SPU.IS_GRRZ, true, SPU.STORE_SETTINGS);
                finish();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


}
