<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">


<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>

</head>
<body>

<form action="">
<input type="hidden" name="opt" id="opt" value="${opt}" />
<table  class="formTable">

<thead>
  <tr>
    <th colspan="2">实习生考核申请</th>
  </tr>
  </thead>
  <tbody>
  <tr>
    <th>姓名：</th>
    <td><input type="text" style="width: 40%" name="name_cn" id="name_cn"></td>
  </tr>
  <tr>  
    <th>身份证号：</th>
    <td><input type="text" style="width: 40%" name="idcard" id="idcard"></td>
  </tr>

  <tr>
     <th>实习指导老师：</th>
     <td><input id="checkerid" name="checkerid"  autoid="10016" value="" /></td>
   </tr>
   <tr>
     <th>实习报告：</th>
     <td><input type="hidden" id="report" value="" /></td>
   </tr>
   <tr>
     <th>参与项目情况：</th>
     <td><textarea id="project_case" cols="30" rows="5"></textarea></td>
   </tr>
   <tr>
       <th></th>
    <td>
    
     <button type="button" onclick="doApplyCheck()">申请考核</button>
     <button type="button" onclick="parent.window.close()">退出</button>
    
    </td>
    </tr>
  </tbody>
</table>
</form>
<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
备注：请如实填写实习指导老师参与项目情况等信息，一旦点击发出申请，填写资料将无法修改。



</body>
<script type="text/javascript">

Ext.onReady(function(){
	attachInit("report");
});

function check(){

	var id = document.getElementById("idcard").value;
	var name = document.getElementById("name_cn").value;
	
	if(name == "" || name == null){
		alert("姓名不能为空");
		return false;
	}
	
	 var b = validateIdCard(id);
	 if(b == 0){
		 return true;
	 }else{
		 alert("您输入的身份证号有误，请重新输入");
		 return false;
	 }
}

function doApplyCheck(){
	
	if(!check())return false;
	var idcard = document.getElementById("idcard").value;
	var name = document.getElementById("name_cn").value;
	var report=$("#report").val();
	var checkerid=$("#checkerid").val();
	var project_case=$("#project_case").val();
	if(report==''||checkerid==''||project_case==''){
		alert("请确认完成填写相关资料");
		return false;
	}
   // alert(checkerid);
	if(!confirm("是否确认提交考核申请？提交后将无法修改"))return;
	var url="${pageContext.request.contextPath}/cadet.do?method=doApplyCheck";
	var param={name_cn:name,idcard:idcard,report:report,checkerid:checkerid,project_case:project_case};
	$.post(url,param,function(str){alert(str);parent.window.close();});
	
}



</script>
</html>