package com.hongbao5656.util;
import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import com.example.aaron.library.MLog;
public class SU {
	public final static char ELLIPSIZE = '…';//省略号
	public final static String ACCOUNT_NUMBER = "[0-9]";/** 数字 */
	public final static String ACCOUNT_LETTER = "[A-Za-z]";/** 字母 */
	public final static String REGISTER_ACCOUNT = "^[A-Za-z]{1}[\\dA-Za-z]{5,19}$";/** 字母开头 ,6-20位 */
	public final static String PHONE_PATTERN = "^[1][3-8]\\d{9}$";/** 手机号 */
	public final static String NUMBER_PATTERN = "^[0-9]\\d{10}$";
	public final static String EMAIL_PATTERN =
			"^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})$";/** 电子邮件格式 */
	public final static String DATE_FORMAT = "yyyy-MM-dd";/** 日期格式 */
	public static final String ENG_DATE_FROMAT = "EEE, d MMM yyyy HH:mm:ss z";
	public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
	public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
	public static final String MM_DD_HH_MM = "MM-dd HH:mm";
	public static final String YYYY_MM_DD = "yyyy-MM-dd";
	public static final String YYYY = "yyyy";
	public static final String MM_DD = "MM-dd";
	public static final String HH_MM = "HH:mm";
	public static final String MM = "MM";
	public static final String DD = "dd";
	public final static String dayNames[] =
			{"星期日","星期一","星期二","星期三","星期四","星期五","星期六"};

	public static boolean isVoid(Object obj) {
		return null == obj || obj.equals("");
	}


	/**
	 * 判断字符串是否为空
	 */
	public static boolean isEmpty(CharSequence cs) {
		return null == cs||"".equals(cs)||"null".equals(cs)||(cs.toString().trim()).length()==0;
	}

//	public static boolean isEmpty(CharSequence cs) {
//		return cs == null || cs.length() == 0 || "null".equals(cs);
//	}

//	public static boolean isEmpty(CharSequence cs){
//		if (TextUtils.isEmpty(cs)){
//			return true;
//		}
//		if(cs == "null"){
//			return true;
//		}
//		return TextUtils.isEmpty(cs.toString().trim());
//	}

