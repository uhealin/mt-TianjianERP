<%@ page language="java" contentType="text/html; charset=utf-8"
	errorPage="/hasNoRight.jsp" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<style type="text/css">
.myTable,.myTable td {
	border: 1px solid #0080FF;
	border-collapse: collapse;
}

.dogtd {
	text-align: center;
}
</style>
<script type="text/javascript">
Ext.onReady(function(){
	
	 new Ext.Toolbar({
		renderTo:'divBtn',
           items:[{
            text:'保存',
            icon:'${pageContext.request.contextPath}/img/save.gif',
            handler:function(){	
            	if (!formSubmitCheck('thisForm')) return;
            	but_submit("1");
			}
      	}
         /*,'-',{
	            text:'密码重置',
	            icon:'${pageContext.request.contextPath}/img/clear.gif',
	            handler:function(){
	            	but_submit("0");
	            }
	           
	   }*/
         ,'-',{
	            text:'关闭',
	            icon:'${pageContext.request.contextPath}/img/close.gif',
	            handler:function () {
	            	closeTab(parent.tab);
	            }
	        },'->'
        ]
        });  
        
});
</script>
</head>
<body>
<div id="divBtn"></div>
<div id="divForm" align="center">

<div
	style="height: expression(document.body.clientHeight-27); width: 100%; overflow: auto;">

<form method="post" name="thisForm"	id="thisForm" class="autoHeightForm">
<fieldset><legend>更改密码</legend> 
<table width="500px" border=0>
	<tr>
	
		<td nowrap="nowrap" align="right" width="80">新密码   <span class="mustSpan">[*]</span>：</td>
		<td nowrap="nowrap" ><input type="password"  class="required" name="password" 
			 onBlur="chkpwd(this)"	id="password" >	
			<a href="#" onClick="javascript:window.open('user.do?method=Help','','width=600, height=650, location=no, menubar=no, status=no, toolbar=no, scrollbars=yes, resizable=yes');">密码建议:<label id="chkResult"></label></a>
			</td>

	</tr>

	<tr>
		<td nowrap="nowrap" align="right" width="80">确认密码<span class="mustSpan">[*]</span>：</td>
		<td nowrap="nowrap" ><input type="password"  name="newPassword2" 
			id="newPassword2" class="validate-passwd-identical" ></td>
	</tr>
</table>
</fieldset>

</form>
</div>
</div>

</body>
<script type="text/javascript">
new Validation('thisForm');


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

//保存
function but_submit(obj){
	
	if(obj=="0"){
       document.thisForm.action="${pageContext.request.contextPath}/user.do?method=changePassword&pwdReset=0";
	}
	if(obj =="1"){
	       document.thisForm.action="${pageContext.request.contextPath}/user.do?method=changePassword";
	}
       document.thisForm.submit();
}
	
	
</script>

</html>