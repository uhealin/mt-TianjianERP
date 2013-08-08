package com.matech.framework.pub.util;

import java.beans.PropertyDescriptor;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.userpopedom.UserPopedomService;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.autocode.DELAutocode;
import com.matech.framework.pub.db.DbUtil;

public class StringUtil {

	private static final String UMBER_PATTERN = "^(-)?\\d+(\\.\\d+)?$";

	private static NumberFormat curformatter = new DecimalFormat("#,###,##0.00");
	private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	/**
	 * 获取当天
	 * 
	 * @return 当天字符串
	 */
	public final static String getToday() {
		return (new java.sql.Date(System.currentTimeMillis())).toString();
	}

	public static String showNull(String str, String v) {
		return "<div nowrap>" + showNull(str) + "</div>";
	}

	public static String dealData(String n, String s) {
		// 第一步，格式化内容。不带<div>
		if (n == null) {
			return null;
		}
		// 无条件把空值赋值""
		if (s == null) {
			s = "";
		}
		if (n.indexOf("showNull") != -1) {
			s = showNull(s);
		}

		// 第二步，第一次加TD,style，一般都加在这里
		if (n.indexOf("showMoney1") != -1) {
			s = showMoney1(s);
		} else if (n.indexOf("showMoney") != -1) {
			s = showMoney(s);
		} else if (n.indexOf("maxLen") != -1) {
			s = maxLen(s, n);
		} else if (n.indexOf("hiddenLen") != -1) {
			s = hiddenLen(s, n);
		} else if (n.indexOf("showDateByLong") != -1) {
			s = showDateByLong(s, n);
		} else if (n.indexOf("showDate") != -1) {
			s = showDate(s, n);
		} else if (n.indexOf("showHidden") != -1) {
			s = showHidden(s); // 隐藏一列
		} else if (n.indexOf("showRight") != -1) {
			s = showRight(s);
		} else if (n.indexOf("showCenter") != -1) {
			s = showCenter(s);
		} else if (n.indexOf("showLeft") != -1) {
			s = showLeft(s);
		} else if (n.indexOf("color") != -1) {
			s = color(s, n);
		} else if (n.indexOf("showPercent") != -1) {
			s = showPercent(s);
		} else {
			s = showLeft(s);
		}

		return s;
	}

	// n 是format 字符串 s是数据库查出来的值，和传出去的值。
	// value 是witchisValue
	public static String dealData(String n, String s, String value) {

		// 第一步，格式化内容。不带<div>
		if (n == null) {
			return null;
		}
		// 无条件把空值赋值""
		if (s == null) {
			s = "";
		}
		if (n.indexOf("showNull") != -1) {
			s = showNull(s);
		}

		// 第二步，第一次加TD,style，一般都加在这里
		if (n.indexOf("showMoney1") != -1) {
			s = showMoney1(s);
		} else if (n.indexOf("showMoney") != -1) {
			s = showMoney(s);
		} else if (n.indexOf("maxLen") != -1) {
			s = maxLen(s, n);
		} else if (n.indexOf("hiddenLen") != -1) {
			s = hiddenLen(s, n);
		} else if (n.indexOf("showDateByLong") != -1) {
			s = showDateByLong(s, n);
		} else if (n.indexOf("showDate") != -1) {
			s = showDate(s, n);
		} else if (n.indexOf("showHidden") != -1) {
			s = showHidden(s); // 隐藏一列
		} else if (n.indexOf("showRight") != -1) {
			s = showRight(s);
		} else if (n.indexOf("showCenter") != -1) {
			s = showCenter(s);
		} else if (n.indexOf("showLeft") != -1) {
			s = showLeft(s);
		} else if (n.indexOf("color") != -1) {
			s = color(s, n);
		} else if (n.indexOf("showPercent") != -1) {
			s = showPercent(s);
		} else {
			s = showLeft(s);
		}

		return s;
	}

	public static String showHidden(String s) {
		return "<div style=\"display:none\">" + s + "</div>";
	}

	public static String showWidth(String s, String v) {
		return "<div id=\"widthTD" + v + "\" >" + s + "</div>";
	}

	public static String showHidden(String s, String v) {
		return "<div id=\"hiddenTD" + v + "\" style=\"display:none\">" + s
				+ "</div>";
	}

	public static String showMoney2(String s) {
		// 123,456,789.13
		return s == null ? "" : s.replaceAll(",", "");
	}

	public static String showMoney3(String s) {
		s = showNull(s);
		if (!s.equals("")) {
			s = curformatter.format(Double.valueOf(s).doubleValue()); // -1,234,568
		}
		return s;
	}

	public static String showMoney(String s) {

		s = showNull(s);
		if (!s.equals("")) {

			double d = Double.valueOf(s).doubleValue();
			s = curformatter.format(d); // -1,234,568

			if (s.trim().indexOf("-") == 0) {
				return ("<div style=\"text-align:right;color:#FF0000;\" title=\"" + s + "\" >" + s + "</div>");

			} else {
				return ("<div style=\"text-align:right;color:#0000FF;\" title=\"" + s + "\" >" + s + "</div>");
			}
		} else {
			return ("<div style=\"text-align:right; color:#0000FF;\" > - </div>");
		}
	}

	public static String showMoney1(String s) {

		s = showNull(s).trim();
		String s1 = "";

		if (!s.equals("")) {

			double d = Double.valueOf(s).doubleValue();
			if (d >= 0) {
				s1 = curformatter.format(d); // 1,234,568
			} else {
				s1 = "(" + curformatter.format(-d) + ")";
			}

			if (Math.abs(d) < 0.005) {
				s1 = " ";
			}

			if (s.trim().indexOf("-") == 0) {
				return ("<div style=\"text-align:right;color:#FF0000\" title=\"" + s1 + "\" >" + s1 + "</div>");

			} else {
				return ("<div style=\"text-align:right;color:#0000FF\" title=\"" + s1 + "\" >" + s1 + "</div>");
			}
		} else {
			return s1;
		}
	}

	public static String showMoney(String s, int x) {

		s = showNull(s);
		if (!s.equals("")) {
			String temp = "";
			for (int i = 0; i < x; i++) {
				temp += "0";
			}
			NumberFormat formatter = new DecimalFormat("#,###,##0." + temp);
			double d = Double.valueOf(s).doubleValue();
			if (Math.abs(d) < 0.005) {
				s = " ";
			} else {
				s = formatter.format(d); // -1,234,568
			}

			if (s.trim().indexOf("-") == 0) {
				return ("<div style=\"text-align:right;color:#FF0000\" title=\"" + s + "\" >" + s + "</div>");

			} else {
				return ("<div style=\"text-align:right;color:#0000FF\" title=\"" + s + "\" >" + s + "</div>");
			}
		} else {
			return "";
		}
	}

	public static String showMoney(String s, String style) {

		s = showNull(s);
		if (!s.equals("")) {

			s = curformatter.format(Double.valueOf(s).doubleValue()); // -1,234,568

			// org.util.Debug.prtOut(s);

			if (s.trim().indexOf("-") == 0) {
				return ("<div style=\"text-align:right;color:#FF0000\" "
						+ style + " title=\"" + s + "\" >" + s + "</div>");

			} else {
				return ("<div style=\"text-align:right;color:#0000FF\" "
						+ style + " title=\"" + s + "\" >" + s + "</div>");
			}
		} else {
			return ("<div style=\"text-align:right;color:#0000FF\" " + style + "></div>");
		}
	}

	/**
	 * 百分比
	 * 
	 * @param value
	 * @return
	 */
	public static String showPercent(String value) {
		NumberFormat formatter = new DecimalFormat("0.00");

		value = showNull(value);

		if ("".equals(value)) {
			return ("<div style=\"text-align:right; color:#0000FF;\"> - </div>");
		}

		double number = Double.valueOf(value).doubleValue();

		value = formatter.format(number);

		if (number < 0) {
			return ("<div style=\"text-align:right;color:#FF0000;\" title=\"" + value + "%\" >" + value + "%</div>");

		} else {
			return ("<div style=\"text-align:right;color:#0000FF;\" title=\"" + value + "%\">" + value + "%</div>");
		}
	}

	public static String showPercent(double number) {
		NumberFormat formatter = new DecimalFormat("0.00");

		if (number < 0) {
			return ("<div style=\"text-align:right;color:#FF0000\" nowrap>"
					+ formatter.format(number) + "%</div>");

		} else {
			return ("<div style=\"text-align:right;color:#0000FF\" nowrap>"
					+ formatter.format(number) + "%</div>");
		}

	}

	public static String showRight(String s) {
		return "<div style=\"text-align:right;\" title=\"" + s + "\" >" + s + "</div>";
	}

	public static String showLeft(String s) {
		return "<div style=\"text-align:left;\" title=\"" + s + "\" >" + s + "</div>";
	}

	public static String showCenter(String s) {
		return "<div style=\"text-align:center;\" title=\"" + s + "\" >" + s + "</div>";
	}

	public static String maxLen(String s, String format) {

		int i = format.indexOf("maxLen:") + 7;
		int j = format.indexOf(" ", i);
		int len = 0;
		try {
			if (j == -1) {
				j = format.length();
			}
			len = Integer.parseInt(format.substring(i, j));
		} catch (Exception e) {
		}

		return "<div style=\"width:" + len + "\" > " + s + "</div>";
	}

	public static String showDate(String s, String format) {
		if (s == null || "".equals(s)) {
			return s;
		}

		int i = format.indexOf("showDate:") + 9;
		int j = format.length();
		String formatString = "";

		formatString = format.substring(i, j);

		try {
			java.util.Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.parse(s);
			return new SimpleDateFormat(formatString).format(d);
		} catch (Exception e) {
			e.printStackTrace();
			return s;
		}

	}

