<%@page import="com.matech.audit.work.process.ProcessAction.ProcessCheckedColumns"%>
<%@page import="com.matech.audit.work.process.ProcessAction.ProcessUnCheckColumns"%>
<%@ page language="java" contentType="text/html;charset=utf-8" pageEncoding="utf-8" %>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
    <meta http-equiv="Cache-Control" content="no-store"/>
    <meta http-equiv="Pragma" content="no-cache"/>
    <meta http-equiv="Expires" content="0"/>
    <title>流程信息</title>
    
    <script type="text/javascript"> 
 
		Ext.onReady(function(){
			new Ext.Toolbar({
				renderTo:'toolDiv',
				items:[
					{ 
						text:'检查sql',
						icon:'${pageContext.request.contextPath}/img/save.gif' ,
						handler:function(){
							var url="process.do";
							var pkey=$("input[name=pkey]").val();
							var join_sql=$("#join_sql").val();
							var join_head_jarr=$("#join_head_jarr").val();
							var param={pkey:pkey,method:"checkAuditList",join_sql:join_sql,join_head_jarr:join_head_jarr};
							$.post(url,param,function(str){
								
								var json=eval("("+str+")");
								  var suc=json["re"]==0;
								  var title=suc?"测试成功":"测试失败,请检查sql";
								  //$("#re_"+code).html(title);
								 
								    Ext.MessageBox.alert(title,json["sql"]);
								  
							});
						}
					},'-',{ 
						text:'保存',
						icon:'${pageContext.request.contextPath}/img/save.gif' ,
						handler:function(){
							save();
						}
					},'-',{
						text:'返回',
						icon:'${pageContext.request.contextPath}/img/back.gif' ,
						handler:function(){
						      window.history.back();
						}
					}
				]
			});
		});
		 

		//window.attachEvent('onload',ext_init);
	</script>
    
  </head>
  <body>
  	<div id=toolDiv></div>
	<form name="thisForm" method="post">
		<table border="0" cellspacing="0" class="editTable" >
			<tr>
				<td class="editTitle" colspan="2">流程信息</td>
			</tr>
			
			<tr>
				<td align="right" nowrap="nowrap" style="width: 200px">流程名称<span class="mustSpan">[*]</span>：</td>
				<td align=left>
					<input type="text" size="60" name="pname" id="pname" class="required" value="${processDeploy.pname}">
				</td>
			</tr>
			
			<tr style="display: none;">
				<td nowrap="nowrap">审核关联表单<span class="mustSpan">[*]</span>：</td>
				<td>
					<input type="text" name="relateForm" id="relateForm" autoid=10003 noinput=true size=30 value="${processDeploy.relateForm}"/>
				</td>
			</tr>
			
			<tr style="display: none;">
				<td nowrap="nowrap">是否使用关联表单排序<span class="mustSpan">[*]</span>：</td>
				<td>
					<input type="text" name="orderByRelateForm" id="orderByRelateForm" autoid=700 refer="是否" noinput=true size=10 value="${processDeploy.orderByRelateForm}"/>
				</td>
			</tr>
			
			<tr>
				<td align="right" nowrap="nowrap" style="width: 200px">隐藏列<span class="mustSpan">[*]</span>：</td>
				<td align=left>
				            注意:以英文逗号分隔，参考值如下<br/>
				    审核页面<% for(ProcessUnCheckColumns un:ProcessUnCheckColumns.values()){ %>
				       <%=un.name() %>(<%=un.getLabel() %>),
				    <%} %>         <br/>
				 已 审核页面<% for(ProcessCheckedColumns ed:ProcessCheckedColumns.values()){ %>
				       <%=ed.name() %>(<%=ed.getLabel() %>),
				    <%} %>
					<input type="text" size="70" name="hidden_cols" id="hidden_cols" value="${processDeploy.hidden_cols}">
				</td>
			</tr>
			
			<tr >
			   <td align="right">外链sql：</td>
			   <td>
			      (注意 别名a已被占用 ,a.foreignUuid为外链id，sql格式 如: left join xxx b on a.foreignUuid =b.id left join........ )<br/>
			      <textarea rows="5" cols="70" id="join_sql" name="join_sql">${processDeploy.join_sql}</textarea>
			   </td>
			</tr>
			
			<tr >
			   <td align="right">外链表头：</td>
			   <td>
			      (使用json数组格式 如: [ {colName:"外链表别名.字段名",colAsName:"字段别名",colLabel:"显示名",colWidth:"宽度数字}"},.....] )<br/>
			      <textarea rows="8" cols="70" id="join_head_jarr" name="join_head_jarr">${processDeploy.join_head_jarr}</textarea>
			   </td>
			</tr>
			
			<tr>
				<td align="right">事项描述：</td>
				<td align=left>
					<textarea cols="70" rows="5" id="processDes" name="processDes">${processDeploy.processDes}</textarea> 
				</td>
			</tr>
			
			<tr>
				<td align="right">流程描述：</td>
				<td align=left>
					<textarea cols="70" rows="5" id="desccontent" name="desccontent">${processDeploy.desccontent}</textarea> 
				</td>
			</tr>
			
			   
		</table>
		<input type="hidden" name="id" id="id" value="${processDeploy.id}" />
		<input type="hidden" name="pkey" id="pkey" value="${processDeploy.pkey}" />
	</form>
  </body>
  
  <script type="text/javascript">
  
  	function save() {
  		if (!formSubmitCheck('thisForm')) {
    		return;
    	}

  		document.thisForm.action="${pageContext.request.contextPath}/process.do?method=save" ;
  		document.thisForm.submit();
  	}
  	
 
 </script>
  
</html>
