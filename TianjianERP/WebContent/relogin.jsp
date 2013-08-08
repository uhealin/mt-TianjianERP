<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page  import="java.security.MessageDigest"%>
<%@ page  import="java.text.SimpleDateFormat"%>
<%@ page  import="java.util.Date"%>
<%@ page  import="com.matech.framework.listener.UserSession"%>
<%!

private static final String RESERVED_WORDS = "panchina";

//123
/*private static String host="172.19.7.123";
private static String port="8088";*/

private static String host="www.pccpa.com.cn";
private static String port="80";

/**
 * 获取url
 * @param id
 * @return
 */
public String getChatUrl(String id) {
	String checkCode = null;
	try {
		checkCode = getMD5String(id + "|" + getServerTime() + "|" +RESERVED_WORDS);
	} catch (Exception e) {
		e.printStackTrace();
	}
	return checkCode;
}

/**
 * 系统时间
 * @return
 */
private static String getServerTime(){
	Date d = new Date();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	return sdf.format(d);
}

/**
 * MD5加密
 * @param str
 * @return
 * @throws Exception
 */
private static String getMD5String(String str) throws Exception {
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

%>	
<%
	UserSession userSession = (UserSession)request.getSession().getAttribute("userSession") ;
	String userid=userSession.getUserId();
	String checkCode=getChatUrl(userid);
  
%>
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<title>天健即时通讯工具单点登陆</title> 
<br><br><br><br><br><br><br><br><br><br><br>
<form action="http://<%=host %>:<%=port %>/Panchina_ChatSys/login.action" 
	method="post" name="thisForm">
	<input type=hidden name=id value='<%= userid %>'>
	<input type=hidden name=checkCode value='<%= checkCode %>'>
正在登陆系统，请稍候......如果等待时间过长，请点击按钮：

<input type=submit value='手工登录'>
</form>


<script>
thisForm.submit();

</script>



</html>
