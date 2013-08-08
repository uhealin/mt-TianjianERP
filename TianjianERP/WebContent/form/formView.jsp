<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>表单编辑 </title>


<script type="text/javascript">


${extjsSupportFunction}
 
var isView = "${param.view}";
function ext_init(){
    var tbar = new Ext.Toolbar({
	   		renderTo: "divBtn",
	   		id:'mt_form_tbar',
	   		items:[${extjs}]
	});
	
	var saveBtn = {
		text:'保存',
		icon:'${pageContext.request.contextPath}/img/save.gif',
		handler:function(){
			if (!formSubmitCheck('thisForm')) {
				return;
			}
			
			if(funExists("beforeSave")) {
				var formId = document.getElementById("mt_formid").value;
				var uuid = document.getElementById("uuid").value;
				if(!beforeSave(formId, uuid)) {
					return;
				}
			}
			
			//保存
			var url = "${pageContext.request.contextPath}/formDefine.do?method=formDataSave";
			
			var param=getParamObject();
        	for(var key in param){
        		if(key=='method'||key=="uuid"||key=="view")continue;
        		url+="&"+key+"="+param[key];
        		
        	}
			thisForm.action = url;
			thisForm.submit();
		}
	};
	
	var returnBtn = {
		text:'返回',
        cls:'x-btn-text-icon',
        icon:'${pageContext.request.contextPath}/img/back.gif',
        handler:function(){
			//window.history.back();
        	var formId = document.getElementById("mt_formId").value;
        	var url="";
        	var param=getParamObject();
        	if(param["where_id"]){
            	url ="${pageContext.request.contextPath}/formDefine.do?method=formListExtView&uuid=" + formId;        		
        	}else{
            	url ="${pageContext.request.contextPath}/formDefine.do?method=formListView&uuid=" + formId;
        	}
       	
        	for(var key in param){
        		if(key=="method"||key=="uuid"||key=="view")continue;
        		url+="&"+key+"="+param[key];
        		
        	}
        	window.location=url;
		}
   	};
	
	if(isView == "true") {
		view();
	} else {
		tbar.add(saveBtn);
		tbar.add('-');
	}
	
	tbar.add(returnBtn);
	
	tbar.doLayout();
}

Ext.onReady(function(){
	ext_init();
	
	mt_form_initDateSelect();
	mt_form_initAttachFile();
	
});

function view(){
	var form_obj = document.all; 
	
	//form的值
	for (var i=0; i < form_obj.length; i++ ) {
		e=form_obj[i];
		if (e.tagName=='INPUT' || e.tagName=='TEXTAREA') {
			e.readOnly = true ;
			e.className = "readonly";
			e.disabled = true;
			e.backgroundImage = "none";
		}
		if(e.tagName=='SELECT'){
			e.disabled= true;
			e.className = "readonly";
		}
		if(e.tagName == 'A'){
			e.style.display = "none";
			e.disabled = true;
		}
		if(e.tagName == "IMG"){
			e.style.display = "none";
			e.disabled = true;
		}
	}
}
</script>


</head>
<body>
<div id="divBtn"></div>
<form name="thisForm" method="post" action="" >

<div class="autoHeightDiv" style="overflow: auto;" >
${html}
</div>
</form>
</body>
</html>
<script type="text/javascript">
Array.prototype.contains = function (element) { 
	for (var i = 0; i < this.length; i++) { 
		if (this[i] == element) { 
			return true; 
		} 
	} 
	return false; 
}




</script>

