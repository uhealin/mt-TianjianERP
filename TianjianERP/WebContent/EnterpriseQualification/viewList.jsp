<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<html>
<head> 
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>图片浏览</title>
<style>

.before{
	border: 0px;
	background-color: #FFFFFF !important;
}

.data_tb {
	background-color: #ffffff;
	text-align:center;
	margin:0 0px;
	width:80%;
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
	font-size: 13px;
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
	padding:5px;
	WORD-WRAP: break-word
}

</style>
<script type="text/javascript">
var tbar_project;
var tab;

function extInit(){
	

	tbar_project = new Ext.Toolbar({
		renderTo: "divBtn",
		items:[
			{ 
				text:'关闭',
				cls:'x-btn-text-icon',
				icon:'${pageContext.request.contextPath}/img/close.gif',
				handler:function(){
					//window.history.back();
					closeTab(parent.tab);
				}
	   		},'->'
   		]
    });
	<c:if test="${flag == 'file'}">     	
	new ExtButtonPanel({
		desc:'',
		renderTo:'sbtBtn',
		columns:1,
		colspan:1, 
		items:[{
        	text: '返回上一页',
        	id:'appSubmit24', 
        	icon:'${pageContext.request.contextPath}/img/back_32.png' ,
        	scale: 'large',
          	handler:function(){
          		window.history.back();
	   		}
       	}]  
	});	
	</c:if> 
}


</script>
</head>
<body>
<div id="divBtn" ></div>
<form name="thisForm" method="post" action="" id="thisForm" >
<div style="height:expression(document.body.clientHeight-23);overflow: auto;" >
<span class="formTitle" >图片浏览<br/><br/> </span>
<br>

<table  cellpadding="8" cellspacing="0" align="center" class="data_tb" >
<tr>
  <td class="data_tb_alignright" align="center" colspan="6">图片目录列表</td>
</tr>
<tr>

<c:choose>
<c:when test="${flag != 'file'}">

	<c:forEach var="map" items="${mapList}" varStatus="status">
	<c:choose>
	<c:when test="${status.count % 6 == 1 }">
	</tr>
	<tr>
		<td class="data_tb_content" align="center" width="17%"><a href="javascript:void(0);" onclick="getImg('${flag}','${map.attachfileid}');"  /><img src="${pageContext.request.contextPath}/img/folder.gif" width="80" height="80" border="0"><br>${map.title}</a></td>
	</c:when>
	<c:otherwise>
		<td class="data_tb_content" align="center" width="17%"><a href="javascript:void(0);" onclick="getImg('${flag}','${map.attachfileid}');"  /><img src="${pageContext.request.contextPath}/img/folder.gif" width="80" height="80" border="0"><br>${map.title}</a></td>
	</c:otherwise>
	</c:choose>
	</c:forEach>

</c:when>
<c:otherwise>

	<c:forEach var="map" items="${mapList}" varStatus="status">
	<c:choose>
	<c:when test="${status.count % 6 == 1 }">
	</tr>
	<tr>
		<td class="data_tb_content" align="center" width="17%"><a href="javascript:void(0);" onclick="getImg('${flag}','${map.attachid}','${map.indexid }');"  /><img src="${pageContext.request.contextPath}/common.do?method=attachDownload&op=1&attachId=${map.attachid}" onerror="this.src='${pageContext.request.contextPath}/images/noView.jpg'" title="${map.tilte }" id="${map.attachid}" width="80" height="80" border="0"><br>${map.attachname}</a></td>
	</c:when>
	<c:otherwise>
		<td class="data_tb_content" align="center" width="17%"><a href="javascript:void(0);" onclick="getImg('${flag}','${map.attachid}','${map.indexid }');"  /><img src="${pageContext.request.contextPath}/common.do?method=attachDownload&op=1&attachId=${map.attachid}" onerror="this.src='${pageContext.request.contextPath}/images/noView.jpg'" title="${map.tilte }" id="${map.attachid}" width="80" height="80" border="0"><br>${map.attachname}</a></td>
	</c:otherwise>
	</c:choose>
	</c:forEach>

</c:otherwise>
</c:choose>   	       

<c:if test="${lineLength < 6 }">
<c:forEach   begin='1' end='${lineLength}'>
	<td class="data_tb_content" align=center width=ceil(100/6)%>&nbsp;</td>
</c:forEach>
</c:if>

</tr>
</table>

<input type="hidden" id="attachfileid" name="attachfileid">
<input type="hidden" id="flag" name="flag">

<center><div id="sbtBtn" ></div></center>
</div>
</form>
</body>

<script type="text/javascript">
//alert(${lineLength});
function getImg(flag,attachfileid,indexid){
	//flag=file 就表示要打开附件
	//alert(flag+"|"+attachfileid+"|"+indexid)
	
	document.getElementById("attachfileid").value = attachfileid;
	document.getElementById("flag").value = flag;
	
	if(flag == "file"){
		
		var imgId = document.getElementById(attachfileid);
		if(imgId.src.indexOf("${pageContext.request.contextPath}/images/noView.jpg")>-1){
			alert("该文件不是图片，请重新选择！");
			return;
		}
		
		var url = "imageBrowser/view.jsp?indexId="+indexid+"&defaultImageId="+attachfileid;
		try{
			parent.openTab("attachfileId","企业资质浏览",url);	
		}catch(e){
			//alert(url);
			window.open = url;
		}
		
	}else{
		thisForm.action = "${pageContext.request.contextPath}/enterpriseQualification.do?method=viewList&rand="+Math.random();
		thisForm.submit();
	}
	
	
}



//ext初始化
Ext.onReady(extInit);

	 
</script>
</html>