	public static String showDateByLong(String s, String format) {
		if (s == null || "".equals(s)) {
			return s;
		}

		try {
			long datalong = Long.parseLong(s);
			Date date = new Date(datalong);
			return dateTimeFormat.format(date);
		} catch (Exception e) {
			e.printStackTrace();
			return s;
		}

	}

	public static String hiddenLen(String s, String format) {

		int i = format.indexOf("hiddenLen:") + 10;
		int j = format.indexOf(" ", i);
		int len = 0;
		try {
			if (j == -1) {
				j = format.length();
			}
			len = Integer.parseInt(format.substring(i, j));
		} catch (Exception e) {
		}

		if (s.length() > len) {
			s = "<div title=\"" + s + "\">" + s.substring(0, len) + "..."
					+ "</div>";
		}

		return s;
	}

	public static String color(String s, String format) {
		int i = format.indexOf("color:") + 6;
		int j = format.indexOf(" ", i);
		if (j == -1) {
			j = format.length();
		}
		String color = format.substring(i, j);

		return s.replaceFirst("style=\\\"", "style=\"color:" + color + ";");
	}

	public static String getCurrentDateTime() {
		java.text.SimpleDateFormat dateformat = new java.text.SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		java.util.Date currentdate = new java.util.Date();
		return ("" + dateformat.format(currentdate));
	}

	public static String getCurrentDate() {
		java.text.SimpleDateFormat dateformat = new java.text.SimpleDateFormat(
				"yyyy-MM-dd");
		java.util.Date currentdate = new java.util.Date();
		return ("" + dateformat.format(currentdate));
	}

	public String getDateFormat() {
		java.text.SimpleDateFormat dateformat = new java.text.SimpleDateFormat(
				"yyyyMMdd");
		java.util.Date currentdate = new java.util.Date();
		return ("" + dateformat.format(currentdate));
	}

	public static String getCurrentTime() {
		java.text.SimpleDateFormat dateformat = new java.text.SimpleDateFormat(
				"HH:mm:ss");
		java.util.Date currenttime = new java.util.Date();
		return ("" + dateformat.format(currenttime));
	}

	// 字符串替换 s 搜索字符串 s1 要查找字符串 s2 要替换字符串
	public static String replaceStr(String s, String s1, String s2) {
		if (s == null) {
			return null;
		}
		int i = 0;
		if ((i = s.indexOf(s1, i)) >= 0) {
			char ac[] = s.toCharArray();
			char ac1[] = s2.toCharArray();
			int j = s1.length();
			StringBuffer stringbuffer = new StringBuffer(ac.length);
			stringbuffer.append(ac, 0, i).append(ac1);
			i += j;
			int k;
			for (k = i; (i = s.indexOf(s1, i)) > 0; k = i) {
				stringbuffer.append(ac, k, i - k).append(ac1);
				i += j;
			}
			stringbuffer.append(ac, k, ac.length - k);
			return stringbuffer.toString();
		} else {
			return s;
		}
	}

	/**
	 * 获取文件名的扩展名
	 * 
	 * @param filename
	 * @return
	 */
	public static String getExtension(String filename) {
		return getExtension(filename, "");
	}

	/**
	 * 获取文件名的扩展名,如果不存在，返回指定扩展名
	 * 
	 * @param filename
	 * @param defExt
	 * @return
	 */
	public static String getExtension(String filename, String defExt) {
		if ((filename != null) && (filename.length() > 0)) {
			int i = filename.lastIndexOf('.');

			if ((i > 0) && (i < (filename.length() - 1))) {
				return filename.substring(i + 1);
			}
		}
		return defExt;
	}

	public static String getMime(String filename) {
		String extname = getExtension(filename).toLowerCase();
		String Mime = "application/doc";
		if (filename == null || filename.equals("")) {
		} else {
			/*
			 * text/html 超文本标记语言文本 .txt text/plain 普通文本 .rtf application/rtf
			 * RTF文本 .gif image/gif GIF图形 .ipeg,.jpg image/jpeg JPEG图形 .au
			 * audio/basic au声音文件 mid,.midi audio/midi,audio/x-midi MIDI音乐文件
			 * .ra, .ram audio/x-pn-realaudio RealAudio音乐文件 .mpg,.mpeg
			 * video/mpeg MPEG文件 .avi video/x-msvideo AVI文件 .gz
			 * application/x-gzip GZIP文件 .tar application/x-tar TAR文件
			 */
			if ("html".equals(extname) || "htm".equals(extname))
				Mime = "text/html";
			if ("doc".equals(extname) || "dot".equals(extname))
				Mime = "application/doc";
			if ("jpeg".equals(extname) || "jpg".equals(extname))
				Mime = "image/jpeg";
			if ("xsl".equals(extname))
				Mime = "application/vnd.ms-excel";
		}

		return Mime;
	}

	/**
	 * 获取全路径文件名的文件名，包括文件名和扩展名，不带目录名
	 * 
	 * @param fullpathname
	 *            全路径文件名
	 * @return 文件名
	 */
	public static String getFileName(String fullpathname) {
		String filename = fullpathname;
		if ((fullpathname != null) && (fullpathname.length() > 0)) {
			int i = fullpathname.lastIndexOf('/');

			if ((i > 0) && (i < (fullpathname.length() - 1))) {
				filename = fullpathname.substring(i + 1);
			}

			i = fullpathname.lastIndexOf('\\');
			if ((i > 0) && (i < (fullpathname.length() - 1))) {
				filename = fullpathname.substring(i + 1);
			}
		}
		return filename;
	}

	public static String formatNumber(double num) {
		return curformatter.format(num);
	}

	public static String showNull(String str) {
		if (str == null || "null".equalsIgnoreCase(str)) {
			return "";
		} else {
			return str;
		}
	}

	public static String showNull(Object obj) {
		String str = String.valueOf(obj);
		if (str == null || "null".equalsIgnoreCase(str)) {
			return "";
		} else {
			return str;
		}
	}

	public static String kill1(String strIn) {
		if (strIn.length() > 0) {
			if (strIn.substring(0, 1).equals("-"))
				return strIn.substring(1);
			else
				return strIn;

		} else {
			return strIn;
		}
	}

	// 将n个字符c组装为一个字符串String
	public static String nCharToString(char c, int n) {
		String ret = "";
		for (int i = 0; i < n; i++) {
			ret += c;
		}
		return ret;
	}

	public static String nCharToString(String c, int n) {
		String ret = "";
		for (int i = 0; i < n; i++) {
			ret += c;
		}
		return ret;
	}

	public static String killEndToken(String c, String token) {
		String result = c;
		if (c == null || c.equals(""))
			return result;
		int opt = token.length();
		if (result.substring(result.length() - opt).equals(token)) {
			result = result.substring(0, result.length() - opt);
		}
		return result;
	}

	/**
	 * 把字符串数组拼装成一个长字符串，之间用STRTOKEN分隔， 请注意，
	 * getStringFromArray：会把最后多出来的STRTOKEN截掉，比如1,2,3
	 * getStringFromArray：会把最后多出来的STRTOKEN截掉，比如1,2,3,
	 * 
	 * @param strArray
	 *            String[]
	 * @param strToken
	 *            String
	 * @return String
	 */
	public static String getStringFromArray(String[] strArray, String strToken) {
		return killEndToken(getStringFromArray1(strArray, strToken), strToken);
	}

	/**
	 * 把字符串数组拼装成一个长字符串，之间用STRTOKEN分隔， 请注意，
	 * getStringFromArray：会把最后多出来的STRTOKEN截掉，比如1,2,3
	 * getStringFromArray：会把最后多出来的STRTOKEN截掉，比如1,2,3,
	 * 
	 * @param strArray
	 *            String[]
	 * @param strToken
	 *            String
	 * @return String
	 */
	public static String getStringFromArray1(String[] strArray, String strToken) {
		String strResult = "";
		if (strArray == null)
			return "";
		for (int i = 0; i < strArray.length; i++) {
			strResult += strArray[i] + strToken;
		}
		return strResult;
	}

	public final static String[] getSqls(String str) {
		if (str == null || str.equals(""))
			return null;
		int i1 = 0, i2 = 0, i = 0;
		String[] _retStr = new String[str.length() / 3];
		String[] retStr;
		while ((i1 = str.indexOf("sql{", i2)) != -1) {
			i2 = str.indexOf("}", i1);
			_retStr[i++] = str.substring(i1 + 4, i2);
		}
		retStr = new String[i];
		for (int j = 0; j < i; j++) {
			retStr[j] = _retStr[j];
		}
		return retStr;
	}

	
	/**
	 * 从类似select a.* FROM c_account a,c_accpkgsubject b where
	 * a.accpackageid='${curPackageid}' and a.submonth=${submonth} and
	 * b.accpackageid='${curPackageid}' and b.SubjectFullName like
	 * CONCAT('${kmmc}','%') and b.isleaf=1 and a.subjectid=b.subjectid order by
	 * a.subjectid 的SQL语句中取出${}包括的部分，放到字符串数组中返回
	 * 
	 * @param str
	 *            String
	 * @return String[]
	 */
	public final static String[] getVaribles(String str) {
		if (str == null || str.equals(""))
			return null;
		int i1 = 0, i2 = 0, i = 0;
		String[] _retStr = new String[str.length() / 3];
		String[] retStr;
		while ((i1 = str.indexOf("${", i2)) != -1) {
			i2 = str.indexOf("}", i1);
			_retStr[i++] = str.substring(i1 + 2, i2);
		}
		retStr = new String[i];
		for (int j = 0; j < i; j++) {
			retStr[j] = _retStr[j];
		}
		return retStr;
	}

