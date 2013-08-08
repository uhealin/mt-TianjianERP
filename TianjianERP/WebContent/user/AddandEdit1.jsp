<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"  errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<script src="${pageContext.request.contextPath}/AS_INCLUDE/getPy.js" type="text/javascript" charset="gbk"></script>
<title>用户信息</title>

<script Language=JavaScript>

var mytab;
Ext.onReady(function (){

	 mytab = new Ext.TabPanel({
        id: "tab",
        renderTo: "divTab",
        activeTab: 0, //选中第一个 tab
        autoScroll:true,
        frame: true,
        height: document.body.clientHeight-Ext.get('divTab').getTop()-50, 
        defaults: {autoHeight: true,autoWidth:true},
        items:[
            {contentEl: "tabUser1", title: "基本信息",id:"myUser1"},
            {contentEl: "tabUser2", title: "详细信息",id:"myUser2"}
           // {contentEl: "tabUser3", title: "自定义信息",id:"myUser3"}
            
        ]
    });
    
    if('${UserOpt}' == '3'){
    	//mytab.add({contentEl: "tabUser4", title: "工作目录",id:"myUser4"});
    }
    
	var tbar_customer = new Ext.Toolbar({
		renderTo: "divBtn",
		defaults: {autoHeight: true,autoWidth:true},
 		items:[{
			text:'保存',
            icon:'${pageContext.request.contextPath}/img/save.gif',
            handler:function () {
            	if (formSubmitCheck('thisForm')){
					goAdEd();
				}
			}
      	},
<c:if test="${param.myControl != 'myControl'}">      	
      	'-',{
      	cls:'x-btn-text-icon',
<c:if test="${close == '1'}">
			text:'关闭',
			icon:'${pageContext.request.contextPath}/img/close.gif',
			handler:function(){
				closeTab(parent.tab);
			}
</c:if>	  	
<c:if test="${close != '1'}">
			text:'返回',
			icon:'${pageContext.request.contextPath}/img/back.gif',
	        handler:function(){
	        	window.history.back();
	        }
</c:if>	  	
            
      	},
</c:if>      	
      	'->']
	});
        
}) ;

var queryWin = null;
function queryWinFun(id){

	if(!queryWin) { 
		new BlockDiv().show();
		var searchDiv = document.getElementById("search") ;
	    queryWin = new Ext.Window({
			title: '图片上传',
	     	renderTo : searchWin,
	     	width: 580,
	     	height:160,
        	closeAction:'hide',
       	    listeners : {
	         	'hide':{
	         		fn: function () {
	         			new BlockDiv().hidden();
						queryWin.hide();
					}
				}
	        },
        	layout:'fit',
	    	html:searchDiv.innerHTML,
	    	buttons:[{
            	text:'确定',
          		//handler:goSearch_key
          		handler:function(){
               		upLoadSumbit();
               		queryWin.hide();
            	}
        	},{
            	text:'取消',
            	handler:function(){
               		queryWin.hide();
            	}
        	}]
	    });
    }
    queryWin.show();
}



</script>
<style>
.mySpan {
	color:#FF6600; 
	font-family:"宋体";
	font: normal;
	font-size: 9pt;
	padding: 0px;
	margin: 0px;
}

input {
	border: 1px solid #AEC9D3;
}

legend {
	color: #006699;
}

body {
	background-image:url("${pageContext.request.contextPath}/images/new_bg.gif");
	overflow:hidden ;
	
}
</style>


<script type="text/javascript">
<!--
	function init() {
		var ss = window.parent.location + "";

	    if(ss.indexOf('init.jsp') >= 0) {
	    	parent.enableNext();
	    	parent.enableBack();


	  //  	parent.setStep("step4");
	  //  	parent.setNavUrl("${pageContext.request.contextPath}/user.do?method=Edit&UserOpt=3");
	    }
	}
//-->
</script>

<script Language=JavaScript>




function chkpwd(obj)
{
	var t=obj.value;
	var id=getResult(t);

	//定义对应的消息提示
	var msg=new Array(4);
	msg[0]="初始密码";				// 小陆要求修改的
	msg[1]="低级密码强度";
	msg[2]="中级密码强度";
	msg[3]="高级密码强度";


	var col=new Array(4);
	col[0]="gray";
	col[1]="red";
	col[2]="#ff6600";
	col[3]="Green";

	//设置显示效果
	//var sWidth=150;
	//var sHeight=20;
	var Bobj=document.getElementById("chkResult");

	//Bobj.style.fontSize="17px";
	Bobj.style.color=col[id];
	//Bobj.style.width=sWidth + "px";
	//Bobj.style.height=sHeight + "px";
	//Bobj.style.lineHeight=sHeight + "px";
	//Bobj.style.textIndent="20px";
	Bobj.innerHTML=msg[id];
	}

//定义检测函数,返回0/1/2/3分别代表无效/差/一般/强

// 小陆要求修改的
function getResult(s)
{

	if(s == 1) {
		return 0 ;
	}
	
	if (s.match(/[0-9]/ig) && s.match(/[a-z]/ig) && s.length >=6)
	{
		//密码大于6位，且包含数字和字母，判断为高级强度密码
		return 3
	}
	
	if(s.length >=6) {
		//密码大于6位，判断为中级强度密码
		return 2
	}

	return 1 ;     //其它则为低级强度密码
}
// 小陆要求修改的

