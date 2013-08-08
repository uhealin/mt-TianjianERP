<%@page import="com.matech.audit.service.doc.model.DocPostFileVO"%>
<%@page import="com.matech.framework.pub.util.StringUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="com.matech.framework.listener.UserSession"%>
<%@page import="com.matech.audit.service.user.model.UserVO"%>
<%@page import="com.matech.audit.service.doc.model.DocPostVO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.matech.framework.pub.util.WebUtil"%>
<%@page import="com.matech.audit.service.attachFileUploadService.model.MtComAttachVO"%>
<%@page import="java.text.MessageFormat"%>
<%@page import="com.matech.audit.service.attachFileUploadService.model.AttachExtVO"%>
<%@page import="com.matech.framework.pub.db.DbUtil"%>
<%@page import="com.matech.audit.service.attachFileUploadService.model.AttachFile"%>
<%@page import="java.util.List"%>
<%@page import="com.matech.audit.pub.db.DBConnect"%>
<%@page import="java.sql.Connection"%>    
 <%
      String uuid=request.getParameter("uuid");
      WebUtil webUtil=new WebUtil(request,response);
      UserSession userSession=webUtil.getUserSession();
      Connection conn=null;
      DbUtil dbUtil=null;
      DocPostFileVO docPostFileVO=null;
      
      try{
    	  conn=new DBConnect().getConnect();
    	  dbUtil=new DbUtil(conn);
    	  docPostFileVO=dbUtil.load(DocPostFileVO.class,uuid);
      }catch(Exception ex){
    	  ex.printStackTrace();
      }finally{
    	  DbUtil.close(conn);
      }
 %>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>作废或更改:<%=docPostFileVO.getTitle() %></title>

<script type="text/javascript">

  Ext.onReady(function(){
	  
	  mt_form_initDateSelect();
	  
  });

  function doCancel(){
	  var url="${pageContext.request.contextPath}/docpost.do?method=doCancelFile";
	  var param=$('#formCancel').serialize();
      $.post(url,param,function(str){
    	  alert(str);
    	  window.parent.location.reload();
    	  window.close();
      });
  }
  
</script>
</head>
<body>
  <form id="formCancel" method="post">
   <input name="uuid" type="hidden" value="<%=docPostFileVO.getUuid() %>" />
   <table class="formTable">

     <tbody>
       <tr>
         <th>文号</th>
         <td><%=docPostFileVO.getDoc_no() %></td>
       </tr>
       <tr>
         <th>操作类型</th>
         <td>
          <input type="radio" name="cancel_state" value="d"
          onclick="$('.trCancel').hide();" checked="checked"
           />删除
         <input type="radio" name="cancel_state" value="c" 
          onclick="$('.trCancel').show();"
          />作废
         <input type="radio" name="cancel_state" value="n"
          onclick="$('.trCancel').hide();"
          />更改为无文号
         </td>
       </tr>
       <tr class="trCancel" style="display: none;">
         <th >作废原因</th>
         <td><textarea cols="30" rows="5" name="cancel_reason"></textarea></td>
       </tr>
       <tr class="trCancel" style="display: none;">
          <th>作废日期</th>
          <td><input id="cancel_date" ext_type="date" name="cancel_date" value="<%=StringUtil.getCurDate() %>" /></td>
       </tr>
       <tr>
         <th></th>
         <td><button onclick="doCancel()" type="button">确认</button><button type="button" onclick="window.close()">取消</button></td>
       </tr>
     </tbody>
   </table>
   
  </form>
</body>
</html>