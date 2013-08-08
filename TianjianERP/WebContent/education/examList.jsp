<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>考试列表</title>
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
          		print_examList();
				//window.open('glossary.do?method=print','','height=1000,width=2000,location=no;');
			}
        },'-',{
           text:'查询',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/query.gif',
          	handler:function(){
				goSearch();
			}
		},'->'
        ]
    });  
});
</script>
</head>
<body>
<div id="divBtn"></div>
<div>
<form name="thisForm" method="post" action="">
<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>
<div style="height:expression(document.body.clientHeight-30);overflow:auto;">
<mt:DataGridPrintByBean name="examList"/>
</div>
</form>
</div>
<div id="search" style="display:none">
<br/><br/><br/>
<div style="margin:0 20 0 20">请在下面输入查询条件：</div>
<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
	<table border="0" cellpadding="0" cellspacing="0" width="100%" bgcolor="">
		<tr align="center">
			<td align="right">考试类型：</td>
			<td align=left><input  type="text" name="examType" id="examType" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" noinput="true" onClick="onPopDivClick(this);" autoid=3052/></td>
			</tr>
			<tr>
			<td align="right">考试科目：</td>
			<td align=left><input type="text" name="examSubject" id="examSubject"/></td>
			</tr>
			<tr>	
			<td align="right">资格要求：</td>
			<td align=left><input type="text" name="qualifications" id="qualifications"/></td>
			</tr>
	</table>
</div>
</body>
<script type="text/javascript">
function goAdd(){
	window.location="education.do?method=addExam&&act=add";
}
function goEdit()
{
	if(document.getElementById("chooseValue_examList").value=="")
	{
		alert("请选择要修改的数据！");
	}
	else
	{
		window.location="education.do?method=addExam&&act=edit&&id="+document.getElementById("chooseValue_examList").value;
	}
}
function goDelete()
{
	if(document.getElementById("chooseValue_examList").value=="")
	{
		alert("请选择要删除的数据！");
	}
	else
	{
		if(confirm("确定删除此数据？")){
			window.location="education.do?method=delExam&&id="+document.getElementById("chooseValue_examList").value;
		}
	}
}
var queryWin = null;
function goSearch(){
	if(!queryWin) {
			var searchDiv = document.getElementById("search") ;
		    queryWin = new Ext.Window({
		     title: '考试查询',
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
		               	goSearch_examList();
		            
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

//双击
function grid_dblclick(obj) {
	window.location="education.do?method=addExam&&act=edit&&id="+obj.examListId;
}
</script>
</html>