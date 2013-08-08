<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>通迅录</title>
<style>
.stepDiv {
	 color:#ffffff; border: 1px solid #cccccc; padding: 5px; margin: 5px;
}

.before{
	border: 0px;
	background-color: #FFFFFF !important;
}

.data_tb {
	background-color: #ffffff;
	text-align:center;
	margin:0 0px;
	width:50%;
	border:#8db2e3 1px solid; 
	BORDER-COLLAPSE: collapse; 
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
<script type="text/javascript">

	function keydown(){ 
	   if(event.keyCode==9){
       		event.keyCode=0;     
       		event.returnValue=false;
       		document.getElementById("password_two").focus(); 
       		return false;
 		}
	}

	

	function ext_init(){

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
		            	  goSave();
	   			   }
	            	   
	           },
			{
                text: '关闭',
                id:'appSubmit232', 
                icon:'${pageContext.request.contextPath}/img/exit.gif' ,
                scale: 'large',
	               handler:function(){
	            	  closeTab(parent.tab);
	   			   }
	           }	           
            ]  
		});
    
		new Ext.form.DateField({
			applyTo : 'partytime',
			width: 133,
			format: 'Y-m-d'
		});
		
		new Ext.form.DateField({
			applyTo : 'diplomatime',
			width: 133,
			format: 'Y-m-d'
		});
		
		new Ext.form.DateField({
			applyTo : 'borndate',
			width: 133,
			format: 'Y-m-d'
		});
		
    }
    window.attachEvent('onload',ext_init);

