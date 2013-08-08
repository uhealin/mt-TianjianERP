<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<title>客户管理</title>
	<script type="text/javascript"><!--

	function openUrl(text,url){
		try{
			var randStr = Math.random();
	  		
	        var tab = parent.tab ;
			n = tab.add({    
				'title':text,    
				closable:true,  //通过html载入目标页    
				html:'<iframe name="test_' + randStr + '" scrolling="auto" frameborder="0" width="100%" height="100%" src=""></iframe>'   
			}); 
	        tab.setActiveTab(n);

	        thisForm.action = url;
	        thisForm.target = "test_" + randStr;
	        thisForm.submit();
		}catch(e){
			window.location=url;
		}
		
	}	

function goDelete() {
	if(document.getElementById("chooseValue_customer").value=="") {
		alert("请选择要删除的菜单项！");
	}
	else {
		//alert(document.thisForm.chooseValue.value);
		if(confirm("您的操作可能会造成数据丢失，您确定要删除该记录吗？","提示")){
			window.location="customer.do?method=del&&act=del&&chooseValue="+document.getElementById("chooseValue_customer").value;
		}

	}
}
function readyFun() {
	var tbar_customer = new Ext.Toolbar({
			renderTo:'gridDiv_customer',
            items:[

<c:if test="${flag == 'mylist'}">
{
    text:'修改',
    cls:'x-btn-text-icon',
    icon:'/AuditSystem/img/edit.gif',
   	handler:function(){
<c:if test="${isView == '是'}">goInformation();</c:if>
<c:if test="${isView != '是'}">goEdit() ;</c:if>           	
		}
},'-',{
    text:'查询',
    id:'btn-query',
    cls:'x-btn-text-icon',
    icon:'${pageContext.request.contextPath}/img/query.gif',
    handler:queryWinFun
},

</c:if>
<c:if test="${flag != 'mylist'}">
            {
	            text:'新增',
	            cls:'x-btn-text-icon',
	            icon:'/AuditSystem/img/add.gif',
	            handler:function(){
	            	window.location = "${pageContext.request.contextPath}/customer.do?method=del&&act=add";
				}
       		},'-',{
            text:'修改',
            cls:'x-btn-text-icon',
            icon:'/AuditSystem/img/edit.gif',
           	handler:function(){
<c:if test="${isView == '是'}">goInformation();</c:if>
<c:if test="${isView != '是'}">goEdit() ;</c:if>           	
				}
        },'-',{
            text:'切换至批量删除',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/delete.gif',
            handler:function () {
            	goMutiDelete();
            }
        },
/*    
        '-',{
            text:'查看详细信息',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/query.gif',
            handler:function () {
            	goInformation();
        	}
        },
*/
 
<c:if test="${userSession.userLoginId == 'admin' || isPrint == '1'}">     
	'-',{
            text:'打 印',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/print.gif',
            handler:function () {
            	print_customer();
            }
        },
</c:if>         
<c:if test="${userSession.userLoginId == 'admin'}">     
        '-',{
            text:'批量导入',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/import.gif',
        	handler:function () {
        		goJoin() ;
        	}
        },'-',{
            text:'特殊保密设置',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/user.gif',
           	handler:function () {
           		goPopedom();
           	}
        },

</c:if>         
        '-',{
            text:'集团维护',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/edit.gif',
            handler:function () {
            	var url = "${pageContext.request.contextPath}/group.do";
            	openUrl("集团维护",url);
            	//window.location=url;
           	}
        },'-',{
            text:'${iText}',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/edit.gif',
            handler:function(){
            	var url = "${pageContext.request.contextPath}/customer.do?method=manager";
            	openUrl("${iText}",url);
            	//window.location=url;
            }
        },'-',{
            text:'查询',
            id:'btn-query',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/query.gif',
            handler:queryWinFun
        },
<c:if test="${svalue != '立信'}">        
        {
        	text:'关闭',
			icon:'${pageContext.request.contextPath}/img/close.gif',
           	handler:function(){
           		closeTab(parent.tab);
			}
        },
</c:if>        
</c:if>        
        '->',{
            text:'刷新',
            cls:'x-btn-text-icon',
            icon:'/AuditSystem/img/refresh.gif',
            handler:function(){
            	window.location.reload();
				
			}
       	}]
       });  
	}
	var queryWin = null;
	        
	function queryWinFun() {
		if(!queryWin) {
			var searchDiv = document.getElementById("search") ;
		    queryWin = new Ext.Window({
		     title: '客户查询',
		     renderTo :'searchWin',
		     width: 495,
		     height:300,
		  	 //modal:true,
		        closeAction:'hide',
		        listeners   : {
		        	'hide':{fn: function () {
					new BlockDiv().hidden();
					queryWin.hide();	         	
		        	}}
		        },
		       layout:'fit',
			  html:searchDiv.innerHTML,
		    buttons:[{
		           text:'确定',
		         	handler:function(){
		               	queryWin.hide();
		               	getDepartmentId();
		               	goSearch_customer();
		            
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
</head>
<body leftmargin="0" topmargin="0">
<form name="thisForm" method="post" action="" style="width:100%">
<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>

<div style="height:expression(document.body.clientHeight-27);width:100%"><mt:DataGridPrintByBean name="customer"/></div>
</form>
<Script>
function goJoin(){
//	alert("gojoin");
	window.location="customer.do?method=manuUpLoad";
}
function goPopedom(){
	if(document.getElementById("chooseValue_customer").value=="") {
		alert("请选择要客户编号！");
		return false;
	}
	window.location="customer.do?method=customerRight&&DepartID="+document.getElementById("chooseValue_customer").value+"&random="+Math.random();
}


//goSearch();
function goPrintln(){
	window.location="customer.do?method=xlsPrint";
}
function goInitPage() {

	window.location="customer.do";
}
function goAdd() {

	window.location="customer.do?method=del&&act=add";
}
function goDelete() {
	if(document.getElementById("chooseValue_customer").value=="") {
		alert("请选择要删除的菜单项！");
	}
	else {
		//alert(document.thisForm.chooseValue.value);
		if(confirm("您的操作可能会造成数据丢失，您确定要删除该记录吗？","提示")){
			window.location="customer.do?method=del&&act=del&&chooseValue="+document.getElementById("chooseValue_customer").value;
			//window.location.src="Del.jsp?chooseValue="+thisForm.chooseValue.value;
		}

	}
	}

function goMutiDelete() {

	window.location="customer.do?method=mutiDeleteList";
}

function goEdit() {
	if(document.getElementById("chooseValue_customer").value=="") {
		alert("请选择要修改的客户！");
	} else {
		//alert(document.thisForm.chooseValue.value);
		window.location="customer.do?method=del&&act=update&&chooseValue="+document.getElementById("chooseValue_customer").value+"&flag=${flag}";
	}
}

function goCheckObj(){
	if(document.getElementById("chooseValue_customer").value=="") {
		alert("请选择要查看的客户！");
	} else {
  		if(checkPopedom())	
			window.location="${pageContext.request.contextPath}/attach/Manager.jsp?&CustomerId="+document.getElementById("chooseValue_customer").value;
	}
}

function checkPopedom(){
var oBao = new ActiveXObject("Microsoft.XMLHTTP");
	oBao.open("POST","attach.do?method=checkPopedom&&CustomerId="+document.getElementById("chooseValue_customer").value+"&random="+Math.random(),false);
    oBao.send();
	var strResult = unescape(oBao.responseText);
	if(strResult.indexOf("ok")>-1){
		alert("用户没有查看该客户的权限！");
		return false;
	}
	return true;

}

function subClearSearch() {
	window.location="customer.do";
}
function subSearch() {
	var flag = false;
	var strW = "";
	
	if(document.thisForm.CustomerID.value != "") {
		flag = true;
	}

	if(document.thisForm.inType.value != "") {
		flag = true;
	} 
	
	if(!flag) {
		alert("请至少填写一个查找条件！");
	}
	
	return flag;
}

function goInformation(){
	var customer = document.getElementById("chooseValue_customer").value;
	
	if(customer==""){
		alert("请选择您要查看的客户!");
	}else{
		window.location = "${pageContext.request.contextPath}/Customer/CustomerInformationFrame.jsp?customer="+customer+"&flag=${flag}";
	}	
}


function grid_dblclick(obj){
	var isView = '${isView}';
	if(isView == '是'){
		//显示客户详细列表
		window.location = "${pageContext.request.contextPath}/Customer/CustomerInformationFrame.jsp?customer="+obj.DepartID;
	}else{
		//修改页面
		window.location="customer.do?method=del&&act=update&&chooseValue="+obj.DepartID;
	}
	
	//myOpenUrlByWindowOpen("${pageContext.request.contextPath}/Customer/CustomerInformationFrame.jsp?customer="+DepartID,"","");
}


</Script>

<div id="search" style="display:none">
<br/><br/><br/>
<div style="margin:0 20 0 20">请在下面输入查询条件：</div>
<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
	<table border="0" cellpadding="0" cellspacing="0" width="100%" bgcolor="">
		<tr align="center">
			<td align="right">客户编号/名称：</td>
			<td align=left><input  type="text" name="CustomerID" id="CustomerID" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"  class="validate-alpha" valuemustexist=true autoid=2059/></td>
			<td align="right">联系人：</td>
			<td align=left><input type="text" name="linkMan" id="linkMan"  value="" /></td>
			</tr>
			
			<tr>	
			<td align="right">联系电话：</td>
			<td align=left><input type="text" name="phoneNumber" id="phoneNumber" value="" /></td>
			<td align="right">会计制度类型：</td>
			<td align=left>
			<input  type="text" 
					name="inType" 
					value=""
					hideresult=true 
					onkeydown="onKeyDownEvent();" 
					onkeyup="onKeyUpEvent();" 
					onclick="onPopDivClick(this);" 
					valuemustexist=true 
					autoid=93 
					noinput=true>
			</td>
			
		</tr>
	 
     <tr>
      <td align="right"><div >所属集团单位：</div></td>
      <td align=left><input name="groupname" type="text" id="groupname" value="" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();"  onClick="onPopDivClick(this);"   valuemustexist="true" autoid=614  maxlength="20"></td>
      <td align="right"><div >所属部门：</div></td>
      <td align=left><input name="departmentid1" type="text" id="departmentid1" value="" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();"  onClick="onPopDivClick(this);" multilevel=true  valuemustexist="true" autoid=958 noinput=true  maxlength="20">
	      <input type="hidden" id="departmentid" name="departmentid"/>
      </td>
     </tr>
     
      <tr>
      <td align="right"><div> 是否会员：</div></td>
      <td align=left>
		<select id="vip" name="vip">
			<option value="">请选择</option>
			<option value="否">否</option>
			<option value="是">是</option>
		</select>      	
      </td>
	 	<td  align="right">客户级别：</td>
		<td >
		<select name="customerIeve" id="customerIeve" title="请正确输入客户级别">
			<option value=""  >请选择</option>
			<option value="A级"  >A级</option>
			<option value="B级"  >B级</option>
			<option value="C级"  >C级</option>
			<option value="D级"  >D级</option>
			<option value="E级"  >E级</option>
		</select>
		</td>
	 </tr>
	 
	 <tr>
		<td  align="right">客户来源：</td>
		<td >
			<input class="required" onKeyDown="onKeyDownEvent();" 
					onKeyUp="onKeyUpEvent();"
					onClick="onPopDivClick(this);" 
					valuemustexist=true autoid=700 refer='客户来源方式' 
					noinput=true name="approach" 
					type="text" id="approach"  maxlength="20" ></td>
	 <td align="right">行业类型：</td>
	 <td  >
     	<input name="hylx" type="text" id="hylx" value="${customer.hylx}" 
		     	onkeydown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();"
		     	onClick="onPopDivClick(this);" valuemustexist=true autoid=261  >
	</td>
	</tr>
	
	<tr>
		<td align="right">公司性质：</td>
		<td ><input class="required" onpropertychange="company(this);" 
					onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" 
					onClick="onPopDivClick(this);" valuemustexist=true autoid=700 refer='公司性质'
					noinput=true name="companyProperty" type="text" id="companyProperty" maxlength="200"  
					value="${customer.companyProperty }"  title="组织机构性质"></td>
		<td align="right">状态：</td>		
		<td ><input name="state" type="text" id="state" style="width: 50" > </td>
	</tr>
	</table>
</div>

<script type="text/javascript">
		Ext.onReady(readyFun);
		
</script>
<script type="text/javascript">
function getDepartmentId(){
	var departmentName =document.getElementById("departmentid1").value;
	if(departmentName !=""){
		var departId = departmentName.substring(departmentName.indexOf("-")+1,departmentName.length);		
		document.getElementById("departmentid").value = departId;
	}
}
</script>
</body>
</html>