	public static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		return m.matches();
	}

	/**
	 * <br>正则表达式匹配 </br> 
	 * @param patternStr
	 * @param input
	 * @return
	 */
	public static boolean isMatcherFinded(String patternStr, CharSequence input) {
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(input);
		if (matcher.find()) {
			return true;
		}
		return false;
	}

	/**
	 * @param time
	 * @return
	 * @描述 —— 指定时间距离当前时间的中文信息
	 */
	public static String getLnow(long time) {
		Calendar cal = Calendar.getInstance();
		long timel = cal.getTimeInMillis() - time;
		if (timel / 1000 < 60) {
			return "1分钟以内";
		} else if (timel / 1000 / 60 < 60) {
			return timel / 1000 / 60 + "分钟前";
		} else if (timel / 1000 / 60 / 60 < 24) {
			return timel / 1000 / 60 / 60 + "小时前";
		} else {
			return timel / 1000 / 60 / 60 / 24 + "天前";
		}
	}

	/**
	 * @param formatYMD
	 * @param date
     * @return
     */
	public static String getWeek(SimpleDateFormat formatYMD,String date){
		String str = "";
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(formatYMD.parse(date));
			int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
			str = dayNames[dayOfWeek - 1];
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return str;
	}


	/**
	 * 整数取万
	 * */
	public static String intToString(int num){
		String str = "";
		if (num >9999) {
			String temp = String.valueOf(num);
			String start = temp.substring(0, temp.length() - 4);
			String end = temp.substring(temp.length() - 4,temp.length() - 3);
			str = start;
			if (Integer.parseInt(end) > 0) {
				str +=  "."+end;
			}
			str += "万";
		}else {
			str = (String.valueOf(num));
		}
		return str;
	}

	public static String formatAudioDurationMillis(long durationMillis) {
		int durationSeconds = (int) Math.max(1, durationMillis / 1000);
		int minutatesPart = durationSeconds / 60;
		int secondsPart = durationSeconds % 60;
		if (minutatesPart > 0) {
			return "59\"";
		} else {
			return secondsPart + "\"";
		}
	}

	private static final String[] IMAGE_SUFFIXS = new String[] { "jpg", "jpeg", "bmp", "png", "gif" };

	@SuppressLint("DefaultLocale")
	public static boolean isValidImagePath(String path) {
		MLog.i("isValidImagePath - " + path);
		if (isVoid(path))
			return false;
		if (!path.startsWith("/"))
			return false;
		int dotIndex = path.lastIndexOf('.');
		if (dotIndex == -1)
			return false;
		if (!new File(path).isFile())
			return false;
		String suffixLower = path.substring(dotIndex + 1).toLowerCase();
		for (String imgSuffix : IMAGE_SUFFIXS) {
			if (suffixLower.equals(imgSuffix)) {
				MLog.i("isValidImagePath - ok");
				return true;
			}
		}
		return false;
	}

	public static boolean isGifImage (String path) {
		if (isVoid(path)) {
			return false;
		} else if (path.endsWith("gif")) {
			return true;
		}
		return false;
	}

	/**
	 * 获取对应的缩略图url
	 * @param url
	 * @return
	 */
	public static String getThumbUrl(String url) {
		if (!url.startsWith("http")) {
			return url;
		}
		String fileName = url.substring(url.lastIndexOf("/")+1);
		int suffixIdx = fileName.lastIndexOf('.');
		if (suffixIdx == -1) {
			//没有扩展名
			return url + "_thumb";
		}else{
			int suffixIdxx = url.lastIndexOf('.');
			//有扩展名
			return url.substring(0, suffixIdxx) + "_thumb" + url.substring(suffixIdxx);
		}
	}

	/**
	 * 获得单字符填充的字符串
	 * @param length
	 * @param c
	 * @return
	 */
	public static String filledString(int length, char c) {
		char[] array = new char[length];
		Arrays.fill(array, c);
		return new String(array);
	}

	/**
	 * <br> 判断首字母是否为字母</br>
	 * @param label
	 * @return
	 */
	public static boolean isFirstCharLetter(String label) {
		if (isVoid(label)) {
			return false;
		}
		char c = label.charAt(0);
		int i = (int) c;
		if ((i >= 65 && i <= 90) || (i >= 97 && i <= 122)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 使用分隔符连接数组
	 * @param sep 分隔符
	 * @param items 需连接的项
	 * @return
	 *
	 * @see String#split(String)
	 * @see String#split(String, int)
	 */
	public static String join(String sep, Object[] items) {
		if (items == null || items.length == 0) {
			return "";
		} else {
			StringBuilder builder = new StringBuilder(String.valueOf(items[0]));
			for (int i = 1; i < items.length; i++) {
				builder.append(sep);
				builder.append(String.valueOf(items[i]));
			}
			return builder.toString();
		}
	}

	/**
	 * <br>
	 * 根据出生日期计算年龄 </br>
	 * @param dateStr yyyy-MM-dd格式
	 * @return
	 */
	public static String getAgeFromDate(String dateStr) {
		String deltaAge = null;
		if(null == dateStr || "".equals(dateStr) || "0000-00-00".equals(dateStr)) {
			return "22";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.CHINA);
		try {
			long deltaTime = sdf.parse(sdf.format(new Date())).getTime()
					- sdf.parse(dateStr).getTime();
			long date = deltaTime / (1 * 24 * 60 * 60 * 1000) - 1;
			String ageStr = new DecimalFormat("#.00").format(date /  365.25f);
			String age[] = ageStr.split("\\.");
			int tempLength = age[0].replace("-", "0").length();
			if (tempLength < 1) {
				deltaAge = "22";
			} else {
				deltaAge = age[0].replace("-", "0");
			}
		} catch (ParseException e) {
			deltaAge = "22";
			e.printStackTrace();
		}
		return deltaAge;
	}
	/**
	 * 判断评分颜色
	 * -1：不显示评分，1：评分为绿色，2：评分为橙色，3：评分为红色
	 * @return
	 */
	public static int judgeScoreColor(String score){
		try{
			if(score==null || score.equals("")){
				return -1;
			}
			if(Double.valueOf(score)<=Double.valueOf("5.9")
					&& Double.valueOf(score)>Double.valueOf("0")){
				return 1;
			}else if(Double.valueOf(score)<=Double.valueOf("7.9")
					&& Double.valueOf(score)>Double.valueOf("5.9")){
				return 2;
			}else if(Double.valueOf(score)<=Double.valueOf("10")
					&& Double.valueOf(score)>Double.valueOf("7.9")){
				return 3;
			}else{
				return -1;
			}
		}catch(Exception e){
			return -1;
		}

	}
	public static String generateTime(long time) {
		int totalSeconds = (int) (time / 1000);
		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;

		return hours >= 0 ? String.format("%02d:%02d", minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
	}


	/**
	 * 判断字符串是否是數字
	 *
	 * @author huajun
	 * */
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	/**
	 * 判断是否是手机号
	 *
	 * @param phoneNumber
	 * @return
	 */
	public static boolean isPhoneNumber(String phoneNumber) {
		String expression = "^1[3|4|5|7|8][0-9]{9}$";
		CharSequence inputStr = phoneNumber;
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(inputStr);
		return matcher.matches();
	}
	/**
	 * 判断是否是身份证号码
	 */
	public static boolean isIDCardNo(String iDCardNo) {
//		String expression = "(^\\d{18}$)|(^\\d{15}$)";
		String expression = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$|^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X|x)$";
		CharSequence inputStr = iDCardNo;
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(inputStr);
		return matcher.matches();
	}

	/**
	 * 判断字符串是否包含中文
	 *
	 * @author lvliuyan
	 * */
	public static final boolean isChinese(String strName) {
		char[] ch = strName.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (isChinese(c)) {
				return true;
			}
		}
		return false;
	}

	private static final boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}


	public static String getLocalMacAddress(Context context) {
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}

	public static int strToInt(String number) {
		int n = 0;
		try {
			n = Integer.valueOf(number);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return n;
	}

	public static BigDecimal strToBigDecimal(String number) {
		BigDecimal n = null;
		try {
			n = new BigDecimal(number);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return n;
	}

	public static double strToDouble(String number) {
		double n = 0l;
		try {
			n = Double.valueOf(number);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return n;
	}

	/**
	 * 根据毫秒值返回字符串.e.g."0天0小时0分钟0秒"
	 *
	 * @param millisSecond
	 * @return
	 */
	public static String millisToString(long millisSecond) {
		int s = 1000;
		int m = 60 * s;
		int h = 60 * m;
		int d = 24 * h;
		StringBuffer sb = new StringBuffer();
		if (millisSecond / d > 0) {
			long day = millisSecond / d;
			sb.append(day < 10 ? "0" + day : day);
			sb.append(" 天 ");
		}
		long hour = millisSecond % d / h;
		sb.append(hour < 10 ? "0" + hour : hour);
		sb.append(" 时 ");
		long minute = millisSecond % d % h / m;
		sb.append(minute < 10 ? "0" + minute : minute);
		sb.append(" 分 ");
		long second = millisSecond % d % h % m / s;
		sb.append(second < 10 ? "0" + second : second);
		sb.append(" 秒 ");
		return sb.toString();
	}

	/***
	 * 将输入金额num转换为汉字大写格式
	 *
	 * @return 金额的大写格式
	 */
	public static String transferPriceToHanzi(String number) {
		BigDecimal num;
		if (TextUtils.isEmpty(number.trim())) {
			return "零";
		} else if (number.startsWith("-")) {
			return "输入金额不能为负数";
		} else {
			num = new BigDecimal(number.trim());
		}
		String title = "人民币:";
		String[] upChinese = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌",
				"玖", };
		String[] upChinese2 = { "分", "角", "圆", "拾", "佰", "仟", "萬", "拾", "佰",
				"仟", "亿", "拾", "佰", "仟", "兆" };
		StringBuffer result = new StringBuffer();
		int count = 0;
		int zeroflag = 0;
		boolean mantissa = false;
		if (num.compareTo(BigDecimal.ZERO) < 0) {
			// 输入值小于零
			return "输入金额不能为负数";
		}
		if (num.compareTo(BigDecimal.ZERO) == 0) {
			// 输入值等于零
			return "零";
		}
		if (String.valueOf(num).length() > 12) { // 输入值过大转为科学计数法本方法无法转换
			return "您输入的金额过大";
		}
		BigDecimal temp = num.multiply(BigDecimal.TEN.pow(2));
		BigDecimal[] divideAndRemainder = temp
				.divideAndRemainder(BigDecimal.TEN.pow(2));
		if (divideAndRemainder[1].compareTo(BigDecimal.ZERO) == 0) {
			// 金额为整时
			if (temp.compareTo(BigDecimal.ZERO) == 0)
				return "您输入的金额过小";
			// 输入额为e:0.0012小于分计量单位时
			result.insert(0, "整");
			temp = temp.divide(BigDecimal.TEN.pow(2));
			count = 2;
			mantissa = true;
		}
		while (temp.compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal[] divideAndRemainder2 = temp
					.divideAndRemainder(BigDecimal.TEN);
			BigDecimal t = divideAndRemainder2[1];
			// 取得最后一位
			if (t.compareTo(BigDecimal.ZERO) != 0) {
				// 最后一位不为零时
				if (zeroflag >= 1) {
					// 对该位前的单个或多个零位进行处理
					if (((!mantissa) && count == 1)) {
						// 不是整数金额且分为为零
					} else if (count > 2 && count - zeroflag < 2) {
						// 输入金额为400.04小数点前后都有零

						result.insert(1, "零");
					} else if (count > 6 && count - zeroflag < 6 && count < 10) {
						// 万位后为零且万位为零
						if (count - zeroflag == 2) {
							// 输入值如400000
							result.insert(0, "萬");
						} else {
							result.insert(0, "萬零");
							// 输入值如400101
						}
					} else if (count > 10 && count - zeroflag < 10) {
						if (count - zeroflag == 2) {
							result.insert(0, "亿");
						} else {
							result.insert(0, "亿零");
						}
					} else if (((count - zeroflag) == 2)) {
						// 个位为零
					} else if (count > 6 && count - zeroflag == 6 && count < 10) { // 以万位开始出现零如4001000
						result.insert(0, "萬");
					} else if (count == 11 && zeroflag == 1) {
						result.insert(0, "亿");
					} else {
						result.insert(0, "零");
					}
				}
				result.insert(0, upChinese[t.intValue()] + upChinese2[count]);
				zeroflag = 0;
			} else {
				if (count == 2) {
					result.insert(0, upChinese2[count]);
					// 个位为零补上"圆"字
				}
				zeroflag++;
			}
			BigDecimal[] divideAndRemainder3 = temp
					.divideAndRemainder(BigDecimal.TEN);
			temp = divideAndRemainder3[0];
			// System.out.println("count=" + count + "---zero=" + zeroflag
			// + "----" + result.toString());
			count++;
			if (count > 14)
				break;
		}
		return title + result.toString();
	}

	/***
	 * 半角转换为全角
	 *
	 * @param input
	 * @return
	 */
	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}

	/**
	 * * 去除特殊字符或将所有中文标号替换为英文标号
	 *
	 * @param str
	 * @return
	 */
	public static String stringFilter(String str) {
		str = str.replaceAll("【", "[").replaceAll("】", "]")
				.replaceAll("！", "!").replaceAll("：", ":");// 替换中文标号
		String regEx = "[『』]"; // 清除掉特殊字符
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}



	/**
	 * 过滤特殊字符
	 * @param keyword
	 * @return
	 */
	public static String StringFilter(String keyword){
		if(keyword==null || keyword.equals("")){
			return "";
		}
		keyword = keyword.replace("\\", "");
		String regEx="[`~!@#$%^&*()+|{}':;',\\[\\].<>/~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern   p   =   Pattern.compile(regEx);
		Matcher   m   =   p.matcher(keyword);
		return m.replaceAll("").trim();
	}

	public static PackageInfo getVersionCodeAndVersionName(Context mContext) {
		PackageInfo packInfo = null;
		try {
			// getPackageName()是你当前类的包名，0代表是获取版本信息
			packInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(),0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return packInfo;
	}

}

