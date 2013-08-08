<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; utf-8">
<title>学时管理</title>
<script>
   Ext.onReady(function(){
     new Ext.form.DateField({			
		applyTo: 'beginTime',
		width: 133,
		format: 'Y-m-d'	
});

    new Ext.form.DateField({			
		applyTo: 'endTime',
		width: 133,
		format: 'Y-m-d'	
});

      var btar = new Ext.Toolbar({
	  
              renderTo:'btndiv',
              
              items:[{
                 text:'查询',
                 cls:'x-btn-text-icon',
                 icon:'${pageContext.request.contextPath}/img/query.gif',
                 handler:queryWinFun
              },'-',{
                 text:'修改',
                 cls:'x-btn-text-icon',
                 icon:'${pageContext.request.contextPath}/img/edit.gif',
                 handler:goEdit
              },'-',{
                 text:'批量导入',
                 cls:'x-btn-text-icon',
                 icon:'${pageContext.request.contextPath}/img/import.gif',
                 handler:function () {
			           	goJoin();
			      }
			   },'-',{
			     text:'删除',
			     cls:'x-btn-text-icon',
                 icon:'${pageContext.request.contextPath}/img/delete.gif',
                 handler:godelete
           }]  
      });
      
      //人员查询
	var queryWin = null;
	function queryWinFun(id){
		if(!queryWin) { 
			new BlockDiv().show();
			var searchDiv = document.getElementById("search") ;
			searchDiv.style.display="block";
		    queryWin = new Ext.Window({
				title: '学时查询',
		     	contentEl :'search',
		     	width: 300,
		     	height:225,
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
	          		handler:function (){
	          		goSearch1();
	          		}
	          		//handler:goView
	        	},{
	            	text:'取消',
	            	handler:function(){
		            	goClean_CH_education();
	               		queryWin.hide();
	            	}
	        	}]
		    });
	    }
	    queryWin.show();
	}
   });

</script>
</head>
<body>

<div id="btndiv"></div>

<div style="height:expression(document.body.clientHeight-29);width:100%">

<mt:DataGridPrintByBean name="timelist"  message="请选择单位编号" />
</div>
<div id="search" style="display:none">
<br/>
<div style="margin:0 20 0 20">请在下面输入查询条件：</div>
<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
<form name="thisForm" method="post" action="" id="thisForm"  >
	<table border="0" cellpadding="0" cellspacing="0" width="100%" bgcolor="">
		<tr align="center">
			
			<td align="right">学员姓名：</td>
			<td align=left>
				<input type="text" name="username" id="username"> 
			</td>
		</tr>
		<tr align="center">
			
			<td align="right">学时形式：</td>
			<td align=left>
				<input type="text" name="hoursType" id="hoursType" >
			</td>
		</tr>
		<tr align="center">
			
			<td align="right">培训日期：</td>
			<td align=left>
			<input name="beginTime" id="beginTime" type="text" >
			</td>
		</tr>
		<tr align="center">
			
			<td align="right">&nbsp;</td>
			<td align=left>
			<input name="endTime" id="endTime" type="text" >
			</td>
		</tr>
	</table>
		</form>
</div>

</body>
<script>
   /*批量导入*/
function goJoin(){
	window.location="${pageContext.request.contextPath}/educationTime.do?method=Upload";
}
	function goSearch1() {
 		var flag = false;
		var strW = "";
		if(document.getElementById("username").value != "") {
	
			flag = true;
		}
		if(document.getElementById("hoursType").value != "") {
	
			flag = true;
		}
		if(document.getElementById("beginTime").value != "") {
	
			flag = true;
		}
		if(document.getElementById("endTime").value != "") {
	
			flag = true;
		}
		if(flag) {
			//goSearch_user();
			document.thisForm.action="${pageContext.request.contextPath}/educationTime.do";
			document.thisForm.submit();
		} else {
			alert("至少选一项查询条件！");
		}
}
   function goClean_CH_education() {
	window.location="${pageContext.request.contextPath}/educationTime.do";
}
  /*修改*/
function goEdit()
{
	if(document.getElementById("chooseValue_timelist").value=="")
	{
		alert("请选择要修改的学时！");
	}
	else
	{
			window.location="${pageContext.request.contextPath}/educationTime.do?method=timeEdit&id="+document.getElementById("chooseValue_timelist").value;
	}
}
/*删除*/
 function godelete(){
    if(chooseValue_timelist.value==""){
		alert("请选择要删除的学时！");
		return; 
	}else{
    	var str = "您确认要删除该学时吗?";
        if(confirm(str,"提示")){
			var oBao = new ActiveXObject("Microsoft.XMLHTTP");
			var url="educationTime.do?method=Remove&id="+chooseValue_timelist.value+"&random="+Math.random();
	
			oBao.open("POST", url, false);
			oBao.send();
			var strResult = unescape(oBao.responseText);
		 	alert(strResult);
		  	goClean_CH_education();
	  }
   }
 }
</script>
</html>