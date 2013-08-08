<%@page import="com.matech.audit.work.form.FormDefineAction"%>
<%@page import="com.matech.audit.service.user.model.JobVO"%>
<%@page import="java.util.List"%>
<%@page import="com.matech.framework.pub.db.DbUtil"%>
<%@page import="com.matech.audit.pub.db.DBConnect"%>
<%@page import="java.sql.Connection"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
   Connection conn=new DBConnect().getConnect();
   List<JobVO>  jobVOs=null;
   String type=request.getParameter("type");
   String type_name="";
   try{
     DbUtil dbUtil=new DbUtil(conn);
     if(type==null||type.isEmpty()){
     jobVOs=dbUtil.select(JobVO.class, "select * from {0} where state=?  ","有效");
     }else{
    	 
    	 if(type.equals("0")){
    		 type_name="社会招聘";
    	 }else if(type.equals("1")){
    		 type_name="应届生招聘";
    	 }else{
    		 throw new Exception("没有改类型的工作");
    	 }
         jobVOs=dbUtil.select(JobVO.class, "select * from {0} where state=? and type=? ","有效",type_name);
     }
     
   }finally{
	   DbUtil.close(conn);
   }
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<title>Insert title here</title>

<style type="text/css">
  #divJobList input{
     border-width: 0px
  }
</style>
<script type="text/javascript">

var parentList = window.dialogArguments; //接收传参
var idFieldId = parentList.idFieldId;  

var idFieldName = parentList.idFieldName;   
var partentWindow = parentList.partentWindowObj; //父窗口window对象

  function loadJob(obj){
	  var url="${pageContext.request.contextPath}/job.do";
	
      var param={method:"jobView",table:"k_job",flag:"list",unid:obj.id};
      
      $("#divJob").load(url,param,function(){
    	  $("#divJob").find("input,textarea,select").each(function(index){
    		  $(this).attr("disabled","disabled");
    	  });
      });
  }
  
  function doSelectJob(){
	  var job=$("input[name=job]:checked");
	  //alert( job.attr["jobName"]);
	  partentWindow.document.getElementById(idFieldId).value=job.attr("id");
	  partentWindow.document.getElementById(idFieldName).value=job.attr("jobName");
	  window.close();
  }
</script>
</head>
<body>
    <div id="divJobList">
      <fieldset>
       <legend><%=type_name %> 可选工作</legend>
      <%for(JobVO jobVO:jobVOs){ %>
        <span id="jobName_<%=jobVO.getUnid() %>"> <input type="radio" value="<%=jobVO.getUnid() %>" jobName="<%=jobVO.getJobname() %>"  name="job" id="<%=jobVO.getUnid() %>" onclick="loadJob(this)"  />
         
        <%=jobVO.getJobname() %></span>  &nbsp;
      <%} %>
      <button type="button" onclick="doSelectJob()">选择</button>
      <button type="button" onclick="window.close()">返回</button>
      </fieldset>
    </div>
    
    <div id="divJob">
    
    </div>
</body>
</html>