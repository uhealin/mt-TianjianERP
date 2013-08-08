<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>	
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp" %>
<%@ page import="java.util.*" %>
<%@page import="com.matech.framework.pub.util.ASFuntion"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>联系人资料</title>
<%
	ASFuntion CHF = new ASFuntion();
	List list = new ArrayList();
	if(request.getAttribute("manager_list") != null){
		list = (List)request.getAttribute("manager_list");
	}
%>
<script type="text/javascript">  

function ext_init(){
	
	var tbar_customer = new Ext.Toolbar({
   		renderTo: "divBtn",
   		height:30,
   		defaults: {autoHeight: true,autoWidth:true},
           items:[
           <c:if test="${empty param.agent || param.agent==''}">
           { 
            text:'保存',
            icon:'${pageContext.request.contextPath}/img/save.gif',
            handler:function(){
				setManager();
			}
      	},'-',
      	</c:if>
      	{ 
            text:'返回',
            icon:'${pageContext.request.contextPath}/img/back.gif',
            handler:function(){
                  <c:choose>
                 <c:when test="${param.agent=='agent'}">
				    window.location="${pageContext.request.contextPath}/agent.do?method=birthToCust";
				 </c:when>
				 <c:otherwise>
				  	window.location="${pageContext.request.contextPath}/manager1.do?departid=<%=CHF.showNull(request.getParameter("departid")) %>&frameTree=1";
				 </c:otherwise>
               </c:choose>
			
			}
      	},'-',new Ext.Toolbar.Fill()]
	});
	
} 



window.attachEvent('onload',ext_init);
</script>

</head>
<body >
<div id="divBtn" ></div>



<input name="autoid" type="hidden" id="autoid"  value="${autoid}"/> 
<input name="customerid" type="hidden" id="customerid"  value="<%=CHF.showNull(request.getParameter("departid")) %>"/> 

<fieldset  style="width:100%">
<legend>联系人资料</legend>
<br>
<jodd:form bean="manager1" scope="request">
<form name="thisForm" action="" method="post">

<table width="100%" height="150" border="0" cellpadding="0" cellspacing="0">

    <tr>
    <td width="25%"><div align="right"><font color="red" size=3>*</font>职位：</div></td>
    <td width="75%" ><input name="position" type="text" class='required' id="position"  maxlength="40" 	autoid="227" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"  title="请选择职位">  
    </td>
  </tr>
      <tr>
    <td width="25%"><div align="right"><font color="red" size=3>*</font>姓名：</div></td>
    <td width="75%" ><input name="name" type="text" class='required' id="name"  maxlength="40"title="请正确输入姓名" >   
    </td>
  </tr>
      <tr>
    <td width="25%"><div align="right"><font color="red" size=3></font>性别：</div></td>
    <td width="75%" ><input name="sex" type="text" id="sex" maxlength="20" 	autoid="228" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);">
    
    </td>
  </tr>
  
  <tr>
    <td width="25%"><div align="right"><font color="red" size=3></font>出生日期：</div></td>
    <td width="75%" ><input name="birthday" type="text" id="birthday" title="请输入出生年月！" showcalendar="true">  
    </td>
  </tr>
  
      <tr>
    <td width="25%"><div align="right"><font color="red" size=3></font>学历：</div></td>
    <td width="75%" ><input name="qualification" type="text"  id="qualification"  maxlength="40" title="请输入最高学历">   
    </td>
  </tr>
  
        <tr>
    <td width="25%"><div align="right"><font color="red" size=3></font>手机：</div></td>
    <td width="75%" ><input name="mobilephone" type="text"  class="phonenumber-wheninputed" id="mobilephone"  maxlength="40" title="请输入正确的联系电话">   
    </td>
  </tr>
  
   <tr>
    <td width="25%"><div align="right"><font color="red" size=3></font>座机：</div></td>
    <td width="75%" ><input name="fixedphone" type="text"  class="phonenumber-wheninputed" id="fixedphone"  maxlength="40" title="请输入正确的联系电话">   
    </td>
  </tr>

      <tr>
    <td width="25%" valign="top"><div align="right"><font color="red" size=3></font>邮箱：</div></td>
    <td width="75%" ><input name="email" type="text" id="email"  class="validate-email" maxlength="20">   				 
    </td>
  </tr>
  
    
      <tr>
    <td width="25%" valign="top"><div align="right"><font color="red" size=3></font>其他联系方式1：</div></td>
    <td width="75%" ><input name="contact1" type="text" id="contact1" maxlength="20">   				 
    </td>
  </tr>
  
        <tr>
    <td width="25%" valign="top"><div align="right"><font color="red" size=3></font>其他联系方式2：</div></td>
    <td width="75%" ><input name="contact2" type="text" id="contact2" maxlength="20">   				 
    </td>
  </tr>

  </tr>
    <tr>
      <td align="right"><div >工作简历：</div></td>
      <td colspan="3"><textarea name="resume" id="resume" cols="50" rows="10"  title="工作简历"></textarea></td>
    </tr>
    
	<tr> 
	<td width="25%"></td>
	<td width="75%">
	
	*<font Color="FF0000">工作简历字数不得超过1000字</font>
	
	</td>
	
	</tr>
</table>



 </form>
</jodd:form>
</body>
<script>
new Validation("thisForm");
 
function setManager(){

	
	var autoid = document.getElementById("autoid").value;
	var customerid = document.all.customerid.value;
	var resume = document.getElementById("resume").value;
	
	if(resume.length>1000){
	alert("输入的工作简历字数不得超过1000字!");
	return false;
	}


    if(autoid !=""){
    

       document.thisForm.action="${pageContext.request.contextPath}/manager1.do?method=updateManager&autoid=" + autoid+"&customerid=" +customerid ;
        
    	
    }else{

    	
		document.thisForm.action="${pageContext.request.contextPath}/manager1.do?method=Manageradd&customerid=" + customerid;
	}
	
	document.thisForm.submit();
}

try{  
    var action = "${action}";
    

        
    if(action=="look"){
   
         
         var aa = document.getElementsByTagName("input") ;
         var bb = document.getElementsByTagName("textarea") ;
         
         for(var i=0;i<aa.length;i++) {
         if(!aa[i].nodisable){
         	aa[i].disabled="true";
         	}
         }
         for(var i=0;i<bb.length;i++){
         	if(!bb[i].nodisable){
         		bb[i].disabled="true";
         	}
         }
            
    
    }else{
   
    }

}catch(e){

}


</script>
</html>