</script>
</head>
<body style="padding: 0px; margin: 0px; background-color: #006699; background-image:none;" >
<div id="divBtn"></div>
<form name="thisForm" id="thisForm" method="post" action="" >
<jodd:form bean="user" scope="request">
<div style="height:expression(document.body.clientHeight);" >
<br>
<table cellpadding="0" cellspacing="0" border="0" width="100%" height="80%">
<tr><td valign="middle" align="center">

	<input type="hidden" id="flag" name="flag" value="${flag }">
	<table  cellpadding="8" cellspacing="0" align="center" class="data_tb" >
	<tr>
	  <td class="data_tb_alignright"  width="30%" align="center" colspan="4">个人资料</td>
	</tr>	
	<tr>
	  <td class="data_tb_alignright"  width="30%" align="right">登录名：</td>
	  <td class="data_tb_content" colspan="3"><input class='before' readonly type="text" name="loginid" id="loginid"   /></td>
	</tr>
	<tr>  
	  <td class="data_tb_alignright"  width="30%" align="right">姓名：</td>
	  <td class="data_tb_content" colspan="3" ><input class='before' readonly type="text" name="name" id="name"   /></td>
	</tr>
	<tr>
		<td class="data_tb_alignright"  width="30%" align="right">登录密码<span class="mustSpan">[*]</span>：</td>
		<td class="data_tb_content" colspan="3" >
			<input name="password" maxlength="18" size=25 type="password" id="password"  onBlur="chkpwd(this)"
			onkeydown="keydown()"	onkeyup="value=value.replace(/[\W]/g,'');" onbeforepaste="clipboardData.setData('text',clipboardData.getData('text').replace(/[^\d]/g,''))"
				class="required"  title="请输入6位以上的数字或字母" value="" ><a href="javascript:void(0);" onClick="javascript:window.open('${pageContext.request.contextPath}/user.do?method=Help','','width=600, height=650, location=no, menubar=no, status=no, toolbar=no, scrollbars=yes, resizable=yes');">密码建议:<label id="chkResult"></label></a></td>
	</tr>
	<tr>
		<td class="data_tb_alignright"  width="30%" align="right">确认密码<span class="mustSpan">[*]</span>：</td>
		<td class="data_tb_content" colspan="3">
			<input name="password_two" maxlength="18" size=25 type="password"  maxLength="18"
				onkeyup="value=value.replace(/[\W]/g,'') " onbeforepaste="clipboardData.setData('text',clipboardData.getData('text').replace(/[^\d]/g,''))"
				id="password_two"  class="required validate-passwd-identical" ></td>
	</tr>
	<tbody style="display: none">
	<tr>
		<td class="data_tb_content"  width="20%" align="right" height="10px" colspan="4"></td>
	</tr>		
	<tr>  
	  <td class="data_tb_alignright"  width="20%" align="right">手机<span class="mustSpan">[*]</span>：</td>
	  <td class="data_tb_content" width="30%"><input title='手机号码不能为空' maxlength="11" value="${user.mobilePhone }" class="required validate-phonenumber" type="text" name="mobilePhone" id="mobilePhone"   /></td>
	  <td class="data_tb_alignright"  width="20%" align="right">办公电话<span class="mustSpan">[*]</span>：</td>
	  <td class="data_tb_content" width="30%"><input title='请输入正确的办公电话' maxlength="13" value="${user.phone}" class="required phonenumber-wheninputed" type="text" name="phone" id="phone"  /></td>
	</tr>
	<tr>  
	  <td class="data_tb_alignright"  width="20%" align="right">楼层<span class="mustSpan">[*]</span>：</td>
	  <td class="data_tb_content" ><input title='用户所在楼层不能为空' class="required" type="text" name="floor" id="floor"   /></td>
	  <td class="data_tb_alignright"  width="30%" align="right">房间号：</td>
	  <td class="data_tb_content" ><input type="text" name="house" id="house"  /></td>
	</tr>	
	<tr>  
	  <td class="data_tb_alignright"  width="30%" align="right">工位号：</td>
	  <td class="data_tb_content" ><input type="text" name="station" id="station"   /></td>
	</tr>
	<tr>
		<td class="data_tb_content"  width="20%" align="right" height="10px" colspan="4"></td>
	</tr>
	<tr>
		<td class="data_tb_alignright"  width="20%" align="right">证件类型<span class="mustSpan">[*]</span>：</td>
		<td class="data_tb_content" width="30%"><input  name="paperstype" type="text" id="paperstype"  value="身份证" class='before' readonly ></td>
		<td class="data_tb_alignright"  width="20%" align="right">证件号码<span class="mustSpan">[*]</span>：</td>
		<td class="data_tb_content"><input  class="required" name="identityCard" type="text" id="identityCard" value="${user.identityCard }"  title="证件号码不能为空" ></td>
	</tr>	
	<tr>
		<td class="data_tb_alignright"  width="20%" align="right" >性别<span class="mustSpan">[*]</span>：</td>
		<td class="data_tb_content" width="30%"><input name="sex" type="radio" class="required" value="M" checked >男 <input type="radio" name="sex" value="F">女</td>
		<td class="data_tb_alignright"  width="20%" align="right" >出生年月<span class="mustSpan">[*]</span>：</td>
		<td class="data_tb_content"><input name="borndate" type="text" id="borndate" value="${user.borndate}"  class="required validate-date-cn,required" title="请输入日期！" showcalendar="true"></td>
	</tr>
	<tr>
		<td class="data_tb_alignright"  width="20%" align="right" >民族<span class="mustSpan">[*]</span>：</td>
		<td class="data_tb_content" width="30%"><input  name="nation" type="text" id="nation"  class="required" ></td>
		<td class="data_tb_alignright"  width="20%" align="right" >婚姻状况<span class="mustSpan">[*]：</td>
		<td class="data_tb_content">
			<select id="marriage" name="marriage" style="width: 130px" class="required">
			<option value="未婚" >未婚</option>
			<option value="已婚" >已婚</option>
			<option value="离异" >离异</option>
			<option value="丧偶" >丧偶</option>
			</select>
		</td>
	</tr>
	<tr>
		<td class="data_tb_alignright"  width="20%" align="right" >籍贯<span class="mustSpan">[*]：</td>
		<td class="data_tb_content" width="30%"><input multilevel=true  name="place" type="text" id="place"  class="required" autoid=740 onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist=true noinput=true autoHeight=150 ></td>
		<td class="data_tb_alignright"  width="20%" align="right" >户口所在地<span class="mustSpan">[*]：</td>
		<td class="data_tb_content"><input  name="residence" type="text" id="residence"  class="required" ></td>
	</tr>
	<tr>
		<td class="data_tb_content"  width="20%" align="right" height="10px" colspan="4"></td>
	</tr>
	<tr>
		<td class="data_tb_alignright"  width="20%" align="right" >政治面貌</td>
		<td class="data_tb_content" width="30%"><input  name="politics" type="text" id="politics"   autoid=700 refer='政治面貌' onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist=true noinput=true autoHeight=150 ></td>
		<td class="data_tb_alignright"  width="20%" align="right">入党团时间：</td>
		<td class="data_tb_content"><input  name="partytime" type="text" id="partytime"   ></td>
	</tr>
	<tr>
		<td class="data_tb_alignright"  width="20%" align="right">组织关系所在单位：</td>
		<td class="data_tb_content" colspan="3"><input  name="relationships" type="text" id="relationships"  size="50" ></td>
		
	</tr>
	<tr>
		<td class="data_tb_content"  width="20%" align="right" height="10px" colspan="4"></td>
	</tr>	
	<tr>
		<td class="data_tb_alignright"  width="20%" align="right" >毕业院校<span class="mustSpan">[*]：</td>
		<td class="data_tb_content" ><input name="diploma" maxlength="50" type="text" id="diploma"  class="required"  title="毕业院校及专业"></td>
		<td class="data_tb_alignright"  width="20%" align="right" >毕业时间<span class="mustSpan">[*]：</td>
		<td class="data_tb_content"><input  name="diplomatime" type="text" id="diplomatime" class="required"  ></td>
	</tr>
	<tr>
		<td class="data_tb_alignright"  width="20%" align="right">学历：</td>
		<td class="data_tb_content"><input name="educational" maxlength="20" type="text" id="educational"   title="学历" autoid=700 refer='员工学历' onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist=true noinput=true autoHeight=150></td>
		<td class="data_tb_alignright"  width="20%" align="right" >专业 <span class="mustSpan">[*]：</td>
		<td class="data_tb_content"><input  name="profession" type="text" id="profession"  class="required" ></td>
	</tr>
	<tr>
		<td class="data_tb_alignright"  width="20%" align="right">英语能力：</td>
		<td class="data_tb_content" width="30%"><input  name="english" type="text" id="english"   ></td>
		<td class="data_tb_alignright"  width="20%" align="right">CPA号：</td>
		<td class="data_tb_content"><input name="cpano" maxlength="50" type="text" id="cpano"    title="请正确填写您的CPA编号"></td>
	</tr>
	</tbody>
	<tr>
	  <td class="data_tb_content"  width="30%" align="center" colspan="4"><font color="red">您是第一次使用本系统，请完善相关内容</font></td>
	</tr>							
	</table>

