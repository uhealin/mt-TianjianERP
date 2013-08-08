<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"  errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>导入简历库</title>

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
	    if(f1.toLowerCase().indexOf(".zip")>-1){
	    }else{
	    	alert("提供的文件必须是ZIP压缩包!");
	    	return false;
	    }
	    
	    document.thisForm.action="${pageContext.request.contextPath}/job.do?method=SaveUpload";
	    document.thisForm.submit() ;
	}
</script>

</head>
<body leftmargin="0" topmargin="0">
<div id="divBtn"></div>

<form name="thisForm" method="post" action="" id="thisForm" enctype="multipart/form-data" class="autoHeightForm">

<span class="formTitle" >
批&nbsp;&nbsp;量&nbsp;&nbsp;导&nbsp;&nbsp;入&nbsp;&nbsp;简&nbsp;&nbsp;历&nbsp;&nbsp;库<br/><br/> 
</span>

<fieldset  style="width:100%">
<legend>EXCEL导入</legend>

<table width="750" height="144" border="0" cellpadding="0" cellspacing="0">
<tr>
<td width="160" height="24">&nbsp;</td>
<td >

<br>
	<ul>
	<li><b>批量导入简历库规则说明</b></li>
	<li>&nbsp;</li>
	<li>批量导入只能导入ZIP压缩包，</li>
	<li>ZIP包中必须包含一个叫做【<font color=blue><b>简历库.xls</b></font>】<font color=red>(必须)</font>的文件</li>
	<li>ZIP包中还可包含文件名是【<font color=blue><b>姓名(证件编号)</b></font>】<font color=red>(可选)</font>的附件文件</li>
	<li>&nbsp;</li>
	<li>EXCEL表中应该有一个叫做<font color=blue><b>简历库</b></font>的表页</li>
	<li>该表页含有至少<font color=blue><b>应聘岗位,姓名,证件类型,证件编号</b></font><font color=red>(必填)</font>这么几列</li>
	<li>该表页还可包含<font color=blue><b>CPA编号,性别,出生年月,学历,毕业院校及专业,手机,邮箱,工作简历</b></font><font color=red>(可选)</font>这么几列</li>
	<br><br><b><a href='#' onclick="javascript:downLoad();"><font color='#FF0000'>点击下载示例模版</font></a></b>
	</ul>			
	

</td></tr>
<tr><td width="160" height="24">&nbsp;</td><td height=10>&nbsp;</td></tr>
<tr><td width="160" height="24">&nbsp;</td><td>用户数据导入文件路径:</td></tr>

  <tr>
<td width="160" height="24">&nbsp;</td>
    <td >
    	<input type="file" name="image" id="image" value=""  size="90" title="请输入，不得为空">
      &nbsp;
	</td>
  </tr>
</table>
</fieldset>
<input name="User" type="hidden" id="User" value='UserManager'>

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
    var url = getlocationhost()+'${pageContext.request.contextPath}/user/resume_template.zip';

		AuditReport.IEDownLoad2(url, 'resume_template.zip',0);

}
</script>

<script Language=JavaScript>ext_init();</script>