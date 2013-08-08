<%@page import="sun.misc.*"%>
<%@page import="com.matech.audit.service.user.model.UserVO"%>
<%@page import="com.matech.framework.pub.db.DbUtil"%>
<%@page import="com.matech.audit.pub.db.DBConnect"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.text.MessageFormat"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page  import="java.security.MessageDigest"%>
<%@ page  import="java.text.SimpleDateFormat"%>
<%@ page  import="java.util.Date"%>
<%@ page  import="com.matech.framework.listener.UserSession"%>
<%!

private static final String RESERVED_WORDS = "panchina";
private static String host="www.pccpa.com.cn";
private static String port="80";


/**
 * @return String
 * @description 获取服务器时间  注：凡是发送消息 以服务器时间为准，客户端时间不准 
 */
public static String getServerTime(){
	Date d = new Date();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	return sdf.format(d);
}

/**
 * 
 * @param justYearMonthDay true为年月日格式，false为年月日 时分秒格式
 * @return
 * @description 获取服务器时间  注：凡是发送消息 以服务器时间为准，客户端时间不准 
 */
public static String getServerTime(boolean justYearMonthDay){
	if(justYearMonthDay) {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(d);
	} else {
		return getServerTime();
	}
}

/**
 * MD5加密字符串
 * @param str
 * @return
 * @throws Exception
 */      //mailCheck.action?id={id}&loginId={loginid}&checkCode={}
         //mailAdmin.action?id={id}&loginId={loginid}&checkCode={}
public static String getMD5String(String str) throws Exception {   
	MessageDigest digest = MessageDigest.getInstance("md5");
	byte[] buf = digest.digest(str.getBytes());
	StringBuffer returnValue = new StringBuffer();
	for (int i = 0; i < buf.length; i++) {
		String x = Integer.toHexString(buf[i] & 0XFF);
		if(x.length() == 1) {
			x = 0 + x;
		}
		returnValue.append(x);
	}
	return returnValue.toString();
}

/**
 * 反转字符串
 * @param initialStr 初始字符串
 * @return
 */
private static String reverseString(String initialStr) {
	StringBuffer buffer = new StringBuffer(initialStr);
	return buffer.reverse().toString();
}

/**
 * BASE64编码
 * @param initialStr 初始字符串
 * @return
 */
public static String encoderByBASE64(String initialStr) {
	String uuid = uuid();
	String str = null;
	try {
		BASE64Encoder encoder = new BASE64Encoder();
		str = encoder.encode(initialStr.getBytes());
		str = reverseString(str);
		str = encoder.encode(str.getBytes());
		str = uuid.substring(0, 16) + str + uuid.substring(16, 32);
		str = encoder.encode(str.getBytes());
	} catch (Exception e) {
		e.printStackTrace();
	}
	return str;
}

/**
 * BASE64解码
 * @param initialStr 初始字符串
 * @return
 */
public static String decoderByBASE64(String initialStr) {
//	initialStr = initialStr.substring(16, initialStr.length() - 16);
	String str = null;
	try {
		BASE64Decoder decoder = new BASE64Decoder();
		str = new String(decoder.decodeBuffer(initialStr));
		str = str.substring(16, str.length() - 16);
		str = new String(decoder.decodeBuffer(str));
		str = reverseString(str);
		str = new String(decoder.decodeBuffer(str));
	} catch (Exception e) {
		e.printStackTrace();
	}
	return str;
}

/**
 * 获取一组随即字符串
 * @return
 */
private static String uuid() {
	String uuid = java.util.UUID.randomUUID().toString().toUpperCase();
	return uuid.replaceAll("-", "");
}

%>	
<%
    String act="true".equals(request.getParameter("admin"))?"mailAdmin.action":"mailCheck.action";
	UserSession userSession = (UserSession)request.getSession().getAttribute("userSession") ;
	String userid=userSession.getUserId();
	Connection conn=new DBConnect().getConnect();
	DbUtil dbUtil=new DbUtil(conn);
	UserVO userVO=dbUtil.load(UserVO.class, Integer.valueOf(userid));
	////id|loinid|getServerTime(true)|panchina_mail
	String checkCode=getMD5String(MessageFormat.format("{0}|{1}|{2}|{3}", 
		String.valueOf(userVO.getId())
		,userVO.getLoginid()
		,getServerTime(true)
		,"panchina_mail"
			));
    String action=MessageFormat.format("http://{0}:{1}/Panchina_Mail/{2}?id={3}&loginId={4}&checkCode={5}",
    		host,
    		port,
    		act,
    		userSession.getUserId()
    		,userSession.getUserLoginId()
    		,checkCode
    		);
%>



<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<title>天健即时通讯工具单点登陆</title> 
<br><br><br><br><br><br><br><br><br><br><br>

<form action="<%=action %>" 
	method="post" name="thisForm">
	<!--  
	<input type=hidden name=loginId value='<%=userVO.getLoginid() %>' />
	<input type=hidden name=checkCode value='<%= checkCode %>' />
	<input type="hidden" name="id" value="<%=userVO.getId()%>" />
	-->
正在登陆系统，请稍候......如果等待时间过长，请点击按钮：

<input type=submit value='手工登录'>
</form>

<script>

document.forms["thisForm"].submit();

</script>



</html>
