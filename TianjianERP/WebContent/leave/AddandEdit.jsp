<%@page import="com.matech.framework.listener.UserSession"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<%
	UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
%>
<title>请假申请</title>
<script type="text/javascript">


Ext.onReady(function (){
	new Ext.Toolbar({
			renderTo: "divBtn",
			height:30,
			defaults: {autoHeight: true,autoWidth:true},
	       items:[{ 
	           id:'saveBtn',
	           text:'保存',
	           icon:'${pageContext.request.contextPath}/img/save.gif' ,
	           handler:function(){
			   		mySubmit();
			   }
	     	 },'-',{ 
	        text:'返回',
	        icon:'${pageContext.request.contextPath}/img/back.gif', 
	        handler:function(){
				window.history.back();
			}
	  	},'->']
	});
	
	new ExtButtonPanel({
		desc:'',
		renderTo:'sbtBtn',
		items:[
		{
            text: '保存',
            id:'appSubmit23', 
            icon:'${pageContext.request.contextPath}/img/receive.png' ,
            scale: 'large',
            handler:function(){
            	mySubmit();
   			}
           },{
            text: '返回',
            id:'appSubmit25', 
            icon:'${pageContext.request.contextPath}/img/back_32.png' ,
            scale: 'large',
               handler:function(){
            	  //closeTab(parent.tab);
					window.history.back();
   			   }
           }
        ]  
	});    
	 
});
</script>

<style type="text/css">

.before{
	border: 0px;
	background-color: #FFFFFF !important;
}

.data_tb {
	background-color: #ffffff;
	text-align:center;
	margin:0 0px;
	width:70%;
	border:#8db2e3 1px solid; 
	BORDER-COLLAPSE: collapse; 
	margin-top: 20px;
}
.data_tb_alignright {	
	BACKGROUND: #e4f4fe; 
	white-space:nowrap;
	padding:5px;
	border-top: #8db2e3 1px solid;
	border-left: #8db2e3 1px solid;
	BORDER-RIGHT: #8db2e3 1px solid;
	BORDER-BOTTOM: #8db2e3 1px solid; 
	height:30px;
	background-color: #d3e1f1;
	width:15%;
	font-size: 13px;
	font-family:"宋体";
}
.data_tb_content {
	PADDING-LEFT: 2px; 
	BORDER-TOP: #8db2e3 1px solid; 
	BORDER-LEFT: #8db2e3 1px solid;
	BORDER-RIGHT: #8db2e3 1px solid;
	BORDER-BOTTOM: #8db2e3 1px solid;  
	WORD-BREAK: break-all; 
	TEXT-ALIGN: left; 
	WORD-WRAP: break-word
}

</style>
</head>
<body leftmargin="0" topmargin="0" >
<div id="divBtn" ></div>
	<div style="height:92%;overflow: auto;">
