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
import com.hongbao5656.view.SelectPhotoWindow;
import com.squareup.okhttp.Request;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.util.LinkedHashMap;

public class PersionCertificationActivity extends BaseActivity implements
        HttpDataHandlerListener, OnClickListener {
    private RelativeLayout rl_image;
    private InputMethodManager inputMethodManager;
    private SelectPhotoWindow selectphotoWindow;
    private String localPath;
    private Bitmap bitmap;
    private ImageView iv_image;
    private ImageView iv_sfzzm;
    private RelativeLayout rl_image2;
//    private RelativeLayout rl_image3;
//    private ImageView iv_sfzfm;
    private boolean rl_image_b1;
//    private boolean rl_image_b3;
    private boolean rl_image_b2;
    private TextView iv_right;
    private EditText et_real_name;
//    private EditText et_sfzh;
    private String datas;
    private String datas_realphoto = "";
    private String datas_sfzzm = "";
//    private String datas_sfzfm = "";
    private TextView tv_realphoto;
    private TextView tv_sfzzm;
//    private TextView tv_sfzfm;
    private String name;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persioncertification);
        this.mContext = PersionCertificationActivity.this;
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        initView();
        initListener();
//        hideSoftKeyboard();
        setInputUI(findViewById(R.id.rl_super),this);
    }


    private void initListener() {
        rl_image.setOnClickListener(this);
        rl_image2.setOnClickListener(this);
//        rl_image3.setOnClickListener(this);
        iv_right.setOnClickListener(this);
        findViewById(R.id.leftBt).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SPU.setPreferenceValueBoolean(mContext, SPU.IS_GRRZ, true, SPU.STORE_SETTINGS);
                finish();
            }
        });
    }

    private void initView() {
        sendPostRequest(
                Constants.grrzmx,
                new ParamsService().request(Constants.grrzmx),
                this,
                false);
        et_real_name = (EditText) findViewById(R.id.et_real_name);
        et_real_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,int count) {
                String t = et_real_name.getText().toString();
                String editable = et_real_name.getText().toString();
                String str = SU.StringFilter(editable.toString());
                if (!editable.equals(str)) {
                    et_real_name.setText(str);
                    et_real_name.setSelection(str.length()); //光标置后
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });
//        et_sfzh = (EditText) findViewById(R.id.et_sfzh);
        rl_image = (RelativeLayout) findViewById(R.id.rl_image);
        iv_image = (ImageView) findViewById(R.id.iv_image);
        rl_image2 = (RelativeLayout) findViewById(R.id.rl_image2);
        iv_sfzzm = (ImageView) findViewById(R.id.iv_sfzzm);
//        rl_image3 = (RelativeLayout) findViewById(R.id.rl_image3);
//        iv_sfzfm = (ImageView) findViewById(R.id.iv_sfzfm);
        iv_right = (TextView) findViewById(R.id.iv_right);
        tv_realphoto = (TextView) findViewById(R.id.tv_realphoto);
        tv_sfzzm = (TextView) findViewById(R.id.tv_sfzzm);
