<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>规章制度列表</title>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/searchSession.js" charset=gbk></script>

<script type="text/javascript">

Ext.onReady(function(){
  //  queryWinFun();
	var tbar_customer = new Ext.Toolbar({
		renderTo:'divBtn',
           items:[
           {
			text:'查询',
			id:'btn-query',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
			handler:queryWinFun
		},'-',
		{
			text:'打印',
			id:'btn-print',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/print.gif',
			handler:function(){
				print_listSaraly();
			}
		},
		'->'
        ]
        });  

});
var queryWin = null;
function queryWinFun(){
	var searchDiv = document.getElementById("search") ;
	searchDiv.style.display = "" ;
	if(!queryWin) { 
	    queryWin = new Ext.Window({
			title: '工资查询',
			contentEl:'search',
	     	renderTo : searchWin,
	     	width: 300,
	     	height:260,
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
          			query();
            	}
        	},{
            	text:'清空',
          		handler:function(){
          			emprty();
          		}
        	}]
	    });
    }
    new BlockDiv().show();
    queryWin.show();
}

</script>

</head>
<body style="overflow: hidden;">
<div id="divBtn"></div>
<div id="divprint">
<form name="thisForm" id="thisForm" method="post" action="${pageContext.request.contextPath}/salary.do?method=listSalarybyDepart">
<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>

<div id="search" style="display:none;">
<br>
<br>
			<table border="0" cellpadding="0" cellspacing="0" bgcolor="" align="center" style="width:100%;line-height: 25px;" width="100%">
                <tr>
                  <td  align="right" width="30%">选择部门：</td>
                   <td  align="left"> 
                    <input name="departmentId" id="departmentId" type="hidden" > 
                    <input type="text" name="departmentName" id="departmentName"   onfocus="onPopDivClick(this);"
				onkeydown="onKeyDownEvent();"
				onkeyup="onKeyUpEvent();"
				onclick="onPopDivClick(this);" 
				autoid=958 noinput=true multilevel=true norestorehint=true  valuemustexist=true/> 
                   </td>
                </tr> 
			   <tr>
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
<div style="height:expression(document.body.clientHeight-27);">
<mt:DataGridPrintByBean name="listSaraly"  />
</div>
<Script>

function query(){

	var month = document.getElementById("month").value;
	var year = document.getElementById("year").value;
	var departmentName = document.getElementById("departmentName").value;



	if(departmentName !=""){
		var departId = departmentName.substring(departmentName.indexOf("-")+1,departmentName.length);	
		document.getElementById("departmentId").value = departId;
	}
	 document.getElementById("thisForm").submit();
}

function emprty()
{
	document.getElementById("year").value="";
	document.getElementById("month").value="";
	document.getElementById("departmentId").value="";
}
 

</script>
</body>
</html>