<form name="thisForm" method="post" action="" id="thisForm" > 
	<input type="hidden" id="uuid" name="uuid" value="${leave.uuid}">
	<span class="formTitle" ><br>${ctype}申请</span>
	<table border="0"  style="line-height: 28px"   class="data_tb" align="center">
		<tr>
			<td colspan="4" style="height: 15px;" class="data_tb_alignright"> 
				${ctype}申请
			</td>
		</tr>
		<tr>
			<td class="data_tb_alignright" align="right" >请假人：</td>
			<td class="data_tb_content"  nowrap="nowrap" width="35%"><input type="text" style="border-style:none" id="name" class="required"
				maxlength="30"  name="name" value="${userSession.userName}" readonly="readonly" />
				<div id="checkCustomerDiv"> </div>
				</td>
			<td class="data_tb_alignright" align="right">申请时间：</td>
			<td class="data_tb_content"><input type="text" id="applyDate" class="required"
				maxlength="50" name="applyDate" style="border-style:none" value="${todateTime }${leave.applyDate}" readonly="readonly"/></td>
		</tr>
		<tr>
			<td class="data_tb_alignright" align="right">请假类型：</td>
			<td class="data_tb_content" colspan="3">
			<c:choose>
				<c:when test="${ctype =='销假'}">
					<input type="text" id="leaveTypeId" class="required"
					maxlength="30"  name="leaveTypeId" value="${leave.leaveTypeId}" 
					onkeydown="onKeyDownEvent();"
					onkeyup="onKeyUpEvent();"
					onclick="onPopDivClick(this);"
					noinput=true
					autoid=865
					onchange="checkLeaveType()"
					style="border-style:none" />
				</c:when>
				<c:otherwise>
				<input type="text" id="leaveTypeId" class="required"
					maxlength="30"  name="leaveTypeId" value="${leave.leaveTypeId}" 
					onkeydown="onKeyDownEvent();"
					onkeyup="onKeyUpEvent();"
					onclick="onPopDivClick(this);"
					noinput=true
					autoid=865
					onchange="checkLeaveType()"/>
					</c:otherwise>
			</c:choose>
			<input type="hidden" id="sex" name="sex" value="${sex}">
			<span id="checkUseTypeDiv"></span>	
			</td>
		</tr>
		<tr>
			<td class="data_tb_alignright" align="right">请假开始时间：</td>
			<td class="data_tb_content">
				<input type="text" id="leaveStartTime" maxlength="40"  readonly="readonly" name="leaveStartTime"  value="${leave.leaveStartTime}" <c:if test="${ctype =='请假'}"> onclick="setday(this)" </c:if> >
				<span id="leaveStartTimeSpan"></span>
				</td>
			<td class="data_tb_alignright" align="right">请假结束时间：</td>
			<td class="data_tb_content" nowrap="nowrap">
				<input type="text" id="leaveEndTime" maxlength="3" readonly="readonly" size="20" name="leaveEndTime" value="${leave.leaveEndTime}" <c:if test="${ctype =='请假'}"> onclick="setday(this)" </c:if>  />
				<span id="leaveEndTimeSpan" ></span>
				<span id="leaveEndTimeMsg" ></span>	
				<input type="hidden" id="leaveHourCount" name="leaveHourCount" value="${leave.leaveHourCount}" >
			</td>
		</tr>
		<tr >
			<td class="data_tb_alignright" align="right">请假事由：</td>
			<td class="data_tb_content"  nowrap="nowrap"colspan="3">
			<c:choose>
				<c:when test="${ctype =='销假'}">
					${leave.memo}
				</c:when>
				<c:otherwise>
					<textarea style="width: 100%;height:60;overflow:visible;" name="memo" id="memo" class="required" >${leave.memo}</textarea>
				</c:otherwise>
			</c:choose>
			</td>
		</tr>
		<tr style="display: none;" id="destroyDate">
			<td class="data_tb_alignright" align="right">实际请假开始时间：</td>
			<td class="data_tb_content">
				<input type="text" id="destroyStartTime"  maxlength="40"  name="destroyStartTime" value="${leave.destroyStartTime}" onclick="setday(this);" />
				<span id="destroyStartTimeSpan"></span>
				</td>
			<td class="data_tb_alignright" align="right">实际请假结束时间：</td>
			<td class="data_tb_content" nowrap="nowrap">
				<input type="text" id="destroyEndTime" maxlength="3" size="20" name="destroyEndTime" value="${leave.destroyEndTime}" onclick="setday(this);" />
				<span id="destroyEndTimeSpan" ></span>
				<span id="destroyHourCountMsg" ></span>
				<input type="hidden" id="destroyHourCount" name="destroyHourCount" value="${leave.destroyHourCount }">	
			</td>
		</tr>
	</table>
	<c:if test="${ctype =='请假'}">
		<center style="padding-top: 5px;">填写完请假开始时间与请假结束时间，请点击<a href="#" onclick="dateBad()" style="font-weight: 900;">自动计算请假小时数</a>，计算请假时长！</center>
	</c:if>
	<c:if test="${ctype =='销假'}">
		<center style="padding-top: 5px;">请选择实际请假开始时间与实际请假结束时间，请点击<a href="#" onclick="dateBad()" style="font-weight: 900;">自动计算实际请假小时数</a>，计算请假时长！</center>
	</c:if>
		<pre style="margin-top: 15px;margin-left: 20px;">
		         
			提示：a.员工因急诊无法到公司办理请假手续的，需在上班前30分钟内电话通知部门经理及人力资源部，并在
			        病假复职后第一个工作日内补办相关病假手续。
			      b.申请婚嫁的员工应在结婚登记日起一年内提出休假申请，经所在部门经理核准后报分管副总经理审批，
			        请假时需提交结婚证复印件、《员工请假审批表》备案。
		         	      c.年休假提前至少30个工作日提出书面申请并获批准。
		    
		          注意：如果是跨节假日、双休等请假。请分段请假！
		</pre>
		<center><div id="sbtBtn" ></div></center>
		<br>
		<input type="hidden" id="leaveHourCount" name="leaveHourCount" >
		<input type="hidden" id="leaveBeginTime" name="leaveBeginTime">
 </form>
	</div>


