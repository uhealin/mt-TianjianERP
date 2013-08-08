<%@page import="com.matech.framework.pub.util.StringUtil"%>
<%@page import="com.matech.audit.service.club.model.ClubVO"%>
<%@page import="com.matech.audit.pub.db.DBConnect"%>
<%@page import="com.matech.framework.listener.UserSession"%>
<%@page import="com.matech.framework.pub.util.WebUtil"%>
<%@page import="com.matech.framework.pub.db.DbUtil"%>
<%@page import="java.sql.Connection"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%
Connection conn=null;
DbUtil dbUtil=null;
WebUtil webUtil=new WebUtil(request, response);
UserSession userSession=webUtil.getUserSession();
int eff=0;
String re="";
response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
ClubVO clubVO=null;
String uuid=request.getParameter("uuid");
String rtype=request.getParameter("rtype");
String rname="in".equals(rtype)?"收入":"支出";
try{
	conn=new DBConnect().getConnect();
	dbUtil=new DbUtil(conn);
	clubVO=dbUtil.load(ClubVO.class, uuid);
}catch(Exception ex){
	re=ex.getLocalizedMessage();
}finally{
	DbUtil.close(conn);
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><%=clubVO.getName() %>俱乐部 <%=rname %>登记</title>
<script type="text/javascript">

  Ext.onReady(function(){
	  
	  mt_form_initDateSelect();
	  
  });
  
  function winClose(isRefrash) {
      window.returnValue = isRefrash;
      window.close();
  }
  
  function doSubmit(){
	  
	  var url="${pageContext.request.contextPath}/club.do?method=doFinanceReg";
	  var param=$("#regForm").serialize();
	  $.post(url,param,function(str){
		  alert(str);
		  //window.opener.window.location.reload();
		  winClose(1);
	  });
	  
  }
  
  function loadEvent(){
	  var event_id=$("input[name=event_id]").val();
	  var url="${pageContext.request.contextPath}/club.do?method=jsonEvent";
      var param={uuid:event_id};
	  $.getJSON(url,param,function(json){
		  $("#member_ids").val(json["member_ids"]);
		  $("#member_names").val(json["member_names"]);
	  });
  }

</script>
</head>
<body>
<form id="regForm" >
    <input type="hidden" name="ftype" value="<%=rtype %>" />
    <input type="hidden" name="club_id" value="<%=clubVO.getUuid() %>" />
    
    <table class="formTable">
    <tr>
        
      <tr>
        <th><%=rname %>日期</th>
        <td><input name="create_date" id="create_date" ext_type="date" value="<%=StringUtil.getCurDate() %>" /></td>
      </tr>
       <tr>
        <th>登记人</th>
        <td>
          <input name="userid" id="userid" autoid="10016" value="<%=userSession.getUserId() %>" />
        </td>
      </tr>
       <tr>
        <th>收入金额</th>
        <td>
          <input name="amount" id="amount"   />
        </td>
      </tr>
       <tr> 
        <th>内容</th>
        <td>
          <textarea name="descp" id="descp" rows="3" cols="30"   ></textarea>
        </td>
      </tr>
      <%if("in".equals(rtype)){ %>
        <th>会费标准</th>
        <td><%=clubVO.getFee_amount() %></td>
      </tr>
        <tr>
        <th>缴费人</th>
        <td>
          <input name="member_ids" id="member_ids"  type="hidden"  />
          <textarea rows="6" cols="30"  name="member_names" id="member_names" onclick="show_selectUser('member_names','member_ids')" readonly="readonly" ></textarea>
        </td>
        </tr>
      <%}else if("out".equals(rtype)){ %>
      <tr>
        <th>相关活动</th>
        <td>
          <input autoid="5025" name="event_id" id="event_id" onselect="loadEvent()" refer="<%=clubVO.getUuid() %>" />
        </td>
      </tr>
      <tr>
        <th>参与人</th>
        <td>
          <input name="member_ids" id="member_ids"  type="hidden"  />
          <textarea rows="6" cols="30"  name="member_names" id="member_names" onclick="show_selectUser('member_names','member_ids')" readonly="readonly" ></textarea>
        </td>
        </tr>
      <%} %>
      <tr>
        <th></th>
        <td><button type="button" onclick="doSubmit()">提交</button>&nbsp;&nbsp;
        <button type="button" onclick="winClose(0)">取消</button>
        </td>
      </tr>
    </table>
</form>
</body>
</html>