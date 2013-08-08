<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%

String tip=(String)session.getAttribute("tip");
if(tip!=null && !tip.equals("")){
	out.println("<script>alert(\""+tip+"\");</script>");
	session.setAttribute("tip","");
}

String customerid = (String)request.getAttribute("coustomerid");
String action =  (String)request.getAttribute("action");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>关联公司管理</title>

<script type="text/javascript">  

function ext_init(){
	
	var tbar_connectCompanysList = new Ext.Toolbar({
		renderTo: 'gridDiv_connectCompanysList',
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
            icon:'/AuditSystem/img/edit.gif',
           	handler:function(){
				goUpdate();
			}
        },'-',{
            text:'删除',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/delete.gif',
            handler:function () {
            	goDelete();
            }
        },'-',{
			text:'查询', 
			id:'btn-query',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
			handler:function () {
            	queryWinFun();
            }
		},'-',{
			text:'批量导入',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/import.gif',
			handler:function () {
				//importExcel();
				queryWinFun1();
			}
		},'-',{
			text:'从帐套选择',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/import.gif',
			handler:function () {
				selectFromAccount();
			}
		},'-',{
			text:'关闭',
			cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/close.gif',
			handler:function () {
				closeTab(parent.tab);
				//parent.tab.remove(parent.tab.getActiveTab()); 
			}
		}]
	});
	
} 


