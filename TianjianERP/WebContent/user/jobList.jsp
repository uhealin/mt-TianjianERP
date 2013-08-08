<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>招聘计划管理</title>
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


Ext.onReady(function(){
	
	var tbar_customer = new Ext.Toolbar({
		renderTo:'gridDiv_${tableid}',
           items:[
        <c:if test="${param.mode!=readonly}">
        {
            text:'发布招聘岗位',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/add.gif',
            handler:function(){
            	goAdd();
			}
      	}
        ,{
           text:'修改招聘岗位',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/edit.gif',
          	handler:function(){
				goEdit();
			}
        },'-',/* {
           text:'审阅网投简历',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/edit.gif',
          	handler:function(){
				goEdits();
			}
        },'-',*/
        {
           text:'启动岗位网投',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/edit.gif',
          	handler:function(){
				updateJob("有效");
			}
        },{
           text:'关闭岗位网投',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/edit.gif',
          	handler:function(){
				updateJob("失效");
			}
        },'-',
        </c:if>
        <c:if test="${param.mode==readonly}">
        {
            text:'查看投递状况',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/edit.gif',
           	handler:function(){
           		goViewResume();
 			}
         },'-',
         </c:if>         
       {
           text:'打印',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/print.gif',
          	handler:function(){
				print_${tableid}();
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
	            text:'关闭',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/close.gif',
	            handler:function () {
	            	closeTab(parent.tab);
	            }
	     }
        ]
        });  
	
	new Ext.form.DateField({
		applyTo : 'toworktime',
		width: 133,
		format: 'Y-m-d'
	});

});

</script>


</head>
<body>
<form name="thisForm" method="post" action="" >
<div style="height:expression(document.body.clientHeight-23);" >
<mt:DataGridPrintByBean name="${tableid}"  />
</div>

<input type="hidden" id="flag" name="flag" value="${flag }">

<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>

<div id="search" style="display:none;">
<br>
<div style="margin:0 20 0 20">请在下面输入查询条件：</div>
<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
<table border="0" cellpadding="0" cellspacing="0" bgcolor="" width="100%" align="center">
	<tr>
      <td align="right">部门：</td>
      <td align=left>
        <input name="departmentid" type="text" id="departmentid"  onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist=true autoid=123 noinput=true autoHeight=150 >
      </td>
   	</tr>
	<tr>
      <td align="right">岗位名称：</td>
      <td align=left>
        <input value='' name="jobname" type="text" id="jobname" >
      </td>
   	</tr>
   	<tr>
      <td align="right">工作城市：</td>
      <td align=left>
      	<input value='' name="city" type="text" id="city" autoid=740 multilevel=true onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist=true noinput=true autoHeight=150>
      </td>
	</tr>
	<tr>
      <td align="right">到岗时间：</td>
      <td align=left>
      	<input value='' name="toworktime" type="text" id="toworktime"  >
      </td>
	</tr>
	<tr>
      <td align="right">工时要求：</td>
      <td align=left>
      	<select id='working' name="working" style="width: 130px">
      		<option value = ''  >--请选择--</option>
			<option value = '全职'  >全职</option>
			<option value = '实习'  >实习</option>
		</select>
      </td>
	</tr>
	<tr>
      <td align="right">状态：</td>
      <td align=left>
      		<select id='state' name="state" style="width: 130px">
      		<option value = ''  >--请选择--</option>
			<option value = '有效'  >有效</option>
			<option value = '失效'  >失效</option>
			</select>
      </td>
	</tr>
</table>
</div>
</form>
</body>
</html>
<script type="text/javascript">
//新增
function goAdd(){
	window.location="${pageContext.request.contextPath}/job.do?method=edit&table=k_job&flag=list&rand="+Math.random()+"&who=list";
}

//申请
function goEdits(){
	var id=document.getElementById("chooseValue_${tableid}").value;
	if(id==""){
		alert("请选择要查看的招聘计划！");
	}else{
		window.location="${pageContext.request.contextPath}/job.do?method=alEdit&table=k_job&flag=list&unid="+id+"&rand="+Math.random();
	}
} 

function goViewResume(){
	var id=document.getElementById("chooseValue_${tableid}").value;
	if(id==""){
		alert("请选择要查看的招聘计划！");
	}else{
		//
		var url="${pageContext.request.contextPath}/formDefine.do?method=formListView&uuid=52d67802-2c6b-48c9-ab5b-5f6b149e0d19&jobid="+id;
		//alert(url);
		window.location=url;
	}
} 

function goEdit(){
	var id=document.getElementById("chooseValue_${tableid}").value;
	if(id==""){
		alert("请选择要查看的招聘计划！");
	}else{
		window.location="${pageContext.request.contextPath}/job.do?method=edit&table=k_job&flag=list&unid="+id+"&rand="+Math.random();
	}
}

//启动/关闭
function updateJob(opt){
	var id=document.getElementById("chooseValue_${tableid}").value;

	if(opt == "有效") {
		opt = 1;
	} else {
		opt = 2;
	}	
	
	if(id==""){
		alert("请选择需要改变状态的招聘计划！");
	}else{
		Ext.Ajax.request({
			method:'POST',
			url:'${pageContext.request.contextPath}/job.do?method=updateJob&table=k_job&state='+opt+"&unid="+id,
			success:function (response,options) {
				//alert(response.responseText);	
				var result = response.responseText;
				if(result.indexOf("OK")>-1){
					alert("修改招聘计划的状态成功！");
					goSearch_${tableid}();
				}
			},
			failure:function (response,options) {
				alert("后台出现异常,获取文件信息失败!");
			}
		});
	}
}
function grid_dblclick(obj, tableId) {
		var id=document.getElementById("chooseValue_${tableid}").value;
		alter(id);
		if(obj.id){
		window.open="${pageContext.request.contextPath}/job.do?method=edit&table=k_job&flag=${flag}&unid="+obj.id+"&rand="+Math.random();
  	}
  }
</script>