<center><div id="sbtBtn" ></div></center>

</td></tr>
</table>

</div>	
</jodd:form>
</form>
</body>
</html>
<script type="text/javascript">
//校验密码：只能输入6-20个字母、数字、下划线 
new Validation('thisForm');
function isPasswd(s) 
{ 
	//var patrn=/^(\w){6,18}$/; 
	var patrn=new RegExp(/[A-Za-z].*[0-9]|[0-9].*[A-Za-z]/);
	if (!patrn.test(s)){
		return false ;
	}else{
		return true ;
	}
} 
function goSave(){
	
	   if (!formSubmitCheck('thisForm')) return;
  	   var pwd1 = document.getElementById("password").value; 
  	   //if(!isPasswd(pwd1)){
  	     if(pwd1.length<6)
  			alert("登陆密码不得少于六位！");	
  			return ;
  		}
  	
  /*
    var cardId = document.getElementById("identityCard").value;
	var phone = document.getElementById("phone").value;
	var mobilePhone = document.getElementById("mobilePhone").value;
	var bornDate = document.getElementById("borndate").value;
	var diplomatime = document.getElementById("diplomatime").value;
	var partytime = document.getElementById("partytime").value;
	
	
	if(!check(bornDate) || !check(diplomatime) ||(partytime!="" && !check(partytime))){
				 alert("请输入正确的时间格式");
  	             return;
  	}
	if(mobilePhone.length !=11){
		alert("手机号必须为11位！");
		return ;
	}
	if(!isphone(phone)){
 		return ;
	}
	if(!isIdCardNo(cardId)){
  	   return; 
  	}
  */
  
	thisForm.action = "${pageContext.request.contextPath}/user.do?method=addressSave";
	var save = alert("保存成功！");
	thisForm.target = "";
	thisForm.submit();	
	
}

function chkpwd(obj){
	var t=obj.value;
	if(t.length <6 && 18<t.length){
		alert("登陆密码不得少于六位大于十八位且必须为数字与字母和下划线组合的形式！");
		obj.select();
		return ;
	}
	
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
function isIdCardNo(num) {
	if(!num || (num.length!=15 && num.length!=18)){
            tip = "身份证号格式错误";
            alert(tip);
            return false;
     }
		return true;
}
function isphone(num){
   if(!num || !/^(\d{3,4}-)?\d{7,9}$/g.test(num)){
   			alert("办公电话格式错误");
   			return false;
   }
   		return true;
}
function check(t){
   if(!/^\d{4}-\d{2}-\d{2}/g.test(t)){
   		   return false;
   }
   return true;
}
function checkDate(date)
{
    return true;
}
function getResult(s){

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
</script>
