<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>规章制度列表</title>
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
    var htmlMonth = "<select style='width: 60;' onpropertychange='' id='monthNow'><option value=''>请选择</option>" ;
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
	    
	//工资发放列表工具条
	var tbar_customer = new Ext.Toolbar({
		renderTo:'divBtn',
           items:[{
			text:'修改',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/edit.gif',
			handler:function(){
				updateSalary();
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
			handler:function (){
				queryWinFun('businessList');
			}
		},'-',html,htmlMonth,'-',{
            text:'增加',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/add.gif',
            handler:function(){
            	goAdd();
			}
      	},'->'
        ]
        });  
	var tbar_customer1 = new Ext.Toolbar({
		renderTo:'divBtn1',
           items:[{
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
			handler:function (){
				queryWinFun('businessYetList');
			}
		},'->'
        ]
        });  
        
        //添加工资发放的工具条
	var tbar_customer_add = new Ext.Toolbar({
		renderTo:'divBtnAdd',
           items:[{
            text:'确认',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/save.gif',
            handler:function(){
            	submitAdd();
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
	var tbar_customer_add = new Ext.Toolbar({
		renderTo:'divBtnUpdate',
           items:[{
            text:'确认',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/save.gif',
            handler:function(){
            	submitUpdate();
			}
      	},'-',{
			text:'返回',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/back.gif',
			handler:function(){
				document.getElementById("add").style.display="none";
				document.getElementById("showList").style.display="";
				document.getElementById("update").style.display="none";
			}
		},'->'
        ]
        });  

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
    var departmentIds = document.getElementById("departmentId").innerText;
	if(monthNows=="")
	{
		alert("请选择月份！");
		document.getElementById("monthNow").focus();
	}
	else
	{
	  /*
	    Ext.Ajax.request({
					method:'POST',
					params : { 
						rand : Math.random(),
						yearNow:yearNows,
						monthNow:monthNows,
						departmentId:departmentIds
					},
					url:"${pageContext.request.contextPath}/salary.do?method=checkNY",
					success:function (response,options) {
						var request = response.responseText;
						if(request =="0"){
							//查询不存在所选年月
							addSalary(yearNows,monthNows);
						}else{
							alert("对不起，您所选的月份已初始化、暂存、或者发放");
							document.getElementById("monthNow").focus();
						}
					},
					failure:function (response,options) {
						alert("后台出现异常,修改信息失败!");
					}
				});*/
				addSalary(yearNows,monthNows);
			  	
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
	height:25PX;
	WORD-WRAP: break-word
}

</style>
</head>
<body style="overflow: hidden;" >
<div id="evalScript" style="display:none"></div>
<div id="departmentId" style="display:none">${ departmentId}</div>
<div id="my-tabs">
	<div id="tab1">
	<div id="showList">
		<div id="divBtn"></div>
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
		<div id="newTableDiv" style="overflow: auto;width: 100%;height: expression(document.body.clientHeight-31)"></div><p>&nbsp;</p>
	</form>
</div>

<div id="update" style="display:none;" >
	<div id="divBtnUpdate"></div>
	<form name="updateForm" method="post" action="${pageContext.request.contextPath}/salary.do?method=update">
		<input type="hidden" value="" id= "PCH_">
		<div id="newTableDiv2" style="overflow: auto;width: 100%;height: expression(document.body.clientHeight-31)"></div><p>&nbsp;</p>
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
//增加方法
function addSalary(yearNows,monthNows){
	Ext.Ajax.request({
						method:'POST',
						params : { 
							rand : Math.random(),
							paraYear:yearNows,
							paraMonth:monthNows 
						},
						url:"${pageContext.request.contextPath}/salary.do?method=init",
						success:function (response,options) {
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
								document.getElementById("add").style.display="";
								document.getElementById("showList").style.display="none";
								document.getElementById("YY").value=yearNows;
								document.getElementById("MM").value=monthNows;
								document.getElementById("newTableDiv").innerHTML=tble + "<br>";
								document.getElementById("evalScript").innerHTML=str;
						
								autoInit();
						   	 }
						   },
						failure:function (response,options) {
							alert("后台出现异常,获取文件信息失败!");
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
	Ext.Ajax.request({
						method:'POST',
						params : { 
							rand : Math.random(),
							p_ch:pch,
							e_ditRight:editRight
						},
						url:"${pageContext.request.contextPath}/salary.do?method=updateInit",
						success:function (response,options) {
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
								document.getElementById("showList").style.display="none";
								document.getElementById("update").style.display="";
								document.getElementById("newTableDiv2").innerHTML=tble + "<br>";
								document.getElementById("PCH_").value=pch;
								document.getElementById("evalScript").innerHTML=str;
								autoInit();
						   	 }
						   },
						failure:function (response,options) {
							alert("后台出现异常,获取文件信息失败!");
						}
					
					});
	}
}

function goDelete()
{
	if(document.getElementById("chooseValue_businessList").value=="")
	{
		alert("请选择一项！");
	}
	else
	{
		if(confirm("此操作会导致数据丢失，您确认删除吗？")){
			window.location="${pageContext.request.contextPath}/salary.do?method=deleteByPch&p_ch="+document.getElementById("chooseValue_businessList").value;
		}
	}
}

//确认增加，提交
function submitAdd(){
	var userArray = document.getElementsByName("userId");
	var pch = document.getElementById("pch_add").value;
	var strUserIds ="";
	for(i=0;i<userArray.length;i++){
		if(userArray.length>1){
			strUserIds +=userArray[i].value;
			if(i<userArray.length-1){
				strUserIds +=",";
			}
		}else{
			strUserIds=userArray[i].value;
		}
	}
	if(confirm("您已确定工资填写内容无误，进行提交？")){
		document.addForm.action="${pageContext.request.contextPath}/salary.do?method=add&strUserIds="+strUserIds+"&pch="+pch;
		document.addForm.submit();
	}
	
}

//确认修改，提交
function submitUpdate(){
	var userArray = document.getElementsByName("userId");
	var strUserIds ="";
	var YY = document.getElementsByName("Nian");
	var MM = document.getElementsByName("Yue");
	var Nian = YY[0].value;
	var Yue = MM[0].value;
	var pch = document.getElementById("PCH_").value;
	for(var i=0;i<userArray.length;i++){
		if(userArray.length>1){
			strUserIds +=userArray[i].value;
			if(i<userArray.length-1){
				strUserIds +=",";
			}
		}else{
			strUserIds=userArray[i].value;
		}
	}
	if(confirm("您已确定工资填写内容无误，进行提交？")){
		document.updateForm.action="${pageContext.request.contextPath}/salary.do?method=update&pch="+pch+"&strUserIds="+strUserIds+"&Nian="+Nian+"&Yue="+Yue;
		document.updateForm.submit();
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
 
//根据年月删除
function deleteByNY(){
	var yearNows = document.getElementById("yearNow").value;
    var monthNows = document.getElementById("monthNow").value;
	window.location="${pageContext.request.contextPath}/salary.do?method=deleteByNY&yearNows="+yearNows+"&monthNows="+monthNows;
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
	try{
		
		var executeStr=document.getElementsByName("executeStr");
		for(var i=0;i<executeStr.length;i++){
			eval(executeStr[i].value);
		}
	}catch(e){
		alert("初始化失败：找不到对象！");
	}
}

//页面自动计算
function m(userid,strFormulas_s,strCNames_s,strENames_s){
	var i,j,k,strT;
	var str = document.getElementById("evalScript").innerHTML;
	eval(str);
	eval("var strFormulas="+strFormulas_s);
	eval("var strCNames="+strCNames_s);
	eval("var strENames="+strENames_s);
	for (i=0;i<strFormulas.length;i++){
		//批量替换公式并完成计算
		strT=strFormulas[i];
		for (j=0;j<strCNames.length;j++){
 				strT=strT.replace(strCNames[j], "document.getElementById('"+strENames[j]+userid+"').value");  
		}
		//最终公式
		//alert(strT);
		//计算
		try{
			eval(strT);
		}catch(e){alert('自动计算字段间关系出错：'+e)}
	}
	
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
</script>
</body>
</html>
