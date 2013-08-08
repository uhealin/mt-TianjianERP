<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>我的工资查询</title>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/searchSession.js" charset=gbk></script>

<script type="text/javascript">

Ext.onReady(function(){
	var tbar_customer = new Ext.Toolbar({
		renderTo:'divBtn',
           items:[{
			text:'查询',
			id:'btn-query',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
			handler:queryWinFun
		},'->'
        ]
        });  

});
var queryWin = null;
function queryWinFun(){
	var searchDiv = document.getElementById("search") ;
	searchDiv.style.display = "" ;
	if(!queryWin) { 
	    queryWin = new Ext.Window({
			title: '工资查询',
			contentEl:'search',
	     	renderTo : searchWin,
	     	width: 350,
	     	height:240,
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
	    	buttons:[{
            	text:'确定',
          		handler:function(){
          			query();
            	}
        	}]
	    });
    }
    new BlockDiv().show();
    queryWin.show();
}

</script>

</head>
<body style="overflow: hidden;">
<div id="divBtn"></div>
<div id="divprint">
<form name="thisForm" id="thisForm" method="post" action="">
<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>

<div id="search" style="display:none;">
<br>
<br>
			<table border="0" cellpadding="0" cellspacing="0" bgcolor="" align="center" style="width:100%;line-height: 25px;" width="100%">
				   <tr style="display: none;">
					  <td  align="right" width="10%">登录密码：</td>
				      <td  align="left">
				      	<input type="password" name="passowrd" id="passowrd" maxLength="16"  />
					  </td>
				</tr>
				<tr style="display: none;">
					<td  align="right" >身份证号：</td>
					<td  align="left">
						<input type="text" name="identityCard" id="identityCard" maxLength="18">
				    </td>
				</tr>
				<tr style="display: none;">
					<td  align="right" >手机密钥：</td>
					<td  align="left">
						<input type="text" name="yzmValue" id="yzmValue" maxLength="6" size="6">
						<input type="button" value="索取手机验证码" onclick="addYzm()" id="fsdxId" name="fsdxId" style="width: 105px;">
				    </td>
				</tr>
			   <tr>
					  <td  align="right" width="30%">发放日期：</td>
				      <td  align="left">
				      	<select style="width: 60;" id="year" name="year">
				      		<option value="">请选择</option>
				      		<%
				      			for(int i=2012;i<2500;i++){
				      			
				      		%>
				      			<option value="<%=i %>"><%=i %></option>
			      			<%
				      			}
			      			%>
				      	</select>&nbsp; 年
				      		<select style="width: 60;" id="month" name="month">
				      		<option value="">请选择</option>
				      		<%
				      			for(int i=1;i<=12;i++){
				      			
				      		%>
				      			<option value="<%=i %>"><%=i %></option>
			      			<%
				      			}
			      			%>
				      	</select>&nbsp;月
					  </td>
				</tr>
				<tr >
					<td  align="right" >发放项目：</td>
					<td  align="left">
						<input type="text" name="pchname" id="pchname" autoid=30022 class="required"  >
				    </td>
				</tr>
				<tr>
					<td  align="left" colspan="2" style="padding-left: 30px;color:gray;line-height: 15px; ">
							<br>
							您的个人收入情况属个人隐私，<br>
							请您浏览之前确保您的页面没有被窥视,<br>查询结果页面将在3分钟后关闭。 
				    </td>
				</tr>
		</table>
</div>	
</form>
</div>
<div style="height:expression(document.body.clientHeight-27);">
<mt:DataGridPrintByBean name="mySalaryList" outputData="${outputData}"/>
</div>
<Script>
var arg = 6;
var fsdxIntervar;

//定时器
function fsdxIv(){
	
	fsdxIntervar = window.setInterval("fsdx()",1000) ;
}
//控制是否可点击
function fsdx(){
	//fsdxIntervar = window.setInterval(fsdx,1000);
	//document.getElementById("fsdxId").value =countyzm++ +"秒后可重新发送";
	//document.getElementById("fsdxId").disabled=true;
	//if(countyzm >30){
	//	clearInterval(fsdxIntervar);
	//	document.getElementById("fsdxId").disabled = false;
	//	document.getElementById("fsdxId").value =countyzm+ "索取手机验证码";
	//}(countyzm) ;
	arg--;
		(function(arg){
			document.getElementById("fsdxId").value =arg +"秒后可重新发送";
			document.getElementById("fsdxId").disabled=true;
				if(arg == 0){
				clearInterval(fsdxIntervar);
				document.getElementById("fsdxId").disabled = false;
				document.getElementById("fsdxId").value ="索取手机验证码";	
			    }
		})(arg);	
}

