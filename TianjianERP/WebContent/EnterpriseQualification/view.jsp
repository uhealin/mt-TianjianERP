<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style>
	 
	.tTable td,th {
		height:30px;
	}


</style>
<script type="text/javascript">
var tbar_project;
var tab;

function extInit(){
	
	function navHandler(dir) {
		var i = 0;
		var cur = 0;
		var curTab = tab.getActiveTab();
	
		tab.items.each(function(item) {   
			if(item == curTab) {
				cur = i;
			} 
			
			i++;
		});  
		
		cur += dir;

	    tab.setActiveTab(cur);
	}

	tab = new Ext.TabPanel({
        id: "tab",
        renderTo: "divTab",
        activeTab: 0, //选中第一个 tab
        autoScroll:true,
        frame: true,
        height: document.body.clientHeight-Ext.get('divTab').getTop()-50, 
        defaults: {autoHeight: true,autoWidth:true},
        items:[{
        			contentEl: "tab1", 
        			title: "企业资质信息"
        		}
        ]
	});
	

	tbar_project = new Ext.Toolbar({
		renderTo: "divBtn",
		items:[
			{ 
				text:'返回',
				cls:'x-btn-text-icon',
				icon:'${pageContext.request.contextPath}/img/back.gif',
				handler:function(){
					window.history.back();
				}
	   		},'->'
   		]
    });
      	
 	new Ext.Viewport({
		defaults:{border:false},
		items:[
			tbar_project
		]
	});

 	
 	mt_form_initAttachFile(document.getElementById("attachFileId"));
 	//view();
}

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
<div id="panel"></div>
<div id="divBtn" ></div>
<form name="thisForm" method="post" action="" id="thisForm" class="autoHeightForm">

<br/>	
<div id="divTab" style="overflow:auto">
	<div id="tab1" class="tabDiv">
		<table cellpadding="5" align="left" border="0" cellspacing="10">
			<tr>
				<th width="150" style="text-align: right">标题<span class="mustSpan">[*]</span>：</th>
				<td align="left" colspan="3">
					<input type="text" 
						   name="title" 
						   id="title"
						   title="必填"
						   maxlength="80"  
						   size="50"
						   readonly="readonly"
						   value="${eqm.title}" class="required" />
				</td>
			</tr>
			
			<tr>
				<th width="150" style="text-align: right">模块名称：</th>
				<td align="left" colspan="3">
					<input type="text"
						   id="modelName"
						   name="modelName"
						   value="${modelName}"
						   maxlength="500"
						   readonly="readonly"
						   size="50" />
				</td>
			</tr>
			
			
			
			<tr>
				<th width="150" style="text-align: right">附件：</th>
				<td align="left" colspan="3">
					<input type="hidden" 
						   id="attachFileId"
						   name="attachFileId" 
						   ext_type=attachFile
						   attachFile=true
						   maxlength="50" 
						   size="50"   
						   ext_readonly="true"
						   indexTable="${eqm.attachFileId}"
						   value="${eqm.attachFileId}" />
				</td>
			</tr>
			
		</table>
	</div>
	
	
</div>

<input type="hidden" id="uuid" name="uuid" value="${eqm.uuid}" >
<input type="hidden" id="opt" name="opt" value="${opt}" >
<input type="hidden" id="joinUser" name="joinUser" value="${joinUser}">
<input type="hidden" id="joinRole" name="joinRole" value="${powerId}">
<input type="hidden" id="powerId" name="powerId" value="">
<input type="hidden" id="powerType" name="powerType" value="">
<input type="hidden" id="joinUserDepartmentId" name="joinUserDepartmentId" value="${joinUserDepartmentId}" >

</form>
</body>

<script type="text/javascript">

var joinUser = document.getElementById("joinUser");
var joinRole = document.getElementById("joinRole");

//ext初始化
Ext.onReady(extInit);

	 
</script>
</html>