	public final static String[] getVaribles(String startStr, String endStr,
			String str) {
		if (str == null || str.equals(""))
			return null;
		int i1 = 0, i2 = 0, i = 0;
		String[] _retStr = new String[str.length() / 3];
		String[] retStr;
		while ((i1 = str.indexOf(startStr, i2)) != -1) {
			i2 = str.indexOf(endStr, i1);
			_retStr[i++] = str.substring(i1 + startStr.length(), i2);
		}
		retStr = new String[i];
		for (int j = 0; j < i; j++) {
			retStr[j] = _retStr[j];
		}
		return retStr;
	}

	/**
	 * 数据库的PROPERTY字段是按照位进行控制，比如第一位控制是否显示，
	 * 第二位控制是否允许为空等；为了方便大家操纵数据库表PROPERTY字段，设立本函数 实现读取指定位置的位值
	 * 注意:目前是按一位控制，位与位之间没有间隔符
	 * 
	 * @param s
	 *            Property字段的初始值
	 * @param index
	 *            设置第几位
	 * @return char 读取的Property相应位置的值;注意返回-1表示INDEX出错； 例如：
	 * 
	 */
	public static String getProperty(String s, int index) {
		if (s == null)
			return "";
		if (index <= 0)
			return "";
		// 一般习惯是从1到5，而不是JAVA的从0到4，这里作一个替换；
		index--;
		int length = s.length();
		String ret = "";
		if (index <= length - 1) {
			ret = s.substring(index, index + 1);
		}
		return ret;
	}

	/**
	 * 数据库的PROPERTY字段是按照位进行控制，比如第一位控制是否显示，
	 * 第二位控制是否允许为空等；为了方便大家操纵数据库表PROPERTY字段，设立本函数 实现设置指定位置的位值
	 * 注意:目前是按一位控制，位与位之间没有间隔符
	 * 
	 * @param s
	 *            Property字段的初始值
	 * @param index
	 *            设置第几位
	 * @param c
	 *            需要设置的值
	 * @return String 设置完毕的Property字段; 例如：
	 * 
	 */
	public static String SetProperty(String s, int index, char c) {
		index--;
		// 如果index值非法，则返回初始Property;
		if (index < 0)
			return s;

		if (s == null) {
			s = "";
		}

		String result = "";
		int length = s.length();

		// 如果index值<当前Property长度，则修改制定串
		if (index < length - 1) {
			String front = s.substring(0, index);
			String end = s.substring(index + 1);
			result = front + c + end;
		}

		// 如果 index值=当前Property长度，则修改末尾值
		if (index == length - 1) {
			String front = s.substring(0, index);
			result = front + c;
		}

		// 如果index超出当前Property长度，则中间补充空格
		if (index >= length) {
			result = s + nCharToString(' ', index - length) + c;
		}
		return result;
	}

