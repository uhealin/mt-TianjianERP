<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<style type="text/css">

	fieldset {margin: 10px;}
	#tTable {margin-top:10px;border:#d7e2f3 1px solid;border-collapse:collapse;}
	#tTable td,th {
		padding: 5 5 5 1px;text-align: left;white-space:nowrap;border-top:#d7e2f3 1px solid;border-left: #d7e2f3 1px solid;height:30px;
	}
	#tTable th{background-color: #f8f9f9;}
	#tTable td{background-color: #f8f9f9;}
	#tTable input {border:1px solid #d7e2f3;}
	
</style>
 
<script type="text/javascript">


Ext.onReady(function (){
	new Ext.Toolbar({
			renderTo: "divBtn",
			height:30,
			defaults: {autoHeight: true,autoWidth:true},
	        items:[
		        { 
			        text:'返回',
			        icon:'${pageContext.request.contextPath}/img/back.gif', 
			        handler:function(){
						f_back();
					}
	  			},'-',
		     	{ 
		           text:'保存',
		           icon:'${pageContext.request.contextPath}/img/save.gif' ,
		           handler:function(){
		        	   if (!formSubmitCheck('thisForm')) return;
						  f_save();
					   }
		     	},'->'
	  		]
	});
	
});
</script>
</head>
<body>
<div id="divBtn" ></div>
<div style="height:expression(document.body.clientHeight-27);width:100%;overflow: auto;">
<form id="thisForm" name="thisForm" method="post" action="" style="background-color: #ecf2f2;border: 1px solid #AEC9D3;height: 100%">

	<fieldset>
		<legend>会议室信息</legend>
		<table cellpadding="1" align="center" cellspacing="1" width="100%" height="100%" id="tTable">
		
			<tr>
				<th width="150" style="text-align: right">会议室名称<span class="mustSpan">[*]</span>：</th>
				<td align="left">
					<input type="text"
						   name="name"
						   id="name"
						   maxlength="80"
						   value="${mr.name}"
						   title="必填"
						   class="required"
						   size="50" /> 
				</td>
				
				<th width="150" style="text-align: right">所属机构<span class="mustSpan">[*]</span>：</th>
				<td align="left">
					<input type="text" 
						   name="organ" 
						   id="organ"
						   title="必填"
						   maxlength="50" 
						   size="50" 
						   onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" autoid=4583 
						   value="${mr.organ}" class="required" />
				</td>
			</tr>
			
			<tr>
				<th width="150" style="text-align: right">可容纳人数：</th>
				<td>
					<input type="text"
						   id="containPerson"
						   name="containPerson"
						   value="${mr.containPerson}"
						   maxlength="20"
						   onkeyup="value=value.replace(/[^\d\.\\-]/g,'');"
						   size="50" />
				</td>
				
				<th width="150" style="text-align: right">设备情况：</th>
				<td>
					<input type="text" 
						   id="device"
						   name="device" 
						   maxlength="50" 
						   size="50"
						   autoWidth="210"
	      				   onkeydown="onKeyDownEvent();" 
	      				   onkeyup="onKeyUpEvent();" 
	      				   onclick="onPopDivClick(this);" 
	      				   autoid=10022
	      				   refer="deviceDescribe"
	      				   multiselect="true" 
						   value="${mr.device}" />
				</td>
			</tr>
			
			<tr>
				<th width="150" style="text-align: right">会议室地点：</th>
				<td colspan="3">
					<input type="text"
						   id="place"
						   name="place"
						   value="${mr.place}"
						   maxlength="50" size="70" />
				</td>
				
			</tr>
			
			<tr>
				<th width="150" style="text-align: right">会议室描述：</th>
				<td colspan="3">
					<textarea name="describes" id="describes" style="width: 90%;height: 100px;">${mr.describes }</textarea>
				</td>
			</tr>
		</table>
	</fieldset>

<input type="hidden" id="uuid" name="uuid" value="${mr.uuid}">
<input type="hidden" id="opt" name="opt" value="${opt}">

</form>

</div>
</body>

<script type="text/javascript">
	
	new Validation("thisForm");
	
	var uuid = "${mr.uuid}";

	// 保存
	function f_save() {
		var meetingRoom = document.getElementById("name").value;
		var rs = isNameRepeat(meetingRoom);
		if(rs=="Y"){
			alert("该会议室名称已经存在,请命其他名称！");
			document.getElementById("name").select();
			return;
		}
		
		// 防止网络慢 用户点击 多次保存
		showWaiting();
		var opt = document.getElementById("opt").value;
		if(opt=="add"){
			document.thisForm.action = "${pageContext.request.contextPath}/meetingRoom.do?method=save";
			document.thisForm.submit();
		}else{
			document.thisForm.action = "${pageContext.request.contextPath}/meetingRoom.do?method=save";
			document.thisForm.submit();
		}
	}


	function f_close(){
		new BlockDiv().hidden();
		projectWin.hide();
	}
	
	
	// 会议室名称是否重复
	function isNameRepeat(name){
		var url = "${pageContext.request.contextPath}/meetingRoom.do?method=isNameRepeat";
		var requestString = "&name=" + name +"&id="+uuid;
		var result = ajaxLoadPageSynch(url,requestString);
		return result;
	}
	
	
	function f_back(){
		document.thisForm.action = "${pageContext.request.contextPath}/meetingRoom.do?method=list";
		document.thisForm.submit();
	}
</script>
</html>