//        tv_sfzfm = (TextView) findViewById(R.id.tv_sfzfm);
    }


    @Override
    public void setHandlerPostDataSuccess(int connectionId, Object data) throws JSONException {
        ResponseParams<LinkedHashMap> iParams = jiexiData(data);
        if (connectionId == Constants.grzz) {
                TU.show(this, "保存成功");
                SPU.setPreferenceValueBoolean(mContext, SPU.IS_GRRZ, true, SPU.STORE_SETTINGS);
                SPU.setPreferenceValueString(mContext, SPU.USERNAMENEW, name, SPU.STORE_USERINFO);
                App.username = name;
                this.finish();
        } else if (connectionId == Constants.grrzmx) {
            et_real_name.setText(iParams.getData1().get("username").toString());
            if ("1".equals(iParams.getData1().get("imagehead").toString())) {
                iv_image.setBackgroundResource(R.drawable.dsh);
            } else if ("2".equals(iParams.getData1().get("imagehead").toString())) {
                rl_image.setClickable(false);
                iv_image.setBackgroundResource(R.drawable.ysh);
            } else if ("-1".equals(iParams.getData1().get("imagehead").toString())) {
                tv_realphoto.setVisibility(View.VISIBLE);
                tv_realphoto.setText("注:" + iParams.getData1().get("imageheadappmsg").toString());
                tv_realphoto.setTextColor(Color.parseColor("#C73F3F"));
                iv_image.setBackgroundResource(R.drawable.wtg);
            } else {
                tv_realphoto.setVisibility(View.GONE);
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
            case R.id.rl_image:
                switchClick(1);
//                hideSoftKeyboard();
                selectphotoWindow = new SelectPhotoWindow(this, this);
                selectphotoWindow.showAtLocation(
                        this.findViewById(R.id.rl_super), Gravity.BOTTOM
                                | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
                break;
            case R.id.rl_image2:
                switchClick(2);
//                hideSoftKeyboard();
                selectphotoWindow = new SelectPhotoWindow(this, this);
                selectphotoWindow.showAtLocation(
                        this.findViewById(R.id.rl_super), Gravity.BOTTOM
                                | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
                break;
//            case R.id.rl_image3:
//                switchClick(3);
////                hideSoftKeyboard();
//                selectphotoWindow = new SelectPhotoWindow(this, this);
//                selectphotoWindow.showAtLocation(
//                        this.findViewById(R.id.rl_super), Gravity.BOTTOM
//                                | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
//                break;
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
                name = et_real_name.getText().toString();
//                String cardID = et_sfzh.getText().toString();
                if (SU.isEmpty(name)) {
                    TU.showShort(this, "姓名不能为空");
                    return;
                }
//                if (!SU.isIDCardNo(cardID)) {
//                    TU.showLong(this, "请输入正确的身份证号码");
//                    return;
//                }
//			if (SU.isEmpty(datas_realphoto)) {
//				TU.showShort(this, "真实图片未上传");
//				return;
//			}
//			if (SU.isEmpty(datas_sfzzm)) {
//				TU.showShort(this, "身份证正面未上传");
//				return;
//			}
//			if (SU.isEmpty(datas_sfzfm)) {
//				TU.showShort(this, "身份证反面未上传");
//				return;
//			}
                sendPostRequest(
                        Constants.grzz,
                        new ParamsService().grzz(name,datas_realphoto, datas_sfzzm,App.phone),
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
//                rl_image_b3 = false;
                break;
            case 2:
                rl_image_b1 = false;
                rl_image_b2 = true;
//                rl_image_b3 = false;
                break;
            case 3:
                rl_image_b1 = false;
                rl_image_b2 = false;
//                rl_image_b3 = true;
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
                        // iv_image.setImageURI(ImageUtils.imageUriFromCamera);
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
                            //photouri 》 content://media/external/images/media/124013
                            localPath = ImageUtils.getImagePathFromUri(this, photouri);
                            //localPath > /storage/emulated/0/DCIM/Camera/1458277806433.jpg
                            //setPicToView2(ImageUtils.getImagePathFromUri(this, photouri));
                            //uploadImg();
                            //从图库拿到Bitmap压缩后给控件设置
                            ImageCompress.CompressOptions options = new CompressOptions();
//                            options.maxHeight = 40;
//                            options.maxWidth = 40;
                            //options.uri = data.getData();
                            options.uri = photouri;
                            ImageCompress imageCompress = new ImageCompress();
                            bitmap = imageCompress.compressFromUri(this, options);

                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            bitmap.compress(CompressFormat.PNG, 100, byteArrayOutputStream);
                            byte[] imageData = byteArrayOutputStream.toByteArray();
                            //ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageData);
                            String datas = Base64.encodeToString(imageData, Base64.DEFAULT);//编码
                            if (rl_image_b1) {
                                iv_image.setImageURI(photouri);
                                datas_realphoto = datas;
                            } else if (rl_image_b2) {
                                datas_sfzzm = datas;
                                iv_sfzzm.setImageURI(photouri);
                            }
//                            else if (rl_image_b3) {
//                                datas_sfzfm = datas;
////                                iv_sfzfm.setImageURI(photouri);
//                            }
                        }
                    }
                }
                break;
        }
    }

//    void hideSoftKeyboard() {
//        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
//            if (getCurrentFocus() != null)
//                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
//                        InputMethodManager.HIDE_NOT_ALWAYS);
//        }
//    }







    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            SPU.setPreferenceValueBoolean(mContext, SPU.IS_GRRZ, true, SPU.STORE_SETTINGS);
            PersionCertificationActivity.this.finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
