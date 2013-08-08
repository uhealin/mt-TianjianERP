<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"  errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>商机批量导入</title>

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
	    
	    document.thisForm.action="${pageContext.request.contextPath}/customer.do?method=businessUpload";
	    document.thisForm.submit() ;
	}
</script>

</head>
<body leftmargin="0" topmargin="0">
<div id="divBtn"></div>

<form name="thisForm" method="post" action="" id="thisForm" enctype="multipart/form-data" class="autoHeightForm">

<span class="formTitle" >
EXCEL&nbsp;&nbsp;导&nbsp;&nbsp;入&nbsp;&nbsp;市&nbsp;&nbsp;场&nbsp;&nbsp;机&nbsp;&nbsp;会&nbsp;&nbsp;分&nbsp;&nbsp;析&nbsp;&nbsp;信&nbsp;&nbsp;息<br/><br/> 
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
	<li>EXCEL表中应该有一个叫做<font color=blue><b>商机列表</b></font>的表页</li>
	<li>该表页含有至少<font color=blue><b>客户名称,客户来源,客户联系人,客户联系电话,标题,商机截止日期</b></font><font color=red>(必填)</font>这么几列</li>
	<li>该表页还可包含<font color=blue><b>详细情况,商机责任人</b></font><font color=red>(可选)</font>这么几列</li>
	<li><br><font color=red><b>注：</b></font><font color=blue><b>商机截止日期必须是文本类型!</b></font></li>
	<br><br><b><a href='#' onclick="javascript:downLoad();"><font color='#FF0000'>点击下载示例模版</font></a></b>
	</ul>			
	

</td></tr>
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
    
    
    //下载模版
    function downLoad() {
    	var url = getlocationhost()+'${pageContext.request.contextPath}/Customer/customerBusiness_template.zip';
		AuditReport.IEDownLoad3(url, 'customerBusiness_template.zip',0);

	}
</script>

<script Language=JavaScript>ext_init();</script>