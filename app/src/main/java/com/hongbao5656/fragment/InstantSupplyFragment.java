package com.hongbao5656.fragment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.aaron.library.MLog;
import com.hongbao5656.R;
import com.hongbao5656.activity.AddOffenLineActivity;
import com.hongbao5656.activity.LoginActivity;
import com.hongbao5656.activity.MainActivity;
import com.hongbao5656.activity.ShowLineActivity;
import com.hongbao5656.adapter.OffenLineAdapter;
import com.hongbao5656.adapter.OffenLineAdapter.DeleteListener;
import com.hongbao5656.base.App;
import com.hongbao5656.base.BaseFragment;
import com.hongbao5656.okhttp.HttpException;
import com.hongbao5656.util.Constants;
import com.hongbao5656.entity.OffenLine;
import com.hongbao5656.entity.SupplyItem;
import com.hongbao5656.okhttp.HttpDataHandlerListener;
import com.hongbao5656.util.IMap;
import com.hongbao5656.util.ParamsService;
import com.hongbao5656.util.ResponseParams;
import com.hongbao5656.util.SPU;
import com.hongbao5656.util.SU;
import com.hongbao5656.util.TU;
import com.squareup.okhttp.Request;

import org.json.JSONException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
public class InstantSupplyFragment
        extends BaseFragment
        implements HttpDataHandlerListener {
    private ImageView tv_setOftenStation;
    private ToggleButton tv_delete;
    private OffenLineAdapter adapter;
    private ListView lv_offenline;
    private Button btn_callback;
    private View rootView;
    private OffenLine ol;
    private TextView item_delete;
    private TextView item_notice;
    /**
     * 标记是否初始化完成 即是否执行了onCreateView方法
     */
    protected boolean isPrepared = false;
    private MainActivity mActivity;
    private RelativeLayout rl_allline;
    private TextView rl_allline_tv;
    private int ALLNAME = 1;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof MainActivity) {
//            MLog.i("fragment生命周期：onAttach");
            mActivity = (MainActivity) activity;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
//        MLog.i("fragment生命周期：onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        MLog.i("fragment生命周期：onCreateView");
        rootView = inflater.inflate(R.layout.fragment_instantsupply, null);
        initView();
        initMonitor();
        isPrepared = true;
        lazyLoad();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        MLog.i("fragment生命周期：onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onStart() {
//        MLog.i("fragment生命周期：onStart");
        super.onStart();
    }

    public void onResume() {
//        MLog.i("fragment生命周期：onResume");
        super.onResume();
    }

    public void onPause() {
        MLog.i("fragment生命周期：onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
//        MLog.i("fragment生命周期：onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        MLog.i("fragment生命周期：onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        MLog.i("fragment生命周期：onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        MLog.i("fragment生命周期：onDetach");
        super.onDetach();
    }

    @Override
    public void lazyLoad() {
//        MLog.i("fragment生命周期：执行了->lazyLoad()方法");
//        if (isVisible && isPrepared) {
        if (isPrepared) {
//            isPrepared = false;
            requestOffenLine();
//            MLog.i("fragment生命周期：fragment可见&&onCreateView执行了才执行->lazyLoad()方法");
        }
    }

    public void requestOffenLine() {
        sendPostRequest(
                Constants.requestOffenLine,
                new ParamsService().request(Constants.requestOffenLine),
                this,
                false);
    }

    public void updateNotice(SupplyItem si) {//ctrl + alt + t
        try {
            if (!SU.isEmpty(si.JpushTags)
                    && null != adapter
                    && null != adapter.datas
                    && null != rl_allline_tv
                    && null != mActivity.rb_jishihuoyuan_count) {

                String JpushTags = si.JpushTags.trim().substring(1);
                if (12 == JpushTags.length()) {
                    OffenLine o2 = adapter.datas.FindFirstOrDeault(JpushTags);
                    if (o2 != null) {
//                        o2.count += 1;
                        setNumberAdd(JpushTags);
                    }
                } else if (12 < JpushTags.length()) {
                    String[] JpushTag = JpushTags.split(",");
                    for (int i = 0; i < JpushTag.length; i++) {
                        OffenLine o2 = adapter.datas.FindFirstOrDeault(JpushTag[i].trim());
                        if (o2 != null) {
//                            o2.count += 1;
                            setNumberAdd(JpushTag[i].trim());
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                mActivity.playMusic(1);
            }
        } catch (Exception ex) {
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            Throwable cause = ex.getCause();
            while (cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }
            printWriter.close();
            String result = writer.toString();
            sendEMailDM("split_bug<updateNotice>",result,"");
            ex.printStackTrace();
        }
    }

    private void setNumberAdd(String line) {
        int jpushnum = SPU.getPreferenceValueInt(App.getInstance(), SPU.JPUSHNALLUM, 0, SPU.STORE_USERINFO);
        int lines = SPU.getPreferenceValueInt(App.getInstance(), line, 0, SPU.STORE_USERINFO);

        int num = ++jpushnum;
        int linesnum = ++lines;

        SPU.setPreferenceValueInt(App.getInstance(), SPU.JPUSHNALLUM, num, SPU.STORE_USERINFO);
        SPU.setPreferenceValueInt(App.getInstance(), line, linesnum, SPU.STORE_USERINFO);

        rl_allline_tv.setText("" + num);
        mActivity.rb_jishihuoyuan_count.setText("" + num);
    }

    public void upDateItemAdd(Context cxt) {//添加一条线路设置tag
        String tags = SPU.getPreferenceValueString(cxt, SPU.MY_TAGS, "", SPU.STORE_USERINFO);
        String addtags = "1"+App.supplys.get(0).cityfrom + "" + App.supplys.get(0).cityto;
        String tag = "";
        if (SU.isEmpty(tags)) {
            tag = addtags.trim();
        } else {
            tag = tags + "," + addtags.trim();
        }
        setMyTags(tag.trim());
        adapter.updateList(App.supplys);
    }

    protected void initMonitor() {
        tv_setOftenStation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, AddOffenLineActivity.class);
                startActivity(intent);
            }
        });
        tv_delete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    adapter.updateIsCancle(true);
                } else {
                    adapter.updateIsCancle(false);
                }
            }
        });
        adapter.setDeleteListener(new DeleteListener() {
            private com.hongbao5656.util.AllDialog allDialog;

            @Override
            public void onDeleteListener(final OffenLine vo) {
                allDialog = new com.hongbao5656.util.AllDialog(mActivity);
                allDialog.setContent("您确认要删除吗？");
                allDialog.show();
                allDialog.btn_ok.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        deleteoffenline(vo);
                        if (allDialog != null) {
                            allDialog.dismiss();
                        }
                    }
                });
                allDialog.btn_fangqi.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        allDialog.dismiss();
                    }
                });
            }

            @Override
            public void onSelectItemListener(OffenLine vo, int n) {
                setNumberJian(n);
                Intent intent = new Intent(mActivity, ShowLineActivity.class);
                intent.putExtra("vo", vo);
                startActivity(intent);
            }
        });

        rl_allline.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                setNumber(0);
                //TODO 请求全部常跑线路
                Intent intent = new Intent(mActivity, ShowLineActivity.class);
                intent.putExtra("allname", ALLNAME);
                startActivity(intent);
            }
        });
    }

    private void setNumberJian(int n) {
        int jpushnum = SPU.getPreferenceValueInt(App.getInstance(), SPU.JPUSHNALLUM, 0, SPU.STORE_USERINFO);
        if (n >= 0 && n <= jpushnum) {
            int num = jpushnum - n;
            SPU.setPreferenceValueInt(App.getInstance(), SPU.JPUSHNALLUM, num, SPU.STORE_USERINFO);
            if (null != rl_allline_tv && null != mActivity.rb_jishihuoyuan_count) {
                rl_allline_tv.setText("" + num);
                mActivity.rb_jishihuoyuan_count.setText("" + num);
            }
        }
    }

    void deleteoffenline(OffenLine ol) {
        this.ol = ol;
        sendPostRequest(
                Constants.deleteoffenline,
                new ParamsService().requestKV("lineid", ol.lineid, Constants.deleteoffenline),
                this,
                false);
    }

    protected void initView() {
        View topview = mActivity.instantSupplyFragmentTop;
        if (topview != null) {
            tv_setOftenStation = (ImageView) topview.findViewById(R.id.tv_setOftenStation);
            tv_delete = (ToggleButton) topview.findViewById(R.id.tv_delete);
        }
        item_delete = (TextView) rootView.findViewById(R.id.item_delete);
        item_notice = (TextView) rootView.findViewById(R.id.item_notice);
        rl_allline = (RelativeLayout) rootView.findViewById(R.id.rl_allline);
        rl_allline_tv = (TextView) rootView.findViewById(R.id.rl_allline_tv);
        int jpushnum = SPU.getPreferenceValueInt(App.getInstance(), SPU.JPUSHNALLUM, 0, SPU.STORE_USERINFO);
        rl_allline_tv.setText(jpushnum + "");
        lv_offenline = (ListView) rootView.findViewById(R.id.lv_offenline);
        adapter = new OffenLineAdapter(mActivity, null);
        lv_offenline.setAdapter(adapter);
    }

    @Override
    public void setHandlerPostDataSuccess(int connectionId, Object data) throws JSONException {
        int errcode = -1;
        ResponseParams<LinkedHashMap> iParams = null;
        if (null != data) {
            iParams = IMap.jieXiResponse(data.toString());
            errcode = iParams.getErrcode();
        }
        switch (errcode) {
            case 50000://token过期   只清坐标
                TU.show(mActivity, "" + iParams.getErrmsg());
                mActivity.clearAndStopLocationData();//只清坐标
                openActivity(LoginActivity.class);
                mActivity.finish();
                break;
            case 50001://第三方登录  清坐标 清个人信息 清推送
                TU.show(mActivity, "" + iParams.getErrmsg());
                eixtAccount();
                openActivity(LoginActivity.class);
                mActivity.finish();
                break;
            case 44444://接口异常 升级app
                TU.show(mActivity, iParams.getErrmsg() + "-" + errcode);
                UpdateApp();
                if (10013 == connectionId || 10014 == connectionId) {
                    clearAndStopLocationData();
                }
                break;
            case 0://成功
                if (connectionId == Constants.requestOffenLine) {//查询常跑路线
                        ArrayList<OffenLine> supplys = (ArrayList<OffenLine>) IMap.getData2FromResponse(iParams, OffenLine.class);
                        if (supplys.size() >= 0) {
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < supplys.size(); i++) {
                                OffenLine vo = supplys.get(i);
                                sb.append(vo.cityfromparentname + "-" + vo.cityfromname + "-->");
                                sb.append(vo.citytoparentname + "-" + vo.citytoname);
                            }
                            SPU.setPreferenceValueString(mActivity, SPU.ONLINE, sb.toString(), SPU.STORE_USERINFO);
                            SPU.setPreferenceValueInt(mActivity, SPU.LINES, supplys.size(), SPU.STORE_USERINFO);
                        }
                        adapter.clearListView();
                        adapter.updateList(supplys);
                        String tags = SPU.getPreferenceValueString(mActivity, SPU.MY_TAGS, "", SPU.STORE_USERINFO);
                        setMyTags(tags);//获取长跑线路添加tag
                } else if (connectionId == Constants.deleteoffenline) {//删除常跑路线
                        TU.show(mActivity, "删除成功");
                        //删除线路重新设置数据
                        setNumberJian(SPU.getPreferenceValueInt(mActivity, ol.cityfrom + "" + ol.cityto, 0, SPU.STORE_USERINFO));

                        int lines = SPU.getPreferenceValueInt(mActivity, SPU.LINES, 0, SPU.STORE_USERINFO);
                        if (lines >= 1) {
                            SPU.setPreferenceValueInt(mActivity, SPU.LINES, lines - 1, SPU.STORE_USERINFO);
                        }

                        int i = adapter.datas.indexOf(ol.cityfrom + "" + ol.cityto);
                        OffenLine ol = adapter.datas.get(i);
                        adapter.datas.remove(i);
                        adapter.notifyDataSetChanged();

                        String tags = SPU.getPreferenceValueString(mActivity, SPU.MY_TAGS, "", SPU.STORE_USERINFO);
                        String[] ss = tags.split(",");

                        String addtags = "1"+ol.cityfrom + "" + ol.cityto;
                        StringBuffer sb = new StringBuffer();
                        for (int j = 0; j < ss.length; j++) {
                            if (!addtags.trim().equals(ss[j].trim())) {
                                if (j == ss.length - 1) {
                                    sb.append(ss[j]);
                                } else {
                                    sb.append(ss[j] + ",");
                                }
                            }
                        }
                        setMyTags(sb.toString().trim());//删除一条线路添加一次tag
                        //TODO  lineid
                }
                break;
            default:
                TU.show(mActivity, iParams.getErrmsg() + "-" + errcode);
                break;
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
