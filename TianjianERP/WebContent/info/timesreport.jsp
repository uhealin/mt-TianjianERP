<%@ page language="java" contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8">


<title>知识首页</title>

<style type="text/css">
	
	.appTable tr{height:20px}
	.appTable tr td{padding: 5px;}

</style>

</head>

<style>

.datetime {
	color: #cccccc;
	font-size: 12px;
	padding-left: 5px;
}

.linkdiv{border-bottom:1px #DDDDEE dotted;padding:5px;}


</style>
<script type="text/javascript">
	
	Ext.onReady(function(){
		
		new Ext.Toolbar({
	   		renderTo: "divTool",
	   		height:27,
	           items:[
	             { 
	               text:'查询',
	               icon:'${pageContext.request.contextPath}/img/query.gif' ,
	               handler:function(){
	            	   queryWinFun();
	   			   }
	         	},'->']
		});
	 	
	}) ;

</script>

<body>	

	<div id=divTool></div>
 	<div id=divTable style="height:expression(document.body.clientHeight - 27);">
 		<table border="0" cellSpacing="1" cellPadding="5" width="80%" bgColor="#99BBE8" align="left" class="appTable">
			<tr bgColor="#DDE9F9">
				<td width="10%" align="center"><b>起时间</b></td>
				<td width="10%" align="center"><b>止时间</b></td>
				<td width="20%" align="center"><b>工作内容</b></td>
				<td width="30%" align="center"><b>参与项目</b></td>
				<td width="20%" align="center"><b>备注</b></td>
				<td width="10%" align="center"><b>用时</b></td>
			</tr>
			<tr bgColor="#ffffff">
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
			
			</tr>
			<tr bgColor="#ffffff">
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
			
			</tr>
			
		</table>
 	</div>
</body>
</html>
<script>

function showQuestion(id) {
	var tab = parent.tab ;
	var url = "${pageContext.request.contextPath}/question/View.jsp?chooseValue="+id ;
	if(tab && tab.id == "mainFrameTab"){
		try{
			n = parent.parent.tab.add({    
				'title':"问题库",  
				 closable:true,  //通过html载入目标页    
				 html:'<iframe scrolling="no" frameborder="0" width="100%" height="100%" src="' + url + '"></iframe>'   
			});    
			parent.parent.tab.setActiveTab(n);
		}catch(e){
			window.location = url ;
		}
	}else {
		window.location = url ;
	}
	
}

</script>