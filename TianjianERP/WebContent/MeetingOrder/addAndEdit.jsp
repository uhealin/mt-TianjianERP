<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<html>
<head>
<!-- 
<script src="${pageContext.request.contextPath}/AS_INCLUDE/ext_time/ext-all.js" type="text/javascript"></script>
 -->
<script src="${pageContext.request.contextPath}/AS_INCLUDE/SpinnerField.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/AS_INCLUDE/Spinner.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/AS_INCLUDE/timeFiled.js" charset="GBK" type="text/javascript"></script>


<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style>
	 
	.tTable td,th {
		height:30px;
	}


</style>
<script type="text/javascript">

	
	var flag = "${param.flag}";
	var text = "返回";
	var icon = "back.gif";
	function extInit(){
	tbar_project = new Ext.Toolbar({
		renderTo: "divBtn",
		items:[
			{ 
				text:text,
				cls:'x-btn-text-icon',
				icon:'${pageContext.request.contextPath}/img/' + icon,
				handler:function(){
					window.history.go(-1);
				}
	   		},'-',
	   		{ 
				text:'保存',
				cls:'x-btn-text-icon',
				icon:'${pageContext.request.contextPath}/img/save.gif',
				handler:function(){
					save();
				}
	   		},'->'
   		]
    });
      	
 	
	}

</script>
</head>
<body>

