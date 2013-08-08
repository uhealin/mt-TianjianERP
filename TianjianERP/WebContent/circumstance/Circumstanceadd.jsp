<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>环境设置</title>

<script type="text/javascript">

Ext.onReady(function(){
	var tbar_customer = new Ext.Toolbar({
		renderTo:'divBtn',
           items:[{
            text:'确定',
            icon:'${pageContext.request.contextPath}/img/edit.gif',
            handler:function(){
            	if (!formSubmitCheck('thisForm')) return;
            	setCircumstanceType();
            	document.thisForm.submit() ;
			}
      	},'-',{
           text:'返回',
           icon:'${pageContext.request.contextPath}/img/refresh.gif',
          	handler:function(){
				history.back() ;
			}
        },'->'
        ]
        });  

});

</script>

</head>
<body>
<div id="divBtn"></div>
<input name="autoid" type="hidden" id="autoid"  value="${autoid}"/> 
<input name="sautoid" type="hidden" id="sautoid"  value="${circumstance.sautoid}"/> 
<input name="multiselect" type="hidden" id="multiselect"  value="${circumstance.multiselect}"/> 
<jodd:form bean="circumstance" scope="request">
<form name="thisForm" action="" method="post" class="autoHeightForm">
<fieldset  style="width:100%">
<legend>环境设置</legend>
<br>

<table width="100%" height="150" border="0" cellpadding="0" cellspacing="0">
  
    <tr>
    <td width="25%"><div align="right"><font color="red" size=3></font>参数名称：</div></td>
    <td width="75%" >${circumstance.sname}<input name="sname" type="hidden"  id="sname"  maxlength="40" readonly="readonly">  
    </td>
  </tr>
 
  <tr> 
  		<td width="25%"  align="right"><font color="red" size=3></font>参数设定：</td>
 
 <c:if test="${circumstance.sautoid =='' || circumstance.sautoid ==null}">		
	<td><input id="svalue" name="svalue" title="请输入，不得为空" size="30" class="required" value="${circumstance.svalue}"/></td>
</c:if>	 
 
<c:if test="${circumstance.sautoid!='' && circumstance.multiselect=='' }">		
					
	<td><input autoid="${circumstance.sautoid }"  id="svalue" name="svalue" title="请输入，不得为空" size="30" noinput="true"  	onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();"onclick="onPopDivClick(this);"  value="${circumstance.svalue}"/></td>
   
</c:if>	  
<c:if test="${circumstance.sautoid!='' && circumstance.multiselect!='' }">
	 
	<td><input autoid="${circumstance.sautoid }"  id="svalue" name="svalue" title="请输入，不得为空" size="30"  noinput="true"   onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();"	onclick="onPopDivClick(this);" multiselect="true" value="${circumstance.svalue}"/></td>

</c:if>
  
   </tr>
   
    <tr>
    <td width="25%" valign="top"><div align="right"><font color="red" size=3></font>参数用途说明：</div></td>
    <td width="75%" ><textarea cols="50" rows="7" name="smemo" id="smemo" readonly="readonly" ></textarea>
    </td>
  </tr>
      <tr>
    <td width="25%"><div align="right"><font color="red" size=3></font>最后修改人：</div></td>
    <td width="75%" ><input name="upuser" type="text" id="upuser"  maxlength="40" readonly="readonly">   
    
    </td>
  </tr>
      <tr>
    <td width="25%"><div align="right"><font color="red" size=3></font>最后修改时间：</div></td>
    <td width="75%" ><input name="uptime" type="text" id="uptime" maxlength="20" readonly="readonly">
   
    </td>
  </tr>
  
</table>
</form>
</jodd:form>
</body>

<script>

function setCircumstanceType(){

	
	var autoidvalue = document.getElementById("autoid").value;
					
    document.thisForm.action="${pageContext.request.contextPath}/circumstance.do?method=updatecircumstance&autoid=" + autoidvalue;
          	
}

</script>
</html>