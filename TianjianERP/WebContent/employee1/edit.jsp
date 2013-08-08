<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<%@ taglib uri="http://ckeditor.com" prefix="ckeditor" %>

<script type="text/javascript" src="${pageContext.request.contextPath}/baiduUeditor/editor_all.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/baiduUeditor/editor_config.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/baiduUeditor/themes/default/ueditor.css"/>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title></title>


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
        
    }
    window.attachEvent('onload',ext_init);
</script>

</head>
<body >
<form name="thisForm" action="${pageContext.request.contextPath}/employeeedit1.do?method=addSave" class="autoHeightForm" method="post" >
<div id="divBtn"></div>
<input name="uuid" type="hidden" id="uuid" value="${employeeedit1.uuid}" />

 <div style="overflow: auto; width: 100%;overflow-x:hidden;" > 		
                        <script type="text/plain" id="editor" name="content"></script>		  		
				    	<textarea  style="width: 100%;"  id="content" >${employeeedit1.content}</textarea>
					  </div>
				

</form>

</body>

<script type="text/javascript">

new Validation('thisForm');

var editor ;
//编辑器
try{
	editor = new baidu.editor.ui.Editor({

		textarea:'content',
		elementPathEnabled : false, //隐藏body
		wordCount:false,  //隐藏字符统计
		autoFloatEnabled: false 
	});
	editor.render("content");
}catch (e){
	
}


//保存
function setBargainType(){
	
	//var autoId = document.getElementById("uuid").value;
		//document.forms["thisForm"].action="${pageContext.request.contextPath}/employeeedit.do?method=addSave";
		editor.sync();
		document.forms["thisForm"].submit();
	}


</script>
</html>
