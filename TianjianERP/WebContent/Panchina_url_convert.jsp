<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="com.matech.framework.listener.UserSession"%>
<%@ page import="java.security.MessageDigest"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%
	String currurl = request.getScheme()+"://"+ request.getServerName()+":"+request.getServerPort()+request.getRequestURI()+"?"+request.getQueryString(); 
	System.out.println("--------currurl:"+currurl);
	String baseurl = currurl.substring(0,currurl.indexOf("/erp")+1);
	
	UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
	//out.println("用户ID="+userSession.getUserId());
	//out.println("areaid="+userSession.getAreaid());
	
	if(userSession==null){
		out.println("对不起，您还未登录，请先登录！");
		return;
	}
	
	if(request.getParameter("url")!=null){
		String url=request.getParameter("url");
		System.out.println("--------url0:"+url);
		String jumpToUrl = "";
		if(request.getParameter("AE") == null){
			if(url.indexOf("http://")==-1){
				url=baseurl+url;
			}
			
			System.out.println("--------url:"+url);
			
			jumpToUrl=url+"?areaid="+userSession.getAreaid()+"&userid="+userSession.getUserId();
			
			if(request.getParameter("classEName")!=null){
				String _classEName=request.getParameter("classEName");
				
				//特殊处理
				if(_classEName.equals("tjzx_")){
					String _areaid=userSession.getAreaid();
					if(_areaid.equals("1251")) _classEName="tjzx_bj";
					if(_areaid.equals("1253")) _classEName="tjzx_sh";
					if(_areaid.equals("1255")) _classEName="tjzx_cs";
					if(_areaid.equals("1257")) _classEName="tjzx_sz";
					if(_areaid.equals("1259")) _classEName="tjzx_gz";
					if(_areaid.equals("1261")) _classEName="tjzx_sd";
					if(_areaid.equals("1263")) _classEName="tjzx_hf";
					if(_areaid.equals("1265")) _classEName="tjzx_yn";
					if(_areaid.equals("1271")) _classEName="tjzx_hb";
					if(_areaid.equals("1273")) _classEName="tjzx_cq";
					if(_areaid.equals("1275")) _classEName="tjzx_sc";
					if(_areaid.equals("1100")) _classEName="tjzx_hz";
				}
				
				jumpToUrl+="&classEName="+_classEName;
			}
			if(request.getParameter("classType")!=null){
				jumpToUrl+="&classType="+request.getParameter("classType");
			}
			if(request.getParameter("specialID")!=null){
				jumpToUrl+="&specialID="+request.getParameter("specialID");
			}
			if(request.getParameter("userID")!=null){
				jumpToUrl+="&userID="+request.getParameter("userID");
			}
			if(request.getParameter("start")!=null){
				jumpToUrl+="&start="+request.getParameter("start");
			}
			if(request.getParameter("limit")!=null){
				jumpToUrl+="&limit="+request.getParameter("limit");
			}
			if(request.getParameter("areaID")!=null){
				jumpToUrl+="&areaID="+request.getParameter("areaID");
			}
			if(request.getParameter("autoID")!=null){
				jumpToUrl+="&autoID="+request.getParameter("autoID");
			}
		}else {
			String id = userSession.getUserId();
			String loginId = userSession.getUserLoginId();
			Date d = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String serverTime = sdf.format(d);
			String str = id + "|" + loginId + "|" +serverTime + "|panchina_aeSys";
			MessageDigest digest = MessageDigest.getInstance("md5");
			byte[] buf = digest.digest(str.getBytes());
			StringBuffer returnValue = new StringBuffer();
			for(int i = 0;i < buf.length;i++) {
				String x = Integer.toHexString(buf[i] & 0XFF);
				if(x.length() == 1) {
					x = 0 + x;
				}
				returnValue.append(x);
			}
			String checkCode = returnValue.toString();
			jumpToUrl = "http://oaae.pccpa.cn/" + url + "?id=" + id + 
											"&loginId=" + loginId +
											"&checkCode=" + checkCode;
		}
		System.out.println("jumpToUrl="+jumpToUrl);
	%>
		<script>
			window.location.href='<%=jumpToUrl%>';
		</script>
	<%	
	}else{
		out.println("请传参数url和mark");
	}
	
%>