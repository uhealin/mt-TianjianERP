<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>简历库</title>
<script type="text/javascript">

var queryWin = null;
function queryWinFun(id){
	var searchDiv = document.getElementById("search") ;
	searchDiv.style.display = "" ; 
	    queryWin = new Ext.Window({
			title: '查询',
			contentEl:'search',
	     	renderTo : searchWin,
	     	width: 350,
	     	height:300,
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
          			goSearch_${tableid}();
          			queryWin.hide();
            	}
        	},{ 
            	text:'取消',
            	handler:function(){
               		queryWin.hide();
            	}
        	}]
	    });
    new BlockDiv().show();
    queryWin.show();
}

var queryWin1 = null;
function queryWinFun1(choose){
	var searchDiv = document.getElementById("search1") ;
	searchDiv.style.display = "" ; 
	    queryWin1 = new Ext.Window({
			title: '批量修改状态',
			contentEl:'search1',
	     	renderTo : searchWin,
	     	width: 350,
	     	height:150,
        	closeAction:'hide',
       	    listeners : {
	         	'hide':{
	         		fn: function () {
	         			new BlockDiv().hidden();
						queryWin1.hide();
					}
				}
	        },
        	layout:'fit',
	    	buttons:[{
            	text:'确定',
          		handler:function(){
          			Ext.Ajax.request({
         				method:'POST',
         				params:{
         					table : "k_resume",
         					unid : choose,
         					state : document.getElementById("state1").value
         				},
         				url:'${pageContext.request.contextPath}/job.do?method=updateJob',
         				success:function (response,options) {
         					//alert(response.responseText);	
         					var result = response.responseText;
         					if(result.indexOf("OK")>-1){
         						alert("状态修改成功！");
         						goSearch_${tableid}();
         					}
         				},
         				failure:function (response,options) {
         					alert("后台出现异常,获取文件信息失败!");
         				}
          			
          			});
          			queryWin1.hide();
            	}
        	},{ 
            	text:'取消',
            	handler:function(){
               		queryWin1.hide();
            	}
        	}]
	    });
    new BlockDiv().show();
    queryWin1.show();
}

Ext.onReady(function(){
	
	var tbar_customer = new Ext.Toolbar({
		renderTo:'gridDiv_${tableid}',
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
            text:'批量修改状态',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/import.gif',
           	handler:function(){
 				goImport();
 			}
         },'-',{
            text:'打印',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/print.gif',
           	handler:function(){
 				print_${tableid}();
 			}
         },'-',{
           text:'删除',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/delete.gif',
          	handler:function(){
				goDelete();
			}
        },'-',{
			text:'查询',
			id:'btn-query',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
			handler:function(){
				queryWinFun();
			}
		},'-',{
           text:'批量导入',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/import.gif',
          	handler:function(){
          		window.location="${pageContext.request.contextPath}/job.do?method=Upload";
			}
        },'-',{
            text:'发送短信',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/flow.gif',
           	handler:function(){
           		window.location="${pageContext.request.contextPath}/job.do?method=note";
 			}
         },'-',{
	            text:'关闭',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/close.gif',
	            handler:function () {
	            	closeTab(parent.tab);
	            }
	     }
        ]
        });  

});

</script>


</head>
<body>
<form name="thisForm" method="post" action="" >
<div style="height:expression(document.body.clientHeight-23);" >
<mt:DataGridPrintByBean name="${tableid}"  />
</div>

<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>