function deleteLine()
{
	var t=false;
	for (var i=UserDefTbody.children.length-1; i>=0 ; i-- )
	if (UserDefTbody.children[i].firstChild.firstChild.checked){
		UserDefTbody.deleteRow(i);
		t=true;
	}
	if(!t)
	{
		alert("请选定其中一列！！");
	}
}

function addLine()
{
	var obj=document.getElementById("UserDefTbodyCount");
	//增加计数


	var objTr=attachstable.insertRow(-1);
	objTr.bgColor = "#ffffff";
	var objTd=objTr.insertCell(-1);
	objTd.innerHTML="<input type=checkbox id=checkLine name=\"checkbox\">";
	//alert(objTr.innerHTML);

	objTd=objTr.insertCell(-1);
	objTd.innerHTML="<input name=\"UserDefName\" id=\"UserDefName\" class='required'  maxLength=20  title='请输入，不得为空' size=10 style=\"width: 200; height: 18\">";

	objTd=objTr.insertCell(-1);
	objTd.innerHTML="<input name=\"UserDefValue\" id=\"UserDefValue\" class='required'  maxLength=100 onKeyUp=\"if(this.value.length>100)this.value=this.value.substring(0,100);\"  title='请输入，不得为空' size=10 style=\"width: 500; height: 18\">";

	obj.value=Number(obj.value)+1;
}
</script>
</head>
<body leftmargin="0" topmargin="0" >


<div id="divBtn"></div>

