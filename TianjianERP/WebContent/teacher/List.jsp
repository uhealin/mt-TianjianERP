<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<%@ page  import="java.util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>培训教师库维护</title>
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
        },'-',
        /**{
           text:'打印',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/print.gif',
          	handler:function(){
          		print_teacher();
				//window.open('glossary.do?method=print','','height=1000,width=2000,location=no;');
			}
        },'-',
        */{
           text:'查询',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/query.gif',
          	handler:function(){
				goSearch();
			}
		},'-',{
           text:'培训资历查询',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/query.gif',
          	handler:function(){
				goHistory();
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
<mt:DataGridPrintByBean name="teacher"/>
</div>
</form>
</div>
<div id="search" style="display:none">
<br/><br/><br/>
<div style="margin:0 20 0 20">请在下面输入查询条件：</div>
<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
	<table border="0" cellpadding="0" cellspacing="0" width="100%" bgcolor="">
		<tr align="center">
			<td align="right">姓名：</td>
			<td align=left><input  type="text" name="name" id="name"/></td>
			</tr>
			<tr>
			<td align="right">教师编号：</td>
			<td align=left><input type="text" name="teacherNum" id="teacherNum"/></td>
			</tr>
			<tr>	
			<td align="right">职称：</td>
			<td align=left><input type="text" name="title" id="title"/></td>
			</tr>
			<tr>
			<td align="right">职位：</td>
			<td align=left>
			<input  type="text" name="position" id="position"/>
			</td>
			</tr>
     		<tr>
      		<td align="right">授课专业：</td>
      		<td align=left><input name="professional" type="text" id="professional"/></td>
     		</tr>
     		<tr>
      		<td align="right">性别：</td>
      		<td align=left>
      		<select id="sex" name="sex">
      		<option value="男">男</option>
      		<option value="女">女</option>
      		</select></td>
     		</tr>
     		<tr>	
			<td align="right">工作单位：</td>
			<td align=left><input type="text" name="company" id="company"/></td>
			</tr>	
	</table>
</div>
</body>
<script type="text/javascript">
function goAdd()
{
	window.location="teacher.do?method=add&&act=add";
}

function goEdit()
{
	if(document.getElementById("chooseValue_teacher").value=="")
	{
		alert("请选择要修改的数据！");
	}
	else
	{
		window.location="teacher.do?method=add&&act=edit&&id="+document.getElementById("chooseValue_teacher").value;
	}
}

function goDelete()
{
	if(document.getElementById("chooseValue_teacher").value=="")
	{
		alert("请选择要删除的数据！");
	}
	else
	{
		if(confirm("确定删除此数据？")){
			window.location="teacher.do?method=del&&id="+document.getElementById("chooseValue_teacher").value;
		}
	}
}
var queryWin = null;
function goSearch(){
	if(!queryWin) {
			var searchDiv = document.getElementById("search") ;
		    queryWin = new Ext.Window({
		     title: '教师查询',
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
		               	goSearch_teacher();
		            
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

function goHistory(){
	if(document.getElementById("chooseValue_teacher").value=="")
	{
		alert("请选择要查看的数据！");
	}
	else
	{
			var url="teacher.do?method=getHistory&&id="+document.getElementById("chooseValue_teacher").value;
			var pageId = new Date().getTime();
			parent.openTab(pageId,"评价维护标准",url);
	}
}

//双击
function grid_dblclick(obj) {
	window.location="teacher.do?method=add&&act=edit&&id="+obj.teacherId;
}
</script>
</html>