<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>工资审核</title>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/searchSession.js" charset=gbk></script>

<script type="text/javascript">

Ext.onReady(function(){
	
	var html = "<table><tr><td>退回至：</td><td><input name='backstatus' id='backstatus' type=text autoid=4621 value='' refer='${param.status}' size='10' onkeydown='onKeyDownEvent();' onkeyup='onKeyUpEvent();' onclick='onPopDivClick(this);' noinput=true refer='status' ></td></tr></table>";
	
	//工资发放列表工具条
	var tbar_customer = new Ext.Toolbar({
		renderTo:'divBtn',
           items:[{
        <c:if test="${view != 'view'}">
			text:'修改',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/edit.gif',
			handler:function(){
				
				//新版本用EXCEL方式在线修改
				var pch = document.getElementById("chooseValue_personnelList").value;
				if(pch==""){
					alert("请选择一项！");
					return;
				}
				var url  ="${pageContext.request.contextPath}/salary.do?method=export&pch="+pch;
			    window.open(url); 
			}
		</c:if>
		<c:if test="${view == 'view'}">
			text:'查看详情',
			id:'btn-showall',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
			handler:function (){
				 //新版本用EXCEL方式在线修改
				var pch = document.getElementById("chooseValue_personnelList").value;
				if(pch==""){
					alert("请选择一项！");
					return;
				}
				var url  ="${pageContext.request.contextPath}/salary.do?method=export&pch="+pch+"&mode=view";
			    window.open(url); 
			}
		</c:if>
		},'-',{
			text:'审批',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/confirm.gif',
			handler:function(){
				goNext();
			}
		},'-',{
			text:'查询',
			id:'btn-query',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
			handler:queryWinFun
		},'-',html,{
			text:'确定',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/back.gif',
			handler:function(){
				goBack();
			}
		},
		
		'->'
        ]
    });  
	
	initCombox(document.getElementById("backstatus"));
});