<div id="divBtn" ></div>
<form name="thisForm" method="post" action="" id="thisForm" class="autoHeightForm">
	<!-- 会议信息 -->
		<table cellpadding="5" align="left" border="0" cellspacing="10">
		   	
			<tr>
				<th width="150" style="text-align: right">会议主题<span class="mustSpan">[*]</span>：</th>
				<td align="left" >
					<input type="text" 
						   name="title" 
						   id="title"
						   title="会议主题"
						   maxlength="80"  
						   size="50"
						   value="${mo.title}" class="required" />
				</td>
				<th width="150" style="text-align: right">会议名称<span class="mustSpan">[*]</span>：</th>
				<td align="left">
					<input type="text"
						   name="name"
						   id="name"
						   maxlength="80"
						   value="${mo.name}"
						   title="会议名称"
						   class="required"
						   size="50" /> 
				</td>
			</tr>
			
			<tr>
				<th width="150" style="text-align: right">会议室<span class="mustSpan">[*]</span>：</th>
				<td>
					<input type="text" 
						   id="meetingRoomId"
						   name="meetingRoomId"
						   title="会议室" 
						   maxlength="50" 
						   size="50"
						   autoWidth="210"
	      				   onkeydown="onKeyDownEvent();" 
	      				   onkeyup="onKeyUpEvent();" 
	      				   onclick="onPopDivClick(this);" 
	      				   autoid=751
	      				   grid=true listWidth="500"
						   value="${mo.meetingRoomId}" class="required" />
				</td>
				<th width="150" style="text-align: right">申请部门<span class="mustSpan">[*]</span>：</th>
				<td align="left">
				 <input type="hidden" name="departmentId" id="departmentId"    value="${userSession.userAuditDepartId}"  />
				 <input type="text" name="departmentName" id="departmentName" value="${userSession.userAuditDepartName}" maxlength="80" size="30" readonly="readonly"/>
				</td>
			</tr>
			
			<tr>
				<th width="150" style="text-align: right">会议开始时间<span class="mustSpan">[*]</span>：</th>
				<td >
					<input type="hidden" id="startTime" name="startTime" >
					<span id="startTimes" ></span>
				</td>
				<th width="150" style="text-align: right">会议结束时间<span class="mustSpan">[*]</span>：</th>
				<td >
					<input type="hidden" id="endTime" name="endTime" >
					<span id="endTimes" ></span>
				</td>
				
			</tr>
			<tr>
				<th width="150" style="text-align: right">姓名<span class="mustSpan">[*]</span>：</th>
				<td align="left" >
					<input type="text" 
						   name="title" 
						   id="title"
						   title="姓名"
						   maxlength="80"  
						   size="20"
						   value="${userSession.userName}" class="required" readonly="readonly"/>
				</td>
				<th width="150" style="text-align: right">手机</th>
				<td align="left">
					<input type="text"
						   name="name"
						   id="name"
						   maxlength="80"
						   value="${userSession.userMobilePhone}"
						   title="手机"
						   
						   size="20" readonly="readonly"/> 
				</td>
			</tr>
			<tr>
				<th width="150" style="text-align: right">楼层</th>
				<td align="left" >
					<input type="text" 
						   name="title" 
						   id="title"
						   title="楼层"
						   maxlength="80"  
						   size="30"
						   value="${userSession.userFloor}"  readonly="readonly"/>
				</td>
				<th width="150" style="text-align: right">楼层楼秘<span class="mustSpan">[*]</span>：</th>
				<td align="left" >
					<input type="text" 
						   name="auditUserId" 
						   id="auditUserId"
						   title="楼层楼秘"
						   maxlength="80"  
						   size="50"
						   autoWidth="210"
	      				   onkeydown="onKeyDownEvent();" 
	      				   onkeyup="onKeyUpEvent();" 
	      				   onclick="onPopDivClick(this);" 
	      				   autoid=80001
	      				   listWidth="300"
						   value="${mo.auditUserId}" class="required" />
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
	      				   onkeydown="onKeyDownEvent();" 
	      				   onkeyup="onKeyUpEvent();" 
	      				   onclick="onPopDivClick(this);" 
	      				   onselect="
	      				    var map={'横幅':'spanNeedCheck1','桌签':'spanNeedCheck','门口欢迎词':'spanNeedCheck2'};
	      				   for(var key in  map){
	      				       if(this.value.indexOf(key)>-1){
	      				         document.getElementById(map[key]).style.display='inline';
	      				       }else{
	      				         document.getElementById(map[key]).style.display='none';
	      				       }
	      				   }
	      				   /*
	      				   if(this.value.indexOf('横幅','桌签','门口欢迎词')>-1){
	      				  
	      				          document.getElementById('spanNeedCheck2').style.display='inline'
   								 document.getElementById('spanNeedCheck').style.display='inline'
    							document.getElementById('spanNeedCheck1').style.display='inline'
	      				   }else if(this.value.indexOf('横幅','桌签')>-1){
	      				   		 document.getElementById('spanNeedCheck2').style.display='none'
   								 document.getElementById('spanNeedCheck').style.display='inline'
    							document.getElementById('spanNeedCheck1').style.display='inline'
	      				   }else if(this.value.indexOf('桌签','门口欢迎词')>-1){
	      				          document.getElementById('spanNeedCheck2').style.display='inline'
   								 document.getElementById('spanNeedCheck').style.display='inline'
    							document.getElementById('spanNeedCheck1').style.display='none'
	      				   }else if(this.value.indexOf('横幅','门口欢迎词')>-1){
	      				   			 document.getElementById('spanNeedCheck2').style.display='inline'
   								 document.getElementById('spanNeedCheck').style.display='none'
    							document.getElementById('spanNeedCheck1').style.display='inline'
	      				   }else if(this.value.indexOf('横幅')>-1){
	      				           document.getElementById('spanNeedCheck2').style.display='none'
   								 document.getElementById('spanNeedCheck').style.display='none'
    							document.getElementById('spanNeedCheck1').style.display='inline'
	      				   }else if(this.value.indexOf('门口欢迎词')>-1){
	      				         document.getElementById('spanNeedCheck2').style.display='inline'
   								 document.getElementById('spanNeedCheck').style.display='none'
    							document.getElementById('spanNeedCheck1').style.display='none'
	      				   }else if(this.value.indexOf('桌签')>-1){
	      				       document.getElementById('spanNeedCheck2').style.display='none'
   								 document.getElementById('spanNeedCheck').style.display='inline'
    							document.getElementById('spanNeedCheck1').style.display='none'
	      				   }else{
   document.getElementById('spanNeedCheck').style.display='none'
   document.getElementById('spanNeedCheck1').style.display='none'
   document.getElementById('spanNeedCheck2').style.display='none'
   }
   */
	      				   "
	      				   autoid=10022
	      				   multiselect="true" 
	      				   refer="requirementsType"
						   value="${mo.requirements}" />
				</td>
			</tr>
			
			<tr id="spanNeedCheck" style="display:none;">
				<th width="150" style="text-align: right">桌签接待描述：</th>
				<td colspan="3">
					
					<input type="text" 
						   name="describe" 
						   id="describe"
						   
						   maxlength="80"  
						   size="50"
						   class="required" />
				</td>
			</tr>
						<tr id="spanNeedCheck1" style="display:none;">
				<th width="150" style="text-align: right">横幅接待描述：</th>
				<td colspan="3">
					
					<input type="text" 
						   name="describe1" 
						   id="describe1"
						   
						   maxlength="80"  
						   size="50"
						   class="required" />
				</td>
			</tr>			<tr id="spanNeedCheck2" style="display:none;">
				<th width="150" style="text-align: right">门口欢迎词接待描述：</th>
				<td colspan="3">
					
					<input type="text" 
						   name="describe2" 
						   id="describe2"
						   
						   maxlength="80"  
						   size="50"
						   class="required" />
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
	      				   onkeydown="onKeyDownEvent();" 
	      				   onkeyup="onKeyUpEvent();" 
	      				   onclick="onPopDivClick(this);" 
	      				   autoid=10022
	      				   multiselect="true" 
	      				   refer="deviceDescribe"
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
						   size="110" />
				</td>
			</tr>
			
			<tr>
				<th width="150" style="text-align: right">参与人</th>
				<td colspan="3">
					<textarea name="joinUserName" id="joinUserName" style="width: 85%;height: 50px;">${joinUserName}</textarea>
					<span style="margin-left: 15px"><a href="#" onclick="show_selectUser('joinUserName','joinUser')">添加联系人</a> </span><br>
					
				</td>
			</tr>
			
			<tr>
				<th width="150" style="text-align: right">会议描述<span class="mustSpan">[*]</span>：</th>
				<td colspan="3">
					<textarea name="describes" id="describes" style="width: 90%;height: 100px;" class="required">${mo.describes }</textarea>
				</td>
			</tr>
			
		</table>


