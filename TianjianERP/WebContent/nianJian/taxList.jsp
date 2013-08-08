<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<%@ taglib uri="http://ckeditor.com" prefix="ckeditor" %>
<script type="text/javascript" src="${pageContext.request.contextPath}/ckeditor/ckeditor.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/ckfinder/ckfinder.js"></script>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/images/editor.js" charset=GBK></script>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/images/DhtmlEdit.js" charset=GBK></script>    

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8/">
<title>人员列表</title>
<script>
function exit(){
	new Ext.Toolbar({
		renderTo:'GridDiv_taxNianJianList',
		items:[{ 
				text:'查询',
				icon:'${pageContext.request.contextPath}/img/query.gif' ,
				handler:function(){
					queryWinFun();
				}
			},'-',{ 
				text:'查看详细信息',
				icon:'${pageContext.request.contextPath}/img/query.gif',
				handler:function(){
					queryDetail();
				}
			},'-',{
				text:'生成注税年检表',
				icon:'${pageContext.request.contextPath}/img/check.png' ,
				handler:function(){
					createReport();
				}
			},'-',{ 
				text:'修改',
				icon:'${pageContext.request.contextPath}/img/edit.gif' ,
				handler:function(){
					edit();
				}
			}
		]
	});
	
	
}

var queryWin = null;
function queryWinFun(){
	document.getElementById("search").style.display = "";
	if(queryWin == null) { 
	    queryWin = new Ext.Window({
			title: '查询',
			width: 470,
			height:360,
			contentEl:'search',  
	        closeAction:'hide',
	        modal:true,
	        listeners:{
				'hide':{fn: function () {
					 document.getElementById("search").style.display = "none";
				}}
			},
	        layout:'fit', 
	        contentEl:'search',
		    buttons:[{
	            text:'确定',
	            icon:'${pageContext.request.contextPath}/img/confirm.gif',
	          	handler:function() {
	        		goSearch_taxNianJianList(2);
	          		queryWin.hide();
	          	}
	        },{
	            text:'重置',
	            icon:'${pageContext.request.contextPath}/img/refresh.gif',
	            handler:function(){
	            	reset("thisForm");
	            }
	        },{
	            text:'取消',
	            icon:'${pageContext.request.contextPath}/img/close.gif',
	            handler:function(){
	            	queryWin.hide();
	            }
	        }]
	    });
	}

	queryWin.show();
}

Ext.onReady(exit);
</script>
</head>

<body>
<div class="autoHeightDiv">
	<mt:DataGridPrintByBean name="taxNianJianList" />
</div>

<div id="search" style="display: none;">
	<form name="thisForm" id="thisForm" method="post">
		<br/>
		<div style="margin:0 20 0 20">请在下面输入查询条件：</div>
		<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
		
		<table border="0" cellpadding="5" cellspacing="10" width="100%" align="center">
			<tr>
				<td align="right">姓名：</td>
				<td align=left>
					<input type="text" name="name" id="name" size="30"/>
				</td>
			</tr>
			<tr>
				<td align="right">所属部门：</td>
				<td align=left>
					<input type="text" name="departmentId" id="departmentId"  autoid=30026 size="21"/>
				</td>
			</tr>
			<tr>
				<td align="right">性别：</td>
				<td align=left>
					<input type="text" name="sex" id="sex"  autoid=10001 refer="用户性别" size="21"/>
				</td>
			</tr>
			<tr>
				<td align="right">荣誉注税：</td>
				<td align=left>
					<input type="text" name="taxRegister" id="taxRegister"  autoid=10001 refer="是否" size="21"/>
				</td>
			</tr>
			<tr>
				<td align="right">主要项目清单生成模式：</td>
				<td align=left>
					<input type="text" name="reportType" id="reportType"  size="30"/>
				</td>
			</tr>
			<tr>
				<td align="right">是否生成过年检表：</td>
				<td align=left>
					<input type="text" name="taxCreate" id="taxCreate" autoid=10001 refer="是否" size="21"/>
				</td>
			</tr>
		</table>		
	</form>		
</div>
</body>
<script type="text/javascript">
//修改项目生成模式
 function edit(){
	 var chooseValue = document.getElementById("chooseValue_taxNianJianList").value;
	 
	 if(chooseValue==""){
		 alert("请选择要修改的人员!");
		 return;
	 }else{
		 var data={a:"部门内随机",b:"签字项目金额前20位"};
		    
		    var msg="";
		    for(var k in data){
		       msg+="<br/>"+k+":"+data[k];
		    }
		    
		    var btns={ok:"确认",cancel:"取消"};
		 
		    Ext.MessageBox.prompt("设置项目清单生成模式","设置为(请输入对应英文代码 )"+msg
		     ,function(e,text){ 
		        if(e=="cancel")return false;
		        if(!data[text]){
		          alert("请输入有效的代码!");return false;
		        }
		        var url="nianJian.do";
		        var param={method:"modifyProject",uuid:chooseValue,
		        state:data[text]};
		        $.post(url,param,function(str){alert(str);window.location.reload();});
		     }
		    
		    );

	 }
 }
	//生成注税年检表
 function createReport(){
	 var chooseValue = document.getElementById("chooseValue_taxNianJianList").value;
	 
	 if(chooseValue==""){
		 alert("请选择要操作的人员!");
		 return;
	 }else{
		 var url="nianJian.do?method=editTax&chooseValue="+chooseValue;
		 parent.openTab(chooseValue,"生成注税年检申报表",url);
	 }
	 
	
 }
//查看明细
function queryDetail(){
	var chooseValue = document.getElementById("chooseValue_taxNianJianList").value;
	 
	 if(chooseValue==""){
		 alert("请选择要操作的人员!");
		 return;
	 }else{
		 var url="nianJian.do?method=editTax&chooseValue="+chooseValue+"&opt=view";
		 parent.openTab(new Date().getTime(),"查看注税年检申报表",url);
	 }
}
</script>
</html>