<div id="search" style="display:none;">
<br>
<div style="margin:0 20 0 20">请在下面输入查询条件：</div>
<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
<table border="0" cellpadding="0" cellspacing="0" bgcolor="" width="100%" align="center">
	<tr>
      <td align="right">招聘岗位：</td>
      <td align=left>
        <input name="jobname" type="text" id="jobname" autoid=741 onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist=true >
      </td>
   	</tr>
   	<tr>
      <td align="right">姓名：</td>
      <td align=left>
      	<input  name="name" type="text" id="name"  >
      </td>
	</tr>
	<tr>
      <td align="right">证件类型：</td>
      <td align=left>
      	<input   name="paperstype" type="text" id="paperstype"  autoid=700 refer='证件类型' onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist=true noinput=true autoHeight=150 >
      </td>
	</tr>
	<tr>
      <td align="right">证件编号：</td>
      <td align=left>
      	<input  name="papersnumber" type="text" id="papersnumber"  >
      </td>
	</tr>
	<tr>
      <td align="right">状态：</td>
      <td align=left>
      		<select id='state' name="state" style="width: 130px">
      		<c:choose>
      		<c:when test="${userSession.userAuditDepartmentName !='人力资源与培训部' }">
				<option value = ''  >--请选择--</option>
	      		<option value = '退回'  >退回</option>
				<option value = '候选'  >候选</option>
				<option value = '入选'  >入选</option>
				<option value = '通知到达'  >通知到达</option>
				<option value = '通知未达'  >通知未达</option>
				<option value = '初试通过'  >初试通过</option>
				<option value = '复式通过'  >复式通过</option>
				<option value = '录取'  >录取</option>
				<option value = '被拒'  >被拒</option>
      		</c:when>
      		<c:otherwise>
      			<option value = '录取'  >录取</option>
      		</c:otherwise>
      		</c:choose>
			</select>
      </td>
	</tr>
</table>
</div>

<div id="search1" style="display:none;">
<br>
<table border="0" cellpadding="0" cellspacing="0" bgcolor="" width="100%" align="center">
      <td align="right">简历状态：</td>
      <td align=left>
      		<select id='state1' name="state1" style="width: 130px">
      		<c:choose>
      		<c:when test="${userSession.userAuditDepartmentName =='人力资源与培训部' }">
      		<option value = '退回'  >退回</option>
			<option value = '候选'  >候选</option>
			<option value = '入选'  >入选</option>
<c:if test="${param.opt == null || param.opt == '' }">			
			<option value = '通知到达'  >通知到达</option>
			<option value = '通知未达'  >通知未达</option>
			<option value = '初试通过'  >初试通过</option>
			<option value = '复式通过'  >复式通过</option>
			<option value = '录取'  >录取</option>
			<option value = '被拒'  >被拒</option>
</c:if>			
			</c:when>
      		<c:otherwise>
      			<option value = '入选'  >入选</option>
      		</c:otherwise>
      		</c:choose>
			</select>
      </td>
	</tr>
</table>
</div>

<input type="hidden" id="flag" name="flag" value="${flag }">

</form>
</body>
</html>
<script type="text/javascript">
//新增
function goAdd(){
	window.location="${pageContext.request.contextPath}/job.do?method=edit&table=k_resume&rand="+Math.random();
}

//修改
function goEdit(){
	//var id=document.getElementById("chooseValue_${tableid}").value;
	var choose = getChooseValue("${tableid}");
	if(choose == ""){
		alert("请选择要修改的简历！");
	}else{
		if(choose.indexOf(",")>-1){
			alert("修改简历一次只能修改一个，请重新选择！");
			return;
		}
		var id = choose;
		window.location="${pageContext.request.contextPath}/job.do?method=edit&table=k_resume&unid="+id+"&rand="+Math.random();
	}
}

function goDelete(){
	var id=getChooseValue("${tableid}");
	if(id==""){
		alert("请选择要删除的简历！");
	}else{
		var str="您的操作会永远删除该记录，您将无法还原该记录，是否继续？";
		if(confirm(str,"提示")){
			Ext.Ajax.request({
			method:'POST',
			params:{unid : id},
			url:'${pageContext.request.contextPath}/job.do?method=delete&table=k_resume',
			success:function (response,options) {
				//alert(response.responseText);	
				var result = response.responseText;
				if(result.indexOf("OK")>-1){
					alert("删除简历成功！");
					goSearch_${tableid}();
				}
			},
			failure:function (response,options) {
				alert("后台出现异常,获取文件信息失败!");
			}
		});		
		}
	}
}
function goImport(){
	var choose = getChooseValue("${tableid}");
	if(choose == ""){
		alert("请选择要修改状态的简历！");
	}else{
		queryWinFun1(choose);
		
		
		
	}
}


</script>