<form name="thisForm" method="post" action="" id="thisForm" class="autoHeightForm">
<div id="divTab" style="overflow:auto">

	<span class="formTitle" >
	用&nbsp;&nbsp;户&nbsp;&nbsp;信&nbsp;&nbsp;息<br/><br/> 
	</span>
	
	<jodd:form bean="uservo" scope="request">
	
	<input type="hidden" id="UserOpt" value="${param.UserOpt}">
	<input type="hidden" id="userid" name="userid" value="${param.id}">
	<input type="hidden" id="getuesrname" id="getuesrname" value="">
	<input type="hidden" id="myControl" name="myControl" value="${param.myControl}">
	
	<input name="id" type="hidden" id="id">
	<input type="hidden" id="UserOpt" name="UserOpt" value="${UserOpt}" />
	<div id="tabUser1" style="padding:10 10 10 10;">

	<fieldset>
		<legend>基本信息</legend>
	
		<table width="100%" border="0" cellpadding="0" cellspacing="0" > 
		<tr><td width="60%">
		
		<table width="98%" border="0" cellpadding="0" cellspacing="0" > 
		<tr>
			<td align="right" width="40%">姓名<span class="mySpan">[*]</span>：</td>
			<td>
				<input name="name" type="text" id="name"  maxlength="20"  onBlur="query();" size="25" class="required" title="请输入，不得为空">
			</td>
		</tr>
		
	<c:choose>
	<c:when test="${UserOpt==1}">	
		
		<tr>
			<td align="right" width="20%">登录名<span class="mySpan">[*]</span>：</td>
			<td>
				<input name="loginid" maxlength="80" size=50 onBlur="goCheckUser();" type="text"  class="required" title="请输入，不得为空,且只能是字母">
				<div id="divb" style="display:none;float:left;"></div>
			</td>
		</tr>
		<tr>
			<td align="right" width="20%">登录密码<span class="mySpan">[*]</span>：</td>
			<td>
				<input name="password" maxlength="50" size=25 type="password" id="password"  onBlur="chkpwd(this)" class="required"  title="请输入密码" >
				<a href="#" onClick="javascript:window.open('user.do?method=Help','','width=600, height=650, location=no, menubar=no, status=no, toolbar=no, scrollbars=yes, resizable=yes');">密码建议:<label id="chkResult"></label></a>
			</td>
		</tr>
		<tr>
			<td align="right" width="20%">确认密码<span class="mySpan">[*]</span>：</td>
			<td>
				<input name="password_two" maxlength="50" size=25 type="password" id="password_two"  class="required validate-passwd-identical" >
			</td>
		</tr>
		
	</c:when>
	<c:when test="${UserOpt==2}">
		<tr>
			<td align="right" width="20%">登录名<span class="mySpan">[*]</span>：</td>
			<td>
				<input name="loginid" id="loginid" maxlength="80"  size=50 onBlur="checkNamewhenModify();" type="text"  class="required" title="请输入，不得为空,且只能是数字和字母">
				<div id="divb" style="display:none;float:left;"></div>
			</td>
		</tr>
		<tr>
			<td align="right" width="20%">新密码<span class="mySpan">[*]</span>：</td>
			<td>
				<input name="password"  maxlength="50" size=25 type="password" id="password"  onBlur="chkpwd(this)"   title="请输入密码" >
				<a href="#" onClick="javascript:window.open('user.do?method=Help','','width=600, height=650, location=no, menubar=no, status=no, toolbar=no, scrollbars=yes, resizable=yes');">密码建议:<label id="chkResult"></label></a>
			</td>
		</tr>
		<tr>
			<td align="right" width="20%">确认密码<span class="mySpan">[*]</span>：</td>
			<td>
				<input name="password_two" maxlength="50" size=25 type="password" id="password_two"  class="validate-passwd-identical" >
				<input type="button" name="up_pwd" id="up_pwd" value="修改密码" class="flyBT" onClick="updatePwd()">
			</td>
		</tr>
	
	</c:when>	
	<c:when test="${UserOpt==3}">
	
	   <input type="hidden" name="getloginid" id="getloginid" value="${loginid}">
		<tr>
			<td align="right" width="20%">登录名<span class="mySpan">[*]</span>：</td>
			<td>
				<input name="loginid" id="loginid" maxlength="80"  size=50 onBlur="checkNamewhenModify();" type="text"  class="required" title="请输入，不得为空,且只能是数字和字母">
				<div id="divb" style="display:none;float:left;"></div>
			</td>
		</tr>
		<tr>
			<td align="right" width="20%">旧密码<span class="mySpan">[*]</span>：</td>
			<td>
				<input type="password" name="password_old" id="password_old" size=25 maxlength="50" title="请输入，不得为空">
			</td>
		</tr>
		<tr>
			<td align="right" width="20%">登录密码<span class="mySpan">[*]</span>：</td>
			<td>
				<input name="password" maxlength="50" size=25 type="password" id="password"  onBlur="chkpwd(this)"   title="请输入密码" >
				<a href="#" onClick="javascript:window.open('user.do?method=Help','','width=600, height=650, location=no, menubar=no, status=no, toolbar=no, scrollbars=yes, resizable=yes');">密码建议:<label id="chkResult"></label></a>
			</td>
		</tr>
		<tr>
			<td align="right" width="20%">确认密码<span class="mySpan">[*]</span>：</td>
			<td>
				 <input name="password_two" maxlength="50" size=25 type="password" id="password_two" onBlur="chkPwd();">
				 <input type="button" name="up_pwd" id="up_pwd" value="修改密码" class="flyBT" onClick="updatePwd()">
			</td>
		</tr>
	
	</c:when>
	</c:choose>	
	
		<tr>
			<td align="right" width="20%">操作权限<span class="mySpan">[*]</span>：</td>
			<td>
				<input name="roles" id="roles" type="text" id="roles"  size="25"  class="required"  multiselect="true"  title="请输入，不得为空" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();"  onClick="onPopDivClick(this);" valuemustexist=true autoid=178>
			</td>
		</tr>
		<tr>
			<td align="right" width="20%">所属单位<span class="mySpan">[*]</span>：</td>
			<td>
				<input name="departid" type="text" id="departid" value="555555" size=25  maxlength="50">
			</td>
		</tr>
		
		<tr>
			<td align="right" width="20%">所属部门：</td>
			<td>
				<c:choose>
			    <c:when test="${userSession.userLoginId=='admin'}"> 
					<input name="departmentid" type="text" id="departmentid" size="25"   title="请输入，不得为空" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" noinput="true" onClick="onPopDivClick(this);" valuemustexist=true autoid=123 autoHeight=150>
			    </c:when>
			    <c:otherwise>
					<input name="departmentid" type="text" id="departmentid" size="25"   title="请输入，不得为空" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" noinput="true" onClick="onPopDivClick(this);" valuemustexist=true autoid=715 autoHeight=150>
			    </c:otherwise>
	    		</c:choose>
	    		<c:if test="${UserOpt!=3}">
	    		<c:if test="${userSession.userLoginId == 'admin'}">     
	   	 			<input type=button class="flyBT" value="增加或修改部门" onclick="openDepartment()">
	   	 		</c:if>
	    		</c:if>
			</td>
		</tr>

		<tr>
			<td align="right" width="20%">楼层：</td>
			<td>
				<input title='用户所在楼层不能为空' class="required"  name="floor" maxlength="50" type="text" id="floor"  size="25"  >
			</td>
		</tr>
		<!--
		<tr>
			<td align="right" width="20%">房间号：</td>
			<td>
				<input name="house" maxlength="50" type="text" id="house"  size="25"  title="请填写您所在的房间号">
			</td>
		</tr>
		-->
		<tr>
			<td align="right" width="20%">工位号：</td>
			<td>
				<input name="station" maxlength="50" type="text" id="station"  size="25"  title="请填写您的工位号">
			</td>
		</tr>						
		<tr>
			<td align="right" width="20%">手机：</td>
			<td>
				<input class="required validate-digits" name="mobilePhone" maxlength="11" type="text" id="mobilePhone"  size="25"  title="手机号码不能为空">
			</td>
		</tr>
			<tr>
			<td align="right" width="20%">办公电话：</td>
			<td>
				<input class="required phonenumber-wheninputed" title='请输入正确的办公电话' name="phone"  maxlength="13" type="text" id="phone"  size="25"  >
				例如：010-68888888
			</td>
		</tr>	
		</table>
		
		</td>
		<td valign="top">
		
			<img id="bill" height="120" width="100" src="${pageContext.request.contextPath}/images/noPhoto.gif"><br>
			<input type="text" id="myText" size="1" style="display:none"> <br>
			<a href="#" onclick="queryWinFun();">点击上传相片</a>
			<a href="#" onclick="deletePhto();"><img src="${pageContext.request.contextPath}/images/del.gif"></a>
		
		</td>
		</tr></table>
		
	</fieldset>
	
	</div>
	<div id="tabUser2" class="x-hide-display" style="padding:10 10 10 10;">
	
	<fieldset>
		<legend>详细信息</legend>
		
		<table width="98%" border="0" cellpadding="0" cellspacing="0" > 
		<tr>
			<td align="right" width="20%">岗位：</td>
			<td>
				<input name="post" type="text" id="post" maxlength="20" size="25" title="请输入，不得为空">
			</td>
		</tr>
		<tr>
			<td align="right" width="20%">性别：</td>
			<td>
				<input name="sex" type="radio" value="M" checked>男 <input type="radio" name="sex" value="F">女
			</td>
		</tr>
		<tr>
			<td align="right" width="20%">学历：</td>
			<td>
				<input name="educational" maxlength="20" type="text" id="educational"  size="25" title="学历">
			</td>
		</tr>
		<tr>
			<td align="right" width="20%">出生年月：</td>
			<td>
				<input name="borndate" type="text" id="borndate"   class="validate-date-cn"  title="请输入日期！" showcalendar="true">
			</td>
		</tr>

		<tr>
			<td align="right" width="20%">职级：</td>
			<td>
				<input name="rank" type="text" id="rank" maxlength="10"  size="10" title="请输入，不得为空"
				onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" 
				noinput="true" onClick="onPopDivClick(this);" valuemustexist=true autoid=864 autoHeight=150
				>
			</td>
		</tr>
		<tr>
			<td align="right" width="20%">毕业院校及专业：</td>
			<td>
				<input name="diploma" maxlength="50" type="text" id="diploma"  size="85"  title="毕业院校及专业">
			</td>
		</tr>
		<tr>
			<td align="right" width="20%">邮箱：</td>
			<td>
				<input name="email" maxlength="50" type="text" class="validate-email" id="email"  size="30"  title="请正确填写您的邮箱">
			</td>
		</tr>
		
		<tr>
			<td align="right" width="20%">CPA编号：</td>
			<td>
				<input name="cpano" maxlength="50" type="text" id="cpano"  size="30"  title="请正确填写您的CPA编号">
			</td>
		</tr>
		
		<tr>
			<td align="right" width="20%">特长：</td>
			<td>
				<textarea name="specialty" cols="55" rows="5" maxlength="100" id="specialty" onkeyup="isMax(this);"></textarea>
			</td>
		</tr>
		
		<c:if test="${temp==1}">	
	
		<tr>
			<td align="right" width="20%">加密狗信息：</td>
			<td>
				<input name="clientDogSysUi" size=25  maxlength="50" type="text" id="clientDogSysUi"  >
				<input type="button" name="dog" id="dog" value="读取狗信息" class="flyBT" onClick="getBog(this)">
				<script>setObjDisabled("clientDogSysUi");//设置加密狗信息禁用</script>
			</td>
		</tr>
		
	</c:if>		
		
		</table> 
	
	</fieldset>
	
	</div>
	
	</jodd:form>
	<jodd:form bean="userdef" scope="request">
	
	<div id="tabUser3" class="x-hide-display" style="padding:10 10 10 10;">
	
	<fieldset>
		<legend>自定义信息</legend>
		
		<input type="hidden" name="setdef" id="setdef" value="0">
		<input type="hidden" id=UserDefTbodyCount name=UserDefTbodyCount value="${UserDefTbodyCount}"  />
	
		<input type="hidden" id="commondefNames" name="commondefNames" value="names" />
		<input type="hidden" id="commondefValues" name="commondefValues" value="values" />
		<br><br>
		<table border="0" name=attachstable id=attachstable cellSpacing="1" width="600" cellPadding="3" bgColor="#6595D6">
	    <tr bgColor="#B9C4D5">
			<td width="5% align="center">选</td>
			<td width="30%" align="center">自定义名字</td>
			<td width="65%" align="center">自定义值</td>
	    </tr>
	    
	<c:choose>
	<c:when test="${UserOpt==1}">
		<script>document.getElementById("setdef").value="1";</script>
	
	<c:forEach items="${setValueList}" var="setvalue">
		<tr>
			<td>&nbsp;</td>
			<td><input type="text" name="defName"  value="${setvalue.defName}" id="defName" size=10 readonly="readonly" maxLength=20  style="width: 200; height: 18"></td>
			<td><input type="text" name="defValue" value="" id="defValue" size=10  maxLength=50   style="width: 500; height: 18"></td>
		</tr>
	</c:forEach>
	
	</c:when>
	<c:when test="${UserOpt==2}">
		<script>document.getElementById("setdef").value="1";</script>
	
	<c:forEach items="${setValueList}" var="setvalue">
		<tr>
			<td>&nbsp;</td>
			<td><input type="text" name="defName"  value="${setvalue.defName}" id="defName" size=10 readonly="readonly" maxLength=20  style="width: 200; height: 18"></td>
			<td><input type="text" name="defValue" value="${setvalue.defValue}" id="defValue" size=10  maxLength=50   style="width: 500; height: 18"></td>
		</tr>
	</c:forEach>
	
	</c:when>
	<c:when test="${UserOpt==3}">
		<script>document.getElementById("setdef").value="1";</script>
	
	<c:forEach items="${setValueList}" var="setvalue">
		<tr>
			<td>&nbsp;</td>
			<td><input type="text" name="defName"  value="${setvalue.defName}" id="defName" size=10 readonly="readonly" maxLength=20  style="width: 200; height: 18"></td>
			<td><input type="text" name="defValue" value="${setvalue.defValue}" id="defValue" size=10  maxLength=50   style="width: 500; height: 18"></td>
		</tr>
	</c:forEach>
	
	</c:when>
	</c:choose>
	
		<tbody id=UserDefTbody>
	<c:forEach items="${userdef}" var="item"  varStatus="var">
		<tr>
			<td><input type=checkbox id=checkLine name="checkbox"></td>
			<td><input type="text" name="UserDefName"  value="${item.name}" class='required'  maxLength=20  title='请输入，不得为空' size=10 style="width: 200; height: 18"></td>
			<td><input type="text" name="UserDefValue"  value="${item.value}" class='required'  maxLength=100 onKeyUp="if(this.value.length>100)this.value=this.value.substring(0,100);"  title='请输入，不得为空' size=10 style="width: 500; height: 18"></td>
		</tr>
	</c:forEach>
	
		</tbody>
		</table>
		
	<br/>
	<input type=button onClick="addLine()" value=添加自定义行 name="button">
	<input type=button onClick="deleteLine()" value=删除自定义行 name="button">
		
		
	</fieldset>
	</div>
	
	</jodd:form>
	
	
		<!-- 12-07--yb -->
 		
	<jodd:form bean="userdef" scope="request">
	
	<div id="tabUser4" class="x-hide-display" style="padding:10 10 10 10;">
	
	<fieldset>
		<legend>工作目录</legend> <br>
		
	<input type="radio" name="work" id="work1" value="E" onclick="f_select()" ${workName=='E审通工作目录' || workName=='' || workName == null ? 'checked':'' }/>&nbsp;	E审通工作目录    <br><br>
	<input type="radio" name="work" id="work2" value="selfDefine" onclick="f_select()"  ${workName!='E审通工作目录' && workName!='' && workName!=null ? 'checked':'' }/>&nbsp;	指定工作目录：&nbsp; 
	<input type="text" id="workingPath" name="workingPath" readonly="readonly">
	<input type="button" name="ll" value="浏览" onclick="f_getContext()"><br><br>
	<span class="mySpan">[*]注意</span>：更改了底稿工作目录，保存后必须退出E审通，重新登录后才可生效
	<!-- 这是文件浏览  <input type="file" value="浏览..."/>   -->
		
		
	</fieldset>
	</div>
	
	</jodd:form>
	
		<!-- 12-07--yb -->
	
	
	
	
	<input  name="AS_dog" type="hidden" id="AS_dog" value="">
	<input type="hidden" id="fileRondomName" name="fileRondomName" value="${fileTempName}">
	<input type="hidden" id="uploadFileName" name="uploadFileName" value="${fn}">
	<input type="hidden" id="fn" name="fn" value="${fn}">
	
