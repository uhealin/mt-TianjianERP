<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>	
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>离职申请</title>
<script type="text/javascript" src="baiduUeditor/editor_all.js"></script>
<script type="text/javascript" src="baiduUeditor/editor_config.js"></script>
<link rel="stylesheet" href="baiduUeditor/themes/default/ueditor.css"/>

<script Language=JavaScript>

	function ext_init(){
	
	    var tbar = new Ext.Toolbar({
	   		renderTo: "divBtn",
	   		defaults: {autoHeight: true,autoWidth:true},
            items:[{ 
    			text:'保存',
    			cls:'x-btn-text-icon',
    			icon:'${pageContext.request.contextPath}/img/save.gif',
    			handler:function(){
    				addEdit();
    			}
       		},'-',{
	            text:'返回',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/back.gif',
	            handler:function(){
					window.history.back();
				}
       		},'->'
			]
        });
        
    }
    window.attachEvent('onload',ext_init);
</script>
</head>

<body leftmargin="0" topmargin="0">
<div id="divBtn" style="top: 0px;left: 0px;" ></div>

<form name="thisForm" action="${pageContext.request.contextPath}/projectManagerConfig.do?method=updateConfigGeneralReference" method="post">

<table width="70%" border="0" align="center" cellpadding="2" cellspacing="1" bgcolor="#6595d6">
			<caption  style="font-size: 25; text-align: center; height: 30">员工离职申请表</caption>
			<tbody id="SWlist">
			<tr height="30" class="DGtd">
			  <td height="30" align="center"  bgColor="#EEEEEE" ><b>姓名</b></td>
		      <td height="30"  bgColor="#ffffff"  id="title">
		       <input type="text" name="userId"  id="userId" style="display: none;" value="${user.id }" />
		       <input type="text" name=username  id="username"  value="${user.name }" />
		      </td>
			  <td height="30" align="center"  bgColor="#EEEEEE" ><b>性别</b></td>
		      <td height="30"  bgColor="#ffffff"  id="title">
		       <input type="text" name="sex"  id="sex"  value="${lc.sex}" />
		      </td>
			  <td height="30" align="center"  bgColor="#EEEEEE" ><b>所在部门</b></td>
		      <td height="30"  bgColor="#ffffff"  id="title">
		       <input type="text"  name="departmentId"  id="departmentId"   value="${lc.departmentid}"/>
		      </td>
			</tr>
			<tr height=30>
			  <td height="30" align="center" bgColor="#EEEEEE" ><b>出生年月</b></td>
		       <td height="30" bgColor="#ffffff" id="title"><input type="text" id="birthday" name="birthday" value="${lc.borndate }"/></td>
			  <td height="30" align="center" bgColor="#EEEEEE" ><b>参加工作时间</b></td>
		       <td height="30" bgColor="#ffffff" id="title"><input type="text" id="inworktime" name="inworktime" value=""/></td>
			  <td height="30" align="center" bgColor="#EEEEEE" ><b>进所时间</b></td>
		       <td height="30" bgColor="#ffffff" id="title"><input type="text" id="incompanytime" name="incompanytime" value="${lc.entrytime }"/></td>
			  </tr>
			  
			<tr height=30>
			  <td height="30" align="center" bgColor="#EEEEEE" ><b>最高学历</b></td>
		       <td height="30" bgColor="#ffffff" id="title"><input type="text" id="education" name="education" value="${lc.educational }"/></td>
			  <td height="30" align="center" bgColor="#EEEEEE" ><b>最高学位</b></td>
		       <td height="30" bgColor="#ffffff" id="title"><input type="text" id="degree" name="degree" value=""/></td>
			  <td height="30" align="center" bgColor="#EEEEEE" ><b>所学专业</b></td>
		       <td height="30" bgColor="#ffffff" id="title"><input type="text" id="major" name="major" value="${lc.profession }"/></td>
			  </tr>
			  
			<tr height=30>
			  <td height="30" align="center" bgColor="#EEEEEE" ><b>毕业院校</b></td>
		       <td height="30" bgColor="#ffffff" id="title" colspan="2"><input type="text" id="graduation" name="graduation" value="${lc.educational }"/></td>
			  <td height="30" align="center" bgColor="#EEEEEE" ><b>执业资格</b></td>
		       <td height="30" bgColor="#ffffff" id="title" colspan="2"><input type="text" id="qualification" name="qualification" value=""/></td>
			  </tr>
			  
			<tr height=30>
			  <td height="30" align="center" bgColor="#EEEEEE" ><b>现任职级</b></td>
		       <td height="30" bgColor="#ffffff" id="title"><input type="text" id="rank" name="rank" value="${lc.rank }"/></td>
			  <td height="30" align="center" bgColor="#EEEEEE" ><b>执业技术资格</b></td>
		       <td height="30" bgColor="#ffffff" id="title"><input type="text" id="techQualification" name="techQualification" value=""/></td>
			  <td height="30" align="center" bgColor="#EEEEEE" ><b>电话号码</b></td>
		       <td height="30" bgColor="#ffffff" id="title"><input type="text" id="mobilePhone" name="mobilePhone" value="${lc.mobilePhone }"/></td>
			  </tr>
			  
			<tr height=30>
			 <td height="30" bgColor="#EEEEEE" id="title">离职原因</td>
			 <td height="30" align="left" bgColor="#ffffff"  colspan="5">
			<font color="red" >( 注：请不要直接从 office 办公软件黏贴信息过来,可以从记事本黏贴信息过来。)</font> 	
			 <div style="height:280; overflow: auto; width: 99%;overflow-x:hidden;" >		  		
			 <textarea  style="width: 100%;" name="reason" id="reason"  value=""></textarea>		
			</div><br>
			</td>
		      </tr>
			  
			</tbody>
</table>
</form>
<script type="text/javascript">
	function clearVar(){
		document.getElementById("addtime").value="";
	    document.getElementById("addmoney").value="";
	}

	function addEdit(){
		//var hiddenPid = document.getElementById("hiddenPid").value;
		//var hiddenPid=""
		//alert("------"+hiddenPid);
		//var departmentId = document.getElementById("departmentId").value;
		//var translation = document.getElementById("translation").value;
		//if(departmentId!=""&&translation!=""){
			//if(isNaN(departmentId)||isNaN(translation)){
			//	alert("请填写数字");
			//	return;
		//	}
			
		//}else{
		//	alert("请填写信息");
		//	return;
		//}
	    //if(hiddenPid !=""){
	    //	alert("hiddenpid");
	   //    document.thisForm.action="${pageContext.request.contextPath}/leaveCompany.do?method=updateLeaveCompany";
	   // }else{
			document.thisForm.action="${pageContext.request.contextPath}/leaveCompany.do?method=addLeaveCompany";
		//}
		document.thisForm.submit();
	}
	
</script>
</body>
</html>