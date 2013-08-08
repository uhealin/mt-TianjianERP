<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="../hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
<title>流程审批</title>


<script type="text/javascript" src="${pageContext.request.contextPath}/js/form.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<script type="text/javascript">


function ext_init() {
	
	var tbar = new Ext.Toolbar({
		region:'north',
   		id:'tbar',
   		height:30
	});
		
	tbar.add({ 
        text:'保存',
        icon:'${pageContext.request.contextPath}/img/save.gif' ,
        handler:function(){
				        	
        	if (!formSubmitCheck('thisForm')) {
				return;
			}
        	var jsb_idea = document.getElementById("jsb_idea1").value;
        	if(jsb_idea == ""){
        		alert("回复内容不能为空!");
        		return;
        	}
			showWaiting("100%","100%");
			
			document.getElementById("jsb_idea").value = document.getElementById("jsb_idea1").value;
			thisForm.action = "${pageContext.request.contextPath}/content.do?method=answerSave";
			thisForm.submit();
		}
  	}) ;
	
	  
	tbar.add('-',{     
	    text:'返回',
	    icon:'${pageContext.request.contextPath}/img/back.gif',
	    handler:function(){
	    	window.location = "${pageContext.request.contextPath}/content.do?op=${op}&classid=${content.classid}" ;
		}
	}) ;
	
	
	var mytab = new Ext.TabPanel({
        id: "tab",
        region:'south',
        activeTab:0, //选中第一个 tab
        layoutOnTabChange:true, 
        forceLayout : true,
        deferredRender:false,
        height: 300
    });
	
	mytab.add({title:"咨询回复",id:"adviceTab",el:"adviceDiv"}) ;
	mytab.doLayout();
	
	 //定义页面布局
	 var layout = new Ext.Viewport({
			layout:'border',
			items:[tbar,new Ext.Panel({
					region:'center',
					contentEl: 'center',
					margins:'0 0 0 0',
					autoScroll:true, 
			        lines:false
				}),mytab
			 ]
		});
	 
}

</script>

</head>
<body >
<form name="thisForm" id="thisForm" class="formular"  method="post" action="">
<!-- 表单区域DIV -->
<div id="center">
	<table border="0" cellspacing="0" class="editTable" style="width: 85%">
	<tr><td class="editTitle" >咨询类别--》：<font color=blue>${classname}</font>--》咨询题目：<font color=blue>${content.advtitle}</font></td></tr>
	<tr>
		<th class="editTitle" style="text-align: center;" >咨询正文</th>	
	</tr>
	<tr>
		<td >
		${content.advcontent}
		<br>
		附件：
		<input ext_type=attachFile maxlength="100" type="hidden" readonly 
		indexTable="content" name="attachid" id="attachid" 
		value="${content.attachid}"  />
		</td>
	</tr>
	<tr>
		<td style="border-top: 1px solid #99BBE8;">咨询人：<font color=blue>${content.advmen}</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			咨询时间：<font color=blue>${content.advtime}</font>
		</td>
	</tr>
	<c:forEach items="${subList}" var="sub">
	<tr>
		<th class="editTitle" style="text-align: center;" >${sub.orderid}楼回复</th>	
	</tr>
	<tr>
		<td ><pre>${sub.jsb_idea}</pre></td>
	</tr>
	<tr>
		<td style="border-top: 1px solid #99BBE8;">回复人：<font color=blue>${sub.jsb_men}</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;回复时间：<font color=blue>${sub.jsb_time}</font></td>
	</tr>
	</c:forEach>
</table>	
	
</div>

<!-- 填写意见区域DIV -->
<div id=adviceDiv style="margin-left: 10px;">
	<table>
		<tr>
			<td>回复：</td>
			<td>
				<textarea id="jsb_idea1" name="jsb_idea1" cols="155" rows="8" class="required" ></textarea>
			</td>
		</tr>
	</table>
</div>

<input type="hidden" name="op" id="op" value="${op}"  />
<input type="hidden" name="classid" id="classid" value="${content.classid}"  />
<input type="hidden" name="advid" id="advid" value="${content.advid}"  />
<input type="hidden" name="autoid" id="autoid" value="${content.autoid}"  />
<input type="hidden" name="jsb_idea" id="jsb_idea" value=""  />
</form>
</body>
</html>

<script language="javascript">

	Ext.onReady(function(){
		ext_init();
		mt_form_initAttachFile(document.getElementById("attachid"));
	});
	
	
</script>
