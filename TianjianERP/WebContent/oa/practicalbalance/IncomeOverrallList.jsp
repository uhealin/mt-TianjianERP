<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/Validate_include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>

<%@ taglib prefix="mt" uri="http://www.matech.cn/tag"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>收款总览</title>


<script type="text/javascript">
	function ext_init() {
		var tbar_policy = new Ext.Toolbar( {
			renderTo : 'divBtn',
			items : [ {
				text : '查询',
				cls : 'x-btn-text-icon',
				icon : '${pageContext.request.contextPath}/img/query.gif',
				handler : queryWinFun
			}, '-', {
				text : '打印',
				cls : 'x-btn-text-icon',
				icon : '${pageContext.request.contextPath}/img/print.gif',
				handler : function() {
					goPrint();
				}
			}, '-',{
				text : '关闭',
				cls : 'x-btn-text-icon',
				icon : '${pageContext.request.contextPath}/img/close.gif',
				handler : function() {
					closeTab(parent.tab);
				}
			}, '->' ]
		});

	}
	window.attachEvent('onload', ext_init);
</script>

</head>

<body>

<div id="divBtn"></div>



<form name="thisForm" method="post" action="">
<div style="height: expression(document.body.clientHeight-23);">
<mt:DataGridPrintByBean name="IncomeOverrallList" /></div>

<div id="searchWin"
	style="position: absolute; left: expression(( document.body.clientWidth-250)/2 ); top: expression(this.offsetParent.scrollTop +200); z-index: 2"></div>

<div id="nav" style="display: none;"><iframe src="direct.jsp"
	style="width: 100%; height: 100%; padding: 0px; margin: 0px;"
	frameborder="0" scrolling="no"></iframe></div>


<div id="search" style="display: none"><input type="hidden"
	name="receicedate" id="receicedate" type="text" size="20" /> 
<fieldset><legend style="font-size: 12px;">收款总览查询</legend>
<table border="0" cellpadding="0" cellspacing="0" width="100%"
	bgcolor="">
	<tr align="center">
		<td align="right">项目名称：</td>
		<td align="left" colspan="3"><input type="text" name="projectid"
			id="projectid" maxlength="10" size="30" /></td>
	</tr>
	<tr align="center">
		<td align="right">项目负责人：</td>
		<td align="left" colspan="3"><input type="text" name="managerUserId"
			id="managerUserId" maxlength="10" title="请输入有效的值" size="30" onkeydown="onKeyDownEvent();"
					   onkeyup="onKeyUpEvent();"
					   onclick="onPopDivClick(this);"
					   onchange="setProjectName();"
					   autoid="617" /></td>
	</tr>
	<tr align="center">
		<td align="right">付款单位：</td>
		<td align="left" colspan="3"><input name="customername" id="customername"
			type="text" size="30" onkeydown="onKeyDownEvent();"
					   onkeyup="onKeyUpEvent();"
					   onclick="onPopDivClick(this);"
					   autoid=2 /></td>
	</tr>
	<tr align="center">
		<td align="right">委托号：</td>
		<td align="left" colspan="3"><input name="entrustNumber" id="entrustNumber"
			type="text" size="30"  /></td>
	</tr>
	<tr>
		<td align="right">委托内容：</td>
		<td align="left" colspan="3"><input name="auditpara" id="auditpara"
			type="text" size="30" onkeydown="onKeyDownEvent();"
					   onkeyup="onKeyUpEvent();"
					   onclick="onPopDivClick(this);"
					   onchange="setProjectName();"
					   autoid="58" /></td>
	</tr>
	<tr align="center">
		<td align="right">报告号：</td>
		<td align="left" colspan="3"><input name="reportNumber" id="reportNumber"
			type="text" size="30" /></td>
	</tr> 
	<tr>
		<td align="right">承接部门：</td>
		<td align="left" colspan="3"><input name="departname" id="departname"
			type="text" size="30"   onKeyDown="onKeyDownEvent();" 
					       onKeyUp="onKeyUpEvent();" 
					       onClick="onPopDivClick(this);" 
					       autoid=123 /></td>
	</tr>
	<tr>
		<td align="right">业务费用：</td>
		<td align="left" colspan="3">
			<input name="businesscost1" id="businesscost1"
			type="text" size="10" value="1" />(元) 至
			<input name="businesscost2" id="businesscost2"
			type="text" size="10" value="1000000" />(元)	
		</td>
	</tr>
	<tr>
		<td align="right" >建项日期：</td>
		<td align="left" ><input name="createDate1" id="createDate1" type="text" size="20"  /></td>
		<td align="left" >&nbsp;至&nbsp;</td>
		<td align="left" ><input name="createDate2" id="createDate2" type="text" size="20"  /></td>
	</tr>
	
	<tr>
		<td align="right">凭证号：</td>
		<td align="left" colspan="3"><input name="ctypenumber" id="ctypenumber"
			type="text" size="30" /></td>
	</tr>

