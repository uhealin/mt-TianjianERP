<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>职业道德说明</title>
<style>
.stepDiv {
	 color:#ffffff; border: 1px solid #cccccc; padding: 5px; margin: 5px;
}
</style>

<script type="text/javascript">

Ext.onReady(function(){
	
	var tbar_customer = new Ext.Toolbar({
		renderTo: "divBtn",
           items:[
<c:if test="${isOk != '1'}">		           
           {
            text:'保存事业道德说明',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/save.gif',
            handler:function(){
            	goSave();
			}
      	}, '-',
</c:if>      	
      	{
	            text:'返回',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/back.gif',
	            handler:function () {
	            	window.history.back();
	            	//closeTab(parent.tab);
	            }
	     }
        ]
        });  

});

</script>


</head>
<body style=" padding: 0px; margin: 0px; background-color: #006699; background-image:none;" >
<div id="divBtn"></div>
<form name="thisForm" method="post" action="" >
<table border="0" width="100%"><tr><td id="thesteps" valign="top" align="left" height='20px'></td></tr></table>


<center>
<div id="protocol" style=" border: 1px solid #cccccc; background-color: #ffffff; width: 80%; font-size: 12px; text-align: left; padding: 10px; height: 375px; overflow-y:scroll;">


<p class=MsoNormal align=center style='text-align:center;line-height:24.0pt;
mso-line-height-rule:exactly'><b style='mso-bidi-font-weight:normal'><span
style='font-size:16.0pt;font-family:宋体;mso-ascii-font-family:"Times New Roman";
mso-hansi-font-family:"Times New Roman"'>遵守职业道德的声明</span></b><b
style='mso-bidi-font-weight:normal'><span lang=EN-US style='font-size:16.0pt'><o:p></o:p></span></b></p>

<p class=MsoNormal align=center style='text-align:center;line-height:24.0pt;
mso-line-height-rule:exactly'><span lang=EN-US style='font-size:12.0pt'><o:p>&nbsp;</o:p></span></p>


 	${moral.content}

</div>
<c:if test="${isOk != '1'}">
<div id="divCheck" style="color: #ffffff; margin-top:5px; text-align:left; font-size: 12px;width: 80%;"><input id="check" type="checkbox" onclick="isAgree(this);" style="cursor: hand;" /><label for="check" style="cursor:hand;">我已经认真阅读以上协议,并同意该协议</label></div>
<br />
<input id="btnAgree" name="btnAgree" type="button" class="flyBT" value="我同意" disabled="disabled" onclick="goSave('agree');"/>
<input id="btnDissent" name="btnDissent" type="button" class="flyBT" value="我不同意" onclick="window.history.back();"/>
</c:if>
<c:if test="${isOk == '1'}">
<div id="divCheck" style="color: #ffffff; margin-top:5px; text-align:left; font-size: 12px;width: 80%;">
<label for="check" style="cursor:hand;">您本年已经签署了职业道德声明。</label></div>
</c:if>
</center>


<input name="userid" type="hidden" id="userid" value="${userid }">

</form>
</body>
</html>
<script type="text/javascript">

function isAgree(me) {
	if(me.checked == true) {
		document.all.btnAgree.disabled = false;
	}else {
		document.all.btnAgree.disabled = true;
	}
}

function goSave(agree){
	thisForm.action = "${pageContext.request.contextPath}/declare.do?method=saveMoralNorm&flag="+agree;
	thisForm.target = "";
	thisForm.submit();	
}


</script>