	/**
	 * 字符串格式转换函数，将字符串由ISO8859_1转换为UTF-8
	 * 
	 * @param strIn
	 * @return
	 */
	public String GBToUTF(String strIn) {
		String strOut = null;
		if (strIn == null || (strIn.trim()).equals(""))
			return strIn;
		try {
			byte[] b = strIn.getBytes("ISO8859_1");
			strOut = new String(b, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return strOut;
	}

	/**
	 * 
	 * @param strIn
	 *            源字符串
	 * @param sourceCode
	 *            源字符串的编码.比如"ISO8859_1"
	 * @param targetCode
	 *            目标字符串的编码，比如"UTF-8"
	 * @return 目标字符串
	 */
	public static String code2code(String strIn, String sourceCode,
			String targetCode) {
		String strOut = null;
		if (strIn == null || (strIn.trim()).equals(""))
			return strIn;
		try {
			byte[] b = strIn.getBytes(sourceCode);
			strOut = new String(b, targetCode);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return strOut;
	}

	/**
	 * 将UTF8的编码汉字转换回来 反函数是
	 * 
	 * @param s
	 * @param enc
	 * @return
	 * @throws java.lang.Exception
	 */
	public static String decode(String s, String enc) throws Exception {

		boolean needToChange = false;
		StringBuffer sb = new StringBuffer();
		int numChars = s.length();
		int i = 0;

		if (enc.length() == 0) {
			throw new Exception("URLDecoder: empty string enc parameter");
		}

		while (i < numChars) {
			char c = s.charAt(i);
			switch (c) {
			case '+':
				sb.append(' ');
				i++;
				needToChange = true;
				break;
			case '%':

				/*
				 * Starting with this instance of %, process all consecutive
				 * substrings of the form %xy. Each substring %xy will yield a
				 * byte. Convert all consecutive bytes obtained this way to
				 * whatever character(s) they represent in the provided
				 * encoding.
				 */

				try {

					// (numChars-i)/3 is an upper bound for the number
					// of remaining bytes
					byte[] bytes = new byte[(numChars - i) / 3];
					int pos = 0;

					while (((i + 2) < numChars) && (c == '%')) {
						bytes[pos++] = (byte) Integer.parseInt(
								s.substring(i + 1, i + 3), 16);
						i += 3;
						if (i < numChars)
							c = s.charAt(i);
					}

					// A trailing, incomplete byte encoding such as
					// "%x" will cause an exception to be thrown

					if ((i < numChars) && (c == '%'))
						throw new IllegalArgumentException(
								"URLDecoder: Incomplete trailing escape (%) pattern");

					sb.append(new String(bytes, 0, pos, enc));
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException(
							"URLDecoder: Illegal hex characters in escape (%) pattern - "
									+ e.getMessage());
				}
				needToChange = true;
				break;
			default:
				sb.append(c);
				i++;
				break;
			}
		}

		return (needToChange ? sb.toString() : s);
	}

	/**
	 * 将文件名中的汉字转为UTF8编码的串,以便下载时能正确显示另存的文件名. 纵横软件制作中心雨亦奇2003.08.01
	 * 
	 * @param s
	 *            原文件名
	 * @return 重新编码后的文件名
	 */
	public static String toUtf8String(String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c >= 0 && c <= 255) {
				sb.append(c);
			} else {
				byte[] b;
				try {
					b = String.valueOf(c).getBytes("utf-8");
				} catch (Exception ex) {
					ex.printStackTrace();
					b = new byte[0];
				}
				for (int j = 0; j < b.length; j++) {
					int k = b[j];
					if (k < 0)
						k += 256;
					sb.append("%" + Integer.toHexString(k).toUpperCase());
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 将文件名中的汉字转为类似UTF8编码的串,以便上传和下载时能正确访问而不被程序错误转换. 类似将%转变成了.号，以方便文件存放。
	 * 
	 * @param s
	 *            原文件名
	 * @return 重新编码后的文件名
	 */
	public static String toUtfQQ8String(String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c >= 0 && c <= 255) {
				sb.append(c);
			} else {
				byte[] b;
				try {
					b = String.valueOf(c).getBytes("utf-8");
				} catch (Exception ex) {
					ex.printStackTrace();
					b = new byte[0];
				}
				for (int j = 0; j < b.length; j++) {
					int k = b[j];
					if (k < 0)
						k += 256;
					sb.append("." + Integer.toHexString(k).toUpperCase());
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 把乱七八糟的底稿编号，翻译成可以正常排序的排序编号
	 * 
	 * @param taskCode
	 *            String 底稿编号
	 * @return String 排序编号 例子：
	 *         org.util.Debug.prtOut(UTILString.getOrderId("测试A1－77(3-1)"));
	 *         org.util.Debug.prtOut(UTILString.getOrderId("测试A1－77"));
	 *         org.util.Debug.prtOut(UTILString.getOrderId("测试A1"));
	 *         org.util.Debug.prtOut(UTILString.getOrderId("1")); 返回结果如下：
	 *         测试A00001－00077(00003-00001) 测试A00001－00077 测试A00001 00001
	 */
	public static String getOrderId(String taskCode) {
		if ("".equals(taskCode) || taskCode == null)
			return "";
		String orderId = "";
		String strTemp = "";
		String strCTemp;
		int iStart = -1, iEnd = -1;

		if (taskCode == null || taskCode.equals("")) {
			return "";
		}
		for (int i = 0; i < taskCode.length(); i++) {
			strCTemp = taskCode.substring(i, i + 1);
			try {
				// 尝试做一次转换，成功说明是数字
				Integer.parseInt(strCTemp);
				// 如果是数字
				if (iStart == -1) {
					// 记录起始位置
					iStart = i;
					iEnd = i;
				} else {
					// 延长结束位置
					iEnd++;
				}

			} catch (Exception e) {
				// 异常说明不是数字
				// 如果是非数字
				if (iStart == -1) {
					// 说明前面没有遇到过，不用管，直接追加到最终结果上
					orderId += strCTemp;
				} else {
					// 开始截取这一段纯粹数字组成的字符串做替换
					strTemp = taskCode.substring(iStart, iEnd + 1);
					// 把这一段格式化成5位长度的字符串，不足位在前补零；
					// 如果这一段长度超过5位，则保留并追加
					orderId += nCharToString('0', 4 - iEnd + iStart) + strTemp
							+ taskCode.substring(i, i + 1);

					// 重新设置变量
					iStart = iEnd = -1;
				}
			} // catch
		}

		// 最后可能还有一段未处理，在此追加
		if (iStart >= 0) {
			strTemp = taskCode.substring(iStart, iEnd + 1);
			// 把这一段格式化成5位长度的字符串，不足位在前补零；
			// 如果这一段长度超过5位，则保留并追加
			orderId += nCharToString('0', 4 - iEnd + iStart) + strTemp;
		}

		return orderId;
	}

	/**
	 * 提供一个taskcode，仿照这个taskcode，得到新的taskcode
	 * 
	 * @param taskCode
	 *            String 输入taskcode
	 * @return String 新taskcode 例子：
	 *         org.util.Debug.prtOut(UTILString.getNewTaskCode("1"));
	 *         org.util.Debug.prtOut(UTILString.getNewTaskCode("01"));
	 *         org.util.Debug.prtOut(UTILString.getNewTaskCode("啊啊啊－01")); 结果： 2
	 *         02 啊啊啊－02
	 * 
	 */
	public static String getNewTaskCode(String taskCode) {
		if ("".equals(taskCode) || taskCode == null)
			return "";
		String strTemp = "", orderid = "";
		int intTemp = 0;
		int intEnd = 0;
		intEnd = getNumericIndex(taskCode, 0);
		if (intEnd >= 0) {
			strTemp = taskCode.substring(intEnd);
			intTemp = Integer.parseInt(strTemp);
			intTemp++;
			strTemp = String.valueOf(intTemp);
			// 生成，补零
			orderid = taskCode.substring(0, intEnd)
					+ nCharToString('0',
							taskCode.length() - intEnd - strTemp.length())
					+ strTemp;
		} else {
			orderid = taskCode + "1";
		}
		return orderid;
	}

	private static int getNumericIndex(String srch, int flag) {
		if ("".equals(srch) || srch == null) {
			return -1;
		}
		int idx = -1;
		char temp = ' ';
		if (flag == 0) {
			for (int i = srch.length() - 1; i >= 0; i--) {
				temp = srch.charAt(i);
				if (Character.isDigit(temp)) {
					idx = i;
				} else {
					break;
				}
			}
		} else {
			for (int i = 0; i < srch.length(); i++) {
				temp = srch.charAt(i);
				if (Character.isDigit(temp)) {
					idx = i;
				} else {
					break;
				}
			}
		}
		return idx;
	}

	/**
	 * 判断一个字符串是否数字字符串的函数 support Numeric format:<br>
	 * "33" "+33" "033.30" "-.33" ".33" " 33." " 000.000 "
	 * 
	 * @param str
	 *            String
	 * @return boolean
	 */
	public static boolean isNumeric(String str) {
		int begin = 0;
		boolean once = true;
		if (str == null || str.trim().equals("")) {
			return false;
		}
		str = str.trim();
		if (str.startsWith("+") || str.startsWith("-")) {
			if (str.length() == 1) {
				// "+" "-"
				return false;
			}
			begin = 1;
		}
		for (int i = begin; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i))) {
				if (str.charAt(i) == '.' && once) {
					// '.' can only once
					once = false;
				} else {
					return false;
				}
			}
		}
		if (str.length() == (begin + 1) && !once) {
			// "." "+." "-."
			return false;
		}
		return true;
	}

	/**
	 * 判断是不是整数字符串的函数 support Integer format:<br>
	 * "33" "003300" "+33" " -0000 "
	 * 
	 * @param str
	 *            String
	 * @return boolean
	 */
	public static boolean isInteger(String str) {
		int begin = 0;
		if (str == null || str.trim().equals("")) {
			return false;
		}
		str = str.trim();
		if (str.startsWith("+") || str.startsWith("-")) {
			if (str.length() == 1) {
				// "+" "-"
				return false;
			}
			begin = 1;
		}
		for (int i = begin; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断是否数字字符串的函数，使用异常来完成 use Exception support Numeric format:<br>
	 * "33" "+33" "033.30" "-.33" ".33" " 33." " 000.000 "
	 * 
	 * @param str
	 *            String
	 * @return boolean
	 */
	public static boolean isNumericEx(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	/**
	 * 判断是否整数的字符串，使用异常完成 use Exception support less than 11 digits(<11)<br>
	 * support Integer format:<br>
	 * "33" "003300" "+33" " -0000 " "+ 000"
	 * 
	 * @param str
	 *            String
	 * @return boolean
	 */
	public static boolean isIntegerEx(String str) {
		str = str.trim();
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException ex) {
			if (str.startsWith("+")) {
				return isIntegerEx(str.substring(1));
			}
			return false;
		}
	}

	public static String getStr(String Str) {
		int iS = Str.indexOf(">");
		int iE = Str.indexOf(";");
		String str = "";
		if (iS >= 0 && iE >= 0 && iS < 6 && iE < 6) {
			if (iS < iE) {
				str = Str.substring(iS + 1);
			} else {
				str = Str.substring(iE + 1);
			}
		} else if (iS >= 0 && iS < 6) {
			str = Str.substring(iS + 1);
		} else if (iE >= 0 && iE < 6) {
			str = Str.substring(iE + 1);
		} else {
			str = Str;
		}
		iS = str.lastIndexOf("<");
		iE = str.lastIndexOf("&");
		if (iS > str.length() - 6 && iE > str.length() - 6) {
			if (iS > iE) {
				str = str.substring(0, iS);
			} else {
				str = str.substring(0, iE);
			}
		} else if (iS > str.length() - 6) {
			str = str.substring(0, iS);
		} else if (iE > str.length() - 6) {
			str = str.substring(0, iE);
		}
		return str;
	}

	public static String getStr(String Str, String swhere) {
		int sS = Str.indexOf(swhere);
		int sE = sS + swhere.length();

		int iS = Str.indexOf(">");
		int iE = Str.indexOf(";");
		String str = "";
		if (iS >= 0 && iE >= 0 && iS < sS && iE < sS) {
			if (iS > iE) {
				str = Str.substring(iS + 1);
			} else {
				str = Str.substring(iE + 1);
			}
		} else if (iS >= 0 && iS < sS) {
			str = Str.substring(iS + 1);
		} else if (iE >= 0 && iE < sS) {
			str = Str.substring(iE + 1);
		} else {
			str = Str;
		}
		iS = str.lastIndexOf("<");
		iE = str.lastIndexOf("&");
		if (iS > sE && iE > sE) {
			if (iS > iE) {
				str = str.substring(0, iS);
			} else {
				str = str.substring(0, iE);
			}
		} else if (iS > sE) {
			str = str.substring(0, iS);
		} else if (iE > sE) {
			str = str.substring(0, iE);
		}

		return str;
	}

	/**
	 * 判断字符串是否包含中文
	 * 
	 * @param strIn
	 * @return
	 */
	public static boolean isStringContainChinese(String strIn) {
		if (strIn == null || "".equals(strIn)) {
			return false;
		}

		try {
			if (strIn.length() == (new String(strIn.getBytes(), "8859_1"))
					.length()) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 获得token在str出现的字数
	 * 
	 * @return
	 */
	public static int getStrDisplayTime(String str, String token) {
		int result = 0;
		int i = -1;
		while ((i = str.indexOf(token, i + 1)) != -1) {
			result++;
		}
		return result;
	}

	/**
	 * 格式化日期为yyyy-MM-dd
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDate(String date) {
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd");
			Date date2 = simpleDateFormat.parse(date);
			date = simpleDateFormat.format(date2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;

	}

	/**
	 * 根据一个String得到另一个String：select * from k_user where name =
	 * 'aaa'->select,*,from,k_user,where,name,=,aaa
	 * 
	 * @param date
	 * @return
	 */
	public static String getStringByString(String str) {
		String strs = "";
		try {

			if (str != null) {

				str = str.replaceAll("'", "").replaceAll("\"", "");

				for (int i = 0; i < str.split(" ").length; i++) {

					if (!"".equals(str.split(" ")[i].trim())) {

						strs += str.split(" ")[i] + ",";

					}

				}

				if (strs.endsWith(",")) {

					strs = strs.substring(0, strs.length() - 1);

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return strs;

	}

	/**
	 * 根据一个String得到另一个String：select * from k_user where name =
	 * 'aaa'->select,*,from,k_user,where,name,=,aaa
	 * 
	 * @param date
	 * @return
	 */
	public static String addBlank(String str) {

		try {

			str = str.replaceAll("<=", " #1# ").replaceAll(">=", " #2# ")
					.replaceAll("!=", " #3# ");

			str = str.replaceAll("\\(", " ( ").replaceAll("\\)", " ) ")
					.replaceAll("=", " = ").replaceAll("<>", " <> ")
					.replaceAll("#1#", "<=").replaceAll("#2#", ">=")
					.replaceAll("#3#", "!=");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;

	}

	public static String setXMLData(String XML, String name, String data) {
		// XML = "<"+name+">"+data+"</"+name+">";
		try {
			if (XML == null || name == null) {
				XML = "";
			}
			String XML2 = XML;

			int len1 = name.length();
			int i = XML.indexOf("<" + name + ">");
			if (i >= 0) {
				int j = XML.indexOf("</" + name + ">");
				if (j >= 0) {
					// String temp=XML.substring(i+len1+2,j-1);
					// String temp =
					return (XML.substring(0, i + len1 + 2) + data + XML2
							.substring(j));
				}
				return XML;
			}
			return XML;
		} catch (Exception e) {
			return XML;
		}
	}

	public static String getXMLData(String XML, String name) {
		try {
			if (XML == null || name == null) {
				return "";
			}

			int len1 = name.length();
			int i = XML.indexOf("<" + name + ">");
			if (i >= 0) {
				int j = XML.indexOf("</" + name + ">");
				if (j >= 0) {
					return (XML.substring(i + len1 + 2, j).trim());
				}
				return "";
			}
			return "";
		} catch (Exception e) {
			return "";
		}
	}

	public static String getXMLData(String xml) {

		StringBuffer jsonString = new StringBuffer();
		try {

			String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<REQUEST_XML_ROOT>" + xml + "</REQUEST_XML_ROOT>";

			Document srcdoc = DocumentHelper.parseText(xmlString);
			Element root = srcdoc.getRootElement();

			List list = root.elements();

			for (int i = 0; i < list.size(); i++) {
				Element element = (Element) list.get(i);
				jsonString.append(element.getName()).append("=")
						.append(element.getText());

				if (i != list.size() - 1) {
					jsonString.append(",");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonString.toString();
	}

	public static String getXMLData(String XML, String name,
			String defaultvalule) {
		try {
			if (XML == null || name == null) {
				return "";
			}

			int len1 = name.length();
			int i = XML.indexOf("<" + name + ">");
			if (i >= 0) {
				int j = XML.indexOf("</" + name + ">");
				if (j >= 0) {
					String tempStr = XML.substring(i + len1 + 2, j).trim();
					if (tempStr.equals(""))
						return defaultvalule;
					else
						return (tempStr);
					// return temp.trim();
				}
				return defaultvalule;
			}
			return defaultvalule;
		} catch (Exception e) {
			return defaultvalule;
		}
	}

	/**
	 * 
	 * 输入原有DATAGRID的多表头定义
	 * tableHead="记录日期,名称{登录名,用户名称},详细记录,详细记录,测试{啊啊啊,cccccc}";
	 * 
	 * System.out.println("aaa:"+UTILString.tranExtTableTitle(tableHead));
	 * 
	 * 输出结果是EXT多表头要的样式： {},{header: ',名', colspan: 2, align:
	 * 'center'},{},{},{header: ',测', colspan: 2, align: 'center'}
	 * 
	 * 请注意：
	 * 
	 * @todo:此函数只支持2层多表头，3层及以上的翻译不支持！
	 * 
	 * @param strMtTableTitle
	 * @return
	 */
	public static String tranExtTableTitle(String strMtTableTitle) {
		if (strMtTableTitle == null || "".equals(strMtTableTitle)) {
			return "";
		}

		char[] c = strMtTableTitle.toCharArray();

		char cc = 0, oldcc = 0;
		int iLevel = 0;
		StringBuffer result = new StringBuffer("");
		int iTokenStart = 0, iColCount = 0;
		String strToken = "";
		for (int i = 0; i < c.length; i++) {
			oldcc = cc;
			cc = c[i];

			switch (cc) {
			case ',':
				if (iLevel == 0 && oldcc != '}') {
					result.append(",{}");
				}

				iColCount++;

				iTokenStart = i;
				break;
			case '{':
				iLevel++;

				iColCount = 1;

				strToken = strMtTableTitle.substring(iTokenStart + 1, i);

				break;
			case '}':
				iLevel--;
				iTokenStart = i;

				result.append(",{header: '" + (strToken) + "', colspan: "
						+ iColCount + ", align: 'center'}");

				break;
			default:
			}

			// System.out.println("i:"+i+"|cc"+cc+"|result:"+result.toString());
		}

		if (cc != '}') {
			result.append(",{}");
		}
		return result.toString().substring(1);
	}

	/**
	 * 全球唯一ID
	 * 
	 * @return
	 */
	public static synchronized String getUUID() {
		return UUID.randomUUID().toString();
	}

	public synchronized static String getNumUnid() {
		return String.valueOf(new Date().getTime());
	}

	/**
	 * 当前日期时间 yyyy-MM-dd HH:mm:ss 格式
	 * 
	 * @return
	 */
	public static synchronized String getCurDateTime() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}
	
	/**
	 * 
	 * @param format 格式，如：yyyy-MM-dd HH:mm:ss
	 * @param l：当前时间的前后相差秒数
	 * @return
	 */
	public static synchronized String getCurDateTime(String format,int l) {
		Calendar c = Calendar.getInstance();   
		  
		c.add(Calendar.SECOND, l); // 目前時間加指定秒数   
		return new SimpleDateFormat(format).format(c.getTime());
	}

	/**
	 * 当前日期 yyyy-MM-dd 格式
	 * 
	 * @return
	 */
	public static synchronized String getCurDate() {
		return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	}

	/**
	 * 当前时间 HH:mm:ss 格式
	 * 
	 * @return
	 */
	public static synchronized String getCurTime() {
		return new SimpleDateFormat("HH:mm:ss").format(new Date());
	}

	/**
	 * 当前年份 yyyy 格式
	 * 
	 * @return
	 */
	public static synchronized String getCurDay() {
		return new SimpleDateFormat("dd").format(new Date());
	}

	/**
	 * 当前年份 yyyy 格式
	 * 
	 * @return
	 */
	public static synchronized String getCurMonth() {
		return new SimpleDateFormat("MM").format(new Date());
	}

	/**
	 * 当前年份 yyyy 格式
	 * 
	 * @return
	 */
	public static synchronized String getCurYear() {
		return new SimpleDateFormat("yyyy").format(new Date());
	}

	/**
	 * 获取年份 默认取本年
	 * 
	 * @param year
	 *            年份
	 * @return
	 */
	public static String getOrSetDataYear(String year) {
		String dataYear = showNull(year);
		return "".equals(dataYear) ? getCurYear() : dataYear;
	}

	/**
	 * 获取或设置 开始和结束年 开始年默认取本年
	 * 
	 * @param startYear
	 *            开始年
	 * @param endYear
	 *            结束年
	 * @param n
	 *            跨几年
	 * @return
	 */
	public static int[] getOrSetStartAndEndYear(String startYear,
			String endYear, int n) {

		int[] startAndEndYear = new int[2];

		if (!"".equals(StringUtil.showNull(startYear))
				&& !"".equals(StringUtil.showNull(endYear))) {
			startAndEndYear[0] = Integer.parseInt(startYear);
			startAndEndYear[1] = Integer.parseInt(endYear);
		} else {
			int curYear = Integer.parseInt(StringUtil.getCurYear()); // 当前年

			startAndEndYear[1] = curYear;
			startAndEndYear[0] = curYear - n;
		}
		return startAndEndYear;
	}

	/**
	 * 当值小于10时，补充前导0 如：09
	 * 
	 * @param value
	 * @return
	 */
	public static String fixPreZero(String value) {

		String result = showNull(value).trim();
		if (result.length() == 1) {
			result = "0" + result;
		}
		return result;
	}

	/**
	 * 当值小于10时，补充前导0 如：09
	 * 
	 * @param value
	 * @return
	 */
	public static String fixPreZero(int value) {

		String result = String.valueOf(value).trim();
		if (result.length() == 1) {
			result = "0" + result;
		}
		return result;
	}

	/**
	 * 获得下一个值 如：0105 --> 0106
	 * 
	 * @param value
	 * @return
	 */
	public static String getNextValue(String value) {
		if (StringUtils.isBlank(value))
			return null;
		if (value.length() > 2) {
			String preValue = value.substring(0, value.length() - 2);
			String lastValue = value.substring(value.length() - 2,
					value.length());
			int temp = Integer.valueOf(lastValue);
			int nextValue = temp + 1;
			return preValue + fixPreZero(nextValue);
		} else {
			return fixPreZero(value);
		}

	}

	/**
	 * 生成时间 如： 09:12
	 * 
	 * @param hour
	 * @param minute
	 * @return
	 */
	public static String createTime(int hour, int minute) {

		return fixPreZero(hour) + ":" + fixPreZero(minute);
	}

	public static String createTime(String hour, String minute) {
		return fixPreZero(hour) + ":" + fixPreZero(minute);
	}

	public static String buildToString(Object obj) {
		return ToStringBuilder.reflectionToString(obj,
				ToStringStyle.MULTI_LINE_STYLE);
	}

	/**
	 * 将字符传(1,2,3)转换成('1','2','3')
	 * 
	 * @param String
	 *            待转换字符串
	 * @return String 转换后字符串
	 */
	public static String tranStrWithSign(String oldStr) {
		if ("".equals(oldStr) || oldStr == null) {
			return "' '";
		}
		String[] newStrs = oldStr.split(",");
		String returnStr = "";
		for (String newStr : newStrs) {
			returnStr = returnStr + "'" + newStr + "',";
		}
		returnStr = returnStr + "' '";// 必须为空格
		return returnStr;
	}

	public synchronized static String getCurDateTime2() {
		return new SimpleDateFormat("yyMMddHHmmssSS").format(new Date());
	}

	/**
	 * 处理自动编号
	 * 
	 * @param session
	 * @param srcStr
	 * @return
	 */
	public static String transAutoCodeValue(String srcStr) {

		String result = srcStr;
		String[] varibles = StringUtil.getVaribles("autoCode{", "}", srcStr);
		String value = "";

		if (varibles != null) {
			for (int i = 0; i < varibles.length; i++) {
				String varible = varibles[i];
				System.out.println(varible);
				try {
					value = (String) new DELAutocode().getAutoCode(varible,"all");

					result = result.replaceAll("autoCode\\{" + varible + "\\}",
							value);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		}

		return result;
	}

	/**
	 * 处理用户自定义的request变量
	 * 
	 * @param request
	 * @param srcStr
	 * @return
	 */
	public static String transSqlValue(HttpServletRequest request,
			String srcStr) {

		String result = srcStr;
		String[] varibles = StringUtil.getSqls(srcStr);
        Connection conn=null;
		DbUtil dbUtil=null;
		try{
		conn=new DBConnect().getConnect();
		dbUtil=new DbUtil(conn);
		if (varibles != null) {
			String sql = "";
			for (int i = 0; i < varibles.length; i++) {
				//if(!varibles[i].startsWith("sql."))continue;
				sql=varibles[i];
				
				try{
				String val=dbUtil.queryForString(sql);
				result = StringUtil.replaceStr(result,"sql{" + varibles[i] + "}",StringUtil.showNull(val));
				}catch(Exception ex){
					result = StringUtil.replaceStr(result,"sql{" + varibles[i] + "}",ex.getLocalizedMessage());
				}
			}
		}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return result;
	}

	
	/**
	 * 处理用户自定义的SESSION变量
	 * 
	 * @param session
	 * @param srcStr
	 * @return
	 */
	public static String transSessionValue(HttpSession session, String srcStr) {

		String result = srcStr;
		String[] varibles = StringUtil.getVaribles(srcStr);

		if (varibles != null) {
			UserSession userSession = (UserSession) session
					.getAttribute("userSession");
			Class<UserSession> clazz = UserSession.class;
			String value = "";
			for (int i = 0; i < varibles.length; i++) {
				String varible = varibles[i];

				// 从userSession里取值
				if (varible.indexOf("userSession.") > -1) {
					try {
						String str = varible.replaceAll("userSession.", "");
						PropertyDescriptor pd = new PropertyDescriptor(str,
								clazz);
						Method getMethod = pd.getReadMethod();// 获得get方法
						value = (String) getMethod.invoke(userSession);
					} catch (Exception ex) {
						ex.printStackTrace();
					}

					result = result.replaceAll("\\$\\{" + varible + "\\}",
							StringUtil.showNull(value));
				} else if (varible.indexOf("session.") > -1) {
					String str = varible.replaceAll("session.", "");
					value = (String) session.getAttribute(str);
				}

			}
		}

		return result;
	}

	/**
	 * 处理用户自定义的request变量
	 * 
	 * @param request
	 * @param srcStr
	 * @return
	 */
	public static String transRequestValue(HttpServletRequest request,
			String srcStr) {

		String result = srcStr;
		String[] varibles = StringUtil.getVaribles(srcStr);

		if (varibles != null) {
			String value = "";
			for (int i = 0; i < varibles.length; i++) {
				String varible = varibles[i];

				// 从userSession里取值
				if (varible.indexOf("request.") > -1) {
					
						String str = varible.replaceAll("request.", "");
                        Object obj=request.getAttribute(str);
                        if(obj==null){
						  value = ""; 
                        }else{
                        	value=obj.toString();
                        }

					result = result.replaceAll("\\$\\{" + varible + "\\}",
							StringUtil.showNull(value));
				}else if (varible.indexOf("param.") > -1) {
					
					String str = varible.replaceAll("param.", "");
                    Object obj=request.getParameter(str);
                    if(obj==null){
					  value =  "";
                    }else{
                    	value=obj.toString();
                    }

				result = result.replaceAll("\\$\\{" + varible + "\\}",
						StringUtil.showNull(value));
			}
			}
		}

		return result;
	}
	
	/**
	 * 处理用户自定义的部门授权 
	 * userPopedom.菜单固定编号
	 * @param session
	 * @param srcStr
	 * @return
	 */
	public static String transUserPopedomValue(HttpServletRequest request, String srcStr) {

		String result = srcStr;
		String[] varibles = StringUtil.getVaribles(srcStr);
		Connection conn=null;
		try{
			if (varibles != null) {
				conn=new DBConnect().getConnect();
				UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
				String menuid = "";
				String userid = userSession.getUserId();
				String value = "";
				for (int i = 0; i < varibles.length; i++) {
					String varible = varibles[i];
					boolean bool = false;
					// 从userSession里取值
					if (varible.indexOf("userPopedom.") > -1) {
						menuid = varible.replaceAll("userPopedom.", "");
						bool = true;
					} 
					if("userPopedom".equals(varible)) {
						menuid = StringUtil.showNull(request.getParameter("menuid"));
						bool = true;
					}

					if(bool){
						try {
							value = new UserPopedomService(conn).getUserPopedom(userid, menuid);
						} catch (Exception ex) {
							ex.printStackTrace();
						}

						result = StringUtil.replaceStr(result,"${" + varible + "}",StringUtil.showNull(value));
					}
					
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return result;
	}

	/**
	 * 替换系统变量
	 * 
	 * @param srcStr
	 * @return
	 */
	public static String transSystemValue(String srcStr) {
		String result = srcStr;
		String[] varibles = StringUtil.getVaribles("sys{", "}", srcStr);
		String value = "";
		if (varibles != null) {
			for (int i = 0; i < varibles.length; i++) {
				String varible = varibles[i];

				if (varible.indexOf("curDate") > -1) {
					try {
						// 当前日期
						String format = "";

						format = varible.replaceAll("curDate_", "");
						if ("".equals(format.trim())
								|| "curDate".equals(format)) {
							format = "yyyy-MM-dd HH:mm:ss";
						}
						value = new SimpleDateFormat(format).format(new Date());
					} catch (Exception e) {
						e.printStackTrace();
						value = StringUtil.getCurDateTime();
					}

				}

				result = result.replaceAll("sys\\{" + varible + "\\}", value);
			}

		}

		return result;
	}

	/**
	 * 将数值四舍五入到2位小数．
	 * 
	 * @param dsc
	 *            源数值
	 * @return formatNumber(88.888) ＝ 88.89 formatNumber(88.0) ＝ 88.00
	 */
	public static String formatNumber(String dsc) {
		return formatNumber(Double.parseDouble(dsc), 2);
	}

	/**
	 * 将数值四舍五入到length位．
	 * 
	 * @param dsc
	 *            源数值
	 * @param length
	 *            保留长度． 小于0则将低位的length个数字替换为0，第length+1们四舍五入；大于0且长度大于小数长度，
	 *            则在四舍五入到length位后的后面补0到长度等于length
	 * @return formatNumber(88.88, 1) ＝ 88.9 formatNumber(88.88, 3) ＝ 88.880
	 *         formatNumber(88.88, -2) ＝ 100 formatNumber(88.88, -3) ＝ 0000
	 */
	public static String formatNumber(double dsc, int length) {
		String src = String.valueOf(dsc);
		String tmp = null;
		if (length > 0) {
			double multiple = 1;
			for (int i = 0; i < length; i++) {
				multiple *= 10.0;
			}
			tmp = String.valueOf(Math.round(dsc * multiple) / multiple);
			int index = src.indexOf(".");
			int decLength = src.length() - index - 1;
			if (length > decLength) {
				for (int i = 0; i < length - decLength; i++) {
					tmp += "0";
				}
			}
		} else if (length < 0) {
			String zero = "";
			for (int i = 0; i < -length; i++) {
				zero += "0";
				dsc = Math.round(dsc * 0.1);
			}
			tmp = String.valueOf(dsc).replaceFirst("\\.\\d*", zero);
		} else {
			tmp = String.valueOf(Math.round(dsc));
		}
		return tmp;
	}

	/**
	 * 换行，空格字符的替换操作
	 * 
	 * @param in
	 *            要进行转换的字符串
	 * @return 替换后的字符串
	 */
	public static String replaceNewLine(final String in) {
		if (in == null) {
			return null;
		}
		char ch;
		char[] input = in.toCharArray();
		int len = input.length;
		final StringBuffer out = new StringBuffer((int) (len * 1.3));
		for (int index = 0; index < len; index++) {
			ch = input[index];
			if (ch == '\n') {
				out.append("<br>");
			} else if (ch == ' ') {
				out.append("&nbsp;");
			} else {
				out.append(ch);
			}

		}
		return out.toString();
	}

	/**
	 * 把字符串的字符集从ISO转换为gb2312
	 * 
	 * @param in
	 *            输入的ISO字符串
	 * @return GB2312字符串
	 */
	public static String convertIso8859ToGb2312(String in) {
		String out = null;
		try {
			byte[] ins = in.getBytes("iso-8859-1");
			out = new String(ins, "gb2312");
		} catch (Exception e) {
		}
		return out;
	}

	/**
	 * 把字符串的字符集从GB2312转换为ISO
	 * 
	 * @param in
	 *            输入的GB2312字符串
	 * @return ISO字符串
	 */
	public static String convertGb2312ToIso8859(String in) {
		String out = null;
		try {
			byte[] ins = in.getBytes("gb2312");
			out = new String(ins, "iso-8859-1");
		} catch (Exception e) {
		}
		return out;
	}

	/**
	 * 取得特定长度的字符串
	 * 
	 * @param str
	 *            待处理的字符串
	 * @param length
	 *            截取字符串的长度
	 * @return 处理后的字符串
	 */
	public static String getShortString(String str, int length) {
		String result;

		if (str.length() > length) {
			result = str.substring(0, length) + "...";
		} else {
			result = str;
		}
		return result;
	}

	/**
	 * 去掉字符串两头的空格
	 * 
	 * @param str
	 *            待处理的字符串
	 * @return 处理后的字符串
	 */
	public static String convertNullToString(String str) {

		if (str == null) {
			return "";
		} else {
			return str.trim();
		}
	}

	/**
	 * 检查电子邮件合法性
	 * 
	 * @param email
	 *            带验证的电子邮件地址
	 * @return true表示合法 false表示非法
	 */
	public static boolean checkEmailIsValid(String email) {

		boolean isok = false;

		if (email.equals("") || email == "" || email == null)
			isok = false;

		for (int i = 1; i < email.length(); i++) {

			char s = email.charAt(i);
			if (s == '@') {
				isok = true;
				break;

			}
		}
		return isok;
	}

	/**
	 * 替换字符串某些字符操作
	 * 
	 * @param str
	 *            原始的字符串 例如：bluesunny
	 * @param pattern
	 *            配备的字符 例如：blue
	 * @param replace
	 *            替换为的字符 例如：green
	 * @return 返回处理结果 例如：greensunny
	 */
	public static String replace(String str, String pattern, String replace) {

		if (replace == null) {
			replace = "";
		}
		int s = 0, e = 0;

		StringBuffer result = new StringBuffer((int) str.length() * 2);
		while ((e = str.indexOf(pattern, s)) >= 0) {
			result.append(str.substring(s, e));
			result.append(replace);
			s = e + pattern.length();
		}
		result.append(str.substring(s));
		return result.toString();
	}

	/**
	 * 判断字符串是否为数字类型
	 * 
	 * @param str
	 *            待处理的字符串
	 * @return true表示为数字类型 false表示为非数字类型
	 */
	public static boolean isNumber(String str) {
		if (str == null || str.length() == 0) {
			return false;
		}
		String sStr = "";
		int m = 0;
		m = str.indexOf(".");

		for (int j = 0; j < str.length(); j++) {
			if (m != j)
				sStr = sStr + str.charAt(j);
		}

		byte[] btyeStr = sStr.getBytes();
		for (int i = 0; i < btyeStr.length; i++) {
			if ((btyeStr[i] < 48) || (btyeStr[i] > 57)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 通过正则表达式判断字符是否是纯数字
	 * 
	 * @param str
	 *            待判断字符
	 * @return true 纯数字，false 非纯数字
	 */
	public static boolean isAllNumber(String str) {
		return str.matches("\\d*");
	}

	/**
	 * 把string型字符串转为整型
	 * 
	 * @param str
	 *            待处理的字符串
	 * @return 整数
	 */
	public static int strToInt(String str) {
		int i = 0;
		if (str != null && str.length() != 0) {
			try {
				i = Integer.parseInt(str.trim());
			} catch (NumberFormatException nfe) {
				nfe.printStackTrace();
			}
		}
		return i;
	}

	/**
	 * 把string型字符串转为Double
	 * 
	 * @param str
	 *            待处理的字符串
	 * @return double
	 */
	public static double strToDouble(String str) {
		double i = 0;
		if (str != null && str.length() != 0) {
			try {
				i = Double.parseDouble(str.trim());
			} catch (NumberFormatException nfe) {
				nfe.printStackTrace();
			}
		}
		return i;
	}

	/**
	 * 使用千分分隔符将金额分隔开(方海峰加)
	 * 
	 * @param moneyValue
	 * @return
	 */
	public static final String separateMoney(String moneyValue) {
		if (moneyValue == null || moneyValue.trim().equals(""))
			return "";
		else {
			String prefix = moneyValue.indexOf("-") >= 0 ? "-" : "";
			String tempMoney = moneyValue.indexOf(".") > 0 ? moneyValue
					.substring(0, moneyValue.indexOf(".")) : moneyValue;

			if (!prefix.equals(""))
				tempMoney = tempMoney.substring(1);

			// System.out.println("---tempMoney:"+tempMoney);

			String retValue = "";

			int i = 0;
			i = tempMoney.length();

			String tmp = tempMoney.substring((i - 3 >= 0 ? i - 3 : 0), i);

			while (!tmp.equals("")) {
				// System.out.println("---tmp:"+tmp );

				if (tmp.length() == 3)
					retValue = "," + tmp + retValue;
				else
					retValue = tmp + retValue;

				i = i - 3 >= 0 ? i - 3 : 0;

				tmp = tempMoney.substring((i - 3 >= 0 ? i - 3 : 0), i);
			}

			// System.out.println("---retvalue:"+retValue);

			if (retValue.charAt(0) == ',')
				retValue = retValue.substring(1);

			retValue = moneyValue.indexOf(".") > 0 ? retValue + "."
					+ moneyValue.substring(moneyValue.indexOf(".") + 1)
					: retValue;

			if (!prefix.equals(""))
				retValue = prefix + retValue;

			return retValue;
		}

	}

	/**
	 * 将Id数组转换成逗号分隔的字符串
	 * 
	 * @param fid
	 * @return
	 */
	public static final String comboIdStr(String[] fid) {
		String IdStr = "";
		for (int i = 0; i < fid.length; i++) {
			IdStr += fid[i];

			if (i != fid.length - 1)
				IdStr += ",";
		}
		return IdStr;
	}

	/**
	 * 将带特殊分隔符的字符串转换为按照指定替换符的字符串
	 * 
	 * @param oldValue
	 * @param separateChar
	 * @param replaceStr
	 * @return
	 */
	public static final String getTreeLevelValue(String oldValue,
			String separateChar, String replaceStr) {
		String[] spStr = oldValue.split(separateChar);
		String tmp = "";

		for (int i = 0; i < spStr.length - 1; i++) {
			tmp += replaceStr;
		}

		return tmp;
	}

	public static String getStrFromStr(String s, String s1, String s2) {
		String s3 = "";
		if (s == null || s1 == null || s2 == null || s.length() < 1
				|| s1.length() < 1 || s1.length() > s.length() || s1.equals(s2)
				|| s.indexOf(s1) == -1) {
			s3 = s;
		} else {
			int i = s.indexOf(s1);
			int j = s.length();
			String s4 = s.substring(0, i);
			String s5 = s.substring(i + s1.length(), j);
			s3 = s4 + s2;
			if (s5.indexOf(s1) != -1) {
				s3 = s3 + getStrFromStr(s5, s1, s2);
			} else {
				s3 = s3 + s5;
			}
		}
		return s3;
	}

	public static String filter(String s) {
		StringBuffer stringbuffer = new StringBuffer(s.length());
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '<') {
				stringbuffer.append("&lt;");
			} else if (c == '>') {
				stringbuffer.append("&gt;");
			} else if (c == '"') {
				stringbuffer.append("&quot;");
			} else if (c == '&') {
				stringbuffer.append("&amp;");
			} else if (c == '\n') {
				stringbuffer.append("<br>");
			} else {
				stringbuffer.append(c);
			}
		}

		return stringbuffer.toString();
	}

	public static String UnFilter(String s) {
		String s1 = "";
		try {
			s1 = getStrFromStr(s, "&nbsp;", " ");
			s1 = getStrFromStr(s1, "<br>", "\n");
			s1 = getStrFromStr(s1, "&amp;", "&");
			s1 = getStrFromStr(s1, "&quot;", "\"");
			s1 = getStrFromStr(s1, "&gt;", ">");
			s1 = getStrFromStr(s1, "&lt;", "<");
		} catch (Exception exception) {
			s1 = null;
		}
		return s1;
	}

	/**
	 * 检查是否为空，如果为空，用传入的字符串代替
	 * 
	 * @param obj
	 * @param rtobj
	 * @return
	 */
	public static String checkNull(String obj, String rtobj) {
		String s;
		if (obj == null) {
			s = rtobj;
		} else {
			s = obj.trim();
		}
		return s;
	}

	/**
	 * 取随机颜色值
	 * 
	 * @return
	 */
	public static String getColor() {
		String colors = "#";
		for (int j = 0; j < 3; j++) {
			int r = (int) (Math.random() * 16 + 1);
			switch (r) {
			case 16:
				colors += "00";
				break;
			case 15:
				colors += "FF";
				break;
			case 14:
				colors += "EE";
				break;
			case 13:
				colors += "DD";
				break;
			case 12:
				colors += "CC";
				break;
			case 11:
				colors += "BB";
				break;
			case 10:
				colors += "AA";
				break;
			}
			if (r <= 9 && r >= 0)
				colors += String.valueOf(r) + String.valueOf(r);
		}
		return colors;
	}

	/**
	 * 得到一个字符串的长度,显示的长度,一个汉字或日韩文长度为2,英文字符长度为1
	 * 
	 * @param s
	 *            需要得到长度的字符串
	 * @return i得到的字符串长度
	 */
	public static int length(String s) {
		if (s == null)
			return 0;
		char[] c = s.toCharArray();
		int len = 0;
		for (int i = 0; i < c.length; i++) {
			len++;
			if (!isLetter(c[i])) {
				len++;
			}
		}
		return len;
	}

	/**
	 * 判断一个字符是Ascill字符还是其它字符（如汉，日，韩文字符）
	 * 
	 * @param c
	 *            需要判断的字符
	 * @return 返回true,Ascill字符
	 */
	public static boolean isLetter(char c) {
		int k = 0x80;
		return c / k == 0 ? true : false;
	}

	/**
	 * 截取一段字符的长度,不区分中英文,如果数字不正好，则少取一个字符位
	 * 
	 * 
	 * @param origin
	 *            原始字符串
	 * @param len
	 *            截取长度(一个汉字长度按2算的)
	 * @param c
	 *            后缀
	 * @return 返回的字符串
	 */
	public static String substring(String origin, int len, String c) {
		if (origin == null || origin.equals("") || len < 1)
			return "";
		byte[] strByte = new byte[len];
		if (len > length(origin)) {
			return origin + c;
		}
		try {
			System.arraycopy(origin.getBytes("GBK"), 0, strByte, 0, len);
			int count = 0;
			for (int i = 0; i < len; i++) {
				int value = (int) strByte[i];
				if (value < 0) {
					count++;
				}
			}
			if (count % 2 != 0) {
				len = (len == 1) ? ++len : --len;
			}
			return new String(strByte, 0, len, "GBK") + c;
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 取得随机数字
	 * 
	 * @param randLength
	 *            随机数字的位数
	 * @return 随机数字
	 * @throws Exception
	 */
	public static String doCreateRandomCode(int randLength) {
		String radStr = "8702461359";
		StringBuffer reVal = new StringBuffer();
		Random rand = new Random();
		// int randLength=6;//随机数长度
		for (int i = 0; i < randLength; i++) {
			int randNum = rand.nextInt(radStr.length());
			reVal.append(radStr.substring(randNum, randNum + 1));
		}
		return reVal.toString();
	}

	/**
	 * 取得随机数字 0和1
	 * 
	 * @param randLength
	 * @return 随机数字
	 * @throws Exception
	 */
	public static int getRandomZeroOne() {
		return (int) (Math.random() * 10) % 2;
	}

	/**
	 * 隐藏中间字符串
	 * 
	 * @param str
	 *            原字符串
	 * @param leftStrInt
	 *            左边字符串长度
	 * @param midStrInt
	 *            中间隐藏字符串长度
	 * @param midStr
	 *            中间隐藏字符串
	 * @param rightStrInt
	 *            右边字符串长度
	 * @return
	 */
	public static String hiddenMidString(String str, int leftStrInt,
			int midStrInt, String midStr, int rightStrInt) {
		StringBuffer reStr = new StringBuffer();
		if (str == null || str.length() == 0) {
			return "";
		} else if (str.length() == 2) {
			reStr.append(midStr + str.substring(1, 2));
		} else {
			int len = str.length();
			if (len > leftStrInt) {
				reStr.append(str.substring(0, leftStrInt));
			}
			for (int i = 0; i < midStrInt; i++) {
				reStr.append(midStr);
			}
			if (len > rightStrInt) {
				reStr.append(str.subSequence(str.length() - rightStrInt,
						str.length()));
			}
		}
		return reStr.toString();
	}

	/**
	 * 将格式为yyyymm的字符串转化为yyyy-mm的字符串。如：200810转化为2008-10
	 * 
	 * @param str
	 *            yyyymm格式字符串
	 * @return yyyy-mm格式字符串
	 */
	public static String stringToMonth(String str) {
		return str.substring(0, str.length() - 2) + "-"
				+ str.substring(str.length() - 2, str.length());
	}

	/**
	 * 将格式为yyyymmdd的字符串转化为yyyy-mm-dd的字符串。如：20081010转化为2008-10-10
	 * 
	 * @param str
	 *            yyyymmdd格字符串
	 * @return yyyy-mm-dd字符串
	 */
	public static String stringToDay(String str) {
		return str.substring(0, 4) + "-" + str.substring(4, 6) + "-"
				+ str.substring(6, 8);
	}

	/**
	 * 如果对象为null，替换为空
	 * 
	 * @param str
	 * @return
	 */
	public static String replaceNullObject(Object str) {
		if (str == null) {
			return "";
		}

		return str.toString();
	}

	/**
	 * 如果对象为null，替换为toStr
	 * 
	 * @param str
	 * @param toStr
	 * @return
	 */
	public static String replaceNullObject(Object str, String toStr) {
		if (str == null || str.equals("")) {
			return toStr;
		}

		return str.toString();
	}

	/**
	 * 在src上追加空格字符直到src的长度为length.如果src的不小于length，刚不作操作直接返回 注意src长度按字节算，字节编码为
	 * gb2312
	 * 
	 * @param src
	 * @param length
	 * @return
	 */
	public static String appendSpace(String src, int length) {
		return append(' ', src, length);
	}

	/**
	 * 在src上追加字符aChar直到src的长度为length。如果src的不小于length，刚不作操作直接返回 注意src长度按字节算，字节编码为
	 * gb2312
	 * 
	 * @param aChar
	 * @param src
	 * @param length
	 */
	public static String append(char aChar, String src, int length) {
		int srcByteLength = 0;
		if (src == null)
			src = "";
		try {
			srcByteLength = src.getBytes("gb2312").length;
			if (srcByteLength >= length) {
				byte[] dest = new byte[length];
				System.arraycopy(src.getBytes("gb2312"), 0, dest, 0, length);
				return new String(dest);
				// return src.substring(0, length);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		StringBuffer sb = new StringBuffer(src);
		for (int i = 0; i < length - srcByteLength; i++) {
			sb.append(aChar);
		}
		return sb.toString();
	}

	public static byte[] intToByteArray(int res) throws Exception {
		byte[] targets = new byte[4];
		targets[0] = (byte) (res & 0xff);// 最低位
		targets[1] = (byte) ((res >> 8) & 0xff);// 次低位
		targets[2] = (byte) ((res >> 16) & 0xff);// 次高位
		targets[3] = (byte) (res >>> 24);// 最高位,无符号右移。
		return targets;
	}

	public static int ByteArrayToInt(byte res[]) throws Exception {
		int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00) // | 表示安位或
				| ((res[2] << 24) >>> 8) | (res[3] << 24);
		return targets;
	}

	/**
	 * 转义xml特殊字符
	 * 
	 * @param xml
	 * @return
	 */
	public static String replaceXMLSpecialChar(String xml) {
		xml = xml.replace("&", "&#x26;");
		xml = xml.replace("'", "&#x27;");
		xml = xml.replace("<", "&#x3c;");
		xml = xml.replace(">", "&#x3e;");
		xml = xml.replace("\"", "&#x22;");
		return xml;
	}

	public static String filterSQLStr(Object sql) {
		sql = String.valueOf(sql).replace("'", "''");
		return String.valueOf(sql);
	}

	/**
	 * 小写转换大写金额
	 */
	public static String RMBToUpper(String amount) {
		// 如果不是数字
		if (!amount.matches(UMBER_PATTERN)) {
			return "";
		}

		StringBuffer result = new StringBuffer("");
		String[] tmp = amount.replaceAll(",", "").split("\\.");
		String integer = tmp[0];
		final int LEN = integer.length();
		if (LEN > 12) {
			throw new RuntimeException("太大了 ，处理不了啊！");
		}
		for (int k = 12; k > LEN; k--) {
			integer = "0" + integer;
		}
		int part1 = Integer.parseInt(integer.substring(0, 4));
		int part2 = Integer.parseInt(integer.substring(4, 8));
		int part3 = Integer.parseInt(integer.substring(8, 12));

		if (part1 != 0) {
			result.append(parseIntRMB(part1) + "亿");
		}

		if (part2 != 0) {
			if (part2 < 1000)
				result.append("零");
			result.append(parseIntRMB(part2) + "万");
		} else {
			result.append("零");
		}

		if (part3 != 0) {
			if (part3 < 1000)
				result.append("零");
			result.append(parseIntRMB(part3));
		}
		result.append("元");
		if (tmp.length == 2) {
			result.append(parseFloatRMB(tmp[1]));
		}
		return result.toString();
	}

	/**
	 * 解析四位整数转换为中文金额大写
	 * 
	 * @param sStr
	 * @return
	 */
	public static String parseIntRMB(int i) {
		String[] num = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
		String result = "";
		int tmp = i;
		if (tmp / 1000 != 0) {
			result += num[tmp / 1000] + "仟";
			tmp = tmp - (tmp / 1000) * 1000;
		}
		if (tmp / 100 != 0) {
			result += num[tmp / 100] + "佰";
			tmp = tmp - (tmp / 100) * 100;
		}
		if (tmp / 10 != 0) {
			result += num[tmp / 10] + "拾";
			tmp = tmp - (tmp / 10) * 10;
		}
		if (tmp != 0)
			result += num[tmp];
		return result;
	}

	/**
	 * 解析小数部分
	 */
	public static String parseFloatRMB(String sStr) {
		String[] num = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "七", "捌", "玖" };
		String[] unit = { "角", "分" };
		String result = "";

		final int LEN = sStr.length();
		for (int i = 0; i < LEN; i++) {
			String tmp = sStr.substring(i, i + 1);
			int k = Integer.parseInt(tmp);
			result += num[k] + unit[i];
		}
		return result;
	}
	
	public static String trimEnd(String value, String... suffixs){
		if(isBlank(value)){return value; }
		String v=value.trim();
		for(String suffix:suffixs){
        if(suffix==null){continue; }
        
        int index=0;
        while(v.endsWith(suffix)){
        	index=v.lastIndexOf(suffix);
        	v=v.substring(0, index);
        }
        
		}
		return v;
	}
	
	public static String trimStart(String value, String... prefixs){
		if(isBlank(value)){return value; }
		String v=value.trim();
		for(String prefix:prefixs){
		if(prefix==null){continue; }
        int index=0;
        int len=prefix.length();
        while(v.startsWith(prefix)){
        	index=v.indexOf(prefix)+len;
        	v=v.substring(index, v.length());
        }
		}
        return v;
	}
	
	public static String trim(String value,String... fixs){
		String v=trimStart(value, fixs);
		v=trimEnd(v, fixs);
		return v;
	}  
	
	public static boolean isBlank(String pStr)
	{
		if(pStr==null)return true;
		CharSequence cs= (CharSequence) pStr;
		int strLen = pStr.length();
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(cs.charAt(i))) {
				return false;
			}
		}
		return true;
		
	}

	public static boolean isIn(String val, String[] vals) {
		// TODO Auto-generated method stub
		for(String v: vals)
		{		
			if((val==null&&v==null)||val.equals(v))return true;
		}
		return false;
	}
}