</div>
</form>

<div id="search" style="display:none">
<br/>
<div style="margin:0 20 0 20">图片上传</div>
<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
	<table border="0" cellpadding="0" cellspacing="0" width="100%" bgcolor="">
		<tr align="center">
			
			<td align="right">上传附件：</td>
			<td align=left>
				<input type="file" id="Attachments" size="50" name="Attachments"/>
			</td>
			</tr>
	</table>
</div>





<iframe name='hidden_frame' id="hidden_frame" style='display:none'></iframe>

<form name="myForm" id="myForm" action="user.do?method=uploadPhoto" method="post" enctype="multipart/form-data" target="hidden_frame">

	<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>
  	<input type="hidden" id="fileTempName" name="fileTempName" value="${fileTempName}">
  	
</form>

<script language="javascript" >
	var mtoffice =  new ActiveXObject("MTOffice.WebOffice");
</script>



<!-- Y B -->
<script language="javascript" >
	 var pd="";
	 var va=document.getElementsByName("work");
	 	for(var i=0;i<va.length;i++) {
	 		if(va[i].checked) {
	 			if(va[i].value=="E"){ 
	 				pd="E";
	 			}else{ 
	 				pd="selfDefine";
					
	 			}
	 		}
	 	}

	
	 // 获取用户指定工作目录的路径的方法
	 function f_getContext(){
	 	// 1.调用弹出目录的方法
	 	// 2.将选中的目录路径填充到文本框中
	 	var ocx = new ActiveXObject("MTOffice.WebOffice");
		var s = ocx.funcSelectFolder("选择文件夹", "C:\\");
		delete ocx;
		ocx = null;
		
		if (s !="") {
			alert("您更改了底稿工作目录，保存后必须退出E审通，重新登录后才可生效！");
		}
		
		document.getElementById("workingPath").value=s;
	 }
	 
	 // 判断是选中的哪个目录
	 function  f_select(){
	 	var va=document.getElementsByName("work");
	 	for(var i=0;i<va.length;i++) {
	 		if(va[i].checked) {
	 			if(va[i].value=="E"){
	 				// 禁掉浏览按钮
	 				document.getElementById("ll").disabled="disabled";
	 				document.getElementById("workingPath").value="";
	 				document.getElementById("workingPath").disabled="disabled";
	 				pd="E";
	 			}else{
					document.getElementById("ll").disabled="";
	 				document.getElementById("workingPath").disabled="";
	 				pd="selfDefine";
					
	 			}
	 		}
	 	}
	 	
	 }
	 
	 
	 // 将获得的工作目录路径提交到后台
	 function f_sure(){
	 	// 1.判断是哪个工作目录
		if(pd=="E"){
			// 得到E申通的目录
			var pathName="E审通目录";
			// 传工作目录名称和对应的工作目录路径值过去
			var pathValue="E审同的工作目录";
			
			
		}else{
			// 得到用户指定的路径
			var pathName="用户自定义目录";
			var pathValue=document.getElementById("workingPath").value;
		    
		}	 
	 	 
	 }
	 
	 
	 function f_init(workName,workValue){
	 	if(workName=="E审通工作目录"){
			// 禁掉浏览按钮
			document.getElementById("ll").disabled="disabled";
			document.getElementById("workingPath").value="";
			document.getElementById("workingPath").disabled="disabled";
	 	}else{
			document.getElementById("workingPath").value=workValue;
	 	}
	 }
	 
	 
