<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>

<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>



<html>

<head>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title>薪酬模块人事审核</title>

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
		        {contentEl:'tab1', title:'待处理', id:'cur1'},
		        {contentEl:'tab2', title:'已处理', id:'cur2'} 
		    ]
		});
		
		tabs.on("tabchange",function(tabpanel,tab) {
	    	if(tab.id == "cur2") {
					goSearch_businessYetList();			
			} 
	    }) ;
	    goSearch_businessList();
    var year = new Date().getFullYear() ;
    var month = new Date().getMonth() ;
    if(month == 0) {
        month = 12;
    }
    var html = "<select onpropertychange='' id='yearNow'>" ;
    var htmlMonth = "<table><tr><td><select style='width: 60;' onpropertychange='' id='monthNow'><option value=''>请选择</option>" ;
		for(var i=year-5;i<year+5;i++) {
			var select = "" ;
			if(year == i) {
				select = "selected" ;
			}
			html += " <option value="+i+" "+select+">"+i+"</optoin>" ;
		}
	    html += "</select>&nbsp;年" ;
	    
	    for(var i=1;i<=12;i++) {
	        var select = "";
	        if(month == i) {
				select = "selected" ;
			}
			htmlMonth += " <option value=" +i+" "+select+">"+i+"</optoin>" ;
		}
	    htmlMonth += "</select>&nbsp;月份" ;
	    htmlMonth += "</td><td><input name='departmentId' id='departmentId' type=text autoid='30021' value='${departmentId}'  multilevel=true multiselect=true size='30' refer='${userSession.areaid}' ></td><td>部门</td></tr></table>" ;
	    
	    
	//工资发放列表工具条

	var tbar_customer = new Ext.Toolbar({

		renderTo:'divBtn',

           items:[{

			text:'修改',

			cls:'x-btn-text-icon',

	   		icon:'${pageContext.request.contextPath}/img/edit.gif',

			handler:function(){
				// 老版本的在线修改
				// goUpdate();
				
				//新版本用EXCEL方式在线修改
				var pch = document.getElementById("chooseValue_businessList").value;
				if(pch==""){
					alert("请选择一项！");
					return;
				}
				
				var url  ="${pageContext.request.contextPath}/salary.do?method=export&pch="+pch;
			    window.open(url); 
			}
		},'-',{
			text:'提交',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/confirm.gif',
			handler:function(){
				goNext();
			}
		},'-',{
			text:'删除',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/delete.gif',
			handler:function(){
				goDelete();
			}
		},'-',{
			text:'查询',
			id:'btn-query',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
			handler:queryWinFun
		},'-',html,htmlMonth,{
            text:'增加',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/add.gif',
            handler:function(){
            	goAdd();
			}
      	},'-',{
            text:'打印',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/print.gif',
            handler:function(){
            	print_businessList();
            	//goAdd();
			}
      	},'-',{
            text:'批量导入【临时费用或福利】',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/import.gif',
            handler:function(){
            	window.location="${pageContext.request.contextPath}/salary/SalaryUpload.jsp";
			}
      	},
      	'->'

        ]

        });  
        
    	var tbar_customer1 = new Ext.Toolbar({
		renderTo:'divBtn1',
           items:[
           {
				text:'查询',
				id:'btn-query',
				cls:'x-btn-text-icon',
		   		icon:'${pageContext.request.contextPath}/img/query.gif',
				handler:function (){
					queryWinFun('businessYetList');
				}
		},'-',{
				text:'查看详情',
				id:'btn-showall',
				cls:'x-btn-text-icon',
		   		icon:'${pageContext.request.contextPath}/img/query.gif',
				handler:function (){
					 showquery();
				}
		},'->'  ]
        });    
        //添加工资发放的工具条
	var tbar_customer_add = new Ext.Toolbar({
		renderTo:'divBtnAdd',
           items:[{
            text:'提交',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/save.gif',
            handler:function(){
            	submitAdd();
			}
      	},'-',{
            text:'暂存',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/save.gif',
            handler:function(){
           		submitAdd(1);
			}
      	},'-',{
			text:'导出',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/excel1.gif',
			handler:function(){
	
				var pch = document.getElementById("pch_add").value;
			    var month = document.getElementById("monthNow").value;

				
			    var url  ="${pageContext.request.contextPath}/salary.do?method=export&pch="+pch+"&month="+month;
			    
			    var result =  ajaxLoadPageSynch(url,null);
			    eval(result);
			  
			}
		},'-',{
            text:'批量导入',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/import.gif',
            handler:function(){
            	addupload();
			}
      	},'-',{
			text:'返回',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/back.gif',
			handler:function(){
				//document.getElementById("add").style.display="none";
				//document.getElementById("showList").style.display="";
				//document.getElementById("update").style.display="none";
				deleteByNY();
			}
		},'->'
        ]
        });  
        
    //修改工资的工具条
	var tbar_customer_upload = new Ext.Toolbar({
		renderTo:'divBtnUpdate',
           items:[{
            text:'提交',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/save.gif',
            handler:function(){
            	submitUpdate();
			}
      	},'-',{
            text:'暂存',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/save.gif',
            handler:function(){
            	submitUpdate(1);
			}
      	},'-',{
			text:'导出',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/excel1.gif',
			handler:function(){
				
				var pch = document.getElementById("PCH_").value;
			    var month = document.getElementById("monthNow").value;

			    var url  ="${pageContext.request.contextPath}/salary.do?method=export&pch="+pch+"&month="+month;
			    
			    alert(url);
			    window.open(url);
			    
			    
			    //var result =  ajaxLoadPageSynch(url,null);
			    //eval(result);
			  
			}
		},'-',{
            text:'批量导入',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/import.gif',
            handler:function(){
            	updateupload();
			}
      	},'-',{
			text:'返回',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/back.gif',
			handler:function(){
				document.getElementById("add").style.display="none";
				document.getElementById("my-tabs").style.display="";
				document.getElementById("update").style.display="none";
			}
		},'->'
        ]
        });  
	var tbar_add_upload = new Ext.Toolbar({
		renderTo: "adduploadbtn",
		defaults: {autoHeight: true,autoWidth:true},
 		items:[{
			text:'确定',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/save.gif',
            handler:function () {
				checkit1();
			}
      	},'-',{
            text:'返回',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/back.gif',
            handler:function(){
				document.getElementById("addupload").style.display="none";
				document.getElementById("add").style.display="";
				document.getElementById("my-tabs").style.display="none";
			}
      	},'-',new Ext.Toolbar.Fill()]
	});
	
	var tbar_update_upload = new Ext.Toolbar({
		renderTo: "updateuploadbtn",
		defaults: {autoHeight: true,autoWidth:true},
 		items:[{
			text:'确定',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/save.gif',
            handler:function () {
				checkit2();
			}
      	},'-',{
            text:'返回',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/back.gif',
            handler:function(){
            
				document.getElementById("updateupload").style.display="none";
				document.getElementById("update").style.display="";
				document.getElementById("my-tabs").style.display="none";
			}
      	},'-',new Ext.Toolbar.Fill()]
	});

	initCombox(document.getElementById("departmentId"));
});





