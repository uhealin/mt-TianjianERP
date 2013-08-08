<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style>
	 
	.tTable td,th {
		height:30px;
	}
	 .txt{ 
		border-bottom:0px;
		border-top:0px; 
		border-left:0px; 
		border-right:0px; 
		background-color:transparent; /* 背景色透明 */ 
	}

</style>
<script type="text/javascript">
var tbar_project;
var tab;

function setStep(tab) {	
	var btnNext = Ext.getCmp("move-next");
	var btnBack = Ext.getCmp("move-prev");	
	    
    if (tab == 0) {
        btnBack.disable();
    } else {
        btnBack.enable();
    }

    if (tab == 1) {
        btnNext.disable();
    } else {
        btnNext.enable();
    }
}

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

	    setStep(cur);	
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
        			title: "会议信息", 
        			listeners: {
        				activate: function(){
        					setStep(0);
        				}
        			}
        		},{
        			contentEl: "tab4", 
        			title: "通知方式",
        			listeners: {
						activate: function(){
        				}
        			}
        		}
        ],
        bbar:[ '->',{
				id:'move-prev',
				text:'上一步',
				disabled: true,
				handler: function(){
					navHandler(-1);
				}  
			},'-',{
				id:'move-next',
				text:'下一步',
				handler: function(){
					navHandler(1);
				}
			}
          ]
	});
	
	
	tbar_project = new Ext.Toolbar({
		renderTo: "divBtn",
		items:[
			{ 
				text:"通过",
				cls:'x-btn-text-icon',
				icon:'${pageContext.request.contextPath}/img/confirm.gif',
				handler:function(){
					goPass();
				}
			}
			,'-',
			{ 
				text:"不通过",
				cls:'x-btn-text-icon',
				icon:'${pageContext.request.contextPath}/img/close.gif',
				handler:function(){
					goNotPass();
				}
			},'-',
   			{ 
				text:'返回',
				cls:'x-btn-text-icon',
				icon:'${pageContext.request.contextPath}/img/back.gif',
				handler:function(){
					//window.history.back();
					window.location.href="${pageContext.request.contextPath}/meetingOrder.do?method=goAudit";
				}
				
			}
   		]
    });
 	new Ext.Viewport({
		defaults:{border:false},
		items:[
			tbar_project
		]
	});
	
   
}


</script>
</head>
<body>
<div id="panel"></div>
<div id="divBtn" ></div>
<form name="thisForm" method="post" action="" id="thisForm" class="autoHeightForm">
<br/>

<table class="tTable" id="tab_reason" style="display: none">
	<tr>
		<td width="15%" style="text-align: right;">不通过原因：</td>
		<td width="65%" style="text-align: left;"><textarea rows="5" cols="90%" id="reason" name="reason" >${mo.reason }</textarea></td>
		<td width="20%" style="text-align: center"><input type="button" value="确定" onclick="f_sure()">&nbsp;&nbsp;&nbsp;<input type="button" value="取消" onclick="f_clear()"></td>
	</tr>
</table>
<br/>	
	<div style="text-align: center;margin-bottom: 10px;" >以下信息只做查看，修改无效! </div>
