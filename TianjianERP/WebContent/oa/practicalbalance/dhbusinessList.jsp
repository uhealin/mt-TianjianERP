<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script type="text/javascript">

function extInit(){
	
	new Ext.Toolbar({
   		renderTo: "divBtn",
   		height:30,
   		defaults: {autoHeight: true,autoWidth:true},
           items:[{ 
                text:'查询',
                icon:'${pageContext.request.contextPath}/img/query.gif',
                handler:function(){
                	queryWinFun();
                }
   			},'-',{ 
   	            text:'关闭',
   	            icon:'${pageContext.request.contextPath}/img/close.gif',
   	            handler:function(){
   	            	//closeTab(parent.tab);
   	            	parent.projectWin.hide();
   				}
      		},'->']
	});
	
}

var queryWin = null;
function queryWinFun() {
	document.getElementById("search").style.display = "";
	if(queryWin == null) { 
	    queryWin = new Ext.Window({
			title: '项目查询',
			width: 600,
			height:300,
			contentEl:'search', 
	        closeAction:'hide',
	        listeners:{
				'hide':{fn: function () {
					 document.getElementById("search").style.display = "none";
				}}
			},
	        layout:'fit',
	        contentEl:'search',
		    buttons:[{
	            text:'搜索',
	          	handler:function() {
	          		goSearch_showModelGetFundsProject();
	          		queryWin.hide();
	          		
	          		document.body.onselectstart = function () {} 
	        		document.body.unselectable="off" ;
	          	}
	        },{
	            text:'取消',
	            handler:function(){
	            	queryWin.hide();
	            }
	        }]
	    });
	}
	 queryWin.show();
}
window.attachEvent('onload',extInit);
</script>
</head>
<body scroll="no">
<div id="divBtn" ></div>
<form id="thisForm" action="" method="post" target="projectFrame">
	<div style="height: expression(document.body.clientHeight-27);">
		<mt:DataGridPrintByBean name="showModelGetFundsProject" />
	</div>
</form>
</body>


<div id="search" style="display: none;">
<br/>
<fieldset>
    <legend style="font-size:12px;">项目查询</legend>
	<table border="0" cellpadding="0" cellspacing="0" width="100%" bgcolor="">
		<tr align="center">
			<td align="right" >项目名称：</td>
			<td align="left" colspan="3">
				<input type="text"
				   name="projectid"
				   id="projectid"
				   maxlength="10"
				   title="请输入有效的值" 
				   size="20" /> 
			</td>
		</tr>
		<tr>
			<td align="right" >付款单位：</td>
			<td align="left" colspan="3">
				<input name="customerId" id="customerId" type="text" onkeydown="onKeyDownEvent();"
					   onkeyup="onKeyUpEvent();"
					   onclick="onPopDivClick(this);"
					   size="20"
					   autoid=2  />
			</td>
		</tr>
		<tr align="center">
			<td align="right" >委托号：</td>
			<td align="left" colspan="3">
				<input name="entrustNumber" id="entrustNumber" type="text" size="20"  />
			</td>
		</tr>
		<tr align="center">
			<td align="right" >报告号：</td>
			<td align="left" colspan="3">
				<input name="reportNumber" id="reportNumber" type="text" size="20"  />
			</td>
		</tr>
		<tr>
			<td align="right" >承接部门：</td>
			<td align="left" colspan="3">
				<input name="departname" id="departname" type="text" 
						   onKeyDown="onKeyDownEvent();" 
					       onKeyUp="onKeyUpEvent();" 
					       onClick="onPopDivClick(this);" 
					       valuemustexist=true 
					       autoid=123 size="20"  />
			</td>
		</tr>
		 
	</table>
</fieldset>
</div>


<script type="text/javascript">

function grid_dblclick(obj){ 
	var projectid=obj.projectid;
	var projectname = obj.projectname;

	if(projectid != null && projectid != ""){
		parent.window.document.getElementById("pname").value = projectname;
		parent.window.document.getElementById("pname").title = projectid;
		parent.window.document.getElementById("projectid").value = projectid;
		parent.window.document.getElementById("projectidspan").innerHTML = projectid;

		// 设置项目信息
		var oBao = new ActiveXObject("Microsoft.XMLHTTP");
		var url = "${pageContext.request.contextPath}/getFunds.do?method=getProjectInfoJson&projectid="+projectid;
		oBao.open("post",url,false);
		oBao.send();
		var strResult = oBao.responseText;

		var obj = eval('('+strResult+')'); 
		if(parent.window.document.getElementById("customerId").value ==""){
			parent.window.document.getElementById("customerId").value=obj[0].customername;
		}else{
			if(parent.window.document.getElementById("advice-customerId")){
				parent.window.document.getElementById("advice-customerId").value=obj[0].customername;
			}
		}
		parent.window.document.getElementById("entrustNumber").value=obj[0].entrustNumber;
		parent.window.document.getElementById("reportNumber").value=obj[0].reportNumber;
		if(parent.window.document.getElementById("departname").value ==""){
			parent.window.document.getElementById("departname").value=obj[0].departname;	
		}else{
			if(parent.window.document.getElementById("advice-customerId")){
				parent.window.document.getElementById("advice-departname").value=obj[0].departname;
			}
		}
		//parent.window.document.getElementById("departname").value=obj[0].departmentId;
		parent.window.document.getElementById("businessCost").value=obj[0].businessCost;
		parent.window.document.getElementById("nmoney").value=obj[0].getFundsMoney*1;
		parent.window.document.getElementById("ymoney").value=accSub(obj[0].getFundsMoney*1,obj[0].businessCost*1);
		// parent.window.document.getElementById("receiceMoney").value=0;
		
		if(obj[0].reportfilename=="无"){
			parent.window.document.getElementById("reportfilename").innerHTML="<a href='#'>无</a>";
		}else{
			parent.window.document.getElementById("reportfilename").innerHTML="<a href='#' onclick=f_downReportFile('"+obj[0].reportfiletempname+"','"+obj[0].reportfilename+"')>"+obj[0].reportfilename+"</a>";
		}
		
		parent.window.document.getElementById("property").value=obj[0].property;
		parent.window.document.getElementById("isStock").value=obj[0].isStock;
		
		// 默认开票时间为当前时间
		var now = new Date();
		var year = now.getYear();
		var month = now.getMonth()+1;
		if(month*1<10){
			month = "0"+month;
		}
		var date = now.getDate();
		if(date*1<10){
			date = "0"+date;
		}
		//parent.window.document.getElementById("receicedate").value=year+"-"+month+"-"+date; 
		//parent.window.document.getElementById("accounttype").value=""; 
		//parent.window.document.getElementById("ctype").value=""; 
		//parent.window.document.getElementById("ctypenumber").value=""; 
		parent.window.document.getElementById("remark").value=""; 
	}
	parent.window.projectWin.hide();	
}




//减法函数
function accSub(arg1,arg2){
     var r1,r2,m,n;
     try{r1=arg1.toString().split(".")[1].length}catch(e){r1=0}
     try{r2=arg2.toString().split(".")[1].length}catch(e){r2=0}
     m=Math.pow(10,Math.max(r1,r2));
     //last modify by deeka
     //动态控制精度长度
     n=(r1>=r2)?r1:r2;
     return ((arg2*m-arg1*m)/m).toFixed(n);
}


function grid_dblclick2(obj){
	var value = obj.projectid+"$"+obj.projectname;
	window.returnValue=value;
	window.close();
}

Ext.onReady(function () {
	document.body.onselectstart = function () {} 
	document.body.unselectable="off" ;
})
</script>
</html>