var queryWin = null;
function queryWinFun(id){
	var searchDiv = document.getElementById("search") ;
	searchDiv.style.display = "" ;
	if(!queryWin) { 
	    queryWin = new Ext.Window({
			title: '发放工资审批查询',
			contentEl:'search',
	     	renderTo : searchWin,
	     	width: 300,
	     	height:200,
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
	    	buttons:[{
            	text:'搜索',
          		handler:function(){
          			goSearch_personnelList();
          			queryWin.hide();
            	}
        	},{
            	text:'清空',
          		handler:function(){
          			emprty();
          		}
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


</script>
<style type="text/css">

.before{
	border: 0px;
	background-color: #FFFFFF !important;
}

.data_tb {
	background-color: #ffffff;
	text-align:center;
	margin:0 0px;
	width:90%;
	text-align:center;
	border:#8db2e3 1px solid; 
	BORDER-COLLAPSE: collapse; 
}
.data_tb_alignright {	
	BACKGROUND: #e4f4fe; 
	white-space:nowrap;
	padding:5px;
	border-top: #8db2e3 1px solid;
	border-left: #8db2e3 1px solid;
	BORDER-RIGHT: #8db2e3 1px solid;
	BORDER-BOTTOM: #8db2e3 1px solid; 
	height:30px;
	background-color: #d3e1f1;
	margin-top:20px;
	width:20%;
	font-size: 13px;
	TEXT-ALIGN: center; 
	font-family:"宋体";
}
.data_tb_content {
	PADDING-LEFT: 2px; 
	BORDER-TOP: #8db2e3 1px solid; 
	BORDER-LEFT: #8db2e3 1px solid;
	BORDER-RIGHT: #8db2e3 1px solid;
	BORDER-BOTTOM: #8db2e3 1px solid;  
	WORD-BREAK: break-all; 
	TEXT-ALIGN: center; 
	margin-top:20px;
	height:25PX;
	WORD-WRAP: break-word
}
 .FixedHeaderColumnsTableDiv {
            overflow: auto;
            position: relative;
            margin: 2px;
 }
 .FixedCell  {
 			BACKGROUND: #e4f4fe; 
			white-space:nowrap;
			padding:5px;
			border-top: #8db2e3 1px solid;
			border-left: #8db2e3 1px solid;
			BORDER-RIGHT: #8db2e3 1px solid;
			BORDER-BOTTOM: #8db2e3 1px solid; 
			height:30px;
			background-color: #d3e1f1;
			margin-top:20px;
			width:20%;
			font-size: 13px;
			TEXT-ALIGN: center; 
			font-family:"宋体";
            position: relative;
            top: expression(this.offsetParent.scrollTop);
            left: expression(this.parentElement.offsetParent.scrollLeft);
            z-index: 1800;
  }
.FixedHeaderRow1  {
            position: relative;
            top: expression(this.offsetParent.scrollTop);
            background-color: #ccc;
            z-index: 300;
}
.FixedHeaderRow2  {
            position: relative;
            top: expression(this.offsetParent.scrollTop);
            z-index: 290;
            background-color:#ffccff;
 }
.FixedDataColumn {
			PADDING-LEFT: 2px; 
			BORDER-TOP: #8db2e3 1px solid; 
			BORDER-LEFT: #8db2e3 1px solid;
			BORDER-RIGHT: #8db2e3 1px solid;
			BORDER-BOTTOM: #8db2e3 1px solid;  
			WORD-BREAK: break-all; 
			TEXT-ALIGN: center; 
			margin-top:20px;
			height:25PX;
			WORD-WRAP: break-word;
            position: relative;
            left: expression(this.parentElement.offsetParent.parentElement.scrollLeft);
            z-index: 200;
            BACKGROUND: #e4f4fe; 
 }
</style>
</head>
<body style="overflow: hidden;">
<div id="evalScript" style="display:none"></div>
<div id="wageTotal" style="display:none"></div>
<div id="showList">
	<div id="divBtn"></div>
	<div style="height:expression(document.body.clientHeight-27);">
	<mt:DataGridPrintByBean name="personnelList"/>
	</div>
</div>

<div id="updateAudit" style="display:none;" >
	<div id="divBtnAudit"></div>
	<form name="AuditForm" method="post" action="${pageContext.request.contextPath}/salary.do?method=updateAudit">
		<div id="newTableDiv3" style="overflow: auto;width: 100%;height: expression(document.body.clientHeight-30)"></div><p>&nbsp;</p>
	</form>
</div>

<div id="divprint">
<form name="thisForm" method="post" action="rankWages.do">
<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>

<div id="search" style="display:none;">
<br>
<br>
		<table border="0" cellpadding="0" cellspacing="0" bgcolor="" align="center" style="width:100%;line-height: 25px;">
		   <tr style="height:50px;">
				  <td  align="right" width="30%">发放日期：</td>
			      <td  align="left">
			      	<select style="width: 60;" id="year" name="year">
			      		<option value="">请选择</option>
			      		<%
			      			for(int i=2000;i<2500;i++){
			      			
			      		%>
			      			<option value="<%=i %>"><%=i %></option>
		      			<%
			      			}
		      			%>
			      	</select>&nbsp; 年
			      		<select style="width: 60;" id="month" name="month">
			      		<option value="">请选择</option>
			      		<%
			      			for(int i=1;i<=12;i++){
			      			
			      		%>
			      			<option value="<%=i %>"><%=i %></option>
		      			<%
			      			}
		      			%>
			      	</select>&nbsp;月
				  </td>
			</tr>
		</table>
</div>	
</form>
</div>
<div id="addupload" style="display: none">
<div id="adduploadbtn"></div>

<form name="auploadForm" method="post" action="" id="auploadForm" enctype="multipart/form-data" class="autoHeightForm" style="height: 420">

<span class="formTitle" >
EXCEL&nbsp;&nbsp;导&nbsp;&nbsp;入&nbsp;&nbsp;用&nbsp;&nbsp;户&nbsp;&nbsp;工&nbsp;&nbsp;资<br/><br/> 
</span>

<fieldset  style="width:90%">
<legend>EXCEL导入</legend>

<table width="750" height="44" border="0" cellpadding="0" cellspacing="0">
<tr>
<td width="160" height="24">&nbsp;</td>
<td >

<br>
	<ul>
	<li><b>EXCEL文件内容格式说明</b></li>
	<li>&nbsp;</li>
	<li>EXCEL表中应该有一个叫做<font color=blue><b>人员列表</b></font>的表页</li>
	<li>该表页含有至少<font color=blue><b>身份证</b></font><font color=red>(必填)</font>这一列</li>
	<li>该表页还可包含<font color=blue><b>姓名,部门,月份和录入工资项</b></font><font color=red>(可选)</font></li>
	<li>	<br><br><b>Excel必须为office 2003格式方可导入成功。</b>
	</li>
	<br><br><b><a href='#' onclick="javascript:downLoad();"><font color='#FF0000'>导入文件由弹性工资录入导出的execl为标准</font></a></b>
	</ul>			

</td></tr>
<tr><td width="160" height="24">&nbsp;</td><td height=10>&nbsp;</td></tr>
<tr><td width="160" height="24">&nbsp;</td><td>用户数据EXCEL格式文件路径:</td></tr>

  <tr>
<td width="160" height="24">&nbsp;</td>
    <td >
    	<input type="file" name="image" id="image" value=""  size="90" title="请输入，不得为空">
      &nbsp;
	</td>
  </tr>
</table>
</fieldset>
<input name="User" type="hidden" id="User" value='UserManager'>
<input name="status" type="hidden" id="status" value='${status}'>

</form>
</div>

<Script>
//审批方法
function goNext(){
	if(document.getElementById("chooseValue_personnelList").value==""){
		alert("请选择一项！");
	} else{
	
		var pch=document.getElementById("chooseValue_personnelList").value;
		Ext.Ajax.request({
		method:'POST',
					timeout:600000,
					params : { 
						rand : Math.random(),
						pch:pch
					},
					url:"${pageContext.request.contextPath}/salary.do?method=handleNext",
					success:function (response,options) {
						var request = response.responseText;
						alert(request);
						
						//刷新结果
						goSearch_personnelList();
						document.getElementById("chooseValue_personnelList").value="";
					},
					failure:function (response,options) {
						alert("等待超时，请检查网络质量!");
					}
		});
	}
}

//退回方法
function goBack(){
	if(document.getElementById("chooseValue_personnelList").value==""){
		alert("请选择一项！");
	} else{
	
		var pch=document.getElementById("chooseValue_personnelList").value;
		var backstatus=document.getElementById("backstatus").value;
		if(backstatus==""){
			alert('请先选择要退回至那一步！');
			return;
		}
		
		Ext.Ajax.request({
		method:'POST',
					timeout:600000,
					params : { 
						rand : Math.random(),
						pch:pch,
						status:backstatus
					},
					url:"${pageContext.request.contextPath}/salary.do?method=handlePre",
					success:function (response,options) {
						var request = response.responseText;
						alert(request);
						
						//刷新结果
						goSearch_personnelList();
						document.getElementById("chooseValue_personnelList").value="";
					},
					failure:function (response,options) {
						alert("等待超时，请检查网络质量!");
					}
		});
	}
}

//清空方法
function emprty()
{
	document.getElementById("year").value="";
	document.getElementById("month").value="";
	document.getElementById("userName").value="";
	document.getElementById("departName").value="";
}

</script>
</body>
</html>
