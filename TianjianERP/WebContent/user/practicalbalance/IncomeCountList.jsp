<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/Validate_include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>

<%@ taglib prefix="mt" uri="http://www.matech.cn/tag"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>收款信息</title>


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
<mt:DataGridPrintByBean name="IncomCountList" outputData="true"
	outputType="invokeSearch" /></div>

<div id="searchWin"
	style="position: absolute; left: expression(( document.body.clientWidth-250)/2 ); top: expression(this.offsetParent.scrollTop +200); z-index: 2"></div>

<div id="nav" style="display: none;"><iframe src="direct.jsp"
	style="width: 100%; height: 100%; padding: 0px; margin: 0px;"
	frameborder="0" scrolling="no"></iframe></div>


<div id="search" style="display: none"><input type="hidden"
	name="receicedate" id="receicedate" type="text" size="20" /> 
<fieldset><legend style="font-size: 12px;">业务收入统计查询</legend>
<table border="0" cellpadding="0" cellspacing="0" width="100%"
	bgcolor="">
	<tr align="center">
		<td align="right">项目名称：</td>
		<td align="left" colspan="3"><input type="text" name="projectid"
			id="projectid" maxlength="10" title="请输入有效的值"  size="20" /></td>
	</tr>
	<tr align="center">
		<td align="right">收款日期：</td>
		<td align="left"><input name="receicedate1" id="receicedate1"
			type="text" size="20" /></td>
		<td align="center">至</td>
		<td align="left"><input name="receicedate2" id="receicedate2"
			type="text" size="20" /></td>
	</tr>
	<tr align="center">
		<td align="right">付款单位：</td>
		<td align="left"><input name="customername" id="customername"
			type="text" size="20" onkeydown="onKeyDownEvent();"
					   onkeyup="onKeyUpEvent();"
					   onclick="onPopDivClick(this);"
					   autoid=2 /></td>
		<td align="right">委托内容：</td>
		<td align="left"><input name="auditpara" id="auditpara"
			type="text" size="20" onkeydown="onKeyDownEvent();"
					   onkeyup="onKeyUpEvent();"
					   onclick="onPopDivClick(this);"
					   onchange="setProjectName();"
					   autoid=58" /></td>
	</tr>
	<tr align="center">
		<td align="right">项目负责人：</td>
		<td align="left"><input name="name" id="name"
			type="text" size="20" onkeydown="onKeyDownEvent();"
						   onkeyup="onKeyUpEvent();"
						   onclick="onPopDivClick(this);"
						   autoid="617" /></td>
		<td align="right">承接部门：</td>
		<td align="left"><input name="departname" id="departname"
			type="text" size="20"   onKeyDown="onKeyDownEvent();" 
					       onKeyUp="onKeyUpEvent();" 
					       onClick="onPopDivClick(this);" 
					       autoid=123 /></td>
	</tr>
	
	<tr align="center">
		<td align="right">委托号：</td>
		<td align="left">
			<input name="entrustNumber" id="entrustNumber" type="text" size="20"  />
		</td>
		<td align="right">报告号：</td>
		<td align="left">
			<input name="reportNumber" id="reportNumber" type="text" size="20"  />
		</td>
	</tr>
	
	<tr align="center">
		<td align="right">凭证号：</td>
		<td align="left">
			<input type="text"
				   name="ctypenumber"
				   id="ctypenumber"
				   maxlength="10"
				   title="请输入有效的值" 
				   size="20"  /> 
		</td>
		<td align="right">帐面分类：</td>
		<td align="left">
			<input name="accounttype" id="accounttype" type="text" size="20" 
						   onKeyDown="onKeyDownEvent();" 
					       onKeyUp="onKeyUpEvent();" 
					       onClick="onPopDivClick(this);" 
					       valuemustexist=true 
					        autoid=4574  />
		</td>
	</tr>
	
	<tr align="center">
		<td align="right" >收款金额：</td>
		<td align="left" >
			<input name="receiceMoney1" id="receiceMoney1" type="text" size="20"  />
		</td>
		<td align="center" >至</td>
		<td align="left" >
			<input name="receiceMoney2" id="receiceMoney2" type="text" size="20"  />
		</td>
	</tr>
	
	<tr align="center">
			<td align="right" >收款形式：</td>
			<td align="left">
				<input type="text"
				   name="ctype"
				   id="ctype"
				   maxlength="10"
				   title="请输入有效的值"
				   onkeydown="onKeyDownEvent();"
				   onkeyup="onKeyUpEvent();"
				   onclick="onPopDivClick(this);"
				   size="20"
				   autoid=4573 /> 
			</td>
			<td align="right" >&nbsp;</td>
			<td align="left" >
				&nbsp;
			</td>
		</tr>

</table>
</fieldset>
</div>
</form>
</body>
</html>

<Script type="text/javascript">
	function go_select() {
		if (document.getElementById("ChooseItem").style.display == "none") {
			document.getElementById("ChooseItem").style.display = "";
		} else {
			document.getElementById("ChooseItem").style.display = "none";
		}
	}

	function goSearch() {
		document.getElementById("receicedate").value = "";
		var receicedate1 = document.getElementById("receicedate1").value;
		var receicedate2 = document.getElementById("receicedate2").value;
		if (receicedate1 != "" && receicedate2 != "") {
			document.getElementById("receicedate").value = receicedate1 + "!"
					+ receicedate2;
		}
		queryWin.hide();
		goSearch_IncomCountList();
		
		document.body.onselectstart = function () {} 
		document.body.unselectable="off" ;
	}

	function goPrint() {
		try {
			var printSQL = document.getElementById("printSql_IncomCountList").value;
			if (printSQL == null || printSQL == "") {
				alert("请先进行查询，再使用本功能！");
			} else {
				print_IncomCountList();
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
				title : '业务收入统计查询',
				contentEl : 'search',
				renderTo : searchWin,
				width : 740,
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
						window.location = "getFunds.do?method=incomeCount";
					}
				}, {
					text : '取消',
					handler : function() {
						queryWin.hide();
					}
				} ]
			});

			  var cdate1 = new Ext.form.DateField({
					applyTo : 'receicedate1',
					width: 135,
					format: 'Y-m-d',
					value:new Date(),
					emptyText: '' 
				});
			    var cdate2 = new Ext.form.DateField({
					applyTo : 'receicedate2',
					width: 135,
					format: 'Y-m-d', 
					emptyText: '' 
				});
			    
		    }
		    new BlockDiv().show();
		    queryWin.show();
		}

	function grid_dblclick(obj){
		
	}
	
	Ext.onReady(function () {
		document.body.onselectstart = function () {} 
		document.body.unselectable="off" ;
	}) ;

</Script>
