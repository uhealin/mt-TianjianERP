<%@page import="java.util.UUID"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp"%>

<%@ taglib uri="http://ckeditor.com" prefix="ckeditor" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title>公告信息</title>
<script type="text/javascript">
	window.onload=function(){
		document.getElementById('goDate').value=getDate();
	}
	Ext.onReady(function() {
		
		// var IframeID=document.getElementById("oblog_Composition").contentWindow;
		// IframeID.document.body.innerHTML = document.getElementById("content").value;
		
		new Ext.Toolbar({
			renderTo : "divBtn",
			height : 30,
			defaults : {
				autoHeight : true,
				autoWidth : true
			},
			items : [ {
				id : 'saveBtn',
				text : '保存',
				icon : '${pageContext.request.contextPath}/img/save.gif',
				handler : function() {
					checkUp();
				    if(!check_Date()){
				    	return;
				    }
					mySubmit();
				}
			}, '-', {
				text : '返回',
				icon : '${pageContext.request.contextPath}/img/back.gif',
				handler : function() {
					window.history.back();
				}
			}, '->' ]
		});

	});
//获得系统当前的时间	
function getDate(){
	var d;
	var s='';
    d = new Date();                           
    s +=  d.getYear() + "-";   
    var moth=d.getMonth() + 1;
    if(moth<10){
        s += "0"+moth+ "-";    
    }else{
      	s += moth + "-";
    }
    var date=d.getDate();
    if(date<10){
      	s += "0"+date;  
    } else{
        s += date;  
    }                      
    return(s);                               
}
</script>
<script type="text/javascript">
	function $d(id){
		return document.getElementById(id);
	}
	function checkUp(){
		var flg=$d('up_id').checked;
		if(!flg){
			$d('upDates_id').value='-1';
		}
	}
	
</script>
<script type="text/javascript">
	function dateParse_xl(dateStr){
		var date=new Date();
		var ds=dateStr.split('-');
		date.setFullYear(ds[0]);
		date.setMonth(ds[1]);
		date.setDate(ds[2]);
		return date;
	}
	function check_Date(){
		var d1=$d('goDate_id').value;
		var d2=$d('endGoDate_id').value;
		if(d2 != ''){
			var date_1=dateParse_xl(d1);
			var date_2=dateParse_xl(d2);
			var i=date_2.getTime() -date_1.getTime() ;
			if(i>0){
				document.getElementById('endGoDate_error').innerHTML="<font color='#0000FF'>为空,手动终止!</font>";
				return true;
			}else{
				document.getElementById('endGoDate_error').innerHTML="<br><font color='red'>不能小于开始日期</font>";
				return false;
			}
		}else{
			document.getElementById('endGoDate_error').innerHTML="<font color='#0000FF'>为空,手动终止!</font>";
			return true;
		}
		
	}
</script>
<script type="text/javascript" src="baiduUeditor/editor_all.js"></script>
<script type="text/javascript" src="baiduUeditor/editor_config.js"></script>
<link rel="stylesheet" href="baiduUeditor/themes/default/ueditor.css"/>
<style type="text/css">

.before{
	border: 0px;
	background-color: #FFFFFF !important;
}

.data_tb {
	background-color: #ffffff;
	text-align:center;
	margin:0 0px;
	width:85%;
	border:#8db2e3 1px solid; 
	BORDER-COLLAPSE: collapse; 
	margin-top: 20px;
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
	width:10%;
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
	TEXT-ALIGN: left; 
	WORD-WRAP: break-word
}

</style>

</head>
<body style="overflow: hidden;">
<div style="overflow: auto;height: 98%" >
<div id="divBtn"></div>
<form name="thisForm" method="post" action="" id="thisForm" >
<input type="hidden" id="uuid" name="uuid" value="${proclamation.uuid }" />
<input type="hidden" id="opt" name="opt" value="${opt}" />
<input type="hidden" id="taskId" name="taskId" value="${taskId}" />

