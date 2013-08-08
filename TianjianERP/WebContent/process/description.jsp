<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="../hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<title>流程zip包上传发布</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<script type="text/javascript">  

function ext_init(){
	
	 new Ext.Toolbar({
   		renderTo: "divBtn",
   		height:30,
   		defaults: {autoHeight: true,autoWidth:true},
           items:[{ 
               text:'发布',
               icon:'${pageContext.request.contextPath}/img/start.png' ,
               handler:function(){
   				  save();
   			   }
         	},'-',{ 
            text:'返回',
            icon:'${pageContext.request.contextPath}/img/back.gif',
            handler:function(){
            	window.history.back();
			}
      	},'->']
	});
	
} 

window.attachEvent('onload',ext_init);

</script>

</head>
<body >

<div id="divBtn" ></div>

<div id="applyList" style="height:expression(document.body.clientHeight-27);width:100%">

<form name="thisForm" method="post" action="" id="thisForm"  class="autoHeightForm" enctype="multipart/form-data">
<br><br>
	<table width="100%" border="0" cellpadding="10" cellspacing="10">
		<tr>
			<td nowrap="nowrap" align="right"  width="25%">
				流程文件：
			</td>
			<td>
				<input type="file" name="processfile" id="processfile" size="50" class="required" title="请输上传流程文件" class="required">
			</td>
		</tr>
		<tr style="margin:20 0px; height: 50px;">
			<td nowrap="nowrap" colspan="2" align="center" width="70%">
				<b>注：流程文件是后缀名为.zip的压缩包，里面包含了一张流程图片和一个后缀名为.xml的流程描述文件</b>
			</td>
		</tr>
	</table>
	<input type="hidden" id="processKey" name="processKey" value="${param.processKey}">
</form>
</div>

</body>
</html>

<script language="javascript">
	
	new Validation('thisForm');

	
	function save() {
		document.thisForm.action = "${pageContext.request.contextPath}/process.do?method=deploy" ;
		document.thisForm.submit();
	}
		

</script>