</script>
<!-- Y B -->



<script type="text/javascript"><!--

new Validation('thisForm');
setObjDisabled("departid");//设置所属单位禁用

//密码一致的验证
function chkPwd(){
	if(document.getElementById("password_two").value!=document.getElementById("password").value){
		alert("密码不一致！");
		document.getElementById("password_two").value="";
		document.getElementById("password").value="";
		document.getElementById("password").focus();
		return false;
	}
	return true;
}

//验证是否修改
//protect("thisForm");
//hide("up_pwd");

function isMax(v){
	if(v.value.length>100)v.value=v.value.substring(0,100);
}
function goCheckUser() {

	if(thisForm.loginid.value != '') {

		var oBao = new ActiveXObject("Microsoft.XMLHTTP");
		oBao.open("POST","user.do?method=CheckUser&loginid=" + thisForm.loginid.value,false);
		oBao.send();
		if(oBao.responseText != 'yes') {
			document.getElementById("divb").style.display = "" ;
			document.getElementById("divb").innerHTML = "<font color='red'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;用户名已经存在!</font>";
			thisForm.loginid.value = '';
			document.all.loginid.focus();
		} else {
			document.getElementById("divb").innerHTML = "";
		}
	}else {
		document.getElementById("divb").innerHTML = "";
	}

}