<table width="100%" class="data_tb" align="center">
	<tr>
		<td class="data_tb_alignright" align="right" width="15%"><font color="red" size=3>*</font>标题：</td>
		<td class="data_tb_content"style="paddingpadding-left: 50px;"><input id="title" type="text" class="required" maxlength="90"
			name="title" title="请输入，不能为空！" size="85"  value="${proclamation.title}" /></td>
	</tr>
	<tr height="20">
				  <td class="data_tb_alignright" align="right"><font color="red" size=3>*</font>分类：</td>
			      <td  align="left" class="data_tb_content">
			      	<input name="ctype" id="ctype" title="请输入标题" class='required' value="${proclamation.ctype }"
			      	 	noinput=true
			      	 	size="68"
					   title="请输入有效的值"
					   onkeydown="onKeyDownEvent();"
					   onkeyup="onKeyUpEvent();"
					   onclick="onPopDivClick(this);"
					   onchange="setProjectName();"
					   autoid=853
			      	 />
				  </td>
	</tr>
	
	<tr>
		<td class="data_tb_alignright" align="right" width="15%">生效日期：</td>
		<td class="data_tb_content"   style="paddingpadding-left: 50px;">
			<table>
				<tr>
					<td><input id="goDate_id" name="goDate" type="text" class="required" value="${proclamation.goDate}" maxlength="90"/></td>
					<td>至</td>
					<td><input id="endGoDate_id" onblur="check_Date()" name="endGoDate" value="${proclamation.endGoDate}" type="text"  maxlength="90"/></td>
					<td><span id="endGoDate_error">
						<font color="#0000FF">为空,手动终止!</font>
						</span>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	
	<tr>
			<td align="right"  style="vertical-align: top;margin-top: 30px;" class="data_tb_alignright">附件：</td>
			<td class="data_tb_content">
				<input type="hidden" id="fileName" name="fileName" value="${proclamation.fileName}">
				<div style="vertical-align: middle;width: 100%;margin-top: 10px;">
				<script type="text/javascript">
				if("${proclamation.fileName}"==""){
					<%
							String fileName=UUID.randomUUID().toString();  //生成uuid
					%>
					document.getElementById("fileName").value="<%=fileName%>";
					attachInit('proclamation','<%=fileName%>');					
				}else{
					attachInit('proclamation','${proclamation.fileName}');
				}
				</script>
				</div>
			</td>
		</tr>
		<tr>
			<td width="30%" class="data_tb_alignright"  align="right">置顶：</td>
			<c:choose>
				<c:when test="${proclamation.up=='置顶'}">
					<td align="left"><input type="checkbox" id="up_id" name="up" checked="checked" value="置顶">使公告通知置顶，显示为重要&nbsp;&nbsp;
					<input type="text" style="width: 24px;" id="upDates_id" name="upDates" width="10"  value="${proclamation.upDates}">天后结束置顶，0表示一直置顶</td>
				</c:when>
				<c:otherwise>
					<td align="left"><input type="checkbox" id="up_id" name="up"  value="置顶">使公告通知置顶，显示为重要&nbsp;&nbsp;
					<input type="text" style="width: 24px;" id="upDates_id" name="upDates" width="10"  value="0">天后结束置顶，0表示一直置顶</td>
				</c:otherwise>
			</c:choose>
		</tr>
	<tr>
		<td align="right" width="15%" class="data_tb_alignright">
				<div><font color="red" size=3>*</font>内容：</div>
		</td>
		<td   class="data_tb_content">
		      <div style="height:280; overflow: auto; width: 99%;overflow-x:hidden;" > 
				<textarea  id="content" name="content"  style="width: 100%" >${proclamation.content}</textarea>
				</div><br>
		</td>
	</tr>
	
