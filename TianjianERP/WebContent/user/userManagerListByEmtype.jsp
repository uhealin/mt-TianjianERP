<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"  errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>人员管理</title>
<script type="text/javascript">
Ext.onReady(function(){
	var tbar_customer = new Ext.Toolbar({
		renderTo:'divBtn',
           items:[{
			text:'新增',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/add.gif',
			handler:function(){
				goAdd();
			}
		},'-',{
			text:'修改',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/edit.gif',
			handler:function(){
				goEdit();
			}
		},'-',{
			text:'作废',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/delete.gif',
			handler:function(){
				goCancel();
			}
		},'-',{
			text:'修改所内排序',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/edit.gif',
			handler:function(){
				Ext.MessageBox.prompt("修改所内排序","请输入新的所内排序号(数字)",function(e,text){
					if(e!="ok"&&e!="yes")return;
					var url="user.do";
					
					var uservalue=document.getElementById("chooseValue_user_${emtype }").value;
					var param={method:"updatePccpa_seqno",userid:uservalue,pccpa_seqno:text};
					
					$.post(url,param,function(str){
						alert(str);
						window.location.reload();
					});
				});
				
			}
		},'->'
        ]
        });  
});

function goAdd()
{
	window.location="${pageContext.request.contextPath}/user.do?method=Edit&UserOpt=1&departmentidNList=${departmentidNList}&areaid=${areaid}&emtype=${emtype}";
}

function goCancel()
{
	var uservalue=document.getElementById("chooseValue_user_${emtype }").value;
	if(uservalue==""){
		alert("请选择要查看的对象！");
	}else{
		if(confirm("确定作废？")){
			Ext.Ajax.request({url:"user.do",success:function(res,config){ 
				if(res.responseText=='ok'){
					alert("作废成功！");
					window.location.reload(); 
				}else{
					alert("作废失败！");
				}
			},method:"post",params:{method:"changeEmType",emtype:"024",userid:uservalue}});
		}
	}
}
function goView()
{
	var uservalue=document.getElementById("chooseValue_user_${emtype }").value;
	if(uservalue==""){
		alert("请选择要查看的对象！");
	}else{
		window.location = "${pageContext.request.contextPath}/oa/UserInformationFrame.jsp?myUserid="+uservalue+"&judge=${judge}";
	}
}


function goEdit()
{
	var uservalue=document.getElementById("chooseValue_user_${emtype }").value;
	if(uservalue=="")
	{
		alert("请选择要修改的人员！");
	}
	else
	{
		var aJax = new ActiveXObject("Microsoft.XMLHTTP");
		var url="${pageContext.request.contextPath}/user.do?method=CheckName&id="+uservalue;

	    aJax.open("POST", url, false);
	    aJax.send();

	    if(aJax.responseText == 'yes'){
	    	alert("系统管理员不能被修改！");
	    }else{
			window.location="${pageContext.request.contextPath}/user.do?method=Edit&UserOpt=2&id="+uservalue+"&departmentidNList=${departmentidNList}&areaid=${areaid}&emtype=${emtype}";
	    }

	}
}
</script>
</head>
<body style="height: 100%">
<div id="divBtn"></div>
<div style="width: 100%;height: 95%">
<mt:DataGridPrintByBean name="user_${emtype }"  />
</div>

</body>
</html>