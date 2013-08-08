<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"  errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>人员管理</title>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/searchSession.js" charset=gbk></script>
</head>

<script type="text/javascript">

var temp = 0;

function setStep(tab) {	
	var btnNext = Ext.getCmp("move-next");
	var btnBack = Ext.getCmp("move-prev");	
	    
    if (tab == 0) {
        btnBack.disable();
    } else {
        btnBack.enable();
    }

    if (tab == 1) {
        btnNext.disable();
    } else {
        btnNext.enable();
    }
}

function nextStep(){
	var ids = getChooseValue("usersList");
	var year = document.getElementById("year").value;
	var month = document.getElementById("month").value;
	 if(month == "" ){
	    alert("请选择月份");
	    temp = 0;
	    document.getElementById("month").focus();
	    return false;
	}
	 if(ids == ""){
		alert("请选择人员");
		temp = 0;
		return false;
	}else{
		if(confirm("您确定要初始化人员工资吗？","yes")){
			//var url="${pageContext.request.contextPath}/salary.do?method=getPch";
			//var request= ajaxLoadPageSynch(url,"");
			//if(request ==""){
			     temp = 1;
				 Ext.Ajax.request({
						method:'POST',
						timeout:600000,
						params : { 
							rand : Math.random(),
							userIds:ids,
							paraYear:year,
							paraMonth:month
						},
						url:"${pageContext.request.contextPath}/salary.do?method=init",
						success:function (response,options) {
							var request = response.responseText;
							if(request !=""){
								document.getElementById("newTableDiv").innerHTML=request;
							 	return true;
							}else{
							    temp = 0;
								return false;
							}
						},
						failure:function (response,options) {
							alert("后台出现异常,获取文件信息失败!");
							temp = 0;
							return false;
						}
					});
				 //}else{
			//	alert("本月工资已初始化成功,无需再次初始化!");
			//	return false;
			//}
		}else{
		    temp = 0;
			return false; 
		}
	}
}

