<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/form.js"></script>
<script type="text/javascript"> 

function ext_init(){
	var _tbar=new Ext.Toolbar({
		renderTo: "divBtn",
		items:[
			{ 
				text:'保存',
				icon:'${pageContext.request.contextPath}/img/save.gif' ,
				handler:function(){
					if (!formSubmitCheck('thisForm')) {
						return;
					}
					showWaiting("100%","100%");
					
					thisForm.action = "${pageContext.request.contextPath}/content.do?method=save";
					thisForm.submit();
				}
			},'-',{
				text:'返回',
				icon:'${pageContext.request.contextPath}/img/back.gif' ,
				handler:function(){
				 	window.location = "${pageContext.request.contextPath}/content.do?op=${op}&classid=${content.classid}";
				}
			}
			
		]
	});
	
}



Ext.onReady(function(){
	ext_init();
	mt_form_initAttachFile(document.getElementById("attachid"));
});

</script>

</head>

<body>
<div id="divBtn" ></div>
<form name="thisForm" method="post" action="" >	
<input type="hidden" name="op" id="op" value="${op}"  />
<input type="hidden" name="autoid" id="autoid" value="${content.autoid}"  />
<input type="hidden" name="classid" id="classid" value="${content.classid}"  />

<input type="hidden" name="advid" id="advid" value="${content.advid}"  />
<input type="hidden" name="advtime" id="advtime" value="${content.advtime}"  />
<input type="hidden" name="advmen" id="advmen" value="${content.advmen}"  />
<input type="hidden" name="eadvmen" id="eadvmen" value="${content.eadvmen}"  />
<input type="hidden" name="isfb" id="isfb" value="${content.isfb}"  />
<input type="hidden" name="ishf" id="ishf" value="${content.ishf}"  />

<table border="0" cellspacing="0" class="editTable">
	<tr><td class="editTitle" colspan="2">咨询类别：<font color=blue>${classname}</font></td></tr>
	<tr>
		<th >咨询题目：<span class="mustSpan">[*]</span></th>	
		<td ><input maxlength="500" size=80 type="text" name="advtitle" id="advtitle" value="${content.advtitle}" class="required" /></td>
	</tr>
	<tr>
		<th >上传附件：</th>
		<td ><input ext_type=attachFile maxlength="100" type="hidden" 
		attachFile=true indexTable="content" name="attachid" id="attachid" 
		value="${content.attachid}"  /></td>
	</tr>
	<tr>
		<th >提示：</th>
		<td >业务人员应当陈述需要咨询的事项。如果是疑难问题，应表述咨询者自身的观点、支持所持观点的相关依据等；如果是	争议事项，应表述不同观点、支持所持观点的相关依据等。</td>
	</tr>
	<tr>
		<th >内容：<span class="mustSpan">[*]</span></th>	
		<td ><textarea id="advcontent" name="advcontent" cols="155" rows="30" class="required" >${content.advcontent}</textarea></td>
	</tr>
</table>
		
</form>		

	
</body>

</html>

<script type="text/javascript">

</script>
