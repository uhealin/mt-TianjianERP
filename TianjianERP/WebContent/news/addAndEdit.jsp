<%@page import="java.util.UUID"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>新闻管理</title>
<script type="text/javascript" src="baiduUeditor/editor_all.js"></script>
<script type="text/javascript" src="baiduUeditor/editor_config.js"></script>
<link rel="stylesheet" href="baiduUeditor/themes/default/ueditor.css"/>
<style>
	#t {
		{border-collapse:collapse;border:none;};
	}
	
	#t td{
		{border:solid #6595d6 1px;};
	}
	
	
</style>
<script Language=JavaScript>

	function ext_init(){

		mt_form_initDateSelect();
	    var tbar = new Ext.Toolbar({
	   		renderTo: "divBtn",
	   		defaults: {autoHeight: true,autoWidth:true},
            items:[{
	            text:'保存',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/save.gif',
	            handler:function(){
	            	return setBargainType();
				}  
       		},'-',{
	            text:'返回',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/back.gif',
	            handler:function(){
					window.history.back();
				}
       		},'->'
			]
        });
	    attachInit('attachmentId');	
    }
    window.attachEvent('onload',ext_init);
</script>

</head>
<body >

		
<div id="divBtn" style="top: 0px;left: 0px;" ></div>
<input type="hidden" id="opt" name="opt" value="<%=request.getParameter("opt") %>">

	<form name="thisForm" action="${pageContext.request.contextPath}/news.do" class="autoHeightForm" style="height: 90%" method="post">
		<input name="autoId" type="hidden" id="autoId" value="${autoId}"/>
		<!--  <input name="attachmentId" type="hidden" id="attachmentId"/>-->
		<input name="fileName" type="hidden" id="fileName" value="${fileName}" />
		<input name="filePath" type="hidden" id="filePath" value="${filePath}" />
		<input type="hidden" name="publishUserId" name="publishUserId" value="${userSession.userId }" />
		<input name="big_type" type="hidden" id="big_type" value="<%=request.getParameter("opt") %>"/>
		<input name="menuid" type="hidden" value="${param.menuid }" /> 
        <input name="hasclass" type="hidden" value="${param.hasclass }" />
	<div style="overflow:auto;width: 98%;position: relative;left: 10px;top: 10px;" >
		<table id="t" width="100%" border="1" align="center" cellpadding="2" cellspacing="1"  >
			<tr height="20">
				  <td height="23" align="center" bgColor="#EEEEEE" width="10%"><strong>新闻标题：</strong></td>
			      <td height="23" align="left"><input name="title" id="title" title="请输入标题" class='required' value="${news.title }" style="width: 580px;border: 1px solid gray" />
				  </td>
				  
			</tr>
			<tr height="20">
				  <td height="23" align="center" bgColor="#EEEEEE" width="10%"><strong>新闻子标题：</strong></td>
			      <td height="23" align="left"><input name="sub_title" id="sub_title" title="请输入子标题"  value="${news.sub_title }" style="width: 580px;border: 1px solid gray" />
				  </td>
				  
			</tr>
						<tr height="20">
				  <td height="23" align="center" bgColor="#EEEEEE" width="10%"><strong>文号：</strong></td>
			      <td height="23" align="left"><input name="doc_no" id="doc_no" title="请输入文号"  value="${news.doc_no }" style="width: 580px;border: 1px solid gray" />
				  </td>
				  
			</tr>
			<tr>
			<td height="23" align="center" bgColor="#EEEEEE"><strong>上传时间：</strong></td><td>
				       <input type="text" name="updateTime" id="updateTime" ext_id="updateTime" ext_name="updateTime" ext_type="date" value="${news.
	updateTime}">
				  </td>
			</tr>
			<c:if test="${b }" >
			<tr style="display: none;">
				<td  height="23" align="center" bgColor="#EEEEEE"><strong>所属区域：</strong></td>
				<td><input type="text" id="dept_type" name="dept_type" autoid="30017" onkeydown="onKeyDownEvent();" 
	      				   onkeyup="onKeyUpEvent();" 
	      				   onclick="onPopDivClick(this);" 
	      				   value="${news.dept_type}"
				></td>
			</tr>
			</c:if>
			<c:if test="!${b }" >
			<tr style="display: none;">
				<td  height="23" align="center" bgColor="#EEEEEE"><strong>所属区域：</strong></td>
				<td><input type="text" id="dept_type" name="dept_type" autoid="30017" onkeydown="onKeyDownEvent();" 
	      				   onkeyup="onKeyUpEvent();" 
	      				   onclick="onPopDivClick(this);" 
	      				   value="分所"
				></td>
			</tr>
			</c:if>
			<tr>
				<td height="23" align="center" bgColor="#EEEEEE"><strong>附件上传：</strong></td>
				<td  align="left" >
				  <input type="hidden" id="attachmentId" name="attachmentId" value="${news.attachmentId}" >
			    </td>
		   
			</tr>
	
	        <c:if test="${param.hasclass == 'true' }">
			
			<tr>
				<td height="23" align="center" bgColor="#EEEEEE"><strong>请选择分类：</strong></td>
				<td>
					<input type="text" id="type" name="type" title="分类" 
						   maxlength="20" size="20" autoWidth="210"
	      				   onkeydown="onKeyDownEvent();" 
	      				   onkeyup="onKeyUpEvent();" 
	      				   onclick="onPopDivClick(this);" 
	      				   autoid=700
	      				   refer="公告类型" 
						   value="${news.type}" class="required" />
				</td>
			</tr>
			</c:if>
			<tr height=18>
				  <td height="280" align="center" bgColor="#EEEEEE"><strong>新闻内容：</strong></td>
				  <td align="left" >
				  	<div style="height:280; overflow: auto; width: 99%;overflow-x:hidden;" > 				  		
					    <textarea  style="width: 100%;" name="contents" id="contents" >${news.contents }</textarea>					    
					 <!--   <ckeditor:replace replace="contents" basePath="/ckeditor/" />-->
					  </div><br>
				  	<font color="red" >( 注：请不要直接从 office 办公软件黏贴信息过来,可以从记事本黏贴信息过来。)</font> 
				</td>
		 	 </tr>
			<tr style="display: none">
				<td height="23" align="center" bgColor="#EEEEEE"><strong>备注：</strong></td>
				<td height="23" align="left"><input name="memo" id="memo" type="text" style="border: 1px solid red" size="30" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" maxlength="50"  onClick="onPopDivClick(this);" valuemustexist=true autoid=371>
			    </td>
		   
			</tr>
	
		</table>
	</div>

	</form>