<script type="text/javascript">
new Validation('thisForm');

//mian 求 时间差
function timeDifference(){
	//alert(myTimeCount('2011-01-01 7:00:00','2011-01-02 7:30:00','08:00:00','12:00:00','14:00:00','18:00:00'));
	
	return myCount('2011-01-01 07:00:00','2011-01-03 08:30:00','08:00:00','12:00:00','14:00:00','18:00:00');
}

//s1:请假起始日期及时间,e1：请假结束日期及时间,morning1,morning2,afternoon1,afternoon2：工作上午上班时间，上午下班时间；下午上班时间，下午下班时间
//alert(myCount('2011-01-01 7:00:00','2011-01-03 8:30:00','08:00:00','12:00:00','14:00:00','18:00:00'));
function myCount(s1,e1,morning1,morning2,afternoon1,afternoon2){
	if (s1 == "请选择开始日期" || e1 == "请选择结束日期"){
		return -1;
	}
	if(s1>=e1){
		alert('请假结束时间必须大于起始时间');
		return -1;
	}
	var dayCount,time1,time2;
	var myTimeHours ="";
	dayCount=days_between(e1,s1);
	if (dayCount>0){
		//请假隔了至少1天
		//起始天当天小时数 + 结束天当天小时数 + （天数-1）*每日工作时数
		myTimeHours = myTimeCount(s1,afternoon2,morning1,morning2,afternoon1,afternoon2)
				+myTimeCount(morning1,e1,morning1,morning2,afternoon1,afternoon2)
				+(dayCount-1)*(strToTime(morning2)-strToTime(morning1)+strToTime(afternoon2)-strToTime(afternoon1))/3600;
		//myTimeHours = myTimeHours.substr(0,myTimeHours.indexOf(".")+3);
		//alert(myTimeHours);		
		//return myTimeHours;
	}else{
		//请假在当天
		myTimeHours = myTimeCount(s1,e1,morning1,morning2,afternoon1,afternoon2);
	}
		var myTimeHours = myTimeHours+"" ;
		myTimeHours = myTimeHours.substr(0,myTimeHours.indexOf(".")+3);
		return myTimeHours;
}


function strToDate(str){
	
	var arys1= new Array(),arys= new Array();
	arys1=str.split(' ');
	arys=arys1[0].split('-');
	var newDate=new Date(arys[0],parseInt(arys[1], 10) - 1,arys[2]);
	return newDate;
} 

function days_between(date1,date2){
  var ONE_DAY=1000*60*60*24;
  var date1_ms = strToDate(date1).getTime();
  var date2_ms = strToDate(date2).getTime();
  
  var difference_ms = Math.abs(date1_ms- date2_ms);
  return Math.round(difference_ms/ONE_DAY);
}

function strToTime(str){
	if (str==null){
		return 0;
	}
	var arys1= new Array(),arys= new Array();
	if (str.indexOf(' ')>=0){
		arys1=str.split(' ');
		arys=arys1[1].split(':');
	}else{
		arys=str.split(':');
	}
	
	return arys[0]*3600+arys[1]*60+arys[2]*1;

} 


