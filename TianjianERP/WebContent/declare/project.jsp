<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>项目独立性声明</title>
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
            text:'保存项目独立性声明',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/save.gif',
            handler:function(){
            	goSave();
			}
      	}, '-',
</c:if>      	
      	{
	            text:'关闭',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/close.gif',
	            handler:function () {
	            	closeTab(parent.tab);
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

<h1 align=center style='text-align:center'><span style='font-size:18.0pt;
line-height:240%;font-family:宋体;mso-ascii-font-family:"Times New Roman";
mso-hansi-font-family:"Times New Roman"'>项目组成员独立性声明书</span><span lang=EN-US
style='font-size:18.0pt;line-height:240%'><o:p></o:p></span></h1>

<p class=MsoNormal style='margin-top:7.8pt;mso-para-margin-top:.5gd;line-height:
150%'><span lang=EN-US style='font-size:12.0pt;line-height:150%;font-family:
宋体'><span style='mso-spacerun:yes'>&nbsp;&nbsp;&nbsp; </span></span><span
style='font-size:12.0pt;line-height:150%;font-family:宋体'>本人接受委派，对</span><span
style='font-size:12.0pt;line-height:150%;font-family:宋体;mso-ascii-font-family:
"Times New Roman";mso-hansi-font-family:"Times New Roman"'>当前</span><span
style='font-size:12.0pt;line-height:150%;font-family:宋体'>项目进行审计，现就本人在接受委派前及执行该业务过程中有关独立性作出如下声明：<span
lang=EN-US><o:p></o:p></span></span></p>

<p class=MsoNormal style='margin-top:7.8pt;mso-para-margin-top:.5gd;text-indent:
24.0pt;mso-char-indent-count:2.0;line-height:150%'><span lang=EN-US
style='font-size:12.0pt;line-height:150%;font-family:宋体'>1.</span><span
style='font-size:12.0pt;line-height:150%;font-family:宋体'>本人承诺在执行该项业务过程中遵守《中国注册会计师执业准则》的相关规定，遵守会计师事务所职业道德规范相关政策与程序，恪守独立、客观、公正的原则，保持应有的职业谨慎、专业胜任能力及应有的关注，勤勉尽责，并对执行该项业务过程中获知的信息保密。<span
lang=EN-US><o:p></o:p></span></span></p>

<p class=MsoNormal style='margin-top:7.8pt;mso-para-margin-top:.5gd;text-indent:
24.0pt;mso-char-indent-count:2.0;line-height:150%'><span lang=EN-US
style='font-size:12.0pt;line-height:150%;font-family:宋体'>2.</span><span
style='font-size:12.0pt;line-height:150%;font-family:宋体'>本人承诺在执行该项业务过程中保持形式上和实质上的独立，不因任何利害关系影响客观、公正的立场。<span
lang=EN-US><o:p></o:p></span></span></p>

<p class=MsoNormal style='margin-top:7.8pt;mso-para-margin-top:.5gd;text-indent:
24.0pt;mso-char-indent-count:2.0;line-height:150%'><span lang=EN-US
style='font-size:12.0pt;line-height:150%;font-family:宋体'>3.</span><span
style='font-size:12.0pt;line-height:150%;font-family:宋体'>本人承诺未兼任与所执行的业务不兼容的其他职务。<span
lang=EN-US><o:p></o:p></span></span></p>

<p class=MsoNormal style='margin-top:7.8pt;mso-para-margin-top:.5gd;text-indent:
24.0pt;mso-char-indent-count:2.0;line-height:150%'><span lang=EN-US
style='font-size:12.0pt;line-height:150%;font-family:宋体'>4.</span><span
style='font-size:12.0pt;line-height:150%;font-family:宋体'>本人承诺在执行该项业务时，做到实事求是，不为他人所左右，也不因个人好恶影响分析、判断的客观性。<span
lang=EN-US><o:p></o:p></span></span></p>

<p class=MsoNormal style='margin-top:7.8pt;mso-para-margin-top:.5gd;text-indent:
24.0pt;mso-char-indent-count:2.0;line-height:150%'><span lang=EN-US
style='font-size:12.0pt;line-height:150%;font-family:宋体'>5.</span><span
style='font-size:12.0pt;line-height:150%;font-family:宋体'>本人承诺在执行该项业务时，做到正直、诚实，不偏不倚地对待有关利益各方。<span
lang=EN-US><o:p></o:p></span></span></p>

<p class=MsoNormal style='margin-top:7.8pt;mso-para-margin-top:.5gd;text-indent:
24.0pt;mso-char-indent-count:2.0;line-height:150%'><span lang=EN-US
style='font-size:12.0pt;line-height:150%;font-family:宋体'>6.</span><span
style='font-size:12.0pt;line-height:150%;font-family:宋体'>本人承诺本人或与本人关系密切的家庭成员与该客户及其关联方之间不存在及不发生以下可能损害独立性的情况和关联关系：<span
lang=EN-US><o:p></o:p></span></span></p>

<p class=MsoNormal style='margin-top:7.8pt;mso-para-margin-top:.5gd;text-indent:
24.0pt;mso-char-indent-count:2.0;line-height:150%'><span lang=EN-US
style='font-size:12.0pt;line-height:150%;font-family:宋体'>(1) </span><span
style='font-size:12.0pt;line-height:150%;font-family:宋体'>曾是该客户及其关联方的董事、经理、其他关键管理人员或能够对该项业务产生直接重大影响的员工；<span
lang=EN-US><o:p></o:p></span></span></p>

<p class=MsoNormal style='margin-top:7.8pt;mso-para-margin-top:.5gd;text-indent:
24.0pt;mso-char-indent-count:2.0;line-height:150%'><span lang=EN-US
style='font-size:12.0pt;line-height:150%;font-family:宋体'>(2) </span><span
style='font-size:12.0pt;line-height:150%;font-family:宋体'>为该客户及其关联方提供直接影响该项业务对象的其他服务；<span
lang=EN-US><o:p></o:p></span></span></p>

<p class=MsoNormal style='margin-top:7.8pt;mso-para-margin-top:.5gd;text-indent:
24.0pt;mso-char-indent-count:2.0;line-height:150%'><span lang=EN-US
style='font-size:12.0pt;line-height:150%;font-family:宋体'>(3) </span><span
style='font-size:12.0pt;line-height:150%;font-family:宋体'>为该客户及其关联方编制属于该项业务对象的数据或其他记录；<span
lang=EN-US><o:p></o:p></span></span></p>

<p class=MsoNormal style='margin-top:7.8pt;mso-para-margin-top:.5gd;text-indent:
24.0pt;mso-char-indent-count:2.0;line-height:150%'><span lang=EN-US
style='font-size:12.0pt;line-height:150%;font-family:宋体'>(4) </span><span
style='font-size:12.0pt;line-height:150%;font-family:宋体'>与该客户及其关联方长期交往，存在超越业务范围的私人关系；<span
lang=EN-US><o:p></o:p></span></span></p>

<p class=MsoNormal style='margin-top:7.8pt;mso-para-margin-top:.5gd;text-indent:
24.0pt;mso-char-indent-count:2.0;line-height:150%'><span lang=EN-US
style='font-size:12.0pt;line-height:150%;font-family:宋体'>(5) </span><span
style='font-size:12.0pt;line-height:150%;font-family:宋体'>接受该客户及其关联方或其董事、经理、其他关键管理人员或能够对该项业务产生直接重大影响的员工的贵重礼品或超出社会礼仪的款待；<span
lang=EN-US><o:p></o:p></span></span></p>

<p class=MsoNormal style='margin-top:7.8pt;mso-para-margin-top:.5gd;text-indent:
24.0pt;mso-char-indent-count:2.0;line-height:150%'><span lang=EN-US
style='font-size:12.0pt;line-height:150%;font-family:宋体'>(6) </span><span
style='font-size:12.0pt;line-height:150%;font-family:宋体'>购买该客户及其关联方的股票或对其拥有股权投资；<span
lang=EN-US><o:p></o:p></span></span></p>

<p class=MsoNormal style='margin-top:7.8pt;mso-para-margin-top:.5gd;text-indent:
24.0pt;mso-char-indent-count:2.0;line-height:150%'><span lang=EN-US
style='font-size:12.0pt;line-height:150%;font-family:宋体'>(7) </span><span
style='font-size:12.0pt;line-height:150%;font-family:宋体'>与该客户及其关联方存在其他紧密的合资与合作关系；<span
lang=EN-US><o:p></o:p></span></span></p>

<p class=MsoNormal style='margin-top:7.8pt;mso-para-margin-top:.5gd;text-indent:
24.0pt;mso-char-indent-count:2.0;line-height:150%'><span lang=EN-US
style='font-size:12.0pt;line-height:150%;font-family:宋体'>(8) </span><span
style='font-size:12.0pt;line-height:150%;font-family:宋体'>向该客户及其关联方贷款或作为该客户及其关联方借款的担保人，或从该客户及其关联方处取得贷款，或由该客户及其关联方担保而取得贷款；<span
lang=EN-US><o:p></o:p></span></span></p>

<p class=MsoNormal style='margin-top:7.8pt;mso-para-margin-top:.5gd;text-indent:
24.0pt;mso-char-indent-count:2.0;line-height:150%'><span lang=EN-US
style='font-size:12.0pt;line-height:150%;font-family:宋体'>(9) </span><span
style='font-size:12.0pt;line-height:150%;font-family:宋体'>受托或代理该客户及其关联方的资产或业务并获得经济利益；<span
lang=EN-US><o:p></o:p></span></span></p>

<p class=MsoNormal style='margin-top:7.8pt;mso-para-margin-top:.5gd;text-indent:
24.0pt;mso-char-indent-count:2.0;line-height:150%'><span lang=EN-US
style='font-size:12.0pt;line-height:150%;font-family:宋体'>(10) </span><span
style='font-size:12.0pt;line-height:150%;font-family:宋体'>在执行该项业务过程中利用该客户关系购买该客户提供的产品或劳务；<span
lang=EN-US><o:p></o:p></span></span></p>

<p class=MsoNormal style='margin-top:7.8pt;mso-para-margin-top:.5gd;text-indent:
24.0pt;mso-char-indent-count:2.0;line-height:150%'><span lang=EN-US
style='font-size:12.0pt;line-height:150%;font-family:宋体'>(11) </span><span
style='font-size:12.0pt;line-height:150%;font-family:宋体'>其他损害独立性的情况和关联关系（如存在，请具体列示；如不存在，应删除此条款）。<span
lang=EN-US><o:p></o:p></span></span></p>

<p class=MsoNormal style='margin-top:7.8pt;mso-para-margin-top:.5gd;text-indent:
24.0pt;mso-char-indent-count:2.0;line-height:150%'><span lang=EN-US
style='font-size:12.0pt;line-height:150%;font-family:宋体'>7.</span><span
style='font-size:12.0pt;line-height:150%;font-family:宋体'>本人承诺在接受委派及执行该项业务过程中将注意到的违反独立性要求或对独立性造成威胁的情况和关系及时告知会计师事务所。<span
lang=EN-US><o:p></o:p></span></span></p>

<p class=MsoNormal style='margin-top:7.8pt;mso-para-margin-top:.5gd;text-indent:
24.0pt;mso-char-indent-count:2.0;line-height:150%'><span lang=EN-US
style='font-size:12.0pt;line-height:150%;font-family:宋体'>8.</span><span
style='font-size:12.0pt;line-height:150%;font-family:宋体'>本人承诺对在执行该业务过程中获知的全部非公开信息予以保密，不与任何无关人员（包括会计师事务所与该业务无关人员）谈及相关信息。<span
lang=EN-US><o:p></o:p></span></span></p>

<p class=MsoNormal style='margin-top:7.8pt;mso-para-margin-top:.5gd;text-indent:
24.0pt;mso-char-indent-count:2.0;line-height:150%'><span lang=EN-US
style='font-size:12.0pt;line-height:150%;font-family:宋体'>9.</span><span
style='font-size:12.0pt;line-height:150%;font-family:宋体'>本人承诺一旦本人有计划或寻求在该客户及其关联方任职，本人将立即停止执行该项业务并报告该项目负责人。<span
lang=EN-US><o:p></o:p></span></span></p>

<p class=MsoNormal style='margin-top:7.8pt;mso-para-margin-top:.5gd;text-indent:
24.0pt;mso-char-indent-count:2.0;line-height:150%'><span lang=EN-US
style='font-size:12.0pt;line-height:150%;font-family:宋体'>10.</span><span
style='font-size:12.0pt;line-height:150%;font-family:宋体'>本人确信上述声明不存在任何虚假、误导性陈述或重大遗漏，并对其内容的真实性负责。<span
lang=EN-US><o:p></o:p></span></span></p>

</div>
<c:if test="${isOk != '1'}">
<div id="divCheck" style="color: #ffffff; margin-top:5px; text-align:left; font-size: 12px;width: 80%;"><input id="check" type="checkbox" onclick="isAgree(this);" style="cursor: hand;" /><label for="check" style="cursor:hand;">我已经认真阅读以上协议,并同意该协议</label></div>
<br />
<input id="btnAgree" name="btnAgree" type="button" class="flyBT" value="我同意" disabled="disabled" onclick="goSave();"/>
<input id="btnDissent" name="btnDissent" type="button" class="flyBT" value="我不同意" onclick="closeTab(parent.tab);"/>
</c:if>
</center>


<input name="flag" type="hidden" id="flag" value="${flag }">
<input name="opt" type="hidden" id="opt" value="${opt }">
<input name="userid" type="hidden" id="userid" value="${userid }">

<input name="projectid" type="hidden" id="projectid" value="${projectid }">
<input name="changeTitle" type="hidden" id="changeTitle" value="${changeTitle }">
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


function goSave(){
	thisForm.action = "${pageContext.request.contextPath}/declare.do?method=save";
	thisForm.target = "";
	thisForm.submit();	
}


</script>