</table>
</fieldset>
</div>
</form>
</body>
</html>

<Script type="text/javascript">
var date = new Date();
var year = date.getYear();
var month = date.getMonth();
month = month*1+1;
if(month*1<10){
	month = "0"+month; 
}
var day = date.getDate();
if(day*1<10){
	day = "0"+day;
}

// 显示前三年的收款纵览数据
document.getElementById("createDate1").value = (year*1-2)+"-"+month+"-"+day;
document.getElementById("createDate2").value = year+"-"+month+"-"+day;

	function go_select() {
		if (document.getElementById("ChooseItem").style.display == "none") {
			document.getElementById("ChooseItem").style.display = "";
		} else {
			document.getElementById("ChooseItem").style.display = "none";
		}
	}

	function goSearch() {
		queryWin.hide();
		goSearch_IncomeOverrallList();
		
		document.body.onselectstart = function () {} 
		document.body.unselectable="off" ;
	}

	function goPrint() {
		try {
			var printSQL = document.getElementById("printSql_IncomeOverrallList").value;
			if (printSQL == null || printSQL == "") {
				alert("请先进行查询，再使用本功能！");
			} else {
				print_IncomeOverrallList();
			}
		} catch (e) {
			alert("请先进行查询，再使用本功能！");
		}
	}

	// 条件查询
	var queryWin = null;
	function queryWinFun(id) {
		var searchDiv = document.getElementById("search");
		searchDiv.style.display = "";
		if (!queryWin) {
			queryWin = new Ext.Window( {
				title : '菜单查询',
				contentEl : 'search',
				renderTo : searchWin,
				width : 540,
				height : 350,
				closeAction : 'hide',
				listeners : {
					'hide' : {
						fn : function() {
							new BlockDiv().hidden();
							queryWin.hide();
						}
					}
				},
				layout : 'fit',
				buttons : [ {
					text : '搜索',
					handler : function() {
						goSearch();
					}
				}, {
					text : '清空',
					handler : function() {
						document.getElementById("projectid").value = "";
						document.getElementById("customername").value = "";
						document.getElementById("entrustNumber").value = "";
						document.getElementById("auditpara").value = "";
						document.getElementById("reportNumber").value = "";
						document.getElementById("departname").value = "";
						document.getElementById("businesscost1").value = "";
						document.getElementById("businesscost2").value = "";
						document.getElementById("createDate1").value = "";
						document.getElementById("createDate2").value = "";
						goSearch();
					}
				}, {
					text : '取消',
					handler : function() {
						queryWin.hide();
					}
				} ]
			});
 			var cdate1 = new Ext.form.DateField({
				applyTo : 'createDate1',
				width: 135,
				format: 'Y-m-d',
				emptyText: '' 
			});
	    	var cdate2 = new Ext.form.DateField({
				applyTo : 'createDate2',
				width: 135,
				format: 'Y-m-d', 
				emptyText: '' 
		});
	    
		    }
		    new BlockDiv().show();
		    queryWin.show();
		}

	function grid_dblclick(obj){
		window.location="getFunds.do?method=goDetail&&projectid="+obj.projectid;
	}  
	
	Ext.onReady(function () {
		document.body.onselectstart = function () {} 
		document.body.unselectable="off" ;
	}) ;

</Script>
