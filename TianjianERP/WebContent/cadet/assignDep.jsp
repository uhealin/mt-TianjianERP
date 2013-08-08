<%@page import="com.matech.audit.service.cadet.model.CadetVO"%>
<%@page import="java.util.List"%>
<%@page import="com.matech.framework.pub.util.StringUtil"%>
<%@page import="com.matech.framework.pub.db.DbUtil"%>
<%@page import="com.matech.audit.pub.db.DBConnect"%>
<%@page import="java.sql.Connection"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%
   Connection conn=new DBConnect().getConnect();
   DbUtil dbUtil=null;
   String uuids=request.getParameter("uuids");
   List<CadetVO> cadetVOs=null;
    uuids=uuids.replace("'", "''");
    uuids=StringUtil.trim(uuids, ",");
    try{
    dbUtil=new DbUtil(conn);
	cadetVOs=dbUtil.select(CadetVO.class, "select * from {0} where {1} in ("+uuids+")");
	
   }catch(Exception ex){
	   
   }finally{
	   DbUtil.close(conn);
   }
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<style type="text/css">
  
</style>
<script type="text/javascript">
   Ext.onReady(function(){
	  
	   mt_form_initDateSelect();
   });
   
   function validate(){
	   if($("#departmentid").val()==""){
		   alert("请选择部门");return false;
	   }
	   //if($("#prac_real_start_date").val()>$("#prac_real_end_date").val()){
	  //	   alert("实习开始时间不能大于结束时间");return false;
	  // }
	   return true;
   }
</script>
</head>
<body>
<form action="${pageContext.request.contextPath }/cadet.do?method=doAssignDep" method="post" onsubmit="return validate()" >
   <input type="hidden" name="menuid" value="${param.menuid }" />
   <input type="hidden" name="formid" value="${param.formid }" />
   <input type="hidden" name="uuids" value="${param.uuids }" />
   <table class="formTable">
     <thead>
       <tr>
         <th colspan="2">实习生部门分配</th>
       </tr>
     </thead>
     <tbody>
       <tr >
          <th>
                                       所选实习生
          </th>
          <td>
          
            <% 
               for(CadetVO cadetVO :cadetVOs){
            %>
               <%=cadetVO.getName_cn() %>, &nbsp;
            <%} %>
           
           
          </td>
         
       </tr>
       
       <tr>
         <th>分配到部门</th>
         <td>
           <input type="text" name="departmentid" id="departmentid"   onfocus="onPopDivClick(this);"
				onkeydown="onKeyDownEvent();"
				onkeyup="onKeyUpEvent();"
				onclick="onPopDivClick(this);" 
				autoid=958 noinput=true multilevel=true   valuemustexist=true value="" class="required" />

         </td>
       
       </tr>
       
       <tr style="display: none;">
         <th>实际实习开始时间</th>
         <td><input ext_type="date" name="prac_real_start_date" id="prac_real_start_date"
          value="<%=StringUtil.getCurDate() %>"
          />
                                
         </td>
       </tr>
       
       
       <tr>
         <th></th>
         <td>
           <button type="submit">确认</button>
           <button type="button" onclick="window.history.back();">返回</button>
         </td>
       </tr>
       
     </tbody>
     
   </table>  
</form>
</body>
</html>