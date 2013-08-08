<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/Validate_include.jsp"%>
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
			 <c:if test="${opt!='view'}">
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
	        </c:if>
	        <c:if test="${opt=='view'}">
		        {
					text:'查看',
					cls:'x-btn-text-icon',
					icon:'${pageContext.request.contextPath}/img/query.gif',
					handler:goView 
				},'-',
			</c:if>
	        {
	            text:'关闭',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/close.gif',
	            handler:function () {
	           		closeTab(parent.tab);
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
<mt:DataGridPrintByBean name="enterpriseQualificationList"  />
</div>

</form>

</body>
</html>


<script type="text/javascript">


function goSearch(){
	queryWin.hide();
    goSearch_enterpriseQualificationList(); 
} 

// 清空
function goClear(){
	document.getElementById("name").value="";
	document.getElementById("organ").value="";
	document.getElementById("place").value="";
	document.getElementById("device").value="";
	document.getElementById("describes").value="";
	
	queryWin.hide();
    goSearch_enterpriseQualificationList(); 
    
}

// 添加
function goAdd(){
	document.getElementById("thisForm").action = "${pageContext.request.contextPath}/enterpriseQualification.do?method=go&opt=add"; 
	document.getElementById("thisForm").submit();
}

//编辑
function goEdit(){
	var id = document.getElementById("chooseValue_enterpriseQualificationList").value;
	if(id==""){
		alert("请选择一项！");
	}else{
		document.getElementById("thisForm").action = "${pageContext.request.contextPath}/enterpriseQualification.do?method=go&opt=update&id="+id; 
		document.getElementById("thisForm").submit();
	}
}


function grid_dblclick(obj){
	//document.getElementById("thisForm").action = "${pageContext.request.contextPath}/enterpriseQualification.do?method=view&id="+obj.uuid; 
	//document.getElementById("thisForm").submit();
}


// 删除
function goDelete(){
	if(document.getElementById("chooseValue_enterpriseQualificationList").value==""){
		alert("请选择一项！");
	}else{
		if(confirm("是否确定删除吗？","提示")){
			window.location="enterpriseQualification.do?method=delete&&id="+document.getElementById("chooseValue_enterpriseQualificationList").value;
		}
	}
}

function goView(){
	var id = document.getElementById("chooseValue_enterpriseQualificationList").value;
	if(id==""){
		alert("请选择一项！");
	}else{
		document.getElementById("thisForm").action = "${pageContext.request.contextPath}/enterpriseQualification.do?method=view&id="+id; 
		document.getElementById("thisForm").submit();
	}
} 

</Script>


