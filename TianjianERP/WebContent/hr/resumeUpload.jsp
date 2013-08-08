<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"  errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>导入EXCEL表</title>

<style>
.mySpan {
	color:#FF6600; 
	font-family:"宋体";
	font: normal;
	font-size: 9pt;
	padding: 0px;
	margin: 0px;
}

input {
	border: 1px solid #AEC9D3;
}

legend {
	color: #006699;
}

body {
	background-image:url("${pageContext.request.contextPath}/images/new_bg.gif");
	overflow:hidden ;
	
}
</style>

<script>

function ext_init(){

	var tbar_customer = new Ext.Toolbar({
		renderTo: "divBtn",
		defaults: {autoHeight: true,autoWidth:true},
 		items:[{
			text:'确定',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/save.gif',
            handler:function () {
				checkit();
			}
      	},'-',{
            text:'返回',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/back.gif',
            handler:function(){
				window.history.back();
			}
      	},'-',new Ext.Toolbar.Fill()]
	});
        
}


//响应提交前的检查
function checkit(){
	    var f1=thisForm.image.value;
	    if (f1==""){
	    	//上传文件不得为空
	    	alert("上传文件不得为空");
	    	return false;
	    }
	    if(f1.toLowerCase().indexOf(".xls")>-1){
	    }else{
	    	alert("提供的文件必须是excel文档!");
	    	return false;
	    }
	    showWaiting();
	    var wantjob = document.getElementById("wantjob").value;
	    document.thisForm.action="${pageContext.request.contextPath}/resume.do?method=doUpLoad&wantjob="+wantjob;
	    document.thisForm.submit() ;
	}
</script>

</head>
<body leftmargin="0" topmargin="0">
<div id="divBtn"></div>

<form name="thisForm" method="post" " id="thisForm" enctype="multipart/form-data" class="autoHeightForm"><!-- enctype="multipart/form-data" -->

<span class="formTitle" >
EXCEL&nbsp;简&nbsp;历&nbsp;基&nbsp;本&nbsp;信&nbsp;息<br/><br/> 
</span>

<fieldset  style="width:100%">
<legend>EXCEL导入</legend>

<table width="750" height="144" border="0" cellpadding="0" cellspacing="0">
<tr>
<td width="160" height="24">&nbsp;</td>
<td >

<br>
	<ul>
	<li><b>EXCEL文件内容格式说明</b></li>
	<li>&nbsp;</li>
	<li>EXCEL表中应该有一个叫做<font color=blue><b>简历列表</b></font>的表页</li>
	<li>该表页含有至少<font color=blue><b>身份证号,姓名,毕业院校,学历,专业</b></font><font color=red>(必填)</font>这么几列</li>
	<!--  
	<li>该表页还可包含<font color=blue><b>性别,出生日期,籍贯,住址,高考录取批次,辅修专业,英语等级</b></font><font color=red>(可选)</font>等列</li>
	-->
	<li>日期请使用标准格式: yyyy-MM-dd 如(2012-11-27)</li>
	<li>已考-*、已过-* 请填写是或否</li>
	<li><font color=red><b>注：</b></font>
		<br><br><b>Excel必须为office 2003格式方可导入成功。</b>
	</li>
	<br><br><b><a href='#' onclick="javascript:downLoad();"><font color='#FF0000'>点击下载示例模版</font></a></b>
	</ul>			
	
</td>
	<td>求职岗位：<input id="wantjob" name="wantjob"
				onKeyDown="onKeyDownEvent();" 
				onKeyUp="onKeyUpEvent();"
				 onClick="onPopDivClick(this);" 
				 valuemustexist=true autoid=5013 value="${param.apply_job_id }"
				 type="text" class="required" /></td>
</tr>
<tr><td width="160" height="24">&nbsp;</td><td height=10>&nbsp;</td></tr>
<tr><td width="160" height="24">&nbsp;</td><td>用户数据EXCEL格式文件路径:</td></tr>

  <tr>
<td width="160" height="24">&nbsp;</td>
    <td >
    	<input type="file" name="image" id="image" value=""  size="90" title="请输入，不得为空">
      &nbsp;
	</td>
  </tr>
</table>
</fieldset>

</form>
</body>
</html>
<script type="text/javascript">
    new Validation('thisForm');
    
    function getlocationhost(){
    
		return "http:\/\/"+window.location.host;
	}
  
    var AuditReport =  new ActiveXObject("MTOffice.WebOffice");
    function downLoad() {
    	var file="通用简历导入模版.xls";
        var url = getlocationhost()+'${pageContext.request.contextPath}/hr/excel/'+file;

		AuditReport.IEDownLoad3(url, file,0);

}
</script>

<script Language=JavaScript>ext_init();</script>