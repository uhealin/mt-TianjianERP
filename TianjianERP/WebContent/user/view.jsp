<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>视图</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/baiduUeditor/editor_all.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/baiduUeditor/editor_config.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/baiduUeditor/themes/default/ueditor.css"/>

<script Language=JavaScript>

    
    var editor;
	function ext_init(){
	editor.render("contents");
	    var tbar = new Ext.Toolbar({
	   		renderTo: "divBtn",
	   		defaults: {autoHeight: true,autoWidth:true},
            items:[{
            	text:'保存',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/save.gif',
	            handler:function () {
	            if (!formSubmitCheck('thisForm'))return;
            		save();
	            }
       		},{
            	text:'关闭',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/close.gif',
	            handler:function () {
            		closeTab(parent.tab);
	            }
       		},{
            	text:'返回',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/back.gif',
	            handler:function () {
            		window.history.back();
	            }
       		}
			]
        });
        
    }
    
    
	editor = new baidu.editor.ui.Editor({
		textarea:'contents',
		elementPathEnabled : false, //隐藏body
		wordCount:false,  //隐藏字符统计
		autoFloatEnabled: false 
	});
    
    window.attachEvent('onload',ext_init);
</script>

</head>
<body leftmargin="0" topmargin="0" >

<div style="overflow: scroll;height: 100%">
<div id="divBtn"></div>
	<form name="thisForm" action="" method="post">
		<table width="100%" border="0" bgcolor="#6595d6" >
		<input name="autoid" type="hidden" id="autoid" value="${resum.autoid}">
			 <tr align="center">
		     	 <td  align="center">
		      		招聘标题：<input name="title" id="title" value="${resum.title}"/>
	  					<select id="type" name="type">
	  						<option value="应届生招聘" <c:if test="${resum.type == '应届生招聘'}">selected</c:if> >应届生招聘</option>
	  						<option value="社会招聘" <c:if test="${resum.type == '社会招聘'}">selected</c:if> >社会招聘</option>
	  					</select>
	 			  </td>
		     </tr>
		     <tr>
		     	<td  style="100%"> 
  					   <script type="text/plain" id="editor" name="contents" ></script>
				    	<textarea name="contents" id="contents"  style="width: 100%;" >${resum.content}</textarea>
					 
				 </td>
		     </td>
	    </tr>
	    </table>
	 </form>
	 </div>
</body>
<script>
new Validation("thisForm");
 
function save(){
    editor.sync();
    var autoid = document.getElementById("autoid").value;
    if(autoid ==""){
    	thisForm.action = "${pageContext.request.contextPath}/resum.do?method=save";
    }else{
    	
    	thisForm.action = "${pageContext.request.contextPath}/resum.do?method=update&autoid=" + autoid;
    }
	document.forms["thisForm"].submit();
}
  
</script>
</html>
