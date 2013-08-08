<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
 <%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<% 

%>
<html>
<head>
<title>导入EXCEL表</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" type="text/css" href="/AuditSystem/AS_CSS/style.css" />
<script>
function ext_init(){
        
	    var tbar = new Ext.Toolbar({
	   		renderTo: "divBtn",
	   		defaults: {autoHeight: true,autoWidth:true},
            items:[{
	            text:'确定',
	            icon:'${pageContext.request.contextPath}/img/confirm.gif',
	            handler:function(){
	            	if (checkit()) {
						document.thisForm.submit();
					}
				}  
       		},'-',{
	            text:'返回',
	            icon:'${pageContext.request.contextPath}/img/back.gif',
	            handler:function(){
					window.history.back();
				}
       		},'->'
			]
        });
        
    }
    window.attachEvent('onload',ext_init);


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
	    return true ;
	}
</script>
</head>

<body leftmargin="0" topmargin="0">
<div id="divBtn"></div>
<form name="thisForm" method="post" action="customer.do?method=SaveUpload" style="padding:30 30 30 30" class="autoHeightForm" id="thisForm" enctype="multipart/form-data">
  <font color="#FF0000" size="2" ><strong>您现在所在位置: </strong><font color="#0000CC">系统管理-&gt;客户管理-&gt;批量导入</font></font><br>
  <table width="500"  border="0" cellpadding="0" cellspacing="0">
    <tr>
      <td height="1" bgcolor="#0000FF"></td>
  </tr>
</table>

<table width="75%" height="113" border="0" cellpadding="0" cellspacing="0">
<tr><td>
<fieldset  style="width:100%;">
<legend>EXCEL文件内容格式说明</legend>
	<ul>
	<li>EXCEL表中应该有一个叫做<font color=blue><b>客户列表</b></font>的表页</li>
	<li>该表页含有至少<font color=blue><b> 客户名称</b></font><font color=red>(必填)</font>这么几列</li>
	<li>该表页还可包含<font color=blue><b><br>
	代码，所属集团，英文名称，法人代表，纳税人识别号，国税号，地税号，营业执照注册号，<br>
	注册资本，企业代码，成立日期，注册地址，股东成员，单位联系人，传真号码，<br>
	联系电话，电子邮件，单位地址，邮政编码，经营范围，备注</b></font><br><font color=red>(可选)</font>这么几列</li>
	<li><font color=red><b>注：</b></font><font color=blue><b><br>EXCEL文件中除上述的列外，其它都会自动成为客户的自定义属性；<br>『成立日期』的格式为：</b>“YYYY-MM-DD”</font></li>
	<br><br><b><a href='#' onclick="javascript:downLoad();"><font color='#FF0000'>点击下载示例模版</font></a></b>
	</ul>			
	
</fieldset>
</td></tr>
<tr><td height=10></td></tr>
<tr><td>用户数据EXCEL格式文件路径:</td></tr>
  <tr>

    <td >
    	<input type="file" name="image" id="image" value=""  size="90" title="请输入，不得为空">
      &nbsp;
	</td>
  </tr>
</table>

  <p>&nbsp;</p>
<input name="Cust" type="hidden" id="Cust" value='Customer'>

</form>


<script type="text/javascript">
    new Validation('thisForm');
    
    function goto(){
    	window.location="customer.do";
    }
    
     function getlocationhost(){
		return "http:\/\/"+window.location.host;
	}
    
    
    var AuditReport =  new ActiveXObject("MTOffice.WebOffice");
    function downLoad() {
	    var url = getlocationhost()+'${pageContext.request.contextPath}/Customer/customer_template.zip';
		AuditReport.IEDownLoad3(url,'customer_template.zip',0);
	}
</script>
</body>
</html>

