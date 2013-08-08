<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GB18030">
<title>账龄分析</title>

<script type="text/javascript">

function ext_init(){
	
	var tbar_${DataGrid } = new Ext.Toolbar({
		renderTo: 'divBtn',
		items:[{
			text:'查询',
			id:'btn-query',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
			handler:queryWinFun
		},'-',{
			id:'printid',
			text:'打印',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/print.gif',
			handler:function () {
				if('${DataGrid }'=='') return ;
				print_${DataGrid }();
			}
		},'-',{
			text:'关闭',
			cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/close.gif',
			handler:function () {
				closeTab(parent.tab);
			}
		},'->',{
			text:'刷新',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/refresh.gif',
			handler:function () {
				window.location.reload();
			}
			
		}${menuLocation}]
	});
	
} 

//人员查询
var queryWin = null;
function queryWinFun(id){

	if(!queryWin) { 
		var searchDiv = document.getElementById("search") ;
	    queryWin = new Ext.Window({
			title: '账龄分析查询',
	     	renderTo : searchWin,
	     	width: 400,
	     	height:220,
        	closeAction:'hide',
       	    listeners : {
	         	'hide':{
	         		fn: function () {
	         			new BlockDiv().hidden();
						queryWin.hide();
					}
				}
	        },
        	layout:'fit',
	    	html:searchDiv.innerHTML,
	    	buttons:[{
            	text:'确定',
          		handler:function(){
          			if (formSubmitCheck('thisForm')){
	          			getSubmit();
	          		
	               		queryWin.hide();
               		}
            	}
          		
          		//handler:goView
        	},{
            	text:'取消',
            	handler:function(){
               		queryWin.hide();
            	}
        	}]
	    });
    }
    new BlockDiv().show();
    queryWin.show();
}

function getSubmit(){
	thisForm.action = "${pageContext.request.contextPath}/analyse.do?method=subjectlist";
	thisForm.submit();
}

function goOpen(){
	var end = document.getElementById("EndYear").value;
	var cid = document.getElementById("CustomerID").value;
	var acc = "";
	if(cid !="" && end !="") acc = cid+ end;
	if(acc == "") return ;
	window.showModalDialog("${pageContext.request.contextPath}/analyse.do?method=day&AccPackageID="+acc+"&random="+Math.random(),window,'dialogHeight:400px;dialogWidth:300px;');
	
}

window.attachEvent('onload',ext_init);


</script>

<c:if test="${SubjectID == ''}"><script>window.attachEvent('onload',queryWinFun); </script></c:if>

</head>
<body>
<div id="divBtn"></div>
<form name="thisForm" method="post" action="" id="thisForm" >
<c:if test="${SubjectID == '' }">
	<span class="formTitle" >请点击[查询]按钮设置要校验的帐套</span>
</c:if>

<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>

<c:if test="${SubjectID != '' }">
	<div style="height:expression(document.body.clientHeight-25);" >
	<mt:DataGridPrintByBean name="${DataGrid }" outputData="true" outputType="invokeSearch" message="请选择账套编号" />
	</div>
	<input type="hidden" name="DataGrid" id="DataGrid" value="${DataGrid }">
</c:if>
</form>

<div id="search" style="display:none">
<br/>
<div style="margin:0 20 0 20">请在下面输入查询条件：</div>
<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 0 20" ></div>

	<table border="0" cellpadding="0" cellspacing="0" width="100%"  bgcolor="" >
		<tr align="center">
			
			<td align="right">单位编号<span class="mustSpan">[*]</span>：</td>
			<td align=left>
				<input  type="text" class="required" name="CustomerID" id="CustomerID" size="10" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"  valuemustexist=true autoid=2 value="${CustomerID}" refreshtarget="AccPackageID" title="单位编号"  />
			</td>
		</tr>
		<tr align="center">
			<td align="right">截止年<span class="mustSpan">[*]</span>：</td>
			<td align=left>
				<input title="截止年" class="required" hideresult="true" norestorehint=true  type="text" name="EndYear" id="EndYear" size="10" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" value="${EndYear}"   valuemustexist="true" autoid="118"  noinput="true" refer=CustomerID  />
				月份：
				<input type="text" name="EndDate" id="EndDate" value="${EndDate}" size="5" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"   valuemustexist="true" autoid="24"  noinput="true" />
			</td>
		</tr>
		<tr align="center">
			<td align="right">科目编号<span class="mustSpan">[*]</span>：</td>
			<td align=left>
				<input type="text" name="SubjectID" class="required" id="SubjectID" value="${SubjectID }" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"   valuemustexist=true multilevel=true   refer=CustomerID refer1=EndYear autoid=91  >
			</td>
		</tr>
		<tr align="center">
			<td align="right">账龄区间：</td>
			<td align=left>
				<input name="view" type="button" class="flyBT" value=" 区间设置 " onclick="goOpen()">
			</td>
		</tr>
		
	</table>
	<input name="sDate" type="hidden" id="sDate" value="${sDate }">
</div>


</body>
</html>


