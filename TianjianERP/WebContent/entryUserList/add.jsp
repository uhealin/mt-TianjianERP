<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>报到清单</title>
<style type="text/css">
</style>
<script type="text/javascript">
Ext.onReady(function (){
	new Validation('thisForm');
});
function ext_init(){
	
    var tbar = new Ext.Toolbar({
   		renderTo: "divBtn",
   		defaults: {autoHeight: true,autoWidth:true},
        items:[
               
               { 
			text:'保存',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/save.gif',
			handler:function(){
					var email=document.getElementById("email").value;
					var loginid=document.getElementById("loginid").value;
					if(email!=''&&loginid!=''){
    				document.thisForm.action="${pageContext.request.contextPath}/entryUserList.do?method=addUser";
    				document.thisForm.submit();
					}else{
						alert("请填写信息");
					}
            	 
			}
   		},'-',{
            text:'返回',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/back.gif',
            handler:function(){
				window.history.back();
			}
   		},'->'
		]
    });
    
}
window.attachEvent('onload',ext_init);
</script>
</head>
<body>
<div id="divBtn"></div>
<form name="thisForm" id="thisForm"  action="${pageContext.request.contextPath}/entryUserList.do?method=addUser" method="post" >
<input type="hidden" id="cardNum" name="cardNum" value="${cardNum }" />
<input type="hidden"  id="uuid" name="uuid" value="${uuid }" />
<input type="hidden"  id="from" name="from" value="${from }" />
<input type="hidden"  id="roles" name="roles" value="267" />
<table  class="formTable" style="width: 500px;"  >
	<tr>
	<th colspan="4" ><div align="center">报到清单</div></th>
	</tr>
	<c:if test="${from=='officialVO' }">
	<tr>
		<th align="right" width="25%"><p>用户名<p></th>
		<th >
			 ${officialVO.name}
		</th>
		<th align="right" width="25%">性别</th>
		<th >
			 ${officialVO.sex}
		</th>
	</tr>
	<tr>
		<th align="right" width="25%"><p>出生日期<p></th>
		<th >
			 ${officialVO.birthday}
		</th>
		<th align="right" width="25%">学位</th>
		<th >
			 ${officialVO.education}
		</th>
	</tr>
	</c:if>
	
	<c:if test="${from=='employeeVO' }">
	<tr>
		<th align="right" width="25%"><p>用户名<p></th>
		<th >
			 ${employeeVO.name}
		</th>
		<th align="right" width="25%">性别</th>
		<th >
			 ${employeeVO.sex}
		</th>
	</tr>
	<tr>
		<th align="right" width="25%"><p>出生日期<p></th>
		<th >
			 ${employeeVO.brithday}
		</th>
		<th align="right" width="25%">学位</th>
		<th >
			 ${employeeVO.grad_degrees}
		</th>
	</tr>
	</c:if>
	<tr>
		<th colspan="2" align="right" width="50%"><p>用户登录名<p></th>
		<td colspan="2" width="50%" >
			<div>
			<input name="loginid" maxlength="80" class="required" size=25 onBlur="goCheckUser()" type="text"   title="请输入，不得为空,且只能是字母"  onkeyup="value=value.replace(/[^\w\.\/]/ig,'')"> 
			<br><div id="divb"  ></div>
			</div>
		</td>
	</tr>
	<tr>
		<th colspan="2"  align="right"  width="50%">email：</th>
		<td colspan="2"  width="50%">
			<input id="email" name="email" class="required"  maxlength="80" size=25 onBlur="checkemail();" type="text"   title="请输入，不得为空,且只能是字母">
		</td>
	</tr>
</table>
</form>
</table>
</body>
<script type="text/javascript">
new Ext.form.DateField({
	applyTo : 'except_complete_time',
	width: 133,
	format: 'Y-m-d'
});
new Ext.form.DateField({
	applyTo : 'reaudit_time',
	width: 133,
	format: 'Y-m-d'
});
new Ext.form.DateField({
	applyTo : 'report_EndTime',
	width: 133,
	format: 'Y-m-d'
});

function goCheckUser() {
	if(document.thisForm.loginid.value != '') {

		var oBao = new ActiveXObject("Microsoft.XMLHTTP");
		oBao.open("POST","user.do?method=CheckUser&loginid=" + thisForm.loginid.value,false);
		oBao.send();
		if(oBao.responseText != 'yes') {
			alert("用户名已存在！");
			document.all.loginid.select();
		} else {
			document.getElementById("divb").innerHTML = "";
		}
	}else {
		document.getElementById("divb").innerHTML = "";
	}

}

function checkemail() {
            var temp = document.getElementById("email");
            //对电子邮件的验证
            var tempvalue=temp.value;
            if(tempvalue!=null&&tempvalue!=''){
            var myreg = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
	            if(!myreg.test(temp.value))
	            {
	                alert('提示\n\n请输入有效的E_mail！');
	                temp.select();
	                 return false;
	            }
 					var oBao = new ActiveXObject("Microsoft.XMLHTTP");
		    		oBao.open("POST","user.do?method=CheckUserEmail&email=" + tempvalue,false);
		    		oBao.send();
			    		if(oBao.responseText != 'yes') {
			    			alert("email已存在！");
			                temp.select();
			    		} else {
			    			document.getElementById("email").innerHTML = "";
			    		}
		    	}else {
		    		document.getElementById("email").innerHTML = "";
		    	}
         }
</script>



<script>
function jiSuan(obj)	{	
	var newStr = "";		var count = 0;		
	if(obj.value.indexOf(".")==-1)		{		
		for(var i=obj.value.length-1;i>=0;i--)	 {	
			if(count % 3 == 0 && count != 0) {	
				newStr = obj.value.charAt(i) + "," + newStr;
			} else{	
				newStr = obj.value.charAt(i) + newStr;
			}
			count++;
			}
		obj.value = newStr + ".00";
		}		else
		{
			for(var i=obj.value.indexOf(".")-1;i>=0;i--) {
				if(count % 3 == 0 && count != 0){
					newStr = obj.value.charAt(i) + "," + newStr;
					}else		{		
						newStr = obj.value.charAt(i) + newStr;	
						}				count++;	
						}		
			obj.value = newStr + (obj.value + "00").substr((obj.value + "00").indexOf("."),3);		
	}	} </script>

</html>