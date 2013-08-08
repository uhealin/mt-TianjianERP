<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@page import="com.matech.audit.service.role.model.RoleTable"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<%
	RoleTable rt = (RoleTable)request.getAttribute("rt");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title>角色定义</title>

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
	            
	            	if (!formSubmitCheck('thisForm')) return;
	            	
	            	if(document.thisForm.id.value!=""){
						document.thisForm.adored.value="ed";
					}
					document.thisForm.action="role.do?method=del";	
					document.thisForm.submit();
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

<div id="divBtn"></div>


<form name="thisForm" method="post" action="" id="thisForm"  class="autoHeightForm">


<span class="formTitle" >
角色维护<br/><br/> 
</span>


<table width="75%" height="182" border="0" cellpadding="0" cellspacing="0">

    <tr>
      <td width="140"><div align="right">角色名称<span class="mustSpan">[*]</span>：</div></td>
      <td>
      	<input name="rolename" type="text" id="rolename" value="<%=rt==null ?"": rt.getRolename() %>" size="50"  maxlength="20" class="required" title="请输入，不得为空" >
      </td>
    </tr>
	
	<tr>
      <td width="140"><div align="right">角色功能：</div></td>
      <td>
      	<textarea name="rolevalue" cols="55" rows="5" maxlength="200" id="rolevalue" onkeyup="if(this.value.length>200)this.value=this.value.substring(0,200);"><%=rt==null ?"": rt.getRolevalue() %></textarea>
      </td>
    </tr>
	
	<tr>
      <td width="140"><div align="right">优先级：</div></td>
      <td>
      	<input name="property" id="property" value="<%=rt==null ?"": rt.getProperty() %>">
      </td>
    </tr>
	<tr>
      <td width="140"><div align="right">属性：</div></td>
      <td>
      	<input  type="checkbox" id="ltype" name="ltype" value="部门维护" <%=rt==null ?"": ((rt.getLtype() == null || !"部门维护".equals(rt.getLtype())) ? "" :"checked" ) %> >部门可维护
      </td>
    </tr>  
	<tr>
      <td width="140"><div align="right">对应职级：</div></td>
      <td>
      	<input name="innername" type="text" id="innername" value="<%=rt==null ? "": (rt.getInnername() == null ? "" : rt.getInnername())%>" >
      </td>
    </tr> 
	<tr>
      <td width="140"><div align="right">审批权限：</div></td>
      <td>
      	<input name="property" type="text" id="property" value="<%=rt==null ?"": rt.getProperty() %>" >
      </td>
    </tr>            
</table>

<input name="id" type="hidden" id="id" value="<%=rt==null ?"": rt.getId() %>">
<input name="adored" type="hidden" id="adored" value="ad">

</form>
</body>
</html>