function ext_init(){

    var year = new Date().getFullYear() ;
    var html = "<select onchange='changeDate()' id='year'>" ;
    var htmlMonth = "<select style='width: 60;' onchange='changeDate()' id='month'><option id='defaultMonth'></option><option value=''>请选择</option>" ;
		for(var i=year-5;i<year+5;i++) {
			var select = "" ;
			if(year == i) {
				select = "selected" ;
			}
			html += " <option value="+i+" "+select+">"+i+"</optoin>" ;
		}
	    html += "</select>&nbsp;年" ;
	    
	    for(var i=1;i<=12;i++) {
			htmlMonth += " <option value=" +i+ ">" +i+ "</optoin>" ;
		}
	    htmlMonth += "</select>&nbsp;月份" ;

	function navHandler(dir) {
		var i = 0;
		var cur = 0;
		var curTab = tab.getActiveTab();
		
	
		tab.items.each(function(item) {   
			if(item == curTab) {
				cur = i;
			} 
			
			i++;
		});  
		
		cur += dir;

	    setStep(cur);	
	    tab.setActiveTab(cur);
	}
	tab = new Ext.TabPanel({
        id: "tab",
        renderTo: "divTab",
        activeTab: 0, //选中第一个 tab
        autoScroll:true,
        frame: true,
        height: document.body.clientHeight-Ext.get('divTab').getTop(), 
        defaults: {autoHeight: true,autoWidth:true},
        items:[{
        			contentEl: "tab1", 
        			title: "选择人员", 
        			listeners: {
        				activate: function(){
        					setStep(0);
        				}
        			}
        		},{
        			contentEl: "tab2", 
        			title: "检阅初始化信息",
        			//disabled: true,
        			listeners: {
						activate: function(){
							setStep(1);
        				}
        			}
        		}
        ],
        bbar:[ '->',{
				id:'move-prev',
				text:'关闭',
				disabled: true,
				handler: function(){
					navHandler(-1);
					//closeTab(parent.tab);
				}  
			},'-',{
				id:'move-next',
				text:'下一步',
				handler: function(){
					nextStep();
					if(temp == 1) {
					    navHandler(1);
					}
				}
			}
          ]
	});
	var tbar_user = new Ext.Toolbar({
		renderTo: 'gridDiv_user',
		items:[{
			text:'弹性工资录入',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/mytask.gif',
			handler:function(){
				departInitSalary();
			}
		},'-',{
			text:'修改',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/edit.gif',
			handler:queryWinFun
		},'-',{
			text:'查询',
			id:'btn-query',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
			handler:queryWinFun
		},'-',html,htmlMonth
		]
	});
	 
	//人员查询
	var queryWin = null;
	function queryWinFun(id){
	
		if(!queryWin) { 
			new BlockDiv().show();
			var searchDiv = document.getElementById("search") ;
		    queryWin = new Ext.Window({
				title: '人员查询',
				contentEl:'search',
		     	width: 300,
		     	height:195,
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
	          			goSearch_softSalaryList();
	          			queryWin.hide();
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
	margin-top:20px;
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
	WORD-WRAP: break-word
}

</style>
<body>
<div id="divTab"></div>
<div id="tab1" >
<div id="gridDiv_user"></div>
		<div style="height:expression(document.body.clientHeight-85);width:100%">
			<mt:DataGridPrintByBean name="softSalaryList"  message="请选择单位编号" />
		</div>
</div>
<div id="tab2">
			<table class="data_tb" >
				<tr>
				<td class="data_tb_alignright">姓名</td>
				<td class="data_tb_alignright">薪酬级别</td>
				<td class="data_tb_alignright">性别</td>
				<td class="data_tb_alignright">本月工时</td>
				<td class="data_tb_alignright">本月外勤天数</td>
				<td class="data_tb_alignright">绩效工资</td>
				<td class="data_tb_alignright">其他补助</td>
				</tr>
				<div></div>
			</table>

		<div id="newTableDiv"> </div>
			<form action="${pageContext.request.contextPath}/salary.do?method=departSalaryEdit" method="post" name="thisForm" id="thisForm" >
				
				<input type="hidden" Id="todayYear" name="todayYear" >
				<input type="hidden" Id="todayMonth" name="todayMonth">
				<input type="hidden" Id="userIds" name="userIds">
				
			</form>
			 
</div>
<div id="search" style="display:none">
<br/>
<div style="margin:0 20 0 20">请在下面输入查询条件：</div>
<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
	<table border="0" cellpadding="0" cellspacing="0" width="100%" bgcolor="">
		<tr align="center">
			
			<td align="right" >姓名：</td>
			<td align=left>
				<input type="text" name="name" id="name" >
			</td>
		</tr>
		<tr align="center">
			
			<td align="right">薪酬级别：</td>
			<td    align="left">
					<input type="text" name="rankId" id="rankId" 
					onkeydown="onKeyDownEvent();"
					onkeyup="onKeyUpEvent();"
					onclick="onPopDivClick(this);"
					noinput=true
					autoid=863
					>
						 
				  </td>
		</tr>
	</table>
</div>
</body>
<script language="javascript"  type="text/javascript">

	function departInitSalary() {
		var year = document.getElementById("year").value;
		var month = document.getElementById("month").value;
		var userIds = getChooseValue("softSalaryList");
		if(userIds ==""){
			alert("请选择人员");
			return ;		
		}
		
		if(year == "" ||  month ==""){
			return ;
		}
		document.getElementById("todayMonth").value=month ;
		document.getElementById("todayYear").value=year;
		document.getElementById("userIds").value=userIds;
		
		 Ext.Ajax.request({
					method:'POST',
					timeout:600000,
					params : { 
						rand : Math.random(),
						todayMonth:month,
						todayYear:year,
						userIds:userIds 
					},
					url:"${pageContext.request.contextPath}/salary.do?method=departSalaryEdit",
					success:function (response,options) {
						var request = response.responseText;
						alert(request);
						if(request !=""){
							
							document.getElementById(sumSalary).innerText=request;
							setStep(1);
						}
						 
					},
					failure:function (response,options) {
						alert("等待超时，请检查网络质量!");
					}
				});

	}

	ext_init();

	var lastMonth = new Date().getMonth();
    if(lastMonth == 0) {
        lastMonth = 12;
    }
	document.getElementById("defaultMonth").value=lastMonth;
    document.getElementById("defaultMonth").innerHTML=lastMonth;
    
	function ajaxEditSaray(pch,userId,n1,v1,sumSalary){
		
		var vValue = document.getElementById(v1).value;
		if(pch !="" && userId !="" && v1 !="" && vValue !=""){
			 Ext.Ajax.request({
					method:'POST',
					timeout:600000,
					params : { 
						rand : Math.random(),
						pch:pch,
						userId:userId,
						nName:n1,
						vValue:vValue
					},
					url:"${pageContext.request.contextPath}/salary.do?method=ajaxEditSaray",
					success:function (response,options) {
						var request = response.responseText;
						if(request !=""){
							
							document.getElementById(sumSalary).innerText=request;
							
						}
						 
					},
					failure:function (response,options) {
						alert("等待超时，请检查网络质量!");
					}
				});
			
		}else{
			return ;
		}
		
		
	}
	
	function finish(){
		var ids = getChooseValue("usersList");
		if(ids == ""){
			alert("请选择人员");
			return ;
		}else{
			
		}
	}
	
	function changeDate() {
		var year = document.getElementById("year").value;
		var month = document.getElementById("month").value;
		if(year == "" ||  month ==""){
			return ;
		}
		document.getElementById("todayMonth").value= year;
		document.getElementById("todayYear").value=month;
		
		goSearch_softSalaryList();
	
			//window.location="${pageContext.request.contextPath}/salary.do?method=softSalaryList&te=2&paraYear="
			//+ document.getElementById("year").value+"&paraMonth="+document.getElementById("month").value;
    }
</script>
</html>