function queryWinFun(obj){
	var searchDiv = document.getElementById("search") ;
	searchDiv.style.display = "" ;
	    var queryWin = new Ext.Window({
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
          			if(obj == "businessYetList"){
          				goSearch_businessYetList();
          			}else{
          				goSearch_businessList();
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
    new BlockDiv().show();
    queryWin.show();
}

//增加前检查
function goAdd(){
	var yearNows = document.getElementById("yearNow").value;
    var monthNows = document.getElementById("monthNow").value;
    var departmentIds = document.getElementById("departmentId").value;
    if(yearNows==""){
		alert("请选择年份！");
		document.getElementById("yearNow").focus();
		return;
	}
	if(monthNows==""){
		alert("请选择月份！");
		document.getElementById("monthNow").focus();
		return;
	}
	if (departmentIds==""){
		alert("请选择部门！");
		document.getElementById("departmentId").focus();
		return;
	}
	
    Ext.Ajax.request({
				method:'POST',
				params:{ 
					rand : Math.random(),
					yearNow:yearNows,
					monthNow:monthNows,
					departmentId:departmentIds
				},
				url:"${pageContext.request.contextPath}/salary.do?method=checkNY",
				success:function (response,options) {
					var request = response.responseText;
					if(request =="no" && confirm("有部分部门存在当月工资，是否重复发放?")==false){
						return;
					}
					//查询不存在所选年月
					addSalary(yearNows,monthNows,departmentIds);
				},
				failure:function (response,options) {
					alert("等待超时，请检查网络质量!");
				}
			});
}
//修改前检查
function goUpdate(){
if(document.getElementById("chooseValue_businessList").value==""){
		alert("请选择一项！");
} else{
		var pch = document.getElementById("chooseValue_businessList").value;
		
	    Ext.Ajax.request({
					method:'POST',
					params : { 
						rand : Math.random(),
						pch:pch
					},
					url:"${pageContext.request.contextPath}/salary.do?method=checkStatus",
					success:function (response,options) {
						var request = response.responseText;
						if(request =="yes"){
							//查询不存在所选年月
							updateSalary();
						}else{
							alert("该月工资已经提交人事审核，不允许修改!");
						}
					},
					failure:function (response,options) {
						alert("等待超时，请检查网络质量!");
					}
				});
	}
}
//去除数组重复元素
function delRepeat(arr){
	for(var i=0,len=arr.length,result=[],item;i<len;i++){
	        item = arr[i].value;
	        if(result.indexOf(item) < 0) {
	            result[result.length] = item;
	        }
	    }
	    return result;
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
.rankHead{
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
	WORD-WRAP: break-word;
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

<body style="overflow: hidden;" >
<div id="evalScript" style="display:none"></div>
<div id="totalwages" style="display:none"></div>
<div id="my-tabs">
 <div id="tab1">
	<div id="showList">

	<div id="divBtn" ></div>

		<div style="height:expression(document.body.clientHeight-57);">
		<mt:DataGridPrintByBean name="businessList"/>
		</div>
     </div>
</div>
<div id="tab2">
		<div id="divBtn1"></div>
		<div style="height:expression(document.body.clientHeight-57);">
			<mt:DataGridPrintByBean name="businessYetList"/>
		</div>
</div>
</div>
<div id="add" style="display:none;" >
	<div id="divBtnAdd"></div>
	<form name="addForm" method="post" action="${pageContext.request.contextPath}/salary.do?method=add">
		<input type="hidden" id="YY" name="YY" value="" ><input type="hidden" id="MM" name="MM" value="">
		<div id="newTableDiv" style="overflow: auto;width: 100%;height: expression(document.body.clientHeight-35)"></div><p>&nbsp;</p>
		
	</form>
</div>

<div id="update" style="display:none;" >
	<div id="divBtnUpdate"></div>
	<form name="updateForm" method="post" action="${pageContext.request.contextPath}/salary.do?method=update">
		<input type="hidden" value="" id= "PCH_">
		<div id="newTableDiv2" style="overflow: auto;width: 100%;height: expression(document.body.clientHeight-35)"></div><p>&nbsp;</p>
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
		<br><br><b>Excel必须为office 2003格式方可导入成功。</b>
	
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

</form>
</div>

<div id="updateupload" style="display: none">
<div id="updateuploadbtn"></div>

<form name="uuploadForm" method="post" action="" id="uuploadForm" enctype="multipart/form-data" class="autoHeightForm" style="height: 420">

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
	 <li><br><br><b>Excel必须为office 2003格式方可导入成功。</b></li>
	
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

</form>
</div>
<div id="divprint">

<form name="thisForm" method="post" action="rankWages.do">

<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>


<div id="search" style="display:none;">

<br>

<br>

		<table border="0" cellpadding="0" cellspacing="0" bgcolor="" align="center" style="width:100%;line-height: 25px;" >

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



<Script >
 /*批量导入*/
function addupload(){
		document.getElementById("add").style.display="none";
		document.getElementById("my-tabs").style.display="none";
		document.getElementById("addupload").style.display ="";
	
   // var month = document.getElementById("monthNow").value;
   //	window.location="/AuditSystem/salary.do?method=Upload&month="+month;
}
function updateupload(){
		document.getElementById("update").style.display="none";
		document.getElementById("my-tabs").style.display="none";
		document.getElementById("updateupload").style.display ="";
}		
//增加方法
function addSalary(yearNows,monthNows,departmentIds){
     Ext.MessageBox.show({   
			msg : '正在初始化中，请稍等...',   
			// progressText : '保存中...',   
			width : 300,   
			wait : true,   
			progress : true,   
			closable : true,   
			waitConfig : {   
			interval : 300  
			},   
			icon : Ext.Msg.INFO   
	});
	Ext.Ajax.request({
			method:'POST',
			timeout:600000,
			params : { 
				rand : Math.random(),
				paraYear:yearNows,
				paraMonth:monthNows,
				departmentIds:departmentIds
			},
			url:"${pageContext.request.contextPath}/salary.do?method=init",
			success:function (response,options) {
				Ext.MessageBox.hide();
				var result = response.responseText;
				alert(result);
				
				//刷新结果
				goSearch_businessList();
			},
			failure:function (response,options) {
				Ext.MessageBox.hide();
				alert("等待超时，请检查网络质量!");
			}
		
	});
}
//修改方法
function updateSalary(){
if(document.getElementById("chooseValue_businessList").value==""){
		alert("请选择一项！");
	} else{
	var pch=document.getElementById("chooseValue_businessList").value;
	var editRight = "业务部";
	Ext.MessageBox.show({   
			msg : '正在初始化中，请稍等...',   
			// progressText : '保存中...',   
			width : 300,   
			wait : true,   
			progress : true,   
			closable : true,   
			waitConfig : {   
			interval : 200  
			},   
			icon : Ext.Msg.INFO   
			});
	
	Ext.Ajax.request({
						method:'POST',
						timeout:600000,
						params : { 
							rand : Math.random(),
							p_ch:pch,
							e_ditRight:editRight
						},
						url:"${pageContext.request.contextPath}/salary.do?method=updateInit",
						success:function (response,options) {
							Ext.MessageBox.hide();
							var request = response.responseText;
							if(request !=""){
								var tble="" ;      //保存表格
								var str="";         //保存javascript
								var num = request.indexOf("var strFormulas");
								if(num>0){
									tble= request.substring(0,num);
									str = request.substring(num);
								}else{
									tble=request;
								}
								document.getElementById("add").style.display="none";
								document.getElementById("my-tabs").style.display="none";
								document.getElementById("update").style.display="";
								document.getElementById("newTableDiv2").innerHTML=tble + "<br>";
								document.getElementById("PCH_").value=pch;
								document.getElementById("evalScript").innerHTML=str;
								var totalStr = str.substring(str.indexOf("var myvar"));
								document.getElementById("totalwages").innerHTML = totalStr;
								getSum();
	 							getAllSum();
								//autoInit();
						   	 }
						   },
						failure:function (response,options) {
						 	Ext.MessageBox.hide();
							alert("等待超时，请检查网络质量!");
						}
					
					});
	}
}


function goNext(){
	if(document.getElementById("chooseValue_businessList").value==""){
		alert("请选择一项！");
	} else{
		var pch = document.getElementById("chooseValue_businessList").value;
		
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
						goSearch_businessList();
						document.getElementById("chooseValue_businessList").value="";
					},
					failure:function (response,options) {
						alert("等待超时，请检查网络质量!");
					}
				});
	}
}


function goDelete()
{
 if(document.getElementById("chooseValue_businessList").value==""){
		alert("请选择一项！");
} else{
		var pch = document.getElementById("chooseValue_businessList").value;
		
	    Ext.Ajax.request({
					method:'POST',
					params : { 
						rand : Math.random(),
						pch:pch
					},
					url:"${pageContext.request.contextPath}/salary.do?method=checkStatus",
					success:function (response,options) {
						var request = response.responseText;
						if(request =="yes"){
							//查询不存在所选年月
							if(confirm("此操作会导致数据丢失，您确认删除吗？")){
								window.location="${pageContext.request.contextPath}/salary.do?method=deleteByPch&p_ch="+document.getElementById("chooseValue_businessList").value;
							}
						}else{
							alert("该月工资已经提交人事审核，不允许删除!");
						}
					},
					failure:function (response,options) {
						alert("等待超时，请检查网络质量!");
					}
				});
	}
}

function goDelete2(){
    if(document.getElementById("chooseValue_businessYetList").value=="")
	{
		alert("请选择一项！");
	}
	else
	{
		if(confirm("此操作会导致数据丢失，您确认删除吗？")){
			window.location="${pageContext.request.contextPath}/salary.do?method=deleteByPch&p_ch="+document.getElementById("chooseValue_businessYetList").value;
		}
	}
}

//确认增加，提交
function submitAdd(flag){
	var pch = document.getElementById("pch_add").value;
    if(flag=="1"){
       document.addForm.action="${pageContext.request.contextPath}/salary.do?method=add&pch="+pch+"&flag="+flag;
		document.addForm.submit();
    }else{
		if(confirm("您已确定工资填写内容无误，进行提交？")){
			document.addForm.action="${pageContext.request.contextPath}/salary.do?method=add&pch="+pch+"&flag="+flag;
			document.addForm.submit();
		}
	}	
	
}

//确认修改，提交
function submitUpdate(flag){
	var YY = document.getElementsByName("Nian");
	var MM = document.getElementsByName("Yue");
	var Nian = YY[0].value;
	var Yue = MM[0].value;
	var pch = document.getElementById("PCH_").value;
    if(flag=="1"){
           document.updateForm.action="${pageContext.request.contextPath}/salary.do?method=update&pch="+pch+"&Nian="+Nian+"&Yue="+Yue+"&flag="+flag;
		   document.updateForm.submit();
    }else{
		if(confirm("您已确定工资填写内容无误，进行提交？")){
			document.updateForm.action="${pageContext.request.contextPath}/salary.do?method=update&pch="+pch+"&Nian="+Nian+"&Yue="+Yue+"&flag="+flag;
			document.updateForm.submit();
		}
	}	
}

//清空方法
function emprty()
{
	document.getElementById("year").value="";
	document.getElementById("month").value="";
	//document.getElementById("userName").value="";
	//document.getElementById("departName").value="";
}
 
//根据年月删除
function deleteByNY(){
	var yearNows = document.getElementById("yearNow").value;
    var monthNows = document.getElementById("monthNow").value;
    var departmentId = document.getElementById("departmentId").innerText;
 	window.location="${pageContext.request.contextPath}/salary.do?method=deleteByNY&yearNows="+yearNows+"&monthNows="+monthNows+"&departmentId="+departmentId;
}
 

 
//双击
//function grid_dblclick(obj) {
	
//	if(obj.pch !=""){
//		window.location="${pageContext.request.contextPath}/salary.do?method=auditSkip&pch="+obj.pch+"&ctype=1";
//	}
//}




</script>


<script language="javascript">
//加载页面自动初始化
function autoInit(){

  	
  	
	var executeStr=document.getElementsByName("executeStr");
   try{	
	for(var i=0;i<executeStr.length;i++){
		eval(executeStr[i].value);
	}  
	}catch(e){
	    alert("构建视图失败");
	}
	 getSum();
	 getAllSum();
  
}

//页面自动计算
function m(userid,strFormulas_s,strCNames_s,strENames_s,rankId,myvar){
	var i,j,k,strT;
	var str = document.getElementById("evalScript").innerHTML;
	eval(str);

	eval("var strFormulas="+strFormulas_s);
	eval("var strCNames="+strCNames_s);
	eval("var strENames="+strENames_s);
	var strError="";
	for (i=0;i<strFormulas.length;i++){
		//批量替换公式并完成计算
		strT=strFormulas[i];
		for (j=0;j<strCNames.length;j++){
 				//strT=strT.replace(strCNames[j], "document.getElementById('"+strENames[j]+userid+"').value");  
 				strT=strT.split(strCNames[j]).join("document.getElementById('"+strENames[j]+userid+"').value");  
 				strT=strT.replace("》",">");
 				
		}
		//最终公式
		//alert(strT);
		//计算
		try{
			eval(strT);
		}catch(e){
			 var errorName = strFormulas[i].substring(0,strFormulas[i].indexOf('='));
		     alert("自动计算出错：请重新检查职级为"+rankId+","+errorName+"工资项的相关公式");
		}
	}
	
}
function getSum(){
    var a = document.getElementById("totalwages").innerHTML;
    eval(a);
  	for(var e in myvar){
  	   if(typeof(myvar[e]) != "function"){
  		   var rank = e;
  		   //alert(myvar[e].length);  
  		   for(var i = 0;i<myvar[e].length;i++){     //遍历一个职级下的所有工资项
  		       var temp = myvar[e][i]+""+e;   //工资项
  		       var wagesTotal = document.getElementsByName(temp);
  		       var getSumresult = 0;
  		       for(var j= 0;j<wagesTotal.length;j++){        //该职级下该工资项的总和
  		       		getSumresult+=parseFloat(wagesTotal[j].value);
  		       }
  		       var temp2 = temp+"_all";
  		       getSumresult = getSumresult.toFixed(2);
  		       document.getElementById(temp2).value=getSumresult;    
  		   }	   
  		}
  		
  	}
}

function getAllSum(){
	  var total = document.getElementById("evalScript").innerHTML;
	  total = total.substring(total.indexOf("var getAllSum"));
	  eval(total);
	  for(var i =0;i<wageList.length;i++){
	     var wageTotal =0;
	  	 for(var e in myvar){
	  	 	if(typeof(myvar[e]) != "function"){
	  	 		 var rwTotal=wageList[i]+""+e+"_all";
	  	 	     var temp = document.getElementById(rwTotal);
		  	 	 if(temp!=null){
		  	 	     wageTotal+=parseFloat(temp.value);
		  	 	 }
	  	 	}	
	  	 }
	  	 document.getElementById(wageList[i]+"_allsum").value=wageTotal;
	  }
}
function showRankDiv(showdiv){
	var total = document.getElementById("evalScript").innerHTML;
	 eval(total);
	for(var e in myvar){
		if(typeof(myvar[e]) != "function"){
		   var tempDiv = "div_"+e;
		   if(showdiv==tempDiv){
		   		document.getElementById(tempDiv).style.display="";
		   		document.getElementById("sumDiv").style.display="";
		   }else{
		   		document.getElementById(tempDiv).style.display="none"
		   }
		}
	}
	if(document.getElementById(showdiv)!=null){
		document.getElementById(showdiv).style.display="";
	}
	
}
function checkit1(){
	    var f1=auploadForm.image.value;
	    if (f1==""){
	    	//上传文件不得为空
	    	alert("上传文件不得为空");
	    	return false;
	    }
	    if(f1.toLowerCase().indexOf(".xls")>-1){
	    }else{
	    	alert("提供的文件必须是excel文档!");
	    	return false;
	    }
	    showWaiting();
	    var pch = document.getElementById("chooseValue_businessList").value;
	    document.auploadForm.action="${pageContext.request.contextPath}/AS_SYSTEM/saveSalaryExcel.jsp?pch="+pch;
	    document.auploadForm.submit() ;
	}
	
function checkit2(){
	    var f1=uuploadForm.image.value;
	    if (f1==""){
	    	//上传文件不得为空
	    	alert("上传文件不得为空");
	    	return false;
	    }
	    if(f1.toLowerCase().indexOf(".xls")>-1){
	    }else{
	    	alert("提供的文件必须是excel文档!");
	    	return false;
	    }
	    showWaiting();
	   	var pch = document.getElementById("chooseValue_businessList").value;
	    document.uuploadForm.action="${pageContext.request.contextPath}/AS_SYSTEM/saveSalaryExcel.jsp?pch="+pch;
	    document.uuploadForm.submit() ;
	}
function getWagevalue(object,inputid){
    alert(object);
}
//限制输入框只能输入数字和两位小数
function inputkeypress(inputobj){
   if(!inputobj.value.match(/^\d*?\.?\d*?$/))
    inputobj.value=inputobj.t_value;
   else
    inputobj.t_value=inputobj.value;
   if(inputobj.value.match(/^(?:\d+(?:\.\d+)?)?$/))
    inputobj.o_value=inputobj.value
   if(/\.\d\d$/.test(inputobj.value))event.returnValue=false
}
//限制输入框只能输入数字和两位小数
function inputkeyup(inputobj){
   if(!inputobj.value.match(/^\d*?\.?\d*?$/))
    inputobj.value=inputobj.t_value;
   else
    inputobj.t_value=inputobj.value;
   if(inputobj.value.match(/^(?:\d+(?:\.\d+)?)?$/))
    inputobj.o_value=inputobj.value
}
//限制输入框只能输入数字和两位小数
function inputblur(inputobj){
   if(!inputobj.value.match(/^(?:\d+(?:\.\d+)?|\.\d*?)?$/))
    inputobj.value=inputobj.o_value;
    else{
     if(inputobj.value.match(/^\.\d+$/))
      inputobj.value=0+inputobj.value;
     if(inputobj.value.match(/^\.$/))
      inputobj.value=0;
     inputobj.o_value=inputobj.value
    }
}

function showquery(){
	
	//新版本用EXCEL方式在线修改
	var pch = document.getElementById("chooseValue_businessYetList").value;
	if(pch==""){
		alert("请选择一项！");
		return;
	}
	
	var url  ="${pageContext.request.contextPath}/salary.do?method=export&pch="+pch+"&mode=view";
    window.open(url); 

}
</script>

</body>

</html>