</table>
	<br>
	<br>
 </form>
 
 <c:forEach items="${nodeList}" var="node">
		<table border="0" cellSpacing="0" cellPadding="0" width="98%"
			align="center">
			<tr>
				<td width="100%" align="middle"><img
					src="${pageContext.request.contextPath}/images/downline.jpg"></td>
			</tr>
		</table>
		<table border="0" cellSpacing="1" cellPadding="2" width="95%"
			bgColor="#99BBE8" align="center" class="appTable">
			<tr bgColor="#DDE9F9">
				<td colSpan="2" style="height: 25px;"><b><span style="width: 30%;margin-left: 10px;">${node.nodeName}</span>
				<span style="width: 15%;">处理人：${node.dealUserId}</span> <span
					style="width: 25%;">处理时间：${node.dealTime}</span> </b></td>
			</tr>
			<c:forEach items="${node.formList}" var="form">
	
				<tr bgColor="#ffffff" style="height: 20px;">
					<td width="20%" align="right">${form.key}：</td>
					<td width="80%" style="padding-left: 5px;"><c:choose>
						<c:when test="${form.property != ''}">
							<a href=# onclick="fileOpen('${form.property}','${form.value}')">${form.value}
							</a>
						</c:when>
						<c:otherwise>
										${form.value}
									</c:otherwise>
					</c:choose></td>
				</tr>
			</c:forEach>
		</table>
	</c:forEach>
	<br>
</div>

<script type="text/javascript">
var editor;

	new Validation('thisForm');
	function mySubmit() {
		if (!formSubmitCheck('thisForm'))return ;
		if(!editor.hasContents()){ //判断是否填写了内容
			alert("请编辑内容！");
			return ;
		}    
		if (document.getElementById("uuid").value != "") {
			document.thisForm.action = "${pageContext.request.contextPath}/proclamationSy.do?method=update";
		} else {
			document.thisForm.action = "${pageContext.request.contextPath}/proclamationSy.do?method=add";
		}
		if(editor.hasContents()){  //提交条件满足时提交内容
		    editor.sync();           //此处的editor是页面实例化出来的编辑器对象
			document.thisForm.submit();
		}

	}

//得到innerHTML置内容
function getContent(){
       var arr = [];
       arr.push("使用editor.getContent()方法可以获得编辑器的内容");
       arr.push("内容为：");
       arr.push(editor.getContent());
       alert(arr.join("\n"));
   }
//设置内容	
function setContent(){
    var arr = [];
    arr.push("使用editor.setContent('欢迎使用ueditor')方法可以设置编辑器的内容");
    editor.setContent('欢迎使用ueditor');
    alert(arr.join("\n"));
}
//得到当前选中的innerText置内容
function getText(){
    //当你点击按钮时编辑区域已经失去了焦点，如果直接用getText将不会得到内容，所以要在选回来，然后取得内容
    var range = editor.selection.getRange();
    range.select();
    var txt = editor.selection.getText();
    alert(txt);
}
//得到innerText置内容
function getContentTxt(){
    var arr = [];
    arr.push("使用editor.getContentTxt()方法可以获得编辑器的纯文本内容");
    arr.push("编辑器的纯文本内容为：");
    arr.push(editor.getContentTxt());
    alert(arr.join("\n"));
}
//判断是否填写内容
function hasContent(){
    var arr = [];
    arr.push("使用editor.hasContents()方法判断编辑器里是否有内容");
    arr.push("判断结果为：");
    arr.push(editor.hasContents());
    alert(arr.join("\n"));
}

//把光标移入编辑框
function setFocus(){
    editor.focus();
    
}


try{
	editor = new baidu.editor.ui.Editor({
		textarea:'content',
		elementPathEnabled : false, //隐藏body
		wordCount:false,  //隐藏字符统计
		autoFloatEnabled: false 
	});
	editor.render("content");
}catch(e){
	
}
// 生效时间
new Ext.form.DateField({			
	applyTo : 'goDate',
	width: 133,
	format: 'Y-m-d'	
});
// 失效时间
new Ext.form.DateField({			
	applyTo : 'endGoDate',
	width: 133,
	format: 'Y-m-d'	
});
</script>

</body>
</html>
