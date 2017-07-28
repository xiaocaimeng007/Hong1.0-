package com.hongbao5656.jpush;

public interface MsgRoute {

	// 审核
	static final String MQ_DATA_CONSTANT_USER_CHECK = "1001";// 用户审核

	// 货源
	static final String MQ_DATA_CONSTANT_PRODUCT_ADD = "2001";// 货源发布
	static final String MQ_DATA_CONSTANT_PRODUCT_UNPASS = "2003";// 货源审核不通过
	static final String MQ_DATA_CONSTANT_PRODUCT_CHECKED = "2004";// 货源审核通过
	static final String MQ_DATA_CONSTANT_PRODUCT_UPDATE = "2005";// 货源修改
	static final String MQ_DATA_CONSTANT_PRODUCT_STOP = "2006";// 货源停发
	static final String MQ_DATA_CONSTANT_PRODUCT_AGENSHOW = "2008";// 货源停发到再发
	static final String MQ_DATA_CONSTANT_PRODUCT_CLOSED = "2007";// 货源关闭
	static final String MQ_DATA_CONSTANT_PRODUCT_STRAIN = "2009";// 货源紧张
	static final String MQ_DATA_CONSTANT_PRODUCT_SACK = "2010";// 货源抢光
	static final String MQ_DATA_CONSTANT_PRODUCT_PUSH = "2011";// 货源推送(车队推送)

	// 订单
	static final String MQ_DATA_CONSTANT_ORDER_RESERVE = "3001";// 预定接单
	static final String MQ_DATA_CONSTANT_ORDER_CANCEL = "3006";// 取消接单
	static final String MQ_DATA_CONSTANT_ORDER_PRINT = "3002";// 接单打印
	static final String MQ_DATA_CONSTANT_ORDER_FINSIH = "3004";// 接单完成
	static final String MQ_DATA_CONSTANT_ORDER_LOADING = "3007";// 订单装车
	static final String MQ_DATA_CONSTANT_ORDER_NO_FINSIH = "3008";// 接单未完成

	// 积分
	static final String MQ_DATA_CONSTANT_INTEEGRAL_ADD = "5001";// 加积分
	static final String MQ_DATA_CONSTANT_INTEEGRAL_OUT = "5002";// 消费积分

	// 红包
	static final String MQ_DATA_CONSTANT_REDPAKET_DUIHUAN = "6002";// 提现
	static final String MQ_DATA_CONSTANT_REDPAKET_CHECKED = "6004";// 提现申请

	// 等级
	static final String MQ_DATA_CONSTANT_LEVEL_UP = "7001";// 等级变化

	// 联系计划
	static final String MQ_DATA_CONSTANT_REL_JOB = "8001";// 联系计划

	// 来源
	static final String MQ_DATA_CONSTANT_SOURCE_CRM = "3";// 后台

	static final String MQ_DATA_CONSTANT_SOURCE_PUSH = "2";// 地推端

	static final String MQ_DATA_CONSTANT_SOURCE_PLAT = "1";// 平台端

	static String QUARZ_JOB_CLAZZ = "com.huoda.quartz.job.OrderQuarzJob";
}