function myTimeCount(s1,e1,morning1,morning2,afternoon1,afternoon2){
	//这里传入的时间全部是当天时分秒字符串，用:分隔
	var ts1,te1,tm1,tm2,ta1,ta2;
	ts1=strToTime(s1);
	te1=strToTime(e1);   
	tm1=strToTime(morning1);   
	tm2=strToTime(morning2);
	ta1=strToTime(afternoon1);
	ta2=strToTime(afternoon2);
	
	var nowstart,nowend;
	if (te1<=tm1){
		return 0;
	}
	if(ts1>=ta2){
		return 0;
	}
	if(ts1<=tm2){
		//起始时间落在上午区间,取大的作为起点
		nowstart=myMax(ts1,tm1);
		if (te1<=ta1){
			//alert(1);
			//结束时间没在下午区间
			nowend=myMin(te1,tm2);
			return (nowend-nowstart)/3600;
		}else{
			//alert(2);
			//结束时间在下午区间
			nowend=myMin(te1,ta2); //取最小值
			return ((tm2-nowstart)+(nowend-ta1))/3600;
		}
	}else{
		//起始之间落在中午或下午区间
		//取大的做起点
		nowstart=myMax(ts1,ta1);
	     if(te1>=ta2){
			//alert(3);
			//超过下班时间，不算请假
		
		 	return (ta2-nowstart)/3600;
		}else{
			//alert(4);
			nowend=myMin(te1,ta2); //取最小值
			return (nowend-nowstart)/3600;
		}
	}
	
	return 0;
}

function myMax(a,b){
	if (a>b) 
		return a;
	else 
		return b;
}

function myMin(a,b){
	if (a>b) 
		return b;
	else 
		return a;
}


 var fWorkTime= "${fWorkTime}"; //上午上班时间
 var fOffDutyTime= "${fOffDutyTime}"; //上午下班时间 
 var arvoWorkTime= "${arvoWorkTime}";//下午上班时间
 var arvoOffDutyTime= "${arvoOffDutyTime}";//下午下班时间
 
 function getHour(hours){
	 return  hours.substring(0,2);
 }
 
 function getMinute(minute){
	 return  minute.substring(0,2);
 }
  
 function dateBad(){
	 //var dt1 = "2009-11-5 10:30";
	 //var dt2 = "2009-11-5 11:8";
	//var dt1 = document.getElementById("id_startTimes").childNodes[0].childNodes[0].childNodes[0].value;
	//var dt2= document.getElementById("id_endTimes").childNodes[0].childNodes[0].childNodes[0].value;
	var dt1,dt2;
	if("${ctype}" =="请假"){	
		dt1 = document.getElementById("leaveStartTime").value;
		dt2= document.getElementById("leaveEndTime").value;
	}else if("${ctype}" =="销假"){
		dt1 = document.getElementById("destroyStartTime").value;
		dt2= document.getElementById("destroyEndTime").value;
		
	}
 	if(dt1 =="" && dt2==""){
		return ;
	}
	var timeHours = myCount(dt1,dt2,fWorkTime,fOffDutyTime,arvoWorkTime,arvoOffDutyTime);
	if(timeHours =="Na"){
		timeHours = "0";
	}
	if("${ctype}" =="请假"){	
		document.getElementById("leaveEndTimeMsg").innerHTML="&nbsp;累计：<font color=blue>"+timeHours+"</font>小时";
		document.getElementById("leaveHourCount").value=timeHours;
	}else if("${ctype}" =="销假"){
		document.getElementById("destroyHourCountMsg").innerHTML="&nbsp;累计：<font color=blue>"+timeHours+"</font>小时";
		document.getElementById("destroyHourCount").value=timeHours;
	}
	
 }
