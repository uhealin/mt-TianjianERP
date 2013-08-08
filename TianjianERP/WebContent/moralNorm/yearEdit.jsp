<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>	
<%@ taglib uri="http://ckeditor.com" prefix="ckeditor" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>年度独立性声明</title>
<script type="text/javascript" src="baiduUeditor/editor_all.js"></script>
<script type="text/javascript" src="baiduUeditor/editor_config.js"></script>
<link rel="stylesheet" href="baiduUeditor/themes/default/ueditor.css"/>
<script type="text/javascript">
var tbar_customer ;
Ext.onReady(function(){
	
	tbar_customer = new Ext.Toolbar({
		renderTo:'div_btn',
           items:[          
           {
            text:'保存',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/add.gif',
            handler:function(){
            	save();
			}
      	},'-',{
	            text:'关闭',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/close.gif',
	            handler:function () {
	            	closeWin();
	            }
	     }
        ]
        });  

});

function savePrompt(){
	var prompt=document.getElementById("result").value;
	if(prompt=="success"){
		alert("保存成功");
	}
}

window.onload=savePrompt;
</script>
</head>
<body>
<div id="div_btn"></div>
<input type="hidden" id="result" name="result" value="${prompt}"/>
<form id="thisFrm" method="post" name="thisFrm" >
<div style="overflow: auto;margin-left:150px; margin-right:150px;height:430px;overflow-x:hidden;text-align: center;" align="center">
<div style="overflow: scroll;height:400px; overflow-x:hidden;">
<textarea style="text-align: center;" name="content" id="content">
<c:choose>
	<c:when test="${morality.content !=null}">
		${morality.content}
	</c:when>
	<c:otherwise>
		请输入内容
	</c:otherwise>
</c:choose>
</textarea>

</div>
</div>
</form>
</body>
<script type="text/javascript">
var editor;
function save(){
	var form =document.getElementById("thisFrm");
	form.action="${pageContext.request.contextPath}/declare.do?method=saveYear";
	editor.sync();           //此处的editor是页面实例化出来的编辑器对象
	if(!editor.hasContents()){ //判断是否填写了内容
		alert("请编辑内容！");
		return ;
	}
	form.submit();
}

function closeWin(){
	closeTab(parent.tab);
}
</script>
<script type="text/javascript">
	editor = new baidu.editor.ui.Editor({
		textarea:'content',
		elementPathEnabled : false, //隐藏body
		wordCount:false,  //隐藏字符统计
		autoFloatEnabled: false 
		});
	editor.render("content");
</script>
</html>