/*
function yesorno(){

	if(document.getElementById("username").value!=""&&document.getElementById("password").value!=""&&document.getElementById("password_two").value!=""&&document.getElementById("name").value!=""&&document.getElementById("departmentid").value!=""&&document.getElementById("departid").value!=""){
		var yon=window.confirm("是否设置权限？");
		if(yon){
		document.getElementById("opt").value="goToListPage";
		}
	}

}
*/

function openDepartment(){

	var randStr = Math.random();
	
	var url,title;
	title="部门管理";
	url='${pageContext.request.contextPath}/department.do';
	
	var tab = parent.tab ;
	if (tab){
		//不是弹出新窗口
		n = tab.add({    
			'title':'部门管理',    
			closable:true,  //通过html载入目标页    
			html:'<iframe name="test_' + randStr + '" scrolling="auto" frameborder="0" width="100%" height="100%" src="'+url+'"></iframe>'   
		}); 
	    tab.setActiveTab(n);
	}else{
		window.open(url);
	}
	
	
}


function updatePwd(){

	var UserOpt = document.getElementById("UserOpt").value;
	var password_old = "";

	if(UserOpt == "3" || UserOpt == "2"){
			
		if(UserOpt == "3"){
		
		password_old = document.getElementById("password_old").value;
		
		
		if(password_old=="" ||password_old==null){
			alert("旧密码不能为空，密码修改失败，请重新输入!");
			return false;
		}
		password = document.getElementById("password").value;
		if(password == "" ||password==null) {
				alert("新密码不能为空，密码修改失败，请重新输入!");
			return false;

		}
		password_two = document.getElementById("password_two").value;
		if(password_two == ""||password_two==null) {
				alert("确认密码不能为空，密码修改失败，请重新输入!");
			return false;

			}
		}
			if(UserOpt == "2"){
		
		password = document.getElementById("password").value;
		if(password == "" ||password==null) {
				alert("新密码不能为空，密码修改失败，请重新输入!");
			return false;

		}
		password_two = document.getElementById("password_two").value;
		if(password_two == ""||password_two==null) {
				alert("确认密码不能为空，密码修改失败，请重新输入!");
			return false;

			}
		}
	}
	var id = document.getElementById("id").value;
	var password = document.getElementById("password").value;

	if(password.indexOf("\\") > -1
			|| password.indexOf("/") > -1
			|| password.indexOf(":") > -1
			|| password.indexOf("*") > -1
			|| password.indexOf("?") > -1
			|| password.indexOf("\"") > -1
			|| password.indexOf("<") > -1
			|| password.indexOf(">") > -1
			|| password.indexOf("|") > -1
			|| password.indexOf("+") > -1
			|| password.indexOf("&") > -1
			|| password.indexOf("=") > -1) {

			alert("密码不能含有\/:*?\"<>|+&=等符号");
			return false;
		}

	var oBao = new ActiveXObject("Microsoft.XMLHTTP");
	var url="user.do?method=UpdatePWD&UserOpt="+UserOpt+"&id="+id+"&password="+password+"&password_old="+password_old+"&random="+Math.random();

	oBao.open("POST", url, false);
	oBao.send();
	if(UserOpt == "3"){
		if(oBao.responseText == "error"){
			alert("旧密码不正确！");
			return false;
		}
	}

	if(oBao.responseText == 'yes'){
		alert('密码修改成功!');
		

	}else{
		alert('密码修改失败，请重新输入!');
	}

	document.getElementById("password").value = "";
	document.getElementById("password_two").value = "";
	if(UserOpt == "3"){
		document.getElementById("password_old").value = "";
	}
}