//初始化 申请 日期
function init() {	
	//初始化日期控件
	if("${ctype}" =="请假"){	
		 
		/*var txt1 = new Ext.Container({
		 	  id:'id_startTimes',
		      layout: "column",
		      items: [
		      { xtype: "datetimefield" ,emptyText:'请选择开始时间'}]
	 	  });
		 txt1.render("leaveStartTimeSpan");	
		 
		  var txt2 = new Ext.Container({
		  	id:'id_endTimes', 
	      	layout: "column",
	      	items: [
	     	 {xtype: "datetimefield" ,emptyText:'请选择结束时间',listHeight : 1000}]
	  	  });
		 txt2.render("leaveEndTimeSpan");	*/
		// 开始时间
		//var startTimeObj = document.getElementById("id_startTimes").childNodes[0].childNodes[0].childNodes[0];
		if("${leave.leaveStartTime}" !=""){
			document.getElementById("destroyStartTime").value = "${leave.leaveStartTime}";
		}
		// 结束时间
		if("${leave.leaveEndTime}" !=""){
			document.getElementById("destroyEndTime").value = "${leave.leaveEndTime}";
		}
		//endTimeObj.readOnly = true;
			
	}else{
		//setObjDisabled("leaveTypeId");
		//document.getElementById("memo").onpropertychange=null; //去掉 事件
		//document.getElementById("memo").readOnly=true;	
		//document.getElementById("leaveStartTimeSpan").innerHTML= "${leave.leaveStartTime}";
		//document.getElementById("leaveEndTimeSpan").innerHTML= "${leave.leaveEndTime}";
		//document.getElementById("leaveTypeId").style.backgroundColor ="transparent";
	}
		if("${leave.leaveHourCount}" !=""){
			document.getElementById("leaveEndTimeMsg").innerHTML="&nbsp;累计：<font color=blue>${leave.leaveHourCount}</font>小时";
		}
}

	//初始化销假日期控件
function initDestroy() {	
		 var txt1 = new Ext.Container({
		 	  id:'id_startTimes',
		      layout: "column",
		      items: [
		      { xtype: "datetimefield" ,emptyText:'请选择实际开始时间'}]
	 	  });
		 txt1.render("destroyStartTimeSpan");	
		 
		  var txt2 = new Ext.Container({
		  	id:'id_endTimes', 
	      	layout: "column",
	      	items: [
	     	 { xtype: "datetimefield" ,emptyText:'请选择实际结束时间'}]
	  	  });
		 txt2.render("destroyEndTimeSpan");	
		
		// 开始时间
		var startTimeObj = document.getElementById("id_startTimes").childNodes[0].childNodes[0].childNodes[0];
		startTimeObj.readOnly = true;
		
		// 结束时间
		var endTimeObj = document.getElementById("id_endTimes").childNodes[0].childNodes[0].childNodes[0];
		endTimeObj.readOnly = true;
}


if("${ctype}" == "销假"){
	document.getElementById("destroyDate").style.display="block";
	setObjDisabled("leaveTypeId");
	//initDestroy(); //初始化 销假日期控件
	//document.getElementById("leaveTypeId").className="before";
	//document.getElementById("leaveTypeId").style.readOnly=true;
	
}

//ext初始化
Ext.onReady(init);
function checkLeaveType(){
	 var leaveTypeId = document.getElementById("leaveTypeId").value;
	 var sex = document.getElementById("sex").value;
	 if(leaveTypeId==5 && sex=='男'){
	 	document.getElementById("checkUseTypeDiv").innerHTML ="对不起，您不能请产假，请选择陪产假！";
	 	return;
	 }
	 if(leaveTypeId == 11 && sex == '女'){
	 	document.getElementById("checkUseTypeDiv").innerHTML ="对不起，您不能请陪产假，请选择产假";
	 	return;
	 }
	 if(leaveTypeId == ""){
		 return ;
	 }

	   posturl = "${pageContext.request.contextPath}/leave.do?method=checkUse";
     
	 Ext.Ajax.request({
			method:'POST',
			params : { 
				leaveTypeId : leaveTypeId
			},
			url:"${pageContext.request.contextPath}/leave.do?method=checkUse",
			success:function (response,options) {
				var request = response.responseText;
	          	 if(request !=""){
					 document.getElementById("checkUseTypeDiv").innerHTML =request;
				 }else{
					 document.getElementById("checkUseTypeDiv").innerHTML="";
					 return ;
				 }
			},
			failure:function (response,options) {
				alert("后台出现异常,获取文件信息失败!");
			}
		});
}