<input type="hidden" id="uuid" name="uuid" value="${mo.uuid}" >
<input type="hidden" id="opt" name="opt" value="${opt}" >
<input type="hidden" id="joinUser" name="joinUser" value="${joinUser}">
<input type="hidden" id="joinRole" name="joinRole" value="${joinRole}">
<input type="hidden" id="joinUserDepartmentId" name="joinUserDepartmentId" value="${joinUserDepartmentId}" >

</form>
</body>

<script type="text/javascript">



//初始化
function init() {	
	//初始化日期控件
	
	 var txt1 = new Ext.Container({
	 	id:'id_startTimes',
        layout: "column",
        readonly:true,
        items: [
        { xtype: "datetimefield" ,emptyText:'请选择时间'}]
    });
 
	 txt1.render("startTimes");	
	 
	  var txt2 = new Ext.Container({
	  	id:'id_endTimes',
        layout: "column",
        readonly:true,
        items: [
        { xtype: "datetimefield" ,emptyText:'请选择时间'}]
    });
	 txt2.render("endTimes");	
	 
	var startTimeObj = document.getElementById("id_startTimes").childNodes[0].childNodes[0].childNodes[0];
	//startTimeObj.value = "2012-09-28 18:07:02";
	startTimeObj.value = "${mo.startTime}";
	
	// 结束时间
	var endTimeObj = document.getElementById("id_endTimes").childNodes[0].childNodes[0].childNodes[0];
	endTimeObj.value = "${meetingorder.endTime}";


	
	
}


//ext初始化
Ext.onReady(extInit);
Ext.onReady(init);


//保存项目
function save() {
	
	var date = new Date();
	
	var startTimes = document.getElementById("id_startTimes").childNodes[0].childNodes[0].childNodes[0].value;
	var endTimes = document.getElementById("id_endTimes").childNodes[0].childNodes[0].childNodes[0].value;


	if(startTimes=="" || startTimes==null || startTimes=="请选择时间"){
		alert("会议开始时间不能为空！");
		document.getElementById("id_startTimes").childNodes[0].childNodes[0].childNodes[0].focus();
		return;
	}else{
		 var dt = new Date(startTimes.replace(/-/,"/"));  
		    var now=new Date();
		    if(dt<now){
		    alert ("会议开始时间不能小于当前日期");
		    return false;
		    }  
	}
	if(endTimes=="" || endTimes==null || endTimes=="请选择时间"){
		alert("会议结束时间不能为空！");
		document.getElementById("id_endTimes").childNodes[0].childNodes[0].childNodes[0].focus();
		return;
	}

	//判断日期是否正确
	if(startTimes > endTimes){
		alert("开始时间不能大于结束时间!");
		return;
	}
	
	document.getElementById("startTime").value = startTimes;
	document.getElementById("endTime").value = endTimes;
   
	var joinUser = document.getElementById("joinUser").value;
	var joinRole = document.getElementById("joinRole").value;

	if(joinUser==""  && joinRole==""){
		alert("请选择参与人！");
		return;
	}else{
		if (!formSubmitCheck('thisForm')){
	  		return ;
	  	}
		var form = document.getElementById("thisForm");
		form.action = "${pageContext.request.contextPath}/meetingOrder.do?method=save";
		
		showWaiting();
	
		form.submit();
	}
}

	 
</script>
</html>