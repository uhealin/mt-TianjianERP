<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>部门列表</title>
<script type="text/javascript">

Ext.onReady(function(){
	var tbar_customer = new Ext.Toolbar({
		renderTo:'gridDiv_department',
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
            text:'删除',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/delete.gif',
            handler:function () {
				goDelete();
            }
        },'-',{
            text:'打 印',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/print.gif',
            handler:function () {
            	print_department();
            }
        },'-',{
            text:'设置部门排序',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/flow.gif',
            handler:function () {
            	setOrderBy();
            }
        },'-',{
	            text:'关闭',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/close.gif',
	            handler:function () {
	            	closeTab(parent.tab);
	            	//parent.tab.remove(parent.tab.getActiveTab()); 
	            }
	        }
        ]
        });  

});

</script>
</head>
<body>
<form name="thisForm" method="post" action="" class="autoHeightDiv">
<input type="hidden" name="act" value="" id="act"/>
<mt:DataGridPrintByBean name="department"  />
</form>
</body>
<Script>

var act=document.getElementById("act");
function goAdd() { 
	act.value="add";
	window.location="department.do?method=login&act="+act.value;
}

function goDelete() {
	var id = document.getElementById("chooseValue_department").value;
	if(id == "") {
		alert("请选择要删除的部门！");
	} else {
		
		//检查部门下面有没有人
		var url1="${pageContext.request.contextPath}/department.do?method=isUpDepartment&autoId=" + id;
		var result1 = ajaxLoadPageSynch(url1,null);
		
		if(result1.indexOf("ko") < 0){
			alert(result1);
			return;
		}else{
			if(confirm("确定删除此部门？","提示")) {
			    act.value="del";
				window.location="${pageContext.request.contextPath}/department.do?method=login&act="+act.value+"&chooseValue="+id;
			}
		}
	}
}

function goEdit() {
	var id=document.getElementById("chooseValue_department").value;
	if(id==""){
		alert("请选择要修改的部门！");
	}else{
		act.value="update";
		window.location="${pageContext.request.contextPath}/department.do?method=login&act="+act.value+"&chooseValue="+id;
	}

}

var setOrderByWin = null;
function setOrderBy() {

	var url="${pageContext.request.contextPath}/department.do?method=setOrderBy&flag=window&rand=" + Math.random();
		
	if(!setOrderByWin) { 
		setOrderByWin = new Ext.Window({
	     	renderTo : Ext.getBody(),
	     	width: 500,
	     	id:'setOrderByWin',
	     	height:350,
	     	title:'设置部门排序',
	     	closable:'flase',
        	closeAction:'hide',
       	    listeners : {
	         	'hide':{
	         		fn: function () {
	         			new BlockDiv().hidden();
						setOrderByWin.hide();
						
					}
				}
	        },
	       	html:'<iframe name="orderByFrame" id="orderByFrame" scrolling="no" frameborder="0" width="100%" height="100%" src="' + url + '"></iframe>',
        	layout:'fit'
	    });
    } else {
    	document.getElementById("orderByFrame").src = url;
    }
	
	new BlockDiv().show();
	setOrderByWin.show(); 
}

</Script>

</html>