function goAdEd(){
 if(document.getElementById("setdef").value=="1"){

	    var defname = new Array();
	    var defvalue = new Array();
	    defname = document.getElementsByName("defName");
	    defvalue = document.getElementsByName("defValue");
	    var names="";
	    for(i=0;i<defname.length;i++){
	        if(defname[i].value!=""){
	        	names+=defname[i].value+"-";
	        }else{
	        	names+=+"?"+"-";
	        }

	    }
	    var values="";
	    for(i=0;i<defvalue.length;i++){
	    	if(defvalue[i].value!=""){
	    		values+=defvalue[i].value+"-";
	    	}else{
	    		values+=+"?"+"-";
	    	}

	    }

	    document.getElementById("commondefNames").value=names;
	    document.getElementById("commondefValues").value=values;

    }

    var password = document.getElementById("password").value;
    if(password.indexOf("\\") > -1
			|| password.indexOf("/") > -1
			|| password.indexOf(":") > -1
			|| password.indexOf("*") > -1
			|| password.indexOf("?") > -1
			|| password.indexOf("\"") > -1
			|| password.indexOf("<") > -1
			|| password.indexOf(">") > -1
			|| password.indexOf("|") > -1
			|| password.indexOf("+") > -1
			|| password.indexOf("&") > -1
			|| password.indexOf("=") > -1) {


			alert("密码不能含有\/:*?\"<>|+&=等符号");
			return false;
		}
		
 	var ch2=document.getElementById("work2");
	var workPa=document.getElementById("workingPath").value;
	if('${UserOpt}' == '3'){
		//是个人信息维护状态时，才有这个判断
		if(ch2.checked){
			if(workPa=="" || workPa==null){
				alert("工作目录自定义路径不能为空！");
				return;
			}
		} 	
	}
	
	//判断部门里某个角色是否超过3个人
	var roles = document.getElementById("roles").value ;
	var departmentid = document.getElementById("departmentid").value ;
	Ext.Ajax.request({
		method:'POST',
		url:'${pageContext.request.contextPath}/user.do?method=isRoleOutride',
		success:function (response,options) {
			if(response.responseText != "ok") {
				alert("按照所内规定,角色【"+response.responseText+"】每个部门不允许超过3个,您所在部门已达到最大个数限制,请修改!") ;
				return ;
			}else {
				document.thisForm.action="user.do?method=Save&pd="+pd;
				document.thisForm.submit();
			}
		},
		params : { 
			opt:"${UserOpt}",
			userId:"${param.id}",
			roleId:roles,
			departmentid:departmentid
		}
	});
	
	
//	yesorno();
	return;
}

	// yb
	
	if('${UserOpt}' == '3'){
		var workName="${workName}";
		var workValue="${workValue}";
		f_init(workName,workValue);
	}
	
	if('${UserOpt}' != '3')
//		init();