<div id="divTab" style="overflow:auto">
	<!-- 会议信息 -->
	<div id="tab1" class="tabDiv">
		<table cellpadding="5" align="left" border="0" cellspacing="10">
			<tr>
				<th width="150" style="text-align: right">会议主题<span class="mustSpan">[*]</span>：</th>
				<td align="left">
					<input type="text" 
						   name="title" 
						   id="title"
						   title="必填"
						   maxlength="80"  
						   size="50"
						   value="${mo.title}" class="txt" />
				</td>
				<th width="150" style="text-align: right">会议名称<span class="mustSpan">[*]</span>：</th>
				<td align="left">
					<input type="text"
						   name="name"
						   id="name"
						   maxlength="80"
						   value="${mo.name}"
						   title="必填"
						   class="txt"
						   size="50" /> 
				</td>
			</tr>
			
			<tr>
				<th width="150" style="text-align: right">会议室<span class="mustSpan">[*]</span>：</th>
				<td>
					<input type="text" 
						   id="meetingRoomId"
						   name="meetingRoomId" 
						   maxlength="50" 
						   size="50"
						   autoWidth="210"
	      				   autoid=751
	      				   multiselect="true" 
						   value="${mo.meetingRoomId}" class="txt" />
				</td>
				<th width="150" style="text-align: right">申请部门<span class="mustSpan">[*]</span>：</th>
				<td align="left">
					<input type="text" 
						   name="departmentId" 
						   id="departmentId"
						   title="申请部门"
						   maxlength="80"  
						   size="50"
						  valuemustexist=true autoid=123 autoHeight=150
						   value="${mo.departmentId}" class="txt" />
				</td>
			</tr>
			
			<tr>
				<th width="150" style="text-align: right">会议开始时间<span class="mustSpan">[*]</span>：</th>
				<td >
					<input type="text" id="startTime" name="startTime" value="${mo.startTime}" class="txt">
				</td>
				<th width="150" style="text-align: right">会议结束时间<span class="mustSpan">[*]</span>：</th>
				<td >
					<input type="text" id="endTime" name="endTime" class="txt" value="${mo.endTime}">
				</td>
				
			</tr>
			
			<tr>
				<th width="150" style="text-align: right">接待要求：</th>
				<td colspan="3">
					<input type="text" 
						   id="requirements"
						   name="requirements" 
						   maxlength="50" 
						   size="50"
						   autoWidth="210"
	      				   autoid=700
	      				   multiselect="true" 
	      				   class="txt"
	      				   refer="requirementsType"
						   value="${mo.requirements}" />
				</td>
			</tr>
			
			<tr>
				<th width="150" style="text-align: right">设备要求：</th>
				<td colspan="3">
					<input type="text" 
						   id="equipment"
						   name="equipment" 
						   maxlength="50" 
						   size="50"
						   autoWidth="210"
	      				   class="txt"
	      				   multiselect="true" 
	      				   refer="equipmentType"
						   value="${mo.equipment}" />
				</td>
			</tr>
			
			
			
			<tr>
				<th width="150" style="text-align: right">事由：</th>
				<td colspan="3">
					<input type="text"
						   id="event"
						   name="event"
						   value="${mo.event}"
						   maxlength="500"
						   size="117" class="txt" />
				</td>
			</tr>
			
			<tr>
				<th width="150" style="text-align: right">会议描述：</th>
				<td colspan="3">
					<textarea name="describes" id="describes" style="width: 90%;height: 100px; overflow-x:hidden;overflow-y:hidden" class="txt">${mo.describes }</textarea>
				</td>
			</tr>
			<tr>
				<th width="150" style="text-align: right">已参加会议人：</th>
				<td colspan="3">
					${joinUserName}
				</td>
			</tr>
		</table>
	</div>

	
	<div id="tab4" class="x-hide-display tabDiv">
		<input type="checkbox" name="mobilePhoneMsg" id="mobilePhoneMsg" value="是">手机短信 &nbsp; <br>
		<input type="checkbox" name="instationMsg" id="instationMsg" value="是"> 站内通知
	</div>
</div>

<input type="hidden" id="uuid" name="uuid" value="${mo.uuid}" >
<input type="hidden" id="taskId" name="taskId" value="${taskId}" >
<input type="hidden" id="opt" name="opt" value="${opt}" >
<input type="hidden" id="status" name="status" >
<input type="hidden" id="joinUser" name="joinUser" value="${joinUser}">
<input type="hidden" id="joinRole" name="joinRole" value="${joinRole}">
<input type="hidden" id="joinUserDepartmentId" name="joinUserDepartmentId" value="${joinUserDepartmentId}" >

</form>
</body>

<script type="text/javascript">
 

	


//ext初始化
Ext.onReady(extInit);
Ext.onReady(view);


//审核
function goPass() {
	 
	document.getElementById("status").value = "已通过";
	 
	var form = document.getElementById("thisForm");
	form.action = "${pageContext.request.contextPath}/meetingOrder.do?method=saveAuidt";
	
	showWaiting();

	form.submit();
	
}

// 不通过
function goNotPass(){
	var reason = document.getElementById("tab_reason");
	if(reason.style.display=="none"){
	 	tab.setHeight(305);
	 	document.getElementById("tab_reason").style.display="";
	 	document.getElementById("reason").focus();
	 }else{
	 	document.getElementById("tab_reason").style.display="none";
	 	tab.setHeight(377);
	 }
	
}



//确定
function f_sure(){
	
	var reason = document.getElementById("reason").value + "";
	if(reason.trim() == ""){
		alert("请填写不通过原因！");
		document.getElementById("reason").focus();
		return;
	}
	 
	document.getElementById("status").value = "不通过";
	
	if(confirm("您确定要做此操作吗？","yes")){
		
		var form = document.getElementById("thisForm");
		form.action = "${pageContext.request.contextPath}/meetingOrder.do?method=saveAuidt";
		
		showWaiting();
		
		form.submit();
	}
}


// 清空
function f_clear(){
	document.getElementById("reason").value = "";
	document.getElementById("tab_reason").style.display = "none";
}

// 只读
function view(){
	var form_obj = document.all; 
	//form的值
	for (i=0;i<form_obj.length ;i++ ) {
		e=form_obj[i];
		if (e.tagName=='INPUT' || e.tagName=='TEXTAREA') {
			if(e.id!="reason"){
				e.readOnly = true ;
			}
		}
	}
		
}
	 
</script>
</html>