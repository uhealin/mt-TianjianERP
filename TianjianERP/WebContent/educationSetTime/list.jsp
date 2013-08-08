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
          		print_educationSetTime();
				//window.open('glossary.do?method=print','','height=1000,width=2000,location=no;');
			}
        },'-',{
           text:'查询',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/query.gif',
          	handler:goSearch  
			 
		},'->'
        ]
        });  
        
        
var queryWin = null;
function goSearch(){
	if(!queryWin) {
			var searchDiv = document.getElementById("search");
			searchDiv.style.display = "" ;
		    queryWin = new Ext.Window({
		     title: '必修学时查询',
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
		               	goSearch_educationSetTime();
		            
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
<div>
<form name="thisForm" method="post" action="">
<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>
<div style="height:expression(document.body.clientHeight-30);overflow:auto;">
<mt:DataGridPrintByBean name="educationSetTime"/>
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
			<td align="left"><input  type="text" name="userName" id="userName"/></td>
			</tr>
			<tr>
			<td align="right">职级：</td>
			<td align="left"><input type="text" name="rankId" id="rankId" onkeydown="onKeyDownEvent();" 
					onkeyup="onKeyUpEvent();" 
					onclick="onPopDivClick(this);" 
					valuemustexist=true
					noinput=true 
					autoid=959/></td>
			</tr>
	</table>
</div>
</body>
<script type="text/javascript">
function goAdd()
{
	window.location="educationSetTime.do?method=add&&act=add";
}

function goEdit()
{
	if(document.getElementById("chooseValue_educationSetTime").value=="")
	{
		alert("请选择要修改的数据！");
	}
	else
	{
		window.location="educationSetTime.do?method=add&&act=edit&&id="+document.getElementById("chooseValue_educationSetTime").value;
	}
}

function goDelete()
{
	if(document.getElementById("chooseValue_educationSetTime").value=="")
	{
		alert("请选择要删除的数据！");
	}
	else
	{
		if(confirm("确定删除此数据？")){
			window.location="educationSetTime.do?method=del&&id="+document.getElementById("chooseValue_educationSetTime").value;
		}
	}
}

//双击
function grid_dblclick(obj) {
	window.location="educationSetTime.do?method=add&&act=edit&&id="+obj.educationId;
}
</script>
</html>