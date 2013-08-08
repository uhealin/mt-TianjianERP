<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>复核小组</title>
<style type="text/css">
td div,th div{
float: left;

}
input{
width: 95%;
	}
</style>
<script type="text/javascript">


Ext.onReady(function (){
new Validation('thisForm');
});
function ext_init(){
	
    var tbar = new Ext.Toolbar({
   		renderTo: "divBtn",
   		defaults: {autoHeight: true,autoWidth:true},
        items:[
               
               { 
			text:'保存',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/save.gif',
			handler:function(){
  
				document.thisForm.submit();
            	 
			}
   		},'-',{
            text:'返回',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/back.gif',
            handler:function(){
				window.location.href="${pageContext.request.contextPath}/formDefine.do?method=formListView&uuid=aa2742a8-042d-4aaf-8469-eee86ae48571&formId=aa2742a8-042d-4aaf-8469-eee86ae48571&formTypeId=838a1d67-462d-4b64-a25b-b49935b85658";
			}
   		},'->'
		]
    });
    
}
window.attachEvent('onload',ext_init);
</script>
</head>
<body>
<div id="divBtn"></div>
<form action="${pageContext.request.contextPath}/businessreport.do?method=updateVindicateGroup" method="post" name="thisForm" id="thisForm" >
<table class="formTable">
				<input type="hidden" id="group_member" name="group_member" value="${groupVindicateVO.group_member }"  />
				<input type="hidden" id="uuid" name="uuid" value="${groupVindicateVO.uuid }"  />
				<input type="hidden" id="departmentid" name="departmentid" value="${groupVindicateVO.departmentid }"  />
				<input type="hidden" id="group_id" name="group_id" value="${groupVindicateVO.group_id }"  />
				<input type="hidden" id="group_property" name="group_property" value="${groupVindicateVO.group_property }"  />
				<input type="hidden" id="old_groupHeanMan" name="old_groupHeanMan" value="${old_groupHeanMan }"  />
				<input  id="group_department_name" name="group_department_name"  value="${groupVindicateVO.group_department_name }"   type="hidden"   />
	<thead>
		<tr>
			<th colspan="2">

				复核小组</th>
		</tr>
	</thead>
	<tbody>
		<tr>
			<th style="text-align: right;width: 20%">
				复核小组</th>
			<td>
				<input  id="group_name" name="group_name"  size="50"  type="text"  width="50"  value="${groupVindicateVO.group_name }"  /></td>
		</tr>
		<tr>
			<th style="text-align: right">
				组长</th>
			<td>
				<input   id="group_headman" name="group_headman" size="50"  width="50"   autoid=10016 type="text"  value="${groupVindicateVO.group_headman }"  /></td>
		</tr>
		<tr>
			<th style="text-align: right">
				组员</th>
			<td>
				<input  id="group_member_name"  size="120" name="group_member_name"    type="text"  value="${groupVindicateVO.group_member_name }"  /></td>
		</tr>
		<tr>
			<th style="text-align: right">
				对口部门(或分所)</th>
			<td>
				<input  id="group_departmentid" size="120"  name="group_departmentid" multiselect="true"  value="${groupVindicateVO.group_departmentid }"   autoid=30026 onselect="group_departmentid_onSelect(this)"   type="text" /></td>
		</tr>
	</tbody>
</table>
</form>
<script>
 Ext.onReady(function(){
 $("#group_member_name").click(function(){
show_selectUser("group_member_name","group_member");
})
});   // Ext.onReady
function group_departmentid_onSelect(obj){
$("#group_department_name").val(obj.value);
}
</script>
</body>
</html>