//添加一条手机验证码记录
function addYzm(){
	
	Ext.Ajax.request({
		method:'POST',
		params : { 
			rand : Math.random(),
			module:"工资单查询",
			aparttime:"5"
		},
		url:"${pageContext.request.contextPath}/salary.do?method=addAuthCode",
		success:function (response,options) {
			var request = response.responseText;
			alert(request);
			    if(request == "短信发送成功"){
			    fsdxIv();
			    arg = 30;
			    }
		},
		failure:function (response,options) {
			alert("后台出现异常,修改信息失败!");
		}
	});
}

//得到手机验证码
function getYzm(){
	var request;
	Ext.Ajax.request({
		method:'POST',
		params : { 
			rand : Math.random(),
			module:"工资单查询"
		},
		url:"${pageContext.request.contextPath}/salary.do?method=getYzm",
		success:function (response,options) {
			request = response.responseText;
		},
		failure:function (response,options) {
			alert("后台出现异常,修改信息失败!");
		}
	});
	return request;	 
}
//判断是否第一次进入
if("${ifOne}" ==""){
	var date = new Date();
	document.getElementById("year").value=date.getYear();
	if(date.getMonth()+1 ==1){
		
		document.getElementById("month").value=12;
	}else{
		document.getElementById("month").value=date.getMonth();
	}
	queryWinFun();
}else{
	window.setInterval(closePlant,180000);
}
function closePlant(){
	//closeTab(parent.tab);
	document.getElementById("identityCard").value="";
	document.getElementById("passowrd").value="";
	document.getElementById("month").value="";
	document.getElementById("year").value="";
	document.getElementById("thisForm").submit();
}

function query(){
	
	var identityCard = document.getElementById("identityCard").value;
	var passowrd = document.getElementById("passowrd").value;
	var month = document.getElementById("month").value;
	var year = document.getElementById("year").value;
	var yzmValue = document.getElementById("yzmValue").value;
	
	if(year==""){
		alert("年份不能为空!");
		return ;
	}
	if(month ==""){
		alert("月份不能为空!");
		return ;
	}
	if (!formSubmitCheck('thisForm')) {
		return;
	}
	
	//alert('临时去掉检查，方便测试，通过恢复');
	document.getElementById("thisForm").action = "${pageContext.request.contextPath}/salary.do?method=mySalaryList";
	document.getElementById("thisForm").submit();
	retrun;
	
	/*
	if(passowrd ==""){
		alert("登录密码不能为空!");
		return ;
	}
	if(identityCard ==""){
		alert("身份证号不能为空!");
		return ;
	}
	if(yzmValue ==""){
		alert("请输入手机密钥!");
		return;
	}
	*/
	
	if(identityCard !="" && passowrd !=""){
		 Ext.Ajax.request({
				method:'POST',
				params : { 
					rand : Math.random(),
					passowrd:passowrd,
					identityCard:identityCard
				},
				url:"${pageContext.request.contextPath}/salary.do?method=queryUser",
				success:function (response,options) {
					var request = response.responseText;
					if(request =="1"){
						//获取验证码
						var yzm ; 
						Ext.Ajax.request({
							method:'POST',
							params : { 
								rand : Math.random(),
								yzm:yzmValue,
								module:"工资单查询"
							},
							url:"${pageContext.request.contextPath}/salary.do?method=getYzm",
							success:function (response,options) {
								yzm = response.responseText;
								if(yzmValue == yzm){
									queryWin.hide();
								    
									//作废已使用的验证码
									var strMdl="工资单查询";
									var urlState="${pageContext.request.contextPath}/salary.do?method=updateYzmIsUse";
								    var requestString = "&module="+strMdl+"&yzm="+yzmValue;
								    ajaxLoadPageSynch(urlState,requestString);
									
								    document.getElementById("thisForm").submit();
								}else{
									if(yzm =="false"){
										alert("手机验证码超时,请重新索取手机验证码！");
										return ;
									}
									alert("验证码错误，请检查手机短信!");
									return;
								}
								
							},
							failure:function (response,options) {
								alert("后台出现异常,修改信息失败!");
							}
						});
						
					}else{
						alert("登录密码或身份证号错误！请重新检查");
						return ;
					}
					 
				},
				failure:function (response,options) {
					alert("后台出现异常,修改信息失败!");
				}
			});
	} 
	
}

function emprty()
{
	document.getElementById("year").value="";
	document.getElementById("month").value="";
	document.getElementById("passowrd").value="";
	document.getElementById("identityCard").value="";
	document.getElementById("pchname").value="";
}
 
//双击
function grid_dblclick(obj) {
	
	if(obj.pch !=""){
		
		window.location="${pageContext.request.contextPath}/salary.do?method=auditSkip&pch="+obj.pch+"&ctype=0";
	}
}

</script>
</body>
</html>
