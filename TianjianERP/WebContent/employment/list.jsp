<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
<script type="text/javascript">
Ext.onReady(function(){
	var tbar_customer = new Ext.Toolbar({
		renderTo:'divBtn',
           items:[{
            text:'增加',
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
           text:'删除',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/delete.gif',
          	handler:function(){
				goDelete();
			}
        },'-',{
           text:'打印',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/print.gif',
          	handler:function(){
          		print_education();
				//window.open('glossary.do?method=print','','height=1000,width=2000,location=no;');
			}
        },'-',{
           text:'查询',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/query.gif',
          	handler:goSearch  
			 
		},'-',{
			text:'批量修改状态',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/import.gif',
           	handler:function(){
 				goImport();
 			}
		},'-',{
			text:'查看详细信息',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/query.gif',
           	handler:function(){
 				goLook();
 			}
		},'-',{
			text:'分发简历',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/query.gif',
           	handler:function(){
 				distributionResume();
 			}
		},'-',{
			text:'分发简历历史',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/query.gif',
           	handler:function(){
 				distributionHistory();
 			}
		},'->'
		]
        });  
        
        
var queryWin = null;
function goSearch(){
	if(!queryWin) {
			var searchDiv = document.getElementById("search");
			searchDiv.style.display = "" ;
		    queryWin = new Ext.Window({
		     title: '查询',
   			 contentEl:'search',
		     width: 455,
		     height:295,
		  	 //modal:true,
		        closeAction:'hide',
		        listeners   : {
		        	'hide':{fn: function () {
					new BlockDiv().hidden();
					queryWin.hide();	         	
		        	}}
		        },
		       layout:'fit',
		    buttons:[{
		           text:'确定',
		         	handler:function(){
		               	queryWin.hide();
		               	goSearch_employment();
		            
		           }
		       },{
		           text:'取消',
		           handler:function(){
		               queryWin.hide();
		           }
		       }]
		    });
	   }
	   new BlockDiv().show();
	   queryWin.show();
}

});	        	
</script>
</head>
<body>
<div id="divBtn"></div>
<input type="hidden" id="canUser" name="canUser"/>
<input type="hidden" id="canUserName" name="canUserName"/>
<div>
<form name="thisForm" method="post" action="">
<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>
<div style="height:expression(document.body.clientHeight-28);overflow:auto;">
<mt:DataGridPrintByBean name="employment"/>
</div>
<div id="search" style="display:none">
<br/><br/><br/>
<div style="margin:0 20 0 20">请在下面输入查询条件：</div>
<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
	<table border="0" cellpadding="0" cellspacing="0" width="100%" bgcolor="">
		<tr align="center">
			<td align="right">姓名：</td>
			<td align=left><input  type="text" name="chineseName" id="chineseName"/></td>
		</tr>
		<tr>
			<td align="right">性别：</td>
			<td align=left><input type="text" name="gender" id="gender"/></td>
		</tr>
		<tr>	
			<td align="right">学历:</td>
			<td align=left>
				<input type="text" name="degree" id="degree" autoid=3056 onkeydown="onKeyDownEvent();" 
					onkeyup="onKeyUpEvent();" 
					onclick="onPopDivClick(this);" 
					valuemustexist=true
					noinput=true/>
			</td>
		</tr>
	</table>
</div>

</form>
</div>

<div id="search1" style="display:none;">
<br>
<table border="0" cellpadding="0" cellspacing="0" bgcolor="" width="100%" align="center">
	<tr>
      <td align="right">简历状态：</td>
      <td align=left>
      		<select id='state1' name="state1" style="width: 130px">
      		<c:choose>
      		<c:when test="${userSession.userAuditDepartmentName =='北京--人力资源部' }">
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
</body>
<script type="text/javascript">
function goAdd(){
	window.location="employment.do?method=addPersonal";
}

function goEdit(){
	var choose = getChooseValue("employment");
	if(choose == "")
	{
		alert("请选择要修改的数据！");
	}
	else{
		if(choose.indexOf(",")>-1){
			alert("修改简历一次只能修改一个，请重新选择！");
			return;
		}
		window.location="employment.do?method=editPersonal&&id="+choose;
	}
}

function goDelete(){
	var choose = getChooseValue("employment");
	if(choose == ""){
		alert("请选择要删除的数据！");
	}else if(choose.indexOf(",")>-1){
		alert("只能选择一条记录进行删除");
	}else{
		if(confirm("确定删除此数据？")){
			window.location="employment.do?method=delPersonal&&id="+choose;
		}
	}
}

function  goSearch(){
	if(!queryWin) {
			var searchDiv = document.getElementById("search") ;
		    queryWin = new Ext.Window({
		     title: '简历查询',
		     renderTo :'searchWin',
		     width: 455,
		     height:295,
		  	 //modal:true,
		        closeAction:'hide',
		        listeners   : {
		        	'hide':{fn: function () {
					new BlockDiv().hidden();
					queryWin.hide();	         	
		        	}}
		        },
		       layout:'fit',
			  html:searchDiv.innerHTML,
		    buttons:[{
		           text:'确定',
		         	handler:function(){
		               	queryWin.hide();
		               	goSearch_employment();
		           }
		       },{
		           text:'取消',
		           handler:function(){
		               queryWin.hide();
		           }
		       }]
		    });
	   }
	   new BlockDiv().show();
	   queryWin.show();
}

function goImport(){
	var choose = getChooseValue("employment");
	if(choose == ""){
		alert("请选择要修改状态的简历！");
	}else{
		queryWinFun1(choose);
	}
}

function goLook(){
	var choose = getChooseValue("employment");
	if(choose == ""){
		alert("请选择要修改状态的简历！");
	}else if(choose.indexOf(",")>-1){
		alert("只能选择一条记录进行查看");
	}else{
		window.location="employment.do?method=lookPersonal&&id="+choose;
	}
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
         					table : "k_employment_personalinfo",
         					unid : choose,
         					state : document.getElementById("state1").value
         				},
         				url:'${pageContext.request.contextPath}/employment.do?method=updateJob',
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

function distributionResume(){
	var choose = getChooseValue("employment");
	if(choose == ''){
		alert("请选择要分发的数据");
		return;
	}
	show_selectUser('canUserName','canUser');
	var userId = document.getElementById("canUser").value;
	if(userId==''){
		return;
	}
	if(userId.indexOf(",")>-1){
		alert("只能选择一个人分发");
		document.getElementById("canUser").value == '';
		return;
	}
	window.location="employment.do?method=addDistribution&&id="+choose+"&&userId="+userId;
}

function distributionHistory(){
	var choose = getChooseValue("employment");
	if(choose == ''){
		alert("请选择要查看的数据");
		return;
	}else if(choose.indexOf(",")>-1){
		alert("只能选择一条数据查看");
		return;
	}else{
		parent.openTab("distributionHistoryId","简历分发历史","employment.do?method=distributionHistory&&employmentId="+choose);
	}
}
</script>
</html>