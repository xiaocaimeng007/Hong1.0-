package com.hongbao5656.util;
import android.os.Environment;
/**
 * 常量类
 */
public class Constants {
    public static final String SDCARD_PATH =
            Environment.getExternalStorageDirectory().getPath() + "/hytx/";// sdcard路径
    public static final int page = 10;// 分页查询的数量
    public static final int btn_getyzm = 10001;// 获取验证码  type 1 注册 2 找回密码
    public static final int btn_submit = 10005;// 修改密码
    public static final int ADVERISEREQUEST = 20002;// 首页广告
    public static final int btn_register = 10002;// 注   册
    public static final int btn_login = 10003;// 登录
    public static final int checksupply = 30105;//查询已发货源
    public static final int OFFENSUPPLY = 20013;//查询常跑货源
    public static final int emptycarseek = 30323;//查询
    public static final int seekcar = 30324;//查找车源
    public static final int seekmycar = 30326;//查找车源
    public static final int checksupply_seek = 30106;//查找单条线路货源
    public static final int requestAllLines = 30109;//查找所有常跑线路货源
    public static final int goodsbaojia = 30111;//货源报价
    public static final int goodsbaojiaInfo = 30113;//货源报价详情
    public static final int goodsbaojiaList = 30112;//报价货源列表

    public static final int deletesupply = 30103;//删除货源
    public static final int DELETEOFFENSUPPLY = 20014;
    public static final int deleteemptycar = 30322;
    public static final int wxpay = 4001;
    public static final int alipay = 4002;
    public static final int unionpay = 4003;
    public static final int detailsupply = 30110;//货源详情
    public static final int detailsupplyBaoJia = 30114;//货源详情-报价
    public static final int detailcar = 10023;//车源详情
    public static final int tel = 30401;
    public static final int cancleGaunZhu = 30306;//取消关注
    public static final int addGaunZhu = 30305;//添加关注
    public static final int online_3g = 10809;//3g充值
    public static final int online_3g_trading_record = 10810;//3g充值记录
    public static final int online_3g_trading_endtime = 10811;//3g充值到期时间
    public static final int TOAST = 90003;//广告提示
    public static final int addYouXuan = 30307;//添加优选
    public static final int cancleYouXuan = 30308;//取消优选
    public static final int editsupply2 = 30107;//编辑货源
//    public static final int locaton = 10013;//上传地址的老接口
    public static final int location = 10014;//上传地址的新接口
    public static final int locatonlast = 10012;//获取最新经纬度
    public static final int requestOffenLine = 30203;//查询常跑路线
    public static final int deleteoffenline = 30204;//删除常跑路线
    public static final int callbackline = 30205;
    public static final int UPDATETAB = 10007;//表数据更新
    public static final int rl_image = 20001;// 上传图片
    public static final int grzz = 30301;
    public static final int hzyz = 30302;//货主认证
    public static final int txsc = 30300;//上传个人头像
    public static final int sjyz = 30303;//司机验证
    public static final int rzhz = 30311;//认证汇总
    public static final int download = 10311;//判断有无新版本
    public static final int oilprvice = 666;//判断有无新版本
    public static final int invite = 20003;//提交邀请码
    public static final int grrzmx = 30312;//个人认证明细
    public static final int hzrzmx = 30313;//货主认证明细
    public static final int sjrzmx = 30314;//司机认证明细
    public static final int sendsupply = 30101;//发货
    public static final int emptycar = 30325;
    public static final int esetcarlen = 10009;//设置车长
    public static final int addoffenline = 30202;
    public static final int feedBack = 20011;//提交反馈
    public static final int complaint = 30304;//提交投诉
}
