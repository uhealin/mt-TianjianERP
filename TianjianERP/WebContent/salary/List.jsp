<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>工资最终发放</title>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/searchSession.js" charset=gbk></script>

<script type="text/javascript">

var tabs;

Ext.onReady(function(){
	
	tabs = new Ext.TabPanel({
	    renderTo: 'my-tabs',
	    activeTab: 0,
	    layoutOnTabChange:true, 
	    forceLayout : true,
	    deferredRender:false,
	    height: document.body.clientHeight-Ext.get('my-tabs').getTop(),
	    width : document.body.clientWidth, 
	    defaults: {autoWidth:true,autoHeight:true},
	    items:[
	        {contentEl:'tab1', title:'待发放', id:'cur1'},
	        {contentEl:'tab2', title:'已发放', id:'cur2'} 
	    ]
	});
	
	tabs.on("tabchange",function(tabpanel,tab) {
    	if(tab.id == "cur2") {
				goSearch_giveYetList();			
		} 
    }) ;

	goSearch_giveList();
	
    
    
    var html = "<table><tr><td>退回至：</td><td><input name='backstatus' id='backstatus' type=text autoid=4621 value='' refer='4' size='10' onkeydown='onKeyDownEvent();' onkeyup='onKeyUpEvent();' onclick='onPopDivClick(this);' noinput=true refer='status' ></td></tr></table>";
    
	    
	//工资发放列表工具条
	var tbar_customer = new Ext.Toolbar({
		renderTo:'divBtn',
           items:[{
			text:'发放工资',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/confirm.gif',
			handler:function(){
				updateGive();
			}
		},'-',{
			text:'查询',
			id:'btn-query',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
			handler:queryWinFun
		},'-',{

				text:'查看详情',
				id:'btn-showall',
				cls:'x-btn-text-icon',
		   		icon:'${pageContext.request.contextPath}/img/query.gif',
				handler:function (){
					showquery1();
				}

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
        var tbar_customer1 = new Ext.Toolbar({

		renderTo:'divBtn2',

           items:[

           {

				text:'查询',
				id:'btn-query',
				cls:'x-btn-text-icon',
		   		icon:'${pageContext.request.contextPath}/img/query.gif',
				handler:function (){
					queryWinFun('giveYetList');
				}

		},'-',{

				text:'查看详情',
				id:'btn-showall',
				cls:'x-btn-text-icon',
		   		icon:'${pageContext.request.contextPath}/img/query.gif',
				handler:function (){
					showquery2();
				}

		},'->']

        });    
        initCombox(document.getElementById("backstatus"));
});





var queryWin = null;
function queryWinFun(obj){
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
          			var departmentName = document.getElementById("departmentName").value;
					if(departmentName !=""){
						var departId = departmentName.substring(departmentName.indexOf("-")+1,departmentName.length);	
						document.getElementById("departmentId").value  = departId;
					}		
          		   if(obj == "giveYetList"){

          				goSearch_giveYetList();

          			}else{

          				goSearch_giveList();

          			}
          			
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



//增加前检查
function goAdd(){
	var yearNows = document.getElementById("yearNow").value;
    var monthNows = document.getElementById("monthNow").value;
	if(monthNows=="")
	{
		alert("请选择月份！");
		document.getElementById("monthNow").focus();
	}
	else
	{
	    Ext.Ajax.request({
					method:'POST',
					params : { 
						rand : Math.random(),
						yearNow:yearNows,
						monthNow:monthNows 
					},
					url:"${pageContext.request.contextPath}/salary.do?method=checkNY",
					success:function (response,options) {
						var request = response.responseText;
						if(request =="0"){
							//查询不存在所选年月
							addSalary(yearNows,monthNows);
						}else{
							alert("对不起，您所选的月份已发放工资");
							document.getElementById("monthNow").focus();
						}
					},
					failure:function (response,options) {
						alert("后台出现异常,修改信息失败!");
					}
				});
	}
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
<div id="my-tabs">
	<div id="tab1">
		<div id="showList">
			<div id="divBtn"></div>
			<div style="height:expression(document.body.clientHeight-57);">
			<mt:DataGridPrintByBean name="giveList"/>
			</div>
		</div>
	</div>	
	<div id="tab2">

	    <div id="divBtn2"></div>
		<div style="height:expression(document.body.clientHeight-57);">
			<mt:DataGridPrintByBean name="giveYetList"/>
		</div>
	</div>	
</div>
	<div id="updateGive" style="display:none;" >

	<div id="divBtnGive"></div>

<form name="GiveForm" method="post" action="${pageContext.request.contextPath}/salary.do?method=updateGive">

		<div id="newTableDiv3" style="overflow: auto;width: 100%;height: expression(document.body.clientHeight-27)"></div><p>&nbsp;</p>


<input name="status" type="hidden" id="status" value='${status}'>


</form>

</div>


<div id="divprint">
<form name="thisForm" method="post" action="rankWages.do">
<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>

<div id="search" style="display:none;">
<br>
<br>
		<table border="0" cellpadding="0" cellspacing="0" bgcolor="" align="center" style="width:100%;line-height: 25px;">
		  <tr style="height:20px;">
		    <td  align="right" width="30%">选择部门：</td>
		    <td  align="left">   <input name="departmentId" id="departmentId" type="hidden" > 
                    <input type="text" name="departmentName" id="departmentName"   onfocus="onPopDivClick(this);"
				              onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" 
				autoid=958 noinput=true multilevel=true norestorehint=true  valuemustexist=true/></td>
		  </tr>
		  
		   <tr style="height:50px;">
				  <td  align="right" width="30%">发放日期：</td>
			      <td  align="left">
			      	<select style="width: 60;" id="year" name="year">
			      		<option value="">请选择</option>
			      		<%
			      			for(int i=2012;i<2500;i++){
			      			
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

<Script>
//工资发放方法
function updateGive(){
	if(document.getElementById("chooseValue_giveList").value==""){
		alert("请选择一项！");
	} else{
	var pch=document.getElementById("chooseValue_giveList").value;
	Ext.Ajax.request({
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
						goSearch_giveList();
						document.getElementById("chooseValue_giveList").value="";
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
 
function emprty()
{
	document.getElementById("year").value="";
	document.getElementById("month").value="";
	document.getElementById("departmentName").value="";
	document.getElementById("advice-departmentName").value="";
	document.getElementById("departmentId").value="";
}

//未发放
function showquery1(){

	var pch=document.getElementById("chooseValue_giveList").value;
	if(pch==""){
		alert("请选择一项！");
		return;
	}	
	var url  ="${pageContext.request.contextPath}/salary.do?method=export&pch="+pch+"&mode=view";
    window.open(url); 
}

//已发放
function showquery2(){

	var pch=document.getElementById("chooseValue_giveYetList").value;
	if(pch==""){
		alert("请选择一项！");
		return;
	}	
	var url  ="${pageContext.request.contextPath}/salary.do?method=export&pch="+pch+"&mode=view";
    window.open(url); 
}


//退回方法
function goBack(){
	var pch=document.getElementById("chooseValue_giveList").value;
	if(pch==""){
		alert("请选择一项！");
	} else{
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
						goSearch_giveList();
						document.getElementById("chooseValue_giveList").value="";
					},
					failure:function (response,options) {
						alert("等待超时，请检查网络质量!");
					}
		});
	}
}



</script>
</body>
</html>
