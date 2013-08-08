<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>我的工资查询</title>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/searchSession.js" charset=gbk></script>

<script type="text/javascript">

Ext.onReady(function(){
	var tbar_customer = new Ext.Toolbar({
		renderTo:'divBtn',
           items:[{
			text:'查询',
			id:'btn-query',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
			handler:queryWinFun
		},'->'
        ]
        });  
	
	if('${year}' == ''){
		queryWinFun();
	}
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
	     	width: 350,
	     	height:240,
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
            	text:'确定',
          		handler:function(){
          			query();
            	}
        	}]
	    });
    }
    new BlockDiv().show();
    queryWin.show();
}

</script>

</head>
<div id="divBtn"></div>

<div id="divprint">
<form name="thisForm" id="thisForm" method="post" action="">
<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>

<div id="search" style="display:none;">
<br>
<br>
			<table border="0" cellpadding="0" cellspacing="0" bgcolor="" align="center" style="width:100%;line-height: 25px;" width="100%">
			   <tr>
					  <td  align="right" width="30%">发放年份：</td>
				      <td  align="left">
				      <input type="hidden" name="month" id="month"  >
				      <input type="hidden" name="pchname" id="pchname"   >
				      	<input type="text" name="year" id="year" autoid=30050 class="required"  >
				      
					  </td>
				</tr>
				<tr >
					<td  align="right" >发放项目：</td>
					<td  align="left">
						<input type="text" name="pchname1" id="pchname1" refer=year autoid=30022 class="required" onselect="go(this);"  >
				    </td>
				</tr>
		</table>
</div>	

</form>
<div style="height:expression(document.body.clientHeight-27);overflow: auto;" >
<c:if test="${year !='' and month !='' and pchname!='' }">

${salary}

</c:if>
</div>
</div>
<Script>
function go(obj){
	try{
		var value = obj.value;
		if(value !=""){
			var v = value.split("|");
			document.getElementById("month").value = v[0];
			document.getElementById("pchname").value = v[1];
		}	
	}catch(e){
		document.getElementById("month").value = "";
		document.getElementById("pchname").value = "";
	}
	
}

function query(){
	
	var month = document.getElementById("month").value;
	var year = document.getElementById("year").value;
	
	if(year==""){
		alert("年份不能为空!");
		return ;
	}
	if(month ==""){
		alert("月份不能为空!");
		return ;
	}
	if (!formSubmitCheck('thisForm')) {
		return;
	}
	
	//alert('临时去掉检查，方便测试，通过恢复');
	document.getElementById("thisForm").action = "${pageContext.request.contextPath}/salary.do?method=mySalaryList";
	document.getElementById("thisForm").submit();
	retrun;
	
}

document.getElementById("month").value = '${month}';
document.getElementById("year").value= '${year}';

</script>
</body>
</html>