var queryWin = null;
function queryWinFun(id){
	var searchDiv = document.getElementById("search") ;
	searchDiv.style.display = "" ; 	
	if(!queryWin) { 
	    queryWin = new Ext.Window({
			title: '查询',
			contentEl:'search',
		    renderTo :'searchWin',
	     	width: 350,
	     	height:200,
        	closeAction:'hide',
       	    listeners : {
	         	'hide':{
	         		fn: function () {
	         			new BlockDiv().hidden();
	         			searchDiv.style.display = "none" ;
						queryWin.hide();
					}
				}
	        },
        	layout:'fit',
	    	//html:searchDiv.innerHTML,
	    	buttons:[{
            	text:'确定',
          		handler:function(){
          			goSearch_connectCompanysList();
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
    new BlockDiv().show();
    queryWin.show();
}

var queryWinExcel = null;
function queryWinFun1(id){
	var searchDiv = document.getElementById("searchExcel") ;
	searchDiv.style.display = "" ; 	
	if(!queryWinExcel) { 
	    queryWinExcel = new Ext.Window({
			title: '批量导入',
			contentEl:'searchExcel',
		    renderTo :'searchWin',
	     	width: 650,
	     	height:250,
        	closeAction:'hide',
       	    listeners : {
	         	'hide':{
	         		fn: function () {
	         			new BlockDiv().hidden();
	         			searchDiv.style.display = "none" ;
						queryWinExcel.hide();
					}
				}
	        },
        	layout:'fit',
	    	//html:searchDiv.innerHTML,
	    	buttons:[{
            	text:'确定',
          		handler:function(){
          			importData();
    	           	queryWinExcel.hide();
               		
            	}
          		
        	},{
            	text:'取消',
            	handler:function(){
               		queryWinExcel.hide();
            	}
        	}]
	    });
		
    }
    new BlockDiv().show();
    queryWinExcel.show();
}

window.attachEvent('onload',ext_init);




</script>


</head>
<body >

<div style="height:expression(document.body.clientHeight-23);" >
<mt:DataGridPrintByBean name="connectCompanysList" outputData="true" outputType="invokeSearch" />
</div>
<input name="connectaccount" type="hidden" value="关联单位余额表" class="flyBT" onclick="selectConnectAccount();">&nbsp;

<form id="thisForm" action="/AuditSystem/Customer/manuupload2.jsp" method="post" enctype="multipart/form-data">

<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth)/2);top:expression((document.body.clientHeight)/2); z-index: 2"></div>

<div id="search" style="display:none">
<br/>
<div style="margin:0 20 0 20">请在下面输入查询条件：</div>

<table border="0" cellpadding="0" cellspacing="0" width="100%"  bgcolor="" >
	<tr align="center">
		
		<td align="right">关联公司名称：</td>
		<td align=left>
			<input  type="text" name="connectcompanyName" id="connectcompanyName" size="20" />
		</td>
	</tr>	
</table>
</div>




<div id="searchExcel" style="display:none">

<table><tr><td>
<fieldset><legend>EXCEL文件内容格式说明</legend>
<ul>
<li>EXCEL表中应该有一个叫做<font color=blue><b>关联公司名称</b></font>的表页</li>
<li>该表页含有至少<font color=blue><b>关联公司名称</b></font><font color=red>(必填)</font>这么一列</li>
<br><br><b><a href='#' onclick='javascript:downLoad();'><font color='#FF0000'>点击下载示例模版</font></a></b>
</ul>				
</fieldset>

</td></tr>
<tr><td>用户数据EXCEL格式文件路径:</td></tr><tr><td >
<input type="file" name="image" id="image"   size="90" title="请输入，不得为空">&nbsp;</td><td>

</td></tr></table>


</div>

<input type="hidden" name="customerid" id="customerid" value="<%=customerid%>"/>
<input type="hidden" name="acts" id="acts" value="<%=action%>"/>
</form>


<form id="myForm" name="myForm" action="" method="post">
<input type="hidden" id="AccPackageID" name="AccPackageID"/>
<input type="hidden" id="AssItemID" name="AssItemID"/>
<input type="hidden" id="SubjectID1" name="SubjectID1"/>
<input type="hidden" id="BeginDate" name="BeginDate"/>
<input type="hidden" id="EndDate" name="EndDate"/>
</form>

</body>
<script language="javascript" type="text/javascript">

//添加关联公司
function goAdd(){

    window.location="${pageContext.request.contextPath}/Customer/AddconnectCompanys.jsp?act=add&customerid="+document.getElementById("customerid").value+"&acts="+document.getElementById("acts").value ;
//	window.showModalDialog("${pageContext.request.contextPath}/Customer/AddconnectCompanys.jsp?act=add&customerid="+document.getElementById("customerid").value+"&math="+Math.random(),window,"dialogWidth:640px;dialogHeight:480px;status=0;help=0;scroll=1;")  
}                                 

//修改关联公司
function goUpdate(){
	var choose_connectCompanysList = document.getElementById("chooseValue_connectCompanysList").value;

	if(choose_connectCompanysList == ""){
		alert("请选择要修改的关联公司！");
	} else {
			
		window.location="${pageContext.request.contextPath}/connectcompanys.do?method=edit&autoid=" + choose_connectCompanysList+"&customerid="+document.getElementById("customerid").value+"&acts="+document.getElementById("acts").value;
		
	}
}

//删除关联公司
function goDelete(){
	var choose_connectCompanysList = document.getElementById("chooseValue_connectCompanysList").value;
	if(choose_connectCompanysList == ""){
		alert("请选择要删除的关联公司！");
	} else {
		if(confirm("您的操作可能会造成数据丢失，您确定要删除该记录吗？","提示")){
				
			window.location="${pageContext.request.contextPath}/connectcompanys.do?method=remove&autoid=" + choose_connectCompanysList+"&customerid="+document.getElementById("customerid").value+"&acts="+document.getElementById("acts").value;
		}
	}
}

//清空
function clearVal(){

   var connectcompanyName = document.getElementById("connectcompanyName");
   connectcompanyName.value = "";

}
//批量导入
function importExcel() {
//alert(document.getElementById("importExcel").style.display);
	var str = "<table><tr><td><fieldset><legend>EXCEL文件内容格式说明</legend>";
		str+="<ul>";
		str+="<li>EXCEL表中应该有一个叫做<font color=blue><b>关联公司名称</b></font>的表页</li>";
		str+="<li>该表页含有至少<font color=blue><b>关联公司名称</b></font><font color=red>(必填)</font>这么一列</li>";
		str+="<br><br><b><a href='#' onclick='javascript:downLoad();'><font color='#FF0000'>点击下载示例模版</font></a></b>";
		str+="</ul>";				
		str+="</fieldset></td></tr>";
		str+="<tr><td>用户数据EXCEL格式文件路径:</td></tr><tr><td ><input type=\"file\" name=\"image\" id=\"image\"   size=\"90\" title=\"请输入，不得为空\">&nbsp;</td><td><input type=\"button\" id=\"\" value=\"确定\" class=\"flyBT\" onclick=\"return importData();\"></td></tr></table>";
	if(document.getElementById("importExcel").innerHTML=="") {
		document.getElementById("importExcel").innerHTML=str;
		document.getElementById("import").value="隐藏批量导入";
		document.getElementById("importExcel").style.display="";
	} else {
		document.getElementById("importExcel").innerHTML="";
		document.getElementById("import").value="批量导入";
		document.getElementById("importExcel").style.display="none";
	}
}
//处理上传文件
function importData() {
	var f1=document.getElementById("image").value;
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
	thisForm.submit();
}

function selectFromAccount(){
	var customerid = document.getElementById("customerid").value;
	var url = "/AuditSystem/selectFromAccount.do?method=selectAccount&CustomerID="+customerid;
	
	//window.open(url);
	
	var randStr = Math.random();
		          		
	var tab = parent.parent.tab ;
	if(tab){ 
		n = tab.add({    
			'title':"关联单位",    
			closable:true,  //通过html载入目标页    
			html:'<iframe name="test_' + randStr + '" scrolling="auto" frameborder="0" width="100%" height="100%" src="'+url+'"></iframe>'   
		}); 
		tab.setActiveTab(n);
	}
	
	
	
	
}
//关联单位余额表
function selectConnectAccount() {
var choose_connectCompanysList = document.getElementById("chooseValue_connectCompanysList").value;

	if(choose_connectCompanysList == ""){
		alert("请选择要查询的关联公司！");
	} else {
			var url = "/AuditSystem/connectcompanys.do";
			query_String = "method=connectAccount&autoid="+choose_connectCompanysList;
			var myreturnValue = ajaxLoadPageSynch(url,query_String);
			var daccpackageid=myreturnValue.split('`~')[0];
			var dsubjectid=myreturnValue.split('`~')[1];
			var dassitemid=myreturnValue.split('`~')[2];
			if(daccpackageid=='') {
				alert("请先填写关联公司的详细信息");
				return;
			} else if(dsubjectid!='') {
				//科目
				window.open("/AuditSystem/account.do?method=ledgerlist&DataGrid=account&AccPackageID="+daccpackageid+"&SubjectID="+dsubjectid+"&BeginDate=01&EndDate=12",'_blank','height=480,width=640, resizable=yes,toolbar=no,menubar=no,titlebar=no');
			} else {
				alert("请先填写关联公司的详细信息");
				return;
			}
	}

}
//穿透
function goSort() {
    var obj=getTR();
    var daccpackageid = obj.daccpackageid;
    var dsubjectid = obj.dsubjectid;
    var dassitemid = obj.dassitemid;
    
    if(daccpackageid=='') {
    	alert("请先填写关联公司的详细信息");
    	return;
    } else if(dsubjectid!='' && dassitemid!='') {
	    //核算明细帐
	    document.getElementById("AccPackageID").value = daccpackageid;
	    document.getElementById("AssItemID").value = dassitemid;
	    document.getElementById("SubjectID1").value = dsubjectid;
	    document.getElementById("BeginDate").value = '01';
	    document.getElementById("EndDate").value = '12';
	    
	    myForm.action = "assitementry.do";
	    myForm.target = "_blank";
	    myForm.submit();
    } else if(dsubjectid!='') {
   		 //科目明细帐
    	var url="/AuditSystem/subjectentry.do?AccPackageID="+daccpackageid+"&SubjectID="+dsubjectid+"&BeginDate=01&EndDate=12 ";
		myOpenUrlByWindowOpen(url,'','');
    } else {
		alert("请先填写关联公司的详细信息");
		return;
	}
}

 function getlocationhost(){
		return "http:\/\/"+window.location.host;
	}
    
    var AuditReport =  new ActiveXObject("MTOffice.WebOffice");
    function downLoad() {
	    var url = getlocationhost()+'${pageContext.request.contextPath}/Customer/connectCompany_template.zip';
		AuditReport.IEDownLoad3(url,'connectCompany_template.zip',0);
	}
</Script>