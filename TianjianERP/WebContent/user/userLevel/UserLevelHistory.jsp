<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>人员考核历史记录</title>
<script type="text/javascript">


function ext_init(){ 
	var tbar = new Ext.Toolbar({
		renderTo: 'divBtn',
		items:[{
			text:'新增',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/add.gif',
			handler:function () {
				addLevel();
			}
		},'-',{
           text:'查看',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/edit.gif',
           handler:function(){
				checkLevel();
		   }
        },'-',{
			text:'审批',
			id:'del',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/confirm.gif',
			handler:function(){
				passExamine();
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


<input type="hidden" name="userid" id="userid" value="${userid}"><!--

<input type="button" class="flyBT" value="新  增" onclick="addLevel();">
<input type="button" class="flyBT" value="查  看" onclick="checkLevel();">
<input type="button" class="flyBT" value="审  批" onclick="passExamine();">
<input type="button" class="flyBT" value="打  印"
	onclick="print_levellist();">
<br>

<table width="100%" cellspacing="0" cellpadding="0">
	<tr>
		<td>

		<fieldset><legend>考核查找</legend>

		<table width="100%" cellspacing="0" cellpadding="0">
			<tr>
				<td align="center" nowrap="nowrap">人员姓名：&nbsp; <input
					type="text" name="userName" id="userName" size="20" />
				&nbsp;&nbsp; 提交人：<input type="text" name="recorder" id="recorder" />
				&nbsp;&nbsp; 审批时间：<input type="text" name="examineTime"
					id="examineTime" showcalendar="true" class="validate-date-cn" /></td>
			</tr>

			<tr>
				<td align="center" height="30" valign="bottom"><input
					type="button" size="10" style="cursor:hand" value="确  定"
					onclick="return goSearch_levellist();" class="flyBT">
				&nbsp;&nbsp;&nbsp;&nbsp;<input type="button" size="10"
					style="cursor:hand" value=" 清 空 " onclick="clearAll();"
					class="flyBT"></td>
			</tr>

		</table>
		</fieldset>
		</td>
	</tr>
</table>
<br />

-->
<div style="height:expression(document.body.clientHeight-28);" >
<mt:DataGridPrintByBean name="levellist" />
</div>

</body>
<script>
	
	//判断值情况方法(缺）
	function getEstate(autoid){
		var aJax = new ActiveXObject("Microsoft.XMLHTTP");
	    aJax.open("POST","${pageContext.request.contextPath}/userLevel.do?method=getestate&autoid="+autoid,false);
	    aJax.send();
	    
	    var result = aJax.responseText;

	    if(result=="yes"){   	
	    	alert("该记录已评审通过,不能重新评审!");    	
	    	return false;
	    }else{
	    	return true;
	    }
	}

	function passExamine(){
		var recode = document.getElementById("chooseValue_levellist").value;
		
		if(recode==""){
			alert("请选择要审批的评级记录！");
			return;
		}else{
			if(getEstate(recode)){
				if("${userid}"=="" || "all"=='${all}'){
					window.location = "${pageContext.request.contextPath}/userLevel.do?method=exitUserLevel&userid="+recode+"&onlylook=false&toall=true&all=all";		
				}else{
					window.location = "${pageContext.request.contextPath}/userLevel.do?method=exitUserLevel&userid="+recode+"&onlylook=false";				
				}			
			}				
		}
	}
	
	function addLevel(){	
		var userid = document.getElementById("userid").value;

		if(userid=="" || "all"=='${all}'){			
			window.location = "${pageContext.request.contextPath}/userLevel.do?method=setLevel&toall=true&all=all&userid="+userid;				
		}else{
			window.location = "${pageContext.request.contextPath}/userLevel.do?method=setLevel&userid="+userid;
		}		
	}
	
	
	function checkLevel(){
		var recode = document.getElementById("chooseValue_levellist").value;
		
		if(recode==""){
			alert("请选择要查看的评级记录！");
			return;
		}else{
			if("${userid}"=="" || "all"=='${all}'){
				window.location = "${pageContext.request.contextPath}/userLevel.do?method=exitUserLevel&userid="+recode+"&onlylook=true&toall=true&all=all";		
			}else{
				window.location = "${pageContext.request.contextPath}/userLevel.do?method=exitUserLevel&userid="+recode+"&onlylook=true&all=${all}";		
			}
		}		
	}
	
	function goSort(recode){
		var all = "${all}";
		myOpenUrlByWindowOpen("${pageContext.request.contextPath}/userLevel.do?method=exitUserLevel&userid="+recode+"&all="+all,"","");
	}
	
	function clearAll(){	
		var all = "${all}";
		if("${userid}"=="" || "all"=='${all}'){
			window.location = "${pageContext.request.contextPath}/userLevel.do?method=levelHistory&all="+all;
		}else{
			window.location = "${pageContext.request.contextPath}/userLevel.do?method=levelHistory&userid=${userid}&all="+all;
		}	
	}
	
</script>
</html>
