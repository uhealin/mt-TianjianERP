package com.matech.framework.pub.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import com.matech.framework.pub.sys.UTILSysProperty;

public class ASFuntion {

	private NumberFormat curformatter = new DecimalFormat("#,###,##0.00");

	/**
	 * 对结果集的以往行与当前行的某些列进行四则混合运算，从面改变当前行的输出
	 * <p>
	 * 1. map为某些临时变量累加存值使用，当datagrid循环打印完则自动释放
	 * </p>
	 * <p>
	 * 2. fieldValue为当前行的记录，读写会改变输出，数组长度不能改变
	 * </p>
	 * 
	 * @param map
	 *            临时变量累加存值使用
	 * @param fieldValue
	 *            结果集中当前行的记录
	 * @since 1.2
	 */
	public void process(Map map, String[] fieldValue) {
		// do
	}

	/**
	 * 对结果集上一行与当前行的某些列进行四则混合运算，从面改变当前行的输出
	 * <p>
	 * 1. fieldValue2为上一行记录，只能读，写不会改变当前输出 2. fieldValue为当前行的记录，读写会改变输出，但数组长度不能改变
	 * </p>
	 * 
	 * @param fieldValue2
	 *            结果集中的上一行记录
	 * @param fieldValue
	 *            结果集中当前行的记录
	 * @since 1.2
	 */
	public void process(String[] fieldValue2, String[] fieldValue) {
		double x = Double.valueOf(fieldValue2[5]).doubleValue()
				+ Double.valueOf(fieldValue[3]).doubleValue()
				- Double.valueOf(fieldValue[4]).doubleValue();
		fieldValue[5] = String.valueOf(x);
	}

	/**
	 * 获取当天
	 * 
	 * @return 当天字符串
	 */
	public final static String getToday() {
		return (new java.sql.Date(System.currentTimeMillis())).toString();
	}

	public final String showNull(String str) {
		if (str == null || "null".equals(str)) {
			str = "";
		}
		return str;
	}

	public final String showNull(String str, String v) {
		return "<td nowrap>" + showNull(str) + "</td>";
	}

	public String dealData(String n, String s) {
		if (n == null) {
			return "";
		}
		if (n.equals("showNull")) {
			return showNull(s);
		}
		return s;
	}

	// n 是format 字符串 s是数据库查出来的值，和传出去的值。
	// value 是witchisValue
	public String dealData(String n, String s, String value) {

		// 第一步，格式化内容。不带<td >
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
			s = this.maxLen(s, n);
		} else if (n.indexOf("hiddenLen") != -1) {
			s = this.hiddenLen(s, n);
		} else if (n.indexOf("showTakeOut") != -1) {
			s = showTakeOut(s, value);
		} else if (n.indexOf("showDoubt") != -1) {
			s = showDoubt(s, value);
		} else if (n.indexOf("showRectify") != -1) {
			s = showRectify(s, value);
		} else if (n.indexOf("showAnalyze") != -1) {
			s = showAnalyze(s, value);
		} else if (n.indexOf("showDate") != -1) {
			s = showDate(s, n);
		} else if (n.indexOf("showTaskTax") != -1) {
			s = showTaskTax(s, value);
		} else if (n.indexOf("showEliminate") != -1) {
			s = showEliminate(s, value);
		} else if (n.indexOf("showProportion") != -1) {
			s = showProportion(s);
		} else {
			s = "<td onselectstart=\"return false\" onmousemove=\"return selectMultiCell(event);\" onmousedown=\"return selectCell(event)\" style=\"\" > "
					+ s + "</td>";
		}

		/**
		 * add showing by left/right/center
		 */
		if (n.indexOf("showRight") != -1) {
			s = showRight(s);
		} else if (n.indexOf("showCenter") != -1) {
			s = showCenter(s);
		} else if (n.indexOf("showLeft") != -1) {
			s = showLeft(s);
		}

		// color of td
		if (n.indexOf("color") != -1) {
			s = color(s, n);
		}