</body>


<script>
var editor;
new Validation("thisForm");


function setBargainType(){
	var title = document.getElementById("title").value;

	if(title=="" || title==null){
		alert("标题不能为空!");
		document.getElementById("title").select();
		return;
	}
	if(!editor.hasContents()){ //判断是否填写了内容
		alert("请编辑内容！");
		return ;
	}  
	
	var autoId = document.getElementById("autoId").value;

    if(autoId !=""){
    
       document.thisForm.action="${pageContext.request.contextPath}/news.do?method=updateSave&opt=<%=request.getParameter("opt")%>";
    }else{
    
		document.thisForm.action="${pageContext.request.contextPath}/news.do?method=addSave&opt=<%=request.getParameter("opt")%>";
	}
    	editor.sync();           //此处的editor是页面实例化出来的编辑器对象
		document.thisForm.submit();
}

//下载 
function down(fileName,filePath){
	document.getElementById("fileName").value = fileName;
	document.getElementById("filePath").value = filePath;
	document.thisForm.action = "${pageContext.request.contextPath}/news.do?method=download";
	document.thisForm.submit();
}

// 删除附件
function deleteFile(fileName,filePath,unid){
	if(confirm("您确定要删除附件吗?")){
		var filePath =  filePath;
		var attachmentId =  document.getElementById("attachmentId").value;
		var oBao = new ActiveXObject("Microsoft.XMLHTTP");
		oBao.open("POST","${pageContext.request.contextPath}/news.do?method=deleteFile&filePath="+filePath+"&attachmentId="+unid,false);
		oBao.send();
		var result = oBao.responseText;
		if(result=="Y"){
			alert("删除成功!");
			document.getElementById(unid).style.display="none";
			
		}else if(result=="N"){
			alert("删除失败!");
		}else{
			alert("文件不存在!");
		}
	}
}
try{
	editor = new baidu.editor.ui.Editor({
		textarea:'contents',
		elementPathEnabled : false, //隐藏body
		wordCount:false,  //隐藏字符统计
		autoFloatEnabled: false 
	});
	editor.render("contents");
}catch(e){
	
}
</script>


</html>
