<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
 

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>企业咨询</title>
 
<Script type="text/javascript">

function ext_init(){ 
		var tbar = new Ext.Toolbar({
		renderTo: 'divBtn',
		items:[
<c:if test="${op=='0'}">
			{
				text:'新增',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/add.gif',
				handler:function () {
					goAdd();
				}
			},'-',
			{
	           text:'修改',
	           cls:'x-btn-text-icon',
	           icon:'${pageContext.request.contextPath}/img/edit.gif',
	          	handler:function(){
					goEdit();
				}
	        },'-',
			{
	           text:'删除',
	           cls:'x-btn-text-icon',
	           icon:'${pageContext.request.contextPath}/img/delete.gif',
	          	handler:function(){
					goDelete();
				}
	        },'-',
/*	        
	        {
		           text:'发布',
		           cls:'x-btn-text-icon',
		           icon:'${pageContext.request.contextPath}/img/confirm.gif',
		          	handler:function(){
						goUpdate();
					}
			},'-',
*/			
</c:if>
<c:if test="${op=='1'}">
		        {
					text:'回复',
					cls:'x-btn-text-icon',
					icon:'${pageContext.request.contextPath}/img/flow.gif',
					handler:function(){
						goAnswer();
					}
				},'-',
</c:if>
	        {
	            text:'查询',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/query.gif',
	            handler:function () {
	            	customQryWinFun('${tableid}');
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

<form id="thisForm" name="thisForm" method="post" action="">
<div style="height:expression(document.body.clientHeight-23);" >
<mt:DataGridPrintByBean name="${tableid}"  />
</div>

<input id="op" name="op" type="hidden" value="${op}">
<input id="classid" name="classid" type="hidden" value="${classid}">

</form>

</body>
</html>


<script type="text/javascript">

// 添加
function goAdd(){
	document.getElementById("thisForm").action = "${pageContext.request.contextPath}/content.do?method=edit"; 
	document.getElementById("thisForm").submit();
}

//编辑
function goEdit(){
	var id = document.getElementById("chooseValue_${tableid}").value;
	if(id==""){
		alert("请选择一项！");
	}else{
		document.getElementById("thisForm").action = "${pageContext.request.contextPath}/content.do?method=edit&autoid="+id; 
		document.getElementById("thisForm").submit();
	}
}


function grid_dblclick(obj){
	
}


// 删除
function goDelete(){
	if(document.getElementById("chooseValue_${tableid}").value==""){
		alert("请选择一项！");
	}else{
		if(confirm("是否确定删除吗？","提示")){
			Ext.Ajax.request({
				method:'POST',
				params : { 
					autoid : document.getElementById("chooseValue_${tableid}").value								
				},
				url:MATECH_SYSTEM_WEB_ROOT + "/content.do?method=del&rand="+Math.random(),
				success:function (response,options) {
					var result = response.responseText;
					alert(result);
					goSearch_${tableid}();				
				},
				failure:function (response,options) {
					alert("后台出现异常,获取文件信息失败!");
				}
			});
		}
	}
}

function goAnswer(){
	var id = document.getElementById("chooseValue_${tableid}").value;
	if(id==""){
		alert("请选择一项！");
	}else{
		document.getElementById("thisForm").action = "${pageContext.request.contextPath}/content.do?method=answer&autoid="+id; 
		document.getElementById("thisForm").submit();
	}
} 

</Script>