function getBog(v){
	if(v.value == "读取狗信息"){
		try{
			document.getElementById("AS_dog").value = mtoffice.funReadDog();
		}catch(e){

			mtoffice = null;
			alert("验证控件安装失败或者没安装,请安装");
			//出错了，说明控件安装不成功，导航到专门的安装界面
	        window.location="/AuditSystem/AS_SYSTEM/ocxsetup.htm";
		}

		var AS_dog = document.getElementById("AS_dog").value;

		if(AS_dog.indexOf("没有查找") < 0){

			var oBao = new ActiveXObject("Microsoft.XMLHTTP");
			var url="user.do?method=ReadDog&AS_dog="+AS_dog+"&random="+Math.random();

			oBao.open("POST", url, false);
			oBao.send();
			var strResult = unescape(oBao.responseText);
			if(strResult==0){
				alert("该加密狗已绑定，请先解除绑定或用另一加密狗作绑定！");
				return false;
			}
			if(strResult==1){
				alert("服务器没有插加密狗，请先插入加密狗！");
				return false;
			}
			if(strResult==2){
				alert("客户端没有插加密狗，请先插入加密狗！");
				return false;
			}
			if(strResult==3){
				alert("服务器与客户端的加密狗信息不同，请重新插入加密狗！");
				return false;
			}

			document.getElementById("clientDogSysUi").value = strResult;
			v.value = "清除狗信息";
		}else{
			alert(AS_dog);
			document.getElementById("clientDogSysUi").value = "";
		}
	}else if(v.value == "清除狗信息"){
		v.value = "读取狗信息";
		document.getElementById("clientDogSysUi").value = "";
	}
}
if('${UserOpt}' == 3){
     setObjDisabled("departmentid");
     setObjDisabled("roles");

    var getloginid = document.getElementById("getloginid").value;
    if(getloginid == "admin"){
    	setObjDisabled("loginid");
    }
    
    if("${loginDisabled}" == "是") setObjDisabled("loginid");//禁止用户自己修改登录名
    
    var loginid = document.getElementById("loginid").value;
    document.getElementById("getuesrname").value = loginid;
}
var userid = document.getElementById("userid").value;

if('${UserOpt}' == 2){
/*    
	if('${userSession.userLoginId}' != 'admin'){
    	setObjDisabled("departmentid");
        setObjDisabled("roles");
    }
*/
    var loginid = document.getElementById("loginid").value;
    document.getElementById("getuesrname").value = loginid;
}


function checkNamewhenModify(){

	var loginid = document.getElementById("loginid").value;
	var getuesrname = document.getElementById("getuesrname").value;

	if(loginid == getuesrname){

	}else{
	    goCheckUser();
	}
}

document.getElementById("password").value = "";


function upLoadFile() {


	queryWinFun();
	
}

function removeDiv(id) {
	document.getElementById(id).style.display = "none" ;
}


function upLoadSumbit() {

	var Attachments = document.getElementById("Attachments");

	document.getElementById("uploadFileName").value = Attachments.value ;
	var myForm = document.getElementById("myForm");
	myForm.action = "user.do?method=uploadPhoto" ;
	removeDiv("upLoadDiv") ;

}

function deletePhto() {

	var Attachments = document.getElementById("Attachments");
	var showFileName = "${fileTempName}" ;
	var fileName = document.getElementById("uploadFileName").value ;
	var deleteName = "" ;

	if(fileName == "") {
		alert("您没有上传任何照片!");
		return ;
	}else {
		 deleteName = showFileName+getFileName(fileName).substr(getFileName(fileName).indexOf("."),fileName.length);
	}


		var opt = "${UserOpt}" ;
		var resText = ""

		if(opt == "2" || opt == "3") {

			var fn = document.getElementById("fn").value ;

			if(fn == "") {
				alert("您没有上传任何照片!");
				return ;
			}
			var oBao = new ActiveXObject("Microsoft.XMLHTTP");
			var url="user.do?method=deleteUpdatePhoto&deleteName="+deleteName+"&id=${uservo.id}" ;
			oBao.open("POST",url,false);
			oBao.send();

			resText = oBao.responseText ;

		}else {
			var oBao = new ActiveXObject("Microsoft.XMLHTTP");
			var url="user.do?method=deletePhoto&deleteName="+deleteName ;
			oBao.open("POST",url,false);
			oBao.send();
			resText = oBao.responseText ;
		}

		if(resText == "suc") {
			document.getElementById("Attachments").value = "";
			document.getElementById("uploadFileName").value = "" ;
			document.getElementById("bill").src = "${pageContext.request.contextPath}/images/noPhoto.gif" ;
			alert("文件删除成功!") ;
		}else if(resText == "fail"){
			alert("文件删除失败!") ;
		}else {
			alert("您要删除的文件不存在,请联管理员!");
		}

}

function changePhoto() {
	var Attachments = document.getElementById("Attachments");
	if(Attachments.value != "") {
		var uploadPath = Attachments.value ;
		var showFileName = "${fileTempName}" ;
		document.getElementById("bill").src = "${pageContext.request.contextPath}/userPhoto/"+showFileName+getFileName(uploadPath).substr(getFileName(uploadPath).indexOf("."),uploadPath.length) ;
	}

}


function getFileName(filepath) {
		var returnstr = filepath;
		var length = filepath.trim().length;

		if (length > 0) {
			var i = filepath.lastIndexOf("\\");
			if (i >= 0) {
				filepath = filepath.substring(i + 1);
				returnstr = filepath;
			}
		}
		return returnstr;
	}

String.prototype.trim  = function(){return this.replace(/(^\s*)|(\s*$)/g, "");}

function query(){
	var str = document.getElementById("name").value.trim();
	if(str == "") return;
	var arrRslt = makePy(str);
	var loginidInput = document.getElementById("loginid");
	if(arrRslt.length >0 && loginidInput.value.length==0) {
		//只在没有值的时候才设置
		loginidInput.value = arrRslt[0] ;
	}
}


//
--></script>
</body>
</html>

