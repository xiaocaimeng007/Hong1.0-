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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hongbao5656.R;
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
import java.util.LinkedHashMap;

public class ShipperCertificationActivity extends BaseActivity implements
        HttpDataHandlerListener, OnClickListener {
    private RelativeLayout rl_gsyyzz;
    private InputMethodManager inputMethodManager;
    private SelectPhotoWindow selectphotoWindow;
    private String localPath;
    private RoundAngleImageView iv_person_photo;
    private Bitmap bitmap;
    //	private RelativeLayout rl_zzjgm;
//	private RelativeLayout rl_swdjz;
    private EditText et_gsmc;
    private EditText et_gsdz;
    private ImageView iv_gsyyzz;
    //	private ImageView iv_zzjgdm;
//	private ImageView iv_swdjz;
    private boolean rl_image_b1;
    private boolean rl_image_b3;
    private boolean rl_image_b2;
    private String datas_gsyyzz = "";
    private String datas_sfzzm = "";
    private String datas_swdjz = "";
    private TextView iv_right;
    private TextView tv_gsyyzz;
    //	private TextView tv_zzjgdm;
//	private TextView tv_swdjz;
    private Context mContext;
    private EditText et_real_name;
    private RelativeLayout rl_image2;
    private ImageView iv_sfzzm;
    private TextView tv_sfzzm;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shippercertification);
        this.mContext = ShipperCertificationActivity.this;
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        initView();
        initListener();
        setInputUI(findViewById(R.id.rl_super), this);
    }

    private void initListener() {
        rl_gsyyzz.setOnClickListener(this);
        rl_image2.setOnClickListener(this);
//		rl_zzjgm.setOnClickListener(this);
//		rl_swdjz.setOnClickListener(this);
        iv_right.setOnClickListener(this);
        findViewById(R.id.leftBt).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SPU.setPreferenceValueBoolean(mContext, SPU.IS_GRRZ, true, SPU.STORE_USERINFO);
                finish();
            }
        });
    }

    private void initView() {
        sendPostRequest(
                Constants.hzrzmx,
                new ParamsService().request(Constants.hzrzmx),
                this,
                false);
        rl_gsyyzz = (RelativeLayout) findViewById(R.id.rl_gsyyzz);
        et_gsmc = (EditText) findViewById(R.id.et_gsmc);
        et_gsmc.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String t = et_gsmc.getText().toString();
                String editable = et_gsmc.getText().toString();
                String str = SU.StringFilter(editable.toString());
                if (!editable.equals(str)) {
                    et_gsmc.setText(str);
                    et_gsmc.setSelection(str.length()); //光标置后
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        et_gsdz = (EditText) findViewById(R.id.et_gsdz);
        et_gsdz.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String t = et_gsdz.getText().toString();
                String editable = et_gsdz.getText().toString();
                String str = SU.StringFilter(editable.toString());
                if (!editable.equals(str)) {
                    et_gsdz.setText(str);
                    et_gsdz.setSelection(str.length()); //光标置后
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        iv_gsyyzz = (ImageView) findViewById(R.id.iv_gsyyzz);
//		iv_zzjgdm = (ImageView)findViewById(R.id.iv_zzjgdm);
//		iv_swdjz = (ImageView)findViewById(R.id.iv_swdjz);
        iv_right = (TextView) findViewById(R.id.iv_right);
        tv_gsyyzz = (TextView) findViewById(R.id.tv_gsyyzz);
//		tv_zzjgdm = (TextView)findViewById(R.id.tv_zzjgdm);
//		tv_swdjz = (TextView)findViewById(R.id.tv_swdjz);


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
    }

    @Override
    public void setHandlerPostDataSuccess(int connectionId, Object data) throws JSONException {
        ResponseParams<LinkedHashMap> iParams = jiexiData(data);
        if (connectionId == Constants.hzyz) {
                TU.show(this, "保存成功");
                SPU.setPreferenceValueBoolean(mContext, SPU.IS_GRRZ, true, SPU.STORE_SETTINGS);
                SPU.setPreferenceValueString(mContext, SPU.USERNAMENEW, username, SPU.STORE_USERINFO);
                App.username = username;
                ShipperCertificationActivity.this.finish();
        } else if (connectionId == Constants.hzrzmx) {
            et_real_name.setText(iParams.getData1().get("username").toString());
            et_gsmc.setText(iParams.getData1().get("usercompany").toString());
            et_gsdz.setText(iParams.getData1().get("usercompanyaddress").toString());
            if ("1".equals(iParams.getData1().get("yingyezhizhao").toString())) {
                iv_gsyyzz.setBackgroundResource(R.drawable.dsh);
            } else if ("2".equals(iParams.getData1().get("yingyezhizhao").toString())) {
                rl_gsyyzz.setClickable(false);
                iv_gsyyzz.setBackgroundResource(R.drawable.ysh);
            } else if ("-1".equals(iParams.getData1().get("yingyezhizhao").toString())) {
                tv_gsyyzz.setVisibility(View.VISIBLE);
                tv_gsyyzz.setText("注:" + iParams.getData1().get("yingyezhizhaoappmsg").toString());
                tv_gsyyzz.setTextColor(Color.parseColor("#C73F3F"));
                iv_gsyyzz.setBackgroundResource(R.drawable.wtg);
            } else {
                tv_gsyyzz.setVisibility(View.GONE);
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
    public void setHandlerGetDataError(int connectionId, Object data) throws JSONException {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_gsyyzz:
                switchClick(1);
//				hideSoftKeyboard();
                selectphotoWindow = new SelectPhotoWindow(this, this);
                selectphotoWindow.showAtLocation(
                        this.findViewById(R.id.rl_super), Gravity.BOTTOM
                                | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
                break;
            case R.id.rl_image2://身份证正面
                switchClick(2);
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
                String gsmc = et_gsmc.getText().toString();
                String gsdz = et_gsdz.getText().toString();
                username = et_real_name.getText().toString();
                if (SU.isEmpty(username)) {
                    TU.showShort(this, "真实姓名不能为空");
                    return;
                }
                if (SU.isEmpty(gsmc)) {
                    TU.showShort(this, "公司名称不能为空");
                    return;
                }
                if (SU.isEmpty(gsdz)) {
                    TU.showLong(this, "公司地址不能为空");
                    return;
                }
                sendPostRequest(
                        Constants.hzyz,
                        new ParamsService().hzyz(username, gsmc, gsdz, datas_gsyyzz, datas_sfzzm, datas_swdjz, App.phone),
                        this,
                        false);
                break;
        }
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
                            options.maxHeight = 100;
                            options.maxWidth = 100;
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
                                iv_gsyyzz.setImageURI(photouri);
                                datas_gsyyzz = datas;
                            } else if (rl_image_b2) {
                                datas_sfzzm = datas;
                                iv_sfzzm.setImageURI(photouri);
                            }
// 							else if (rl_image_b3) {
//								datas_swdjz = datas;
//								iv_swdjz.setImageURI(photouri);
//							}
                        }
                    }
                }
                break;
        }
    }

    void hideSoftKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            SPU.setPreferenceValueBoolean(mContext, SPU.IS_GRRZ, true, SPU.STORE_USERINFO);
            ShipperCertificationActivity.this.finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


}
