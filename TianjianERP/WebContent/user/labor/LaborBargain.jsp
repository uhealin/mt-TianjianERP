<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>劳动合同</title>
<script type="text/javascript">


function ext_init(){ 
	var tbar = new Ext.Toolbar({
		renderTo: 'divBtn',
		items:[{
			text:'新增',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/add.gif',
			handler:function () {
				goAdd();
			}
		},'-',{
           text:'修改',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/edit.gif',
           handler:function(){
				goUpdate();
		   }
        },'-',{
			text:'删除',
			id:'del',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/delete.gif',
			handler:function(){
				goDelete();
			}
		},'->'
        ]
     });  
}
window.attachEvent('onload',ext_init);

</script>
</head>
<body>
<div id="divBtn"></div><!--


<input name="add" type="button" class="flyBT" value="增  加"
	onclick="return goAdd();">
&nbsp;
<input name="update" type="button" class="flyBT" value="修  改"
	onclick="return goUpdate();">
&nbsp;
<input name="delete" type="button" class="flyBT" value="删  除"
	onclick="return goDelete();">
&nbsp;
<input name="save" type="button" class="flyBT" value="打  印"
	onclick="print_LaborBargainList();">
&nbsp;

<br>
<br>
--><form id="thisForm" name="thisForm" action="" method="post"/><!--

<c:if test="${all=='all' }">
<fieldset style="width:100%">
    <legend>查询条件</legend>
	<table width="100%" height="46" border="0" cellpadding="0" cellspacing="0" bgcolor="">
	<tr>
		<td>
		合同编号：&nbsp;&nbsp;<input type="text" name="laborCode" id="laborCode" value="" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" autoid=2002>
		&nbsp;&nbsp;
		合同人：&nbsp;&nbsp;<input type="text" name="laborPerson" id="laborPerson" value="" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" autoid=2003>
		<input type="submit" name="srh" class="flyBT" value="搜 索" />
             &nbsp;
             <input type="reset" name="csrh" value="显示全部" class="flyBT" onclick="thisForm.submit();" />
             </td>
	  </tr>
  </table>
</fieldset>
</c:if>


-->
<div style="height:expression(document.body.clientHeight-28);" >
<mt:DataGridPrintByBean name="LaborBargainList" />
</div>
</form>
</body>
<script>
//添加劳动合同
function goAdd(){
	
    window.location="${pageContext.request.contextPath}/oa/labor/LaborBargainadd.jsp?all=${all}" ;
	       
}   
//修改劳动合同
function goUpdate(){
	var choose_laborbargain = document.getElementById("chooseValue_LaborBargainList").value;
	
	if(choose_laborbargain == ""){
		alert("请选择要修改的劳动合同记录！");
	} else {		
		window.location="${pageContext.request.contextPath}/laborbargain.do?method=exitLabor&autoid=" + choose_laborbargain+"&all=${all}";		
	}
}   
//删除劳动合同
function goDelete(){
	var choose_laborbargain = document.getElementById("chooseValue_LaborBargainList").value;

	if(choose_laborbargain == ""){
		alert("请选择要删除的劳动合同记录！");
	} else {
		if(confirm("您的操作可能会造成数据丢失，您确定要删除该记录吗？","提示")){
			window.location="${pageContext.request.contextPath}/laborbargain.do?method=removeLabor&autoid=" + choose_laborbargain+"&all=${all}";	
		}
	}
} 

</script>

</html>