		return s;
	}

	public String showWidth(String s, String v) {
		return "<td  id=\"widthTD" + v
				+ "\" nowarp width=\"100\" onselectstart=\"return false\">" + s
				+ "</td>";
	}

	public String showHidden(String s, String v) {
		return "<td  id=\"hiddenTD" + v
				+ "\" style=\"display:none\" onselectstart=\"return false\">"
				+ s + "</td>";
	}

	public String showAnalyze(String s, String v) {
		// ?strDate=2005-06-30&strSub=51010202&strDir=-1
		String[] values = s.split(",");
		if (values.length >= 3) {
			return "<td  id=\"analyzeTD"
					+ v
					+ "\" align=\"center\" onselectstart=\"return false\" ><a href='javascript:goAnalyze(\""
					+ values[0] + "\",\"" + values[1] + "\",\"" + values[2]
					+ "\");'>析</a></td>";
		} else {
			return "<td></td>";
		}
	}

	public String showRectify(String s, String v) {
		String APPTYPE = com.matech.framework.pub.sys.UTILSysProperty.SysProperty
				.getProperty("APPTYPE");
		if ("1".equals(APPTYPE)) {
			return "<td style='display:none'></td>";
		} else {
			if ("-1".equals(s)) {
				return "<td></td>";
			}

			return "<td  id=\"rectifyTD"
					+ v
					+ "\" align=\"center\" onselectstart=\"return false\" ><a href='javascript:goRectify("
					+ v + ");'>调</a></td>";
		}

	}

	public String showTaskTax(String s, String v) {
		String APPTYPE = com.matech.framework.pub.sys.UTILSysProperty.SysProperty
				.getProperty("APPTYPE");
		if ("1".equals(APPTYPE)) {
			return "<td style='display:none'></td>";
		} else {
			s = showNull(s);
			if (s.equals("")) {
				return ("<td  id=\"TaskTaxTD"
						+ v
						+ "\" align=\"center\" onselectstart=\"return false\" ><span onmouseover=\"autoDisplay(this,"
						+ v
						+ ");\" onmouseout=\"autoHidden();\"><a href='javascript:taskTax("
						+ v + ");'>税</a></span></td>");
			} else if ("0".equals(s)) {
				return "<td></td>";
			} else {
				// return ("<td></td>");
				return ("<td id=\"TaskTaxTD"
						+ v
						+ "\" align=\"center\" onselectstart=\"return false\" ><span onmouseover=\"autoDisplay(this,"
						+ v
						+ ");\" onmouseout=\"autoHidden();\"><a id=\"outT"
						+ v
						+ "\" href='javascript:taskTax("
						+ v
						+ ");'><font color=\"red\">税</font></a>&nbsp;|&nbsp;<a id=\"outT"
						+ v + "\" href='javascript:taskEdit(" + v + ");'><font color=\"red\">改</font></a></span></td>");

			}
		}

	}

	// 剔字
	public String showEliminate(String s, String v) {
		String APPTYPE = com.matech.framework.pub.sys.UTILSysProperty.SysProperty
				.getProperty("APPTYPE");
		if ("1".equals(APPTYPE)) {
			return "<td style='display:none'></td>";
		} else {
			s = showNull(s);
			if (s.equals("")) {
				return ("<td  id=\"EliminateTD"
						+ v
						+ "\" align=\"center\" onselectstart=\"return false\" ><a href='javascript:eliminate("
						+ v + ");'>剔</a></td>");
			} else if ("0".equals(s)) {
				return "<td></td>";
			} else {
				// return ("<td></td>");
				return ("<td id=\"EliminateTD"
						+ v
						+ "\" align=\"center\" onselectstart=\"return false\" ><a id=\"outT"
						+ v + "\" href='javascript:eliminate(" + v + ");'><font color=\"red\">撤</font></a></td>");

			}
		}

	}

	public String showDoubt(String s, String v) {
		s = showNull(s);
		if (s.equals("")) {
			return ("<td  id=\"doubtTD" + v
					+ "\" align=\"center\" onselectstart=\"return false\" > "
					+ "<a href='javascript:takeDoubt(" + v + ");'>疑</a></td>");
		} else if ("0".equals(s)) {
			return "<td onselectstart=\"return false\"></td>";
		} else {
			// return ("<td></td>");
			return ("<td id=\"doubtTD" + v
					+ "\" align=\"center\"  onselectstart=\"return false\" > "
					+ "<a id=\"outA" + v + "\" href='javascript:takeDoubt(" + v + ");'><font color=\"red\">撤</font></a></td>");

		}
	}

	/**
	 * 附件
	 * 
	 * @param s
	 * @param v
	 * @return
	 */
	public String showAttach(String s, String v) {
		s = showNull(s);

		if (s.equals("")) {
			return ("<td id=\"attachTD"
					+ v
					+ "\" align=\"center\" onselectstart=\"return false\" ><a id=\"attachA"
					+ v + "\" href='javascript:setAttach(" + v + ");' " + ">附</a></td>");
		} else if ("0".equals(s)) {
			return "<td onselectstart=\"return false\"></td>";
		} else {
			return ("<td id=\"outTD"
					+ v
					+ "\" align=\"center\"  onselectstart=\"return false\" ><a id=\"attachA"
					+ v + "\" href='javascript:setAttach(" + v + ");'><font color=\"red\">附</font></a></td>");
		}
	}

	public String showTakeOut(String s, String v) {
		s = showNull(s);

		if (s.equals("")) {
			return ("<td id=\"outTD"
					+ v
					+ "\" align=\"center\" onselectstart=\"return false\" ><a id=\"outA"
					+ v + "\" href='javascript:takeOutEntry(" + v + ");' " + ">抽</a></td>");
		} else if ("0".equals(s)) {
			return "<td onselectstart=\"return false\"></td>";
		} else {
			return ("<td id=\"outTD"
					+ v
					+ "\" align=\"center\"  onselectstart=\"return false\" ><a id=\"outA"
					+ v + "\" href='javascript:takeOutEntry(" + v + ");'><font color=\"red\">抽</font></a></td>");
		}

		// if(s.length()>=3)
		// {
		// s = s.substring(2);
		// }
		// else
		// {
		// return ("<td>"+s+"</td>");
		// }
		// if(s.equals("1"))
		// {
		// return ("<td id=\"outTD"+v+"\" align=\"center\"><a id=\"outA"+v+"\"
		// href='javascript:takeOutEntry("+v+");'>抽</a></td>");
		// }
		// else if(s.equals("2"))
		// {
		// return ("<td id=\"outTD"+v+"\" align=\"center\"><a id=\"outA"+v+"\"
		// href='javascript:takeOutEntry("+v+");'><font
		// color=\"red\">撤</font></a></td>");
		//
		// }
		// else
		// {
		// return ("<td id=\"outTD"+v+"\" align=\"center\"></td>");
		// }

	}

	/**
	 * “结”字：表示结转分录，s表示是否结转，v表示分录的autoid
	 * 
	 * @return
	 */
	public String showCarry(String s, String v) {
		s = showNull(s);

		if (s.equals("")) {
			return ("<td id=\"carryTD" + v
					+ "\" align=\"center\" onselectstart=\"return false\" >"
					+ "<a id=\"outC" + v + "\" href='javascript:takeCarry(" + v
					+ ");' " + ">结</a></td>");
		} else if ("0".equals(s)) {
			return "<td onselectstart=\"return false\"></td>";
		} else {
			return ("<td id=\"carryTD" + v
					+ "\" align=\"center\"  onselectstart=\"return false\" >"
					+ "<a id=\"outC" + v + "\" href='javascript:takeCarry(" + v + ");'><font color=\"red\">撤</font></a></td>");
		}
	}

	public String showMoney2(String s) {
		// 123,456,789.13
		return s == null ? "" : s.replaceAll(",", "");
	}

	public String showMoney3(String s) {
		s = showNull(s);
		if (!s.equals("")) {

			s = this.curformatter.format(Double.valueOf(s).doubleValue()); // -1,234,568
		}

		return s;
	}

	public String showMoney(String s) {

		s = showNull(s);
		if (!s.equals("")) {
			try {
				double d = Double.valueOf(s).doubleValue();
				if ("是".equals(UTILSysProperty.SysProperty.get("零值不显示"))
						&& Math.abs(d) < 0.005) {
					s = " ";
				} else {
					s = this.curformatter.format(d); // -1,234,568
				}
			}catch(NumberFormatException e) {
				//不处理
			}
			// org.util.Debug.prtOut(s);

			if (s.trim().indexOf("-") == 0) {
				return ("<td style=\"text-align:right;color:#FF0000\" unselectable=\"on\" onselectstart=\"return false\" onmousemove=\"return selectMultiCell(event);\" onmousedown=\"return selectCell(event)\"  nowrap>"
						+ s + "</td>");

			} else {
				return ("<td style=\"text-align:right;color:#0000FF\" unselectable=\"on\" onselectstart=\"return false\" onmousemove=\"return selectMultiCell(event);\" onmousedown=\"return selectCell(event)\"  nowrap>"
						+ s + "</td>");
			}
		} else {
			return ("<td style=\"text-align:right;color:#0000FF\" unselectable=\"on\" nowrap onselectstart=\"return false\" onmousemove=\"return selectMultiCell(event);\" onmousedown=\"return selectCell(event)\"></td>");
		}
	}

	public String showMoney1(String s) {

		s = showNull(s).trim();
		String s1 = "";
		if (!s.equals("")) {

			double d = Double.valueOf(s).doubleValue();
			if (d >= 0) {
				s1 = this.curformatter.format(d); // 1,234,568
			} else {
				s1 = "(" + this.curformatter.format(-d) + ")";
			}

			if ("是".equals(UTILSysProperty.SysProperty.get("零值不显示"))
					&& Math.abs(d) < 0.005) {
				s1 = " ";
			}

			if (s.indexOf("-") == 0) {
				return ("<td style=\"text-align:right;color:#FF0000\" onselectstart=\"return false\"  nowrap>"
						+ s1 + "</td>");

			} else {
				return ("<td style=\"text-align:right;color:#0000FF\" onselectstart=\"return false\"  nowrap>"
						+ s1 + "</td>");
			}
		} else {
			return ("<td style=\"text-align:right;color:#0000FF\" nowrap onselectstart=\"return false\"></td>");
		}
	}

	public String showMoney(String s, int x) {

		s = showNull(s);
		if (!s.equals("")) {
			String temp = "";
			for (int i = 0; i < x; i++) {
				temp += "0";
			}
			NumberFormat formatter = new DecimalFormat("#,###,##0." + temp);
			double d = Double.valueOf(s).doubleValue();
			if ("是".equals(UTILSysProperty.SysProperty.get("零值不显示"))
					&& Math.abs(d) < 0.005) {
				s = " ";
			} else {
				s = formatter.format(d); // -1,234,568
			}

			if (s.trim().indexOf("-") == 0) {
				return ("<font color=\"#FF0000\" >" + s + "</font>");

			} else {
				return ("<font color=\"#0000FF\" >" + s + "</font>");
			}
		} else {
			return ("");
		}
	}

	public String showMoney(String s, String style) {

		s = showNull(s);
		if (!s.equals("")) {

			s = this.curformatter.format(Double.valueOf(s).doubleValue()); // -1,234,568

			// org.util.Debug.prtOut(s);

			if (s.trim().indexOf("-") == 0) {
				return ("<td style=\"text-align:right;color:#FF0000\" " + style
						+ "  onselectstart=\"return false\" nowrap>" + s + "</td>");

			} else {
				return ("<td style=\"text-align:right;color:#0000FF\" " + style
						+ " onselectstart=\"return false\" nowrap>" + s + "</td>");
			}
		} else {
			return ("<td style=\"text-align:right;color:#0000FF\" " + style + " nowrap onselectstart=\"return false\"></td>");
		}
	}

	public String showPercent(double number) {
		NumberFormat formatter = new DecimalFormat("0.00");

		if (number < 0) {
			return ("<td style=\"text-align:right;color:#FF0000\" nowrap onselectstart=\"return false\">"
					+ formatter.format(number) + "%</td>");

		} else {
			return ("<td style=\"text-align:right;color:#0000FF\" nowrap onselectstart=\"return false\">"
					+ formatter.format(number) + "%</td>");
		}

	}

	public String showProportion(String number) {
		NumberFormat formatter = new DecimalFormat("0.00");

		double value = Double.parseDouble(number);
		if (value < 0) {
			return ("<td style=\"text-align:right;color:#FF0000\" nowrap onselectstart=\"return false\" >"
					+ formatter.format(value) + "%</td>");

		} else {
			return ("<td style=\"text-align:right;color:#0000FF\" nowrap onselectstart=\"return false\" >"
					+ formatter.format(value) + "%</td>");
		}
	}

	public String showRight(String s) {

		return s.replaceFirst("style=\\\"", "style=\"text-align:right;");

	}

	public String showLeft(String s) {
		return s.replaceFirst("style=\\\"", "style=\"text-align:left;");
	}

	public String showCenter(String s) {
		return s.replaceFirst("style=\\\"", "style=\"text-align:center;");
	}

	public String maxLen(String s, String format) {

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

		return "<td style=\"width:" + len
				+ "\" onselectstart=\"return false\" > " + s + "</td>";
	}

	public String showDate(String s, String format) {
		if (s == null || "".equals(s)) {
			return "<td style=\"\" nowarp onselectstart=\"return false\" >" + s
					+ "</td>";
		}

		int i = format.indexOf("showDate:") + 9;
		int j = format.length();
		String formatString = "";

		formatString = format.substring(i, j);

		try {
			java.util.Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.parse(s);
			return "<td style=\"\" nowarp onselectstart=\"return false\" >"
					+ new SimpleDateFormat(formatString).format(d) + "</td>";
		} catch (Exception e) {
			e.printStackTrace();
			return "<td style=\"\" nowarp onselectstart=\"return false\" >" + s
					+ "</td>";
		}

	}

	public String hiddenLen(String s, String format) {

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
			s = "<td style=\"\" title=\"" + s + "\">" + s.substring(0, len)
					+ "..." + "</td>";
		} else {
			s = "<td style=\"\" >" + s + "</td>";
		}

		return s;
	}

	public String color(String s, String format) {
		int i = format.indexOf("color:") + 6;
		int j = format.indexOf(" ", i);
		if (j == -1) {
			j = format.length();
		}
		String color = format.substring(i, j);

		return s.replaceFirst("style=\\\"", "style=\"color:" + color + ";");
	}

	public String getXMLData(String XML, String name) {
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

	public String getXMLData(String XML, String name, String defaultvalule) {
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

	public String setXMLData(String XML, String name, String data) {
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
	
	public String getPreDate() {
		java.text.SimpleDateFormat dateformat = new java.text.SimpleDateFormat(
				"yyyy-MM-dd");
		dateformat.setTimeZone(java.util.TimeZone.getTimeZone("GMT+0800"));
		
		Calendar cal=Calendar.getInstance();
        cal.add(Calendar.DATE,-1);
		return ("" + dateformat.format(cal.getTime()));
	}

	public String getCurrentDate(String strFormat) {
		java.text.SimpleDateFormat dateformat = new java.text.SimpleDateFormat(strFormat);
		dateformat.setTimeZone(java.util.TimeZone.getTimeZone("GMT+0800"));
		java.util.Date currentdate = new java.util.Date();
		return ("" + dateformat.format(currentdate));
	}
	
	
	public String getCurrentDate() {
		java.text.SimpleDateFormat dateformat = new java.text.SimpleDateFormat(
				"yyyy-MM-dd");
		dateformat.setTimeZone(java.util.TimeZone.getTimeZone("GMT+0800"));
		java.util.Date currentdate = new java.util.Date();
		return ("" + dateformat.format(currentdate));
	}

	public String getCurrentTime() {
		java.text.SimpleDateFormat dateformat = new java.text.SimpleDateFormat(
				"HH:mm:ss");
		dateformat.setTimeZone(java.util.TimeZone.getTimeZone("GMT+0800"));
		java.util.Date currenttime = new java.util.Date();
		return ("" + dateformat.format(currenttime));
	}

	// 字符串替换 s 搜索字符串 s1 要查找字符串 s2 要替换字符串
	public final String replaceStr(String s, String s1, String s2) {
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

	public static void main(String args[]) {
		ASFuntion asf = new ASFuntion();
		String s1;
		// org.util.Debug.prtOut(asf.showMoney2("123,456,789.13"));

		s1 = asf.showDate("1999-01-01 19:20:12", "showDate:yyyy-MM-dd mm-HH");
		System.out.println(s1);
	}

}