function mySubmit() {
	if (!formSubmitCheck('thisForm')) return ;
	//var startDate = document.getElementById("id_startTimes").childNodes[0].childNodes[0].childNodes[0].value;
	//var endTimeObj = document.getElementById("id_endTimes").childNodes[0].childNodes[0].childNodes[0].value;
	
	var startDate = document.getElementById("leaveStartTime").value;
	var endTimeObj = document.getElementById("leaveEndTime").value;
	
	if(startDate == "" || startDate =="请选择开始时间" || startDate=="请选择实际开始时间"){
		alert("开始日期不能为空！");
		return ;
	}/*else{
		if("${ctype}" != "销假"){
			 var dt = new Date(startDate.replace(/-/,"/"));  
			    var now=new Date();
			    if(dt<now){
			    alert ("开始时间不能小于当前日期");
			    return false;
			  }  
		}
	}*/
	
	if(endTimeObj == "" || endTimeObj=="请选择结束时间" || endTimeObj=="请选择实际结束时间"){
		alert("结束日期不能为空！");
		return ;
	}else{
		    var endDate = new Date(endTimeObj.replace(/-/,"/"));  
		    var beginDate=new Date(startDate.replace(/-/,"/"));
		    if(beginDate>=endDate){
			    alert ("结束时间不能大于或等于开始日期");
			    return false;
		 	 }  
	}
	if("${ctype}" == "销假"){ //销假
		startDate = document.getElementById("destroyStartTime").value ;
		endTimeObj = document.getElementById("destroyEndTime").value ;
		
		var timeHours = myCount(startDate,endTimeObj,fWorkTime,fOffDutyTime,arvoWorkTime,arvoOffDutyTime);
		document.getElementById("destroyHourCountMsg").innerHTML="&nbsp;累计：<font color=blue>"+timeHours+"</font>小时";
		document.getElementById("destroyHourCount").value=timeHours;
		document.thisForm.action="${pageContext.request.contextPath}/leave.do?method=destroySave";
	}else{ //新增 和修改
	   
		document.getElementById("leaveStartTime").value = startDate;
		document.getElementById("leaveEndTime").value = endTimeObj;
		
		var timeHours = myCount(startDate,endTimeObj,fWorkTime,fOffDutyTime,arvoWorkTime,arvoOffDutyTime);
		document.getElementById("leaveEndTimeMsg").innerHTML="&nbsp;累计：<font color=blue>"+timeHours+"</font>小时";
		document.getElementById("leaveHourCount").value=timeHours;
		document.thisForm.action="${pageContext.request.contextPath}/leave.do?method=add";
		var leaveTypeId = document.getElementById("leaveTypeId").value;
		var sex = document.getElementById("sex").value;
		var leaveHourCount = document.getElementById("leaveHourCount").value;
		var uuid = document.getElementById("uuid").value;
	    var url ="${pageContext.request.contextPath}/leave.do?method=checkUse2";
	    var requestString = "leaveTypeId="+leaveTypeId+"&leaveHourCount="+leaveHourCount+"&uuid="+uuid;

	    if(leaveTypeId==5 && sex=='男'){
	 		alert("对不起，您不能请产假，请选择陪产假！");
	 		return;
	   }
	   if(leaveTypeId == 11 && sex == '女'){
	 	   alert("对不起，您不能请陪产假，请选择产假");
	 	   return;
	   }		
		var request= ajaxLoadPageSynch(url,requestString);	
	 	if(request == "不能请假了"){
		   alert("您当月的次数或者时间已经超出了");
		   return;
		}
	 	
	 if(!checkDay(leaveTypeId,document.getElementById("leaveStartTime").value)) return ;
	}
	document.thisForm.submit();
	
}

function checkDay(leaveTypeId,startDate){
	if(leaveTypeId == "" ||  startDate == ""){
		return ;
	}
	var url ="${pageContext.request.contextPath}/leave.do?method=checkDay";
	var requestString = "leaveTypeId="+leaveTypeId+"&leaveTypeId="+leaveTypeId+"&startDate="+startDate+"&rand="+Math.random();
	var request= ajaxLoadPageSynch(url,requestString);	
	
	var check = "1",checkTime = "0";
	if(request !=""){
		check = request.split("~")[0];
		checkTime = request.split("~")[1];
		if(check == "1"){
			if(checkTime !="0"){
				alert("请假开始时间必须提前"+checkTime+"天进行申请!");
				return false;
			}
		}
	}
	return true;
}
</script>

</body>
</html>
